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

package de.bfs.radon.omsimulation.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.xy.XYDataset;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.PageSize;

import de.bfs.radon.omsimulation.data.OMCampaign;
import de.bfs.radon.omsimulation.data.OMRoomType;
import de.bfs.radon.omsimulation.data.OMSimulation;
import de.bfs.radon.omsimulation.gui.data.OMCharts;
import de.bfs.radon.omsimulation.gui.data.OMExports;
import de.bfs.radon.omsimulation.gui.data.OMStatistics;

/**
 * Creates and shows the results panel for this software tool. Allows the user
 * to inspect the simulation results and to view distribution charts.
 * 
 * @author A. Schoedon
 * 
 */
public class OMPanelResults extends JPanel {

  /**
   * Unique serial version ID.
   */
  private static final long serialVersionUID = -6271878950235202218L;

  /**
   * Stores the absolute path to the OMS object which will be analysed in this
   * panel.
   */
  private String omsFile;

  /**
   * UI: Label "Select Simulation"
   */
  private JLabel lblSelectSimulation;

  /**
   * UI: Label "Select Statistics"
   */
  private JLabel lblSelectStatistics;

  /**
   * UI: Label for first orientation, content: "Select an OMS-Simulation file to
   * analyse the simulation results and display the distribution chart."
   */
  private JLabel lblHelp;

  /**
   * UI: Label "Select OMS-File"
   */
  private JLabel lblSelectOms;

  /**
   * UI: Label "Export chart to..."
   */
  private JLabel lblExportChartTo;

  /**
   * UI: Text field to enter the absolute path to the OMS object.
   */
  private JTextField txtOmsFile;

  /**
   * UI: Button to load the selected OMS file to the panel.
   */
  private JButton btnRefresh;

  /**
   * UI: Button to open a file browser to save an OMS file.
   */
  private JButton buttonBrowse;

  /**
   * UI: Button to export the chart to CSV.
   */
  private JButton btnCsv;

  /**
   * UI: Button to export the chart to PDF.
   */
  private JButton btnPdf;

  /**
   * UI: Button to display the chart in fullscreen mode.
   */
  private JButton btnMaximize;

  /**
   * UI: Combobox to select a simulation to analyse.
   */
  private JComboBox<OMSimulation> comboBoxSimulations;

  /**
   * UI: Combobox to select a certain statistics type to analyse.
   */
  private JComboBox<OMStatistics> comboBoxStatistics;

  /**
   * UI: Progress bar to display the status of certain actions performed on this
   * panel.
   */
  private JProgressBar progressBar;

  /**
   * UI: Panel where the statistics distribution chart is drawn to.
   */
  private JPanel panelDistribution;

  /**
   * UI: Panel which is used as a place holder for the chart.
   */
  private JPanel panelChart;

  /**
   * Stores the task to load OMS files to the panel which will be executed in a
   * separate thread to ensure the UI wont freeze.
   */
  private RefreshSimulations refreshSimulationsTask;

  /**
   * Stores the task to update the charts which will be executed in a separate
   * thread to ensure the UI wont freeze.
   */
  private RefreshCharts refreshChartsTask;

  /**
   * Gets the absolute path to the OMS object which will be analysed in this
   * panel.
   * 
   * @return The absolute path to the OMS object.
   */
  public String getOmsFile() {
    return this.omsFile;
  }

  /**
   * Sets the absolute path to the OMS object which will be analysed in this
   * panel.
   * 
   * @param omsFile
   *          The absolute path to the OMS object.
   */
  public void setOmsFile(String omsFile) {
    this.omsFile = omsFile;
  }

  /**
   * The inner class RefreshCharts used to update the charts which will be
   * executed in a separate thread to ensure the UI wont freeze.
   * 
   * @author A. Schoedon
   */
  class RefreshCharts extends SwingWorker<Void, Void> {

