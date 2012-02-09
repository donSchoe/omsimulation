package omSimulation.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Public class OMCampaign, defining the simulated field campaigns and its
 * attributes.
 * 
 * @author A. Schoedon
 */
public class OMCampaign {

  /**
   * Stores the timestamp of the first measurement for the simulated campaign,
   * starting with 0 for the first hour of the real value-sets. The unit is [h].
   */
  private int Start;

  /**
   * Stores a simple string to identify the used rooms and their order in this
   * campaign.
   */
  private String Variation;

  /**
   * Stores the campaign type. The campaign type identifies how many different
   * rooms were used to create this campaign. For example 'Five' means 5
   * different rooms were used.
   */
  private OMCampaignType Type;

  /**
   * Stores an integer which defines the random noise that is added to the
   * values. A random noise of 0 means the original values wont be modified. The
   * unit is [%].
   */
  private int RandomNoise;

  /**
   * Stores an array of different rooms of the simulated survey campaign.
   */
  private OMRoom[] Rooms;

  /**
   * Stores the cellar for the campaign.
   */
  private OMRoom Cellar;

  /**
   * Stores an array of selected values of all 6 normal rooms. The first values
   * is determined by the start timestamp. The unit is [Bq/m^3].
   */
  private double[] RoomValues;

  /**
   * Stores an array of selected values for the cellar room. The first value is
   * determined by the start timestamp. The unit is [Bq/m^3].
   */
  private double[] CellarValues;

  /**
   * Stores an array of logarithmic values of the normal values. Used for
   * geometric mean later on.
   */
  private double[] RoomLogValues;

  /**
   * Stores an array of logarithmic values for the cellar values. Used for
   * geometric mean later on.
   */
  private double[] CellarLogValues;

  /**
   * Stores the arithmetic average of the selected values for normal rooms. The
   * unit is [Bq/m^3].
   */
  private double RoomAvarage;

  /**
   * Stores the arithmetic avarage of the selected cellar. The unit is [Bq/m^3].
   */
  private double CellarAvarage;

  /**
   * Stores the highest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   */
  private double RoomMaxima;

  /**
   * Stores the highest value out of the selected cellar. The unit is [Bq/m^3].
   */
  private double CellarMaxima;

  /**
   * Stores the lowest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   */
  private double RoomMinima;

  /**
   * Stores the lowest value out of the selected cellar. The unit is [Bq/m^3].
   */
  private double CellarMinima;

  /**
   * Stores the standard deviation of the selected values for normal rooms. The
   * unit is [Bq/m^3].
   */
  private double RoomDeviation;

  /**
   * Stroes the standard deviation of the selected cellar. The unit is [Bq/m^3].
   */
  private double CellarDeviation;

  /**
   * Stores the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for normal rooms.
   */
  private double RoomVarCoefficient;

  /**
   * Stores the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for cellars.
   */
  private double CellarVarCoefficient;

  /**
   * Stores the range of the values which is calculated by substracting the
   * minimum by the maximum value for normal rooms. The unit is [Bq/m^3].
   */
  private double RoomRange;

  /**
   * Stores the range of the values which is calculated by substracting the
   * minimum by the maximum value for cellar rooms. The unit is [Bq/m^3].
   */
  private double CellarRange;

  /**
   * Stores the quantile 5 for normal rooms, where only 5% of the values are
   * lower. The unit is [Bq/m^3].
   */
  private double RoomQuantile05;

  /**
   * Stores the quantile 5 for cellar rooms, where only 5% of values are lower.
   * The unit is [Bq/m^3].
   */
  private double CellarQuantile05;

  /**
   * Stores the quantile 95 for normal rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   */
  private double RoomQuantile95;

  /**
   * Stores the quantile 95 for cellar rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   */
  private double CellarQuantile95;

  /**
   * Stores the median (quantile 50) for normal rooms, where 50% of the values
   * are lower. The unit is [Bq/m^3].
   */
  private double RoomMedian;

