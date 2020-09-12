package com.tactoapps.caran.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tactoapps.caran.R;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Empresa;
import com.tactoapps.caran.modelo.sistema.Utility;

import java.util.ArrayList;

/**
 * Created by andres on 3/7/18.
 */

public class EmpresasAsociadasAdapter extends BaseAdapter {


    private Context mContext;
    private LayoutInflater mInflater;
    Modelo model = Modelo.getInstance();
    ArrayList<Empresa> empresas;



    public EmpresasAsociadasAdapter(Context context){

        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        empresas =  model.empresas;

    }


    @Override
    public int getCount() {

        return empresas.size();
    }

    @Override
    public Object getItem(int i) {

        return empresas.get(i);

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        View rowView = mInflater.inflate(R.layout.item_lista_empresas, parent, false);
        TextView nombre =  (TextView) rowView.findViewById(R.id.nombre);



        Empresa empresa = empresas.get(i);
        nombre.setText(empresa.nombre);

        return rowView;
    }
}
