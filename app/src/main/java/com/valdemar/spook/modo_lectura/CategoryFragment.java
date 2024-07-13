package com.valdemar.spook.modo_lectura;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.valdemar.spook.R;
import com.valdemar.spook.holder.CategoryViewHolder;
import com.valdemar.spook.model.Category;
import com.valdemar.spook.model.style_chat.ChatModelBase;

import kotlin.jvm.functions.Function1;

public class CategoryFragment extends Fragment {
    private AdView mAdView;
    private RecyclerView mRecycler;
    private DatabaseReference mDatabase,mDataBaseChatStyle;
    private ProgressDialog mProgress;

    private long nroMeGusta = 0;
    private long nroMeEncanta = 0;
    private long nroMeAsombra = 0;

    private Integer[] reactionsQuantity = {0,0,0};
    private final String[] reaction_keys = {
            "me_gusta", "me_encanta", "me_asombra"
    };
    private final String[] strings = {
            "me gusta", "me encanta", "wow"
    };

    final DatabaseReference mReactionsRef= FirebaseDatabase.getInstance().getReference()
            .child("Reacciones").child("id_proyectos");

    private String blog_category = null;

    private LottieAnimationView mLottieAnimationView;
    private NestedScrollView mRecy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_category, container, false);
        initLoading(root);
        initConfigNetwork();
        initView(root);
        initAnuncio(root);
        return root;
    }

    private void initLoading(View root) {

        mRecy = root.findViewById(R.id.recy);

        mLottieAnimationView = root.findViewById(R.id.animationView);
        mLottieAnimationView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecy.setVisibility(View.VISIBLE);
            }
        },1000);
    }

    private void initAnuncio(View root) {
        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initConfigNetwork() {
        mProgress = new ProgressDialog(getActivity().getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Historias");
        mDatabase.keepSynced(true);

        mDataBaseChatStyle = FirebaseDatabase.getInstance().getReference().child("chats");
        mDataBaseChatStyle.keepSynced(true);

    }

    private void initView(View root) {

        Bundle datosRecuperados = getArguments();
        if (datosRecuperados == null) {
            // No hay datos, manejar excepción
            return;
        }
        blog_category = datosRecuperados.getString("blog_category");
        if(blog_category.equalsIgnoreCase("chats")){
            initViewGrillaCategoryChats(root);
        }else{
            initViewGrillaCategory(root,blog_category);
        }

    }

    private void initViewGrillaCategory(final View root, String blog_category) {
        Query queryategorysall = mDatabase.orderByChild("category").equalTo(blog_category);

        mRecycler = root.findViewById(R.id.recyclerAll);
        mRecycler.setHasFixedSize(true);

        // Configurar GridLayoutManager con 2 columnas (ajusta según tus necesidades)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecycler.setLayoutManager(gridLayoutManager);

        mRecycler.setItemViewCacheSize(100); // Incrementa el tamaño del caché
        mRecycler.setDrawingCacheEnabled(true);
        mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Configurar FirebaseRecyclerOptions
        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(queryategorysall, Category.class)
                        .build();

        // Adaptador FirebaseRecyclerAdapter
        final FirebaseRecyclerAdapter<Category, CategoryViewHolder> firebaseRecyclerAdaptermRecyclerEpisodiosPerdidos =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull CategoryViewHolder viewHolder, @SuppressLint("RecyclerView") int position, @NonNull Category model) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setSendBy(model.getAuthor());

                        // Asegúrate de que el método setImage esté correctamente implementado en CategoryViewHolder
                        viewHolder.setImage(getActivity(), model.getImage());

                        Log.v("Seguimiento", "dentro");

                        viewHolder.mViewStructure_h.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewDetails(post_key, root);
                            }
                        });
                /*
                        viewHolder.mReaction_icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reacciones(post_key, viewHolder, position, mRecycler);
                            }
                        });     */
                    }

                    @NonNull
                    @Override
                    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);
                        return new CategoryViewHolder(view);
                    }
                };

        mRecycler.setAdapter(firebaseRecyclerAdaptermRecyclerEpisodiosPerdidos);

        // Iniciar escucha del adaptador al iniciar la actividad

        firebaseRecyclerAdaptermRecyclerEpisodiosPerdidos.startListening(); ((SimpleItemAnimator) mRecycler.getItemAnimator()).setSupportsChangeAnimations(false); // Disable animations



        // Detener escucha del adaptador al detener la actividad
        // Asegúrate de manejar el ciclo de vida del adaptador correctamente
        // por ejemplo, en el método onStop de la actividad
    /*
    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdaptermRecyclerEpisodiosPerdidos.stopListening();
    }
    */
    }


    private void initViewGrillaCategoryChats(final View root) {
        Query queryategorysall = mDataBaseChatStyle;

        mRecycler = root.findViewById(R.id.recyclerAll);
        mRecycler.setHasFixedSize(true);

        // Configurar GridLayoutManager con 2 columnas (ajusta según tus necesidades)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecycler.setLayoutManager(gridLayoutManager);

        // Configurar FirebaseRecyclerOptions
        FirebaseRecyclerOptions<ChatModelBase> options =
                new FirebaseRecyclerOptions.Builder<ChatModelBase>()
                        .setQuery(queryategorysall, ChatModelBase.class)
                        .build();

        // Adaptador FirebaseRecyclerAdapter
        final FirebaseRecyclerAdapter<ChatModelBase, CategoryViewHolder> firebaseRecyclerAdaptermRecyclerEpisodiosPerdidos =
                new FirebaseRecyclerAdapter<ChatModelBase, CategoryViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull CategoryViewHolder viewHolder, @SuppressLint("RecyclerView") int position, @NonNull ChatModelBase model) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitulo());
                        viewHolder.setSendBy(model.getAuthor());

                        // Asegúrate de que el método setImage esté correctamente implementado en CategoryViewHolder
                        viewHolder.setImage(getActivity(), model.getImagen());

                        Log.v("Seguimiento", "dentro");

                        viewHolder.mViewStructure_h.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewDetailsChatStyle(post_key);
                            }
                        });

                        /*viewHolder.mReaction_icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reacciones(post_key, viewHolder, position, mRecycler);
                            }
                        });*/
                    }

                    @NonNull
                    @Override
                    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);
                        return new CategoryViewHolder(view);
                    }
                };

        mRecycler.setAdapter(firebaseRecyclerAdaptermRecyclerEpisodiosPerdidos);

        firebaseRecyclerAdaptermRecyclerEpisodiosPerdidos.startListening(); ((SimpleItemAnimator) mRecycler.getItemAnimator()).setSupportsChangeAnimations(false); // Disable animations

    }

    private void viewDetails(String post_key, View root){

        DescBlankFragment descBlankFragment = new DescBlankFragment();

        Bundle datosSend = new Bundle();
        datosSend.putString("blog_id", post_key);
        descBlankFragment.setArguments(datosSend);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenido_dinamico, descBlankFragment)
                .addToBackStack(null).commit();


        Log.v("id","id"+post_key);
    }

    private void viewDetailsChatStyle(String post_key){
        // mProgress.setMessage("Accediendo...");
        //mProgress.show();
        //mProgress.setCancelable(true);


        StyleChatFragment styleChatFragment = new StyleChatFragment();

        Bundle datosSend = new Bundle();
        datosSend.putString("blog_id", post_key);
        styleChatFragment.setArguments(datosSend);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenido_dinamico, styleChatFragment)
                .addToBackStack(null).commit();

        //mProgress.dismiss();
        Log.v("id","id"+post_key);
    }

    private void reacciones(final String post_key, final CategoryViewHolder viewHolder, final int position, final RecyclerView mRecycler) {

        String uID = "";

        try{
            if(FirebaseAuth.getInstance().getCurrentUser().getUid() != null){
                uID =FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
        }catch (Exception e){
            uID = "xxx";

        }



        final String finalUID = uID;
        mReactionsRef.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nroMeGusta = dataSnapshot.child("me_gusta").getChildrenCount();
                nroMeEncanta = dataSnapshot.child("me_encanta").getChildrenCount();
                nroMeAsombra = dataSnapshot.child("me_asombra").getChildrenCount();
                viewHolder.meGustaImage.setVisibility(nroMeGusta>0?View.VISIBLE:View.GONE);
                viewHolder.meEncantaImage.setVisibility(nroMeEncanta>0?View.VISIBLE:View.GONE);
                viewHolder.meAsombraImage.setVisibility(nroMeAsombra>0?View.VISIBLE:View.GONE);
                long totalReacciones = nroMeGusta + nroMeEncanta + nroMeAsombra;
                if(totalReacciones>0)  {
                    viewHolder.nroReactions.setVisibility(View.VISIBLE);
                    viewHolder.nroReactions.setText(String.valueOf(totalReacciones));
                }else {
                    viewHolder.nroReactions.setVisibility(View.GONE);
                    viewHolder.nroReactions.setText(" ");
                }
                viewHolder.reactionImage.setImageResource(R.drawable.favorite_flaco);
                viewHolder.reactionImage.setTag("empty_reaction");

                if (dataSnapshot.child("me_gusta").hasChild(finalUID)) {
                    viewHolder.reactionImage.setImageResource(R.mipmap.ic_fb_like);
                    viewHolder.reactionImage.setTag("me_gusta");
                }else if (dataSnapshot.child("me_encanta").hasChild(finalUID)) {
                    viewHolder.reactionImage.setImageResource(R.mipmap.ic_fb_love);
                    viewHolder.reactionImage.setTag("me_encanta");
                } else if (dataSnapshot.child("me_asombra").hasChild(finalUID)) {
                    viewHolder.reactionImage.setImageResource(R.mipmap.ic_fb_wow);
                    viewHolder.reactionImage.setTag("me_asombra");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Log.e("EJEMPLO", "onReactionClick: ");

        ReactionPopup popup = new ReactionPopup(
                getActivity(),
                new ReactionsConfigBuilder(getActivity())
                        .withReactions(new int[]{
                                R.mipmap.ic_fb_like,
                                R.mipmap.ic_fb_love,
                                R.mipmap.ic_fb_wow,
                        })
                        .withReactionTexts(new Function1<Integer, CharSequence>() {
                            @Override
                            public CharSequence invoke(Integer position) {
                                return strings[position];
                            }
                        })
                        .withTextBackground(new ColorDrawable(Color.TRANSPARENT))
                        .withTextColor(Color.BLUE)
                        .withTextHorizontalPadding(0)
                        .withTextVerticalPadding(0)
                        .withTextSize(getActivity().getResources().getDimension(R.dimen.reactions_text_size))
                        .build(),
                new Function1<Integer, Boolean>() {
                    @Override
                    public Boolean invoke(Integer position) {
                        return true;
                    }
                });


        final String finalUID1 = uID;
        popup.setReactionSelectedListener(new Function1<Integer, Boolean>() {
            @Override
            public Boolean invoke(Integer reactionPosition) {
                if (reactionPosition != -1) {

                    //holder.likeImage.getBackground().getAlpha() R.drawable.ic_fb_like
                    String reaccion = (reaction_keys[reactionPosition]);
                    String tagReaction = viewHolder.reactionImage.getTag().toString();
                    if(tagReaction.equalsIgnoreCase(reaccion)){
                        mReactionsRef.child(post_key).child(reaccion).child(finalUID1).removeValue();
                    }else {
                        mReactionsRef.child(post_key).child(reaccion).child(finalUID1).setValue("id_emprendedor");
                        for (int i = 0; i < reaction_keys.length; i++) {
                            if(i!=reactionPosition){
                                String reaccionDelete = (reaction_keys[i]);
                                mReactionsRef.child(post_key).child(reaccionDelete).child(finalUID1).removeValue();
                            }
                        }
                    }

                    mRecycler.getAdapter().notifyItemChanged(position);
                }

                // Close selector if not invalid item (testing purpose)
                return true;
            }
        });

        viewHolder.reactionImage.setOnTouchListener(popup);
    }


}