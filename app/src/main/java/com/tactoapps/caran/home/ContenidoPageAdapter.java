
package com.tactoapps.caran.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.tactoapps.caran.R;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Contenido;
import com.tactoapps.caran.modelo.sistema.ImageFire;
import com.tactoapps.caran.modelo.sistema.ImageFire.OnImageFireChange;
import com.tactoapps.caran.views.GlideApp;

import java.util.ArrayList;


/**
 * Created by andres on 10/24/17.
 */

public class ContenidoPageAdapter extends PagerAdapter {


    private Modelo model = Modelo.getInstance();
    private Context mContext;
    private OnImageFireChange listener;


    private ArrayList<Contenido> contenidos;

    ImageView fotoPortada;
    TextView titulo;

    public ContenidoPageAdapter(Context context, OnImageFireChange listener, ArrayList<Contenido> recientes) {
        mContext = context;
        this.listener = listener;
        this.contenidos = recientes;


    }


    @Override
    public Object instantiateItem(final ViewGroup container, int position) {



        final Contenido cont = contenidos.get(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.layout_destacado_page, container, false);
        fotoPortada =  layout.findViewById(R.id.foto);
        titulo = layout.findViewById(R.id.titulo);

        titulo.setText(cont.titulo);


        StorageReference sRef = contenidos.get(position).portada.getStorageRef();

        GlideApp.with(mContext)
                .load(sRef)
                .into(fotoPortada);


        fotoPortada.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mContext, DetalleContenido.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("IDCONTENIDO", cont.id);
                mContext.startActivity(i);

            }
        });

        container.addView(layout);

        return layout;


    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return contenidos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
