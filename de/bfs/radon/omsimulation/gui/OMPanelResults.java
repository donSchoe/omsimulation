package de.bfs.radon.omsimulation.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
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
import de.bfs.radon.omsimulation.data.OMSimulation;
import de.bfs.radon.omsimulation.gui.data.OMCharts;
import de.bfs.radon.omsimulation.gui.data.OMExports;
import de.bfs.radon.omsimulation.gui.data.OMStatistics;

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

public class OMPanelResults extends JPanel {
  private static final long serialVersionUID = -6271878950235202218L;
  private String OmsFile;
  private JLabel lblSelectSimulation;
  private JLabel lblSelectStatistics;
  private JButton btnRefresh;
  private JComboBox<OMSimulation> comboBoxSimulations;
  private JProgressBar progressBar;
  private Refresh refreshTask;
  private JPanel panelDistribution;
  private JPanel panelChart;
  private JComboBox<OMStatistics> comboBoxStatistics;
  private JLabel lblHelp;
  private JLabel lblSelectOms;
  private JTextField txtOmsFile;
  private JButton buttonBrowse;
  private JButton btnCsv;
  private JButton btnPdf;
  private JLabel lblExportChartTo;
  private JButton btnMaximize;

  public String getOmsFile() {
    return OmsFile;
  }

  public void setOmsFile(String omsFile) {
    OmsFile = omsFile;
  }

  class Refresh extends SwingWorker<Void, Void> {

    private void tmpUpdate(String s, int i) {
      progressBar.setString(s);
      progressBar.setValue(i);
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignore) {
      }
    }

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

