package com.actinver.report_generator.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PaginaCincoService {

	@Autowired
	private ReportDataService reportDataService;

	private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
	private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

	@SuppressWarnings("static-access")
	public boolean addExplanationPage(Document document, PdfWriter writer, String textSlite, String estrategia) {
		try {
			// Imagen de fondo
			try {
				// Image image5 = Image.getInstance("src/main/resources/static/image5.png");
				InputStream imageStream5 = getClass().getClassLoader().getResourceAsStream("static/image5.png");
				Image image5 = Image.getInstance(reportDataService.toByteArray(imageStream5));
				float imgWidth = 140;
				float imgHeight = PageSize.A4.rotate().getHeight() * 0.75f;
				image5.scaleAbsolute(imgWidth, imgHeight);
				image5.setAbsolutePosition(10, PageSize.A4.rotate().getHeight() - imgHeight - 70);
				writer.getDirectContentUnder().addImage(image5);
			} catch (Exception e) {
				System.err.println("Error al cargar image5.png: " + e.getMessage());
			}

			document.add(new Paragraph("\n"));
			Paragraph title = new Paragraph("Solución " + estrategia + ":", SUBTITLE_FONT);
			title.setIndentationLeft(90);
			title.setSpacingBefore(40f);
			document.add(title);

			// Triángulo amarillo
			PdfContentByte canvas = writer.getDirectContent();
			canvas.saveState();
			canvas.setColorFill(new BaseColor(0xb4, 0xa2, 0x69));
			canvas.moveTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 50);
			canvas.lineTo(PageSize.A4.rotate().getWidth() - 110, PageSize.A4.rotate().getHeight() - 50);
			canvas.lineTo(PageSize.A4.rotate().getWidth() - 10, PageSize.A4.rotate().getHeight() - 150);
			canvas.fill();
			canvas.restoreState();

			// Contenido del texto
			Font testFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
			Paragraph content = new Paragraph(textSlite, testFont);
			content.setAlignment(Element.ALIGN_JUSTIFIED);
			content.setIndentationLeft(85);
			content.setIndentationRight(30);
			document.add(content);

			// Pie de página
			float footerY = 70;
			float leftMargin = 30;
			float rightMargin = PageSize.A4.rotate().getWidth() - 30;

			Paragraph disclaimer = new Paragraph();
			disclaimer.add(new Chunk("Documento informativo", NORMAL_FONT));
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, disclaimer, leftMargin + 80,
					footerY, 0);

			try {
				// Image image6 = Image.getInstance("src/main/resources/static/image6.png");
				InputStream imageStream6 = getClass().getClassLoader().getResourceAsStream("static/image6.png");
				Image image6 = Image.getInstance(reportDataService.toByteArray(imageStream6));
				image6.scaleToFit(200, 200);
				image6.setAbsolutePosition(rightMargin - image6.getScaledWidth(),
						footerY - (image6.getScaledHeight() / 2) + 5);
				document.add(image6);
			} catch (Exception e) {
				System.err.println("Error al cargar image6.png: " + e.getMessage());
			}

			Paragraph footer = new Paragraph("Página 5 de 6", NORMAL_FONT);
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, footer,
					PageSize.A4.rotate().getWidth() / 2, footerY, 0);

			return true; // Éxito
		} catch (Exception e) {
			e.printStackTrace();
			return false; // Fallo
		}
	}

}
