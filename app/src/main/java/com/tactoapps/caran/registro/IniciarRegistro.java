package com.tactoapps.caran.registro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tactoapps.caran.BuildConfig;
import com.tactoapps.caran.R;
import com.tactoapps.caran.comandos.CmdRegistro;
import com.tactoapps.caran.comandos.CmdRegistro.OnCmdRegistro;
import com.tactoapps.caran.home.Home;
import com.tactoapps.caran.modelo.Modelo;


public class IniciarRegistro extends Activity {


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    Modelo model = Modelo.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_iniciar_registro);
        progressDialog = new ProgressDialog(this);

        TextView textoBienvenida = findViewById(R.id.textoBienvenida);

        textoBienvenida.setText(model.params.textoBienvenida);


        if (estaConectado()) {


            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        progressDialog.setMessage("Validando la información Por favor, espere...");
                        progressDialog.show();

                        ingresarComoCliente(user);


                    } else {
                        // Users is signed out
                        Log.d("REGISTRO", "onAuthStateChanged:signed_out" + user + "no logueado");
                        //Toast.makeText(getApplication(),"...."+user+"no logueado", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        } else {
            // Toast.makeText(getApplicationContext(),"Sin Conexión a Internet", Toast.LENGTH_SHORT).show();
            showAlertSinInternet();
            return;
        }




    }


    public void ingresarComoCliente(final FirebaseUser user) {

        model.cliente.id = user.getUid();
        String token = FirebaseInstanceId.getInstance().getToken();


        String provider =  user.getProviderId();
        if (provider.equals("firebase")) {
            model.tipoLogeo = "normal";
        } else {
            model.tipoLogeo = "faceb";
        }


       /* if (user.getProviders().size() > 0) {
            provider = user.getProviders().get(0);

            if (provider.equals("password")) {
                model.tipoLogeo = "normal";
            } else {
                model.tipoLogeo = "faceb";
            }
        } else {
            model.tipoLogeo = "anonimo";
        }*/





        if (model.tipoLogeo.equals("faceb")) {
            CmdRegistro.enviarRegistro3("" + user.getDisplayName(), "" + user.getUid(), user.getEmail());
        }

        if (model.tipoLogeo.equals("normal")) {
            CmdRegistro.enviarRegistro2(user.getUid());
        }

        if (model.tipoLogeo.equals("anonimo")) {
            CmdRegistro.enviarRegistro2(user.getUid());
        }

        continuarCarga(user.getUid());

    }




    private void continuarCarga(String uid) {

        CmdRegistro.getUsuario(uid, new OnCmdRegistro() {
            @Override
            public void clienteNoExiste() {

            }

            @Override
            public void gotCliente() {
                if (model.cliente.tieneDatosBasicos() || model.tipoLogeo.equals("anonimo")) {
                    preCargar();
                } else {
                    Intent i = new Intent(getApplicationContext(), RegistroComplementario.class);
                    startActivity(i);
                    finish();
                }

            }
        });

    }



    private void preCargar() {

        Intent i = new Intent(getApplicationContext(), Home.class);
        startActivity(i);
        finish();

    }



    public void didTapRegistrarse(View v) {
        Intent i = new Intent(getApplicationContext(), ClienteRegistro.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
        finish();
    }


    public void didTapIngresar(View v) {
        Intent i = new Intent(getApplicationContext(), ClienteLogin.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
        finish();
    }


    //validacion conexion internet
    protected Boolean estaConectado() {
        if (conectadoWifi()) {
            Log.v("wifi", "Tu Dispositivo tiene Conexion a Wifi.");
            return true;
        } else {
            if (conectadoRedMovil()) {
                Log.v("Datos", "Tu Dispositivo tiene Conexion Movil.");
                return true;
            } else {
                showAlertSinInternet();
                // Toast.makeText(getApplicationContext(),"Sin Conexión a Internet", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    protected Boolean conectadoWifi() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Boolean conectadoRedMovil() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showAlertSinInternet() {

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                IniciarRegistro.this);

        // set title
        alertDialogBuilder.setTitle("Sin Internet");

        // set dialog message
        alertDialogBuilder
                .setMessage("Sin Conexión a Internet")
                .setCancelable(false)
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent inte = new Intent(getBaseContext(), ClienteRegistro.class);
                        startActivity(inte);
                    }
                });

        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void saltarRegistro(View v) {
       /* Intent i = new Intent(getApplicationContext(), HomeCliente.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
        finish();
       */
        signInAnonymously();
    }



    private void signInAnonymously() {

        progressDialog.setMessage("Cargado ...");
        progressDialog.show();
        // [START signin_anonymously]
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("REGISTRO", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            registrarAnonimo(user);

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("REGISTRO", "signInAnonymously:failure", task.getException());
                            Toast.makeText(IniciarRegistro.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]
    }


    public void registrarAnonimo(final FirebaseUser user) {

        String versionName = BuildConfig.VERSION_NAME;

        CmdRegistro.registroInicialCliente("anonimo@mail.com", user.getUid(), versionName, true);

    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAuth == null || mAuthListener == null) {
            return;
        } else {
            mAuth.addAuthStateListener(mAuthListener);
        }


    }

    @Override
    public void onStop() {
        super.onStop();


        if (mAuth == null || mAuthListener == null) {
            return;
        } else {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }



}
