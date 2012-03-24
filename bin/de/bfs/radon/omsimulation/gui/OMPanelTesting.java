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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.PageSize;

import de.bfs.radon.omsimulation.data.OMBuilding;
import de.bfs.radon.omsimulation.data.OMCampaign;
import de.bfs.radon.omsimulation.data.OMRoom;
import de.bfs.radon.omsimulation.data.OMRoomType;
import de.bfs.radon.omsimulation.data.OMSimulation;
import de.bfs.radon.omsimulation.gui.data.OMCharts;
import de.bfs.radon.omsimulation.gui.data.OMExports;

/**
 * Creates and shows the analysing and testing panel for this software tool.
 * Allows the user to analyse the simulation results and to manually create
 * virtual campaigns.
 * 
 * @author A. Schoedon
 */
public class OMPanelTesting extends JPanel implements ActionListener {

  /**
   * Unique serial version ID.
   */
  private static final long     serialVersionUID = -5496779392128285570L;

  /**
   * Stores the absolute path to the OMB object which will be used to analyse
   * the imported data.
   */
  private String                ombFile;

  /**
   * Indicates whether any virtual campaign is simulated or not.
   */
  private boolean               isSimulated;

  /**
   * Indicates whether the displayed campaign is a simulation result from
   * results panel or not.
   */
  private boolean               isResult;

  /**
   * Stores the selected campaign from the simulation results panel.
   */
  private OMCampaign            resultCampaign;

  /**
   * UI: Label "Select Project"
   */
  private JLabel                lblSelectProject;

  /**
   * UI: Label "Select Rooms"
   */
  private JLabel                lblSelectRooms;

  /**
   * UI: Label "Start Time"
   */
  private JLabel                lblStartTime;

  /**
   * UI: Label "Select 6 rooms and 1 cellar!"
   */
  private JLabel                lblWarning;

  /**
   * UI: Label "Open OMB-File"
   */
  private JLabel                lblOpenOmbfile;

  /**
   * UI: Label for first orientation, content: "Select an OMB-Object file to
   * manually simulate virtual campaigns."
   */
  private JLabel                lblHelp;

  /**
   * UI: Label "Export chart to..."
   */
  private JLabel                lblExportChartTo;

  /**
   * UI: Text field to enter the absolute path to the OMB object file.
   */
  private JTextField            txtOmbFile;

  /**
   * UI: Button to load the selected OMB file to the panel.
   */
  private JButton               btnRefresh;

  /**
   * UI: Button to display the chart in fullscreen mode.
   */
  private JButton               btnMaximize;

  /**
   * UI: Button to open a file browser to save an OMB file.
   */
  private JButton               btnBrowse;

  /**
   * UI: Button to export the chart to CSV.
   */
  private JButton               btnCsv;

  /**
   * UI: Button to export the chart to PDF.
   */
  private JButton               btnPdf;

  /**
   * UI: Combobox to select a project to analyse.
   */
  private JComboBox<OMBuilding> comboBoxProjects;

  /**
   * UI: Combobox to select the 1st room of the virtual campaign.
   */
  private JComboBox<OMRoom>     comboBoxRoom1;

  /**
   * UI: Combobox to select the 2nd room of the virtual campaign.
   */
  private JComboBox<OMRoom>     comboBoxRoom2;

  /**
   * UI: Combobox to select the 3rd room of the virtual campaign.
   */
  private JComboBox<OMRoom>     comboBoxRoom3;

  /**
   * UI: Combobox to select the 4th room of the virtual campaign.
   */
  private JComboBox<OMRoom>     comboBoxRoom4;

  /**
   * UI: Combobox to select the 5th room of the virtual campaign.
   */
  private JComboBox<OMRoom>     comboBoxRoom5;

  /**
   * UI: Combobox to select the 6th room of the virtual campaign.
   */
  private JComboBox<OMRoom>     comboBoxRoom6;

  /**
   * UI: Combobox to select the 7th room of the virtual campaign.
   */
  private JComboBox<OMRoom>     comboBoxRoom7;

  /**
   * UI: Spinner with an integer value to adjust the start time of the virtual
   * campaign.
   */
  private JSpinner              spnrStartTime;

  /**
   * UI: Slider to adjust the start time of the virtual campaign.
   */
  private JSlider               sliderStartTime;

  /**
   * UI: Progress bar to display the status of certain actions performed on this
   * panel.
   */
  private JProgressBar          progressBar;

  /**
   * UI: Panel where the virtual campaign's radon concentration chart is drawn
   * to.
   */
  private JPanel                panelCampaign;

