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
import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import de.bfs.radon.omsimulation.data.OMBuilding;
import de.bfs.radon.omsimulation.data.OMCampaign;
import de.bfs.radon.omsimulation.data.OMHelper;
import de.bfs.radon.omsimulation.data.OMRoom;
import de.bfs.radon.omsimulation.data.OMSimulation;

/**
 * @author A. Schoedon
 */
public class OMPanelSimulation extends JPanel implements ActionListener {

  /**
   * Unique serial version ID.
   */
  private static final long serialVersionUID = -2584829845531323801L;

  /**
   * Stores the status of the simulation process. Used to update the progress
   * bar.
   */
  private int status;

  /**
   * Stores the current log message which will be both written to the log file
   * and displayed at the progress bar.
   */
  private String logMsg;

  /**
   * Stores the selected building which is used to run simulations with.
   */
  private OMBuilding selectedObject;

  /**
   * Stores the absolute path to the OMB object which will be used for
   * simulations.
   */
  private String ombFile;

  /**
   * Stores the absolute path to the OMS object which will be used analysing the
   * results later on.
   */
  private String omsFile;

  /**
   * Stores a custom name for the object defined by the user.
   */
  private String projectName;

  /**
   * Indicates whether the current simulation is systematic or random.
   */
  private boolean isSystematic;

  /**
   * Stores the number of how many random campaigns will be simulated.
   */
  private int randomCampaigns;

  /**
   * Stores the ratio of 3-of-6-rooms simulations.
   */
  private int ratio3;

  /**
   * Stores the ratio of 4-of-6-rooms simulations.
   */
  private int ratio4;

  /**
   * Stores the ratio of 5-of-6-rooms simulations.
   */
  private int ratio5;

  /**
   * Stores the ratio of 6-of-6-rooms simulations.
   */
  private int ratio6;

  /**
   * Stores the random noise factor.
   */
  private int randomNoise;

  /**
   * UI: Label "Select Project"
   */
  private JLabel lblSelectProject;

  /**
   * UI: Label "Simulation Type"
   */
  private JLabel lblSimulationType;

  /**
   * UI: Label "campaigns"
   */
  private JLabel lblCampaigns;

  /**
   * UI: Label "Ratio"
   */
  private JLabel lblRatio;

  /**
   * UI: Label "%"
   */
  private JLabel lblPercent;

  /**
   * UI: Label for first orientation, content: "Select an OMB-Object file to run
   * simulations. Limited random simulations can be saved as OMS-Simulation
   * files used for analysis."
   */
  private JLabel lblHelp;

  /**
   * UI: Label "Select OMB-File"
   */
  private JLabel lblSelectOmbfile;

  /**
   * UI: Label "Save OMS-File"
   */
  private JLabel lblOmsFile;

  /**
   * UI: Text field to enter the absolute path to the OMB object file.
   */
  private JTextField txtOmbFile;

  /**
   * UI: Text field to enter the absolute path to the OMS object file.
   */
  private JTextField txtOmsFile;

  /**
   * UI: Button to load an OMB file to panel.
   */
  private JButton btnRefresh;

  /**
   * UI: Button to start the simulation process.
   */
  private JButton btnStart;

  /**
   * UI: Button to open a file browser to load an OMB file.
   */
  private JButton btnBrowseOmb;

  /**
   * UI: Button to open a file browser to save an OMS file.
   */
  private JButton btnBrowseOms;

  /**
   * UI: Spinner for integer values to set the total number of random campaigns.
   */
  private JSpinner spnrRandomCampaigns;

  /**
   * UI: Spinner for integer values to set the 3-of-6-rooms ratio.
   */
  private JSpinner spnrRatio3;

  /**
   * UI: Spinner for integer values to set the 3-of-6-rooms ratio.
   */
  private JSpinner spnrRatio4;

  /**
   * UI: Spinner for integer values to set the 3-of-6-rooms ratio.
   */
  private JSpinner spnrRatio5;

  /**
   * UI: Spinner for integer values to set the 3-of-6-rooms ratio.
   */
  private JSpinner spnrRatio6;

  /**
   * UI: Spinner for integer values to set the random noise.
   */
  private JSpinner spnrRandomNoise;

  /**
   * UI: Checkbox for activating the ratio for 3-of-6-rooms simulations.
   */
  private JCheckBox chckbxRatio3;

  /**
   * UI: Checkbox for activating the ratio for 4-of-6-rooms simulations.
   */
  private JCheckBox chckbxRatio4;

  /**
   * UI: Checkbox for activating the ratio for 5-of-6-rooms simulations.
   */
  private JCheckBox chckbxRatio5;

  /**
   * UI: Checkbox for activating the ratio for 6-of-6-rooms simulations.
   */
  private JCheckBox chckbxRatio6;

  /**
   * UI: Checkbox for activating the random noise.
   */
  private JCheckBox chckbxRandomNoise;

  /**
   * UI: Combobox to display all loaded buildings.
   */
  private JComboBox<OMBuilding> comboBoxSelectProject;

  /**
   * UI: Radio button to select systematic simulations.
   */
  private JRadioButton rdbtnSystematic;

  /**
   * UI: Radio button to select random simulations.
   */
  private JRadioButton rdbtnRandom;

  /**
   * UI: Progress bar to display the simulation status and messages.
   */
  private JProgressBar progressBarSimulation;

  /**
   * Stores the refreshing process task which will be executed in a separate
   * thread to ensure the UI wont freeze.
   */
  private Refresh refreshTask;

  /**
   * Stores the simulation process task which will be executed in a separate
   * thread to ensure the UI wont freeze.
   */
  private Simulation simulationTask;

  /**
   * Gets the absolute path to the OMB object which will be used for
   * simulations.
   * 
   * @return The absolute path to the OMB object.
   */
  public String getOmbFile() {
    return this.ombFile;
  }

  /**
   * Sets the absolute path to the OMB object which will be used for
   * simulations.
   * 
   * @param ombFile
   *          The absolute path to the OMB object.
   */
  public void setOmbFile(String ombFile) {
    this.ombFile = ombFile;
  }

  /**
   * Gets the absolute path to the OMS object which will be used analysing the
   * results later on.
   * 
   * @return The absolute path to the OMS object.
   */
  public String getOmsFile() {
    return this.omsFile;
  }

  /**
   * Sets the absolute path to the OMS object which will be used analysing the
   * results later on.
   * 
   * @param omsFile
   *          The absolute path to the OMS object.
   */
  public void setOmsFile(String omsFile) {
    this.omsFile = omsFile;
  }

  /**
   * Gets the selected building which is used to run simulations with.
   * 
   * @return The selected building.
   */
  public OMBuilding getSelectedObject() {
    return this.selectedObject;
  }

  /**
   * Sets the selected building which is used to run simulations with.
   * 
   * @param selectedObject
   *          The selected building.
   */
  public void setSelectedObject(OMBuilding selectedObject) {
    this.selectedObject = selectedObject;
  }

  /**
   * Gets a custom name for the object defined by the user.
   * 
   * @return A custom name for the object defined by the user.
   */
  public String getProjectName() {
    return this.projectName;
  }

  /**
   * Sets a custom name for the object.
   * 
   * @param projectName
   *          A custom name for the object.
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * Sets the indicator whether the current simulation is systematic or random.
   * 
   * @return True if the current simulation is systematic.
   */
  public boolean isSystematic() {
    return this.isSystematic;
  }

  /**
   * Gets the indicator whether the current simulation is systematic or random.
   * 
   * @param isSystematic
   *          True if the current simulation is systematic.
   */
  public void setSystematic(boolean isSystematic) {
    this.isSystematic = isSystematic;
  }

  /**
   * Gets the number of how many random campaigns will be simulated.
   * 
   * @return The number of how many random campaigns will be simulated.
   */
  public int getRandomCampaigns() {
    return this.randomCampaigns;
  }

  /**
   * Sets the number of how many random campaigns will be simulated.
   * 
   * @param randomCampaigns
   *          The number of how many random campaigns will be simulated.
   */
  public void setRandomCampaigns(int randomCampaigns) {
    this.randomCampaigns = randomCampaigns;
  }

  /**
   * Gets the ratio of 3-of-6-rooms simulations.
   * 
   * @return The ratio of 3-of-6-rooms simulations.
   */
  public int getRatio3() {
    return this.ratio3;
  }

  /**
   * Sets the ratio of 3-of-6-rooms simulations.
   * 
   * @param ratio3
   *          The ratio of 3-of-6-rooms simulations.
   */
  public void setRatio3(int ratio3) {
    this.ratio3 = ratio3;
  }

  /**
   * Gets the ratio of 4-of-6-rooms simulations.
   * 
   * @return The ratio of 4-of-6-rooms simulations.
   */
  public int getRatio4() {
    return this.ratio4;
  }

  /**
   * Sets the ratio of 4-of-6-rooms simulations.
   * 
   * @param ratio4
   *          The ratio of 4-of-6-rooms simulations.
   */
  public void setRatio4(int ratio4) {
    this.ratio4 = ratio4;
  }

  /**
   * Gets the ratio of 5-of-6-rooms simulations.
   * 
   * @return The ratio of 5-of-6-rooms simulations.
   */
  public int getRatio5() {
    return this.ratio5;
  }

  /**
   * Sets the ratio of 5-of-6-rooms simulations.
   * 
   * @param ratio5
   *          The ratio of 5-of-6-rooms simulations.
   */
  public void setRatio5(int ratio5) {
    this.ratio5 = ratio5;
  }

  /**
   * Gets the ratio of 6-of-6-rooms simulations.
   * 
   * @return The ratio of 6-of-6-rooms simulations.
   */
  public int getRatio6() {
    return this.ratio6;
  }

  /**
   * Sets the ratio of 6-of-6-rooms simulations.
   * 
   * @param ratio6
   *          The ratio of 6-of-6-rooms simulations.
   */
  public void setRatio6(int ratio6) {
    this.ratio6 = ratio6;
  }

  /**
   * Gets the random noise factor.
   * 
   * @return The random noise factor.
   */
  public int getRandomNoise() {
    return this.randomNoise;
  }

  /**
   * Sets the random noise factor.
   * 
   * @param randomNoise
   *          The random noise factor.
   */
  public void setRandomNoise(int randomNoise) {
    this.randomNoise = randomNoise;
  }

  /**
   * Gets the status of the simulation process. Used to update the progress bar.
   * 
   * @return The status of the simulation process in percent.
   */
  public int getStatus() {
    return this.status;
  }

  /**
   * Sets the status of the simulation process. Used to update the progress bar.
   * 
   * @param status
   *          The status of the simulation process in percent.
   */
  public void setStatus(int status) {
    this.status = status;
  }

  /**
   * Gets the current log message which will be both written to the log file and
   * displayed at the progress bar.
   * 
   * @return The current log message.
   */
  public String getLogMsg() {
    return this.logMsg;
  }

  /**
   * Sets the current log message which will be both written to the log file and
   * displayed at the progress bar.
   * 
   * @param logMsg
   *          The current log message.
   */
  public void setLogMsg(String logMsg) {
    this.logMsg = logMsg;
  }

  /**
   * Inner class Refresh used to create the refreshing process task which will
   * be executed in a separate thread to ensure the UI wont freeze.
   * 
   * @author A. Schoedon
   */
  class Refresh extends SwingWorker<Void, Void> {

    /**
     * Updates the progress bar status and message..
     * 
     * @param s
     *          The log message.
     * @param i
     *          The status in percent.
     */
    private void tmpUpdate(String s, int i) {
      setLogMsg(s);
      setStatus(i);
      progressBarSimulation.setString(s);
      progressBarSimulation.setValue(i);
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
      tmpUpdate("Getting objects from file '" + getOmbFile() + "'.", 1);
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
        comboBoxSelectProject.addItem(found);
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
      comboBoxSelectProject.setEnabled(true);
      tmpUpdate(" ", 0);
      progressBarSimulation.setStringPainted(false);
      progressBarSimulation.setIndeterminate(false);
      progressBarSimulation.setValue(0);
      progressBarSimulation.setVisible(false);
      setCursor(null);
    }
  }

  /**
   * The inner class Simulation used to create the simulation process task which
   * will be executed in a separate thread to ensure the UI wont freeze.
   * 
   * @author A. Schoedon
   */
  class Simulation extends SwingWorker<Void, Void> {

    /**
     * Stores the starting timestamp of the simulation to calculate the status
     * and remaining time later.
     */
    private double start;

    /**
     * Gets the starting timestamp of the simulation.
     * 
     * @return The starting timestamp of the simulation.
     */
    public double getStart() {
      return start;
    }

    /**
     * Sets the starting timestamp of the simulation to calculate the status and
     * remaining time later.
     * 
     * @param start
     *          The starting timestamp of the simulation.
     */
    public void setStart(double start) {
      this.start = start;
    }

