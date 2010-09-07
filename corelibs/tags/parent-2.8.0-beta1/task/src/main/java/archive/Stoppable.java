
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

package archive;


/**
 * A hook for stopping a running process and waiting for it to exit.
 * A process which this hook refers to can only be run once per instance of
 * this interface; once a process is stopped, it cannot magically
 * restart.  This implies that repeated calls to <code>stop()</code> will
 * have the same effect on the [running] process as a single call will.
 */
public interface Stoppable {
	/**
	 * Guarantees that the process has exited by the time this method returns.
	 * If <code>stop()</code> has been called and has returned before an
	 * application has started the corresponding process, the process should
	 * terminate immediately upon starting.  If a process has been started and
	 * has finished executing before <code>stop()</code> is called, a
	 * call to <code>stop()</code> should exit immediately.<p>
	 * If a framework using this interface chooses to support multiple threads,
	 * multiple concurrent calls to <code>stop()</code> should all block until
	 * the underlying process finishes.<p>
	 * The difference between <code>Haltable.halt()</code> and
	 * <code>Stoppable.stop()</code> is that <code>halt()</code> is
	 * non-blocking; <code>halt()</code> does not wait for a process to stop
	 * before it returns.
	 */
	public void stop();
}
