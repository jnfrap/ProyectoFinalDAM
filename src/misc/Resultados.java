package misc;

import java.util.ArrayList;

public class Resultados {
	private String nombre;
	private String idema;
	private float temperatura;
	private float humedad;
	private float velViento;
	private String fecha;
	private ArrayList<String> tiposSuelo;
	
	public Resultados(float temperatura, float humedad, float velViento, ArrayList<String> tiposSuelo) {
		this.temperatura = temperatura;
		this.humedad = humedad;
		this.velViento = velViento;
		this.tiposSuelo = tiposSuelo;
	}
	
	public Resultados(String nombre,float temperatura, float humedad, float velViento, ArrayList<String> tiposSuelo) {
		this.temperatura = temperatura;
		this.humedad = humedad;
		this.velViento = velViento;
		this.tiposSuelo = tiposSuelo;
		this.nombre = nombre;
	}
	
	public Resultados(String nombre, String idema, float temperatura, float humedad, float velViento, String fecha) {
		this.nombre = nombre;
		this.idema = idema;
		this.temperatura = temperatura;
		this.humedad = humedad;
		this.velViento = velViento;
		this.fecha = fecha;
	}
	
	public String getIdema() {
		return idema;
	}

	public void setIdema(String idema) {
		this.idema = idema;
	}

	public float getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(float temperatura) {
		this.temperatura = temperatura;
	}

	public float getHumedad() {
		return humedad;
	}

	public void setHumedad(float humedad) {
		this.humedad = humedad;
	}

	public float getVelViento() {
		return velViento;
	}

	public void setVelViento(float velViento) {
		this.velViento = velViento;
	}
	
	public ArrayList<String> getTiposSuelo() {
		return tiposSuelo;
	}

	public void setTiposSuelo(ArrayList<String> tiposSuelo) {
		this.tiposSuelo = tiposSuelo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	@Override
	public String toString() {
		return "Resultados [nombre=" + nombre + ", idema=" + idema + ", temperatura=" + temperatura + ", humedad="
				+ humedad + ", velViento=" + velViento + ", fecha=" + fecha + ", tiposSuelo=" + tiposSuelo + "]";
	}
}
