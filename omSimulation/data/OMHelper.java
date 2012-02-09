package omSimulation.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Public abstract class OMHelper providing helper methods for writing
 * simulation logs and doing calculations. Can not be instantiated.
 * 
 * @author A. Schoedon
 */
public abstract class OMHelper {

  /**
   * Stores the generic log writer, used for the whole program's log output.
   */
  private static BufferedWriter LogOutput;

  /**
   * Sets the log-writer, creating a timestamp, a filename based on the
   * timestamp and the project name and finally initializing the log writer.
   * 
   * @param projectName
   *          The name of the current project to identify the log file.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public static void setLogOutput(String projectName) throws IOException {
    Format format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
    double current = System.currentTimeMillis();
    String timestamp = format.format(current);
    if (projectName == null) {
      projectName = "main";
    } else {
      if (projectName.length() == 0) {
        projectName = "main";
      }
    }
    String logName = timestamp + "_" + projectName + ".log";
    File logFile = new File(logName);
    FileWriter logWriter = new FileWriter(logFile);
    BufferedWriter logOutput = new BufferedWriter(logWriter);
    LogOutput = logOutput;
  }

  /**
   * Calculates the variation coefficient using the arithmetric mean and
   * standard deviation.
   * 
   * @param arithMean
   *          The arithmetric mean.
   * @param stdDeviation
   *          The standard deviation.
   * @return The variations coefficient.
   */
  public static double calculateCV(double arithMean, double stdDeviation) {
    double cv = stdDeviation / arithMean;
    return cv;
  }

  /**
   * Calculates the quantile deviations using the quantiles 5, 50 and 95.
   * 
   * @param q5
   *          The quantile 5.
   * @param q50
   *          The quantile 50 (median).
   * @param q95
   *          The quantile 95).
   * @return The quantile deviation.
   */
  public static double calculateQD(double q5, double q50, double q95) {
    double qd = (q95 - q5) / q50;
    return qd;
  }

  /**
   * Calculates the geometric standard deviation.
   * 
   * @param n
   *          The number of values.
   * @param values
   *          An array of values.
   * @param geoMean
   *          The geometric mean of the values.
   * @return The geometric standard deviation.
   */
  public static double calculateGSD(long n, double[] values, double geoMean) {
    double gsd = 0.0;
    double[] logValues = new double[(int)n];
    for (int i = 0; i < n; i++) {
      if (values[i] != 0) {
        logValues[i] = Math.log(values[i]);
      } else {
        logValues[i] = 0;
      }
      gsd = gsd + (logValues[i] / geoMean) * (logValues[i] / geoMean);
    }
    gsd = gsd / n;
    gsd = Math.sqrt(gsd);
    gsd = Math.exp(gsd);
    return gsd;
  }

  /**
   * Calculates the factorial of n!.
   * 
   * @param n
   *          Integer value which is used to calculate factorial of n!.
   * @return The factorial n!.
   */
  public static int calculateFactorial(int n) {
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
  public static String getTimestamp() {
    Format format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss,SSS ");
    double current = System.currentTimeMillis();
    String timestamp = format.format(current);
    return timestamp;
  }

  /**
   * Writes a new line with a timestamp to the log file using the
   * BufferedReader. Call this method once per line to write.
   * 
   * @param newLine
   *          A string to write into the log file.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public static void writeLog(String newLine) throws IOException {
    LogOutput.write(getTimestamp() + newLine);
    LogOutput.newLine();
  }

  /**
   * Helper method to close the log file and the BufferedReader.
   * 
   * @throws IOException
   *           If closing log file or writing logs fails.
   */
  public static void closeLog() throws IOException {
    LogOutput.close();
  }
}
