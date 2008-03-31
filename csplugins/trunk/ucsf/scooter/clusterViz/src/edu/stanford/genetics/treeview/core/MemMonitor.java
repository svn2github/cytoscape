/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: MemMonitor.java,v $
* $Revision: 1.1 $
* $Date: 2006/09/27 04:56:41 $
* $Name:  $
*
* This file is part of Java TreeView
* Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved.
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
package edu.stanford.genetics.treeview.core;


import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MemMonitor extends Frame
{
	public String textString;
	Runtime rt;
	
	long freeMem, freeMem2, totalMem, totalMem2;
	
	public static void main(String args[])
	{
		MemMonitor m = new MemMonitor();
	}
	
	public MemMonitor()
	{
		super("VM Memory Example");
		Button clearMem = new Button("Run Garbage Collector");
		add("South", clearMem);
		final MemMonitor top = this;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
		    top.dispose();
			}
		});
		start();
	}
	
	public void start()
	{
		setSize(400,250);
		rt = Runtime.getRuntime();
		show();
	}
	
	public void paint(Graphics g)
	{
		g.drawString("Free memory (pre-GC)  = " + Long.toString(freeMem), 
		15, 40);
		g.drawString("Used memory (pre-GC)  = " + Long.toString(totalMem-freeMem), 
		15, 55);
		
		g.drawString("Total memory (pre-GC) = " + Long.toString(totalMem), 
		15, 70);
		
		g.drawString("Used memory (post-GC) = " + Long.toString(totalMem2-freeMem2), 
		15, 90);
		g.drawString("Free memory (post-GC) = " + Long.toString(freeMem2), 
		15, 105);
		
		g.drawString("Total memory (post-GC) =" + Long.toString(totalMem2), 
		15, 120);
		
		g.setColor(Color.blue);
		g.drawString("All memory in bytes", 15, 135);
	}
	
	public boolean handleEvent(Event e)
	{
		if(e.target instanceof Button)
		{
			String label = ((Button)e.target).getLabel();
			if(label.equals("Run Garbage Collector"))
			{
				//System.gc();
				freeMem = rt.freeMemory();
				totalMem = rt.totalMemory();
				rt.gc();
				freeMem2 = rt.freeMemory();
				totalMem2 = rt.totalMemory();
				repaint();
				return true;
			}
		}
		return false;
	}
}
