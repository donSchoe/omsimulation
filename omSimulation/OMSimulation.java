package omSimulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import omSimulation.data.OMBuilding;
import omSimulation.data.OMCampaign;
import omSimulation.data.OMHelper;
import omSimulation.data.OMRoom;
import omSimulation.data.OMRoomType;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

/**
 * Public abstract class OMSimulation, which is the main entry point of this
 * software. Can not be instantiated.
 * 
 * @author A. Schoedon
 */
public abstract class OMSimulation {

  /**
   * Stores a custom name for the project which is set by the user creating the
   * simulation.
   */
  private static String ProjectName;

  /**
   * Stores the total number of measurements for the building. The unit is [h].
   */
  private static int ValueCount;

  /**
   * Stores the total number of rooms for the building.
   */
  private static int RoomCount;

  /**
   * Gets a custom name for the project which is set by the user creating the
   * simulation.
   * 
   * @return The custom name for the project.
   */
  private static String getProjectName() {
    return ProjectName;
  }

  /**
   * Sets a custom name for the project which is set by the user creating the
   * simulation.
   * 
   * @param projectName
   *          The custom name for the project.
   */
  private static void setProjectName(String projectName) {
    ProjectName = projectName;
  }

  /**
   * Gets the total number of measurements for the building. The unit is [h].
   * 
   * @return The total number of measurements for the building.
   */
  private static int getValueCount() {
    return ValueCount;
  }

  /**
   * Sets the total number of measurements for the building. The unit is [h].
   * 
   * @param count
   *          The total number of measurements for the building.
   */
  private static void setValueCount(int count) {
    ValueCount = count;
  }

  /**
   * Gets the total number of rooms for the building.
   * 
   * @return The total number of rooms for the building.
   */
  private static int getRoomCount() {
    return RoomCount;
  }

  /**
   * Sets the total number of rooms for the building.
   * 
   * @param roomCount
   *          The total number of rooms for the building.
   */
  private static void setRoomCount(int roomCount) {
    RoomCount = roomCount;
  }

