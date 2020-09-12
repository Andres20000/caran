package com.tactoapps.caran.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tactoapps.caran.MainActivity;
import com.tactoapps.caran.R;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Contenido;
import com.tactoapps.caran.views.IndexableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class BuscarContenido extends Activity {


    private static final List<String> nombres = new ArrayList<String>();
    private IndexableListView listView;
    private EditText search;
    private EfficientAdapter adapter;
    Modelo modelo = Modelo.getInstance();
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buscar);

        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            return;
        }



            nombres.clear();
            modelo.data.clear();


            for (int i = 0; i < modelo.contenidos.size(); i++) {

                nombres.add(modelo.contenidos.get(i).titulo );
                adicionarLetraInicial(modelo.contenidos.get(i).titulo );
            }


            /*for (char c = 'A'; c <= 'Z'; c++) {
                nombres.add(c + "");
            }*/



            Collections.sort(nombres);

            listView = (IndexableListView) findViewById(R.id.listView_main_menu);
            adapter = new EfficientAdapter(this, R.layout.buscar_main_list, nombres);
            listView.setAdapter(adapter);
            listView.setFastScrollEnabled(true);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                    String nombre = modelo.data.get(pos);

                    for (char c = 'A'; c <= 'Z'; c++) {
                        if (nombre.equals(c)) {
                            return;
                        } else {
                            continue;
                        }
                    }

                    if (nombre.equals("A") || nombre.equals("B") || nombre.equals("C") || nombre.equals("D") ||
                            nombre.equals("E") || nombre.equals("F") || nombre.equals("G") || nombre.equals("H") ||
                            nombre.equals("I") || nombre.equals("J") || nombre.equals("K") || nombre.equals("L") ||
                            nombre.equals("M") || nombre.equals("N") || nombre.equals("O") || nombre.equals("P") ||
                            nombre.equals("Q") || nombre.equals("R") || nombre.equals("S") || nombre.equals("T") ||
                            nombre.equals("U") || nombre.equals("V") || nombre.equals("W") || nombre.equals("X") ||
                            nombre.equals("Y") || nombre.equals("Z")) {

                    } else {

                        Contenido con = modelo.getContenidoByName(nombre);
                        if (con != null){
                            Intent i = new Intent(getApplicationContext(), DetalleContenido.class);
                            i.putExtra("IDCONTENIDO",con.id);
                            startActivity(i);
                            overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

                        }


                    }


                }
            });

            search = (EditText) findViewById(R.id.editText_main_search);
            search.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Toast.makeText(Medicamentos.this,"posición: "+start+"id: "+before, Toast.LENGTH_LONG).show();
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    //  Toast.makeText(Medicamentos.this,"posición: "+start+"id: "+count+"3: "+after, Toast.LENGTH_LONG).show();

                }

                @Override
                public void afterTextChanged(Editable s) {

                    adapter.getFilter().filter(s);

                }
              });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("OPT","RECARGAR");
    }

    public void atras(){
        modelo.data.clear();
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            atras();
        }
        return true;
    }




    /******************************** EfficientAdapter ************************************/

    public class EfficientAdapter extends ArrayAdapter<String> implements SectionIndexer, Filterable {

        private Filter filter;
        private String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private LayoutInflater mInflater;
        @SuppressWarnings("unused")
        private String TAG=EfficientAdapter.class.getSimpleName();
        @SuppressWarnings("unused")
        private Context context;

        Modelo modelo = Modelo.getInstance();


        public EfficientAdapter(Context context, int textViewResourceId, List<String> data) {
            super(context, textViewResourceId, data);
            mInflater = LayoutInflater.from(context);
            this.context=context;
            this.modelo.data.addAll(data);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null){
                convertView = mInflater.inflate(R.layout.buscar_main_list, parent, false);
                holder = new ViewHolder();
                holder.textLine = (TextView) convertView.findViewById(R.id.textView_main_item);
                holder.textSeperater = (TextView) convertView.findViewById(R.id.textView_main_seperater);
                convertView.setTag(holder);
            }
            holder =(ViewHolder) convertView.getTag();

            if(getItem(position).length()<=1){
                holder.textLine.setVisibility(View.GONE);
                holder.textSeperater.setVisibility(View.VISIBLE);
                holder.textSeperater.setText(getItem(position));
            }else{
                holder.textLine.setVisibility(View.VISIBLE);
                holder.textSeperater.setVisibility(View.GONE);
                String nombreM =getItem(position);
                //String[] arrayNombreMedicamentos = nombreM.split("\n");
                //String nombre= ""+arrayNombreMedicamentos[0];
                //String nombreGeneral= arrayNombreMedicamentos[1];

                holder.textLine.setText(nombreM);

            }

            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return modelo.data.size();
        }

        @Override
        public String getItem(int position) {
            // TODO Auto-generated method stub
            return modelo.data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        class ViewHolder {
            TextView textLine;
            TextView textSeperater;
        }

        @Override
        public Filter getFilter()
        {
            if(filter == null)
                filter = new MangaNameFilter();
            return filter;
        }

        /************** sectionIndexer Overriding Functions **************/

        @Override
        public int getPositionForSection(int section) {
            // If there is no item for current section, previous section will be selected
            for (int i = section; i >= 0; i--) {
                for (int j = 0; j < getCount(); j++) {
                    if (i == 0) {
                        // For numeric section
                        for (int k = 0; k <= 9; k++) {
                            if (match(String.valueOf(getItem(j).charAt(0)), String.valueOf(k)))
                                return j;
                        }
                    } else {
                        if (match(String.valueOf(getItem(j).charAt(0)), String.valueOf(mSections.charAt(i))))
                            return j;
                    }
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++)
                sections[i] = String.valueOf(mSections.charAt(i));
            return sections;
        }

        /************** MangaNameFilter Class **************/
        private class MangaNameFilter extends Filter {

            final List<String> list = new ArrayList<String>();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if(constraint != null && constraint.toString().length() > 0){

                    ArrayList<String> allItems = new ArrayList<String>();
                    synchronized (this)
                    {
                        allItems.addAll(nombres);
                    }

                    list.clear();

                    for(int i=0;i<allItems.size();i++){
                        String item = allItems.get(i).toLowerCase();
                        if (item.length() == 1) {
                            //if(item.contains(constraint+"")){
                                list.add(allItems.get(i));
                            //}
                        } else {
                            if (item.contains(constraint + "")) {
                                list.add(allItems.get(i));
                            }
                        }
                    }

                    result.count = list.size();
                    result.values = list;

                }
                else{
                    synchronized(this){
                        list.clear();
                        for(int i = 0; i< nombres.size(); i++){
                            list.add(nombres.get(i));
                        }

                        result.values = list;
                        result.count = list.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                modelo.data = (ArrayList<String>)results.values;
                notifyDataSetChanged();
            }
        }

    } //EfficientAdapter class ends

    /************** Function for sectionIndexer **************/

    public boolean match(String value, String keyword) {
        if (value == null || keyword == null)
            return false;
        if (keyword.length() > value.length())
            return false;

        int i = 0, j = 0;
        do {
            if (keyword.charAt(j) == value.charAt(i)) {
                i++;
                j++;
            } else if (j > 0)
                break;
            else
                i++;
        } while (i < value.length() && j < keyword.length());

        return (j == keyword.length())? true : false;
    }


//validacion a internet


    //validacion conexion internet
    protected Boolean estaConectado(){
        if(conectadoWifi()){
            Log.v("wifi","Tu Dispositivo tiene Conexion a Wifi.");
            return true;
        }else{
            if(conectadoRedMovil()){
                Log.v("Datos", "Tu Dispositivo tiene Conexion Movil.");
                return true;
            }else{
                showAlertSinInternet();
                // Toast.makeText(getApplicationContext(),"Sin Conexión a Internet", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }




    public void showAlertSinInternet(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Sin Internet");

        // set dialog message
        alertDialogBuilder
                .setMessage("Sin Conexión a Internet")
                .setCancelable(false)
                .setPositiveButton("Reintentar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent inte  = new Intent(getBaseContext(),MainActivity.class);
                        startActivity(inte);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    public void adicionarLetraInicial(String contenido){

        if (contenido.length() == 0 ){
            return;
        }
        String inicial = contenido.substring(0,1);
        for (String letra : nombres) {
            if (letra.equals(inicial)){
                return;
            }
        }
        nombres.add(inicial);
    }

}
