package com.valdemar.spook.ui.slideshow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valdemar.spook.MainActivity;
import com.valdemar.spook.ProfileActivity;
import com.valdemar.spook.R;
import com.valdemar.spook.model.style_chat.IModal;
import com.valdemar.spook.ui.home.HomeFragment;
import com.valdemar.spook.view.AccessRelato;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment implements AdapterMensajes.OnEliminarMensajeClickListener{

    private static final int RESULT_OK = -1;
    private DatabaseReference mMensajeShare;
    private ProgressDialog mProgress;

    private LinearLayout mLinearLayout_;


    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;
    private ImageButton btnEnviarFoto;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseUserOff,databaseUserOff2;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;
    private String fotoPerfilCadena;
    private Uri photoUrl;
    private String name = "Cargando...";
    private String email = "Cargando...";
    private ProgressDialog mProgresDialog;
    private DatabaseReference mDatabaseUser;
    private Dialog MyDialog;
    private FirebaseUser user;


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        initView(root);
        initViewLogin();


        return root;
    }

    private void initViewLogin() {

        if(user != null) {
            Uri photoUrl = user.getPhotoUrl();
            if(photoUrl != null){
                fotoPerfilCadena = photoUrl.toString();
                Glide.with(getActivity()).load(photoUrl.toString()).into(fotoPerfil);

            }
            String name_ = user.getDisplayName();
            String email_ = user.getEmail();
            name = name_;
            email = email_;
            nombre.setText(name);


        }
    }

    private void initView(final View root) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        mProgresDialog= new ProgressDialog(getActivity());
        fotoPerfil = (CircleImageView) root.findViewById(R.id.fotoPerfil);
        nombre = (TextView) root.findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) root.findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) root.findViewById(R.id.txtMensaje);
        btnEnviar = (Button) root.findViewById(R.id.btnEnviar);
        btnEnviarFoto = (ImageButton) root.findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena = "";

        database = FirebaseDatabase.getInstance();
        //--databaseReference = database.getReference("chat");//Sala de chat (nombre)
        databaseReference = database.getReference("chat");//Sala de chat (nombre)
        storage = FirebaseStorage.getInstance();




        IModal listener = new IModal() {
            @Override
            public void modalIniciar(String nombre, String url, String uidUser) {
                initViewProfile(nombre,url, uidUser);

            }

            @Override
            public void modalIniciarDetail(String id) {

            }

        };

        adapter = new AdapterMensajes(getActivity(), listener);

        LinearLayoutManager l = new LinearLayoutManager(getActivity());
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);
        adapter.setOnEliminarMensajeClickListener(this);

        if(user != null){
            databaseUserOff = database.getReference("Users").child(user.getUid());//Sala de chat (nombre)
            databaseUserOff.keepSynced(true);
        }else{

            initValidarAccess();

        }

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null) {
                    databaseUserOff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("bloqueado").getValue() != null){
                                Toast.makeText(getActivity(),"Bloqueado",Toast.LENGTH_SHORT).show();
                            }else{
                                if(!txtMensaje.getText().toString().isEmpty()){
                                    databaseReference.push().setValue(new MensajeEnviar(txtMensaje.getText().toString(),nombre.getText().toString(),fotoPerfilCadena,"1", ServerValue.TIMESTAMP,""+user.getUid()));
                                    txtMensaje.setText("");
                                }else{
                                    Toast.makeText(getActivity(),"Por favor escribir un mensaje.",Toast.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    initValidarAccess();
                }

            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user != null) {
                    databaseUserOff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("bloqueado").getValue() != null){
                                Toast.makeText(getActivity(),"Bloqueado",Toast.LENGTH_SHORT).show();

                            }else{
                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                i.setType("image/jpeg");
                                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SEND);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    initValidarAccess();
                }



            }
        });


        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(final int positionStart, final int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rvMensajes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(getActivity(),"id: "+positionStart+"coundt: "+itemCount,Toast.LENGTH_SHORT).show();
                    }
                });
                setScrollbar();
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MensajeRecibir m = dataSnapshot.getValue(MensajeRecibir.class);
                adapter.addMensaje(m);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initViewProfile(String nombre, String photoUrlProfile, String uidUser) {



        MyDialog = new Dialog(getActivity());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.modal_view_profile);
        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final Button modal_bloquear = MyDialog.findViewById(R.id.modal_bloquear);
        modal_bloquear.setVisibility(View.GONE);


        TextView name_modal = MyDialog.findViewById(R.id.name_modal);
        ImageView imgMeme = MyDialog.findViewById(R.id.imgMeme);
        name_modal.setText(nombre);
        Glide.with(getActivity())
                .load(photoUrlProfile)
                .into(imgMeme);

        databaseUserOff2 = database.getReference("Users").child(uidUser);
        databaseUserOff2.keepSynced(true);


        databaseUserOff2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("bloqueado").getValue() != null){
                    modal_bloquear.setText("Usuario Bloqueado");
                }else{
                    modal_bloquear.setText("Bloquear Usuario");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        modal_bloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseUserOff2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("bloqueado").getValue() == null){

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("bloqueado", "1");

                            databaseUserOff.updateChildren(childUpdates);
                            modal_bloquear.setText("Usuario Bloqueado");
                        }else{
                            //databaseUserOff.child("bloqueado").removeValue();
                            modal_bloquear.setText("Bloquear Usuario");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });



        MyDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_SEND && resultCode == RESULT_OK){
            if(user != null) {

                myDialog(data);

            }else{
                Toast.makeText(getActivity(),"Necesitas Iniciar Sesión",Toast.LENGTH_LONG).show();
            }

        }
    }

    private void myDialog(Intent data) {
        AlertDialog.Builder makeDialog = new AlertDialog.Builder(getActivity());
        makeDialog.setMessage("Si continuas compartirás la imagen seleccionada.");
        makeDialog.setTitle("Publicar foto");

        makeDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgresDialog.setMessage("Publicando imagen");
                mProgresDialog.setCancelable(true);
                mProgresDialog.show();
                Uri u = data.getData();
                storageReference = storage.getReference("imagenes_chat");//imagenes_chat
                final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
                fotoReferencia.putFile(u).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fotoReferencia.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                //Uri u = taskSnapshot.getDownloadUrl();
                                MensajeEnviar m = new MensajeEnviar(name+" ha compartido una foto",downloadUrl.toString(),nombre.getText().toString(),fotoPerfilCadena,"2", ServerValue.TIMESTAMP,""+user.getUid());
                                databaseReference.push().setValue(m);
                                mProgresDialog.dismiss();

                            }
                        });

                    }
                });
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


    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
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

        startActivity(new Intent(getActivity(), AccessRelato.class));
        getActivity().finish();
    }

    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftKeyboard();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard();
    }

    @Override
    public void onStart() {
        super.onStart();
        hideSoftKeyboard();
    }

    @Override
    public void onEliminarMensajeClick() {
        adapter.notifyDataSetChanged();
    }
}