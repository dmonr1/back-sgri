package com.dp.sgri.repository;

import com.dp.sgri.entity.ArchivoExcel;
import com.dp.sgri.entity.TipoArchivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArchivoExcelRepository extends JpaRepository<ArchivoExcel, Long> {
    List<ArchivoExcel> findByTipoArchivo(TipoArchivo tipoArchivo);
    Optional<ArchivoExcel> findByNombreArchivoAndTipoArchivo(String nombreArchivo, TipoArchivo tipoArchivo);
    boolean existsByHashContenido(String hashContenido);

}