    /**
     * Updates the chart panel with the distribution of the selected statistics.
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Void doInBackground() throws Exception {
      OMSimulation simulation = (OMSimulation) comboBoxSimulations
          .getSelectedItem();
      String title = simulation.toString();
      DescriptiveStatistics statistics = null;
      OMStatistics statisticsType = (OMStatistics) comboBoxStatistics
          .getSelectedItem();
      OMRoomType roomType = null;
      switch (statisticsType) {
      case RoomArithmeticMeans:
        title = "R_AM, " + title;
        statistics = simulation.getRoomAmDescriptiveStats();
        roomType = OMRoomType.Room;
        break;
      case RoomGeometricMeans:
        title = "R_GM, " + title;
        statistics = simulation.getRoomGmDescriptiveStats();
        roomType = OMRoomType.Room;
        break;
      case RoomMedianQ50:
        title = "R_MED, " + title;
        statistics = simulation.getRoomMedDescriptiveStats();
        roomType = OMRoomType.Room;
        break;
      case RoomMaxima:
        title = "R_MAX, " + title;
        statistics = simulation.getRoomMaxDescriptiveStats();
        roomType = OMRoomType.Room;
        break;
      case CellarArithmeticMeans:
        title = "C_AM, " + title;
        statistics = simulation.getCellarAmDescriptiveStats();
        roomType = OMRoomType.Cellar;
        break;
      case CellarGeometricMeans:
        title = "C_GM, " + title;
        statistics = simulation.getCellarGmDescriptiveStats();
        roomType = OMRoomType.Cellar;
        break;
      case CellarMedianQ50:
        title = "C_MED, " + title;
        statistics = simulation.getCellarMedDescriptiveStats();
        roomType = OMRoomType.Cellar;
        break;
      case CellarMaxima:
        title = "C_MAX, " + title;
        statistics = simulation.getCellarMaxDescriptiveStats();
        roomType = OMRoomType.Cellar;
        break;
      default:
        title = "R_AM, " + title;
        statistics = simulation.getRoomAmDescriptiveStats();
        roomType = OMRoomType.Misc;
        break;
      }
      panelChart = createDistributionPanel(title, statistics, roomType, false,
          true);
      panelDistribution = new JPanel();
      panelDistribution.setBounds(10, 118, 730, 347);
      panelDistribution.add(panelChart);
      return null;
    }

    /**
     * Executed in event dispatching thread after finishing the refresh task.
     * Updates the interface and adds the new chart panel.
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    public void done() {
      add(panelDistribution);
      btnPdf.setVisible(true);
      btnCsv.setVisible(true);
      lblExportChartTo.setVisible(true);
      comboBoxStatistics.setEnabled(true);
      updateUI();
      setCursor(null);
    }
  }

  /**
   * The inner class RefreshSimulations used load OMS files to the panel which
   * will be executed in a separate thread to ensure the UI wont freeze.
   * 
   * @author A. Schoedon
   */
  class RefreshSimulations extends SwingWorker<Void, Void> {

