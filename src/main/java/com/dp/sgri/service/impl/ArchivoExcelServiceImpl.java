package com.dp.sgri.service.impl;

import com.dp.sgri.entity.ArchivoExcel;
import com.dp.sgri.entity.TipoArchivo;
import com.dp.sgri.repository.ArchivoExcelRepository;
import com.dp.sgri.service.ArchivoExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dp.sgri.dto.ClienteSoftwareDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    @Override
    public List<ClienteSoftwareDTO> buscarClienteSoftware(Long id, String clienteBuscado) {
        ArchivoExcel archivo = obtenerPorId(id);
        List<ClienteSoftwareDTO> resultados = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(archivo.getContenido()))) {
            Sheet hoja = workbook.getSheetAt(0);

            int columnas = hoja.getRow(0).getPhysicalNumberOfCells();

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);
                if (fila == null) continue;

                String cliente = obtenerTextoCelda(fila.getCell(0)).toLowerCase();
                if (cliente.contains(clienteBuscado.toLowerCase())) {
                    ClienteSoftwareDTO dto = new ClienteSoftwareDTO();
                    dto.setCliente(obtenerTextoCelda(fila.getCell(0)));
                    dto.setInstalado(obtenerTextoCelda(fila.getCell(1)));
                    dto.setNombre(obtenerTextoCelda(fila.getCell(2)));
                    dto.setFecha(obtenerTextoCelda(fila.getCell(3)));
                    dto.setVersion(obtenerTextoCelda(fila.getCell(4)));
                    dto.setFabricante(obtenerTextoCelda(fila.getCell(5)));
                    dto.setUsuario(obtenerTextoCelda(fila.getCell(6)));
                    resultados.add(dto);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error procesando el archivo Excel", e);
        }

        return resultados;
    }

    private String obtenerTextoCelda(Cell celda) {
        if (celda == null) return "";

        String valor;

        switch (celda.getCellType()) {
            case STRING:
                valor = celda.getStringCellValue().trim();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(celda)) {
                    valor = celda.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    valor = String.valueOf((long) celda.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                valor = celda.getBooleanCellValue() ? "Sí" : "No";
                break;
            case FORMULA:
                try {
                    valor = celda.getStringCellValue().trim(); // o celda.getRichStringCellValue().getString()
                } catch (Exception e) {
                    valor = String.valueOf(celda.getNumericCellValue());
                }
                break;
            default:
                valor = "";
        }

        return repararTexto(valor);
    }


    private String repararTexto(String textoDañado) {
        if (textoDañado == null || textoDañado.isEmpty()) return textoDañado;
        try {
            byte[] bytes = textoDañado.getBytes("ISO-8859-1");
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            return textoDañado;
        }
    }



    public List<String> obtenerClientesUnicosDesdeSoftware(Long archivoId) {
        ArchivoExcel archivo = obtenerPorId(archivoId);

        if (!archivo.getTipoArchivo().equals(TipoArchivo.software)) {
            throw new IllegalArgumentException("El archivo no es de tipo software.");
        }

        try (InputStream inputStream = new ByteArrayInputStream(archivo.getContenido())) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Set<String> clientesUnicos = new HashSet<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // omitir encabezado

                Cell clienteCell = row.getCell(0); // primera columna
                if (clienteCell != null) {
                    clienteCell.setCellType(CellType.STRING);
                    String valor = clienteCell.getStringCellValue().trim();
                    if (!valor.isEmpty()) {
                        clientesUnicos.add(valor);
                    }
                }
            }

            workbook.close();
            return new ArrayList<>(clientesUnicos);

        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo Excel", e);
        }
    }

    @Override
    public List<String> obtenerClientesUnicosDesdeResumen(Long archivoId) {
        return obtenerClientesUnicosGenerico(archivoId, TipoArchivo.resumen);
    }

    @Override
    public List<String> obtenerClientesUnicosDesdeHardware(Long archivoId) {
        return obtenerClientesUnicosGenerico(archivoId, TipoArchivo.hardware);
    }

    private List<String> obtenerClientesUnicosGenerico(Long archivoId, TipoArchivo tipoEsperado) {
        ArchivoExcel archivo = obtenerPorId(archivoId);

        if (!archivo.getTipoArchivo().equals(tipoEsperado)) {
            throw new IllegalArgumentException("El archivo no es de tipo " + tipoEsperado.name());
        }

        try (InputStream inputStream = new ByteArrayInputStream(archivo.getContenido())) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Set<String> clientesUnicos = new HashSet<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Cell clienteCell = row.getCell(0);
                if (clienteCell != null) {
                    clienteCell.setCellType(CellType.STRING);
                    String valor = clienteCell.getStringCellValue().trim();
                    if (!valor.isEmpty()) {
                        clientesUnicos.add(repararTexto(valor));
                    }
                }
            }

            workbook.close();
            return new ArrayList<>(clientesUnicos);

        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo Excel", e);
        }
    }


}
