/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import database.DBConnection;
import database.AuditLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author Admin
 */
public class ChangePasswordd extends javax.swing.JFrame {
  

    public ChangePasswordd() {

        initComponents();

        setTitle("Change Password");
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );
        setLocationRelativeTo(null);

        if (UserSession.staffID <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No logged-in staff account was found."
            );

            btnChange.setEnabled(false);
        }
    }

   
    
    
    private void changePassword() {

        String currentPassword =
                new String(
                        txtCurrentPassword.getPassword()
                );

        String newPassword =
                new String(
                        txtNewPassword.getPassword()
                );

        String confirmPassword =
                new String(
                        txtConfirmPassword.getPassword()
                );

        if (currentPassword.isEmpty()
                || newPassword.isEmpty()
                || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all password fields."
            );
            return;
        }

        if (newPassword.length() < 8) {

            JOptionPane.showMessageDialog(
                    this,
                    "Your new password must be at least 8 characters long."
            );
            return;
        }

        if (!newPassword.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "The new passwords do not match."
            );
            return;
        }

        if (currentPassword.equals(newPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Your new password must be different from your current password."
            );
            return;
        }

        String sql =
                "SELECT Password FROM Staff "
                + "WHERE StaffID = ? "
                + "AND Status = 'Active'";

        try (Connection conn =
                     DBConnection.getConnection();
             PreparedStatement pst =
                     conn.prepareStatement(sql)) {

            pst.setInt(1, UserSession.staffID);

            try (ResultSet rs = pst.executeQuery()) {

                if (!rs.next()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Your active staff account could not be found."
                    );
                    return;
                }

                String savedPassword =
                        rs.getString("Password");

                if (!PasswordUtil.verify(
                        currentPassword,
                        savedPassword)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Your current password is incorrect."
                    );
                    return;
                }
            }

            String hashedPassword =
                    PasswordUtil.hash(newPassword);

            String updateSQL =
                    "UPDATE Staff SET Password = ? "
                    + "WHERE StaffID = ? "
                    + "AND Status = 'Active'";

            try (PreparedStatement updatePst =
                         conn.prepareStatement(updateSQL)) {

                updatePst.setString(
                        1, hashedPassword
                );

                updatePst.setInt(
                        2, UserSession.staffID
                );

                int updated =
                        updatePst.executeUpdate();

                if (updated > 0) {

                    try {
                        AuditLogger.log(
                                "Password changed",
                                "Password changed for staff ID: "
                                + UserSession.staffID
                        );
                    } catch (Exception auditError) {
                        auditError.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(
                            this,
                            "Your password has been changed successfully."
                    );

                    dispose();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Password could not be changed. Please try again."
                    );
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password change failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

  

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNewPassword = new javax.swing.JPasswordField();
        txtConfirmPassword = new javax.swing.JPasswordField();
        txtCurrentPassword = new javax.swing.JPasswordField();
        btnCancel = new javax.swing.JButton();
        btnChange = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(650, 590));
        setPreferredSize(new java.awt.Dimension(650, 590));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("CHANGE PASSWORD");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 320, 50));

        jLabel2.setText("Update your staff account password");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, -1, 20));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("New Password");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Confirm New Password:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 350, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Current Password:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, -1, -1));
        getContentPane().add(txtNewPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, 540, 50));
        getContentPane().add(txtConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 370, 540, 50));
        getContentPane().add(txtCurrentPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, 540, 50));

        btnCancel.setBackground(new java.awt.Color(255, 0, 102));
        btnCancel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancel.setText("CANCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 460, 250, 50));

        btnChange.setBackground(new java.awt.Color(51, 255, 51));
        btnChange.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnChange.setText("CHANGE PASSWORD");
        btnChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeActionPerformed(evt);
            }
        });
        getContentPane().add(btnChange, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 460, 250, 50));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
    dispose();

        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed

        changePassword();

        // TODO add your handling code here:
    }//GEN-LAST:event_btnChangeActionPerformed

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
            java.util.logging.Logger.getLogger(ChangePasswordd.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChangePasswordd.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChangePasswordd.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChangePasswordd.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChangePasswordd().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnChange;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JPasswordField txtCurrentPassword;
    private javax.swing.JPasswordField txtNewPassword;
    // End of variables declaration//GEN-END:variables
}