    /**
     * Updates the progress bar status and message.
     * 
     * @param s
     *          The log message.
     * @param i
     *          The status in percent.
     */
    private void tmpUpdate(String s, int i) {
      progressBar.setString(s);
      progressBar.setValue(i);
      try {
        Thread.sleep(100);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }

    /**
     * Loads the object from the OMS file to the panel.
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    public Void doInBackground() {
      tmpUpdate("Getting objects from Database... ", 1);
      ObjectContainer db4o = Db4oEmbedded.openFile(
          Db4oEmbedded.newConfiguration(), getOmsFile());
      ObjectSet<OMSimulation> result = db4o.queryByExample(OMSimulation.class);
      OMSimulation found;
      tmpUpdate("Refreshing list... ", 2);
      tmpUpdate("Adding items... ", 3);
      for (int i = 0; i < result.size(); i++) {
        double perc = (double) i / (double) result.size() * 100.0 + 3.0;
        while (perc > 99) {
          perc--;
        }
        found = (OMSimulation) result.next();
        comboBoxSimulations.addItem(found);
        tmpUpdate("Added: " + found, (int) perc);
      }
      tmpUpdate("Finished. ", 100);
      db4o.close();
      return null;
    }

    /**
     * Executed in event dispatching thread after finishing the refresh task,
     * updates the interface and the distribution chart.
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    public void done() {
      btnRefresh.setEnabled(true);
      comboBoxSimulations.setEnabled(true);
      tmpUpdate(" ", 0);
      progressBar.setStringPainted(false);
      progressBar.setValue(0);
      progressBar.setVisible(false);
      setCursor(null);
      updateDistribution();
    }
  }

  /**
   * Initializes the interface of the results panel without any preloaded
   * objects.
   */
  public OMPanelResults() {
    initialize();
  }

  /**
   * Initializes the interface of the results panel with a preloaded object from
   * simulation panel. Launching a refresh task in background.
   * 
   * @param oms
   *          Absolute path to an OMS object file to load on init.
   */
  public OMPanelResults(String oms) {
    initialize();
    setOmsFile(oms);
    txtOmsFile.setText(oms);
    btnRefresh.setEnabled(false);
    comboBoxSimulations.setEnabled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    progressBar.setStringPainted(true);
    progressBar.setVisible(true);
    progressBar.setIndeterminate(true);
    btnPdf.setVisible(false);
    btnCsv.setVisible(false);
    btnMaximize.setVisible(false);
    lblExportChartTo.setVisible(false);
    refreshSimulationsTask = new RefreshSimulations();
    refreshSimulationsTask.execute();
  }

  /**
   * Initializes the interface of the results panel.
   */
  protected void initialize() {
    setLayout(null);

    lblExportChartTo = new JLabel("Export chart to ...");
    lblExportChartTo.setBounds(436, 479, 144, 14);
    lblExportChartTo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblExportChartTo.setVisible(false);
    add(lblExportChartTo);

    btnMaximize = new JButton("Fullscreen");
    btnMaximize.setBounds(10, 475, 124, 23);
    btnMaximize.setFont(new Font("SansSerif", Font.PLAIN, 11));
    btnMaximize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (comboBoxSimulations.isEnabled()) {
          if (comboBoxSimulations.getSelectedItem() != null) {
            JFrame chartFrame = new JFrame();
            OMSimulation simulation = (OMSimulation) comboBoxSimulations
                .getSelectedItem();
            String title = simulation.toString();
            DescriptiveStatistics statistics = null;
            OMStatistics statisticsType = (OMStatistics) comboBoxStatistics
                .getSelectedItem();
            OMRoomType roomType = null;
            switch (statisticsType) {
            case RoomArithmeticMeans:
              title = "R_AM, " + title;
              statistics = simulation.getRoomAmDescriptiveStats();
              roomType = OMRoomType.Room;
              break;
            case RoomGeometricMeans:
              title = "R_GM, " + title;
              statistics = simulation.getRoomGmDescriptiveStats();
              roomType = OMRoomType.Room;
              break;
            case RoomMedianQ50:
              title = "R_MED, " + title;
              statistics = simulation.getRoomMedDescriptiveStats();
              roomType = OMRoomType.Room;
              break;
            case RoomMaxima:
              title = "R_MAX, " + title;
              statistics = simulation.getRoomMaxDescriptiveStats();
              roomType = OMRoomType.Room;
              break;
            case CellarArithmeticMeans:
              title = "C_AM, " + title;
              statistics = simulation.getCellarAmDescriptiveStats();
              roomType = OMRoomType.Cellar;
              break;
            case CellarGeometricMeans:
              title = "C_GM, " + title;
              statistics = simulation.getCellarGmDescriptiveStats();
              roomType = OMRoomType.Cellar;
              break;
            case CellarMedianQ50:
              title = "C_MED, " + title;
              statistics = simulation.getCellarMedDescriptiveStats();
              roomType = OMRoomType.Cellar;
              break;
            case CellarMaxima:
              title = "C_MAX, " + title;
              statistics = simulation.getCellarMaxDescriptiveStats();
              roomType = OMRoomType.Cellar;
              break;
            default:
              title = "R_AM, " + title;
              statistics = simulation.getRoomAmDescriptiveStats();
              roomType = OMRoomType.Misc;
              break;
            }
            JPanel chartPanel = createDistributionPanel(title, statistics,
                roomType, false, true);
            chartFrame.getContentPane().add(chartPanel);
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.setBounds(10, 61, 730, 404);
            chartFrame.setTitle("OM Simulation Tool: " + title);
            chartFrame.setResizable(true);
            chartFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            chartFrame.setVisible(true);
          }
        }
      }
    });
    add(btnMaximize);

