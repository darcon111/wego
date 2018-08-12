package com.wego.app.holder;

public class Serviciescarac {


    private int id;
    private String nombre;
    private String descripcion;
    private String respt;
    private String respc;
    private String costo;
    private int view=0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRespt() {
        return respt;
    }

    public void setRespt(String respt) {
        this.respt = respt;
    }

    public String getRespc() {
        return respc;
    }

    public void setRespc(String respc) {
        this.respc = respc;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public Serviciescarac(int id, String nombre, String descripcion, String respt, String respc, String costo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.respt = respt;
        this.respc = respc;
        this.costo = costo;
    }
}
