------------------------------------
* OM Simulation Tool v0.4.46-beta4 *
------------------------------------

Copyright (C) 2012 Alexander Schoedon <a.schoedon@student.htw-berlin.de>


1. Introduction
----------------------------------
This software is a simulation tool for virtual orientated measurement (OM) 
campaigns following the protocol "6+1" to determine and evaluate the level 
of radon exposure in buildings.

For questions regarding this project contact:
- Dr. Bernd Hoffmann <bhoffmann@bfs.de>
    - Strahlenschutz und Umwelt SW 1.1 Radon
     (Radiation Protection and Environment SW 1.1 Radon)
	- Bundesamt für Strahlenschutz
	 (German Federal Office for Radiation Protection)

For questions concerning this software tool contact:
- Alexander Schoedon <a.schoedon@student.htw-berlin.de>
    - Betriebliche Umweltinformatik
	 (Industrial Environmental Informatics)
	- Hochschule für Technik und Wirtschaft Berlin
	 (University of Applied Sciences)


2. License
----------------------------------
This program is free software: you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation, either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
this program. If not, see <http://www.gnu.org/licenses/>.


3. Latest Version
----------------------------------
The latest version is available at github:
- http://github.com/donschoe/omsimulation


4. Documentation
----------------------------------
Documentation can be found at the github wiki:
- http://github.com/donschoe/omsimulation/wiki

The documentation includes a tutorial on installing and running this software.
- http://github.com/donschoe/omsimulation/wiki/installation

For source API, have a look at the javadocs in ./doc/index.html


5. Dependencies
----------------------------------
The OM-Simulation tool requires JDK 1.7.0 (Java SE 7) or higher.

The following 3rd-party-libraries are required:
- Apache Commons Math 2.2 or higher.
- DB4O 8.0 or higher.
- iTextPDF 5.2.0 or higher.
- JCommon 1.0.17 or higher.
- JFreeChart 1.0.14 or higher.
- JCalendar 1.4 or higher.

These libs are included in this build. Please note the remark at the end
of this document (8. Notice).


6. Changelog
----------------------------------
0.4.46-beta4: (2012-03-10)
    - fixed the batch script for windows
    - updated documentation and readme

0.4.45-beta3: (2012-03-10)
    - fixed some minor unicode issues
    - created ant build script
      - created manifest
      - created javadocs
      - created launch scripts
    - ready to roll

0.4.44-beta3: (2012-03-09)
    - fixed: threads tend to collide
	- fixed: some minor bugs
	- fixed: some performance issues
	- created buffer for refreshing charts in background thread
	- updated log strings

0.4.43-beta2: (2012-03-08)
    - added code documentation
	- renamed field variables to fit with java conventions (lowerCamelCase)
	- removed obsolete status-update methods

0.4.42-beta1: (2012-03-08)
    - misc rooms can now be inspected in data panel
	- all values below detection limit are now treated the same way: =limit/2
	- distribution charts for rooms are now red and for cellars are now blue
	- added date chooser for import panel
	- fixed: caught exceptions when updating background tabs
	- fixed: export buttons went missing on testing panel while analyzing

0.4.41-alpha1: (2012-03-06)
    - 0.3:40-dev fork to start public testing

0.3.40-dev: (2012-03-06)
    - added PDF+CSV exports to all charts
    - caught malformed input
    - added message boxes to all panels to
      - either inform the user that everything went well
      - or to spam warning and error messages if input is incorrect
    - updated icons to work from within JAR files
    - final devel commit, public testing ahead
	
0.3.38-dev: (2012-03-04)
    - campaigns now store roompatterns
    - simulations now store buildings
    - logs and csv-results are stored at the same location as omb/oms files
    - tab navigation / cross db loading implemented
    - 3rd party libs added for pdf exports
	
0.3.37-dev: (2012-03-01)
    - updated all panels to new file-based persistence system
    - updated all swing items to have the same font
    - added help texts
    - added the about panel
    - added some logos 
	
0.3.36-dev: (2012-02-29)
    - updated progress bars of all the panels
    - created createDistributionChart() in OMCharts.java
    - create OMStatistics.java enum type for dropdown menu in results panel
    - added distribution panel to results panel
	
0.3.35-dev: (2012-02-28)
    - moved charts to external class
    - some improvements made to charts
    - started working on simulation results
      - added OMSimulation.java to store simulations
      - prepared results panel
	
