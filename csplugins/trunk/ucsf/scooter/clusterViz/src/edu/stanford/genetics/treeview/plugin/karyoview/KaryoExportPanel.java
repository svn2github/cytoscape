/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: KaryoExportPanel.java,v $
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
import java.awt.event.*;
import javax.swing.event.*;
/**
* This class is a superclass which implements a GUI for selection of options relating to output. 
* It makes most of the relevant variables accessible to subclasses through protected methods.
*/
public abstract class KaryoExportPanel extends javax.swing.JPanel {
  private ConfigNode root;

  // external links
  private FileSet sourceSet;          // FileSet from which current data was constructed.
  private TreeSelectionI geneSelection;
  private KaryoDrawer karyoDrawer;
  
  	// accessors
  protected TreeSelectionI getGeneSelection() {
	return geneSelection;
  }
  protected KaryoDrawer getKaryoDrawer() {
	return karyoDrawer;
  }
  
  public FileSet getSourceSet() {
	return sourceSet;
  }
  public void setSourceSet( FileSet fs) {
	sourceSet = fs;
	if (filePanel != null) {
	  filePanel.setFilePath(getInitialFilePath());
	}
  }
  
  /**
  * for communication with subclass...
  */
  protected boolean hasBbox() {
	return true;
  }
  // components
  private FilePanel filePanel;
  private InclusionPanel inclusionPanel;
  private PreviewPanel previewPanel;
  
  // accessors for configuration information
  /**
  * Returns true if a bounding box is to be included.
  * Only for postscript.
  */
  protected boolean includeBbox() {
	return inclusionPanel.useBbox();
  }


  protected File getFile() {
	return filePanel.getFile();
  }
  protected String getInitialExtension() {
	return ".ps";
  }
  protected String getInitialFilePath() {
	String defaultPath = null;
	if (sourceSet == null) {
	  defaultPath = System.getProperty("user.home");
	} else {
		defaultPath = sourceSet.getDir() + sourceSet.getRoot() + getInitialExtension();
	}
	if (root == null) {
	  return defaultPath;
	} else {
	  return root.getAttribute("file", defaultPath);
	}
  }

  

  public KaryoExportPanel( TreeSelectionI geneSelection, KaryoDrawer karyoDrawer) {
	this.geneSelection = geneSelection;
	this.karyoDrawer = karyoDrawer;
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
	  Box upperPanel; // holds major widget panels
	  upperPanel = new Box(BoxLayout.X_AXIS);
//	  headerSelectionPanel = new HeaderSelectionPanel();
//	  upperPanel.add(headerSelectionPanel);
	  inclusionPanel = new InclusionPanel();
	  upperPanel.add(inclusionPanel);
	  previewPanel = new PreviewPanel();
	  upperPanel.add(previewPanel);
	  setLayout(new BorderLayout());
	  add(upperPanel, BorderLayout.CENTER);
	  filePanel = new FilePanel(getInitialFilePath());
	  add(filePanel, BorderLayout.SOUTH);
	}
	
	//drawing specific convenience methods...
	public double getXscale() {
	  return inclusionPanel.getXscale();
	}
	public double getYscale() {
	  return inclusionPanel.getYscale();
	}
	
