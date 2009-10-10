/*
 File: CyCommandException.java

 Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies
 - University of California San Francisco

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

package cytoscape.command;

/**
 * The CyCommandException is thrown when a CyCommand execution encounters an
 * exception condition. 
 */
public class CyCommandException extends Exception {
	private CyCommandResult result;

	/**
	 * Create a CyCommandException with a message, but no results
	 *
	 * @param message the message to include
	 */
	public CyCommandException(String message) {
		super(message);
		result = new CyCommandResult();
		result.addError(message);
	}
	
	/**
	 * Create a CyCommandException with a message and a set of results
	 *
	 * @param message the message to include
	 * @param results the (possibly intermediate) results from this execution
	 */
	public CyCommandException(String message, CyCommandResult results) {
		super(message);
		this.result = results;
		this.result.addError(message);
	}
}
