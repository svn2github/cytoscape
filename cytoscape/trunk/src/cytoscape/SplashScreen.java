/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

package cytoscape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.net.URL;

/**
 * This file only uses AWT APIs so that it is displayed faster on startup.
 */
public class SplashScreen extends Canvas implements Runnable
{
                 public void run() {
			  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		image = getToolkit().getImage(
			getClass().getResource("/cytoscape/images/cytoSplash.jpg"));
		MediaTracker tracker = new MediaTracker(this);
		if (image !=null)
			tracker.addImage(image,0);

		try
		{
			tracker.waitForAll();
		}
		catch(Exception e)
		{
			System.out.println("Can not load the image" + e.getMessage()); 
		}

		win = new Frame("Cytoscape is loading...");

		Dimension screen = getToolkit().getScreenSize();
		//Dimension size = new Dimension(image.getWidth(this) + 2,
			//image.getHeight(this) + 2);
		Dimension size = new Dimension(400, 330);		
		win.setSize(size);

		win.setLayout(new BorderLayout());
		win.add(BorderLayout.CENTER,this);
		progressBar = new JProgressBar(0,100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		win.add(BorderLayout.SOUTH,progressBar);


		win.setLocation((screen.width - size.width) / 2,
			(screen.height - size.height) / 2);
		win.validate();
		win.show();

		
		while (progress < 100)
		{
			try {
			    t.sleep(100);
			    progressBar.setValue(progress);
			    progress++;
			}
		
			catch(InterruptedException ie)
			{
				
			}
		}
		if (!noGraph) {
			progressBar.setIndeterminate(true);
			progressBar.setString("Loading and laying out the graph...");
		}
         }
     
 
	
	public SplashScreen()
	{
		
		t = new Thread(this);
		t.start();
	}
	
	public void advance(int progress)
	{
		this.progress = progress;
	}

	public void dispose()
	{
		t.stop();
		if(win != null)
			win.dispose();
	}


	public void update(Graphics g)
	{
		paint(g);
	}

	public synchronized void paint(Graphics g)
	{
		Dimension size = getSize();

		if(offscreenImg == null)
		{
			offscreenImg = createImage(size.width,size.height);
			offscreenGfx = offscreenImg.getGraphics();
		}

		offscreenGfx.setColor(Color.black);
		offscreenGfx.drawRect(0,0,size.width - 1,size.height - 1);

		offscreenGfx.drawImage(image,1,1,this);

		offscreenGfx.setColor(Color.white);
		offscreenGfx.setFont(new Font("Arial",Font.BOLD, 22));
		offscreenGfx.drawString("Welcome to Cytoscape!", 3,40);
		
		offscreenGfx.setFont(new Font("Arial", Font.BOLD, 14));
		offscreenGfx.drawString ("A general-purpose modeling environment",3,210 );
		offscreenGfx.drawString ("for integrating biomolecular interaction",3,230);
		offscreenGfx.drawString ("networks, network states, and functional",3,250);
		offscreenGfx.drawString ("ontologies.",3,270); 
		g.drawImage(offscreenImg,0,0,this);
		
		notify();
	}
	
	Thread t;
	Frame win;
	Image image;
	Image offscreenImg;
	Graphics offscreenGfx;
	int progress;
	public boolean noGraph = false;
	JProgressBar progressBar;
}
