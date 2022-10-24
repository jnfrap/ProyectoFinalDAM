package main;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Settings extends JFrame {

    private JPanel contentPane;
    private JPasswordField pfActualPass;
    private JPasswordField pfNewPass;
    private JPasswordField pfConfirmPass;
    private JTextField tfText;
    private JButton btnDone;

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
        lblSettings.setBounds(406, 11, 46, 14);
        contentPane.add(lblSettings);
        
        JPanel panelChangePass = new JPanel();
        panelChangePass.setBounds(26, 36, 382, 243);
        contentPane.add(panelChangePass);
        panelChangePass.setLayout(null);
        panelChangePass.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));
        
        JLabel lblChangePass = new JLabel("Cambio de contraseña");
        lblChangePass.setBounds(123, 11, 169, 14);
        panelChangePass.add(lblChangePass);
        
        JLabel lblActualPass = new JLabel("Contraseña actual");
        lblActualPass.setBounds(10, 36, 134, 14);
        panelChangePass.add(lblActualPass);
        
        pfActualPass = new JPasswordField();
        pfActualPass.setBounds(10, 61, 362, 20);
        panelChangePass.add(pfActualPass);
        
        JLabel lblNewPass = new JLabel("Nueva contraseña");
        lblNewPass.setBounds(10, 92, 149, 14);
        panelChangePass.add(lblNewPass);
        
        pfNewPass = new JPasswordField();
        pfNewPass.setBounds(10, 117, 362, 20);
        panelChangePass.add(pfNewPass);
        
        JLabel lblConfirmPass = new JLabel("Confirmar contraseña");
        lblConfirmPass.setBounds(10, 150, 159, 14);
        panelChangePass.add(lblConfirmPass);
        
        pfConfirmPass = new JPasswordField();
        pfConfirmPass.setBounds(10, 175, 362, 20);
        panelChangePass.add(pfConfirmPass);
        
        JButton btnChangePass = new JButton("Cambiar contraseña");
        btnChangePass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actualPass = new String(pfActualPass.getPassword());
                String newPass = new String(pfNewPass.getPassword());
                String confirmPass = new String(pfConfirmPass.getPassword());

                //Comprobar si actualPass es correcta con la base de datos
                //Comprobar si newPass y confirmPass son iguales
                //Cambiar la contraseña de la base de datos
                //Activar la cuenta
                //Mensaje de bienvenida
                //Activar frame Main
                //dispose();
            }
        });
        btnChangePass.setBounds(123, 206, 134, 23);
        panelChangePass.add(btnChangePass);
        
        btnDone = new JButton("Terminar");
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
        lblWarning.setBounds(50, 36, 300, 20);
        lblWarning.setForeground(Color.RED);
        panel.add(lblWarning);
        
        JLabel lblText = new JLabel("<html><body><p align=\"center\">Escribe lo siguiente:<br>Estoy totalmente seguro de que quiero eliminar mi cuenta<br>y entiendo lo que eso significa</p></body></html>");
        lblText.setBounds(34, 67, 327, 50);
        panel.add(lblText);
        
        tfText = new JTextField();
        tfText.setBounds(10, 128, 362, 20);
        panel.add(tfText);
        tfText.setColumns(10);
        
        JButton btnDeleteAccount = new JButton("Eliminar la cuenta");
        btnDeleteAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        btnDeleteAccount.setBounds(127, 186, 123, 23);
        btnDeleteAccount.setBackground(new Color(255, 102, 102));
        panel.add(btnDeleteAccount);
    }
    
    public JButton getDoneButton() {
        return btnDone;
    }
}
