/* 
* Created on 11. December 2007
 * 
 */
package de.layclust.taskmanaging;


/**
 * Exception for when the given type has not yet been implemented,
 * or has not been added to the respective factory class properly.
 * @author sita
 */
public class InvalidTypeException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidTypeException(String message) {
		super(message);		
	}
}
