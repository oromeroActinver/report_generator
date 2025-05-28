package com.actinver.report_generator.service;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PaginaSeisService {
	
	public void addFooterPage(Document document, PdfWriter writer) throws DocumentException {
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

}
