package com.dp.sgri.service;

import com.dp.sgri.dto.ClienteSoftwareDTO;
import com.dp.sgri.entity.ArchivoExcel;
import com.dp.sgri.entity.TipoArchivo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArchivoExcelService {
    ArchivoExcel guardar(MultipartFile file, TipoArchivo tipo);
    List<ArchivoExcel> listarPorTipo(TipoArchivo tipo);
    ArchivoExcel obtenerPorId(Long id);
    void eliminarPorId(Long id);
    List<ArchivoExcel> listarTodos();
    List<ClienteSoftwareDTO> buscarClienteSoftware(Long id, String cliente);

}
