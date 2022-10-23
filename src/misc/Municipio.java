package misc;

public class Municipio {
	private String nombre;
	private float latitud;
	private float longitud;
	
	public Municipio(String nombre, float latitud, float longitud) {
		super();
		this.nombre = nombre;
		this.latitud = latitud;
		this.longitud = longitud;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public float getLatitud() {
		return latitud;
	}
	public void setLatitud(float latitud) {
		this.latitud = latitud;
	}
	public float getLongitud() {
		return longitud;
	}
	public void setLongitud(float longitud) {
		this.longitud = longitud;
	}
	
	@Override
	public String toString() {
		return "Municipio [nombre=" + nombre + ", latitud=" + latitud + ", longitud=" + longitud + "]";
	}
}