  /**
   * Stores the task to load OMB files to the panel which will be executed in a
   * separate thread to ensure the UI wont freeze.
   */
  private Refresh               refreshTask;

  /**
   * Gets the absolute path to the OMB object which will be used to analyse the
   * imported data.
   * 
   * @return The absolute path to the OMB object.
   */
  public String getOmbFile() {
    return this.ombFile;
  }

  /**
   * Sets the absolute path to the OMB object which will be used to analyse the
   * imported data.
   * 
   * @param ombFile
   *          The absolute path to the OMB object.
   */
  public void setOmbFile(String ombFile) {
    this.ombFile = ombFile;
  }

  /**
   * Gets the selected campaign from the simulation results panel.
   * 
   * @return The selected campaign from the simulation results panel.
   */
  public OMCampaign getResultCampaign() {
    return this.resultCampaign;
  }

  /**
   * Sets the selected campaign from the simulation results panel.
   * 
   * @param resultCampaign
   *          The selected campaign from the simulation results panel.
   */
  public void setResultCampaign(OMCampaign resultCampaign) {
    this.resultCampaign = resultCampaign;
  }

  /**
   * The inner class Refresh used load OMB files to the panel which will be
   * executed in a separate thread to ensure the UI wont freeze.
   * 
   * @author A. Schoedon
   */
  class Refresh extends SwingWorker<Void, Void> {

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
     * Loads the object from the OMB file to the panel.
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    public Void doInBackground() {
      removeEventListener();
      addEventListener();
      tmpUpdate("Getting objects from Database... ", 1);
      ObjectContainer db4o = Db4oEmbedded.openFile(
          Db4oEmbedded.newConfiguration(), getOmbFile());
      ObjectSet<OMBuilding> result = db4o.queryByExample(OMBuilding.class);
      OMBuilding found;
      tmpUpdate("Refreshing list... ", 2);
      tmpUpdate("Adding items... ", 3);
      for (int i = 0; i < result.size(); i++) {
        double perc = (double) i / (double) result.size() * 100.0 + 3.0;
        while (perc > 99) {
          perc--;
        }
        found = (OMBuilding) result.next();
        comboBoxProjects.addItem(found);
        tmpUpdate("Added: " + found.getName(), (int) perc);
      }
      tmpUpdate("Finished. ", 100);
      db4o.close();
      return null;
    }

    /**
     * Executed in event dispatching thread after finishing the refresh task,
     * updates the interface.
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    public void done() {
      btnRefresh.setEnabled(true);
      comboBoxProjects.setEnabled(true);
      isResult = false;
      tmpUpdate(" ", 0);
      progressBar.setStringPainted(false);
      progressBar.setIndeterminate(false);
      progressBar.setValue(0);
      progressBar.setVisible(false);
      setCursor(null);
    }
  }

  /**
   * Initializes the interface of the testing panel without any preloaded
   * objects.
   */
  public OMPanelTesting() {
    isResult = false;
    initialize();
    removeEventListener();
    addEventListener();
  }

  /**
   * Initializes the interface of the testing panel with a preloaded object from
   * import panel. Launching a refresh task in background.
   * 
   * @param omb
   *          Absolute path to an OMB object file to load on init.
   * @param building
   *          The imported building object.
   */
  public OMPanelTesting(String omb, OMBuilding building) {
    isResult = false;
    initialize();
    removeEventListener();
    addEventListener();
    txtOmbFile.setText(omb);
    setOmbFile(omb);
    comboBoxProjects.addItem(building);
    comboBoxProjects.setEnabled(true);
  }

