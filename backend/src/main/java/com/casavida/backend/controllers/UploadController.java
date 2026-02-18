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

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/lotes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<?> uploadLotes(@RequestParam("file") MultipartFile file) {
        return processExcel(file, (sheet, row) -> {
            Lote lote = new Lote();
            lote.setNumeroLote(getCellString(row, 0));
            lote.setManzana(getCellString(row, 1));
            lote.setPrecioTotal(BigDecimal.valueOf(getCellNumeric(row, 2)));
            lote.setAreaMetrosCuadrados((int) getCellNumeric(row, 3));
            
            Long fraccId = (long) getCellNumeric(row, 4);
            fraccionamientoRepository.findById(fraccId).ifPresent(lote::setFraccionamiento);
            
            lote.setCoordenadasGeo(getCellString(row, 5));
            lote.setEstatus(EStatusLote.DISPONIBLE);
            
            return loteRepository.save(lote);
        }, "lotes");
    }

    @PostMapping("/clientes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('VENDEDOR')")
    public ResponseEntity<?> uploadClientes(@RequestParam("file") MultipartFile file) {
        return processExcel(file, (sheet, row) -> {
            Cliente cliente = new Cliente();
            cliente.setNombre(getCellString(row, 0));
            cliente.setApellidos(getCellString(row, 1));
            cliente.setEmail(getCellString(row, 2));
            cliente.setTelefono(getCellString(row, 3));
            cliente.setDireccion(getCellString(row, 4));
            cliente.setRfc(getCellString(row, 5));
            cliente.setFechaRegistro(LocalDate.now());
            
            return clienteRepository.save(cliente);
        }, "clientes");
    }

    @PostMapping("/contratos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<?> uploadContratos(@RequestParam("file") MultipartFile file) {
        return processExcel(file, (sheet, row) -> {
            Contrato contrato = new Contrato();
            
            Long clienteId = (long) getCellNumeric(row, 0);
            Long loteId = (long) getCellNumeric(row, 1);
            
            Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + clienteId));
            Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado: " + loteId));
            
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
            
            return contratoRepository.save(contrato);
        }, "contratos");
    }

    @PostMapping("/pagos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('CONTABILIDAD')")
    public ResponseEntity<?> uploadPagos(@RequestParam("file") MultipartFile file) {
        return processExcel(file, (sheet, row) -> {
            Pago pago = new Pago();
            
            Long contratoId = (long) getCellNumeric(row, 0);
            Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado: " + contratoId));
            
            pago.setContrato(contrato);
            String fechaStr = getCellString(row, 1);
            pago.setFechaPago(LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            pago.setMonto(BigDecimal.valueOf(getCellNumeric(row, 2)));
            pago.setConcepto(getCellString(row, 3));
            pago.setReferencia(getCellString(row, 4));
            pago.setMetodoPago(getCellString(row, 5));
            pago.setEstatus(EPagoStatus.PENDIENTE);
            
            return pagoRepository.save(pago);
        }, "pagos");
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
            try (var baos = new java.io.ByteArrayOutputStream()) {
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
        return switch (tipo.toLowerCase()) {
            case "lotes" -> new String[]{"numero_lote", "manzana", "precio", "area_m2", "fraccionamiento_id", "coordenadas"};
            case "clientes" -> new String[]{"nombre", "apellidos", "email", "telefono", "direccion", "rfc"};
            case "contratos" -> new String[]{"cliente_id", "lote_id", "monto_total", "enganche", "plazo_meses", "tasa_anual"};
            case "pagos" -> new String[]{"contrato_id", "fecha_pago (dd/MM/yyyy)", "monto", "concepto", "referencia", "metodo_pago"};
            default -> new String[]{};
        };
    }

    private String[] getExamplesForType(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "lotes" -> new String[]{"L-001", "A", "250000", "200", "1", "19.4326,-99.1332"};
            case "clientes" -> new String[]{"Juan", "Pérez García", "juan@email.com", "5551234567", "Calle 123", "PEGJ800101"};
            case "contratos" -> new String[]{"1", "5", "300000", "30000", "60", "12.0"};
            case "pagos" -> new String[]{"1", "15/02/2024", "5000", "Mensualidad", "REF-001", "Transferencia"};
            default -> new String[]{};
        };
    }

    private ResponseEntity<?> processExcel(MultipartFile file, ExcelProcessor processor, String tipo) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        List<String> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Object result = processor.process(sheet, row);
                    successCount++;
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("row", i + 1);
                    resultMap.put("status", "success");
                    resultMap.put("data", result);
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
        response.put("tipo", tipo);
        response.put("totalProcesados", sheet.getLastRowNum());
        response.put("exitosos", successCount);
        response.put("errores", errorCount);
        response.put("detallesErrores", errors);
        response.put("resultados", results);
        
        return ResponseEntity.ok(response);
    }

    private String getCellString(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) return "";
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    private double getCellNumeric(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) return 0;
        
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield 0;
                }
            }
            default -> 0;
        };
    }

    @FunctionalInterface
    interface ExcelProcessor {
        Object process(Sheet sheet, Row row) throws Exception;
    }
}
