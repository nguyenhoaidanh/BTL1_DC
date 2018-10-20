/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class SendFileFrame extends javax.swing.JFrame {

    String sender;
    String filePath;
    String destIP;
    int desPort;
    DatagramSocket socket; //socket of client not of receiver
    JTextPane messContent; // mess content of owner
    HTMLEditorKit htmlKit;
    HTMLDocument htmlDoc;
    public String thePersonIamChattingWith;
    int PIECES_OF_FILE_SIZE = 1024 * 32;

    public SendFileFrame(String receiver, String sender, String destIP, int desPort, DatagramSocket socket, JTextPane messContent,HTMLEditorKit htmlKit,HTMLDocument htmlDoc) {
        this.desPort = desPort;
        this.destIP = destIP;
        this.socket = socket;
        initComponents();
        this.thePersonIamChattingWith = receiver;
        this.sender = sender;
        this.setTitle(sender + " send file to " + thePersonIamChattingWith);
        this.messContent = messContent;
        this.htmlKit=htmlKit;
        this.htmlDoc=htmlDoc;
    }

    public void LoadFile(String sourcePath, String destIP, int desPort, DatagramSocket socket) throws IOException, BadLocationException {
        InetAddress inetAddress;
        DatagramPacket sendPacket;
        try {
            File fileSend = new File(sourcePath);
            InputStream inputStream = new FileInputStream(fileSend);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            inetAddress = InetAddress.getByName(destIP);
            byte[] bytePart = new byte[PIECES_OF_FILE_SIZE];

            // get file size
            long fileLength = fileSend.length();
            int piecesOfFile = (int) (fileLength / PIECES_OF_FILE_SIZE);
            int lastByteLength = (int) (fileLength % PIECES_OF_FILE_SIZE);

            // check last bytes of file
            if (lastByteLength > 0) {
                piecesOfFile++;
            }

            // split file into pieces and assign to fileBytess
            byte[][] fileBytess = new byte[piecesOfFile][PIECES_OF_FILE_SIZE];
            int count = 0;
            while (bis.read(bytePart, 0, PIECES_OF_FILE_SIZE) > 0) {
                fileBytess[count++] = bytePart;
                bytePart = new byte[PIECES_OF_FILE_SIZE];
            }

            // read file info
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFilename(fileSend.getName());
            fileInfo.setFileSize(fileSend.length());
            fileInfo.setPiecesOfFile(piecesOfFile);
            fileInfo.setLastByteLength(lastByteLength);

            // send file info
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(fileInfo);
            sendPacket = new DatagramPacket(baos.toByteArray(),
                    baos.toByteArray().length, inetAddress, desPort);
            socket.send(sendPacket);

            // send file content
            System.out.println("Sending file...");
            // send pieces of file
            for (int i = 0; i < (count - 1); i++) {
                sendPacket = new DatagramPacket(fileBytess[i], PIECES_OF_FILE_SIZE,
                        inetAddress, desPort);

                socket.send(sendPacket);
                //waitServer(40);
            }
            // send last bytes of file
            sendPacket = new DatagramPacket(fileBytess[count - 1], PIECES_OF_FILE_SIZE,
                    inetAddress, desPort);
            socket.send(sendPacket);
            //waitServer(40);

            // close stream
            bis.close();

            // tạo 1 message of owner thong bao la minh da gui file
            
            messContent.setEditorKit(htmlKit);
            messContent.setDocument(htmlDoc);
            String msg = "You just send a file: " + fileInfo.getFilename();
            htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p style=\"color:green; padding: 3px; margin-top: 4px; margin-left:35px; text-align:right; font:normal 10px Tahoma;\"><span style=\"background-color: lightblue; -webkit-border-radius: 10px;\">" + msg + "</span></p>", 0, 0, null);
            messContent.setCaretPosition(messContent.getDocument().getLength());
            System.out.println("Sent file done.");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        

    }

    public String getFilePath() {
        return filePath;
    }

    public String getThePersonIamChattingWith() {
        return thePersonIamChattingWith;
    }

    public JTextField getTfFilePath() {
        return tfFilePath;
    }

    public JTextField getTfReceiver() {
        return tfReceiver;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tfFilePath = new javax.swing.JTextField();
        btBrowse = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        tfReceiver = new javax.swing.JTextField();
        btSendFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 51));
        jLabel1.setText("Select a file:");

        tfFilePath.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        btBrowse.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btBrowse.setText("...");
        btBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBrowseActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 0, 51));
        jLabel2.setText("Reciever:");

        tfReceiver.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        btSendFile.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btSendFile.setText("Send");
        btSendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSendFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfReceiver, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btSendFile)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btBrowse))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfReceiver, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btSendFile))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBrowseActionPerformed
        displayOpenDialog();
    }//GEN-LAST:event_btBrowseActionPerformed

    private void btSendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSendFileActionPerformed
        this.thePersonIamChattingWith = tfReceiver.getText();
        this.filePath = tfFilePath.getText();
        System.out.println("file name " + filePath + " to " + thePersonIamChattingWith);
        if (this.filePath.equals("") == false) {
            this.setVisible(false);
            try {
                LoadFile(this.filePath, destIP, desPort, socket);
            } catch (IOException ex) {
                Logger.getLogger(SendFileFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadLocationException ex) {
                Logger.getLogger(SendFileFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }//GEN-LAST:event_btSendFileActionPerformed

    private void displayOpenDialog() {
        JFileChooser chooser = new JFileChooser();
        int kq = chooser.showOpenDialog(this);
        if (kq == JFileChooser.APPROVE_OPTION) {
            tfFilePath.setText(chooser.getSelectedFile().getAbsolutePath());
        } else {
            tfFilePath.setText("");
        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btBrowse;
    private javax.swing.JButton btSendFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField tfFilePath;
    private javax.swing.JTextField tfReceiver;
    // End of variables declaration//GEN-END:variables

}
