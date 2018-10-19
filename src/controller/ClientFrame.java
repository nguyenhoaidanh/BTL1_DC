package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import view.LoginPanel;
import view.Chat;
import view.UserOnline;
import view.SignUpPanel;
import view.WelcomePanel;

public class ClientFrame extends JFrame implements Runnable {

    String serverHost;
    public static final String NICKNAME_EXIST = "This nickname is already login in another place! Please using another nickname";
    public static final String NICKNAME_VALID = "This nickname is OK";
    public static final String NICKNAME_INVALID = "Nickname or password is incorrect";
    public static final String SIGNUP_SUCCESS = "Sign up successful!";
    public static final String ACCOUNT_EXIST = "This nickname has been used! Please use another nickname!";

    String name;
    Socket socketOfClient;
    BufferedWriter bw;
    BufferedReader br;
    JPanel mainPanel;
    LoginPanel loginPanel;
    WelcomePanel welcomePanel;
    SignUpPanel signUpPanel;
    UserOnline userOnline;
    Thread clientThread;
    boolean isRunning;

    JMenuBar menuBar;
    JMenu menuShareFile;
    JMenuItem itemSendFile;
    JMenu menuAccount;
    JMenuItem itemLogout;

    SendFileFrame sendFileFrame;

    StringTokenizer tokenizer;

    DefaultListModel<String> listModel_rp;

    boolean isConnectToServer;

    int timeClicked = 0;    ///biến này để kiểm tra xem người dùng vừa click hay vừa double-click vào jList.

    Hashtable<String, Chat> listReceiver;

