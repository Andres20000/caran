package com.tactoapps.caran.modelo;


import android.content.Intent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tactoapps.caran.BuildConfig;
import com.tactoapps.caran.comandos.CmdCliente;
import com.tactoapps.caran.comandos.CmdContenidos;
import com.tactoapps.caran.comandos.CmdRegistro;
import com.tactoapps.caran.comandos.CmdRegistro.OnCmdRegistro;
import com.tactoapps.caran.modelo.cliente.Calificacion;
import com.tactoapps.caran.modelo.cliente.Cliente;
import com.tactoapps.caran.modelo.cliente.Contenido;
import com.tactoapps.caran.modelo.cliente.Empresa;
import com.tactoapps.caran.modelo.cliente.Favorito;
import com.tactoapps.caran.modelo.cliente.Params;
import com.tactoapps.caran.modelo.sistema.Sistema;
import com.tactoapps.caran.registro.RegistroComplementario;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by andres on 2/24/18.
 */

public class Modelo {

    private static final Modelo ourInstance = new Modelo();

    public static Modelo getInstance() {
        return ourInstance;
    }


    //Los de siempre
    public SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    public DateFormat hf = new SimpleDateFormat("hh:mm a");
    public SimpleDateFormat dfull = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

    public Params params = new Params();
    public String versionName = "";
    public Sistema sistema = new Sistema();
    public String tipoLogeo = "";
    public ArrayList<Contenido> contenidos = new ArrayList<>();

    public ArrayList<Contenido> nuevos = new ArrayList<>();
    public ArrayList<Favorito> favoritos = new ArrayList<>();
    public ArrayList<Favorito> recientes = new ArrayList<>();


    public ArrayList<String> profesiones = new ArrayList<>();
    public ArrayList<String> ciudades = new ArrayList<>();
    public ArrayList<String> instituciones = new ArrayList<>();
    public ArrayList<String> escolaridades = new ArrayList<>();

    public List<String> data= new ArrayList<String>();
    public List<String> afiliaciones = new ArrayList<>();
    public ArrayList<Empresa> empresas = new ArrayList<>();



    public Cliente cliente = new Cliente();



    public interface OnModelRecargarListener {

        void terminoPrecarga();

    }



    private Modelo() {

    }



    public void addFavorito(Favorito fav, boolean subir) {

        int index = getIndexDeFavorito(fav.idContenido);
        if (index  >= 0 ) {
            favoritos.set(index,fav);
            if (subir) {
                CmdCliente.addFavorito(fav.idContenido);
            }
            return;
        }

        favoritos.add(fav);

        if (subir) {
            CmdCliente.addFavorito(fav.idContenido);
        }

    }


    public void addReciente(Favorito fav, boolean subir) {

        int index = getIndexDeReciente(fav.idContenido);
        if (index  >= 0 ) {
            recientes.set(index,fav);
            if (subir) {
                CmdCliente.addReciente(fav.idContenido);
            }
            return;
        }

        recientes.add(fav);
        if (subir){
            CmdCliente.addReciente(fav.idContenido);
        }


    }

    public void removeFavorito(String idContenido) {

        int index = getIndexDeFavorito(idContenido);
        if (index  >= 0 ) {
            favoritos.remove(index);
            CmdCliente.removeFavorito(idContenido);
            return;
        }

    }



    private int getIndexDeFavorito(String idContenido) {
        int index = 0;
        for (Favorito fav: favoritos) {
            if (fav.idContenido.equals(idContenido) ){
                return index ;
            }
            index++;
        }
        return  -1;
    }



   private int getIndexDeReciente(String idContenido) {
        int index = 0;
        for (Favorito fav: recientes) {
            if (fav.idContenido.equals(idContenido) ){
                return index ;
            }
            index++;
        }
        return  -1;
    }



    public void addAfiliado(String idEmpresa) {

        int index = getIndexDeAfiliado(idEmpresa);
        if (index  >= 0 ) {
            afiliaciones.set(index,idEmpresa);
            return;
        }

        afiliaciones.add(idEmpresa);

    }



    public void addEmpresa(Empresa empresa) {


        if (empresa.id.equals("IdEducarGenerico")){
            return;
        }
        if (empresa.estado == false) {
            return;
        }

        int index = getIndexDeEmpresa(empresa);
        if (index  >= 0 ) {
            empresas.set(index,empresa);
            return;
        }

        empresas.add(empresa);

    }



    public boolean puedoVerContenido(Contenido contenido){


        if (BuildConfig.FLAVOR.equals("caran") || BuildConfig.FLAVOR.equals("higadosano")){
            return  true;
        }

        for (String afiliacion: afiliaciones){
            if (afiliacion.equals(contenido.idEmpresa)){
                return true;
            }
        }

        return false;
    }


    public boolean addContenido(Contenido contenido) {

        if (!puedoVerContenido(contenido)){
            return false;
        }

        int index = getIndexDeContenido(contenido);
        if (index  >= 0 ) {
            contenidos.set(index,contenido);
            return false;
        }

        contenidos.add(contenido);
        Collections.sort(contenidos);
         return true;

    }

    private int getIndexDeEmpresa(Empresa empresa) {
        int index = 0;
        for (Empresa emp: empresas) {
            if (emp.id.equals(empresa.id) ){
                return index ;
            }
            index++;
        }
        return  -1;
    }


