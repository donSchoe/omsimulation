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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class OMSimulation {
  private String Name;
  private OMCampaign[] Campaigns;
  private OMBuilding Building;
  private int Total;
  private DescriptiveStatistics RoomAmDescriptiveStats;
  private DescriptiveStatistics CellarAmDescriptiveStats;
  private DescriptiveStatistics RoomGmDescriptiveStats;
  private DescriptiveStatistics CellarGmDescriptiveStats;
  private DescriptiveStatistics RoomMedDescriptiveStats;
  private DescriptiveStatistics CellarMedDescriptiveStats;
  private DescriptiveStatistics RoomMaxDescriptiveStats;
  private DescriptiveStatistics CellarMaxDescriptiveStats;

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }

  public OMCampaign[] getCampaigns() {
    return Campaigns;
  }

  public void setCampaigns(OMCampaign[] campaigns) {
    Campaigns = campaigns;
  }

  public OMBuilding getBuilding() {
    return Building;
  }

  public void setBuilding(OMBuilding building) {
    Building = building;
  }

  public int getTotal() {
    return Total;
  }

  public void setTotal(int total) {
    Total = total;
  }

  public DescriptiveStatistics getRoomAmDescriptiveStats() {
    return RoomAmDescriptiveStats;
  }

  public void setRoomAmDescriptiveStats(
      DescriptiveStatistics roomAmDescriptiveStats) {
    RoomAmDescriptiveStats = roomAmDescriptiveStats;
  }

  public DescriptiveStatistics getCellarAmDescriptiveStats() {
    return CellarAmDescriptiveStats;
  }

  public void setCellarAmDescriptiveStats(
      DescriptiveStatistics cellarAmDescriptiveStats) {
    CellarAmDescriptiveStats = cellarAmDescriptiveStats;
  }

  public DescriptiveStatistics getRoomGmDescriptiveStats() {
    return RoomGmDescriptiveStats;
  }

  public void setRoomGmDescriptiveStats(
      DescriptiveStatistics roomGmDescriptiveStats) {
    RoomGmDescriptiveStats = roomGmDescriptiveStats;
  }

  public DescriptiveStatistics getCellarGmDescriptiveStats() {
    return CellarGmDescriptiveStats;
  }

  public void setCellarGmDescriptiveStats(
      DescriptiveStatistics cellarGmDescriptiveStats) {
    CellarGmDescriptiveStats = cellarGmDescriptiveStats;
  }

  public DescriptiveStatistics getRoomMedDescriptiveStats() {
    return RoomMedDescriptiveStats;
  }

  public void setRoomMedDescriptiveStats(
      DescriptiveStatistics roomMedDescriptiveStats) {
    RoomMedDescriptiveStats = roomMedDescriptiveStats;
  }

  public DescriptiveStatistics getCellarMedDescriptiveStats() {
    return CellarMedDescriptiveStats;
  }

  public void setCellarMedDescriptiveStats(
      DescriptiveStatistics cellarMedDescriptiveStats) {
    CellarMedDescriptiveStats = cellarMedDescriptiveStats;
  }

  public DescriptiveStatistics getRoomMaxDescriptiveStats() {
    return RoomMaxDescriptiveStats;
  }

  public void setRoomMaxDescriptiveStats(
      DescriptiveStatistics roomMaxDescriptiveStats) {
    RoomMaxDescriptiveStats = roomMaxDescriptiveStats;
  }

  public DescriptiveStatistics getCellarMaxDescriptiveStats() {
    return CellarMaxDescriptiveStats;
  }

  public void setCellarMaxDescriptiveStats(
      DescriptiveStatistics cellarMaxDescriptiveStats) {
    CellarMaxDescriptiveStats = cellarMaxDescriptiveStats;
  }

  public OMSimulation(String name, OMBuilding building, OMCampaign[] campaigns,
      DescriptiveStatistics roomAmDescriptiveStats,
      DescriptiveStatistics cellarAmDescriptiveStats,
      DescriptiveStatistics roomGmDescriptiveStats,
      DescriptiveStatistics cellarGmDescriptiveStats,
      DescriptiveStatistics roomMedDescriptiveStats,
      DescriptiveStatistics cellarMedDescriptiveStats,
      DescriptiveStatistics roomMaxDescriptiveStats,
      DescriptiveStatistics cellarMaxDescriptiveStats) {
    this.Building = building;
    this.Campaigns = campaigns;
    this.RoomAmDescriptiveStats = roomAmDescriptiveStats;
    this.CellarAmDescriptiveStats = cellarAmDescriptiveStats;
    this.RoomGmDescriptiveStats = roomGmDescriptiveStats;
    this.CellarGmDescriptiveStats = cellarGmDescriptiveStats;
    this.RoomMedDescriptiveStats = roomMedDescriptiveStats;
    this.CellarMedDescriptiveStats = cellarMedDescriptiveStats;
    this.RoomMaxDescriptiveStats = roomMaxDescriptiveStats;
    this.CellarMaxDescriptiveStats = cellarMaxDescriptiveStats;
    this.Name = name;
    this.Total = campaigns.length;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(Campaigns);
    result = prime * result + ((Name == null) ? 0 : Name.hashCode());
    result = prime * result + Total;
    return result;
  }

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
    if (!Arrays.equals(Campaigns, other.Campaigns)) {
      return false;
    }
    if (Name == null) {
      if (other.Name != null) {
        return false;
      }
    } else if (!Name.equals(other.Name)) {
      return false;
    }
    if (Total != other.Total) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
    String date = dateFormat.format(new Date());
    DecimalFormat format = new DecimalFormat("#,###,###.###");
    return Name + ", " + format.format(Total) + " simulations, " + date;
  }

}
