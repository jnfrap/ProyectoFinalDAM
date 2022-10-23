package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import misc.AES256;
import misc.BDDConnection;
import misc.Utils;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField tfLoginEmail;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
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
	public Login() {
		setUndecorated(true);
		getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		tfLoginEmail = new JTextField();
		tfLoginEmail.setBounds(121, 55, 169, 20);
		contentPane.add(tfLoginEmail);
		tfLoginEmail.setColumns(10);
		
		JLabel lblLogin = new JLabel("Inicia Sesión");
		lblLogin.setBounds(176, 11, 90, 14);
		contentPane.add(lblLogin);
		
		JLabel lblCorreo = new JLabel("Correo electronico");
		lblCorreo.setBounds(121, 40, 145, 14);
		contentPane.add(lblCorreo);
		
		JLabel lblNewLabel = new JLabel("Contraseña");
		lblNewLabel.setBounds(122, 86, 72, 14);
		contentPane.add(lblNewLabel);
		
		JButton btnLogin = new JButton("Iniciar sesión");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tfLoginEmail.getText().equals("") || passwordField.getPassword().equals("")) {
					JOptionPane.showMessageDialog(null, "Rellena los datos de inicio de sesión para continuar");
				}else if(!Utils.checkIfEmail(tfLoginEmail.getText())) {
					JOptionPane.showMessageDialog(null, "Introduce un email valido");
				}else {
					String email = tfLoginEmail.getText();
					String pass = new String(passwordField.getPassword());
					
					boolean userExist = false;
					try {
						AES256 decryptor = new AES256();
						BDDConnection bdd = new BDDConnection();
						Connection con = bdd.getConnection();
						
						PreparedStatement sentence = con.prepareStatement("SELECT email,password,name,activated from usuarios where email=upper(?) and password=?");
						sentence.setString(1, email.toUpperCase());
						sentence.setString(2,decryptor.encrypt(pass));
						ResultSet rs = sentence.executeQuery();
						
						while(rs.next()) {
							String reUser = rs.getString(1);
							String rePass = decryptor.decrypt(rs.getString(2));
							
							if (reUser.equalsIgnoreCase(email) && rePass.equals(pass)) {
								userExist = true;
								break;
							}
						}
						bdd.closeConnection();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					if (userExist) {
						Frame[] panels = Frame.getFrames();
						for (Frame frame : panels) {
							if (frame.getTitle().equals("Incendios")) {
								frame.setEnabled(true);
								frame.toFront();
								break;
							}
						}
						JOptionPane.showMessageDialog(null, "Bienvenido");
						dispose();
					}else {
						JOptionPane.showMessageDialog(null, "El correo y/o la contraseña no coinciden");
					}
				}
			}
		});
		btnLogin.setBounds(154, 133, 122, 23);
		contentPane.add(btnLogin);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(121, 102, 169, 20);
		contentPane.add(passwordField);
		
		passwordField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLogin.doClick();
			}
		});
		
		JLabel lblNoAccount = new JLabel("¿No tienes una cuenta?");
		lblNoAccount.setBounds(154, 171, 136, 14);
		contentPane.add(lblNoAccount);
		
		JButton btnRegister = new JButton("Registrate");
		btnRegister.setBounds(169, 187, 97, 23);
		contentPane.add(btnRegister);
		
		JButton btnSalir = new JButton("Salir");
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnSalir.setBounds(176, 221, 89, 23);
		contentPane.add(btnSalir);
	}
}
