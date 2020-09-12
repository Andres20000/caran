package com.tactoapps.caran.comandos;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tactoapps.caran.modelo.Modelo;

/**
 * Created by andres on 2/24/18.
 */

public class CmdSystem {


    public interface OnCmdSystem {


        void  actualizoSistema();
    }


    public static void getSytem(final OnCmdSystem listener){

        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("system/");//ruta path
        ref.addListenerForSingleValueEvent(new ValueEventListener() {//addListenerForSingleValueEvent no queda escuchando peticiones
            @Override
            public void onDataChange(DataSnapshot snap) {


                Long androideBuildlong = (Long)snap.child("androideBuild").getValue();
                modelo.sistema.setBuild(androideBuildlong.intValue());

                modelo.sistema.setLink(snap.child("androideLink").getValue().toString());
                boolean double1 = (boolean)snap.child("androideUpdateMandatorio").getValue();
                modelo.sistema.setUpdateMandatorio(double1);

                Long entero2 = (Long) snap.child("androideVersion").getValue();
                modelo.sistema.setVersion(entero2);


                listener.actualizoSistema();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
