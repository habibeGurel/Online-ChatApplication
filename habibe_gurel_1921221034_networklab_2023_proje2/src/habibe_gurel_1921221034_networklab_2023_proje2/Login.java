/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package habibe_gurel_1921221034_networklab_2023_proje2;

import Client.Client;
import Message.Message;

/**
 *
 * @author Habibe
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    public static Login user;

    public Login() {
        initComponents();
        user = this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_login = new javax.swing.JPanel();
        txt_username = new javax.swing.JTextField();
        btn_login = new javax.swing.JButton();
        lbl_username = new javax.swing.JLabel();
        lbl_login_icon = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(100, 181));
        setPreferredSize(new java.awt.Dimension(607, 689));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        pnl_login.setBackground(new java.awt.Color(0, 102, 153));
        pnl_login.setForeground(new java.awt.Color(0, 102, 153));

        txt_username.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btn_login.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btn_login.setForeground(new java.awt.Color(0, 0, 0));
        btn_login.setText("LOG IN");
        btn_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_loginActionPerformed(evt);
            }
        });

        lbl_username.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lbl_username.setForeground(new java.awt.Color(0, 0, 0));
        lbl_username.setText("USERNAME");

        lbl_login_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/habibe_gurel_1921221034_networklab_2023_proje2/icons/login.png"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 20)); // NOI18N
        jLabel1.setText("WELCOME TO ONLINE CHAT APPLICATION");

        javax.swing.GroupLayout pnl_loginLayout = new javax.swing.GroupLayout(pnl_login);
        pnl_login.setLayout(pnl_loginLayout);
        pnl_loginLayout.setHorizontalGroup(
            pnl_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_loginLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_loginLayout.createSequentialGroup()
                        .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_login, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
                    .addGroup(pnl_loginLayout.createSequentialGroup()
                        .addComponent(lbl_username, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_loginLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(lbl_login_icon, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(87, 87, 87))
        );
        pnl_loginLayout.setVerticalGroup(
            pnl_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_loginLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(lbl_login_icon)
                .addGap(18, 18, 18)
                .addComponent(lbl_username, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnl_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_login, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(69, 69, 69))
        );

        getContentPane().add(pnl_login);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_loginActionPerformed
        // TODO add your handling code here:
        chat.name = txt_username.getText();
        if (txt_username.getText().length() != 0) {//bos degilse baslat
            Client.Start("localHost", 2000);
            chat chatScreen = new chat();
            chatScreen.show();
            chat.mychat.lbl_myname.setText(txt_username.getText());
            Message msg = new Message(Message.Message_Type.Connect);
            msg.content = txt_username.getText();
            Client.Send(msg);
            dispose();
            
        }
    }//GEN-LAST:event_btn_loginActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_login;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbl_login_icon;
    private javax.swing.JLabel lbl_username;
    private javax.swing.JPanel pnl_login;
    public javax.swing.JTextField txt_username;
    // End of variables declaration//GEN-END:variables
}