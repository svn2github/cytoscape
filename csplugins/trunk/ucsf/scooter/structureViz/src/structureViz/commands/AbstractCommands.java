/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
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
package structureViz.commands;

import java.util.ArrayList;
import java.util.List;

import cytoscape.command.CyCommandResult;

import structureViz.actions.Chimera;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;

/**
 * 
 */
public class AbstractCommands {

	protected static List<ChimeraStructuralObject> specListFromStructureList(Chimera chimera, List<Structure>structureList) {
		List<ChimeraStructuralObject> objList = new ArrayList<ChimeraStructuralObject>();
		for (Structure st: structureList) {
			objList.add(chimera.getChimeraModel(st.modelNumber()));
		}
		return objList;
	}

	protected static boolean legalArgument(String arg, String[] legalList) {
		if (arg == null) 
			return true;
		for (String legalArg: legalList) {
			if (arg.equals(legalArg))
				return true;
		}
		return false;
	}

	protected static CyCommandResult addReplies(CyCommandResult result, List<String>reply, String defaultReply) {
		if (reply != null && reply.size() > 0) 
			for (String s: reply)
				result.addMessage(s);
		else
			result.addMessage(defaultReply);

		return result;
	}
}