	public int getBboxWidth() {
	  return inclusionPanel.getBboxWidth();
	}
	public int getBboxHeight() {
	  return inclusionPanel.getBboxHeight();
	}

	
	public int estimateHeight() {
	  	int height = 0;
	  // FIXME
		return height;
	}
	public int estimateWidth() {
	  int width = 0;
	  // FIXME
		return width;
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
		drawPreview.setSelected(true);
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
			double scale = (double)width / estimateWidth();
			// FIXME actually draw preview here...

		  } else {
			g.setColor(Color.red);
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
	
	class InclusionPanel extends JPanel {
		JCheckBox bboxBox;
		JTextField xScaleField, yScaleField;
		BboxRow bboxRow;
		SizeRow sizeRow;
		public boolean useBbox() {
			return bboxBox.isSelected();
		}
		public double getXscale() {
			Double tmp = new Double(xScaleField.getText());
			return tmp.doubleValue();
		}
		public double getYscale() {
			Double tmp = new Double(yScaleField.getText());
			return tmp.doubleValue();
		}
		public int getBboxWidth() {
			return bboxRow.xSize();
		}
		public int getBboxHeight() {
			return bboxRow.ySize();
		}
		public void synchEnabled() {
			bboxRow.setEnabled(bboxBox.isSelected());
			updateSize();
			if (previewPanel != null) previewPanel.updatePreview();
		}
		
	  
	  /**
	  * This routine selects options so that they make sense with respect to the current data
	  * in the dendrogram. It should be called during initialization before synchEnabled()
	  */
	  public void synchSelected() {
		  updateSize();
		  if (previewPanel != null) previewPanel.updatePreview();
	  }
	  public void recalculateBbox() {
		  bboxRow.setXsize(0);
		  bboxRow.setYsize(0);
	  }
	  
	  public void updateSize() {
		  try {
			  sizeRow.setXsize(estimateWidth());
			  sizeRow.setYsize(estimateHeight());
		  } catch (Exception e) {
			  // ignore...
		  }
	  }

	  InclusionPanel() {
		  documentListener = new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  updateSize();
				  if (previewPanel != null) previewPanel.updatePreview();
			  }
			  public void insertUpdate(DocumentEvent e) {
				  updateSize();
				  if (previewPanel != null) previewPanel.updatePreview();
			  }
			  public void removeUpdate(DocumentEvent e) {
				  updateSize();
				  if (previewPanel != null) previewPanel.updatePreview();
			  }
		  };
		  setupWidgets();
		  recalculateBbox();
	  }
	  DocumentListener documentListener = null;
	  private void setupWidgets() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		ActionListener syncher = new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
			synchEnabled();
		  }
		};

		JPanel scalePanel = new JPanel();
		scalePanel.add(new JLabel("Scale:"));
		// FIXME should grab ppv, ppm from karyodrawer?
		xScaleField = new JTextField(1);
		yScaleField = new JTextField(1);
		scalePanel.add(xScaleField);
		scalePanel.add(new JLabel("x"));
		scalePanel.add(yScaleField);
		add(scalePanel);
		
		xScaleField.getDocument().addDocumentListener(documentListener);
		yScaleField.getDocument().addDocumentListener(documentListener);

		
		bboxBox = new JCheckBox("Bounding Box?", hasBbox());

		bboxBox.addActionListener(syncher);
		
		JPanel outputPanel = new JPanel();
		outputPanel.add(bboxBox);
		bboxRow = new BboxRow();
		if (hasBbox()) {
		  add(outputPanel);
		  add(bboxRow);
		}
		sizeRow = new SizeRow();
		add(sizeRow);
	  }
	  class BboxRow extends SizeRow {
		protected void setupWidgets() {
		 DocumentListener documentListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			  updateSize();
		if (previewPanel != null) previewPanel.updatePreview();
			}
			public void insertUpdate(DocumentEvent e) {
			  updateSize();
		if (previewPanel != null) previewPanel.updatePreview();
			}
			public void removeUpdate(DocumentEvent e) {
			  updateSize();
		if (previewPanel != null) previewPanel.updatePreview();
			}
		  };
		  add(new JLabel("BBox size:"));
		  xSize = new JTextField("2", 4);
		  ySize = new JTextField("2", 4);
		  add(xSize);
		  add(new JLabel("x"));
		  add(ySize);
		  add(new JLabel("(inch)"));
		  xSize.getDocument().addDocumentListener(documentListener);
		  ySize.getDocument().addDocumentListener(documentListener);
		}
	  }
	  class SizeRow extends JPanel {
		JTextField xSize, ySize;
		public SizeRow() {
		  setupWidgets();
		}
		protected void setupWidgets() {
		  add(new JLabel("Total Size:"));
		  xSize = new JTextField("2", 5);
		  ySize = new JTextField("2", 5);
		  add(xSize);
		  add(new JLabel("x"));
		  add(ySize);
		  add(new JLabel("(inch)"));
		}
		int xSize() {
		  Double inch = new Double(xSize.getText());
		  return (int) (inch.doubleValue() * 72);
		}
		
		void setXsize(int points) {
		  xSize.setText(convert(points));
		}
		void setYsize(int points) {
		  ySize.setText(convert(points));
		}
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
	    add(new JLabel("Export To: "));
	    fileField = new JTextField(initial);
	    add(fileField);
	    JButton chooseButton = new JButton("Browse");
	    chooseButton.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
			try {
			  JFileChooser chooser = new JFileChooser();
			  int returnVal = chooser.showSaveDialog(KaryoExportPanel.this);
			  if(returnVal == JFileChooser.APPROVE_OPTION) {
				fileField.setText(chooser.getSelectedFile().getCanonicalPath());
			  }
			} catch (java.io.IOException ex) {
			  LogBuffer.println("Got exception " + ex);
			}
		  }
		});
	    add(chooseButton);
	}
  }
}

	class TestExportPanel extends KaryoExportPanel {
	  TestExportPanel(TreeSelectionI sel, KaryoDrawer drawer) {		
		super(sel, drawer);
	  }
	}


