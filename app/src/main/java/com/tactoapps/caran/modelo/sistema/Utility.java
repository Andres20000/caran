package com.tactoapps.caran.modelo.sistema;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tacto on 4/05/17.
 */

public class Utility {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }



    public static String convertToMoney(String valor) {

        String temp = "$";

        try {
            Locale colombia = new Locale("es","CO");
            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(colombia);
            defaultFormat.setMaximumFractionDigits(0);
            temp = defaultFormat.format(Integer.valueOf(valor));
            temp = temp.replace(',','.');


        }catch (RuntimeException r) {
            Log.i("MONEY",r.getLocalizedMessage());
            Log.i("MONEY",r.getMessage());
            return  "$";


        }
        return temp;


    }

    public static String convertToDistancia(float distancia) {

       int metros = (int)distancia;

       if (metros < 1000){
           return metros + " m";
       }
       else {
           String res = String.format("%.1f kms", distancia / 1000);
           return  res;
       }

    }



    public static String convertToMoney(int valor) {

        String temp = "$";
        try {
            Locale colombia = new Locale("es","CO");
            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(colombia);
            defaultFormat.setMaximumFractionDigits(0);
            temp  = defaultFormat.format(valor);
            temp = temp.replace(',','.');


        }catch (RuntimeException r) {
            Log.i("MONEY",r.getLocalizedMessage());
            Log.i("MONEY",r.getMessage());
            return  "$";

        }
        return  temp;

    }


    public static String getFechaHora(){
        DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");

        String fecha = hourdateFormat.format(new Date());
        fecha = fecha.replace("a. m.","AM");
        fecha = fecha.replace("p. m.","PM");
        fecha = fecha.replace("a.m.","AM");
        fecha = fecha.replace("p.m.","PM");
        fecha = fecha.replace("am","AM");
        fecha = fecha.replace("pm","PM");

        return fecha;

    }



    public static String convertDateToString(Date indate)
    {
        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy");

        try{
            dateString = sdfr.format( indate );
        }catch (Exception ex ){
            System.out.println(ex);
        }
        return dateString;
    }


    public static Calendar convertDateToCalendar(Date fecha){
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        return  cal;
    }


    public static String llenarCeros(String inicial, int numero){

        String snum = numero + "";
        while (snum.length() < 6){
            snum = "0" + snum;
        }

        return  inicial + snum;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }


    public static boolean LunCheck(String ccNumber)
    {
        int sum = 0;
        boolean alternate = false;
        for (int i = ccNumber.length() - 1; i >= 0; i--)
        {
            int n = 0;
            try {
                n = Integer.parseInt(ccNumber.substring(i, i + 1));
            }catch (Exception e) {
                return false;
            }
            if (alternate)
            {
                n *= 2;
                if (n > 9)
                {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }


    public static int calcularMesesEntresFechas(Date fechaInicio, Date fechaFin) {
        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(fechaInicio);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(fechaFin);
            int startMes = (startCalendar.get(Calendar.YEAR) * 12) + startCalendar.get(Calendar.MONTH);
            int endMes = (endCalendar.get(Calendar.YEAR) * 12) + endCalendar.get(Calendar.MONTH);
            int diffMonth = endMes - startMes;
            return diffMonth;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getEdad(Calendar newDate){
        long diff = new Date().getTime() - newDate.getTime().getTime()  ;
        long dias = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        int anios = (int) Math.floor(dias / 365);
        long resto  = dias % 365;
        int meses = (int) Math.floor(resto / 30);

        if (anios == 1){
            return "1 año " + meses + " meses";
        }

        if (anios > 0 ){
            return anios + " Años " + meses + " meses";
        }
        else {
            return meses + " meses";
        }


    }

    public static String getVersionParaUsuario(Context context){
        PackageInfo pInfo;

        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return  "Versión "+pInfo.versionName + " ("+ pInfo.versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }

        return "";


    }

}