    /**
     * Updates the progress bar status and message and writes the current action
     * to a log file if existing.
     * 
     * @param s
     *          The log message.
     * @param i
     *          The status in percent.
     */
    private void tmpUpdate(String s, int i) {
      setLogMsg(s);
      setStatus(i);
      progressBarSimulation.setString(s);
      progressBarSimulation.setValue(i);
      try {
        if (OMHelper.isLogOutputEnabled()) {
          OMHelper.writeLog(getLogMsg());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * Writes messages to the log file which are not displayed at the progress
     * bar. This is used to increase performance.
     * 
     * @param s
     *          The log message.
     * @param i
     *          The status in percent.
     */
    private void logOnly(String s, int i) {
      setLogMsg(s);
      setStatus(i);
      try {
        if (OMHelper.isLogOutputEnabled()) {
          OMHelper.writeLog(getLogMsg());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * Starts the main simulation task which is executed in background thread.
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    public Void doInBackground() {
      try {
        int status = 0;
        setStart(System.currentTimeMillis());
        String logPath = getOmsFile();
        String logType = "random_simulation";
        if (isSystematic) {
          logPath = getOmbFile();
          logType = "systematic_simulation";
        }
        OMHelper.setLogOutput(logPath, logType);
        tmpUpdate("Starting ...", 1);
        OMBuilding building = getSelectedObject();
        tmpUpdate("Opened building: " + building, 1);
        int maxCampaigns = getRandomCampaigns();
        int ratioThree = getRatio3();
        int ratioFour = getRatio4();
        int ratioFive = getRatio5();
        int ratioSix = getRatio6();
        int randomNoise = getRandomNoise();
        if (isSystematic()) {
          generateSystematicCampaigns(building, randomNoise);
        } else {
          generateRandomCampaigns(building, maxCampaigns, ratioThree,
              ratioFour, ratioFive, ratioSix, randomNoise);
        }
        status = getStatus();
        String strFormat = "#.##";
        DecimalFormat decFormat = new DecimalFormat(strFormat);
        double total = (System.currentTimeMillis() - getStart()) / 1000;
        String unit = " seconds.";
        if (total > 100) {
          total = total / 60.0;
          unit = " minutes.";
          if (total > 100) {
            total = total / 60.0;
            unit = " hours.";
          }
        }
        if (status == 100) {
          tmpUpdate("Simulation finished after " + decFormat.format(total)
              + unit, status);
          JOptionPane.showMessageDialog(null, "Simulation finished after "
              + decFormat.format(total) + unit, "Success",
              JOptionPane.INFORMATION_MESSAGE);
        } else {
          tmpUpdate("Simulation failed. See log for details.", status);
          JOptionPane.showMessageDialog(null,
              "Simulation failed. See log for details.", "Failed",
              JOptionPane.ERROR_MESSAGE);
        }
        OMHelper.closeLog();
      } catch (IOException ioe) {
        tmpUpdate("Error: " + ioe.getMessage(), 0);
        tmpUpdate("Error: Completely Failed.", 0);
        JOptionPane.showMessageDialog(null,
            "Completely failed.\n" + ioe.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
        ioe.printStackTrace();
      }
      return null;
    }

    /**
     * Method to simulate systematic survey campaigns. It calculates summary
     * statistics for simulations with n > 1 million and descriptive statistics
     * for simulations with n <= 1 million. It writes a CSV file with the
     * results of the statistics in the end. This may take a while, grab a
     * coffee.
     * 
     * @param building
     *          An building consisting of all the rooms and their values.
     * @param randomNoise
     *          An integer which defines the random noise that is added to the
     *          values. A random noise of 0 means the original values wont be
     *          modified. The unit is [%].
     * @throws IOException
     *           If creating log file or writing logs fails.
     */
    private void generateSystematicCampaigns(OMBuilding building,
        int randomNoise) throws IOException {
      int valueCount = building.getValueCount();
      int total = valueCount - 7 * 24 + 1;
      if (total >= 1) {
        tmpUpdate(valueCount + " data records allow " + total
            + " possible times for starting a simulation.", 1);
        OMRoom[] variationSchemeSix[] = building.getVariationSchemeSix();
        OMRoom[] variationSchemeFive[] = building.getVariationSchemeFive();
        OMRoom[] variationSchemeFour[] = building.getVariationSchemeFour();
        OMRoom[] variationSchemeThree[] = building.getVariationSchemeThree();
        long x = 0;
        int campaignLengthSix = variationSchemeSix.length;
        int campaignLengthFive = variationSchemeFive.length;
        int campaignLengthFour = variationSchemeFour.length;
        int campaignLengthThree = variationSchemeThree.length;
        long perc = 1;
        OMCampaign campaign;
        SummaryStatistics roomAmSummaryStats = new SummaryStatistics();
        SummaryStatistics cellarAmSummaryStats = new SummaryStatistics();
        SummaryStatistics roomGmSummaryStats = new SummaryStatistics();
        SummaryStatistics cellarGmSummaryStats = new SummaryStatistics();
        SummaryStatistics roomMedSummaryStats = new SummaryStatistics();
        SummaryStatistics cellarMedSummaryStats = new SummaryStatistics();
        SummaryStatistics roomMaxSummaryStats = new SummaryStatistics();
        SummaryStatistics cellarMaxSummaryStats = new SummaryStatistics();
        DescriptiveStatistics roomAmDescriptiveStats = new DescriptiveStatistics();
        DescriptiveStatistics cellarAmDescriptiveStats = new DescriptiveStatistics();
        DescriptiveStatistics roomGmDescriptiveStats = new DescriptiveStatistics();
        DescriptiveStatistics cellarGmDescriptiveStats = new DescriptiveStatistics();
        DescriptiveStatistics roomMedDescriptiveStats = new DescriptiveStatistics();
        DescriptiveStatistics cellarMedDescriptiveStats = new DescriptiveStatistics();
        DescriptiveStatistics roomMaxDescriptiveStats = new DescriptiveStatistics();
        DescriptiveStatistics cellarMaxDescriptiveStats = new DescriptiveStatistics();
        boolean isDescriptive = false;
        long max = 0;
        if (campaignLengthSix > 0) {
          max = total * campaignLengthSix;
        } else {
          if (campaignLengthFive > 0) {
            max = total * campaignLengthFive;
          } else {
            if (campaignLengthFour > 0) {
              max = total * campaignLengthFour;
            } else {
              if (campaignLengthThree > 0) {
                max = total * campaignLengthThree;
              } else {
                max = 0;
              }
            }
          }
        }
        if (max <= 1000000) {
          isDescriptive = true;
          if (campaignLengthSix > 0) {
            tmpUpdate(
                "Starting unlimited descriptive simulation for 6 different rooms.",
                1);
            for (int a = 0; a < campaignLengthSix; a++) {
              perc = (x * 100) / max;
              tmpUpdate("Status: " + perc + "% (Estimated time left: "
                  + timeLeft(((double) x * 100.00) / (double) max) + ")",
                  (int) perc);
              for (int start = 0; start < total; start++) {
                campaign = new OMCampaign(start, variationSchemeSix[a],
                    randomNoise);
                roomAmDescriptiveStats.addValue(campaign.getRoomAvarage());
                cellarAmDescriptiveStats.addValue(campaign.getCellarAvarage());
                roomGmDescriptiveStats.addValue(campaign.getRoomLogAvarage());
                cellarGmDescriptiveStats.addValue(campaign
                    .getCellarLogAvarage());
                roomMedDescriptiveStats.addValue(campaign.getRoomMedian());
                cellarMedDescriptiveStats.addValue(campaign.getCellarMedian());
                roomMaxDescriptiveStats.addValue(campaign.getRoomMaxima());
                cellarMaxDescriptiveStats.addValue(campaign.getCellarMaxima());
                logOnly(campaign.toString(), (int) perc);
                x++;
              }
            }
            perc = (x * 100) / max;
            tmpUpdate(
                "Status: " + perc + "% - finished for 6 different rooms.",
                (int) perc);
          } else {
            tmpUpdate(
                "Warning: No variations for 6 different rooms available.",
                (int) perc);
            if (campaignLengthFive > 0) {
              tmpUpdate(
                  "Starting unlimited descriptive simulation for 5 different rooms.",
                  (int) perc);
              for (int a = 0; a < campaignLengthFive; a++) {
                perc = (x * 100) / max;
                tmpUpdate("Status: " + perc + "% (Estimated time left: "
                    + timeLeft(((double) x * 100.00) / (double) max) + ")",
                    (int) perc);
                for (int start = 0; start < total; start++) {
                  campaign = new OMCampaign(start, variationSchemeFive[a],
                      randomNoise);
                  roomAmDescriptiveStats.addValue(campaign.getRoomAvarage());
                  cellarAmDescriptiveStats
                      .addValue(campaign.getCellarAvarage());
                  roomGmDescriptiveStats.addValue(campaign.getRoomLogAvarage());
                  cellarGmDescriptiveStats.addValue(campaign
                      .getCellarLogAvarage());
                  roomMedDescriptiveStats.addValue(campaign.getRoomMedian());
                  cellarMedDescriptiveStats
                      .addValue(campaign.getCellarMedian());
                  roomMaxDescriptiveStats.addValue(campaign.getRoomMaxima());
                  cellarMaxDescriptiveStats
                      .addValue(campaign.getCellarMaxima());
                  logOnly(campaign.toString(), (int) perc);
                  x++;
                }
              }
              perc = (x * 100) / max;
              tmpUpdate("Status: " + perc
                  + "% - finished for 5 different rooms.", (int) perc);
            } else {
              tmpUpdate(
                  "Warning: No variations for 5 different rooms available.",
                  (int) perc);
              if (campaignLengthFour > 0) {
                tmpUpdate(
                    "Starting unlimited descriptive simulation for 4 different rooms.",
                    (int) perc);
                for (int a = 0; a < campaignLengthFour; a++) {
                  perc = (x * 100) / max;
                  tmpUpdate("Status: " + perc + "% (Estimated time left: "
                      + timeLeft(((double) x * 100.00) / (double) max) + ")",
                      (int) perc);
                  for (int start = 0; start < total; start++) {
                    campaign = new OMCampaign(start, variationSchemeFour[a],
                        randomNoise);
                    roomAmDescriptiveStats.addValue(campaign.getRoomAvarage());
                    cellarAmDescriptiveStats.addValue(campaign
                        .getCellarAvarage());
                    roomGmDescriptiveStats.addValue(campaign
                        .getRoomLogAvarage());
                    cellarGmDescriptiveStats.addValue(campaign
                        .getCellarLogAvarage());
                    roomMedDescriptiveStats.addValue(campaign.getRoomMedian());
                    cellarMedDescriptiveStats.addValue(campaign
                        .getCellarMedian());
                    roomMaxDescriptiveStats.addValue(campaign.getRoomMaxima());
                    cellarMaxDescriptiveStats.addValue(campaign
                        .getCellarMaxima());
                    logOnly(campaign.toString(), (int) perc);
                    x++;
                  }
                }
                perc = (x * 100) / max;
                tmpUpdate("Status: " + perc
                    + "% - finished for 4 different rooms.", (int) perc);
              } else {
                tmpUpdate(
                    "Warning: No variations for 4 different rooms available.",
                    (int) perc);
                if (campaignLengthThree > 0) {
                  tmpUpdate(
                      "Starting unlimited descriptive simulation for 3 different rooms.",
                      (int) perc);
                  for (int a = 0; a < campaignLengthThree; a++) {
                    perc = (x * 100) / max;
                    tmpUpdate("Status: " + perc + "% (Estimated time left: "
                        + timeLeft(((double) x * 100.00) / (double) max) + ")",
                        (int) perc);
                    for (int start = 0; start < total; start++) {
                      campaign = new OMCampaign(start, variationSchemeThree[a],
                          randomNoise);
                      roomAmDescriptiveStats
                          .addValue(campaign.getRoomAvarage());
                      cellarAmDescriptiveStats.addValue(campaign
                          .getCellarAvarage());
                      roomGmDescriptiveStats.addValue(campaign
                          .getRoomLogAvarage());
                      cellarGmDescriptiveStats.addValue(campaign
                          .getCellarLogAvarage());
                      roomMedDescriptiveStats
                          .addValue(campaign.getRoomMedian());
                      cellarMedDescriptiveStats.addValue(campaign
                          .getCellarMedian());
                      roomMaxDescriptiveStats
                          .addValue(campaign.getRoomMaxima());
                      cellarMaxDescriptiveStats.addValue(campaign
                          .getCellarMaxima());
                      logOnly(campaign.toString(), (int) perc);
                      x++;
                    }
                  }
                  perc = (x * 100) / max;
                  tmpUpdate("Status: " + perc
                      + "% - finished for 3 different rooms.", (int) perc);
                } else {
                  tmpUpdate(
                      "Warning: No variations for 3 different rooms available.",
                      (int) perc);
                  tmpUpdate(
                      "Error: No variations generated yet, something went wrong.",
                      0);
                }
              }
            }
          }
        } else {
          isDescriptive = false;
          if (campaignLengthSix > 0) {
            tmpUpdate(
                "Starting unlimited summary simulation for 6 different rooms.",
                (int) perc);
            for (int a = 0; a < campaignLengthSix; a++) {
              perc = (x * 100) / max;
              tmpUpdate("Status: " + perc + "% (Estimated time left: "
                  + timeLeft(((double) x * 100.00) / (double) max) + ")",
                  (int) perc);
              for (int start = 0; start < total; start++) {
                campaign = new OMCampaign(start, variationSchemeSix[a],
                    randomNoise);
                roomAmSummaryStats.addValue(campaign.getRoomAvarage());
                cellarAmSummaryStats.addValue(campaign.getCellarAvarage());
                roomGmSummaryStats.addValue(campaign.getRoomLogAvarage());
                cellarGmSummaryStats.addValue(campaign.getCellarLogAvarage());
                roomMedSummaryStats.addValue(campaign.getRoomMedian());
                cellarMedSummaryStats.addValue(campaign.getCellarMedian());
                roomMaxSummaryStats.addValue(campaign.getRoomMaxima());
                cellarMaxSummaryStats.addValue(campaign.getCellarMaxima());
                logOnly(campaign.toString(), (int) perc);
                x++;
              }
            }
            perc = (x * 100) / max;
            tmpUpdate(
                "Status: " + perc + "% - finished for 6 different rooms.",
                (int) perc);
          } else {
            tmpUpdate(
                "Warning: No variations for 6 different rooms available.",
                (int) perc);
            if (campaignLengthFive > 0) {
              tmpUpdate(
                  "Starting unlimited summary simulation for 5 different rooms.",
                  (int) perc);
              for (int a = 0; a < campaignLengthFive; a++) {
                perc = (x * 100) / max;
                tmpUpdate("Status: " + perc + "% (Estimated time left: "
                    + timeLeft(((double) x * 100.00) / (double) max) + ")",
                    (int) perc);
                for (int start = 0; start < total; start++) {
                  campaign = new OMCampaign(start, variationSchemeFive[a],
                      randomNoise);
                  roomAmSummaryStats.addValue(campaign.getRoomAvarage());
                  cellarAmSummaryStats.addValue(campaign.getCellarAvarage());
                  roomGmSummaryStats.addValue(campaign.getRoomLogAvarage());
                  cellarGmSummaryStats.addValue(campaign.getCellarLogAvarage());
                  roomMedSummaryStats.addValue(campaign.getRoomMedian());
                  cellarMedSummaryStats.addValue(campaign.getCellarMedian());
                  roomMaxSummaryStats.addValue(campaign.getRoomMaxima());
                  cellarMaxSummaryStats.addValue(campaign.getCellarMaxima());
                  logOnly(campaign.toString(), (int) perc);
                  x++;
                }
              }
              perc = (x * 100) / max;
              tmpUpdate("Status: " + perc
                  + "% - finished for 5 different rooms.", (int) perc);
            } else {
              tmpUpdate(
                  "Warning: No variations for 5 different rooms available.",
                  (int) perc);
              if (campaignLengthFour > 0) {
                tmpUpdate(
                    "Starting unlimited summary simulation for 4 different rooms.",
                    (int) perc);
                for (int a = 0; a < campaignLengthFour; a++) {
                  perc = (x * 100) / max;
                  tmpUpdate("Status: " + perc + "% (Estimated time left: "
                      + timeLeft(((double) x * 100.00) / (double) max) + ")",
                      (int) perc);
                  for (int start = 0; start < total; start++) {
                    campaign = new OMCampaign(start, variationSchemeFour[a],
                        randomNoise);
                    roomAmSummaryStats.addValue(campaign.getRoomAvarage());
                    cellarAmSummaryStats.addValue(campaign.getCellarAvarage());
                    roomGmSummaryStats.addValue(campaign.getRoomLogAvarage());
                    cellarGmSummaryStats.addValue(campaign
                        .getCellarLogAvarage());
                    roomMedSummaryStats.addValue(campaign.getRoomMedian());
                    cellarMedSummaryStats.addValue(campaign.getCellarMedian());
                    roomMaxSummaryStats.addValue(campaign.getRoomMaxima());
                    cellarMaxSummaryStats.addValue(campaign.getCellarMaxima());
                    logOnly(campaign.toString(), (int) perc);
                    x++;
                  }
                }
                perc = (x * 100) / max;
                tmpUpdate("Status: " + perc
                    + "% - finished for 4 different rooms.", (int) perc);
              } else {
                tmpUpdate(
                    "Warning: No variations for 4 different rooms available.",
                    (int) perc);
                if (campaignLengthThree > 0) {
                  tmpUpdate(
                      "Starting unlimited summary simulation for 3 different rooms.",
                      (int) perc);
                  for (int a = 0; a < campaignLengthThree; a++) {
                    perc = (x * 100) / max;
                    tmpUpdate("Status: " + perc + "% (Estimated time left: "
                        + timeLeft(((double) x * 100.00) / (double) max) + ")",
                        (int) perc);
                    for (int start = 0; start < total; start++) {
                      campaign = new OMCampaign(start, variationSchemeThree[a],
                          randomNoise);
                      roomAmSummaryStats.addValue(campaign.getRoomAvarage());
                      cellarAmSummaryStats
                          .addValue(campaign.getCellarAvarage());
                      roomGmSummaryStats.addValue(campaign.getRoomLogAvarage());
                      cellarGmSummaryStats.addValue(campaign
                          .getCellarLogAvarage());
                      roomMedSummaryStats.addValue(campaign.getRoomMedian());
                      cellarMedSummaryStats
                          .addValue(campaign.getCellarMedian());
                      roomMaxSummaryStats.addValue(campaign.getRoomMaxima());
                      cellarMaxSummaryStats
                          .addValue(campaign.getCellarMaxima());
                      logOnly(campaign.toString(), (int) perc);
                      x++;
                    }
                  }
                  perc = (x * 100) / max;
                  tmpUpdate("Status: " + perc
                      + "% - finished for 3 different rooms.", (int) perc);
                } else {
                  tmpUpdate(
                      "Warning: No variations for 3 different rooms available.",
                      (int) perc);
                  tmpUpdate(
                      "Error: No variations generated yet, what went wrong?", 0);
                }
              }
            }
          }
        }
        tmpUpdate("Generated " + x + " campaigns.", (int) perc);
        String csvPath = "";
        if (getOmsFile() != null && !getOmsFile().equals("") && !getOmsFile().equals(" ")) {
          csvPath = getOmsFile();
        } else {
          csvPath = getOmbFile();
        }
        String logName = csvPath + "_simulation.result.csv";
        File logFile = new File(logName);
        FileWriter logWriter = new FileWriter(logFile);
        BufferedWriter csvOutput = new BufferedWriter(logWriter);
        String strFormat = "#.#########";
        DecimalFormat decFormat = new DecimalFormat(strFormat);
        if (isDescriptive) {
          descriptiveStatistics(x, roomAmDescriptiveStats,
              cellarAmDescriptiveStats, roomGmDescriptiveStats,
              cellarGmDescriptiveStats, roomMedDescriptiveStats,
              cellarMedDescriptiveStats, roomMaxDescriptiveStats,
              cellarMaxDescriptiveStats, csvOutput, decFormat);
        } else {
          summaryStatistics(x, roomAmSummaryStats, cellarAmSummaryStats,
              roomGmSummaryStats, cellarGmSummaryStats, roomMedSummaryStats,
              cellarMedSummaryStats, roomMaxSummaryStats,
              cellarMaxSummaryStats, csvOutput, decFormat);
        }
        csvOutput.close();
        setStatus(100);
      } else {
        tmpUpdate("Error: " + valueCount + " are not enough data records.", 0);
        tmpUpdate("Make sure you have at least one week of records (> 168).", 0);
        setStatus(0);
      }
    }

    /**
     * Method to simulate random survey campaigns using a defined maximum number
     * and a defined ratio between different types of variations used. It
     * calculates descriptive statistics for simulations with n <= 100000. It
     * writes a CSV file with the results of the statistics in the end. This may
     * take a while, grab a coffee.
     * 
     * @param building
     *          An building consisting of all the rooms and their values.
     * @param maxCampaigns
     *          The maximum number of campaigns to simulate.
     * @param ratioThree
     *          An integer to define the ratio of variations which use three
     *          different rooms only.
     * @param ratioFour
     *          An integer to define the ratio of variations which use four
     *          different rooms only.
     * @param ratioFive
     *          An integer to define the ratio of variations which use five
     *          different rooms only.
     * @param ratioSix
     *          An integer to define the ratio of variations which use six
     *          different rooms.
     * @param randomNoise
     *          An integer which defines the random noise that is added to the
     *          values. A random noise of 0 means the original values wont be
     *          modified. The unit is [%].
     * @throws IOException
     *           If creating log file or writing logs fails.
     */
    private void generateRandomCampaigns(OMBuilding building, int maxCampaigns,
        int ratioThree, int ratioFour, int ratioFive, int ratioSix,
        int randomNoise) throws IOException {
      int valueCount = building.getValueCount();
      int absoluteThree;
      int absoluteFour;
      int absoluteFive;
      int absoluteSix;
      long absoluteTotal;
      if (ratioThree >= 0 && ratioFour >= 0 && ratioFive >= 0 && ratioSix >= 0) {
        OMRoom[] variationSchemeSix[] = building.getVariationSchemeSix();
        OMRoom[] variationSchemeFive[] = building.getVariationSchemeFive();
        OMRoom[] variationSchemeFour[] = building.getVariationSchemeFour();
        OMRoom[] variationSchemeThree[] = building.getVariationSchemeThree();
        double tmpDiv = 4.0;
        int campaignLengthSix = variationSchemeSix.length;
        if (campaignLengthSix <= 0) {
          tmpUpdate("Warning: No variations for 6 rooms. Setting ratio to 0.",
              1);
          ratioSix = 0;
          tmpDiv--;
        }
        int campaignLengthFive = variationSchemeFive.length;
        if (campaignLengthFive <= 0) {
          tmpUpdate("Warning: No variations for 5 rooms. Setting ratio to 0.",
              1);
          ratioFive = 0;
          tmpDiv--;
        }
        int campaignLengthFour = variationSchemeFour.length;
        if (campaignLengthFour <= 0) {
          tmpUpdate("Warning: No variations for 4 rooms. Setting ratio to 0.",
              1);
          ratioFour = 0;
          tmpDiv--;
        }
        int campaignLengthThree = variationSchemeThree.length;
        int ratioTotal = ratioThree + ratioFour + ratioFive + ratioSix;
        int total = valueCount - 7 * 24 + 1;
        if (ratioTotal <= 0) {
          tmpUpdate("Warning: Your entered ratio is: " + ratioThree + ", "
              + ratioFour + ", " + ratioFive + ", " + ratioSix + ".", 1);
          tmpUpdate("Warning: Splitting the simulation in equal parts of 25%.",
              1);
          absoluteThree = (int) ((double) maxCampaigns / tmpDiv);
          if (campaignLengthFour >= 0 && tmpDiv >= 2) { // WTF?
            absoluteFour = (int) ((double) maxCampaigns / tmpDiv);
            if (campaignLengthFive >= 0 && tmpDiv >= 3) {
              absoluteFive = (int) ((double) maxCampaigns / tmpDiv);
              if (campaignLengthSix >= 0 && tmpDiv >= 4) {
                absoluteSix = (int) ((double) maxCampaigns / tmpDiv);
              } else {
                absoluteSix = 0;
              }
            } else {
              absoluteFive = 0;
              absoluteSix = 0;
            }
          } else {
            absoluteFour = 0;
            absoluteFive = 0;
            absoluteSix = 0;
          }
          absoluteTotal = absoluteSix + absoluteFive + absoluteFour
              + absoluteThree;
          while (absoluteTotal != maxCampaigns) {
            if (absoluteTotal < maxCampaigns) {
              if (ratioSix > 0) {
                absoluteSix++;
              } else {
                if (ratioFive > 0) {
                  absoluteFive++;
                } else {
                  if (ratioFour > 0) {
                    absoluteFour++;
                  } else {
                    absoluteThree++;
                  }
                }
              }
            } else {
              if (ratioSix > 0) {
                absoluteSix--;
              } else {
                if (ratioFive > 0) {
                  absoluteFive--;
                } else {
                  if (ratioFour > 0) {
                    absoluteFour--;
                  } else {
                    absoluteThree--;
                  }
                }
              }
            }
            absoluteTotal = absoluteSix + absoluteFive + absoluteFour
                + absoluteThree;
          }
        } else {
          absoluteThree = (int) (((double) ratioThree / (double) ratioTotal) * (double) maxCampaigns);
          absoluteFour = (int) (((double) ratioFour / (double) ratioTotal) * (double) maxCampaigns);
          absoluteFive = (int) (((double) ratioFive / (double) ratioTotal) * (double) maxCampaigns);
          absoluteSix = (int) (((double) ratioSix / (double) ratioTotal) * (double) maxCampaigns);
          absoluteTotal = absoluteSix + absoluteFive + absoluteFour
              + absoluteThree;
          while (absoluteTotal != maxCampaigns) {
            if (absoluteTotal < maxCampaigns) {
              if (ratioSix > 0) {
                absoluteSix++;
              } else {
                if (ratioFive > 0) {
                  absoluteFive++;
                } else {
                  if (ratioFour > 0) {
                    absoluteFour++;
                  } else {
                    absoluteThree++;
                  }
                }
              }
            } else {
              if (ratioSix > 0) {
                absoluteSix--;
              } else {
                if (ratioFive > 0) {
                  absoluteFive--;
                } else {
                  if (ratioFour > 0) {
                    absoluteFour--;
                  } else {
                    absoluteThree--;
                  }
                }
              }
            }
            absoluteTotal = absoluteSix + absoluteFive + absoluteFour
                + absoluteThree;
          }
        }
        tmpUpdate("Simulation will start with " + absoluteTotal
            + " random variations:", 1);
        tmpUpdate(absoluteThree
            + " Simulations using 3 different rooms. (Ratio: ca. "
            + (int) ((double) ratioThree / (double) ratioTotal * 100.0) + "% ("
            + ratioThree + "))", 1);
        if (absoluteThree > campaignLengthThree * total) {
          tmpUpdate("Warning: Simulating " + absoluteThree
              + " campaigns for 3 rooms, but only "
              + (campaignLengthThree * total)
              + " variations are existing for 3 rooms.", 1);
          tmpUpdate(
              "Warning: Consider to reduce the ratio or number of total simulations.",
              1);
        }
        tmpUpdate(absoluteFour
            + " Simulations using 4 different rooms. (Ratio: ca. "
            + (int) ((double) ratioFour / (double) ratioTotal * 100.0) + "% ("
            + ratioFour + "))", 1);
        if (absoluteFour > campaignLengthFour * total) {
          tmpUpdate("Warning: Simulating " + absoluteFour
              + " campaigns for 4 rooms, but only "
              + (campaignLengthFour * total)
              + " variations are existing for 4 rooms.", 1);
          tmpUpdate(
              "Warning: Consider to reduce the ratio or number of total simulations.",
              1);
        }
        tmpUpdate(absoluteFive
            + " Simulations using 5 different rooms. (Ratio: ca. "
            + (int) ((double) ratioFive / (double) ratioTotal * 100.0) + "% ("
            + ratioFive + "))", 1);
        if (absoluteFive > campaignLengthFive * total) {
          tmpUpdate("Warning: Simulating " + absoluteFive
              + " campaigns for 5 rooms, but only "
              + (campaignLengthFive * total)
              + " variations are existing for 5 rooms.", 1);
          tmpUpdate(
              "Warning: Consider to reduce the ratio or number of total simulations.",
              1);
        }
        tmpUpdate(absoluteSix
            + " Simulations using 6 different rooms. (Ratio: ca. "
            + (int) ((double) ratioSix / (double) ratioTotal * 100.0) + "% ("
            + ratioSix + "))", 1);
        if (absoluteSix > campaignLengthSix * total) {
          tmpUpdate("Warning: Simulating " + absoluteSix
              + " campaigns for 6 rooms, but only "
              + (campaignLengthSix * total)
              + " variations are existing for 6 rooms.", 1);
          tmpUpdate(
              "Warning: Consider to reduce the ratio or number of total simulations.",
              1);
        }
        if (total >= 1) {
          tmpUpdate(valueCount + " data records allow " + total
              + " possible times for starting a simulation.", 1);
          long x = 0;
          long perc = 1;
          OMCampaign campaign;
          DescriptiveStatistics roomAmDescriptiveStats = new DescriptiveStatistics();
          DescriptiveStatistics cellarAmDescriptiveStats = new DescriptiveStatistics();
          DescriptiveStatistics roomGmDescriptiveStats = new DescriptiveStatistics();
          DescriptiveStatistics cellarGmDescriptiveStats = new DescriptiveStatistics();
          DescriptiveStatistics roomMedDescriptiveStats = new DescriptiveStatistics();
          DescriptiveStatistics cellarMedDescriptiveStats = new DescriptiveStatistics();
          DescriptiveStatistics roomMaxDescriptiveStats = new DescriptiveStatistics();
          DescriptiveStatistics cellarMaxDescriptiveStats = new DescriptiveStatistics();
          boolean isDescriptive = false;
          OMCampaign[] campaigns = new OMCampaign[(int) absoluteTotal];
          if (absoluteTotal <= 1000000) {
            isDescriptive = true;
            if (campaignLengthThree > 0) {
              tmpUpdate("Starting descriptive simulation for 3 rooms with "
                  + absoluteThree + " random variations.", (int) perc);
              Random generator = new Random();
              int[] random = new int[absoluteThree];
              for (int n = 0; n < absoluteThree; n++) {
                random[n] = generator.nextInt(campaignLengthThree);
              }
              int start;
              int mod = 0;
              for (int a = 0; a < absoluteThree; a++) {
                mod = a % 100;
                if (mod == 0) {
                  perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                  tmpUpdate("Status: " + perc + "% (Estimated time left: "
                    + timeLeft(((double) x / (double) absoluteTotal) * (double) 100.0) + ")",
                    (int) perc);
                }
                if (total > 1) {
                  start = generator.nextInt(total);
                } else {
                  start = 0;
                }
                campaign = new OMCampaign(start,
                    variationSchemeThree[random[a]], randomNoise);
                roomAmDescriptiveStats.addValue(campaign.getRoomAvarage());
                cellarAmDescriptiveStats.addValue(campaign.getCellarAvarage());
                roomGmDescriptiveStats.addValue(campaign.getRoomLogAvarage());
                cellarGmDescriptiveStats.addValue(campaign
                    .getCellarLogAvarage());
                roomMedDescriptiveStats.addValue(campaign.getRoomMedian());
                cellarMedDescriptiveStats.addValue(campaign.getCellarMedian());
                roomMaxDescriptiveStats.addValue(campaign.getRoomMaxima());
                cellarMaxDescriptiveStats.addValue(campaign.getCellarMaxima());
                logOnly(campaign.toString(), (int) perc);
                campaigns[(int) x] = campaign;
                x++;
              }
              perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
              tmpUpdate("Status: " + perc
                  + "% - finished for 3 different rooms.", (int) perc);
              if (campaignLengthFour > 0) {
                tmpUpdate("Starting descriptive simulation for 4 rooms with "
                    + absoluteFour + " random variations.", (int) perc);
                generator = new Random();
                random = new int[absoluteFour];
                for (int n = 0; n < absoluteFour; n++) {
                  random[n] = generator.nextInt(campaignLengthFour);
                }
                mod = 0;
                for (int a = 0; a < absoluteFour; a++) {
                  mod = a % 100;
                  if (mod == 0) {
                    perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                    tmpUpdate("Status: " + perc + "% (Estimated time left: "
                      + timeLeft(((double) x / (double) absoluteTotal) * (double) 100.0) + ")",
                      (int) perc);
                  }
                  if (total > 1) {
                    start = generator.nextInt(total);
                  } else {
                    start = 0;
                  }
                  campaign = new OMCampaign(start,
                      variationSchemeFour[random[a]], randomNoise);
                  roomAmDescriptiveStats.addValue(campaign.getRoomAvarage());
                  cellarAmDescriptiveStats
                      .addValue(campaign.getCellarAvarage());
                  roomGmDescriptiveStats.addValue(campaign.getRoomLogAvarage());
                  cellarGmDescriptiveStats.addValue(campaign
                      .getCellarLogAvarage());
                  roomMedDescriptiveStats.addValue(campaign.getRoomMedian());
                  cellarMedDescriptiveStats
                      .addValue(campaign.getCellarMedian());
                  roomMaxDescriptiveStats.addValue(campaign.getRoomMaxima());
                  cellarMaxDescriptiveStats
                      .addValue(campaign.getCellarMaxima());
                  logOnly(campaign.toString(), (int) perc);
                  campaigns[(int) x] = campaign;
                  x++;
                }
                perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                tmpUpdate("Status: " + perc
                    + "% - finished for 4 different rooms.", (int) perc);
                if (campaignLengthFive > 0) {
                  tmpUpdate("Starting descriptive simulation for 5 rooms with "
                      + absoluteFive + " random variations.", (int) perc);
                  generator = new Random();
                  random = new int[absoluteFive];
                  for (int n = 0; n < absoluteFive; n++) {
                    random[n] = generator.nextInt(campaignLengthFive);
                  }
                  mod = 0;
                  for (int a = 0; a < absoluteFive; a++) {
                    mod = a % 100;
                    if (mod == 0) {
                      perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                      tmpUpdate("Status: " + perc + "% (Estimated time left: "
                        + timeLeft(((double) x / (double) absoluteTotal) * (double) 100.0) + ")",
                        (int) perc);
                    }
                    if (total > 1) {
                      start = generator.nextInt(total);
                    } else {
                      start = 0;
                    }
                    campaign = new OMCampaign(start,
                        variationSchemeFive[random[a]], randomNoise);
                    roomAmDescriptiveStats.addValue(campaign.getRoomAvarage());
                    cellarAmDescriptiveStats.addValue(campaign
                        .getCellarAvarage());
                    roomGmDescriptiveStats.addValue(campaign
                        .getRoomLogAvarage());
                    cellarGmDescriptiveStats.addValue(campaign
                        .getCellarLogAvarage());
                    roomMedDescriptiveStats.addValue(campaign.getRoomMedian());
                    cellarMedDescriptiveStats.addValue(campaign
                        .getCellarMedian());
                    roomMaxDescriptiveStats.addValue(campaign.getRoomMaxima());
                    cellarMaxDescriptiveStats.addValue(campaign
                        .getCellarMaxima());
                    logOnly(campaign.toString(), (int) perc);
                    campaigns[(int) x] = campaign;
                    x++;
                  }
                  perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                  tmpUpdate("Status: " + perc
                      + "% - finished for 5 different rooms.", (int) perc);
                  if (campaignLengthSix > 0) {
                    tmpUpdate(
                        "Starting descriptive simulation for 6 rooms with "
                            + absoluteSix + " random variations.", (int) perc);
                    generator = new Random();
                    random = new int[absoluteSix];
                    for (int n = 0; n < absoluteSix; n++) {
                      random[n] = generator.nextInt(campaignLengthSix);
                    }
                    mod = 0;
                    for (int a = 0; a < absoluteSix; a++) {
                      mod = a % 100;
                      if (mod == 0) {
                        perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                        tmpUpdate("Status: " + perc + "% (Estimated time left: "
                          + timeLeft(((double) x / (double) absoluteTotal) * (double) 100.0) + ")",
                          (int) perc);
                      }
                      if (total > 1) {
                        start = generator.nextInt(total);
                      } else {
                        start = 0;
                      }
                      campaign = new OMCampaign(start,
                          variationSchemeSix[random[a]], randomNoise);
                      roomAmDescriptiveStats
                          .addValue(campaign.getRoomAvarage());
                      cellarAmDescriptiveStats.addValue(campaign
                          .getCellarAvarage());
                      roomGmDescriptiveStats.addValue(campaign
                          .getRoomLogAvarage());
                      cellarGmDescriptiveStats.addValue(campaign
                          .getCellarLogAvarage());
                      roomMedDescriptiveStats
                          .addValue(campaign.getRoomMedian());
                      cellarMedDescriptiveStats.addValue(campaign
                          .getCellarMedian());
                      roomMaxDescriptiveStats
                          .addValue(campaign.getRoomMaxima());
                      cellarMaxDescriptiveStats.addValue(campaign
                          .getCellarMaxima());
                      logOnly(campaign.toString(), (int) perc);
                      campaigns[(int) x] = campaign;
                      x++;
                    }
                    perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                    tmpUpdate("Status: " + perc
                        + "% - finished for 6 different rooms.", (int) perc);
                  } else {
                    tmpUpdate(
                        "Warning: No variations for 6 different rooms available.",
                        (int) perc);
                  }
                } else {
                  tmpUpdate(
                      "Warning: No variations for 5 different rooms available.",
                      (int) perc);
                  tmpUpdate(
                      "Warning: No variations for 6 different rooms available.",
                      (int) perc);
                }
              } else {
                tmpUpdate(
                    "Warning: No variations for 4 different rooms available.",
                    (int) perc);
                tmpUpdate(
                    "Warning: No variations for 5 different rooms available.",
                    (int) perc);
                tmpUpdate(
                    "Warning: No variations for 6 different rooms available.",
                    (int) perc);
              }
            } else {
              tmpUpdate(
                  "Warning: No variations for 3 different rooms available.",
                  (int) perc);
              tmpUpdate(
                  "Warning: No variations for 4 different rooms available.",
                  (int) perc);
              tmpUpdate(
                  "Warning: No variations for 5 different rooms available.",
                  (int) perc);
              tmpUpdate(
                  "Warning: No variations for 6 different rooms available.",
                  (int) perc);
              tmpUpdate("Error: No variations generated yet, what went wrong?",
                  0);
            }
          } else {
            tmpUpdate(
                "Error: Strange exception occured, this should never happen.",
                0);
            tmpUpdate("Error: No campaigns simulated.", 0);
            JOptionPane.showMessageDialog(null,
                "Strange exception occured, no campaigns simulated.", "Error",
                JOptionPane.ERROR_MESSAGE);
          }
          tmpUpdate("Generated " + x + " campaigns.", (int) perc);
          String csvPath = "";
          if (getOmsFile() != null && !getOmsFile().equals("") && !getOmsFile().equals(" ")) {
            csvPath = getOmsFile();
          } else {
            csvPath = getOmbFile();
          }
          String logName = csvPath + "_simulation.result.csv";
          File logFile = new File(logName);
          FileWriter logWriter = new FileWriter(logFile);
          BufferedWriter csvOutput = new BufferedWriter(logWriter);
          String strFormat = "#.#########";
          DecimalFormat decFormat = new DecimalFormat(strFormat);
          if (isDescriptive) {
            descriptiveStatistics(x, roomAmDescriptiveStats,
                cellarAmDescriptiveStats, roomGmDescriptiveStats,
                cellarGmDescriptiveStats, roomMedDescriptiveStats,
                cellarMedDescriptiveStats, roomMaxDescriptiveStats,
                cellarMaxDescriptiveStats, csvOutput, decFormat);
            tmpUpdate(
                "Storing simulation to object file. This can take a few minutes.",
                (int) perc);
            progressBarSimulation.setIndeterminate(true);
            ObjectContainer db4o = Db4oEmbedded.openFile(
                Db4oEmbedded.newConfiguration(), getOmsFile());
            OMSimulation simulation = new OMSimulation(building.getName(),
                building, campaigns, roomAmDescriptiveStats,
                cellarAmDescriptiveStats, roomGmDescriptiveStats,
                cellarGmDescriptiveStats, roomMedDescriptiveStats,
                cellarMedDescriptiveStats, roomMaxDescriptiveStats,
                cellarMaxDescriptiveStats);
            db4o.store(simulation);
            db4o.close();
            tmpUpdate(
                "Done. Stored simulation to file '" + getOmsFile() + "'.",
                (int) perc);
          } else {
            tmpUpdate(
                "Error: Strange exception occured, this should never happen.",
                0);
            tmpUpdate("Error: No statistics calculated.", 0);
            JOptionPane.showMessageDialog(null,
                "Strange exception occured, no statistics calculated.",
                "Error", JOptionPane.ERROR_MESSAGE);
          }
          csvOutput.close();
          setStatus(100);
        } else {
          tmpUpdate("Error: " + valueCount + " are not enough data records.", 0);
          tmpUpdate("Make sure you have at least one week of records (> 168).",
              0);
          setStatus(0);
        }
      } else {
        tmpUpdate("Error: Your entered ratio is: " + ratioThree + ", "
            + ratioFour + ", " + ratioFive + ", " + ratioSix + ".", 0);
        tmpUpdate("Error: Please, correct your input.", 0);
        setStatus(0);
      }
    }

    /**
     * Method used to calculate descriptive statistics which are stored in
     * memory. Writes results of the calculations to a separate CSV file. Only
     * use this for small simulations as this can cause memory exceptions.
     * 
     * @param x
     *          The total number of simulations.
     * @param roomAmDescriptiveStats
     *          Statistics for room arithmetric means.
     * @param cellarAmDescriptiveStats
     *          Statistics for cellar arithmetric means.
     * @param roomGmDescriptiveStats
     *          Statistics for room geometric means.
     * @param cellarGmDescriptiveStats
     *          Statistics for cellar geometric means.
     * @param roomMedDescriptiveStats
     *          Statistics for room medians.
     * @param cellarMedDescriptiveStats
     *          Statistics for cellar medians.
     * @param roomMaxDescriptiveStats
     *          Statistics for room maxima.
     * @param cellarMaxDescriptiveStats
     *          Statistics for cellar maxima.
     * @param csvOutput
     *          A a file buffer writer used to write the results to CSV.
     * @param decFormat
     *          The format used to store the results with comma or dot used as
     *          decimal separator.
     * @throws IOException
     *           If creating log file or writing logs fails.
     */
    private void descriptiveStatistics(long x,
        DescriptiveStatistics roomAmDescriptiveStats,
        DescriptiveStatistics cellarAmDescriptiveStats,
        DescriptiveStatistics roomGmDescriptiveStats,
        DescriptiveStatistics cellarGmDescriptiveStats,
        DescriptiveStatistics roomMedDescriptiveStats,
        DescriptiveStatistics cellarMedDescriptiveStats,
        DescriptiveStatistics roomMaxDescriptiveStats,
        DescriptiveStatistics cellarMaxDescriptiveStats,
        BufferedWriter csvOutput, DecimalFormat decFormat) throws IOException {
      double roomArithMeans_AM = roomAmDescriptiveStats.getMean();
      double roomArithMeans_SD = roomAmDescriptiveStats.getStandardDeviation();
      double roomArithMeans_CV = OMHelper.calculateCV(roomArithMeans_AM,
          roomArithMeans_SD);
      double roomArithMeans_Q05 = roomAmDescriptiveStats.getPercentile(5);
      double roomArithMeans_Q50 = roomAmDescriptiveStats.getPercentile(50);
      double roomArithMeans_Q95 = roomAmDescriptiveStats.getPercentile(95);
      double roomArithMeans_QD = OMHelper.calculateQD(roomArithMeans_Q05,
          roomArithMeans_Q50, roomArithMeans_Q95);
      double roomArithMeans_GM = roomAmDescriptiveStats.getGeometricMean();
      double roomArithMeans_GSD = OMHelper.calculateGSD(x,
          roomAmDescriptiveStats.getValues(), roomArithMeans_GM);
      double cellarArithMeans_AM = cellarAmDescriptiveStats.getMean();
      double cellarArithMeans_SD = cellarAmDescriptiveStats
          .getStandardDeviation();
      double cellarArithMeans_CV = OMHelper.calculateCV(cellarArithMeans_AM,
          cellarArithMeans_SD);
      double cellarArithMeans_Q05 = cellarAmDescriptiveStats.getPercentile(5);
      double cellarArithMeans_Q50 = cellarAmDescriptiveStats.getPercentile(50);
      double cellarArithMeans_Q95 = cellarAmDescriptiveStats.getPercentile(95);
      double cellarArithMeans_QD = OMHelper.calculateQD(cellarArithMeans_Q05,
          cellarArithMeans_Q50, cellarArithMeans_Q95);
      double cellarArithMeans_GM = cellarAmDescriptiveStats.getGeometricMean();
      double cellarArithMeans_GSD = OMHelper.calculateGSD(x,
          cellarAmDescriptiveStats.getValues(), cellarArithMeans_GM);
      double roomLogMeans_AM = roomGmDescriptiveStats.getMean();
      double roomLogMeans_SD = roomGmDescriptiveStats.getStandardDeviation();
      double roomLogMeans_CV = OMHelper.calculateCV(roomLogMeans_AM,
          roomLogMeans_SD);
      double roomLogMeans_Q05 = roomGmDescriptiveStats.getPercentile(5);
      double roomLogMeans_Q50 = roomGmDescriptiveStats.getPercentile(50);
      double roomLogMeans_Q95 = roomGmDescriptiveStats.getPercentile(95);
      double roomLogMeans_QD = OMHelper.calculateQD(roomLogMeans_Q05,
          roomLogMeans_Q50, roomLogMeans_Q95);
      double roomLogMeans_GM = roomGmDescriptiveStats.getGeometricMean();
      double roomLogMeans_GSD = OMHelper.calculateGSD(x,
          roomGmDescriptiveStats.getValues(), roomLogMeans_GM);
      double cellarLogMeans_AM = cellarGmDescriptiveStats.getMean();
      double cellarLogMeans_SD = cellarGmDescriptiveStats
          .getStandardDeviation();
      double cellarLogMeans_CV = OMHelper.calculateCV(cellarLogMeans_AM,
          cellarLogMeans_SD);
      double cellarLogMeans_Q05 = cellarGmDescriptiveStats.getPercentile(5);
      double cellarLogMeans_Q50 = cellarGmDescriptiveStats.getPercentile(50);
      double cellarLogMeans_Q95 = cellarGmDescriptiveStats.getPercentile(95);
      double cellarLogMeans_QD = OMHelper.calculateQD(cellarLogMeans_Q05,
          cellarLogMeans_Q50, cellarLogMeans_Q95);
      double cellarLogMeans_GM = cellarGmDescriptiveStats.getGeometricMean();
      double cellarLogMeans_GSD = OMHelper.calculateGSD(x,
          cellarGmDescriptiveStats.getValues(), cellarLogMeans_GM);
      double roomMedians_AM = roomMedDescriptiveStats.getMean();
      double roomMedians_SD = roomMedDescriptiveStats.getStandardDeviation();
      double roomMedians_CV = OMHelper.calculateCV(roomMedians_AM,
          roomMedians_SD);
      double roomMedians_Q05 = roomMedDescriptiveStats.getPercentile(5);
      double roomMedians_Q50 = roomMedDescriptiveStats.getPercentile(50);
      double roomMedians_Q95 = roomMedDescriptiveStats.getPercentile(95);
      double roomMedians_QD = OMHelper.calculateQD(roomMedians_Q05,
          roomMedians_Q50, roomMedians_Q95);
      double roomMedians_GM = roomMedDescriptiveStats.getGeometricMean();
      double roomMedians_GSD = OMHelper.calculateGSD(x,
          roomMedDescriptiveStats.getValues(), roomMedians_GM);
      double cellarMedians_AM = cellarMedDescriptiveStats.getMean();
      double cellarMedians_SD = cellarMedDescriptiveStats
          .getStandardDeviation();
      double cellarMedians_CV = OMHelper.calculateCV(cellarMedians_AM,
          cellarMedians_SD);
      double cellarMedians_Q05 = cellarMedDescriptiveStats.getPercentile(5);
      double cellarMedians_Q50 = cellarMedDescriptiveStats.getPercentile(50);
      double cellarMedians_Q95 = cellarMedDescriptiveStats.getPercentile(95);
      double cellarMedians_QD = OMHelper.calculateQD(cellarMedians_Q05,
          cellarMedians_Q50, cellarMedians_Q95);
      double cellarMedians_GM = cellarMedDescriptiveStats.getGeometricMean();
      double cellarMedians_GSD = OMHelper.calculateGSD(x,
          cellarMedDescriptiveStats.getValues(), cellarMedians_GM);
      double roomMaxima_AM = roomMaxDescriptiveStats.getMean();
      double roomMaxima_SD = roomMaxDescriptiveStats.getStandardDeviation();
      double roomMaxima_CV = OMHelper.calculateCV(roomMaxima_AM, roomMaxima_SD);
      double roomMaxima_Q05 = roomMaxDescriptiveStats.getPercentile(5);
      double roomMaxima_Q50 = roomMaxDescriptiveStats.getPercentile(50);
      double roomMaxima_Q95 = roomMaxDescriptiveStats.getPercentile(95);
      double roomMaxima_QD = OMHelper.calculateQD(roomMaxima_Q05,
          roomMaxima_Q50, roomMaxima_Q95);
      double roomMaxima_GM = roomMaxDescriptiveStats.getGeometricMean();
      double roomMaxima_GSD = OMHelper.calculateGSD(x,
          roomMaxDescriptiveStats.getValues(), roomMaxima_GM);
      double cellarMaxima_AM = cellarMaxDescriptiveStats.getMean();
      double cellarMaxima_SD = cellarMaxDescriptiveStats.getStandardDeviation();
      double cellarMaxima_CV = OMHelper.calculateCV(cellarMaxima_AM,
          cellarMaxima_SD);
      double cellarMaxima_Q05 = cellarMaxDescriptiveStats.getPercentile(5);
      double cellarMaxima_Q50 = cellarMaxDescriptiveStats.getPercentile(50);
      double cellarMaxima_Q95 = cellarMaxDescriptiveStats.getPercentile(95);
      double cellarMaxima_QD = OMHelper.calculateQD(cellarMaxima_Q05,
          cellarMaxima_Q50, cellarMaxima_Q95);
      double cellarMaxima_GM = cellarMaxDescriptiveStats.getGeometricMean();
      double cellarMaxima_GSD = OMHelper.calculateGSD(x,
          cellarMaxDescriptiveStats.getValues(), cellarMaxima_GM);
      tmpUpdate("Calculated statistics for room arithmetic means:", 99);
      logOnly("AM=" + roomArithMeans_AM + ", SD=" + roomArithMeans_SD + ", CV="
          + roomArithMeans_CV + ", GM=" + roomArithMeans_GM + ", GSD="
          + roomArithMeans_GSD + ", Q5=" + roomArithMeans_Q05 + ", Q50="
          + roomArithMeans_Q50 + ", Q95=" + roomArithMeans_Q95 + ", QD="
          + roomArithMeans_QD, 99);
      tmpUpdate("Calculated statistics for cellar arithmetic means:", 99);
      logOnly("AM=" + cellarArithMeans_AM + ", SD=" + cellarArithMeans_SD
          + ", CV=" + cellarArithMeans_CV + ", GM=" + cellarArithMeans_GM
          + ", GSD=" + cellarArithMeans_GSD + ", Q5=" + cellarArithMeans_Q05
          + ", Q50=" + cellarArithMeans_Q50 + ", Q95=" + cellarArithMeans_Q95
          + ", QD=" + cellarArithMeans_QD, 99);
      tmpUpdate("Calculated statistics for room geometric means:", 99);
      logOnly("AM=" + roomLogMeans_AM + ", SD=" + roomLogMeans_SD + ", CV="
          + roomLogMeans_CV + ", GM=" + roomLogMeans_GM + ", GSD="
          + roomLogMeans_GSD + ", Q5=" + roomLogMeans_Q05 + ", Q50="
          + roomLogMeans_Q50 + ", Q95=" + roomLogMeans_Q95 + ", QD="
          + roomLogMeans_QD, 99);
      tmpUpdate("Calculated statistics for cellar geometric means:", 99);
      logOnly("AM=" + cellarLogMeans_AM + ", SD=" + cellarLogMeans_SD + ", CV="
          + cellarLogMeans_CV + ", GM=" + cellarLogMeans_GM + ", GSD="
          + cellarLogMeans_GSD + ", Q5=" + cellarLogMeans_Q05 + ", Q50="
          + cellarLogMeans_Q50 + ", Q95=" + cellarLogMeans_Q95 + ", QD="
          + cellarLogMeans_QD, 99);
      tmpUpdate("Calculated statistics for room medians:", 99);
      logOnly("AM=" + roomMedians_AM + ", SD=" + roomMedians_SD + ", CV="
          + roomMedians_CV + ", GM=" + roomMedians_GM + ", GSD="
          + roomMedians_GSD + ", Q5=" + roomMedians_Q05 + ", Q50="
          + roomMedians_Q50 + ", Q95=" + roomMedians_Q95 + ", QD="
          + roomMedians_QD, 99);
      tmpUpdate("Calculated statistics for cellar medians:", 99);
      logOnly("AM=" + cellarMedians_AM + ", SD=" + cellarMedians_SD + ", CV="
          + cellarMedians_CV + ", GM=" + cellarMedians_GM + ", GSD="
          + cellarMedians_GSD + ", Q5=" + cellarMedians_Q05 + ", Q50="
          + cellarMedians_Q50 + ", Q95=" + cellarMedians_Q95 + ", QD="
          + cellarMedians_QD, 99);
      tmpUpdate("Calculated statistics for room maxima:", 99);
      logOnly("AM=" + roomMaxima_AM + ", SD=" + roomMaxima_SD + ", CV="
          + roomMaxima_CV + ", GM=" + roomMaxima_GM + ", GSD=" + roomMaxima_GSD
          + ", Q5=" + roomMaxima_Q05 + ", Q50=" + roomMaxima_Q50 + ", Q95="
          + roomMaxima_Q95 + ", QD=" + roomMaxima_QD, 99);
      tmpUpdate("Calculated statistics for cellar maxima:", 99);
      logOnly("AM=" + cellarMaxima_AM + ", SD=" + cellarMaxima_SD + ", CV="
          + cellarMaxima_CV + ", GM=" + cellarMaxima_GM + ", Q5="
          + cellarMaxima_Q05 + ", Q50=" + cellarMaxima_Q50 + ", Q95="
          + cellarMaxima_Q95 + ", QD=" + cellarMaxima_QD, 99);
      csvOutput
          .write("\"ID\";\"AM\";\"SD\";\"CV\";\"GM\";\"GSD\";\"Q5\";\"Q50\";\"Q95\";\"QD\"");
      csvOutput.newLine();
      csvOutput.write("\"R_AM\";\"" + decFormat.format(roomArithMeans_AM)
          + "\";\"" + decFormat.format(roomArithMeans_SD) + "\";\""
          + decFormat.format(roomArithMeans_CV) + "\";\""
          + decFormat.format(roomArithMeans_GM) + "\";\""
          + decFormat.format(roomArithMeans_GSD) + "\";\""
          + decFormat.format(roomArithMeans_Q05) + "\";\""
          + decFormat.format(roomArithMeans_Q50) + "\";\""
          + decFormat.format(roomArithMeans_Q95) + "\";\""
          + decFormat.format(roomArithMeans_QD) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"R_GM\";\"" + decFormat.format(roomLogMeans_AM)
          + "\";\"" + decFormat.format(roomLogMeans_SD) + "\";\""
          + decFormat.format(roomLogMeans_CV) + "\";\""
          + decFormat.format(roomLogMeans_GM) + "\";\""
          + decFormat.format(roomLogMeans_GSD) + "\";\""
          + decFormat.format(roomLogMeans_Q05) + "\";\""
          + decFormat.format(roomLogMeans_Q50) + "\";\""
          + decFormat.format(roomLogMeans_Q95) + "\";\""
          + decFormat.format(roomLogMeans_QD) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"R_Q50\";\"" + decFormat.format(roomMedians_AM)
          + "\";\"" + decFormat.format(roomMedians_SD) + "\";\""
          + decFormat.format(roomMedians_CV) + "\";\""
          + decFormat.format(roomMedians_GM) + "\";\""
          + decFormat.format(roomMedians_GSD) + "\";\""
          + decFormat.format(roomMedians_Q05) + "\";\""
          + decFormat.format(roomMedians_Q50) + "\";\""
          + decFormat.format(roomMedians_Q95) + "\";\""
          + decFormat.format(roomMedians_QD) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"R_MAX\";\"" + decFormat.format(roomMaxima_AM)
          + "\";\"" + decFormat.format(roomMaxima_SD) + "\";\""
          + decFormat.format(roomMaxima_CV) + "\";\""
          + decFormat.format(roomMaxima_GM) + "\";\""
          + decFormat.format(roomMaxima_GSD) + "\";\""
          + decFormat.format(roomMaxima_Q05) + "\";\""
          + decFormat.format(roomMaxima_Q50) + "\";\""
          + decFormat.format(roomMaxima_Q95) + "\";\""
          + decFormat.format(roomMaxima_QD) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"C_AM\";\"" + decFormat.format(cellarArithMeans_AM)
          + "\";\"" + decFormat.format(cellarArithMeans_SD) + "\";\""
          + decFormat.format(cellarArithMeans_CV) + "\";\""
          + decFormat.format(cellarArithMeans_GM) + "\";\""
          + decFormat.format(cellarArithMeans_GSD) + "\";\""
          + decFormat.format(cellarArithMeans_Q05) + "\";\""
          + decFormat.format(cellarArithMeans_Q50) + "\";\""
          + decFormat.format(cellarArithMeans_Q95) + "\";\""
          + decFormat.format(cellarArithMeans_QD) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"C_GM\";\"" + decFormat.format(cellarLogMeans_AM)
          + "\";\"" + decFormat.format(cellarLogMeans_SD) + "\";\""
          + decFormat.format(cellarLogMeans_CV) + "\";\""
          + decFormat.format(cellarLogMeans_GM) + "\";\""
          + decFormat.format(cellarLogMeans_GSD) + "\";\""
          + decFormat.format(cellarLogMeans_Q05) + "\";\""
          + decFormat.format(cellarLogMeans_Q50) + "\";\""
          + decFormat.format(cellarLogMeans_Q95) + "\";\""
          + decFormat.format(cellarLogMeans_QD) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"C_Q50\";\"" + decFormat.format(cellarMedians_AM)
          + "\";\"" + decFormat.format(cellarMedians_SD) + "\";\""
          + decFormat.format(cellarMedians_CV) + "\";\""
          + decFormat.format(cellarMedians_GM) + "\";\""
          + decFormat.format(cellarMedians_GSD) + "\";\""
          + decFormat.format(cellarMedians_Q05) + "\";\""
          + decFormat.format(cellarMedians_Q50) + "\";\""
          + decFormat.format(cellarMedians_Q95) + "\";\""
          + decFormat.format(cellarMedians_QD) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"C_MAX\";\"" + decFormat.format(cellarMaxima_AM)
          + "\";\"" + decFormat.format(cellarMaxima_SD) + "\";\""
          + decFormat.format(cellarMaxima_CV) + "\";\""
          + decFormat.format(cellarMaxima_GM) + "\";\""
          + decFormat.format(cellarMaxima_GSD) + "\";\""
          + decFormat.format(cellarMaxima_Q05) + "\";\""
          + decFormat.format(cellarMaxima_Q50) + "\";\""
          + decFormat.format(cellarMaxima_Q95) + "\";\""
          + decFormat.format(cellarMaxima_QD) + "\"");
      csvOutput.newLine();
      csvOutput
          .write("\" \";\" \";\" \";\" \";\" \";\" \";\" \";\" \";\" \";\" \"");
      csvOutput.newLine();


      csvOutput.write("\" \";\" \";\" \";\"N\";\"" + x
          + "\";\" \";\" \";\" \";\" \";\" \"");
    }

    /**
     * Method used to calculate summary statistics which are not stored in
     * memory. Writes results of the calculations to a separate CSV file.
     * 
     * @param roomAmSummaryStats
     *          Statistics for room arithmetric means.
     * @param cellarAmSummaryStats
     *          Statistics for cellar arithmetric means.
     * @param roomGmSummaryStats
     *          Statistics for room geometric means.
     * @param cellarGmSummaryStats
     *          Statistics for cellar geometric means.
     * @param roomMedSummaryStats
     *          Statistics for room medians.
     * @param cellarMedSummaryStats
     *          Statistics for cellar medians.
     * @param roomMaxSummaryStats
     *          Statistics for room maxima.
     * @param cellarMaxSummaryStats
     *          Statistics for cellar maxima.
     * @param csvOutput
     *          A a file buffer writer used to write the results to CSV.
     * @param decFormat
     *          The format used to store the results with comma or dot used as
     *          decimal separator.
     * @throws IOException
     *           If creating log file or writing logs fails.
     */
    private void summaryStatistics(long x,
        SummaryStatistics roomAmSummaryStats,
        SummaryStatistics cellarAmSummaryStats,
        SummaryStatistics roomGmSummaryStats,
        SummaryStatistics cellarGmSummaryStats,
        SummaryStatistics roomMedSummaryStats,
        SummaryStatistics cellarMedSummaryStats,
        SummaryStatistics roomMaxSummaryStats,
        SummaryStatistics cellarMaxSummaryStats, BufferedWriter csvOutput,
        DecimalFormat decFormat) throws IOException {
      double roomArithMeans_AM = roomAmSummaryStats.getMean();
      double roomArithMeans_SD = roomAmSummaryStats.getStandardDeviation();
      double roomArithMeans_CV = OMHelper.calculateCV(roomArithMeans_AM,
          roomArithMeans_SD);
      double roomArithMeans_GM = roomAmSummaryStats.getGeometricMean();
      double cellarArithMeans_AM = cellarAmSummaryStats.getMean();
      double cellarArithMeans_SD = cellarAmSummaryStats.getStandardDeviation();
      double cellarArithMeans_CV = OMHelper.calculateCV(cellarArithMeans_AM,
          cellarArithMeans_SD);
      double cellarArithMeans_GM = cellarAmSummaryStats.getGeometricMean();
      double roomLogMeans_AM = roomGmSummaryStats.getMean();
      double roomLogMeans_SD = roomGmSummaryStats.getStandardDeviation();
      double roomLogMeans_CV = OMHelper.calculateCV(roomLogMeans_AM,
          roomLogMeans_SD);
      double roomLogMeans_GM = roomGmSummaryStats.getGeometricMean();
      double cellarLogMeans_AM = cellarGmSummaryStats.getMean();
      double cellarLogMeans_SD = cellarGmSummaryStats.getStandardDeviation();
      double cellarLogMeans_CV = OMHelper.calculateCV(cellarLogMeans_AM,
          cellarLogMeans_SD);
      double cellarLogMeans_GM = cellarGmSummaryStats.getGeometricMean();
      double roomMedians_AM = roomMedSummaryStats.getMean();
      double roomMedians_SD = roomMedSummaryStats.getStandardDeviation();
      double roomMedians_CV = OMHelper.calculateCV(roomMedians_AM,
          roomMedians_SD);
      double roomMedians_GM = roomMedSummaryStats.getGeometricMean();
      double cellarMedians_AM = cellarMedSummaryStats.getMean();
      double cellarMedians_SD = cellarMedSummaryStats.getStandardDeviation();
      double cellarMedians_CV = OMHelper.calculateCV(cellarMedians_AM,
          cellarMedians_SD);
      double cellarMedians_GM = cellarMedSummaryStats.getGeometricMean();
      double roomMaxima_AM = roomMaxSummaryStats.getMean();
      double roomMaxima_SD = roomMaxSummaryStats.getStandardDeviation();
      double roomMaxima_CV = OMHelper.calculateCV(roomMaxima_AM, roomMaxima_SD);
      double roomMaxima_GM = roomMaxSummaryStats.getGeometricMean();
      double cellarMaxima_AM = cellarMaxSummaryStats.getMean();
      double cellarMaxima_SD = cellarMaxSummaryStats.getStandardDeviation();
      double cellarMaxima_CV = OMHelper.calculateCV(cellarMaxima_AM,
          cellarMaxima_SD);
      double cellarMaxima_GM = cellarMaxSummaryStats.getGeometricMean();
      tmpUpdate("Calculated statistics for room arithmetic means:", 99);
      logOnly("AM=" + roomArithMeans_AM + ", SD=" + roomArithMeans_SD + ", CV="
          + roomArithMeans_CV + ", GM=" + roomArithMeans_GM, 99);
      tmpUpdate("Calculated statistics for cellar arithmetic means:", 99);
      logOnly("AM=" + cellarArithMeans_AM + ", SD=" + cellarArithMeans_SD
          + ", CV=" + cellarArithMeans_CV + ", GM=" + cellarArithMeans_GM, 99);
      tmpUpdate("Calculated statistics for room geometric means:", 99);
      logOnly("AM=" + roomLogMeans_AM + ", SD=" + roomLogMeans_SD + ", CV="
          + roomLogMeans_CV + ", GM=" + roomLogMeans_GM, 99);
      tmpUpdate("Calculated statistics for cellar geometric means:", 99);
      logOnly("AM=" + cellarLogMeans_AM + ", SD=" + cellarLogMeans_SD + ", CV="
          + cellarLogMeans_CV + ", GM=" + cellarLogMeans_GM, 99);
      tmpUpdate("Calculated statistics for room medians:", 99);
      logOnly("AM=" + roomMedians_AM + ", SD=" + roomMedians_SD + ", CV="
          + roomMedians_CV + ", GM=" + roomMedians_GM, 99);
      tmpUpdate("Calculated statistics for cellar medians:", 99);
      logOnly("AM=" + cellarMedians_AM + ", SD=" + cellarMedians_SD + ", CV="
          + cellarMedians_CV + ", GM=" + cellarMedians_GM, 99);
      tmpUpdate("Calculated statistics for room maxima:", 99);
      logOnly("AM=" + roomMaxima_AM + ", SD=" + roomMaxima_SD + ", CV="
          + roomMaxima_CV + ", GM=" + roomMaxima_GM, 99);
      tmpUpdate("Calculated statistics for cellar maxima:", 99);
      logOnly("AM=" + cellarMaxima_AM + ", SD=" + cellarMaxima_SD + ", CV="
          + cellarMaxima_CV + ", GM=" + cellarMaxima_GM, 99);
      csvOutput.write("\"ID\";\"AM\";\"SD\";\"CV\";\"GM\"");
      csvOutput.newLine();
      csvOutput.write("\"R_AM\";\"" + decFormat.format(roomArithMeans_AM)
          + "\";\"" + decFormat.format(roomArithMeans_SD) + "\";\""
          + decFormat.format(roomArithMeans_CV) + "\";\""
          + decFormat.format(roomArithMeans_GM) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"R_GM\";\"" + decFormat.format(roomLogMeans_AM)
          + "\";\"" + decFormat.format(roomLogMeans_SD) + "\";\""
          + decFormat.format(roomLogMeans_CV) + "\";\""
          + decFormat.format(roomLogMeans_GM) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"R_Q50\";\"" + decFormat.format(roomMedians_AM)
          + "\";\"" + decFormat.format(roomMedians_SD) + "\";\""
          + decFormat.format(roomMedians_CV) + "\";\""
          + decFormat.format(roomMedians_GM) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"R_MAX\";\"" + decFormat.format(roomMaxima_AM)
          + "\";\"" + decFormat.format(roomMaxima_SD) + "\";\""
          + decFormat.format(roomMaxima_CV) + "\";\""
          + decFormat.format(roomMaxima_GM) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"C_AM\";\"" + decFormat.format(cellarArithMeans_AM)
          + "\";\"" + decFormat.format(cellarArithMeans_SD) + "\";\""
          + decFormat.format(cellarArithMeans_CV) + "\";\""
          + decFormat.format(cellarArithMeans_GM) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"C_GM\";\"" + decFormat.format(cellarLogMeans_AM)
          + "\";\"" + decFormat.format(cellarLogMeans_SD) + "\";\""
          + decFormat.format(cellarLogMeans_CV) + "\";\""
          + decFormat.format(cellarLogMeans_GM) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"C_Q50\";\"" + decFormat.format(cellarMedians_AM)
          + "\";\"" + decFormat.format(cellarMedians_SD) + "\";\""
          + decFormat.format(cellarMedians_CV) + "\";\""
          + decFormat.format(cellarMedians_GM) + "\"");
      csvOutput.newLine();
      csvOutput.write("\"C_MAX\";\"" + decFormat.format(cellarMaxima_AM)
          + "\";\"" + decFormat.format(cellarMaxima_SD) + "\";\""
          + decFormat.format(cellarMaxima_CV) + "\";\""
          + decFormat.format(cellarMaxima_GM) + "\"");
      csvOutput.newLine();
      csvOutput.write("\" \";\" \";\" \";\" \";\" \"");
      csvOutput.newLine();
      csvOutput.write("\" \";\" \";\" \";\"N\";\"" + x + "\"");
    }

    /**
     * Creates a timestamp indicating how much time is left using the percentage
     * of the overall status and the start time.
     * 
     * @param perc
     *          The percentage of the overall status.
     * @return A timestamp indicating how much time is left.
     */
    private String timeLeft(double perc) {
      String strFormat = "#";
      double timePassed = (System.currentTimeMillis() - getStart()) / 1000;
      double time = (timePassed / perc * 100.0) - timePassed;
      String unit = " seconds.";
      final double days = time / 60.0 / 60.0 / 24.0;
      if (time > 100) {
        strFormat = "#";
        time = time / 60.0;
        unit = " minutes.";
        if (time > 100) {
          strFormat = "#.#";
          time = time / 60.0;
          unit = " hours.";
          if (time > 48) {
            strFormat = "#.##";
            time = time / 24.0;
            unit = " days.";     
            if (time > 50) {
              strFormat = "#.##";
              time = time / 28.0;
              unit = " months.";
              if (time > 20) {
                strFormat = "#.##";
                time = days / 365.2424;
                unit = " years.";
              }
            }
          }
        }
      }
      DecimalFormat decFormat = new DecimalFormat(strFormat);
      return decFormat.format(time) + unit;
    }

    /**
     * Executed in event dispatching thread after finishing the simulation task.
     * Updates the interface and adds current simulation results to results
     * panel if this is a random simulation.
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    public void done() {
      tmpUpdate(getLogMsg(), getStatus());
      comboBoxSelectProject.setEnabled(true);
      progressBarSimulation.setIndeterminate(false);
      progressBarSimulation.setVisible(false);
      btnStart.setEnabled(true);
      setCursor(null);
      try {
        Thread.sleep(500);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
      if (!isSystematic) {
        try {
          JTabbedPane tab = (JTabbedPane) getParent();
          tab.remove(tab.getComponentAt(3));
          JPanel jpanelResults = new OMPanelResults(getOmsFile());
          tab.add(jpanelResults, "Results", 3);
          tab.updateUI();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Initializes the interface of the simulation panel without any preloaded
   * objects.
   */
  public OMPanelSimulation() {
    initialize();
  }

  /**
   * Initializes the interface of the simulation panel with a preloaded object
   * from import panel. Launching a refresh task in background.
   * 
   * @param omb
   *          Absolute path to an OMB object file to load on init.
   */
  public OMPanelSimulation(String omb) {
    initialize();
    txtOmbFile.setText(omb);
    setOmbFile(omb);
    btnRefresh.setEnabled(false);
    comboBoxSelectProject.setEnabled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    progressBarSimulation.setVisible(true);
    progressBarSimulation.setStringPainted(true);
    progressBarSimulation.setIndeterminate(true);
    refreshTask = new Refresh();
    refreshTask.execute();
  }

  /**
   * Initializes the interface of the simulation panel.
   */
  protected void initialize() {
    setLayout(null);

    btnStart = new JButton("Start");
    btnStart.addActionListener(this);
    btnStart.setBounds(616, 326, 124, 23);
    btnStart.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(btnStart);

    progressBarSimulation = new JProgressBar();
    progressBarSimulation.setBounds(10, 475, 730, 23);
    progressBarSimulation.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    progressBarSimulation.setVisible(false);
    add(progressBarSimulation);

    lblSelectOmbfile = new JLabel("Open OMB-File");
    lblSelectOmbfile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblSelectOmbfile.setBounds(10, 36, 132, 14);
    add(lblSelectOmbfile);

    lblOmsFile = new JLabel("Save OMS-File");
    lblOmsFile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblOmsFile.setBounds(174, 272, 120, 14);
    add(lblOmsFile);

    txtOmsFile = new JTextField();
    txtOmsFile.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent arg0) {
        setOmsFile(txtOmsFile.getText());
      }
    });
    txtOmsFile.setBounds(304, 269, 302, 20);
    txtOmsFile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(txtOmsFile);
    txtOmsFile.setColumns(10);

    btnBrowseOms = new JButton("Browse");
    btnBrowseOms.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.oms", "oms"));
        fileDialog.showSaveDialog(getParent());
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
    btnBrowseOms.setBounds(616, 268, 124, 23);
    btnBrowseOms.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(btnBrowseOms);

    lblSelectProject = new JLabel("Select Project");
    lblSelectProject.setBounds(10, 65, 132, 14);
    lblSelectProject.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(lblSelectProject);

    lblSimulationType = new JLabel("Simulation Type");
    lblSimulationType.setBounds(10, 94, 132, 14);
    lblSimulationType.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(lblSimulationType);

    lblCampaigns = new JLabel("campaigns");
    lblCampaigns.setBounds(421, 122, 185, 14);
    lblCampaigns.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(lblCampaigns);

    lblRatio = new JLabel("Ratio");
    lblRatio.setBounds(174, 152, 44, 14);
    lblRatio.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(lblRatio);

    lblPercent = new JLabel("%");
    lblPercent.setBounds(246, 302, 360, 14);
    lblPercent.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(lblPercent);

    spnrRandomCampaigns = new JSpinner();
    spnrRandomCampaigns.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        setRandomCampaigns((int) spnrRandomCampaigns.getValue());
      }
    });
    spnrRandomCampaigns.setModel(new SpinnerNumberModel(10000, 10, 100000, 1));
    spnrRandomCampaigns.setBounds(304, 119, 107, 22);
    spnrRandomCampaigns.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(spnrRandomCampaigns);

    spnrRatio3 = new JSpinner();
    spnrRatio3.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if (chckbxRatio3.isSelected()) {
          setRatio3((int) spnrRatio3.getValue());
        } else {
          setRatio3(0);
          spnrRatio3.setEnabled(false);
        }
      }
    });
    spnrRatio3.setModel(new SpinnerNumberModel(2, 0, 10000, 1));
    spnrRatio3.setBounds(330, 149, 81, 22);
    spnrRatio3.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(spnrRatio3);

