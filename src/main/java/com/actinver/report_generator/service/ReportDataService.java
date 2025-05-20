package com.actinver.report_generator.service;

import org.springframework.stereotype.Service;

import com.actinver.report_generator.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportDataService {
	
	public String getAño() {
		return "2025";
	}
	
	public String getFechaElavoracion() {
		return "28 de febrero del 2025";
	}
	
	public ClientData getClientData() {
        ClientData client = new ClientData();
        client.setClientName("Juan Pérez García");
        client.setClientId("CLI-123456");
        client.setEmail("juan.perez@example.com");
        client.setAddress("Av. Reforma 123, CDMX, México");
        return client;
    }
	
	public DatosGenerales getDatosGenerales() {
		DatosGenerales datosGenerales = new DatosGenerales();
		
		datosGenerales.setContrato("5333542");
		datosGenerales.setEstrategia("PATRIMONIAL");
		datosGenerales.setFechaInicioAlpha("25/mar/2022");
		
		datosGenerales.setDeuda(77.37);
		datosGenerales.setAcciones(22.65);
		
		
       /* portfolio.setCorporatePercentage(10.01);
        portfolio.setDebtFundsPercentage(41.95);
        portfolio.setGovernmentPercentage(25.41);
        
        portfolio.setNationalStocksPercentage(22.63);
        portfolio.setInitialInvestment(10002308.60);
        portfolio.setCurrentValue(12687592.91);*/
        return datosGenerales;
    }

    public PortfolioData getPortfolioData() {
        PortfolioData portfolio = new PortfolioData();
        portfolio.setContractNumber("5333542");
        portfolio.setSolutionType("PATRIMONIAL");
        portfolio.setStartDate("25/mar/2022");
        portfolio.setDebtPercentage(77.37);
        portfolio.setCorporatePercentage(10.01);
        portfolio.setDebtFundsPercentage(41.95);
        portfolio.setGovernmentPercentage(25.41);
        portfolio.setStocksPercentage(22.65);
        portfolio.setNationalStocksPercentage(22.63);
        portfolio.setInitialInvestment(10002308.60);
        portfolio.setCurrentValue(12687592.91);
        return portfolio;
    }

    public PerformanceData getPerformanceData() {
        PerformanceData performance = new PerformanceData();
        
        Map<String, Double> portfolioReturns = new HashMap<>();
        portfolioReturns.put("Enero", -0.0565);
        portfolioReturns.put("Febrero", -0.1284);
        portfolioReturns.put("En el Año", 0.1378);
        portfolioReturns.put("2024", 0.1337);
        
        Map<String, Double> benchmarkReturns = new HashMap<>();
        benchmarkReturns.put("Enero", -0.0470);
        benchmarkReturns.put("Febrero", -0.1583);
        benchmarkReturns.put("En el Año", 0.1404);
        benchmarkReturns.put("2024", 0.1565);
        
        performance.setPortfolioMonthlyReturns(portfolioReturns);
        performance.setBenchmarkMonthlyReturns(benchmarkReturns);
        performance.setAnnualReturn(0.141);
        
        return performance;
    }

}
