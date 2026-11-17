package Datos;

public class Stand {

    private String id;
    private double m2;
    private double precio;
    private String estado;

    public Stand(String id, double m2, double precio, String estado) {
        this.id = id;
        this.m2 = m2;
        this.precio = precio;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public double getM2() {
        return m2;
    }

    public double getPrecio() {
        return precio;
    }

    public String getEstado() {
        return estado;
    }
}