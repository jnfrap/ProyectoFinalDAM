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
import misc.Utils;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private JPanel contentPane;
	private CustomJTextField tfLoginEmail;
	private CustomJPasswordField passwordField;

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
		getRootPane().setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.DARK_GRAY));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		tfLoginEmail = new CustomJTextField();
		tfLoginEmail.setLabelText("Correo electronico");
		tfLoginEmail.setBounds(121, 45, 169, 35);
		contentPane.add(tfLoginEmail);
		tfLoginEmail.setColumns(10);
		
		JLabel lblLogin = new JLabel("Inicia Sesión");
		lblLogin.setBounds(176, 11, 90, 14);
		contentPane.add(lblLogin);
		
		CustomJButton btnLogin = new CustomJButton("Iniciar sesión");
		btnLogin.addActionListener(new ActionListener() {
			@SuppressWarnings("unlikely-arg-type")
            public void actionPerformed(ActionEvent e) {
				if (tfLoginEmail.getText().equals("") || passwordField.getPassword().equals("")) {
					JOptionPane.showMessageDialog(null, "Rellena los datos de inicio de sesión para continuar");
				}else if(!Utils.checkIfEmail(tfLoginEmail.getText())) {
					JOptionPane.showMessageDialog(null, "Introduce un email valido");
				}else {
					String email = tfLoginEmail.getText();
					String pass = new String(passwordField.getPassword());
					
					boolean userExist = false;
					String reName = null;
					int activated = 0;
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
							reName = rs.getString(3);
							activated = rs.getInt(4);
							
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
							if (frame.getTitle().equals("WildFireTraker")) {
								frame.setEnabled(true);
								frame.toFront();
								break;
							}
						}
						if (activated == 1) {
							JOptionPane.showMessageDialog(null, "Bienvenido, "+reName);
							dispose();
						}else {
						    JOptionPane.showMessageDialog(null, "<html><body>Bienvenido, "+reName+"<br>Por favor, cambia tu contraseña</body></html>");
						    Settings settings = new Settings();
						    settings.setVisible(true);
						    settings.setTitle("Opciones");
						    settings.setLocationRelativeTo(null);
						    settings.getDoneButton().setEnabled(false);
			                dispose();
						}
						Main.getWelcomeLabel().setText(email+" | Bienvenido, "+reName);
						Main.getWelcomeLabel2().setText("Bienvenido, "+reName);
					}else {
						JOptionPane.showMessageDialog(null, "El correo y/o la contraseña no coinciden");
					}
				}
			}
		});
		btnLogin.setBounds(154, 133, 122, 23);
		contentPane.add(btnLogin);
		
		passwordField = new CustomJPasswordField();
		passwordField.setLabelText("Contraseña");
		passwordField.setBounds(121, 91, 169, 35);
		contentPane.add(passwordField);
		
		passwordField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLogin.doClick();
			}
		});
		
		JLabel lblNoAccount = new JLabel("¿No tienes una cuenta?");
		lblNoAccount.setBounds(154, 171, 136, 14);
		contentPane.add(lblNoAccount);
		
		CustomJButton btnRegister = new CustomJButton("Registrate");
		btnRegister.setStyle(ButtonStyle.SECONDARY);
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Register rFrame = new Register();
				rFrame.setVisible(true);
				rFrame.setTitle("Registro");
				rFrame.setLocationRelativeTo(null);
				dispose();
			}
		});
		btnRegister.setBounds(169, 187, 97, 23);
		contentPane.add(btnRegister);
		
		CustomJButton btnSalir = new CustomJButton("Salir");
		btnSalir.setStyle(ButtonStyle.DESTRUCTIVE);
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnSalir.setBounds(176, 221, 89, 23);
		contentPane.add(btnSalir);
	}
}
