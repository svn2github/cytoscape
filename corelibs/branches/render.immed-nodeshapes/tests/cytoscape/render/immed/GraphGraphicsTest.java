
/*
 Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.render.immed;

import junit.framework.*;

import java.util.List;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.awt.BasicStroke;

public class GraphGraphicsTest extends TestCase {


	GraphGraphics currentGraphGraphics;
	OldGraphGraphics oldGraphGraphics;
	int numNodes = 10000;
	int numEdges = 10000;
	BufferedImage image;
	int canvasSize = 1000;

	public void setUp() {
		image = new BufferedImage(canvasSize,canvasSize,BufferedImage.TYPE_INT_ARGB);
		currentGraphGraphics = new GraphGraphics(image,false);
		currentGraphGraphics.clear(Color.white,0,0,1.0);
		oldGraphGraphics = new OldGraphGraphics(image,false);
		oldGraphGraphics.clear(Color.white,0,0,1.0);
	}

	public void testRenderGraphFull() {
		// run everything once, to prime the system
		Random rand = new Random(10);
		long oldDur = drawOldFull(rand);
		rand = new Random(10);
		long currDur = drawCurrentFull(rand);

		// now run each test 10 times and sum the durations
		long totalOldDur = 0;
		long totalCurrDur = 0;
		for ( int i = 0; i < 10; i++ ) {
			rand = new Random(i);
			oldDur = drawOldFull(rand);
			System.out.println("Old    : " + oldDur);
			totalOldDur += oldDur;

			rand = new Random(i);
			currDur = drawCurrentFull(rand);
			System.out.println("Current: " + currDur);
			totalCurrDur += currDur;
		}

		System.out.println("Total Old    : " + totalOldDur);
		System.out.println("Total Current: " + totalCurrDur);
		long diff = (totalOldDur-totalCurrDur)/10;
		if ( diff >= 0 )
			System.out.println("Faster on avg by: " + diff);
		else
			System.out.println("Slower on avg by: " + diff);

		assertTrue( totalCurrDur < totalOldDur );
	}

	private long drawCurrentFull(Random rand) {
		final float nodeSizeFactor = 50f;
		float size = (float) canvasSize;

		long begin = System.nanoTime();
		for ( int i = 0; i < numNodes; i++ ) {
			float x = rand.nextFloat() * (rand.nextBoolean() ? size : -size); 
			float y = rand.nextFloat() * (rand.nextBoolean() ? size : -size); 
			currentGraphGraphics.drawNodeFull( (byte)(i % (int) GraphGraphics.s_last_shape), 
							x,
							y,
							(x + (rand.nextFloat() * nodeSizeFactor)),	
							(y + (rand.nextFloat() * nodeSizeFactor)),
							Color.blue,
							1.0f + (i % 10),
					    	Color.yellow);
		}
		long end = System.nanoTime();

		BasicStroke edgeStroke = new BasicStroke(1f);

		begin = System.nanoTime();
		for ( int i = 0; i < numEdges; i++ ) {
			currentGraphGraphics.drawEdgeFull(
				(byte)((i % 7)-8),
				rand.nextFloat() * (20f),
				Color.red, 
				(byte)((i % 7)-8),
				rand.nextFloat() * (20f),
				Color.orange, 
				rand.nextFloat() * (rand.nextBoolean() ? size : -size),
				rand.nextFloat() * (rand.nextBoolean() ? size : -size), 
				currentGraphGraphics.m_noAnchors,
				rand.nextFloat() * (rand.nextBoolean() ? size : -size),
				rand.nextFloat() * (rand.nextBoolean() ? size : -size), 
				1f, 
				edgeStroke, 
				Color.green);
		}
		end = System.nanoTime();
		long duration = end - begin;

//		try {
//			ImageIO.write(image,"PNG",new File("/tmp/homer-current.png"));
//		} catch (IOException ioe) { ioe.printStackTrace(); }
		return duration;
	}

	private long drawOldFull(Random rand) {
		final float nodeSizeFactor = 50f;
		float size = (float) canvasSize;

		long begin = System.nanoTime();
		for ( int i = 0; i < numNodes; i++ ) {
			float x = rand.nextFloat() * (rand.nextBoolean() ? size : -size); 
			float y = rand.nextFloat() * (rand.nextBoolean() ? size : -size); 
			currentGraphGraphics.drawNodeFull( (byte)(i % (int) GraphGraphics.s_last_shape), 
							x,
							y,
							(x + (rand.nextFloat() * nodeSizeFactor)),	
							(y + (rand.nextFloat() * nodeSizeFactor)),
							Color.blue,
							1.0f + (i % 10),
					    	Color.yellow);
		}
		long end = System.nanoTime();

		BasicStroke edgeStroke = new BasicStroke(1f);

		begin = System.nanoTime();
		for ( int i = 0; i < numEdges; i++ ) {
			currentGraphGraphics.drawEdgeFull(
				(byte)((i % 7)-8),
				rand.nextFloat() * (20f),
				Color.red, 
				(byte)((i % 7)-8),
				rand.nextFloat() * (20f),
				Color.orange, 
				rand.nextFloat() * (rand.nextBoolean() ? size : -size),
				rand.nextFloat() * (rand.nextBoolean() ? size : -size), 
				currentGraphGraphics.m_noAnchors,
				rand.nextFloat() * (rand.nextBoolean() ? size : -size),
				rand.nextFloat() * (rand.nextBoolean() ? size : -size), 
				1f, 
				edgeStroke, 
				Color.green);
		}
		end = System.nanoTime();
		
		long duration = end - begin;
		

//		try {
//			ImageIO.write(image,"PNG",new File("/tmp/homer-old.png"));
//		} catch (IOException ioe) { ioe.printStackTrace(); }
		return duration;	
	}

	// This will run the JUnit gui, which can be useful for debugging. 
	public static void main(String[] args) {
		String[] newargs = { "cytoscape.render.immed.GraphGraphicsTest", "-noloading" };
		junit.swingui.TestRunner.main(newargs);
	}
}
