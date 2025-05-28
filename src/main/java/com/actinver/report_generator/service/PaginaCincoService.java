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
public class PaginaCincoService {

	private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
	private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
	
	public void addExplanationPage(Document document, PdfWriter writer, String textSlite, String estrategia) throws DocumentException {

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
		Paragraph title = new Paragraph("Solución " + estrategia + ":", SUBTITLE_FONT);
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

		String text = textSlite;
		/*String text = "Continuamos este año como se esperaba con un alto grado de incertidumbre y nerviosismo a lo largo y ancho de todos los mercados derivado de la llegada de Trump a la administración de EE.UU. Esto se deriva principalmente de el 'estira y afloja' con la guerra arancelaria para diversas economías como herramienta de negociación para alcanzar ciertas metas en la agenda de Donald Trump. En este sentido todos los activos tanto de renta fija como de variable han tenido un alto nivel de volatilidad lo cual ha llevado a la recomposición de las carteras para posicionarlas más cautas entre este entorno. Los principales activos beneficiados han sido los de renta fija en donde el inversionista se ha refugiado y por lo tanto han tenido un recorrido a la baja interesante. El peso mexicano si bien de principio a fin del mes no tuvo cambios significativos si experimento una alta volatilidad detonado por la primera casi implementación de los aranceles a principios de mes y nuevamente cerrando este. Por otro lado tuvimos decisión de política monetaria en EE.UU. en donde se mantuvo sin cambios haciendo referencia en donde se sigue la lucha para llevar la inflación hacia niveles objetivos del banco central. En México a diferencia de EE.UU. de tomo la decisión de recortar en 50bps la tasa de referencia para llevarla a 9.5% argumentando una tasa real ex-ante elevada y una inflación contenida dentro del rango de tolerancia de Banco de México.\n\n"
				+ "Nosotros en la Solución Patrimonial si bien este año tiene cierto grado de incertidumbre sobre los recortes esperados por parte de la Fed así como de Banxico, si se han tomado las decisiones correctas para capturar el mejor rendimiento posible entregando a nuestros clientes un rendimiento de 14.1%. Así mismo, con la estrategia que tenemos actualmente y viendo hacia este 2025, apunta a continuar con excelentes rendimientos, así como intereses altos que podemos entregar a nuestros clientes. La estrategia se enfoca en Cetes menores a 1 año con un premio derivado de nuestro escenario central que se orienta en la continuidad de normalización de tasas por parte de los bancos centrales que será benéfico para el rendimiento de los portafolios. Además del posicionamiento en Renta Fija también la valuación de la Bolsa Mexicana de valores muestra oportunidades claves para capturar aún más rendimientos para nuestros clientes. Con esto continua la impecable tendencia histórica a pesar de los diversos escenarios a los cuales se ha enfrentado durante los últimos años. No obstante, nos mantenemos atentos al desarrollo de la economía a nivel global y local como la toma de la presidencia de EE.UU. por Donald Trump y algunas reformas en México con el fin de capitalizar oportunidades que pudiesen existir.\n\n"
				+ "Como se esperaba este 2025 estará lleno de retos y a su vez de oportunidades. Sin embargo, la cautela es lo que debe de prevalecer y una cartera bien diversificada para hacer frente al entorno actual. Si bien en lo general los datos económicos se siguen viendo saludables existen ciertos datos que se han moderado y pudiesen indicar una economía que empieza a desacelerarse. Con esta visión, continuamos esperando un buen 2025 aunque sin duda será turbulento pero lleno de oportunidades las cuales estaremos trabajando en Soluciones Alpha para capturarlas y continuar dando los resultados consistentes a nuestros clientes.";*/

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
	
}
