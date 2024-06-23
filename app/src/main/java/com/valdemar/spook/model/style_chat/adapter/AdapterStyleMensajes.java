package com.valdemar.spook.model.style_chat.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.valdemar.spook.R;
import com.valdemar.spook.model.style_chat.IModal;

import java.util.ArrayList;
import java.util.List;

public class AdapterStyleMensajes extends RecyclerView.Adapter<HolderMensajeStyle>{

    private List<MensajeRecibirStyle> listMensaje = new ArrayList<>();
    private Context c;
    private IModal listener;
    private Animation mShowFromBottom,mUp_to_down,mhide_to_bottom;

    public AdapterStyleMensajes(Context c, IModal listener) {
        this.c = c;
        this.listener = listener;
    }
    public AdapterStyleMensajes(Context c) {
        this.c = c;
    }

    public void addMensaje(String nombre,String mensaje, String posicion){
        MensajeRecibirStyle s = new MensajeRecibirStyle();
        s.setNombre(nombre);
        s.setMensaje(mensaje);
        s.setPosicion(posicion);
        listMensaje.add(s);
        notifyItemInserted(listMensaje.size());
    }

    @Override
    public HolderMensajeStyle onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes_style,parent,false);
        mShowFromBottom = AnimationUtils.loadAnimation(c,R.anim.show_from_bottom);
        v.setAnimation(mShowFromBottom);
        return new HolderMensajeStyle(v);
    }

    @Override
    public void onBindViewHolder(final HolderMensajeStyle holder, final int position) {
        holder.setIsRecyclable(false);




        if(listMensaje.get(position).getPosicion().equals("1")){
            holder.getNombre().setGravity(Gravity.LEFT);
            holder.getNombre().setText(listMensaje.get(position).getNombre());
            holder.getMensaje().setText(listMensaje.get(position).getMensaje());

        }else if(listMensaje.get(position).getPosicion().equals("2")){
            holder.getNombre().setText(listMensaje.get(position).getNombre());
            holder.getMensaje().setText(listMensaje.get(position).getMensaje());

            holder.getMensaje().setTextColor(ContextCompat.getColor(c, R.color.black));
            holder.getNombre().setPadding(0,0,20,0);

            holder.getMensaje().setBackground(ContextCompat.getDrawable(c, R.drawable.friend_bubble_shape));
            holder.getNombre().setGravity(Gravity.RIGHT);
            holder.getChat_posicion().setGravity(Gravity.RIGHT);
        }else{
            holder.getNombre().setText(listMensaje.get(position).getNombre());

            holder.getNombre().setPadding(0,200,0,200);
            holder.getNombre().setTextSize(16);

            holder.getMensaje().setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

}