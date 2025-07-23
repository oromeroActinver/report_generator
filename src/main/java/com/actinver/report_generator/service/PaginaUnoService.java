package com.actinver.report_generator.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
	public boolean addFirstPage(Document document, PdfWriter writer, String anual, String fechaFinElaboracion) {
	    try {
	        PdfContentByte canvas = writer.getDirectContentUnder();
	        canvas.saveState();
	        canvas.setColorFill(new BaseColor(0x1a, 0x22, 0x49));

	        float imageWidth = PageSize.A4.rotate().getWidth() * 0.6f;
	        float fondoHeight = PageSize.A4.rotate().getHeight() * 0.8f;
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
	                float image4Size = imageWidth * 0.8f;
	                image4.scaleToFit(image4Size, image4Size);
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
	            float logoWidth = PageSize.A4.rotate().getWidth() * 0.3f;
	            float logoHeight = logoWidth * 0.20f;
	            image3.scaleAbsolute(logoWidth, logoHeight);
	            float logoX = 40;
	            float logoY = 100;
	            image3.setAbsolutePosition(logoX, logoY);
	            document.add(image3);
	        } catch (Exception e) {
	            Paragraph logoText = new Paragraph("ACTINVER\nAsset Management",
	                    new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK));
	            logoText.setAlignment(Element.ALIGN_LEFT);
	            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, logoText, 40, 40, 0);
	        }

	        Font whiteFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);
	        Font titleFont = new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD, BaseColor.WHITE);

	        Paragraph title = new Paragraph("Soluciones\nAlpha " + anual, titleFont);
	        title.setAlignment(Element.ALIGN_LEFT);
	        title.setIndentationLeft(20);
	        title.setSpacingBefore(100);

	        document.add(title);
	        document.add(Chunk.NEWLINE);

	        Paragraph subtitle = new Paragraph("Dirección de Asset Management", whiteFont);
	        subtitle.setIndentationLeft(20);
	        document.add(subtitle);

	        DateTimeFormatter entrada = DateTimeFormatter.ofPattern("yyyyMMdd");
	        LocalDate fecha = LocalDate.parse(fechaFinElaboracion, entrada);
	        DateTimeFormatter salida = DateTimeFormatter.ofPattern("d 'de' MMMM 'del' yyyy", new Locale("es", "ES"));
	        String fechaFormateada = fecha.format(salida);

	        Paragraph date = new Paragraph("Fecha de elaboración: " + fechaFormateada, whiteFont);
	        date.setIndentationLeft(20);
	        document.add(date);
	        document.add(Chunk.NEWLINE);
	        document.add(Chunk.NEWLINE);

	        return true; // Éxito
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false; // Fallo
	    }
	}


}
