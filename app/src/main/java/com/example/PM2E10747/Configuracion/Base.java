package com.example.PM2E10747.Configuracion;

public class Base {

    public static final String NameDatabase = "Personas";



    public static final String tablacontactos = "contactos";



    public static final String id = "id";
    public static final String nombre = "nombre";
    public static final String telefono = "telefono";
    public static final String nota = "nota";

    public static final String foto = "foto";



    public static final String CreateTableContacto = "CREATE TABLE CONTACTOS"+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, telefono TEXT, nota TEXT,foto BLOB)";

    public static final String DROPTableContactos = "DROP TABLE IF EXISTS contactos";

    public static final String SelectTableContacto = "SELECT * FROM " + Base.tablacontactos;
}
