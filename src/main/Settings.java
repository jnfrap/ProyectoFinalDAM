package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import customComponents.CustomJButton;
import customComponents.CustomJButton.ButtonStyle;
import customComponents.CustomJPasswordField;
import customComponents.CustomJTextField;
import misc.AES256;
import misc.BDDConnection;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

public class Settings extends JFrame {

    private JPanel contentPane;
    private CustomJPasswordField pfActualPass;
    private CustomJPasswordField pfNewPass;
    private CustomJPasswordField pfConfirmPass;
    private CustomJTextField tfTextDeleteAccount;
    private CustomJButton btnDone;
    private CustomJPasswordField pfDeleteAccount;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Settings frame = new Settings();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Settings() {
        setUndecorated(true);
		getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 870, 330);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblSettings = new JLabel("Opciones");
        lblSettings.setBounds(406, 11, 100, 14);
        contentPane.add(lblSettings);
        
        JPanel panelChangePass = new JPanel();
        panelChangePass.setBounds(26, 36, 382, 243);
        contentPane.add(panelChangePass);
        panelChangePass.setLayout(null);
        panelChangePass.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));
        
        JLabel lblChangePass = new JLabel("Cambio de contraseña");
        lblChangePass.setBounds(123, 11, 169, 14);
        panelChangePass.add(lblChangePass);
        
        pfActualPass = new CustomJPasswordField();
        pfActualPass.setLabelText("Contraseña actual");
        pfActualPass.setBounds(10, 36, 362, 35);
        panelChangePass.add(pfActualPass);
        
        pfNewPass = new CustomJPasswordField();
        pfNewPass.setLabelText("Nueva contraseña");
        pfNewPass.setBounds(10, 92, 362, 35);
        panelChangePass.add(pfNewPass);
        
        pfConfirmPass = new CustomJPasswordField();
        pfConfirmPass.setLabelText("Confirmar contraseña");
        pfConfirmPass.setBounds(10, 150, 362, 35);
        panelChangePass.add(pfConfirmPass);
        
        CustomJButton btnChangePass = new CustomJButton("Cambiar contraseña");
        btnChangePass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AES256 decryptor = new AES256();
                String email = Main.getWelcomeLabel().getText().split("\\|")[0].trim();
                System.out.println(email);
                String actualPass = new String(pfActualPass.getPassword());
                String newPass = new String(pfNewPass.getPassword());
                String confirmPass = new String(pfConfirmPass.getPassword());
                
                boolean passwordMatch = false;
                if(newPass.equals(confirmPass)) {
                    passwordMatch = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden");
                }

                if (passwordMatch) {
                    try {
                        BDDConnection bdd = new BDDConnection();
                        Connection con = bdd.getConnection();
                        
                        PreparedStatement sentence = con.prepareStatement("SELECT email,password from usuarios where email=upper(?) and password=?");
                        sentence.setString(1, email.toUpperCase());
                        sentence.setString(2,decryptor.encrypt(actualPass));
                        ResultSet rs = sentence.executeQuery();
                        
                        if (rs.next()) {
                            sentence = con.prepareStatement("UPDATE usuarios SET password=? WHERE email=upper(?)");
                            sentence.setString(1, decryptor.encrypt(newPass));
                            sentence.setString(2, email.toUpperCase());
                            sentence.executeUpdate();
                            
                            sentence = con.prepareStatement("UPDATE usuarios SET activated=? WHERE email=upper(?)");
                            sentence.setInt(1, 1);
                            sentence.setString(2, email.toUpperCase());
                            sentence.executeUpdate();
                            
                            JOptionPane.showMessageDialog(null, "Contraseña cambiada");
                            btnDone.setEnabled(true);
                        }else {
                            JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
                        }

                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(null, "Error con la base de datos");
                        e1.printStackTrace();
                    }
                }
                
                pfActualPass.setText("");
                pfNewPass.setText("");
                pfConfirmPass.setText("");
            }
        });
        btnChangePass.setBounds(107, 206, 169, 23);
        panelChangePass.add(btnChangePass);
        
        btnDone = new CustomJButton("Terminar");
        btnDone.setStyle(ButtonStyle.DESTRUCTIVE);
        btnDone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Frame[] panels = Frame.getFrames();
                for (Frame frame : panels) {
                    if (frame.getTitle().equals("Incendios")) {
                        frame.setEnabled(true);
                        frame.toFront();
                        break;
                    }
                }
                dispose();
            }
        });
        btnDone.setBounds(380, 290, 89, 23);
        contentPane.add(btnDone);
        
        JPanel panel = new JPanel();
        panel.setBounds(459, 36, 382, 243);
        contentPane.add(panel);
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));
        
        JLabel lblDeleteAccount = new JLabel("Eliminar la cuenta");
        lblDeleteAccount.setBounds(127, 11, 123, 14);
        panel.add(lblDeleteAccount);
        
        JLabel lblWarning = new JLabel("Esta acción es permanente y no se puede deshacer");
        lblWarning.setBounds(61, 24, 300, 20);
        lblWarning.setForeground(Color.RED);
        panel.add(lblWarning);
        
        JLabel lblText = new JLabel("<html><body><p align=\"center\">Escribe lo siguiente:<br>Estoy totalmente seguro de que quiero eliminar mi cuenta<br>y entiendo lo que eso significa</p></body></html>");
        lblText.setBounds(34, 117, 327, 50);
        panel.add(lblText);
        
        tfTextDeleteAccount = new CustomJTextField();
        tfTextDeleteAccount.setLabelText("");
        tfTextDeleteAccount.setBounds(10, 165, 362, 35);
        panel.add(tfTextDeleteAccount);
        tfTextDeleteAccount.setColumns(10);
        
        CustomJButton btnDeleteAccount = new CustomJButton("Eliminar la cuenta");
        btnDeleteAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AES256 decryptor = new AES256();
                String email = Main.getWelcomeLabel().getText().split("\\|")[0].trim();
                String writtenText = tfTextDeleteAccount.getText();
                String writtenPass = new String(pfDeleteAccount.getPassword());
                String text = "Estoy totalmente seguro de que quiero eliminar mi cuenta y entiendo lo que eso significa";
                
                //Check if fields are not empty
                if (writtenText.isEmpty() || writtenPass.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Rellena todos los campos");
                } else {
                    //Check if the text is correct
                    if (writtenText.equals(text)) {
                        try {
                            BDDConnection bdd = new BDDConnection();
                            Connection con = bdd.getConnection();
                            
                            PreparedStatement sentence = con.prepareStatement("SELECT email,password,name,surname,phone,address,city,country,zip from usuarios where email=upper(?) and password=?");
                            sentence.setString(1, email.toUpperCase());
                            sentence.setString(2, decryptor.encrypt(writtenPass));
                            ResultSet rs = sentence.executeQuery();
                            
                            if (rs.next()) {
                                String dbEmail = rs.getString(1);
                                String dbPassword = rs.getString(2);
                                String dbName = rs.getString(3);
                                String dbSurname = rs.getString(4);
                                int dbPhone = rs.getInt(5);
                                String dbAdress = rs.getString(6);
                                String dbCity = rs.getString(7);
                                String dbCountry = rs.getString(8);
                                int dbZip = rs.getInt(9);
                                
                                sentence = con.prepareStatement("insert into deleted_users(email,password,name,surname,phone,address,city,country,zip) values(?,?,?,?,?,?,?,?,?)");
                                sentence.setString(1, dbEmail);
                                sentence.setString(2, dbPassword);
                                sentence.setString(3, dbName);
                                sentence.setString(4, dbSurname);
                                sentence.setInt(5, dbPhone);
                                sentence.setString(6, dbAdress);
                                sentence.setString(7, dbCity);
                                sentence.setString(8, dbCountry);
                                sentence.setInt(9, dbZip);
                                sentence.executeUpdate();
                                
                                sentence = con.prepareStatement("DELETE FROM usuarios WHERE email=upper(?)");
                                sentence.setString(1, email.toUpperCase());
                                sentence.executeUpdate();
                                
                                JOptionPane.showMessageDialog(null, "Cuenta eliminada");
                                System.exit(0);
                            }else {
                                JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
                            }
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(null, "Error con la base de datos");
                            e1.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El texto no es correcto");
                    }
                }
            }
        });
        btnDeleteAccount.setBounds(127, 209, 137, 23);
        btnDeleteAccount.setStyle(ButtonStyle.DANGER);
        panel.add(btnDeleteAccount);
        
        pfDeleteAccount = new CustomJPasswordField();
        pfDeleteAccount.setLabelText("Contraseña");
        pfDeleteAccount.setBounds(10, 74, 362, 35);
        panel.add(pfDeleteAccount);
    }
    
    public JButton getDoneButton() {
        return btnDone;
    }
}
