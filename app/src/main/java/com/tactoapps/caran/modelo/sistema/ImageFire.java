package com.tactoapps.caran.modelo.sistema;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by andres on 10/18/17.
 */

public class ImageFire {


    public String nombre = "";
    public String prefijo = "";
    public int versionFirebase = 1;
    public int versionLocal = 0;
    public String extension = "";
    public String folderStorage = "";
    protected Bitmap foto = null;
    boolean yallamado = false;
    public ArrayList<OnImageFireChange> interesados = new ArrayList<>();


    public interface OnImageFireChange {
        void cargoImagen(String tipo);

    }

    private ImageFire.OnImageFireChange mListener;

    public ImageFire(OnImageFireChange mListener){

        this.mListener = mListener;
    }

    public ImageFire(){

    }

    public  void setListener(OnImageFireChange mListener){
        this.mListener= mListener;
    }

    public void addListener(OnImageFireChange mListener){
        interesados.add(mListener);
    }



    public String getIdImagen() {

        String idImg = prefijo + eliminarTildes(nombre).replaceAll("\\s","");
        return idImg;
    }


    public String getNombreImagen() {

        String nombreImagen = prefijo + eliminarTildes(nombre).replaceAll("\\s","");
        nombreImagen  = nombreImagen + extension;
        return nombreImagen;
    }

    public String getPathEnStorage(){
        return folderStorage + "/" + getNombreImagen();
    }


    public void setFoto(Bitmap foto) {
        this.foto= foto;
        //TODO: Salvar en el disco
    }

    public Bitmap getFoto(String identificador){

        if (yallamado){
            return  foto;
        }

        getFotoFromFirebase(identificador);
        yallamado = true;


        if (foto != null) {
            return  foto;
        }

        //// TODO: 10/18/17 intentar cargar la imagen desde disco.


       return null;

    }



    private  void getFotoFromFirebase(final String identificador){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final String path = getPathEnStorage();
        StorageReference fotoRef = storageRef.child(path);
        Log.i("BAJANDO",path);
        final long ONE_MEGABYTE = 1024 * 1024;

        try {
            fotoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    try {

                        Bitmap foto=  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        if(foto !=null){
                            setFoto(foto);
                            versionLocal = versionFirebase;
                            if (mListener == null){
                                Log.v("exception","Listener esta NULO!");
                                for (OnImageFireChange fire :interesados){
                                    fire.cargoImagen(identificador);
                                }
                            }else{
                                Log.i("YA BAJO",path);
                                mListener.cargoImagen(identificador);
                                for (OnImageFireChange fire :interesados){
                                    fire.cargoImagen(identificador);
                                }
                            }

                        }
                    }catch (Exception e){
                        Log.v("PILAS ERROR:","exeption"+e.getMessage());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.v("PILAS ERROR","exeption"+exception.getMessage());
                }
            });

           }catch (RejectedExecutionException r) {

                Log.i("PILAS ERROR", "Paró de bajar:  " + r.getLocalizedMessage());

           }

    }



    public  StorageReference getStorageRef(){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final String path = getPathEnStorage();
        StorageReference fotoRef = storageRef.child(path);

        return fotoRef;

    }



    //eliminar tildes
    private static final String ORIGINAL
            = "ÁáÉéÍíÓóÚúÑñÜü";
    private static final String REPLACEMENT
            = "AaEeIiOoUuNnUu";
    public String eliminarTildes(String str) {
        if (str == null) {
            return null;
        }
        char[] array = str.toCharArray();
        for (int index = 0; index < array.length; index++) {
            int pos = ORIGINAL.indexOf(array[index]);
            if (pos > -1) {
                array[index] = REPLACEMENT.charAt(pos);
            }
        }
        return new String(array);
    }



}
