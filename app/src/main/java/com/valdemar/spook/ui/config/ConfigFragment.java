package com.valdemar.spook.ui.config;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.valdemar.spook.R;
import com.valdemar.spook.ui.home.HomeFragment;
import com.valdemar.spook.util.sounds.BackgroundSoundService;
import com.valdemar.spook.view.AccessRelato;

public class ConfigFragment extends Fragment implements ICofig {
    private SharedPreferences prefs = null;
    private SharedPreferences prefs_notificacion = null;

    private MediaPlayer player;

    private ImageView setting_sonido;

    private ImageView setting_notification;
    private Dialog MyDialog;
    private  ImageView sonido;
    private ImageView notificacion;
    FirebaseUser user;


    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_config, container, false);
        user  = FirebaseAuth.getInstance().getCurrentUser();
        prefs = getActivity().getSharedPreferences("com.valdemar.utilidades.sounds", getActivity().MODE_PRIVATE);
        prefs_notificacion = getActivity().getSharedPreferences("com.valdemar.notificacion", getActivity().MODE_PRIVATE);

        sonido = root.findViewById(R.id.setting_sonido);
        notificacion = root.findViewById(R.id.setting_notification);

        toggleSound(root);
        //toggleNotificacion(root);
        notificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null) {
                    initValidarAccess();
                }else{
                    toggleNotificacion(root);
                }
            }
        });
        initSettingCheck();

        return root;
    }

    private void initSettingCheck() {

        if(prefs.getBoolean("firstrun", true)){
            sonido.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_volumen));
        }else{
            sonido.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_volumen_off));
        }


        if(prefs_notificacion.getBoolean("prefs_notificacion", true)){
            //FirebaseMessaging.getInstance().subscribeToTopic("Historias");
            FirebaseMessaging.getInstance().subscribeToTopic("chat");
            notificacion.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notification));
        }else{
            //FirebaseMessaging.getInstance().unsubscribeFromTopic("Historias");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("chat");
            notificacion.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notification_off));

        }

    }

    @Override
    public void toggleSound(final View root) {
        setting_sonido = root.findViewById(R.id.setting_sonido);
        setting_sonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null) {
                    initValidarAccess();
                }else{
                    if (prefs.getBoolean("firstrun", true)) {
                        prefs.edit().putBoolean("firstrun", false).commit();
                        Intent svc = new Intent(getActivity().getApplicationContext(), BackgroundSoundService.class);
                        getActivity().stopService(svc);
                        sonido.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_volumen_off));
                        showSnackBar("Se activó el sonido de fondo.",root);

                    } else {
                        prefs.edit().putBoolean("firstrun", true).commit();
                        Intent svc = new Intent(getActivity().getApplicationContext(), BackgroundSoundService.class);
                        getActivity().startService(svc);
                        sonido.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_volumen));
                        showSnackBar("Se desactivó el sonido de fondo.",root);

                    }
                }

            }
        });
    }

    @Override
    public void toggleNotificacion(View root) {
        if(prefs_notificacion.getBoolean("prefs_notificacion", true)){
            //FirebaseMessaging.getInstance().subscribeToTopic("Historias");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("chat");
            notificacion.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notification_off));
            showSnackBar("Se desactivó la notificación del chat.",root);

            prefs_notificacion.edit().putBoolean("prefs_notificacion", false).commit();
        }else{
            //FirebaseMessaging.getInstance().unsubscribeFromTopic("Historias");
            FirebaseMessaging.getInstance().subscribeToTopic("chat");
            notificacion.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notification));
            showSnackBar("Se activó la notificación del chat.",root);
            prefs_notificacion.edit().putBoolean("prefs_notificacion", true).commit();

        }
    }


    public void showSnackBar(String msg,View root) {
        Snackbar
                .make(root.findViewById(R.id.config_), msg, Snackbar.LENGTH_LONG)
                .show();
    }


    private void initValidarAccess() {

        MyDialog = new Dialog(getActivity());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.moda_need_permiso);
        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnModalAcessoRelato = MyDialog.findViewById(R.id.modal_need_inicia_sesion);
        Button btnModalCancel = MyDialog.findViewById(R.id.modal_need_cancel);
        TextView mModal_need_try_feature_text_body = MyDialog.findViewById(R.id.modal_need_text_body);
        // mModal_need_try_feature_text_body.setText("Pronto Estará Disponible");

        btnModalAcessoRelato.setEnabled(true);

        btnModalAcessoRelato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciaSesion();
                MyDialog.dismiss();

            }
        });

        btnModalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment inicio = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.coordinator, inicio)
                        .addToBackStack(null)
                        .remove(inicio)
                        .commit();
                MyDialog.dismiss();
            }
        });

        MyDialog.show();
    }

    private void iniciaSesion(){
        Intent i = new Intent(getActivity(), AccessRelato.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        getActivity().finish();
    }
}
