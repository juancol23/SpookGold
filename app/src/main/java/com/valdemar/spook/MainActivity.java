package com.valdemar.spook;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.valdemar.spook.databinding.ActivityMainBinding;
import com.valdemar.spook.tiktok.TiktokMainActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Dialog MyDialog;
    private SharedPreferences monedas = null;
    private TextView mId_monedas_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
            Toast.makeText(MainActivity.this,
                    "Video .",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else {
            startActivity(new Intent(MainActivity.this, TiktokMainActivity.class));
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.busquedaSpooky) {
            // Toast.makeText(ViewSpook.this,"Buqueda",Toast.LENGTH_SHORT).show();
            openCoins();
            Toast.makeText(MainActivity.this,
                    "Video .",
                    Toast.LENGTH_SHORT).show();
            return true;
        }else if (item.getItemId() == R.id.videoSpooky) {
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
               /* if(mRewardedVideoAd.isLoaded()){
                    mRewardedVideoAd.show();
                }else{
                    Toast.makeText(ViewSpook.this,
                            "Video no disponible.",
                            Toast.LENGTH_SHORT).show();
                }*/
                Toast.makeText(MainActivity.this,
                        "Video no disponible.",
                        Toast.LENGTH_SHORT).show();
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

    private void addCoins(int coins) {

        int miValorGuardado = monedas.getInt("valorGuardadoTest", 0);
        int allCoins  = miValorGuardado +coins;

        mId_monedas_text.setText("Monedas: "+allCoins);
        monedas.edit().putInt("valorGuardadoTest", allCoins).apply();
        Log.v("rewardItem",""+allCoins+miValorGuardado+coins);

    }



}