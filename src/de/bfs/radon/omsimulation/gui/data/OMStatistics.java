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

package de.bfs.radon.omsimulation.gui.data;

/**
 * Public enum OMStatistics, defining the different types of statistics:
 * RoomArithmeticMeans, RoomGeometricMeans, RoomMedianQ50, RoomMaxima,
 * CellarArithmeticMeans, CellarGeometricMeans, CellarMedianQ50, CellarMaxima.
 * 
 * @author A. Schoedon
 */
public enum OMStatistics {
  RoomArithmeticMeans, RoomGeometricMeans, RoomMedianQ50, RoomMaxima, CellarArithmeticMeans, CellarGeometricMeans, CellarMedianQ50, CellarMaxima
}
