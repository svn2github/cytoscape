/* BEGIN_HEADER                                              Java TreeView
*
* $Author: rqluk $
* $RCSfile: KaryoViewExportPanel.java,v $
* $Revision: 1.1 $
* $Date: 2006/08/16 19:13:49 $
* $Name:  $
*
* This file is part of Java TreeView
* Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
*
* This software is provided under the GNU GPL Version 2. In particular, 
*
* 1) If you modify a source file, make a comment in it containing your name and the date.
* 2) If you distribute a modified version, you must do it under the GPL 2.
* 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
*
* A full copy of the license can be found in gpl.txt or online at
* http://www.gnu.org/licenses/gpl.txt
*
* END_HEADER 
*/
package edu.stanford.genetics.treeview.plugin.karyoview;

import edu.stanford.genetics.treeview.*;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.event.*;
/**
* This class is a superclass which implements a GUI for selection of options relating to output. 
* It makes most of the relevant variables accessible to subclasses through protected methods.
*
* The chooseable widgets should consist of a

* - listbox full of chromosomes to choose from initially with just the visible ones chosen.
* - field values to indicate the x scale and y scale, relative to the current displayed image.
* - non-editable field values indicating the predicted total size.
*/

public abstract class KaryoViewExportPanel extends JPanel implements SettingsPanel {
	private ConfigNode root;
	
	// external links
	private KaryoView karyoView;        // we're not doing anything clever here...
	private FileSet sourceSet;          // FileSet from which current data was constructed.
	
	// accessors
	
	public FileSet getSourceSet() {
		return sourceSet;
	}
	public void setSourceSet( FileSet fs) {
		sourceSet = fs;
		if (filePanel != null) {
			filePanel.setFilePath(getInitialFilePath());
		}
	}
	
	
	/*
	* for communication with subclass...
	*/
	// components
	private FilePanel filePanel;
	private InclusionPanel inclusionPanel;
	private PreviewPanel previewPanel;
	
	// accessors for configuration information
	
	protected File getFile() {
		return filePanel.getFile();
	}
	public String getFilePath() {
		return filePanel.getFilePath();
	}
	public void setFilePath(String newFile) {
		filePanel.setFilePath(newFile);
	}
	protected String getInitialExtension() {
		return ".png";
	}
	protected String getInitialFilePath() {
		String defaultPath = null;
		if (sourceSet == null) {
			defaultPath = System.getProperty("user.home") + System.getProperty("file.separator")  + "scatterplot" + getInitialExtension();
		} else {
			defaultPath = sourceSet.getDir() + sourceSet.getRoot() + getInitialExtension();
		}
		if (root == null) {
			return defaultPath;
		} else {
			return root.getAttribute("file", defaultPath);
		}
	}
	
	
	/**
	* This export panel literally just prints out the image to the specified file, with no configuration possible.
	*/
	public KaryoViewExportPanel(KaryoView karyoView) {
		this.karyoView = karyoView;
		setupWidgets();
		inclusionPanel.synchSelected();
		inclusionPanel.synchEnabled();
	}
	public void bindConfig(ConfigNode configNode)
	{
		root = configNode;
	}
	public ConfigNode createSubNode()
	{
		return root.create("File");
	}
	