  /**
   * Initializes the interface of the data panel with a preloaded object from
   * results panel. Displaying the selected campaign only.
   * 
   * @param simulation
   *          The selected simulation object.
   * @param campaign
   *          The selected campaign to display.
   */
  public OMPanelTesting(OMSimulation simulation, OMCampaign campaign) {
    isResult = true;
    isSimulated = true;
    setResultCampaign(campaign);
    initialize();
    comboBoxProjects.removeAllItems();
    removeEventListener();
    OMBuilding building = simulation.getBuilding();
    comboBoxProjects.addItem(building);
    comboBoxProjects.setEnabled(true);
    sliderStartTime.setMaximum(campaign.getStart());
    spnrStartTime.setModel(new SpinnerNumberModel(campaign.getStart(), campaign
        .getStart(), campaign.getStart(), 1));
    OMRoom[] rooms = campaign.getRoomPattern();
    comboBoxRoom1.removeAllItems();
    comboBoxRoom1.addItem(rooms[0]);
    comboBoxRoom1.setEnabled(true);
    comboBoxRoom2.removeAllItems();
    comboBoxRoom2.addItem(rooms[1]);
    comboBoxRoom2.setEnabled(true);
    comboBoxRoom3.removeAllItems();
    comboBoxRoom3.addItem(rooms[2]);
    comboBoxRoom3.setEnabled(true);
    comboBoxRoom4.removeAllItems();
    comboBoxRoom4.addItem(rooms[3]);
    comboBoxRoom4.setEnabled(true);
    comboBoxRoom5.removeAllItems();
    comboBoxRoom5.addItem(rooms[4]);
    comboBoxRoom5.setEnabled(true);
    comboBoxRoom6.removeAllItems();
    comboBoxRoom6.addItem(rooms[5]);
    comboBoxRoom6.setEnabled(true);
    comboBoxRoom7.removeAllItems();
    comboBoxRoom7.addItem(rooms[6]);
    comboBoxRoom7.setEnabled(true);
    spnrStartTime.setEnabled(false);
    sliderStartTime.setEnabled(false);
    sliderStartTime.setValue(campaign.getStart());
    btnMaximize.setVisible(true);
    btnPdf.setVisible(true);
    btnCsv.setVisible(true);
    lblExportChartTo.setVisible(true);
    JPanel campaignChart = createCampaignPanel(campaign, false, false);
    remove(panelCampaign);
    panelCampaign = new JPanel();
    panelCampaign.setBounds(10, 150, 730, 315);
    panelCampaign.add(campaignChart);
    add(panelCampaign);
    updateUI();
  }

