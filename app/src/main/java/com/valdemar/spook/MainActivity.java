package com.valdemar.spook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valdemar.spook.databinding.ActivityMainBinding;
import com.valdemar.spook.tiktok.TiktokMainActivity;
import com.valdemar.spook.util.sounds.BackgroundSoundService;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Dialog MyDialog;
    private SharedPreferences monedas = null;
    private TextView mId_monedas_text;

    private CircleImageView mMenu_profile_image;
    private ImageView mConfigurarcion;
    private TextView mMenu_profile_name;
    private TextView mMenu_profile_email;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private ProgressDialog mProgresDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseVersionApp;
    private SharedPreferences prefs_sound = null;
    private SharedPreferences prefs_notificacion = null;

    private RewardedAd rewardedAd;
    private AdRequest adRequest;
    private final static String video_id_test = "ca-app-pub-3940256099942544/5224354917";
    private final static String video_id_prd = "ca-app-pub-5861158224745303/9834495827";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this);
        adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, video_id_test, // Reemplaza con tu ID de unidad de anuncio de AdMob
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;

                    }
                });


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        initView();

    }

    private void initMonedaActual() {
        int miValorGuardado = monedas.getInt("valorGuardadoTest", 0);
        // Suma las monedas nuevas al valor guardado
        // Actualiza el texto en la interfaz de usuario
        mId_monedas_text = (TextView) findViewById(R.id.monedas_profile);
        mId_monedas_text.setText("Monedas: " + miValorGuardado);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);


        storage = FirebaseStorage.getInstance();
        mProgresDialog = new ProgressDialog(MainActivity.this);

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.view_spook, menu);
        mConfigurarcion = findViewById(R.id.configurarcion);
        mConfigurarcion.setVisibility(View.GONE);
        mConfigurarcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ViewSpook.this, Configuration.class));
            }
        });

        mMenu_profile_image = (CircleImageView) findViewById(R.id.menu_profile_image);
        mMenu_profile_name = findViewById(R.id.menu_profile_name);
        mMenu_profile_email = findViewById(R.id.menu_profile_email);
        mId_monedas_text = (TextView) findViewById(R.id.monedas_profile);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Uri photoUrl = user.getPhotoUrl();
            String name = user.getDisplayName();
            String email = user.getEmail();

            mMenu_profile_name.setText(name);
            if (user.getEmail() != null) {
                mMenu_profile_email.setText(email);
            }

            Glide.with(MainActivity.this)
                    .load(photoUrl)
                    .thumbnail(Glide.with(MainActivity.this)
                            .load(R.drawable.b))
                    .into(mMenu_profile_image);
        }
        mMenu_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null) {
                    // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                    mostrarDialog(v);
                }
            }
        });

        initMonedaActual();

        return true;
    }

    private void mostrarDialog(View v) {
        AlertDialog.Builder makeDialog = new AlertDialog.Builder(MainActivity.this);
        makeDialog.setMessage("Si continuar editarás tu foto de perfil");
        makeDialog.setTitle("Foto perfil");

        makeDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_PERFIL);
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

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        validarActualizacion();
        initNotificacion();
        initSettingCheck();

    }



    private void initSettingCheck() {

        if(prefs_sound.getBoolean("prefs_sound", true)){
            Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
            startService(svc);
        }else{
            Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
            stopService(svc);
        }

        if(prefs_notificacion.getBoolean("prefs_notificacion", true)){
            //FirebaseMessaging.getInstance().subscribeToTopic("Historias");
            FirebaseMessaging.getInstance().subscribeToTopic("chat");
        }else{
            //FirebaseMessaging.getInstance().unsubscribeFromTopic("Historias");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("chat");
        }

    }

    public void validarActualizacion(){

        mDatabaseVersionApp = FirebaseDatabase.getInstance().getReference().child("VersionApp");
        mDatabaseVersionApp.keepSynced(true);
        mDatabaseVersionApp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String appVersion = (String) dataSnapshot.child("version").getValue();
                String title = (String) dataSnapshot.child("title").getValue();
                String body = (String) dataSnapshot.child("body").getValue();

                Log.v("PackageInfo",""+appVersion);

                PackageInfo pInfo = null;

                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String version = pInfo.versionName;
                int verCode = pInfo.versionCode;

                Log.v("PackageInfo",""+version);
                Log.v("PackageInfo",""+verCode);

                int verCodeActual = Integer.parseInt(appVersion);

                if(verCode < verCodeActual){
                    ModalCheckUpdate(title,body);
                    Log.v("PackageInfo",""+verCode+""+verCodeActual);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void ModalCheckUpdate(String title,String body){

        MyDialog = new Dialog(MainActivity.this);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.activity_check_update_version_app);
        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView mModal_check_update_title = MyDialog.findViewById(R.id.modal_check_update_title);
        TextView mModal_check_update_body = MyDialog.findViewById(R.id.modal_check_update_body);

        mModal_check_update_title.setText(title);
        mModal_check_update_body.setText(body);

        Button btnModalActualizar = MyDialog.findViewById(R.id.modal_check_update_actualizar);
        Button btnModalCancel = MyDialog.findViewById(R.id.modal_check_update_later);

        btnModalActualizar.setEnabled(true);

        btnModalActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.dismiss();
                Intent intent1 = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="
                                + MainActivity.this.getPackageName()));
                startActivity(intent1);
            }
        });

        btnModalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.hide();
            }
        });

        MyDialog.show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.busquedaSpooky) {
            openCoins();
            return true;
        }if (item.getItemId() == R.id.videoSpooky) {
            startActivity(new Intent(MainActivity.this, TiktokMainActivity.class));
            return true;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.busquedaSpooky) {
            // Toast.makeText(ViewSpook.this,"Buqueda",Toast.LENGTH_SHORT).show();

            openCoins();
            //Toast.makeText(MainActivity.this,"Video .",Toast.LENGTH_SHORT).show();
            return true;
        }if (item.getItemId() == R.id.videoSpooky) {
            startActivity(new Intent(MainActivity.this, TiktokMainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
        }


    public void openCoins() {
        MyDialog = new Dialog(MainActivity.this);

        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.activity_modal_coins_needs);
        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Button btnModalCoinsNeed = MyDialog.findViewById(R.id.modal_coins_need_ver_video);
        Button btnModalSalir = MyDialog.findViewById(R.id.modal_coins_need_salir);

        //if(mRewardedVideoAd.isLoaded()){
            btnModalCoinsNeed.setText("Ver Anuncio");
        //}

        btnModalCoinsNeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedAd();

            }
        });

        btnModalSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.dismiss();
            }
        });

        MyDialog.show();
    }


    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // El usuario ha ganado una recompensa
                    // Puedes manejar la recompensa aquí
                    Toast.makeText(MainActivity.this,"Recompensa conseguida",Toast.LENGTH_SHORT).show();

                    addCoins(5);
                }
            });


        } else {
            // El anuncio no está listo para mostrarse

            Toast.makeText(this,"Video no disponible. else",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addCoins(int coins) {
        // Obtiene el valor guardado previamente
        int miValorGuardado = monedas.getInt("valorGuardadoTest", 0);
        // Suma las monedas nuevas al valor guardado
        int allCoins = miValorGuardado + coins;
        // Actualiza el texto en la interfaz de usuario
        mId_monedas_text.setText("Monedas: " + allCoins);
        // Guarda el nuevo valor en SharedPreferences
        SharedPreferences.Editor editor = monedas.edit();
        editor.putInt("valorGuardadoTest", allCoins);
        editor.apply(); // O usa commit() si prefieres asegurarte que los datos se guarden inmediatamente
        // Registro en el log para depuración
        Log.v("rewardItem", "Total Coins: " + allCoins + ", Previous Coins: " + miValorGuardado + ", Added Coins: " + coins);

        // Carga un anuncio recompensado
        RewardedAd.load(this, video_id_test, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
            }
        });
    }



    private void initNotificacion() {
        prefs_sound = getSharedPreferences("com.valdemar.spook", MODE_PRIVATE);
        prefs_notificacion = getSharedPreferences("com.valdemar.spook", MODE_PRIVATE);
        monedas = getSharedPreferences("relato.app.dems.com.relato.beta", MODE_PRIVATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){
            mProgresDialog.setMessage("Actualizando perfil");
            mProgresDialog.setCancelable(true);
            mProgresDialog.show();


            final FirebaseUser user =  mAuth.getCurrentUser();


            Uri u = data.getData();
            storageReference = storage.getReference("foto_perfil");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fotoReferencia.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            // Uri u = taskSnapshot.getDownloadUrl();
                            String fotoPerfilCadena = downloadUrl.toString();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(fotoPerfilCadena))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "User profile updated.");
                                            }
                                        }
                                    });

                            Glide.with(MainActivity.this)
                                    .load(fotoPerfilCadena)
                                    .thumbnail(Glide.with(MainActivity.this)
                                            .load(R.drawable.b))
                                    .into(mMenu_profile_image);

                            mProgresDialog.dismiss();
                        }
                    });

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initSettingCheck();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
        stopService(svc);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
        stopService(svc);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
        stopService(svc);
    }


}