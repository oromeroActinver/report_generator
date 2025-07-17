package com.actinver.report_generator.service;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.springframework.stereotype.Service;

import com.actinver.report_generator.dto.CarteraDetalleDTO;
import com.actinver.report_generator.dto.DatosReporteAlphaDTO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
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
public class PaginaDosService {

	private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

	public boolean addPortfolioDataPage(Document document, PdfWriter writer, DatosReporteAlphaDTO datosReporte) {
		try {
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
			titleTable.setWidthPercentage(100);
			titleTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			titleTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

			float circleY = PageSize.A4.rotate().getHeight() - 100;
			float circleX = PageSize.A4.rotate().getWidth() / 2 - 70;
			float tableYPosition = circleY - 35;
			titleTable.setSpacingBefore(tableYPosition);

			titleTable.addCell("\n\n\n");
			Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK);
			PdfPCell titleCell = new PdfPCell(new Phrase("Datos generales \ndel portafolio.", titleFont));
			titleCell.setBorder(Rectangle.NO_BORDER);
			titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titleCell.setPaddingBottom(0);
			titleCell.setPaddingLeft(40);
			titleTable.addCell(titleCell);

			PdfContentByte canvas = writer.getDirectContent();
			canvas.saveState();
			canvas.setColorFill(new BaseColor(0x3c, 0xb4, 0xe5));
			float circleRadius = 40;
			canvas.circle(circleX, circleY, circleRadius);
			canvas.fill();
			canvas.restoreState();

			document.add(titleTable);
			document.add(new Paragraph("\n"));

			canvas.saveState();
			canvas.setColorFill(new BaseColor(0xb4, 0xa2, 0x69));
			canvas.moveTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 50);
			canvas.lineTo(PageSize.A4.rotate().getWidth() - 110, PageSize.A4.rotate().getHeight() - 50);
			canvas.lineTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 150);
			canvas.fill();
			canvas.restoreState();

			SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyyMMdd");
			Date fecha = formatoEntrada.parse(datosReporte.getFechaInicioAlpha());

			SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MMM/yyyy", new Locale("es", "ES"));
			String fechaFormateada = formatoSalida.format(fecha).toLowerCase();

			Font boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
			Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

			Paragraph contractInfo = new Paragraph();
			contractInfo.setSpacingBefore(20);
			contractInfo.setIndentationLeft(400);
			contractInfo.add(new Chunk("Contrato: ", boldFont));
			contractInfo.add(new Chunk(datosReporte.getContrato() + "\n", normalFont));
			contractInfo.add(new Chunk("Tipo de Solución: ", boldFont));
			contractInfo.add(new Chunk(datosReporte.getEstrategia() + "\n", normalFont));
			contractInfo.add(new Chunk("Fecha de Inicio en Inversión Alpha: ", boldFont));
			contractInfo.add(new Chunk(fechaFormateada + "\n\n", normalFont));
			document.add(contractInfo);

			DefaultPieDataset dataset = getDatosGraficoPastel(datosReporte.getCarteraDetalleDTO());
			JFreeChart chart = ChartFactory.createPieChart("", dataset, false, false, false);
			PiePlot plot = getColoresGraficoPastel(chart, datosReporte.getCarteraDetalleDTO());

			ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
			int width = 1000;
			int height = 1000;
			ChartUtils.writeChartAsPNG(chartOutputStream, chart, width, height);
			Image chartImage = Image.getInstance(chartOutputStream.toByteArray());
			chartImage.scaleToFit(250, 250);
			chartImage.setAbsolutePosition(100, PageSize.A4.rotate().getHeight() - 240 - 150);
			document.add(chartImage);

			Paragraph tableContainer = new Paragraph();
			tableContainer.setAlignment(Element.ALIGN_RIGHT);
			tableContainer.setIndentationRight(56.7f);
			tableContainer.setSpacingBefore(0);

			PdfPTable compositionTable = new PdfPTable(2);
			compositionTable.setWidthPercentage(35);
			compositionTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
			compositionTable.setSpacingBefore(10);
			compositionTable.setWidths(new float[] { 2, 1 });
			compositionTable.setSpacingAfter(0);
			compositionTable.setTotalWidth(320f);
			compositionTable.setLockedWidth(true);
			compositionTable.setExtendLastRow(false);

			Font largeLabelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			Font largeValueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

			generateDatosTabla(datosReporte.getCarteraDetalleDTO(), compositionTable, largeLabelFont, largeValueFont);
			tableContainer.add(compositionTable);
			document.add(tableContainer);

			try {
				Image image6 = Image.getInstance("src/main/resources/static/image6.png");
				image6.scaleToFit(200, 200);
				float rightMargin = PageSize.A4.rotate().getWidth() - 30;
				float footerY = 70;
				image6.setAbsolutePosition(rightMargin - image6.getScaledWidth(),
						footerY - (image6.getScaledHeight() / 2) + 5);
				document.add(image6);
			} catch (Exception e) {
				System.err.println("Error al cargar image6.png: " + e.getMessage());
			}

			Paragraph footer = new Paragraph("Página 2 de 6", NORMAL_FONT);
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, footer,
					PageSize.A4.rotate().getWidth() / 2, 70, 0);

			return true; // Éxito
		} catch (Exception e) {
			e.printStackTrace();
			return false; // Fallo
		}
	}

	private DefaultPieDataset getDatosGraficoPastel(List<CarteraDetalleDTO> carteraDetalleDTO) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		if (!carteraDetalleDTO.isEmpty()) {
			for (CarteraDetalleDTO entry : carteraDetalleDTO) {
				if (!entry.getDescripcion().equals("LIQUIDEZ")) {
					dataset.setValue(entry.getDescripcion(), entry.getPorcentaje()); // ← sin espacio
				}
			}
		} else {
			dataset.setValue("SIN DATOS", 0);
		}

		return dataset;
	}

	private PiePlot getColoresGraficoPastel(JFreeChart chart, List<CarteraDetalleDTO> carteraDetalleDTO) {
		PiePlot plot = (PiePlot) chart.getPlot();

		// Lista de colores personalizados (tonos de azul y gris)
		List<Color> colores = Arrays.asList(new Color(149, 179, 215, 255), new Color(42, 125, 225, 255),
				new Color(165, 197, 237, 255), new Color(107, 196, 232, 255), new Color(54, 96, 146, 255),
				new Color(197, 217, 241, 255), new Color(22, 45, 92, 255));

		int index = 0;
		for (CarteraDetalleDTO categoria : carteraDetalleDTO) {
			if (!categoria.getDescripcion().equals("LIQUIDEZ")) {
				Color color = colores.get(index % colores.size());
				plot.setSectionPaint(categoria.getDescripcion(), color); // Asegúrate que coincida con el dataset
				index++;
			}
		}

		// Configuración visual del gráfico
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		chart.setBackgroundPaint(Color.WHITE);

		// Configuración de etiquetas
		plot.setLabelGenerator(
				new StandardPieSectionLabelGenerator("{0} {2}", new DecimalFormat("0.00"), new DecimalFormat("0.00%")));
		plot.setLabelFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 30));
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

		BaseColor blue = new BaseColor(0x04, 0x1e, 0x42); // Encabezado Deuda
		BaseColor gray = new BaseColor(0x80, 0x80, 0x80); // Encabezado Acciones
		BaseColor lightGray = new BaseColor(0xf2, 0xf2, 0xf2); // Gris claro para alternancia

		double deuda = 0;
		double acciones = 0;
		Map<String, Double> deudaList = new LinkedHashMap<>();
		Map<String, Double> accionesList = new LinkedHashMap<>();

		for (CarteraDetalleDTO entry : carteraDetalleDTO) {
			String nombre = entry.getDescripcion();
			double valor = entry.getPorcentaje();

			if (!nombre.equals("LIQUIDEZ")) {
				if (nombre.contains("Acciones") || nombre.contains("Fondos de R. V.")) {
					acciones += valor;
					accionesList.put(nombre, valor);
				} else {
					deuda += valor;
					deudaList.put(nombre, valor);
				}
			}
		}

		if (deuda > 0) {
			addColoredTableCellCentered(compositionTable, "Deuda", String.format("%.2f%%", deuda), blue, false,
					largeLabelFont, largeValueFont, BaseColor.WHITE);

			boolean alternate = false;
			for (Map.Entry<String, Double> entryDeuda : deudaList.entrySet()) {
				BaseColor rowColor = alternate ? lightGray : BaseColor.WHITE;
				addColoredTableCellCentered(compositionTable, entryDeuda.getKey(),
						String.format("%.2f%%", entryDeuda.getValue()), rowColor, false, largeLabelFont,
						largeValueFont);
				alternate = !alternate;
			}
		}

		if (acciones > 0) {
			addColoredTableCellCentered(compositionTable, "Acciones", String.format("%.2f%%", acciones), gray, false,
					largeLabelFont, largeValueFont, BaseColor.WHITE);

			boolean alternate = false;
			for (Map.Entry<String, Double> entryAcc : accionesList.entrySet()) {
				BaseColor rowColor = alternate ? lightGray : BaseColor.WHITE;
				addColoredTableCellCentered(compositionTable, entryAcc.getKey(),
						String.format("%.2f%%", entryAcc.getValue()), rowColor, false, largeLabelFont, largeValueFont);
				alternate = !alternate;
			}
		}
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

		Font whiteLabelFont = new Font(labelFont);
		Font whiteValueFont = new Font(valueFont);

		whiteLabelFont.setColor(textColor);
		whiteValueFont.setColor(textColor);

		PdfPCell cell1 = new PdfPCell(new Phrase(content1, whiteLabelFont));
		PdfPCell cell2 = new PdfPCell(new Phrase(content2, whiteValueFont));

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

}
