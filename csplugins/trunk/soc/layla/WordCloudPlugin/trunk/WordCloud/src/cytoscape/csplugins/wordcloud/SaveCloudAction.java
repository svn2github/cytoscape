/*
 File: SaveCloudAction.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.wordcloud;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.cytopanels.CytoPanel;

public class SaveCloudAction extends CytoscapeAction
{
	//VARIABLES
	
	// Extensions for the new file
	public static String SESSION_EXT = ".jpg";

	
	//CONSTRUCTORS
	
	/**
	 * SaveCloudAction constructor.
	 */
	public SaveCloudAction()
	{
		super("Save Cloud Image");
	}
	
	//METHODS
	
	/**
	 * Method called when a Create Network Cloud action occurs.
	 * 
	 * @param ActionEvent - event created when choosing to save the image
	 * of a cloud.
	 */
	public void actionPerformed(ActionEvent ae) {
		
		//Open save dialog and get name of file
		
		String name; // file name

		// Open Dialog to ask user the file name.
		try {
	
			name = FileUtil.getFile("Save Current Cloud as JPEG File", FileUtil.SAVE,
			                        new CyFileFilter[] {  }).toString();
		} catch (Exception exp) {
			// this is because the selection was canceled
			return;
		}
		if (!name.endsWith(SESSION_EXT))
			name = name + SESSION_EXT;
		
		saveFile(name);
	}
	
	
	/**
	 * Method that actually creates the file
	 * @param name
	 */
	private void saveFile(String name)
	{
		//Retrieve current panel
		CloudDisplayPanel panel = SemanticSummaryManager.getInstance().getCloudWindow();
		String cloudName = SemanticSummaryManager.getInstance().getCurCloud().getCloudName();
		
		//Testing
		
		Dimension size = panel.getPreferredSize();
		JFrame frame = new JFrame(cloudName);
		//frame.setSize(size);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocation(-100, -100);
		frame.setVisible(true);
		
		Dimension newSize = frame.getPreferredSize();
	
		
		BufferedImage b = new BufferedImage(newSize.width,newSize.height,BufferedImage.TYPE_INT_RGB); /* change sizes of course */
		Graphics2D g = b.createGraphics();
		frame.printAll(g);
		try{ImageIO.write(b,"jpg",new File(name));}catch (Exception e) {}
		
		frame.dispose();
		
		new SemanticSummaryPluginAction().loadCloudPanel();
		
	}

}
