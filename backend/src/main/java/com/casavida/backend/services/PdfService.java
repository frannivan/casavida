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
                    + contrato.getLote().getFraccionamiento().getNombre()));
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
}
