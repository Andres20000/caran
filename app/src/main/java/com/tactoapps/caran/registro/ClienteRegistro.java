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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;

import com.tactoapps.caran.BuildConfig;
import com.tactoapps.caran.R;
import com.tactoapps.caran.comandos.CmdRegistro.OnCmdRegistro;
import com.tactoapps.caran.home.Home;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Cliente;
import com.tactoapps.caran.comandos.CmdRegistro;
import com.tactoapps.caran.modelo.sistema.Utility;


public class ClienteRegistro extends Activity {

    EditText email, password;
    ProgressBar progressBar;
    Button entrar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    Modelo model = Modelo.getInstance();
    private static final String TAG ="AndroidBash";
    public Cliente cliente;
    private ProgressDialog mProgressDialog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final Context context = this;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_cliente_registro);


        email =(EditText)findViewById(R.id.email);
        password =(EditText)findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        entrar = (Button) findViewById(R.id.registro);



        if(estaConectado()){

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        if (user.isAnonymous()){
                            return;
                        }

                        model.cliente.id = user.getUid();

                        String provider =  user.getProviderId();

                        model.tipoLogeo = "normal";
                        CmdRegistro.enviarRegistro2(user.getUid());


                        CmdRegistro.getUsuario(user.getUid(), new OnCmdRegistro() {
                            @Override
                            public void clienteNoExiste() {

                            }

                            @Override
                            public void gotCliente() {
                                continuarCarga();
                            }
                        });




                    } else {
                        // Users is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out" + " no logueado");
                    }

                }
            };
        }else{
            // Toast.makeText(getApplicationContext(),"Sin Conexión a Internet", Toast.LENGTH_SHORT).show();
            showAlertSinInternet();
            return;
        }




        email.clearFocus();
        password.clearFocus();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode,
          //      resultCode, data);
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

        }else{
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }




    public void IrIngresar(View v){
        Intent i = new Intent(getApplicationContext(), ClienteLogin.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

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



    public void registroCliente(View v){
        if (!Utility.isEmailValid(email.getText().toString()) || password.getText().toString().equals("")){
            android.app.AlertDialog.Builder dialogo1 = new android.app.AlertDialog.Builder(this);
            dialogo1.setTitle("Campos Erroneos");
            dialogo1.setMessage("Todos los campos son obligatorios");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    return;
                }
            });
            dialogo1.show();
            return;
        }


        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isAnonymous()){
            //linkAccount();
        }else {
            hacerRegistro();
        }


    }







    public void hacerRegistro(){

        showProgressDialog();


        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //checking if success
                            Log.d(TAG, "signInWithEmail:onComplete:"+task.isSuccessful());

                            FirebaseAuthException ex = (FirebaseAuthException) task.getException();
                            if (ex!=null){
                                Log.v("error re", "registro error"+ex.getLocalizedMessage());

                                //Toast.makeText(getApplicationContext(), "Ocurrio un error al registrarse, intente nuevamente.", Toast.LENGTH_LONG).show();
                                System.out.print(""+ex.getLocalizedMessage());

                                String error = ex.getErrorCode();


                                Log.v("log re","error re"+error);
                                if(error.equals("ERROR_EMAIL_ALREADY_IN_USE")){
                                    Toast.makeText(getApplicationContext(), "Una cuenta con ese correo ya  existe.", Toast.LENGTH_LONG).show();

                                }

                                if(error.equals("ERROR_WEAK_PASSWORD")){
                                    Toast.makeText(getApplicationContext(), " Contraseña muy debil.", Toast.LENGTH_LONG).show();
                                }
                                mProgressDialog.dismiss();
                                return;
                            }


                            if(task.isSuccessful()){
                               createNewUser(task.getResult().getUser());


                            }else{
                                //display some message here
                                Toast.makeText(getApplicationContext(),"error de registro, intentelo de nuevo", Toast.LENGTH_LONG).show();
                            }


                            mProgressDialog.dismiss();
                        }
                    });


    }

    private void createNewUser(FirebaseUser userFromRegistration) {

        String versionName = BuildConfig.VERSION_NAME;
        String userId = userFromRegistration.getUid();

        CmdRegistro.registroInicialCliente(email.getText().toString(),userId, versionName, false );
        email.setText("");
        password.setText("");

        Intent i  = new Intent(getApplicationContext(), RegistroComplementario.class);
        startActivity(i);
        finish();

    }



    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Esperando...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }



    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
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

                        Intent inte  = new Intent(getBaseContext(),ClienteRegistro.class);
                        startActivity(inte);
                    }
                });

        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


    public static boolean checkValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern) && email.length() > 0){
            return true;
        }
        return false;
    }




    public void continuarCarga(){

        /*if ( model.cliente.tieneDatosBasicos() ) {
            Mascota mascota = model.getMascotaActiva();
            if (mascota == null ) {
                Intent i = new Intent(getApplicationContext(), InvitacionMascota.class);
                startActivity(i);
                finish();
            } else {
                preCargarFull();
            }
        } else {*/
            Intent i = new Intent(getApplicationContext(), RegistroComplementario.class);
            startActivity(i);
            finish();
       // }


    }



    private void preCargar(){


    }


    private void preCargarFull() {

           //Abrir Home
        Intent i = new Intent(getApplicationContext(),Home.class);
        startActivity(i);
        finish();

    }


    public void didTapVerTerminos(View view) {

        Intent i = new Intent(getApplicationContext(), TerminosYCondiciones.class);
        startActivity(i);
    }
}
