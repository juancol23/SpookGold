package com.valdemar.spook.model.style_chat;


import java.util.List;

public class ChatModel{

    private String titulo;
    private String imagen;
    private List<MensajeStyle> chat;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<MensajeStyle> getChat() {
        return chat;
    }

    public void setChat(List<MensajeStyle> chat) {
        this.chat = chat;
    }
}
