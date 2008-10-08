
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

package cytoscape.hyperedge.unittest;

import cytoscape.hyperedge.event.ChangeListener;
import cytoscape.hyperedge.event.EventNote;

import junit.framework.Assert;


/**
 * Support for testing Change Events.
 * @author Michael L. Creech
 */
public class ChangeTester implements ChangeListener {
    private Object requiredAffected;
    private EventNote.Type requiredType;
    private EventNote.SubType requiredSubType;
    private boolean requiredHasSupportingInfo;
    private Object requiredSupportingInfo;
    private boolean checkSupportingInfo = true;
    private EventNote lastEventNote;
    /**
     * Create a ChangeTester with the given event information.
     * @param requiredAffected the object affected by the event.
     * @param requiredType the primary type of the change event.
     * @param requiredSubType the supporting secondary type of the change event.
     * @param requiredHasSupportingInfo the supporting info for this event.
     */
    public ChangeTester(final Object requiredAffected, final EventNote.Type requiredType,
        final EventNote.SubType requiredSubType,
        final boolean requiredHasSupportingInfo) {
        this.requiredAffected = requiredAffected;
        this.requiredType = requiredType;
        this.requiredSubType = requiredSubType;
        this.requiredHasSupportingInfo = requiredHasSupportingInfo;
        this.checkSupportingInfo = false;
    }
    /**
     * Create a ChangeTester with the given event information.
     * @param requiredAffected the object affected by the event.
     * @param requiredType the primary type of the change event.
     * @param requiredSubType the supporting secondary type of the change event.
     * @param requiredHasSupportingInfo true iff this event requires supporting info to be available.
     * @param requiredSupportingInfo the supporting info for this event.
     */
    public ChangeTester(final Object requiredAffected, final EventNote.Type requiredType,
        final EventNote.SubType requiredSubType,
        final boolean requiredHasSupportingInfo, final Object requiredSupportingInfo) {
	this.requiredAffected = requiredAffected;
	this.requiredType = requiredType;
	this.requiredSubType = requiredSubType;
	this.requiredHasSupportingInfo = requiredHasSupportingInfo;
	this.requiredSupportingInfo = requiredSupportingInfo;
    }

    /**
     * Set the supoorting information of this ChangeTester.
     * @param obj the supporting information.
     */
    public void setSupportingInfo(final Object obj) {
        requiredSupportingInfo = obj;
    }

    /**
     * @return the last EventNote set.
     */
    public EventNote getLastEventNote() {
        return lastEventNote;
    }
    /**
     * {@inheritDoc}
     */
    public void objectChanged(final EventNote en) {
        lastEventNote = en;
        Assert.assertTrue(en.getAffected() == requiredAffected);
        Assert.assertTrue(en.isEventType(requiredType));
        Assert.assertTrue(en.isEventSubType(requiredSubType));
        Assert.assertTrue(en.hasSupportingInfo() == requiredHasSupportingInfo);

        if (checkSupportingInfo) {
            if (requiredSupportingInfo != null) {
                Assert.assertTrue(requiredSupportingInfo.equals(
                        en.getSupportingInfo()));
            } else {
                Assert.assertTrue(requiredSupportingInfo == en.getSupportingInfo());
            }
        }
    }
}
