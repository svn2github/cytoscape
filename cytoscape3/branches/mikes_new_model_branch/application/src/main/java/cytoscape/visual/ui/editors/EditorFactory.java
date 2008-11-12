
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
package cytoscape.visual.ui.editors;

import org.cytoscape.vizmap.VisualPropertyType;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;


public class EditorFactory {

	private Set<EditorDisplayer> displayers;

	public EditorFactory() {
		displayers = new HashSet<EditorDisplayer>();
	}

	public void addEditorDisplayer(EditorDisplayer ed, Map props) {
		displayers.add(ed);
	}

	public void removeEditorDisplayer(EditorDisplayer ed, Map props) {
		displayers.remove(ed);
	}

	private Object showEditor(VisualPropertyType type, EditorDisplayer.Type edType) {
		final Class<?> dataType = type.getDataType();
		EditorDisplayer res = null; 
		for (EditorDisplayer command : displayers) {
			if ( dataType == command.getDataType() && 
			     edType == command.getEditorType() ) {
				res = command;
				break;
			}
		}

		if ( res == null )
			throw new NullPointerException("no editor found for: " + type.toString());

	
		return res.showEditor(type);
	}

	/**
	 * Display discrete value editor for this visual property.
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Object showDiscreteEditor(VisualPropertyType type) throws Exception {
		return showEditor(type,EditorDisplayer.Type.DISCRETE);
	}
	

	/**
	 * Display continuous value editor.
	 *
	 * <p>
	 *         Continuous editor always update mapping automatically, so there is no return value.
	 * </p>
	 * @throws Exception DOCUMENT ME!
	 */
	public Object showContinuousEditor(VisualPropertyType type) throws Exception {
		return showEditor(type,EditorDisplayer.Type.CONTINUOUS);
	}
}	
