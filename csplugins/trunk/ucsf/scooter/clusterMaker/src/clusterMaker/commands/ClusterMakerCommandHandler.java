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
package clusterMaker.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.layout.Tunable;

// clusterMaker imports
import clusterMaker.algorithms.ClusterProperties;


/**
 * 
 */
abstract class ClusterMakerCommandHandler extends AbstractCommandHandler {

	public ClusterMakerCommandHandler(String namespace) {
		super(CyCommandManager.reserveNamespace(namespace));
	}

	public void addArguments(String command, ClusterProperties props) {
		if (props == null) {
			addArgument(command);
			return;
		}

		for (Tunable t: props.getTunables()) {
			if (t.getType() == Tunable.BUTTON || t.getType() == Tunable.GROUP)
				continue;

			// Is there a default value for this prop?
			if (t.getValue() != null)
				addArgument(command, t.getName(), t.getValue().toString());
			else
				addArgument(command, t.getName());
		}
	}

	public Tunable getTunable(Collection<Tunable>args, String name) {
		for (Tunable t: args) {
			if (t.getName().equals(name)) return t;
		}
		return null;
	}

	public void setTunables(ClusterProperties props, Collection<Tunable>args) throws Exception {
		// Set the Tunables
		for (Tunable t: args) {
			if (props.get(t.getName()) != null) {
				Tunable target = props.get(t.getName());
				Object value = t.getValue();
				try {
					if ((target.getType() == Tunable.LIST) && 
					    (t.getType() == Tunable.STRING)) {
						setListTunable(target, value.toString());
					} else {
						target.setValue(value.toString());
					}
				} catch (Exception e) {
					throw new Exception("Unable to parse value for "+
					                     t.getName()+": "+value.toString());
				}
			}
		}
	}

	private void setListTunable(Tunable listTunable, String value) {
		Object[] optionList = (Object [])listTunable.getLowerBound();
		String[] inputList = value.split(",");
		String v = "";
		Integer first = null;
		for (int i = 0; i < inputList.length; i++) {
			for (int j = 0; j < optionList.length; j++) {
				if (optionList[j].toString().equals(inputList[i])) {
					v = v+","+j;
					if (first == null) first = new Integer(j);
				}
			}
		}
		v = v.substring(1);
		if (listTunable.checkFlag(Tunable.MULTISELECT)) {
			listTunable.setValue(v);
		} else {
			listTunable.setValue(first);
		}
	}

}