  /**
   * Removes eventlistener from the projects combobox to avoid strange behaviour
   * of the panel while loading a selected campaign from results panel.
   */
  protected void removeEventListener() {
    try {
      ActionListener[] a = comboBoxProjects.getActionListeners();
      for (int i = 0; i < a.length; i++) {
        comboBoxProjects.removeActionListener(a[i]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      PropertyChangeListener[] p = comboBoxProjects
          .getPropertyChangeListeners();
      for (int i = 0; i < p.length; i++) {
        comboBoxProjects.removePropertyChangeListener(p[i]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds eventlisteners to the projects combobox.
   */
  protected void addEventListener() {
    comboBoxProjects.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        boolean b = false;
        Color c = null;
        if (comboBoxProjects.isEnabled() || isResult) {
          if (comboBoxProjects.getSelectedItem() != null) {
            b = true;
            c = Color.WHITE;
            OMBuilding building = (OMBuilding) comboBoxProjects
                .getSelectedItem();
            comboBoxRoom1.removeAllItems();
            comboBoxRoom2.removeAllItems();
            comboBoxRoom3.removeAllItems();
            comboBoxRoom4.removeAllItems();
            comboBoxRoom5.removeAllItems();
            comboBoxRoom6.removeAllItems();
            comboBoxRoom7.removeAllItems();
            for (int i = 0; i < building.getRooms().length; i++) {
              comboBoxRoom1.addItem(building.getRooms()[i]);
              comboBoxRoom2.addItem(building.getRooms()[i]);
              comboBoxRoom3.addItem(building.getRooms()[i]);
              comboBoxRoom4.addItem(building.getRooms()[i]);
              comboBoxRoom5.addItem(building.getRooms()[i]);
              comboBoxRoom6.addItem(building.getRooms()[i]);
              comboBoxRoom7.addItem(building.getRooms()[i]);
            }
            for (int i = 0; i < building.getCellars().length; i++) {
              comboBoxRoom1.addItem(building.getCellars()[i]);
              comboBoxRoom2.addItem(building.getCellars()[i]);
              comboBoxRoom3.addItem(building.getCellars()[i]);
              comboBoxRoom4.addItem(building.getCellars()[i]);
              comboBoxRoom5.addItem(building.getCellars()[i]);
              comboBoxRoom6.addItem(building.getCellars()[i]);
              comboBoxRoom7.addItem(building.getCellars()[i]);
            }
            comboBoxRoom7.setSelectedIndex(comboBoxRoom7.getItemCount() - 1);
            sliderStartTime.setMaximum(building.getValueCount() - 168);
            spnrStartTime.setModel(new SpinnerNumberModel(0, 0, building
                .getValueCount() - 168, 1));
          } else {
            b = false;
            c = null;
          }
        } else {
          b = false;
          c = null;
        }
        lblSelectRooms.setEnabled(b);
        lblStartTime.setEnabled(b);
        panelCampaign.setEnabled(b);
        btnMaximize.setVisible(isSimulated);
        btnPdf.setVisible(isSimulated);
        btnCsv.setVisible(isSimulated);
        lblExportChartTo.setVisible(isSimulated);
        sliderStartTime.setEnabled(b);
        spnrStartTime.setEnabled(b);
        comboBoxRoom1.setEnabled(b);
        comboBoxRoom2.setEnabled(b);
        comboBoxRoom3.setEnabled(b);
        comboBoxRoom4.setEnabled(b);
        comboBoxRoom5.setEnabled(b);
        comboBoxRoom6.setEnabled(b);
        comboBoxRoom7.setEnabled(b);
        panelCampaign.setBackground(c);
      }
    });
    comboBoxProjects.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        boolean b = false;
        Color c = null;
        if (comboBoxProjects.isEnabled() || isResult) {
          if (comboBoxProjects.getSelectedItem() != null) {
            b = true;
            c = Color.WHITE;
            OMBuilding building = (OMBuilding) comboBoxProjects
                .getSelectedItem();
            comboBoxRoom1.removeAllItems();
            comboBoxRoom2.removeAllItems();
            comboBoxRoom3.removeAllItems();
            comboBoxRoom4.removeAllItems();
            comboBoxRoom5.removeAllItems();
            comboBoxRoom6.removeAllItems();
            comboBoxRoom7.removeAllItems();
            for (int i = 0; i < building.getRooms().length; i++) {
              comboBoxRoom1.addItem(building.getRooms()[i]);
              comboBoxRoom2.addItem(building.getRooms()[i]);
              comboBoxRoom3.addItem(building.getRooms()[i]);
              comboBoxRoom4.addItem(building.getRooms()[i]);
              comboBoxRoom5.addItem(building.getRooms()[i]);
              comboBoxRoom6.addItem(building.getRooms()[i]);
              comboBoxRoom7.addItem(building.getRooms()[i]);
            }
            for (int i = 0; i < building.getCellars().length; i++) {
              comboBoxRoom1.addItem(building.getCellars()[i]);
              comboBoxRoom2.addItem(building.getCellars()[i]);
              comboBoxRoom3.addItem(building.getCellars()[i]);
              comboBoxRoom4.addItem(building.getCellars()[i]);
              comboBoxRoom5.addItem(building.getCellars()[i]);
              comboBoxRoom6.addItem(building.getCellars()[i]);
              comboBoxRoom7.addItem(building.getCellars()[i]);
            }
            comboBoxRoom7.setSelectedIndex(comboBoxRoom7.getItemCount() - 1);
            sliderStartTime.setMaximum(building.getValueCount() - 168);
            spnrStartTime.setModel(new SpinnerNumberModel(0, 0, building
                .getValueCount() - 168, 1));
          } else {
            b = false;
            c = null;
          }
        } else {
          b = false;
          c = null;
        }
        lblSelectRooms.setEnabled(b);
        lblStartTime.setEnabled(b);
        panelCampaign.setEnabled(b);
        btnMaximize.setVisible(isSimulated);
        btnPdf.setVisible(isSimulated);
        btnCsv.setVisible(isSimulated);
        lblExportChartTo.setVisible(isSimulated);
        sliderStartTime.setEnabled(b);
        spnrStartTime.setEnabled(b);
        comboBoxRoom1.setEnabled(b);
        comboBoxRoom2.setEnabled(b);
        comboBoxRoom3.setEnabled(b);
        comboBoxRoom4.setEnabled(b);
        comboBoxRoom5.setEnabled(b);
        comboBoxRoom6.setEnabled(b);
        comboBoxRoom7.setEnabled(b);
        panelCampaign.setBackground(c);
      }
    });
  }

