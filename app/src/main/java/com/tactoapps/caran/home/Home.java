package com.tactoapps.caran.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.tactoapps.caran.BuildConfig;
import com.tactoapps.caran.MainActivity;
import com.tactoapps.caran.R;
import com.tactoapps.caran.comandos.CmdCalificacion;
import com.tactoapps.caran.comandos.CmdCalificacion.OnCmdCalificacionListener;
import com.tactoapps.caran.comandos.CmdContenidos;
import com.tactoapps.caran.comandos.CmdContenidos.OnGetAfiliaciones;
import com.tactoapps.caran.comandos.CmdContenidos.OnGetContenidos;
import com.tactoapps.caran.comandos.CmdParams;
import com.tactoapps.caran.comandos.CmdRegistro;
import com.tactoapps.caran.comandos.CmdRegistro.OnReadEstado;
import com.tactoapps.caran.home.media.MusicService;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Cliente;
import com.tactoapps.caran.modelo.sistema.ImageFire.OnImageFireChange;

public class Home extends Activity  {

    private Modelo model = Modelo.getInstance();

    private ContenidoPageAdapter pageAdapter;
    ViewPager pager;
    RecyclerViewAdapter adapterHorizontalReci;
    RecyclerViewAdapter adapterHorizontalFav;
    RecyclerViewAdapter adapterHorizontalTop;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView verLecturas, verFavoritos, verTop, vacioLecturas, vacioFavoritos, vacioTop;
    private LinearLayout barraTop;
    LinearLayoutManager horiLayoutManagerReci;
    LinearLayoutManager horiLayoutManagerFav;
    LinearLayoutManager horiLayoutManagerTop;

    RecyclerView recReci;
    RecyclerView recFav;
    RecyclerView recTop;

    ImageView logoBarra;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pager = (ViewPager) findViewById(R.id.pager);
        barraTop = findViewById(R.id.barraTop);

