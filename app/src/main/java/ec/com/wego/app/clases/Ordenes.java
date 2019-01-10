package ec.com.wego.app.clases;

public class Ordenes {

    private int id;
    private String name;
    private int estado;
    private String fecha;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Ordenes(int id, String name, int estado, String fecha) {
        this.id = id;
        this.name = name;
        this.estado = estado;
        this.fecha = fecha;
    }

    public Ordenes() {
    }
}