  /**
   * Stores the median (quantile 50) for cellar rooms, where 50% of the values
   * are lower. The unit is [Bq/m^3].
   */
  private double CellarMedian;

  /**
   * Stores the quantile deviation for normal rooms which determines the ratio
   * of the quantiles.
   */
  private double RoomQuantileDeviation;

  /**
   * Stores the quantile deviation for cellar rooms which determines the ratio
   * of the quantiles.
   */
  private double CellarQuantileDeviation;

  /**
   * Stores the geometric mean for normal rooms. The unit is [Bq/m^3].
   */
  private double RoomLogAvarage;

  /**
   * Stores the geometric mean for cellar rooms. The unit is [Bq/m^3].
   */
  private double CellarLogAvarage;

  /**
   * Stores the geometric standard deviation for normal rooms.
   */
  private double RoomLogDeviation;

  /**
   * Stores the geometric standard deviation for cellar rooms.
   */
  private double CellarLogDeviation;

  /**
   * Gets the timestamp of the first measurement for the simulated campaign. The
   * unit is [h].
   * 
   * @return The timestamp of the first measurement for the simulated campaign.
   */
  public int getStart() {
    return Start;
  }

  /**
   * Sets the timestamp of the first measurement for the simulated campaign. The
   * unit is [h].
   * 
   * @param start
   *          The timestamp of the first measurement for the simulated campaign,
   *          0 equals the first hour of the real value-sets.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void setStart(int start) throws IOException {
    this.Start = start;
    calculateAttributes();
  }

  /**
   * Gets a simple string to identify the used rooms and their order in this
   * campaign. For example R1R2C1R3R3R4R6.
   * 
   * @return A simple string to identify the used rooms and their order in this
   *         campaign.
   */
  public String getVariation() {
    return this.Variation;
  }

  /**
   * Sets a simple string to identify the used rooms and their order in this
   * campaign. For example R1R2C1R3R3R4R6.
   * 
   * @param variation
   */
  private void setVariation(String variation) {
    this.Variation = variation;
  }

  /**
   * Gets the campaign type. The campaign type identifies how many different
   * rooms were used to create this campaign. For example 'Five' means 5
   * different rooms were used.
   * 
   * @return The campaign type.
   */
  public OMCampaignType getType() {
    return this.Type;
  }

  /**
   * Sets the campaign type. The campaign type identifies how many different
   * rooms were used to create this campaign. For example 'Five' means 5
   * different rooms were used.
   * 
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  private void setType() throws IOException {
    OMRoom[] rooms = this.Rooms;
    OMCampaignType type = OMCampaignType.Six;
    if (rooms[0].getId() != rooms[1].getId()) {
      if (rooms[1].getId() != rooms[2].getId()) {
        if (rooms[2].getId() != rooms[3].getId()) {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Six;
            } else {
              type = OMCampaignType.Five;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Five;
            } else {
              type = OMCampaignType.Four;
            }
          }
        } else {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Five;
            } else {
              type = OMCampaignType.Four;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          }
        }
      } else {
        if (rooms[2].getId() != rooms[3].getId()) {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Five;
            } else {
              type = OMCampaignType.Four;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          }
        } else {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Three;
            } else {
              type = OMCampaignType.Two;
              OMHelper
                  .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
            }
          }
        }
      }
    } else {
      if (rooms[1].getId() != rooms[2].getId()) {
        if (rooms[2].getId() != rooms[3].getId()) {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Five;
            } else {
              type = OMCampaignType.Four;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          }
        } else {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Three;
            } else {
              type = OMCampaignType.Two;
              OMHelper
                  .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
            }
          }
        }
      } else {
        if (rooms[2].getId() != rooms[3].getId()) {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Three;
            } else {
              type = OMCampaignType.Two;
              OMHelper
                  .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
            }
          }
        } else {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Three;
            } else {
              type = OMCampaignType.Two;
              OMHelper
                  .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Two;
              OMHelper
                  .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
            } else {
              type = OMCampaignType.One;
              OMHelper
                  .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
            }
          }
        }
      }
    }
    this.Type = type;
  }

  /**
   * Gets an integer which defines the random noise that is added to the values.
   * A random noise of 0 means the original values wont be modified. The unit is
   * [%].
   * 
   * @return An integer which defines the random noise that is added to the
   *         values.
   */
  public int getRandomNoise() {
    return this.RandomNoise;
  }

