package com.tactoapps.caran.comandos;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Calificacion;
import com.tactoapps.caran.modelo.cliente.Contenido;
import com.tactoapps.caran.modelo.sistema.Utility;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by andres on 11/14/17.
 */

public class CmdCalificacion {


    Modelo model = Modelo.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private ArrayList<OnCmdCalificacionListener> listeners = new ArrayList<>();

    private static final CmdCalificacion ourInstance = new CmdCalificacion();

    public static CmdCalificacion getInstance() {
        return ourInstance;

    }


    public interface OnCmdCalificacionListener {

        void cargoCalificacion();

    }


    public void registrarListener (OnCmdCalificacionListener listener) {
        listeners.add(listener);
    }


    public void llegoNotificacion(String idContenido, Contenido con){
        for (OnCmdCalificacionListener listener:listeners){
            if (con.id.equals(idContenido)){
                listener.cargoCalificacion();
            }
        }

    }

    private CmdCalificacion() {


    }


    public void getCalificaciones(final String idContenido){

        DatabaseReference ref = database.getReference("calificaciones");

        Query query = ref.orderByChild("idContenido").equalTo(idContenido);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snap, String s) {

                Contenido con = model.getContenidoById(idContenido);
                if (con == null){
                    return;
                }
                con.addCalificacion(readCalificacion(snap));
                llegoNotificacion(idContenido, con);
            }

            @Override
            public void onChildChanged(DataSnapshot snap, String s) {
                Contenido con = model.getContenidoById(idContenido);
                con.addCalificacion(readCalificacion(snap));
                llegoNotificacion(idContenido, con);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addChildEventListener(listener);

    }



    private Calificacion readCalificacion(DataSnapshot snap) {

        Calificacion cali = snap.getValue(Calificacion.class);
        getAutorCalificacion(cali.idUsuario, cali);
        cali.id = snap.getKey();
        return cali;
    }


    public static void  getAutorCalificacion(String idUsuario, final Calificacion cal){

        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("usuarios/" + idUsuario);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                if (snap.getValue() == null){
                    cal.autor = "Thomas McKeown";
                    return;
                }

                String autor = "";

                if (snap.hasChild("nombre")) {
                    autor = snap.child("nombre").getValue().toString();
                }

                if (snap.hasChild("nombres")) {
                    autor = snap.child("nombres").getValue().toString();
                }

                cal.autor = autor;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void crearCalificacion(String idContenido, int calificacion, String comentario){
        DatabaseReference ref = database.getReference("calificaciones");
        ref = ref.child(ref.push().getKey());

        Map<String, Object> mapa = new HashMap<String, Object>();
        mapa.put("idUsuario", model.cliente.id);
        mapa.put("idContenido",  idContenido);
        mapa.put("calificacion", calificacion);
        mapa.put("comentario", comentario);
        mapa.put("fecha", Utility.convertDateToString(new Date()));
        mapa.put("timestamp", ServerValue.TIMESTAMP);

        ref.setValue(mapa);

    }

    public void actualizarCalificacion(String idContenido, int calificacion, String comentario, String idCalificacion){
        DatabaseReference ref = database.getReference("calificaciones");
        ref = ref.child(idCalificacion);

        Map<String, Object> mapa = new HashMap<String, Object>();
        mapa.put("idUsuario", model.cliente.id);
        mapa.put("idContenido",  idContenido);
        mapa.put("calificacion", calificacion);
        mapa.put("comentario", comentario);
        mapa.put("fecha", Utility.convertDateToString(new Date()));
        mapa.put("timestamp", ServerValue.TIMESTAMP);

        ref.setValue(mapa);

    }





}
