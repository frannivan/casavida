package com.casavida.backend.services;

import com.casavida.backend.entity.Contrato;
import com.casavida.backend.entity.Pago;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class PdfService {

    public ByteArrayInputStream generateEstadoCuenta(Contrato contrato, List<Pago> pagos) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);

            // 1. Header
            Paragraph title = new Paragraph("CasaVida Inmobiliaria", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Estado de Cuenta", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // 2. Info Section
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            // Client Info
            PdfPCell cell = new PdfPCell(new Phrase(
                    "Cliente: " + contrato.getCliente().getNombre() + " " + contrato.getCliente().getApellidos()));
            cell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell);

            // Lot Info
            cell = new PdfPCell(new Phrase("Lote: " + contrato.getLote().getNumeroLote() + " - "
                    + (contrato.getLote().getFraccionamiento() != null ? contrato.getLote().getFraccionamiento().getNombre() : "Independiente")));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell);

            // Contract Info
            cell = new PdfPCell(new Phrase("Contrato No: " + contrato.getId()));
            cell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell);

            cell = new PdfPCell(new Phrase("Fecha: " + java.time.LocalDate.now()));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell);

            document.add(infoTable);
            document.add(new Paragraph(" ")); // Spacer

            // 3. Financial Summary
            NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

            document.add(new Paragraph("Resumen Financiero:", subHeaderFont));
            document.add(new Paragraph("Precio Total: " + currency.format(contrato.getMontoTotal()), normalFont));
            document.add(new Paragraph("Enganche: " + currency.format(contrato.getEnganche()), normalFont));
            document.add(
                    new Paragraph("Mensualidad Pactada: " + currency.format(contrato.getMensualidad()), normalFont));
            document.add(new Paragraph(" "));

            // 4. Payments Table
            PdfPTable table = new PdfPTable(4); // Date, Concept, Ref, Amount
            table.setWidthPercentage(100);
            table.setWidths(new int[] { 3, 4, 3, 3 });

            // Headers
            java.util.stream.Stream.of("Fecha", "Concepto", "Referencia", "Monto")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                        header.setBorderWidth(1);
                        header.setPhrase(new Phrase(columnTitle, subHeaderFont));
                        table.addCell(header);
                    });

            // Data
            double totalPagado = 0;
            for (Pago pago : pagos) {
                table.addCell(new Phrase(pago.getFechaPago().toString(), normalFont));
                table.addCell(new Phrase(pago.getConcepto(), normalFont));
                table.addCell(new Phrase(pago.getReferencia(), normalFont));
                table.addCell(new Phrase(currency.format(pago.getMonto()), normalFont));
                totalPagado += pago.getMonto().doubleValue();
            }

            document.add(table);

            // 5. Total Footer
            document.add(new Paragraph(" "));
            Paragraph totalP = new Paragraph("Total Pagado: " + currency.format(totalPagado), subHeaderFont);
            totalP.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalP);

            Paragraph restante = new Paragraph(
                    "Saldo Pendiente: " + currency.format(contrato.getMontoTotal().doubleValue() - totalPagado),
                    subHeaderFont);
            restante.setAlignment(Element.ALIGN_RIGHT);
            document.add(restante);

            document.close();

        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateUsersReport(List<java.util.Map<String, Object>> users) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.BOLD);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);

            Paragraph title = new Paragraph("Reporte de Usuarios Registrados", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 3, 4, 3});

            java.util.stream.Stream.of("ID", "Usuario", "Email", "Roles")
                .forEach(h -> {
                    PdfPCell cell = new PdfPCell(new Phrase(h, subHeaderFont));
                    cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                    table.addCell(cell);
                });

            for (java.util.Map<String, Object> user : users) {
                table.addCell(new Phrase(String.valueOf(user.get("id")), normalFont));
                table.addCell(new Phrase(String.valueOf(user.get("username")), normalFont));
                table.addCell(new Phrase(String.valueOf(user.get("email")), normalFont));
                table.addCell(new Phrase(String.valueOf(user.get("roles")), normalFont));
            }
            document.add(table);
            document.close();
        } catch (Exception ex) { ex.printStackTrace(); }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateInventarioReport(List<com.casavida.backend.entity.Lote> lotes) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.BOLD);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);
            NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

            Paragraph title = new Paragraph("Reporte de Inventario Disponible", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{2, 2, 4, 3, 2, 2});

            java.util.stream.Stream.of("Lote", "Manzana", "Fraccionamiento", "Precio", "Área", "Estatus")
                .forEach(h -> {
                    PdfPCell cell = new PdfPCell(new Phrase(h, subHeaderFont));
                    cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                    table.addCell(cell);
                });

            for (com.casavida.backend.entity.Lote lote : lotes) {
                table.addCell(new Phrase(lote.getNumeroLote(), normalFont));
                table.addCell(new Phrase(lote.getManzana(), normalFont));
                table.addCell(new Phrase(lote.getFraccionamiento() != null ? lote.getFraccionamiento().getNombre() : "Indep.", normalFont));
                table.addCell(new Phrase(currency.format(lote.getPrecioTotal()), normalFont));
                table.addCell(new Phrase(lote.getAreaMetrosCuadrados() + " m2", normalFont));
                table.addCell(new Phrase(String.valueOf(lote.getEstatus()), normalFont));
            }
            document.add(table);
            document.close();
        } catch (Exception ex) { ex.printStackTrace(); }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generatePagosReport(List<Pago> pagos) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.BOLD);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);
            NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

            Paragraph title = new Paragraph("Reporte Detallado de Pagos", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{2, 3, 2, 4, 2, 2});

            java.util.stream.Stream.of("Fecha", "Cliente", "Monto", "Concepto", "Lote", "Referencia")
                .forEach(h -> {
                    PdfPCell cell = new PdfPCell(new Phrase(h, subHeaderFont));
                    cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                    table.addCell(cell);
                });

            for (Pago p : pagos) {
                table.addCell(new Phrase(p.getFechaPago().toString(), normalFont));
                String cli = p.getContrato() != null && p.getContrato().getCliente() != null ? 
                    p.getContrato().getCliente().getNombre() + " " + p.getContrato().getCliente().getApellidos() : "N/A";
                table.addCell(new Phrase(cli, normalFont));
                table.addCell(new Phrase(currency.format(p.getMonto()), normalFont));
                table.addCell(new Phrase(p.getConcepto(), normalFont));
                String lote = p.getContrato() != null && p.getContrato().getLote() != null ? 
                    "Lote " + p.getContrato().getLote().getNumeroLote() : "N/A";
                table.addCell(new Phrase(lote, normalFont));
                table.addCell(new Phrase(p.getReferencia(), normalFont));
            }
            document.add(table);
            document.close();
        } catch (Exception ex) { ex.printStackTrace(); }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
