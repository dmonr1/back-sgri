package com.dp.sgri.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "archivos_excel")
public class ArchivoExcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_archivo", nullable = false)
    private TipoArchivo tipoArchivo;

    @Column(name = "nombre_original", nullable = false)
    private String nombreOriginal;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    @Lob
    @Column(name = "contenido", nullable = false)
    private byte[] contenido;

    @Column(name = "hash_contenido", nullable = false, unique = true, length = 64)
    private String hashContenido;

    public ArchivoExcel() {
    }

    public ArchivoExcel(Long id, String nombreArchivo, TipoArchivo tipoArchivo,
                        String nombreOriginal, LocalDateTime fechaSubida, byte[] contenido) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.tipoArchivo = tipoArchivo;
        this.nombreOriginal = nombreOriginal;
        this.fechaSubida = fechaSubida;
        this.contenido = contenido;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public TipoArchivo getTipoArchivo() { return tipoArchivo; }
    public void setTipoArchivo(TipoArchivo tipoArchivo) { this.tipoArchivo = tipoArchivo; }

    public String getNombreOriginal() { return nombreOriginal; }
    public void setNombreOriginal(String nombreOriginal) { this.nombreOriginal = nombreOriginal; }

    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }

    public byte[] getContenido() { return contenido; }
    public void setContenido(byte[] contenido) { this.contenido = contenido; }

    public String getHashContenido() { return hashContenido; }
    public void setHashContenido(String hashContenido) { this.hashContenido = hashContenido; }

}
