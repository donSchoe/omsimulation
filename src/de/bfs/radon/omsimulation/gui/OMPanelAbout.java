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

package de.bfs.radon.omsimulation.gui;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

/**
 * Creates and shows the credits panel for this software tool.
 * 
 * @author A. Schoedon
 */
public class OMPanelAbout extends JPanel {

  /**
   * UI: Icon label: Bundesamt fuer Strahlenschutz.
   */
  private JLabel            lblLogoBfs;

  /**
   * UI: Icon label: Hochschule fuer Technik und Wirtschaft Berlin.
   */
  private JLabel            lblLogoHtw;

  /**
   * UI: Icon label: GNU General Public License 3.
   */
  private JLabel            lblLogoGpl;

  /**
   * UI: Text pane including software description and authors.
   */
  private JTextPane         txtpnCredits;

  /**
   * UI: Text pane including licensing terms and links to source code.
   */
  private JTextPane         txtpnLicense;

  /**
   * Unique serial version ID.
   */
  private static final long serialVersionUID = 7750782641349856002L;

  /**
   * Creates the credits panel with project and author information aswell as
   * some shiny logos.
   * 
   * @param version
   *          The version string to display the most current version number.
   */
  public OMPanelAbout(String version) {
    setLayout(null);

    txtpnCredits = new JTextPane();
    txtpnCredits.setBackground(null);
    txtpnCredits.setForeground(Color.GRAY);
    txtpnCredits.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    txtpnCredits.setEditable(false);
    txtpnCredits.setText("OM Simulation Tool " + version
        + ", April/2012.\r\n\r\nThis tool intends to test and evaluate the"
        + " scientific robustness of the protocol \"6+1\". Therefore, it"
        + " generates a huge amount of virtual measurement campaigns based"
        + " on real radon concentration data following the mentioned protocol."
        + "\r\n\r\nImplementation by Alexander"
        + " Schoedon, <a.schoedon@student.htw-berlin.de>\r\nBetriebliche"
        + " Umweltinformatik, Industrial Environmental Informatics\r\n"
        + "Hochschule f\u00FCr Technik und Wirtschaft Berlin, University "
        + "of Applied Sciences\r\n\r\nProject by Dr. Bernd Hoffmann, "
        + "<bhoffmann@bfs.de>\r\nStrahlenschutz und Umwelt SW 1.1,"
        + " Radiation Protection and Environment SW 1.1\r\nBundesamt"
        + " f\u00FCr Strahlenschutz, German Federal Office for Radiation"
        + " Protection");
    txtpnCredits.setBounds(10, 10, 520, 298);
    add(txtpnCredits);

    lblLogoBfs = new JLabel(
        createImageIcon("/de/bfs/radon/omsimulation/gui/img/bfs.png"));
    lblLogoBfs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblLogoBfs.setBounds(613, 10, 127, 50);
    add(lblLogoBfs);

    lblLogoHtw = new JLabel(
        createImageIcon("/de/bfs/radon/omsimulation/gui/img/htw.png"));
    lblLogoHtw.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblLogoHtw.setBounds(613, 72, 127, 74);
    add(lblLogoHtw);

    lblLogoGpl = new JLabel(
        createImageIcon("/de/bfs/radon/omsimulation/gui/img/gpl.png"));
    lblLogoGpl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblLogoGpl.setBounds(613, 444, 127, 51);
    add(lblLogoGpl);

    txtpnLicense = new JTextPane();
    txtpnLicense
        .setText("This program is free software: you can redistribute it "
            + "and/or modify it under the terms of the GNU General Public License "
            + "as published by the Free Software Foundation, either version 3 "
            + "of the License, or (at your option) any later version.\r\n\r\n"
            + "The source code is available at: http://github.com/donschoe/omsimulation");
    txtpnLicense.setForeground(Color.GRAY);
    txtpnLicense.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    txtpnLicense.setEditable(false);
    txtpnLicense.setBackground((Color) null);
    txtpnLicense.setBounds(10, 400, 520, 95);
    add(txtpnLicense);

  }

  /**
   * Creates an image icon from resources. This is important to keep icons
   * available in JAR files or similar.
   * 
   * @param path
   *          the relative path to the image.
   * @return The image icon.
   */
  public ImageIcon createImageIcon(String path) {
    URL imageURL = this.getClass().getResource(path);
    if (path != null) {
      ImageIcon icon = new ImageIcon(imageURL);
      return icon;
    } else {
      return null;
    }
  }
}
