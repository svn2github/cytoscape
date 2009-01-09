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
package cytoscape.data.webservice;

import java.util.EventObject;


/**
 * Event object used by Web Service Clients and the core.
 */
public class CyWebServiceEvent extends EventObject {
	
	public enum WSEventType {
		SEARCH_DATABASE,
		IMPORT_NETWORK,
		EXPAND_NETWORK,
		IMPORT_ATTRIBUTE,
		EXECUTE_ANALYSIS;
	}

	private final WSEventType type;
	private final Object parameter;
	private final WSEventType nextMove; 

//	public CyWebServiceEvent(Object source) {
//		this((String) source, null, null);
//	}

	public CyWebServiceEvent(String compatibleClient, WSEventType type, Object parameter) {
		this(compatibleClient, type, parameter, null);
	}
	
	public CyWebServiceEvent(String compatibleClient, WSEventType type, Object parameter, WSEventType nextMove) {
		super(compatibleClient);
		this.type = type;
		this.parameter = parameter;
		this.nextMove = nextMove;
	}

	public WSEventType getEventType() {
		return type;
	}

	public Object getParameter() {
		return parameter;
	}
	
	public WSEventType getNextMove() {
		return nextMove;
	}
}
