package org.example.tunable;

/**
 * Allows an object to listen to changes in a Handler.
 */
public interface HandlerListener { 
	void handlerChanged(Handler otherHandler);
}
