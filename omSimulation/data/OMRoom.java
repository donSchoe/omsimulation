package omSimulation.data;

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
  private OMRoomType Type;

  /**
   * Stores the unique ID of the room which is used to parse the type.
   */
  private String Id;

  /**
   * Stores an array of radon values for the certain room. Each value[i] equals
   * the avarage of one hour. The unit is [Bq/m^3].
   */
  private double[] Values;

  /**
   * Stores the total number of measurements for the room. The unit is [h].
   */
  private int Count;

  /**
   * Stores the arithmetic average of all values. The unit is [Bq/m^3].
   */
  private double Avarage;

  /**
   * Stores the highest value out of all measurements. The unit is [Bq/m^3].
   */
  private double Maxima;

  /**
   * Stores the standard deviation of all values. The unit is [Bq/m^3].
   */
  private double Deviation;

  /**
   * Gets the type of the room: Normal, Cellar, Misc.
   * 
   * @return the type of the room.
   */
  public OMRoomType getType() {
    return this.Type;
  }

  /**
   * Sets the type of the room by parsing the unique ID.
   */
  private void setType() {
    OMRoomType type;
    String id = this.Id;
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
    this.Type = type;
  }

  /**
   * Gets the unique ID of the room which is used to parse the type.
   * 
   * @return The unique ID of the room which is used to parse the type.
   */
  public String getId() {
    return this.Id;
  }

  /**
   * Sets the unique ID of the room. In Addition, this will parse and update the
   * type.
   * 
   * @param id
   *          The unique ID of the room which is used to parse the type.
   */
  public void setId(String id) {
    this.Id = id;
    setType();
  }

  /**
   * Gets an array of radon values for the certain room. Each value[i] equals
   * the avarage of one hour. The unit is [Bq/m^3].
   * 
   * @return An array of radon values for the certain room.
   */
  public double[] getValues() {
    return this.Values;
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
    this.Values = values;
    calculateAttributes();
  }

  /**
   * Gets the total number of measurements for the room. The unit is [h].
   * 
   * @return The total number of measurements for the room.
   */
  public int getCount() {
    return this.Count;
  }

  /**
   * Sets the total number of measurements for the room by simply counting them.
   * The unit is [h].
   */
  private void setCount() {
    double[] values = this.Values;
    this.Count = values.length;
  }

  /**
   * Gets the arithmetic average of all values. The unit is [Bq/m^3].
   * 
   * @return The arithmetic average of all values.
   */
  public double getAvarage() {
    return this.Avarage;
  }

  /**
   * Sets the arithmetic average of all values by calculating the sum of all
   * values divided by total count.
   */
  private void setAvarage() {
    int count = this.Count;
    double[] values = this.Values;
    double sum = 0.0;
    for (int i = 0; i < count; i++) {
      sum = sum + values[i];
    }
    this.Avarage = sum / (double) count;
  }

  /**
   * Gets the highest value out of all measurements. The unit is [Bq/m^3].
   * 
   * @return The highest value out of all measurements.
   */
  public double getMaxima() {
    return this.Maxima;
  }

  /**
   * Sets the highest value out of all measurements by comparing all values[i]
   * to find out which one is the highest. The unit is [Bq/m^3].
   */
  private void setMaxima() {
    double maxima = 0;
    int count = this.Count;
    double[] values = this.Values;
    for (int i = 0; i < count; i++) {
      if (values[i] > maxima) {
        maxima = values[i];
      }
    }
    this.Maxima = maxima;
  }

  /**
   * Gets the standard deviation of all values. The unit is [Bq/m^3].
   * 
   * @return The standard deviation of all values.
   */
  public double getDeviation() {
    return this.Deviation;
  }

  /**
   * Sets the standard deviation of all values by calculating it via the forumla
   * of the avarage square deviation: deviation = sqrt((sum(value[i] -
   * avg)^2)/count)). The unit is [Bq/m^3].
   */
  private void setDeviation() {
    double deviation = 0;
    int count = this.Count;
    double[] values = this.Values;
    double avg = this.Avarage;
    double tmpSum = 0;
    for (int i = 0; i < count; i++) {
      tmpSum = tmpSum + (values[i] - avg) * (values[i] - avg);
    }
    tmpSum = tmpSum / count;
    deviation = Math.sqrt(tmpSum);
    this.Deviation = deviation;
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
    result = prime * result + ((this.Id == null) ? 0 : this.Id.hashCode());
    result = prime * result + ((this.Type == null) ? 0 : this.Type.hashCode());
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
    if (this.Id == null) {
      if (other.Id != null)
        return false;
    } else if (!this.Id.equals(other.Id))
      return false;
    if (this.Type == null) {
      if (other.Type != null)
        return false;
    } else if (!this.Type.equals(other.Type))
      return false;
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.Id;
  }
}