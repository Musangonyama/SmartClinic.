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
import java.sql.Statement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.*;
import java.awt.print.*;
import javax.swing.*;
import java.text.SimpleDateFormat;


import java.awt.image.BufferedImage;
/**
 *
 * @author Student
 */
public class Register extends javax.swing.JFrame {
  private int registeredPatientID = -1;
  private String registeredPatientName = "";
  private String registeredPatientDOB = "";
  private String registeredMedicalRecordNo ="";
  private int editingPatientID = -1;
private boolean editMode = false;
    /**
     * Creates new form Register
     */
    public Register() {
        initComponents();
    }
    public Register(int patientID) {
    initComponents();

    editingPatientID = patientID;
    editMode = true;

    jLabel1.setText("Edit Patient Details");
    btnRegister.setText("UPDATE PATIENT");

    loadPatientDetails(patientID);
}

private boolean isValidSouthAfricanID(String id, java.util.Date selectedDOB) {
    // South African ID numbers must contain 13 digits.
    if (id == null || !id.matches("\\d{13}")) {
        JOptionPane.showMessageDialog(this,
                "National ID must contain exactly 13 digits.");
        return false;
    }

    try {
        String yy = id.substring(0, 2);
        String mm = id.substring(2, 4);
        String dd = id.substring(4, 6);

        int yearPart = Integer.parseInt(yy);
        int month = Integer.parseInt(mm);
        int day = Integer.parseInt(dd);

        // Determine the century using the current date.
        java.util.Calendar today = java.util.Calendar.getInstance();
        int currentYear = today.get(java.util.Calendar.YEAR);
        int currentYY = currentYear % 100;

        int year = (yearPart <= currentYY)
                ? 2000 + yearPart
                : 1900 + yearPart;

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        java.util.Date idDOB =
                sdf.parse(String.format("%02d/%02d/%04d",
                        day, month, year));

        // Ensure the ID date matches the selected calendar date.
        java.text.SimpleDateFormat compareFormat =
                new java.text.SimpleDateFormat("yyyyMMdd");

        if (!compareFormat.format(idDOB)
                .equals(compareFormat.format(selectedDOB))) {
            JOptionPane.showMessageDialog(this,
                    "Date of birth does not match the National ID number.");
            return false;
        }

        return true;

    } catch (java.text.ParseException | NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
                "The National ID contains an invalid date of birth.");
        return false;
    }
}

private boolean isDOBNotInFuture(java.util.Date dob) {
    if (dob == null) {
        JOptionPane.showMessageDialog(this,
                "Please select a date of birth.");
        return false;
    }

    java.util.Calendar selected = java.util.Calendar.getInstance();
    selected.setTime(dob);
    selected.set(java.util.Calendar.HOUR_OF_DAY, 0);
    selected.set(java.util.Calendar.MINUTE, 0);
    selected.set(java.util.Calendar.SECOND, 0);
    selected.set(java.util.Calendar.MILLISECOND, 0);

    java.util.Calendar today = java.util.Calendar.getInstance();
    today.set(java.util.Calendar.HOUR_OF_DAY, 0);
    today.set(java.util.Calendar.MINUTE, 0);
    today.set(java.util.Calendar.SECOND, 0);
    today.set(java.util.Calendar.MILLISECOND, 0);

    if (selected.after(today)) {
        JOptionPane.showMessageDialog(this,
                "Date of birth cannot be in the future.");
        return false;
    }

    return true;
}

