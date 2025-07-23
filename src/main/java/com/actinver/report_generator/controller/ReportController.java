package com.actinver.report_generator.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.actinver.report_generator.dto.DatosReporteAlphaDTO;
import com.actinver.report_generator.service.PdfGenerationService;
import com.actinver.report_generator.service.ReportDataService;
import com.itextpdf.text.DocumentException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
	
	private final PdfGenerationService pdfGenerationService;
    private final ReportDataService reportDataService;

    @Autowired
    public ReportController(PdfGenerationService pdfGenerationService, ReportDataService reportDataService) {
        this.pdfGenerationService = pdfGenerationService;
        this.reportDataService = reportDataService;
        
    }
    
    @PostMapping("/generar-pdf")
    public ResponseEntity<?> generarPDF(@RequestBody DatosReporteAlphaDTO datos) throws DocumentException {
        byte[] pdfBytes = pdfGenerationService.generarReporte(datos);

        if (pdfBytes == null) {
            // Error al generar el PDF
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se generó el PDF. Ocurrieron errores durante la creación del documento.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=investment_report.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }


}
