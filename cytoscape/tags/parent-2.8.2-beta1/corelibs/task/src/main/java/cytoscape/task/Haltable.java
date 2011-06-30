
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

package cytoscape.task;


/**
 * A hook for requesting that a running process be halted.
 * A process which this hook refers to can only be run once per instance of
 * this interface; once a process is stopped, it cannot magically
 * restart.  This implies that repeated calls to <code>halt()</code> will
 * have the same effect on the [running] process as a single call will.
 */
public interface Haltable {
	/**
	 * Requests that a running process be aborted.  <code>halt()</code> does
	 * not block; it returns quickly; it is likely that <code>halt()</code>
	 * will return before an underlying running process exits.
	 * If <code>halt()</code> is called before an underlying process is started,
	 * that underlying process should abort immediately if it is ever started.
	 * If an underlying process has been started and has finished executing
	 * before <code>halt()</code> is called, <code>halt()</code> will have no
	 * effect.
	 */
	public void halt();
}
