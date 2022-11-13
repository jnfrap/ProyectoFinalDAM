package main;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import org.json.JSONObject;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import customComponents.CustomJButton;
import customComponents.CustomJButton.ButtonStyle;
import customComponents.CustomJTextField;
import misc.Municipio;
import misc.Utils;

import java.awt.Color;

import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class Main extends JFrame {
	private JTextField textField;
	private static JLabel lblWelcome = new JLabel("Bienvenido a Incendios");
	private static JLabel lblData = new JLabel("Data");
	private JPanel panelInfoMarker = new JPanel();
	private JLabel lblDataCoords = new JLabel("Coordenadas: 38.9942400, -1.8564300");

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
		setBounds(100, 100, 1500, 720);
		getContentPane().setLayout(null);
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.setMinimumSize(new Dimension(1500, 720));

		JLabel lblCoords = new JLabel("Coordenadas:");
        lblCoords.setBounds(273, 11, 347, 14);
        getContentPane().add(lblCoords);
		
		///////////////////Map
		CustomJButton btnZoomIn = new CustomJButton("");
		btnZoomIn.setStyle(ButtonStyle.SECONDARY);
		try {
            Image img = ImageIO.read(getClass().getResource("/images/plus.png"));
            btnZoomIn.setIcon(new ImageIcon(img));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		CustomJButton btnZoomOut = new CustomJButton("");
		btnZoomOut.setStyle(ButtonStyle.SECONDARY);
		try {
            Image img = ImageIO.read(getClass().getResource("/images/minus.png"));
            btnZoomOut.setIcon(new ImageIcon(img));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
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
        
        //Set location of things
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            	int w = (int)Math.round(e.getComponent().getSize().width);
            	int h = (int)Math.round(e.getComponent().getSize().height);
            	
            	mapViewer.setLocation(w-(w-10),h-(h-30));
            	mapViewer.setSize(w-40,h-120);
            	
            	btnZoomIn.setLocation((mapViewer.getLocation().x+mapViewer.getSize().width)-55,btnZoomIn.getLocation().y);
            	btnZoomOut.setLocation((mapViewer.getLocation().x+mapViewer.getSize().width)-25,btnZoomOut.getLocation().y);
            	
            	panelInfoMarker.setLocation(10, h-87);
            	panelInfoMarker.setSize(w-40,23);
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
        btnZoomIn.setBounds(1408, 3, 25, 25);
        getContentPane().add(btnZoomIn);

        btnZoomOut.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (mapViewer.getZoom()<7) {
        			mapViewer.setZoom(mapViewer.getZoom()+1);
        		}
        	}
        });
        btnZoomOut.setBounds(1440, 3, 25, 25);
        getContentPane().add(btnZoomOut);
        
        JComboBox<String> cbMunicipios = new JComboBox<String>();
        cbMunicipios.setBounds(151, 3, 224, 19);
        
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
		ArrayList<Municipio> municipios = Utils.getMunicipiosArray();
		for (int i = 0; i < municipios.size(); i++) {
			cbMunicipios.addItem(municipios.get(i).getNombre());
		}
        
        lblWelcome.setBounds(11, 11, 260, 14);
        getContentPane().add(lblWelcome);
        
        panelInfoMarker.setBounds(10, 625, 1244, 23);
        getContentPane().add(panelInfoMarker);
        panelInfoMarker.setLayout(null);

        panelInfoMarker.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        
        JLabel lblTemperatura = new JLabel("Temperatura:");
        lblTemperatura.setBounds(10, 5, 139, 14);
        panelInfoMarker.add(lblTemperatura);
        
        JLabel lblHumedad = new JLabel("Humedad:");
        lblHumedad.setBounds(142, 5, 145, 14);
        panelInfoMarker.add(lblHumedad);
        
        JLabel lblVelViento = new JLabel("Velocidad del viento:");
        lblVelViento.setBounds(284, 5, 241, 14);
        panelInfoMarker.add(lblVelViento);
        
        JLabel lblTipoSuelo = new JLabel("Tipo de suelo predominante:");
        lblTipoSuelo.setBounds(492, 5, 381, 14);
        panelInfoMarker.add(lblTipoSuelo);
        
        JLabel lblRiskOfFire = new JLabel("Riesgo de incendio:");
        lblRiskOfFire.setBounds(817, 5, 258, 14);
        panelInfoMarker.add(lblRiskOfFire);
        
        //Get coordinates where clicked
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GeoPosition gp = mapViewer.convertPointToGeoPosition(e.getPoint());
                double lat = gp.getLatitude();
                double lon = gp.getLongitude();
                lblCoords.setText(String.format("Coordenadas: %.7f, %.7f",lat,lon));
                lblDataCoords.setText("Coordenadas: "+lat+", "+lon);
                
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
        
        CustomJButton btnCheckCoords = new CustomJButton("<html><body>Comprobar marcador</body></html>");
        btnCheckCoords.setStyle(ButtonStyle.SECONDARY);
        btnCheckCoords.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String lat = lblDataCoords.getText().split(",")[0].split(":")[1].trim();
                String lon = lblDataCoords.getText().split(",")[1].trim();
                 
                JSONObject json = new JSONObject(Utils.getOpenWeatherAPIResponse(lat,lon));
                JSONObject jsonMain = json.getJSONObject("main");
                JSONObject jsonWind = json.getJSONObject("wind");
                
                float temperatura = jsonMain.getFloat("temp")-273.15f;
                float humedad = jsonMain.getFloat("humidity");
                float velViento = jsonWind.getFloat("speed");
                
                ArrayList<String> tiposSuelo = Utils.getSoilGridsAPIResponse(lat,lon);
                String suelo = tiposSuelo.get(0);
                
                lblTemperatura.setText(String.format("Temperatura: %.2f", temperatura));
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
        btnCheckCoords.setBounds(500, 5, 187, 23);
        try {
            Image img = ImageIO.read(getClass().getResource("/images/SendIcon.png"));
            btnCheckCoords.setIcon(new ImageIcon(img));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        getContentPane().add(btnCheckCoords);
        
        JPanel panelSearch = new JPanel();
        panelSearch.setBounds(697, 2, 718, 25);
        getContentPane().add(panelSearch);
        panelSearch.setLayout(null);
        panelSearch.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        
        CustomJButton btnSubmitMunicipio = new CustomJButton("Ir");
        btnSubmitMunicipio.setStyle(ButtonStyle.SECONDARY);
        btnSubmitMunicipio.setBounds(619, 5, 89, 15);
        panelSearch.add(btnSubmitMunicipio);
        
        textField = new JTextField();
        textField.setBounds(385, 3, 224, 19);
        panelSearch.add(textField);
        textField.setColumns(10);
        
        cbMunicipios.removeAllItems();
        panelSearch.add(cbMunicipios);
        
        JLabel lblMunSearch = new JLabel("Buscador de municipios");
        lblMunSearch.setBounds(10, 5, 138, 14);
        panelSearch.add(lblMunSearch);
        
        lblDataCoords.setBounds(124, 445, 102, 14);
        lblDataCoords.setVisible(false);
        getContentPane().add(lblDataCoords);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu mnAEMET = new JMenu("AEMET");
        menuBar.add(mnAEMET);
        
        JMenuItem mntmSeeAll = new JMenuItem("Datos de todo lo visible en el mapa");
        mnAEMET.add(mntmSeeAll);
        
        mntmSeeAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
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
        mnAEMET.addSeparator();
        
        JMenuItem mntmHistorial = new JMenuItem("Historial");
        mnAEMET.add(mntmHistorial);
        
        mntmHistorial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                History history = new History();
                history.setTitle("History");
                history.setVisible(true);
                history.setLocationRelativeTo(null);
                setEnabled(false);
            }
        });
        
        JMenu mnOpciones = new JMenu("Opciones");
        menuBar.add(mnOpciones);
        
        JMenuItem mntmOpciones = new JMenuItem("Opciones");
        mnOpciones.add(mntmOpciones);
        
        mntmOpciones.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                Settings settings = new Settings();
                settings.setTitle("Settings");
                settings.setVisible(true);
                settings.setLocationRelativeTo(null);
                setEnabled(false);
            }
        });
        mnOpciones.addSeparator();
        
        JMenuItem mntmSalir = new JMenuItem("Salir");
        mnOpciones.add(mntmSalir);
        
        mntmSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                    System.exit(0);
            }
        });
        
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
        
	}
	
	public static JLabel getWelcomeLabel() {
	    return lblWelcome;
	}
	
	public static JLabel getDataLabel() {
	    return lblData;
	}
}
