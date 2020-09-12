package com.tactoapps.caran.comandos;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.tactoapps.caran.modelo.Modelo;

/**
 * Created by andres on 2/25/18.
 */

public class CmdCliente {



    public static void addFavorito(String idContenido){


        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("usuarios/" + modelo.cliente.id + "/favoritos/" + idContenido);

        ref.setValue(ServerValue.TIMESTAMP);


    }

    public static void addReciente(String idContenido){


        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("usuarios/" + modelo.cliente.id + "/recientes/" + idContenido);

        ref.setValue(ServerValue.TIMESTAMP);


    }

    public static void removeFavorito(String idContenido){


        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("usuarios/" + modelo.cliente.id + "/favoritos/" + idContenido);

        ref.removeValue();

    }







}

