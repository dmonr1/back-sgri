package com.dp.sgri.controller;

import com.dp.sgri.entity.ArchivoExcel;
import com.dp.sgri.entity.TipoArchivo;
import com.dp.sgri.service.ArchivoExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "*")
public class ArchivoExcelController {

    private final ArchivoExcelService archivoExcelService;

    @Autowired
    public ArchivoExcelController(ArchivoExcelService archivoExcelService) {
        this.archivoExcelService = archivoExcelService;
    }

    @PostMapping("/subir")
    public ResponseEntity<ArchivoExcel> subirArchivo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") TipoArchivo tipo) {
        ArchivoExcel archivo = archivoExcelService.guardar(file, tipo);
        return ResponseEntity.ok(archivo);
    }

    @GetMapping("/tipo/{tipo}")
    public List<ArchivoExcel> listarPorTipo(@PathVariable TipoArchivo tipo) {
        return archivoExcelService.listarPorTipo(tipo);
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id) {
        ArchivoExcel archivo = archivoExcelService.obtenerPorId(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(archivo.getNombreArchivo())
                .build());

        return new ResponseEntity<>(archivo.getContenido(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/contenido")
    public ResponseEntity<byte[]> obtenerContenidoArchivo(@PathVariable Long id) {
        ArchivoExcel archivo = archivoExcelService.obtenerPorId(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(archivo.getContenido());
    }

    @GetMapping("/ultimo/tipo/{tipo}")
    public ResponseEntity<ArchivoExcel> obtenerUltimoPorTipo(@PathVariable TipoArchivo tipo) {
        List<ArchivoExcel> archivos = archivoExcelService.listarPorTipo(tipo);
        if (archivos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ArchivoExcel ultimo = archivos.get(archivos.size() - 1); // suponiendo orden por fecha
        return ResponseEntity.ok(ultimo);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarArchivo(@PathVariable Long id) {
        archivoExcelService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ArchivoExcel>> listarTodos() {
        List<ArchivoExcel> archivos = archivoExcelService.listarTodos();
        return ResponseEntity.ok(archivos);
    }

}
