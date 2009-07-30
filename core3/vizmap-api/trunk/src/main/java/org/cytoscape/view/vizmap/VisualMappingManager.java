
/*
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

package org.cytoscape.view.vizmap;

import java.util.Collection;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.RootVisualLexicon;


/**
 * Managing mapping from View --> Visual Style.
 * Also, this class manages list of all available Visual Styles.
 * Creation/deletion of VS is a function of this class.  
 *
 * @author abeld
 * @author kono
 * 
 * @since Cytoscape 3.0
 *
 */
public interface VisualMappingManager {
	/**
	 *  Set a Visual Style to the target view
	 *
	 * @param vs Visual Style to be set.
	 * @param nv Target network view
	 */
	public void setVisualStyle(VisualStyle vs, CyNetworkView nv);

	/**
	 *  Returns the associated Visual Style for the target view.
	 *
	 * @param nv Target network view
	 *
	 * @return  Associated Visual Style for the view.
	 */
	public VisualStyle getVisualStyle(CyNetworkView nv);

	/**
	 * Create a copy of given Visual Style.
	 *
	 * @param originalVS
	 *            VS to be copied from.
	 *
	 * @return Copied VS
	 */
	public VisualStyle copyVisualStyle(VisualStyle originalVS);

	/**
	 * Create a new Visual Style.
	 * 
	 * @param title Title of the visual style.  This can be null, but in that case, 
	 * 					default title will be used.
	 * 			Note: This is NOT an identifier of this object, just a title.
	 *
	 * @return New Visual Style
	 */
	public VisualStyle createVisualStyle(String title);

	/**
	 * Returns all available Visual Styles managed by this object.
	 *
	 * @return Collection of all available Visual Styles.
	 * 
	 * TODO: Collection or Set? 
	 */
	public Collection<VisualStyle> getAllVisualStyles();

	/**
	 * Remove a Visual Style.
	 *
	 * @param vs Visual Style to be removed.
	 * 
	 */
	public void removeVisualStyle(VisualStyle vs);

	/**
	 * Get all Visual Lexicons from all rendering engines.
	 * 
	 * @return All Visual Lexicon
	 */
	public RootVisualLexicon getRootVisualLexicon();	
}
