package com.valdemar.spook.model.style_chat;

import java.util.List;

public class MensajeRecibirStyle extends MensajeStyle {

    private String titulo;
    private long countLike;
    private String imagen;
    private List<MensajeStyle> chat;

    public MensajeRecibirStyle() {
    }


    public MensajeRecibirStyle(String mensaje, String nombre, String posicion) {
        super(mensaje, nombre, posicion);
    }




}