    private int getIndexDeAfiliado(String idAfiliado) {
        int index = 0;
        for (String afi: afiliaciones) {
            if (afi.equals(idAfiliado) ){
                return index ;
            }
            index++;
        }
        return  -1;
    }


    private int getIndexDeContenido(Contenido contenido) {
        int index = 0;
        for (Contenido cas: contenidos) {
            if (cas.id.equals(contenido.id) ){
                return index ;
            }
            index++;
        }
        return  -1;
    }


    public void addContenidoReciente(Contenido contenido) {

        if (!puedoVerContenido(contenido)){
            return;
        }

        int index = getIndexDeContenidoReciente(contenido);
        if (index  >= 0 ) {
            nuevos.set(index,contenido);
            Collections.sort(nuevos);

            return;
        }

        nuevos.add(0,contenido);
        Collections.sort(nuevos);

        if (nuevos.size() > 5 ){
            nuevos.remove(5);
        }

    }



    private int getIndexDeContenidoReciente(Contenido Contenido) {
        int index = 0;
        for (Contenido cas: nuevos) {
            if (cas.id.equals(Contenido.id) ){
                return index ;
            }
            index++;
        }
        return  -1;
    }



    public  Contenido getContenidoById(String idContenido) {
        for (Contenido Contenido:contenidos) {
            if (Contenido.id.equals(idContenido) ){
                return Contenido;
            }
        }

        return  null;
    }


    public  Contenido getContenidoByName(String nombre) {
        for (Contenido Contenido:contenidos) {
            if (Contenido.titulo.equals(nombre) ){
                return Contenido;
            }
        }

        return  null;
    }


    public boolean esFavorito(String idContenido){
        for (Favorito item: favoritos) {
            if (item.idContenido.equals(idContenido)){
                return true;
            }
        }
        return false;
    }



    public ArrayList<Contenido> soloMisFavoritos(int cuantos){
        Collections.sort(favoritos);
        ArrayList<Contenido> misFavoritos = new ArrayList<>();
        for (Favorito item: favoritos) {
           Contenido con = getContenidoById(item.idContenido);
            if (con == null){
                continue;
            }
           misFavoritos.add(0 ,  con);
        }

        return new ArrayList<>(misFavoritos.subList(0, Math.min(cuantos,misFavoritos.size())));
    }



    public ArrayList<Contenido> soloMisRecientes(int cuantos){
        Collections.sort(recientes);
        ArrayList<Contenido> misRecientes = new ArrayList<>();
        for (Favorito item: recientes) {
            Contenido con = getContenidoById(item.idContenido);
            if (con == null){
                continue;
            }
            misRecientes.add(0 , con);
        }


        return new ArrayList<>(misRecientes.subList(0, Math.min(cuantos,misRecientes.size())));
    }

    public ArrayList<Contenido> soloTopCinco(int cuantos){
        Collections.reverse(contenidos);
        Collections.sort(contenidos, new Comparator<Contenido>() {
            @Override
            public int compare(Contenido c1, Contenido c2) {
                return (c1.getCalificacion() > c2.getCalificacion() ? -1 :
                        (c1.getCalificacion() == c2.getCalificacion() ? 1 : 0));
            }
        });



        return new ArrayList<>(contenidos.subList(0, Math.min(cuantos,contenidos.size())));
    }



    public ArrayList<Contenido> soloMasNuevos(int cuantos){

        return new ArrayList<>(contenidos.subList(0, Math.min(cuantos,contenidos.size())));
    }




    public String[] getProfesiones() {


        String[] array = new String[profesiones.size()];

        for (int i = 0; i < profesiones.size(); i++) {
            array[i] = profesiones.get(i);
        }
        return array;

    }

    public String[] getCiudades() {


        String[] array = new String[ciudades.size()];

        for (int i = 0; i < ciudades.size(); i++) {
            array[i] = ciudades.get(i);
        }
        return array;

    }

    public String[] getEscolaridades() {


        String[] array = new String[escolaridades.size()];

        for (int i = 0; i < escolaridades.size(); i++) {
            array[i] = escolaridades.get(i);
        }
        return array;

    }

    public String[] getInstitutos() {


        String[] array = new String[instituciones.size()];

        for (int i = 0; i < instituciones.size(); i++) {
            array[i] = instituciones.get(i);
        }
        return array;

    }


    public boolean reiniciarCarga(final String idContenido, final OnModelRecargarListener listener){

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        CmdRegistro.getProfesiones();
        //CmdRegistro.getIntituciones();
        CmdRegistro.getEscolaridades();
        CmdRegistro.getCiudades();

        CmdRegistro.getUsuario(user.getUid(), new OnCmdRegistro() {
            @Override
            public void clienteNoExiste() {

            }

            @Override
            public void gotCliente() {
                ingresarComoCliente(user, idContenido,listener);

            }
        });

        return true;


    }


    public void ingresarComoCliente(final FirebaseUser user , final String idContenido, final OnModelRecargarListener listener) {

        cliente.id = user.getUid();


        String provider = user.getProviderId();
        if (provider.equals("firebase")) {
            tipoLogeo = "normal";
        } else {
            tipoLogeo = "faceb";
        }

        CmdRegistro.getUsuario(cliente.id, new OnCmdRegistro() {
            @Override
            public void clienteNoExiste() {

            }

            @Override
            public void gotCliente() {
                CmdContenidos.getContenido(idContenido,listener);

            }
        });




    }


}



