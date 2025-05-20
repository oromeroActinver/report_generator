package com.actinver.report_generator.model;

import java.util.List;

import lombok.Data;

@Data
public class DatosGenerales {
	
	private String contrato;
    private String estrategia;
    private String fechaInicioAlpha;
    private double deuda;
    private double acciones;
    private List<Double> deudaList;
    private List<Double> accionesList;
    
    
	public String getContrato() {
		return contrato;
	}
	public void setContrato(String contrato) {
		this.contrato = contrato;
	}
	public String getEstrategia() {
		return estrategia;
	}
	public void setEstrategia(String estrategia) {
		this.estrategia = estrategia;
	}
	public String getFechaInicioAlpha() {
		return fechaInicioAlpha;
	}
	public void setFechaInicioAlpha(String fechaInicioAlpha) {
		this.fechaInicioAlpha = fechaInicioAlpha;
	}
	public double getDeuda() {
		return deuda;
	}
	public void setDeuda(double deuda) {
		this.deuda = deuda;
	}
	public double getAcciones() {
		return acciones;
	}
	public void setAcciones(double acciones) {
		this.acciones = acciones;
	}
	public List<Double> getDeudaList() {
		return deudaList;
	}
	public void setDeudaList(List<Double> deudaList) {
		this.deudaList = deudaList;
	}
	public List<Double> getAccionesList() {
		return accionesList;
	}
	public void setAccionesList(List<Double> accionesList) {
		this.accionesList = accionesList;
	}
    
    

}
