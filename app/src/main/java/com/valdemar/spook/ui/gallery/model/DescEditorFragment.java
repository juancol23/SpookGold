package com.valdemar.spook.ui.gallery.model;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdemar.spook.R;

import java.util.HashMap;
import java.util.Map;


public class DescEditorFragment extends Fragment {

    private String mPost_key = null;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike, mDatabaseLikeCount;
    private TextInputEditText mPostTitleDetails;
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
    TextInputEditText webViewDetail;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.desc_editor_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        initView(root);
        initFavorite(root);
        return root;

    }





    private void initView(View root) {

        Bundle datosRecuperados = getArguments();
        if (datosRecuperados == null) {
            // No hay datos, manejar excepción
            return;
        }
        mPost_key = datosRecuperados.getString("blog_id");

        mPostTitleDetails = (TextInputEditText) root.findViewById(R.id.postTitleDetails);
        mImage_paralax = (ImageView) root.findViewById(R.id.image_paralax);

        initWebView(root);
    }



    private void initWebView(final View root) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Historias").child(mPost_key);
        mDatabase.keepSynced(true);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String textoCentradoDesc = post_desc;
                webViewDetail = (TextInputEditText) root.findViewById(R.id.webViewDetail);

                mPostTitleDetails.setText(post_title);
                webViewDetail.setText(post_desc);

                Glide.with(getActivity().getApplicationContext())
                        .load(post_image)
                        .into(mImage_paralax);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initFavorite(final View root) {
        mFav_favorite =  root.findViewById(R.id.fav_favorite);
        mVounn_icon =  root.findViewById(R.id.vounn_icon);
        mPostBloquear = root.findViewById(R.id.actualizarHistoria);
        mFav_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    //mDatabase.addValueEventListener()
                    mPostTitleDetails.setEnabled(true);
                    webViewDetail.setEnabled(true);
                    mPostBloquear.setVisibility(View.VISIBLE);
                    showSnackBar("Ahora ya puedes editar.", root);


                }else{
                    showSnackBar("Necesitas Iniciar Sesión", root);
                }
            }
        });

        Button actualizarHistoria = root.findViewById(R.id.actualizarHistoria);
        actualizarHistoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //String post_title = (String) dataSnapshot.child("title").getValue();
                        //String post_desc = (String) dataSnapshot.child("desc").getValue();



                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("title", mPostTitleDetails.getText()+"");
                        childUpdates.put("desc", webViewDetail.getText()+"");

                        mDatabase.updateChildren(childUpdates);
                        mPostBloquear.setVisibility(View.GONE);
                        showSnackBar("La historia fue actualizada correctamente.",root);
                        mPostTitleDetails.setEnabled(false);
                        webViewDetail.setEnabled(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showSnackBar("Problemas al subir, verifique su conexión a internet.",root);

                    }
                });

            }
        });



    }

    private void showSnackBar(String msg, View root) {
        Snackbar
                .make(root.findViewById(R.id.coordinator), msg, Snackbar.LENGTH_LONG)
                .show();
    }


}
