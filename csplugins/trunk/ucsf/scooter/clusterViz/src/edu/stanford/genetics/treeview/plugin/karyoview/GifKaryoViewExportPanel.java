/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: GifKaryoViewExportPanel.java,v $
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

import java.awt.Image;
import java.io.*;

import com.gurge.amd.*;

import edu.stanford.genetics.treeview.LogBuffer;

class GifKaryoViewExportPanel extends KaryoViewExportPanel {
	GifKaryoViewExportPanel(KaryoView scatterView) {
		super(scatterView);
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
		  
		  write(output);
		  
		  output.close();
	  } catch (Exception e) {
		  LogBuffer.println("GIF KaryoView Export Panel caught exception " + e);
	  }
  }
 
	/**
	* Save image to the currently selected file...
	*/
	public void write(OutputStream output) {
		Image i = generateImage();
		try {
			int pixels[][] = TestQuantize.getPixels(i);
			// quant... probably unnecessary here...
			int palette[] = Quantize.quantizeImage(pixels, 256);
			GIFEncoder enc = new GIFEncoder(createImage(TestQuantize.makeImage(palette, pixels)));
			enc.Write(output);
		} catch (Exception e) {
			LogBuffer.println("In GifKaryoViewExportPanel.synchronizeTo() got exception " + e);
		}
	}
}


