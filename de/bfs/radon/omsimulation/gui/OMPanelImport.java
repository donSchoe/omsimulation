package de.bfs.radon.omsimulation.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
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
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

import de.bfs.radon.omsimulation.data.OMBuilding;
import de.bfs.radon.omsimulation.data.OMHelper;
import de.bfs.radon.omsimulation.data.OMRoom;
import de.bfs.radon.omsimulation.data.OMRoomType;

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

public class OMPanelImport extends JPanel implements ActionListener,
    PropertyChangeListener {

  private static final long serialVersionUID = -1277182634219975890L;
  private JTextField txtProjectName;
  private JTextField txtCsvFile;
  private JSpinner spnrProjectDate;
  private JSpinner spnrDetectionLimit;
  private String ProjectName;
  private String CsvFile;
  private String OmbFile;
  private Date ProjectDate;
  private int DetectionLimit;
  private JCheckBox chckbxToday;
  private JLabel lblProjectName;
  private JLabel lblProjectDate;
  private JLabel lblCsvFile;
  private JLabel lblDetectionLimit;
  private JLabel lblBqm;
  private JProgressBar progressBarImport;
  private JButton btnBrowseCsv;
  private JButton btnImport;
  private JLabel lblHelp;
  private JLabel lblSaveOmbfile;
  private JButton btnBrowseOmb;
  private ImportTask importTask;
  private int status;
  private String logMsg;
  private int ValueCount;
  private int RoomCount;
  private JTextField txtOmbFile;

  /**
   * Gets the total number of measurements for the building. The unit is [h].
   * 
   * @return The total number of measurements for the building.
   */
  private int getValueCount() {
    return ValueCount;
  }

  /**
   * Sets the total number of measurements for the building. The unit is [h].
   * 
   * @param count
   *          The total number of measurements for the building.
   */
  private void setValueCount(int count) {
    ValueCount = count;
  }

  /**
   * Gets the total number of rooms for the building.
   * 
   * @return The total number of rooms for the building.
   */
  private int getRoomCount() {
    return RoomCount;
  }

  /**
   * Sets the total number of rooms for the building.
   * 
   * @param roomCount
   *          The total number of rooms for the building.
   */
  private void setRoomCount(int roomCount) {
    RoomCount = roomCount;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getLogMsg() {
    return logMsg;
  }

  private void setLogMsg(String logMsg) {
    this.logMsg = logMsg;
  }

  public String getProjectName() {
    return ProjectName;
  }

  public void setProjectName(String projectName) {
    ProjectName = projectName;
  }

  public String getCsvFile() {
    return CsvFile;
  }

  public void setCsvFile(String csvFile) {
    CsvFile = csvFile;
  }

  public String getOmbFile() {
    return OmbFile;
  }

  public void setOmbFile(String ombFile) {
    OmbFile = ombFile;
  }

  public Date getProjectDate() {
    return ProjectDate;
  }

  public void setProjectDate(Date projectDate) {
    ProjectDate = projectDate;
  }

  public int getDetectionLimit() {
    return DetectionLimit;
  }

  public void setDetectionLimit(int detectionLimit) {
    DetectionLimit = detectionLimit;
  }

  public void updateStatus() {
    progressBarImport.setString(getLogMsg());
    progressBarImport.setValue(getStatus());
  }

  class ImportTask extends SwingWorker<Void, Void> {

    private void tmpUpdate(String s, int i) {
      setLogMsg(s);
      setStatus(i);
      progressBarImport.setString(s);
      progressBarImport.setValue(i);
      try {
        if (OMHelper.isLogOutputEnabled()) {
          OMHelper.writeLog(getLogMsg());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignore) {
      }
    }

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

    /*
     * Main task. Executed in background thread.
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
        if (building.getRoomCount() >= 4) {
          progressBarImport.setIndeterminate(true);
          db4o.store(building);
          progressBarImport.setIndeterminate(false);
          status = 100;
          tmpUpdate("Stored new building to database: " + building, status);
        } else {
          status = 0;
          tmpUpdate("Error: Not enough rooms. No building stored to database.",
              status);
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
        }
        if (total > 100) {
          total = total / 60.0;
          unit = " hours.";
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
      } catch (IOException e) {
        tmpUpdate("Error: " + e.getMessage(), 0);
        tmpUpdate("Error: Completely failed.", 0);
        JOptionPane.showMessageDialog(null,
            "Completely failed.\n" + e.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
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
     *          building. The date can be choosen either to identify the start
     *          date of the measurements or to identify the start date of the
     *          simulations. That's up to the user and does not affect the
     *          simulations.
     * @param detectionLimit
     *          The detection limit is used to set empty or '0'-values as radon
     *          concentrations of 0 Bq/m³ are most likely below detection limit.
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
     *          concentrations of 0 Bq/m³ are most likely below detection limit.
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
                  if (values[x][y] <= 0.0) {
                    tmpUpdate("Warning: Empty value " + i + " for room "
                        + roomId[j] + ": '" + tmpValues[j]
                        + "'. Using half of the detection limit ("
                        + detectionLimit + ").", 4);
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
     *          building. The date can be choosen either to identify the start
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
                  normals, cellars);
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

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
      tmpUpdate(getLogMsg(), getStatus());
      Toolkit.getDefaultToolkit().beep();
      progressBarImport.setVisible(false);
      btnImport.setEnabled(true);
      setCursor(null);
      JTabbedPane tab = (JTabbedPane) getParent();
      tab.remove(tab.getComponentAt(1));
      JPanel jpanelData = new OMPanelData(getOmbFile());
      tab.add(jpanelData, "Data", 1);
      tab.updateUI();
    }
  }

  public OMPanelImport() {
    setLayout(null);

    lblHelp = new JLabel(
        "Import CSV-Files and convert them to OMB-Project-Files which can be used by this tool to run simulations.");
    lblHelp.setForeground(Color.GRAY);
    lblHelp.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblHelp.setBounds(10, 11, 729, 20);
    add(lblHelp);

    lblProjectName = new JLabel("Project Name");
    lblProjectName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblProjectName.setBounds(10, 45, 89, 14);
    add(lblProjectName);

    lblProjectDate = new JLabel("Project Date");
    lblProjectDate.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblProjectDate.setBounds(10, 70, 89, 14);
    add(lblProjectDate);

    lblCsvFile = new JLabel("Read CSV-File");
    lblCsvFile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblCsvFile.setBounds(10, 120, 89, 14);
    add(lblCsvFile);

    lblDetectionLimit = new JLabel("Detection Limit");
    lblDetectionLimit.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblDetectionLimit.setBounds(10, 95, 89, 14);
    add(lblDetectionLimit);

    lblBqm = new JLabel("Bq/m\u00B3");
    lblBqm.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblBqm.setBounds(580, 95, 160, 14);
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
    lblSaveOmbfile.setBounds(10, 145, 89, 14);
    add(lblSaveOmbfile);
    txtProjectName.setBounds(109, 42, 529, 20);
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
    txtCsvFile.setBounds(109, 117, 529, 20);
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
    txtOmbFile.setBounds(109, 142, 529, 20);
    add(txtOmbFile);
    txtOmbFile.setColumns(10);

    spnrProjectDate = new JSpinner();
    spnrProjectDate.setModel(new SpinnerDateModel(new Date(), null, null,
        Calendar.DAY_OF_YEAR));
    spnrProjectDate.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    spnrProjectDate.setBounds(109, 68, 461, 18);
    add(spnrProjectDate);

    spnrDetectionLimit = new JSpinner();
    spnrDetectionLimit.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        setDetectionLimit((int) spnrDetectionLimit.getValue());
      }
    });
    spnrDetectionLimit.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    spnrDetectionLimit.setModel(new SpinnerNumberModel(20, 1, 100, 1));
    spnrDetectionLimit.setBounds(109, 93, 461, 18);
    add(spnrDetectionLimit);

    chckbxToday = new JCheckBox("Today");
    chckbxToday.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    chckbxToday.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        if (chckbxToday.isSelected()) {
          spnrProjectDate.setEnabled(false);
          setProjectDate(new Date());
        } else {
          spnrProjectDate.setEnabled(true);
          setProjectDate((Date) spnrProjectDate.getValue());
        }
      }
    });
    chckbxToday.setBounds(580, 66, 159, 23);
    add(chckbxToday);

    progressBarImport = new JProgressBar();
    progressBarImport.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    progressBarImport.setBounds(10, 476, 729, 23);
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
    btnBrowseCsv.setBounds(648, 116, 91, 23);
    add(btnBrowseCsv);

    btnImport = new JButton("Import");
    btnImport.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnImport.setBounds(648, 175, 91, 23);
    btnImport.addActionListener(this);

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
    btnBrowseOmb.setBounds(648, 141, 91, 23);
    add(btnBrowseOmb);
    add(btnImport);
  }

  /**
   * Invoked when the user presses the start button.
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
                spnrProjectDate.setEnabled(false);
                setProjectDate(new Date());
              } else {
                spnrProjectDate.setEnabled(true);
                setProjectDate((Date) spnrProjectDate.getValue());
              }
              setProjectName(txtProjectName.getText());
              setCsvFile(txtCsvFile.getText());
              setDetectionLimit((int) spnrDetectionLimit.getValue());
              progressBarImport.setVisible(true);
              progressBarImport.setStringPainted(true);
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              importTask = new ImportTask();
              importTask.addPropertyChangeListener(this);
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

  /**
   * Invoked when task's progress property changes.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    updateStatus();
  }
}
