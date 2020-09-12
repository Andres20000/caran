package com.tactoapps.caran.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.tactoapps.caran.R;
import com.tactoapps.caran.modelo.cliente.Contenido;
import com.tactoapps.caran.modelo.sistema.ImageFire.OnImageFireChange;
import com.tactoapps.caran.views.GlideApp;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by andres on 4/24/18.
 */

public class VerTodoAdapter extends BaseAdapter {


    private final Context mContext;
    private ArrayList<Contenido> contenidos;
    private final LayoutInflater layoutInflater;


    public VerTodoAdapter(Context context,  ArrayList<Contenido> contenidos) {
        this.mContext = context;
        this.contenidos = contenidos;
        layoutInflater = LayoutInflater.from(mContext);

    }

    public void setContenidos(ArrayList<Contenido> contenidos){
        this.contenidos = contenidos;

    }

    @Override
    public int getCount() {
        return contenidos.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final  int pos = i;
        final Contenido con = contenidos.get(i);

        view = layoutInflater.inflate(R.layout.item__contenido_para_recycler_view_horizontal, null);

        final ImageView foto = (ImageView)view.findViewById(R.id.foto);
        final TextView nombre = (TextView)view.findViewById(R.id.text);
        final TextView nota = (TextView)view.findViewById(R.id.nota);

        nombre.setText(con.titulo);
        nota.setText(con.getCalificacion()+"");

        StorageReference sRef = contenidos.get(i).portada.getStorageRef();

        GlideApp.with(mContext)
                .load(sRef)
                .into(foto);


        foto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, DetalleContenido.class);
                i.putExtra("IDCONTENIDO",con.id);
                mContext.startActivity(i);
                ((Activity)mContext).overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        width = (int) Math.round(width * 0.47);

        view.setLayoutParams(new GridView.LayoutParams(width, (int) Math.round(width*0.75)));
        return view;

    }
}
