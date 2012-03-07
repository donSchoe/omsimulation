package de.bfs.radon.omsimulation.gui.data;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfree.chart.JFreeChart;

import com.itextpdf.awt.FontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/*
 * OM Simulation Tool: This software is a simulation tool for virtual
 * orientated measurement (OM) campaigns following the protocol "6+1" to
 * determine and evaluate the level of radon exposure in buildings.
 * 
 * Copyright (C) 2012 Alexander Schoedon <donc_oe@qhor.net>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

public abstract class OMExports {

  @SuppressWarnings("deprecation")
  public static void exportPdf(String path, JFreeChart chart, int width,
      int height, FontMapper mapper, String title) throws IOException {
    File file = new File(path);
    FileOutputStream pdfStream = new FileOutputStream(file);
    BufferedOutputStream pdfOutput = new BufferedOutputStream(pdfStream);
    Rectangle pagesize = new Rectangle(width, height);
    Document document = new Document();
    document.setPageSize(pagesize);
    document.setMargins(50, 50, 50, 50);
    document.addAuthor("OMSimulationTool");
    document.addSubject(title);
    try {
      PdfWriter pdfWriter = PdfWriter.getInstance(document, pdfOutput);
      document.open();
      PdfContentByte contentByte = pdfWriter.getDirectContent();
      PdfTemplate template = contentByte.createTemplate(width, height);
      Graphics2D g2D = template.createGraphics(width, height, mapper);
      Double r2D = new Rectangle2D.Double(0, 0, width, height);
      chart.draw(g2D, r2D);
      g2D.dispose();
      contentByte.addTemplate(template, 0, 0);
    } catch (DocumentException de) {
      System.err.println(de.getMessage());
    }
    document.close();
  }
}
