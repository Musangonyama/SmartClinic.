/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;
import gui.UserSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
/**
 *
 * @author Student
 */
public class AuditLogger {
    public static void log(String action, String description){
        String sql = "INSERT INTO AuditLogs "
                + "(StaffID, StaffName, Role, Action, Description) "
                + "VALUES (?, ?, ?, ?, ?)";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, UserSession.staffID);
            pst.setString(2, UserSession.fullName);
            pst.setString(3, UserSession.role);
            pst.setString(4, action);
            pst.setString(5, description);
            pst.executeUpdate();
            pst.close();
            conn.close();
        }catch(Exception e){
            System.out.println("Audit log Error: " + e.getMessage());
        }
    }
}
