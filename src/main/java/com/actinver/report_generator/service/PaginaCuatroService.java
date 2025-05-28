package com.actinver.report_generator.service;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.stereotype.Service;

import com.actinver.report_generator.dto.DatosMovimientosDTO;
import com.actinver.report_generator.dto.DatosReporteAlphaDTO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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

	// CUARTA HOJA DEL SLITE ALPHA
	public void addDepositsWithdrawalsPage(Document document, PdfWriter writer, List<DatosMovimientosDTO> datosMovimientosDTO, String anual) throws DocumentException {

		// 1. Imagen5 más grande y mejor posicionada
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
		titleTable.setWidthPercentage(100); // Ancho completo de la página
		titleTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		titleTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

		// 2. Configurar posición vertical exacta
		float circleY = PageSize.A4.rotate().getHeight() - 120; // Tu coordenada Y del círculo
		float tableYPosition = circleY - 35; // Ajuste fino para superposición
		titleTable.setSpacingBefore(tableYPosition);

		// 3. Configurar el texto del título
		titleTable.addCell("\n\n\n\n\n");
		Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK);
		PdfPCell titleCell = new PdfPCell(new Phrase("Depósitos y Retiros " + anual, titleFont));
		titleCell.setBorder(Rectangle.NO_BORDER);
		titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		titleCell.setPaddingLeft(200);
		titleCell.setPaddingBottom(0);

		// 5. Añadir celda a la tabla
		titleTable.addCell(titleCell);

		// 6. Dibujar el círculo (tus coordenadas exactas)
		PdfContentByte canvas = writer.getDirectContent();

		canvas.saveState();
		canvas.setColorFill(new BaseColor(0x3c, 0xb4, 0xe5));
		float circleX = PageSize.A4.rotate().getWidth() / 2 - 200;
		float circleRadius = 40; // Radio más pequeño como en tu imagen

		canvas.circle(circleX, circleY, circleRadius);
		canvas.fill();
		canvas.restoreState();
		// document.add(Chunk.NEWLINE);
		document.add(titleTable);

		// 3. Triángulo amarillo (sin cambios)
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

		// Crear tabla con 4 columnas
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(60);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER); // Eliminar bordes de todas las celdas

		// Definir colores
		BaseColor colorFondoDepositos = new BaseColor(4, 30, 66); // RGBA(4,30,66)
		BaseColor colorTextoDepositos = new BaseColor(198, 183, 132); // RGBA(198,183,132)
		BaseColor colorFondoRetiros = new BaseColor(198, 183, 132); // RGBA(198,183,132)
		BaseColor colorTextoRetiros = new BaseColor(4, 30, 66); // RGBA(4,30,66)
		
		
		
		
		
		double totalDepositos = 0;
	    double totalRetiros = 0;
	    
	    for (DatosMovimientosDTO dato : datosMovimientosDTO) {
	        totalDepositos += dato.getDeposito();
	        totalRetiros += dato.getRetiro();
	    }
	    
	    // Formatear los totales como moneda
	    NumberFormat formatter = NumberFormat.getCurrencyInstance();
	    String totalDepositosStr = formatter.format(totalDepositos);
	    String totalRetirosStr = formatter.format(totalRetiros);
	 
	    // Agregar fila de títulos con los totales calculados
	    addStyledCell(table, "Depósitos", colorFondoDepositos, colorTextoDepositos, true);
	    addStyledCell(table, totalDepositosStr, colorFondoDepositos, colorTextoDepositos, false);
	    addStyledCell(table, "Retiros", colorFondoRetiros, colorTextoRetiros, true);
	    addStyledCell(table, totalRetirosStr, colorFondoRetiros, colorTextoRetiros, false);
	 
	    // Agregar filas de datos dinámicamente
	    for (DatosMovimientosDTO dato : datosMovimientosDTO) {
	        String depositoStr = formatter.format(dato.getDeposito());
	        String retiroStr = formatter.format(dato.getRetiro());
	        addDataRow(table, dato.getMes(), depositoStr, dato.getMes(), retiroStr);
	    }
		

		// Agregar fila de títulos
		/*addStyledCell(table, "Depósitos", colorFondoDepositos, colorTextoDepositos, true);
		addStyledCell(table, "$0.00", colorFondoDepositos, colorTextoDepositos, false);
		addStyledCell(table, "Retiros", colorFondoRetiros, colorTextoRetiros, true);
		addStyledCell(table, "$0.00", colorFondoRetiros, colorTextoRetiros, false);

		// Agregar filas de datos (ejemplo)
		addDataRow(table, "marzo", "$15,000.00", "marzo", "$5,000.00");
		addDataRow(table, "abril", "$20,000.00", "abril", "$7,500.00");*/

		document.add(table);

		document.add(Chunk.NEWLINE);
		// Configuración común
		float footerY = 70; // Altura desde el borde inferior
		float leftMargin = 30; // Margen izquierdo
		float rightMargin = PageSize.A4.rotate().getWidth() - 30; // Margen derecho calculado

		// 1. Mensaje en parte izquierda inferior (posicionamiento absoluto)
		Paragraph disclaimer = new Paragraph();
		disclaimer.add(new Chunk("Documento informativo", NORMAL_FONT));
		ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, disclaimer, leftMargin + 80, footerY,
				0);

		// 2. Imagen en posición derecha (ajustada a la misma altura)
		try {
			Image image6 = Image.getInstance("src/main/resources/static/image6.png");
			image6.scaleToFit(100, 100); // Reduje el tamaño para mejor ajuste
			image6.setAbsolutePosition(rightMargin - image6.getScaledWidth(),
					footerY - (image6.getScaledHeight() / 2) + 5 // Ajuste fino de altura
			);
			document.add(image6);
		} catch (Exception e) {
			System.err.println("Error al cargar image6.png: " + e.getMessage());
		}

		// 3. Pie de página centrado (en la misma línea)
		Paragraph footer = new Paragraph("Página 4 de 6", NORMAL_FONT);
		ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, footer,
				PageSize.A4.rotate().getWidth() / 2, footerY, 0);
	}

	// Método para celdas con estilo
	private void addStyledCell(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor,
			boolean isHeader) {
		PdfPCell cell = new PdfPCell(new Phrase(text));
		cell.setBackgroundColor(backgroundColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(PdfPCell.NO_BORDER);

		Font font = FontFactory.getFont(FontFactory.HELVETICA, isHeader ? 10 : 8, Font.NORMAL, textColor);
		cell.setPhrase(new Phrase(text, font));
		table.addCell(cell);
	}

	// Método para filas de datos
	private void addDataRow(PdfPTable table, String mesDeposito, String montoDeposito, String mesRetiro,
			String montoRetiro) {
		// Celdas con texto negro
		Font blackFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

		PdfPCell cell1 = new PdfPCell(new Phrase(mesDeposito, blackFont));
		PdfPCell cell2 = new PdfPCell(new Phrase(montoDeposito, blackFont));
		PdfPCell cell3 = new PdfPCell(new Phrase(mesRetiro, blackFont));
		PdfPCell cell4 = new PdfPCell(new Phrase(montoRetiro, blackFont));

		// Configurar celdas
		for (PdfPCell cell : new PdfPCell[] { cell1, cell2, cell3, cell4 }) {
			cell.setBorder(PdfPCell.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
	}
	
	// Método para generar la tabla dinámicamente
	/*public void generarTablaDinamica(Table table) {
	    // Calcular totales
	    double totalDepositos = 0;
	    double totalRetiros = 0;
	    
	    for (DatosMovimientosDTO dato : datosMovimientosDTO) {
	        totalDepositos += dato.getDeposito();
	        totalRetiros += dato.getRetiro();
	    }
	    
	    // Formatear los totales como moneda
	    NumberFormat formatter = NumberFormat.getCurrencyInstance();
	    String totalDepositosStr = formatter.format(totalDepositos);
	    String totalRetirosStr = formatter.format(totalRetiros);
	 
	    // Agregar fila de títulos con los totales calculados
	    addStyledCell(table, "Depósitos", colorFondoDepositos, colorTextoDepositos, true);
	    addStyledCell(table, totalDepositosStr, colorFondoDepositos, colorTextoDepositos, false);
	    addStyledCell(table, "Retiros", colorFondoRetiros, colorTextoRetiros, true);
	    addStyledCell(table, totalRetirosStr, colorFondoRetiros, colorTextoRetiros, false);
	 
	    // Agregar filas de datos dinámicamente
	    for (DatosMovimientosDTO dato : datosMovimientosDTO) {
	        String depositoStr = formatter.format(dato.getDeposito());
	        String retiroStr = formatter.format(dato.getRetiro());
	        addDataRow(table, dato.getMes(), depositoStr, dato.getMes(), retiroStr);
	    }
	}*/

}
