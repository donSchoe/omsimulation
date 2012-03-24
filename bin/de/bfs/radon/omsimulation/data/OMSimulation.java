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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * Public class OMSimulation used for creating simulation meta-objects with
 * custom name and all the campaigns used for analysis later. OMS simulation
 * files are DB4O databases of this type!
 * 
 * @author A. Schoedon
 */
public class OMSimulation {

  /**
   * Stores a custom name for the simulation.
   */
  private String                name;

  /**
   * Stores a date string marking the day when the simulation was run.
   * (YYYY-MM-dd)
   */
  private String                date;

  /**
   * Stores an array of campaigns after simulation.
   */
  private OMCampaign[]          campaigns;

  /**
   * Stores the used building of the simulation.
   */
  private OMBuilding            building;

  /**
   * Stores the total number of generated campaigns.
   */
  private int                   count;

  /**
   * Stores all room's arithmetic means and connected statistics.
   */
  private DescriptiveStatistics roomAmDescriptiveStats;

  /**
   * Stores all cellar's arithmetric means and connected statistics.
   */
  private DescriptiveStatistics cellarAmDescriptiveStats;

  /**
   * Stores all room's geometric means and connected statistics.
   */
  private DescriptiveStatistics roomGmDescriptiveStats;

  /**
   * Stores all cellar's geometic means and connected statistics.
   */
  private DescriptiveStatistics cellarGmDescriptiveStats;

  /**
   * Stores all room's medians and connected statistics.
   */
  private DescriptiveStatistics roomMedDescriptiveStats;

  /**
   * Stiores all cellar's medians and connected statistics.
   */
  private DescriptiveStatistics cellarMedDescriptiveStats;

  /**
   * Stores all room's maxima and connected statistics.
   */
  private DescriptiveStatistics roomMaxDescriptiveStats;

  /**
   * Stores all cellar's maxima and connected statistics.
   */
  private DescriptiveStatistics cellarMaxDescriptiveStats;

  /**
   * Gets a custom name for the simulation.
   * 
   * @return A custom name for the simulation.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets a custom name for the simulation.
   * 
   * @param name
   *          A custom name for the simulation.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets a date string marking the day when the simulation was run.
   * (YYYY-MM-dd)
   * 
   * @return A date string marking the day when the simulation was run.
   */
  public String getDate() {
    return this.date;
  }

  /**
   * Sets a date string marking the day when the simulation was run.
   * (YYYY-MM-dd)
   * 
   * @param date
   *          A date string marking the day when the simulation was run.
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * Gets an array of campaigns after simulation.
   * 
   * @return An array of campaigns.
   */
  public OMCampaign[] getCampaigns() {
    return this.campaigns;
  }

  /**
   * Sets array of campaigns after simulation.
   * 
   * @param campaigns
   *          An array of campaigns.
   */
  public void setCampaigns(OMCampaign[] campaigns) {
    this.campaigns = campaigns;
  }

  /**
   * Gets the used building of the simulation.
   * 
   * @return The used building of the simulation.
   */
  public OMBuilding getBuilding() {
    return this.building;
  }

  /**
   * Sets the used building of the simulation.
   * 
   * @param building
   *          The used building of the simulation.
   */
  public void setBuilding(OMBuilding building) {
    this.building = building;
  }

  /**
   * Gets the total number of generated campaigns.
   * 
   * @return The total number of generated campaigns.
   */
  public int getTotal() {
    return this.count;
  }

  /**
   * Sets the total number of generated campaigns.
   * 
   * @param total
   *          The total number of generated campaigns.
   */
  public void setTotal(int total) {
    this.count = total;
  }

  /**
   * Gets all room's arithmetic means and connected statistics.
   * 
   * @return All room's arithmetic means and connected statistics.
   */
  public DescriptiveStatistics getRoomAmDescriptiveStats() {
    return this.roomAmDescriptiveStats;
  }

  /**
   * Sets all room's arithmetic means and connected statistics.
   * 
   * @param roomAmDescriptiveStats
   *          All room's arithmetic means and connected statistics.
   */
  public void setRoomAmDescriptiveStats(
      DescriptiveStatistics roomAmDescriptiveStats) {
    this.roomAmDescriptiveStats = roomAmDescriptiveStats;
  }

  /**
   * Gets all cellar's arithmetric means and connected statistics.
   * 
   * @return All cellar's arithmetric means and connected statistics.
   */
  public DescriptiveStatistics getCellarAmDescriptiveStats() {
    return this.cellarAmDescriptiveStats;
  }

  /**
   * Sets all cellar's arithmetric means and connected statistics.
   * 
   * @param cellarAmDescriptiveStats
   *          All cellar's arithmetric means and connected statistics.
   */
  public void setCellarAmDescriptiveStats(
      DescriptiveStatistics cellarAmDescriptiveStats) {
    this.cellarAmDescriptiveStats = cellarAmDescriptiveStats;
  }

  /**
   * Gets all room's geometric means and connected statistics.
   * 
   * @return All room's geometric means and connected statistics.
   */
  public DescriptiveStatistics getRoomGmDescriptiveStats() {
    return this.roomGmDescriptiveStats;
  }

  /**
   * Sets all room's geometric means and connected statistics.
   * 
   * @param roomGmDescriptiveStats
   *          All room's geometric means and connected statistics.
   */
  public void setRoomGmDescriptiveStats(
      DescriptiveStatistics roomGmDescriptiveStats) {
    this.roomGmDescriptiveStats = roomGmDescriptiveStats;
  }

  /**
   * Gets all cellar's geometic means and connected statistics.
   * 
   * @return All cellar's geometic means and connected statistics.
   */
  public DescriptiveStatistics getCellarGmDescriptiveStats() {
    return this.cellarGmDescriptiveStats;
  }

