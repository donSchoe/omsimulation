package omSimulation;

import omSimulation.data.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Public abstract class OMSimulation, which is the main entry point of this
 * software.
 * 
 * @author A. Schoedon
 */
public abstract class OMSimulation {
    /**
     * Stores the total number of measurements for the room. The count equals
     * hours.
     */
    private static int ValueCount;

    /**
     * Stores the total number of rooms for the building.
     */
    private static int RoomCount;

    /**
     * Stores all cellar-type rooms in an array.
     */
    private static OMRoom[] Cellars;

    /**
     * Stores all normal-type rooms in an array.
     */
    private static OMRoom[] Rooms;

    /**
     * Stores all miscellaneous-type rooms in an array.
     */
    private static OMRoom[] Miscs;

    /**
     * Stores a pattern of all possible variations of rooms.
     */
    private static OMRoom[][] VariationScheme;

    /**
     * Stores the generic log writer, used for the whole program's log output.
     */
    private static BufferedWriter LogOutput;

    /**
     * Gets the total number of measurements for the room.
     * 
     * @return The total number of measurements for the room.
     */
    private static int getValueCount() {
        return ValueCount;
    }

    /**
     * Sets the total number of measurements for the room.
     * 
     * @param count
     *            The total number of measurements for the room.
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
     *            The total number of rooms for the building.
     */
    private static void setRoomCount(int roomCount) {
        RoomCount = roomCount;
    }

    /**
     * Gets all cellar-type rooms in an array.
     * 
     * @return All cellar-type rooms in an array.
     */
    private static OMRoom[] getCellars() {
        return Cellars;
    }

    /**
     * Sets all cellar-type rooms in an array.
     * 
     * @param cellars
     *            All cellar-type rooms in an array.
     */
    private static void setCellars(OMRoom[] cellars) {
        Cellars = cellars;
    }

    /**
     * Gets all normal-type rooms in an array.
     * 
     * @return All normal-type rooms in an array.
     */
    private static OMRoom[] getRooms() {
        return Rooms;
    }

    /**
     * Sets all normal-type rooms in an array.
     * 
     * @param rooms
     *            All normal-type rooms in an array.
     */
    private static void setRooms(OMRoom[] rooms) {
        Rooms = rooms;
    }

    /**
     * Gets all miscellaneous-type rooms in an array.
     * 
     * @return All miscellaneous-type rooms in an array.
     */
    private static OMRoom[] getMiscs() {
        return Miscs;
    }

    /**
     * Sets all miscellaneous-type rooms in an array.
     * 
     * @param miscs
     *            All miscellaneous-type rooms in an array.
     */
    private static void setMiscs(OMRoom[] miscs) {
        Miscs = miscs;
    }

    /**
     * Gets a pattern of all possible variations of rooms.
     * 
     * @return A pattern of all possible variations of rooms.
     */
    private static OMRoom[][] getVariationScheme() {
        return VariationScheme;
    }

    /**
     * Sets a pattern of all possible variations of rooms.
     * 
     * @param variationScheme
     *            A pattern of all possible variations of rooms.
     */
    private static void setVariationScheme(OMRoom[][] variationScheme) {
        VariationScheme = variationScheme;
    }

    /**
     * Sets the log-writer, creating a timestamp, a filename based on the
     * timestamp and initializing finally the log writer.
     * 
     * @throws IOException
     *             If creating log file or writing logs fails.
     */
    private static void setLogOutput() throws IOException {
        Format format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        double current = System.currentTimeMillis();
        String timestamp = format.format(current);
        String logName = timestamp + "_main.log";
        File logFile = new File(logName);
        FileWriter logWriter = new FileWriter(logFile);
        BufferedWriter logOutput = new BufferedWriter(logWriter);
        LogOutput = logOutput;
    }

    /**
     * The main entry point of this software. Launching a terminal application
     * which reads radon data from predefined CSV-files for development purposes
     * only. If you read this and you are looking for the final OMSimulation
     * tool with GUI, look out for newer releases.
     * 
     * @param args
     *            An array of random strings.
     * @throws IOException
     *             If creating log file or writing logs fails.
     */
    public static void main(String[] args) throws IOException {
        double start = System.currentTimeMillis();
        setLogOutput();
        LogOutput.write(timestamp() + "OM-Simulation tool started.");
        LogOutput.newLine();
        String csvFile = "object8_2008.csv";
        String[] csv;
        try {
            csv = parseCSV(csvFile);
        } catch (Exception e) {
            csv = new String[0];
            LogOutput.write(timestamp() + "Error: " + e.getMessage());
            LogOutput.newLine();
            e.printStackTrace();
            LogOutput.write(timestamp() + "Error: Failed to read CSV-File.");
            LogOutput.newLine();
        }
        OMRoom[] rooms = parseRooms(csv);
        seperateRooms(rooms);
        generateVariations();
        int maxCampaigns = 0;
        generateCampaigns(maxCampaigns);
        double total = (System.currentTimeMillis() - start) / 1000 / 60;
        LogOutput.write(timestamp() + "OM-Simulation tool finished after "
                + total + " minutes.");
        LogOutput.newLine();
        LogOutput.close();
    }

    /**
     * Method for reading and parsing an CSV-file line by line.
     * 
     * @param fileName
     *            The name (and optional: path) of the CSV-file to read.
     * @return An array of strings, each string representing one line.
     * @throws IOException
     *             If creating log file or writing logs fails.
     */
    private static String[] parseCSV(String fileName) throws IOException {
        String[] tmpArray = new String[65536];
        String[] csv = null;
        LogOutput.write(timestamp() + "Trying to read CSV-File '" + fileName
                + "'.");
        LogOutput.newLine();
        try {
            FileInputStream fileInput = new FileInputStream(fileName);
            DataInputStream dataInput = new DataInputStream(fileInput);
            InputStreamReader inputReader = new InputStreamReader(dataInput);
            BufferedReader buffReader = new BufferedReader(inputReader);
            LogOutput.write(timestamp()
                    + "Read CSV-File with success, trying to "
                    + "parse line by line:");
            LogOutput.newLine();
            int valueCount = 0;
            while ((tmpArray[valueCount] = buffReader.readLine()) != null) {
                valueCount++;
            }
            csv = new String[valueCount];
            for (int x = 0; x < valueCount; x++) {
                csv[x] = tmpArray[x];
                LogOutput.write(timestamp() + csv[x]);
                LogOutput.newLine();
            }
            valueCount--;
            setValueCount(valueCount);
            LogOutput.write(timestamp() + "Parsed " + valueCount
                    + " lines with success.");
            LogOutput.newLine();
            buffReader.close();
            inputReader.close();
            dataInput.close();
            fileInput.close();
        } catch (Exception e) {
            csv = new String[0];
            LogOutput.write(timestamp() + "Error: " + e.getMessage());
            LogOutput.newLine();
            e.printStackTrace();
            LogOutput.write(timestamp() + "Error: Failed to read CSV-File"
                    + fileName + ".");
            LogOutput.newLine();
        }
        tmpArray = null;
        return csv;
    }

