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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.toedter.calendar.JDateChooser;

import de.bfs.radon.omsimulation.data.OMBuilding;
import de.bfs.radon.omsimulation.data.OMHelper;
import de.bfs.radon.omsimulation.data.OMRoom;
import de.bfs.radon.omsimulation.data.OMRoomType;

/**
 * Creates and shows the data import panel for this software tool. Allows the
 * user to convert prepared CSV files to OMB object files used for simulations
 * later on.
 * 
 * @author A. Schoedon
 * 
 */
public class OMPanelImport extends JPanel implements ActionListener {

  /**
   * Unique serial version ID.
   */
  private static final long serialVersionUID = -1277182634219975890L;

  /**
   * Stores a custom name for the object which is set by the user creating the
   * building.
   */
  private String            projectName;

  /**
   * Stores the absolute path to the CSV file which will be imported.
   */
  private String            csvFile;

  /**
   * Stores the absolute path to the OMB object which will be used for
   * simulations later.
   */
  private String            ombFile;

  /**
   * Stores the imported building object which will be stored to file after
   * import.
   */
  private OMBuilding        ombObject;

  /**
   * Stores a custom start date which is set by the user creating the building.
   * The date can be chosen either to identify the start date of the
   * measurements or to identify the start date of the simulations. That's up to
   * the user and does not affect the simulations.
   */
  private Date              projectDate;

  /**
   * Stores the detection limit of the used instruments. If values occur which
   * are lower than the specified limit, half of the limit will be set as radon
   * concentration.
   */
  private int               detectionLimit;

  /**
   * Stores the status of the import process. Used to update the progress bar.
   */
  private int               status;

  /**
   * Stores the current log message which will be both written to the log file
   * and displayed at the progress bar.
   */
  private String            logMsg;

  /**
   * Stores the total number of measurements for the building.
   */
  private int               valueCount;

  /**
   * Stores the total number of rooms for the building.
   */
  private int               roomCount;

  /**
   * UI: Label "Project Name"
   */
  private JLabel            lblProjectName;

  /**
   * UI: Label "Project Date"
   */
  private JLabel            lblProjectDate;

  /**
   * UI: Label "Read CSV-File"
   */
  private JLabel            lblCsvFile;

  /**
   * UI: Label "Detection Limit"
   */
  private JLabel            lblDetectionLimit;

  /**
   * UI: Label "Bq/m\0x00B3"
   */
  private JLabel            lblBqm;

  /**
   * UI: Label for first orientation, content: "Import CSV-Files and convert
   * them to OMB-Project-Files which can be used by this tool to run
   * simulations."
   */
  private JLabel            lblHelp;

  /**
   * UI: Label "Save OMB-File"
   */
  private JLabel            lblSaveOmbfile;

  /**
   * UI: Text field to enter the project name.
   */
  private JTextField        txtProjectName;

  /**
   * UI: Text field to enter the absolute path to the CSV file.
   */
  private JTextField        txtCsvFile;

  /**
   * UI: Text field to enter the absolute path to the OMB object file.
   */
  private JTextField        txtOmbFile;

  /**
   * UI: Button to open a file browser to select a CSV file.
   */
  private JButton           btnBrowseCsv;

  /**
   * UI: Button to start the import process.
   */
  private JButton           btnImport;

  /**
   * UI: Button to open a file browser to save an OMB file.
   */
  private JButton           btnBrowseOmb;

  /**
   * UI: Spinner for integer values to set the detection limit of the used
   * instruments.
   */
  private JSpinner          spnrDetectionLimit;

  /**
   * UI: Checkbox to select the current date as project date.
   */
  private JCheckBox         chckbxToday;

  /**
   * UI: Progress bar to display status of the import process.
   */
  private JProgressBar      progressBarImport;

  /**
   * UI: Date chooser to select the project date.
   */
  private JDateChooser      projectDateChooser;

  /**
   * Stores the import process task which will be executed in a separate thread
   * to ensure the UI wont freeze.
   */
  private ImportTask        importTask;

  /**
   * Gets the total number of measurements for the building. The unit is [h].
   * 
   * @return The total number of measurements for the building.
   */
  private int getValueCount() {
    return this.valueCount;
  }

  /**
   * Sets the total number of measurements for the building. The unit is [h].
   * 
   * @param count
   *          The total number of measurements for the building.
   */
  private void setValueCount(int count) {
    this.valueCount = count;
  }

