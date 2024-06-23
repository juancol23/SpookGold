package com.valdemar.spook.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.valdemar.spook.MainActivity;
import com.valdemar.spook.R;
import com.valdemar.spook.utilidades.ValidarEmail;

public class AccessRelato extends AppCompatActivity {

    //progreso
    private ProgressDialog mProgress;

    //Editext
    private EditText mAccesso_login_email;

    //@BindView(R.id.accesso_login_password)
    private EditText mAccesso_login_password;

    //@BindView(R.id.accesso_login_btn)
    private Button mAccesso_login_btn;

    // @BindView(R.id.acceso_create_register)
    private TextView mAcceso_create_register;

    //@BindView(R.id.acceso_forget_password)
    private TextView mAcceso_forget_password;

    //login
    FirebaseAuth mAuth;

    private static Typeface Pacifico;

    private TextView splash_text_spook;

    private static final int RC_SIGN_IN = 1;

    private String TAG = "TAG";

    private SharedPreferences prefs_notificacion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_relato);
        initView();
    }

    private void initView() {
        hideSoftKeyboard();
        splash_text_spook = findViewById(R.id.splash_text_spook);
        mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        mAccesso_login_btn = findViewById(R.id.accesso_login_btn);

        mAcceso_create_register = findViewById(R.id.acceso_create_register);
        mAcceso_forget_password = findViewById(R.id.acceso_forget_password);


        mAccesso_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        mAcceso_create_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccessRelato.this,RegisterRelato.class));
                System.out.println("registrando");
            }
        });

        mAcceso_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccessRelato.this,ForgetPasswordRelato.class));
            }
        });
        //initFonts();
        checkaccess();

    }

    private void checkaccess() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            Log.v("sesion","Inciado");
            accesoPermitido();
        }else{
            Log.v("sesion","Sin Iniciar");
        }
    }

    private void initFonts() {
        String pacificoFuente= "fuentes/BloodLust.ttf";
        this.Pacifico = Typeface.createFromAsset(getAssets(),pacificoFuente);
        splash_text_spook.setTypeface(Pacifico);
    }
    private void startLogin() {
        Log.v("accesoPermitido", "Ingresando");


        mAccesso_login_email = findViewById(R.id.accesso_login_email);
        mAccesso_login_password = findViewById(R.id.accesso_login_password);

        String display_email = mAccesso_login_email.getText().toString();
        String display_password = mAccesso_login_password.getText().toString();

        if (!validarEmail(display_email)){
            showSnackBar("Email no válido.");
            mAccesso_login_email.setError("Email no válido.");
        }else if(display_password.length() < 6){
            showSnackBar("La contraseña es muy corta.");
            mAccesso_login_password.setError("Mínimo 6 dígitos.");
        }else{
            //showSnackBar("Accediendo");
            mProgress.setTitle("Iniciando Sessión...");
            mProgress.setMessage("Por espere mientras verificamos sus credenciales.");
            mProgress.setCancelable(false);
            mProgress.show();
            loginUser(display_email,display_password);
        }
    }

    private boolean validarEmail(String display_email) {
        ValidarEmail check = new ValidarEmail(getApplicationContext());
        boolean booleamCheckEmail = check.checkEmail(display_email);
        Log.v("checkEmail",""+booleamCheckEmail);
        return booleamCheckEmail;
    }

    private void loginUser(String display_email, String display_password) {
        mProgress.setCancelable(false);
        mProgress.setMessage("Accediendo ...");
        mProgress.show();

        mAuth.signInWithEmailAndPassword(display_email,display_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            accesoPermitido();
                        }else{
                            Log.v("task_valdemar",""+task);
                            mProgress.hide();
                            showSnackBar("Acceso denegado, Intente nuevamente.");
                            mProgress.dismiss();
                        }
                    }
                });


    }

    public void accesoPermitido() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            Uri photoUrl = user.getPhotoUrl();
            String name = user.getDisplayName();
            String email = user.getEmail();
            String email2 = user.getEmail();

        }

        mProgress.dismiss();
        Intent i = new Intent(AccessRelato.this, MainActivity.class);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();

        Log.v("accesoPermitido", "Ingresando");
        showSnackBar("Acceso Permitido ...");
        prefs_notificacion = getSharedPreferences("com.valdemar.spook.notificacion", MODE_PRIVATE);

        prefs_notificacion.edit().putBoolean("prefs_notificacion", true).commit();


    }

    public void showSnackBar(String msg) {
        Snackbar
                .make(findViewById(R.id.linearAccesso), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}