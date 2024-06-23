package com.valdemar.spook.modo_lectura;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.valdemar.spook.R;
import com.valdemar.spook.model.Comentarios;
import com.valdemar.spook.util.sounds.BackgroundSoundService;
import com.valdemar.spook.utilidades.RelatoViewHolderStructureComentarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DescBlankFragment extends Fragment implements TextToSpeech.OnInitListener {

    private String mPost_key = null;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike, mDatabaseLikeCount;
    private TextView mPostTitleDetails;
    private ImageView mImage_paralax;
    private FloatingActionButton mFav_favorite;
    private ImageView mVounn_icon;
    private boolean mProcessLike;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgresDialog;
    private Button mPostRemoveDetails;
    private Button mPostBloquear;

    private DatabaseReference databaseUserOff;
    private FirebaseDatabase database;

    private TextToSpeech tts;


    private RecyclerView mRecyclerComentarios;
    private DatabaseReference mDatabaseMisComentarios;
    private ProgressDialog mProgress;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {




        View root = inflater.inflate(R.layout.fragment_desc_blank, container, false);



        // Toast.makeText(getActivity().getApplicationContext(),"OTRO FRAGMENT", Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        initView(root);

        initFavorite(root);
        initSpeack();
        // initRemove(mPost_key,root);
        // initBloquear(mPost_key,root);
        initComentarios(root,mPost_key);
        return root;

    }

    private void initComentarios(View root, final String mPost_key) {

        mProgress = new ProgressDialog(getContext());

        mDatabaseMisComentarios = FirebaseDatabase.getInstance().getReference().child("HistoriasDetalle").child("comentarios");
        mDatabaseMisComentarios.keepSynced(true);

        LinearLayoutManager layoutManagerMisLecturas
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        layoutManagerMisLecturas.setReverseLayout(true);
        layoutManagerMisLecturas.setStackFromEnd(true);

        mRecyclerComentarios = (RecyclerView) root.findViewById(R.id.recyclerComentarios);
        mRecyclerComentarios.setHasFixedSize(true);
        mRecyclerComentarios.setLayoutManager(layoutManagerMisLecturas);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerComentarios.setVisibility(View.VISIBLE);

            }
        },1000);

        Query queryRef = mDatabaseMisComentarios.child(mPost_key);

        // Configura las opciones del FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Comentarios> options = new FirebaseRecyclerOptions.Builder<Comentarios>()
                .setQuery(queryRef, Comentarios.class)
                .build();

        // Crea el adaptador con las nuevas opciones
        FirebaseRecyclerAdapter<Comentarios, RelatoViewHolderStructureComentarios>
                firebaseRecyclerAdapterMyLecturas = new FirebaseRecyclerAdapter<Comentarios, RelatoViewHolderStructureComentarios>(options) {
            @NonNull
            @Override
            public RelatoViewHolderStructureComentarios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflar el layout para cada item de la lista
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_comentarios, parent, false);
                return new RelatoViewHolderStructureComentarios(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RelatoViewHolderStructureComentarios viewHolder, int position, @NonNull Comentarios model) {
                // Asignar los datos al ViewHolder
                if(model != null) {
                    viewHolder.setAutor(model.getNombre());
                    viewHolder.setMensaje(model.getComentario());
                    viewHolder.goneHora();
                    viewHolder.setImage(getActivity().getApplicationContext(), model.getFoto());
                }
            }
        };


        mRecyclerComentarios.setAdapter(firebaseRecyclerAdapterMyLecturas);

        // Inicia la escucha del adaptador
        firebaseRecyclerAdapterMyLecturas.startListening();

        TextView mTxtComentarios = root.findViewById(R.id.txtComentarios);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.v("TAG_LIKE", "Favorito");
            mTxtComentarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Dialog MyDialog;

                    MyDialog = new Dialog(getActivity());
                    MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    MyDialog.setContentView(R.layout.comentario_add);
                    MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    MyDialog.setCancelable(false);
                    Button btnModalAcessoRelato = MyDialog.findViewById(R.id.modal_need_inicia_sesion);
                    Button btnModalCancel = MyDialog.findViewById(R.id.modal_need_cancel);
                    final TextInputEditText txtComentario = MyDialog.findViewById(R.id.comentarioTextInput);

                    btnModalAcessoRelato.setEnabled(true);

                    btnModalAcessoRelato.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mDatabaseMisComentarios.addListenerForSingleValueEvent(new ValueEventListener() {
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    if(!txtComentario.getText().toString().isEmpty()) {
                                        DatabaseReference newPost = mDatabaseMisComentarios.child(mPost_key).push();
                                        newPost.child("foto").setValue(user.getPhotoUrl().toString());
                                        newPost.child("comentario").setValue(txtComentario.getText().toString());
                                        newPost.child("nombre").setValue(user.getDisplayName().toString());
                                        newPost.child("idss").setValue(newPost.getKey());
                                        newPost.child("idEmprendedor").setValue(user.getUid());

                                        mRecyclerComentarios.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(getActivity(),"Por favor ingrese un texto válido.",Toast.LENGTH_LONG).show();
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            MyDialog.dismiss();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerComentarios.setVisibility(View.VISIBLE);

                                }
                            },500);
                        }
                    });

                    btnModalCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyDialog.dismiss();
                        }
                    });

                    MyDialog.show();

                }
            });

        }else{
            mTxtComentarios.setText("Iniciar Sesión para comentar");
        }



    }

    private void initView(View root) {

        Bundle datosRecuperados = getArguments();
        if (datosRecuperados == null) {
            // No hay datos, manejar excepción
            return;
        }
        mPost_key = datosRecuperados.getString("blog_id");

        mPostTitleDetails = (TextView) root.findViewById(R.id.postTitleDetails);
        mImage_paralax = (ImageView) root.findViewById(R.id.image_paralax);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Historias");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLike.keepSynced(true);


        LinearLayout layout_ = root.findViewById(R.id.container_);
        try {
            View cri = getLayoutInflater().inflate(R.layout.dynamic_linearlayout,null,false);

            layout_.addView(cri);

            initWebView(root);
            System.out.println("chevere web view: ");
        }catch (Exception e){

            View cri = getLayoutInflater().inflate(R.layout.text_dynamic_linearlayout,null,false);

            layout_.addView(cri);

            initNoWebView(root);
            System.out.println("error web view: "+e);
        }





        initCount(root,mPost_key);
        //initRemove(mPost_key,root);
        //initBloquear(mPost_key,root);
    }


    //initRemove(mPost_key,root);
    private void initCount(final View root, final String mPost_key){
        mDatabaseLikeCount = FirebaseDatabase.getInstance().getReference().child("HistoriasDetalle").child("count").child(mPost_key);
        mDatabaseLikeCount.keepSynced(true);
        mDatabaseLikeCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView vounn = root.findViewById(R.id.vounn);
                vounn.setText(dataSnapshot.getChildrenCount()+" ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

/*
    private void initRemove(final String mPost_key, View root) {
        mPostRemoveDetails = (Button) root.findViewById(R.id.postRemoveDetails);
        //mPostRemoveDetails.setVisibility(View.VISIBLE);
        mProgresDialog= new ProgressDialog(getActivity());

*/


    private void initWebView(final View root) {

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                final String textoCentradoDesc = post_desc;
                String text_string_center = "<html><body style='text-align:justify;'>"+textoCentradoDesc+"<body><html>";
                /*****************************************/
                String justifyTag = "<html><body style='text-align:justify;background:black !important;color:#c1c0c0;font-size:15px;'>%s</body></html>";
                String dataString = String.format(Locale.US, justifyTag, text_string_center);


                WebView webViewDetail = (WebView) root.findViewById(R.id.webViewDetail);
                webViewDetail.loadDataWithBaseURL("", dataString, "text/html", "UTF-8", "");


                /*****************************************/
                mPostTitleDetails.setText(post_title);
                Glide.with(getActivity().getApplicationContext())
                        .load(post_image)
                        .into(mImage_paralax);
                if(textoCentradoDesc != null){
                    final FloatingActionButton play = root.findViewById(R.id.fav_play);
                    final FloatingActionButton pausa = root.findViewById(R.id.fav_stop);

                    play.setVisibility(View.VISIBLE);
                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent svc = new Intent(getActivity(), BackgroundSoundService.class);
                            getActivity().stopService(svc);
                            List<String> splitList = new ArrayList<String>();
                            int index = 0;
                            while (index < textoCentradoDesc.length()) {
                                splitList.add(textoCentradoDesc.substring(index, Math.min(index + 3000,textoCentradoDesc.length())));
                                index += 4000;
                            }

                            for (String i: splitList){
                                tts.speak(i, TextToSpeech.QUEUE_ADD, null);
                            }


                            //  stopService(new Intent(this, BackgroundSoundService.class));
                            play.setVisibility(View.GONE);
                            pausa.setVisibility(View.VISIBLE);
                        }
                    });

                    pausa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tts.stop();
                            play.setVisibility(View.VISIBLE);
                            pausa.setVisibility(View.GONE);

                        }
                    });



                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initNoWebView(final View root) {

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                final String textoCentradoDesc = post_desc;
                String text_string_center = "<html><body style='text-align:justify;'>"+textoCentradoDesc+"<body><html>";
                /*****************************************/
                String justifyTag = "<html><body style='text-align:justify;background:black !important;color:#c1c0c0;font-size:15px;'>%s</body></html>";
                String dataString = String.format(Locale.US, justifyTag, text_string_center);


                TextView textViewDetails = root.findViewById(R.id.textViewDetails);
                textViewDetails.setText(post_desc);




                /*****************************************/
                mPostTitleDetails.setText(post_title);
                Glide.with(getActivity().getApplicationContext())
                        .load(post_image)
                        .into(mImage_paralax);  //aplicar cuando no exista contexto


                if(textoCentradoDesc != null){
                    final FloatingActionButton play = root.findViewById(R.id.fav_play);
                    final FloatingActionButton pausa = root.findViewById(R.id.fav_stop);

                    play.setVisibility(View.VISIBLE);
                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent svc = new Intent(getActivity(), BackgroundSoundService.class);
                            getActivity().stopService(svc);
                            List<String> splitList = new ArrayList<String>();
                            int index = 0;
                            while (index < textoCentradoDesc.length()) {
                                splitList.add(textoCentradoDesc.substring(index, Math.min(index + 3000,textoCentradoDesc.length())));
                                index += 4000;
                            }

                            for (String i: splitList){
                                tts.speak(i, TextToSpeech.QUEUE_ADD, null);
                            }


                            //  stopService(new Intent(this, BackgroundSoundService.class));
                            play.setVisibility(View.GONE);
                            pausa.setVisibility(View.VISIBLE);
                        }
                    });

                    pausa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tts.stop();
                            play.setVisibility(View.VISIBLE);
                            pausa.setVisibility(View.GONE);

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void initFavorite(final View root) {
        mFav_favorite =  root.findViewById(R.id.fav_favorite);
        mVounn_icon =  root.findViewById(R.id.vounn_icon);

        mFav_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike = true;
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){

                    mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String post_title = (String) dataSnapshot.child("title").getValue();
                            final String post_image = (String) dataSnapshot.child("image").getValue();
                            final String post_category = (String) dataSnapshot.child("category").getValue();
                            final String post_author = (String) dataSnapshot.child("author").getValue();
                            final String post_desc = (String) dataSnapshot.child("desc").getValue();

                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (mProcessLike){

                                        if(dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(mPost_key)){
                                            Log.v("TAG_LIKE","LINE NO");
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).removeValue();
                                            showSnackBar("Eliminado de favoritos",root);
                                            mFav_favorite.setImageResource(R.drawable.favorite_flaco);

                                            mProcessLike = false;
                                        }else{
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("title").setValue(post_title);
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("image").setValue(post_image);
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("author").setValue(post_author);
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("category").setValue(post_category);


                                            mFav_favorite.setImageResource(R.drawable.favorite_gordo);
                                            mProcessLike = false;
                                            showSnackBar("Agregado a favoritos", root);
                                        }
                                        if(dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())){
                                            showSnackBar("Dislike", root);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.v("TAG_LIKE","LINE onCancelled");

                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mDatabaseLikeCount.addListenerForSingleValueEvent(new ValueEventListener(){

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Boolean status = false;
                            if(dataSnapshot.child(user.getUid()).getValue() != null){
                                if(dataSnapshot.child(user.getUid()).getValue().equals(true)){
                                    status = true;
                                }
                            }
                            if(status){
                                mDatabaseLikeCount.child(mAuth.getCurrentUser().getUid()).removeValue();
                                showSnackBar("I love", root);
                            }else{
                                mDatabaseLikeCount.child(user.getUid()).setValue(true);
                                showSnackBar("I don't love", root);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    showSnackBar("Necesitas Iniciar Sesión", root);
                }
            }
        });

        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    //primero la historia el id
                    if(mAuth.getCurrentUser().getUid() != null){
                        if(dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(mPost_key)){
                            mFav_favorite.setImageResource(R.drawable.favorite_gordo);
                            //mVounn_icon.setImageResource(R.drawable.favorite_flaco);
                            Log.v("TAG_LIKE","Favorito");
                            mDatabaseLikeCount.child(user.getUid()).setValue(true);
                        }else{
                            mVounn_icon.setImageResource(R.drawable.favorite_flaco);
                            //mFav_favorite.setImageResource(R.drawable.ic_star_half);
                            Log.v("TAG_LIKE","no Favorito");
                            mDatabaseLikeCount.child(mAuth.getCurrentUser().getUid()).removeValue();
                        }
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showSnackBar(String msg, View root) {
        Snackbar
                .make(root.findViewById(R.id.coordinator), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    /*
    private void initRemove(final String mPost_key, View root) {
        mPostRemoveDetails = (Button) root.findViewById(R.id.postRemoveDetails);
        mPostRemoveDetails.setVisibility(View.VISIBLE);
        mProgresDialog= new ProgressDialog(getActivity());

        mPostRemoveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder makeDialog = new AlertDialog.Builder(getActivity());
                makeDialog.setMessage("Si continuas eliminarás está lectura");
                makeDialog.setTitle("Eliminar Lectura");

                makeDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgresDialog.setMessage("Removiendo Historia");
                        mProgresDialog.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mDatabase.child(mPost_key).removeValue();
                                startActivity(new Intent(getActivity().getApplicationContext(), ViewSpook.class));
                                mProgresDialog.dismiss();
                            }
                        },500);
                    }
                });

                makeDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog ad = makeDialog.create();
                ad.show();


            }
        });
    }*/

    private void initBloquear(final String mPost_key, View root) {
        mPostBloquear = (Button) root.findViewById(R.id.postBloquear);
        mPostBloquear.setVisibility(View.VISIBLE);
        mProgresDialog= new ProgressDialog(getActivity());

        mPostBloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mPost_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String usuarioForBloquear = (String) dataSnapshot.child("IdMiLectura").getValue();
                        databaseUserOff = database.getReference("Users").child(usuarioForBloquear);//Sala de chat (nombre)
                        databaseUserOff.keepSynced(true);

                        databaseUserOff.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child("bloqueado").getValue() == null){

                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put("bloqueado", "1");

                                    databaseUserOff.updateChildren(childUpdates);
                                    mPostBloquear.setText("Usuario Bloqueado");
                                }else{
                                    databaseUserOff.child("bloqueado").removeValue();
                                    mPostBloquear.setText("Bloquear Usuario");
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }


    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        //Toast.makeText(getActivity(),"INICIADOR 1 ",Toast.LENGTH_SHORT).show();
        if (status == TextToSpeech.SUCCESS) {
            //Toast.makeText(getActivity(),"INICIADOR 2",Toast.LENGTH_SHORT).show();
            int result = tts.setLanguage(new Locale("es","US"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Toast.makeText(getActivity(),"INICIADOR 3",Toast.LENGTH_SHORT).show();
                Log.e("TTS", "Language is not supported");
                Toast.makeText(getActivity(),"Language is not supported",Toast.LENGTH_SHORT).show();

            } else {
                //Toast.makeText(getActivity(),"INICIADOR 4",Toast.LENGTH_SHORT).show();
                Log.e("TTS", "Language is  supported");
                //mPostDescDetails = (TextView) findViewById(R.id.postDescDetails);
                //String text = mPostDescDetails.getText().toString();

                tts.speak("", TextToSpeech.QUEUE_FLUSH, null);
                tts.playSilence(100, TextToSpeech.QUEUE_FLUSH,null);

            }

        } else {
            Log.e("TTS", "Initilization Failed");
            Toast.makeText(getActivity(),"Initilization Failed",Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onStop() {
        super.onStop();
        Intent svc = new Intent(getActivity(), BackgroundSoundService.class);
        getActivity().stopService(svc);
        tts.stop();

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            tts.stop();
        }catch (Exception e){
            System.out.println(e+"");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent svc = new Intent(getActivity(), BackgroundSoundService.class);
        getActivity().stopService(svc);
    }





    private void initSpeack() {
        tts = new TextToSpeech(getActivity(), this);
    }
}
