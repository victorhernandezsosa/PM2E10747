package com.example.PM2E10747;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.PM2E10747.Configuracion.Base;
import com.example.PM2E10747.Configuracion.SQLiteConexion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Spinner pais;
    Button botonfoto,btnguardar,btnvercontactos;
    EditText nombre,telefono,nota;
    ArrayAdapter<String> agregar;

    ImageView imagen;
    String currentPhotoPath;


    static final int peticion_captura_imagen = 101;

    static final int peticion_acceso_camara = 102;
    String[] paises = {"Honduras (504)", "Costa Rica","Guatemala (502)", "El Salvador"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pais = (Spinner) findViewById(R.id.pais);
        botonfoto = (Button) findViewById(R.id.botonfoto);
        btnguardar = (Button) findViewById(R.id.btnguardar);
        btnvercontactos = (Button) findViewById(R.id.btnvercontactos);
        nombre = (EditText) findViewById(R.id.nombre);
        telefono = (EditText) findViewById(R.id.telefono);
        imagen = (ImageView) findViewById(R.id.imagen);
        nota = (EditText) findViewById(R.id.nota);



        agregar = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paises);
        agregar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pais.setAdapter(agregar);

        botonfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarcontactos();
            }
        });

        btnvercontactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vercontactos();

            }
        });

    }

    private void vercontactos() {
        Intent intent = new Intent(getApplicationContext(),VerContactos.class);
        startActivity(intent);
    }

    //Codigo de las Alertas
    private void salvarcontactos() {
        String nomb = nombre.getText().toString();
        String tel = telefono.getText().toString();
        String not = nota.getText().toString();


        if (nomb.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ALERTA");
            builder.setMessage("Por favor, debe escribir un nombre");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
                    builder.show();

        }else if(tel.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ALERTA");
            builder.setMessage("Por favor, Debe escribir un numero de telefono");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();

        }else if (not.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ALERTA");
            builder.setMessage("Por favor, Debe escribir una nota");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
        else{
            try {
                Bitmap foto = obtenerfoto();

                guardarfoto(foto);

                CleanScreen();

            }catch (Exception e){

            }
        }


    }



    private Bitmap obtenerfoto() {
        imagen.setDrawingCacheEnabled(true);
        imagen.buildDrawingCache();
        Bitmap foto = imagen.getDrawingCache();

        return foto;
    }

    private void CleanScreen() {
        nombre.setText("");
        telefono.setText("");
        nota.setText("");

    }


    //Codigo para poder tomar  y guardar la fotografia

    private byte[] convertirimagen(Bitmap imagen){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void guardarfoto(Bitmap imagen){
        byte[] foto = convertirimagen(imagen);

        SQLiteConexion conexion = new SQLiteConexion(this, Base.NameDatabase,null,1);
        SQLiteDatabase bd = conexion.getWritableDatabase();


        ContentValues datos = new ContentValues();
        datos.put(Base.nombre, nombre.getText().toString());
        datos.put(Base.telefono, telefono.getText().toString());
        datos.put(Base.nota, nota.getText().toString());
        datos.put(Base.foto,foto);

        Long result = bd.insert(Base.tablacontactos, Base.id, datos);
        Toast.makeText(getApplicationContext(),"Registro Ingresado",Toast.LENGTH_LONG).show();

        bd.close();

        CleanScreen();
    }
    private void permisos() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},peticion_acceso_camara);
        }
        else
        {
            intenttomarfoto();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == peticion_acceso_camara )
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                intenttomarfoto();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "Se necesita el permiso para que se pueda acceder a la camara", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == peticion_captura_imagen) {

            try {
                File foto = new File(currentPhotoPath);
                imagen.setImageURI(Uri.fromFile(foto));
            } catch (Exception ex) {
                ex.toString();
            }

        }
    }

    private File carpetaimagen() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void intenttomarfoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = carpetaimagen();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.PM2E10747.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, peticion_captura_imagen);
            }
        }
    }
}