/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlite:C:/Users/Admin/Downloads/smartClinic_db_working.db";

    public static Connection getConnection() {

        Connection conn = null;

        try {

            conn = DriverManager.getConnection(URL);

            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA busy_timeout = 10000");
            }

            System.out.println("Connected successfully!");

        } catch (SQLException e) {

            System.out.println("Database connection failed: "
                    + e.getMessage());

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            return null;
        }

        return conn;
    }
}
