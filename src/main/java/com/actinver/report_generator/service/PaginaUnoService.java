package com.actinver.report_generator.service;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PaginaUnoService {

	// HOJA 1
	public void addFirstPage(Document document, PdfWriter writer) {

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
		try {
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
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
