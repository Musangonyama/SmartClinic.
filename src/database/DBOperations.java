/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;
import database.DBConnection;
import java.sql.*;
/**
 *
 * @author Student
 */
public class DBOperations {
     public static void main(String[] args) {

        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println("Connected successfully!");
        } else {
            System.out.println("Connection failed!");
        }
    }
}
