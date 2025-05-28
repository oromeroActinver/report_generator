package com.actinver.report_generator.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping(value = "/investment", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateInvestmentReport() throws Exception {
        byte[] pdfBytes = pdfGenerationService.generateReport(
            reportDataService.getClientData(),
            reportDataService.getPortfolioData(),
            reportDataService.getPerformanceData()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=investment_report.pdf");

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
    }
    
    @PostMapping("/generar-pdf")
    public ResponseEntity<byte[]> generarPDF(@RequestBody DatosReporteAlphaDTO datos) throws DocumentException {
        // Aquí generas el PDF como byte[]
        byte[] pdfBytes = pdfGenerationService.generarReporte(datos);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=investment_report.pdf");
 
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}
