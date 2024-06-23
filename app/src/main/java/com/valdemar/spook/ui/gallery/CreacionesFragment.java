package com.valdemar.spook.ui.gallery;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.valdemar.spook.MainActivity;
import com.valdemar.spook.R;
import com.valdemar.spook.ui.gallery.model.DescEditorFragment;
import com.valdemar.spook.ui.gallery.model.ItemFeed;
import com.valdemar.spook.ui.gallery.model.RelatoViewHolderStructure;
import com.valdemar.spook.ui.home.HomeFragment;
import com.valdemar.spook.view.AccessRelato;


public class CreacionesFragment extends Fragment {

    private RecyclerView mRecyclerMisLecturas;
    private DatabaseReference mDatabaseMisLecturas;
    private ProgressDialog mProgress;
    private Dialog MyDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_creaciones, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            initValidarAccess();
        } else {
            initView(root);
        }

        return root;
    }

    private void initView(View root) {
        mProgress = new ProgressDialog(getContext());
        mDatabaseMisLecturas = FirebaseDatabase.getInstance().getReference().child("Historias");
        mDatabaseMisLecturas.keepSynced(true);
        LinearLayoutManager layoutManagerMisLecturas
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        layoutManagerMisLecturas.setReverseLayout(true);
        layoutManagerMisLecturas.setStackFromEnd(true);

        mRecyclerMisLecturas = (RecyclerView) root.findViewById(R.id.fragmento_mis_lecturas);
        mRecyclerMisLecturas.setHasFixedSize(true);

        mRecyclerMisLecturas.setLayoutManager(layoutManagerMisLecturas);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();


        Query queryRef = mDatabaseMisLecturas.orderByChild("IdMiLectura").equalTo(userId);

        FirebaseRecyclerOptions<ItemFeed> options = new FirebaseRecyclerOptions.Builder<ItemFeed>()
                .setQuery(queryRef, ItemFeed.class)
                .build();

        FirebaseRecyclerAdapter<ItemFeed, RelatoViewHolderStructure> firebaseRecyclerAdapterMyLecturas =
                new FirebaseRecyclerAdapter<ItemFeed, RelatoViewHolderStructure>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull RelatoViewHolderStructure viewHolder, int position, @NonNull ItemFeed model) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setCatergory(model.getCategory());
                        viewHolder.setAuthor(model.getAuthor());
                        viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());

                        viewHolder.mViewStructure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mProgress.setMessage("Accediendo...");
                                mProgress.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgress.hide();
                                        mProgress.dismiss();
                                        viewDetails(post_key);
                                    }
                                }, 100);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RelatoViewHolderStructure onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.design_structure_relato_menu, parent, false);
                        return new RelatoViewHolderStructure(view);
                    }
                };

        mRecyclerMisLecturas.setAdapter(firebaseRecyclerAdapterMyLecturas);
        firebaseRecyclerAdapterMyLecturas.startListening();


    }


    private void viewDetails(String post_key) {
        DescEditorFragment descEditorFragment = new DescEditorFragment();
        Bundle datosSend = new Bundle();
        datosSend.putString("blog_id", post_key);
        descEditorFragment.setArguments(datosSend);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenido_dinamico, descEditorFragment)
                .addToBackStack(null).commit();
        Log.v("id", "id" + post_key);

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

    private void iniciaSesion() {
        startActivity(new Intent(getActivity(), AccessRelato.class));
    }
}

