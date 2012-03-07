package de.bfs.radon.omsimulation.data;

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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Public class OMBuilding used for creating building meta-objects with custom
 * name and alle the rooms and cellars used in simulations later.
 * 
 * @author A. Schoedon
 */
public class OMBuilding {

  /**
   * Stores a custom name for the object which is set by the user creating the
   * building.
   */
  private String Name;

  /**
   * Stores a custom start date which is set by the user creating the building.
   * The date can be choosen either to identify the start date of the
   * measurements or to identify the start date of the simulations. That's up to
   * the user and does not affect the simulations.
   */
  private Date Start;

  /**
   * Stores the total number of measurements for the building.
   */
  private int ValueCount;

  /**
   * Stores the total number of rooms for the building.
   */
  private int RoomCount;

  /**
   * Stores an array of all normal rooms of the building.
   */
  private OMRoom[] Rooms;

  /**
   * Stores an array of all cellar rooms of the building.
   */
  private OMRoom[] Cellars;

  /**
   * Stores an array of rooms-arrays representing all available variations using
   * six rooms. If there are not enough rooms, this array will be empty.
   */
  private OMRoom[][] VariationSchemeSix;

  /**
   * Stores an array of rooms-arrays representing all available variations using
   * five rooms. If there are not enough rooms, this array will be empty.
   */
  private OMRoom[][] VariationSchemeFive;

  /**
   * Stores an array of rooms-arrays representing all available variations using
   * four rooms. If there are not enough rooms, this array will be empty.
   */
  private OMRoom[][] VariationSchemeFour;

  /**
   * Stores an array of rooms-arrays representing all available variations using
   * three rooms.
   */
  private OMRoom[][] VariationSchemeThree;

  /**
   * Gets a custom name for the object which was set by the user creating the
   * building.
   * 
   * @return A custom name for the object.
   */
  public String getName() {
    return this.Name;
  }

  /**
   * Sets a custom name for the object for the building.
   * 
   * @param name
   *          A custom name for the object.
   */
  public void setName(String name) {
    this.Name = name;
  }

  /**
   * Gets a custom start date which was set by the user creating the building.
   * The date can be choosen either to identify the start date of the
   * measurements or to identify the start date of the simulations. That's up to
   * the user and does not affect the simulations.
   * 
   * @return A custom start date for the building.
   */
  public Date getStart() {
    return this.Start;
  }

  /**
   * Sets a custom start date for the building. The date can be choosen either
   * to identify the start date of the measurements or to identify the start
   * date of the simulations. That's up to the user and does not affect the
   * simulations.
   * 
   * @param start
   *          A custom start date for the building.
   */
  public void setStart(Date start) {
    this.Start = start;
  }

  /**
   * Gets the total number of measurements for the building.
   * 
   * @return The total number of measurements for the building.
   */
  public int getValueCount() {
    return this.ValueCount;
  }

  /**
   * Sets the total number of measurements for the building.
   * 
   * @param valueCount
   *          The total number of measurements for the building.
   */
  private void setValueCount(int valueCount) {
    this.ValueCount = valueCount;
  }

  /**
   * Gets the total number of rooms for the building.
   * 
   * @return The total number of rooms for the building.
   */
  public int getRoomCount() {
    return this.RoomCount;
  }

  /**
   * Sets the total number of rooms for the building.
   * 
   * @param roomCount
   *          The total number of rooms for the building.
   */
  private void setRoomCount(int roomCount) {
    this.RoomCount = roomCount;
  }

  /**
   * Gets an array of all normal rooms of the building.
   * 
   * @return An array of all normal rooms of the building.
   */
  public OMRoom[] getRooms() {
    return this.Rooms;
  }

  /**
   * Sets an array of all normal rooms of the building. Triggers re-generation
   * of all possible variations. Use with care as this can take a couple of
   * minutes.
   * 
   * @param rooms
   *          An array of all normal rooms of the building.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void setRooms(OMRoom[] rooms) throws IOException {
    this.Rooms = rooms;
    generateVariations();
  }

  /**
   * Gets an array of all cellar rooms of the building.
   * 
   * @return An array of all cellar rooms of the building.
   */
  public OMRoom[] getCellars() {
    return this.Cellars;
  }