  /**
   * Gets the total number of rooms for the building.
   * 
   * @return The total number of rooms for the building.
   */
  private int getRoomCount() {
    return this.roomCount;
  }

  /**
   * Sets the total number of rooms for the building.
   * 
   * @param roomCount
   *          The total number of rooms for the building.
   */
  private void setRoomCount(int roomCount) {
    this.roomCount = roomCount;
  }

  /**
   * Gets the status of the import process. Used to update the progress bar.
   * 
   * @return The status of the import process.
   */
  public int getStatus() {
    return this.status;
  }

  /**
   * Sets the status of the import process. Used to update the progress bar.
   * 
   * @param status
   *          The status of the import process.
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
  private void setLogMsg(String logMsg) {
    this.logMsg = logMsg;
  }

  /**
   * Gets a custom name for the object which is set by the user creating the
   * building.
   * 
   * @return A custom name for the object.
   */
  public String getProjectName() {
    return this.projectName;
  }

  /**
   * Sets a custom name for the object which is set by the user creating the
   * building.
   * 
   * @param projectName
   *          A custom name for the object.
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * Gets the absolute path to the CSV file which will be imported.
   * 
   * @return The absolute path to the CSV file.
   */
  public String getCsvFile() {
    return this.csvFile;
  }

  /**
   * Sets the absolute path to the CSV file which will be imported.
   * 
   * @param csvFile
   *          The absolute path to the CSV file.
   */
  public void setCsvFile(String csvFile) {
    this.csvFile = csvFile;
  }

  /**
   * Gets the absolute path to the OMB object which will be used for simulations
   * later.
   * 
   * @return The absolute path to the OMB object.
   */
  public String getOmbFile() {
    return this.ombFile;
  }

  /**
   * Sets the absolute path to the OMB object which will be used for simulations
   * later.
   * 
   * @param ombFile
   *          The absolute path to the OMB object.
   */
  public void setOmbFile(String ombFile) {
    this.ombFile = ombFile;
  }

  /**
   * Gets the imported building object which will be stored to file after
   * import.
   * 
   * @return The imported building object.
   */
  public OMBuilding getOmbObject() {
    return this.ombObject;
  }

  /**
   * Sets the imported building object which will be stored to file after
   * import.
   * 
   * @param ombObject
   *          The imported building object.
   */
  public void setOmbObject(OMBuilding ombObject) {
    this.ombObject = ombObject;
  }

  /**
   * Gets a custom start date which is set by the user creating the building.
   * The date can be chosen either to identify the start date of the
   * measurements or to identify the start date of the simulations. That's up to
   * the user and does not affect the simulations.
   * 
   * @return A custom start date which is set by the user creating the building.
   */
  public Date getProjectDate() {
    return this.projectDate;
  }

  /**
   * Sets a custom start date. The date can be chosen either to identify the
   * start date of the measurements or to identify the start date of the
   * simulations. That's up to the user and does not affect the simulations.
   * 
   * @param projectDate
   *          A custom start date.
   */
  public void setProjectDate(Date projectDate) {
    this.projectDate = projectDate;
  }

  /**
   * Gets the detection limit of the used instruments. If values occur which are
   * lower than the specified limit, half of the limit will be set as radon
   * concentration.
   * 
   * @return The detection limit of the used instruments.
   */
  public int getDetectionLimit() {
    return this.detectionLimit;
  }

  /**
   * Sets the detection limit of the used instruments. If values occur which are
   * lower than the specified limit, half of the limit will be set as radon
   * concentration.
   * 
   * @param detectionLimit
   *          The detection limit of the used instruments.
   */
  public void setDetectionLimit(int detectionLimit) {
    this.detectionLimit = detectionLimit;
  }

