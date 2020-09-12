package com.tactoapps.caran.home;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tactoapps.caran.R;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Calificacion;

import java.util.ArrayList;


/**
 * Created by andres on 10/31/17.
 */

public class ListaResenasAdapter  extends BaseAdapter {


    Modelo modelc = Modelo.getInstance();
    private Context mContext;
    private final LayoutInflater mInflater;

    ArrayList<Calificacion> calificaciones ;


    public ListaResenasAdapter(Context c, ArrayList<Calificacion> calificaciones) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
        this.calificaciones = calificaciones;
    }


    @Override
    public int getCount() {
        return calificaciones.size();
    }


    @Override
    public Object getItem(int i) {
        return calificaciones.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        ViewHolder holder;

        Calificacion cali = calificaciones.get(i);

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.item_resena,parent, false);

            holder = new ViewHolder();
            holder.hora = (TextView) convertView.findViewById(R.id.hora);
            holder.nota = (TextView) convertView.findViewById(R.id.nota);
            holder.autor = (TextView) convertView.findViewById(R.id.autor);
            holder.comentario = (TextView) convertView.findViewById(R.id.comentario);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        TextView hora = holder.hora;
        TextView mensaje = holder.comentario;
        TextView nota = holder.nota;
        TextView autor = holder.autor;

        hora.setText(cali.fecha);
        mensaje.setText(cali.comentario);
        autor.setText(cali.autor);
        nota.setText(cali.calificacion+"/5");

        return convertView;

    }



    private static class ViewHolder {

        public TextView hora;
        public TextView comentario;
        public TextView nota;
        public TextView autor;


    }

}
