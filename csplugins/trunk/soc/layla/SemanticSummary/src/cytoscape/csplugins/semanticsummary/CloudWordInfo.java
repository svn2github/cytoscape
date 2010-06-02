/*
 File: CloudWordInfo.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.csplugins.semanticsummary;

import java.awt.Font;

import javax.swing.JLabel;

/**
 * The CloudWordInfo class defines information pertaining to a particular
 * word in a Cloud.  In particular this class defines information that 
 * relates to how that word will be displayed in the cloud.
 * @author Layla Oesper
 * @version 1.0
 */

public class CloudWordInfo implements Comparable<CloudWordInfo>
{
	//VARIABLES
	String word;
	Integer fontSize;
	CloudParameters params;
	
	//CONSTRUCTORS
	
	/**
	 * Creates a blank CloudWordInfo Object for the specified word.
	 * @param String - the word for this object
	 * @param Integer - the font size for this object
	 */
	public CloudWordInfo(String aWord, Integer size)
	{
		word = aWord;
		fontSize = size;
	}
	
	//METHODS
	
	/**
	 * Compares two CloudWordInfo objects based on their fontSize.
	 * @param CloudWordInfo - object to compare
	 * @return true if 
	 */
	public int compareTo(CloudWordInfo c)
	{
		Integer first = this.getFontSize();
		Integer second = c.getFontSize();
		
		return first.compareTo(second);
	}
	
	/**
	 * Returns a JLabel that can be used to display this word in a cloud.
	 * @return JLabel - for display in Cloud.
	 */
	public JLabel createCloudLabel()
	{
		JLabel label = new JLabel(this.getWord());
		label.setFont(new Font("sansserif",Font.BOLD, this.getFontSize()));
		
		//TODO - add listener stuff here
		
		return label;
	}
	
	//Getters and Setters
	public void setWord(String aWord)
	{
		word = aWord;
	}
	
	public String getWord()
	{
		return word;
	}
	
	public void setFontSize(Integer size)
	{
		fontSize = size;
	}
	
	public Integer getFontSize()
	{
		return fontSize;
	}
	
	public void setCloudParameters(CloudParameters curParams)
	{
		params = curParams;
	}
	
	public CloudParameters getCloudParameters()
	{
		return params;
	}
}
