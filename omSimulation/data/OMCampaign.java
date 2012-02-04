package omSimulation.data;

import java.util.Arrays;

/**
 * Public class OMCampaign, defining the simulated field campaigns and its
 * attributes.
 * 
 * @author A. Schoedon
 */
public class OMCampaign {

    /**
     * Stores the timestamp of the first measurement for the simulated campaign,
     * starting with 0 for the first hour of the real value-sets.
     */
    private int Start;

    /**
     * Stores an array of rooms of the simulated survey campaign.
     */
    private OMRoom[] Rooms;

    /**
     * Stores an array of selected values of all 7 rooms. The first values is
     * determined by the start timestamp.
     */
    private double[] Values;

    /**
     * Stores an array of logarithmic values of the normal values. Used for
     * geometric mean later on.
     */
    private double[] LogValues;

    /**
     * Stores the arithmetic average of the selected values. Day0: Room0, day1:
     * Room1, ... etc.
     */
    private double Avarage;

    /**
     * Stores the highest value out of the selected measurements. Day0: Room0,
     * day1: Room1, ... etc.
     */
    private double Maxima;

    /**
     * Stores the lowest value out of the selected measurements. Day0: Room0,
     * day1: Room1, ... etc.
     */
    private double Minima;

    /**
     * Stores the standard deviation of the selected values. Day0: Room0, day1:
     * Room1, ... etc.
     */
    private double Deviation;

    /**
     * Gets the timestamp of the first measurement for the simulated campaign.
     * 
     * @return The timestamp of the first measurement for the simulated
     *         campaign.
     */

    /**
     * Stores the variation coefficient which determines the ration between
     * arithmetic mean and standard deviation.
     */
    private double VarCoefficient;

    /**
     * Stores the range of the values which is calculated by subtracting the
     * minimum by the maximum value.
     */
    private double Range;

    /**
     * Stores the quantile 5, where only 5% of the values are lower.
     */
    private double Quantile05;

    /**
     * Stores the quantile 95, where only 5% of the values are higher.
     */
    private double Quantile95;

    /**
     * Stores the geometric mean.
     */
    private double LogAvarage;

    /**
     * Stores the geometric standard deviation.
     */
    private double LogDeviation;

    /**
     * Gets the timestamp of the first measurement for the simulated campaign.
     * 
     * @return The timestamp of the first measurement for the simulated
     *         campaign.
     */
    public int getStart() {
        return Start;
    }

