package com.tactoapps.caran.registro;

import android.Manifest.permission;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tactoapps.caran.BuildConfig;
import com.tactoapps.caran.MainActivity;
import com.tactoapps.caran.R;
import com.tactoapps.caran.comandos.CmdRegistro;
import com.tactoapps.caran.home.Home;
import com.tactoapps.caran.modelo.Modelo;
import com.tactoapps.caran.modelo.cliente.Cliente;
import com.tactoapps.caran.modelo.sistema.Utility;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RegistroComplementario extends AppCompatActivity  {

    LinearLayout layoutDir1;
    LinearLayout cuandroInstitucion;
    ScrollView scroll;

    EditText nombre, tipoGenero;
    EditText profesion, escolaridad, institucion, ciudad;

    Date fecha;
    TextView ayudanos, correo, fechaNacimiento;

    Modelo model = Modelo.getInstance();

    String[] profesiones = model.getProfesiones();
    String[] ciudades = model.getCiudades();
    String[] escolariades = model.getEscolaridades();
    String[] institutos = model.getInstitutos();


    Cliente cli = model.cliente;

    String modo = "NUEVO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            return;
        }


        setContentView(R.layout.activity_registro_complementario);  /////////////////////////////////////Layout

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        if (getIntent().hasExtra("MODO")) {
            modo = getIntent().getStringExtra("MODO");
        }

        scroll = (ScrollView) this.findViewById(R.id.scroll);



        nombre = (EditText) findViewById(R.id.nombre);

        tipoGenero = (EditText) findViewById(R.id.sexo);
        fechaNacimiento = findViewById(R.id.fechaNacimiento);
        profesion = (EditText) findViewById(R.id.profesion);
        institucion = (EditText) findViewById(R.id.institucion);
        cuandroInstitucion = findViewById(R.id.cuandroInstitucion);
        escolaridad = (EditText) findViewById(R.id.escolaridad);
        ciudad = (EditText) findViewById(R.id.ciudad);


        ayudanos = (TextView) findViewById(R.id.ayudanos);
        correo = (TextView) findViewById(R.id.correo);


        nombre.setText(cli.nombre);
        tipoGenero.setText(cli.sexo);
        fechaNacimiento.setText(Utility.convertDateToString(cli.fechaNacimiento));
        profesion.setText(cli.profesion);
        escolaridad.setText(cli.escolaridad);


        if (BuildConfig.FLAVOR.equals("caran") || BuildConfig.FLAVOR.equals("higadosano")){
            cuandroInstitucion.setVisibility(View.GONE);

        }

        institucion.setText(cli.institucion);



        ciudad.setText(cli.ciudad);
        fecha = cli.fechaNacimiento;

        if (model.tipoLogeo.equals("faceb")) {
            correo.setText(cli.correo + "(Facebook)");
        } else {
            correo.setText(cli.correo);
        }


        refrescarDatos();
        getCiudad();

    }


    public void refrescarDatos() {

        if (model.tipoLogeo.equals("faceb")) {
            //imgIngreso.setImageResource(R.drawable.imgfbok);

        }


        if (modo.equals("EDITAR")) {
            ayudanos.setVisibility(View.GONE);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    public void didTapOKContinuar(View v) {
        if (nombre.getText().toString().length() < 3) {
            nombre.setError("Debes incluir tu nombre");
            nombre.requestFocus();
            return;
        }


        if (fecha == null && fechaNacimiento.getText().toString().length() < 8) {
            fechaNacimiento.setError("Debes seleccionar la fecha de nacimiento");
            fechaNacimiento.requestFocus();
            return;

        }

        String token = FirebaseInstanceId.getInstance().getToken();

        cli.nombre = nombre.getText().toString();
        cli.profesion = profesion.getText().toString();
        cli.ciudad = ciudad.getText().toString();
        cli.escolaridad = escolaridad.getText().toString();
        cli.institucion = institucion.getText().toString();
        cli.fechaNacimiento = fecha;
        cli.sexo = tipoGenero.getText().toString();



        CmdRegistro.actualizarDatosBasicos();


        if (modo.equals("EDITAR")) {

            finish();
            return;
        }

        // if (modelc.compraActual.idPublicacion.equals("")) {

        //   if (modelc.publicaciones.size() == 0) {
        preCargar();
        // }


        //} else {
        //   googleApiClient.disconnect();
        //   finish();


    }





    public void didTapFondo(View v) {


        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch(Exception e) {

        }
    }



    public void tipoGenero(View v){

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        showOpcionesGenero();

    }


    public void didTapFecha(View v){
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        Calendar newCalendar =  Calendar.getInstance();

        DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fecha = newDate.getTime();
                fechaNacimiento.setText(dateFormat.format(newDate.getTime()));

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



        StartTime.setTitle("Fecha de nacimiento");
        StartTime.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        StartTime.show();
    }

    private void showOpcionesGenero() {


        final String[] generos  = new String[]{"Femenino","Masculino"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Genero");
        builder.setItems(generos, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

                String genero =  generos[item];
                tipoGenero.setText(genero);



            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    private void preCargar() {

        Intent i = new Intent(getApplicationContext(), Home.class);
        startActivity(i);
        finish();

    }



    @Override
    protected void onResume() {
        super.onResume();
        refrescarDatos();

    }


    public void tipoProfesion(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        showOpcionesProfesion();

    }

    public void tipoEscolaridad(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        showOpcionesEscolaridad();

    }

    public void tipoInstitucion(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        showOpcionesInstitutos();

    }

    public void tipoCiudad(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        showOpcionesCiudades();

    }


    private void showOpcionesProfesion() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profesión");
        builder.setItems(profesiones, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                profesion.setText(profesiones[item]);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showOpcionesEscolaridad() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tipo");
        builder.setItems(escolariades, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                escolaridad.setText(escolariades[item]);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showOpcionesCiudades() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ciudad");
        builder.setItems(ciudades, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ciudad.setText(ciudades[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showOpcionesInstitutos() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Institución");
        builder.setItems(institutos, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                institucion.setText(institutos[item]);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    private void getCiudad(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://ip-api.com/json";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("LUGAR", response);

                        try {
                            JSONObject json = new JSONObject(response);

                            ciudad.setText(json.getString("city"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("LUGAR", "No encrontro");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
