package com.actinver.report_generator.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.actinver.report_generator.service.PdfGenerationService;
import com.actinver.report_generator.service.ReportDataService;

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

}
