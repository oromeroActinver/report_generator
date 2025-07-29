package com.actinver.report_generator.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

@Service
public class ReportDataService {
	
	public static byte[] toByteArray(InputStream input) throws IOException {
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    int nRead;
	    byte[] data = new byte[16384];

	    while ((nRead = input.read(data, 0, data.length)) != -1) {
	        buffer.write(data, 0, nRead); // <- esta es la línea corregida
	    }

	    buffer.flush();
	    return buffer.toByteArray();
	}




}
