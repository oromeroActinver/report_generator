package com.actinver.report_generator.service;

import java.awt.Color;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.actinver.report_generator.dto.DatosReporteAlphaDTO;
import com.actinver.report_generator.model.ClientData;
import com.actinver.report_generator.model.PerformanceData;
import com.actinver.report_generator.model.PortfolioData;
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
public class PdfGenerationService {

	@Autowired
	private PaginaUnoService paginaUnoService;
	@Autowired
	private PaginaDosService paginaDosService;
	@Autowired
	private PaginaTresService paginaTresService;
	@Autowired
	private PaginaCuatroService paginaCuatroService;
	@Autowired
	private PaginaCincoService paginaCincoService;
	@Autowired
	private PaginaSeisService paginaSeisService;

	private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
	private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
	private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private PdfWriter writer;

	public byte[] generateReport(ClientData client, PortfolioData portfolio, PerformanceData performance)
			throws DocumentException, IOException {

		// Cambiar a PageSize.A4.rotate() para orientación horizontal
		Document document = new Document(PageSize.A4.rotate());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// 2. Asigna a la variable de clase usando this.writer
		this.writer = PdfWriter.getInstance(document, outputStream);

		document.open();

		// Página 1 - Portada
		addFirstPage(document, this.writer, client);
		document.newPage();
		// Página 2 - Datos del portafolio
		addPortfolioDataPage(document, this.writer, portfolio);
		document.newPage();
		// Página 3 - Desempeño
		addPerformancePage(document, performance, portfolio);
		document.newPage();
		// Página 4 - Depósitos y retiros
		addDepositsWithdrawalsPage(document);
		document.newPage();
		// Página 5 - Explicación
		addExplanationPage(document);
		document.newPage();
		// Página 6 - Pie de página
		addFooterPage(document);

		document.close();
		return outputStream.toByteArray();
	}

