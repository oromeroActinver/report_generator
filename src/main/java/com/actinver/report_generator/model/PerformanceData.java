package com.actinver.report_generator.model;

import java.util.Map;

import lombok.Data;

@Data
public class PerformanceData {
	
	private Map<String, Double> portfolioMonthlyReturns;
    private Map<String, Double> benchmarkMonthlyReturns;
    private double annualReturn;
    
    
	public Map<String, Double> getPortfolioMonthlyReturns() {
		return portfolioMonthlyReturns;
	}
	public void setPortfolioMonthlyReturns(Map<String, Double> portfolioMonthlyReturns) {
		this.portfolioMonthlyReturns = portfolioMonthlyReturns;
	}
	public Map<String, Double> getBenchmarkMonthlyReturns() {
		return benchmarkMonthlyReturns;
	}
	public void setBenchmarkMonthlyReturns(Map<String, Double> benchmarkMonthlyReturns) {
		this.benchmarkMonthlyReturns = benchmarkMonthlyReturns;
	}
	public double getAnnualReturn() {
		return annualReturn;
	}
	public void setAnnualReturn(double annualReturn) {
		this.annualReturn = annualReturn;
	}
    
    

}