    public ClientFrame(String name) {
        this.name = name;
        socketOfClient = null;
        bw = null;
        br = null;
        isRunning = true;

        listModel_rp = new DefaultListModel<>();
        isConnectToServer = false;
        listReceiver = new Hashtable<>();

        mainPanel = new JPanel();
        loginPanel = new LoginPanel();

        welcomePanel = new WelcomePanel();
        signUpPanel = new SignUpPanel();
        userOnline = new UserOnline();

        welcomePanel.setVisible(true);
        signUpPanel.setVisible(false);
        loginPanel.setVisible(false);
        userOnline.setVisible(false);

        mainPanel.add(welcomePanel);
        mainPanel.add(signUpPanel);
        mainPanel.add(loginPanel);
        mainPanel.add(userOnline);

        addEventsForWelcomePanel();
        addEventsForSignUpPanel();
        addEventsForLoginPanel();
        addEventsForOnlineList();

        menuBar = new JMenuBar();   //menuBar

        menuAccount = new JMenu();

        itemLogout = new JMenuItem();
        menuAccount.setText("Account");
        itemLogout.setText("Logout");

        menuAccount.add(itemLogout);
        menuBar.add(menuAccount);

        itemLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int kq = JOptionPane.showConfirmDialog(ClientFrame.this, "Are you sure to logout?", "Notice", JOptionPane.YES_NO_OPTION);
                if (kq == JOptionPane.YES_OPTION) {
                    try {
                        isConnectToServer = false;
                        socketOfClient.close();
                        ClientFrame.this.setVisible(false);
                    } catch (IOException ex) {
                        Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    new ClientFrame(null).setVisible(true);
                }
            }
        });

        menuBar.setVisible(false);

        setJMenuBar(menuBar);
        pack();

        add(mainPanel);
        setSize(550, 350);
        setLocation(400, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(name);
    }

    private void addEventsForWelcomePanel() {

        welcomePanel.getBtLogin_welcome().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                welcomePanel.setVisible(false);
                signUpPanel.setVisible(false);
                loginPanel.setVisible(true);

                userOnline.setVisible(false);
            }
        });
        welcomePanel.getBtSignUp_welcome().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                welcomePanel.setVisible(false);
                signUpPanel.setVisible(true);
                loginPanel.setVisible(false);

                userOnline.setVisible(false);
            }
        });

    }

    private void addEventsForSignUpPanel() {
        signUpPanel.getLbBack_signup().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                welcomePanel.setVisible(true);
                signUpPanel.setVisible(false);
                loginPanel.setVisible(false);

                userOnline.setVisible(false);
            }
        });
        signUpPanel.getBtSignUp().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btSignUpEvent();
            }
        });
    }

    private void addEventsForLoginPanel() {
        loginPanel.getTfNickname().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    btOkEvent();
                }
            }

        });
        loginPanel.getTfPass().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    btOkEvent();
                }
            }

        });
        loginPanel.getBtOK().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btOkEvent();
            }
        });
        loginPanel.getLbBack_login().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                welcomePanel.setVisible(true);
                signUpPanel.setVisible(false);
                loginPanel.setVisible(false);

                userOnline.setVisible(false);
            }
        });
    }

    private void addEventsForOnlineList() {

        userOnline.getOnlineList_rp().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                try {
                    openPrivateChat();
                } catch (IOException ex) {
                    Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void openPrivateChat() throws IOException {
        timeClicked++;
        if (timeClicked == 1) {
            Thread countingTo500ms = new Thread(counting);
            countingTo500ms.start();
        }

        if (timeClicked == 2) {  //nếu như countingTo500ms chưa thực hiện xong, tức là timeClicked vẫn = 2:
            String privateReceiver = userOnline.getOnlineList_rp().getSelectedValue();
            String Receiver=privateReceiver.split("-")[0];
            String IP=socketOfClient.getInetAddress().toString().split("/")[1];
           int desPort=Integer.parseInt(privateReceiver.split("-")[1]);
           int srcPort=socketOfClient.getLocalPort();
           
             Chat pc = new Chat(this.name, Receiver, srcPort, desPort, IP);
          
            if (pc == null) {    //nếu đây là lần đầu tiên nhắn tin với người mà ta vừa double-click vào
               
              pc = new Chat(this.name, Receiver, srcPort, desPort, IP);
               
                pc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
               
            } else {
                pc.setVisible(true);
            }
        }
    }

    Runnable counting = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            timeClicked = 0;
        }
    };

    ////////////////////////Events////////////////////////////
    private void btOkEvent() {
        String hostname = loginPanel.getTfHost().getText().trim();
        String nickname = loginPanel.getTfNickname().getText().trim();
        String pass = loginPanel.getTfPass().getText().trim();

        this.serverHost = hostname;
        this.name = nickname;

        if (hostname.equals("") || nickname.equals("") || pass.equals("")) {
            JOptionPane.showMessageDialog(this, "Please fill up all fields", "Notice!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isConnectToServer) {
            isConnectToServer = true;
            this.connectToServer(hostname); //tạo 1 socket kết nối tói server
        }
        this.sendToServer("LOGIN_REQ|" + this.name + "|" + pass);       //sau đó gửi tên đến để yêu cầu đăng nhập =  tên đó

        //server phản hồi rằng tên vừa nhập có hợp lệ hay ko:
        String response = this.recieveFromServer();
        if (response != null) {
            if (response.equals(NICKNAME_EXIST) || response.equals(NICKNAME_INVALID)) {
                JOptionPane.showMessageDialog(this, response, "Error", JOptionPane.ERROR_MESSAGE);
                //loginPanel.getBtOK().setText("OK");
            } else {
                //Tên hợp lệ, vào chon phòng chat:
                loginPanel.setVisible(false);
                userOnline.setVisible(true);

                this.setTitle(name);

                menuBar.setVisible(true);

                clientThread = new Thread(this);
                clientThread.start();
                this.sendToServer("GET_USER_ONLINE");     //yêu cầu ds các user đang online để có thể chat private

                System.out.println("this is \"" + name + "\"");

            }
        } else {
            System.out.println("[btOkEvent()] Server is not open yet, or already closed!");
        }
    }

    private void btSignUpEvent() {
        String pass = this.signUpPanel.getTfPass().getText();
        String pass2 = this.signUpPanel.getTfPass2().getText();
        if (!pass.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Passwords don't match", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String nickname = signUpPanel.getTfID().getText().trim();
            String hostName = signUpPanel.getTfHost().getText().trim();
            if (hostName.equals("") || nickname.equals("") || pass.equals("") || pass2.equals("")) {
                JOptionPane.showMessageDialog(this, "Please fill up all fields", "Notice!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!isConnectToServer) {
                isConnectToServer = true;
                this.connectToServer(hostName); //tạo 1 socket kết nối tói server
            }
            this.sendToServer("REGISTER_REQ|" + nickname + "|" + pass);       //sau đó gửi tên đến để yêu cầu đăng nhập =  tên đó

            String response = this.recieveFromServer();
            if (response != null) {
                if (response.equals(NICKNAME_EXIST) || response.equals(ACCOUNT_EXIST)) {
                    JOptionPane.showMessageDialog(this, response, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, response + "\nYou can now go back and login to join chat ", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    signUpPanel.clearTf();
                }
            }
        }

    }
    ////////////////////////End of Events////////////////////////////   

    public void connectToServer(String hostAddress) {   //mỗi lần connect tới server là khởi tạo lại thuộc tính socketOfClient
        try {
            socketOfClient = new Socket(hostAddress, 1000);
            bw = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));

        } catch (java.net.UnknownHostException e) {
            JOptionPane.showMessageDialog(this, "Host IP is not correct.\nPlease try again!", "Failed to connect to server", JOptionPane.ERROR_MESSAGE);
        } catch (java.net.ConnectException e) {
            JOptionPane.showMessageDialog(this, "Server maybe server is not open yet, or can't find this host.\nPlease try again!", "Failed to connect to server", JOptionPane.ERROR_MESSAGE);
        } catch (java.net.NoRouteToHostException e) {
            JOptionPane.showMessageDialog(this, "Can't find this host!\nPlease try again!", "Failed to connect to server", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public void sendToServer(String line) {
        try {
            this.bw.write(line);
            this.bw.newLine();   //phải có newLine thì mới dùng đc hàm readLine()
            this.bw.flush();
        } catch (java.net.SocketException e) {
            JOptionPane.showMessageDialog(this, "Server is closed, can't send message!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (java.lang.NullPointerException e) {
            System.out.println("[sendToServer()] Server is not open yet, or already closed!");
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String recieveFromServer() {
        try {
            return this.br.readLine();  //chú ý rằng chỉ nhận 1 dòng từ server gửi về thôi, nếu server gửi nhiều dòng thì các dòng sau ko đọc
        } catch (java.lang.NullPointerException e) {
            System.out.println("[recieveFromServer()] Server is not open yet, or already closed!");
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void disconnect() {
        System.out.println("disconnect()");
        try {
            if (br != null) {
                this.br.close();
            }
            if (bw != null) {
                this.bw.close();
            }
            if (socketOfClient != null) {
                this.socketOfClient.close();
            }
            System.out.println("trong khoi try catch");
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        ClientFrame client = new ClientFrame(null);
        client.setVisible(true);
    }

    @Override
    public void run() {
        String response;
        String sender, receiver, fileName, thePersonIamChattingWith, thePersonSendFile;
        String msg;
        String cmd;
        Chat pc;

        while (isRunning) {
            response = this.recieveFromServer();   //nhận phản hồi từ server sau khi đã gửi data ở trên
            tokenizer = new StringTokenizer(response, "|");
            cmd = tokenizer.nextToken();
            switch (cmd) {
                case "CHAT_MSG":     //khi server gửi message của sender, đứng ở góc nhìn của thằng client này
                    //thì thằng gửi tới đó chính là thằng nhận, vì thằng client này chat với thằng gửi đó,
                    //nên thằng gửi đó sẽ nhận tin từ thằng này
                    sender = tokenizer.nextToken();
                    msg = response.substring(cmd.length() + sender.length() + 2, response.length());

                    pc = listReceiver.get(sender);

                    if (pc == null) {
                        try {
                            pc = new Chat("", "", 1, 1, "x");
                        } catch (IOException ex) {
                            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        listReceiver.put(sender, pc);
                    } else {
                        pc.setVisible(true);
                    }
                    pc.appendMessage_Left(sender + ": ", msg);
                    break;

                case "GET_USER_ONLINE":

                    listModel_rp.clear();
                    while (tokenizer.hasMoreTokens()) {
                        cmd = tokenizer.nextToken();

                        listModel_rp.addElement(cmd);
                    }

                    listModel_rp.removeElement(this.name);
                    userOnline.getOnlineList_rp().setModel(listModel_rp);
                    break;

                case "MSG_FILE_AVAILABLE":
                    System.out.println("file available");
                    fileName = tokenizer.nextToken();
                    thePersonIamChattingWith = tokenizer.nextToken();
                    thePersonSendFile = tokenizer.nextToken();

                    pc = listReceiver.get(thePersonIamChattingWith);

                    if (pc == null) {
                        sender = this.name;
                        receiver = thePersonIamChattingWith;
                try {
                    pc = new Chat("","",1,1,"x");
                } catch (IOException ex) {
                    Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                     
                        pc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                        listReceiver.put(receiver, pc);
                    }

                    pc.setVisible(true);    //nếu cái pc đó đang Visible rồi thì lệnh này cho focus vào cái frame này
                    pc.insertButton(thePersonSendFile, fileName);
                    break;
                default:

            }
        }
        System.out.println("Disconnected to server!");
    }

}
