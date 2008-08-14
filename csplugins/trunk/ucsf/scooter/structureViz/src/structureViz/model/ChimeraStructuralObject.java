/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package structureViz.model;

import java.lang.String;
import java.util.List;
import structureViz.model.ChimeraModel;

/**
 * This interface provides a common set of methods that are implemented
 * by the ChimeraModel, ChimeraChain, and ChimeraResidue classes.
 * 
 * @author scooter
 *
 */

public interface ChimeraStructuralObject {
		
	/**
	 * Return the Chimera selection specification for this object
	 *
	 * @return a String representing a Chimera atom-spec
	 */
	public String toSpec();

	/**
	 * Return a String representation for this object
	 *
	 * @return a String representing the object name
	 */
	public String toString();

	/**
	 * Return a String representation for this object
	 *
	 * @return a String representing the object name
	 */
	public String displayName();

	/**
	 * Return the userData for this object
	 *
	 * @return an Object representing the userData (usually TreePath)
	 */
	public Object getUserData();

	/**
	 * Set the userData for this object
	 *
	 * @param userData the Object representing the userData (usually TreePath)
	 */
	public void setUserData(Object userData);

	/**
	 * Return the ChimeraModel for this object
	 *
	 * @return the ChimeraModel this object is part of
	 */
	public ChimeraModel getChimeraModel();

	/**
	 * Set the "selected" state of this object
	 *
	 * @param selected boolean value as to whether this object is selected
	 */
	public void setSelected(boolean selected);

	/**
	 * Get the "selected" state of this object
	 *
	 * @return the selected state of this object
	 */
	public boolean isSelected();

	/**
	 * Get the children of this object (if any)
	 *
	 * @return the children of the object
	 */
	public List getChildren();
}