    @Override
    public void done() {
      Toolkit.getDefaultToolkit().beep();
      btnRefresh.setEnabled(true);
      comboBoxSimulations.setEnabled(true);
      tmpUpdate(" ", 0);
      progressBar.setStringPainted(false);
      progressBar.setValue(0);
      progressBar.setVisible(false);
      setCursor(null);
    }
  }

  /**
   * Create the panel.
   */
  public OMPanelResults() {
    initialize();
  }

  protected void initialize() {
    setLayout(null);

    lblExportChartTo = new JLabel("Export chart to ...");
    lblExportChartTo.setBounds(436, 476, 144, 14);
    lblExportChartTo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblExportChartTo.setVisible(false);
    add(lblExportChartTo);

    btnMaximize = new JButton("Fullscreen");
    btnMaximize.setBounds(10, 472, 124, 23);
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
            switch (statisticsType) {
            case RoomArithmeticMeans:
              title = "R_AM, " + title;
              statistics = simulation.getRoomAmDescriptiveStats();
              break;
            case RoomGeometricMeans:
              title = "R_GM, " + title;
              statistics = simulation.getRoomGmDescriptiveStats();
              break;
            case RoomMedianQ50:
              title = "R_MED, " + title;
              statistics = simulation.getRoomMedDescriptiveStats();
              break;
            case RoomMaxima:
              title = "R_MAX, " + title;
              statistics = simulation.getRoomMaxDescriptiveStats();
              break;
            case CellarArithmeticMeans:
              title = "C_AM, " + title;
              statistics = simulation.getCellarAmDescriptiveStats();
              break;
            case CellarGeometricMeans:
              title = "C_GM, " + title;
              statistics = simulation.getCellarGmDescriptiveStats();
              break;
            case CellarMedianQ50:
              title = "C_MED, " + title;
              statistics = simulation.getCellarMedDescriptiveStats();
              break;
            case CellarMaxima:
              title = "C_MAX, " + title;
              statistics = simulation.getCellarMaxDescriptiveStats();
              break;
            default:
              title = "R_AM, " + title;
              statistics = simulation.getRoomAmDescriptiveStats();
              break;
            }
            JPanel chartPanel = createDistributionPanel(title, statistics,
                false, false);
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
        fileDialog.showOpenDialog(getParent());
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
          }
        } else {
          JOptionPane.showMessageDialog(null,
              "Failed to write CSV. Please check the file path!", "Failed",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    btnCsv.setBounds(590, 472, 70, 23);
    btnCsv.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnCsv.setVisible(false);
    add(btnCsv);

    btnPdf = new JButton("PDF");
    btnPdf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.pdf", "pdf"));
        fileDialog.showOpenDialog(getParent());
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
          switch (statisticsType) {
          case RoomArithmeticMeans:
            title = "R_AM, " + title;
            statistics = simulation.getRoomAmDescriptiveStats();
            break;
          case RoomGeometricMeans:
            title = "R_GM, " + title;
            statistics = simulation.getRoomGmDescriptiveStats();
            break;
          case RoomMedianQ50:
            title = "R_MED, " + title;
            statistics = simulation.getRoomMedDescriptiveStats();
            break;
          case RoomMaxima:
            title = "R_MAX, " + title;
            statistics = simulation.getRoomMaxDescriptiveStats();
            break;
          case CellarArithmeticMeans:
            title = "C_AM, " + title;
            statistics = simulation.getCellarAmDescriptiveStats();
            break;
          case CellarGeometricMeans:
            title = "C_GM, " + title;
            statistics = simulation.getCellarGmDescriptiveStats();
            break;
          case CellarMedianQ50:
            title = "C_MED, " + title;
            statistics = simulation.getCellarMedDescriptiveStats();
            break;
          case CellarMaxima:
            title = "C_MAX, " + title;
            statistics = simulation.getCellarMaxDescriptiveStats();
            break;
          default:
            title = "R_AM, " + title;
            statistics = simulation.getRoomAmDescriptiveStats();
            break;
          }
          JFreeChart chart = OMCharts.createDistributionChart(title,
              statistics, false);
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
          }
        } else {
          JOptionPane.showMessageDialog(null,
              "Failed to write PDF. Please check the file path!", "Failed",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    btnPdf.setBounds(670, 472, 70, 23);
    btnPdf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnPdf.setVisible(false);
    add(btnPdf);

    lblSelectSimulation = new JLabel("Select Simulation");
    lblSelectSimulation.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblSelectSimulation.setBounds(10, 64, 132, 14);
    add(lblSelectSimulation);

    lblSelectStatistics = new JLabel("Select Statistics");
    lblSelectStatistics.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblSelectStatistics.setBounds(10, 89, 132, 14);
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
    comboBoxSimulations.setBounds(152, 60, 454, 22);
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
            refreshTask = new Refresh();
            refreshTask.execute();
          } else {
            txtOmsFile.setBackground(new Color(255, 222, 222, 128));
            JOptionPane.showMessageDialog(null,
                "OMS-file not found, please check the file path!",
                "Error", JOptionPane.ERROR_MESSAGE);
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
    comboBoxStatistics.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        updateDistribution();
      }
    });
    comboBoxStatistics.setBounds(152, 85, 454, 22);
    comboBoxStatistics.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        updateDistribution();
      }
    });
    add(comboBoxStatistics);

    btnRefresh.setBounds(616, 60, 124, 23);
    add(btnRefresh);

    panelDistribution = new JPanel();
    panelDistribution.setBounds(10, 114, 730, 352);
    add(panelDistribution);

    progressBar = new JProgressBar();
    progressBar.setFont(new Font("SansSerif", Font.PLAIN, 11));
    progressBar.setBounds(10, 477, 730, 22);
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
    lblSelectOms.setBounds(10, 35, 132, 14);
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
    txtOmsFile.setBounds(152, 32, 454, 20);
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
    buttonBrowse.setBounds(616, 31, 124, 23);
    add(buttonBrowse);
    progressBar.setVisible(false);
  }

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
    refreshTask = new Refresh();
    refreshTask.execute();
  }

  public JPanel createDistributionPanel(String title,
      DescriptiveStatistics statistics, boolean preview, boolean mouseEvent) {
    JFreeChart chart = OMCharts.createDistributionChart(title, statistics,
        preview);
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(730, 352));
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
            JTabbedPane tab = (JTabbedPane) getParent();
            tab.remove(tab.getComponentAt(4));
            JPanel jpanelTesting = new OMPanelTesting(simulation, result);
            tab.add(jpanelTesting, "Analyse", 4);
            tab.updateUI();
            tab.setSelectedIndex(4);
          } catch (Exception excpt) {
          } // ignore
        }

        @Override
        public void chartMouseMoved(ChartMouseEvent e) {
        }
      });
    }
    JPanel distPanel = (JPanel) chartPanel;
    return distPanel;
  }

  protected void updateDistribution() {
    if (comboBoxStatistics.isEnabled()) {
      if (comboBoxStatistics.getSelectedItem() != null) {
        OMSimulation simulation = (OMSimulation) comboBoxSimulations
            .getSelectedItem();
        String title = simulation.toString();
        DescriptiveStatistics statistics = null;
        OMStatistics statisticsType = (OMStatistics) comboBoxStatistics
            .getSelectedItem();
        switch (statisticsType) {
        case RoomArithmeticMeans:
          title = "R_AM, " + title;
          statistics = simulation.getRoomAmDescriptiveStats();
          break;
        case RoomGeometricMeans:
          title = "R_GM, " + title;
          statistics = simulation.getRoomGmDescriptiveStats();
          break;
        case RoomMedianQ50:
          title = "R_MED, " + title;
          statistics = simulation.getRoomMedDescriptiveStats();
          break;
        case RoomMaxima:
          title = "R_MAX, " + title;
          statistics = simulation.getRoomMaxDescriptiveStats();
          break;
        case CellarArithmeticMeans:
          title = "C_AM, " + title;
          statistics = simulation.getCellarAmDescriptiveStats();
          break;
        case CellarGeometricMeans:
          title = "C_GM, " + title;
          statistics = simulation.getCellarGmDescriptiveStats();
          break;
        case CellarMedianQ50:
          title = "C_MED, " + title;
          statistics = simulation.getCellarMedDescriptiveStats();
          break;
        case CellarMaxima:
          title = "C_MAX, " + title;
          statistics = simulation.getCellarMaxDescriptiveStats();
          break;
        default:
          title = "R_AM, " + title;
          statistics = simulation.getRoomAmDescriptiveStats();
          break;
        }
        panelChart = createDistributionPanel(title, statistics, false, true);
        remove(panelDistribution);
        panelDistribution = new JPanel();
        panelDistribution.setBounds(10, 114, 730, 352);
        panelDistribution.add(panelChart);
        add(panelDistribution);
        updateUI();
      }
    }
  }

}
