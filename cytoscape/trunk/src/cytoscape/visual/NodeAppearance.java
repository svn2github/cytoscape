/*
 File: NodeAppearance.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.visual;

/**
 * Objects of this class hold data describing the appearance of a Node.
 */
public class NodeAppearance extends Appearance {

	/**
	 * Constructor.
	 */
	public NodeAppearance() {
		super();
	}

	private VisualPropertyDependency dep;

	/**
	 * @deprecated This exists only for backwards compatibility.  Do not use it!
     * Will be removed Jan 2011.
	 */
	@Deprecated
	NodeAppearance(VisualPropertyDependency dep) {
		super();
		this.dep = dep;
	}


	public Object clone() {
		NodeAppearance ga = new NodeAppearance();
		ga.copy(this);
		return ga;
	}

    private boolean nodeSizeLocked = true;
    /**
     * Returns whether or not the node height and width are locked.
     * @return Whether or not the node height and width are locked.     
     * @deprecated Use VisualStyle.getDependency().check(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED) instead. 
     * Will be removed Jan 2011.
     */
    @Deprecated
    public boolean getNodeSizeLocked() {
        return dep.check(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED);
    }

    /**
     * Sets whether or not the node height and width are locked.     
     * @deprecated Use VisualStyle.getDependency().set(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED,b) instead. 
     * Will be removed Jan 2011.
     */
    @Deprecated
    public void setNodeSizeLocked(boolean b) {
       	dep.set(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED,b); 
    }
}