  /**
   * Sets an integer which defines the random noise that is added to the values.
   * A random noise of 0 means the original values wont be modified. The unit is
   * [%].
   * 
   * @param randomNoise
   *          An integer which defines the random noise that is added to the
   *          values.
   */
  private void setRandomNoise(int randomNoise) {
    this.RandomNoise = randomNoise;
  }

  /**
   * Gets an array of rooms of the simulated survey campaign.
   * 
   * @return An array of rooms of the simulated survey campaign.
   */
  public OMRoom[] getRooms() {
    return this.Rooms;
  }

  /**
   * Sets an array of rooms of the simulated survey campaign and triggers a
   * re-calculation of all attributes.
   * 
   * @param rooms
   *          An array of rooms of the simulated survey campaign.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void setRooms(OMRoom[] rooms) throws IOException {
    if (rooms.length == 6) {
      this.Rooms = rooms;
      calculateAttributes();
    } else {
      OMHelper.writeLog("Error: 6 rooms are needed to create a campaign.");
    }
  }

  /**
   * Gets the cellar for the campaign.
   * 
   * @return The cellar for the campaign.
   */
  public OMRoom getCellar() {
    return this.Cellar;
  }

  /**
   * Sets the cellar for the campaign and triggers a re-calculation of all
   * attributes.
   * 
   * @param cellar
   *          The cellar for the campaign.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void setCellar(OMRoom cellar) throws IOException {
    this.Cellar = cellar;
    calculateAttributes();
  }

  /**
   * Gets an array of selected values of all normal rooms. The first values is
   * determined by the start timestamp. The unit is [Bq/m^3].
   * 
   * @return An array of selected values of all normal rooms.
   */
  public double[] getRoomValues() {
    return this.RoomValues;
  }

  /**
   * Sets an array of selected values of all normal rooms. The first values is
   * determined by the start timestamp. The values will be sorted from lowest to
   * highest. The unit is [Bq/m^3].
   */
  private void setRoomValues() {
    OMRoom[] rooms = this.Rooms;
    int start = this.Start;
    int total = 144;
    int day = 24;
    int randomNoise = this.RandomNoise * 10;
    Random generator = new Random();
    double tmpNoise = 0.0;
    double[] values = new double[total];
    int x = 0;
    double[] tmpValues = rooms[0].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    tmpValues = rooms[1].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    tmpValues = rooms[2].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    tmpValues = rooms[3].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    tmpValues = rooms[4].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    tmpValues = rooms[5].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    Arrays.sort(values);
    this.RoomValues = values;
  }

  /**
   * Gets an array of selected values for the cellar room. The first value is
   * determined by the start timestamp. The unit is [Bq/m^3].
   * 
   * @return An array of selected values for the cellar room.
   */
  public double[] getCellarValues() {
    return this.CellarValues;
  }

  /**
   * Sets an array of selected values for the cellar room. The first value is
   * determined by the start timestamp. The values will be sorted from lowest to
   * highest. The unit is [Bq/m^3].
   */
  private void setCellarValues() {
    OMRoom cellar = this.Cellar;
    int start = this.Start;
    int total = 24;
    int day = 24;
    int randomNoise = this.RandomNoise * 10;
    Random generator = new Random();
    double tmpNoise = 0.0;
    double[] values = new double[total];
    int x = 0;
    double[] tmpValues = cellar.getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    Arrays.sort(values);
    this.CellarValues = values;
  }

  /**
   * Gets an array of logarithmic values of the normal rooms. Used for geometric
   * mean later on.
   * 
   * @return An array of logarithmic values.
   */
  public double[] getRoomLogValues() {
    return this.RoomLogValues;
  }

