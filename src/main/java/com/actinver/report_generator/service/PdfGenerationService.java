package com.actinver.report_generator.service;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.actinver.report_generator.dto.DatosReporteAlphaDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
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
	
	private PdfWriter writer;

	

	public byte[] generarReporte(DatosReporteAlphaDTO datosReporte) throws DocumentException {
		Document document = new Document(PageSize.A4.rotate());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		this.writer = PdfWriter.getInstance(document, outputStream);

		try {
			document.open();

			if (!paginaUnoService.addFirstPage(document, this.writer, datosReporte.getAnual(),
					datosReporte.getFechaFinElaboracion())) {
				throw new RuntimeException("No se pudo generar la página 1.");
			}
			document.newPage();

			if (!paginaDosService.addPortfolioDataPage(document, this.writer, datosReporte)) {
				throw new RuntimeException("No se pudo generar la página 2.");
			}
			document.newPage();

			if (!paginaTresService.addPerformancePage(document, this.writer, datosReporte)) {
				throw new RuntimeException("No se pudo generar la página 3.");
			}
			document.newPage();

			if (!paginaCuatroService.addDepositsWithdrawalsPage(document, this.writer,
					datosReporte.getDatosMovimientosDTO(), datosReporte.getAnual())) {
				throw new RuntimeException("No se pudo generar la página 4.");
			}
			document.newPage();

			if (!paginaCincoService.addExplanationPage(document, this.writer, datosReporte.getTextSlite5(),
					datosReporte.getEstrategia())) {
				throw new RuntimeException("No se pudo generar la página 5.");
			}
			document.newPage();

			if (!paginaSeisService.addFooterPage(document, this.writer)) {
				throw new RuntimeException("No se pudo generar la página 6.");
			}

			document.close();
			return outputStream.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
			if (document.isOpen()) {
				document.close();
			}
			System.err.println("No se generó el PDF. Ocurrieron errores: " + e.getMessage());
			return null;
		}
	}
	
	public void validacionDatosPDF() {
		
	}

}
