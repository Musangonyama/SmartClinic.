/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
/**
 *
 * @author Student
 */
public class MedicalRecord extends javax.swing.JFrame {
private int patientID;
private String openedBy;
private int referralID = -1;
private int currentQueueID = -1;
private NurseDashboard nurseDashboard;

    /**
     * Creates new form MedicalRecord
     */
    public MedicalRecord() {
        initComponents();
    }
   public MedicalRecord(int patientID, String openedBy, int referralID) {

    initComponents();

    this.patientID = patientID;
    this.openedBy = openedBy;
    this.referralID = referralID;

    loadPatientInformation();
    loadVisitHistory();
}
   public void setCurrentQueueID(int currentQueueID) {
    this.currentQueueID = currentQueueID;
}

public void setNurseDashboard(NurseDashboard nurseDashboard) {
    this.nurseDashboard = nurseDashboard;
}
    public MedicalRecord(int patientID, String openedBy){
    initComponents();

    this.patientID = patientID;
    this.openedBy = openedBy;
    loadCHWEscalation();
    loadPatientInformation();
    loadVisitHistory();
}
private void loadPatientInformation(){
    String sql ="SELECT PatientID, FullName, DateOfBirth, Phone "
            + "FROM Patients "
            + "WHERE PatientID = ?";
    try{
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, patientID);
        ResultSet rs = pst.executeQuery();
        
        if(rs.next()){
            lblPatID.setText("P" + String.format("%04d", rs.getInt("PatientID")));
            lblName.setText(rs.getString("FullName"));
            lblDOB.setText(rs.getString("DateOfBirth"));
            lblPhone.setText(rs.getString("Phone"));
        }
        rs.close();
        pst.close();
        conn.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, "Error loading patient information: " + e.getMessage(), "Medical Record Error", JOptionPane.ERROR_MESSAGE);
    }
}
private void loadVisitHistory(){
    DefaultTableModel model = (DefaultTableModel)tblHistory.getModel();
    model.setRowCount(0);
    
    String sql = "SELECT m.RecordID, "
            + "m.VisitDate, "
            + "m.Department, "
            + "s.FullName AS StaffName, "
            + "s.Role, "
            + "m.Diagnosis "
            + "FROM MedicalRecords m "
            + "LEFT JOIN Staff s "
            + "ON m.StaffID = s.StaffID "
            + "WHERE m.PatientID = ? "
            + "ORDER BY m.visitDate DESC, m.RecordID DESC";
    
    try{
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, patientID);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            String staffName = rs.getString("StaffName");
            String role = rs.getString("Role");
            String healthCare;
            if(staffName == null){
                healthCare = "Not recorded";
            }else if(role == null){
               healthCare = staffName; 
            }else{
                healthCare = staffName + " (" + role + ")";
            }
            model.addRow(new Object[]{
                rs.getString("VisitDate"),
                rs.getString("Department"), 
                healthCare,
                rs.getString("Diagnosis")
            });
        }
        rs.close();
        pst.close();
        conn.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, "Error loading visit history: " + e.getMessage(),
                "Medical Record Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void loadPrescriptionForVisit(String visitDate) {

    String sql = "SELECT Medicine, Dosage, Frequency, Duration, "
            + "Quantity, Instructions "
            + "FROM Prescriptions "
            + "WHERE PatientID = ? "
            + "AND date(PrescriptionDate) = date(?) "
            + "ORDER BY PrescriptionID DESC";

    StringBuilder details = new StringBuilder("<html>");

    try {
        Connection conn = DBConnection.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);

        pst.setInt(1, patientID);
        pst.setString(2, visitDate);

        ResultSet rs = pst.executeQuery();

        boolean found = false;

        while (rs.next()) {
            found = true;

            details.append("<b>Medicine:</b> ")
                    .append(rs.getString("Medicine"))
                    .append("<br>");

            details.append("<b>Dosage:</b> ")
                    .append(rs.getString("Dosage"))
                    .append("<br>");

            details.append("<b>Frequency:</b> ")
                    .append(rs.getString("Frequency"))
                    .append("<br>");

            details.append("<b>Duration:</b> ")
                    .append(rs.getString("Duration"))
                    .append("<br>");

            details.append("<b>Quantity:</b> ")
                    .append(rs.getString("Quantity"))
                    .append("<br>");

            details.append("<b>Instructions:</b> ")
                    .append(rs.getString("Instructions"))
                    .append("<br><br>");
        }

        rs.close();
        pst.close();
        conn.close();

        if (found) {
            txtPrescribed.setText(details.append("</html>").toString());
        } else {
            txtPrescribed.setText(
                    "No prescription recorded for this visit."
            );
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
                this,
                "Error loading prescription: " + e.getMessage()
        );
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
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHistory = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        lblBack = new javax.swing.JButton();
        btnPrescribe = new javax.swing.JButton();
        btnRefer = new javax.swing.JButton();
        lblCHWEscalation = new javax.swing.JLabel();
        btnView = new javax.swing.JButton();
        btnAcknowledgs = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblPatID = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblDOB = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblPhone = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblDep = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtComplaint = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblBp = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblTemp = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblHR = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblWeight = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblDiag = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtTreat = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtNotes = new javax.swing.JTextArea();
        jLabel19 = new javax.swing.JLabel();
        lblFollow = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtCHWEscalation = new javax.swing.JTextArea();
        jLabel20 = new javax.swing.JLabel();
        txtPrescribed = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(102, 153, 255));
        jLabel6.setText("VISIT HISTORY");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 98, -1, -1));

        tblHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Department", "Nurse/Doctor", "Diagnosis"
            }
        ));
        tblHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHistoryMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblHistory);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 129, 661, 219));

        btnAdd.setBackground(new java.awt.Color(102, 204, 255));
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-add-properties-30.png"))); // NOI18N
        btnAdd.setText("ADD RECORD");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jPanel1.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 651, -1, -1));

        btnPrint.setBackground(new java.awt.Color(102, 204, 255));
        btnPrint.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-print-40.png"))); // NOI18N
        btnPrint.setText("PRINT RECORD");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(212, 650, -1, 39));

        lblBack.setBackground(new java.awt.Color(102, 204, 255));
        lblBack.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblBack.setForeground(new java.awt.Color(255, 255, 255));
        lblBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-back-24.png"))); // NOI18N
        lblBack.setText("BACK");
        lblBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblBackActionPerformed(evt);
            }
        });
        jPanel1.add(lblBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(1258, 654, -1, -1));

        btnPrescribe.setBackground(new java.awt.Color(102, 204, 255));
        btnPrescribe.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnPrescribe.setForeground(new java.awt.Color(255, 255, 255));
        btnPrescribe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-prescription-40.png"))); // NOI18N
        btnPrescribe.setText("PRESCRIBE MEDICINE");
        btnPrescribe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrescribeActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrescribe, new org.netbeans.lib.awtextra.AbsoluteConstraints(428, 650, -1, 39));

        btnRefer.setBackground(new java.awt.Color(102, 204, 255));
        btnRefer.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnRefer.setForeground(new java.awt.Color(255, 255, 255));
        btnRefer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-doctor-refer-to-specialists-78.png"))); // NOI18N
        btnRefer.setText("REFER TO DOCTOR");
        btnRefer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReferActionPerformed(evt);
            }
        });
        jPanel1.add(btnRefer, new org.netbeans.lib.awtextra.AbsoluteConstraints(706, 646, -1, 48));
        jPanel1.add(lblCHWEscalation, new org.netbeans.lib.awtextra.AbsoluteConstraints(909, 581, 214, 16));

        btnView.setBackground(new java.awt.Color(102, 204, 255));
        btnView.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnView.setForeground(new java.awt.Color(255, 255, 255));
        btnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-view-30 (1).png"))); // NOI18N
        btnView.setText("VIEW CHW FOLLOW-UP");
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });
        jPanel1.add(btnView, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 538, -1, -1));

        btnAcknowledgs.setBackground(new java.awt.Color(102, 204, 255));
        btnAcknowledgs.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnAcknowledgs.setForeground(new java.awt.Color(255, 255, 255));
        btnAcknowledgs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-handshake-64.png"))); // NOI18N
        btnAcknowledgs.setText("ACKNOWLEDGE");
        btnAcknowledgs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcknowledgsActionPerformed(evt);
            }
        });
        jPanel1.add(btnAcknowledgs, new org.netbeans.lib.awtextra.AbsoluteConstraints(1035, 538, -1, 37));

        jPanel2.setBackground(new java.awt.Color(102, 204, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-medical-record-64.png"))); // NOI18N
        jLabel1.setText("MEDICAL RECORDS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(469, 469, 469)
                .addComponent(jLabel1)
                .addContainerGap(605, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.setBackground(new java.awt.Color(102, 204, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Patient ID :");

        lblPatID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPatID.setForeground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Patient Name :");

        lblName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblName.setForeground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Date Of Birth :");

        lblDOB.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDOB.setForeground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Phone :");

        lblPhone.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPhone.setForeground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("SELECTED VISIT");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Date :");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Department :");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Complaint :");

        txtComplaint.setColumns(20);
        txtComplaint.setRows(5);
        jScrollPane2.setViewportView(txtComplaint);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Vital Signs :");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Blood Pressure :");

        lblBp.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblBp.setForeground(new java.awt.Color(255, 255, 255));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Temperature :");

        lblTemp.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTemp.setForeground(new java.awt.Color(255, 255, 255));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Heart Rate :");

        lblHR.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHR.setForeground(new java.awt.Color(255, 255, 255));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Weight :");

        lblWeight.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblWeight.setForeground(new java.awt.Color(255, 255, 255));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Diagnosis :");

        lblDiag.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDiag.setForeground(new java.awt.Color(255, 255, 255));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Treatment :");

        txtTreat.setColumns(20);
        txtTreat.setRows(5);
        jScrollPane4.setViewportView(txtTreat);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Notes :");

        txtNotes.setColumns(20);
        txtNotes.setRows(5);
        jScrollPane3.setViewportView(txtNotes);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Follow-up :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(jLabel7))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDep, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPatID, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addComponent(jLabel11))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(lblBp, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel16))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblHR, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDiag, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel17)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(76, 76, 76)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFollow, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel19)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblPatID, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(9, 9, 9)
                        .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(lblPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(lblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(lblDep, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel12))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblBp, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(lblHR, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblWeight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(lblDiag, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel19))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFollow, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 98, -1, -1));

        jPanel4.setBackground(new java.awt.Color(102, 204, 255));

        txtCHWEscalation.setColumns(20);
        txtCHWEscalation.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtCHWEscalation.setForeground(new java.awt.Color(255, 255, 255));
        txtCHWEscalation.setRows(5);
        jScrollPane5.setViewportView(txtCHWEscalation);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 58, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 354, -1, -1));

        jLabel20.setText("Prescribed Medicine from nurse:");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 360, -1, -1));
        jPanel1.add(txtPrescribed, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 390, 190, 130));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        printMedicalRecord();
    }//GEN-LAST:event_btnPrintActionPerformed

    private void tblHistoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHistoryMouseClicked
        // TODO add your handling code here:
        int selectedRow = tblHistory.getSelectedRow();

    if (selectedRow == -1) {
        return;
    }
    Object dateValue = tblHistory.getValueAt(selectedRow, 0);
    
    if(dateValue == null){
        return;
    }

    String visitDate =
            tblHistory.getValueAt(selectedRow, 0).toString();
    if(visitDate.isEmpty()){
        return;
    }

    loadSelectedVisit(visitDate);
    loadPrescriptionForVisit(visitDate);
    }//GEN-LAST:event_tblHistoryMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
       entryMedicalRecord emd = new entryMedicalRecord(patientID);
       emd.setVisible(true);
       emd.setLocationRelativeTo(null);
       dispose();
    }//GEN-LAST:event_btnAddActionPerformed

    private void lblBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblBackActionPerformed
        // TODO add your handling code here:
        dispose();

    if ("Doctor".equals(openedBy)) {

        DoctorDashboard db =
                new DoctorDashboard(referralID, patientID);

        db.setVisible(true);
        db.setLocationRelativeTo(null);

    } else if("Nurse".equals(openedBy)){

        NurseDashboard nb =
                new NurseDashboard();

        nb.setVisible(true);
        nb.setLocationRelativeTo(null);
    }else{
         managePatients manage = new managePatients();
         manage.setVisible(true);
         manage.setLocationRelativeTo(null);
            }
    }//GEN-LAST:event_lblBackActionPerformed

    private void btnPrescribeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrescribeActionPerformed
        // TODO add your handling code here:
        prescribeMedicine();
    }//GEN-LAST:event_btnPrescribeActionPerformed

    private void btnReferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReferActionPerformed
        // TODO add your handling code here:
        referPatientToDoctor();
    }//GEN-LAST:event_btnReferActionPerformed

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        // TODO add your handling code here:
          viewCHWFollowUp();
    }//GEN-LAST:event_btnViewActionPerformed

    private void btnAcknowledgsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcknowledgsActionPerformed
        // TODO add your handling code here:
        acknowledgeCHWEscalation();
    }//GEN-LAST:event_btnAcknowledgsActionPerformed

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
            java.util.logging.Logger.getLogger(MedicalRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MedicalRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MedicalRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MedicalRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MedicalRecord().setVisible(true);
            }
        });
    }
    private void loadSelectedVisit(String visitDate) {

    String sql = "SELECT VisitDate, Department, Complaint, "
            + "BloodPressure, Temperature, HeartRate, Weight, "
            + "Diagnosis, Treatment, Notes, FollowUpDate "
            + "FROM MedicalRecords "
            + "WHERE PatientID = ? "
            + "AND VisitDate = ? "
            + "ORDER BY RecordID DESC "
            + "LIMIT 1";

    try {

        Connection conn = DBConnection.getConnection();

        PreparedStatement pst = conn.prepareStatement(sql);

        pst.setInt(1, patientID);
        pst.setString(2, visitDate);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {

            lblDate.setText(
                    rs.getString("VisitDate")
            );

            lblDep.setText(
                    rs.getString("Department")
            );

            lblBp.setText(
                    rs.getString("BloodPressure")
            );

            lblTemp.setText(
                    rs.getString("Temperature")
            );

            lblHR.setText(
                    rs.getString("HeartRate")
            );

            lblWeight.setText(
                    rs.getString("Weight")
            );

            lblDiag.setText(
                    rs.getString("Diagnosis")
            );

            lblFollow.setText(
                    rs.getString("FollowUpDate")
            );

            txtComplaint.setText(
                    rs.getString("Complaint")
            );

            txtTreat.setText(
                    rs.getString("Treatment")
            );

            txtNotes.setText(
                    rs.getString("Notes")
            );
        }

        rs.close();
        pst.close();
        conn.close();

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Error loading selected visit: "
                + e.getMessage(),
                "Medical Record Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
    
   
    private void printMedicalRecord(){
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Smart Clinic Medical Record");
        job.setPrintable(new Printable(){
            @Override
            public int print(java.awt.Graphics graphics,
                    PageFormat pageFormat,
                    int pageIndex) throws PrinterException{
                if(pageIndex > 0){
                    return NO_SUCH_PAGE;
                }
                java.awt.Graphics2D g2 = (java.awt.Graphics2D)graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                int y = 30;
                
                g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
                g2.drawString("SMART CLINIC", 200, y);
                y += 25;
                g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD,14));
                g2.drawString("MEDICAL RECORD",185,y);

            y += 35;

            g2.setFont(new java.awt.Font("Arial",java.awt.Font.PLAIN,11));

            g2.drawString("Patient ID: "+ lblPatID.getText(),30, y);

            y += 18;

            g2.drawString("Patient Name: "+ lblName.getText(), 30, y);

            y += 18;

            g2.drawString("Date of Birth: " + lblDOB.getText(),30, y);

            y += 18;

            g2.drawString("Phone: "+ lblPhone.getText(),30,y);

            y += 30;

            g2.setFont(new java.awt.Font("Arial",java.awt.Font.BOLD,12));

            g2.drawString(
                    "VISIT INFORMATION",
                    30,
                    y
            );

            y += 20;

            g2.setFont(
                    new java.awt.Font(
                            "Arial",
                            java.awt.Font.PLAIN,
                            11
                    )
            );

            g2.drawString(
                    "Visit Date: "
                    + lblDate.getText(),
                    30,
                    y
            );

            y += 18;

            g2.drawString(
                    "Department: "
                    + lblDep.getText(),
                    30,
                    y
            );

            y += 18;

            g2.drawString(
                    "Complaint: "
                    + txtComplaint.getText(),
                    30,
                    y
            );

            y += 30;

           

            g2.setFont(
                    new java.awt.Font(
                            "Arial",
                            java.awt.Font.BOLD,
                            12
                    )
            );

            g2.drawString(
                    "VITAL SIGNS",
                    30,
                    y
            );

            y += 20;

            g2.setFont(
                    new java.awt.Font(
                            "Arial",
                            java.awt.Font.PLAIN,
                            11
                    )
            );

            g2.drawString(
                    "Blood Pressure: "
                    + lblBp.getText(),
                    30,
                    y
            );

            y += 18;

            g2.drawString(
                    "Temperature: "
                    + lblTemp.getText(),
                    30,
                    y
            );

            y += 18;

            g2.drawString(
                    "Heart Rate: "
                    + lblHR.getText(),
                    30,
                    y
            );

            y += 18;

            g2.drawString(
                    "Weight: "
                    + lblWeight.getText(),
                    30,
                    y
            );

            y += 30;

          

            g2.setFont(
                    new java.awt.Font(
                            "Arial",
                            java.awt.Font.BOLD,
                            12
                    )
            );

            g2.drawString(
                    "CLINICAL INFORMATION",
                    30,
                    y
            );

            y += 20;

            g2.setFont(
                    new java.awt.Font(
                            "Arial",
                            java.awt.Font.PLAIN,
                            11
                    )
            );

            g2.drawString(
                    "Diagnosis: "
                    + lblDiag.getText(),
                    30,
                    y
            );

            y += 18;

            g2.drawString(
                    "Treatment: "
                    + txtTreat.getText(),
                    30,
                    y
            );

            y += 18;

            g2.drawString(
                    "Notes: "
                    + txtNotes.getText(),
                    30,
                    y
            );

            y += 18;

            g2.drawString(
                    "Follow-Up Date: "
                    + lblFollow.getText(),
                    30,
                    y
            );

            y += 40;

           

            g2.drawString(
                    "Printed from Smart Clinic",
                    30,
                    y
            );

            return PAGE_EXISTS;
        }
    });

    boolean printDialog =
            job.printDialog();

    if (printDialog) {

        try {
            job.print();

            JOptionPane.showMessageDialog(this,"Medical record printed successfully.","Print Complete",JOptionPane.INFORMATION_MESSAGE);
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this,"Error printing medical record: "+ e.getMessage(),"Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }
    private void prescribeMedicine() {
    Prescription prescription = new Prescription(patientID, UserSession.staffID);
    prescription.setVisible(true);
    prescription.setLocationRelativeTo(this);
}
    private void referPatientToDoctor() {

    String reason = JOptionPane.showInputDialog(
            this,
            "Enter the reason for referring this patient to a doctor:",
            "Refer Patient to Doctor",
            JOptionPane.QUESTION_MESSAGE
    );

    if (reason == null) {
        return;
    }

    reason = reason.trim();

    if (reason.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter a referral reason.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    /*
     * The nurse's department is used as the destination
     * department for the doctor queue.
     */
    String department = UserSession.department;

    if (department == null
            || department.trim().isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "The nurse does not have a valid department.",
                "Referral Error",
                JOptionPane.ERROR_MESSAGE
        );

        return;
    }

    department = department.trim();

    String checkSQL =
            "SELECT ReferralID "
            + "FROM DoctorReferrals "
            + "WHERE PatientID = ? "
            + "AND QueueDate = date('now', 'localtime') "
            + "AND Status IN ('PENDING', 'IN CONSULTATION') "
            + "LIMIT 1";

    String queueSQL =
            "SELECT COUNT(*) AS Total "
            + "FROM DoctorReferrals "
            + "WHERE ToDepartment = ? "
            + "AND QueueDate = date('now', 'localtime')";

    String insertSQL =
            "INSERT INTO DoctorReferrals "
            + "(PatientID, NurseID, DoctorID, ReferralDate, "
            + "Reason, Status, Notes, ToDepartment, QueueNumber, "
            + "QueueDate, CheckInTime, VisitType) "
            + "VALUES (?, ?, NULL, date('now', 'localtime'), ?, "
            + "'PENDING', ?, ?, ?, date('now', 'localtime'), "
            + "time('now', 'localtime'), 'Nurse Referral')";

    try {

        Connection conn = DBConnection.getConnection();

        // =====================================================
        // 1. CHECK IF PATIENT IS ALREADY IN DOCTOR QUEUE
        // =====================================================

        PreparedStatement checkPst =
                conn.prepareStatement(checkSQL);

        checkPst.setInt(1, patientID);

        ResultSet checkRs =
                checkPst.executeQuery();

        if (checkRs.next()) {

            JOptionPane.showMessageDialog(
                    this,
                    "This patient is already in the doctor queue today.",
                    "Already Referred",
                    JOptionPane.WARNING_MESSAGE
            );

            checkRs.close();
            checkPst.close();
            conn.close();

            return;
        }

        checkRs.close();
        checkPst.close();

        // =====================================================
        // 2. GET NUMBER OF PATIENTS ALREADY IN DOCTOR QUEUE
        // =====================================================

        PreparedStatement queuePst =
                conn.prepareStatement(queueSQL);

        queuePst.setString(1, department);

        ResultSet queueRs =
                queuePst.executeQuery();

        int total = 0;

        if (queueRs.next()) {
            total = queueRs.getInt("Total");
        }

        queueRs.close();
        queuePst.close();

        // =====================================================
        // 3. CREATE NEXT DOCTOR QUEUE NUMBER
        // =====================================================

        int nextNumber = total + 1;

        String prefix =
                getDepartmentPrefix(department);

        String queueNumber =
                prefix
                + "-D"
                + String.format("%03d", nextNumber);

        // =====================================================
        // 4. INSERT REFERRAL INTO DOCTOR QUEUE
        // =====================================================

        PreparedStatement pst =
                conn.prepareStatement(insertSQL);

        pst.setInt(1, patientID);

        // Nurse who made the referral
        pst.setInt(2, UserSession.staffID);

        // Reason for referral
        pst.setString(3, reason);

        // Notes
        pst.setString(
                4,
                "Referred by " + UserSession.fullName
        );

        // Doctor department
        pst.setString(5, department);

        // Doctor queue number
        pst.setString(6, queueNumber);

        int inserted =
                pst.executeUpdate();

        pst.close();
        conn.close();

        // =====================================================
        // 5. SUCCESS MESSAGE
        // =====================================================

        if (inserted > 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Patient has been referred to a doctor.\n\n"
                    + "Doctor Queue Number: " + queueNumber + "\n"
                    + "Doctor Department: " + department + "\n"
                    + "Referral Reason: " + reason,
                    "Referral Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "The patient could not be referred.",
                    "Referral Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Error referring patient: "
                + e.getMessage(),
                "Referral Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
 private void loadCHWEscalation() {

    lblCHWEscalation.setVisible(false);
    txtCHWEscalation.setVisible(false);
    btnView.setVisible(false);
    btnAcknowledgs.setVisible(false);

    String sql =
        "SELECT " +
        "cf.FollowUpID, " +
        "cf.Status, " +
        "cf.Escalated, " +
        "cfr.FollowUpDate, " +
        "cfr.ReasonForMissing, " +
        "cfr.Outcome, " +
        "cfr.Notes, " +
        "s.FullName AS CHWName " +
        "FROM ChronicFollowUp cf " +
        "JOIN CHWFollowUpRecords cfr " +
        "ON cf.FollowUpID = cfr.FollowUpID " +
        "JOIN Staff s " +
        "ON cfr.CHWID = s.StaffID " +
        "WHERE cf.PatientID = ? " +
        "AND cf.Status = 'ESCALATED' " +
        "AND cf.Escalated = 1 " +
        "ORDER BY cfr.RecordID DESC " +
        "LIMIT 1";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, patientID);

        try (ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {

                String chwName =
                    rs.getString("CHWName");

                String followUpDate =
                    rs.getString("FollowUpDate");

                String reason =
                    rs.getString("ReasonForMissing");

                String outcome =
                    rs.getString("Outcome");

                String notes =
                    rs.getString("Notes");

                lblCHWEscalation.setVisible(true);
                txtCHWEscalation.setVisible(true);
                btnView.setVisible(true);
                btnAcknowledgs.setVisible(true);

                txtCHWEscalation.setText(
                    "CHW: " + chwName
                    + "\n"
                    + "Follow-Up Date: " + followUpDate
                    + "\n"
                    + "\n"
                    + "Reason for Missing:"
                    + "\n"
                    + reason
                    + "\n"
                    + "\n"
                    + "Outcome:"
                    + "\n"
                    + outcome
                    + "\n"
                    + "\n"
                    + "Notes:"
                    + "\n"
                    + notes
                    + "\n"
                    + "\n"
                    + "STATUS: ESCALATED"
                );
            }
        }

    } catch (SQLException e) {

        e.printStackTrace();

        JOptionPane.showMessageDialog(
            this,
            "Error loading CHW escalation:\n"
            + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}    
 private void viewCHWFollowUp() {

    String sql =
        "SELECT " +
        "cfr.FollowUpDate, " +
        "cfr.ContactMethod, " +
        "cfr.PatientContacted, " +
        "cfr.ReasonForMissing, " +
        "cfr.EncouragedReturn, " +
        "cfr.Outcome, " +
        "cfr.Notes, " +
        "s.FullName AS CHWName " +
        "FROM ChronicFollowUp cf " +
        "JOIN CHWFollowUpRecords cfr " +
        "ON cf.FollowUpID = cfr.FollowUpID " +
        "JOIN Staff s " +
        "ON cfr.CHWID = s.StaffID " +
        "WHERE cf.PatientID = ? " +
        "AND cf.Status = 'ESCALATED' " +
        "AND cf.Escalated = 1 " +
        "ORDER BY cfr.RecordID DESC " +
        "LIMIT 1";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, patientID);

        try (ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {

                String details =
                    "CHW FOLLOW-UP DETAILS\n\n"
                    + "CHW: "
                    + rs.getString("CHWName")
                    + "\n"
                    + "Follow-Up Date: "
                    + rs.getString("FollowUpDate")
                    + "\n"
                    + "Contact Method: "
                    + rs.getString("ContactMethod")
                    + "\n"
                    + "Patient Contacted: "
                    + rs.getString("PatientContacted")
                    + "\n\n"
                    + "Reason for Missing:\n"
                    + rs.getString("ReasonForMissing")
                    + "\n\n"
                    + "Encouraged to Return: "
                    + rs.getString("EncouragedReturn")
                    + "\n\n"
                    + "Outcome:\n"
                    + rs.getString("Outcome")
                    + "\n\n"
                    + "Notes:\n"
                    + rs.getString("Notes")
                    + "\n\n"
                    + "STATUS: ESCALATED";

                JOptionPane.showMessageDialog(
                    this,
                    details,
                    "CHW Follow-Up",
                    JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "No CHW escalation details were found.",
                    "No Follow-Up",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }

    } catch (SQLException e) {

        e.printStackTrace();

        JOptionPane.showMessageDialog(
            this,
            "Error loading CHW follow-up:\n"
            + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}   

private void acknowledgeCHWEscalation() {

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Has the CHW escalation been dealt with?\n\n"
        + "The follow-up will be marked as COMPLETE.",
        "Resolve CHW Escalation",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    String sql =
        "UPDATE ChronicFollowUp "
        + "SET Status = 'COMPLETE', "
        + "Escalated = 0 "
        + "WHERE PatientID = ? "
        + "AND Status = 'ESCALATED' "
        + "AND Escalated = 1";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, patientID);

        int updated = pst.executeUpdate();

        if (updated > 0) {

            JOptionPane.showMessageDialog(
                this,
                "CHW follow-up has been marked as COMPLETE.",
                "Follow-Up Completed",
                JOptionPane.INFORMATION_MESSAGE
            );

            lblCHWEscalation.setVisible(true);
            txtCHWEscalation.setVisible(true);
            btnView.setVisible(true);
            btnAcknowledgs.setVisible(true);

        } else {

            JOptionPane.showMessageDialog(
                this,
                "No active CHW escalation was found.",
                "No Escalation",
                JOptionPane.INFORMATION_MESSAGE
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error resolving CHW escalation:\n"
            + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

private String getDepartmentPrefix(String department) {

    if (department == null) {
        return "GEN";
    }

    switch (department.trim().toLowerCase()) {

        case "general consultation":
            return "GC";

        case "martenal health":
        case "maternal health":
            return "MH";

        case "child health":
            return "CH";

        case "chronic care":
            return "CC";

        case "tb/respiratory":
            return "TB";

        case "mental health":
            return "MHT";

        case "family planning":
            return "FP";

        case "dental":
            return "DEN";

        default:
            return "GEN";
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcknowledgs;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnPrescribe;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefer;
    private javax.swing.JButton btnView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JButton lblBack;
    private javax.swing.JLabel lblBp;
    private javax.swing.JLabel lblCHWEscalation;
    private javax.swing.JLabel lblDOB;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDep;
    private javax.swing.JLabel lblDiag;
    private javax.swing.JLabel lblFollow;
    private javax.swing.JLabel lblHR;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPatID;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblTemp;
    private javax.swing.JLabel lblWeight;
    private javax.swing.JTable tblHistory;
    private javax.swing.JTextArea txtCHWEscalation;
    private javax.swing.JTextArea txtComplaint;
    private javax.swing.JTextArea txtNotes;
    private javax.swing.JLabel txtPrescribed;
    private javax.swing.JTextArea txtTreat;
    // End of variables declaration//GEN-END:variables
}
