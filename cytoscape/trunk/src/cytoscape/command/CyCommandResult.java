/*
 File: CyCommandResult.java

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CyCommandResult returns the results that result from the execution of a
 * {@link CyCommand} execution.  Generally, a CyCommand would create a CyCommandResult
 * object at the beginning of the execute() method.  A CyCommand should never
 * return a <b>null</b> to indicate failure, and should throw {@link CyCommandException}
 * instead.  This will avoid a lot of redundant checking for null returns.
 */
public class CyCommandResult {
	private boolean errors;
	private List<String>messages;
	private List<String>errorMessages;
	private Map<String,Object> results;

	/**
	 * Create a new empty CyCommandResult.
	 */
	public CyCommandResult() {
		messages = new ArrayList();
		results = null;
		errorMessages = new ArrayList();
		errors = false;
	}

	/**
	 * Create a new CyCommandResult with an initial message and execution results.
	 *
	 * @param message a message to include in the result.  Note that more messages
	 * may be added later.
	 * @param results a map of the results from the execution.
	 */
	public CyCommandResult(String message, Map<String,Object> results) {
		errors = false;
		messages = new ArrayList();
		errorMessages = new ArrayList();
		messages.add(message);
		this.results = results;
	}

	/**
	 * Add a message to the list of messages that results from this execution.  This
	 * is an easy way to provide a record of the operation of a command that might
	 * be displayed to the user by a client plugin.
	 *
	 * @param message the message to add
	 */
	public void addMessage(String message) {
		messages.add(message);
	}

	/**
	 * Add an error message to the list of errors for this execution.  As
	 * a byproduct, this will set the error flag.
	 *
	 * @param message the error message to add
	 */
	public void addError(String message) {
		errorMessages.add(message);
		errors = true;
	}

	/**
	 * Add a result to the result set for this execution.
	 *
	 * @param key the result whose value we're setting
	 * @param value the result value itself
	 */
	public void addResult(String key, Object value) {
		if (results == null) results = new HashMap();
		results.put(key, value);
	}

	/**
	 * Return true if the execution was successful, false
	 * if it failed.
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean successful() {
		if (errors) return false;
		return true;
	}

	/**
	 * Set all of the results for this execution.
	 *
	 * @param results the map of key, value pairs
	 */
	public void setResults(Map<String, Object> results) {
		this.results = results;
	}

	/**
	 * Return a particular result from an execution as
	 * a String.  Internally, results are stored as name, value
	 * pairs where the value is an Object that can be converted
	 * to a Stirng.  This method provides a short-hand to just
	 * calling toString on the Object-return signature.
	 *
	 * @param key the result value we're interested in
	 * @return the string equivilent of that value
	 */
	public String getStringResult(String key) {
		if (results != null && results.containsKey(key))  {
			return results.get(key).toString();
		}
		return null;
	}

	/**
	 * Return a particular result from an execution.
	 * Internally, results are stored as name, value
	 * pairs where the value is an Object that can be converted
	 * to a Stirng.  This method returns the stored Object
	 * with no attempt to do any conversion.
	 *
	 * @param key the result value we're interested in
	 * @return the resulting value
	 */
	public Object getResult(String key) {
		if (results != null && results.containsKey(key)) return results.get(key);
		return null;
	}

	/**
	 * Return all of the results from an execution as a map
	 * indexed by the result name.
	 * 
	 * @return the map of key, value pairs
	 */
	public Map<String, Object> getResults() {
		return results;
	}

	/**
	 * Return all of the messages from an execution.
	 * 
	 * @return list of message strings
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * Return all of the erros from an execution.
	 * 
	 * @return list of erros strings
	 */
	public List<String> getErrors() {
		return errorMessages;
	}
	
}