        logoBarra = findViewById(R.id.logoBarra);

        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            return;
        }



        CmdParams.getParams();

        if (BuildConfig.FLAVOR.equals("caran") || BuildConfig.FLAVOR.equals("higadosano") ){
            bajarContenidos();
            logoBarra.setVisibility(View.GONE);

        }else {
            CmdContenidos.getMisSuscripciones(new OnGetAfiliaciones() {
                @Override
                public void downAfiliaciones() {
                    bajarContenidos();
                }

                @Override
                public void downNewAfiliaciones() {
                    bajarContenidos();
                }
            });

        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("OPT","RECARGAR");
    }



   protected void bajarContenidos(){



       CmdContenidos.getContenidos(new OnGetContenidos() {
           @Override
           public void downContenido() {


               pageAdapter = new ContenidoPageAdapter(getBaseContext(), new OnImageFireChange() {
                   @Override
                   public void cargoImagen(String tipo) {
                       pageAdapter.notifyDataSetChanged();
                   }
               }, model.soloMasNuevos(5));

               pager.setAdapter(pageAdapter);
               TabLayout tabpuntos = (TabLayout) findViewById(R.id.tab_puntos);
               tabpuntos.setupWithViewPager(pager, true);



               Log.i("CONTENIDO", "Nuevo contenido") ;
               //Recientes
               recReci = findViewById(R.id.recientes);
               adapterHorizontalReci = new RecyclerViewAdapter(getBaseContext(), "REC", model.soloMisRecientes(5));
               horiLayoutManagerReci = new LinearLayoutManager(Home.this, LinearLayoutManager.HORIZONTAL,false);

               recReci.setHasFixedSize(true);
               recReci.setLayoutManager(horiLayoutManagerReci);
               recReci.setAdapter(adapterHorizontalReci);


               //Favoritos
               recFav = findViewById(R.id.favoritos);
               adapterHorizontalFav = new RecyclerViewAdapter(getBaseContext(), "FAV", model.soloMisFavoritos(5));
               horiLayoutManagerFav = new LinearLayoutManager(Home.this, LinearLayoutManager.HORIZONTAL,false);

               recFav.setHasFixedSize(true);
               recFav.setLayoutManager(horiLayoutManagerFav);
               recFav.setAdapter(adapterHorizontalFav);




               //Top
               recTop = findViewById(R.id.top);
               adapterHorizontalTop = new RecyclerViewAdapter(getBaseContext(), "TOP", model.soloTopCinco(5));
               horiLayoutManagerTop = new LinearLayoutManager(Home.this, LinearLayoutManager.HORIZONTAL,false);

               recTop.setHasFixedSize(true);
               recTop.setLayoutManager(horiLayoutManagerTop);
               recTop.setAdapter(adapterHorizontalTop);


               //Veres todos
               verFavoritos = findViewById(R.id.verFavoritos);
               verLecturas = findViewById(R.id.verLecturas);
               verTop = findViewById(R.id.verTop);

               vacioFavoritos = findViewById(R.id.vaciofavoritos);
               vacioLecturas = findViewById(R.id.vaciorecientes);
               vacioTop = findViewById(R.id.vaciotop);


               if (BuildConfig.FLAVOR.equals("caran") || BuildConfig.FLAVOR.equals("higadosano")){
                   recTop.setVisibility(View.GONE);
                   verTop.setVisibility(View.GONE);
                   vacioTop.setVisibility(View.GONE);
                   barraTop.setVisibility(View.GONE);
               }


               pintarQuitarVerTodos();



           }
       });

       CmdCalificacion.getInstance().registrarListener(new OnCmdCalificacionListener() {
           @Override
           public void cargoCalificacion() {

               if (adapterHorizontalFav != null){
                   adapterHorizontalFav.refrescarDatos(model.soloMisFavoritos(5));
                   adapterHorizontalFav.notifyDataSetChanged();
               }

               if (adapterHorizontalReci != null){
                   adapterHorizontalReci.refrescarDatos(model.soloMisRecientes(5));
                   adapterHorizontalReci.notifyDataSetChanged();
               }

               if (adapterHorizontalTop != null){
                   adapterHorizontalTop.refrescarDatos(model.soloTopCinco(5));
                   adapterHorizontalTop.notifyDataSetChanged();
               }


           }
       });




   }


    @Override
    protected void onStart() {
        super.onStart();

        if (adapterHorizontalReci != null){
            adapterHorizontalReci.refrescarDatos(model.soloMisRecientes(5));
        }

        if (adapterHorizontalFav != null){
            adapterHorizontalFav.refrescarDatos(model.soloMisFavoritos(5));
        }

        if (adapterHorizontalTop != null){
            adapterHorizontalTop.refrescarDatos(model.soloTopCinco(5));
        }

        CmdRegistro.getEstadoUsuario(new OnReadEstado() {
            @Override
            public void estaActivo() {

            }

            @Override
            public void estaInactivo() {
                mostrarDialogoUsuarioInactivo();
            }
        });

        pintarQuitarVerTodos();
        if (pageAdapter != null) {
            pageAdapter.notifyDataSetChanged();
        }


    }

    private void pintarQuitarVerTodos(){

        if (verFavoritos != null ){
            if (model.soloMisFavoritos(100).size() <= 5) {
                verFavoritos.setVisibility(View.GONE);
            }
            else {

                verFavoritos.setVisibility(View.VISIBLE);
            }

            if (model.soloMisFavoritos(100).size() == 0 ){
                vacioFavoritos.setVisibility(View.VISIBLE);
                recFav.setVisibility(View.GONE);

            }else {
                vacioFavoritos.setVisibility(View.GONE);
                recFav.setVisibility(View.VISIBLE);
            }

        }

        if (verLecturas != null ){
            if (model.soloMisRecientes(100).size() <= 5) {
                verLecturas.setVisibility(View.GONE);
            }
            else {
                verLecturas.setVisibility(View.VISIBLE);
            }

            if (model.soloMisRecientes(100).size() == 0 ){
                vacioLecturas.setVisibility(View.VISIBLE);
                recReci.setVisibility(View.GONE);
            }else {
                vacioLecturas.setVisibility(View.GONE);
                recReci.setVisibility(View.VISIBLE);
            }

        }

        if (verTop != null ){
            if (model.soloTopCinco(100).size() <= 5) {
                verTop.setVisibility(View.GONE);
            }
            else {

                verTop.setVisibility(View.VISIBLE);
            }

            if (model.soloTopCinco(100).size() == 0 ){
                vacioTop.setVisibility(View.VISIBLE);
                recTop.setVisibility(View.GONE);

            }else {
                vacioTop.setVisibility(View.GONE);
                recTop.setVisibility(View.VISIBLE);
            }

        }


        if (BuildConfig.FLAVOR.equals("caran") || BuildConfig.FLAVOR.equals("higadosano")){
            if (verTop != null ) {
                verTop.setVisibility(View.GONE);
                recTop.setVisibility(View.GONE);
                vacioTop.setVisibility(View.GONE);
                barraTop.setVisibility(View.GONE);
            }

        }


    }


    public void didTapMenu(View view) {
        Intent i  = new Intent(getApplicationContext(), com.tactoapps.caran.menu.Menu.class);
        startActivity(i);

    }

    public void didTapVerTodoRecientes(View view) {

        Intent i  = new Intent(getApplicationContext(), VerTodo.class);
        i.putExtra("TIPO", "RECIENTES");
        startActivity(i);
    }

    public void didTapVerTodoFavoritos(View view) {
        Intent i  = new Intent(getApplicationContext(), VerTodo.class);
        i.putExtra("TIPO", "FAVORITOS");
        startActivity(i);
    }

    public void didTapVerTodoTop(View view) {
        Intent i  = new Intent(getApplicationContext(), VerTodo.class);
        i.putExtra("TIPO", "TOP");
        startActivity(i);
    }

    public void didTapVerTodo(View view) {
        Intent i  = new Intent(getApplicationContext(), BuscarContenido.class);
        startActivity(i);

    }


    public void mostrarDialogoUsuarioInactivo() {

        AlertDialog alertDialog = new AlertDialog.Builder(Home.this).create();
        alertDialog.setTitle("Usuario Inactivo");
        alertDialog.setMessage("Lo sentimos, tu usuario ha sido inactivado, tu sesión va a terminar.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mAuth.signOut();
                        model.cliente = new Cliente();
                        model.favoritos.clear();
                        model.recientes.clear();

                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                        finish();
                        dialog.dismiss();

                    }
                });
        alertDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, MusicService.class));
    }
}