    spnrRatio4 = new JSpinner();
    spnrRatio4.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if (chckbxRatio4.isSelected()) {
          setRatio4((int) spnrRatio4.getValue());
        } else {
          setRatio4(0);
          spnrRatio4.setEnabled(false);
        }
      }
    });
    spnrRatio4.setModel(new SpinnerNumberModel(5, 0, 10000, 1));
    spnrRatio4.setBounds(330, 179, 81, 22);
    spnrRatio4.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(spnrRatio4);

    spnrRatio5 = new JSpinner();
    spnrRatio5.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if (chckbxRatio5.isSelected()) {
          setRatio5((int) spnrRatio5.getValue());
        } else {
          setRatio5(0);
          spnrRatio5.setEnabled(false);
        }
      }
    });
    spnrRatio5.setModel(new SpinnerNumberModel(20, 0, 10000, 1));
    spnrRatio5.setBounds(330, 209, 81, 22);
    spnrRatio5.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(spnrRatio5);

    spnrRatio6 = new JSpinner();
    spnrRatio6.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if (chckbxRatio6.isSelected()) {
          setRatio6((int) spnrRatio6.getValue());
        } else {
          setRatio6(0);
          spnrRatio6.setEnabled(false);
        }
      }
    });
    spnrRatio6.setModel(new SpinnerNumberModel(73, 0, 10000, 1));
    spnrRatio6.setBounds(330, 239, 81, 22);
    spnrRatio6.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(spnrRatio6);

    spnrRandomNoise = new JSpinner();
    spnrRandomNoise.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if (chckbxRandomNoise.isSelected()) {
          setRandomNoise((int) spnrRandomNoise.getValue());
        } else {
          setRandomNoise(0);
          spnrRandomNoise.setEnabled(false);
        }
      }
    });
    spnrRandomNoise.setModel(new SpinnerNumberModel(5, 0, 20, 1));
    spnrRandomNoise.setBounds(153, 299, 83, 22);
    spnrRandomNoise.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(spnrRandomNoise);

    chckbxRatio3 = new JCheckBox("3 of 6");
    chckbxRatio3.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (chckbxRatio3.isSelected()) {
          if (rdbtnRandom.isSelected()) {
            spnrRatio3.setEnabled(true);
          }
        } else {
          setRatio3(0);
          spnrRatio3.setEnabled(false);
        }
      }
    });
    chckbxRatio3.setBounds(237, 149, 80, 23);
    chckbxRatio3.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(chckbxRatio3);

    chckbxRatio4 = new JCheckBox("4 of 6");
    chckbxRatio4.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (chckbxRatio4.isSelected()) {
          if (rdbtnRandom.isSelected()) {
            spnrRatio4.setEnabled(true);
          }
        } else {
          setRatio4(0);
          spnrRatio4.setEnabled(false);
        }
      }
    });
    chckbxRatio4.setBounds(237, 179, 80, 23);
    chckbxRatio4.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(chckbxRatio4);

    chckbxRatio5 = new JCheckBox("5 of 6");
    chckbxRatio5.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (chckbxRatio5.isSelected()) {
          if (rdbtnRandom.isSelected()) {
            spnrRatio5.setEnabled(true);
          }
        } else {
          setRatio5(0);
          spnrRatio5.setEnabled(false);
        }
      }
    });
    chckbxRatio5.setBounds(237, 209, 80, 23);
    chckbxRatio5.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(chckbxRatio5);

    chckbxRatio6 = new JCheckBox("6 of 6");
    chckbxRatio6.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (chckbxRatio6.isSelected()) {
          if (rdbtnRandom.isSelected()) {
            spnrRatio6.setEnabled(true);
          }
        } else {
          setRatio6(0);
          spnrRatio6.setEnabled(false);
        }
      }
    });
    chckbxRatio6.setBounds(237, 239, 80, 23);
    chckbxRatio6.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(chckbxRatio6);

    chckbxRandomNoise = new JCheckBox("Random noise");
    chckbxRandomNoise.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (chckbxRandomNoise.isSelected()) {
          if (chckbxRandomNoise.isEnabled()) {
            spnrRandomNoise.setEnabled(true);
          }
        } else {
          setRandomNoise(0);
          spnrRandomNoise.setEnabled(false);
        }
      }
    });
    chckbxRandomNoise.setBounds(10, 299, 137, 23);
    chckbxRandomNoise.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(chckbxRandomNoise);

    btnRefresh = new JButton("Load");
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
            comboBoxSelectProject.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            progressBarSimulation.setVisible(true);
            progressBarSimulation.setStringPainted(true);
            progressBarSimulation.setIndeterminate(true);
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
    btnRefresh.setBounds(616, 61, 124, 23);
    btnRefresh.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(btnRefresh);

    lblHelp = new JLabel(
        "Select an OMB-Object file to run simulations. Limited random simulations can be saved as OMS-Simulation files used for analysis.");
    lblHelp.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblHelp.setForeground(Color.GRAY);
    lblHelp.setBounds(10, 10, 730, 14);
    add(lblHelp);

    txtOmbFile = new JTextField();
    txtOmbFile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    txtOmbFile.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent arg0) {
        setOmbFile(txtOmbFile.getText());
      }
    });
    txtOmbFile.setBounds(152, 33, 454, 20);
    add(txtOmbFile);
    txtOmbFile.setColumns(10);

    btnBrowseOmb = new JButton("Browse");
    btnBrowseOmb.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnBrowseOmb.addActionListener(new ActionListener() {
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
    btnBrowseOmb.setBounds(616, 32, 124, 23);
    add(btnBrowseOmb);

    rdbtnSystematic = new JRadioButton("Systematic all campaigns");
    rdbtnSystematic.setSelected(true);
    rdbtnSystematic.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        if (rdbtnSystematic.isSelected()) {
          setSystematic(true);
          rdbtnRandom.setSelected(false);
          chckbxRatio6.setEnabled(false);
          chckbxRatio5.setEnabled(false);
          chckbxRatio4.setEnabled(false);
          chckbxRatio3.setEnabled(false);
          spnrRatio6.setEnabled(false);
          spnrRatio5.setEnabled(false);
          spnrRatio4.setEnabled(false);
          spnrRatio3.setEnabled(false);
          spnrRandomCampaigns.setEnabled(false);
          lblRatio.setEnabled(false);
          lblCampaigns.setEnabled(false);
          lblOmsFile.setEnabled(false);
          txtOmsFile.setEnabled(false);
          btnBrowseOms.setEnabled(false);
        } else {
          setSystematic(false);
          rdbtnRandom.setSelected(true);
        }
      }
    });
    rdbtnSystematic.setBounds(152, 90, 356, 23);
    rdbtnSystematic.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(rdbtnSystematic);

    rdbtnRandom = new JRadioButton("Random");
    rdbtnRandom.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (rdbtnRandom.isSelected()) {
          setSystematic(false);
          rdbtnSystematic.setSelected(false);
          chckbxRatio6.setEnabled(true);
          chckbxRatio5.setEnabled(true);
          chckbxRatio4.setEnabled(true);
          chckbxRatio3.setEnabled(true);
          spnrRandomCampaigns.setEnabled(true);
          lblRatio.setEnabled(true);
          lblCampaigns.setEnabled(true);
          lblOmsFile.setEnabled(true);
          txtOmsFile.setEnabled(true);
          btnBrowseOms.setEnabled(true);
        } else {
          setSystematic(true);
          rdbtnSystematic.setSelected(true);
        }
      }
    });
    rdbtnRandom.setBounds(152, 119, 142, 23);
    rdbtnRandom.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(rdbtnRandom);

    comboBoxSelectProject = new JComboBox<OMBuilding>();
    comboBoxSelectProject
        .addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent arg0) {
            boolean b = false;
            if (comboBoxSelectProject.isEnabled()) {
              if (comboBoxSelectProject.getSelectedItem() != null) {
                b = true;
                setSelectedObject((OMBuilding) comboBoxSelectProject
                    .getSelectedItem());
                setProjectName(getSelectedObject().getName());
              } else {
                b = false;
              }
            } else {
              b = false;
            }
            progressBarSimulation.setEnabled(b);
            btnStart.setEnabled(b);
            chckbxRandomNoise.setEnabled(b);
            rdbtnRandom.setEnabled(b);
            rdbtnSystematic.setEnabled(b);
            lblPercent.setEnabled(b);
            lblRatio.setEnabled(b);
            lblSimulationType.setEnabled(b);
            lblCampaigns.setEnabled(b);
          }
        });
    comboBoxSelectProject.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        boolean b = false;
        if (comboBoxSelectProject.isEnabled()) {
          if (comboBoxSelectProject.getSelectedItem() != null) {
            b = true;
            setSelectedObject((OMBuilding) comboBoxSelectProject
                .getSelectedItem());
            setProjectName(getSelectedObject().getName());
          } else {
            b = false;
          }
        } else {
          b = false;
        }
        progressBarSimulation.setEnabled(b);
        btnStart.setEnabled(b);
        chckbxRandomNoise.setEnabled(b);
        rdbtnRandom.setEnabled(b);
        rdbtnSystematic.setEnabled(b);
        lblPercent.setEnabled(b);
        lblRatio.setEnabled(b);
        lblSimulationType.setEnabled(b);
        lblCampaigns.setEnabled(b);
      }
    });
    comboBoxSelectProject.setBounds(152, 61, 454, 22);
    comboBoxSelectProject.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    add(comboBoxSelectProject);

    btnStart.setEnabled(false);
    chckbxRandomNoise.setEnabled(false);
    chckbxRatio6.setEnabled(false);
    chckbxRatio5.setEnabled(false);
    chckbxRatio4.setEnabled(false);
    chckbxRatio3.setEnabled(false);
    spnrRandomNoise.setEnabled(false);
    spnrRatio6.setEnabled(false);
    spnrRatio5.setEnabled(false);
    spnrRatio4.setEnabled(false);
    spnrRatio3.setEnabled(false);
    spnrRandomCampaigns.setEnabled(false);
    rdbtnRandom.setEnabled(false);
    rdbtnSystematic.setEnabled(false);
    lblPercent.setEnabled(false);
    lblRatio.setEnabled(false);
    lblSimulationType.setEnabled(false);
    lblCampaigns.setEnabled(false);
    lblOmsFile.setEnabled(false);
    txtOmsFile.setEnabled(false);
    btnBrowseOms.setEnabled(false);
    spnrRatio6.setValue(73);
    spnrRatio5.setValue(20);
    spnrRatio4.setValue(5);
    spnrRatio3.setValue(2);
    spnrRandomNoise.setValue(5);
  }

  /**
   * An action event handler invoked when the user presses the start button.
   * Performs some validation checks on the input fields and starts the
   * simulation task. Displays warnings if malformed input is detected.
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    setSelectedObject((OMBuilding) comboBoxSelectProject.getSelectedItem());
    setProjectName(getSelectedObject().getName());
    if (rdbtnSystematic.isSelected()) {
      setSystematic(true);
      setRandomCampaigns(0);
    } else {
      setSystematic(false);
      setRandomCampaigns((int) spnrRandomCampaigns.getValue());
    }
    if (chckbxRatio3.isSelected()) {
      setRatio3((int) spnrRatio3.getValue());
    } else {
      setRatio3(0);
    }
    if (chckbxRatio4.isSelected()) {
      setRatio4((int) spnrRatio4.getValue());
    } else {
      setRatio4(0);
    }
    if (chckbxRatio5.isSelected()) {
      setRatio5((int) spnrRatio5.getValue());
    } else {
      setRatio5(0);
    }
    if (chckbxRatio6.isSelected()) {
      setRatio6((int) spnrRatio6.getValue());
    } else {
      setRatio6(0);
    }
    if (chckbxRandomNoise.isSelected()) {
      setRandomNoise((int) spnrRandomNoise.getValue());
    } else {
      setRandomNoise(0);
    }

    if (isSystematic) {
      btnStart.setEnabled(false);
      comboBoxSelectProject.setEnabled(false);
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      progressBarSimulation.setStringPainted(true);
      progressBarSimulation.setIndeterminate(false);
      progressBarSimulation.setVisible(true);
      simulationTask = new Simulation();
      simulationTask.execute();
    } else {
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
        if (!omsFile.exists()) {
          txtOmsFile.setBackground(Color.WHITE);
          btnStart.setEnabled(false);
          comboBoxSelectProject.setEnabled(false);
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          progressBarSimulation.setStringPainted(true);
          progressBarSimulation.setIndeterminate(false);
          progressBarSimulation.setVisible(true);
          simulationTask = new Simulation();
          simulationTask.execute();
        } else {
          txtOmsFile.setBackground(new Color(255, 222, 222, 128));
          JOptionPane.showMessageDialog(null,
              "This file already exists. Please select another OMS-file!",
              "Error", JOptionPane.ERROR_MESSAGE);
        }
      } else {
        txtOmsFile.setBackground(new Color(255, 222, 222, 128));
        JOptionPane.showMessageDialog(null, "Please select an OMS-file!",
            "Warning", JOptionPane.WARNING_MESSAGE);
      }
    }
  }
}
