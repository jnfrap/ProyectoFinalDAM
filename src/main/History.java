package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import customComponents.CustomJButton;
import customComponents.CustomJButton.ButtonStyle;
import misc.BDDConnection;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

public class History extends JFrame {

    private JPanel contentPane;
    private static JTable table;
    private int pos;
    private int max;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    History frame = new History();
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
    public History() {
        pos=1;
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblUserAndDate = new JLabel("..........................................");
        lblUserAndDate.setBounds(274, 40, 317, 14);
        contentPane.add(lblUserAndDate);
        
        JLabel lblhistorial = new JLabel("<html><body><h1>Historial</h1></body></html>");
        lblhistorial.setBounds(346, 0, 218, 43);
        contentPane.add(lblhistorial);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 65, 880, 490);
        contentPane.add(scrollPane);
        
        max = 0;
        
        try {
            BDDConnection bdd = new BDDConnection();
            Connection con = bdd.getConnection();
            
            PreparedStatement sentence = con.prepareStatement("SELECT max(id) from peticiones");
            ResultSet rs = sentence.executeQuery();
            
            while(rs.next()) {
                max = rs.getInt(1);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        pos=max;
        
        //Make a table inside the scrollPane, dont add data yet
        table = new JTable();
        scrollPane.setViewportView(table);
        
        CustomJButton btnBack = new CustomJButton("<- Volver");
        btnBack.setStyle(ButtonStyle.DESTRUCTIVE);
        btnBack.addActionListener(new ActionListener() {
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
        btnBack.setBounds(10, 566, 89, 23);
        contentPane.add(btnBack);
        
        CustomJButton btnPrevious = new CustomJButton("<");
        btnPrevious.setStyle(ButtonStyle.SECONDARY);
        CustomJButton btnNext = new CustomJButton(">");
        btnNext.setStyle(ButtonStyle.SECONDARY);
        btnNext.setEnabled(false);
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnPrevious.setEnabled(true);
                pos++;
                if (pos==max) {
                    btnNext.setEnabled(false);
                }
                
                String data = fillTable(pos);
                lblUserAndDate.setText(data);
            }
        });
        btnPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnNext.setEnabled(true);
                pos--;
                if (pos==1) {
                    btnPrevious.setEnabled(false);
                }
                
                String data = fillTable(pos);
                lblUserAndDate.setText(data);
            }
        });
        btnPrevious.setBounds(223, 36, 41, 23);
        contentPane.add(btnPrevious);
        btnNext.setBounds(601, 36, 41, 23);
        contentPane.add(btnNext);
        
        lblUserAndDate.setText(fillTable(pos));
    }
    
    private static String fillTable(int pos){
        
        //Create random data to add to the table
        String[] columnNames = {"Nombre", "Idema", "Temperatura", "Humedad", "Viento", "Fecha", "Suelo"};

        //Create a model for the table
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
        
        int userID = 0;
        String date = null;
        String email = null;
        try {
            BDDConnection bdd = new BDDConnection();
            Connection con = bdd.getConnection();
            
            PreparedStatement sentence = con.prepareStatement("SELECT user_id,date FROM peticiones WHERE id=?");
            sentence.setInt(1, pos);
            ResultSet rs = sentence.executeQuery();
            
            while(rs.next()) {
                userID = rs.getInt(1);
                date = rs.getString(2);
            }
            
            PreparedStatement sentence2 = con.prepareStatement("SELECT email from usuarios where id=?");
            sentence2.setInt(1, userID);
            ResultSet rs2 = sentence2.executeQuery();
            
            while(rs2.next()) {
                email = rs2.getString(1);
            }
            
            PreparedStatement sentence3 = con.prepareStatement("SELECT * from llamadas where peticion_id=?");
            sentence3.setInt(1, pos);
            ResultSet rs3 = sentence3.executeQuery();
            
            while(rs3.next()) {
                String nombre = rs3.getString(3);
                String idema = rs3.getString(4);
                String temperatura = rs3.getString(5);
                String humedad = rs3.getString(6);
                String velViento = rs3.getString(7);
                String fecha = rs3.getString(8);
                String suelo = rs3.getString(9);
                
                model.addRow(new Object[]{nombre+"",idema+"",temperatura+"",humedad+"",velViento+"",fecha+"",suelo+""});
            }
            
            bdd.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        table.setModel(model);
        
        String[] dateParts = date.split("T");
        String[] dateParts2 = dateParts[0].split("-");
        String[] dateParts3 = dateParts[1].split(":");
        String[] dateParts4 = dateParts3[2].split("\\.");
        date = dateParts2[2]+" de "+getMonthName(Integer.parseInt(dateParts2[1]))+" de "+dateParts2[0]+", "+dateParts3[0]+":"+dateParts3[1]+":"+dateParts4[0];
        
        return email+", "+date;
    }

    private static String getMonthName(int parseInt) {
        switch (parseInt) {
        case 1:
            return "Enero";
        case 2:
            return "Febrero";
        case 3:
            return "Marzo";
        case 4:
            return "Abril";
        case 5:
            return "Mayo";
        case 6:
            return "Junio";
        case 7:
            return "Julio";
        case 8:
            return "Agosto";
        case 9:
            return "Septiembre";
        case 10:
            return "Octubre";
        case 11:
            return "Noviembre";
        case 12:
            return "Diciembre";
        default:
            return "Error";
        }
    }
}