  /**
   * Sets an array of all cellar rooms of the building. Triggers re-generation
   * of all possible variations. Use with care as this can take a couple of
   * minutes.
   * 
   * @param cellars
   *          An array of all cellar rooms of the building.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void setCellars(OMRoom[] cellars) throws IOException {
    this.Cellars = cellars;
    generateVariations();
  }

  /**
   * Gets an array of rooms-arrays representing all available variations using
   * six rooms. If there are not enough rooms, this array will be empty.
   * 
   * @return An array of rooms-arrays representing all available variations
   *         using six rooms.
   */
  public OMRoom[][] getVariationSchemeSix() {
    return this.VariationSchemeSix;
  }

  /**
   * Sets an array of rooms-arrays representing all available variations using
   * six rooms. If there are not enough rooms, this array should be empty.
   * 
   * @param variationScheme
   *          An array of rooms-arrays representing all available variations
   *          using six rooms.
   */
  private void setVariationSchemeSix(OMRoom[][] variationScheme) {
    this.VariationSchemeSix = variationScheme;
  }

  /**
   * Gets an array of rooms-arrays representing all available variations using
   * five rooms. If there are not enough rooms, this array will be empty.
   * 
   * @return An array of rooms-arrays representing all available variations
   *         using five rooms.
   */
  public OMRoom[][] getVariationSchemeFive() {
    return this.VariationSchemeFive;
  }

  /**
   * Sets an array of rooms-arrays representing all available variations using
   * five rooms. If there are not enough rooms, this array should be empty.
   * 
   * @param variationScheme
   *          An array of rooms-arrays representing all available variations
   *          using five rooms.
   */
  private void setVariationSchemeFive(OMRoom[][] variationScheme) {
    this.VariationSchemeFive = variationScheme;
  }

  /**
   * Gets an array of rooms-arrays representing all available variations using
   * four rooms. If there are not enough rooms, this array will be empty.
   * 
   * @return An array of rooms-arrays representing all available variations
   *         using four rooms.
   */
  public OMRoom[][] getVariationSchemeFour() {
    return this.VariationSchemeFour;
  }

  /**
   * Sets an array of rooms-arrays representing all available variations using
   * four rooms. If there are not enough rooms, this array should be empty.
   * 
   * @param variationScheme
   *          An array of rooms-arrays representing all available variations
   *          using four rooms.
   */
  private void setVariationSchemeFour(OMRoom[][] variationScheme) {
    this.VariationSchemeFour = variationScheme;
  }

  /**
   * Gets an array of rooms-arrays representing all available variations using
   * three rooms.
   * 
   * @return An array of rooms-arrays representing all available variations
   *         using three rooms.
   */
  public OMRoom[][] getVariationSchemeThree() {
    return this.VariationSchemeThree;
  }

  /**
   * Sets an array of rooms-arrays representing all available variations using
   * three rooms.
   * 
   * @param variationScheme
   *          An array of rooms-arrays representing all available variations
   *          using three rooms.
   */
  private void setVariationSchemeThree(OMRoom[][] variationScheme) {
    this.VariationSchemeThree = variationScheme;
  }

  /**
   * Empty constructor for creating building objects without any attributes.
   * Only use this to avoid exceptions while running into errors or if you are
   * forces to initialize the object first without the ability to set rooms or
   * other attributes. Empty building objects can not be used for simulations.
   */
  public OMBuilding() {
    this.Name = "";
    this.Start = new Date();
    this.RoomCount = 0;
    this.ValueCount = 0;
    this.Rooms = null;
    this.Cellars = null;
    this.VariationSchemeThree = new OMRoom[0][0];
    this.VariationSchemeFour = new OMRoom[0][0];
    this.VariationSchemeFive = new OMRoom[0][0];
    this.VariationSchemeSix = new OMRoom[0][0];
  }

