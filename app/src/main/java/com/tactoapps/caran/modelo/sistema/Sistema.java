package com.tactoapps.caran.modelo.sistema;

/**
 * Created by tactomotion on 9/12/16.
 */
public class Sistema {

    double version;
    int build;
    String link ;
    Boolean updateMandatorio;
    Boolean capacidadCopada;

    public Sistema(){

    }


    public void setVersion(double version){
        this.version = version;
    }

    public double getVersion(){
        return this.version;
    }

    public void setBuild(int build){
        this.build = build;
    }

    public int getBuild(){
        return this.build;
    }

    public void setLink(String link){
        this.link = link;
    }

    public String getLink(){
        return this.link;
    }


    public void setUpdateMandatorio(boolean updateMandatorio){
        this.updateMandatorio = updateMandatorio;
    }

    public boolean getUpdateMandatorio(){
        return this.updateMandatorio;
    }

    public void setCapacidadCopada(boolean capacidadCopada){
        this.capacidadCopada = capacidadCopada;
    }

    public boolean getCapacidadCopada(){
        return this.capacidadCopada;
    }


}