  /**
   * Sets an array of logarithmic values using the sorted normal values. Used
   * for geometric mean later on.
   */
  private void setRoomLogValues() {
    double[] values = this.RoomValues;
    double[] logValues = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      if (values[i] > 0) {
        logValues[i] = Math.log(values[i]);
      } else {
        logValues[i] = 0;
      }
    }
    this.RoomLogValues = logValues;
  }

  /**
   * Gets an array of logarithmic values for the cellar rooms. Used for
   * geometric mean later on.
   * 
   * @return An array of logarithmic values for the cellar rooms.
   */
  public double[] getCellarLogValues() {
    return this.CellarLogValues;
  }

  /**
   * Sets an array of logarithmic values for the cellar rooms using the sorted
   * cellar values. Used for geometric mean later on.
   */
  private void setCellarLogValues() {
    double[] values = this.CellarValues;
    double[] logValues = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      if (values[i] > 0) {
        logValues[i] = Math.log(values[i]);
      } else {
        logValues[i] = 0;
      }
    }
    this.CellarLogValues = logValues;
  }

  /**
   * Gets the arithmetic mean of the selected values for normal rooms. The unit
   * is [Bq/m^3].
   * 
   * @return The arithmetic mean of the selected values.
   */
  public double getRoomAvarage() {
    return this.RoomAvarage;
  }

  /**
   * Sets the arithmetic mean of the selected values for normal rooms. The unit
   * is [Bq/m^3].
   */
  private void setRoomAvarage() {
    double[] values = this.RoomValues;
    double sum = 0.0;
    for (int i = 0; i < values.length; i++) {
      sum = sum + values[i];
    }
    double avg = sum / values.length;
    this.RoomAvarage = avg;
  }

  /**
   * Gets the arithmetic avarage of the selected cellar. The unit is [Bq/m^3].
   * 
   * @return The arithmetic avarage of the selected cellar.
   */
  public double getCellarAvarage() {
    return this.CellarAvarage;
  }

  /**
   * Sets the arithmetic avarage of the selected cellar. The unit is [Bq/m^3].
   */
  private void setCellarAvarage() {
    double[] values = this.CellarValues;
    double sum = 0.0;
    for (int i = 0; i < values.length; i++) {
      sum = sum + values[i];
    }
    double avg = sum / values.length;
    this.CellarAvarage = avg;
  }

  /**
   * Gets the highest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   * 
   * @return The highest value out of the selected normal rooms.
   */
  public double getRoomMaxima() {
    return this.RoomMaxima;
  }

  /**
   * Sets the highest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   */
  private void setRoomMaxima() {
    double[] values = this.RoomValues;
    double maxima = values[values.length - 1];
    this.RoomMaxima = maxima;
  }

  /**
   * Gets the highest value out of the selected cellar. The unit is [Bq/m^3].
   * 
   * @return The highest value out of the selected cellar.
   */
  public double getCellarMaxima() {
    return this.CellarMaxima;
  }

  /**
   * Sets the highest value out of the selected cellar. The unit is [Bq/m^3].
   */
  private void setCellarMaxima() {
    double[] values = this.CellarValues;
    double maxima = values[values.length - 1];
    this.CellarMaxima = maxima;
  }

  /**
   * Gets the lowest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   * 
   * @return The lowest value out of the selected normal rooms.
   */
  public double getRoomMinima() {
    return this.RoomMinima;
  }

  /**
   * Sets the lowest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   */
  private void setRoomMinima() {
    double[] values = this.RoomValues;
    double minima = values[0];
    this.RoomMinima = minima;
  }

  /**
   * Gets the lowest value out of the selected cellar. The unit is [Bq/m^3].
   * 
   * @return The lowest value out of the selected cellar.
   */
  public double getCellarMinima() {
    return this.CellarMinima;
  }

  /**
   * Sets the lowest value out of the selected cellar. The unit is [Bq/m^3].
   */
  private void setCellarMinima() {
    double[] values = this.CellarValues;
    double minima = values[0];
    this.CellarMinima = minima;
  }

  /**
   * Gets the standard deviation of the selected values for normal rooms. The
   * unit is [Bq/m^3].
   * 
   * @return The standard deviation of the selected values for normal rooms.
   */
  public double getRoomDeviation() {
    return this.RoomDeviation;
  }

  /**
   * Sets the standard deviation of the selected values for normal rooms. The
   * unit is [Bq/m^3].
   */
  private void setRoomDeviation() {
    double dev = 0;
    double[] values = this.RoomValues;
    double avg = this.RoomAvarage;
    double tmpSum = 0;
    for (int i = 0; i < values.length; i++) {
      tmpSum = tmpSum + (values[i] - avg) * (values[i] - avg);
    }
    tmpSum = tmpSum / values.length;
    dev = Math.sqrt(tmpSum);
    this.RoomDeviation = dev;
  }

  /**
   * Gets the standard deviation of the selected cellar. The unit is [Bq/m^3].
   * 
   * @return The standard deviation of the selected cellar.
   */
  public double getCellarDeviation() {
    return this.CellarDeviation;
  }

  /**
   * Sets the standard deviation of the selected cellar. The unit is [Bq/m^3].
   */
  private void setCellarDeviation() {
    double dev = 0;
    double[] values = this.CellarValues;
    double avg = this.CellarAvarage;
    double tmpSum = 0;
    for (int i = 0; i < values.length; i++) {
      tmpSum = tmpSum + (values[i] - avg) * (values[i] - avg);
    }
    tmpSum = tmpSum / values.length;
    dev = Math.sqrt(tmpSum);
    this.CellarDeviation = dev;
  }

  /**
   * Gets the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for normal rooms.
   * 
   * @return The variation coefficient.
   */
  public double getRoomVarCoefficient() {
    return this.RoomVarCoefficient;
  }

  /**
   * Sets the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for normal rooms.
   */
  private void setRoomVarCoefficient() {
    double avg = this.RoomAvarage;
    double dev = this.RoomDeviation;
    double vc = dev / avg;
    this.RoomVarCoefficient = vc;
  }

  /**
   * Gets the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for cellars.
   * 
   * @return The variation coefficient.
   */
  public double getCellarVarCoefficient() {
    return this.CellarVarCoefficient;
  }

  /**
   * Sets the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for cellars.
   */
  private void setCellarVarCoefficient() {
    double avg = this.CellarAvarage;
    double dev = this.CellarDeviation;
    double vc = dev / avg;
    this.CellarVarCoefficient = vc;
  }

  /**
   * Gets the range of the values which is calculated by substracting the
   * minimum by the maximum value for normal rooms. The unit is [Bq/m^3].
   * 
   * @return The range of the values.
   */
  public double getRoomRange() {
    return this.RoomRange;
  }

  /**
   * Sets the range of the values which is calculated by substracting the
   * minimum by the maximum value for normal rooms. The unit is [Bq/m^3].
   */
  private void setRoomRange() {
    double min = this.RoomMinima;
    double max = this.RoomMaxima;
    double range = max - min;
    this.RoomRange = range;
  }

  /**
   * Gets the range of the values which is calculated by substracting the
   * minimum by the maximum value for cellar rooms. The unit is [Bq/m^3].
   * 
   * @return The range of the values.
   */
  public double getCellarRange() {
    return this.CellarRange;
  }

  /**
   * Sets the range of the values which is calculated by substracting the
   * minimum by the maximum value for cellar rooms. The unit is [Bq/m^3].
   */
  private void setCellarRange() {
    double min = this.CellarMinima;
    double max = this.CellarMaxima;
    double range = max - min;
    this.CellarRange = range;
  }

  /**
   * Gets the quantile 5 for normal rooms, where only 5% of the values are
   * lower. The unit is [Bq/m^3].
   * 
   * @return The quantile 5.
   */
  public double getRoomQuantile05() {
    return this.RoomQuantile05;
  }

  /**
   * Sets the quantile 5 for normal rooms, where only 5% of the values are
   * lower. The unit is [Bq/m^3].
   */
  private void setRoomQuantile05() {
    double[] values = this.RoomValues;
    double x = ((double) values.length / 100.0) * 5.0;
    int i = (int) x - 1;
    double q05 = values[i];
    this.RoomQuantile05 = q05;
  }

  /**
   * Gets the quantile 5 for cellar rooms, where only 5% of values are lower.
   * The unit is [Bq/m^3].
   * 
   * @return The quantile 5 for cellar rooms.
   */
  public double getCellarQuantile05() {
    return this.CellarQuantile05;
  }

  /**
   * Sets the quantile 5 for cellar rooms, where only 5% of values are lower.
   * The unit is [Bq/m^3].
   */
  private void setCellarQuantile05() {
    double[] values = this.CellarValues;
    double x = ((double) values.length / 100.0) * 5.0;
    int i = (int) x - 1;
    double q05 = values[i];
    this.CellarQuantile05 = q05;
  }

  /**
   * Gets the quantile 95 for normal rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   * 
   * @return The quantile 95.
   */
  public double getRoomQuantile95() {
    return this.RoomQuantile95;
  }

  /**
   * Sets the quantile 95 for normal rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   */
  private void setRoomQuantile95() {
    double[] values = this.RoomValues;
    double x = (double) values.length
        - (((double) values.length / 100.0) * 5.0);
    int i = (int) x - 1;
    double q95 = values[i];
    this.RoomQuantile95 = q95;
  }

  /**
   * Gets the quantile 95 for cellar rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   * 
   * @return The quantile 95 for cellar rooms.
   */
  public double getCellarQuantile95() {
    return this.CellarQuantile95;
  }

  /**
   * Sets the quantile 95 for cellar rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   */
  private void setCellarQuantile95() {
    double[] values = this.CellarValues;
    double x = (double) values.length
        - (((double) values.length / 100.0) * 5.0);
    int i = (int) x - 1;
    double q95 = values[i];
    this.CellarQuantile95 = q95;
  }

  /**
   * Gets the median (quantile 50) for normal rooms, where 50% of the values are
   * lower. The unit is [Bq/m^3].
   * 
   * @return The median (quantile 50) for normal rooms.
   */
  public double getRoomMedian() {
    return this.RoomMedian;
  }

  /**
   * Sets the median (quantile 50) for normal rooms, where 50% of the values are
   * lower. The unit is [Bq/m^3].
   */
  private void setRoomMedian() {
    double[] values = this.RoomValues;
    double x = (double) values.length
        - (((double) values.length / 100.0) * 50.0);
    int i = (int) x - 1;
    double q50 = values[i];
    this.RoomMedian = q50;
  }

  /**
   * Gets the median (quantile 50) for cellar rooms, where 50% of the values are
   * lower. The unit is [Bq/m^3].
   * 
   * @return The median (quantile 50) for cellar rooms.
   */
  public double getCellarMedian() {
    return this.CellarMedian;
  }

  /**
   * Sets the median (quantile 50) for cellar rooms, where 50% of the values are
   * lower. The unit is [Bq/m^3].
   */
  private void setCellarMedian() {
    double[] values = this.CellarValues;
    double x = (double) values.length
        - (((double) values.length / 100.0) * 50.0);
    int i = (int) x - 1;
    double q50 = values[i];
    this.CellarMedian = q50;
  }

  /**
   * Gets the quantile deviation for normal rooms which determines the ratio of
   * the quantiles.
   * 
   * @return The quantile deviation for normal rooms.
   */
  public double getRoomQuantileDeviation() {
    return this.RoomQuantileDeviation;
  }

  /**
   * Sets the quantile deviation for normal rooms which determines the ratio of
   * the quantiles.
   */
  private void setRoomQuantileDeviation() {
    double q05 = this.RoomQuantile05;
    double q50 = this.RoomMedian;
    double q95 = this.RoomQuantile95;
    double qDev = (q95 - q05) / q50;
    this.RoomQuantileDeviation = qDev;
  }

  /**
   * Gets the quantile deviation for cellar rooms which determines the ratio of
   * the quantiles.
   * 
   * @return The quantile deviation for cellar rooms.
   */
  public double getCellarQuantileDeviation() {
    return this.CellarQuantileDeviation;
  }

  /**
   * Sets the quantile deviation for cellar rooms which determines the ratio of
   * the quantiles.
   */
  private void setCellarQuantileDeviation() {
    double q05 = this.CellarQuantile05;
    double q50 = this.CellarMedian;
    double q95 = this.CellarQuantile95;
    double qDev = (q95 - q05) / q50;
    this.CellarQuantileDeviation = qDev;
  }

  /**
   * Gets the geometric mean for normal rooms. The unit is [Bq/m^3].
   * 
   * @return The geometric mean for normal rooms.
   */
  public double getRoomLogAvarage() {
    return this.RoomLogAvarage;
  }

  /**
   * Sets the geometric mean for normal rooms. The unit is [Bq/m^3].
   */
  private void setRoomLogAvarage() {
    double[] logValues = this.RoomLogValues;
    double sum = 0.0;
    for (int i = 0; i < logValues.length; i++) {
      sum = sum + logValues[i];
    }
    double tmpAvarage = Math.exp(sum / logValues.length);
    this.RoomLogAvarage = tmpAvarage;
  }

  /**
   * Gets the geometric mean for cellar rooms. The unit is [Bq/m^3].
   * 
   * @return The geometric mean for cellar rooms.
   */
  public double getCellarLogAvarage() {
    return this.CellarLogAvarage;
  }

  /**
   * Sets the geometric mean for cellar rooms. The unit is [Bq/m^3].
   */
  private void setCellarLogAvarage() {
    double[] logValues = this.CellarLogValues;
    double sum = 0.0;
    for (int i = 0; i < logValues.length; i++) {
      sum = sum + logValues[i];
    }
    double tmpAvarage = Math.exp(sum / logValues.length);
    this.CellarLogAvarage = tmpAvarage;
  }

  /**
   * Gets the geometric standard deviation for normal rooms.
   * 
   * @return The geometric standard deviation for normal rooms.
   */
  public double getRoomLogDeviation() {
    return this.RoomLogDeviation;
  }

  /**
   * Sets the geometric standard deviation for normal rooms.
   */
  private void setRoomLogDeviation() {
    double[] logValues = this.RoomLogValues;
    double logAvarage = this.RoomLogAvarage;
    double tmp = 0.0;
    for (int i = 0; i < logValues.length; i++) {
      if (logValues[i] > 0) {
        tmp = tmp + Math.log(logValues[i] / logAvarage)
            * Math.log(logValues[i] / logAvarage);
      }
    }
    double logDeviation = Math.exp(tmp / logValues.length);
    this.RoomLogDeviation = logDeviation;
  }

  /**
   * Gets the geometric standard deviation for cellar rooms.
   * 
   * @return The geometric standard deviation for cellar rooms.
   */
  public double getCellarLogDeviation() {
    return this.CellarLogDeviation;
  }

  /**
   * Sets the geometric standard deviation for cellar rooms.
   */
  private void setCellarLogDeviation() {
    double[] logValues = this.CellarLogValues;
    double logAvarage = this.CellarLogAvarage;
    double tmp = 0.0;
    for (int i = 0; i < logValues.length; i++) {
      if (logValues[i] > 0) {
        tmp = tmp + Math.log(logValues[i] / logAvarage)
            * Math.log(logValues[i] / logAvarage);
      }
    }
    double logDeviation = Math.exp(tmp / logValues.length);
    this.CellarLogDeviation = logDeviation;
  }

  /**
   * Constructor for objects of the class OMCampaign. Creates survey campaigns
   * for 7 days in 7 different Rooms using 6 normal rooms and 1 cellar room.
   * 
   * @param start
   *          The timestamp of the first measurement for the simulated campaign,
   *          starting with 0 for the first hour of the real value-sets. The
   *          unit is [h].
   * @param rooms
   *          An array of seven rooms for the seven days (steps) of the
   *          simulated campaign. Should be 6 normal rooms and 1 cellar.
   * @param randomNoise
   *          An integer which defines the random noise that is added to the
   *          values. A random noise of 0 means the original values wont be
   *          modified. The unit is [%].
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public OMCampaign(int start, OMRoom[] rooms, int randomNoise)
      throws IOException {
    super();
    this.Start = start;
    try {
      this.Rooms = new OMRoom[6];
      int x = 0;
      String tmpVariation = "";
      for (int i = 0; i < rooms.length; i++) {
        if (rooms[i].getType() != OMRoomType.Cellar) {
          this.Rooms[x] = rooms[i];
          x++;
        } else {
          if (rooms[i].getType() == OMRoomType.Cellar) {
            this.Cellar = rooms[i];
          }
        }
        tmpVariation = tmpVariation + rooms[i].getId();
      }
      setVariation(tmpVariation);
      setRandomNoise(randomNoise);
      calculateAttributes();
    } catch (Exception e) {
      OMHelper.writeLog("Error: " + e.getMessage());
      OMHelper.writeLog("Error: Failed to create campaign.");
    }
  }

  /**
   * Calls the setters for all the attributes of the campaign to re-calculate
   * and update them. Call this method always after changes to the radon values.
   * Note: It's not needed to call this after using the OMCampaign.setRooms(),
   * setCellar() or setStart()-methods as modifying values using the setter
   * always triggers the re-calculation of attributes on its own.
   * 
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void calculateAttributes() throws IOException {
    setType();
    setRoomValues();
    setRoomLogValues();
    setRoomAvarage();
    setRoomMaxima();
    setRoomMinima();
    setRoomDeviation();
    setRoomVarCoefficient();
    setRoomRange();
    setRoomQuantile05();
    setRoomQuantile95();
    setRoomMedian();
    setRoomQuantileDeviation();
    setRoomLogAvarage();
    setRoomLogDeviation();
    setCellarValues();
    setCellarLogValues();
    setCellarAvarage();
    setCellarMaxima();
    setCellarMinima();
    setCellarDeviation();
    setCellarVarCoefficient();
    setCellarRange();
    setCellarQuantile05();
    setCellarQuantile95();
    setCellarMedian();
    setCellarQuantileDeviation();
    setCellarLogAvarage();
    setCellarLogDeviation();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((this.Cellar == null) ? 0 : this.Cellar.hashCode());
    result = prime * result + this.RandomNoise;
    result = prime * result + Arrays.hashCode(this.Rooms);
    result = prime * result + this.Start;
    result = prime * result + ((this.Type == null) ? 0 : this.Type.hashCode());
    result = prime * result
        + ((this.Variation == null) ? 0 : this.Variation.hashCode());
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
    OMCampaign other = (OMCampaign) obj;
    if (this.Cellar == null) {
      if (other.Cellar != null) {
        return false;
      }
    } else if (!this.Cellar.equals(other.Cellar)) {
      return false;
    }
    if (this.RandomNoise != other.RandomNoise) {
      return false;
    }
    if (!Arrays.equals(this.Rooms, other.Rooms)) {
      return false;
    }
    if (this.Start != other.Start) {
      return false;
    }
    if (this.Type != other.Type) {
      return false;
    }
    if (this.Variation == null) {
      if (other.Variation != null) {
        return false;
      }
    } else if (!this.Variation.equals(other.Variation)) {
      return false;
    }
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Campaign: T=" + this.Start + ",\tR=" + this.Variation + ",\tR_AM="
        + (int) this.RoomAvarage + ",\tR_GM=" + (int) this.RoomLogAvarage
        + ",\tR_Q50=" + (int) this.RoomMedian + ",\tR_MAX="
        + (int) this.RoomMaxima + ",\tC_AM=" + (int) this.CellarAvarage
        + ",\tC_GM=" + (int) this.CellarLogAvarage + ",\tC_Q50="
        + (int) this.CellarMedian + ",\tC_MAX=" + (int) this.CellarMaxima;
  }
}
