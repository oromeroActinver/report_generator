package com.actinver.report_generator.service;

import java.awt.Color;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import com.actinver.report_generator.dto.DatosGraficaDTO;
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
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PaginaTresService {

	private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
	private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
	private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

	// TERCER HOJA DEL SLITE ALPHA
	public void addPerformancePage(Document document, PdfWriter writer, DatosReporteAlphaDTO datosReporte)
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
		assetsInfo.add(new Chunk("$ " + datosReporte.getSaldoInicio() + "\n\n", BOLD_FONT));
		assetsInfo.add(new Chunk("Activo al Final del Periodo:\n", NORMAL_FONT));
		assetsInfo.add(new Chunk("$ " + datosReporte.getSaldoFin(), BOLD_FONT));
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
		JFreeChart chart = createPerformanceChart(datosReporte);
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

	private JFreeChart createPerformanceChart(DatosReporteAlphaDTO datosReporteAlphaDTO) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List<DatosGraficaDTO> datosGrafica = datosReporteAlphaDTO.getDatosGraficaDTO();
		String[] categorias = getCategorias(datosGrafica, datosReporteAlphaDTO.getFechaInicioAlpha(),
				datosReporteAlphaDTO.getFechaFinElaboracion());

		for (String cat : categorias) {
			// Buscar el objeto que coincide con el nombre de la categoría
			for (DatosGraficaDTO dato : datosGrafica) {
				String mes = dato.getMes();

				// Reemplazar "Año Ant" por el año si es necesario
				String categoriaActual = mes.equals("Año Ant")
						? datosReporteAlphaDTO.getFechaInicioAlpha().substring(0, 4)
						: mes;

				if (categoriaActual.equals(cat)) {
					dataset.addValue(0, cat, cat); // Línea base, opcional
					dataset.addValue(dato.getBenchmark() * 100, "Benchmark", cat);
					dataset.addValue(dato.getPortafolio() * 100, "Portafolio", cat);
					break;
				}
			}
		}

		// Añadir datos (orden Benchmark primero, Portafolio después para que se dibuje
		// encima)
		// String[] categorias = getCategorias(datosGrafica,
		// datosReporteAlphaDTO.getFechaInicioAlpha(),
		// datosReporteAlphaDTO.getFechaFinElaboracion());
		/*
		 * for (String cat : categorias) { dataset.addValue(0, cat, cat);
		 * //dataset.addValue(performance.getBenchmarkMonthlyReturns().get(cat),
		 * "Benchmark", cat);
		 * //dataset.addValue(performance.getPortfolioMonthlyReturns().get(cat),
		 * "Portafolio", cat);
		 * 
		 * // dataset.addValue(performance.getBenchmarkMonthlyReturns().get(cat) * 100,
		 * // "Benchmark", cat); //
		 * dataset.addValue(performance.getPortfolioMonthlyReturns().get(cat) * 100, //
		 * "Portafolio", cat); }
		 */

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

	private String[] getCategorias(List<DatosGraficaDTO> datosGraficaDTO, String fechaInicioAlpha,
			String fechaFinElaboracion) {

		// si los años son iguales no agreges la columna del año si es menor agregala
		List<String> categoriasList = new ArrayList<>();
		String anioInicio = fechaInicioAlpha.substring(0, 4);
		String anioElaboracion = fechaFinElaboracion.substring(0, 4);

		for (DatosGraficaDTO datos : datosGraficaDTO) {
			String mes = datos.getMes();

			if ("Desde Inicio".equals(mes)) {
				continue;
			}

			if (!anioInicio.equals(anioElaboracion) && "Año Ant".equals(mes)) {
				categoriasList.add(anioElaboracion);
			}

			categoriasList.add(mes);

		}
		return categoriasList.toArray(new String[0]);

	}

}
