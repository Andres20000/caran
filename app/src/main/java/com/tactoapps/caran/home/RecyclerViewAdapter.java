package com.tactoapps.caran.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.tactoapps.caran.R;
import com.tactoapps.caran.comandos.CmdCalificacion;
import com.tactoapps.caran.comandos.CmdCalificacion.OnCmdCalificacionListener;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Contenido;
import com.tactoapps.caran.modelo.sistema.ImageFire.OnImageFireChange;
import com.tactoapps.caran.modelo.sistema.Utility;
import com.tactoapps.caran.views.GlideApp;

import java.util.ArrayList;



/**
 * Created by Andres on 3/27/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyView> {

    Modelo modelc = Modelo.getInstance();
    private ArrayList<Contenido> contenidos;
    private Context context;
    private String categoria;


    public class MyView extends RecyclerView.ViewHolder {

        public ImageView picture;
        public TextView name;
        public FrameLayout principal;
        public TextView nota;


        public MyView(View view) {
            super(view);

            picture = (ImageView) view.findViewById(R.id.foto);
            name = (TextView) view.findViewById(R.id.text);
            principal =  view.findViewById(R.id.principal);
            nota = view.findViewById(R.id.nota);


        }
    }


    public RecyclerViewAdapter(Context context, String categoria, ArrayList<Contenido> contenidos) {
        this.context = context;
        this.contenidos = contenidos;
        this.categoria = categoria;

    }

    public void refrescarDatos(ArrayList<Contenido> contenidos){
        this.contenidos = contenidos;
        notifyDataSetChanged();

    }



    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item__contenido_para_recycler_view_horizontal, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        final Contenido con = contenidos.get(position);


        holder.name.setText(con.titulo);
        holder.nota.setText(con.getCalificacion()+"");

        StorageReference sRef = contenidos.get(position).portada.getStorageRef();

        GlideApp.with(context)
                .load(sRef)
                .into(holder.picture);


        holder.principal.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent i = new Intent(context, DetalleContenido.class);
                i.putExtra("IDCONTENIDO",con.id);
                v.getContext().startActivity(i);
                ((Activity)v.getContext()).overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }

        });



    }

    @Override
    public int getItemCount() {
        if (contenidos == null){
            return  0;
        }

        return contenidos.size();
    }

}