  /**
   * The main entry point of this software. Launching a command line application
   * which reads radon data from predefined CSV-files for development purposes
   * only. If you read this and you are looking for the final OMSimulation tool
   * with GUI, look out for newer releases.
   * 
   * @param args
   *          An array of random strings.
   * @throws IOException
   *           If creating log file or writing logs fails.
   * @throws ParseException
   *           If parsing date does not work.
   */
  public static void main(String[] args) throws IOException, ParseException {
    double start = System.currentTimeMillis();
    ObjectContainer db4o = Db4oEmbedded.openFile(
        Db4oEmbedded.newConfiguration(), "omSimulation.db");
    String version = "0.2-dev";
    // setProjectName("3r1c168_minimal");
    // String csvFile = "3r1c168_minimal.csv";
    setProjectName("8r4c1008_maximal");
    String csvFile = "8r4c1008_maximal.csv";
    OMHelper.setLogOutput(getProjectName());
    OMHelper.writeLog("OM-Simulation tool v" + version + " started.");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH");
    Date projectDate = dateFormat.parse("2012:02:06:15");
    dateFormat.format(projectDate);
    double detectionLimit = 20.0;
    ObjectSet<OMBuilding> result = db4o.queryByExample(OMBuilding.class);
    if (result.size() == 0) {
      OMHelper.writeLog("No objects in database (" + result.size()
          + "). Generating new building from CSV.");
      OMBuilding building = createBuilding(csvFile, getProjectName(),
          projectDate, detectionLimit);
      if (building.getRoomCount() >= 4) {
        db4o.store(building);
        OMHelper.writeLog("Stored new building to database: " + building);
      } else {
        OMHelper
            .writeLog("Error: Not enough rooms. No building stored to database.");
      }
    } else {
      OMHelper.writeLog("Found " + result.size() + " building in Database.");
      if (result.size() == 1) {
        OMBuilding building = (OMBuilding) result.next();
        OMHelper.writeLog("Opened building: " + building);
        int maxCampaigns = 0;
        int ratioThree = 2;
        int ratioFour = 5;
        int ratioFive = 20;
        int ratioSix = 73;
        int randomNoise = 20;
        boolean comma = true;
        if (maxCampaigns > 0) {
          generateRandomCampaigns(building, maxCampaigns, ratioThree,
              ratioFour, ratioFive, ratioSix, randomNoise, comma);
        } else {
          generateSystematicCampaigns(building, randomNoise, comma);
        }
      } else {
        // TODO (postponed) select object, if (result.size() > 1);
        OMHelper.writeLog("Too many buildings (" + result.size()
            + ") found. Dismissing them.");

        OMBuilding found;
        for (int i = 0; i < result.size(); i++) {
          found = (OMBuilding) result.next();
          db4o.delete(found);
        }
      }
    }
    db4o.close();
    double total = (System.currentTimeMillis() - start) / 1000 / 60;
    OMHelper.writeLog("OM-Simulation tool finished after " + total
        + " minutes.");
    OMHelper.closeLog();
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
   *          A custom name for the object which can be set by the user creating
   *          the building.
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
  private static OMBuilding createBuilding(String csvFile, String name,
      Date date, double detectionLimit) throws IOException {
    String[] csv;
    try {
      csv = parseCSV(csvFile);
    } catch (Exception e) {
      csv = new String[0];
      OMHelper.writeLog("Error: " + e.getMessage());
      OMHelper.writeLog("Error: Failed to read CSV-File.");
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
  private static String[] parseCSV(String fileName) throws IOException {
    String[] tmpArray = new String[65536];
    String[] csv = null;
    OMHelper.writeLog("Trying to read CSV-File '" + fileName + "'.");
    try {
      FileInputStream fileInput = new FileInputStream(fileName);
      DataInputStream dataInput = new DataInputStream(fileInput);
      InputStreamReader inputReader = new InputStreamReader(dataInput);
      BufferedReader buffReader = new BufferedReader(inputReader);
      OMHelper.writeLog("Read CSV-File with success, trying to "
          + "parse line by line:");
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
            OMHelper.writeLog(csv[x]);
          }
          setValueCount(valueCount);
          OMHelper.writeLog("Parsed " + valueCount + " lines with success.");
        } else {
          csv = new String[0];
          OMHelper.writeLog("Error: " + valueCount
              + " are too many data sets. Aborting.");
          OMHelper
              .writeLog("Error: Make sure you have at maximum six weeks of records (<= 1008).");
        }
      } else {
        csv = new String[0];
        OMHelper.writeLog("Error: " + valueCount
            + " are not enough data sets. Aborting.");
        OMHelper
            .writeLog("Error: Make sure you have at least one week of records (>= 168).");
      }
      buffReader.close();
      inputReader.close();
      dataInput.close();
      fileInput.close();
    } catch (Exception e) {
      csv = new String[0];
      OMHelper.writeLog("Error: " + e.getMessage());
      OMHelper.writeLog("Error: Failed to read CSV-File" + fileName + ".");
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
  private static OMRoom[] parseRooms(String[] csv, double detectionLimit)
      throws IOException {
    OMHelper.writeLog("Trying to parse rooms:");
    OMRoom[] rooms;
    try {
      if (csv.length > 1) {
        String header = csv[0];
        String[] roomId = header.split("\\;");
        int roomCount = roomId.length;
        for (int i = 1; i < roomCount; i++) {
          OMHelper.writeLog("Found room: " + roomId[i]);
          if (roomId[i].isEmpty()) {
            OMHelper
                .writeLog("Warning: malformed room identifier. Using \"m0\" for misc.");
            roomId[i] = "m0";
          }
        }
        roomCount--;
        setRoomCount(roomCount);
        OMHelper.writeLog("Parsed " + roomCount + " rooms with success.");
        rooms = new OMRoom[roomCount];
        int valueCount = getValueCount();
        double[] values[] = new double[roomCount][valueCount];
        OMHelper.writeLog("Trying to collect values for each room:");
        String[] tmpValues = roomId;
        int tmpLength = tmpValues.length;
        boolean success = true;
        for (int i = 1; i < csv.length; i++) {
          tmpValues = csv[i].split("\\;");
          boolean isFirstDigit = Character.isDigit(csv[i].charAt(0));
          boolean isLastDigit = Character
              .isDigit(csv[i].charAt(csv[i].length() - 1));
          if (isFirstDigit && isLastDigit) {
            if (tmpValues.length == tmpLength) {
              for (int j = 1; j < tmpValues.length; j++) {
                int x = j - 1;
                int y = i - 1;
                if (tmpValues[j].isEmpty()) {
                  OMHelper.writeLog("Warning: Empty string. Using value 0.");
                  values[x][y] = 0.0;
                } else {
                  values[x][y] = (double) Integer.parseInt(tmpValues[j]);
                }
                if (values[x][y] <= 0.0) {
                  OMHelper.writeLog("Warning: Empty value " + i + " for room "
                      + roomId[j] + ": '" + tmpValues[j]
                      + "'. Using half of the detection limit ("
                      + detectionLimit + ").");
                  values[x][y] = detectionLimit / 2.0;
                }
                OMHelper.writeLog("Parsed value " + i + " for room "
                    + roomId[j] + ": " + values[x][y]);
              }
              tmpLength = tmpValues.length;
            } else {
              OMHelper.writeLog("Error: Malformed CSV-file. Aborting.");
              i = csv.length + 1;
              success = false;
            }
          } else {
            OMHelper.writeLog("Error: Malformed CSV-line: " + csv[i]);
            i = csv.length + 1;
            success = false;
          }
        }
        if (success) {
          for (int k = 1; k <= roomCount; k++) {
            int z = k - 1;
            rooms[z] = new OMRoom(roomId[k], values[z]);
          }
          OMHelper.writeLog("Successfully collected values for each room.");
        } else {
          rooms = new OMRoom[0];
          OMHelper.writeLog("Error: Check your CSV-File.");
        }
      } else {
        rooms = new OMRoom[0];
        OMHelper.writeLog("Error: No data records found.");
        OMHelper.writeLog("Error: Check your CSV-File.");
      }
    } catch (Exception e) {
      rooms = new OMRoom[0];
      OMHelper.writeLog("Error: " + e.getMessage());
      OMHelper.writeLog("Error: Failed to parse rooms.");
    }
    return rooms;
  }

  /**
   * Method to separate an array of various rooms by their different types and
   * to create an OMBuilding object out of this rooms.
   * 
   * @param name
   *          A custom name for the object which can be set by the user creating
   *          the building.
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
  private static OMBuilding separateRooms(String name, Date date, OMRoom[] rooms)
      throws IOException {
    OMHelper.writeLog("Trying to separate rooms.");
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
      OMHelper
          .writeLog("Found " + normalCount + " normal rooms, " + cellarCount
              + " cellars and " + miscCount + " miscellaneous rooms.");
      if (normalCount >= 3 && cellarCount >= 1) {
        if (normalCount <= 8 && cellarCount <= 4) {
          if (roomCount == cellarCount + normalCount + miscCount) {
            OMHelper.writeLog("Separation test succeeded, separating rooms.");
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
            OMHelper.writeLog("Setting up rooms by types.");
            int valueCount = getValueCount();
            building = new OMBuilding(name, date, roomCount, valueCount,
                normals, cellars);
            OMHelper.writeLog("Done. Finished setting up rooms.");
          } else {
            OMHelper
                .writeLog("Error: Separation test failed. Malformed input.");
          }
        } else {
          OMHelper
              .writeLog("Error: Separation test failed. Too many rooms or cellars.");
          OMHelper
              .writeLog("Error: Make sure you don't have more than 8 rooms and 4 cellars.");
        }
      } else {
        OMHelper
            .writeLog("Error: Separation test failed. Not enough rooms or cellars.");
        OMHelper
            .writeLog("Error: Make sure you have at least 3 rooms and 1 cellar.");
      }
    } else {
      OMHelper
          .writeLog("Error: Separation test failed. Not enough rooms or cellars.");
    }
    return building;
  }

  /**
   * Method to simulate systematic survey campaigns. It calculates summary
   * statistics for simulations with n > 1 million and descriptive statistics
   * for simulations with n <= 1 million. It writes a CSV file with the results
   * of the statistics in the end. This may take a while, grab a coffee.
   * 
   * @param building
   *          An building consisting of all the rooms and their values.
   * @param randomNoise
   *          An integer which defines the random noise that is added to the
   *          values. A random noise of 0 means the original values wont be
   *          modified. The unit is [%].
   * @param comma
   *          TRUE indicates if a comma is desired to be used as the decimal
   *          separator for the results. FALSE uses default dot.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  private static void generateSystematicCampaigns(OMBuilding building,
      int randomNoise, boolean comma) throws IOException {
    int valueCount = building.getValueCount();
    int total = valueCount - 7 * 24 + 1;
    if (total >= 1) {
      OMHelper.writeLog(valueCount + " data records allow " + total
          + " possible times for starting a simulation.");
      OMRoom[] variationSchemeSix[] = building.getVariationSchemeSix();
      OMRoom[] variationSchemeFive[] = building.getVariationSchemeFive();
      OMRoom[] variationSchemeFour[] = building.getVariationSchemeFour();
      OMRoom[] variationSchemeThree[] = building.getVariationSchemeThree();
      long x = 0;
      int campaignLengthSix = variationSchemeSix.length;
      int campaignLengthFive = variationSchemeFive.length;
      int campaignLengthFour = variationSchemeFour.length;
      int campaignLengthThree = variationSchemeThree.length;
      long perc = 0;
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
          OMHelper
              .writeLog("Starting unlimited descriptive simulation for 6 different rooms.");
          for (int a = 0; a < campaignLengthSix; a++) {
            perc = (x * 100) / max;
            OMHelper.writeLog("Status: " + perc + "%");
            for (int start = 0; start < total; start++) {
              campaign = new OMCampaign(start, variationSchemeSix[a],
                  randomNoise);
              roomAmDescriptiveStats.addValue(campaign.getRoomAvarage());
              cellarAmDescriptiveStats.addValue(campaign.getCellarAvarage());
              roomGmDescriptiveStats.addValue(campaign.getRoomLogAvarage());
              cellarGmDescriptiveStats.addValue(campaign.getCellarLogAvarage());
              roomMedDescriptiveStats.addValue(campaign.getRoomMedian());
              cellarMedDescriptiveStats.addValue(campaign.getCellarMedian());
              roomMaxDescriptiveStats.addValue(campaign.getRoomMaxima());
              cellarMaxDescriptiveStats.addValue(campaign.getCellarMaxima());
              OMHelper.writeLog(campaign.toString());
              x++;
            }
          }
          perc = (x * 100) / max;
          OMHelper.writeLog("Status: " + perc
              + "% - finished for 6 different rooms.");
        } else {
          OMHelper
              .writeLog("Warning: No variations for 6 different rooms available.");
          if (campaignLengthFive > 0) {
            OMHelper
                .writeLog("Starting unlimited descriptive simulation for 5 different rooms.");
            for (int a = 0; a < campaignLengthFive; a++) {
              perc = (x * 100) / max;
              OMHelper.writeLog("Status: " + perc + "%");
              for (int start = 0; start < total; start++) {
                campaign = new OMCampaign(start, variationSchemeFive[a],
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
                OMHelper.writeLog(campaign.toString());
                x++;
              }
            }
            perc = (x * 100) / max;
            OMHelper.writeLog("Status: " + perc
                + "% - finished for 5 different rooms.");
          } else {
            OMHelper
                .writeLog("Warning: No variations for 5 different rooms available.");
            if (campaignLengthFour > 0) {
              OMHelper
                  .writeLog("Starting unlimited descriptive simulation for 4 different rooms.");
              for (int a = 0; a < campaignLengthFour; a++) {
                perc = (x * 100) / max;
                OMHelper.writeLog("Status: " + perc + "%");
                for (int start = 0; start < total; start++) {
                  campaign = new OMCampaign(start, variationSchemeFour[a],
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
                  OMHelper.writeLog(campaign.toString());
                  x++;
                }
              }
              perc = (x * 100) / max;
              OMHelper.writeLog("Status: " + perc
                  + "% - finished for 4 different rooms.");
            } else {
              OMHelper
                  .writeLog("Warning: No variations for 4 different rooms available.");
              if (campaignLengthThree > 0) {
                OMHelper
                    .writeLog("Starting unlimited descriptive simulation for 3 different rooms.");
                for (int a = 0; a < campaignLengthThree; a++) {
                  perc = (x * 100) / max;
                  OMHelper.writeLog("Status: " + perc + "%");
                  for (int start = 0; start < total; start++) {
                    campaign = new OMCampaign(start, variationSchemeThree[a],
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
                    OMHelper.writeLog(campaign.toString());
                    x++;
                  }
                }
                perc = (x * 100) / max;
                OMHelper.writeLog("Status: " + perc
                    + "% - finished for 3 different rooms.");
              } else {
                OMHelper
                    .writeLog("Warning: No variations for 3 different rooms available.");
                OMHelper
                    .writeLog("Error: No variations generated yet, something went wrong.");
              }
            }
          }
        }
      } else {
        isDescriptive = false;
        if (campaignLengthSix > 0) {
          OMHelper
              .writeLog("Starting unlimited summary simulation for 6 different rooms.");
          for (int a = 0; a < campaignLengthSix; a++) {
            perc = (x * 100) / max;
            OMHelper.writeLog("Status: " + perc + "%");
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
              OMHelper.writeLog(campaign.toString());
              x++;
            }
          }
          perc = (x * 100) / max;
          OMHelper.writeLog("Status: " + perc
              + "% - finished for 6 different rooms.");
        } else {
          OMHelper
              .writeLog("Warning: No variations for 6 different rooms available.");
          if (campaignLengthFive > 0) {
            OMHelper
                .writeLog("Starting unlimited summary simulation for 5 different rooms.");
            for (int a = 0; a < campaignLengthFive; a++) {
              perc = (x * 100) / max;
              OMHelper.writeLog("Status: " + perc + "%");
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
                OMHelper.writeLog(campaign.toString());
                x++;
              }
            }
            perc = (x * 100) / max;
            OMHelper.writeLog("Status: " + perc
                + "% - finished for 5 different rooms.");
          } else {
            OMHelper
                .writeLog("Warning: No variations for 5 different rooms available.");
            if (campaignLengthFour > 0) {
              OMHelper
                  .writeLog("Starting unlimited summary simulation for 4 different rooms.");
              for (int a = 0; a < campaignLengthFour; a++) {
                perc = (x * 100) / max;
                OMHelper.writeLog("Status: " + perc + "%");
                for (int start = 0; start < total; start++) {
                  campaign = new OMCampaign(start, variationSchemeFour[a],
                      randomNoise);
                  roomAmSummaryStats.addValue(campaign.getRoomAvarage());
                  cellarAmSummaryStats.addValue(campaign.getCellarAvarage());
                  roomGmSummaryStats.addValue(campaign.getRoomLogAvarage());
                  cellarGmSummaryStats.addValue(campaign.getCellarLogAvarage());
                  roomMedSummaryStats.addValue(campaign.getRoomMedian());
                  cellarMedSummaryStats.addValue(campaign.getCellarMedian());
                  roomMaxSummaryStats.addValue(campaign.getRoomMaxima());
                  cellarMaxSummaryStats.addValue(campaign.getCellarMaxima());
                  OMHelper.writeLog(campaign.toString());
                  x++;
                }
              }
              perc = (x * 100) / max;
              OMHelper.writeLog("Status: " + perc
                  + "% - finished for 4 different rooms.");
            } else {
              OMHelper
                  .writeLog("Warning: No variations for 4 different rooms available.");
              if (campaignLengthThree > 0) {
                OMHelper
                    .writeLog("Starting unlimited summary simulation for 3 different rooms.");
                for (int a = 0; a < campaignLengthThree; a++) {
                  perc = (x * 100) / max;
                  OMHelper.writeLog("Status: " + perc + "%");
                  for (int start = 0; start < total; start++) {
                    campaign = new OMCampaign(start, variationSchemeThree[a],
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
                    OMHelper.writeLog(campaign.toString());
                    x++;
                  }
                }
                perc = (x * 100) / max;
                OMHelper.writeLog("Status: " + perc
                    + "% - finished for 3 different rooms.");
              } else {
                OMHelper
                    .writeLog("Warning: No variations for 3 different rooms available.");
                OMHelper
                    .writeLog("Error: No variations generated yet, what went wrong?");
              }
            }
          }
        }
      }
      OMHelper.writeLog("Generated " + x + " campaigns.");
      Format format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
      double current = System.currentTimeMillis();
      String timestamp = format.format(current);
      String logName = timestamp + "_" + getProjectName() + ".result.csv";
      File logFile = new File(logName);
      FileWriter logWriter = new FileWriter(logFile);
      BufferedWriter csvOutput = new BufferedWriter(logWriter);
      String strFormat = "#.#########";
      DecimalFormatSymbols decSymbols = new DecimalFormatSymbols();
      if (comma) {
        decSymbols.setDecimalSeparator(',');
      } else {
        decSymbols.setDecimalSeparator('.');
      }
      DecimalFormat decFormat = new DecimalFormat(strFormat, decSymbols);
      if (isDescriptive) {
        descriptiveStatistics(x, roomAmDescriptiveStats,
            cellarAmDescriptiveStats, roomGmDescriptiveStats,
            cellarGmDescriptiveStats, roomMedDescriptiveStats,
            cellarMedDescriptiveStats, roomMaxDescriptiveStats,
            cellarMaxDescriptiveStats, csvOutput, decFormat);
      } else {
        summaryStatistics(x, roomAmSummaryStats, cellarAmSummaryStats,
            roomGmSummaryStats, cellarGmSummaryStats, roomMedSummaryStats,
            cellarMedSummaryStats, roomMaxSummaryStats, cellarMaxSummaryStats,
            csvOutput, decFormat);
      }
      csvOutput.close();
    } else {
      OMHelper.writeLog("Error: " + valueCount
          + " are not enough data records.");
      OMHelper
          .writeLog("Make sure you have at least one week of records (> 168).");
    }
  }

  /**
   * Method to simulate random survey campaigns using a defined maximum number
   * and a defined ratio between different types of variations used. It
   * calculates summary statistics for simulations with n > 1 million and
   * descriptive statistics for simulations with n <= 1 million. It writes a CSV
   * file with the results of the statistics in the end. This may take a while,
   * grab a coffee.
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
   * @param comma
   *          TRUE indicates if a comma is desired to be used as the decimal
   *          separator for the results. FALSE uses default dot.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  private static void generateRandomCampaigns(OMBuilding building,
      int maxCampaigns, int ratioThree, int ratioFour, int ratioFive,
      int ratioSix, int randomNoise, boolean comma) throws IOException {
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
        OMHelper
            .writeLog("Warning: No variations for 6 rooms. Setting ratio to 0.");
        ratioSix = 0;
        tmpDiv--;
      }
      int campaignLengthFive = variationSchemeFive.length;
      if (campaignLengthFive <= 0) {
        OMHelper
            .writeLog("Warning: No variations for 5 rooms. Setting ratio to 0.");
        ratioFive = 0;
        tmpDiv--;
      }
      int campaignLengthFour = variationSchemeFour.length;
      if (campaignLengthFour <= 0) {
        OMHelper
            .writeLog("Warning: No variations for 4 rooms. Setting ratio to 0.");
        ratioFour = 0;
        tmpDiv--;
      }
      int campaignLengthThree = variationSchemeThree.length;
      int ratioTotal = ratioThree + ratioFour + ratioFive + ratioSix;
      int total = valueCount - 7 * 24 + 1;
      if (ratioTotal <= 0) {
        OMHelper.writeLog("Warning: Your entered ratio is: " + ratioThree
            + ", " + ratioFour + ", " + ratioFive + ", " + ratioSix + ".");
        OMHelper
            .writeLog("Warning: Splitting the simulation in equal parts of 25%.");
        absoluteThree = (int) ((double) maxCampaigns / tmpDiv);
        if (campaignLengthFour <= 0) {
          absoluteFour = (int) ((double) maxCampaigns / tmpDiv);
          if (campaignLengthFive <= 0) {
            absoluteFive = (int) ((double) maxCampaigns / tmpDiv);
            if (campaignLengthSix <= 0) {
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
      OMHelper.writeLog("Simulation will start with " + absoluteTotal
          + " random variations:");
      OMHelper.writeLog(absoluteThree
          + " Simulations using 3 different rooms. (Ratio: ca. "
          + (int) ((double) ratioThree / (double) ratioTotal * 100.0) + "% ("
          + ratioThree + "))");
      if (absoluteThree > campaignLengthThree * total) {
        OMHelper.writeLog("Warning: Simulating " + absoluteThree
            + " campaigns for 3 rooms, but only "
            + (campaignLengthThree * total)
            + " variations are existing for 3 rooms.");
        OMHelper
            .writeLog("Warning: Consider to reduce the ratio or number of total simulations.");
      }
      OMHelper.writeLog(absoluteFour
          + " Simulations using 4 different rooms. (Ratio: ca. "
          + (int) ((double) ratioFour / (double) ratioTotal * 100.0) + "% ("
          + ratioFour + "))");
      if (absoluteFour > campaignLengthFour * total) {
        OMHelper.writeLog("Warning: Simulating " + absoluteFour
            + " campaigns for 4 rooms, but only "
            + (campaignLengthFour * total)
            + " variations are existing for 4 rooms.");
        OMHelper
            .writeLog("Warning: Consider to reduce the ratio or number of total simulations.");
      }
      OMHelper.writeLog(absoluteFive
          + " Simulations using 5 different rooms. (Ratio: ca. "
          + (int) ((double) ratioFive / (double) ratioTotal * 100.0) + "% ("
          + ratioFive + "))");
      if (absoluteFive > campaignLengthFive * total) {
        OMHelper.writeLog("Warning: Simulating " + absoluteFive
            + " campaigns for 5 rooms, but only "
            + (campaignLengthFive * total)
            + " variations are existing for 5 rooms.");
        OMHelper
            .writeLog("Warning: Consider to reduce the ratio or number of total simulations.");
      }
      OMHelper.writeLog(absoluteSix
          + " Simulations using 6 different rooms. (Ratio: ca. "
          + (int) ((double) ratioSix / (double) ratioTotal * 100.0) + "% ("
          + ratioSix + "))");
      if (absoluteSix > campaignLengthSix * total) {
        OMHelper.writeLog("Warning: Simulating " + absoluteSix
            + " campaigns for 6 rooms, but only " + (campaignLengthSix * total)
            + " variations are existing for 6 rooms.");
        OMHelper
            .writeLog("Warning: Consider to reduce the ratio or number of total simulations.");
      }
      if (total >= 1) {
        OMHelper.writeLog(valueCount + " data records allow " + total
            + " possible times for starting a simulation.");
        long x = 0;
        long perc = 0;
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
        if (absoluteTotal <= 1000000) {
          isDescriptive = true;
          if (campaignLengthThree > 0) {
            OMHelper
                .writeLog("Starting descriptive simulation for 3 rooms with "
                    + absoluteThree + " random variations.");
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
                OMHelper.writeLog("Status: " + perc + "%");
              }
              if (total > 1) {
                start = generator.nextInt(total);
              } else {
                start = 0;
              }
              campaign = new OMCampaign(start, variationSchemeThree[random[a]],
                  randomNoise);
              roomAmDescriptiveStats.addValue(campaign.getRoomAvarage());
              cellarAmDescriptiveStats.addValue(campaign.getCellarAvarage());
              roomGmDescriptiveStats.addValue(campaign.getRoomLogAvarage());
              cellarGmDescriptiveStats.addValue(campaign.getCellarLogAvarage());
              roomMedDescriptiveStats.addValue(campaign.getRoomMedian());
              cellarMedDescriptiveStats.addValue(campaign.getCellarMedian());
              roomMaxDescriptiveStats.addValue(campaign.getRoomMaxima());
              cellarMaxDescriptiveStats.addValue(campaign.getCellarMaxima());
              OMHelper.writeLog(campaign.toString());
              x++;
            }
            perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
            OMHelper.writeLog("Status: " + perc
                + "% - finished for 3 different rooms.");
            if (campaignLengthFour > 0) {
              OMHelper
                  .writeLog("Starting descriptive simulation for 4 rooms with "
                      + absoluteFour + " random variations.");
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
                  OMHelper.writeLog("Status: " + perc + "%");
                }
                if (total > 1) {
                  start = generator.nextInt(total);
                } else {
                  start = 0;
                }
                campaign = new OMCampaign(start,
                    variationSchemeFour[random[a]], randomNoise);
                roomAmDescriptiveStats.addValue(campaign.getRoomAvarage());
                cellarAmDescriptiveStats.addValue(campaign.getCellarAvarage());
                roomGmDescriptiveStats.addValue(campaign.getRoomLogAvarage());
                cellarGmDescriptiveStats.addValue(campaign
                    .getCellarLogAvarage());
                roomMedDescriptiveStats.addValue(campaign.getRoomMedian());
                cellarMedDescriptiveStats.addValue(campaign.getCellarMedian());
                roomMaxDescriptiveStats.addValue(campaign.getRoomMaxima());
                cellarMaxDescriptiveStats.addValue(campaign.getCellarMaxima());
                OMHelper.writeLog(campaign.toString());
                x++;
              }
              perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
              OMHelper.writeLog("Status: " + perc
                  + "% - finished for 4 different rooms.");
              if (campaignLengthFive > 0) {
                OMHelper
                    .writeLog("Starting descriptive simulation for 5 rooms with "
                        + absoluteFive + " random variations.");
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
                    OMHelper.writeLog("Status: " + perc + "%");
                  }
                  if (total > 1) {
                    start = generator.nextInt(total);
                  } else {
                    start = 0;
                  }
                  campaign = new OMCampaign(start,
                      variationSchemeFive[random[a]], randomNoise);
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
                  OMHelper.writeLog(campaign.toString());
                  x++;
                }
                perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                OMHelper.writeLog("Status: " + perc
                    + "% - finished for 5 different rooms.");
                if (campaignLengthSix > 0) {
                  OMHelper
                      .writeLog("Starting descriptive simulation for 6 rooms with "
                          + absoluteSix + " random variations.");
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
                      OMHelper.writeLog("Status: " + perc + "%");
                    }
                    if (total > 1) {
                      start = generator.nextInt(total);
                    } else {
                      start = 0;
                    }
                    campaign = new OMCampaign(start,
                        variationSchemeSix[random[a]], randomNoise);
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
                    OMHelper.writeLog(campaign.toString());
                    x++;
                  }
                  perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                  OMHelper.writeLog("Status: " + perc
                      + "% - finished for 6 different rooms.");
                } else {
                  OMHelper
                      .writeLog("Warning: No variations for 6 different rooms available.");
                }
              } else {
                OMHelper
                    .writeLog("Warning: No variations for 5 different rooms available.");
                OMHelper
                    .writeLog("Warning: No variations for 6 different rooms available.");
              }
            } else {
              OMHelper
                  .writeLog("Warning: No variations for 4 different rooms available.");
              OMHelper
                  .writeLog("Warning: No variations for 5 different rooms available.");
              OMHelper
                  .writeLog("Warning: No variations for 6 different rooms available.");
            }
          } else {
            OMHelper
                .writeLog("Warning: No variations for 3 different rooms available.");
            OMHelper
                .writeLog("Warning: No variations for 4 different rooms available.");
            OMHelper
                .writeLog("Warning: No variations for 5 different rooms available.");
            OMHelper
                .writeLog("Warning: No variations for 6 different rooms available.");
            OMHelper
                .writeLog("Error: No variations generated yet, what went wrong?");
          }
        } else {
          isDescriptive = false;
          if (campaignLengthThree > 0) {
            OMHelper.writeLog("Starting summary simulation with "
                + absoluteThree + " random variations for 3 rooms.");
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
                OMHelper.writeLog("Status: " + perc + "%");
              }
              if (total > 1) {
                start = generator.nextInt(total);
              } else {
                start = 0;
              }
              campaign = new OMCampaign(start, variationSchemeThree[random[a]],
                  randomNoise);
              roomAmSummaryStats.addValue(campaign.getRoomAvarage());
              cellarAmSummaryStats.addValue(campaign.getCellarAvarage());
              roomGmSummaryStats.addValue(campaign.getRoomLogAvarage());
              cellarGmSummaryStats.addValue(campaign.getCellarLogAvarage());
              roomMedSummaryStats.addValue(campaign.getRoomMedian());
              cellarMedSummaryStats.addValue(campaign.getCellarMedian());
              roomMaxSummaryStats.addValue(campaign.getRoomMaxima());
              cellarMaxSummaryStats.addValue(campaign.getCellarMaxima());
              OMHelper.writeLog(campaign.toString());
              x++;
            }
            perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
            OMHelper.writeLog("Status: " + perc
                + "% - finished for 3 different rooms.");
            if (campaignLengthFour > 0) {
              OMHelper.writeLog("Starting summary simulation with "
                  + absoluteFour + " random variations for 4 rooms.");
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
                  OMHelper.writeLog("Status: " + perc + "%");
                }
                if (total > 1) {
                  start = generator.nextInt(total);
                } else {
                  start = 0;
                }
                campaign = new OMCampaign(start,
                    variationSchemeFour[random[a]], randomNoise);
                roomAmSummaryStats.addValue(campaign.getRoomAvarage());
                cellarAmSummaryStats.addValue(campaign.getCellarAvarage());
                roomGmSummaryStats.addValue(campaign.getRoomLogAvarage());
                cellarGmSummaryStats.addValue(campaign.getCellarLogAvarage());
                roomMedSummaryStats.addValue(campaign.getRoomMedian());
                cellarMedSummaryStats.addValue(campaign.getCellarMedian());
                roomMaxSummaryStats.addValue(campaign.getRoomMaxima());
                cellarMaxSummaryStats.addValue(campaign.getCellarMaxima());
                OMHelper.writeLog(campaign.toString());
                x++;
              }
              perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
              OMHelper.writeLog("Status: " + perc
                  + "% - finished for 4 different rooms.");
              if (campaignLengthFive > 0) {
                OMHelper.writeLog("Starting summary simulation with "
                    + absoluteFive + " random variations for 5 rooms.");
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
                    OMHelper.writeLog("Status: " + perc + "%");
                  }
                  if (total > 1) {
                    start = generator.nextInt(total);
                  } else {
                    start = 0;
                  }
                  campaign = new OMCampaign(start,
                      variationSchemeFive[random[a]], randomNoise);
                  roomAmSummaryStats.addValue(campaign.getRoomAvarage());
                  cellarAmSummaryStats.addValue(campaign.getCellarAvarage());
                  roomGmSummaryStats.addValue(campaign.getRoomLogAvarage());
                  cellarGmSummaryStats.addValue(campaign.getCellarLogAvarage());
                  roomMedSummaryStats.addValue(campaign.getRoomMedian());
                  cellarMedSummaryStats.addValue(campaign.getCellarMedian());
                  roomMaxSummaryStats.addValue(campaign.getRoomMaxima());
                  cellarMaxSummaryStats.addValue(campaign.getCellarMaxima());
                  OMHelper.writeLog(campaign.toString());
                  x++;
                }
                perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                OMHelper.writeLog("Status: " + perc
                    + "% - finished for 5 different rooms.");
                if (campaignLengthSix > 0) {
                  OMHelper.writeLog("Starting summary simulation with "
                      + absoluteSix + " random variations for 6 rooms.");
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
                      OMHelper.writeLog("Status: " + perc + "%");
                    }
                    if (total > 1) {
                      start = generator.nextInt(total);
                    } else {
                      start = 0;
                    }
                    campaign = new OMCampaign(start,
                        variationSchemeSix[random[a]], randomNoise);
                    roomAmSummaryStats.addValue(campaign.getRoomAvarage());
                    cellarAmSummaryStats.addValue(campaign.getCellarAvarage());
                    roomGmSummaryStats.addValue(campaign.getRoomLogAvarage());
                    cellarGmSummaryStats.addValue(campaign
                        .getCellarLogAvarage());
                    roomMedSummaryStats.addValue(campaign.getRoomMedian());
                    cellarMedSummaryStats.addValue(campaign.getCellarMedian());
                    roomMaxSummaryStats.addValue(campaign.getRoomMaxima());
                    cellarMaxSummaryStats.addValue(campaign.getCellarMaxima());
                    OMHelper.writeLog(campaign.toString());
                    x++;
                  }
                  perc = (long) (((double) x / (double) absoluteTotal) * (double) 100.0);
                  OMHelper.writeLog("Status: " + perc
                      + "% - finished for 6 different rooms.");
                } else {
                  OMHelper
                      .writeLog("Warning: No variations for 6 different rooms available.");
                }
              } else {
                OMHelper
                    .writeLog("Warning: No variations for 5 different rooms available.");
                OMHelper
                    .writeLog("Warning: No variations for 6 different rooms available.");
              }
            } else {
              OMHelper
                  .writeLog("Warning: No variations for 4 different rooms available.");
              OMHelper
                  .writeLog("Warning: No variations for 5 different rooms available.");
              OMHelper
                  .writeLog("Warning: No variations for 6 different rooms available.");
            }
          } else {
            OMHelper
                .writeLog("Warning: No variations for 3 different rooms available.");
            OMHelper
                .writeLog("Warning: No variations for 4 different rooms available.");
            OMHelper
                .writeLog("Warning: No variations for 5 different rooms available.");
            OMHelper
                .writeLog("Warning: No variations for 6 different rooms available.");
            OMHelper
                .writeLog("Error: No variations generated yet, what went wrong?");
          }
        }
        OMHelper.writeLog("Generated " + x + " campaigns.");
        Format format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        double current = System.currentTimeMillis();
        String timestamp = format.format(current);
        String logName = timestamp + "_" + getProjectName() + ".result.csv";
        File logFile = new File(logName);
        FileWriter logWriter = new FileWriter(logFile);
        BufferedWriter csvOutput = new BufferedWriter(logWriter);
        String strFormat = "#.#########";
        DecimalFormatSymbols decSymbols = new DecimalFormatSymbols();
        if (comma) {
          decSymbols.setDecimalSeparator(',');
        } else {
          decSymbols.setDecimalSeparator('.');
        }
        DecimalFormat decFormat = new DecimalFormat(strFormat, decSymbols);
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
      } else {
        OMHelper.writeLog("Error: " + valueCount
            + " are not enough data records.");
        OMHelper
            .writeLog("Make sure you have at least one week of records (> 168).");
      }
    } else {
      OMHelper.writeLog("Error: Your entered ratio is: " + ratioThree + ", "
          + ratioFour + ", " + ratioFive + ", " + ratioSix + ".");
      OMHelper.writeLog("Error: Please, correct your input.");
    }
  }

  /**
   * Method used to calculate descriptive statistics which are stored in memory.
   * Writes results of the calculations to a separate CSV file. Only use this
   * for small simulations as this can cause memory exceptions.
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
  private static void descriptiveStatistics(long x,
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
    double cellarLogMeans_SD = cellarGmDescriptiveStats.getStandardDeviation();
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
    double roomMedians_CV = OMHelper
        .calculateCV(roomMedians_AM, roomMedians_SD);
    double roomMedians_Q05 = roomMedDescriptiveStats.getPercentile(5);
    double roomMedians_Q50 = roomMedDescriptiveStats.getPercentile(50);
    double roomMedians_Q95 = roomMedDescriptiveStats.getPercentile(95);
    double roomMedians_QD = OMHelper.calculateQD(roomMedians_Q05,
        roomMedians_Q50, roomMedians_Q95);
    double roomMedians_GM = roomMedDescriptiveStats.getGeometricMean();
    double roomMedians_GSD = OMHelper.calculateGSD(x,
        roomMedDescriptiveStats.getValues(), roomMedians_GM);
    double cellarMedians_AM = cellarMedDescriptiveStats.getMean();
    double cellarMedians_SD = cellarMedDescriptiveStats.getStandardDeviation();
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
    double roomMaxima_QD = OMHelper.calculateQD(roomMaxima_Q05, roomMaxima_Q50,
        roomMaxima_Q95);
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
    OMHelper.writeLog("Calculated statistics for room arithmetic means:");
    OMHelper.writeLog("AM=" + roomArithMeans_AM + ", SD=" + roomArithMeans_SD
        + ", CV=" + roomArithMeans_CV + ", GM=" + roomArithMeans_GM + ", GSD="
        + roomArithMeans_GSD + ", Q5=" + roomArithMeans_Q05 + ", Q50="
        + roomArithMeans_Q50 + ", Q95=" + roomArithMeans_Q95 + ", QD="
        + roomArithMeans_QD);
    OMHelper.writeLog("Calculated statistics for cellar arithmetic means:");
    OMHelper.writeLog("AM=" + cellarArithMeans_AM + ", SD="
        + cellarArithMeans_SD + ", CV=" + cellarArithMeans_CV + ", GM="
        + cellarArithMeans_GM + ", GSD=" + cellarArithMeans_GSD + ", Q5="
        + cellarArithMeans_Q05 + ", Q50=" + cellarArithMeans_Q50 + ", Q95="
        + cellarArithMeans_Q95 + ", QD=" + cellarArithMeans_QD);
    OMHelper.writeLog("Calculated statistics for room geometric means:");
    OMHelper.writeLog("AM=" + roomLogMeans_AM + ", SD=" + roomLogMeans_SD
        + ", CV=" + roomLogMeans_CV + ", GM=" + roomLogMeans_GM + ", GSD="
        + roomLogMeans_GSD + ", Q5=" + roomLogMeans_Q05 + ", Q50="
        + roomLogMeans_Q50 + ", Q95=" + roomLogMeans_Q95 + ", QD="
        + roomLogMeans_QD);
    OMHelper.writeLog("Calculated statistics for cellar geometric means:");
    OMHelper.writeLog("AM=" + cellarLogMeans_AM + ", SD=" + cellarLogMeans_SD
        + ", CV=" + cellarLogMeans_CV + ", GM=" + cellarLogMeans_GM + ", GSD="
        + cellarLogMeans_GSD + ", Q5=" + cellarLogMeans_Q05 + ", Q50="
        + cellarLogMeans_Q50 + ", Q95=" + cellarLogMeans_Q95 + ", QD="
        + cellarLogMeans_QD);
    OMHelper.writeLog("Calculated statistics for room medians:");
    OMHelper.writeLog("AM=" + roomMedians_AM + ", SD=" + roomMedians_SD
        + ", CV=" + roomMedians_CV + ", GM=" + roomMedians_GM + ", GSD="
        + roomMedians_GSD + ", Q5=" + roomMedians_Q05 + ", Q50="
        + roomMedians_Q50 + ", Q95=" + roomMedians_Q95 + ", QD="
        + roomMedians_QD);
    OMHelper.writeLog("Calculated statistics for cellar medians:");
    OMHelper.writeLog("AM=" + cellarMedians_AM + ", SD=" + cellarMedians_SD
        + ", CV=" + cellarMedians_CV + ", GM=" + cellarMedians_GM + ", GSD="
        + cellarMedians_GSD + ", Q5=" + cellarMedians_Q05 + ", Q50="
        + cellarMedians_Q50 + ", Q95=" + cellarMedians_Q95 + ", QD="
        + cellarMedians_QD);
    OMHelper.writeLog("Calculated statistics for room maxima:");
    OMHelper.writeLog("AM=" + roomMaxima_AM + ", SD=" + roomMaxima_SD + ", CV="
        + roomMaxima_CV + ", GM=" + roomMaxima_GM + ", GSD=" + roomMaxima_GSD
        + ", Q5=" + roomMaxima_Q05 + ", Q50=" + roomMaxima_Q50 + ", Q95="
        + roomMaxima_Q95 + ", QD=" + roomMaxima_QD);
    OMHelper.writeLog("Calculated statistics for cellar maxima:");
    OMHelper.writeLog("AM=" + cellarMaxima_AM + ", SD=" + cellarMaxima_SD
        + ", CV=" + cellarMaxima_CV + ", GM=" + cellarMaxima_GM + ", Q5="
        + cellarMaxima_Q05 + ", Q50=" + cellarMaxima_Q50 + ", Q95="
        + cellarMaxima_Q95 + ", QD=" + cellarMaxima_QD);
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
    csvOutput.write("\"R_GM\";\"" + decFormat.format(roomLogMeans_AM) + "\";\""
        + decFormat.format(roomLogMeans_SD) + "\";\""
        + decFormat.format(roomLogMeans_CV) + "\";\""
        + decFormat.format(roomLogMeans_GM) + "\";\""
        + decFormat.format(roomLogMeans_GSD) + "\";\""
        + decFormat.format(roomLogMeans_Q05) + "\";\""
        + decFormat.format(roomLogMeans_Q50) + "\";\""
        + decFormat.format(roomLogMeans_Q95) + "\";\""
        + decFormat.format(roomLogMeans_QD) + "\"");
    csvOutput.newLine();
    csvOutput.write("\"R_Q50\";\"" + decFormat.format(roomMedians_AM) + "\";\""
        + decFormat.format(roomMedians_SD) + "\";\""
        + decFormat.format(roomMedians_CV) + "\";\""
        + decFormat.format(roomMedians_GM) + "\";\""
        + decFormat.format(roomMedians_GSD) + "\";\""
        + decFormat.format(roomMedians_Q05) + "\";\""
        + decFormat.format(roomMedians_Q50) + "\";\""
        + decFormat.format(roomMedians_Q95) + "\";\""
        + decFormat.format(roomMedians_QD) + "\"");
    csvOutput.newLine();
    csvOutput.write("\"R_MAX\";\"" + decFormat.format(roomMaxima_AM) + "\";\""
        + decFormat.format(roomMaxima_SD) + "\";\""
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
   * Method used to calculate summary statistics which are not stored in memory.
   * Writes results of the calculations to a separate CSV file.
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
  private static void summaryStatistics(long x,
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
    double roomMedians_CV = OMHelper
        .calculateCV(roomMedians_AM, roomMedians_SD);
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
    OMHelper.writeLog("Calculated statistics for room arithmetic means:");
    OMHelper.writeLog("AM=" + roomArithMeans_AM + ", SD=" + roomArithMeans_SD
        + ", CV=" + roomArithMeans_CV + ", GM=" + roomArithMeans_GM);
    OMHelper.writeLog("Calculated statistics for cellar arithmetic means:");
    OMHelper.writeLog("AM=" + cellarArithMeans_AM + ", SD="
        + cellarArithMeans_SD + ", CV=" + cellarArithMeans_CV + ", GM="
        + cellarArithMeans_GM);
    OMHelper.writeLog("Calculated statistics for room geometric means:");
    OMHelper.writeLog("AM=" + roomLogMeans_AM + ", SD=" + roomLogMeans_SD
        + ", CV=" + roomLogMeans_CV + ", GM=" + roomLogMeans_GM);
    OMHelper.writeLog("Calculated statistics for cellar geometric means:");
    OMHelper.writeLog("AM=" + cellarLogMeans_AM + ", SD=" + cellarLogMeans_SD
        + ", CV=" + cellarLogMeans_CV + ", GM=" + cellarLogMeans_GM);
    OMHelper.writeLog("Calculated statistics for room medians:");
    OMHelper.writeLog("AM=" + roomMedians_AM + ", SD=" + roomMedians_SD
        + ", CV=" + roomMedians_CV + ", GM=" + roomMedians_GM);
    OMHelper.writeLog("Calculated statistics for cellar medians:");
    OMHelper.writeLog("AM=" + cellarMedians_AM + ", SD=" + cellarMedians_SD
        + ", CV=" + cellarMedians_CV + ", GM=" + cellarMedians_GM);
    OMHelper.writeLog("Calculated statistics for room maxima:");
    OMHelper.writeLog("AM=" + roomMaxima_AM + ", SD=" + roomMaxima_SD + ", CV="
        + roomMaxima_CV + ", GM=" + roomMaxima_GM);
    OMHelper.writeLog("Calculated statistics for cellar maxima:");
    OMHelper.writeLog("AM=" + cellarMaxima_AM + ", SD=" + cellarMaxima_SD
        + ", CV=" + cellarMaxima_CV + ", GM=" + cellarMaxima_GM);
    csvOutput.write("\"ID\";\"AM\";\"SD\";\"CV\";\"GM\"");
    csvOutput.newLine();
    csvOutput.write("\"R_AM\";\"" + decFormat.format(roomArithMeans_AM)
        + "\";\"" + decFormat.format(roomArithMeans_SD) + "\";\""
        + decFormat.format(roomArithMeans_CV) + "\";\""
        + decFormat.format(roomArithMeans_GM) + "\"");
    csvOutput.newLine();
    csvOutput.write("\"R_GM\";\"" + decFormat.format(roomLogMeans_AM) + "\";\""
        + decFormat.format(roomLogMeans_SD) + "\";\""
        + decFormat.format(roomLogMeans_CV) + "\";\""
        + decFormat.format(roomLogMeans_GM) + "\"");
    csvOutput.newLine();
    csvOutput.write("\"R_Q50\";\"" + decFormat.format(roomMedians_AM) + "\";\""
        + decFormat.format(roomMedians_SD) + "\";\""
        + decFormat.format(roomMedians_CV) + "\";\""
        + decFormat.format(roomMedians_GM) + "\"");
    csvOutput.newLine();
    csvOutput.write("\"R_MAX\";\"" + decFormat.format(roomMaxima_AM) + "\";\""
        + decFormat.format(roomMaxima_SD) + "\";\""
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
}
