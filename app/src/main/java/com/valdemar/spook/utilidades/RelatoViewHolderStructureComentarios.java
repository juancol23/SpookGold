package com.valdemar.spook.utilidades;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.valdemar.spook.R;

public class RelatoViewHolderStructureComentarios extends RecyclerView.ViewHolder {

    public View mViewStructure;
    public TextView mItem_recycler_structure_comentario;
    public TextView mItem_recycler_structure_nombre;
    public TextView mItem_recycler_structure_hora;
    public ImageView mPost_image;
    private Long fecha;


    public RelatoViewHolderStructureComentarios(View itemView) {
        super(itemView);
        mViewStructure = itemView;
    }

    public void goneHora() {
        //mItem_recycler_structure_title.setTypeface(Pacifico);
        mItem_recycler_structure_hora = mViewStructure.findViewById(R.id.horaMensaje);
        mItem_recycler_structure_hora.setVisibility(View.GONE);
    }

    public void setMensaje(String title) {
        //mItem_recycler_structure_title.setTypeface(Pacifico);
        mItem_recycler_structure_comentario = mViewStructure.findViewById(R.id.mensajeMensaje);
        mItem_recycler_structure_comentario.setText(title);
    }


    public void setAutor(String author) {
        mItem_recycler_structure_nombre = mViewStructure.findViewById(R.id.nombreMensaje);
        mItem_recycler_structure_nombre.setText(author);
    }

    public void setImage(Context context, String image) {
        mPost_image = mViewStructure.findViewById(R.id.fotoPerfilMensaje);

        Glide.with(context)
                .load(image)
                .thumbnail(Glide.with(context).load(R.drawable.b))
                .into(mPost_image);

    }
}

