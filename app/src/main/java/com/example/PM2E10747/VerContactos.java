package com.example.PM2E10747;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.PM2E10747.Configuracion.Base;
import com.example.PM2E10747.Configuracion.Contactos;
import com.example.PM2E10747.Configuracion.SQLiteConexion;

import java.util.ArrayList;

public class VerContactos extends AppCompatActivity {

    static final int REQUEST_CALL_PERMISSION = 1;
    Button btndelete,btnimagen,btnatras,btncompartir,btnactualizar;
    EditText buscar;
    ListView listacontactos;

    SQLiteConexion conexion;

    ArrayList<Contactos> lista;

    ArrayList<String> ArregloContactos;

    int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_contactos);

        conexion = new SQLiteConexion(this, Base.NameDatabase,null,1);
        listacontactos = (ListView) findViewById(R.id.Lista);
        btnatras = (Button) findViewById(R.id.btnatras);
        btndelete = (Button) findViewById(R.id.btndelete);
        btnimagen = (Button) findViewById(R.id.btnimagen);
        btncompartir = (Button) findViewById(R.id.btncompartir);
        btnactualizar = (Button) findViewById(R.id.btnactualizar);

        Obtenertabla();

        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1,ArregloContactos);
        listacontactos.setAdapter(adp);

        listacontactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                selectedIndex = i;
                String objetoseleccionado = (String) adapterView.getItemAtPosition(i);

                Toast.makeText(getApplicationContext(),"Seleccionaste: " + objetoseleccionado,Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(VerContactos.this);
                builder.setTitle("ACCION");
                builder.setMessage("Desea llamar a "+ lista.get(i).getNombre());
                builder.setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Contactos selectcontact = lista.get(i);
                        String numero = selectcontact.getTelefono();

                        hacerllamada(numero);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });

        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });


        btnactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedIndex != -1 && selectedIndex < lista.size()) {
                    Contactos selectcontact = lista.get(selectedIndex);

                    eliminarcontacto(selectcontact.getId());

                    lista.remove(selectedIndex);
                    ArregloContactos.remove(selectedIndex);

                    adp.notifyDataSetChanged();

                }
            }
        });

        btncompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedIndex != -1 && selectedIndex < lista.size()) {
                    Contactos selectcontact = lista.get(selectedIndex);


                    String nombre = selectcontact.getNombre();
                    String telefono = selectcontact.getTelefono();
                    String nota = selectcontact.getNota();


                    String textoCompartir = "Nombre: " + nombre + "\n" +
                            "Teléfono: " + telefono + "\n" +
                            "Nota: " + nota;


                    Intent intentCompartir = new Intent(Intent.ACTION_SEND);
                    intentCompartir.setType("text/plain");
                    intentCompartir.putExtra(Intent.EXTRA_TEXT, textoCompartir);


                    startActivity(Intent.createChooser(intentCompartir, "Compartir contacto"));
                }
            }

        });



    }

    private void hacerllamada(String numero) {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }else{
            vernumero(numero);
        }
    }

    private void vernumero(String numero) {
        Intent callintent = new Intent(Intent.ACTION_CALL);
        callintent.setData(Uri.parse("tel: "+ numero));
        startActivity(callintent);
    }

    private void eliminarcontacto(Integer id) {
        SQLiteDatabase bd = conexion.getWritableDatabase();
        String whereClause = Base.id + " = ?";
        String[] whereArgs = { String.valueOf(id) };
        bd.delete(Base.tablacontactos, whereClause, whereArgs);

        bd.execSQL("DELETE FROM sqlite_sequence WHERE name='" + Base.tablacontactos + "'");
        bd.execSQL("VACUUM");
        bd.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('" + Base.tablacontactos + "', 0)");
        bd.close();
    }

    private void Obtenertabla() {

        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos contacts = null;
        lista = new ArrayList<Contactos>();


        Cursor cursor = db.rawQuery(Base.SelectTableContacto,null);


        while (cursor.moveToNext()){
            contacts = new Contactos();
            contacts.setId(cursor.getInt(0));
            contacts.setNombre(cursor.getString(1));
            contacts.setTelefono(cursor.getString(2));
            contacts.setNota(cursor.getString(3));

            lista.add(contacts);
        }

        cursor.close();

        fillList();

    }

    private void fillList() {

        ArregloContactos = new ArrayList<String>();

        for (Contactos contact : lista){
            ArregloContactos.add(contact.getNombre() + " | " + contact.getTelefono());
        }
    }
}