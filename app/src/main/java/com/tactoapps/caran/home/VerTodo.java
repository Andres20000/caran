package com.tactoapps.caran.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.GridView;
import android.widget.TextView;

import com.tactoapps.caran.MainActivity;
import com.tactoapps.caran.R;
import com.tactoapps.caran.modelo.Modelo;

public class VerTodo extends Activity {


    Modelo model = Modelo.getInstance();
    VerTodoAdapter todoAdapter;
    GridView gridView;
    String tipo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_todo);

        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            return;
        }



        TextView titulo = findViewById(R.id.titulo);
        gridView = findViewById(R.id.grilla);

        tipo = getIntent().getStringExtra("TIPO");

        if (tipo.equals("FAVORITOS")){
            todoAdapter = new VerTodoAdapter(this, model.soloMisFavoritos(500));
            gridView.setAdapter(todoAdapter);
            titulo.setText("Lecturas favoritas");

        }


        if (tipo.equals("RECIENTES")){
            todoAdapter = new VerTodoAdapter(this, model.soloMisRecientes(500));
            gridView.setAdapter(todoAdapter);
            titulo.setText("Lecturas recientes");
        }

        if (tipo.equals("TOP")){
            todoAdapter = new VerTodoAdapter(this, model.soloTopCinco(500));
            gridView.setAdapter(todoAdapter);
            titulo.setText("Lecturas mejor calificadas");

        }


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("OPT","RECARGAR");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ( todoAdapter != null ){
            if (tipo.equals("RECIENTES")){
                todoAdapter.setContenidos(model.soloMisRecientes(500));
            }
            else if (tipo.equals("FAVORITOS")){
                todoAdapter.setContenidos(model.soloMisFavoritos(500));
            }
            else {
                todoAdapter.setContenidos(model.soloTopCinco(500));
            }

            todoAdapter.notifyDataSetChanged();

        }



    }
}
