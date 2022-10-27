package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;

public class Results extends JFrame {

    private JPanel contentPane;
    private JTable table;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Results frame = new Results();
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
    public Results() {
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JButton btnBack = new JButton("<- Volver");
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnBack.setBounds(10, 566, 89, 23);
        contentPane.add(btnBack);
        
        JLabel lblResults = new JLabel("<html><body><h1>Resultados</h1></body></html>");
        lblResults.setBounds(344, 11, 218, 43);
        contentPane.add(lblResults);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 65, 880, 490);
        contentPane.add(scrollPane);
        
        //Make a table inside the scrollPane, dont add data yet
        table = new JTable();
        scrollPane.setViewportView(table);

        //Create random data to add to the table
        String[] columnNames = {"Nombre", "Idema", "Temperatura", "Humedad", "Viento", "Fecha", "Suelo"};

        //Create a model for the table
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        //Add data to the model
        for (int i=0;i<30;i++) {
            model.addRow(new Object[]{"1", "John", "Doe", "25"});
        }

        //Set the model to the table
        table.setModel(model);
        
    }
}