  /**
   * Constructor for creating building objects to store their rooms. Using this
   * constructor will trigger the generation of all available variations.
   * 
   * @param name
   *          A custom name for the object which is set by the user creating the
   *          building.
   * @param start
   *          A custom start date for the building. The date can be choosen
   *          either to identify the start date of the measurements or to
   *          identify the start date of the simulations. That's up to the user
   *          and does not affect the simulations.
   * @param roomCount
   *          The total number of rooms for the building.
   * @param valueCount
   *          The total number of measurements for the building.
   * @param rooms
   *          An array of all normal rooms of the building.
   * @param cellars
   *          An array of all cellar rooms of the building.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public OMBuilding(String name, Date start, int roomCount, int valueCount,
      OMRoom[] rooms, OMRoom[] cellars) throws IOException {
    this.Name = name;
    if (start == null) {
      start = new Date();
    }
    this.Start = start;
    setRoomCount(roomCount);
    setValueCount(valueCount);
    this.Rooms = rooms;
    this.Cellars = cellars;
    this.VariationSchemeThree = new OMRoom[0][0];
    this.VariationSchemeFour = new OMRoom[0][0];
    this.VariationSchemeFive = new OMRoom[0][0];
    this.VariationSchemeSix = new OMRoom[0][0];
    generateVariations();
  }

  /**
   * Method to generate all possible variations of any room and cellar, always
   * following the protocoll "6+1", using only one cellar at any position and
   * three, four five or six normal rooms at any other possible position. Those
   * "patterns" are used to generate campaigns later on. Read more about how
   * these patterns are generated at the handbook.
   * 
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  private void generateVariations() throws IOException {
    OMHelper.writeLog("Generating all possible variations.");
    OMRoom[] rooms = this.Rooms;
    int roomCount = this.Rooms.length;
    int tmpArraySize = 1048576;
    OMRoom[] tmpScheme[] = new OMRoom[0][0];
    int i = 0;
    int n = 0;
    if (roomCount >= 3) {
      OMHelper.writeLog("Generating variations: 3 out of " + roomCount
          + " rooms.");
      tmpArraySize = (OMHelper.calculateFactorial(roomCount) / OMHelper
          .calculateFactorial(roomCount - 3));
      OMHelper.writeLog("Creating " + tmpArraySize + " variations for "
          + roomCount + " normal rooms:");
      tmpScheme = new OMRoom[tmpArraySize][6];
      i = 0;
      n = 0;
      for (int a = 0; a < roomCount; a++) {
        for (int b = 0; b < roomCount; b++) {
          for (int c = 0; c < roomCount; c++) {
            if (i < tmpArraySize) {
              tmpScheme[i][0] = rooms[a];
              tmpScheme[i][1] = rooms[a];
              if (rooms[a] != rooms[b]) {
                tmpScheme[i][2] = rooms[b];
                tmpScheme[i][3] = rooms[b];
                if (rooms[a] != rooms[c] && rooms[b] != rooms[c]) {
                  tmpScheme[i][4] = rooms[c];
                  tmpScheme[i][5] = rooms[c];
                  OMHelper.writeLog(rooms[a] + "" + rooms[b] + rooms[c]);
                  i++;
                }
              }
            }
            n++;
          }
        }
      }
      OMHelper.writeLog("Created " + i + " variations of " + roomCount
          + " rooms within " + n + " iterations.");
      generateVariationsThree(tmpScheme, tmpArraySize);
      if (roomCount >= 4) {
        OMHelper.writeLog("Generating variations: 4 out of " + roomCount
            + " rooms.");
        tmpArraySize = (OMHelper.calculateFactorial(roomCount) / OMHelper
            .calculateFactorial(roomCount - 4));
        OMHelper.writeLog("Creating " + tmpArraySize + " variations for "
            + roomCount + " normal rooms:");
        tmpScheme = new OMRoom[tmpArraySize][4];
        i = 0;
        n = 0;
        for (int a = 0; a < roomCount; a++) {
          for (int b = 0; b < roomCount; b++) {
            for (int c = 0; c < roomCount; c++) {
              for (int d = 0; d < roomCount; d++) {
                if (i < tmpArraySize) {
                  tmpScheme[i][0] = rooms[a];
                  if (rooms[a] != rooms[b]) {
                    tmpScheme[i][1] = rooms[b];
                    if (rooms[a] != rooms[c] && rooms[b] != rooms[c]) {
                      tmpScheme[i][2] = rooms[c];
                      if (rooms[a] != rooms[d] && rooms[b] != rooms[d]
                          && rooms[c] != rooms[d]) {
                        tmpScheme[i][3] = rooms[d];
                        OMHelper.writeLog(rooms[a] + "" + rooms[b] + rooms[c]
                            + rooms[d]);
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
        OMHelper.writeLog("Created " + i + " variations of " + roomCount
            + " rooms within " + n + " iterations.");
        generateVariationsFour(tmpScheme, tmpArraySize);
        if (roomCount >= 5) {
          OMHelper.writeLog("Generating variations: 5 out of " + roomCount
              + " rooms.");
          tmpArraySize = (OMHelper.calculateFactorial(roomCount) / OMHelper
              .calculateFactorial(roomCount - 5));
          OMHelper.writeLog("Creating " + tmpArraySize + " variations for "
              + roomCount + " normal rooms:");
          tmpScheme = new OMRoom[tmpArraySize][5];
          i = 0;
          n = 0;
          for (int a = 0; a < roomCount; a++) {
            for (int b = 0; b < roomCount; b++) {
              for (int c = 0; c < roomCount; c++) {
                for (int d = 0; d < roomCount; d++) {
                  for (int e = 0; e < roomCount; e++) {
                    if (i < tmpArraySize) {
                      tmpScheme[i][0] = rooms[a];
                      if (rooms[a] != rooms[b]) {
                        tmpScheme[i][1] = rooms[b];
                        if (rooms[a] != rooms[c] && rooms[b] != rooms[c]) {
                          tmpScheme[i][2] = rooms[c];
                          if (rooms[a] != rooms[d] && rooms[b] != rooms[d]
                              && rooms[c] != rooms[d]) {
                            tmpScheme[i][3] = rooms[d];
                            if (rooms[a] != rooms[e] && rooms[b] != rooms[e]
                                && rooms[c] != rooms[e] && rooms[d] != rooms[e]) {
                              tmpScheme[i][4] = rooms[e];
                              OMHelper.writeLog(rooms[a] + "" + rooms[b]
                                  + rooms[c] + rooms[d] + rooms[e]);
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
          OMHelper.writeLog("Created " + i + " variations of " + roomCount
              + " rooms within " + n + " iterations.");
          generateVariationsFive(tmpScheme, tmpArraySize);
          if (roomCount >= 6) {
            OMHelper.writeLog("Generating variations: 6 out of " + roomCount
                + " rooms.");
            tmpArraySize = (OMHelper.calculateFactorial(roomCount) / OMHelper
                .calculateFactorial(roomCount - 6));
            OMHelper.writeLog("Creating " + tmpArraySize + " variations for "
                + roomCount + " normal rooms:");
            tmpScheme = new OMRoom[tmpArraySize][6];
            i = 0;
            n = 0;
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
                            if (rooms[a] != rooms[c] && rooms[b] != rooms[c]) {
                              tmpScheme[i][2] = rooms[c];
                              if (rooms[a] != rooms[d] && rooms[b] != rooms[d]
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
                                    OMHelper.writeLog(rooms[a] + "" + rooms[b]
                                        + rooms[c] + rooms[d] + rooms[e]
                                        + rooms[f]);
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
            OMHelper.writeLog("Created " + i + " variations of " + roomCount
                + " rooms within " + n + " iterations.");
            generateVariationsSix(tmpScheme, tmpArraySize);
          } else {
            OMHelper.writeLog("Warning: Not enough rooms for 6-of-" + roomCount
                + "-rooms-variations.");
          }
        } else {
          OMHelper.writeLog("Warning: Not enough rooms for 5-of-" + roomCount
              + "-rooms-variations.");
          OMHelper.writeLog("Warning: Not enough rooms for 6-of-" + roomCount
              + "-rooms-variations.");
        }
      } else {
        OMHelper.writeLog("Warning: Not enough rooms for 4-of-" + roomCount
            + "-rooms-variations.");
        OMHelper.writeLog("Warning: Not enough rooms for 5-of-" + roomCount
            + "-rooms-variations.");
        OMHelper.writeLog("Warning: Not enough rooms for 6-of-" + roomCount
            + "-rooms-variations.");
      }
    } else {
      OMHelper.writeLog("Error: Not enough rooms to create any variations.");
      OMHelper
          .writeLog("Error: Make sure at least 3 rooms and 1 cellar are available.");
      OMHelper.writeLog("Error: No variations created.");
    }
  }

  /**
   * Generates all variations with adding one cellar at any possible position
   * using six different rooms. Sets VariationSchemeSix in the end.
   * 
   * @param tmpScheme
   *          An array of normal rooms-arrays which are used as a pattern to
   *          generate the final variations for six different rooms.
   * @param tmpArraySize
   *          The number of variations available for six different rooms = (R! /
   *          (R-X)!)
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  private void generateVariationsSix(OMRoom[][] tmpScheme, int tmpArraySize)
      throws IOException {
    OMRoom[] cellars = this.Cellars;
    int cellarCount = this.Cellars.length;
    int roomCount = this.Rooms.length;
    OMHelper.writeLog("Adding " + cellarCount
        + " cellar(s) now at any possible position.");
    int finalArraySize = tmpArraySize * cellarCount * 7;
    OMRoom[] variationScheme[] = new OMRoom[finalArraySize][7];
    OMHelper.writeLog("Calculating " + finalArraySize + " variations for "
        + cellarCount + " cellar(s) and " + roomCount + " rooms:");
    int x = 0;
    int y = 0;
    for (int g = 0; g < cellarCount; g++) {
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[g];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][5];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = cellars[g];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][5];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = cellars[g];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][5];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = cellars[g];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][5];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][3];
        variationScheme[x][4] = cellars[g];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][5];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][3];
        variationScheme[x][4] = tmpScheme[y][4];
        variationScheme[x][5] = cellars[g];
        variationScheme[x][6] = tmpScheme[y][5];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][3];
        variationScheme[x][4] = tmpScheme[y][4];
        variationScheme[x][5] = tmpScheme[y][5];
        variationScheme[x][6] = cellars[g];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
    }
    OMHelper.writeLog("Created " + x + " variations for " + cellarCount
        + " cellar(s) and " + roomCount + " rooms.");
    setVariationSchemeSix(variationScheme);
  }

  /**
   * Generates all variations while adding one cellar at any possible position
   * using five different rooms. Sets VariationSchemeFive in the end.
   * 
   * @param tmpScheme
   *          An array of normal rooms-arrays which are used as a pattern to
   *          generate the final variations using five different rooms.
   * @param tmpArraySize
   *          The number of variations available for five different rooms = (R!
   *          / (R-X)!)
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  private void generateVariationsFive(OMRoom[][] tmpScheme, int tmpArraySize)
      throws IOException {
    OMRoom[] cellars = this.Cellars;
    int cellarCount = this.Cellars.length;
    int roomCount = this.Rooms.length;
    OMHelper.writeLog("Adding " + cellarCount
        + " cellar(s) now at any possible position.");
    int finalArraySize = tmpArraySize * 5 * cellarCount * 6;
    OMRoom[] variationScheme[] = new OMRoom[finalArraySize][7];
    OMHelper.writeLog("Calculating " + finalArraySize + " variations for "
        + cellarCount + " cellar(s) and " + roomCount + " rooms:");
    int x = 0;
    int y = 0;
    for (int a = 0; a < cellarCount; a++) {
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][0];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = cellars[a];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = cellars[a];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = cellars[a];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = cellars[a];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = cellars[a];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = cellars[a];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = cellars[a];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = cellars[a];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = cellars[a];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = cellars[a];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = cellars[a];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = cellars[a];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = cellars[a];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = cellars[a];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = cellars[a];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][3];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = cellars[a];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][3];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = cellars[a];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = cellars[a];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = cellars[a];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][3];
        variationScheme[x][4] = cellars[a];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][4];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][3];
        variationScheme[x][4] = tmpScheme[y][4];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
    }
    OMHelper.writeLog("Created " + x + " variations for " + cellarCount
        + " cellar(s) and " + roomCount + " rooms.");
    setVariationSchemeFive(variationScheme);
  }

  /**
   * Generates all variations while adding one cellar at any possible position
   * using four different rooms. Sets VariationSchemeFour in the end.
   * 
   * @param tmpScheme
   *          An array of normal rooms-arrays which are used as a pattern to
   *          generate the final variations using four different rooms.
   * @param tmpArraySize
   *          The number of variations available for four different rooms = (R!
   *          / (R-X)!)
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  private void generateVariationsFour(OMRoom[][] tmpScheme, int tmpArraySize)
      throws IOException {
    OMRoom[] cellars = this.Cellars;
    int cellarCount = this.Cellars.length;
    int roomCount = this.Rooms.length;
    OMHelper.writeLog("Adding " + cellarCount
        + " cellar(s) now at any possible position.");
    int finalArraySize = tmpArraySize * 6 * cellarCount * 5;
    OMRoom[] variationScheme[] = new OMRoom[finalArraySize][7];
    OMHelper.writeLog("Calculating " + finalArraySize + " variations for "
        + cellarCount + " cellar(s) and " + roomCount + " rooms:");
    int x = 0;
    int y = 0;
    for (int a = 0; a < cellarCount; a++) {
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][0];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][1];
        variationScheme[x][5] = tmpScheme[y][2];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = cellars[a];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][1];
        variationScheme[x][5] = tmpScheme[y][2];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = cellars[a];
        variationScheme[x][5] = tmpScheme[y][2];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = cellars[a];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][0];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][2];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = cellars[a];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][2];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = cellars[a];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][2];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = cellars[a];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][0];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = cellars[a];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = cellars[a];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = cellars[a];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][2];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = cellars[a];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][2];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = cellars[a];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][2];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = cellars[a];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = cellars[a];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][1];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = cellars[a];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = cellars[a];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = cellars[a];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = cellars[a];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][2];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = cellars[a];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = tmpScheme[y][3];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][3];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
    }
    OMHelper.writeLog("Created " + x + " variations for " + cellarCount
        + " cellar(s) and " + roomCount + " rooms.");
    setVariationSchemeFour(variationScheme);
  }

  /**
   * Generates all variations while adding one cellar at any possible position
   * using three different rooms. Sets VariationSchemeThree in the end.
   * 
   * @param tmpScheme
   *          An array of normal rooms-arrays which are used as a pattern to
   *          generate the final variations using three different rooms.
   * @param tmpArraySize
   *          The number of variations available for three different rooms = (R!
   *          / (R-X)!)
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  private void generateVariationsThree(OMRoom[][] tmpScheme, int tmpArraySize)
      throws IOException {
    OMRoom[] cellars = this.Cellars;
    int cellarCount = this.Cellars.length;
    int roomCount = this.Rooms.length;
    OMHelper.writeLog("Adding " + cellarCount
        + " cellar(s) now at any possible position.");
    int finalArraySize = tmpArraySize * cellarCount * 4;
    OMRoom[] variationScheme[] = new OMRoom[finalArraySize][7];
    OMHelper.writeLog("Calculating " + finalArraySize + " variations for "
        + cellarCount + " cellar(s) and " + roomCount + " rooms:");
    int x = 0;
    int y = 0;
    for (int a = 0; a < cellarCount; a++) {
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = cellars[a];
        variationScheme[x][1] = tmpScheme[y][0];
        variationScheme[x][2] = tmpScheme[y][1];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][5];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = cellars[a];
        variationScheme[x][3] = tmpScheme[y][2];
        variationScheme[x][4] = tmpScheme[y][3];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][5];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][3];
        variationScheme[x][4] = cellars[a];
        variationScheme[x][5] = tmpScheme[y][4];
        variationScheme[x][6] = tmpScheme[y][5];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
      y = 0;
      while (y < tmpArraySize) {
        variationScheme[x][0] = tmpScheme[y][0];
        variationScheme[x][1] = tmpScheme[y][1];
        variationScheme[x][2] = tmpScheme[y][2];
        variationScheme[x][3] = tmpScheme[y][3];
        variationScheme[x][4] = tmpScheme[y][4];
        variationScheme[x][5] = tmpScheme[y][5];
        variationScheme[x][6] = cellars[a];
        OMHelper.writeLog("" + variationScheme[x][0] + variationScheme[x][1]
            + variationScheme[x][2] + variationScheme[x][3]
            + variationScheme[x][4] + variationScheme[x][5]
            + variationScheme[x][6]);
        x++;
        y++;
      }
    }
    OMHelper.writeLog("Created " + x + " variations for " + cellarCount
        + " cellar(s) and " + roomCount + " rooms.");
    setVariationSchemeThree(variationScheme);
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(this.Cellars);
    result = prime * result + ((this.Name == null) ? 0 : this.Name.hashCode());
    result = prime * result + this.RoomCount;
    result = prime * result + Arrays.hashCode(this.Rooms);
    result = prime * result
        + ((this.Start == null) ? 0 : this.Start.hashCode());
    result = prime * result + this.ValueCount;
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    OMBuilding other = (OMBuilding) obj;
    if (!Arrays.equals(this.Cellars, other.Cellars)) {
      return false;
    }
    if (this.Name == null) {
      if (other.Name != null) {
        return false;
      }
    } else if (!this.Name.equals(other.Name)) {
      return false;
    }
    if (this.RoomCount != other.RoomCount) {
      return false;
    }
    if (!Arrays.equals(this.Rooms, other.Rooms)) {
      return false;
    }
    if (this.Start == null) {
      if (other.Start != null) {
        return false;
      }
    } else if (!this.Start.equals(other.Start)) {
      return false;
    }
    if (this.ValueCount != other.ValueCount) {
      return false;
    }
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/YYYY");
    String startDate = dateFormat.format(this.Start);
    return this.Name + ", " + startDate;
  }

}
