package com.valdemar.spook.ui.slideshow;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.valdemar.spook.R;
import com.valdemar.spook.model.style_chat.IModal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterMensajes extends RecyclerView.Adapter<HolderMensaje>{

    private List<MensajeRecibir> listMensaje = new ArrayList<>();
    private Context c;
    private IModal listener;
    private OnEliminarMensajeClickListener mEliminarMensajeListener;


    public AdapterMensajes(Context c, IModal listener) {
        this.c = c;
        this.listener = listener;
    }
    public AdapterMensajes(Context c) {
        this.c = c;
    }

    public void addMensaje(MensajeRecibir m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }

    @Override
    public HolderMensaje onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes,parent,false);
        return new HolderMensaje(v);
    }

    @Override
    public void onBindViewHolder(final HolderMensaje holder, final int position) {
        if(listMensaje.get(position).isBorrado()) return;
        holder.getImgReplay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c, "Mensaje copiado", Toast.LENGTH_SHORT).show();
                setClipboard(c,listMensaje.get(position).getMensaje());
            }
        });
        holder.getImgDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    listMensaje.get(position).setBorrado(true);
                    mEliminarMensajeListener.onEliminarMensajeClick();
            }
        });
        holder.getNombre().setText(listMensaje.get(position).getNombre());
        holder.getMensaje().setText(listMensaje.get(position).getMensaje());
        if(listMensaje.get(position).getType_mensaje().equals("2")){
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(listMensaje.get(position).getUrlFoto()).into(holder.getFotoMensaje());
        }else if(listMensaje.get(position).getType_mensaje().equals("1")){
            holder.getFotoMensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        }

        Glide.with(c).load(listMensaje.get(position).getFotoPerfil()).into(holder.getFotoMensajePerfil());


        holder.getFotoMensajePerfil().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(c," "+listMensaje.get(position).getFotoPerfil(),Toast.LENGTH_LONG).show();
                listener.modalIniciar(""+listMensaje.get(position).getNombre(),""+listMensaje.get(position).getFotoPerfil(),listMensaje.get(position).getUidUser() );
            }
        });

        holder.getFotoMensaje().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(c," "+listMensaje.get(position).getUrlFoto(),Toast.LENGTH_LONG).show();
            }
        });



        Long codigoHora = listMensaje.get(position).getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat sdf;
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sdf =new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                holder.getHora().setText(sdf.format(d));

            }else{
                holder.getHora().setText("");
            }

        }catch (Exception e){
            System.out.println(e);
        }



        System.out.println("");
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }


    private void setClipboard(Context context, String text) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public void setOnEliminarMensajeClickListener(OnEliminarMensajeClickListener listener) {
        mEliminarMensajeListener = listener;
    }

    public interface OnEliminarMensajeClickListener {
        void onEliminarMensajeClick();
    }
}