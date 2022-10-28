package main;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import misc.BDDConnection;
import misc.Municipio;
import misc.Resultados;
import misc.Utils;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class Main extends JFrame {
	private JTextField textField;
	private static JLabel lblWelcome = new JLabel("Bienvenido, ");
	private static JLabel lblData = new JLabel("Data");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setTitle("Incendios");
					Login lFrame = new Login();
					lFrame.setTitle("Login");
					frame.setVisible(true);
					frame.setEnabled(false);
					lFrame.setVisible(true);
					lFrame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 452, 314);
		getContentPane().setLayout(null);
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.setMinimumSize(new Dimension(1280, 720));

		JLabel lblCoords = new JLabel("Coordenadas:");
        lblCoords.setBounds(20, 123, 497, 14);
        getContentPane().add(lblCoords);
		
		///////////////////Map
		JButton btnZoomIn = new JButton("+");
		JButton btnZoomOut = new JButton("-");
		TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setThreadPoolSize(8);
        GeoPosition center = new GeoPosition(38.9942400,-1.8564300);
        
        JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setLocation(0, 0);
        getContentPane().add(mapViewer);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(center);
        mapViewer.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton btnGoToSettings = new JButton("Opciones");
        btnGoToSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Settings settings = new Settings();
                settings.setTitle("Settings");
                settings.setVisible(true);
				settings.setLocationRelativeTo(null);
                setEnabled(false);
            }
        });
        btnGoToSettings.setBounds(20, 647, 89, 23);
        btnGoToSettings.setLocation(10,30);
        getContentPane().add(btnGoToSettings);
        
        //Set location of things
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            	int w = (int)Math.round(e.getComponent().getSize().width);
            	int h = (int)Math.round(e.getComponent().getSize().height);
            	int wm = (int)Math.round(w*0.68);
            	int hm = (int)Math.round(h*0.9);
            	int ws = (int)Math.round(w*0.3);
            	int hs = (h-(hm))/2;
            	mapViewer.setLocation(ws,hs/2);
            	mapViewer.setSize(wm,hm);
            	
            	btnZoomIn.setLocation(ws-(btnZoomIn.getSize().width+5), hs);
            	btnZoomOut.setLocation(btnZoomIn.getLocation().x, hs+btnZoomIn.getSize().height+5);
            }
        });
        
        //Interaction with the map
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);

        mapViewer.addMouseListener(new CenterMapListener(mapViewer));

        //mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
        
        btnZoomIn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		mapViewer.setZoom(mapViewer.getZoom()-1);
        	}
        });
        btnZoomIn.setBounds(10, 11, 41, 41);
        getContentPane().add(btnZoomIn);

        btnZoomOut.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (mapViewer.getZoom()<7) {
        			mapViewer.setZoom(mapViewer.getZoom()+1);
        		}
        	}
        });
        btnZoomOut.setBounds(20, 56, 41, 41);
        getContentPane().add(btnZoomOut);
        
        //WayPoint
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        Waypoint wp = new Waypoint() {
            @Override
            public GeoPosition getPosition() {
                return new GeoPosition(38.9942400,-1.8564300);
            }
        };
        waypointPainter.setWaypoints(java.util.Collections.singleton(wp));
        mapViewer.setOverlayPainter(waypointPainter);
        lblCoords.setText("Coordenadas: 38.9942400, -1.8564300");
        
        JComboBox<String> cbMunicipios = new JComboBox<String>();
        cbMunicipios.setBounds(20, 179, 224, 41);
        getContentPane().add(cbMunicipios);
        
        cbMunicipios.removeAllItems();
		ArrayList<Municipio> municipios = Utils.getMunicipiosArray();
		for (int i = 0; i < municipios.size(); i++) {
			cbMunicipios.addItem(municipios.get(i).getNombre());
		}
        
        JButton btnSubmitMunicipio = new JButton("Ir");
        btnSubmitMunicipio.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String index = cbMunicipios.getSelectedItem().toString();
        		
        		Municipio municipio = null;
        		for (int i=0;i<municipios.size();i++) {
        			if (index.equals(municipios.get(i).getNombre())) {
        				municipio = municipios.get(i);
        			}
        		}
        		
        		double lat = municipio.getLatitud();
        		double lon = municipio.getLongitud();
        		mapViewer.setAddressLocation(new GeoPosition(lat,lon));
        	}
        });
        btnSubmitMunicipio.setBounds(86, 283, 89, 23);
        getContentPane().add(btnSubmitMunicipio);
        
        textField = new JTextField();
        textField.setBounds(20, 231, 224, 41);
        getContentPane().add(textField);
        textField.setColumns(10);
        
        lblWelcome.setBounds(20, 11, 241, 14);
        getContentPane().add(lblWelcome);
        
        JButton btnExit = new JButton("Salir");
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnExit.setBounds(104, 30, 89, 23);
        getContentPane().add(btnExit);
        
        JButton btnHistory = new JButton("Historial");
        btnHistory.setBounds(129, 627, 89, 23);
        getContentPane().add(btnHistory);
        
        JPanel panelInfoMarker = new JPanel();
        panelInfoMarker.setBounds(20, 386, 277, 129);
        getContentPane().add(panelInfoMarker);
        panelInfoMarker.setLayout(null);

        panelInfoMarker.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        
        JLabel lblTemperatura = new JLabel("Temperatura:");
        lblTemperatura.setBounds(10, 11, 241, 14);
        panelInfoMarker.add(lblTemperatura);
        
        JLabel lblHumedad = new JLabel("Humedad:");
        lblHumedad.setBounds(10, 36, 224, 14);
        panelInfoMarker.add(lblHumedad);
        
        JLabel lblVelViento = new JLabel("Velocidad del viento:");
        lblVelViento.setBounds(10, 61, 241, 14);
        panelInfoMarker.add(lblVelViento);
        
        JLabel lblTipoSuelo = new JLabel("Tipo de suelo predominante:");
        lblTipoSuelo.setBounds(10, 86, 474, 14);
        panelInfoMarker.add(lblTipoSuelo);
        
        JLabel lblRiskOfFire = new JLabel("Riesgo de incendio:");
        lblRiskOfFire.setBounds(10, 111, 258, 14);
        panelInfoMarker.add(lblRiskOfFire);
        
        textField.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyReleased(KeyEvent e) {
        	    String text = textField.getText();
                cbMunicipios.removeAllItems();
                ArrayList<Municipio> municipios = Utils.getMunicipiosArray();
                for (int i = 0; i < municipios.size(); i++) {
                    if (municipios.get(i).getNombre().toLowerCase().contains(text.toLowerCase())) {
                        cbMunicipios.addItem(municipios.get(i).getNombre());
                    }
                }
				
        		
        		if (e.getKeyCode()==KeyEvent.VK_ENTER) {
        			String index = cbMunicipios.getSelectedItem().toString();
            		
            		Municipio municipio = null;
            		for (int i=0;i<municipios.size();i++) {
            			if (index.equals(municipios.get(i).getNombre())) {
            				municipio = municipios.get(i);
            			}
            		}            		
            		double lat = municipio.getLatitud();
            		double lon = municipio.getLongitud();
            		mapViewer.setAddressLocation(new GeoPosition(lat,lon));
        		}
        	}
        });
        
        //Get coordinates where clicked
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GeoPosition gp = mapViewer.convertPointToGeoPosition(e.getPoint());
                double lat = gp.getLatitude();
                double lon = gp.getLongitude();
                lblCoords.setText("Coordenadas: "+lat+", "+lon);
                
                Waypoint wp = new Waypoint() {
                    @Override
                    public GeoPosition getPosition() {
                        return new GeoPosition(lat,lon);
                    }
                };
                waypointPainter.setWaypoints(java.util.Collections.singleton(wp));
                mapViewer.setOverlayPainter(waypointPainter);
            }
        });
        //End of map////////////////////////        
        
        lblData.setBounds(20, 656, 46, 14);
        lblData.setVisible(false);
        getContentPane().add(lblData);
        
        JButton btnCheckCoords = new JButton("<html><body>Comprobar<br>marcador</body></html>");
        btnCheckCoords.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String lat = lblCoords.getText().split(",")[0].split(":")[1].trim();
                String lon = lblCoords.getText().split(",")[1].trim();
                 
                JSONObject json = new JSONObject(Utils.getOpenWeatherAPIResponse(lat,lon));
                JSONObject jsonMain = json.getJSONObject("main");
                JSONObject jsonWind = json.getJSONObject("wind");
                
                float temperatura = jsonMain.getFloat("temp")-273.15f;
                float humedad = jsonMain.getFloat("humidity");
                float velViento = jsonWind.getFloat("speed");
                
                ArrayList<String> tiposSuelo = Utils.getSoilGridsAPIResponse(lat,lon);
                String suelo = tiposSuelo.get(0);
                
                lblTemperatura.setText("Temperatura: "+temperatura);
                lblHumedad.setText("Humedad: "+humedad);
                lblVelViento.setText("Velocidad del viento: "+velViento);
                lblTipoSuelo.setText("Tipo de suelo predominante: "+suelo);
                
                int baseRisk = 30;
                float risk = (temperatura+velViento)-(humedad/2);
                if (risk>baseRisk) {
                    lblRiskOfFire.setText("Riesgo de incendio: Extremo");
                }else if (risk>baseRisk-10) {
                    lblRiskOfFire.setText("Riesgo de incendio: Alto");
                }else if (risk>baseRisk-20) {
                    lblRiskOfFire.setText("Riesgo de incendio: Medio");
                }else if (risk>baseRisk-30) {
                    lblRiskOfFire.setText("Riesgo de incendio: Bajo");
                }else {
                    lblRiskOfFire.setText("Riesgo de incendio: Muy bajo");
                }
            }
        });
        btnCheckCoords.setBounds(69, 327, 124, 48);
        getContentPane().add(btnCheckCoords);
        
        JButton btnCheckAll = new JButton("<html><body align=\"center\">Comprobar datos de pueblos<br>y estaciones AEMET<br>visibles en el mapa</body></html>");
        btnCheckAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //Get coordinates of visible map
                GeoPosition gp1 = mapViewer.convertPointToGeoPosition(new Point(0,0));
                GeoPosition gp2 = mapViewer.convertPointToGeoPosition(new Point(mapViewer.getWidth(),mapViewer.getHeight()));

                double tlLat = gp1.getLatitude();
                double tlLon = gp1.getLongitude();
                
                double brLat = gp2.getLatitude();
                double brLon = gp2.getLongitude();
                
                lblData.setText(tlLat+"|"+tlLon+"|"+brLat+"|"+brLon+"|");
                
                Results results = new Results();
                results.setTitle("Results");
                results.setVisible(true);
                results.setLocationRelativeTo(null);
                setEnabled(false);
            }
        });
        btnCheckAll.setBounds(69, 537, 209, 74);
        getContentPane().add(btnCheckAll);
	}
	
	public static JLabel getWelcomeLabel() {
	    return lblWelcome;
	}
	
	public static JLabel getDataLabel() {
	    return lblData;
	}
}
