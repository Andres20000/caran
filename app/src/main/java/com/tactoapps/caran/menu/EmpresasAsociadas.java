package com.tactoapps.caran.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.tactoapps.caran.MainActivity;
import com.tactoapps.caran.R;
import com.tactoapps.caran.modelo.Modelo;

public class EmpresasAsociadas extends Activity {

    private ListView lisView;
    private EmpresasAsociadasAdapter mAdapter;
    TextView textoArriba, textoAbajo;
    Modelo modelo = Modelo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresas_asociadas);


        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            return;
        }


        mAdapter = new EmpresasAsociadasAdapter(this);
        lisView = (ListView) findViewById(R.id.lista);
        lisView.setAdapter(mAdapter);
        lisView.setEmptyView(findViewById(R.id.vacio));

        textoArriba = findViewById(R.id.textoArriba);
        textoAbajo = findViewById(R.id.textoAbajo);

        if (modelo.empresas.size() == 0 ){
            textoArriba.setVisibility(View.GONE);
            textoAbajo.setVisibility(View.GONE);

        }

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("OPT","RECARGAR");
    }

}
