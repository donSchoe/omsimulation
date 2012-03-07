package de.bfs.radon.omsimulation.gui;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;

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

public class OMPanelAbout extends JPanel {

  private static final long serialVersionUID = 7750782641349856002L;

  /**
   * Create the panel.
   */
  public OMPanelAbout(String version) {
    setLayout(null);

    JTextPane txtpnTest = new JTextPane();
    txtpnTest.setBackground(null);
    txtpnTest.setForeground(UIManager.getColor("CheckBox.darkShadow"));
    txtpnTest.setFont(new Font("SansSerif", Font.PLAIN, 12));
    txtpnTest.setEditable(false);
    txtpnTest.setText("OM Simulation Tool " + version
        + ", 03/2012.\r\n\r\nThis software is a simulation tool for "
        + " virtual orientated measurement (OM) campaigns following the"
        + " protocol \"6+1\" to determine and evaluate the level of radon"
        + " exposure in buildings.\r\n\r\nImplementation by Alexander"
        + " Schoedon, <a.schoedon@student.htw-berlin.de>\r\nBetriebliche"
        + " Umweltinformatik, Industrial Environmental Informatics\r\n"
        + "Hochschule f\u00FCr Technik und Wirtschaft Berlin, University "
        + "of Applied Sciences\r\n\r\nProject by Dr. Bernd Hoffmann, "
        + "<bhoffmann@bfs.de>\r\nStrahlenschutz und Umwelt SW 1.1,"
        + " Radiation Protection and Environment SW 1.1\r\nBundesamt"
        + " f\u00FCr Strahlenschutz, German Federal Office for Radiation"
        + " Protection");
    txtpnTest.setBounds(10, 11, 520, 198);
    add(txtpnTest);

    JLabel lblLogoBfs = new JLabel(createImageIcon(
        "/de/bfs/radon/omsimulation/gui/img/bfs.png"));
    lblLogoBfs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblLogoBfs.setBounds(613, 159, 127, 50);
    add(lblLogoBfs);

    JLabel lblLogoHtw = new JLabel(createImageIcon(
        "/de/bfs/radon/omsimulation/gui/img/htw.png"));
    lblLogoHtw.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblLogoHtw.setBounds(613, 74, 127, 74);
    add(lblLogoHtw);

    JLabel lblLogoGpl = new JLabel(createImageIcon(
        "/de/bfs/radon/omsimulation/gui/img/gpl.png"));
    lblLogoGpl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblLogoGpl.setBounds(613, 429, 127, 51);
    add(lblLogoGpl);

    JTextPane txtpnThisSoftwareIs = new JTextPane();
    txtpnThisSoftwareIs
        .setText("This program is free software: you can redistribute it "
            + "and/or modify it under the terms of the GNU General Public License "
            + "as published by the Free Software Foundation, either version 3 "
            + "of the License, or (at your option) any later  version.");
    txtpnThisSoftwareIs.setForeground(Color.DARK_GRAY);
    txtpnThisSoftwareIs.setFont(new Font("SansSerif", Font.PLAIN, 12));
    txtpnThisSoftwareIs.setEditable(false);
    txtpnThisSoftwareIs.setBackground((Color) null);
    txtpnThisSoftwareIs.setBounds(10, 429, 520, 70);
    add(txtpnThisSoftwareIs);

  }

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
