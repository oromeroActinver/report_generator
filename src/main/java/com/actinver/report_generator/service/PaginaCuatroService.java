package com.actinver.report_generator.service;
 
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
 
import org.springframework.stereotype.Service;
 
import com.actinver.report_generator.dto.DatosMovimientosDTO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
 
@Service
public class PaginaCuatroService {
 
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final BaseColor COLOR_FILA_GRIS = new BaseColor(240, 240, 240);
    private static final Map<Integer, String> NOMBRES_MESES = new HashMap<>();
 
    static {
        NOMBRES_MESES.put(1, "Enero");
        NOMBRES_MESES.put(2, "Febrero");
        NOMBRES_MESES.put(3, "Marzo");
        NOMBRES_MESES.put(4, "Abril");
        NOMBRES_MESES.put(5, "Mayo");
        NOMBRES_MESES.put(6, "Junio");
        NOMBRES_MESES.put(7, "Julio");
        NOMBRES_MESES.put(8, "Agosto");
        NOMBRES_MESES.put(9, "Septiembre");
        NOMBRES_MESES.put(10, "Octubre");
        NOMBRES_MESES.put(11, "Noviembre");
        NOMBRES_MESES.put(12, "Diciembre");
    }
 
    public boolean addDepositsWithdrawalsPage(Document document, PdfWriter writer,
            List<DatosMovimientosDTO> datosMovimientosDTO, String anual) {
        try {
            // Imagen de fondo
            try {
                Image image5 = Image.getInstance("src/main/resources/static/image5.png");
                float imgWidth = 140;
                float imgHeight = PageSize.A4.rotate().getHeight() * 0.75f;
                image5.scaleAbsolute(imgWidth, imgHeight);
                image5.setAbsolutePosition(15, PageSize.A4.rotate().getHeight() - imgHeight - 70);
                writer.getDirectContentUnder().addImage(image5);
            } catch (Exception e) {
                System.err.println("Error al cargar image5.png: " + e.getMessage());
            }
 
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            titleTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            titleTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
 
            float circleY = PageSize.A4.rotate().getHeight() - 120;
            float tableYPosition = circleY - 35;
            titleTable.setSpacingBefore(tableYPosition);
 
            titleTable.addCell("\n\n\n\n\n");
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK);
            PdfPCell titleCell = new PdfPCell(new Phrase("Depósitos y Retiros " + anual, titleFont));
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            titleCell.setPaddingLeft(200);
            titleCell.setPaddingBottom(0);
            titleTable.addCell(titleCell);
 
            PdfContentByte canvas = writer.getDirectContent();
            canvas.saveState();
            canvas.setColorFill(new BaseColor(0x3c, 0xb4, 0xe5));
            float circleX = PageSize.A4.rotate().getWidth() / 2 - 200;
            float circleRadius = 40;
            canvas.circle(circleX, circleY, circleRadius);
            canvas.fill();
            canvas.restoreState();
            document.add(titleTable);
 
            canvas.saveState();
            canvas.setColorFill(new BaseColor(0xb4, 0xa2, 0x69));
            canvas.moveTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 50);
            canvas.lineTo(PageSize.A4.rotate().getWidth() - 110, PageSize.A4.rotate().getHeight() - 50);
            canvas.lineTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 150);
            canvas.fill();
            canvas.restoreState();
 
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
 
            // Procesar datos
            Map<Integer, Double> depositosPorMes = new HashMap<>();
            Map<Integer, Double> retirosPorMes = new HashMap<>();
 
            for (DatosMovimientosDTO dto : datosMovimientosDTO) {
                if ("D".equalsIgnoreCase(dto.getTipo())) {
                    depositosPorMes.merge(dto.getMes(), dto.getImporte(), Double::sum);
                } else if ("R".equalsIgnoreCase(dto.getTipo())) {
                    retirosPorMes.merge(dto.getMes(), dto.getImporte(), Double::sum);
                }
            }
 
            // Configurar colores
            BaseColor colorFondoDepositos = new BaseColor(4, 30, 66);
            BaseColor colorTextoDepositos = new BaseColor(198, 183, 132);
            BaseColor colorFondoRetiros = new BaseColor(198, 183, 132);
            BaseColor colorTextoRetiros = new BaseColor(4, 30, 66);
 
            // Calcular totales
            double totalDepositos = depositosPorMes.values().stream().mapToDouble(Double::doubleValue).sum();
            double totalRetiros = retirosPorMes.values().stream().mapToDouble(Double::doubleValue).sum();
 
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
 
            // Crear tablas individuales
            PdfPTable tablaDepositos = new PdfPTable(2);
            PdfPTable tablaRetiros = new PdfPTable(2);
            
            // Configurar anchos de columnas
            float[] columnWidths = {40f, 40f};
            tablaDepositos.setWidths(columnWidths);
            tablaRetiros.setWidths(columnWidths);
 
            // Agregar encabezados
            addStyledCell(tablaDepositos, "Depósitos", colorFondoDepositos, colorTextoDepositos, true);
            addStyledCell(tablaDepositos, formatter.format(totalDepositos), colorFondoDepositos, colorTextoDepositos, true);
            
            addStyledCell(tablaRetiros, "Retiros", colorFondoRetiros, colorTextoRetiros, true);
            addStyledCell(tablaRetiros, formatter.format(totalRetiros), colorFondoRetiros, colorTextoRetiros, true);
 
            // Agregar datos de depósitos
            Set<Map.Entry<Integer, Double>> depositosOrdenados = new TreeSet<>(Comparator.comparing(Map.Entry::getKey));
            depositosOrdenados.addAll(depositosPorMes.entrySet());
            
            boolean alternarDep = false;
            for (Map.Entry<Integer, Double> entry : depositosOrdenados) {
                String mes = NOMBRES_MESES.get(entry.getKey());
                String monto = formatter.format(entry.getValue());
                addDataRowSimple(tablaDepositos, mes, monto, alternarDep);
                alternarDep = !alternarDep;
            }
 
            // Agregar datos de retiros
            Set<Map.Entry<Integer, Double>> retirosOrdenados = new TreeSet<>(Comparator.comparing(Map.Entry::getKey));
            retirosOrdenados.addAll(retirosPorMes.entrySet());
            
            boolean alternarRet = false;
            for (Map.Entry<Integer, Double> entry : retirosOrdenados) {
                String mes = NOMBRES_MESES.get(entry.getKey());
                String monto = formatter.format(entry.getValue());
                addDataRowSimple(tablaRetiros, mes, monto, alternarRet);
                alternarRet = !alternarRet;
            }
 
            // Configurar posicionamiento absoluto para las tablas
            float marginLeft = 100;
            float marginRight = 100;
            float spaceBetweenTables = 20;
            float availableWidth = PageSize.A4.rotate().getWidth() - marginLeft - marginRight;
            float tableWidth = (availableWidth - spaceBetweenTables) / 2;
            float tableYPositionD = PageSize.A4.rotate().getHeight() - 200;
 
            // Posicionar tabla de depósitos
            tablaDepositos.setTotalWidth(tableWidth);
            tablaDepositos.writeSelectedRows(0, -1, marginLeft, tableYPositionD, canvas);
 
            // Posicionar tabla de retiros
            tablaRetiros.setTotalWidth(tableWidth);
            tablaRetiros.writeSelectedRows(0, -1, marginLeft + tableWidth + spaceBetweenTables, tableYPositionD, canvas);
 
            // Footer
            float footerY = 70;
            float leftMargin = 30;
            float rightMargin = PageSize.A4.rotate().getWidth() - 30;
 
            Paragraph disclaimer = new Paragraph();
            disclaimer.add(new Chunk("Documento informativo", NORMAL_FONT));
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, disclaimer, leftMargin + 80,
                    footerY, 0);
 
            try {
                Image image6 = Image.getInstance("src/main/resources/static/image6.png");
                image6.scaleToFit(200, 200);
                image6.setAbsolutePosition(rightMargin - image6.getScaledWidth(),
                        footerY - (image6.getScaledHeight() / 2) + 5);
                document.add(image6);
            } catch (Exception e) {
                System.err.println("Error al cargar image6.png: " + e.getMessage());
            }
 
            Paragraph footer = new Paragraph("Página 4 de 6", NORMAL_FONT);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, footer,
                    PageSize.A4.rotate().getWidth() / 2, footerY, 0);
 
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
 
    private void addStyledCell(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor,
            boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setFixedHeight(18f);  // Altura reducida para encabezados
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
     
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, isHeader ? 12 : 11, Font.NORMAL, textColor); // Fuente más pequeña
        cell.setPhrase(new Phrase(text, font));
        table.addCell(cell);
    }
 
    private void addDataRowSimple(PdfPTable table, String mes, String monto, boolean alternar) {
        Font font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK); // Fuente más pequeña
        BaseColor bg = alternar ? COLOR_FILA_GRIS : BaseColor.WHITE;
     
        PdfPCell cellMes = new PdfPCell(new Phrase(mes, font));
        cellMes.setBackgroundColor(bg);
        cellMes.setBorder(Rectangle.NO_BORDER);
        cellMes.setPadding(5);  // Padding reducido
        //cellMes.setFixedHeight(15f);  // Altura fija más pequeña
        cellMes.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellMes);
     
        PdfPCell cellMonto = new PdfPCell(new Phrase(monto, font));
        cellMonto.setBackgroundColor(bg);
        cellMonto.setBorder(Rectangle.NO_BORDER);
        cellMonto.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellMonto.setPadding(5);  // Padding reducido
        //cellMonto.setFixedHeight(15f);  // Altura fija más pequeña
        cellMonto.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellMonto);
    }
} 
 