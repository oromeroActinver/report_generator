package com.actinver.report_generator.model;

import lombok.Data;

@Data
public class PortfolioData {
	
	private String contractNumber;
    private String solutionType;
    private String startDate;
    private double debtPercentage;
    private double corporatePercentage;
    private double debtFundsPercentage;
    private double governmentPercentage;
    private double stocksPercentage;
    private double nationalStocksPercentage;
    private double initialInvestment;
    private double currentValue;
	
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	public String getSolutionType() {
		return solutionType;
	}
	public void setSolutionType(String solutionType) {
		this.solutionType = solutionType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public double getDebtPercentage() {
		return debtPercentage;
	}
	public void setDebtPercentage(double debtPercentage) {
		this.debtPercentage = debtPercentage;
	}
	public double getCorporatePercentage() {
		return corporatePercentage;
	}
	public void setCorporatePercentage(double corporatePercentage) {
		this.corporatePercentage = corporatePercentage;
	}
	public double getDebtFundsPercentage() {
		return debtFundsPercentage;
	}
	public void setDebtFundsPercentage(double debtFundsPercentage) {
		this.debtFundsPercentage = debtFundsPercentage;
	}
	public double getGovernmentPercentage() {
		return governmentPercentage;
	}
	public void setGovernmentPercentage(double governmentPercentage) {
		this.governmentPercentage = governmentPercentage;
	}
	public double getStocksPercentage() {
		return stocksPercentage;
	}
	public void setStocksPercentage(double stocksPercentage) {
		this.stocksPercentage = stocksPercentage;
	}
	public double getNationalStocksPercentage() {
		return nationalStocksPercentage;
	}
	public void setNationalStocksPercentage(double nationalStocksPercentage) {
		this.nationalStocksPercentage = nationalStocksPercentage;
	}
	public double getInitialInvestment() {
		return initialInvestment;
	}
	public void setInitialInvestment(double initialInvestment) {
		this.initialInvestment = initialInvestment;
	}
	public double getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}
	public Object getFinalValue() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
