package com.casavida.backend.controllers;

import com.casavida.backend.entity.*;
import com.casavida.backend.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private FraccionamientoRepository fraccionamientoRepository;

    @PostMapping("/lotes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<?> uploadLotes(@RequestParam("file") MultipartFile file) {
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        List<String> errors = new ArrayList<>();
        int totalRows = 0;

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            totalRows = sheet.getLastRowNum();
            
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Lote lote = new Lote();
                    lote.setNumeroLote(getCellString(row, 0));
                    lote.setManzana(getCellString(row, 1));
                    lote.setPrecioTotal(BigDecimal.valueOf(getCellNumeric(row, 2)));
                    lote.setAreaMetrosCuadrados(getCellNumeric(row, 3)); // Double, no int
                    
                    Long fraccId = (long) getCellNumeric(row, 4);
                    Optional<Fraccionamiento> fracc = fraccionamientoRepository.findById(fraccId);
                    if (fracc.isPresent()) {
                        lote.setFraccionamiento(fracc.get());
                    }
                    
                    lote.setCoordenadasGeo(getCellString(row, 5));
                    lote.setEstatus(EStatusLote.DISPONIBLE);
                    
                    loteRepository.save(lote);
                    successCount++;
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("row", i + 1);
                    resultMap.put("status", "success");
                    results.add(resultMap);
                    
                } catch (Exception e) {
                    errorCount++;
                    errors.add("Fila " + (i + 1) + ": " + e.getMessage());
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("row", i + 1);
                    resultMap.put("status", "error");
                    resultMap.put("error", e.getMessage());
                    results.add(resultMap);
                }
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al procesar archivo: " + e.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("tipo", "lotes");
        response.put("totalProcesados", totalRows);
        response.put("exitosos", successCount);
        response.put("errores", errorCount);
        response.put("detallesErrores", errors);
        response.put("resultados", results);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/clientes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('VENDEDOR')")
    public ResponseEntity<?> uploadClientes(@RequestParam("file") MultipartFile file) {
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        List<String> errors = new ArrayList<>();
        int totalRows = 0;

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            totalRows = sheet.getLastRowNum();
            
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Cliente cliente = new Cliente();
                    cliente.setNombre(getCellString(row, 0));
                    cliente.setApellidos(getCellString(row, 1));
                    cliente.setEmail(getCellString(row, 2));
                    cliente.setTelefono(getCellString(row, 3));
                    cliente.setDireccion(getCellString(row, 4));
                    // No RFC en Cliente - campo no existe
                    cliente.setFechaRegistro(LocalDateTime.now()); // LocalDateTime, no LocalDate
                    
                    clienteRepository.save(cliente);
                    successCount++;
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("row", i + 1);
                    resultMap.put("status", "success");
                    results.add(resultMap);
                    
                } catch (Exception e) {
                    errorCount++;
                    errors.add("Fila " + (i + 1) + ": " + e.getMessage());
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("row", i + 1);
                    resultMap.put("status", "error");
                    resultMap.put("error", e.getMessage());
                    results.add(resultMap);
                }
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al procesar archivo: " + e.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("tipo", "clientes");
        response.put("totalProcesados", totalRows);
        response.put("exitosos", successCount);
        response.put("errores", errorCount);
        response.put("detallesErrores", errors);
        response.put("resultados", results);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/contratos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<?> uploadContratos(@RequestParam("file") MultipartFile file) {
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        List<String> errors = new ArrayList<>();
        int totalRows = 0;

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            totalRows = sheet.getLastRowNum();
            
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Long clienteId = (long) getCellNumeric(row, 0);
                    Long loteId = (long) getCellNumeric(row, 1);
                    
                    Cliente cliente = clienteRepository.findById(clienteId)
                        .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + clienteId));
                    Lote lote = loteRepository.findById(loteId)
                        .orElseThrow(() -> new RuntimeException("Lote no encontrado: " + loteId));
                    
                    Contrato contrato = new Contrato();
                    contrato.setCliente(cliente);
                    contrato.setLote(lote);
                    contrato.setMontoTotal(BigDecimal.valueOf(getCellNumeric(row, 2)));
                    contrato.setEnganche(BigDecimal.valueOf(getCellNumeric(row, 3)));
                    contrato.setPlazoMeses((int) getCellNumeric(row, 4));
                    contrato.setTasaInteresAnual(getCellNumeric(row, 5));
                    contrato.setFechaContrato(LocalDate.now());
                    contrato.setEstatus(EStatusContrato.ACTIVO);
                    
                    // Marcar lote como contratado
                    lote.setEstatus(EStatusLote.CONTRATADO);
                    loteRepository.save(lote);
                    
                    contratoRepository.save(contrato);
                    successCount++;
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("row", i + 1);
                    resultMap.put("status", "success");
                    results.add(resultMap);
                    
                } catch (Exception e) {
                    errorCount++;
                    errors.add("Fila " + (i + 1) + ": " + e.getMessage());
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("row", i + 1);
                    resultMap.put("status", "error");
                    resultMap.put("error", e.getMessage());
                    results.add(resultMap);
                }
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al procesar archivo: " + e.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("tipo", "contratos");
        response.put("totalProcesados", totalRows);
        response.put("exitosos", successCount);
        response.put("errores", errorCount);
        response.put("detallesErrores", errors);
        response.put("resultados", results);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pagos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('CONTABILIDAD')")
    public ResponseEntity<?> uploadPagos(@RequestParam("file") MultipartFile file) {
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        List<String> errors = new ArrayList<>();
        int totalRows = 0;

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            totalRows = sheet.getLastRowNum();
            
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Long contratoId = (long) getCellNumeric(row, 0);
                    Contrato contrato = contratoRepository.findById(contratoId)
                        .orElseThrow(() -> new RuntimeException("Contrato no encontrado: " + contratoId));
                    
                    Pago pago = new Pago();
                    pago.setContrato(contrato);
                    String fechaStr = getCellString(row, 1);
                    pago.setFechaPago(LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    pago.setMonto(BigDecimal.valueOf(getCellNumeric(row, 2)));
                    pago.setConcepto(getCellString(row, 3));
                    pago.setReferencia(getCellString(row, 4));
                    pago.setMetodoPago(getCellString(row, 5));
                    pago.setEstatus(EPagoStatus.PENDIENTE);
                    
                    pagoRepository.save(pago);
                    successCount++;
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("row", i + 1);
                    resultMap.put("status", "success");
                    results.add(resultMap);
                    
                } catch (Exception e) {
                    errorCount++;
                    errors.add("Fila " + (i + 1) + ": " + e.getMessage());
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("row", i + 1);
                    resultMap.put("status", "error");
                    resultMap.put("error", e.getMessage());
                    results.add(resultMap);
                }
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al procesar archivo: " + e.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("tipo", "pagos");
        response.put("totalProcesados", totalRows);
        response.put("exitosos", successCount);
        response.put("errores", errorCount);
        response.put("detallesErrores", errors);
        response.put("resultados", results);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/template/{tipo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('VENDEDOR')")
    public ResponseEntity<?> downloadTemplate(@PathVariable String tipo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template");
            
            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            Row headerRow = sheet.createRow(0);
            String[] headers = getHeadersForType(tipo);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }
            
            // Add example row
            Row exampleRow = sheet.createRow(1);
            String[] examples = getExamplesForType(tipo);
            for (int i = 0; i < examples.length; i++) {
                exampleRow.createCell(i).setCellValue(examples[i]);
            }
            
            byte[] excelBytes;
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                workbook.write(baos);
                excelBytes = baos.toByteArray();
            }
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=plantilla_" + tipo + ".xlsx")
                .contentType(org.springframework.http.MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al generar plantilla: " + e.getMessage());
        }
    }

    private String[] getHeadersForType(String tipo) {
        String t = tipo.toLowerCase();
        if ("lotes".equals(t)) {
            return new String[]{"numero_lote", "manzana", "precio", "area_m2", "fraccionamiento_id", "coordenadas"};
        } else if ("clientes".equals(t)) {
            return new String[]{"nombre", "apellidos", "email", "telefono", "direccion"};
        } else if ("contratos".equals(t)) {
            return new String[]{"cliente_id", "lote_id", "monto_total", "enganche", "plazo_meses", "tasa_anual"};
        } else if ("pagos".equals(t)) {
            return new String[]{"contrato_id", "fecha_pago (dd/MM/yyyy)", "monto", "concepto", "referencia", "metodo_pago"};
        }
        return new String[]{};
    }

    private String[] getExamplesForType(String tipo) {
        String t = tipo.toLowerCase();
        if ("lotes".equals(t)) {
            return new String[]{"L-001", "A", "250000", "200", "1", "19.4326,-99.1332"};
        } else if ("clientes".equals(t)) {
            return new String[]{"Juan", "Pérez García", "juan@email.com", "5551234567", "Calle 123"};
        } else if ("contratos".equals(t)) {
            return new String[]{"1", "5", "300000", "30000", "60", "12.0"};
        } else if ("pagos".equals(t)) {
            return new String[]{"1", "15/02/2024", "5000", "Mensualidad", "REF-001", "Transferencia"};
        }
        return new String[]{};
    }

    private String getCellString(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) return "";
        
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return "";
    }

    private double getCellNumeric(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) return 0;
        
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
