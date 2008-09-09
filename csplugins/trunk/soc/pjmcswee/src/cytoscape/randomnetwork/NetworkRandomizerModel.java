/* File: NetworkRandomizerModel.java
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

package cytoscape.randomnetwork;

import cytoscape.*;
import java.util.Random;
import java.util.LinkedList;

/**
 *  This is the base class for all RandomNetworkGenerators that randomize an existing CyNetwork.
 *  A pointer is kept to the CyNetwork, so deleting the CyNetwork will not release memory, unless
 *  any NetworkRandomizers created with that CyNetwork are also released.
 *
 * @author Patrick J. McSweeney
 * @version 1.0
 */
public abstract class NetworkRandomizerModel implements RandomNetworkGenerator{

	/**
	 * Experimental seed.
	 */
	protected long mSeed;

	/**
	 * Random number generated
	 */
	protected Random mRandom;

	/**
	 * Specifies if edges are directed.
	 */
	protected boolean mDirected;

	/**
	 * The graph to randomize.
	 */
	protected RandomNetwork mOriginal;

	
	/**-----------------------------------------------------------------------
	 *  Constructs a RandomNetworkGenerator which operates by randomizing some 
	 *  aspect of an existing network.
	 *
	 * @param network The CyNetwork that is to be randomized
	 * @param pDirected Whether or not to treat the network as directed or not.
	 *
	 *-----------------------------------------------------------------------*/
	NetworkRandomizerModel(RandomNetwork pRandomNetwork, boolean pDirected) 
	{		
		mDirected = pDirected;
		mOriginal = pRandomNetwork;
		mSeed = System.currentTimeMillis(); 
		mRandom = new Random(mSeed);
	}
	
	

	/**-----------------------------------------------------------------------
	 * Sets the seed for this NetworkRandomizerModel.
	 * @param pSeed the seed to set for the random # generator.
	 *-----------------------------------------------------------------------*/
	public void setSeed(long pSeed) 
	{
		mSeed = pSeed;
		mRandom = new Random(mSeed);
	}

	/**-----------------------------------------------------------------------
	 * Returns the seed for this NetworkRandomizerMode.
	 *
	 * @return The seed used by the random network generator.
	 *-----------------------------------------------------------------------*/
	public long getSeed() 
	{
		return mSeed;
	}

	/**-----------------------------------------------------------------------
	 *  This tells us how to treat the CyNetwork originally passed in.
	 *  All subsequant random networks generated will have the same directedness.
	 *
	 *  @return true if this is directed or not
	 *-----------------------------------------------------------------------*/
	public boolean getDirected() 
	{
		return mDirected;
	}

	/**-----------------------------------------------------------------------
	 *  Returns the DynamicGraph used in this NetworkRandomizerModel. 
	 * 
	 *  @return The original network in a DynamicGraph structure.
	 *-----------------------------------------------------------------------*/
	public RandomNetwork getOriginal()
	{
		return mOriginal;
	}

}