    /**
     * Sets the timestamp of the first measurement for the simulated campaign.
     * 
     * @param start
     *            The timestamp of the first measurement for the simulated
     *            campaign, 0 equals the first hour of the real value-sets.
     */
    public void setStart(int start) {
        this.Start = start;
        calculateAttributes();
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
     * re-calculation of all attributes
     * 
     * @param rooms
     *            An array of rooms of the simulated survey campaign.
     */
    public void setRooms(OMRoom[] rooms) {
        this.Rooms = rooms;
        calculateAttributes();
    }

    /**
     * Gets an array of selected values of all 7 rooms. The first values is
     * determined by the start timestamp.
     * 
     * @return An array of selected values of all 7 rooms.
     */
    public double[] getValues() {
        return this.Values;
    }

    /**
     * Sets an array of selected values of all 7 rooms. The first values is
     * determined by the start timestamp. In the end, the values get sorted from
     * lowest to highest.
     */
    private void setValues() {
        OMRoom[] rooms = this.Rooms;
        int start = this.Start;
        int total = 168;
        int day = 24;
        double[] values = new double[total];
        int x = 0;
        double[] tmpValues = rooms[0].getValues();
        for (int i = start; i < start + day; i++) {
            values[x] = (double) tmpValues[i];
            x++;
        }
        start = start + day;
        tmpValues = rooms[1].getValues();
        for (int i = start; i < start + day; i++) {
            values[x] = (double) tmpValues[i];
            x++;
        }
        start = start + day;
        tmpValues = rooms[2].getValues();
        for (int i = start; i < start + day; i++) {
            values[x] = (double) tmpValues[i];
            x++;
        }
        start = start + day;
        tmpValues = rooms[3].getValues();
        for (int i = start; i < start + day; i++) {
            values[x] = (double) tmpValues[i];
            x++;
        }
        start = start + day;
        tmpValues = rooms[4].getValues();
        for (int i = start; i < start + day; i++) {
            values[x] = (double) tmpValues[i];
            x++;
        }
        start = start + day;
        tmpValues = rooms[5].getValues();
        for (int i = start; i < start + day; i++) {
            values[x] = (double) tmpValues[i];
            x++;
        }
        start = start + day;
        tmpValues = rooms[6].getValues();
        for (int i = start; i < start + day; i++) {
            values[x] = (double) tmpValues[i];
            x++;
        }
        Arrays.sort(values);
        this.Values = values;
    }

    /**
     * Gets an array of logarithmic values of the normal values. Used for
     * geometric mean later on.
     * 
     * @return An array of logarithmic values.
     */
    public double[] getLogValues() {
        return this.LogValues;
    }

    /**
     * Sets an array of logarithmic values using the sorted normal values. Used
     * for geometric mean later on.
     */
    private void setLogValues() {
        double[] values = this.Values;
        double[] logValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] > 0) {
                logValues[i] = Math.log(values[i]);
            } else {
                logValues[i] = 0;
            }
        }
        this.LogValues = logValues;
    }

    /**
     * Gets the arithmetic average of the selected values, for day0: Room0,
     * day1: Room1, ... etc.
     * 
     * @return The central tendency of the selected values.
     */
    public double getAvarage() {
        return this.Avarage;
    }

    /**
     * Sets the arithmetic average of the selected values, for day0: Room0,
     * day1: Room1, ... etc.
     */
    private void setAvarage() {
        double[] values = this.Values;
        double sum = 0.0;
        for (int i = 0; i < values.length; i++) {
            sum = sum + values[i];
        }
        double avg = sum / values.length;
        this.Avarage = avg;
    }

    /**
     * Gets the highest value out of the selected measurements, for day0: Room0,
     * day1: Room1, ... etc.
     * 
     * @return The highest value out of the selected measurements.
     */
    public double getMaxima() {
        return this.Maxima;
    }

    /**
     * Sets the highest value out of the selected measurements, for day0: Room0,
     * day1: Room1, ... etc.
     */
    private void setMaxima() {
        double[] values = this.Values;
        double maxima = values[values.length - 1];
        this.Maxima = maxima;
    }

    /**
     * Gets the lowest value out of the selected measurements, for day0: Room0,
     * day1: Room1, ... etc.
     * 
     * @return The lowest value out of the selected measurements.
     */
    public double getMinima() {
        return this.Minima;
    }

    /**
     * Sets the lowest value out of the selected measurements, for day0: Room0,
     * day1: Room1, ... etc.
     */
    private void setMinima() {
        double[] values = this.Values;
        double minima = values[0];
        this.Minima = minima;
    }

    /**
     * Gets the standard deviation of the selected measurements, for day0:
     * Room0, day1: Room1, ... etc.
     * 
     * @return The standard deviation of the selected measurements.
     */
    public double getDeviation() {
        return this.Deviation;
    }

    /**
     * Sets the standard deviation of the selected measurements, for day0:
     * Room0, day1: Room1, ... etc.
     */
    private void setDeviation() {
        double dev = 0;
        double[] values = this.Values;
        double avg = this.Avarage;
        double tmpSum = 0;
        for (int i = 0; i < values.length; i++) {
            tmpSum = tmpSum + (values[i] - avg) * (values[i] - avg);
        }
        tmpSum = tmpSum / values.length;
        dev = Math.sqrt(tmpSum);
        this.Deviation = dev;
    }

    /**
     * Gets the variation coefficient which determines the ration between
     * arithmetic mean and standard deviation.
     * 
     * @return The variation coefficient.
     */
    public double getVarCoefficient() {
        return this.VarCoefficient;
    }

    /**
     * Sets the variation coefficient which determines the ration between
     * arithmetic mean and standard deviation.
     */
    private void setVarCoefficient() {
        double avg = this.Avarage;
        double dev = this.Deviation;
        double vc = dev / avg;
        this.VarCoefficient = vc;
    }

    /**
     * Gets the range of the values which is calculated by subtracting the
     * minimum by the maximum value.
     * 
     * @return The range of the values.
     */
    public double getRange() {
        return this.Range;
    }

    /**
     * Sets the range of the values which is calculated by subtracting the
     * minimum by the maximum value.
     */
    private void setRange() {
        double min = this.Minima;
        double max = this.Maxima;
        double range = max - min;
        this.Range = range;
    }

    /**
     * Gets the quantile 5, where only 5% of the values are lower.
     * 
     * @return The quantile 5.
     */
    public double getQuantile05() {
        return this.Quantile05;
    }

    /**
     * Sets the quantile 5, where only 5% of the values are lower.
     */
    private void setQuantile05() {
        double[] values = this.Values;
        double x = ((double) values.length / 100.0) * 5.0;
        int i = (int) x - 1;
        double q05 = values[i];
        this.Quantile05 = q05;
    }

    /**
     * Gets the quantile 95, where only 5% of the values are higher.
     * 
     * @return The quantile 95.
     */
    public double getQuantile95() {
        return this.Quantile95;
    }

    /**
     * Sets the quantile 95, where only 5% of the values are higher.
     */
    private void setQuantile95() {
        double[] values = this.Values;
        double x = (double) values.length
                - (((double) values.length / 100.0) * 5.0);
        int i = (int) x - 1;
        double q95 = values[i];
        this.Quantile95 = q95;
    }

    /**
     * Gets the geometric mean.
     * 
     * @return The geometric mean.
     */
    public double getLogAvarage() {
        return this.LogAvarage;
    }

    /**
     * Sets the geometric mean.
     */
    private void setLogAvarage() {
        double[] logValues = this.LogValues;
        double sum = 0.0;
        for (int i = 0; i < logValues.length; i++) {
            sum = sum + logValues[i];
        }
        double tmpAvarage = Math.exp(sum / logValues.length);
        this.LogAvarage = tmpAvarage;
    }

    /**
     * Gets the geometric standard deviation.
     * 
     * @return The geometric standard deviation.
     */
    public double getLogDeviation() {
        return this.LogDeviation;
    }

    /**
     * Sets the geometric standard deviation.
     */
    private void setLogDeviation() {
        double[] logValues = this.LogValues;
        double logAvarage = this.LogAvarage;
        double tmp = 0.0;
        for (int i = 0; i < logValues.length; i++) {
            if (logValues[i] > 0) {
                tmp = tmp + Math.log(logValues[i] / logAvarage)
                        * Math.log(logValues[i] / logAvarage);
            }
        }
        double logDeviation = Math.exp(tmp / logValues.length);
        this.LogDeviation = logDeviation;
    }

    /**
     * Constructor for objects of the class OMCampaign. Creates survey campaigns
     * for 7 days in 7 different Rooms.
     * 
     * @param start
     *            The timestamp of the first measurement for the simulated
     *            campaign, starting with 0 for the first hour of the real
     *            value-sets.
     * @param rooms
     *            An array of seven rooms for the seven days (steps) of the
     *            simulated campaign. For first day: rooms[0], second day:
     *            rooms[1], ... etc.
     */
    public OMCampaign(int start, OMRoom[] rooms) {
        super();
        this.Start = start;
        try {
            this.Rooms = new OMRoom[7];
            this.Rooms[0] = rooms[0];
            this.Rooms[1] = rooms[1];
            this.Rooms[2] = rooms[2];
            this.Rooms[3] = rooms[3];
            this.Rooms[4] = rooms[4];
            this.Rooms[5] = rooms[5];
            this.Rooms[6] = rooms[6];
            calculateAttributes();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Calls the setters for the attributes Count, Median, Maxima, Minima and
     * Deviation to re-calculate and update them. Call this method always after
     * changes to the radon values. Note: It's not needed to call this after
     * using the OMCampaign.setRoomX()-methods or OMCampaign.setStart()-method
     * as modifying values using the setter always triggers the re-calculation
     * of attributes on its own.
     */
    public void calculateAttributes() {
        setValues();
        setLogValues();
        setAvarage();
        setMaxima();
        setMinima();
        setDeviation();
        setVarCoefficient();
        setRange();
        setQuantile05();
        setQuantile95();
        setLogAvarage();
        setLogDeviation();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.Rooms);
        result = prime * result + this.Start;
        result = prime * result + Arrays.hashCode(this.Values);
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
        OMCampaign other = (OMCampaign) obj;
        if (!Arrays.equals(this.Rooms, other.Rooms))
            return false;
        if (this.Start != other.Start)
            return false;
        if (!Arrays.equals(this.Values, other.Values))
            return false;
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Campaign: T=" + this.Start + ", R=" + this.Rooms[0].getId()
                + this.Rooms[1].getId() + this.Rooms[2].getId()
                + this.Rooms[3].getId() + this.Rooms[4].getId()
                + this.Rooms[5].getId() + this.Rooms[6].getId() + ", AM="
                + this.Avarage + ", SD=" + this.Deviation + ", CV="
                + this.VarCoefficient;
    }
}
