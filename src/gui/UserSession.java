/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

/**
 *
 * @author Student
 */
public class UserSession {
   public static int userID;
    public static int staffID;
    public static String fullName;
    public static String role;
    public static String department;
    public static String room;

    public static void setUser(
            int id,
            int staff,
            String name,
            String userRole,
            String userDepartment,
            String userRoom) {

        userID = id;
        staffID = staff;
        fullName = name;
        role = userRole;
        department = userDepartment;
        room = userRoom;
    }

    public static void clearSession() {
        userID = 0;
        staffID = 0;
        fullName = null;
        role = null;
        department = null;
        room = null;
    }
}
