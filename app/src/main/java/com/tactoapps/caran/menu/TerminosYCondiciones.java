package com.tactoapps.caran.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.tactoapps.caran.MainActivity;
import com.tactoapps.caran.R;
import com.tactoapps.caran.modelo.Modelo;


public class TerminosYCondiciones extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos);


        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            return;
        }


        String htmlText = "<html><body style=\"text-align:justify\"> %s </body></Html>";
        WebView webView = (WebView) findViewById(R.id.web);
        Modelo modelc = Modelo.getInstance();
        webView.loadDataWithBaseURL(null, modelc.params.politicaUso, "text/html", "utf-8", null);
        
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("OPT","RECARGAR");
    }

}
