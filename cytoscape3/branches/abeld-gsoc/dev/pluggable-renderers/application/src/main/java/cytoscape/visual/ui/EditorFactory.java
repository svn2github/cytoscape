
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
package cytoscape.visual.ui;

import cytoscape.visual.ui.EditorDisplayer;
import cytoscape.visual.ui.EditorDisplayer.EditorType;
import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel;

import org.cytoscape.view.DiscreteValue;
import org.cytoscape.view.VisualProperty;

import java.lang.reflect.*;


public class EditorFactory {

	private static Object showEditor(EditorDisplayer action, VisualProperty type)
	    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
	               SecurityException, NoSuchMethodException {
		Method method = action.getActionClass()
		                      .getMethod(action.getCommand(), action.getParamTypes());
		
		
		Object ret = method.invoke(null, action.getParameters());

		// This is an editor.
		if ((ret != null) && ret instanceof ContinuousMappingEditorPanel)
			return ret;
		
		else if ((ret != null) && type.getName().equals("EDGE_LINE_WIDTH")) {
			try {
				ret = Float.valueOf(((String)ret));
			} catch (NumberFormatException e){
				ret = 1f;
			}
		} else if ( (!action.getCompatibleClass().isAssignableFrom(DiscreteValue.class)  ) 
				&& (ret != null) && (action.getCompatibleClass() != ret.getClass())) {
			try {
				ret = Double.parseDouble(ret.toString());
			} catch (NumberFormatException e){
				ret = 1d;
			}
			
			
		}
		
		// If size, it should be greater than 0.  Otherwise, 1 will be set.
		if((type.getName()).toUpperCase().endsWith("WIDTH") || (type.getName()).toUpperCase().endsWith("SIZE")) {
			if(((Number)ret).doubleValue() < 0) {
				ret = 1f;
			}
		}
		
		if((type.getName()).toUpperCase().endsWith("OPACITY")) {
			if (((Number)ret).doubleValue() > 255) {
				ret = 255d;
			} else if(((Number)ret).doubleValue() < 0) {
				ret = 0d;
			}
		}

		return ret;
	}


	/**
	 * Display discrete value editor for this visual property.
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public static Object showDiscreteEditor(VisualProperty type) throws Exception {
		return showEditor(EditorDisplayer.getEditor(type, EditorType.DISCRETE), type);
	}
	

	/**
	 * Display continuous value editor.
	 *
	 * <p>
	 *         Continuous editor always update mapping automatically, so there is no return value.
	 * </p>
	 * @throws Exception DOCUMENT ME!
	 */
	public static Object showContinuousEditor(VisualProperty type) throws Exception {
		final EditorDisplayer editor = EditorDisplayer.getEditor(type, EditorType.CONTINUOUS);

		if (editor == EditorDisplayer.CONTINUOUS_COLOR)
			editor.setParameters(new Object[] { 450, 180, "Gradient Editor for " + type.getName(), type });
		else if (editor == EditorDisplayer.CONTINUOUS_CONTINUOUS)
			editor.setParameters(new Object[] {
			                         450, 350, "Continuous Editor for " + type.getName(), type
			                     });
		else
			editor.setParameters(new Object[] {
			                         450, 300, "Continuous Editor for " + type.getName(), type
			                     });

		return showEditor(editor,type);
	}
}	