    btnCsv = new JButton("CSV");
    btnCsv.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.csv", "csv"));
        fileDialog.showSaveDialog(getParent());
        final File file = fileDialog.getSelectedFile();
        if (file != null) {
          String csv;
          String[] tmpFileName = file.getAbsolutePath().split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("csv")) {
            csv = "";
          } else {
            csv = ".csv";
          }
          String csvPath = file.getAbsolutePath() + csv;
          OMSimulation simulation = (OMSimulation) comboBoxSimulations
              .getSelectedItem();
          OMCampaign[] campaigns = simulation.getCampaigns();
          File csvFile = new File(csvPath);
          try {
            FileWriter logWriter = new FileWriter(csvFile);
            BufferedWriter csvOutput = new BufferedWriter(logWriter);
            csvOutput
                .write("\"ID\";\"CAMPAIGN\";\"START\";\"R_AM\";\"R_GM\";\"R_MED\";\"R_MAX\";\"C_AM\";\"C_GM\";\"C_MED\";\"C_MAX\"");
            csvOutput.newLine();
            for (int i = 0; i < campaigns.length; i++) {
              csvOutput.write("\"" + i + "\";\"" + campaigns[i].getVariation()
                  + "\";\"" + campaigns[i].getStart() + "\";\""
                  + (int) campaigns[i].getRoomAvarage() + "\";\""
                  + (int) campaigns[i].getRoomLogAvarage() + "\";\""
                  + (int) campaigns[i].getRoomMedian() + "\";\""
                  + (int) campaigns[i].getRoomMaxima() + "\";\""
                  + (int) campaigns[i].getCellarAvarage() + "\";\""
                  + (int) campaigns[i].getCellarLogAvarage() + "\";\""
                  + (int) campaigns[i].getCellarMedian() + "\";\""
                  + (int) campaigns[i].getCellarMaxima() + "\"");
              csvOutput.newLine();
              if (campaigns.length > 65535) {
                i++;
              }
            }
            JOptionPane.showMessageDialog(null, "CSV saved successfully!\n"
                + csvPath, "Success", JOptionPane.INFORMATION_MESSAGE);
            csvOutput.close();
          } catch (IOException ioe) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to write CSV. Please check permissions!\n"
                    + ioe.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
          }
        } else {
          JOptionPane.showMessageDialog(null,
              "Failed to write CSV. Please check the file path!", "Failed",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    btnCsv.setBounds(590, 475, 70, 23);
    btnCsv.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnCsv.setVisible(false);
    add(btnCsv);

    btnPdf = new JButton("PDF");
    btnPdf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.pdf", "pdf"));
        fileDialog.showSaveDialog(getParent());
        final File file = fileDialog.getSelectedFile();
        if (file != null) {
          String pdf;
          String[] tmpFileName = file.getAbsolutePath().split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("pdf")) {
            pdf = "";
          } else {
            pdf = ".pdf";
          }
          String pdfPath = file.getAbsolutePath() + pdf;
          OMSimulation simulation = (OMSimulation) comboBoxSimulations
              .getSelectedItem();
          String title = simulation.toString();
          DescriptiveStatistics statistics = null;
          OMStatistics statisticsType = (OMStatistics) comboBoxStatistics
              .getSelectedItem();
          OMRoomType roomType = null;
          switch (statisticsType) {
          case RoomArithmeticMeans:
            title = "R_AM, " + title;
            statistics = simulation.getRoomAmDescriptiveStats();
            roomType = OMRoomType.Room;
            break;
          case RoomGeometricMeans:
            title = "R_GM, " + title;
            statistics = simulation.getRoomGmDescriptiveStats();
            roomType = OMRoomType.Room;
            break;
          case RoomMedianQ50:
            title = "R_MED, " + title;
            statistics = simulation.getRoomMedDescriptiveStats();
            roomType = OMRoomType.Room;
            break;
          case RoomMaxima:
            title = "R_MAX, " + title;
            statistics = simulation.getRoomMaxDescriptiveStats();
            roomType = OMRoomType.Room;
            break;
          case CellarArithmeticMeans:
            title = "C_AM, " + title;
            statistics = simulation.getCellarAmDescriptiveStats();
            roomType = OMRoomType.Cellar;
            break;
          case CellarGeometricMeans:
            title = "C_GM, " + title;
            statistics = simulation.getCellarGmDescriptiveStats();
            roomType = OMRoomType.Cellar;
            break;
          case CellarMedianQ50:
            title = "C_MED, " + title;
            statistics = simulation.getCellarMedDescriptiveStats();
            roomType = OMRoomType.Cellar;
            break;
          case CellarMaxima:
            title = "C_MAX, " + title;
            statistics = simulation.getCellarMaxDescriptiveStats();
            roomType = OMRoomType.Cellar;
            break;
          default:
            title = "R_AM, " + title;
            statistics = simulation.getRoomAmDescriptiveStats();
            roomType = OMRoomType.Misc;
            break;
          }
          JFreeChart chart = OMCharts.createDistributionChart(title,
              statistics, roomType, false);
          int height = (int) PageSize.A4.getWidth();
          int width = (int) PageSize.A4.getHeight();
          try {
            OMExports.exportPdf(pdfPath, chart, width, height,
                new DefaultFontMapper(), title);
            JOptionPane.showMessageDialog(null, "PDF saved successfully!\n"
                + pdfPath, "Success", JOptionPane.INFORMATION_MESSAGE);
          } catch (IOException ioe) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to write PDF. Please check permissions!\n"
                    + ioe.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
          }
        } else {
          JOptionPane.showMessageDialog(null,
              "Failed to write PDF. Please check the file path!", "Failed",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    btnPdf.setBounds(670, 475, 70, 23);
    btnPdf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnPdf.setVisible(false);
    add(btnPdf);

    lblSelectSimulation = new JLabel("Select Simulation");
    lblSelectSimulation.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblSelectSimulation.setBounds(10, 65, 132, 14);
    add(lblSelectSimulation);

    lblSelectStatistics = new JLabel("Select Statistics");
    lblSelectStatistics.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblSelectStatistics.setBounds(10, 94, 132, 14);
    add(lblSelectStatistics);

    comboBoxSimulations = new JComboBox<OMSimulation>();
    comboBoxSimulations.setFont(new Font("SansSerif", Font.PLAIN, 11));
    comboBoxSimulations.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent arg0) {
        boolean b = false;
        if (comboBoxSimulations.isEnabled()) {
          if (comboBoxSimulations.getSelectedItem() != null) {
            b = true;
            comboBoxStatistics.removeAllItems();
            comboBoxStatistics.setModel(new DefaultComboBoxModel<OMStatistics>(
                OMStatistics.values()));
          } else {
            b = false;
            comboBoxStatistics.removeAllItems();
          }
        } else {
          b = false;
          comboBoxStatistics.removeAllItems();
        }
        progressBar.setEnabled(b);
        btnPdf.setVisible(b);
        btnCsv.setVisible(b);
        btnMaximize.setVisible(b);
        lblExportChartTo.setVisible(b);
        comboBoxStatistics.setEnabled(b);
        lblSelectStatistics.setEnabled(b);
      }
    });
    comboBoxSimulations.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        boolean b = false;
        if (comboBoxSimulations.isEnabled()) {
          if (comboBoxSimulations.getSelectedItem() != null) {
            b = true;
            comboBoxStatistics.removeAllItems();
            comboBoxStatistics.setModel(new DefaultComboBoxModel<OMStatistics>(
                OMStatistics.values()));
            comboBoxStatistics.setSelectedIndex(0);
          } else {
            b = false;
            comboBoxStatistics.removeAllItems();
          }
        } else {
          b = false;
          comboBoxStatistics.removeAllItems();
        }
        progressBar.setEnabled(b);
        btnPdf.setVisible(b);
        btnCsv.setVisible(b);
        btnMaximize.setVisible(b);
        lblExportChartTo.setVisible(b);
        comboBoxStatistics.setEnabled(b);
        lblSelectStatistics.setEnabled(b);
      }
    });
    comboBoxSimulations.setBounds(152, 61, 454, 22);
    add(comboBoxSimulations);

    btnRefresh = new JButton("Load");
    btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 11));
    btnRefresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (txtOmsFile.getText() != null && !txtOmsFile.getText().equals("")
            && !txtOmsFile.getText().equals(" ")) {
          txtOmsFile.setBackground(Color.WHITE);
          String omsPath = txtOmsFile.getText();
          String oms;
          String[] tmpFileName = omsPath.split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("oms")) {
            oms = "";
          } else {
            oms = ".oms";
          }
          txtOmsFile.setText(omsPath + oms);
          setOmsFile(omsPath + oms);
          File omsFile = new File(omsPath + oms);
          if (omsFile.exists()) {
            txtOmsFile.setBackground(Color.WHITE);
            btnRefresh.setEnabled(false);
            comboBoxSimulations.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            progressBar.setStringPainted(true);
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            btnPdf.setVisible(false);
            btnCsv.setVisible(false);
            btnMaximize.setVisible(false);
            lblExportChartTo.setVisible(false);
            refreshSimulationsTask = new RefreshSimulations();
            refreshSimulationsTask.execute();
          } else {
            txtOmsFile.setBackground(new Color(255, 222, 222, 128));
            JOptionPane.showMessageDialog(null,
                "OMS-file not found, please check the file path!", "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        } else {
          txtOmsFile.setBackground(new Color(255, 222, 222, 128));
          JOptionPane.showMessageDialog(null, "Please select an OMS-file!",
              "Warning", JOptionPane.WARNING_MESSAGE);
        }
      }
    });

    comboBoxStatistics = new JComboBox<OMStatistics>();
    comboBoxStatistics.setFont(new Font("SansSerif", Font.PLAIN, 11));
    comboBoxStatistics.setBounds(152, 90, 454, 22);
    comboBoxStatistics.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        updateDistribution();
      }
    });
    add(comboBoxStatistics);

    btnRefresh.setBounds(616, 61, 124, 23);
    add(btnRefresh);

    panelDistribution = new JPanel();
    panelDistribution.setBounds(10, 118, 730, 347);
    add(panelDistribution);

    progressBar = new JProgressBar();
    progressBar.setFont(new Font("SansSerif", Font.PLAIN, 11));
    progressBar.setBounds(10, 475, 730, 23);
    add(progressBar);

    progressBar.setEnabled(false);
    comboBoxStatistics.setEnabled(false);
    lblSelectStatistics.setEnabled(false);
    btnPdf.setVisible(false);
    btnCsv.setVisible(false);
    btnMaximize.setVisible(false);
    lblExportChartTo.setVisible(false);

    lblHelp = new JLabel(
        "Select an OMS-Simulation file to analyse the simulation results and display the distribution chart.");
    lblHelp.setForeground(Color.GRAY);
    lblHelp.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblHelp.setBounds(10, 10, 730, 14);
    add(lblHelp);

    lblSelectOms = new JLabel("Open OMS-File");
    lblSelectOms.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblSelectOms.setBounds(10, 36, 132, 14);
    add(lblSelectOms);

    txtOmsFile = new JTextField();
    txtOmsFile.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        setOmsFile(txtOmsFile.getText());
      }
    });
    txtOmsFile.setFont(new Font("SansSerif", Font.PLAIN, 11));
    txtOmsFile.setColumns(10);
    txtOmsFile.setBounds(152, 33, 454, 20);
    add(txtOmsFile);

    buttonBrowse = new JButton("Browse");
    buttonBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.oms", "oms"));
        fileDialog.showOpenDialog(getParent());
        final File file = fileDialog.getSelectedFile();
        if (file != null) {
          String oms;
          String[] tmpFileName = file.getAbsolutePath().split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("oms")) {
            oms = "";
          } else {
            oms = ".oms";
          }
          txtOmsFile.setText(file.getAbsolutePath() + oms);
          setOmsFile(file.getAbsolutePath() + oms);
        }
      }
    });
    buttonBrowse.setFont(new Font("SansSerif", Font.PLAIN, 11));
    buttonBrowse.setBounds(616, 32, 124, 23);
    add(buttonBrowse);
    progressBar.setVisible(false);
  }

  /**
   * Creates a panel displaying the distribution chart of certain selected
   * statistical values.
   * 
   * @param title
   *          The headline of the chart. Will be hidden if set to null.
   * @param statistics
   *          The selected statistics of a campaign containing all needed
   *          values.
   * @param roomType
   *          The room type to determine the color of the chart.
   * @param preview
   *          Will hide annotations, labels and headlines if set true.
   * @param mouseEvent
   *          Will enable mouseClickedEvent if set true. Use with care, and only
   *          inside the results panel. Set to false if you are unsure what you
   *          are doing.
   * @return A chart displaying the distribution of certain selected statistical
   *         values.
   */
  public JPanel createDistributionPanel(String title,
      DescriptiveStatistics statistics, OMRoomType roomType, boolean preview,
      boolean mouseEvent) {
    JFreeChart chart = OMCharts.createDistributionChart(title, statistics,
        roomType, preview);
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(730, 347));
    if (mouseEvent) {
      chartPanel.addChartMouseListener(new ChartMouseListener() {
        @Override
        public void chartMouseClicked(ChartMouseEvent e) {
          OMSimulation simulation = (OMSimulation) comboBoxSimulations
              .getSelectedItem();
          OMCampaign[] campaigns = simulation.getCampaigns();
          try {
            XYItemEntity entity = (XYItemEntity) e.getEntity();
            XYDataset dataset = entity.getDataset();
            int item = entity.getItem();
            double x = dataset.getXValue(0, item);
            double comparable = 0.0;
            OMCampaign result = null;
            OMStatistics selectedType = (OMStatistics) comboBoxStatistics
                .getSelectedItem();
            for (int i = 0; i < campaigns.length; i++) {
              switch (selectedType) {
              case RoomArithmeticMeans:
                comparable = campaigns[i].getRoomAvarage();
                break;
              case RoomGeometricMeans:
                comparable = campaigns[i].getRoomLogAvarage();
                break;
              case RoomMedianQ50:
                comparable = campaigns[i].getRoomMedian();
                break;
              case RoomMaxima:
                comparable = campaigns[i].getRoomMaxima();
                break;
              case CellarArithmeticMeans:
                comparable = campaigns[i].getCellarAvarage();
                break;
              case CellarGeometricMeans:
                comparable = campaigns[i].getCellarLogAvarage();
                break;
              case CellarMedianQ50:
                comparable = campaigns[i].getCellarMedian();
                break;
              case CellarMaxima:
                comparable = campaigns[i].getCellarMaxima();
                break;
              default:
                comparable = campaigns[i].getRoomAvarage();
                break;
              }
              if (comparable == x) {
                result = campaigns[i];
              }
            }
            if (result != null) {
              try {
                Thread.sleep(250);
              } catch (InterruptedException ie) {
                ie.printStackTrace();
              }
              JTabbedPane tab = (JTabbedPane) getParent();
              tab.remove(tab.getComponentAt(4));
              JPanel jpanelTesting = new OMPanelTesting(simulation, result);
              tab.add(jpanelTesting, "Analyse", 4);
              tab.updateUI();
              tab.setSelectedIndex(4);
            }
          } catch (Exception x) {
            x.printStackTrace();
          }
        }

        @Override
        public void chartMouseMoved(ChartMouseEvent ignore) {
        }
      });
    }
    JPanel distPanel = (JPanel) chartPanel;
    return distPanel;
  }

  /**
   * Updates the distribution chart in a background thread.
   */
  protected void updateDistribution() {
    if (comboBoxStatistics.isEnabled()) {
      if (comboBoxStatistics.getSelectedItem() != null) {
        comboBoxStatistics.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        remove(panelDistribution);
        refreshChartsTask = new RefreshCharts();
        refreshChartsTask.execute();
      }
    }
  }

}