  /**
   * The inner class ImportTask used to create the import process task which
   * will be executed in a separate thread to ensure the UI wont freeze.
   * 
   * @author A. Schoedon
   */
  class ImportTask extends SwingWorker<Void, Void> {

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
      progressBarImport.setString(s);
      progressBarImport.setValue(i);
      try {
        if (OMHelper.isLogOutputEnabled()) {
          OMHelper.writeLog(getLogMsg());
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
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
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }

    /**
     * Starts the main import task which is executed in background thread.
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    public Void doInBackground() {
      try {
        int status = 0;
        double start = System.currentTimeMillis();
        ObjectContainer db4o = Db4oEmbedded.openFile(
            Db4oEmbedded.newConfiguration(), getOmbFile());
        OMHelper.setLogOutput(getOmbFile(), "import");
        tmpUpdate("Starting ...", 1);
        tmpUpdate("Importing new building from CSV.", 1);
        String csvFile = getCsvFile();
        String name = getProjectName();
        Date date = getProjectDate();
        double detectionLimit = (double) getDetectionLimit();
        OMBuilding building = createBuilding(csvFile, name, date,
            detectionLimit);
        setOmbObject(building);
        if (building.getRoomCount() >= 4) {
          progressBarImport.setIndeterminate(true);
          db4o.store(building);
          progressBarImport.setIndeterminate(false);
          status = 100;
          tmpUpdate("Stored new building '" + building + "' to file '"
              + getOmbFile() + "'.", status);
        } else {
          status = 0;
          tmpUpdate("Error: Not enough rooms. No building stored.", status);
        }
        db4o.close();
        String strFormat = "#.##";
        DecimalFormatSymbols decSymbols = new DecimalFormatSymbols();
        decSymbols.setDecimalSeparator('.');
        DecimalFormat decFormat = new DecimalFormat(strFormat, decSymbols);
        double total = (System.currentTimeMillis() - start) / 1000;
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
          tmpUpdate("Import finished after " + decFormat.format(total) + unit,
              status);
          JOptionPane.showMessageDialog(null, "Import finished after "
              + decFormat.format(total) + unit, "Success",
              JOptionPane.INFORMATION_MESSAGE);
        } else {
          tmpUpdate("Import failed. See log for details.", status);
          JOptionPane.showMessageDialog(null,
              "Import failed. See log for details.", "Failed",
              JOptionPane.ERROR_MESSAGE);
        }
        OMHelper.closeLog();
      } catch (IOException ioe) {
        tmpUpdate("Error: " + ioe.getMessage(), 0);
        tmpUpdate("Error: Completely failed.", 0);
        JOptionPane.showMessageDialog(null,
            "Completely failed.\n" + ioe.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
        ioe.printStackTrace();
      }
      return null;
    }

    /**
     * Method to generate a building object from the CSV file. This method
     * triggers parsing the CSV file, parsing the rooms, separating rooms and
     * finally creates a building.
     * 
     * @param csvFile
     *          A string containing the file name of the CSV. (And path if
     *          needed.)
     * @param name
     *          A custom name for the object which can be set by the user
     *          creating the building.
     * @param date
     *          A custom start date which can be set by the user creating the
     *          building. The date can be chosen either to identify the start
     *          date of the measurements or to identify the start date of the
     *          simulations. That's up to the user and does not affect the
     *          simulations.
     * @param detectionLimit
     *          The detection limit is used to set empty or '0'-values as radon
     *          concentrations of 0 Bq/m\0x00B3 are most likely below detection
     *          limit.
     * @return An building object which can be used to generate campaigns.
     * @throws IOException
     *           If creating log file or writing logs fails.
     */
    private OMBuilding createBuilding(String csvFile, String name, Date date,
        double detectionLimit) throws IOException {
      String[] csv;
      try {
        csv = parseCSV(csvFile);
      } catch (Exception e) {
        csv = new String[0];
        tmpUpdate("Error: " + e.getMessage(), 0);
        tmpUpdate("Error: Failed to read CSV-File.", 0);
        e.printStackTrace();
      }
      OMRoom[] rooms = parseRooms(csv, detectionLimit);
      OMBuilding current = separateRooms(name, date, rooms);
      return current;
    }

    /**
     * Method for reading and parsing an CSV-file line by line.
     * 
     * @param fileName
     *          The name (and path) of the CSV-file to read.
     * @return An array of strings, each string representing one line.
     * @throws IOException
     *           If creating log file or writing logs fails.
     */
    private String[] parseCSV(String fileName) throws IOException {
      String[] tmpArray = new String[65536];
      String[] csv = null;
      tmpUpdate("Trying to read CSV-File '" + fileName + "'.", 2);
      try {
        FileInputStream fileInput = new FileInputStream(fileName);
        DataInputStream dataInput = new DataInputStream(fileInput);
        InputStreamReader inputReader = new InputStreamReader(dataInput);
        BufferedReader buffReader = new BufferedReader(inputReader);
        tmpUpdate("Read CSV-File with success, trying to "
            + "parse line by line.", 2);
        int valueCount = 0;
        while ((tmpArray[valueCount] = buffReader.readLine()) != null) {
          valueCount++;
        }
        valueCount--;
        if (valueCount >= 168) {
          if (valueCount <= 1008) {
            csv = new String[valueCount + 1];
            for (int x = 0; x <= valueCount; x++) {
              csv[x] = tmpArray[x];
              logOnly(csv[x], 2);
            }
            setValueCount(valueCount);
            tmpUpdate("Parsed " + valueCount + " lines with success.", 2);
          } else {
            csv = new String[0];
            tmpUpdate("Error: " + valueCount
                + " are too many data sets. Aborting.", 0);
            tmpUpdate(
                "Error: Make sure you have at maximum six weeks of records (<= 1008).",
                0);
          }
        } else {
          csv = new String[0];
          tmpUpdate("Error: " + valueCount
              + " are not enough data sets. Aborting.", 0);
          tmpUpdate(
              "Error: Make sure you have at least one week of records (>= 168).",
              0);
        }
        buffReader.close();
        inputReader.close();
        dataInput.close();
        fileInput.close();
      } catch (Exception e) {
        csv = new String[0];
        tmpUpdate("Error: " + e.getMessage(), 0);
        tmpUpdate("Error: Failed to read CSV-File" + fileName + ".", 0);
        e.printStackTrace();
      }
      tmpArray = null;
      return csv;
    }

    /**
     * Method for parsing rooms from the CSV-file by extracting each unique room
     * ID and any related radon values for each room.
     * 
     * @param csv
     *          An array of strings, each string representing one line of the
     *          previously parsed CSV-file.
     * @param detectionLimit
     *          The detection limit is used to set empty or '0'-values as radon
     *          concentrations of 0 Bq/m\0x00B3 are most likely below detection
     *          limit.
     * @return An array consisting of all rooms of the building.
     * @throws IOException
     *           If creating log file or writing logs fails.
     */
    private OMRoom[] parseRooms(String[] csv, double detectionLimit)
        throws IOException {
      tmpUpdate("Trying to parse rooms.", 3);
      OMRoom[] rooms;
      try {
        if (csv.length > 1) {
          String header = csv[0];
          String[] roomId = header.split("\\;");
          int roomCount = roomId.length;
          for (int i = 1; i < roomCount; i++) {
            tmpUpdate("Found room: " + roomId[i], 3);
            if (roomId[i].isEmpty()) {
              tmpUpdate(
                  "Warning: malformed room identifier. Using \"m0\" for misc.",
                  3);
              roomId[i] = "m0";
            }
          }
          roomCount--;
          setRoomCount(roomCount);
          tmpUpdate("Parsed " + roomCount + " rooms with success.", 3);
          rooms = new OMRoom[roomCount];
          int valueCount = getValueCount();
          double[] values[] = new double[roomCount][valueCount];
          tmpUpdate("Trying to collect values for each room.", 3);
          String[] tmpValues = roomId;
          int tmpLength = tmpValues.length;
          boolean success = true;
          for (int i = 1; i < csv.length; i++) {
            tmpValues = csv[i].split("\\;");
            boolean isFirstDigit = Character.isDigit(csv[i].charAt(0));
            boolean isLastDigit = Character.isDigit(csv[i].charAt(csv[i]
                .length() - 1));
            if (isFirstDigit && isLastDigit) {
              if (tmpValues.length == tmpLength) {
                for (int j = 1; j < tmpValues.length; j++) {
                  int x = j - 1;
                  int y = i - 1;
                  if (tmpValues[j].isEmpty()) {
                    tmpUpdate("Warning: Empty string. Using value 0.", 4);
                    values[x][y] = 0.0;
                  } else {
                    values[x][y] = (double) Integer.parseInt(tmpValues[j]);
                  }
                  if (values[x][y] < detectionLimit) {
                    tmpUpdate("Warning: Value " + i + " for room " + roomId[j]
                        + ": '" + tmpValues[j]
                        + "' below detection limit. Using half of the limit ("
                        + (detectionLimit / 2.0) + ").", 4);
                    values[x][y] = detectionLimit / 2.0;
                  }
                  logOnly("Parsed value " + i + " for room " + roomId[j] + ": "
                      + values[x][y], 4);
                }
                tmpLength = tmpValues.length;
              } else {
                tmpUpdate("Error: Malformed CSV-file. Aborting.", 0);
                i = csv.length + 1;
                success = false;
              }
            } else {
              tmpUpdate("Error: Malformed CSV-line: " + csv[i], 0);
              i = csv.length + 1;
              success = false;
            }
          }
          if (success) {
            for (int k = 1; k <= roomCount; k++) {
              int z = k - 1;
              rooms[z] = new OMRoom(roomId[k], values[z]);
            }
            tmpUpdate("Successfully collected values for each room.", 5);
          } else {
            rooms = new OMRoom[0];
            tmpUpdate("Error: Check your CSV-File.", 0);
          }
        } else {
          rooms = new OMRoom[0];
          tmpUpdate("Error: No data records found.", 0);
          tmpUpdate("Error: Check your CSV-File.", 0);
        }
      } catch (Exception e) {
        rooms = new OMRoom[0];
        tmpUpdate("Error: " + e.getMessage(), 0);
        tmpUpdate("Error: Failed to parse rooms.", 0);
        e.printStackTrace();
      }
      return rooms;
    }

    /**
     * Method to separate an array of various rooms by their different types and
     * to create an OMBuilding object out of this rooms.
     * 
     * @param name
     *          A custom name for the object which can be set by the user
     *          creating the building.
     * @param date
     *          A custom start date which can be set by the user creating the
     *          building. The date can be chosen either to identify the start
     *          date of the measurements or to identify the start date of the
     *          simulations. That's up to the user and does not affect the
     *          simulations.
     * @param rooms
     *          An building consisting of all the rooms.
     * @return An building object.
     * @throws IOException
     *           If creating log file or writing logs fails.
     */
    private OMBuilding separateRooms(String name, Date date, OMRoom[] rooms)
        throws IOException {
      tmpUpdate("Trying to separate rooms.", 6);
      int cellarCount = 0;
      int normalCount = 0;
      int miscCount = 0;
      int roomCount = getRoomCount();
      OMBuilding building = new OMBuilding();
      if (rooms.length > 0) {
        for (int i = 0; i < roomCount; i++) {
          if (rooms[i].getType() == OMRoomType.Cellar) {
            cellarCount++;
          } else {
            if (rooms[i].getType() == OMRoomType.Room) {
              normalCount++;
            } else {
              miscCount++;
            }
          }
        }
        OMRoom[] cellars = new OMRoom[cellarCount];
        OMRoom[] normals = new OMRoom[normalCount];
        OMRoom[] miscs = new OMRoom[miscCount];
        tmpUpdate("Found " + normalCount + " normal rooms, " + cellarCount
            + " cellars and " + miscCount + " miscellaneous rooms.", 6);
        if (normalCount >= 3 && cellarCount >= 1) {
          if (normalCount <= 8 && cellarCount <= 4) {
            if (roomCount == cellarCount + normalCount + miscCount) {
              tmpUpdate("Separation test succeeded, separating rooms.", 6);
              cellarCount = 0;
              normalCount = 0;
              miscCount = 0;
              for (int j = 0; j < roomCount; j++) {
                if (rooms[j].getType() == OMRoomType.Cellar) {
                  cellars[cellarCount] = rooms[j];
                  cellarCount++;
                } else {
                  if (rooms[j].getType() == OMRoomType.Room) {
                    normals[normalCount] = rooms[j];
                    normalCount++;
                  } else {
                    miscs[miscCount] = rooms[j];
                    miscCount++;
                  }
                }
              }
              tmpUpdate(
                  "Setting up rooms by types. This may take a few minutes.", 7);
              progressBarImport.setIndeterminate(true);
              int valueCount = getValueCount();
              building = new OMBuilding(name, date, roomCount, valueCount,
                  normals, cellars, miscs);
              progressBarImport.setIndeterminate(false);
              tmpUpdate("Done. Finished setting up rooms.", 68);
            } else {
              tmpUpdate("Error: Separation test failed. Malformed input.", 0);
            }
          } else {
            tmpUpdate(
                "Error: Separation test failed. Too many rooms or cellars.", 0);
            tmpUpdate(
                "Error: Make sure you don't have more than 8 rooms and 4 cellars.",
                0);
          }
        } else {
          tmpUpdate(
              "Error: Separation test failed. Not enough rooms or cellars.", 0);
          tmpUpdate("Error: Make sure you have at least 3 rooms and 1 cellar.",
              0);
        }
      } else {
        tmpUpdate(
            "Error: Separation test failed. Not enough rooms or cellars.", 0);
      }
      return building;
    }

    /**
     * Executed in event dispatching thread after finishing the import task.
     * Updates the interface and adds current object to data, simulation and
     * testing panel.
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    public void done() {
      tmpUpdate(getLogMsg(), getStatus());
      progressBarImport.setVisible(false);
      btnImport.setEnabled(true);
      setCursor(null);
      try {
        JTabbedPane tab = (JTabbedPane) getParent();
        tab.remove(tab.getComponentAt(1));
        JPanel jpanelData = new OMPanelData(getOmbFile(), getOmbObject());
        tab.add(jpanelData, "Data", 1);
        tab.updateUI();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
      try {
        JTabbedPane tab = (JTabbedPane) getParent();
        tab.remove(tab.getComponentAt(2));
        JPanel jpanelSimulation = new OMPanelSimulation(getOmbFile(),
            getOmbObject());
        tab.add(jpanelSimulation, "Simulation", 2);
        tab.updateUI();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
      try {
        JTabbedPane tab = (JTabbedPane) getParent();
        tab.remove(tab.getComponentAt(4));
        JPanel jpanelTesting = new OMPanelTesting(getOmbFile(), getOmbObject());
        tab.add(jpanelTesting, "Analyse", 4);
        tab.updateUI();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Initialises the interface of the import panel.
   */
  public OMPanelImport() {
    setLayout(null);

    lblHelp = new JLabel(
        "Import CSV-Files and convert them to OMB-Project-Files which can be used by this tool to run simulations.");
    lblHelp.setForeground(Color.GRAY);
    lblHelp.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblHelp.setBounds(10, 10, 730, 14);
    add(lblHelp);

    lblProjectName = new JLabel("Project Name");
    lblProjectName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblProjectName.setBounds(10, 36, 132, 14);
    add(lblProjectName);

    lblProjectDate = new JLabel("Project Date");
    lblProjectDate.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblProjectDate.setBounds(10, 65, 132, 14);
    add(lblProjectDate);

    lblCsvFile = new JLabel("Read CSV-File");
    lblCsvFile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblCsvFile.setBounds(10, 123, 132, 14);
    add(lblCsvFile);

    lblDetectionLimit = new JLabel("Detection Limit");
    lblDetectionLimit.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblDetectionLimit.setBounds(10, 94, 132, 14);
    add(lblDetectionLimit);

    lblBqm = new JLabel("Bq/m\u00B3");
    lblBqm.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblBqm.setBounds(616, 94, 124, 14);
    add(lblBqm);

    txtProjectName = new JTextField();
    txtProjectName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    txtProjectName.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent arg0) {
        setProjectName(txtProjectName.getText());
      }
    });

