package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import misc.BDDConnection;
import misc.Municipio;
import misc.Resultados;
import misc.Utils;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

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
        
        String email = Main.getWelcomeLabel().getText().split("\\|")[0].trim();
        String currentDate = LocalDateTime.now().toString();
        int petitionID = -1;
        int userID = -1;
        try {
            BDDConnection bdd = new BDDConnection();
            Connection con = bdd.getConnection();
            
            //User ID
            PreparedStatement sentence = con.prepareStatement("SELECT id from usuarios where UPPER(email)=?");
            sentence.setString(1, email.toUpperCase());
            ResultSet rs = sentence.executeQuery();
            
            while(rs.next()) {
                userID = rs.getInt(1);
            }

            //Petition ID
            PreparedStatement sentence2 = con.prepareStatement("SELECT max(id) from peticiones");
            ResultSet rs2 = sentence2.executeQuery();
            while(rs2.next()) {
                petitionID = rs2.getInt(1)+1;
            }
            
            bdd.closeConnection();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        //Recoger datos////////////
        String dataLabel = Main.getDataLabel().getText();
        String[] data = dataLabel.split("\\|");
        double tlLat = Double.parseDouble(data[0]);
        double tlLon = Double.parseDouble(data[1]);
        double brLat = Double.parseDouble(data[2]);
        double brLon = Double.parseDouble(data[3]);
        
        JSONObject json = new JSONObject(Utils.getFileContent("estacionesAEMET.json"));
        JSONArray estacionesJsonArray = json.getJSONArray("estaciones");
        
        ArrayList<Resultados> finalArray = new ArrayList<Resultados>();

        for (int i=0;i<estacionesJsonArray.length();i++) {
            double lat = Double.parseDouble(estacionesJsonArray.getJSONObject(i).getString("lat"));
            double lon = Double.parseDouble(estacionesJsonArray.getJSONObject(i).getString("lon"));

            if ((lat<tlLat && lat>brLat) && (lon>tlLon && lon<brLon)) {
                ArrayList<Resultados> array = Utils.getAemetStationAPIResponse(estacionesJsonArray.getJSONObject(i).getString("idema"));
                for (int j=0;j<array.size();j++) {
                    finalArray.add(array.get(j));
                }
            }
        }
        
        ArrayList<Municipio> municipios = Utils.getMunicipiosArray();
        
        for(int i=0;i<municipios.size();i++) {
            double lat = municipios.get(i).getLatitud();
            double lon = municipios.get(i).getLongitud();
            
            if ((lat<tlLat && lat>brLat) && (lon>tlLon && lon<brLon)) {                
                JSONObject jsonMun = new JSONObject(Utils.getOpenWeatherAPIResponse(lat+"",lon+""));
                JSONObject jsonMain = jsonMun.getJSONObject("main");
                JSONObject jsonWind = jsonMun.getJSONObject("wind");
                
                float temperatura = jsonMain.getFloat("temp")-273.15f;
                float humedad = jsonMain.getFloat("humidity");
                float velViento = jsonWind.getFloat("speed");
                
                ArrayList<String> tiposSuelo = Utils.getSoilGridsAPIResponse(lat+"",lon+"");
                
                model.addRow(new Object[]{municipios.get(i).getNombre(),"",temperatura,humedad,velViento,LocalDateTime.now(),tiposSuelo.toString()});
                try {
                    BDDConnection bdd = new BDDConnection();
                    Connection con = bdd.getConnection();
                    
                    Statement stmt=con.createStatement();
                    ResultSet rs1 = stmt.executeQuery("SELECT max(id) from llamadas");
                    rs1.next();
                    int id = (Integer)rs1.getInt(1)+1;
                    String query = "insert into llamadas (id,peticion_id,nombre,idema,temperatura,humedad,velViento,fecha,suelo) values (?,?,?,?,?,?,?,?,?)";
                    PreparedStatement preparedStmt = con.prepareStatement(query);
                    preparedStmt.setInt(1, id);
                    preparedStmt.setInt(2, petitionID);
                    preparedStmt.setString(3, municipios.get(i).getNombre());
                    preparedStmt.setString(4, "");
                    preparedStmt.setString(5, temperatura+"");
                    preparedStmt.setString(6, humedad+"");
                    preparedStmt.setString(7, velViento+"");
                    preparedStmt.setString(8, currentDate);
                    preparedStmt.setString(9, tiposSuelo.toString());
                    preparedStmt.execute();
                    
                    bdd.closeConnection();
                }catch(Exception e) {
                    System.out.println("Error insertando llamada, primer bloque");
                    e.printStackTrace();
                }
            }
        }
        
        //Add data to the model
        for (int i=0;i<finalArray.size();i++) {
            Resultados r = finalArray.get(i);
            model.addRow(new Object[]{"<html><body><b>(AEMET)</b>"+r.getNombre()+"</body></html>", r.getIdema(), r.getTemperatura(), r.getHumedad(),r.getVelViento(),r.getFecha()});
            
            try {
                BDDConnection bdd = new BDDConnection();
                Connection con = bdd.getConnection();
                
                Statement stmt=con.createStatement();
                ResultSet rs1 = stmt.executeQuery("SELECT max(id) from llamadas");
                rs1.next();
                int id = (Integer)rs1.getInt(1)+1;
                String query = "insert into llamadas (id,peticion_id,nombre,idema,temperatura,humedad,velViento,fecha,suelo) values (?,?,?,?,?,?,?,?,?)";
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setInt(1, id);
                preparedStmt.setInt(2, petitionID);
                preparedStmt.setString(3, "<html><body><b>(AEMET)</b>"+r.getNombre()+"</body></html>");
                preparedStmt.setString(4, r.getIdema());
                preparedStmt.setString(5, r.getTemperatura()+"");
                preparedStmt.setString(6, r.getHumedad()+"");
                preparedStmt.setString(7, r.getVelViento()+"");
                preparedStmt.setString(8, r.getFecha());
                preparedStmt.setString(9, "");
                preparedStmt.execute();
                
                bdd.closeConnection();
            }catch(Exception e) {
                System.out.println("Error insertando llamada, segundo bloque");
                e.printStackTrace();
            }
        }
        
        try {
            BDDConnection bdd = new BDDConnection();
            Connection con = bdd.getConnection();
            
            Statement stmt=con.createStatement();
            ResultSet rs1 = stmt.executeQuery("SELECT max(id) from peticiones");
            rs1.next();
            int id = (Integer)rs1.getInt(1)+1;
            String query = "insert into peticiones (id,user_id,date) values (?,?,?)";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1, id);
            preparedStmt.setInt(2, userID);
            preparedStmt.setString(3, currentDate);
            preparedStmt.execute();
            
            bdd.closeConnection();
        }catch(Exception e) {
            System.out.println("Error insertando peticion");
            e.printStackTrace();
        }

        //Set the model to the table
        table.setModel(model);
        Main.getDataLabel().setText("");        
    }
}
