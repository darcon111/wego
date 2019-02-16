package ec.com.wego.app.clases;

public class Ordenes {

    private int id;
    private String name;
    private int estado;
    private String fecha;

    private String cliente;
    private String servicio;
    private String costo;
    private String direccion;
    private String longitud;
    private String latitud;
    private String telefono;
    private String piso;
    private String departamento;
    private int calificacion;
    private String imagen1;
    private String imagen2;
    private String fechaCreacion;
    private String trabajador;

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

    public Ordenes(int id, String name, int estado, String fecha,String fechaCreacion) {
        this.id = id;
        this.name = name;
        this.estado = estado;
        this.fecha = fecha;
        this.fechaCreacion = fechaCreacion;
    }

    public Ordenes() {
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getImagen1() {
        return imagen1;
    }

    public void setImagen1(String imagen1) {
        this.imagen1 = imagen1;
    }

    public String getImagen2() {
        return imagen2;
    }

    public void setImagen2(String imagen2) {
        this.imagen2 = imagen2;
    }

    public Ordenes(int id, String cliente, String servicio, int estado, String fecha, String costo, String direccion, String longitud, String latitud) {
        this.id = id;
        this.cliente = cliente;
        this.servicio = servicio;
        this.estado = estado;
        this.fecha = fecha;
        this.costo = costo;
        this.direccion = direccion;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public Ordenes(int id, String cliente, String servicio, int estado, String fecha, String costo, String direccion, String longitud, String latitud, String telefono, String piso, String departamento, int calificacion, String imagen1, String imagen2,String trabajador) {
        this.id = id;
        this.cliente = cliente;
        this.servicio = servicio;
        this.estado = estado;
        this.fecha = fecha;
        this.costo = costo;
        this.direccion = direccion;
        this.longitud = longitud;
        this.latitud = latitud;
        this.telefono = telefono;
        this.piso = piso;
        this.departamento = departamento;
        this.calificacion = calificacion;
        this.imagen1 = imagen1;
        this.imagen2 = imagen2;
        this.trabajador = trabajador;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(String trabajador) {
        this.trabajador = trabajador;
    }
}