  /**
   * Sets all cellar's geometic means and connected statistics.
   * 
   * @param cellarGmDescriptiveStats
   *          All cellar's geometic means and connected statistics.
   */
  public void setCellarGmDescriptiveStats(
      DescriptiveStatistics cellarGmDescriptiveStats) {
    this.cellarGmDescriptiveStats = cellarGmDescriptiveStats;
  }

  /**
   * Gets all room's medians and connected statistics.
   * 
   * @return All room's medians and connected statistics.
   */
  public DescriptiveStatistics getRoomMedDescriptiveStats() {
    return this.roomMedDescriptiveStats;
  }

  /**
   * Sets all room's medians and connected statistics.
   * 
   * @param roomMedDescriptiveStats
   *          All room's medians and connected statistics.
   */
  public void setRoomMedDescriptiveStats(
      DescriptiveStatistics roomMedDescriptiveStats) {
    this.roomMedDescriptiveStats = roomMedDescriptiveStats;
  }

  /**
   * Gets all cellar's medians and connected statistics.
   * 
   * @return All cellar's medians and connected statistics.
   */
  public DescriptiveStatistics getCellarMedDescriptiveStats() {
    return this.cellarMedDescriptiveStats;
  }

  /**
   * Sets all cellar's medians and connected statistics.
   * 
   * @param cellarMedDescriptiveStats
   *          All cellar's medians and connected statistics.
   */
  public void setCellarMedDescriptiveStats(
      DescriptiveStatistics cellarMedDescriptiveStats) {
    this.cellarMedDescriptiveStats = cellarMedDescriptiveStats;
  }

  /**
   * Gets all room's maxima and connected statistics.
   * 
   * @return All room's maxima and connected statistics.
   */
  public DescriptiveStatistics getRoomMaxDescriptiveStats() {
    return this.roomMaxDescriptiveStats;
  }

  /**
   * Sets all room's maxima and connected statistics.
   * 
   * @param roomMaxDescriptiveStats
   *          All room's maxima and connected statistics.
   */
  public void setRoomMaxDescriptiveStats(
      DescriptiveStatistics roomMaxDescriptiveStats) {
    this.roomMaxDescriptiveStats = roomMaxDescriptiveStats;
  }

  /**
   * Gets all cellar's maxima and connected statistics.
   * 
   * @return All cellar's maxima and connected statistics.
   */
  public DescriptiveStatistics getCellarMaxDescriptiveStats() {
    return this.cellarMaxDescriptiveStats;
  }

  /**
   * Sets all cellar's maxima and connected statistics.
   * 
   * @param cellarMaxDescriptiveStats
   *          All cellar's maxima and connected statistics.
   */
  public void setCellarMaxDescriptiveStats(
      DescriptiveStatistics cellarMaxDescriptiveStats) {
    this.cellarMaxDescriptiveStats = cellarMaxDescriptiveStats;
  }

  /**
   * Constructor for the simulation meta-object. Stores all the campaigns, the
   * connected statistics and the parent building.
   * 
   * @param name
   *          A custom name for the simulation.
   * @param building
   *          The used building of the simulation.
   * @param campaigns
   *          An array of campaigns after simulation.
   * @param roomAmDescriptiveStats
   *          All room's arithmetic means and connected statistics.
   * @param cellarAmDescriptiveStats
   *          All cellar's arithmetric means and connected statistics.
   * @param roomGmDescriptiveStats
   *          All room's geometric means and connected statistics.
   * @param cellarGmDescriptiveStats
   *          All cellar's geometic means and connected statistics.
   * @param roomMedDescriptiveStats
   *          All room's medians and connected statistics.
   * @param cellarMedDescriptiveStats
   *          All cellar's medians and connected statistics.
   * @param roomMaxDescriptiveStats
   *          All room's maxima and connected statistics.
   * @param cellarMaxDescriptiveStats
   *          All cellar's maxima and connected statistics.
   */
  public OMSimulation(String name, OMBuilding building, OMCampaign[] campaigns,
      DescriptiveStatistics roomAmDescriptiveStats,
      DescriptiveStatistics cellarAmDescriptiveStats,
      DescriptiveStatistics roomGmDescriptiveStats,
      DescriptiveStatistics cellarGmDescriptiveStats,
      DescriptiveStatistics roomMedDescriptiveStats,
      DescriptiveStatistics cellarMedDescriptiveStats,
      DescriptiveStatistics roomMaxDescriptiveStats,
      DescriptiveStatistics cellarMaxDescriptiveStats) {
    this.building = building;
    this.campaigns = campaigns;
    this.roomAmDescriptiveStats = roomAmDescriptiveStats;
    this.cellarAmDescriptiveStats = cellarAmDescriptiveStats;
    this.roomGmDescriptiveStats = roomGmDescriptiveStats;
    this.cellarGmDescriptiveStats = cellarGmDescriptiveStats;
    this.roomMedDescriptiveStats = roomMedDescriptiveStats;
    this.cellarMedDescriptiveStats = cellarMedDescriptiveStats;
    this.roomMaxDescriptiveStats = roomMaxDescriptiveStats;
    this.cellarMaxDescriptiveStats = cellarMaxDescriptiveStats;
    this.name = name;
    this.count = campaigns.length;
    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
    this.date = dateFormat.format(new Date());
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(this.campaigns);
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + this.count;
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
    OMSimulation other = (OMSimulation) obj;
    if (!Arrays.equals(this.campaigns, other.campaigns)) {
      return false;
    }
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    if (this.count != other.count) {
      return false;
    }
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    DecimalFormat format = new DecimalFormat("#,###,###.###");
    return this.name + ", " + format.format(this.count) + " simulations, "
        + this.date;
  }

}
