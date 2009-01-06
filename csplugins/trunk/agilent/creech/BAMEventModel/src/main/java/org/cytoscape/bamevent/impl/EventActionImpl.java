package org.cytoscape.bamevent.impl;

import org.cytoscape.bamevent.EventAction;

    /**
     * Implementation of the EventAction interface.
     */
    public class EventActionImpl implements EventAction {
	private String action;
	EventActionImpl (String action) {
	    // TODO: validate non-null:
	    this.action = action;
	}
	public String getEventAction() {
	    return action;
	}
	public boolean isEventAction (EventAction typeToMatch) {
		return action.equals (typeToMatch.getEventAction());
	}
	public boolean isEventAction (String matchAction) {
	    return action.equals (matchAction);
	}
	
	@Override public String toString () {
	    return '[' + getClass().getSimpleName() + ":" + action + ']';
	}
    }
