package com.valdemar.spook.ui.slideshow;

public class Mensaje {

    private String mensaje;
    private String urlFoto;
    private String nombre;
    private String fotoPerfil;
    private String type_mensaje;
    private String uidUser;

    public Mensaje() {
    }

    public Mensaje(String mensaje, String nombre, String fotoPerfil, String type_mensaje, String uidUser) {
        this.mensaje = mensaje;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
        this.type_mensaje = type_mensaje;
        this.uidUser = uidUser;
    }


    public Mensaje(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, String uidUser) {
        this.mensaje = mensaje;
        this.urlFoto = urlFoto;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
        this.type_mensaje = type_mensaje;
        this.uidUser = uidUser;
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

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getType_mensaje() {
        return type_mensaje;
    }

    public void setType_mensaje(String type_mensaje) {
        this.type_mensaje = type_mensaje;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }
}