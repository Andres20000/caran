package com.tactoapps.caran.modelo.cliente;

import java.util.Date;

/**
 * Created by andres on 9/29/17.
 */

public class Cliente {

    public String nombre = "";
    public String correo = "";
    public String id = "";
    public String sexo = "";
    public Date fechaNacimiento = null;
    public String profesion = "";
    public String escolaridad = "";
    public String institucion = "";
    public String ciudad = "";
    public String estado = "";


    public boolean tieneDatosBasicos(){

        if ( sexo.equals("") ) {
            return false;
        }
        return true;

    }






}


