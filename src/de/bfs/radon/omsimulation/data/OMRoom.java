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

package de.bfs.radon.omsimulation.data;

/**
 * Public class OMRoom, defining the important attributes for the room objects
 * which are required for OMBuildings and OMCampaigns later.
 * 
 * @author A. Schoedon
 */
public class OMRoom {
  /**
   * Stores the type of the room: Normal, Cellar, Misc.
   * 
   * @see enum OMRoomType
   */
  private OMRoomType type;

  /**
   * Stores the unique ID of the room which is used to parse the type.
   */
  private String id;

  /**
   * Stores an array of radon values for the certain room. Each value[i] equals
   * the avarage of one hour. The unit is [Bq/m^3].
   */
  private double[] values;

  /**
   * Stores the total number of measurements for the room. The unit is [h].
   */
  private int count;

  /**
   * Stores the arithmetic average of all values. The unit is [Bq/m^3].
   */
  private double avarage;

  /**
   * Stores the highest value out of all measurements. The unit is [Bq/m^3].
   */
  private double maxima;

  /**
   * Stores the standard deviation of all values. The unit is [Bq/m^3].
   */
  private double deviation;

  /**
   * Gets the type of the room: Normal, Cellar, Misc.
   * 
   * @return the type of the room.
   */
  public OMRoomType getType() {
    return this.type;
  }

  /**
   * Sets the type of the room by parsing the unique ID.
   */
  private void setType() {
    OMRoomType type;
    String id = this.id;
    char c = id.charAt(0);
    switch (c) {
    case 'C': {
      type = OMRoomType.Cellar;
      break;
    }
    case 'c': {
      type = OMRoomType.Cellar;
      break;
    }
    case 'R': {
      type = OMRoomType.Room;
      break;
    }
    case 'r': {
      type = OMRoomType.Room;
      break;
    }
    default: {
      type = OMRoomType.Misc;
      break;
    }
    }
    this.type = type;
  }

  /**
   * Gets the unique ID of the room which is used to parse the type.
   * 
   * @return The unique ID of the room which is used to parse the type.
   */
  public String getId() {
    return this.id;
  }

  /**
   * Sets the unique ID of the room. In Addition, this will parse and update the
   * type.
   * 
   * @param id
   *          The unique ID of the room which is used to parse the type.
   */
  public void setId(String id) {
    this.id = id;
    setType();
  }

  /**
   * Gets an array of radon values for the certain room. Each value[i] equals
   * the avarage of one hour. The unit is [Bq/m^3].
   * 
   * @return An array of radon values for the certain room.
   */
  public double[] getValues() {
    return this.values;
  }

  /**
   * Sets an array of radon values for the certain room. Each value[i] equals
   * the avarage of one hour. After setting new values, all attributes of the
   * room object will be re-calculated and updated again.
   * 
   * @param values
   *          An array of radon values for the certain room.
   */
  public void setValues(double[] values) {
    this.values = values;
    calculateAttributes();
  }

  /**
   * Gets the total number of measurements for the room. The unit is [h].
   * 
   * @return The total number of measurements for the room.
   */
  public int getCount() {
    return this.count;
  }

  /**
   * Sets the total number of measurements for the room by simply counting them.
   * The unit is [h].
   */
  private void setCount() {
    double[] values = this.values;
    this.count = values.length;
  }

  /**
   * Gets the arithmetic average of all values. The unit is [Bq/m^3].
   * 
   * @return The arithmetic average of all values.
   */
  public double getAvarage() {
    return this.avarage;
  }

  /**
   * Sets the arithmetic average of all values by calculating the sum of all
   * values divided by total count.
   */
  private void setAvarage() {
    int count = this.count;
    double[] values = this.values;
    double sum = 0.0;
    for (int i = 0; i < count; i++) {
      sum = sum + values[i];
    }
    this.avarage = sum / (double) count;
  }

  /**
   * Gets the highest value out of all measurements. The unit is [Bq/m^3].
   * 
   * @return The highest value out of all measurements.
   */
  public double getMaxima() {
    return this.maxima;
  }

  /**
   * Sets the highest value out of all measurements by comparing all values[i]
   * to find out which one is the highest. The unit is [Bq/m^3].
   */
  private void setMaxima() {
    double maxima = 0;
    int count = this.count;
    double[] values = this.values;
    for (int i = 0; i < count; i++) {
      if (values[i] > maxima) {
        maxima = values[i];
      }
    }
    this.maxima = maxima;
  }

  /**
   * Gets the standard deviation of all values. The unit is [Bq/m^3].
   * 
   * @return The standard deviation of all values.
   */
  public double getDeviation() {
    return this.deviation;
  }

  /**
   * Sets the standard deviation of all values by calculating it via the forumla
   * of the avarage square deviation: deviation = sqrt((sum(value[i] -
   * avg)^2)/count)). The unit is [Bq/m^3].
   */
  private void setDeviation() {
    double deviation = 0;
    int count = this.count;
    double[] values = this.values;
    double avg = this.avarage;
    double tmpSum = 0;
    for (int i = 0; i < count; i++) {
      tmpSum = tmpSum + (values[i] - avg) * (values[i] - avg);
    }
    tmpSum = tmpSum / count;
    deviation = Math.sqrt(tmpSum);
    this.deviation = deviation;
  }

  /**
   * Constructor for objects of the class OMRoom. Creates rooms using an unique
   * ID and a set of radon values.
   * 
   * @param id
   *          The unique ID of the room which is used to parse the type. For
   *          example: R1 for normal rooms, C1 for cellars or M1 for
   *          miscellanous rooms.
   * @param values
   *          An array of radon values for the certain room. Each value[i]
   *          equals the avarage of one hour. The unit is [Bq/m^3].
   */
  public OMRoom(String id, double[] values) {
    super();
    setId(id);
    setType();
    setValues(values);
    calculateAttributes();
  }

  /**
   * Calls the setters for the attributes Count, Median, Maxima and Deviation to
   * re-calculate and update them. Call this method always after changes to the
   * radon values. Note: It's not needed to call this after using the
   * OMRoom.setValues() method as modifying values using the setter always
   * triggers the re-calculation of attributes on its own.
   */
  public void calculateAttributes() {
    setCount();
    setAvarage();
    setMaxima();
    setDeviation();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OMRoom other = (OMRoom) obj;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
      return false;
    if (this.type == null) {
      if (other.type != null)
        return false;
    } else if (!this.type.equals(other.type))
      return false;
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.id;
  }
}
