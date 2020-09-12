package com.tactoapps.caran.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;

import com.google.firebase.storage.StorageReference;
import com.tactoapps.caran.BuildConfig;
import com.tactoapps.caran.R;
import com.tactoapps.caran.comandos.CmdCalificacion;
import com.tactoapps.caran.comandos.CmdCalificacion.OnCmdCalificacionListener;
import com.tactoapps.caran.home.media.MusicService;
import com.tactoapps.caran.home.media.MusicService.LocalBinder;
import com.tactoapps.caran.home.media.OnMediaChanges;
import com.tactoapps.caran.home.media.VideoScreen;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.Modelo.OnModelRecargarListener;
import com.tactoapps.caran.modelo.cliente.Contenido;
import com.tactoapps.caran.modelo.cliente.Favorito;
import com.tactoapps.caran.views.GlideApp;

import java.util.Date;

public class DetalleContenido extends Activity {

    WebView web;
    Contenido con;
    String idContenido = "";
    ImageView foto,favorito;
    ImageView btnPLay;
    TextView titulo, calificacion, resena, autor;
    TextView inicio, fin;
    LinearLayout media, consola;
    FrameLayout video, audio;
    ImageView logoBarra;


    MusicService music;
    boolean mBound = false;

    SeekBar barra;


    private Modelo model = Modelo.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_contenido);


        foto = findViewById(R.id.foto);
        favorito = findViewById(R.id.favorito);
        web = findViewById(R.id.web);
        titulo = findViewById(R.id.titulo);
        calificacion = findViewById(R.id.calificacion);
        resena = findViewById(R.id.resena);
        autor = findViewById(R.id.autor);
        media =  findViewById(R.id.media);
        consola =  findViewById(R.id.consola);
        inicio = findViewById(R.id.inicio);
        fin = findViewById(R.id.fin);
        barra = findViewById(R.id.barra);
        btnPLay = findViewById(R.id.btnPlay);
        video = findViewById(R.id.video);
        audio = findViewById(R.id.audio);
        logoBarra = findViewById(R.id.logoBarra);



        if (savedInstanceState != null  &&  model.contenidos.size() == 0) {

            titulo.setText("");
            resena.setText("");
            idContenido = savedInstanceState.getString("IDCONTENIDO");

            model.reiniciarCarga(idContenido, new OnModelRecargarListener() {
                @Override
                public void terminoPrecarga() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            con =  model.getContenidoById(idContenido);
                            pintarVista();

                        }
                    });

                }
            });
            return;

        }

        idContenido = getIntent().getStringExtra("IDCONTENIDO");
        con =  model.getContenidoById(idContenido);
        pintarVista();

    }


    public void onSaveInstanceState(Bundle outState) {
        outState.putString("IDCONTENIDO", idContenido);
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }



    private void pintarVista(){

        titulo.setText(con.titulo);
        resena.setText(con.resena);
        autor.setText(con.autor);

        autor.setVisibility(View.GONE);

        if (BuildConfig.FLAVOR.equals("caran") || BuildConfig.FLAVOR.equals("higadosano")){
            media.setVisibility(View.GONE);
            consola.setVisibility(View.GONE);
            logoBarra.setVisibility(View.GONE);
        }

        if (con.video.equals("")){
            video.setVisibility(View.GONE);
        }

        if (con.audio.equals("")){
            audio.setVisibility(View.GONE);
        }


        CmdCalificacion.getInstance().registrarListener(new OnCmdCalificacionListener() {
            @Override
            public void cargoCalificacion() {
                calificacion.setText(con.getCalificacion() + "");

            }
        });



        StorageReference sRef = con.portada.getStorageRef();

        GlideApp.with(this)
                .load(sRef)
                .into(foto);


        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setSupportZoom(false);
        web.getSettings().setAllowFileAccess( true );
        web.getSettings().setJavaScriptEnabled( true );
        web.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default

        web.loadData(repararContenido(con.contenido), "text/html; charset=utf-8", "UTF-8");

        pintarFavorito();

        AgregarComoReciente();
        AgregarComoReciente();


        barra.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int porseek, boolean user) {
                if (!user){
                    return;
                }

                long millis = music.player.getDuration();
                float procentaje = (float)porseek / 100 ;
                float avance = (float)millis * procentaje;
                music.player.seekTo((long)avance);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Intent i= new Intent(this, MusicService.class);
        startService(i);


    }



    public String repararContenido(String con){

        String newCon = "<!DOCTYPE html> " +
                "<html> " +
                "<head> " +
                "     <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0;  user-scalable=0;\"/> " +
                "     <style> img {    object-fit: scale-down;\n" +

                "   max-width: 100%;\n" +
                "   max-height: 100%;\n" +
                "   width: auto;\n" +
                "   height: auto; } </style> " +
                "</head> " +
                "<body>";

        return  newCon + con + "</body> </html>";


    }

    public void didTapCalificar(View view) {

        Intent i  = new Intent(getApplicationContext(), ListaResenas.class);
        i.putExtra("IDCONTENIDO", con.id);
        startActivity(i);
    }

    private void pintarFavorito(){
        if (model.esFavorito(con.id)) {
            favorito.setImageResource(R.drawable.btn_favorito_ok);
        } else {
            favorito.setImageResource(R.drawable.btn_favorito_no_ok);
        }

    }

    public void didTapFavorito(View v){

        if (model.esFavorito(con.id)) {
            model.removeFavorito(con.id);
        } else {
            Favorito fav = new Favorito();
            fav.idContenido = con.id;
            fav.timestamp = new Date().getTime();
            model.addFavorito(fav,true);
        }

        pintarFavorito();

    }

    public void AgregarComoReciente(){
        Favorito reciente = new Favorito();
        reciente.idContenido = con.id;
        reciente.timestamp = new Date().getTime();
        model.addReciente(reciente, true);
    }

    public void didTapCompartir(View view) {


        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mira esto");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Este artículo sobre " + con.titulo + " está interesante. Encuentralo en la plataforma de eDucar.  http://www.eDucar.com"  );


        startActivity(emailIntent);
    }

    public void didTapVideo(View view) {
        Intent i  = new Intent(getApplicationContext(), VideoScreen.class);
        i.putExtra("IDCONTENIDO", con.id);
        startActivity(i);


    }




    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        if (calificacion != null && con != null){
            calificacion.setText(con.getCalificacion() + "");
        }

        if (music == null || music.player == null){
            return;
        }

        if (music.player.getPlayWhenReady()){
            btnPLay.setImageResource(R.drawable.btn_audio_pausa);
        }else{
            btnPLay.setImageResource(R.drawable.btn_audio_play);
        }


    }



    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
        }

        mBound = false;

    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            music = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };




    public void didTapIniciar(View view) {

        consola.setVisibility(View.VISIBLE);
        media.setVisibility(View.GONE);
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.

            music.play(con.audio, new OnMediaChanges() {
                @Override
                public void onLineaTiempoCambio(Timeline timeline) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            fin.setText("0:00");
                            inicio.setText("0:00");
                            music.player.setPlayWhenReady(true);
                            timerHandler.postDelayed(timerRunnable, 0);

                        }
                    });

                }
            });
        }
    }




    public void didTapPlayPause(View view) {
        if (music.player.getPlayWhenReady()){
            btnPLay.setImageResource(R.drawable.btn_audio_play);
        }else{
            btnPLay.setImageResource(R.drawable.btn_audio_pausa);
        }

        music.playPausa();

    }

    public void didTapAtras(View view) {
        music.atrazar();
    }


    public void didTapAdelantar(View view) {
        music.adelantar();
    }



    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (!mBound){
                timerHandler.postDelayed(this, 200);
                return;
            }

            long millis = music.player.getContentPosition();

            int  estado = music.player.getPlaybackState();

            timerHandler.postDelayed(this, 200);

            if (estado == Player.STATE_BUFFERING){
                return;
            }


            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            if (minutes < 0) {
                minutes = 0;
            }

            if (seconds < 0){
                seconds = 0;
            }

            inicio.setText(String.format("%d:%02d", minutes, seconds));


            /////

            millis = music.player.getDuration() - music.player.getContentPosition();
            seconds = (int) (millis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;

            if (minutes < 0) {
                minutes = 0;
            }

            if (seconds < 0){
                seconds = 0;
            }

            fin.setText(String.format("%d:%02d", minutes, seconds));



            music.player.getBufferedPercentage();


            float mult = 0 ;
            if (music.player.getDuration() > 0) {
                long duracion =  music.player.getDuration();
                long current = music.player.getContentPosition();
                mult = (float)current / (float)duracion ;
                mult = mult * 100;
            }



            barra.setProgress((int)mult) ;


        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MusicService.class));
    }

    public void didCerrarPlayer(View view) {
        //unbindService(mConnection);
        //music.stopSelf();
        //mBound = false;
        music.player.setPlayWhenReady(false);
        consola.setVisibility(View.GONE);
        media.setVisibility(View.VISIBLE);
    }
}
