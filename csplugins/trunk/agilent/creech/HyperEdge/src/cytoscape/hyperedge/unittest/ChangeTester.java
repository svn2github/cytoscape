/* -*-Java-*-
********************************************************************************
*
* File:         ChangeTester.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/ChangeTester.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Wed Sep 21 09:15:21 2005
* Modified:     Fri Aug 18 07:38:03 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.hyperedge.unittest;

import cytoscape.hyperedge.event.ChangeListener;
import cytoscape.hyperedge.event.EventNote;

import junit.framework.Assert;


/**
 * Support for testing Change Events.
 * @author Michael L. Creech
 * @version 1.5
 */
public class ChangeTester implements ChangeListener {
    private Object _required_affected;
    private EventNote.Type _required_type;
    private EventNote.SubType _required_sub_type;
    private boolean _required_has_supporting_info;
    private Object _required_supporting_info;
    private boolean _check_supporting_info = true;
    private EventNote _last_event_note;

    public ChangeTester(Object required_affected, EventNote.Type required_type,
        EventNote.SubType required_sub_type,
        boolean required_has_supporting_info) {
        _required_affected = required_affected;
        _required_type = required_type;
        _required_sub_type = required_sub_type;
        _required_has_supporting_info = required_has_supporting_info;
        _check_supporting_info = false;
    }

    public ChangeTester(Object required_affected, EventNote.Type required_type,
        EventNote.SubType required_sub_type,
        boolean required_has_supporting_info, Object required_supporting_info) {
        _required_affected = required_affected;
        _required_type = required_type;
        _required_sub_type = required_sub_type;
        _required_has_supporting_info = required_has_supporting_info;
        _required_supporting_info = required_supporting_info;
    }

    public void setSupportingInfo(Object obj) {
        _required_supporting_info = obj;
    }

    public EventNote getLastEventNote() {
        return _last_event_note;
    }

    public void objectChanged(EventNote en) {
        _last_event_note = en;
        Assert.assertTrue(en.getAffected() == _required_affected);
        Assert.assertTrue(en.isEventType(_required_type));
        Assert.assertTrue(en.isEventSubType(_required_sub_type));
        Assert.assertTrue(en.hasSupportingInfo() == _required_has_supporting_info);

        if (_check_supporting_info) {
            if (_required_supporting_info != null) {
                Assert.assertTrue(_required_supporting_info.equals(
                        en.getSupportingInfo()));
            } else {
                Assert.assertTrue(_required_supporting_info == en.getSupportingInfo());
            }
        }
    }
}
