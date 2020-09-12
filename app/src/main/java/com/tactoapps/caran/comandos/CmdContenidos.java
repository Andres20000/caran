package com.tactoapps.caran.comandos;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.Modelo.OnModelRecargarListener;
import com.tactoapps.caran.modelo.cliente.Calificacion;
import com.tactoapps.caran.modelo.cliente.Contenido;
import com.tactoapps.caran.modelo.cliente.Empresa;
import com.tactoapps.caran.modelo.cliente.Params;

import java.util.ArrayList;

/**
 * Created by andres on 4/13/18.
 */

public class CmdContenidos {


    public interface OnGetContenidos {

        void downContenido();
    }

    public interface OnGetAfiliaciones {

        void downAfiliaciones();

        void downNewAfiliaciones();
    }


    public static void getContenido(final String idContenido, final OnModelRecargarListener listener) {
        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("contenidos/" + idContenido);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Contenido con = readContenido(snap);
                if (con.estado.equals("inactivo")) {
                    return;
                }

                con.id = idContenido;
                modelo.addContenido(con);
                CmdCalificacion.getInstance().getCalificaciones(con.id);
                listener.terminoPrecarga();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public static void getContenidos(final OnGetContenidos listener) {

        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("contenidos");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snap, String s) {

                Contenido con = readContenido(snap);
                if (con.estado.equals("inactivo")) {
                    return;
                }
                con.id = snap.getKey();
                if (modelo.addContenido(con)) {
                    listener.downContenido();
                    CmdCalificacion.getInstance().getCalificaciones(con.id);
                }else{
                    listener.downContenido();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


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
        });

    }


    private static Contenido readContenido(DataSnapshot snap) {
        Contenido con = snap.getValue(Contenido.class);
        con.id = snap.getKey();
        con.set();
        getAutor(con.idEditor, con);
        return con;

    }


    public static void getAutor(String idEditor, final Contenido contenido) {

        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("editores/" + idEditor);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {


                if (snap.getValue() == null) {
                    contenido.autor = "";
                    return;
                }
                String autor = "";
                autor = snap.child("nombres").getValue().toString() + " " + snap.child("apellidos").getValue().toString();
                contenido.autor = autor;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public static void getMisSuscripciones(final OnGetAfiliaciones listener) {
        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("empresas/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                for (DataSnapshot snapHijo : snap.getChildren()) {
                    if (snapHijo.hasChild("/usuarios/" + modelo.cliente.id)) {
                        modelo.addAfiliado(snapHijo.getKey());
                        Empresa empresa = new Empresa();
                        empresa.id = snapHijo.getKey();
                        empresa.nombre = snapHijo.child("razonSocial").getValue().toString();
                        empresa.estado = (boolean) snapHijo.child("activo").getValue();
                        modelo.addEmpresa(empresa);

                    } else {
                        escucharAfiliacion(snapHijo.getKey(), listener);
                    }
                }
                listener.downAfiliaciones();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void escucharAfiliacion(final String idEmpresa, final OnGetAfiliaciones listener) {
        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("empresas/" + idEmpresa + "/usuarios/" + modelo.cliente.id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                if (snap.getValue() == null) {
                    return;
                }
                getNombreEmpresa(idEmpresa);
                modelo.addAfiliado(idEmpresa);
                listener.downNewAfiliaciones();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public static void getNombreEmpresa(final String idEmpresa) {
        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("empresas/" + idEmpresa + "/razonSocial");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Empresa empresa = new Empresa();
                empresa.id = idEmpresa;
                empresa.nombre = snap.getValue().toString();
                modelo.addEmpresa(empresa);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }
}