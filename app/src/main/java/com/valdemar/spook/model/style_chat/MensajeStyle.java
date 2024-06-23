package com.valdemar.spook.model.style_chat;

public class MensajeStyle {

    private String mensaje;
    private String nombre;
    private String posicion;


    public MensajeStyle() {
    }


    public MensajeStyle(String mensaje, String nombre, String posicion) {
        this.mensaje = mensaje;
        this.nombre = nombre;
        this.posicion = posicion;

    }



    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }
}
