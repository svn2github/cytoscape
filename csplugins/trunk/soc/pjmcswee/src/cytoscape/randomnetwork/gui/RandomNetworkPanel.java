/*  File: RandomNetworkPanel.java
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
 
 
 package cytoscape.randomnetwork.gui;
 import javax.swing.*;
 import java.awt.*;
 /**
  * Ideally, this might work better as an interface, but as we really
  * want to be able to add it to UI components, it should be a class.
  */
 public abstract class RandomNetworkPanel extends JPanel
 {
 
 
		/**
		 * The previous RandomNetworkPanel
		 */
		private RandomNetworkPanel mPrevious;
		protected RandomNetworkPanel mNext;
		
		private static final int PANEL_WIDTH = 470;
		private static final int PANEL_HEIGHT = 200;
 
		/**
		 * Constructor
		 * @param pPrevious The previous RandomNetworkPanel in the succesion.
		 */
		RandomNetworkPanel(RandomNetworkPanel pPrevious)
		{
			mPrevious = pPrevious;
			mNext = null;
			setBorder(BorderFactory.createLineBorder(Color.gray));
			Dimension dim = new Dimension(PANEL_WIDTH,PANEL_HEIGHT);
			setPreferredSize(dim);
			setMinimumSize(dim);
		}
 
 
		/**
		 *
		 * @return The preivous RandomNetworkPanel in this succession.
		 */
		public RandomNetworkPanel getPrevious()
		{
			return mPrevious;
		}
 
		/**
		 * @return Get the next RandomNetworkPanel in this succession. 
		 */
		public abstract RandomNetworkPanel next();
	
			
		/**
		*
		*/
		//public abstract RandomNetworkPanel next();
		
		
		/**
		 * @param What should we call the Next Button Now
		 */	
		public String getNextText()
		{
			return new String("Next");
		}
		
		
		/**
		 *
		 */
		public abstract String getTitle();
		
		public abstract String getDescription();
		
		
		
		
 }