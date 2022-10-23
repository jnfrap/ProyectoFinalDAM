package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPasswordField;

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
		lblCorreo.setBounds(121, 40, 103, 14);
		contentPane.add(lblCorreo);
		
		JLabel lblNewLabel = new JLabel("Contraseña");
		lblNewLabel.setBounds(122, 86, 72, 14);
		contentPane.add(lblNewLabel);
		
		JButton btnLogin = new JButton("Iniciar sesión");
		btnLogin.setBounds(154, 133, 104, 23);
		contentPane.add(btnLogin);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(121, 102, 169, 20);
		contentPane.add(passwordField);
		
		JLabel lblNoAccount = new JLabel("¿No tienes una cuenta?");
		lblNoAccount.setBounds(154, 171, 112, 14);
		contentPane.add(lblNoAccount);
		
		JButton btnRegister = new JButton("Registrate");
		btnRegister.setBounds(169, 187, 89, 23);
		contentPane.add(btnRegister);
	}
}