private boolean isPhoneUnique(Connection conn, String phone)
        throws SQLException {

    String sql = "SELECT PatientID FROM Patients WHERE Phone = ?";

    if (editMode) {
        sql += " AND PatientID <> ?";
    }

    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, phone);

        if (editMode) {
            pst.setInt(2, editingPatientID);
        }

        try (ResultSet rs = pst.executeQuery()) {
            return !rs.next();
        }
    }
}

    private void loadPatientDetails(int patientID) {

    String sql = "SELECT * FROM Patients WHERE PatientID = ?";

    try {
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);

        pst.setInt(1, patientID);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            txtName.setText(rs.getString("FullName"));
            txtID.setText(rs.getString("NationalID"));
            txtPhone.setText(rs.getString("Phone"));
            txtEmerge.setText(rs.getString("EmergencyContact"));

            String gender = rs.getString("Gender");
            if (gender != null) {
                cmbGender.setSelectedItem(gender);
            }

            String dob = rs.getString("DateOfBirth");

            if (dob != null && !dob.isEmpty()) {
                try {
                    java.util.Date date =
                        new SimpleDateFormat("dd/MM/yyyy").parse(dob);

                    jCalendar1.setDate(date);
                } catch (java.text.ParseException e) {
                    JOptionPane.showMessageDialog(this,
                        "Could not read the patient's date of birth.");
                }
            }

            String address = rs.getString("Address");

            if (address != null) {
                String[] parts = address.split(",\\s*", 4);

                if (parts.length > 0) {
                    String[] streetParts = parts[0].trim().split("\\s+", 2);

                    txtStreetNumber.setText(streetParts[0]);

                    if (streetParts.length > 1) {
                        txtStreetName.setText(streetParts[1]);
                    }
                }

                if (parts.length > 1) {
                    txtSuburb.setText(parts[1]);
                }

                if (parts.length > 2) {
                    txtCity.setText(parts[2]);
                }

                if (parts.length > 3) {
                    txtPostalCode.setText(parts[3]);
                }
            }

        } else {
            JOptionPane.showMessageDialog(this,
                "Patient not found.");
            dispose();
        }

        rs.close();
        pst.close();
        conn.close();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error loading patient: " + e.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtID = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        jLabel6 = new javax.swing.JLabel();
        cmbGender = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtStreetNumber = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtStreetName = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtSuburb = new javax.swing.JTextField();
        txtCity = new javax.swing.JTextField();
        txtPostalCode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtEmerge = new javax.swing.JTextField();
        btnRegister = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblQRCode = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btnBook = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        btnReprint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(102, 204, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-register-48.png"))); // NOI18N
        jLabel1.setText("Register Patient");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(510, 510, 510))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(102, 204, 255));

        jLabel2.setText("Full  Name");

        jLabel3.setText("National ID");

        jLabel4.setText("Phone");

        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        jLabel5.setText("Date Of Birth");

        jLabel6.setText("Gender");

        cmbGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Choose gender", "Female", "Male" }));
        cmbGender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGenderActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(204, 204, 204));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Address");

        jLabel9.setText("Street Number");

        txtStreetNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStreetNumberActionPerformed(evt);
            }
        });

        jLabel10.setText("Street Name");

        jLabel11.setText("Suburb");

        txtCity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCityActionPerformed(evt);
            }
        });

        txtPostalCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPostalCodeActionPerformed(evt);
            }
        });

        jLabel8.setText("Emergency contact");

        btnRegister.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnRegister.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-register-48.png"))); // NOI18N
        btnRegister.setText("REGISTER");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        jLabel12.setText("City");

        jLabel13.setText("Postal Code");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(68, 68, 68)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(64, 64, 64)
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(92, 92, 92)
                        .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel8)
                            .addComponent(jLabel13))
                        .addGap(23, 23, 23)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtEmerge, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPostalCode, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSuburb, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCity, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(46, 46, 46)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtStreetNumber)
                            .addComponent(txtStreetName, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(342, 342, 342)
                        .addComponent(jLabel7))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(136, 136, 136)
                        .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(156, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel4))
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel6))
                    .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStreetNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStreetName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSuburb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPostalCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmerge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(31, 31, 31)
                .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
        );

        lblQRCode.setText("QR CODE");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 153, 255));
        jLabel14.setText("PATIENT QR CODE");

        btnBook.setBackground(new java.awt.Color(102, 204, 255));
        btnBook.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnBook.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-check-in-40.png"))); // NOI18N
        btnBook.setText("CHECK-IN");
        btnBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBookActionPerformed(evt);
            }
        });

        btnBack.setBackground(new java.awt.Color(102, 204, 255));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-back-24.png"))); // NOI18N
        btnBack.setText("BACK");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnReprint.setBackground(new java.awt.Color(102, 204, 255));
        btnReprint.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnReprint.setForeground(new java.awt.Color(255, 255, 255));
        btnReprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-print-40.png"))); // NOI18N
        btnReprint.setText("PRINT CARD");
        btnReprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReprintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(220, 220, 220)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(lblQRCode))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(btnBook, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(btnReprint, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(125, 125, 125)))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(5, 5, 5)
                        .addComponent(lblQRCode)
                        .addGap(333, 333, 333)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnReprint)
                            .addComponent(btnBook))
                        .addGap(38, 38, 38)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(215, 215, 215))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtStreetNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStreetNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStreetNumberActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
       String fullName = txtName.getText().trim();
    String nationalID = txtID.getText().trim();
    String phone = txtPhone.getText().trim();
    String emergencyContact = txtEmerge.getText().trim();

    String dateOfBirth = "";

    if (jCalendar1.getDate() != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dateOfBirth = sdf.format(jCalendar1.getDate());
    }

    String gender = cmbGender.getSelectedItem().toString();

    String streetNumber = txtStreetNumber.getText().trim();
    String streetName = txtStreetName.getText().trim();
    String suburb = txtSuburb.getText().trim();
    String city = txtCity.getText().trim();
    String postalCode = txtPostalCode.getText().trim();

    String address = streetNumber + " "
            + streetName + ", "
            + suburb + ", "
            + city + ", "
            + postalCode;

    // Validate required fields
    if (fullName.isEmpty()
            || nationalID.isEmpty()
            || phone.isEmpty()
            || dateOfBirth.isEmpty()
            || gender.equals("Choose gender")) {

        JOptionPane.showMessageDialog(this,
                "Please fill in all required fields.");

        return;
    }

    // Validate date of birth
    java.util.Date selectedDOB = jCalendar1.getDate();

    if (!isDOBNotInFuture(selectedDOB)) {
        return;
    }

    // Validate that the ID number matches the selected DOB
    if (!isValidSouthAfricanID(nationalID, selectedDOB)) {
        return;
    }

    try {

        Connection conn = DBConnection.getConnection();
        
        // Prevent duplicate phone numbers
        if (!isPhoneUnique(conn, phone)) {
            conn.close();

            JOptionPane.showMessageDialog(this,
                    "This phone number is already registered "
                    + "to another patient.");

            return;
        }


        // ==========================================
        // EDIT MODE: UPDATE EXISTING PATIENT
        // ==========================================
        if (editMode) {

            String sql = "UPDATE Patients SET "
                    + "FullName = ?, "
                    + "NationalID = ?, "
                    + "Phone = ?, "
                    + "DateOfBirth = ?, "
                    + "Gender = ?, "
                    + "Address = ?, "
                    + "EmergencyContact = ? "
                    + "WHERE PatientID = ?";

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, fullName);
            pst.setString(2, nationalID);
            pst.setString(3, phone);
            pst.setString(4, dateOfBirth);
            pst.setString(5, gender);
            pst.setString(6, address);
            pst.setString(7, emergencyContact);
            pst.setInt(8, editingPatientID);

            int rowsUpdated = pst.executeUpdate();

            pst.close();
            conn.close();

            if (rowsUpdated > 0) {

                registeredPatientID = editingPatientID;
                registeredPatientName = fullName;
                registeredPatientDOB = dateOfBirth;

                // Do not change the existing MedicalRecordNo
                // The QR code remains valid because PatientID
                // has not changed.

                AuditLogger.log(
                        "Updated Patient",
                        "Updated details for patient ID: "
                        + editingPatientID
                );

                JOptionPane.showMessageDialog(this,
                        "Patient details updated successfully!\n"
                        + "Patient ID: " + editingPatientID);

                dispose();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Patient not found. No details were updated.");
            }

        } else {

            // ==========================================
            // REGISTRATION MODE: INSERT NEW PATIENT
            // ==========================================

            String sql = "INSERT INTO Patients "
                    + "(FullName, NationalID, Phone, DateOfBirth, "
                    + "Gender, Address, EmergencyContact, RegistrationDate) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, "
                    + "datetime('now', 'localtime'))";

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, fullName);
            pst.setString(2, nationalID);
            pst.setString(3, phone);
            pst.setString(4, dateOfBirth);
            pst.setString(5, gender);
            pst.setString(6, address);
            pst.setString(7, emergencyContact);

            pst.executeUpdate();

            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                    "SELECT last_insert_rowid()"
            );

            if (rs.next()) {

                int patientID = rs.getInt(1);

                String medicalRecordNo =
                        String.format("MR%03d", patientID);

                registeredPatientID = patientID;
                registeredPatientName = fullName;
                registeredPatientDOB = dateOfBirth;
                registeredMedicalRecordNo = medicalRecordNo;

                String updateSQL =
                        "UPDATE Patients SET MedicalRecordNo = ? "
                        + "WHERE PatientID = ?";

                PreparedStatement updatePst =
                        conn.prepareStatement(updateSQL);

                updatePst.setString(1, medicalRecordNo);
                updatePst.setInt(2, patientID);

                updatePst.executeUpdate();

                updatePst.close();

                // Keep the existing QR code generation
                generateQRCode(patientID);

                // Keep the registration audit log
                AuditLogger.log(
                        "Registered Patient",
                        "Registered patient ID: " + patientID
                );

                JOptionPane.showMessageDialog(this,
                        "Patient registered successfully!\n"
                        + "Patient ID: " + patientID
                        + "\nMedical Record No: " + medicalRecordNo);
            }

            rs.close();
            stmt.close();
            pst.close();
            conn.close();
        }

    } catch (SQLException e) {

        String error = e.getMessage();

        if (error != null
                && (error.contains("Patients.NationalID")
                || error.contains("UNIQUE constraint failed"))) {

            JOptionPane.showMessageDialog(this,
                    "A patient with this National ID already exists.");

        } else {

            JOptionPane.showMessageDialog(this,
                    "Database error: " + error);
        }
    }

    }//GEN-LAST:event_btnRegisterActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        receptionistDashboard rd = new receptionistDashboard();
        rd.setVisible(true);
        rd.setLocationRelativeTo(null);
        dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookActionPerformed
        // TODO add your handling code here:
        QRScanner qr = new QRScanner();
        qr.setVisible(true);
        qr.setLocationRelativeTo(null);
        dispose();
    }//GEN-LAST:event_btnBookActionPerformed

    private void btnReprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReprintActionPerformed
   


    if (registeredPatientID == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please register or load a patient before printing the card."
        );
        return;
    }

    printPatientCard();
    
    }//GEN-LAST:event_btnReprintActionPerformed

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void cmbGenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGenderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbGenderActionPerformed

    private void txtCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCityActionPerformed

    private void txtPostalCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPostalCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPostalCodeActionPerformed

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
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Register().setVisible(true);
            }
        });
    }
    private void generateQRCode(int patientID) {

    try {

        String qrData = "SMARTCLINIC:PATIENT:" + patientID;

        int width = 200;
        int height = 200;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(
                qrData,
                BarcodeFormat.QR_CODE,
                width,
                height
        );

        BufferedImage qrImage =
                MatrixToImageWriter.toBufferedImage(bitMatrix);

        lblQRCode.setIcon(new javax.swing.ImageIcon(qrImage));
        lblQRCode.setText("");

    } catch (WriterException e) {

        JOptionPane.showMessageDialog(this,
                "Error generating QR Code: " + e.getMessage());
    }
}
    private void printPatientCard(){
          PrinterJob printerJob =
                PrinterJob.getPrinterJob();

        printerJob.setJobName(
                "Smart Clinic Patient Booklet"
        );

        printerJob.setPrintable(
                new Printable() {

                    @Override
                    public int print(
                            Graphics graphics,
                            PageFormat pageFormat,
                            int pageIndex)
                            throws PrinterException {

                        // We only have two pages
                        if (pageIndex > 1) {
                            return NO_SUCH_PAGE;
                        }

                        Graphics2D g2 =
                                (Graphics2D) graphics;

                        // Translate to printable area
                        g2.translate(
                                pageFormat.getImageableX(),
                                pageFormat.getImageableY()
                        );

                        double pageWidth =
                                pageFormat.getImageableWidth();

                        double pageHeight =
                                pageFormat.getImageableHeight();

                        /*
                         * ======================================
                         * PAGE 1 - OUTSIDE
                         * ======================================
                         */
                        if (pageIndex == 0) {

                            drawOutsidePage(
                                    g2,
                                    pageWidth,
                                    pageHeight
                            );

                            return PAGE_EXISTS;
                        }

                        /*
                         * ======================================
                         * PAGE 2 - INSIDE
                         * ======================================
                         */
                        if (pageIndex == 1) {

                            drawInsidePage(
                                    g2,
                                    pageWidth,
                                    pageHeight
                            );

                            return PAGE_EXISTS;
                        }

                        return NO_SUCH_PAGE;
                    }
                }
        );

        // Show printer selection dialog
        boolean print =
                printerJob.printDialog();

        if (print) {

            try {

                printerJob.print();

                JOptionPane.showMessageDialog(
                        this,
                        "Patient booklet sent to printer."
                );

            } catch (PrinterException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error printing patient booklet: "
                        + e.getMessage()
                );
            }
        }
    }
    private void drawOutsidePage(Graphics2D g2, double pageWidth, double pageHeight){
        int margin = 40;
        
        int halfWidth = (int) (pageWidth / 2);
        g2.drawRect(margin, margin, halfWidth - margin * 2, (int)pageHeight - margin *2);
        int rightX = halfWidth;
        g2.drawRect(rightX, margin, halfWidth - margin, (int)pageHeight - margin * 2);
        
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("SMART CLINIC", rightX + 50, margin + 50);
        g2.setFont(new Font("Arial",Font.BOLD,18));

        g2.drawString("PATIENT CARD",rightX + 65,margin + 80);
        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        g2.drawString(
                "Patient Name:",
                rightX + 30,
                margin + 120
        );

        g2.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        g2.drawString(
                registeredPatientName,
                rightX + 30,
                margin + 140
        );

        // Date of Birth
        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        g2.drawString(
                "Date of Birth:",
                rightX + 30,
                margin + 175
        );

        g2.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        g2.drawString(
                registeredPatientDOB,
                rightX + 30,
                margin + 195
        );

        // Patient ID
        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        g2.drawString(
                "Patient ID:",
                rightX + 30,
                margin + 230
        );

        g2.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        g2.drawString(
                String.valueOf(registeredPatientID),
                rightX + 30,
                margin + 250
        );

        /*
         * --------------------------------------------------
         * QR CODE
         * --------------------------------------------------
         */
        BufferedImage qrImage =
                createQRCodeImage(
                        registeredPatientID,
                        130,
                        130
                );

        if (qrImage != null) {

            g2.drawImage(
                    qrImage,
                    rightX + 100,
                    margin + 280,
                    null
            );
        }

        g2.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        10
                )
        );

        g2.drawString(
                "Please bring this card",
                rightX + 90,
                margin + 425
        );

        g2.drawString(
                "when visiting the clinic.",
                rightX + 90,
                margin + 440
        );
    }

    /**
     * DRAW INSIDE APPOINTMENT PAGE
     */
    private void drawInsidePage(
            Graphics2D g2,
            double pageWidth,
            double pageHeight) {

        int margin = 50;

        /*
         * --------------------------------------------------
         * TITLE
         * --------------------------------------------------
         */
        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        String title =
                "APPOINTMENT RECORD";

        g2.drawString(
                title,
                margin + 150,
                margin + 40
        );

        /*
         * --------------------------------------------------
         * TABLE
         * --------------------------------------------------
         */
        int tableX = margin + 80;
        int tableY = margin + 80;

        int tableWidth =
                (int) pageWidth - margin * 2 - 160;

        int rowHeight = 45;

        // Two columns
        int dateColumnWidth =
                tableWidth / 2;

        int timeColumnWidth =
                tableWidth - dateColumnWidth;

        int numberOfRows = 9;

        /*
         * Draw table outer border
         */
        g2.drawRect(
                tableX,
                tableY,
                tableWidth,
                rowHeight * numberOfRows
        );

        /*
         * Draw vertical line
         */
        g2.drawLine(
                tableX + dateColumnWidth,
                tableY,
                tableX + dateColumnWidth,
                tableY + rowHeight * numberOfRows
        );

        /*
         * Draw horizontal lines
         */
        for (int i = 1; i < numberOfRows; i++) {

            int y =
                    tableY + rowHeight * i;

            g2.drawLine(
                    tableX,
                    y,
                    tableX + tableWidth,
                    y
            );
        }

        /*
         * --------------------------------------------------
         * TABLE HEADERS
         * --------------------------------------------------
         */
        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        g2.drawString(
                "DATE",
                tableX + 40,
                tableY + 29
        );

        g2.drawString(
                "TIME",
                tableX + dateColumnWidth + 40,
                tableY + 29
        );

        /*
         * --------------------------------------------------
         * FOOTER
         * --------------------------------------------------
         */
        g2.setFont(
                new Font(
                        "Arial",
                        Font.ITALIC,
                        11
                )
        );

        g2.drawString(
                "Appointment date and time are written by clinic staff.",
                margin + 80,
                tableY + rowHeight * numberOfRows + 35
        );
    }

    /**
     * CREATE QR IMAGE FOR PRINTING
     */
    private BufferedImage createQRCodeImage(
            int patientID,
            int width,
            int height) {

        try {

            String qrData =
                    "SMARTCLINIC:PATIENT:" + patientID;

            QRCodeWriter qrCodeWriter =
                    new QRCodeWriter();

            BitMatrix bitMatrix =
                    qrCodeWriter.encode(
                            qrData,
                            BarcodeFormat.QR_CODE,
                            width,
                            height
                    );

            return MatrixToImageWriter.toBufferedImage(
                    bitMatrix
            );

        } catch (WriterException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error creating QR Code: "
                    + e.getMessage()
            );

            return null;
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnBook;
    private javax.swing.JButton btnRegister;
    private javax.swing.JButton btnReprint;
    private javax.swing.JComboBox<String> cmbGender;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblQRCode;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtEmerge;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtPostalCode;
    private javax.swing.JTextField txtStreetName;
    private javax.swing.JTextField txtStreetNumber;
    private javax.swing.JTextField txtSuburb;
    // End of variables declaration//GEN-END:variables
}
