package com.tactoapps.caran.comandos;

import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.tactoapps.caran.BuildConfig;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Cliente;
import com.tactoapps.caran.modelo.cliente.Favorito;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andres on 2/24/18.
 */

public class CmdRegistro {
    

    public interface OnCmdRegistro {

        void clienteNoExiste();
        void gotCliente();

    }

    public interface OnReadEstado {

        void estaActivo();
        void estaInactivo();

    }



    public static void getUsuario(String uid, final OnCmdRegistro listener){

        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

       if (uid.equals("")){
           return;
       }


        DatabaseReference ref = database.getReference("usuarios/"+ uid);//ruta path
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                if (snap == null) {
                    listener.clienteNoExiste();
                    return;
                }

                modelo.cliente = readCliente(snap);
                listener.gotCliente();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private static Cliente readCliente(DataSnapshot snap){

        Modelo model = Modelo.getInstance();
        Cliente usu = new Cliente();
        usu.id = snap.getKey();
        if (snap.child("correo").exists()) {
            usu.correo = snap.child("correo").getValue().toString();
        }
        model.cliente = usu;


        if(snap.child("nombre").getValue() == null) {
            return usu;
        }

        usu.nombre = snap.child("nombre").getValue().toString();
        //usu.estado = snap.child("estado").getValue().toString();


        if (snap.child("sexo").exists()) {
            usu.sexo = snap.child("sexo").getValue().toString();
        }

        if (snap.child("profesion").exists()) {
            usu.profesion = snap.child("profesion").getValue().toString();
        }

        if (snap.child("escolaridad").exists()) {
            usu.escolaridad = snap.child("escolaridad").getValue().toString();
        }
        if (snap.child("institucion").exists()) {
            usu.institucion = snap.child("institucion").getValue().toString();
        }


        if (snap.child("ciudad").exists()) {
            usu.ciudad = snap.child("ciudad").getValue().toString();
        }

        if (snap.child("estado").exists()) {
            usu.estado = snap.child("estado").getValue().toString();
        }



        try {
            Date f = model.df.parse(snap.child("fechaNacimiento").getValue().toString());
            usu.fechaNacimiento = f;

        } catch (Exception e) {
            usu.fechaNacimiento = new Date();
        }

        if (snap.child("favoritos").getValue() != null) {
            DataSnapshot snapFavP = snap.child("favoritos");
            for (DataSnapshot snapFav : snapFavP.getChildren()) {
                Favorito fav = new Favorito();
                fav.idContenido = snapFav.getKey();
                fav.timestamp = (long)snapFav.getValue();
                model.addFavorito(fav, false);
            }

        }


        if (snap.child("recientes").getValue() != null) {
            for (DataSnapshot snapFav : snap.child("recientes").getChildren()) {
                Favorito fav = new Favorito();
                fav.idContenido = snapFav.getKey();
                fav.timestamp = (long)snapFav.getValue();
                model.addReciente(fav, false);
            }
        }


        return usu;

    }



    public static void registroInicialCliente(String correo, String idUsu, String versionAndroid, boolean anonimo) {

        Modelo model = Modelo.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference referencia = database.getReference();

        DatabaseReference key = referencia.push();

        Date date = new Date();

        DatabaseReference ref = database.getReference("usuarios/"+idUsu+"/");

        Map<String, Object> enviarRegistro = new HashMap<String, Object>();

        enviarRegistro.put("correo", correo);
        enviarRegistro.put("fechaUltimoLogeo", model.df.format(date));
        enviarRegistro.put("horaUltimoLogeo", model.hf.format(date));
        enviarRegistro.put("systemDevice", "ANDROID" );
        enviarRegistro.put("version", versionAndroid);
        enviarRegistro.put("fechaCreacion", model.df.format(date));
        enviarRegistro.put("timestamp", ServerValue.TIMESTAMP);

        if (BuildConfig.FLAVOR.equals("caran") || BuildConfig.FLAVOR.equals("higadosano")){
            enviarRegistro.put("mysql", false);
        }


        if (anonimo) {
            enviarRegistro.put("anonimo", true);
            enviarRegistro.put("nombre", "");
            enviarRegistro.put("apellido", "");
            enviarRegistro.put("celular", "");
            enviarRegistro.put("documento", "");
        }

        ref.setValue(enviarRegistro);


    }



    public static void actualizarDatosBasicos(){


        Modelo model = Modelo.getInstance();
        Cliente cli = model.cliente;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("usuarios/"+cli.id+"/");

        darAccesoContenidoLibre();

        Map<String, Object> mapa = new HashMap<String, Object>();

        mapa.put("correo",cli.correo);
        mapa.put("nombre", cli.nombre);
        mapa.put("profesion", cli.profesion);
        mapa.put("escolaridad", cli.escolaridad);
        mapa.put("institucion", cli.institucion);
        mapa.put("ciudad", cli.ciudad);
        mapa.put("sexo", cli.sexo);
        mapa.put("modoIngreso", model.tipoLogeo);
        mapa.put("estado", "activo");
        mapa.put("fechaNacimiento", model.df.format(cli.fechaNacimiento));

        ref.updateChildren(mapa);


    }

    public static void darAccesoContenidoLibre(){

        Modelo model = Modelo.getInstance();
        Cliente cli = model.cliente;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("empresas/IdEducarGenerico/usuarios/"+cli.id);

        ref.setValue(true);


    }




    public static  void getEstadoUsuario(final OnReadEstado listener){

        Modelo model = Modelo.getInstance();
        final Cliente cli = model.cliente;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("usuarios/"+cli.id+"/estado");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                if (snap.getValue() == null){
                    listener.estaActivo();
                    return;
                }

                String estado = snap.getValue().toString();

                if (estado.equals("inactivo")){
                    listener.estaInactivo();
                }
                else {
                    listener.estaActivo();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    public static void enviarRegistro2(final String uid){

        Modelo model = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        Date date = new Date();

        final DatabaseReference ref = database.getReference("usuarios/"+uid+"/fechaUltimoLogeo/" );//ruta path
        ref.setValue(model.df.format(date));

        final DatabaseReference ref2 = database.getReference("usuarios/"+uid+"/horaUltimoLogeo/" );//ruta path
        ref2.setValue(""+ model.hf.format(date));

        final DatabaseReference ref3 = database.getReference("usuarios/"+uid+"/systemDevice/" );//ruta path
        ref3.setValue("Android");

    }




    //Pilas pensada para solo llamar cuando se regitro por facebook
    public  static  void enviarRegistro3(final String nombre, final String uid, String correo){
        Date date = new Date();
        Modelo model = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference refs = database.getReference("usuarios/"+uid+"/nombre/" );//ruta path
        refs.setValue(""+nombre);

        if (correo == null) {
            correo = "";
        }


        if (correo.equals("")){
            correo = nombre.replaceAll("\\s+", "");
            correo = correo + "@nocompartiocorreoconFB";
        }

        final DatabaseReference ref0 = database.getReference("usuarios/"+uid+"/correo/" );//ruta path
        ref0.setValue(correo);


        final DatabaseReference ref = database.getReference("usuarios/"+uid+"/fechaUltimoLogeo/" );//ruta path
        ref.setValue(""+ model.df.format(date));

        final DatabaseReference ref2 = database.getReference("usuarios/"+uid+"/horaUltimoLogeo/" );//ruta path
        ref2.setValue(""+ model.hf.format(date));

        final DatabaseReference ref3 = database.getReference("usuarios/"+uid+"/systemDevice/" );//ruta path
        ref3.setValue("Android");

        final DatabaseReference ref4 = database.getReference("usuarios/"+uid+"/logeo/" );//ruta path
        ref4.setValue(model.tipoLogeo);



    }

    public static void getProfesiones() {

        final Modelo model = Modelo.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("listados/profesiones/");//ruta path

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                model.profesiones.clear();
                for (DataSnapshot raza : snap.getChildren()){
                    model.profesiones.add(raza.getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

/*
    public static void getIntituciones() {

        final Modelo model = Modelo.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("listados/instituciones/");//ruta path
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                model.instituciones.clear();
                for (DataSnapshot raza : snap.getChildren()){
                    model.instituciones.add(raza.getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
*/

    public static void getCiudades() {

        final Modelo model = Modelo.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("listados/ciudades/");//ruta path
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                model.ciudades.clear();
                for (DataSnapshot raza : snap.getChildren()){
                    model.ciudades.add(raza.getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void getEscolaridades() {

        final Modelo model = Modelo.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("listados/escolaridad/");//ruta path
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                model.escolaridades.clear();
                for (DataSnapshot raza : snap.getChildren()){
                    model.escolaridades.add(raza.getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}
