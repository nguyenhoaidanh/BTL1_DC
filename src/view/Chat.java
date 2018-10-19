package view;

import controller.FileInfo;
import controller.SendFileFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class Chat extends javax.swing.JFrame implements Runnable {

    int PIECES_OF_FILE_SIZE = 1024 * 32;
    String name;
    int sourcePort;
    String destinationIP = "10.80.255.176";
    int destinationPort;
    int portServer = 1000;
    String serverhost = "localhost";
    public BufferedWriter bw;
     FileInfo fileInfo ; // file info that will be download
    public BufferedReader br;
    InetSocketAddress address;
    DatagramSocket socket;
    boolean running;
    public String dataReceive;
    String msg;   //tin nhan gui di
    String receiver; // nguoi nhan
    String filename; // file  gui di
    HTMLEditorKit htmlKit;
    HTMLDocument htmlDoc;
    SendFileFrame sendFileFrame;

    public Chat(String name, String receiver, int srcPort, int desPort, String desIP) throws SocketException, IOException {
        this.name = name;
        this.sourcePort = srcPort;
        this.destinationIP = desIP;
        this.destinationPort = desPort;
        this.receiver = receiver;
        msg = "";
        initComponents();
        this.setVisible(true);
        this.setTitle(this.name + " chat with " + this.receiver);
        start();
        this.address = new InetSocketAddress(desIP, desPort);
        htmlKit = new HTMLEditorKit();
        htmlDoc = new HTMLDocument();
        messContent.setEditorKit(htmlKit);
        messContent.setDocument(htmlDoc);

    }

    public void start() throws SocketException, IOException {
        bind(sourcePort);
        System.out.println("Peer of " + name + " Started.");
        Thread thread = new Thread(this);
        thread.start();
    }

    public void sendMess() throws IOException {
        String Mess = this.msg;
        if (Mess != "") {
            appendMessage_Right(this.msg);
            Mess = "CHAT_MSG|" + Mess;
            sendTo(this.address, Mess);
        }
    }

    public void insertButton(String sender, String fileName) {
        JButton bt = new JButton(fileName);
        bt.setName(fileName);
        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    downloadFile(fileName,fileInfo);
                } catch (IOException ex) {
                    Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        appendMessage_Left(sender, " sends a file ", "#00dddd", "#00ee11");
        messContent.setCaretPosition(messContent.getDocument().getLength() - 1);
        messContent.insertComponent(bt);
    }

    public void appendMessage_Left(String msg1, String msg2, String color1, String color2) {      //dành cho người mà user này đang chat cùng
        try {
            htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p style=\"color:" + color1 + "; padding: 3px; margin-top: 4px; margin-right:35px; text-align:left; font:normal 12px Tahoma;\"><span><b>" + msg1 + "</b><span style=\"color:" + color2 + ";\">" + msg2 + "</span></span></p><br/>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
            System.out.println(ex.toString());
        }
        messContent.setCaretPosition(messContent.getDocument().getLength());
    }

    public void appendMessage_Left(String msg1, String msg2) {      //dành cho người mà user này đang chat cùng
        try {
            htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p style=\"color:black; padding: 3px; margin-top: 4px; margin-right:35px; text-align:left; font:normal 12px Tahoma;\"><span style=\"background-color:#f3f3f3;\"><b>" + msg1 + "</b><span style=\"color:black;\">" + msg2 + "</span></span></p>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
             System.out.println(ex.toString());
        }
        messContent.setCaretPosition(messContent.getDocument().getLength());
    }

    public void appendMessage_Right(String msg1, String msg2) {     //dành cho người user này
        try {
            //htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p style=\"color:blue; margin-left:30px; text-align:right; font:normal 12px Tahoma;\"><b>" + msg1 + "</b><span style=\"color:black;\">" + msg2 + "</span></p>", 0, 0, null);
            htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p style=\"color:white; padding: 3px; margin-top: 4px; margin-left:35px; text-align:right; font:normal 12px Tahoma;\"><span style=\"background-color: #889eff; -webkit-border-radius: 10px;\">" + msg2 + "</span></p>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
            System.out.println(ex.toString());
        }
        messContent.setCaretPosition(messContent.getDocument().getLength());
    }

    public void appendMessage_Right(String msg1) {     //dành cho người user này
        try {
            //htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p style=\"color:blue; margin-left:30px; text-align:right; font:normal 12px Tahoma;\"><b>" + msg1 + "</b><span style=\"color:black;\">" + msg2 + "</span></p>", 0, 0, null);
            htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p style=\"color:white; padding: 3px; margin-top: 4px; margin-left:35px; text-align:right; font:normal 12px Tahoma;\"><span style=\"background-color: #889eff; -webkit-border-radius: 10px;\">" + msg1 + "</span></p>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
             System.out.println(ex.toString());
        }
        messContent.setCaretPosition(messContent.getDocument().getLength());
    }

    private void openSendFileFrame() {
        sendFileFrame = new SendFileFrame(receiver, name, destinationIP, destinationPort, socket);
        sendFileFrame.getTfReceiver().setText(receiver);
        sendFileFrame.setVisible(true);
        sendFileFrame.setLocation(450, 250);
        sendFileFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void exit() {
        System.out.println("Peer" + name + " is Closed.");
        running = false;
        socket.close();
    }

    private void downloadFile(String buttonName,FileInfo fileInfo) throws FileNotFoundException, IOException {
        String myDownloadFolder;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int kq = chooser.showSaveDialog(this);
        if (kq == JFileChooser.APPROVE_OPTION) {
            myDownloadFolder = chooser.getSelectedFile().getAbsolutePath();
        } else {
            myDownloadFolder = "D:";
            JOptionPane.showMessageDialog(this, "The default folder to save file is in D:\\", "Notice", JOptionPane.INFORMATION_MESSAGE);
        }
        System.out.println("start receiving file");
        byte[] receiveData = new byte[PIECES_OF_FILE_SIZE];
        System.out.println("des dir "+myDownloadFolder);
         if (fileInfo != null) {
                    System.out.println("File name: " + fileInfo.getFilename());
                    System.out.println("File size: " + fileInfo.getFileSize());
                    System.out.println("Pieces of file: " + fileInfo.getPiecesOfFile());
                    System.out.println("Last bytes length: " + fileInfo.getLastByteLength());
                }
        File fileReceive = new File(myDownloadFolder+"\\"+ fileInfo.getFilename());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileReceive));
                // write pieces of file
                for (int i = 0; i < (fileInfo.getPiecesOfFile() - 1); i++) {
                    bos.write(receiveData, 0, PIECES_OF_FILE_SIZE);
                }
                // write last bytes of file
                bos.write(receiveData, 0, fileInfo.getLastByteLength());
                bos.flush();
                
                System.out.println("Done download!");
                 JOptionPane.showMessageDialog(this, "Download OK", "Notice", JOptionPane.INFORMATION_MESSAGE);
       
                // close stream
                bos.close();
        
    }
    
    public void receiveMessage(String sender, String file) throws IOException {
        byte[] receiveData = new byte[PIECES_OF_FILE_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String msg = new String(receiveData, 0, receivePacket.getLength());
        this.dataReceive = msg;
        
        if (msg.contains("CHAT_MSG|")) { // receive string
            String tem = msg.substring(msg.indexOf("CHAT_MSG|") + 9);
            appendMessage_Left(name + " : ", tem);
            System.out.println("in run nhận dc text" + tem);
        } 
        else // receive file
        {
            try {
                InetAddress inetAddress = receivePacket.getAddress();
                ByteArrayInputStream bais = new ByteArrayInputStream(
                        receivePacket.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                fileInfo = (FileInfo) ois.readObject();
                // show file info
                if (fileInfo != null) {
                    System.out.println("File name: " + fileInfo.getFilename());
                    System.out.println("File size: " + fileInfo.getFileSize());
                    System.out.println("Pieces of file: " + fileInfo.getPiecesOfFile());
                    System.out.println("Last bytes length: " + fileInfo.getLastByteLength());
                }
                // get file content
                // write pieces of file
                for (int i = 0; i < (fileInfo.getPiecesOfFile() - 1); i++) {
                    receivePacket = new DatagramPacket(receiveData, receiveData.length,
                            inetAddress, sourcePort);
                    socket.receive(receivePacket);
                   
                }
                // write last bytes of file
                receivePacket = new DatagramPacket(receiveData, receiveData.length,
                        inetAddress, sourcePort);
                socket.receive(receivePacket);
                System.out.println("Send file Done!");
                insertButton(sender, this.fileInfo.getFilename());
                // close stream
               
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    
    public void sendTo(InetSocketAddress address, String msg) throws IOException {
        byte[] buffer = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        packet.setSocketAddress(address);
        socket.send(packet);
    }

    public void bind(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                receiveMessage("YY", "xx");
            } catch (IOException ex) {
                Logger.getLogger(Chat.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) throws IOException {
         Chat x = new Chat("danh111", "danh123", 111, 112, "10.80.255.176");
      // Chat y = new Chat("danh123", "danh111", 112, 111, "10.80.255.176");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        input = new javax.swing.JTextField();
        btnSend = new javax.swing.JButton();
        btnFile = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        messContent = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        btnFile.setText("File");
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(messContent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(input, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSend)
                        .addGap(212, 212, 212)
                        .addComponent(btnFile))
                    .addComponent(jScrollPane2))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(input, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSend)
                    .addComponent(btnFile))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        this.msg = input.getText();
        if ("".equals(this.msg) == false) {
            try {
                this.sendMess();
                input.setText("");
            } catch (IOException ex) {
                Logger.getLogger(Chat.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        openSendFileFrame();
    }//GEN-LAST:event_btnFileActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFile;
    private javax.swing.JButton btnSend;
    public javax.swing.JTextField input;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane messContent;
    // End of variables declaration//GEN-END:variables

}
