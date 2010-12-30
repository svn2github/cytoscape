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
package structureViz.actions;

import java.util.ArrayList;
import java.util.List;

// structureViz imports
import structureViz.StructureViz;
import structureViz.model.Structure;

public class OpenTask implements Runnable {
	private String commandLabel;
	private Object userData;
	private Chimera chimera;
	
	public OpenTask(String commandLabel, Chimera chimera, Object userData) {
		this.commandLabel = commandLabel;
		this.userData = userData;
		this.chimera = chimera;
	}

	public void run() {
		List<Structure>structList = null;
		if (commandLabel.compareTo("all") == 0) {
			structList = (ArrayList)userData;
		} else {
			structList = new ArrayList<Structure>();
			structList.add((Structure)userData);
		}

		// Get the list of structures we already have open
		List<Structure>openStructs = chimera.getOpenStructs();

    // Send initial commands
    for (Structure structure: structList) {
			boolean open = false;
			String structureName = structure.name();
			for (Structure openStructure: openStructs) {
				if (structureName.equals(openStructure.name())) {
					// Map the model numbers
					structure.setModelNumber(openStructure.modelNumber(), openStructure.subModelNumber());
					open = true;
					break;
				}
			}
			if (open == false) {
				chimera.open(structure);
			}
		}
	}
}
