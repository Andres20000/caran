package com.tactoapps.caran.modelo.cliente;

import android.support.annotation.NonNull;

/**
 * Created by andres on 4/22/18.
 */

public class Favorito implements  Comparable<Favorito> {

    public String idContenido = "";
    public long timestamp = 0;


    @Override
    public int compareTo(@NonNull Favorito favorito) {
        return (this.timestamp < favorito.timestamp ? -1 :
                (this.timestamp == favorito.timestamp ? 0 : 1));
    }
}
