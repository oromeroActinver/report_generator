package com.actinver.report_generator.service;

import java.awt.Color;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
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

	private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
	private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

	// TERCER HOJA DEL SLITE ALPHA
	public Boolean addPerformancePage(Document document, PdfWriter writer, DatosReporteAlphaDTO datosReporte)
			throws DocumentException, IOException {

		try {
			Font blueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLUE);

			// Título
			PdfPTable titleTable = new PdfPTable(1);
			titleTable.setWidthPercentage(100);
			titleTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			titleTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

			// 2. circulo Azul
			float circleY = PageSize.A4.rotate().getHeight() - 70;
			float tableYPosition = circleY - 35;
			titleTable.setSpacingBefore(tableYPosition);

			// 3. Texto del título
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
			float circleRadius = 40;

			canvas.circle(circleX, circleY, circleRadius);
			canvas.fill();
			canvas.restoreState();
			document.add(titleTable);

			try {
				Image image7 = Image.getInstance("src/main/resources/static/image7.png");
				image7.scalePercent(35f);
				image7.setAbsolutePosition(10, PageSize.A4.rotate().getHeight() - image7.getScaledHeight() - 30);
				writer.getDirectContentUnder().addImage(image7);
			} catch (Exception e) {
				System.err.println("Error al cargar image7.png: " + e.getMessage());
			}

			PdfPTable tableInvercion = new PdfPTable(1);
			tableInvercion.setWidthPercentage(27);
			tableInvercion.setHorizontalAlignment(Element.ALIGN_RIGHT);
			tableInvercion.setSpacingAfter(20);
			tableInvercion.setSpacingBefore(0);
			tableInvercion.setSpacingAfter(0);

			DecimalFormat df = new DecimalFormat("#,###.##");
			String activoFinal = df.format(((Number) datosReporte.getSaldoInicio()));
			String activoActual = df.format(((Number) datosReporte.getSaldoFin()));

			Paragraph assetsInfo = new Paragraph();
			assetsInfo.add(new Chunk("Activo al Inicio de Inversión Alpha:\n", NORMAL_FONT));
			assetsInfo.add(new Chunk("$ " + activoFinal + "\n\n", BOLD_FONT));
			assetsInfo.add(new Chunk("Activo al Final del Periodo:\n", NORMAL_FONT));
			assetsInfo.add(new Chunk("$ " + activoActual, BOLD_FONT));
			assetsInfo.setAlignment(Element.ALIGN_LEFT);

			PdfPCell cell = new PdfPCell(assetsInfo);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBackgroundColor(new BaseColor(242, 242, 242, 255));
			cell.setPaddingLeft(480);
			cell.setPadding(5);
			cell.setIndent(10);
			cell.setMinimumHeight(0);
			cell.setUseAscender(true);

			tableInvercion.addCell(cell);
			document.add(tableInvercion);

			JFreeChart chart = createPerformanceChart(datosReporte);

			// Renderizar a mayor resolución (doble del tamaño final)
			int renderWidth = 1000;
			int renderHeight = 500;

			ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
			ChartUtils.writeChartAsPNG(chartOutputStream, chart, renderWidth, renderHeight);

			// Insertar imagen escalada al tamaño deseado
			Image chartImage = Image.getInstance(chartOutputStream.toByteArray());
			chartImage.scaleToFit(500, 250); // Escala visual en el PDF
			chartImage.setAlignment(Element.ALIGN_CENTER);
			document.add(chartImage);

			List<DatosGraficaDTO> datos = datosReporte.getDatosGraficaDTO();

			// Separar los datos
			List<DatosGraficaDTO> datosMensuales = new ArrayList<>();
			DatosGraficaDTO datoEnElAnio = null;
			DatosGraficaDTO datoAnioAnt = null;

			for (DatosGraficaDTO dato : datos) {
				String mes = dato.getMes().toLowerCase();
				if (mes.equals("en el año")) {
					datoEnElAnio = dato;
				} else if (mes.equals("año ant")) {
					datoAnioAnt = dato;
				} else if (!mes.equals("desde inicio")) {
					datosMensuales.add(dato);
				}
			}

			// Ordenar meses cronológicamente
			List<String> ordenMeses = Arrays.asList("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio",
					"agosto", "septiembre", "octubre", "noviembre", "diciembre");
			datosMensuales.sort(Comparator.comparingInt(d -> ordenMeses.indexOf(d.getMes().toLowerCase())));

			// Validación para mostrar columna 2024
			Integer anioInicio = Integer.parseInt(datosReporte.getFechaInicioAlpha().substring(0, 4));
			boolean mostrarAnio2024 = anioInicio < Integer.parseInt(datosReporte.getAnual());
			int numColumns = datosMensuales.size() + 2;
			if (mostrarAnio2024) {
				numColumns++;
			}
			float[] columnWidths = new float[numColumns];
			Arrays.fill(columnWidths, 60f);

			PdfPTable table = new PdfPTable(numColumns);
			table.setWidthPercentage(90);
			table.setWidths(columnWidths);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);

			float columnWidth = 60;
			float totalTableWidth = numColumns * columnWidth;

			table.setTotalWidth(totalTableWidth);
			table.setLockedWidth(true);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

			PdfPCell headerCell = new PdfPCell();
			headerCell.setBackgroundColor(new BaseColor(198, 183, 132, 255));
			headerCell.setBorder(PdfPCell.NO_BORDER);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			// Estilo para la fila de Portafolio (blanco sin bordes)
			PdfPCell portafolioCell = new PdfPCell();
			portafolioCell.setBackgroundColor(BaseColor.WHITE);
			portafolioCell.setBorder(PdfPCell.NO_BORDER);
			portafolioCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			// Estilo para la fila de BenchMark (gris sin bordes)
			PdfPCell benchmarkCell = new PdfPCell();
			benchmarkCell.setBackgroundColor(new BaseColor(240, 240, 240));
			benchmarkCell.setBorder(PdfPCell.NO_BORDER);
			benchmarkCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
			Font fontContenido = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
			BaseColor encabezadoColor = new BaseColor(198, 183, 132);
			BaseColor grisClaro = new BaseColor(240, 240, 240);

			// Encabezados
			table.addCell(createStyledCell(" ", fontEncabezado, encabezadoColor));
			if (mostrarAnio2024) {
				table.addCell(createStyledCell("2024", fontEncabezado, encabezadoColor));
			}
			for (DatosGraficaDTO dato : datosMensuales) {
				table.addCell(createStyledCell(dato.getMes(), fontEncabezado, encabezadoColor));
			}
			table.addCell(createStyledCell("En el Año", fontEncabezado, encabezadoColor));

			// Fila Portafolio
			table.addCell(createStyledCell("Portafolio *", fontContenido, BaseColor.WHITE));
			if (mostrarAnio2024) {
				table.addCell(createStyledCell(
						datoAnioAnt != null ? String.format("%.2f%%", datoAnioAnt.getPortafolio() * 100) : "-",
						fontContenido, BaseColor.WHITE));
			}
			for (DatosGraficaDTO dato : datosMensuales) {
				table.addCell(createStyledCell(String.format("%.2f%%", dato.getPortafolio() * 100), fontContenido,
						BaseColor.WHITE));
			}
			table.addCell(createStyledCell(
					datoEnElAnio != null ? String.format("%.2f%%", datoEnElAnio.getPortafolio() * 100) : "-",
					fontContenido, BaseColor.WHITE));

			// Fila Benchmark
			table.addCell(createStyledCell("Benchmark", fontContenido, grisClaro));
			if (mostrarAnio2024) {
				table.addCell(createStyledCell(
						datoAnioAnt != null ? String.format("%.2f%%", datoAnioAnt.getBenchmark() * 100) : "-",
						fontContenido, grisClaro));
			}
			for (DatosGraficaDTO dato : datosMensuales) {
				table.addCell(
						createStyledCell(String.format("%.2f%%", dato.getBenchmark() * 100), fontContenido, grisClaro));
			}
			table.addCell(createStyledCell(
					datoEnElAnio != null ? String.format("%.2f%%", datoEnElAnio.getBenchmark() * 100) : "-",
					fontContenido, grisClaro));

			table.addCell("\n");
			document.add(table);
			document.add(Chunk.NEWLINE);
			Paragraph disclaimer1 = new Paragraph();
			disclaimer1.add(
					new Chunk("* El rendimiento del portafolio es anualizado, neto, después de costos e impuestos.\n",
							NORMAL_FONT));
			disclaimer1.setAlignment(Element.ALIGN_LEFT);
			disclaimer1.setSpacingBefore(10);
			document.add(disclaimer1);

			float footerY = 70;
			float leftMargin = 30;
			float rightMargin = PageSize.A4.rotate().getWidth() - 30;

			Paragraph disclaimer = new Paragraph();
			disclaimer.add(new Chunk("Documento informativo", NORMAL_FONT));
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, disclaimer, leftMargin, footerY,
					0);
			try {
				Image image6 = Image.getInstance("src/main/resources/static/image6.png");
				image6.scaleToFit(200, 200);
				image6.setAbsolutePosition(rightMargin - image6.getScaledWidth(),
						footerY - (image6.getScaledHeight() / 2) + 5 // Ajuste fino de altura
				);
				document.add(image6);
			} catch (Exception e) {
				System.err.println("Error al cargar image6.png: " + e.getMessage());
			}

			Paragraph footer = new Paragraph("Página 3 de 6", NORMAL_FONT);
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, footer,
					PageSize.A4.rotate().getWidth() / 2, footerY, 0);

			return true; // Éxito
		} catch (Exception e) {
			e.printStackTrace();
			return false; // Fallo
		}

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

		boolean addedGap = false;

		for (String cat : categorias) {
			for (DatosGraficaDTO dato : datosGrafica) {
				String mes = dato.getMes();
				String categoriaActual = mes.equals("Año Ant")
						? datosReporteAlphaDTO.getFechaInicioAlpha().substring(0, 4)
						: mes;

				if (categoriaActual.equals(cat)) {
					// Insertar una categoría vacía como separador antes de "Año Ant"
					if (mes.equals("Año Ant") && !addedGap) {
						dataset.addValue(null, "Benchmark", " ");
						dataset.addValue(null, "Portafolio", " ");
						addedGap = true;
					}

					dataset.addValue(dato.getBenchmark() * 100, "Benchmark", cat);
					dataset.addValue(dato.getPortafolio() * 100, "Portafolio", cat);
					break;
				}
			}
		}

		/*
		 * for (String cat : categorias) { for (DatosGraficaDTO dato : datosGrafica) {
		 * String mes = dato.getMes(); String categoriaActual = mes.equals("Año Ant") ?
		 * datosReporteAlphaDTO.getFechaInicioAlpha().substring(0, 4) : mes;
		 * 
		 * if (categoriaActual.equals(cat)) { dataset.addValue(dato.getBenchmark() *
		 * 100, "Benchmark", cat); dataset.addValue(dato.getPortafolio() * 100,
		 * "Portafolio", cat); break; } } }
		 */

		JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, true, true,
				false);

		CategoryPlot plot = chart.getCategoryPlot();

		chart.setBackgroundPaint(Color.WHITE);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

		// Eje Y
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.00'%'"));
		rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		double maxValue = obtenerValorMaximo(dataset);
		double tickUnit = calcularTickUnitDinamico(maxValue);
		rangeAxis.setTickUnit(new NumberTickUnit(tickUnit));

		// Eje X
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryMargin(0.1);

		// Configurar leyenda
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.BOTTOM);
		BarRenderer renderer = new BarRenderer() {
			@Override
			public Paint getItemPaint(int row, int column) {
				if (row == 0)
					return new Color(60, 180, 229, 255); // Benchmark
				else
					return new Color(4, 30, 66, 255); // Portafolio
			}
		};

		renderer.setMaximumBarWidth(0.05); // 🔹 Ajuste de ancho de barra
		renderer.setItemMargin(-0.4);

		// Colores personalizados
		renderer.setSeriesPaint(0, new Color(60, 180, 229, 255)); // color Benchmark
		renderer.setSeriesPaint(1, new Color(4, 30, 66, 255)); // color Portafolio

		// Mostrar etiquetas SOLO para Portafolio (row 1)
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setDefaultItemLabelFont(new java.awt.Font("SansSerif", Font.NORMAL, 10));
		renderer.setDefaultItemLabelPaint(Color.BLACK);
		renderer.setDefaultPositiveItemLabelPosition(
				new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));

		renderer.setSeriesItemLabelsVisible(0, false); // Benchmark
		renderer.setSeriesItemLabelsVisible(1, true); // Portafolio
		renderer.setSeriesItemLabelGenerator(1,
				new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.00'%'")));
		renderer.setShadowVisible(false);
		renderer.setDrawBarOutline(false);
		renderer.setBarPainter(new StandardBarPainter());
		plot.setRenderer(renderer);
		plot.setRangeZeroBaselineVisible(true);
		plot.setOutlineVisible(false);
		plot.setRangeZeroBaselinePaint(Color.BLACK);

		return chart;
	}

	private String[] getCategorias(List<DatosGraficaDTO> datosGraficaDTO, String fechaInicioAlpha,
			String fechaFinElaboracion) {
		List<String> categoriasList = new ArrayList<>();

		// Ordenar meses cronológicamente
		List<String> ordenMeses = Arrays.asList("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio",
				"agosto", "septiembre", "octubre", "noviembre", "diciembre");

		List<DatosGraficaDTO> datosOrdenados = new ArrayList<>(datosGraficaDTO);
		datosOrdenados.sort(Comparator.comparingInt(d -> ordenMeses.indexOf(d.getMes().toLowerCase())));

		for (DatosGraficaDTO datos : datosOrdenados) {
			String mes = datos.getMes().toLowerCase();
			if (!mes.equals("desde inicio") && !mes.equals("en el año") && !mes.equals("año ant")) {
				categoriasList.add(datos.getMes()); // Usamos el mes con capitalización original
			}
		}

		// Solo agregar "En el Año" si existe en los datos
		if (datosGraficaDTO.stream().anyMatch(d -> "En el Año".equalsIgnoreCase(d.getMes()))) {
			categoriasList.add("En el Año");
		}

		return categoriasList.toArray(new String[0]);
	}

	private double obtenerValorMaximo(DefaultCategoryDataset dataset) {
		double max = Double.NEGATIVE_INFINITY;

		for (int row = 0; row < dataset.getRowCount(); row++) {
			for (int col = 0; col < dataset.getColumnCount(); col++) {
				Number value = dataset.getValue(row, col);
				if (value != null && value.doubleValue() > max) {
					max = value.doubleValue();
				}
			}
		}
		return max;
	}

	private double calcularTickUnitDinamico(double maxValue) {
		if (maxValue <= 10)
			return 1.0;
		else if (maxValue <= 20)
			return 2.0;
		else if (maxValue <= 50)
			return 5.0;
		else
			return 10.0;
	}

}
