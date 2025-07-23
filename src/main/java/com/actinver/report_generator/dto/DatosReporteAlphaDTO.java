package com.actinver.report_generator.dto;

import java.util.List;


import lombok.Data;

@Data
public class DatosReporteAlphaDTO {
	
	private String contrato;
    private String estrategia;
    private String fechaInicioAlpha;
    private String fechaFinElaboracion;
    private String anual;
    private String textSlite5;
    private double saldoInicio;
    private double saldoFin;
    private List<CarteraDetalleDTO> carteraDetalleDTO;
    private List<DatosGraficaDTO> datosGraficaDTO;
    private List<DatosMovimientosDTO> datosMovimientosDTO;
    
    

}
