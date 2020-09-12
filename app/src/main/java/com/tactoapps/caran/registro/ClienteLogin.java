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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.tactoapps.caran.MainActivity;
import com.tactoapps.caran.comandos.CmdRegistro;
import com.tactoapps.caran.comandos.CmdRegistro.OnCmdRegistro;
import com.tactoapps.caran.home.Home;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.R;


import java.util.regex.Pattern;


public class ClienteLogin extends Activity {

    EditText email,password;
    Button iniciarsession;
    final Context context = this;

    ProgressBar progressBar4;

    //bd
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    private ProgressDialog mProgressDialog;
    Modelo modelc = Modelo.getInstance();
    private static final String TAG ="AndroidBash";
    String correo1,password1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cliente_login);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        iniciarsession = (Button) findViewById(R.id.iniciarsessionoferente);
        progressBar4 =(ProgressBar)findViewById(R.id.login_progress);
        progressDialog = new ProgressDialog(this);



        if(estaConectado()){

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        modelc.cliente.id = user.getUid();

                        CmdRegistro.getUsuario(user.getUid(), new OnCmdRegistro() {
                            @Override
                            public void clienteNoExiste() {

                            }

                            @Override
                            public void gotCliente() {
                                ingresarComoCliente(user);
                            }
                        });


                    } else {
                        // Users is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out"+user+"no logueado");
                        // Toast.makeText(getApplication(),"...."+user+"no logueado", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }else{
            // Toast.makeText(getApplicationContext(),"Sin Conexión a Internet", Toast.LENGTH_SHORT).show();
            showAlertSinInternet();
            return;
        }




        //FaceBook


        email.clearFocus();
        password.clearFocus();



    }



    public void ingresarComoCliente(FirebaseUser user ){

        modelc.cliente.id = user.getUid();


        //String provider =  user.getProviderId();
        //if (provider.equals("firebase")) {
            modelc.tipoLogeo = "normal";
            CmdRegistro.enviarRegistro2(user.getUid());
        //} else {
          //  modelc.tipoLogeo = "faceb";
          //  CmdRegistro.enviarRegistro3(user.getDisplayName(), user.getUid(), user.getEmail());
        //}


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



       /* if (user.getProviders().size() > 0) {

            String provider = user.getProviders().get(0);
            if (provider.equals("password")) {
                modelc.tipoLogeo = "normal";
            }else{
                modelc.tipoLogeo = "faceb";
            }

            if(modelc.tipoLogeo.equals("faceb")){



            }

            if(modelc.tipoLogeo.equals("normal")){
                CmdRegistro.enviarRegistro2(user.getUid());
            }


        }*/

        if (user.isAnonymous()){
            CmdRegistro.enviarRegistro2(user.getUid());
        }



        if ( modelc.cliente.tieneDatosBasicos() ) {
            preCargar();
        } else {
            Intent i = new Intent(getApplicationContext(),RegistroComplementario.class);
            startActivity(i);
            finish();
        }





    }




    private void preCargar() {

        Intent i = new Intent(getApplicationContext(), Home.class);
        startActivity(i);
        finish();

    }



    //atras hadware
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
            finish();
        }
        return true;
    }



    public void loguinCliente(View v){

        if(estaConectado()){
            if(email.getText().toString().equals("")) {
                email.setError("Correo obligatorio");
                showAlertMsm("Ingreso fallido","Tus datos no parecen estar correctos. Revisa tus datos y/o\n" +
                        "        tu acceso a Internet");
            }
            else if (!validarEmail(email.getText().toString())){
                email.setError("Email no válido");
            }
            else if(password.getText().toString().equals("")) {
                password.setError("Contraseña obligatoria");
                showAlertMsm("Ingreso fallido","Tus datos no parecen estar correctos. Revisa tus datos y/o\n" +
                        "        tu acceso a Internet");
            }
            else if(password.getText().length()<3) {
                password.setError("Contraseña incorrecta");
            }
            else{

                logueo();
            }
        }else{
            showAlertSinInternet();
        }
    }


    public void olvidopaswword(View v){
        if(email.getText().toString().equals("")) {
            email.setError("Correo obligatorio");
            showAlertMsm("e-mail Inválido","El e-mail no es válido o no existe en nuestros registros,\n" +
                    "     escríbelo correctamente para que puedas actualizar tu contraseña.");
        }
        else if (!validarEmail(email.getText().toString())){
            email.setError("Email no válido");
        }else {

            Log.v("ok", "ok");
            progressBar4.setVisibility(View.VISIBLE);
            String emailId = email.getText().toString();
            mAuth.sendPasswordResetEmail(emailId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ClienteLogin.this, "Las instrucciones han sido enviados a su correo electrónico", Toast.LENGTH_SHORT).show();
                                progressBar4.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(ClienteLogin.this, "¡Lo siento! Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                                progressBar4.setVisibility(View.GONE);
                            }

                        }
                    });
        }

    }

    //validacion conexion internet
    protected Boolean estaConectado(){
        if(conectadoWifi()){
            Log.v("wifi","Tu Dispositivo tiene Conexion a Wifi.");
            return true;
        }else{
            if(conectadoRedMovil()){
                Log.v("Datos", "Tu Dispositivo tiene Conexion Movil.");
                return true;
            }else{
                showAlertSinInternet();
                // Toast.makeText(getApplicationContext(),"Sin Conexión a Internet", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }



    protected Boolean conectadoWifi(){
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

    protected Boolean conectadoRedMovil(){
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




    public void showAlertSinInternet(){

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Sin Internet");

        // set dialog message
        alertDialogBuilder
                .setMessage("Sin Conexión a Internet")
                .setCancelable(false)
                .setPositiveButton("Reintentar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent inte  = new Intent(getBaseContext(),ClienteLogin.class);
                        startActivity(inte);
                    }
                });

        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


    public void showAlertMsm(String titulo, String descripcion){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(""+titulo);

        // set dialog message
        alertDialogBuilder
                .setMessage(""+descripcion)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    //validar email
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // callbackManager.onActivityResult(requestCode,
               // resultCode, data);
    }



    @Override
    public void onStart() {
        super.onStart();
        if(mAuth==null || mAuthListener ==null){
            return;
        }else{
            mAuth.addAuthStateListener(mAuthListener);
        }


    }

    @Override
    public void onStop() {
        super.onStop();


        if(mAuth==null || mAuthListener ==null){
            return;
        }else{
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    //logueo
    public void logueo(){
        correo1 = email.getText().toString().toString();
        password1= password.getText().toString().toString();
        // Toast.makeText(this, "Se guarda el registro", Toast.LENGTH_LONG).show();
        progressDialog.setMessage("Validando la información Por favor, espere...");
        progressDialog.show();
        iniciarsession.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:"+task.isSuccessful());

                        if (task.getException() == null){
                            return;
                        }

                        if (task.getException() instanceof FirebaseNetworkException){
                            Log.d(TAG, "signInWithEmail:onComplete:"+"ERROR DE RED");
                            Toast.makeText(getApplication(),""+"No hay acceso a internet", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            iniciarsession.setEnabled(true);
                            return;
                        }

                        FirebaseAuthException ex = (FirebaseAuthException) task.getException();

                        String error = ex.getErrorCode();

                        if (error.equals("ERROR_INVALID_EMAIL")){
                            Log.d(TAG, "signInWithEmail:onComplete:"+"ERROR CON EL CORREO");
                            Toast.makeText(getApplication(),""+"Correo no valido", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        if (error.equals("ERROR_USER_NOT_FOUND")){
                            Log.d(TAG, "signInWithEmail:onComplete:" + "USUARIO NUEVO");
                            Toast.makeText(getApplication(),""+"USUARIO NO REGISTRADO", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        if (error.equals("ERROR_WRONG_PASSWORD")){
                            Log.d(TAG, "signInWithEmail:onComplete:" + "CONTRASEÑA INCORRECTA");
                            Toast.makeText(getApplication(),""+"CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        if (!task.isSuccessful()){
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.getException());
                            Toast.makeText(getApplication(),""+"FALLO EN LA AUTENTICACION", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        iniciarsession.setEnabled(true);

                    }
                });
    }




    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}

