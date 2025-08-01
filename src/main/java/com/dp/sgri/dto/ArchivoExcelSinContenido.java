package com.dp.sgri.dto;

import com.dp.sgri.entity.TipoArchivo;

import java.time.LocalDateTime;

public interface ArchivoExcelSinContenido {
    Long getId();
    String getNombreArchivo();
    String getNombreOriginal();
    LocalDateTime getFechaSubida();
    TipoArchivo getTipoArchivo();
}