  /**
   * Initializes the interface of the results panel.
   */
  protected void initialize() {

    setLayout(null);
    isSimulated = false;

    lblExportChartTo = new JLabel("Export chart to ...");
    lblExportChartTo.setBounds(436, 479, 144, 14);
    lblExportChartTo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblExportChartTo.setVisible(false);
    add(lblExportChartTo);

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
          double[] selectedValues;
          OMRoom[] rooms = new OMRoom[7];
          rooms[0] = (OMRoom) comboBoxRoom1.getSelectedItem();
          rooms[1] = (OMRoom) comboBoxRoom2.getSelectedItem();
          rooms[2] = (OMRoom) comboBoxRoom3.getSelectedItem();
          rooms[3] = (OMRoom) comboBoxRoom4.getSelectedItem();
          rooms[4] = (OMRoom) comboBoxRoom5.getSelectedItem();
          rooms[5] = (OMRoom) comboBoxRoom6.getSelectedItem();
          rooms[6] = (OMRoom) comboBoxRoom7.getSelectedItem();
          int start = sliderStartTime.getValue();
          final int day = 24;
          File csvFile = new File(csvPath);
          try {
            OMCampaign campaign;
            if (isResult) {
              campaign = getResultCampaign();
            } else {
              campaign = new OMCampaign(start, rooms, 0);
            }
            FileWriter logWriter = new FileWriter(csvFile);
            BufferedWriter csvOutput = new BufferedWriter(logWriter);
            csvOutput.write("\"ID\";\"Room\";\"Radon\"");
            csvOutput.newLine();
            selectedValues = campaign.getValueChain();
            int x = 0;
            for (int i = start; i < start + day; i++) {
              csvOutput.write("\"" + i + "\";\"" + rooms[0].getId() + "\";\""
                  + (int) selectedValues[x] + "\"");
              csvOutput.newLine();
              x++;
            }
            start = start + day;
            for (int i = start; i < start + day; i++) {
              csvOutput.write("\"" + i + "\";\"" + rooms[1].getId() + "\";\""
                  + (int) selectedValues[x] + "\"");
              csvOutput.newLine();
              x++;
            }
            start = start + day;
            for (int i = start; i < start + day; i++) {
              csvOutput.write("\"" + i + "\";\"" + rooms[2].getId() + "\";\""
                  + (int) selectedValues[x] + "\"");
              csvOutput.newLine();
              x++;
            }
            start = start + day;
            for (int i = start; i < start + day; i++) {
              csvOutput.write("\"" + i + "\";\"" + rooms[3].getId() + "\";\""
                  + (int) selectedValues[x] + "\"");
              csvOutput.newLine();
              x++;
            }
            start = start + day;
            for (int i = start; i < start + day; i++) {
              csvOutput.write("\"" + i + "\";\"" + rooms[4].getId() + "\";\""
                  + (int) selectedValues[x] + "\"");
              csvOutput.newLine();
              x++;
            }
            start = start + day;
            for (int i = start; i < start + day; i++) {
              csvOutput.write("\"" + i + "\";\"" + rooms[5].getId() + "\";\""
                  + (int) selectedValues[x] + "\"");
              csvOutput.newLine();
              x++;
            }
            start = start + day;
            for (int i = start; i < start + day; i++) {
              csvOutput.write("\"" + i + "\";\"" + rooms[6].getId() + "\";\""
                  + (int) selectedValues[x] + "\"");
              csvOutput.newLine();
              x++;
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
          OMRoom[] rooms = new OMRoom[7];
          rooms[0] = (OMRoom) comboBoxRoom1.getSelectedItem();
          rooms[1] = (OMRoom) comboBoxRoom2.getSelectedItem();
          rooms[2] = (OMRoom) comboBoxRoom3.getSelectedItem();
          rooms[3] = (OMRoom) comboBoxRoom4.getSelectedItem();
          rooms[4] = (OMRoom) comboBoxRoom5.getSelectedItem();
          rooms[5] = (OMRoom) comboBoxRoom6.getSelectedItem();
          rooms[6] = (OMRoom) comboBoxRoom7.getSelectedItem();
          int start = sliderStartTime.getValue();
          OMCampaign campaign;
          try {
            if (isResult) {
              campaign = getResultCampaign();
            } else {
              campaign = new OMCampaign(start, rooms, 0);
            }
            JFreeChart chart = OMCharts.createCampaignChart(campaign, false);
            String title = "Campaign: " + rooms[0].getId() + rooms[1].getId()
                + rooms[2].getId() + rooms[3].getId() + rooms[4].getId()
                + rooms[5].getId() + rooms[6].getId() + ", Start: " + start;
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
          } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Failed to create chart!\n"
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

    lblSelectProject = new JLabel("Select Project");
    lblSelectProject.setBounds(10, 65, 132, 14);
    lblSelectProject.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(lblSelectProject);

    lblSelectRooms = new JLabel("Select Rooms");
    lblSelectRooms.setBounds(10, 94, 132, 14);
    lblSelectRooms.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(lblSelectRooms);

    lblStartTime = new JLabel("Start Time");
    lblStartTime.setBounds(10, 123, 132, 14);
    lblStartTime.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(lblStartTime);

    lblWarning = new JLabel("Select 6 rooms and 1 cellar!");
    lblWarning.setForeground(Color.RED);
    lblWarning.setBounds(565, 123, 175, 14);
    lblWarning.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblWarning.setVisible(false);
    add(lblWarning);

    sliderStartTime = new JSlider();
    sliderStartTime.setMaximum(0);
    sliderStartTime.setBounds(152, 119, 285, 24);
    sliderStartTime.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(sliderStartTime);

    spnrStartTime = new JSpinner();
    spnrStartTime.setModel(new SpinnerNumberModel(0, 0, 0, 1));
    spnrStartTime.setBounds(447, 120, 108, 22);
    spnrStartTime.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(spnrStartTime);

    btnRefresh = new JButton("Load");
    btnRefresh.setBounds(616, 61, 124, 23);
    btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(btnRefresh);

    btnMaximize = new JButton("Fullscreen");
    btnMaximize.setBounds(10, 475, 124, 23);
    btnMaximize.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(btnMaximize);

    panelCampaign = new JPanel();
    panelCampaign.setBounds(10, 150, 730, 315);
    panelCampaign.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(panelCampaign);

    progressBar = new JProgressBar();
    progressBar.setBounds(10, 475, 730, 23);
    progressBar.setFont(new Font("SansSerif", Font.PLAIN, 11));
    progressBar.setVisible(false);
    add(progressBar);

    lblOpenOmbfile = new JLabel("Open OMB-File");
    lblOpenOmbfile.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblOpenOmbfile.setBounds(10, 36, 132, 14);
    add(lblOpenOmbfile);

    lblHelp = new JLabel(
        "Select an OMB-Object file to manually simulate virtual campaigns.");
    lblHelp.setForeground(Color.GRAY);
    lblHelp.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblHelp.setBounds(10, 10, 730, 14);
    add(lblHelp);

    txtOmbFile = new JTextField();
    txtOmbFile.setFont(new Font("SansSerif", Font.PLAIN, 11));
    txtOmbFile.setColumns(10);
    txtOmbFile.setBounds(152, 33, 454, 20);
    add(txtOmbFile);

    btnBrowse = new JButton("Browse");
    btnBrowse.setFont(new Font("SansSerif", Font.PLAIN, 11));
    btnBrowse.setBounds(616, 32, 124, 23);
    add(btnBrowse);

    comboBoxRoom1 = new JComboBox<OMRoom>();
    comboBoxRoom1.setBounds(152, 90, 75, 22);
    comboBoxRoom1.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(comboBoxRoom1);

    comboBoxRoom2 = new JComboBox<OMRoom>();
    comboBoxRoom2.setBounds(237, 90, 75, 22);
    comboBoxRoom2.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(comboBoxRoom2);

    comboBoxRoom3 = new JComboBox<OMRoom>();
    comboBoxRoom3.setBounds(323, 90, 75, 22);
    comboBoxRoom3.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(comboBoxRoom3);

    comboBoxRoom4 = new JComboBox<OMRoom>();
    comboBoxRoom4.setBounds(408, 90, 75, 22);
    comboBoxRoom4.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(comboBoxRoom4);

    comboBoxRoom5 = new JComboBox<OMRoom>();
    comboBoxRoom5.setBounds(494, 90, 75, 22);
    comboBoxRoom5.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(comboBoxRoom5);

    comboBoxRoom6 = new JComboBox<OMRoom>();
    comboBoxRoom6.setBounds(579, 90, 75, 22);
    comboBoxRoom6.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(comboBoxRoom6);

    comboBoxRoom7 = new JComboBox<OMRoom>();
    comboBoxRoom7.setBounds(665, 90, 75, 22);
    comboBoxRoom7.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(comboBoxRoom7);

    comboBoxProjects = new JComboBox<OMBuilding>();
    comboBoxProjects.setBounds(152, 61, 454, 22);
    comboBoxProjects.setFont(new Font("SansSerif", Font.PLAIN, 11));
    add(comboBoxProjects);

    comboBoxRoom1.addActionListener(this);
    comboBoxRoom1.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        validateCampaign();
      }
    });
    comboBoxRoom2.addActionListener(this);
    comboBoxRoom2.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        validateCampaign();
      }
    });
    comboBoxRoom3.addActionListener(this);
    comboBoxRoom3.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        validateCampaign();
      }
    });
    comboBoxRoom4.addActionListener(this);
    comboBoxRoom4.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        validateCampaign();
      }
    });
    comboBoxRoom5.addActionListener(this);
    comboBoxRoom5.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        validateCampaign();
      }
    });
    comboBoxRoom6.addActionListener(this);
    comboBoxRoom6.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        validateCampaign();
      }
    });
    comboBoxRoom7.addActionListener(this);
    comboBoxRoom7.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        validateCampaign();
      }
    });

    sliderStartTime.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (comboBoxProjects.isEnabled() || isResult) {
          if (comboBoxProjects.getSelectedItem() != null) {
            spnrStartTime.setValue((int) sliderStartTime.getValue());
            updateChart();
          }
        }
      }
    });
    spnrStartTime.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        if (comboBoxProjects.isEnabled() || isResult) {
          if (comboBoxProjects.getSelectedItem() != null) {
            sliderStartTime.setValue((Integer) spnrStartTime.getValue());
            updateChart();
          }
        }
      }
    });
    btnRefresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (txtOmbFile.getText() != null && !txtOmbFile.getText().equals("")
            && !txtOmbFile.getText().equals(" ")) {
          txtOmbFile.setBackground(Color.WHITE);
          String ombPath = txtOmbFile.getText();
          String omb;
          String[] tmpFileName = ombPath.split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("omb")) {
            omb = "";
          } else {
            omb = ".omb";
          }
          txtOmbFile.setText(ombPath + omb);
          setOmbFile(ombPath + omb);
          File ombFile = new File(ombPath + omb);
          if (ombFile.exists()) {
            txtOmbFile.setBackground(Color.WHITE);
            btnRefresh.setEnabled(false);
            comboBoxProjects.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            progressBar.setVisible(true);
            btnPdf.setVisible(false);
            btnCsv.setVisible(false);
            btnMaximize.setVisible(false);
            lblExportChartTo.setVisible(false);
            progressBar.setIndeterminate(true);
            progressBar.setStringPainted(true);
            refreshTask = new Refresh();
            refreshTask.execute();
          } else {
            txtOmbFile.setBackground(new Color(255, 222, 222, 128));
            JOptionPane.showMessageDialog(null,
                "OMB-file not found, please check the file path!", "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        } else {
          txtOmbFile.setBackground(new Color(255, 222, 222, 128));
          JOptionPane.showMessageDialog(null, "Please select an OMB-file!",
              "Warning", JOptionPane.WARNING_MESSAGE);
        }
      }
    });
    btnMaximize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          OMRoom[] rooms = new OMRoom[7];
          rooms[0] = (OMRoom) comboBoxRoom1.getSelectedItem();
          rooms[1] = (OMRoom) comboBoxRoom2.getSelectedItem();
          rooms[2] = (OMRoom) comboBoxRoom3.getSelectedItem();
          rooms[3] = (OMRoom) comboBoxRoom4.getSelectedItem();
          rooms[4] = (OMRoom) comboBoxRoom5.getSelectedItem();
          rooms[5] = (OMRoom) comboBoxRoom6.getSelectedItem();
          rooms[6] = (OMRoom) comboBoxRoom7.getSelectedItem();
          int start = sliderStartTime.getValue();
          String title = "Campaign: " + rooms[0].getId() + rooms[1].getId()
              + rooms[2].getId() + rooms[3].getId() + rooms[4].getId()
              + rooms[5].getId() + rooms[6].getId() + ", Start: " + start;
          OMCampaign campaign;
          if (isResult) {
            campaign = getResultCampaign();
          } else {
            campaign = new OMCampaign(start, rooms, 0);
          }
          JPanel campaignChart = createCampaignPanel(campaign, false, true);
          JFrame chartFrame = new JFrame();
          chartFrame.getContentPane().add(campaignChart);
          chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
          chartFrame.setBounds(0, 0, (int) dim.getWidth(),
              (int) dim.getHeight());
          chartFrame.setTitle(title);
          chartFrame.setResizable(true);
          chartFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
          chartFrame.setVisible(true);
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    });
    txtOmbFile.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent arg0) {
        setOmbFile(txtOmbFile.getText());
      }
    });
    btnBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.omb", "omb"));
        fileDialog.showOpenDialog(getParent());
        final File file = fileDialog.getSelectedFile();
        if (file != null) {
          String omb;
          String[] tmpFileName = file.getAbsolutePath().split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("omb")) {
            omb = "";
          } else {
            omb = ".omb";
          }
          txtOmbFile.setText(file.getAbsolutePath() + omb);
          setOmbFile(file.getAbsolutePath() + omb);
        }
      }
    });
  }

  /**
   * Creates a panel displaying the chart of the radon concentration of a
   * virtual campaign.
   * 
   * @param campaign
   *          The campaign object containing all rooms and radon data.
   * @param preview
   *          Will hide annotations, labels and headlines if true.
   * @param fullscreen
   *          Will correctly adjust the preferred size to screen resolution if
   *          true.
   * @return A panel displaying the radon concentration chart of a virtual
   *         campaign.
   */
  protected JPanel createCampaignPanel(OMCampaign campaign, boolean preview,
      boolean fullscreen) {
    JFreeChart chart = OMCharts.createCampaignChart(campaign, preview);
    ChartPanel chartPanel = new ChartPanel(chart);
    Dimension dim;
    if (fullscreen) {
      dim = Toolkit.getDefaultToolkit().getScreenSize();
    } else {
      dim = new Dimension(730, 315);
    }
    chartPanel.setPreferredSize(dim);
    JPanel campaignPanel = (JPanel) chartPanel;
    return campaignPanel;
  }

  /**
   * Validates the selected campaign. Only returns true if a simulation object
   * is selected and among the seven room-comboboxes one cellar and six normal
   * rooms are selected.
   * 
   * @return True if 6 rooms and 1 cellar is selected.
   */
  protected boolean validateCampaign() {
    boolean result = false;
    OMRoom[] rooms = new OMRoom[7];
    if (comboBoxRoom1.isEnabled()) {
      if (comboBoxRoom1.getSelectedItem() != null) {
        if (comboBoxRoom2.isEnabled()) {
          if (comboBoxRoom2.getSelectedItem() != null) {
            if (comboBoxRoom3.isEnabled()) {
              if (comboBoxRoom3.getSelectedItem() != null) {
                if (comboBoxRoom4.isEnabled()) {
                  if (comboBoxRoom4.getSelectedItem() != null) {
                    if (comboBoxRoom5.isEnabled()) {
                      if (comboBoxRoom5.getSelectedItem() != null) {
                        if (comboBoxRoom6.isEnabled()) {
                          if (comboBoxRoom6.getSelectedItem() != null) {
                            if (comboBoxRoom7.isEnabled()) {
                              if (comboBoxRoom7.getSelectedItem() != null) {
                                rooms[0] = (OMRoom) comboBoxRoom1
                                    .getSelectedItem();
                                rooms[1] = (OMRoom) comboBoxRoom2
                                    .getSelectedItem();
                                rooms[2] = (OMRoom) comboBoxRoom3
                                    .getSelectedItem();
                                rooms[3] = (OMRoom) comboBoxRoom4
                                    .getSelectedItem();
                                rooms[4] = (OMRoom) comboBoxRoom5
                                    .getSelectedItem();
                                rooms[5] = (OMRoom) comboBoxRoom6
                                    .getSelectedItem();
                                rooms[6] = (OMRoom) comboBoxRoom7
                                    .getSelectedItem();
                                int rCount = 0;
                                int cCount = 0;
                                for (int i = 0; i < rooms.length; i++) {
                                  if (rooms[i].getType() == OMRoomType.Room) {
                                    rCount++;
                                  } else {
                                    if (rooms[i].getType() == OMRoomType.Cellar) {
                                      cCount++;
                                    }
                                  }
                                }
                                if (rCount == 6 && cCount == 1) {
                                  result = true;
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    if (result) {
      if (comboBoxProjects.isEnabled()) {
        if (comboBoxProjects.getSelectedItem() != null) {
          lblWarning.setVisible(false);
        } else {
          lblWarning.setVisible(false);
        }
      } else {
        lblWarning.setVisible(false);
      }
    } else {
      if (comboBoxProjects.isEnabled()) {
        if (comboBoxProjects.getSelectedItem() != null) {
          lblWarning.setVisible(true);
        } else {
          lblWarning.setVisible(false);
        }
      } else {
        lblWarning.setVisible(false);
      }
    }
    return result;
  }

  /**
   * Updates the campaign chart in realtime if selected campaign is valid.
   */
  public void updateChart() {
    try {
      if (validateCampaign()) {
        OMRoom[] rooms = new OMRoom[7];
        rooms[0] = (OMRoom) comboBoxRoom1.getSelectedItem();
        rooms[1] = (OMRoom) comboBoxRoom2.getSelectedItem();
        rooms[2] = (OMRoom) comboBoxRoom3.getSelectedItem();
        rooms[3] = (OMRoom) comboBoxRoom4.getSelectedItem();
        rooms[4] = (OMRoom) comboBoxRoom5.getSelectedItem();
        rooms[5] = (OMRoom) comboBoxRoom6.getSelectedItem();
        rooms[6] = (OMRoom) comboBoxRoom7.getSelectedItem();
        int start = sliderStartTime.getValue();
        OMCampaign campaign = new OMCampaign(start, rooms, 0);
        JPanel campaignChart = createCampaignPanel(campaign, false, false);
        remove(panelCampaign);
        panelCampaign = new JPanel();
        panelCampaign.setBounds(10, 150, 730, 315);
        panelCampaign.add(campaignChart);
        add(panelCampaign);
        isSimulated = true;
        btnMaximize.setVisible(true);
        updateUI();
      } else {
        isSimulated = false;
      }
    } catch (IOException ioe) {
      isSimulated = false;
      ioe.printStackTrace();
    }
  }

  /**
   * ActionListener for the seven room-comboboxes. Updates the campaign chart.
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0) {
    updateChart();
  }
}
