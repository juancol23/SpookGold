package com.valdemar.spook.modo_lectura;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.valdemar.spook.R;
import com.valdemar.spook.model.style_chat.ChatModel;
import com.valdemar.spook.model.style_chat.ChatModelBase;
import com.valdemar.spook.model.style_chat.MensajeStyle;
import com.valdemar.spook.model.style_chat.adapter.AdapterStyleMensajes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class StyleChatFragment extends Fragment {

    private static final int RESULT_OK = -1;
    private DatabaseReference mMensajeShare;
    private ProgressDialog mProgress;

    private LinearLayout mLinearLayout_;


    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private ImageButton btnEnviarFoto;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseUserOff,databaseUserOff2;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;
    private AdapterStyleMensajes adapter;

    private String fotoPerfilCadena;
    private Uri photoUrl;
    private String name = "Cargando...";
    private String email = "Cargando...";

    private ChatModel listMensaje;

    int inicio = 0;
    int fin = 0;

    private String mPost_key = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat_style, container, false);



        initView(root);

        return root;
    }


    private void initView(final View root) {

        Bundle datosRecuperados = getArguments();
        if (datosRecuperados == null) {
            // No hay datos, manejar excepción
            return;
        }
        mPost_key = datosRecuperados.getString("blog_id");


        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("chats");//Sala de chat (nombre)
        databaseReference.keepSynced(true);
        adapter = new AdapterStyleMensajes(getActivity());

        LinearLayoutManager l = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        rvMensajes = root.findViewById(R.id.rvMensajes);
        btnEnviar = root.findViewById(R.id.btnEnviar);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);




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

        databaseReference.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatModelBase m = dataSnapshot.getValue(ChatModelBase.class);
                System.out.println(m);

                String separador = Pattern.quote("|");

                List<MensajeStyle> mensaje_ = new ArrayList<>();


                if(m.getChat() != null){

                    String chat[] = m.getChat().split(separador);
                    String mensaje[];

                    for (int i= 0; i < chat.length;i++){
                        mensaje = chat[i].split("-");
                        MensajeStyle sss = new MensajeStyle();
                        sss.setMensaje(mensaje[0]);
                        sss.setNombre(mensaje[1]);
                        sss.setPosicion(mensaje[2]);

                        mensaje_.add(sss);
                        System.out.println(mensaje);
                    }

                    System.out.println(mensaje_);

                    ChatModel socrates = new ChatModel();
                    socrates.setChat(mensaje_);
                    socrates.setTitulo("Socorro mi amor");
                    socrates.setImagen("www.redtube.com/1uh23i12");


                    //MensajeStyle


                    listMensaje = socrates;

                    fin = listMensaje.getChat().size();

                    btnEnviar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(inicio < fin)
                                adapter.addMensaje(listMensaje.getChat().get(inicio).getNombre(),
                                        listMensaje.getChat().get(inicio).getMensaje(),
                                        listMensaje.getChat().get(inicio).getPosicion());
                            System.out.println(listMensaje);
                            inicio ++;

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });




    }


    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
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
}