    /**
     * Method for parsing rooms from the CSV-file by extracting each unique room
     * ID and any related radon values for each room.
     * 
     * @param csv
     *            An array of strings, each string representing one line of the
     *            previously parsed CSV-file.
     * @return An array consisting of all rooms of the building.
     * @throws IOException
     *             If creating log file or writing logs fails.
     */
    private static OMRoom[] parseRooms(String[] csv) throws IOException {
        LogOutput.write(timestamp() + "Trying to parse rooms:");
        LogOutput.newLine();
        OMRoom[] rooms;
        try {
            if (csv.length > 1) {
                String header = csv[0];
                String[] roomId = header.split("\\;");
                int roomCount = roomId.length;
                for (int i = 1; i < roomCount; i++) {
                    LogOutput.write(timestamp() + "Found room: " + roomId[i]);
                    LogOutput.newLine();
                }
                roomCount--;
                setRoomCount(roomCount);
                LogOutput.write(timestamp() + "Parsed " + roomCount
                        + " rooms with success.");
                LogOutput.newLine();
                rooms = new OMRoom[roomCount];
                int valueCount = getValueCount();
                double[] values[] = new double[roomCount][valueCount];
                LogOutput.write(timestamp()
                        + "Trying to collect values for each room:");
                LogOutput.newLine();
                for (int i = 1; i < csv.length; i++) {
                    String[] tmpValues = csv[i].split("\\;");
                    for (int j = 1; j < tmpValues.length; j++) {
                        int x = j - 1;
                        int y = i - 1;
                        values[x][y] = (double) Integer.parseInt(tmpValues[j]);
                        LogOutput.write(timestamp() + "Parsed value " + i
                                + " for room " + roomId[j] + ": "
                                + tmpValues[j]);
                        LogOutput.newLine();
                    }
                }
                for (int k = 1; k <= roomCount; k++) {
                    int z = k - 1;
                    rooms[z] = new OMRoom(roomId[k], values[z]);
                }
                LogOutput.write(timestamp()
                        + "Successfully collected values for each room.");
                LogOutput.newLine();
            } else {
                rooms = new OMRoom[0];
                LogOutput.write(timestamp() + "Error: No data records found.");
                LogOutput.newLine();
                LogOutput.write(timestamp() + "Error: Check your CSV-File.");
                LogOutput.newLine();
            }
        } catch (Exception e) {
            rooms = new OMRoom[0];
            LogOutput.write(timestamp() + "Error: " + e.getMessage());
            LogOutput.newLine();
            e.printStackTrace();
            LogOutput.write(timestamp() + "Error: Failed to parse rooms.");
            LogOutput.newLine();
        }
        return rooms;
    }

    /**
     * Method to separate an array of various rooms by their different types and
     * to trigger setters for the attributes Cellars, Rooms and Miscs which can
     * be used by the class after calling this method.
     * 
     * @param rooms
     *            An array consisting of all rooms of the building.
     * @throws IOException
     *             If creating log file or writing logs fails.
     */
    private static void seperateRooms(OMRoom[] rooms) throws IOException {
        LogOutput.write(timestamp() + "Trying to seperate rooms.");
        LogOutput.newLine();
        int cellarCount = 0;
        int normalCount = 0;
        int miscCount = 0;
        int roomCount = getRoomCount();
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
        LogOutput.write(timestamp() + "Found " + normalCount
                + " normal rooms, " + cellarCount + " cellars and " + miscCount
                + " miscellaneous rooms.");
        LogOutput.newLine();
        if (roomCount == cellarCount + normalCount + miscCount) {
            LogOutput.write(timestamp()
                    + "Seperation test succeeded, seperating rooms.");
            LogOutput.newLine();
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
            LogOutput.write(timestamp() + "Setting up rooms by types.");
            LogOutput.newLine();
            setCellars(cellars);
            setRooms(normals);
            setMiscs(miscs);
            LogOutput.write(timestamp() + "Done. Finished setting up rooms.");
            LogOutput.newLine();
        } else {
            LogOutput.write(timestamp() + "Seperation test failed.");
            LogOutput.newLine();
        }
    }

