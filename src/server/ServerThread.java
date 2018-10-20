package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

public class ServerThread extends Thread {

    Socket socketOfServer;      //socket để nối với socket của client kết nối tới
    BufferedWriter bw;
    BufferedReader br;
    String clientName, clientPass;
    public static Hashtable<String, ServerThread> listUser = new Hashtable<>();

    public static final String NICKNAME_EXIST = "This nickname is already login in another place! Please using another nickname";
    public static final String NICKNAME_VALID = "This nickname is OK";
    public static final String NICKNAME_INVALID = "Nickname or password is incorrect";
    public static final String SIGNUP_SUCCESS = "Sign up successful!";
    public static final String CHANGE_PASSWORD_SUCCESS = "Change password successful!";
    public static final String ACCOUNT_EXIST = "This nickname has been used! Please use another nickname!";

    public JTextArea taServer;

    StringTokenizer tokenizer;
    private final int BUFFER_SIZE = 1024;

    UserDatabase userDB;

    static boolean isBusy = false;     //dùng để kiểm tra xem server có đang gửi và nhận file hay ko

    public ServerThread(Socket socketOfServer) {
        this.socketOfServer = socketOfServer;
        this.bw = null;
        this.br = null;

        clientName = "";
        clientPass = "";
        userDB = new UserDatabase();
        userDB.connect();
    }

    public void appendMessage(String message) {
        taServer.append(message);
        taServer.setCaretPosition(taServer.getText().length() - 1);     //thiết lập vị trí con trỏ ngay sau đoạn text vừa chèn vào
    }

    public String recieveFromClient() {
        try {
            return br.readLine();
        } catch (IOException ex) {
            System.out.println(clientName + " is disconnected!");
        }
        return null;
    }

    public void sendToClient(String response) {     //chỉ gửi tin tới client gắn kết với thread này
        try {
            bw.write(response);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendToSpecificClient(ServerThread socketOfClient, String response) {     //chỉ gửi tin tới client cụ thể nào đó
        try {
            BufferedWriter writer = socketOfClient.bw;
            writer.write(response);
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendToSpecificClient(Socket socket, String response) {     //chỉ gửi tin tới client cụ thể nào đó
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(response);
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void notifyToAllUsers(String message) {

        Enumeration<ServerThread> clients = listUser.elements();
        ServerThread st;
        BufferedWriter writer;

        while (clients.hasMoreElements()) {
            st = clients.nextElement();
            writer = st.bw;

            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void closeServerThread() {
        try {
            br.close();
            bw.close();
            socketOfServer.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getAllUsers() {
        Enumeration<ServerThread> clients = listUser.elements();
        String kq = "";
        ServerThread st;
        BufferedWriter writer;
        Enumeration<String> keys = listUser.keys();
        String tem = "";
        while (clients.hasMoreElements()) {
            st = clients.nextElement();
            tem = keys.nextElement();
            kq += "|" + tem + "-" + (st.socketOfServer.getPort()); // name-port
        }
        System.out.println(kq);
        return kq;
    }

    public void clientQuit() {

        if (clientName != "") {

            this.appendMessage("\n- Client \"" + clientName + "\" is disconnected!");
            listUser.remove(clientName);
            if (listUser.isEmpty()) {
                this.appendMessage("\n- No user is connecting to server...");
            }
            notifyToAllUsers("GET_USER_ONLINE|" + getAllUsers());

        }
    }

    public void notifyToUsersInRoom(String message) {      //gửi bản tin message tới phòng room
        Enumeration<ServerThread> clients = listUser.elements();
        ServerThread st;
        BufferedWriter writer;

        while (clients.hasMoreElements()) {
            st = clients.nextElement();

            writer = st.bw;

            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    @Override
    public void run() {
        try {
            //tạo các luồng vào và ra với socket của client
            bw = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));

            boolean isUserExist = true;
            String message, sender, receiver, fileName;
            StringBuffer str;
            String cmd;
            while (true) {   //cứ chờ client gửi tin tới và phản hồi
                try {
                    message = recieveFromClient();
                    tokenizer = new StringTokenizer(message, "|");
                    cmd = tokenizer.nextToken();

                    switch (cmd) {
                        case "CHAT_ROOM_MSG":
                            str = new StringBuffer(message);
                            str = str.delete(0, 9);
                            notifyToUsersInRoom("CHAT_ROOM_MSG|" + this.clientName + "|" + str.toString());    //this.clientName = tên client gửi tin tới
                            break;
                        case "LOGIN_REQ":
                            clientName = tokenizer.nextToken();
                            clientPass = tokenizer.nextToken();
                            isUserExist = listUser.containsKey(clientName);

                            if (isUserExist) {  //nickname is exist, nghĩa là đang có người khác đăng nhập với nick đó rồi
                                sendToClient(NICKNAME_EXIST);
                            } else {  //nickname vẫn chưa có ai đăng nhập
                                int kq = userDB.checkUser(clientName, clientPass);
                                if (kq == 1) {
                                    sendToClient(NICKNAME_VALID);
                                    //sau đó nếu tên hợp lệ thì cho nick đó vào Hashtable và chát với client:
                                    this.appendMessage("\n+ Client \"" + clientName + "\" is connecting to server");
                                    listUser.put(clientName, this);     //thêm tên của đối tượng này và thêm cả đối tượng này vào listUser
                                } else {
                                    sendToClient(NICKNAME_INVALID);
                                }
                            }
                            break;

                        case "REGISTER_REQ":
                            String name = tokenizer.nextToken();
                            String pass = tokenizer.nextToken();
                            System.out.println("name, pass = " + name + " - " + pass);
                            isUserExist = listUser.containsKey(name);

                            if (isUserExist) {
                                sendToClient(NICKNAME_EXIST);
                            } else {
                                int kq = userDB.insertUser(new User(name, pass));
                                if (kq > 0) {
                                    sendToClient(SIGNUP_SUCCESS);

                                } else {
                                    sendToClient(ACCOUNT_EXIST);
                                }
                            }
                            break;
                        case "GET_USER_ONLINE":
                            notifyToAllUsers("GET_USER_ONLINE|" + getAllUsers());
                            break;
                        default:
                            notifyToAllUsers("GET_USER_ONLINE|" + getAllUsers());

                            break;
                    }

                } catch (Exception e) {
                    clientQuit();
                    break;
                }
            }
        } catch (IOException ex) {
            clientQuit();
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
