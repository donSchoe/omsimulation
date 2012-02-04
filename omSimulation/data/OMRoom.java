package omSimulation.data;

import java.lang.Math;

/**
 * Public class OMRoom, defining the important attributes for the room objects
 * which are required for simulations later.
 * 
 * @author A. Schoedon
 */
public class OMRoom {
    /**
     * Stores the type of the room.
     * 
     * @see enum OMRoomType
     */
    private OMRoomType Type;

    /**
     * Stores the unique ID of the room which is used to parse the type.
     */
    private String Id;

    /**
     * Stores an array of radon values for the certain room. Each value[i]
     * equals the avarage of one hour.
     */
    private double[] Values;

    /**
     * Stores the total number of measurements for the room. The count equals
     * hours.
     */
    private int Count;

    /**
     * Stores the arithmetic average of all values.
     */
    private double Avarage;

    /**
     * Stores the highest value out of all measurements.
     */
    private double Maxima;

    /**
     * Stores the lowest values out of all measurements.
     */
    private double Minima;

    /**
     * Stores the standard deviation of all values.
     */
    private double Deviation;

    /**
     * Gets the type of the room.
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
     * Sets the unique ID of the room. In Addition, this will parse and update
     * the type.
     * 
     * @param id
     *            The unique ID of the room which is used to parse the type.
     */
    public void setId(String id) {
        this.Id = id;
        setType();
    }

    /**
     * Gets an array of radon values for the certain room. Each value[i] equals
     * the avarage of one hour.
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
     *            An array of radon values for the certain room.
     */
    public void setValues(double[] values) {
        this.Values = values;
        calculateAttributes();
    }

    /**
     * Gets the total number of measurements for the room. The count equals the
     * number of hours.
     * 
     * @return The total number of measurements for the room.
     */
    public int getCount() {
        return this.Count;
    }

    /**
     * Sets the total number of measurements for the room by simply counting
     * them. The count equals the number of hours.
     */
    private void setCount() {
        double[] values = this.Values;
        this.Count = values.length;
    }

    /**
     * Gets the arithmetic average of all values.
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
     * Gets the highest value out of all measurements.
     * 
     * @return The highest value out of all measurements.
     */
    public double getMaxima() {
        return this.Maxima;
    }

    /**
     * Sets the highest value out of all measurements by comparing all values[i]
     * to find out which one is the highest.
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
     * Gets the lowest values out of all measurements.
     * 
     * @return The lowest values out of all measurements.
     */
    public double getMinima() {
        return this.Minima;
    }

    /**
     * Sets the lowest values out of all measurements by comparing all values[i]
     * to find out which one is the lowest.
     */
    private void setMinima() {
        int count = this.Count;
        double[] values = this.Values;
        double minima = values[0];
        for (int i = 0; i < count; i++) {
            if (values[i] < minima) {
                minima = values[i];
            }
        }
        this.Minima = minima;
    }

    /**
     * Gets the standard deviation of all values.
     * 
     * @return The standard deviation of all values.
     */
    public double getDeviation() {
        return this.Deviation;
    }

    /**
     * Sets the standard deviation of all values by calculating it via the
     * forumla of the avarage square deviation: deviation = sqrt((sum(value[i] -
     * avg)^2)/count))
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
     * Constructor for objects of the class OMRoom. Creates rooms using an
     * unique ID and a set of radon values.
     * 
     * @param id
     *            The unique ID of the room which is used to parse the type.
     * @param values
     *            An array of radon values for the certain room. Each value[i]
     *            equals the avarage of one hour.
     */
    public OMRoom(String id, double[] values) {
        super();
        setId(id);
        setType();
        setValues(values);
        calculateAttributes();
    }

    /**
     * Calls the setters for the attributes Count, Median, Maxima, Minima and
     * Deviation to re-calculate and update them. Call this method always after
     * changes to the radon values. Note: It's not needed to call this after
     * using the OMRoom.setValues() method as modifying values using the setter
     * always triggers the re-calculation of attributes on its own.
     */
    public void calculateAttributes() {
        setCount();
        setAvarage();
        setMaxima();
        setMinima();
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
        result = prime * result
                + ((this.Type == null) ? 0 : this.Type.hashCode());
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