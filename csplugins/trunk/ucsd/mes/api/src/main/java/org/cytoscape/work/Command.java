
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.work;

/** 
 * An interface capturing the execution of an arbitrary
 * task.  Implementations of the Command interface can
 * be parameterized using {@link Tunable}s and used within
 * that framework or using normal getter/setter methods.  
 * Command status can be monitored using the 
 * {@link Monitorable} interface.
 * <p>
 * Should commands have context or namespace, allowing  
 * something like GUI specific commands?  Or just allow
 * this by extending this interface.
 */
public interface Command {
	/**
	 * The name of the Command. This should be unique among Commands and is immutable.
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName();

	/**
	 * A brief description of what the command accomplishes.
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription();

	/**
	 * Triggers execution of the command.
	 */
	public void execute();

	/**
	 * Cancels the execution of the command once the {@link Command#execute()}  method has
	 * been called.<p>I tend to think this belongs here, but I wonder if a separate
	 * Cancelable  interface might be more appropriate?</p>
	 *  <p>How closely, if at all, should this interface mimic the {@link java.lang.Thread}
	 * interface?</p>
	 *  <p>Do we want the ability to "pause".  I tend to think not, but perhaps in another
	 * interface?</p>
	 *  <p>In general the ability to cancel execution is managed by an internal boolean
	 * state variable in the implementation of this interface.  This method should set the state
	 * variable to true,  which should in turn be checked by in the body of the {@link
	 * Command#execute()} method.</p>
	 */
	public void cancel();
}
