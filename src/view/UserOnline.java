package view;

import java.awt.Color;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class UserOnline extends javax.swing.JPanel {
   
    String name; //name of owner
    public UserOnline(String name) {
        this.name=name;
        initComponents();
       messContent.setEditable(false);
       DefaultListCellRenderer renderer =  
        (DefaultListCellRenderer)onlineList_rp.getCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
    }
    public JButton getBtSend() {
        return btnSend;
    }
     public JTextArea getTaInput() {
        return input;
    }
    public JList<String> getOnlineList_rp() {
        return onlineList_rp;
    }
     public void appendMessage(String msg1, String msg2, Color c1, Color c2) {  //thiết lập 2 loại text khác màu nhau trên 1 dòng
        //chèn msg1 trước:
        int len = messContent.getDocument().getLength();
        StyledDocument doc = (StyledDocument) messContent.getDocument();
        
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setFontFamily(sas, "Serif");
        StyleConstants.setBold(sas, true);
        StyleConstants.setFontSize(sas, 14);
        StyleConstants.setForeground(sas, c1);
        
        try {
            doc.insertString(len, msg1, sas);
        } catch (BadLocationException ex) {
            System.out.println(ex.toString());
        }
        
        //sau đó chèn msg2 ngay sau msg1:
        doc = (StyledDocument) messContent.getDocument();
        len = len+msg1.length();
        
        sas = new SimpleAttributeSet();
        StyleConstants.setFontFamily(sas, "Arial");
        StyleConstants.setFontSize(sas, 14);
        StyleConstants.setForeground(sas, c2);
        
        try {
            doc.insertString(len, msg2+"\n", sas);      //phai xuong dong
        } catch (BadLocationException ex) {
            System.out.println(ex.toString());
        }
        
        messContent.setCaretPosition(len+msg2.length());
    }
    
    public void appendMessage(String message, Color color) {
        int len = messContent.getDocument().getLength();
        StyledDocument doc = (StyledDocument) messContent.getDocument();
        
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setFontFamily(sas, "Comic Sans MS");
        StyleConstants.setItalic(sas, true);
        StyleConstants.setFontSize(sas, 14);
        StyleConstants.setForeground(sas, color);
        
        try {
            doc.insertString(len, message+"\n", sas);
        } catch (BadLocationException ex) {
            System.out.println(ex.toString());
        }
        
        messContent.setCaretPosition(len+message.length());
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        onlineList_rp = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        messContent = new javax.swing.JTextPane();
        btnSend = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        input = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(255, 255, 255));

        onlineList_rp.setBackground(new java.awt.Color(204, 204, 255));
        onlineList_rp.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        onlineList_rp.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        onlineList_rp.setToolTipText("double-click to send a message");
        jScrollPane1.setViewportView(onlineList_rp);

        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 0, 0));
        jLabel1.setText("Friend online");
        jLabel1.setToolTipText("");

        jScrollPane2.setViewportView(messContent);

        btnSend.setText("Send");

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 0, 0));
        jLabel2.setText("Chat room");
        jLabel2.setToolTipText("");

        input.setColumns(20);
        input.setRows(5);
        jScrollPane3.setViewportView(input);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSend))
                            .addComponent(jScrollPane2)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(13, 13, 13)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSend;
    private javax.swing.JTextArea input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextPane messContent;
    private javax.swing.JList<String> onlineList_rp;
    // End of variables declaration//GEN-END:variables
}
