package com.valdemar.spook.ui.slideshow;

import java.util.Map;

public class MensajeEnviar extends Mensaje {
    private Map hora;

    public MensajeEnviar() {
    }

    public MensajeEnviar(Map hora) {
        this.hora = hora;
    }


    public MensajeEnviar(String mensaje, String nombre, String fotoPerfil, String type_mensaje, Map hora,String uidUser) {
        super(mensaje, nombre, fotoPerfil, type_mensaje,uidUser);
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, Map hora,String uidUser) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje,uidUser);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}