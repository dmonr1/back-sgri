package com.dp.sgri.dto;

public class ClienteSoftwareDTO {

    private String cliente;
    private String instalado;
    private String nombre;
    private String fecha;
    private String version;
    private String fabricante;
    private String usuario;

    public ClienteSoftwareDTO() {
    }

    public ClienteSoftwareDTO(String cliente, String instalado, String nombre, String fecha, String version, String fabricante, String usuario) {
        this.cliente = cliente;
        this.instalado = instalado;
        this.nombre = nombre;
        this.fecha = fecha;
        this.version = version;
        this.fabricante = fabricante;
        this.usuario = usuario;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getInstalado() {
        return instalado;
    }

    public void setInstalado(String instalado) {
        this.instalado = instalado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
