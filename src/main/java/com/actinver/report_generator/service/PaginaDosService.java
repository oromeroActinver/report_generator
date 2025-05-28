package com.actinver.report_generator.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import com.actinver.report_generator.dto.CarteraDetalleDTO;
import com.actinver.report_generator.dto.DatosReporteAlphaDTO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PaginaDosService {

	private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

	public void addPortfolioDataPage(Document document, PdfWriter writer, DatosReporteAlphaDTO datosReporte)
			throws DocumentException, IOException {

		try {
			Image image5 = Image.getInstance("src/main/resources/static/image5.png");
			float imgWidth = 140;
			float imgHeight = PageSize.A4.rotate().getHeight() * 0.75f;
			image5.scaleAbsolute(imgWidth, imgHeight);
			image5.setAbsolutePosition(10, PageSize.A4.rotate().getHeight() - imgHeight - 70);
			writer.getDirectContentUnder().addImage(image5);
		} catch (Exception e) {
			System.err.println("Error al cargar image5.png: " + e.getMessage());
		}

		PdfPTable titleTable = new PdfPTable(1);
		titleTable.setWidthPercentage(100); // Ancho completo de la página
		titleTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		titleTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

		// 2. Configurar posición del Circulo vertical exacta
		float circleY = PageSize.A4.rotate().getHeight() - 100; // Tu coordenada Y del círculo
		float circleX = PageSize.A4.rotate().getWidth() / 2 - 70;
		float tableYPosition = circleY - 35; // Ajuste fino para superposición
		titleTable.setSpacingBefore(tableYPosition);

		// 3. Configurar el texto del título
		titleTable.addCell("\n\n\n");
		Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK);
		PdfPCell titleCell = new PdfPCell(new Phrase("Datos generales \ndel portafolio.", titleFont));
		titleCell.setBorder(Rectangle.NO_BORDER);
		titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		titleCell.setPaddingBottom(0);
		titleCell.setPaddingLeft(40);
		// titleCell.setPaddingRight(80);

		// 5. Añadir celda a la tabla
		titleTable.addCell(titleCell);

		// 6. Dibujar el círculo (tus coordenadas exactas)
		PdfContentByte canvas = writer.getDirectContent();

		canvas.saveState();
		canvas.setColorFill(new BaseColor(0x3c, 0xb4, 0xe5));
		float circleRadius = 40; // Radio más pequeño como en tu imagen

		canvas.circle(circleX, circleY, circleRadius);
		canvas.fill();
		canvas.restoreState();

		// 7. Añadir la tabla al documento (el texto aparecerá sobre el círculo)
		document.add(titleTable);
		document.add(new Paragraph("\n"));

		// 3. Triángulo amarillo (sin cambios)
		canvas.saveState();
		canvas.setColorFill(new BaseColor(0xb4, 0xa2, 0x69));
		canvas.moveTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 50);
		canvas.lineTo(PageSize.A4.rotate().getWidth() - 110, PageSize.A4.rotate().getHeight() - 50);
		canvas.lineTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 150);
		canvas.fill();
		canvas.restoreState();

		// 4. Información de contrato en formato lineal (sin tabla)
		Font boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
		Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
		// footer.setSpacingBefore(75);
		Paragraph contractInfo = new Paragraph();
		contractInfo.setSpacingBefore(20);
		contractInfo.setIndentationLeft(400);
		contractInfo.add(new Chunk("Contrato: ", boldFont));
		contractInfo.add(new Chunk(datosReporte.getContrato() + "\n", normalFont));
		contractInfo.add(new Chunk("Tipo de Solución: ", boldFont));
		contractInfo.add(new Chunk(datosReporte.getEstrategia() + "\n", normalFont));
		contractInfo.add(new Chunk("Fecha de Inicio en Inversión Alpha: ", boldFont));
		contractInfo.add(new Chunk(datosReporte.getFechaInicioAlpha() + "\n\n", normalFont));
		document.add(contractInfo);

		// 5. Gráfico de pastel con tonos azules
		DefaultPieDataset dataset = new DefaultPieDataset();
		/*
		 * dataset.setValue("Corporativos", portfolio.getCorporatePercentage());
		 * dataset.setValue("Fondos Deuda", portfolio.getDebtFundsPercentage());
		 * dataset.setValue("Gubernamental", portfolio.getGovernmentPercentage());
		 * dataset.setValue("Acciones Nac.", portfolio.getNationalStocksPercentage());
		 */
		dataset = getDatosGraficoPastel(datosReporte.getCarteraDetalleDTO());

		JFreeChart chart = ChartFactory.createPieChart("", dataset, false, false, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		// Nuevos colores en tonos azules
		/*
		 * plot.setSectionPaint("Corporativos", new Color(147, 167, 203, 255)); // gris
		 * Claro plot.setSectionPaint("Fondos Deuda", new Color(42, 125, 225, 255)); //
		 * Azul fuerte plot.setSectionPaint("Gubernamental", new Color(165, 197, 237,
		 * 255)); // Azul bajo plot.setSectionPaint("Acciones Nac.", new Color(60, 180,
		 * 237, 255)); // Azul oscuro
		 * 
		 * // Configuración del gráfico plot.setBackgroundPaint(Color.WHITE);
		 * plot.setOutlinePaint(null); chart.setBackgroundPaint(Color.WHITE);
		 * 
		 * // Etiquetas dentro del gráfico plot.setLabelGenerator(new
		 * StandardPieSectionLabelGenerator("{0}\n{2}")); plot.setLabelFont(new
		 * java.awt.Font("Segoe UI", java.awt.Font.BOLD, 7));
		 * plot.setLabelPaint(java.awt.Color.BLACK); // Mejor contraste
		 * 
		 * plot.setLabelBackgroundPaint(null); plot.setLabelShadowPaint(null);
		 * plot.setLabelOutlinePaint(null); plot.setLabelPaint(Color.BLACK);
		 * plot.setSimpleLabels(true); plot.setLabelGap(0.05);
		 * plot.setInteriorGap(0.05);
		 */

		plot = getColoresGraficoPastel(chart, datosReporte.getCarteraDetalleDTO());

		// Tamaño del gráfico
		int width = 240;
		int height = 240;
		ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
		ChartUtils.writeChartAsPNG(chartOutputStream, chart, width, height);
		Image chartImage = Image.getInstance(chartOutputStream.toByteArray());

		// Posición del gráfico
		float chartX = 100;
		float chartY = PageSize.A4.rotate().getHeight() - height - 150;
		chartImage.setAbsolutePosition(chartX, chartY);
		document.add(chartImage);

		// 6. Tabla de composición con margen derecho
		Paragraph tableContainer = new Paragraph();
		tableContainer.setAlignment(Element.ALIGN_RIGHT);
		tableContainer.setIndentationRight(56.7f); // 2cm de margen (56.7pt = 2cm)
		tableContainer.setSpacingBefore(0); // Espacio superior

		PdfPTable compositionTable = new PdfPTable(2);

		// Configuración de márgenes y tamaño
		compositionTable.setWidthPercentage(35); // Ancho relativo
		compositionTable.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alineación derecha
		compositionTable.setSpacingBefore(10); // Espacio superior
		compositionTable.setWidths(new float[] { 2, 1 }); // Proporción de columnas

		// Margen derecho de ~2cm (56.7 puntos = 2cm)
		compositionTable.setSpacingAfter(0);
		// compositionTable.setTotalWidth(PageSize.A4.rotate().getWidth() * 0.4f);
		compositionTable.setTotalWidth(320f);
		compositionTable.setLockedWidth(true);
		compositionTable.setExtendLastRow(false);

		// Fuentes y colores (se mantienen igual)
		Font largeLabelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
		Font largeValueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
		BaseColor blue = new BaseColor(0x04, 0x1e, 0x42);
		BaseColor gray = new BaseColor(0x80, 0x80, 0x80);
		BaseColor lightGray = new BaseColor(240, 240, 240);
		
		generateDatosTabla(datosReporte.getCarteraDetalleDTO(), compositionTable, largeLabelFont, largeValueFont);

		/*addColoredTableCellCentered(compositionTable, "Deuda", String.format("%.2f%%", portfolio.getDebtPercentage()),
				blue, false, largeLabelFont, largeValueFont, BaseColor.WHITE);

		addColoredTableCellCentered(compositionTable, "Corporativos",
				String.format("%.2f%%", portfolio.getCorporatePercentage()), BaseColor.WHITE, false, largeLabelFont,
				largeValueFont);
		addColoredTableCellCentered(compositionTable, "Fondos Deuda",
				String.format("%.2f%%", portfolio.getDebtFundsPercentage()), lightGray, false, largeLabelFont,
				largeValueFont);
		addColoredTableCellCentered(compositionTable, "Gubernamental",
				String.format("%.2f%%", portfolio.getGovernmentPercentage()), BaseColor.WHITE, false, largeLabelFont,
				largeValueFont);

		addColoredTableCellCentered(compositionTable, "Acciones",
				String.format("%.2f%%", portfolio.getStocksPercentage()), gray, false, largeLabelFont, largeValueFont,
				BaseColor.WHITE);

		addColoredTableCellCentered(compositionTable, "Acciones Nacionales",
				String.format("%.2f%%", portfolio.getNationalStocksPercentage()), BaseColor.WHITE, false,
				largeLabelFont, largeValueFont);*/

		tableContainer.add(compositionTable);
		document.add(tableContainer);

		// 7. Imagen6 visible y bien posicionada
		try {
			Image image6 = Image.getInstance("src/main/resources/static/image6.png");
			image6.scaleToFit(180, 180);
			image6.setAbsolutePosition(PageSize.A4.rotate().getWidth() - image6.getScaledWidth() - 10, 80);
			document.add(image6);
		} catch (Exception e) {
			System.err.println("Error al cargar image6.png: " + e.getMessage());
		}

		// Pie de página
		Paragraph footer = new Paragraph("Página 2 de 6", NORMAL_FONT);
		footer.setAlignment(Element.ALIGN_CENTER);
		footer.setSpacingBefore(75);
		document.add(footer);
	}

	// Nuevo método para celdas centradas
	private void addColoredTableCellCentered(PdfPTable table, String content1, String content2, BaseColor bgColor,
			boolean isHeader, Font labelFont, Font valueFont) {
		addColoredTableCellCentered(table, content1, content2, bgColor, isHeader, labelFont, valueFont,
				BaseColor.BLACK);
	}

	// Método auxiliar modificado para forzar el color del texto
	private void addColoredTableCellCentered(PdfPTable table, String content1, String content2, BaseColor bgColor,
			boolean isHeader, Font labelFont, Font valueFont, BaseColor textColor) {

		// Crear nuevas instancias de Font para evitar modificar las originales
		Font whiteLabelFont = new Font(labelFont);
		Font whiteValueFont = new Font(valueFont);

		// Forzar el color del texto
		whiteLabelFont.setColor(textColor);
		whiteValueFont.setColor(textColor);

		PdfPCell cell1 = new PdfPCell(new Phrase(content1, whiteLabelFont));
		PdfPCell cell2 = new PdfPCell(new Phrase(content2, whiteValueFont));

		cell1.setMinimumHeight(10f); // o 10f, etc.
		cell1.setBackgroundColor(bgColor);
		cell2.setBackgroundColor(bgColor);
		cell1.setBorder(Rectangle.NO_BORDER);
		cell2.setBorder(Rectangle.NO_BORDER);
		cell1.setPadding(4);
		cell2.setPadding(4);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(cell1);
		table.addCell(cell2);
	}

	/*
	 * private DefaultPieDataset getDatosGraficoPastel(Map<String, Double>
	 * datosPortafilio) {
	 * 
	 * DefaultPieDataset dataset = new DefaultPieDataset();
	 * dataset.setValue("Corporativos", portfolio.getCorporatePercentage());
	 * dataset.setValue("Fondos Deuda", portfolio.getDebtFundsPercentage());
	 * dataset.setValue("Gubernamental", portfolio.getGovernmentPercentage());
	 * dataset.setValue("Acciones Nac.", portfolio.getNationalStocksPercentage());
	 * 
	 * 
	 * return dataset; }
	 */

	private DefaultPieDataset getDatosGraficoPastel(List<CarteraDetalleDTO> carteraDetalleDTO) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		if (!carteraDetalleDTO.isEmpty()) {
			for (CarteraDetalleDTO entry : carteraDetalleDTO) {
				dataset.setValue(entry.getDescripcion(), entry.getPorcentaje());
				//dataset.setValue(entry.getKey(), entry.getValue());
			}
		} else {
			dataset.setValue("SIN DATOS", 0);
		}

		return dataset;
	}

	private PiePlot getColoresGraficoPastel(JFreeChart chart, List<CarteraDetalleDTO> carteraDetalleDTO) {
		PiePlot plot = (PiePlot) chart.getPlot();

		// Lista de colores predefinidos
		List<Color> colores = Arrays.asList(new Color(147, 167, 203, 255), // gris claro
				new Color(42, 125, 225, 255), // azul fuerte
				new Color(165, 197, 237, 255), // azul bajo
				new Color(60, 180, 237, 255), // azul oscuro
				new Color(255, 204, 102, 255), // amarillo
				new Color(204, 102, 255, 255), // morado
				new Color(102, 255, 178, 255), // verde menta
				new Color(255, 153, 153, 255) // rosado
		);

		int index = 0;
		for (CarteraDetalleDTO categoria : carteraDetalleDTO) {
			Color color = colores.get(index % colores.size());
			plot.setSectionPaint(categoria.getDescripcion(), color);
			index++;
		}

		// Configuración visual del gráfico
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		chart.setBackgroundPaint(Color.WHITE);

		// Etiquetas
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}\n{2}"));
		plot.setLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 7));
		plot.setLabelPaint(Color.BLACK);
		plot.setLabelBackgroundPaint(null);
		plot.setLabelShadowPaint(null);
		plot.setLabelOutlinePaint(null);
		plot.setSimpleLabels(true);
		plot.setLabelGap(0.05);
		plot.setInteriorGap(0.05);

		return plot;
	}

	private void generateDatosTabla(List<CarteraDetalleDTO> carteraDetalleDTO, PdfPTable compositionTable,
			Font largeLabelFont, Font largeValueFont) {

		BaseColor blue = new BaseColor(0x04, 0x1e, 0x42);
		BaseColor gray = new BaseColor(0x80, 0x80, 0x80);
		BaseColor lightGray = new BaseColor(240, 240, 240);

		double deuda = 0;
		double acciones = 0;
		Map<String, Double> deudaList = new HashMap<>();
		Map<String, Double> accionesList = new HashMap<>();

		for (CarteraDetalleDTO entry : carteraDetalleDTO) {
			String nombre = entry.getDescripcion();
			double valor = entry.getPorcentaje();

			if (nombre.contains("Acciones") || nombre.contains("Fondos de R. V.")) {
				acciones = acciones + valor;
				accionesList.put(nombre, valor);
			} else {
				deuda = deuda + valor;
				deudaList.put(nombre, valor);
			}
		}

		addColoredTableCellCentered(compositionTable, "Deuda", String.format("%.2f%%", deuda), blue, false,
				largeLabelFont, largeValueFont, BaseColor.WHITE);

		for (Map.Entry<String, Double> entryDeuda : deudaList.entrySet()) {
			String nombre = entryDeuda.getKey();
			String valor = String.format("%.2f%%", entryDeuda.getValue());

			addColoredTableCellCentered(compositionTable, nombre, valor, BaseColor.WHITE,
					false, largeLabelFont, largeValueFont);
		}

		addColoredTableCellCentered(compositionTable, "Acciones", String.format("%.2f%%", acciones), gray, false,
				largeLabelFont, largeValueFont, BaseColor.WHITE);

		for (Map.Entry<String, Double> entryAcc : accionesList.entrySet()) {
			String nombre = entryAcc.getKey();
			String valor = String.format("%.2f%%", entryAcc.getValue());

			addColoredTableCellCentered(compositionTable, nombre, valor, BaseColor.WHITE,
					false, largeLabelFont, largeValueFont);
		}

	}

}
