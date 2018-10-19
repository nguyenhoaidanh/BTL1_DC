
package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ServerFrame extends JFrame implements Runnable { 
    JButton btStart, btStop;
    JTextArea taInfo;
    ServerSocket serverSocket;

    ServerThread serverThread;

    public ServerFrame() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lbStateServer = new JLabel("     Server status\n");
        lbStateServer.setFont(new java.awt.Font("Time New Roman", 1, 18));

        taInfo = new JTextArea();
        taInfo.setEditable(false);
        taInfo.setFont(new java.awt.Font("Serif", 0, 16));
        taInfo.setBackground(new java.awt.Color(0, 10, 0));  //màu nền
        taInfo.setForeground(new java.awt.Color(0, 205, 155));  //màu chữ
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(taInfo);
        scroll.setPreferredSize(new Dimension(400, 400));

        btStart = new JButton("Start server");
        btStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btStartEvent(ae);
            }
        });

        btStop = new JButton("Stop server");
        btStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btStopEvent(ae);
            }
        });
        btStart.setEnabled(true);
        btStop.setEnabled(false);

        JPanel panelBtn = new JPanel();
        panelBtn.add(btStart);
        panelBtn.add(btStop);

        JPanel p1 = new JPanel();
        p1.setPreferredSize(new Dimension(30, 30));
        JPanel p2 = new JPanel();
        p2.setPreferredSize(new Dimension(30, 30));

        panel.add(lbStateServer, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelBtn, BorderLayout.SOUTH);
        panel.add(p1, BorderLayout.WEST);
        panel.add(p2, BorderLayout.EAST);

        this.add(panel);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void appendMessage(String message) {
        taInfo.append(message);
        taInfo.setCaretPosition(taInfo.getText().length() - 1);
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(1000); //chỉ có 1 đối tượng serverSocket duy nhất, đối tượng này sẽ tạo từng socket để giao tiếp với socket của client
            appendMessage("- Server is running...");
            appendMessage("\n- No user is connecting to server...");

            while (true) {
                Socket socketOfServer = serverSocket.accept();      //cứ có 1 client kết nối thì lệnh này mới đc thực hiện, sau đó 1 thread mới đc tạo ra để xử lý client đó, nghĩa là có 1 socket mới bên server để nối với socket của client
              
                serverThread = new ServerThread(socketOfServer);
                serverThread.taServer = this.taInfo;
                serverThread.start();
            }

        } catch (java.net.SocketException e) {
            System.out.println("Server is closed");
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Lỗi xảy ra, có thể là: Cổng này đang có server chiếm dụng!");
            System.out.println("Hoặc server đã close");
            JOptionPane.showMessageDialog(this, "Cổng này đang có server chiếm dụng", "Error", JOptionPane.ERROR_MESSAGE);
            this.setVisible(false);
            System.exit(0);
        }
    }

    private void btStartEvent(ActionEvent ae) {
        Connection conn = new UserDatabase().connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Please open MySQL first", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new Thread(this).start();
       
        this.btStart.setEnabled(false);
        this.btStop.setEnabled(true);
    }

    private void btStopEvent(ActionEvent ae) {
        int kq = JOptionPane.showConfirmDialog(this, "Are you sure to close server?", "Notice", JOptionPane.YES_NO_OPTION);
        if (kq == JOptionPane.YES_OPTION) {
            try {
                //notify to all clients that server is closed:
                //code here

                serverThread.notifyToAllUsers("Warnning: Server has been closed!");
                serverSocket.close();

                //then close server:
            } catch (IOException ex) {
                Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        ServerFrame serverFrame = new ServerFrame();
        serverFrame.setVisible(true);
      

    }

    @Override
    public void run() {
        this.startServer();
    }
}