	private void setupWidgets() {
		inclusionPanel = new InclusionPanel();
		previewPanel = new PreviewPanel();
		previewPanel.setMinimumSize(new Dimension(100,100));
		
		Box upperPanel; // holds major widget panels
		upperPanel = new Box(BoxLayout.X_AXIS);
		inclusionPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		upperPanel.add(inclusionPanel);
		previewPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		upperPanel.add(previewPanel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(upperPanel);
		filePanel = new FilePanel(getInitialFilePath());
		add(filePanel);
	}
	
	public int estimateHeight() {
		return inclusionPanel.estimateHeight();
	}
	public int estimateWidth() {
		return inclusionPanel.estimateWidth();
	}
	
	class PreviewPanel extends JPanel {
		JCheckBox drawPreview;
		public void updatePreview() {
			repaint();
		}
		PreviewPanel() {
			setLayout(new BorderLayout());
			add(new JLabel("Preview"), BorderLayout.NORTH);
			add(new DrawingPanel(), BorderLayout.CENTER);
			drawPreview = new JCheckBox("Draw Preview");
			drawPreview.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updatePreview();
				}
			});
			if (estimateHeight() < 2000 && estimateWidth() <1000) {
				drawPreview.setSelected(true);
			} else {
				drawPreview.setSelected(false);
			}
			add(drawPreview, BorderLayout.SOUTH);
		}
		
		class DrawingPanel extends JPanel {
			
			public void paintComponent(Graphics g) {
				Dimension size = getSize();
				int width = estimateWidth();
				int height = estimateHeight();
				if ((width == 0) || (height == 0)) {
					return;
				}
				// if the width * size.height is greater than the the height *size.width
				// then if we make width = size.width, the height will be less than size.height.
				if (width *size.height > height * size.width) {
					height = (height * size.width) / width;
					width = size.width;
				} else { // otherwise, the converse is true.
					width = (width * size.height) / height;
					height = size.height;
				}
				if ((drawPreview == null) || drawPreview.isSelected()) {
					try {
						Image i = generateImage();
						g.drawImage(i,0,0,width,height, Color.white, null);
					} catch (java.lang.OutOfMemoryError ex) {
						JOptionPane.showMessageDialog(this, "Out of memory, Disabling preview");
						drawPreview.setSelected(false);
					}
				} else {
					g.setColor(Color.red);
					//			g.drawOval(0,0,width,height);
					int [] xPoints = new int[4];
					int [] yPoints = new int[4];
					xPoints[0] = 0; xPoints[1] = 5; xPoints[2] = width; xPoints[3] = width - 5;
					yPoints[0] = 5; yPoints[1] = 0; yPoints[2] = height -5; yPoints[3] = height;
					
					g.fillPolygon(xPoints, yPoints, 4);
					yPoints[0] = height-5; yPoints[1] = height; yPoints[2] = 5; yPoints[3] = 0;
					g.fillPolygon(xPoints, yPoints, 4);
					
				}
			}
		}
	}
	protected BufferedImage generateImage() {
		int width = estimateWidth();
		int height = estimateHeight();
		int [] chromosomes = inclusionPanel.getSelectedChromosomes();
		//Image nascent = createImage(width, height);
		BufferedImage nascent = new BufferedImage(width, height,               BufferedImage.TYPE_INT_ARGB);
		
		
		
		if (chromosomes.length == 0) return nascent;
		int perChr = height/chromosomes.length;
		
		Graphics gr = nascent.getGraphics();
		Rectangle r = new Rectangle(0,0,width,height);
		gr.setClip(r);
		KaryoDrawer karyoDrawer = karyoView.getKaryoDrawer();
		Genome genome = karyoDrawer.getGenome();
		Rectangle dest = new Rectangle();
		dest.x = 0;
		dest.y = 0;
		dest.width = width;
		dest.height = perChr;
		
		for (int i = 0; i < chromosomes.length; i++) {
			karyoDrawer.paintBackground(nascent.getGraphics(), dest);
			dest.y += perChr;
		}
		
		dest.y = 0;
		for (int i = 0; i < chromosomes.length; i++) {
			karyoDrawer.paintChromosome(nascent.getGraphics(), genome.getChromosome(chromosomes[i]), dest);
			dest.y += perChr;
		}
		return nascent;
	}
	
	int inset = 5;
	class InclusionPanel extends JPanel {
		public void synchEnabled() {
		}
		public void synchSelected() {
		}
		public int [] getSelectedChromosomes() {
			Object [] selectedI = chrList.getSelectedValues();
			int [] chrs = new int[selectedI.length];
			for(int i = 0; i < selectedI.length; i++) {
				chrs[i] = ((Integer) selectedI[i]).intValue();
			}
			return chrs;
		}
		public int estimateHeight() {
			int perChr = karyoView.getHeight() / karyoView.getKaryoDrawer().getGenome().getMaxChromosome();
			int [] selected = getSelectedChromosomes();
			return (perChr + 2*inset) * selected.length;
		}
		public int estimateWidth() {
			int [] selected = getSelectedChromosomes();
			int max = 0;
			KaryoDrawer karyoDrawer = karyoView.getKaryoDrawer();
			Genome genome = karyoDrawer.getGenome();
			for (int i =0 ;i < selected.length; i++) {
				int dist = karyoDrawer.getFarthestEndDistance(genome.getChromosome(selected[i]));
				if (dist > max) max = dist;
			}
			
			return (max +inset)*2;
		}
		InclusionPanel() {
			setupWidgets();
		}
		JList chrList;
		private void setupWidgets() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JLabel chrLabel  = new JLabel("Chromosomes:");
			chrLabel.setAlignmentX(CENTER_ALIGNMENT);
			add(chrLabel);
			int num = karyoView.getKaryoDrawer().getGenome().getMaxChromosome();
			Integer [] chromosomes = new Integer[num];
			for (int i = 0; i < num; i++) {
				chromosomes[i] = new Integer(i+1);
			}
			chrList = new JList(chromosomes);
			chrList.setAlignmentX(CENTER_ALIGNMENT);
			for (int i = 0; i < num; i++) {
				if (karyoView.isChromosomeVisible(i+1)) {
					chrList.addSelectionInterval(i,i);
				}
			}
			
			// make sure JList set up before SizeRow, so can init vals properly...
			final SizeRow sizePanel = new SizeRow();
			chrList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					previewPanel.updatePreview();
					sizePanel.setXsize(estimateWidth());
					sizePanel.setYsize(estimateHeight());
				}
			});
			add(chrList);
			
			JLabel multLabel  = new JLabel("Use shift or alt to select multiple");
			multLabel.setAlignmentX(CENTER_ALIGNMENT);
			add(multLabel);
			add(sizePanel);
		}
		class SizeRow extends JPanel {
			JTextField xSize, ySize;
			/**
			*	 make sure JList set up before SizeRow, so can init vals properly...
			*/
			public SizeRow() {
				setupWidgets();
			}
			protected void setupWidgets() {
				add(new JLabel("Total Size:"));
				xSize = new JTextField(""+estimateWidth(), 5);
				ySize = new JTextField(""+estimateHeight(), 5);
				add(xSize);
				add(new JLabel("x"));
				add(ySize);
				add(new JLabel("(pixels)"));
			}
			
			
			int xSize() {
				Double inch = new Double(xSize.getText());
				return (int) (inch.doubleValue() * 72);
			}
			
			void setXsize(int points) {
				//xSize.setText(convert(points));
				xSize.setText("" + points);
			}
			void setYsize(int points) {
				//		  ySize.setText(convert(points));
				ySize.setText("" + points);
			}
			/**
			* converts points into inches, with 72 points/inch
			*/
			private String convert(int points) {
				Double inch = new Double(Math.rint(((double) points * 100 )/ 72)/ 100.0);
				return inch.toString();
			}
			int ySize() {
				Double inch = new Double(ySize.getText());
				return (int) (inch.doubleValue() * 72);
			}
			public void setEnabled(boolean flag) {
				super.setEnabled(flag);
				xSize.setEnabled(flag);
				ySize.setEnabled(flag);
			}
			
		}
	}
	
	class FilePanel extends JPanel {
		private JTextField fileField;
		String getFilePath() {
			return fileField.getText();
		}
		File getFile() {
			return new File(getFilePath());
		}
		void setFilePath(String fp) {
			fileField.setText(fp);
			fileField.invalidate();
			fileField.revalidate();
			fileField.repaint();
			
		}
		public FilePanel(String initial) {
			super();
			setLayout(new BorderLayout());
			add(new JLabel("Export To Png: "), BorderLayout.WEST);
			fileField = new JTextField(initial);
			add(fileField, BorderLayout.CENTER);
			JButton chooseButton = new JButton("Browse");
			chooseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						JFileChooser chooser = new JFileChooser();
						int returnVal = chooser.showSaveDialog(KaryoViewExportPanel.this);
						if(returnVal == JFileChooser.APPROVE_OPTION) {
							fileField.setText(chooser.getSelectedFile().getCanonicalPath());
						}
					} catch (java.io.IOException ex) {
						LogBuffer.println("Got exception " + ex);
					}
				}
			});
			add(chooseButton, BorderLayout.EAST);
		}
	}
	
}
/*
class TestExportPanel extends KaryoViewExportPanel {
	TestExportPanel(KaryoView karyoView) {
		super(karyoView);
	}
	public void synchronizeTo() {
	}
	public void synchronizeFrom() {
	}
}
*/

