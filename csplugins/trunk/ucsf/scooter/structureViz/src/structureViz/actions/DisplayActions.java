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
package structureViz.actions;

import java.util.ArrayList;
import java.util.List;

import structureViz.actions.Chimera;

/**
 * 
 */
public class DisplayActions {
	public static List<String> colorAction(Chimera chimera, String atomSpec, String color, String type) {
		return chimera.commandReply(addAtomSpec("color "+color+type,atomSpec));
	}

	public static List<String> rainbowAction(Chimera chimera, String atomSpec) {
		return chimera.commandReply(addAtomSpec("rainbow",atomSpec));
	}

	public static List<String> presetAction(Chimera chimera, String preset) {
		chimera.select("preset apply "+preset);
		return new ArrayList<String>();
	}

	public static List<String> displayAction(Chimera chimera, String atomSpec, String structSpec, boolean hide) {
		String command = "display";
		if (hide) command = "~display";
		return chimera.commandReply(addAtomSpec(command,atomSpec,structSpec));
	}

	public static List<String> focusAction(Chimera chimera, String atomSpec) {
		return chimera.commandReply(addAtomSpec("focus",atomSpec));
	}

	public static List<String> selectAction(Chimera chimera, String atomSpec, String structSpec, boolean clear) {
		String command = "select";
		if (clear) command = "~select";
		return chimera.commandReply(addAtomSpec(command,atomSpec,structSpec));
	}

	public static List<String> depictAtomsAction(Chimera chimera, String atomSpec, String depiction) {
		if (depiction.equals("none"))
			return displayAction(chimera, atomSpec, null, true);

		return chimera.commandReply(addAtomSpec("repr "+depiction, atomSpec));
	}

	public static List<String> depictRibbonsAction(Chimera chimera, String atomSpec, String depiction) {
		if (depiction.equals("none"))
			return chimera.commandReply(addAtomSpec("~ribbon",atomSpec));

		String reprCommand = addAtomSpec("ribrepr "+depiction, atomSpec);
		String ribbonCommand = addAtomSpec(";ribbon", atomSpec);
		return chimera.commandReply(reprCommand+ribbonCommand);
	}

	public static List<String> depictSurfacesAction(Chimera chimera, String atomSpec, String depiction, int transparency) {
		if (depiction.equals("none"))
			return chimera.commandReply(addAtomSpec("~surface",atomSpec));

		String reprCommand = addAtomSpec("surfrepr "+depiction, atomSpec);
		String surfaceCommand = addAtomSpec(";surface", atomSpec);
		if (transparency >= 0) {
			String transparencyCommand = addAtomSpec(";surfacetransparency "+transparency+"%", atomSpec);
			return chimera.commandReply(reprCommand+transparencyCommand+surfaceCommand);
		}
		return chimera.commandReply(reprCommand+surfaceCommand);
	}

	public static	List<String> moveAxisAction(Chimera chimera, String modelSpec, String axis, Double d) {
		if (modelSpec == null)
			return chimera.commandReply("move "+axis+" "+d);
		else
			return chimera.commandReply("move "+axis+" "+d+" models "+modelSpec);
	}

	public static	List<String> rotateAxisAction(Chimera chimera, String modelSpec, String axis, Double angle, String center) {
		String command = "turn "+axis+" "+angle;
		if (modelSpec != null) command += " models "+modelSpec;
		if (center != null) command += " center "+center;
		return chimera.commandReply(command);
	}

	private static String addAtomSpec(String command, String atomSpec, String structSpec) {
		String com = addAtomSpec(command, atomSpec);
		if (atomSpec != null)
			com += " & "+structSpec;
		else
			com += " "+structSpec;
		return com;
	}

	private static String addAtomSpec(String command, String atomSpec) {
		if (atomSpec != null) {
			return command+" "+atomSpec;
		}
		return command;
	}
}