    lblSaveOmbfile = new JLabel("Save OMB-File");
    lblSaveOmbfile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblSaveOmbfile.setBounds(10, 152, 132, 14);
    add(lblSaveOmbfile);
    txtProjectName.setBounds(152, 33, 454, 20);
    add(txtProjectName);
    txtProjectName.setColumns(10);

    txtCsvFile = new JTextField();
    txtCsvFile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    txtCsvFile.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        setCsvFile(txtCsvFile.getText());
      }
    });
    txtCsvFile.setBounds(152, 120, 454, 20);
    add(txtCsvFile);
    txtCsvFile.setColumns(10);

    txtOmbFile = new JTextField();
    txtOmbFile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    txtOmbFile.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent arg0) {
        setOmbFile(txtOmbFile.getText());
      }
    });
    txtOmbFile.setBounds(152, 148, 454, 20);
    add(txtOmbFile);
    txtOmbFile.setColumns(10);

    projectDateChooser = new JDateChooser();
    projectDateChooser.setBounds(152, 61, 454, 22);
    projectDateChooser.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    projectDateChooser.setDate(new Date());
    add(projectDateChooser);

    spnrDetectionLimit = new JSpinner();
    spnrDetectionLimit.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {

        setDetectionLimit((Integer) spnrDetectionLimit.getValue());
      }
    });
    spnrDetectionLimit.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    spnrDetectionLimit.setModel(new SpinnerNumberModel(20, 1, 100, 1));
    spnrDetectionLimit.setBounds(152, 90, 454, 22);
    add(spnrDetectionLimit);

    chckbxToday = new JCheckBox("Today");
    chckbxToday.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    chckbxToday.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        if (chckbxToday.isSelected()) {
          projectDateChooser.setEnabled(false);
          setProjectDate(new Date());
        } else {
          projectDateChooser.setEnabled(true);
          setProjectDate(projectDateChooser.getDate());
        }
      }
    });
    chckbxToday.setBounds(616, 61, 124, 23);
    add(chckbxToday);

    progressBarImport = new JProgressBar();
    progressBarImport.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    progressBarImport.setBounds(10, 475, 730, 23);
    progressBarImport.setVisible(false);
    add(progressBarImport);

    btnBrowseCsv = new JButton("Browse");
    btnBrowseCsv.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnBrowseCsv.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
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
          txtCsvFile.setText(file.getAbsolutePath() + csv);
          setCsvFile(file.getAbsolutePath() + csv);
        }
      }
    });
    btnBrowseCsv.setBounds(616, 119, 124, 23);
    add(btnBrowseCsv);

    btnImport = new JButton("Import");
    btnImport.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnImport.setBounds(616, 177, 124, 23);
    btnImport.addActionListener(this);

    btnBrowseOmb = new JButton("Browse");
    btnBrowseOmb.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnBrowseOmb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.omb", "omb"));
        fileDialog.showSaveDialog(getParent());
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
    btnBrowseOmb.setBounds(616, 148, 124, 23);
    add(btnBrowseOmb);
    add(btnImport);
  }

  /**
   * An action event handler invoked when the user presses the start button.
   * Performs some validation checks on the input fields and starts the import
   * task. Displays warnings if malformed input is detected.
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
    if (txtProjectName.getText() != null
        && !txtProjectName.getText().equals("")
        && !txtProjectName.getText().equals(" ")) {
      txtProjectName.setBackground(Color.WHITE);
      if (txtCsvFile.getText() != null && !txtCsvFile.getText().equals("")
          && !txtCsvFile.getText().equals(" ")) {
        txtCsvFile.setBackground(Color.WHITE);
        if (txtOmbFile.getText() != null && !txtOmbFile.getText().equals("")
            && !txtOmbFile.getText().equals(" ")) {
          txtOmbFile.setBackground(Color.WHITE);
          String csvPath = txtCsvFile.getText();
          File csvFile = new File(csvPath);
          if (csvFile.exists()) {
            txtCsvFile.setBackground(Color.WHITE);
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
            if (!ombFile.exists()) {
              txtOmbFile.setBackground(Color.WHITE);
              btnImport.setEnabled(false);
              if (chckbxToday.isSelected()) {
                projectDateChooser.setEnabled(false);
                setProjectDate(new Date());
              } else {
                projectDateChooser.setEnabled(true);
                setProjectDate(projectDateChooser.getDate());
              }
              setProjectName(txtProjectName.getText());
              setCsvFile(txtCsvFile.getText());
              setDetectionLimit((Integer) spnrDetectionLimit.getValue());
              progressBarImport.setVisible(true);
              progressBarImport.setStringPainted(true);
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              importTask = new ImportTask();
              importTask.execute();
            } else {
              txtOmbFile.setBackground(new Color(255, 222, 222, 128));
              JOptionPane.showMessageDialog(null,
                  "This file already exists. Please select another OMB-file!",
                  "Error", JOptionPane.ERROR_MESSAGE);
            }
          } else {
            txtCsvFile.setBackground(new Color(255, 222, 222, 128));
            JOptionPane.showMessageDialog(null,
                "CSV-file not found, please check the file path!", "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        } else {
          txtOmbFile.setBackground(new Color(255, 222, 222, 128));
          JOptionPane.showMessageDialog(null, "Please select an OMB-file!",
              "Warning", JOptionPane.WARNING_MESSAGE);
        }
      } else {
        txtCsvFile.setBackground(new Color(255, 222, 222, 128));
        JOptionPane.showMessageDialog(null, "Please select a CSV-file!",
            "Warning", JOptionPane.WARNING_MESSAGE);
      }
    } else {
      txtProjectName.setBackground(new Color(255, 222, 222, 128));
      JOptionPane.showMessageDialog(null, "Please enter a project name!",
          "Warning", JOptionPane.WARNING_MESSAGE);
    }
  }
}
