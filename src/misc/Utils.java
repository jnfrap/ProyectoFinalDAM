package misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.json.JSONArray;
import org.json.JSONObject;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class Utils {
	public static String getFileContent(String file) {
		String content = "";
        try {
            InputStream is = Utils.class.getResourceAsStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                content += line;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
	}
	
	public static ArrayList<Municipio> getMunicipiosArray(){
		String fileContent = getFileContent("municipios.json");
		JSONObject json = new JSONObject(fileContent);
        JSONArray municipios = json.getJSONArray("municipios");
        ArrayList<Municipio> municipiosArray = new ArrayList<Municipio>();
        for (int i = 0; i < municipios.length(); i++) {
            JSONObject municipio = municipios.getJSONObject(i);
            String name = municipio.getString("name");
            float lat = municipio.getFloat("lat");
            float lon = municipio.getFloat("lon");
            Municipio m = new Municipio(name, lat, lon);
            municipiosArray.add(m);
        }
        return municipiosArray;
	}
	
	public static String getOpenWeatherAPIResponse(String lat,String lon) {
		String apiKey = new JSONObject(getFileContent("secret.json")).getJSONObject("secret").getString("OpenWeather");
		String link = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid="+apiKey;
		
		Unirest.config().verifySsl(false);
    	
    	HttpResponse<String> apiResponse = Unirest.get(link).header("cache-control", "no-cache").asString();
    	
		return apiResponse.getBody();
	}
	
	public static ArrayList<String> getSoilGridsAPIResponse(String lat,String lon) {
		String link = "https://rest.isric.org/soilgrids/v2.0/classification/query?lon="+lon+"&lat="+lat+"&number_classes=20";
    	
    	HttpResponse<String> response = Unirest.get(link).header("cache-control", "no-cache").asString();
		JSONObject jsonObjSoil = new JSONObject(response.getBody());
		JSONArray jsonArray = jsonObjSoil.getJSONArray("wrb_class_probability");
		
		ArrayList<String> tiposSuelo = new ArrayList<String>();
		for (int i=0;i<jsonArray.length();i++) {
			String[] partido = jsonArray.get(i).toString().split(",");
			String numeroS = partido[1].replace("]", "");
			
			try {
				int numero = Integer.parseInt(numeroS);
				if (numero>0) {
					tiposSuelo.add(jsonArray.get(i).toString());
				}else {
					break;
				}
			} catch(Exception e) {
				break;
			}
		}
		return tiposSuelo;
	}
	
	public static ArrayList<Resultados> getAemetStationAPIResponse(String idema) {
		String apiKey = new JSONObject(getFileContent("secret.json")).getJSONObject("secret").getString("AEMET");
		String link = "https://opendata.aemet.es/opendata/api/observacion/convencional/datos/estacion/"+idema+"/?api_key="+apiKey;
		
		Unirest.config().verifySsl(false);
    	
    	HttpResponse<String> apiResponse = Unirest.get(link).header("cache-control", "no-cache").asString();
    	String response = apiResponse.getBody();
    	JSONObject json = new JSONObject(response);
    	
    	HttpResponse<String> apiResponse2 = Unirest.get(json.getString("datos")).header("cache-control", "no-cache").asString();
    	
    	JSONArray jsonArray = new JSONArray(apiResponse2.getBody());
    	
    	ArrayList<Resultados> array = new ArrayList<Resultados>();
    	for (int i=0;i<jsonArray.length();i++) {
    		JSONObject resJson = jsonArray.getJSONObject(i);
    		
    		String nombre = "";
    		try {
    			nombre = resJson.getString("ubi");
    		}catch(Exception e) {
    			nombre = "Error interno";
    		}
    		String rIdema = "";
    		try {
    			rIdema = resJson.getString("idema");
    		}catch(Exception e) {
    			rIdema = "Error interno";
    		}
    		
    		float temperatura = 0;
    		try {
    			temperatura = resJson.getFloat("ta");
    		}catch(Exception e) {
    			temperatura = 0;
    		}
    		float humedad = 0;
    		try {
    			humedad = resJson.getFloat("hr");
    		}catch(Exception e) {
    			humedad = 0;
    		}
    		float velViento = 0;
    		try {
    			velViento = resJson.getFloat("vv");
    		}catch(Exception e) {
    			velViento = 0;
    		}
    		String fecha = "";
    		try {
    			fecha = resJson.getString("fint");
    		}catch(Exception e) {
    			fecha = "Error interno";
    		}
    		
    		array.add(new Resultados(nombre,rIdema,temperatura,humedad,velViento,fecha));
    	}
    	
		return array;
	}
	
	public static double distanceCoord(double lat1, double lng1, double lat2, double lng2) {
        final double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);  
        double dLng = Math.toRadians(lng2 - lng1);  
        double sindLat = Math.sin(dLat / 2);  
        double sindLng = Math.sin(dLng / 2);  
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)  
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));  
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));  
        double distancia = radioTierra * va2;  
   
        return distancia;  
    }
	
	public static Resultados checkIfMunicipioIsInDB(String municipio) {
		try {
			BDDConnection bdd = new BDDConnection();
			PreparedStatement sentence = bdd.getConnection().prepareStatement("SELECT id from municipios where nombre=upper(?)");
			sentence.setString(1, municipio.toUpperCase());
			ResultSet rs = sentence.executeQuery();
			
			if (rs.next()) {
				int id = rs.getInt(1);
				PreparedStatement sentence2 = bdd.getConnection().prepareStatement("SELECT tiposSuelo from municipios where id=?");
				sentence2.setString(1, id+"");
				ResultSet rs2 = sentence2.executeQuery();
				
				ArrayList<String> tiposSuelo = new ArrayList<String>();
				
				if (rs2.next()) {
					String tiposSueloString = rs2.getString(1);
					JSONArray jsonArray = new JSONArray(tiposSueloString);
					for (int i=0;i<jsonArray.length();i++) {
						tiposSuelo.add(jsonArray.get(i).toString());
					}
				}
				
				Resultados resultado = new Resultados(0, 0, 0, tiposSuelo);
				return resultado;
			}
			
			bdd.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void insertMunicipioInDB(String municipio, ArrayList<String> tiposSuelo) {
		try {
			BDDConnection bdd = new BDDConnection();
			Statement stmt=bdd.getConnection().createStatement();
			ResultSet rs1 = stmt.executeQuery("SELECT max(id) from municipios");
			rs1.next();
			int id = (Integer)rs1.getInt(1)+1;
			
			String query = "INSERT INTO municipios (id, nombre, tiposSuelo) VALUES (?, ?, ?)";
            PreparedStatement preparedStmt = bdd.getConnection().prepareStatement(query);
            preparedStmt.setInt(1, id);
            preparedStmt.setString(2, municipio);
            preparedStmt.setString(3, tiposSuelo.toString());
            preparedStmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkIfEmail(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);

        if(mat.matches()){
            return true;
        }
        return false;
	}

	public static String generatePassword(int length) {
		String password = "";
		for (int i=0;i<length;i++) {
			password += (char)(Math.random()*26+97);
		}
		return password;
	}
	
	public static void sendEmail(String reciver, String subject, String body) {
		JSONObject json = new JSONObject(Utils.getFileContent("secret.json")).getJSONObject("secret").getJSONObject("sendgrid");
		String user = json.getString("user");
		String apiKey = json.getString("key"); //Is the password too
		String server = json.getString("server");
		String port = json.getString("port");
		String from = json.getString("from");
		
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", server);
		prop.put("mail.smtp.port", port);
		prop.put("mail.smtp.ssl.trust", server);
		
		Session session = Session.getInstance(prop, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(user, apiKey);
		    }
		});
		
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(from));
			message.setRecipients(
			  Message.RecipientType.TO, InternetAddress.parse(reciver));
			message.setSubject(subject);

			String msg = body;

			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);

			message.setContent(multipart);

			Transport.send(message);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String parseDate(String date) {
	    String[] ph = date.split("T");
	    String[] phDate = ph[0].split("-");
	    String day = phDate[2]+"/"+phDate[1]+"/"+phDate[0];
	    String[] phHour = ph[1].split(":");
	    String hour = phHour[0]+":"+phHour[1];
	    
	    return day+" | "+hour;
	}
	
	public static String parseSuelo(String suelo) {
	    try {
	        String tipo = suelo.split(",")[0].replace("\"", "").replace("[", "").trim();
	        String porc = suelo.split(",")[1].replace("]", "").trim();
	        return tipo+", "+porc+"%";
	    }catch(Exception e) {
	        return "";
	    }
	}
}
