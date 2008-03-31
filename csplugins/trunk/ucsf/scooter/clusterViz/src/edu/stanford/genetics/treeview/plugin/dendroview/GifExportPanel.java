/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: GifExportPanel.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:45 $
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
package edu.stanford.genetics.treeview.plugin.dendroview;

import java.awt.*;
import java.io.*;

import com.gurge.amd.*;

import edu.stanford.genetics.treeview.*;

/**
*  Subclass of ExportPanel which outputs a postscript version of a DendroView.
*
*/
public class GifExportPanel extends ExportPanel implements SettingsPanel {
  
  // I wish I could just inherit this...
  public GifExportPanel(HeaderInfo arrayHeaderInfo, HeaderInfo geneHeaderInfo, 
	TreeSelectionI geneSelection, 
	TreeSelectionI arraySelection, 
	InvertedTreeDrawer arrayTreeDrawer, LeftTreeDrawer geneTreeDrawer, ArrayDrawer arrayDrawer, MapContainer arrayMap,MapContainer geneMap) {
	super(arrayHeaderInfo, geneHeaderInfo, 
	geneSelection, 
	arraySelection, 
	arrayTreeDrawer, geneTreeDrawer, 
	arrayDrawer, arrayMap,
	geneMap, false);
  }
  
  public void synchronizeTo() {
	save();
  }
  
  public void synchronizeFrom() {
	// do nothing...
  }
  public void save() {
	  try {
		  OutputStream output = new BufferedOutputStream
		  (new FileOutputStream(getFile()));
		  
		  DendroGifWriter gw = new DendroGifWriter();
		  gw.write(output);
		  
		  output.close();
	  } catch (Exception e) {
		  LogBuffer.println("GIF Export Panel caught exception " + e);
	  }
  }
  /**
  * indicate to superclass that this type does not have bbox
  */
  protected boolean hasBbox() { return false;}
  
  protected String getInitialExtension() {
	return(".gif");
  }

  /**
  *  Inner class which outputs a gif version of Dendroview like things
  *
  *	It is "loosely coupled" in that it only calls protected methods in the ExportPanel superclass.
  */
  
  class DendroGifWriter {
	int extraWidth = getBorderPixels();
	int extraHeight = getBorderPixels();
	
	/**
	* write a gif image corresponding to the export panel preview
	* to the OutputStream output.
	*/
	public void write(OutputStream output) {
	  
	  Rectangle destRect = new Rectangle(0,0,
	  estimateWidth(), 
	  estimateHeight());
	  Image i = createImage(destRect.width + extraWidth, destRect.height + extraHeight);
	  Graphics g = i.getGraphics();
	  g.setColor(Color.white);
	  g.fillRect(0,0,destRect.width+1 + extraWidth,  destRect.height+1+extraHeight);
	  g.setColor(Color.black);
	  g.translate(extraHeight/2, extraWidth/2);
	  drawAll(g, 1.0);
	  try {
		  int pixels[][] = TestQuantize.getPixels(i);
		  // quant
		  int palette[] = Quantize.quantizeImage(pixels, 256);
		  GIFEncoder enc = new GIFEncoder(createImage(TestQuantize.makeImage(palette, pixels)));
		  enc.Write(output);
	  } catch (Exception e) {
		LogBuffer.println("In GifExportPanel.DendroGifWriter() got exception " + e);
	  }
	}
	
	}
}


