package com.tactoapps.caran;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tactoapps.caran.home.Home;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.comandos.CmdParams;
import com.tactoapps.caran.comandos.CmdRegistro;
import com.tactoapps.caran.comandos.CmdRegistro.OnCmdRegistro;
import com.tactoapps.caran.comandos.CmdSystem;
import com.tactoapps.caran.comandos.CmdSystem.OnCmdSystem;
import com.tactoapps.caran.registro.IniciarRegistro;
import com.tactoapps.caran.registro.RegistroComplementario;


public class MainActivity extends Activity {

    final Context context = this;
    TextView version;
    PackageInfo pInfo;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;


    Modelo model = Modelo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        version = (TextView)findViewById(R.id.version);
        version.setText("Versión "+pInfo.versionName + " ("+ pInfo.versionCode + ")");
        model.versionName = ""+pInfo.versionName;
        //if(estaConectado()){

            precargarDatos();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();

                    //mAuth.signOut();
                    if (user != null) {



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
                        Intent i = new Intent(getApplicationContext(), IniciarRegistro.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);
                        finish();
                    }
                }
            };

        /*}
        else{
            showAlertSinInternet();
        }*/
    }

    public void precargarDatos() {
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        catch (DatabaseException ex){

        }


        CmdRegistro.getProfesiones();
        //CmdRegistro.getIntituciones();
        CmdRegistro.getEscolaridades();
        CmdRegistro.getCiudades();
        CmdParams.getParams();


        CmdSystem.getSytem(new OnCmdSystem() {
            @Override
            public void actualizoSistema() {

            }
        });

    }


    private void preCargar() {


        Intent i = new Intent(getApplicationContext(), Home.class);
        startActivity(i);
        finish();

    }


    public void ingresarComoCliente(final FirebaseUser user ){

        model.cliente.id = user.getUid();
        String token = FirebaseInstanceId.getInstance().getToken();



        String provider =  user.getProviderId();
        if (provider.equals("firebase")) {
            model.tipoLogeo = "normal";
            //CmdRegistro.enviarRegistro2(user.getUid());
        } else {
            model.tipoLogeo = "faceb";
            //CmdRegistro.enviarRegistro3(user.getDisplayName(), user.getUid(), user.getEmail());
        }


        if(model.tipoLogeo.equals("faceb")){

            CmdRegistro.enviarRegistro3(user.getDisplayName(), user.getUid(), user.getEmail());
        }

        if(model.tipoLogeo.equals("normal")){
            CmdRegistro.enviarRegistro2(user.getUid());
        }

        if(model.tipoLogeo.equals("anonimo")){
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


    //validacion wifi
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Sin Internet");

        // set dialog message
        alertDialogBuilder
                .setMessage("Sin conexión a Internet")
                .setCancelable(false)
                .setPositiveButton("Reintentar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        Intent inte  = new Intent(getBaseContext(),MainActivity.class);
                        startActivity(inte);
                        finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
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



}

