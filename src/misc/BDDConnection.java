package misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.json.JSONObject;

public class BDDConnection {
	private String bddName;
	private String bddUser;
	private String bddPass;
	private String bddURL;
	private Connection con;
	
	public BDDConnection() throws Exception {
		JSONObject json = new JSONObject(Utils.getFileContent("secret.json")).getJSONObject("secret").getJSONObject("mysql");
		bddName = json.getString("bddname");
		bddUser = json.getString("user");
		bddPass = json.getString("pass");
		bddURL = json.getString("server");
		
		Class.forName("com.mysql.jdbc.Driver");
		this.con=DriverManager.getConnection("jdbc:mysql://"+bddURL+"/"+bddName,bddUser,bddPass);
	}
	
	public Connection getConnection() {
		return this.con;
	}
	
	public void closeConnection() throws SQLException {
		con.close();
	}
	
}
