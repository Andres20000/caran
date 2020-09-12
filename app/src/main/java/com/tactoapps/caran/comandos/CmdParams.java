package com.tactoapps.caran.comandos;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Params;


/**
 * Created by andres on 3/24/18.
 */

public class CmdParams {




    public static  void getParams(){

        final Modelo modelo = Modelo.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("params");//ruta path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                modelo.params =  snap.getValue(Params.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }



}
