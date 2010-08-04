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
package groupTool.ui;

// System imports
import java.util.List;
import java.util.ArrayList;

public enum Columns {
	NAME("Group Name", String.class),
	NETWORK("Network", String.class),
	NODES("Nodes", Integer.class),
	INTERNAL("Internal Edges", Integer.class),
	EXTERNAL("External Edges", Integer.class),
	VIEWER("Viewer", String.class);

	protected static List<Columns> columnList;

	protected static void addColumn(Columns column) { 
		if (columnList == null) columnList = new ArrayList<Columns>();
		columnList.add(column); 
	}

	public static int columnCount() { return columnList.size(); }

	public static String getColumnName(int col) { return columnList.get(col).toString(); }
	public static Columns getColumn(int col) { return columnList.get(col); }
	public static int getColumnNumber(Columns column) {
		for (int i = 0; i < columnList.size(); i++) {
			if (columnList.get(i).equals(column)) return i;
		}
		return -1;
	}

	private String columnName = null;
	private Class columnClass = null;

	Columns(String name, Class classType) {
		columnName = name;
		columnClass = classType;
		Columns.addColumn(this);
	}

	public String toString() {return columnName;}
	public Class getColumnClass() { return columnClass; }
}