0.3.34-dev: (2012-02-27)
    - testing panel improved
	
0.3.33-dev: (2012-02-23)
    - testing panel: charts look great now
    - fixed some serious bugs in OMCampaign
      - while collecting values for cellar and rooms, the cellar position was
        not taken into account
      - this was only noticeable through the graphical analysis
      - should work pretty fine now 
	
0.3.32-dev: (2012-02-22)
    - started working on the testing panel 
	
0.3.31-dev: (2012-02-21)
    - finished data panel including charts
	
0.3.30-dev: (2012-02-20)
    - added data panel
    - added jfreechart libraries to plot graphs

0.3.29-dev: (2012-02-19)
    - added file browser dialog to import panel
    - fixed buggs in simulation panel
    - started working on data panel

0.3.28-dev: (2012-02-16)
    - finished simulation panel
    - fixed alot of stuff in import panel
	- created threading to improve performance

0.3.27-dev: (2012-02-16)
    - finished import panel
    - renamed namespace according to java conventions

0.3.25-dev: (2012-02-13)
    - started some serious work on the gui
      - tab panel for file imports created
      - main method now moved to mainframe 

0.3.21-dev: (2012-02-08)
    - added *.gui package
    - added a jframe swing window 
	
0.3.20-dev: (2012-02-07)
    - v0.2:19-dev fork for desktop application

0.2.19-dev: (2012-02-07)
    - final commit for v0.2 prototype (database application) 

0.2.18-dev: (2012-02-07)
    - fix: status (perc) was calculated wrong (int too small data type)
    - fix: calculation of total simulations was wrong: last possible
           simulation got truncated
    - fix: random was calculated wrong: last integer has to be excluded,
           simulation got truncated (random only)
    - fix: valueCount was calculated wrong: last data set got truncated 

0.2.17-dev: (2012-02-06)
    - finished benchmark tests (3b)
    - some minor code fixes
    - whole documentation updated
    - switched to 2-spaces indentation 

0.2.16-dev: (2012-02-03)
    - finished standard tests (2s)
    - some minor code fixes 
	
0.2.15-dev: (2012-02-02)
    - some minor code fixes
	
0.2.14-dev: (2012-02-02)
    - ran all possible exception tests (1a)
    - caught a lot of exceptions and fixed the code 
	
0.2.13-dev: (2012-02-01)
    - finished with all campaign-generation algorithms
    - main functionallity implemented, ready for testing
    - created new testdata to simulate many different setups
	
0.2.12-dev: (2012-01-31)
    - created new class for helper functions (log, calc, etc..)
    - created testdate to simulate many different setups
    - updated buildings to generate its own variation schemes (3/4/5/6 of x)
    - started working on generation of campaigns, not finished yet 

0.2.10-dev: (2012-01-30)
    - created new class for buildings to simplify object storage
    - added object database (with objects)
    - added libraries for apache commons math 2.2
    - updated calculations using descriptive and summery statistics
    - added licenses 

0.2.9-dev: (2012-01-27)
    - added object database (empty)
    - added complete calculations and statistics 
	
0.2.8-dev: (2012-01-26)
    - added campaign types
    - completely overhauled campaigns to separately calculate attributes for
      both: normal rooms and cellars
    - added the functionality to add random noise to campaigns

    - fixed empty measurement values using the half of detection limit
    - prepared libraries for db4b object database 
	
0.2.6-dev: (2012-01-24)
    - v0.1:5-dev fork for database application
	
0.1.5-dev: (2012-01-20)
    - final commit for v0.1 prototype (command line application)
    - fixed last bugs using several tests 


7. Contributors
----------------------------------
- Alexander Schoedon <a.schoedon@student.htw-berlin.de>


8. Notice
----------------------------------
This product includes software developed by
- The Apache Software Foundation (http://www.apache.org/): Commons.Math
- The Versant Corporation (http://www.versant.com/): DB4O
- The Object Refinery Limited (http://www.object-refinery.com/): JFreeChart,
                                                                 JFreeCommon
- The iText Software Corp. and 1T3XT BVBA (http://itextpdf.com/): iTextPDF
- Kai Tödter (http://toedter.com/): JCalendar

For more details about these products see the separate README(s),
NOTICE(s) and LICENSE(s).


9. Troubleshooting
----------------------------------
If you come accross any problems or need any further help,
please file an issue:
- http://github.com/donschoe/omsimulation/issues


