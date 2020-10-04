package com.example.transportapp.models;

public class UserInformation {

    String nombre;
    String apellidos;
    String identificacion;
    String ceular;
    String fechaNacimiento;

    public UserInformation(String nombre, String apellidos, String identifiacion, String celular, String fechaNacimiento) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.identificacion = identifiacion;
        this.ceular = celular;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getCeular() {
        return ceular;
    }

    public void setCeular(String ceular) {
        this.ceular = ceular;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
