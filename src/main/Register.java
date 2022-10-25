package main;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import misc.AES256;
import misc.BDDConnection;
import misc.Utils;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class Register extends JFrame {

	private JPanel contentPane;
	private JTextField tfEmail;
	private JTextField tfName;
	private JTextField tfSurname;
	private JTextField tfPhone;
	private JTextField tfAddress;
	private JTextField tfCity;
	private JTextField tfCountry;
	private JTextField tfZip;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Register frame = new Register();
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
	public Register() {
		setUndecorated(true);
		getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblRegisterTitle = new JLabel("Formulario de registro");
		lblRegisterTitle.setBounds(143, 11, 148, 14);
		contentPane.add(lblRegisterTitle);
		
		JPanel panel = new JPanel();
		panel.setBounds(65, 47, 283, 449);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblRegisterEmail = new JLabel("Correo electronico");
		lblRegisterEmail.setBounds(24, 11, 108, 14);
		panel.add(lblRegisterEmail);
		
		tfEmail = new JTextField();
		tfEmail.setBounds(24, 33, 237, 20);
		panel.add(tfEmail);
		tfEmail.setColumns(10);
		
		JLabel lblName = new JLabel("Nombre");
		lblName.setBounds(24, 64, 65, 14);
		panel.add(lblName);
		
		tfName = new JTextField();
		tfName.setBounds(24, 84, 237, 20);
		panel.add(tfName);
		tfName.setColumns(10);
		
		JLabel lblSurname = new JLabel("Apellido");
		lblSurname.setBounds(24, 114, 65, 14);
		panel.add(lblSurname);
		
		tfSurname = new JTextField();
		tfSurname.setBounds(24, 139, 237, 20);
		panel.add(tfSurname);
		tfSurname.setColumns(10);
		
		JLabel lblPhonel = new JLabel("Teléfono");
		lblPhonel.setBounds(24, 169, 76, 14);
		panel.add(lblPhonel);
		
		tfPhone = new JTextField();
		tfPhone.setBounds(24, 194, 237, 20);
		panel.add(tfPhone);
		tfPhone.setColumns(10);

		tfPhone.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if(!(Character.isDigit(c) || (c==KeyEvent.VK_BACK_SPACE) || (c==KeyEvent.VK_DELETE))) {
					getToolkit().beep();
					e.consume();
				}
				if(tfPhone.getText().length()>=9) {
					e.consume();
				}
			}
		});
		
		JLabel lblAddress = new JLabel("Dirección");
		lblAddress.setBounds(24, 225, 65, 14);
		panel.add(lblAddress);
		
		tfAddress = new JTextField();
		tfAddress.setBounds(24, 250, 237, 20);
		panel.add(tfAddress);
		tfAddress.setColumns(10);
		
		JLabel lblCity = new JLabel("Ciudad");
		lblCity.setBounds(24, 281, 65, 14);
		panel.add(lblCity);
		
		tfCity = new JTextField();
		tfCity.setBounds(24, 306, 237, 20);
		panel.add(tfCity);
		tfCity.setColumns(10);
		
		JLabel lblCountry = new JLabel("País");
		lblCountry.setBounds(24, 337, 65, 14);
		panel.add(lblCountry);
		
		tfCountry = new JTextField();
		tfCountry.setBounds(24, 362, 237, 20);
		panel.add(tfCountry);
		tfCountry.setColumns(10);
		
		JLabel lblZip = new JLabel("Código postal");
		lblZip.setBounds(24, 393, 123, 14);
		panel.add(lblZip);
		
		tfZip = new JTextField();
		tfZip.setBounds(24, 418, 237, 20);
		panel.add(tfZip);
		tfZip.setColumns(10);

		tfZip.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if(!(Character.isDigit(c) || (c==KeyEvent.VK_BACK_SPACE) || (c==KeyEvent.VK_DELETE))) {
					getToolkit().beep();
					e.consume();
				}
				if(tfZip.getText().length()>=5) {
					e.consume();
				}
			}
		});
		
		JButton btnRegister = new JButton("Registrarse");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AES256 decryptor = new AES256();
				boolean allCorrect = true;
				
				String email = tfEmail.getText();
				String name = tfName.getText();
				String surname = tfSurname.getText();
				int phone = 0;
				try {phone = Integer.parseInt(tfPhone.getText());}catch(Exception ex){}
				String address = tfAddress.getText();
				String city = tfCity.getText();
				String country = tfCountry.getText();
				int zip = 0;
				try {zip = Integer.parseInt(tfZip.getText());}catch(Exception ex){}
				String hash = decryptor.encrypt(email+name+phone+country);

				String pass = Utils.generatePassword(32);
				
				if(email.equals("") || name.equals("") || surname.equals("") || tfPhone.getText().equals("") || address.equals("") || city.equals("") || country.equals("") || tfZip.getText().equals("")) {
					allCorrect = false;
					JOptionPane.showMessageDialog(null, "Por favor, rellena todos los campos");
				}

				if (!Utils.checkIfEmail(email)) {
					allCorrect = false;
					JOptionPane.showMessageDialog(null, "Introduce un correo válido");
				}

				if (String.valueOf(phone).length() != 9) {
					allCorrect = false;
					JOptionPane.showMessageDialog(null, "El teléfono debe tener 9 dígitos");
				}

				String szip = ""+zip;
				if (szip.length() == 4) {
				    szip = "0"+szip;
				}
				
				if (szip.length() != 5) {
					allCorrect = false;
					JOptionPane.showMessageDialog(null, "El código postal debe tener 5 dígitos");
				}
				
				if(allCorrect) {
					boolean registered = false;
					try {
						BDDConnection bdd = new BDDConnection();
						Connection con = bdd.getConnection();
						
						Statement stmt=con.createStatement();
						PreparedStatement sentence = con.prepareStatement("select email from usuarios where email=upper(?)");
						sentence.setString(1, email.toUpperCase());
						ResultSet rs = sentence.executeQuery();
						
						
						while(rs.next()) {
							String reUser = rs.getString(1);
							
							if (reUser.equalsIgnoreCase(email)) {
								registered=true;
								break;
							}
						}
						
						if (!registered) {
							ResultSet rs1 = stmt.executeQuery("SELECT max(id) from usuarios");
							rs1.next();
							int id = (Integer)rs1.getInt(1)+1;
				            String query = "INSERT INTO usuarios (id, email, password, name, surname, phone, address, city, country, zip, hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				            PreparedStatement preparedStmt = con.prepareStatement(query);
				            preparedStmt.setInt(1, id);
				            preparedStmt.setString(2, email);
				            preparedStmt.setString(3, decryptor.encrypt(pass));
				            preparedStmt.setString(4, name);
				            preparedStmt.setString(5, surname);
				            preparedStmt.setInt(6, phone);
				            preparedStmt.setString(7, address);
				            preparedStmt.setString(8, city);
				            preparedStmt.setString(9, country);
				            preparedStmt.setInt(10, zip);
				            preparedStmt.setString(11, hash);
				            preparedStmt.execute();
				            
				            String subject = name+", activa tu cuenta en Incendios";
				            String body = "Hola "+name+", gracias por registrarte en nuestra aplicación."
				            		+ "\nEsta es tu contraseña de acceso: "+pass
				            		+ "\nSi no has creado una cuenta, ignora este mensaje";
				            Utils.sendEmail(email,subject,body);
				            
				            JOptionPane.showMessageDialog(null, "Revisa tu correo electrónico para obtener tu contraseña: "+email);
				            bdd.closeConnection();
							Login login = new Login();
							login.setVisible(true);
							login.setLocationRelativeTo(null);
				            dispose();
						}else {
							JOptionPane.showMessageDialog(null, "Este correo ya está registrado");
						}
						bdd.closeConnection();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnRegister.setBounds(187, 514, 161, 23);
		contentPane.add(btnRegister);
		
		JButton btnBack = new JButton("Volver");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login login = new Login();
				login.setVisible(true);
				login.setLocationRelativeTo(null);
				dispose();
			}
		});
		btnBack.setBounds(65, 514, 112, 23);
		contentPane.add(btnBack);
	}
}