	// HOJA 1
	private void addFirstPage(Document document, PdfWriter writer, ClientData client)
			throws DocumentException, IOException {

		PdfContentByte canvas = writer.getDirectContentUnder();
		canvas.saveState();
		canvas.setColorFill(new BaseColor(0x1a, 0x22, 0x49));

		float imageWidth = PageSize.A4.rotate().getWidth() * 0.6f;
		float fondoHeight = PageSize.A4.rotate().getHeight() * 0.8f;
		float fondoX = PageSize.A4.rotate().getWidth() - imageWidth;
		float fondoY = (PageSize.A4.rotate().getHeight() - fondoHeight) / 2;

		canvas.rectangle(10, 60, imageWidth, fondoHeight);
		canvas.fill();
		canvas.restoreState();

		try {
			Image image1 = Image.getInstance("src/main/resources/static/image1.jpeg");
			image1.scaleAbsolute(imageWidth, fondoHeight);
			float image1X = 325;
			float image1Y = 60;

			image1.setAbsolutePosition(image1X, image1Y);
			document.add(image1);

			try {
				Image image4 = Image.getInstance("src/main/resources/static/image1.png");

				// Tamaño aumentado (25% del ancho de image1)
				float image4Size = imageWidth * 0.8f;
				image4.scaleToFit(image4Size, image4Size);
				// Posición dentro de image1 (esquina inferior derecha)
				float image4X = image1X + imageWidth - image4.getScaledWidth();
				image4.setAbsolutePosition(image4X, image1Y);
				document.add(image4);

			} catch (Exception e) {
				System.err.println("Error al cargar image4.png: " + e.getMessage());
			}
		} catch (Exception e) {
			System.err.println("Error al cargar image1.jpeg: " + e.getMessage());
		}

		try {
			Image image3 = Image.getInstance("src/main/resources/static/image3.png");

			// Tamaño aumentado (20% del ancho de página)
			float logoWidth = PageSize.A4.rotate().getWidth() * 0.3f;
			float logoHeight = logoWidth * 0.20f; // Mantener relación de aspecto ~3:1

			image3.scaleAbsolute(logoWidth, logoHeight);

			float logoX = 40; // Margen izquierdo
			float logoY = 100; // Margen inferior

			image3.setAbsolutePosition(logoX, logoY);
			document.add(image3);
		} catch (Exception e) {
			// Texto alternativo también en posición inferior izquierda
			Paragraph logoText = new Paragraph("ACTINVER\nAsset Management",
					new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK));
			logoText.setAlignment(Element.ALIGN_LEFT);

			// Posicionar el texto en la parte inferior izquierda
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, logoText, 40, 40, // Y position
																										// (from bottom)
					0);
		}

		// 5. Contenido de texto (sobre el fondo azul)
		Font whiteFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);
		Font titleFont = new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD, BaseColor.WHITE);

		// Título principal
		Paragraph title = new Paragraph("Soluciones\nAlpha 2025", titleFont);
		title.setAlignment(Element.ALIGN_LEFT);
		title.setIndentationLeft(20);
		title.setSpacingBefore(100);
		document.add(title);
		document.add(Chunk.NEWLINE);

		// Subtítulo
		Paragraph subtitle = new Paragraph("Dirección de Asset Management", whiteFont);
		subtitle.setIndentationLeft(20);
		document.add(subtitle);

		// Fecha
		Paragraph date = new Paragraph("Fecha de elaboración: 28 de febrero del 2025", whiteFont);
		date.setIndentationLeft(20);
		document.add(date);

		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
	}

	// SEGUNDA HOJA DEL SLITE ALPHA
	private void addPortfolioDataPage(Document document, PdfWriter writer, PortfolioData portfolio)
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
		contractInfo.add(new Chunk(portfolio.getContractNumber() + "\n", normalFont));
		contractInfo.add(new Chunk("Tipo de Solución: ", boldFont));
		contractInfo.add(new Chunk(portfolio.getSolutionType() + "\n", normalFont));
		contractInfo.add(new Chunk("Fecha de Inicio en Inversión Alpha: ", boldFont));
		contractInfo.add(new Chunk(portfolio.getStartDate() + "\n\n", normalFont));
		document.add(contractInfo);

		// 5. Gráfico de pastel con tonos azules
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Corporativos", portfolio.getCorporatePercentage());
		dataset.setValue("Fondos Deuda", portfolio.getDebtFundsPercentage());
		dataset.setValue("Gubernamental", portfolio.getGovernmentPercentage());
		dataset.setValue("Acciones Nac.", portfolio.getNationalStocksPercentage());

		JFreeChart chart = ChartFactory.createPieChart("", dataset, false, false, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		// Nuevos colores en tonos azules
		plot.setSectionPaint("Corporativos", new Color(147, 167, 203, 255)); // gris Claro
		plot.setSectionPaint("Fondos Deuda", new Color(42, 125, 225, 255)); // Azul fuerte
		plot.setSectionPaint("Gubernamental", new Color(165, 197, 237, 255)); // Azul bajo
		plot.setSectionPaint("Acciones Nac.", new Color(60, 180, 237, 255)); // Azul oscuro

		// Configuración del gráfico
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		chart.setBackgroundPaint(Color.WHITE);

		// Etiquetas dentro del gráfico
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}\n{2}"));
		plot.setLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 7));
		plot.setLabelPaint(java.awt.Color.BLACK); // Mejor contraste

		plot.setLabelBackgroundPaint(null);
		plot.setLabelShadowPaint(null);
		plot.setLabelOutlinePaint(null);
		plot.setLabelPaint(Color.BLACK);
		plot.setSimpleLabels(true);
		plot.setLabelGap(0.05);
		plot.setInteriorGap(0.05);

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

		addColoredTableCellCentered(compositionTable, "Deuda", String.format("%.2f%%", portfolio.getDebtPercentage()),
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
				largeLabelFont, largeValueFont);

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

	// TERCER HOJA DEL SLITE ALPHA
	private void addPerformancePage(Document document, PerformanceData performance, PortfolioData portfolio)
			throws DocumentException, IOException {
		// Configuración de fuentes
		Font blueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLUE);

		// Título
		PdfPTable titleTable = new PdfPTable(1);
		titleTable.setWidthPercentage(100); // Ancho completo de la página
		titleTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		titleTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

		// 2. Configurar posición vertical exacta
		float circleY = PageSize.A4.rotate().getHeight() - 70; // Tu coordenada Y del círculo
		float tableYPosition = circleY - 35; // Ajuste fino para superposición
		titleTable.setSpacingBefore(tableYPosition);

		// 3. Configurar el texto del título
		// document.add(new Paragraph("\n\n"));
		titleTable.addCell("\n");
		Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK);
		PdfPCell titleCell = new PdfPCell(new Phrase("Desempeño del Portafolio", titleFont));
		titleCell.setBorder(Rectangle.NO_BORDER);
		titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		titleCell.setPaddingLeft(135);
		titleCell.setPaddingBottom(0);

		// 5. Añadir celda a la tabla
		titleTable.addCell(titleCell);

		// 6. Dibujar el círculo (tus coordenadas exactas)
		PdfContentByte canvas = writer.getDirectContent();

		canvas.saveState();
		canvas.setColorFill(new BaseColor(0x3c, 0xb4, 0xe5));
		float circleX = PageSize.A4.rotate().getWidth() / 2 - 240;
		// float circleY = PageSize.A4.rotate().getHeight() - 110;
		float circleRadius = 40; // Radio más pequeño como en tu imagen

		canvas.circle(circleX, circleY, circleRadius);
		canvas.fill();
		canvas.restoreState();
		// document.add(Chunk.NEWLINE);
		document.add(titleTable);

		// 1. Imagen7 más pequeña en superior izquierda
		try {
			Image image7 = Image.getInstance("src/main/resources/static/image7.png");
			// Escalar al 200% del tamaño original
			image7.scalePercent(35f); // 100% sería tamaño original
			image7.setAbsolutePosition(10, PageSize.A4.rotate().getHeight() - image7.getScaledHeight() - 30);
			writer.getDirectContentUnder().addImage(image7);
		} catch (Exception e) {
			System.err.println("Error al cargar image7.png: " + e.getMessage());
		}

		PdfPTable tableInvercion = new PdfPTable(1);
		// Ajusta el ancho para que no sea el 100% (por ejemplo, 50% o el ancho
		// necesario)
		tableInvercion.setWidthPercentage(27);
		tableInvercion.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alineación derecha
		tableInvercion.setSpacingAfter(20);
		tableInvercion.setSpacingBefore(0);
		tableInvercion.setSpacingAfter(0);

		Paragraph assetsInfo = new Paragraph();
		assetsInfo.add(new Chunk("Activo al Inicio de Inversión Alpha:\n", NORMAL_FONT));
		assetsInfo.add(new Chunk("$ 3,106,586.46\n\n", BOLD_FONT));
		assetsInfo.add(new Chunk("Activo al Final del Periodo:\n", NORMAL_FONT));
		assetsInfo.add(new Chunk("$ 4,690,198.13", BOLD_FONT));
		assetsInfo.setAlignment(Element.ALIGN_LEFT);
		// assetsInfo.setIndentationLeft(480);

		PdfPCell cell = new PdfPCell(assetsInfo);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setBackgroundColor(new BaseColor(242, 242, 242, 255)); // Gris claro
		cell.setPaddingLeft(480);
		cell.setPadding(5); // Padding uniforme de 10pt
		cell.setIndent(10); // Sangría del texto dentro de la celda
		cell.setMinimumHeight(0); // Evita altura excesiva
		cell.setUseAscender(true); // Mejor alineación vertical

		tableInvercion.addCell(cell);
		document.add(tableInvercion);

		// Gráfica con colores personalizados
		JFreeChart chart = createPerformanceChart(performance);
		// int width = 500;
		// int height = 250;
		int width = 600; // Aumenta la resolución
		int height = 380;
		ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
		org.jfree.chart.ChartUtils.writeChartAsPNG(chartOutputStream, chart, width, height);
		Image chartImage = Image.getInstance(chartOutputStream.toByteArray());
		chartImage.scaleToFit(500, 250);
		chartImage.setAlignment(Element.ALIGN_CENTER);
		document.add(chartImage);

		int numColumns = 5;// valores.size(); o encabezados.size()
		float columnWidth = 60;
		float totalTableWidth = numColumns * columnWidth;

		PdfPTable table = new PdfPTable(numColumns); // 5 columnas para el nuevo formato
		// table.setWidthPercentage(90);
		table.setTotalWidth(totalTableWidth);
		table.setLockedWidth(true);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER); // Quitar bordes por defecto

		PdfPCell headerCell = new PdfPCell();
		headerCell.setBackgroundColor(new BaseColor(198, 183, 132, 255)); // Gris claro
		headerCell.setBorder(PdfPCell.NO_BORDER);
		headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

		// Estilo para la fila de Portafolio (blanco sin bordes)
		PdfPCell portafolioCell = new PdfPCell();
		portafolioCell.setBackgroundColor(BaseColor.WHITE);
		portafolioCell.setBorder(PdfPCell.NO_BORDER);
		portafolioCell.setHorizontalAlignment(Element.ALIGN_CENTER);

		// Estilo para la fila de BenchMark (gris sin bordes)
		PdfPCell benchmarkCell = new PdfPCell();
		benchmarkCell.setBackgroundColor(new BaseColor(240, 240, 240)); // Gris más claro
		benchmarkCell.setBorder(PdfPCell.NO_BORDER);
		benchmarkCell.setHorizontalAlignment(Element.ALIGN_CENTER);

		Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
		Font fontContenido = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);

		// Encabezados
		table.addCell(createStyledCell(" ", fontEncabezado, new BaseColor(198, 183, 132)));
		table.addCell(createStyledCell("2024", fontEncabezado, new BaseColor(198, 183, 132)));
		table.addCell(createStyledCell("Enero", fontEncabezado, new BaseColor(198, 183, 132)));
		table.addCell(createStyledCell("Febrero", fontEncabezado, new BaseColor(198, 183, 132)));
		table.addCell(createStyledCell("En el Año", fontEncabezado, new BaseColor(198, 183, 132)));

		// Fila Portafolio (blanco)
		table.addCell(createStyledCell("Portafolio *", fontContenido, BaseColor.WHITE));
		table.addCell(createStyledCell(String.format("%.2f%%", 0.1225 * 100), fontContenido, BaseColor.WHITE));
		table.addCell(createStyledCell(String.format("%.2f%%", 0.0255 * 100), fontContenido, BaseColor.WHITE));
		table.addCell(createStyledCell(String.format("%.2f%%", -0.247 * 100), fontContenido, BaseColor.WHITE));
		table.addCell(createStyledCell(String.format("%.2f%%", 0.1760 * 100), fontContenido, BaseColor.WHITE));

		// Fila BenchMark (gris claro)
		BaseColor grisClaro = new BaseColor(240, 240, 240);
		table.addCell(createStyledCell("BenchMark", fontContenido, grisClaro));
		table.addCell(createStyledCell(String.format("%.2f%%", 0.0164 * 100), fontContenido, grisClaro));
		table.addCell(createStyledCell(String.format("%.2f%%", -0.0361 * 100), fontContenido, grisClaro));
		table.addCell(createStyledCell(String.format("%.2f%%", -0.0644 * 100), fontContenido, grisClaro));
		table.addCell(createStyledCell(String.format("%.2f%%", 0.1077 * 100), fontContenido, grisClaro));

		table.addCell("\n");
		document.add(table);
		document.add(Chunk.NEWLINE);

		// 3. Mensaje en parte izquierda inferior
		Paragraph disclaimer1 = new Paragraph();
		disclaimer1.add(new Chunk(
				"* El rendimiento del portafolio es anualizado, neto, después de costos e impuestos.\n", NORMAL_FONT));
		// disclaimer.add(new Chunk("Documento informativo", NORMAL_FONT));
		disclaimer1.setAlignment(Element.ALIGN_LEFT);
		disclaimer1.setSpacingBefore(10);
		document.add(disclaimer1);

		// Configuración común
		float footerY = 70; // Altura desde el borde inferior
		float leftMargin = 30; // Margen izquierdo
		float rightMargin = PageSize.A4.rotate().getWidth() - 30; // Margen derecho calculado

		// 1. Mensaje en parte izquierda inferior (posicionamiento absoluto)
		Paragraph disclaimer = new Paragraph();
		disclaimer.add(new Chunk("Documento informativo", NORMAL_FONT));
		ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, disclaimer, leftMargin, footerY, 0);

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

	private PdfPCell createStyledCell(String text, Font font, BaseColor bgColor) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBackgroundColor(bgColor);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(5);
		return cell;
	}

	private JFreeChart createPerformanceChart(PerformanceData performance) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// Añadir datos (orden Benchmark primero, Portafolio después para que se dibuje
		// encima)
		String[] categorias = { "2024", "Enero", "Febrero", "En el Año" };
		for (String cat : categorias) {
			dataset.addValue(performance.getBenchmarkMonthlyReturns().get(cat), "Benchmark", cat);
			dataset.addValue(performance.getPortfolioMonthlyReturns().get(cat), "Portafolio", cat);

			// dataset.addValue(performance.getBenchmarkMonthlyReturns().get(cat) * 100,
			// "Benchmark", cat);
			// dataset.addValue(performance.getPortfolioMonthlyReturns().get(cat) * 100,
			// "Portafolio", cat);
		}

		// Crear gráfico de barras sin 3D
		JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, true, true,
				false);

		CategoryPlot plot = chart.getCategoryPlot();

		// Estilo de fondo
		chart.setBackgroundPaint(Color.WHITE);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

		// Eje Y como porcentaje
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// rangeAxis.setNumberFormatOverride(new DecimalFormat("#0.00%"));
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.00%"));
		// rangeAxis.setLowerBound(0); // desde 0%
		rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

		// Eje X
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryMargin(0.2);

		// Configurar leyenda
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.BOTTOM);

		// Configurar renderizador
		BarRenderer renderer = new BarRenderer() {
			@Override
			public Paint getItemPaint(int row, int column) {
				if (row == 0)
					return new Color(60, 180, 229); // Benchmark - azul claro
				else
					return new Color(4, 30, 66); // Portafolio - azul oscuro
			}
		};

		renderer.setMaximumBarWidth(0.05); // 🔹 Ajuste de ancho de barra
		renderer.setItemMargin(-0.4);

		// Colores personalizados
		renderer.setSeriesPaint(0, new Color(60, 180, 229)); // Azul claro para Benchmark
		renderer.setSeriesPaint(1, new Color(4, 30, 66)); // Azul oscuro para Portafolio

		// Mostrar etiquetas SOLO para Portafolio (row 1)
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setDefaultItemLabelFont(new java.awt.Font("SansSerif", Font.NORMAL, 10));
		renderer.setDefaultItemLabelPaint(Color.BLACK);
		renderer.setDefaultPositiveItemLabelPosition(
				new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));

		// Mostrar etiquetas solo en la serie "Portafolio" (índice 1)
		renderer.setSeriesItemLabelsVisible(0, false); // Benchmark
		renderer.setSeriesItemLabelsVisible(1, true); // Portafolio
		renderer.setSeriesItemLabelGenerator(1,
				new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.00'%'")));
		renderer.setShadowVisible(false);// para elimar renderizados raros
		renderer.setDrawBarOutline(false);

		// Eliminar "brillo" o efectos 3D
		renderer.setBarPainter(new StandardBarPainter());

		plot.setRenderer(renderer);
		plot.setRangeZeroBaselineVisible(true);
		plot.setOutlineVisible(false);

		plot.setRangeZeroBaselinePaint(Color.BLACK); // o el color que gustes

		return chart;
	}

	// CUARTA HOJA DEL SLITE ALPHA
	private void addDepositsWithdrawalsPage(Document document) throws DocumentException {

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
		PdfPCell titleCell = new PdfPCell(new Phrase("Depósitos y Retiros 2025", titleFont));
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

		// Agregar fila de títulos
		addStyledCell(table, "Depósitos", colorFondoDepositos, colorTextoDepositos, true);
		addStyledCell(table, "$0.00", colorFondoDepositos, colorTextoDepositos, false);
		addStyledCell(table, "Retiros", colorFondoRetiros, colorTextoRetiros, true);
		addStyledCell(table, "$0.00", colorFondoRetiros, colorTextoRetiros, false);

		// Agregar filas de datos (ejemplo)
		addDataRow(table, "marzo", "$15,000.00", "marzo", "$5,000.00");
		addDataRow(table, "abril", "$20,000.00", "abril", "$7,500.00");

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

	private void addExplanationPage(Document document) throws DocumentException {

		// 1. Imagen5 más grande y mejor posicionada
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

		document.add(new Paragraph("\n"));
		Paragraph title = new Paragraph("Solución Patrimonial:", SUBTITLE_FONT);
		title.setIndentationLeft(90);
		title.setSpacingBefore(40f);
		document.add(title);

		// 3. Triángulo amarillo (sin cambios)
		PdfContentByte canvas = writer.getDirectContent();
		canvas.saveState();
		canvas.setColorFill(new BaseColor(0xb4, 0xa2, 0x69));
		canvas.moveTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 50);
		canvas.lineTo(PageSize.A4.rotate().getWidth() - 110, PageSize.A4.rotate().getHeight() - 50);
		canvas.lineTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 150);
		canvas.fill();
		canvas.restoreState();

		// document.add(Chunk.NEWLINE);

		String text = "Continuamos este año como se esperaba con un alto grado de incertidumbre y nerviosismo a lo largo y ancho de todos los mercados derivado de la llegada de Trump a la administración de EE.UU. Esto se deriva principalmente de el 'estira y afloja' con la guerra arancelaria para diversas economías como herramienta de negociación para alcanzar ciertas metas en la agenda de Donald Trump. En este sentido todos los activos tanto de renta fija como de variable han tenido un alto nivel de volatilidad lo cual ha llevado a la recomposición de las carteras para posicionarlas más cautas entre este entorno. Los principales activos beneficiados han sido los de renta fija en donde el inversionista se ha refugiado y por lo tanto han tenido un recorrido a la baja interesante. El peso mexicano si bien de principio a fin del mes no tuvo cambios significativos si experimento una alta volatilidad detonado por la primera casi implementación de los aranceles a principios de mes y nuevamente cerrando este. Por otro lado tuvimos decisión de política monetaria en EE.UU. en donde se mantuvo sin cambios haciendo referencia en donde se sigue la lucha para llevar la inflación hacia niveles objetivos del banco central. En México a diferencia de EE.UU. de tomo la decisión de recortar en 50bps la tasa de referencia para llevarla a 9.5% argumentando una tasa real ex-ante elevada y una inflación contenida dentro del rango de tolerancia de Banco de México.\n\n"
				+ "Nosotros en la Solución Patrimonial si bien este año tiene cierto grado de incertidumbre sobre los recortes esperados por parte de la Fed así como de Banxico, si se han tomado las decisiones correctas para capturar el mejor rendimiento posible entregando a nuestros clientes un rendimiento de 14.1%. Así mismo, con la estrategia que tenemos actualmente y viendo hacia este 2025, apunta a continuar con excelentes rendimientos, así como intereses altos que podemos entregar a nuestros clientes. La estrategia se enfoca en Cetes menores a 1 año con un premio derivado de nuestro escenario central que se orienta en la continuidad de normalización de tasas por parte de los bancos centrales que será benéfico para el rendimiento de los portafolios. Además del posicionamiento en Renta Fija también la valuación de la Bolsa Mexicana de valores muestra oportunidades claves para capturar aún más rendimientos para nuestros clientes. Con esto continua la impecable tendencia histórica a pesar de los diversos escenarios a los cuales se ha enfrentado durante los últimos años. No obstante, nos mantenemos atentos al desarrollo de la economía a nivel global y local como la toma de la presidencia de EE.UU. por Donald Trump y algunas reformas en México con el fin de capitalizar oportunidades que pudiesen existir.\n\n"
				+ "Como se esperaba este 2025 estará lleno de retos y a su vez de oportunidades. Sin embargo, la cautela es lo que debe de prevalecer y una cartera bien diversificada para hacer frente al entorno actual. Si bien en lo general los datos económicos se siguen viendo saludables existen ciertos datos que se han moderado y pudiesen indicar una economía que empieza a desacelerarse. Con esta visión, continuamos esperando un buen 2025 aunque sin duda será turbulento pero lleno de oportunidades las cuales estaremos trabajando en Soluciones Alpha para capturarlas y continuar dando los resultados consistentes a nuestros clientes.";

		Font testFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
		Paragraph content = new Paragraph(text, testFont);
		content.setAlignment(Element.ALIGN_JUSTIFIED);
		content.setIndentationLeft(85);
		content.setIndentationRight(30);
		document.add(content);

		// 3. Mensaje en parte izquierda inferior
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

	private void addFooterPage(Document document) throws DocumentException {
		// Configuración de márgenes
		float horizontalMargin = 10; // 10pt de margen izquierdo/derecho
		float topMargin = 60; // Margen superior más grande
		float bottomMargin = 60; // Margen inferior más grande

		// 1. Fondo que cubre la página con márgenes personalizados
		PdfContentByte canvas = writer.getDirectContentUnder();
		canvas.saveState();
		canvas.setColorFill(new BaseColor(28, 33, 73)); // Color de fondo RGB(28,33,73)

		// Dimensiones del rectángulo con márgenes personalizados
		float rectWidth = document.getPageSize().getWidth() - (2 * horizontalMargin);
		float rectHeight = document.getPageSize().getHeight() - topMargin - bottomMargin;

		canvas.rectangle(horizontalMargin, bottomMargin, rectWidth, rectHeight);
		canvas.fill();
		canvas.restoreState();

		// Color blanco para el texto
		BaseColor whiteColor = new BaseColor(255, 255, 255);

		// 2. Imagen4 (800x300) en esquina inferior derecha
		try {
			Image image4 = Image.getInstance("src/main/resources/static/image1.png");
			image4.scaleToFit(380, 245);

			// Posicionamiento en esquina inferior derecha
			float image4X = document.getPageSize().getWidth() - image4.getScaledWidth() - horizontalMargin;
			float image4Y = bottomMargin;

			image4.setAbsolutePosition(image4X, image4Y);
			document.add(image4);
		} catch (Exception e) {
			System.err.println("Error al cargar image4.png: " + e.getMessage());
		}

		try {
			Image image8 = Image.getInstance("src/main/resources/static/image8.png");
			image8.scaleToFit(487.9f, 103.9f);

			// Centrado perfecto en toda la página
			float image8X = (document.getPageSize().getWidth() - image8.getScaledWidth()) / 2;
			float image8Y = (document.getPageSize().getHeight() - image8.getScaledHeight()) / 2;

			image8.setAbsolutePosition(image8X, image8Y);
			document.add(image8);
		} catch (Exception e) {
			System.err.println("Error al cargar image8.png: " + e.getMessage());
			// Fallback en texto
			Font whiteFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, whiteColor);
			Paragraph title = new Paragraph("Actinver\nAsset Management", whiteFont);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingBefore(document.getPageSize().getHeight() * 0.4f);
			document.add(title);
		}

		// Email con margen derecho de 60pt
		Font whiteSmallFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, whiteColor);
		Paragraph email = new Paragraph("inversionalpha@actinver.com.mx", whiteSmallFont);
		email.setAlignment(Element.ALIGN_CENTER);
		email.setSpacingBefore(400f);
		email.setIndentationRight(60); // Margen derecho de 60pt
		document.add(email);

		// Disclaimer con márgenes laterales
		Font whiteItalicFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, whiteColor);
		String disclaimer = "El presente documento es de carácter informativo, no constituye un comprobante fiscal, ni un estado de cuenta. "
				+ "Los rendimientos calculados para este periodo son aquellos que refleja el estado de cuenta para el periodo correspondiente, "
				+ "los cuales no constituyen una garantía de rendimientos futuros de su inversión.";

		Paragraph disclaimerParagraph = new Paragraph(disclaimer, whiteItalicFont);
		disclaimerParagraph.setAlignment(Element.ALIGN_CENTER);
		disclaimerParagraph.setSpacingBefore(15f);
		disclaimerParagraph.setIndentationLeft(60); // Margen izquierdo de 60pt
		disclaimerParagraph.setIndentationRight(70); // Margen derecho de 60pt
		document.add(disclaimerParagraph);
	}

	public byte[] generarReporte(DatosReporteAlphaDTO datosReporte) throws DocumentException {
		// Cambiar a PageSize.A4.rotate() para orientación horizontal
		Document document = new Document(PageSize.A4.rotate());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// 2. Asigna a la variable de clase usando this.writer
		this.writer = PdfWriter.getInstance(document, outputStream);

		try {
			document.open();
			paginaUnoService.addFirstPage(document, this.writer);
			document.newPage();
			paginaDosService.addPortfolioDataPage(document, this.writer, datosReporte);
			document.newPage();
			paginaTresService.addPerformancePage(document, this.writer, datosReporte);
			document.newPage();
			paginaCuatroService.addDepositsWithdrawalsPage(document, this.writer, datosReporte.getDatosMovimientosDTO(),
					datosReporte.getAnual());
			document.newPage();
			paginaCincoService.addExplanationPage(document, this.writer, datosReporte.getTextSlite5(),
					datosReporte.getEstrategia());
			document.newPage();
			paginaSeisService.addFooterPage(document, this.writer);
			document.close();

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outputStream.toByteArray();

	}

}
