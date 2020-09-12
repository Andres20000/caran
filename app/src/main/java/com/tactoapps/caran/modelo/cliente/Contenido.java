package com.tactoapps.caran.modelo.cliente;

import android.support.annotation.NonNull;

import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.sistema.ImageFire;

import java.util.ArrayList;

/**
 * Created by andres on 4/13/18.
 */

public class Contenido implements  Comparable<Contenido>{


    public String contenido = "";
    public String estado = "";
    public String fechaCreacion = "";
    public String fechaModificacion = "";
    public String idEditor = "";
    public String idEmpresa = "";
    public String resena = "";
    public long timestampCreacion = -1;
    public long timestampModificacion = -1;
    public String titulo = "";
    public String id = "";
    public ImageFire portada;
    public String autor = "";
    public ArrayList<Calificacion> calificaciones = new ArrayList<>();
    Modelo modelo = Modelo.getInstance();
    public  String downUrl = "";
    public String audio = "";
    public String video = "";


    public Contenido(){

    }


    public void addCalificacion(Calificacion cali) {

        int index = getIndexDeCalificacion(cali);
        if (index  >= 0 ) {
            calificaciones.set(index,cali);
            return;
        }

        calificaciones.add(cali);

    }



    private int getIndexDeCalificacion(Calificacion cali) {
        int index = 0;
        for (Calificacion cas: calificaciones) {
            if (cas.id.equals(cali.id) ){
                return index ;
            }
            index++;
        }
        return  -1;
    }


    public int getCalificacion(){

        if (calificaciones == null) {

            return  0;
        }

        if (calificaciones.size() == 0 ) {
            return  0;
        }

        int acum = 0;

        for (Calificacion cali:calificaciones){
            acum += cali.calificacion;
        }

        return acum/calificaciones.size();

    }

    public Calificacion getMiCalificacion(){
        for (Calificacion cali:calificaciones){
            if (cali.idUsuario.equals(modelo.cliente.id)){
                return cali;
            }
        }
        return  null;
    }




    public void set() {
        portada = new ImageFire();
        portada.nombre = "Portada";
        portada.extension = ".png";
        portada.folderStorage = "portadas/" +  id;
    }



    @Override
    public int compareTo(@NonNull Contenido con) {
        return (this.timestampCreacion > con.timestampCreacion ? -1 :
                (this.timestampCreacion == con.timestampCreacion ? 0 : 1));
    }
}
