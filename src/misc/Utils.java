package misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
	private static String getFileContent(String file) {
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
}
