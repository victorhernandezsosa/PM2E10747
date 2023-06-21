package com.example.PM2E10747.Configuracion;

public class Contactos {

    private Integer id;
    private String pais;
    private String nombre;
    private String telefono;
    private String nota;
    private String imagen;

    public  Contactos(Integer id,String pais, String nombre,String telefono,String nota, String imagen){
        this.id = id;
    this.pais = pais;
    this.nombre = nombre;
    this.telefono = telefono;
    this.nota = nota;
    this.imagen = imagen;

    }

    public Contactos(){

    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public byte[] getImagen() {
        return imagen.getBytes();
    }

    public void setImagen(byte[] imagen) {
        this.imagen = new String(imagen);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}


