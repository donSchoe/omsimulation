/*
 * OM Simulation Tool: This tool intends to test and evaluate the scientific
 * robustness of the protocol `6+1`. Therefore, it generates a huge amount of
 * virtual measurement campaigns based on real radon concentration data
 * following the mentioned protocol. <http://github.com/donschoe/omsimulation>
 * 
 * Copyright (C) 2012 Alexander Schoedon <a.schoedon@student.htw-berlin.de>
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

package de.bfs.radon.omsimulation.gui.data;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.jfree.chart.JFreeChart;

import com.itextpdf.awt.FontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Public abstract class OMExports providing helper methods for exporting data.
 * Can not be instantiated.
 * 
 * @author A. Schoedon
 */
public abstract class OMExports {

  /**
   * 
   * Method to export charts as PDF files using the defined path.
   * 
   * @param path
   *          The filename and absolute path.
   * @param chart
   *          The JFreeChart object.
   * @param width
   *          The width of the PDF file.
   * @param height
   *          The height of the PDF file.
   * @param mapper
   *          The font mapper for the PDF file.
   * @param title
   *          The title of the PDF file.
   * @throws IOException
   *           If writing a PDF file fails.
   */
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
      JOptionPane.showMessageDialog(null, "Failed to write PDF document.\n"
          + de.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
      de.printStackTrace();
    }
    document.close();
  }
}
