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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;

import de.bfs.radon.omsimulation.data.OMCampaign;
import de.bfs.radon.omsimulation.data.OMRoom;
import de.bfs.radon.omsimulation.data.OMRoomType;

/**
 * Public abstract class OMCharts providing helper methods and graphical tools
 * for creating chart objects. Can not be instantiated.
 * 
 * @author A. Schoedon
 */
public abstract class OMCharts {

  /**
   * Creates a chart displaying the radon concentration of a single room. Uses
   * red for normal rooms, blue for cellar rooms and green for misc rooms.
   * 
   * @param title
   *          The headline of the chart. Will be hidden if set to null.
   * @param room
   *          The room object containing the radon data.
   * @param preview
   *          Will hide annotations, labels and headlines if true.
   * @return A chart displaying the radon concentration of a single room.
   */
  public static JFreeChart createRoomChart(String title, OMRoom room,
      boolean preview) {
    Color lineColor = new Color(0, 0, 0, 128);
    Color rangeColor = new Color(222, 222, 222, 128);
    if (room.getType() == OMRoomType.Room) {
      lineColor = new Color(255, 0, 0, 128);
      rangeColor = new Color(255, 222, 222, 128);
    } else {
      if (room.getType() == OMRoomType.Cellar) {
        lineColor = new Color(0, 0, 255, 128);
        rangeColor = new Color(222, 222, 255, 128);
      } else {
        lineColor = new Color(0, 128, 0, 255);
        rangeColor = new Color(222, 255, 222, 128);
      }
    }
    double[] values = room.getValues();
    XYSeriesCollection dataSet = new XYSeriesCollection();
    XYSeries series = new XYSeries("Radon");
    int count = room.getCount();
    double maxPointerKey = 0;
    for (int i = 0; i < count; i++) {
      series.add(i, values[i]);
      if (values[i] == room.getMaximum()) {
        maxPointerKey = i;
      }
    }
    dataSet.addSeries(series);
    title = title + ": " + room.getType().toString() + " " + room.getId();
    JFreeChart chart = ChartFactory.createXYLineChart(title, "T [h]",
        "Rn [Bq/m\u00B3]", dataSet, PlotOrientation.VERTICAL, false, true,
        false);
    XYPlot plot = (XYPlot) chart.getPlot();
    double positiveDeviation = room.getAverage() + room.getDeviation();
    double negativeDeviation = room.getAverage() - room.getDeviation();
    IntervalMarker deviation = new IntervalMarker(negativeDeviation,
        positiveDeviation);
    float[] dash = { 5, 3 };
    deviation.setPaint(rangeColor);
    deviation.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 1, dash, 0));
    plot.addRangeMarker(deviation, Layer.BACKGROUND);
    ValueMarker arithMarker = new ValueMarker(room.getAverage(), lineColor,
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1,
            dash, 0));
    plot.addRangeMarker(arithMarker);
    ValueMarker maxiMarker = new ValueMarker(room.getMaximum(), lineColor,
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1,
            dash, 0));
    plot.addRangeMarker(maxiMarker);
    XYTextAnnotation amLabel = new XYTextAnnotation("AM="
        + (int) room.getAverage(), count, room.getAverage() * 1.01);
    plot.addAnnotation(amLabel);
    XYTextAnnotation sdLabel = new XYTextAnnotation("SD="
        + (int) room.getDeviation(), count,
        (room.getAverage() + room.getDeviation()) * 1.01);
    plot.addAnnotation(sdLabel);
    XYTextAnnotation maxLabel = new XYTextAnnotation("MAX="
        + (int) room.getMaximum(), count, room.getMaximum() * 1.01);
    plot.addAnnotation(maxLabel);
    XYPointerAnnotation maxPointer = new XYPointerAnnotation("", maxPointerKey,
        room.getMaximum(), Math.PI * 1.1);
    plot.addAnnotation(maxPointer);
    XYItemRenderer renderer = plot.getRenderer();
    renderer.setSeriesPaint(0, lineColor);
    if (preview) {
      chart.setTitle("");
      plot.clearAnnotations();
    }
    return chart;
  }

  /**
   * Creates a chart displaying the radon concentration of a virtual campaign.
   * Uses red for normal rooms and blue for cellar rooms.
   * 
   * @param campaign
   *          The campaign object containing all rooms and radon data.
   * @param preview
   *          Will hide annotations, labels and headlines if true.
   * @return A chart displaying the radon concentration of a virtual campaign.
   */
  public static JFreeChart createCampaignChart(OMCampaign campaign,
      boolean preview) {
    OMRoom[] rooms = new OMRoom[7];
    OMRoom[] tmpRooms = campaign.getRooms();
    OMRoom tmpCellar = campaign.getCellar();
    String variation = campaign.getVariation();
    char[] variationChar = variation.toCharArray();
    int cellarPosition = 0;
    for (int i = 0; i < variationChar.length; i++) {
      if (variationChar[i] == 'C' || variationChar[i] == 'c') {
        cellarPosition = i / 2;
      }
    }
    int c = 0;
    for (int i = 0; i < rooms.length; i++) {
      if (i == cellarPosition) {
        rooms[i] = tmpCellar;
        c++;
      } else {
        rooms[i] = tmpRooms[i - c];
      }
    }
    int start = campaign.getStart();
    final int finalStart = start;
    String title = "Campaign: " + rooms[0].getId() + rooms[1].getId()
        + rooms[2].getId() + rooms[3].getId() + rooms[4].getId()
        + rooms[5].getId() + rooms[6].getId() + ", Start: " + finalStart;
    int count = 168;
    double[] values = campaign.getValueChain();
    XYSeriesCollection dataSet = new XYSeriesCollection();
    XYSeries roomSeries1 = new XYSeries(" Radon Rooms");
    XYSeries cellarSeries = new XYSeries("Radon Cellar");
    XYSeries roomSeries2 = new XYSeries("Radon Rooms");
    int cellarSeriesStart = cellarPosition * 24;
    int cellarSeriesEnd = cellarSeriesStart + 24;
    double cellarMaximum = campaign.getCellarMaximum();
    double cellarMaximumKey = 0;
    double roomMaximum = campaign.getRoomMaximum();
    double roomMaximumKey = 0;
    if (cellarSeriesStart > 0) {
      for (int i = 0; i < cellarSeriesStart; i++) {
        roomSeries1.add(finalStart + i, values[i]);
        if (values[i] == roomMaximum) {
          roomMaximumKey = i;
        }
      }
    }
    for (int i = cellarSeriesStart - 1; i < cellarSeriesEnd; i++) {
      if (i >= 0) {
        cellarSeries.add(finalStart + i, values[i]);
        if (values[i] == cellarMaximum) {
          cellarMaximumKey = i;
        }
      }
    }
    if (cellarSeriesEnd < count) {
      for (int i = cellarSeriesEnd - 1; i < count; i++) {
        roomSeries2.add(finalStart + i, values[i]);
        if (values[i] == roomMaximum) {
          roomMaximumKey = i;
        }
      }
    }
    dataSet.addSeries(roomSeries1);
    dataSet.addSeries(cellarSeries);
    dataSet.addSeries(roomSeries2);
    JFreeChart chart = ChartFactory.createXYLineChart(title, "T [h]",
        "Rn [Bq/m\u00B3]", dataSet, PlotOrientation.VERTICAL, false, true,
        false);
    XYPlot plot = (XYPlot) chart.getPlot();
    ValueMarker sepMarker;
    Color sepColor = Color.BLACK;
    float[] sepDash = { 1, 2 };
    Stroke sepStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 1, sepDash, 0);
    RectangleInsets sepLabelInsets = new RectangleInsets(20, -20, 0, 0);
    Font sepLabelFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    sepMarker = new ValueMarker(finalStart + 0, sepColor, sepStroke);
    sepMarker.setLabel(rooms[0].getId());
    sepMarker.setLabelOffset(sepLabelInsets);
    sepMarker.setLabelFont(sepLabelFont);
    plot.addDomainMarker(sepMarker);
    if (rooms[0].getId() != rooms[1].getId()) {
      sepMarker = new ValueMarker(finalStart + 23, sepColor, sepStroke);
      sepMarker.setLabel(rooms[1].getId());
      sepMarker.setLabelOffset(sepLabelInsets);
      sepMarker.setLabelFont(sepLabelFont);
      plot.addDomainMarker(sepMarker);
    }
    if (rooms[1].getId() != rooms[2].getId()) {
      sepMarker = new ValueMarker(finalStart + 47, sepColor, sepStroke);
      sepMarker.setLabel(rooms[2].getId());
      sepMarker.setLabelOffset(sepLabelInsets);
      sepMarker.setLabelFont(sepLabelFont);
      plot.addDomainMarker(sepMarker);
    }
    if (rooms[2].getId() != rooms[3].getId()) {
      sepMarker = new ValueMarker(finalStart + 71, sepColor, sepStroke);
      sepMarker.setLabel(rooms[3].getId());
      sepMarker.setLabelOffset(sepLabelInsets);
      sepMarker.setLabelFont(sepLabelFont);
      plot.addDomainMarker(sepMarker);
    }
    if (rooms[3].getId() != rooms[4].getId()) {
      sepMarker = new ValueMarker(finalStart + 95, sepColor, sepStroke);
      sepMarker.setLabel(rooms[4].getId());
      sepMarker.setLabelOffset(sepLabelInsets);
      sepMarker.setLabelFont(sepLabelFont);
      plot.addDomainMarker(sepMarker);
    }
    if (rooms[4].getId() != rooms[5].getId()) {
      sepMarker = new ValueMarker(finalStart + 119, sepColor, sepStroke);
      sepMarker.setLabel(rooms[5].getId());
      sepMarker.setLabelOffset(sepLabelInsets);
      sepMarker.setLabelFont(sepLabelFont);
      plot.addDomainMarker(sepMarker);
    }
    if (rooms[5].getId() != rooms[6].getId()) {
      sepMarker = new ValueMarker(finalStart + 143, sepColor, sepStroke);
      sepMarker.setLabel(rooms[6].getId());
      sepMarker.setLabelOffset(sepLabelInsets);
      sepMarker.setLabelFont(sepLabelFont);
      plot.addDomainMarker(sepMarker);
    }
    double positiveCellarDeviation = campaign.getCellarAverage()
        + campaign.getCellarDeviation();
    double negativeCellarDeviation = campaign.getCellarAverage()
        - campaign.getCellarDeviation();
    IntervalMarker cellarDeviation = new IntervalMarker(
        negativeCellarDeviation, positiveCellarDeviation);
    float[] dash = { 5, 3 };
    cellarDeviation.setPaint(new Color(222, 222, 255, 128));
    cellarDeviation.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 1, dash, 0));
    plot.addRangeMarker(cellarDeviation, Layer.BACKGROUND);
    ValueMarker arithCellarMarker = new ValueMarker(
        campaign.getCellarAverage(), new Color(0, 0, 255, 128),
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1,
            dash, 0));
    plot.addRangeMarker(arithCellarMarker);
    XYTextAnnotation amCellarLabel = new XYTextAnnotation("C_AM="
        + (int) campaign.getCellarAverage(), finalStart + count,
        campaign.getCellarAverage() * 1.01);
    plot.addAnnotation(amCellarLabel);
    XYTextAnnotation sdCellarLabel = new XYTextAnnotation("C_SD="
        + (int) campaign.getCellarDeviation(), finalStart + count,
        (campaign.getCellarAverage() + campaign.getCellarDeviation()) * 1.01);
    plot.addAnnotation(sdCellarLabel);
    ValueMarker maxiCellarMarker = new ValueMarker(cellarMaximum, new Color(0,
        0, 255, 128), new BasicStroke(1, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 1, dash, 0));
    plot.addRangeMarker(maxiCellarMarker);
    XYTextAnnotation maxCellarLabel = new XYTextAnnotation("C_MAX="
        + (int) cellarMaximum, finalStart + count, cellarMaximum * 1.01);
    plot.addAnnotation(maxCellarLabel);
    XYPointerAnnotation maxCellarPointer = new XYPointerAnnotation("",
        finalStart + cellarMaximumKey, cellarMaximum, Math.PI * 1.1);
    plot.addAnnotation(maxCellarPointer);
    double positiveRoomDeviation = campaign.getRoomAverage()
        + campaign.getRoomDeviation();
    double negativeRoomDeviation = campaign.getRoomAverage()
        - campaign.getRoomDeviation();
    IntervalMarker roomDeviation = new IntervalMarker(negativeRoomDeviation,
        positiveRoomDeviation);
    roomDeviation.setPaint(new Color(255, 222, 222, 128));
    roomDeviation.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 1, dash, 0));
    plot.addRangeMarker(roomDeviation, Layer.BACKGROUND);
    ValueMarker arithRoomMarker = new ValueMarker(campaign.getRoomAverage(),
        new Color(255, 0, 0, 128), new BasicStroke(1, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 1, dash, 0));
    plot.addRangeMarker(arithRoomMarker);
    XYTextAnnotation amRoomLabel = new XYTextAnnotation("R_AM="
        + (int) campaign.getRoomAverage(), finalStart + count,
        campaign.getRoomAverage() * 1.01);
    plot.addAnnotation(amRoomLabel);
    XYTextAnnotation sdRoomLabel = new XYTextAnnotation("R_SD="
        + (int) campaign.getRoomDeviation(), finalStart + count,
        (campaign.getRoomAverage() + campaign.getRoomDeviation()) * 1.01);
    plot.addAnnotation(sdRoomLabel);
    ValueMarker maxiRoomMarker = new ValueMarker(roomMaximum, new Color(255, 0,
        0, 128), new BasicStroke(1, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 1, dash, 0));
    plot.addRangeMarker(maxiRoomMarker);
    XYTextAnnotation maxRoomLabel = new XYTextAnnotation("R_MAX="
        + (int) roomMaximum, finalStart + count, roomMaximum * 1.01);
    plot.addAnnotation(maxRoomLabel);
    XYPointerAnnotation maxRoomPointer = new XYPointerAnnotation("", finalStart
        + roomMaximumKey, roomMaximum, Math.PI * 1.1);
    plot.addAnnotation(maxRoomPointer);
    XYItemRenderer renderer = plot.getRenderer();
    renderer.setSeriesPaint(0, new Color(255, 0, 0, 128));
    renderer.setSeriesPaint(1, new Color(0, 0, 255, 128));
    renderer.setSeriesPaint(2, new Color(255, 0, 0, 128));
    if (preview) {
      chart.setTitle("");
      plot.clearAnnotations();
    }
    return chart;
  }

  /**
   * Creates a chart displaying the distribution of certain selected statistical
   * values. Uses red for normal rooms and blue for cellar rooms.
   * 
   * @param title
   *          The headline of the chart. Will be hidden if set to null.
   * @param statistics
   *          The selected statistics of a campaign containing all needed
   *          values.
   * @param roomType
   *          The room type to determine the colour of the chart.
   * @param preview
   *          Will hide annotations, labels and headlines if true.
   * @return A chart displaying the distribution of certain selected statistical
   *         values.
   */
  public static JFreeChart createDistributionChart(String title,
      DescriptiveStatistics statistics, OMRoomType roomType, boolean preview) {
    Color lineColor = new Color(0, 0, 0, 128);
    Color rangeColor = new Color(222, 222, 222, 128);
    if (roomType == OMRoomType.Room) {
      lineColor = new Color(255, 0, 0, 128);
      rangeColor = new Color(255, 222, 222, 128);
    } else {
      if (roomType == OMRoomType.Cellar) {
        lineColor = new Color(0, 0, 255, 128);
        rangeColor = new Color(222, 222, 255, 128);
      } else {
        lineColor = new Color(0, 128, 0, 255);
        rangeColor = new Color(222, 255, 222, 128);
      }
    }
    double[] distValues = statistics.getSortedValues();
    XYSeriesCollection dataSet = new XYSeriesCollection();
    XYSeries distSeries = new XYSeries("Distribution");
    for (int i = 0; i < distValues.length; i++) {
      distSeries.add(distValues[i], (0.5 + (double) i)
          / (double) distValues.length);
    }
    dataSet.addSeries(distSeries);
    JFreeChart chart = ChartFactory.createXYLineChart(title, "Rn [Bq/m\u00B3]",
        "F(emp)", dataSet, PlotOrientation.VERTICAL, false, true, false);
    XYPlot plot = (XYPlot) chart.getPlot();
    float[] dash = { 5, 3 };
    int pos = 0;
    double y = (Double) distSeries.getY(pos);
    XYPointerAnnotation minPointer = new XYPointerAnnotation("MIN="
        + (int) distValues[pos], distValues[pos], y, Math.PI * 1.5);
    plot.addAnnotation(minPointer);
    pos = (int) (((double) distValues.length / 100. * 5.0) - 1.0);
    while (pos < 0) {
      pos++;
    }
    if (pos > 0) {
      y = (Double) distSeries.getY(pos);
    } else {
      y = (Double) distSeries.getY(pos + 1);
    }
    final double posQ5 = distValues[pos];
    XYPointerAnnotation q05Pointer = new XYPointerAnnotation("Q5="
        + (int) distValues[pos], distValues[pos], y, Math.PI * 1.5);
    plot.addAnnotation(q05Pointer);
    pos = (int) (((double) distValues.length / 2.0) - 1.0);
    y = (Double) distSeries.getY(pos);
    XYPointerAnnotation q50Pointer = new XYPointerAnnotation("Q50="
        + (int) distValues[pos], distValues[pos], y, Math.PI * 1.5);
    plot.addAnnotation(q50Pointer);
    ValueMarker medMarker = new ValueMarker(distValues[pos], lineColor,
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1,
            dash, 0));
    plot.addDomainMarker(medMarker);
    pos = (int) (((double) distValues.length / 100.0 * 95.0) - 1.0);
    if (pos < distValues.length - 1) {
      y = (Double) distSeries.getY(pos);
    } else {
      y = (Double) distSeries.getY(pos - 1);
    }
    final double posQ95 = distValues[pos];
    XYPointerAnnotation q95Pointer = new XYPointerAnnotation("Q95="
        + (int) distValues[pos], distValues[pos], y, Math.PI * 0.5);
    plot.addAnnotation(q95Pointer);
    pos = distValues.length - 1;
    y = (Double) distSeries.getY(pos);
    XYPointerAnnotation maxPointer = new XYPointerAnnotation("MAX="
        + (int) distValues[pos], distValues[pos], y, Math.PI * 0.5);
    plot.addAnnotation(maxPointer);
    IntervalMarker percentiles = new IntervalMarker(posQ5, posQ95);
    percentiles.setPaint(rangeColor);
    percentiles.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 1, dash, 0));
    plot.addDomainMarker(percentiles, Layer.BACKGROUND);
    XYItemRenderer renderer = plot.getRenderer();
    renderer.setSeriesPaint(0, lineColor);
    return chart;
  }
}
