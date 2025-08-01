package com.dp.sgri.service.impl;

import com.dp.sgri.entity.ArchivoExcel;
import com.dp.sgri.entity.TipoArchivo;
import com.dp.sgri.repository.ArchivoExcelRepository;
import com.dp.sgri.service.ArchivoExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArchivoExcelServiceImpl implements ArchivoExcelService {

    private final ArchivoExcelRepository repository;

    @Autowired
    public ArchivoExcelServiceImpl(ArchivoExcelRepository repository) {
        this.repository = repository;
    }

    @Override
    public ArchivoExcel guardar(MultipartFile file, TipoArchivo tipo) {
        try {
            byte[] contenido = file.getBytes();
            String hash = calcularHash(contenido);

            // Verificar si ya existe un archivo con ese hash
            if (repository.existsByHashContenido(hash)) {
                throw new RuntimeException("Ya existe un archivo con el mismo contenido.");
            }

            ArchivoExcel archivo = new ArchivoExcel();
            archivo.setNombreArchivo("interno-" + file.getOriginalFilename());
            archivo.setNombreOriginal(file.getOriginalFilename());
            archivo.setTipoArchivo(tipo);
            archivo.setFechaSubida(LocalDateTime.now());
            archivo.setContenido(contenido);
            archivo.setHashContenido(hash);  // << Aquí lo seteas

            return repository.save(archivo);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo", e);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<ArchivoExcel> listarPorTipo(TipoArchivo tipo) {
        return repository.findByTipoArchivo(tipo);
    }

    @Override
    public ArchivoExcel obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado con ID: " + id));
    }

    @Override
    public void eliminarPorId(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ArchivoExcel> listarTodos() {
        return repository.findAll();
    }

    private String calcularHash(byte[] contenido) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(contenido);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No se pudo calcular el hash SHA-256", e);
        }
    }
}
