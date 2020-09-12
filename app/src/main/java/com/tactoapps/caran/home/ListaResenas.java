package com.tactoapps.caran.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import com.tactoapps.caran.MainActivity;
import com.tactoapps.caran.R;
import com.tactoapps.caran.comandos.CmdCalificacion;
import com.tactoapps.caran.comandos.CmdCalificacion.OnCmdCalificacionListener;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Calificacion;
import com.tactoapps.caran.modelo.cliente.Contenido;

import java.util.ArrayList;


public class ListaResenas extends Activity {




    Modelo modelc = Modelo.getInstance();
    private ListView listaView;
    ListaResenasAdapter adapter;


    ImageView star1, star2, star3,star4,star5;
    TextView tcalificacion,titulo;
    EditText comentario;
    int calificacion = 0;
    Contenido con;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_resenas);

        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            return;
        }


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        tcalificacion = (TextView) findViewById(R.id.calificacion);
        titulo = (TextView) findViewById(R.id.titulo);
        comentario = (EditText) findViewById(R.id.comentario);


        star1 = (ImageView) findViewById(R.id.estre1);
        star2 = (ImageView) findViewById(R.id.estre2);
        star3 = (ImageView) findViewById(R.id.estre3);
        star4 = (ImageView) findViewById(R.id.estre4);
        star5 = (ImageView) findViewById(R.id.estre5);




        Modelo modelc  = Modelo.getInstance();

        String idContenido = getIntent().getStringExtra("IDCONTENIDO");
        con = modelc.getContenidoById(idContenido);

        int cal = con.getCalificacion();
        tcalificacion.setText(cal + "");
        titulo.setText(con.titulo);



        pintarEstrellas(0);


        ///Listado


        ArrayList<Calificacion> calificacions = con.calificaciones;

        listaView = (ListView) findViewById(R.id.lista);
        adapter = new ListaResenasAdapter(getBaseContext(),calificacions);
        listaView.setEmptyView(findViewById(R.id.vacio));
        listaView.setAdapter(adapter);


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("OPT","RECARGAR");
    }


    private void pintarEstrellas(double calificacion){

        pintarApagarPrenderEstrella(star1,1,calificacion);
        pintarApagarPrenderEstrella(star2,2,calificacion);
        pintarApagarPrenderEstrella(star3,3,calificacion);
        pintarApagarPrenderEstrella(star4,4,calificacion);
        pintarApagarPrenderEstrella(star5,5,calificacion);
    }


    private void pintarApagarPrenderEstrella(ImageView star, double pos, double nota){

        if (nota >= pos) {
            star.setImageResource(R.drawable.punto_con_relleno_azul);
            return;
        }

        if (Math.abs(nota-pos) <= 0.5) {
            star.setImageResource(R.drawable.punto_sin_relleno_azul);
            return;
        }

        star.setImageResource(R.drawable.punto_sin_relleno_azul);


    }

    public void didTapEnviar(View view) {

        if (calificacion == 0 ){
            return;
        }
        if (con.getMiCalificacion() != null){
            CmdCalificacion.getInstance().actualizarCalificacion(con.id, calificacion, comentario.getText().toString(), con.getMiCalificacion().id);
            finish();
            return;
        }

        CmdCalificacion.getInstance().crearCalificacion(con.id, calificacion, comentario.getText().toString());
        finish();

    }


    public void didTapS1(View v){
        calificacion = 1;
        pintarEstrellas(1);
    }

    public void didTapS2(View v){
        calificacion = 2;
        pintarEstrellas(2);
    }
    public void didTapS3(View v){
        calificacion = 3;
        pintarEstrellas(3);
    }
    public void didTapS4(View v){
        calificacion = 4;
        pintarEstrellas(4);
    }
    public void didTapS5(View v){
        calificacion = 5;
        pintarEstrellas(5);
    }
}