    /**
     * Method to generate all possible variations of any room and cellar, always
     * following the protocoll "6+1", using only one cellar at any position and
     * six normal rooms at any other possible position. Those "patterns" are
     * used to generate campaigns later on. Read more about how these patterns
     * are generated at the handbook.
     * 
     * @throws IOException
     *             If creating log file or writing logs fails.
     */
    private static void generateVariations() throws IOException {
        LogOutput.write(timestamp() + "Generating all possible variations.");
        LogOutput.newLine();
        OMRoom[] rooms = getRooms();
        int roomCount = rooms.length;
        int tmpArraySize = 1048576;
        OMRoom[] tmpScheme[];
        OMRoom[] finalScheme[];
        OMRoom[] cellars = getCellars();
        int cellarCount = cellars.length;
        if (roomCount >= 6) {
            tmpArraySize = (factorial(roomCount) / factorial(roomCount - 6));
            LogOutput.write(timestamp() + "Creating " + tmpArraySize
                    + " variations for " + roomCount + " normal rooms:");
            LogOutput.newLine();
            tmpScheme = new OMRoom[tmpArraySize][6];
            int i = 0;
            int n = 0;
            for (int a = 0; a < roomCount; a++) {
                for (int b = 0; b < roomCount; b++) {
                    for (int c = 0; c < roomCount; c++) {
                        for (int d = 0; d < roomCount; d++) {
                            for (int e = 0; e < roomCount; e++) {
                                for (int f = 0; f < roomCount; f++) {
                                    if (i < tmpArraySize) {
                                        tmpScheme[i][0] = rooms[a];
                                        if (rooms[a] != rooms[b]) {
                                            tmpScheme[i][1] = rooms[b];
                                            if (rooms[a] != rooms[c]

                                            && rooms[b] != rooms[c]) {
                                                tmpScheme[i][2] = rooms[c];
                                                if (rooms[a] != rooms[d]

                                                && rooms[b] != rooms[d]
                                                        && rooms[c] != rooms[d]) {
                                                    tmpScheme[i][3] = rooms[d];
                                                    if (rooms[a] != rooms[e]

                                                    && rooms[b] != rooms[e]

                                                    && rooms[c] != rooms[e]

                                                    && rooms[d] != rooms[e]) {
                                                        tmpScheme[i][4] = rooms[e];
                                                        if (rooms[a] != rooms[f]

                                                                && rooms[b] != rooms[f]

                                                                && rooms[c] != rooms[f]

                                                                && rooms[d] != rooms[f]

                                                                && rooms[e] != rooms[f]) {
                                                            tmpScheme[i][5] = rooms[f];
                                                            LogOutput
                                                                    .write(timestamp()
                                                                            + rooms[a]
                                                                            + ""
                                                                            + rooms[b]
                                                                            + rooms[c]
                                                                            + rooms[d]
                                                                            + rooms[e]
                                                                            + rooms[f]);
                                                            LogOutput.newLine();
                                                            i++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    n++;
                                }
                            }
                        }
                    }
                }
            }
            LogOutput.write(timestamp() + "Created " + i + " variations of "
                    + roomCount + " rooms within " + n + " iterations.");
            LogOutput.newLine();
            LogOutput.write(timestamp() + "Adding " + cellarCount
                    + " cellar(s) now at any possible position.");
            LogOutput.newLine();
            int finalArraySize = tmpArraySize * cellarCount * 7;
            finalScheme = new OMRoom[finalArraySize][7];
            LogOutput.write(timestamp() + "Calculating " + finalArraySize
                    + " variations for " + cellarCount + " cellar(s) and "
                    + roomCount + " rooms:");
            LogOutput.newLine();
            int x = 0;
            int y = 0;
            for (int g = 0; g < cellarCount; g++) {
                y = 0;
                while (y < tmpArraySize) {
                    finalScheme[x][0] = cellars[g];
                    finalScheme[x][1] = tmpScheme[y][0];
                    finalScheme[x][2] = tmpScheme[y][1];
                    finalScheme[x][3] = tmpScheme[y][2];
                    finalScheme[x][4] = tmpScheme[y][3];
                    finalScheme[x][5] = tmpScheme[y][4];
                    finalScheme[x][6] = tmpScheme[y][5];
                    LogOutput.write(timestamp() + "" + finalScheme[x][0]
                            + finalScheme[x][1] + finalScheme[x][2]
                            + finalScheme[x][3] + finalScheme[x][4]
                            + finalScheme[x][5] + finalScheme[x][6]);
                    LogOutput.newLine();
                    x++;
                    y++;
                }
                y = 0;
                while (y < tmpArraySize) {
                    finalScheme[x][0] = tmpScheme[y][0];
                    finalScheme[x][1] = cellars[g];
                    finalScheme[x][2] = tmpScheme[y][1];
                    finalScheme[x][3] = tmpScheme[y][2];
                    finalScheme[x][4] = tmpScheme[y][3];
                    finalScheme[x][5] = tmpScheme[y][4];
                    finalScheme[x][6] = tmpScheme[y][5];
                    LogOutput.write(timestamp() + "" + finalScheme[x][0]
                            + finalScheme[x][1] + finalScheme[x][2]
                            + finalScheme[x][3] + finalScheme[x][4]
                            + finalScheme[x][5] + finalScheme[x][6]);
                    LogOutput.newLine();
                    x++;
                    y++;
                }
                y = 0;
                while (y < tmpArraySize) {
                    finalScheme[x][0] = tmpScheme[y][0];
                    finalScheme[x][1] = tmpScheme[y][1];
                    finalScheme[x][2] = cellars[g];
                    finalScheme[x][3] = tmpScheme[y][2];
                    finalScheme[x][4] = tmpScheme[y][3];
                    finalScheme[x][5] = tmpScheme[y][4];
                    finalScheme[x][6] = tmpScheme[y][5];
                    LogOutput.write(timestamp() + "" + finalScheme[x][0]
                            + finalScheme[x][1] + finalScheme[x][2]
                            + finalScheme[x][3] + finalScheme[x][4]
                            + finalScheme[x][5] + finalScheme[x][6]);
                    LogOutput.newLine();
                    x++;
                    y++;
                }
                y = 0;
                while (y < tmpArraySize) {
                    finalScheme[x][0] = tmpScheme[y][0];
                    finalScheme[x][1] = tmpScheme[y][1];
                    finalScheme[x][2] = tmpScheme[y][2];
                    finalScheme[x][3] = cellars[g];
                    finalScheme[x][4] = tmpScheme[y][3];
                    finalScheme[x][5] = tmpScheme[y][4];
                    finalScheme[x][6] = tmpScheme[y][5];
                    LogOutput.write(timestamp() + "" + finalScheme[x][0]
                            + finalScheme[x][1] + finalScheme[x][2]
                            + finalScheme[x][3] + finalScheme[x][4]
                            + finalScheme[x][5] + finalScheme[x][6]);
                    LogOutput.newLine();
                    x++;
                    y++;
                }
                y = 0;
                while (y < tmpArraySize) {
                    finalScheme[x][0] = tmpScheme[y][0];
                    finalScheme[x][1] = tmpScheme[y][1];
                    finalScheme[x][2] = tmpScheme[y][2];
                    finalScheme[x][3] = tmpScheme[y][3];
                    finalScheme[x][4] = cellars[g];
                    finalScheme[x][5] = tmpScheme[y][4];
                    finalScheme[x][6] = tmpScheme[y][5];
                    LogOutput.write(timestamp() + "" + finalScheme[x][0]
                            + finalScheme[x][1] + finalScheme[x][2]
                            + finalScheme[x][3] + finalScheme[x][4]
                            + finalScheme[x][5] + finalScheme[x][6]);
                    LogOutput.newLine();
                    x++;
                    y++;
                }
                y = 0;
                while (y < tmpArraySize) {
                    finalScheme[x][0] = tmpScheme[y][0];
                    finalScheme[x][1] = tmpScheme[y][1];
                    finalScheme[x][2] = tmpScheme[y][2];
                    finalScheme[x][3] = tmpScheme[y][3];
                    finalScheme[x][4] = tmpScheme[y][4];
                    finalScheme[x][5] = cellars[g];
                    finalScheme[x][6] = tmpScheme[y][5];
                    LogOutput.write(timestamp() + "" + finalScheme[x][0]
                            + finalScheme[x][1] + finalScheme[x][2]
                            + finalScheme[x][3] + finalScheme[x][4]
                            + finalScheme[x][5] + finalScheme[x][6]);
                    LogOutput.newLine();
                    x++;
                    y++;
                }
                y = 0;
                while (y < tmpArraySize) {
                    finalScheme[x][0] = tmpScheme[y][0];
                    finalScheme[x][1] = tmpScheme[y][1];
                    finalScheme[x][2] = tmpScheme[y][2];
                    finalScheme[x][3] = tmpScheme[y][3];
                    finalScheme[x][4] = tmpScheme[y][4];
                    finalScheme[x][5] = tmpScheme[y][5];
                    finalScheme[x][6] = cellars[g];
                    LogOutput.write(timestamp() + "" + finalScheme[x][0]
                            + finalScheme[x][1] + finalScheme[x][2]
                            + finalScheme[x][3] + finalScheme[x][4]
                            + finalScheme[x][5] + finalScheme[x][6]);
                    LogOutput.newLine();
                    x++;
                    y++;
                }
            }
            LogOutput.write(timestamp() + "Created " + x + " variations for "
                    + cellarCount + " cellar(s) and " + roomCount + " rooms.");
            LogOutput.newLine();
            setVariationScheme(finalScheme);
        } else {
            if (roomCount == 5) {
                tmpArraySize = factorial(5);
                tmpScheme = new OMRoom[tmpArraySize][5];
                int i = 0;
                int n = 0;
                LogOutput.write(timestamp() + "Creating " + tmpArraySize
                        + " variations for " + roomCount + " normal rooms:");
                LogOutput.newLine();
                for (int a = 0; a < roomCount; a++) {
                    for (int b = 0; b < roomCount; b++) {
                        for (int c = 0; c < roomCount; c++) {
                            for (int d = 0; d < roomCount; d++) {
                                for (int e = 0; e < roomCount; e++) {
                                    if (i < tmpArraySize) {
                                        tmpScheme[i][0] = rooms[a];
                                        if (rooms[a] != rooms[b]) {
                                            tmpScheme[i][1] = rooms[b];
                                            if (rooms[a] != rooms[c]

                                            && rooms[b] != rooms[c]) {
                                                tmpScheme[i][2] = rooms[c];
                                                if (rooms[a] != rooms[d]

                                                && rooms[b] != rooms[d]
                                                        && rooms[c] != rooms[d]) {
                                                    tmpScheme[i][3] = rooms[d];
                                                    if (rooms[a] != rooms[e]

                                                    && rooms[b] != rooms[e]

                                                    && rooms[c] != rooms[e]

                                                    && rooms[d] != rooms[e]) {
                                                        tmpScheme[i][4] = rooms[e];
                                                        LogOutput
                                                                .write(timestamp()
                                                                        + rooms[a]
                                                                        + ""
                                                                        + rooms[b]
                                                                        + rooms[c]
                                                                        + rooms[d]
                                                                        + rooms[e]);
                                                        LogOutput.newLine();
                                                        i++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    n++;
                                }
                            }
                        }
                    }
                }
                LogOutput.write(timestamp() + "Created " + i
                        + " variations of " + roomCount + " rooms within " + n
                        + " iterations.");
                LogOutput.newLine();
                LogOutput.write(timestamp() + "Adding " + cellarCount
                        + " cellar(s) now at any possible position.");
                LogOutput.newLine();
                int finalArraySize = tmpArraySize * 5 * cellarCount * 6;
                finalScheme = new OMRoom[finalArraySize][7];
                LogOutput.write(timestamp() + "Calculating " + finalArraySize
                        + " variations for " + cellarCount + " cellar(s) and "
                        + roomCount + " rooms:");
                LogOutput.newLine();
                int x = 0;
                int y = 0;
                for (int a = 0; a < cellarCount; a++) {
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = cellars[a];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = tmpScheme[y][0];
                        finalScheme[x][3] = tmpScheme[y][1];
                        finalScheme[x][4] = tmpScheme[y][2];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = cellars[a];
                        finalScheme[x][3] = tmpScheme[y][1];
                        finalScheme[x][4] = tmpScheme[y][2];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = cellars[a];
                        finalScheme[x][4] = tmpScheme[y][2];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = cellars[a];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = cellars[a];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = cellars[a];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = cellars[a];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][1];
                        finalScheme[x][4] = tmpScheme[y][2];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = cellars[a];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][1];
                        finalScheme[x][4] = tmpScheme[y][2];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = cellars[a];
                        finalScheme[x][4] = tmpScheme[y][2];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = cellars[a];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = cellars[a];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = cellars[a];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = cellars[a];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][2];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = cellars[a];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][2];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = cellars[a];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][2];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][2];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = cellars[a];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][2];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = cellars[a];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][2];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = cellars[a];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = cellars[a];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = cellars[a];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = cellars[a];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][2];
                        finalScheme[x][3] = cellars[a];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][3];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][2];
                        finalScheme[x][3] = tmpScheme[y][3];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = cellars[a];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][2];
                        finalScheme[x][3] = tmpScheme[y][3];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = cellars[a];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = cellars[a];
                        finalScheme[x][1] = tmpScheme[y][0];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = cellars[a];
                        finalScheme[x][2] = tmpScheme[y][1];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = cellars[a];
                        finalScheme[x][3] = tmpScheme[y][2];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][2];
                        finalScheme[x][3] = cellars[a];
                        finalScheme[x][4] = tmpScheme[y][3];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][2];
                        finalScheme[x][3] = tmpScheme[y][3];
                        finalScheme[x][4] = cellars[a];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = tmpScheme[y][4];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                    y = 0;
                    while (y < tmpArraySize) {
                        finalScheme[x][0] = tmpScheme[y][0];
                        finalScheme[x][1] = tmpScheme[y][1];
                        finalScheme[x][2] = tmpScheme[y][2];
                        finalScheme[x][3] = tmpScheme[y][3];
                        finalScheme[x][4] = tmpScheme[y][4];
                        finalScheme[x][5] = tmpScheme[y][4];
                        finalScheme[x][6] = cellars[a];
                        LogOutput.write(timestamp() + "" + finalScheme[x][0]
                                + finalScheme[x][1] + finalScheme[x][2]
                                + finalScheme[x][3] + finalScheme[x][4]
                                + finalScheme[x][5] + finalScheme[x][6]);
                        LogOutput.newLine();
                        x++;
                        y++;
                    }
                }
                LogOutput.write(timestamp() + "Created " + x
                        + " variations for " + cellarCount + " cellar(s) and "
                        + roomCount + " rooms.");
                LogOutput.newLine();
                setVariationScheme(finalScheme);
            } else {
                if (roomCount == 4) {
                    tmpArraySize = factorial(4);
                    tmpScheme = new OMRoom[tmpArraySize][4];
                    int i = 0;
                    int n = 0;
                    LogOutput
                            .write(timestamp() + "Creating " + tmpArraySize
                                    + " variations for " + roomCount
                                    + " normal rooms:");
                    LogOutput.newLine();
                    for (int a = 0; a < roomCount; a++) {
                        for (int b = 0; b < roomCount; b++) {
                            for (int c = 0; c < roomCount; c++) {
                                for (int d = 0; d < roomCount; d++) {
                                    if (i < tmpArraySize) {
                                        tmpScheme[i][0] = rooms[a];
                                        if (rooms[a] != rooms[b]) {
                                            tmpScheme[i][1] = rooms[b];
                                            if (rooms[a] != rooms[c]
                                                    && rooms[b] != rooms[c]) {
                                                tmpScheme[i][2] = rooms[c];
                                                if (rooms[a] != rooms[d]
                                                        && rooms[b] != rooms[d]
                                                        && rooms[c] != rooms[d]) {
                                                    tmpScheme[i][3] = rooms[d];
                                                    LogOutput.write(timestamp()
                                                            + rooms[a] + ""
                                                            + rooms[b]
                                                            + rooms[c]
                                                            + rooms[d]);
                                                    LogOutput.newLine();
                                                    i++;
                                                }
                                            }
                                        }
                                    }
                                    n++;

                                }
                            }
                        }
                    }
                    LogOutput.write(timestamp() + "Created " + i
                            + " variations of " + roomCount + " rooms within "
                            + n + " iterations.");
                    LogOutput.newLine();
                    LogOutput.write(timestamp() + "Adding " + cellarCount
                            + " cellar(s) now at any possible position.");
                    LogOutput.newLine();
                    int finalArraySize = tmpArraySize * 6 * cellarCount * 5;
                    finalScheme = new OMRoom[finalArraySize][7];
                    LogOutput.write(timestamp() + "Calculating "
                            + finalArraySize + " variations for " + cellarCount
                            + " cellar(s) and " + roomCount + " rooms:");
                    LogOutput.newLine();
                    int x = 0;
                    int y = 0;
                    for (int a = 0; a < cellarCount; a++) {
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = cellars[a];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][0];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][1];
                            finalScheme[x][5] = tmpScheme[y][2];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = cellars[a];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][1];
                            finalScheme[x][5] = tmpScheme[y][2];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = cellars[a];
                            finalScheme[x][5] = tmpScheme[y][2];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = cellars[a];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = cellars[a];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = cellars[a];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][0];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][2];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = cellars[a];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][2];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = cellars[a];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][2];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = cellars[a];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = cellars[a];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = cellars[a];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][0];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = cellars[a];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = cellars[a];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = cellars[a];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][3];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = cellars[a];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = cellars[a];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][2];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = cellars[a];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][2];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][1];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = cellars[a];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][2];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][1];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = cellars[a];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][1];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = cellars[a];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = cellars[a];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = cellars[a];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][1];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][1];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = cellars[a];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][1];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = cellars[a];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][1];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][3];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = cellars[a];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = cellars[a];
                            finalScheme[x][1] = tmpScheme[y][0];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = cellars[a];
                            finalScheme[x][2] = tmpScheme[y][1];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][1];
                            finalScheme[x][2] = cellars[a];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][2];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][1];
                            finalScheme[x][2] = tmpScheme[y][2];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = cellars[a];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = tmpScheme[y][3];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                        y = 0;
                        while (y < tmpArraySize) {
                            finalScheme[x][0] = tmpScheme[y][0];
                            finalScheme[x][1] = tmpScheme[y][1];
                            finalScheme[x][2] = tmpScheme[y][2];
                            finalScheme[x][3] = tmpScheme[y][2];
                            finalScheme[x][4] = tmpScheme[y][3];
                            finalScheme[x][5] = tmpScheme[y][3];
                            finalScheme[x][6] = cellars[a];
                            LogOutput.write(timestamp() + ""
                                    + finalScheme[x][0] + finalScheme[x][1]
                                    + finalScheme[x][2] + finalScheme[x][3]
                                    + finalScheme[x][4] + finalScheme[x][5]
                                    + finalScheme[x][6]);
                            LogOutput.newLine();
                            x++;
                            y++;
                        }
                    }
                    LogOutput.write(timestamp() + "Created " + x
                            + " variations for " + cellarCount
                            + " cellar(s) and " + roomCount + " rooms.");
                    LogOutput.newLine();
                    setVariationScheme(finalScheme);
                } else {
                    if (roomCount == 3) {
                        tmpArraySize = factorial(3);
                        LogOutput.write(timestamp() + "Creating "
                                + tmpArraySize + " variations for " + roomCount
                                + " normal rooms.");
                        LogOutput.newLine();
                        tmpScheme = new OMRoom[tmpArraySize][6];
                        int i = 0;
                        int j = 1;
                        int k = 2;
                        int n = 0;
                        for (int a = 0; a < roomCount * 2; a++) {
                            if (n == 1) {
                                i = 0;
                                j = 2;
                                k = 1;
                            }
                            if (n == 2) {
                                i = 1;
                                j = 0;
                                k = 2;
                            }
                            if (n == 3) {
                                i = 1;
                                j = 2;
                                k = 0;
                            }
                            if (n == 4) {
                                i = 2;
                                j = 0;
                                k = 1;
                            }
                            if (n == 5) {
                                i = 2;
                                j = 1;
                                k = 0;
                            }
                            tmpScheme[a][0] = rooms[i];
                            tmpScheme[a][1] = rooms[i];
                            tmpScheme[a][2] = rooms[j];
                            tmpScheme[a][3] = rooms[j];
                            tmpScheme[a][4] = rooms[k];
                            tmpScheme[a][5] = rooms[k];
                            n++;
                        }
                        LogOutput.write(timestamp() + "Created " + n
                                + " variations of " + roomCount
                                + " rooms within " + n + " iterations.");
                        LogOutput.newLine();
                        LogOutput.write(timestamp() + "Adding " + cellarCount
                                + " cellar(s) now at any possible position.");
                        LogOutput.newLine();
                        int finalArraySize = tmpArraySize * cellarCount * 4;
                        finalScheme = new OMRoom[finalArraySize][7];
                        LogOutput.write(timestamp() + "Calculating "
                                + finalArraySize + " variations for "
                                + cellarCount + " cellar(s) and " + roomCount
                                + " rooms:");
                        LogOutput.newLine();
                        int x = 0;
                        int y = 0;
                        for (int a = 0; a < cellarCount; a++) {
                            y = 0;
                            while (y < tmpArraySize) {
                                finalScheme[x][0] = cellars[a];
                                finalScheme[x][1] = tmpScheme[y][0];
                                finalScheme[x][2] = tmpScheme[y][1];
                                finalScheme[x][3] = tmpScheme[y][2];
                                finalScheme[x][4] = tmpScheme[y][3];
                                finalScheme[x][5] = tmpScheme[y][4];
                                finalScheme[x][6] = tmpScheme[y][5];
                                LogOutput.write(timestamp() + ""
                                        + finalScheme[x][0] + finalScheme[x][1]
                                        + finalScheme[x][2] + finalScheme[x][3]
                                        + finalScheme[x][4] + finalScheme[x][5]
                                        + finalScheme[x][6]);
                                LogOutput.newLine();
                                x++;
                                y++;
                            }
                            y = 0;
                            while (y < tmpArraySize) {
                                finalScheme[x][0] = tmpScheme[y][0];
                                finalScheme[x][1] = tmpScheme[y][1];
                                finalScheme[x][2] = cellars[a];
                                finalScheme[x][3] = tmpScheme[y][2];
                                finalScheme[x][4] = tmpScheme[y][3];
                                finalScheme[x][5] = tmpScheme[y][4];
                                finalScheme[x][6] = tmpScheme[y][5];
                                LogOutput.write(timestamp() + ""
                                        + finalScheme[x][0] + finalScheme[x][1]
                                        + finalScheme[x][2] + finalScheme[x][3]
                                        + finalScheme[x][4] + finalScheme[x][5]
                                        + finalScheme[x][6]);
                                LogOutput.newLine();
                                x++;
                                y++;
                            }
                            y = 0;
                            while (y < tmpArraySize) {
                                finalScheme[x][0] = tmpScheme[y][0];
                                finalScheme[x][1] = tmpScheme[y][1];
                                finalScheme[x][2] = tmpScheme[y][2];
                                finalScheme[x][3] = tmpScheme[y][3];
                                finalScheme[x][4] = cellars[a];
                                finalScheme[x][5] = tmpScheme[y][4];
                                finalScheme[x][6] = tmpScheme[y][5];
                                LogOutput.write(timestamp() + ""
                                        + finalScheme[x][0] + finalScheme[x][1]
                                        + finalScheme[x][2] + finalScheme[x][3]
                                        + finalScheme[x][4] + finalScheme[x][5]
                                        + finalScheme[x][6]);
                                LogOutput.newLine();
                                x++;
                                y++;
                            }
                            y = 0;
                            while (y < tmpArraySize) {
                                finalScheme[x][0] = tmpScheme[y][0];
                                finalScheme[x][1] = tmpScheme[y][1];
                                finalScheme[x][2] = tmpScheme[y][2];
                                finalScheme[x][3] = tmpScheme[y][3];
                                finalScheme[x][4] = tmpScheme[y][4];
                                finalScheme[x][5] = tmpScheme[y][5];
                                finalScheme[x][6] = cellars[a];
                                LogOutput.write(timestamp() + ""
                                        + finalScheme[x][0] + finalScheme[x][1]
                                        + finalScheme[x][2] + finalScheme[x][3]
                                        + finalScheme[x][4] + finalScheme[x][5]
                                        + finalScheme[x][6]);
                                LogOutput.newLine();
                                x++;
                                y++;
                            }
                        }
                        LogOutput.write(timestamp() + "Created " + x
                                + " variations for " + cellarCount
                                + " cellar(s) and " + roomCount + " rooms.");
                        LogOutput.newLine();
                        setVariationScheme(finalScheme);
                    } else {
                        if (roomCount == 2) {
                            tmpArraySize = factorial(2);
                            LogOutput.write(timestamp() + "Creating "
                                    + tmpArraySize + " variations for "
                                    + roomCount + " normal rooms.");
                            LogOutput.newLine();
                            tmpScheme = new OMRoom[tmpArraySize][6];
                            tmpScheme[0][0] = rooms[0];
                            tmpScheme[0][1] = rooms[0];
                            tmpScheme[0][2] = rooms[0];
                            tmpScheme[0][3] = rooms[1];
                            tmpScheme[0][4] = rooms[1];
                            tmpScheme[0][5] = rooms[1];
                            tmpScheme[1][0] = rooms[1];
                            tmpScheme[1][1] = rooms[1];
                            tmpScheme[1][2] = rooms[1];
                            tmpScheme[1][3] = rooms[0];
                            tmpScheme[1][4] = rooms[0];
                            tmpScheme[1][5] = rooms[0];
                            LogOutput.write(timestamp()
                                    + "Created 2 variations of " + roomCount
                                    + " rooms without any iterations.");
                            LogOutput.newLine();
                            LogOutput
                                    .write(timestamp()
                                            + "Adding "
                                            + cellarCount
                                            + " cellar(s) now at any possible position.");
                            LogOutput.newLine();
                            int finalArraySize = tmpArraySize * cellarCount * 3;
                            finalScheme = new OMRoom[finalArraySize][7];
                            LogOutput.write(timestamp() + "Calculating "
                                    + finalArraySize + " variations for "
                                    + cellarCount + " cellar(s) and "
                                    + roomCount + " rooms:");
                            LogOutput.newLine();
                            int x = 0;
                            int y = 0;
                            for (int a = 0; a < cellarCount; a++) {
                                y = 0;
                                while (y < tmpArraySize) {
                                    finalScheme[x][0] = cellars[a];
                                    finalScheme[x][1] = tmpScheme[y][0];
                                    finalScheme[x][2] = tmpScheme[y][1];
                                    finalScheme[x][3] = tmpScheme[y][2];
                                    finalScheme[x][4] = tmpScheme[y][3];
                                    finalScheme[x][5] = tmpScheme[y][4];
                                    finalScheme[x][6] = tmpScheme[y][5];
                                    LogOutput.write(timestamp() + ""
                                            + finalScheme[x][0]
                                            + finalScheme[x][1]
                                            + finalScheme[x][2]
                                            + finalScheme[x][3]
                                            + finalScheme[x][4]
                                            + finalScheme[x][5]
                                            + finalScheme[x][6]);
                                    LogOutput.newLine();
                                    x++;
                                    y++;
                                }
                                y = 0;
                                while (y < tmpArraySize) {
                                    finalScheme[x][0] = tmpScheme[y][0];
                                    finalScheme[x][1] = tmpScheme[y][1];
                                    finalScheme[x][2] = tmpScheme[y][2];
                                    finalScheme[x][3] = cellars[a];
                                    finalScheme[x][4] = tmpScheme[y][3];
                                    finalScheme[x][5] = tmpScheme[y][4];
                                    finalScheme[x][6] = tmpScheme[y][5];
                                    LogOutput.write(timestamp() + ""
                                            + finalScheme[x][0]
                                            + finalScheme[x][1]
                                            + finalScheme[x][2]
                                            + finalScheme[x][3]
                                            + finalScheme[x][4]
                                            + finalScheme[x][5]
                                            + finalScheme[x][6]);
                                    LogOutput.newLine();
                                    x++;
                                    y++;
                                }
                                y = 0;
                                while (y < tmpArraySize) {
                                    finalScheme[x][0] = tmpScheme[y][0];
                                    finalScheme[x][1] = tmpScheme[y][1];
                                    finalScheme[x][2] = tmpScheme[y][2];
                                    finalScheme[x][3] = tmpScheme[y][3];
                                    finalScheme[x][4] = tmpScheme[y][4];
                                    finalScheme[x][5] = tmpScheme[y][5];
                                    finalScheme[x][6] = cellars[a];
                                    LogOutput.write(timestamp() + ""
                                            + finalScheme[x][0]
                                            + finalScheme[x][1]
                                            + finalScheme[x][2]
                                            + finalScheme[x][3]
                                            + finalScheme[x][4]
                                            + finalScheme[x][5]
                                            + finalScheme[x][6]);
                                    LogOutput.newLine();
                                    x++;
                                    y++;
                                }
                            }
                            LogOutput.write(timestamp() + "Created " + x
                                    + " variations for " + cellarCount
                                    + " cellar(s) and " + +roomCount
                                    + " rooms.");
                            LogOutput.newLine();
                            setVariationScheme(finalScheme);
                        } else {
                            try {
                                if (roomCount == 1) {
                                    tmpArraySize = factorial(1);
                                    LogOutput.write(timestamp() + "Creating "
                                            + tmpArraySize
                                            + " variation(s) for " + roomCount
                                            + " normal room(s).");
                                    LogOutput.newLine();
                                    tmpScheme = new OMRoom[1][6];
                                    for (int i = 0; i < 6; i++) {
                                        tmpScheme[0][i] = rooms[0];
                                    }
                                    LogOutput
                                            .write(timestamp()
                                                    + "Created "
                                                    + tmpArraySize
                                                    + " variation(s) of "
                                                    + roomCount
                                                    + " room(s) without any iterations.");
                                    LogOutput.newLine();
                                    LogOutput
                                            .write(timestamp()
                                                    + "Adding "
                                                    + cellarCount
                                                    + " cellar(s) now at any possible position.");
                                    LogOutput.newLine();
                                    int finalArraySize = tmpArraySize
                                            * cellarCount * 2;
                                    finalScheme = new OMRoom[finalArraySize][7];
                                    LogOutput.write(timestamp()
                                            + "Calculating " + finalArraySize
                                            + " variation(s) for "
                                            + cellarCount + " cellar(s) and "
                                            + roomCount + " room(s):");
                                    LogOutput.newLine();
                                    int x = 0;
                                    int y = 0;
                                    for (int a = 0; a < cellarCount; a++) {
                                        y = 0;
                                        while (y < tmpArraySize) {
                                            finalScheme[x][0] = cellars[a];
                                            finalScheme[x][1] = tmpScheme[y][0];
                                            finalScheme[x][2] = tmpScheme[y][1];
                                            finalScheme[x][3] = tmpScheme[y][2];
                                            finalScheme[x][4] = tmpScheme[y][3];
                                            finalScheme[x][5] = tmpScheme[y][4];
                                            finalScheme[x][6] = tmpScheme[y][5];
                                            LogOutput.write(timestamp() + ""
                                                    + finalScheme[x][0]
                                                    + finalScheme[x][1]
                                                    + finalScheme[x][2]
                                                    + finalScheme[x][3]
                                                    + finalScheme[x][4]
                                                    + finalScheme[x][5]
                                                    + finalScheme[x][6]);
                                            LogOutput.newLine();
                                            x++;
                                            y++;
                                        }
                                        y = 0;
                                        while (y < tmpArraySize) {
                                            finalScheme[x][0] = tmpScheme[y][0];
                                            finalScheme[x][1] = tmpScheme[y][1];
                                            finalScheme[x][2] = tmpScheme[y][2];
                                            finalScheme[x][3] = tmpScheme[y][3];
                                            finalScheme[x][4] = tmpScheme[y][4];
                                            finalScheme[x][5] = tmpScheme[y][5];
                                            finalScheme[x][6] = cellars[a];
                                            LogOutput.write(timestamp() + ""
                                                    + finalScheme[x][0]
                                                    + finalScheme[x][1]
                                                    + finalScheme[x][2]
                                                    + finalScheme[x][3]
                                                    + finalScheme[x][4]
                                                    + finalScheme[x][5]
                                                    + finalScheme[x][6]);
                                            LogOutput.newLine();
                                            x++;
                                            y++;
                                        }
                                    }
                                    LogOutput.write(timestamp() + "Created "
                                            + x + " variation(s) for "
                                            + cellarCount + " cellar(s) and "
                                            + +roomCount + " room(s).");
                                    LogOutput.newLine();
                                    setVariationScheme(finalScheme);
                                } else {
                                    LogOutput
                                            .write(timestamp()
                                                    + "Error: No rooms found. Check your CSV-File.");
                                    LogOutput.newLine();
                                    LogOutput
                                            .write(timestamp()
                                                    + "Error: Failed to generate variations.");
                                    LogOutput.newLine();
                                }
                            } catch (Exception e) {
                                LogOutput.write(timestamp() + "Error: "
                                        + e.getMessage());
                                LogOutput.newLine();
                                e.printStackTrace();
                                LogOutput
                                        .write(timestamp()
                                                + "Error: Failed to generate variations.");
                                LogOutput.newLine();
                            }
                        }
                    }

                }

            }

        }
    }

    /**
     * Method to generate and simulate survey campaigns. If no maximum value is
     * specified (max = 0), it will simply simulate any possible campaign. This
     * can take a while, grab a coffee.
     * 
     * @param max
     *            The maximum value of campaigns to simulate. If set to 0, no
     *            limits are set and any possible campaign will be simulated.
     * @throws IOException
     *             If creating log file or writing logs fails.
     */
    private static void generateCampaigns(int max) throws IOException {
        int valueCount = getValueCount();
        int total = valueCount - 7 * 24;
        if (total >= 1) {
            LogOutput.write(timestamp() + valueCount + " data records allow "
                    + total + " possible times for starting a simulation.");
            LogOutput.newLine();
            OMRoom[] campaignRooms[] = getVariationScheme();
            int x = 0;
            int campaignLength = campaignRooms.length;
            int perc = 0;
            OMCampaign campaign;
            double[] arithMeans;
            if (max > 0) {
                arithMeans = new double[max];
                LogOutput.write(timestamp() + "Starting simulation with " + max
                        + " random variations.");
                LogOutput.newLine();
                Random generator = new Random();
                int[] random = new int[max];
                for (int n = 0; n < max; n++) {
                    random[n] = generator.nextInt(campaignLength - 1);
                }
                int start;
                int mod = 0;
                for (int a = 0; a < max; a++) {
                    mod = a % 100;
                    if (mod == 0) {
                        perc = (a * 100) / max;
                        LogOutput.write(timestamp() + "Status: " + perc + "%");
                        LogOutput.newLine();
                    }
                    start = generator.nextInt(total - 1);
                    campaign = new OMCampaign(start, campaignRooms[random[a]]);
                    arithMeans[x] = campaign.getAvarage();
                    LogOutput.write(timestamp() + campaign);
                    LogOutput.newLine();
                    x++;
                }
                LogOutput.write(timestamp() + "Status: 100% - finished.");
                LogOutput.newLine();
            } else {
                max = total * campaignLength;
                arithMeans = new double[max];
                LogOutput.write(timestamp() + "Starting unlimited simulation.");
                LogOutput.newLine();
                for (int a = 0; a < campaignLength; a++) {
                    perc = (a * 100) / campaignLength;
                    LogOutput.write(timestamp() + "Status: " + perc + "%");
                    LogOutput.newLine();
                    for (int start = 0; start < total; start++) {
                        campaign = new OMCampaign(start, campaignRooms[a]);
                        arithMeans[x] = campaign.getAvarage();
                        LogOutput.write(timestamp() + campaign);
                        LogOutput.newLine();
                        x++;
                    }
                }
                LogOutput.write(timestamp() + "Status: 100% - finished.");
                LogOutput.newLine();
            }
            LogOutput.write(timestamp() + "Generated " + x + " campaigns.");
            LogOutput.newLine();
            Arrays.sort(arithMeans);
            double arithMean = 0.0;
            for (int i = 0; i < x; i++) {
                arithMean = arithMean + arithMeans[i];
            }
            arithMean = arithMean / x;
            double deviation = 0;
            for (int i = 0; i < x; i++) {
                deviation = deviation + (arithMeans[i] - arithMean)
                        * (arithMeans[i] - arithMean);
            }
            deviation = deviation / x;
            deviation = Math.sqrt(deviation);
            double cv = deviation / arithMean;
            double m = ((double) x / 100.0) * 5.0;
            int n = (int) m - 1;
            double q05 = arithMeans[n];
            m = ((double) x / 100.0) * 95.0;
            n = (int) m - 1;
            double q95 = arithMeans[n];
            double range = arithMeans[x - 1] - arithMeans[0];
            LogOutput.write(timestamp() + "Final result: AM=" + arithMean
                    + ", SD=" + deviation + ", CV=" + cv + ", MN="
                    + arithMeans[0] + ", Q5=" + q05 + ", Q95=" + q95 + ", MX="
                    + arithMeans[x - 1] + ", RG=" + range);
            LogOutput.newLine();
        } else {
            LogOutput.write(timestamp() + "Error: " + valueCount
                    + " are not enough data records.");
            LogOutput.newLine();
            LogOutput
                    .write(timestamp()
                            + "Make sure you have at least one week of records (> 168).");
            LogOutput.newLine();
        }
    }

    /**
     * Calculates the factorial of n!.
     * 
     * @param n
     *            Integer value which is used to calculate its factorial n!.
     * @return The factorial n!.
     */
    private static int factorial(int n) {
        int f = 1;
        if (n > 1) {
            for (int i = 1; i <= n; i++) {
                f = f * i;
            }
        }
        return f;
    }

    /**
     * Gets the current timestamp using the format "dd.MM.yyyy HH:mm:ss,SSS ".
     * 
     * @return The current timestamp.
     */
    private static String timestamp() {
        Format format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss,SSS ");
        double current = System.currentTimeMillis();
        String timestamp = format.format(current);
        return timestamp;
    }
}
