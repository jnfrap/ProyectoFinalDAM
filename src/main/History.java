package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class History extends JFrame {

    private JPanel contentPane;

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
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblhistorial = new JLabel("<html><body><h1>Historial</h1></body></html>");
        lblhistorial.setBounds(349, 11, 218, 43);
        contentPane.add(lblhistorial);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 65, 880, 490);
        contentPane.add(scrollPane);
        
        JButton btnBack = new JButton("<- Volver");
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
    }
}
