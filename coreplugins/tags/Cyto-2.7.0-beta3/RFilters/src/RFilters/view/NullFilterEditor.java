
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

package filter.view;

import filter.model.*;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;


/**
 *
 */
public class NullFilterEditor extends FilterEditor {
	/**
	 * Creates a new NullFilterEditor object.
	 */
	public NullFilterEditor() {
		add(new JLabel("There is no Editor for this Filter"));
	}

	/**
	 * Return the Description of this Editor that will
	 * go into its tab
	 */
	public String getFilterID() {
		return "";
	}

	/**
	 * Return a user friendly description of the class of filters edited by this editor
	 */
	public String getDescription() {
		return "";
	}

	/**
	 * This Editor should be able to read in a Filter that it produced
	 * and redisplay that Filter in the Editor, so that the Filter can be
	 * edited.
	 */
	public void editFilter(Filter filter) {
	}

	/**
	 * Create a filter initialized to the proper default values
	 */
	public Filter createDefaultFilter() {
		return null;
	}

	/**
	 * Return the class of filter subclass that I actually want to edit
	 */
	public Class getFilterClass() {
		return null;
	}
}
