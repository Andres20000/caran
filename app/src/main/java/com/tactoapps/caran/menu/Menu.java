package com.tactoapps.caran.menu;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.tactoapps.caran.BuildConfig;
import com.tactoapps.caran.MainActivity;
import com.tactoapps.caran.R;
import com.tactoapps.caran.comandos.CmdParams;
import com.tactoapps.caran.home.VerTodo;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Cliente;
import com.tactoapps.caran.modelo.sistema.Utility;
import com.tactoapps.caran.registro.RegistroComplementario;

public class Menu extends Activity {


    LinearLayout linearEmpresasAsociadas;
    TextView version;
    Modelo modelc = Modelo.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        linearEmpresasAsociadas = findViewById(R.id.linearEmpresasAsociadas);

        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            return;
        }


        if (BuildConfig.FLAVOR.equals("caran") || BuildConfig.FLAVOR.equals("higadosano")){
            linearEmpresasAsociadas.setVisibility(View.GONE);

        }

        version = (TextView) findViewById(R.id.version);
        version.setText(Utility.getVersionParaUsuario(getBaseContext()));


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("OPT","RECARGAR");
    }


    public void didTapPerfil(View view) {
        Intent i = new Intent(getApplicationContext(), RegistroComplementario.class);
        i.putExtra("MODO", "EDITAR");
        startActivity(i);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);


    }


    public void didTapTerminos(View view) {

        Intent i = new Intent(getApplicationContext(), TerminosYCondiciones.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }


    public void hacerLogout(){
        mAuth.signOut();

        //modelc.pararListeners();

        modelc.cliente = new Cliente();
        modelc.favoritos.clear();
        modelc.recientes.clear();

        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
        finish();

    }

    public void didTapCerrarSesion(View view) {

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                Menu.this);

        // set title
        alertDialogBuilder.setTitle("Cerrar sesión");

        // set dialog message
        alertDialogBuilder
                .setMessage("¿Estás seguro?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        hacerLogout();
                    }
                });

        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    public void didTapRecientes(View view) {

        Intent i  = new Intent(getApplicationContext(), VerTodo.class);
        i.putExtra("TIPO", "RECIENTES");
        startActivity(i);
    }

    public void didTapFavoritos(View view) {
        Intent i  = new Intent(getApplicationContext(), VerTodo.class);
        i.putExtra("TIPO", "FAVORITOS");
        startActivity(i);
    }

    public void didTapEmpresasAsociadas(View view) {
        Intent i  = new Intent(getApplicationContext(), EmpresasAsociadas.class);
        startActivity(i);

    }

    public void didTapTop5(View view) {
        Intent i  = new Intent(getApplicationContext(), VerTodo.class);
        i.putExtra("TIPO", "TOP");
        startActivity(i);
    }
}
