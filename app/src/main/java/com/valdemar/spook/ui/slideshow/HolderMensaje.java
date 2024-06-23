package com.valdemar.spook.ui.slideshow;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.valdemar.spook.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderMensaje extends RecyclerView.ViewHolder {

    private TextView nombre;
    private TextView mensaje;
    private TextView hora;
    private CircleImageView fotoMensajePerfil;
    private ImageView fotoMensaje;
    private ImageView imgReplay;
    private ImageView imgDelete;


    public HolderMensaje(View itemView) {
        super(itemView);
        nombre = (TextView) itemView.findViewById(R.id.nombreMensaje);
        mensaje = (TextView) itemView.findViewById(R.id.mensajeMensaje);
        hora = (TextView) itemView.findViewById(R.id.horaMensaje);
        fotoMensajePerfil = (CircleImageView) itemView.findViewById(R.id.fotoPerfilMensaje);
        fotoMensaje = (ImageView) itemView.findViewById(R.id.mensajeFoto);
        imgReplay = (ImageView) itemView.findViewById(R.id.imgReplay);
        imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);
    }


    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public TextView getHora() {
        return hora;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }

    public CircleImageView getFotoMensajePerfil() {
        return fotoMensajePerfil;
    }

    public void setFotoMensajePerfil(CircleImageView fotoMensajePerfil) {
        this.fotoMensajePerfil = fotoMensajePerfil;
    }

    public ImageView getFotoMensaje() {
        return fotoMensaje;
    }

    public void setFotoMensaje(ImageView fotoMensaje) {
        this.fotoMensaje = fotoMensaje;
    }


    public ImageView getImgReplay() {
        return imgReplay;
    }

    public void setImgReplay(ImageView imgReplay) {
        this.imgReplay = imgReplay;
    }

    public ImageView getImgDelete() {
        return imgDelete;
    }

    public void setImgDelete(ImageView imgDelete) {
        this.imgDelete = imgDelete;
    }
}