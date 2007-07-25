/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


/**
 * tree representation of a parsed Genealogical Data Communication
 * (GEDCOM) file.
 *
 * @author Mike Dean <mdean@bbn.com>
 */
class GEDCOM {
    /**
     * representation of a single node within a parsed GEDCOM tree.
     */
    class Node {
	String key = null;
	String tag = null;
	String value = null;

	Node(String key, String tag, String value) {
	    this.key = key;
	    this.tag = tag;
	    this.value = value;
	    if (key != null)
		keys.put(key, this);
	}

	/**
	 * vector of Node.
	 */
	Vector children = new Vector();

	void print(int depth) {
	    for (int i = 0; i < depth; i++)
		System.out.print("  ");
	    if (key != null)
		System.out.print(key + " ");
	    System.out.print(tag);
	    if (value != null)
		System.out.print(" " + value);
	    System.out.println();

	    Iterator iterator = children.iterator();
	    while (iterator.hasNext()) {
		Node child = (Node)iterator.next();
		child.print(depth + 1);
	    }
	}
    }

    static boolean debug = false;

    /**
     * map from String key to Node.
     */
    Hashtable keys = new Hashtable();

    /**
     * return the Node with the specified key (e.g. "@I1@").
     */
    Node lookup(String key) {
	return (Node)keys.get(key);
    }

    void parseLine(String line) {
	int    space1 = line.indexOf(' ');
	int    level = Integer.parseInt(line.substring(0, space1));
	String key = null;
	int    tagStart = space1 + 1;
	if (line.charAt(tagStart) == '@') {
	    int keyEnd = line.indexOf(' ', tagStart);
	    key = line.substring(tagStart, keyEnd);
	    tagStart = keyEnd + 1;
	}
	String tag = null;
	String value = null;
	int    tagEnd = line.indexOf(' ', tagStart);
	if (tagEnd == (-1)) {
	    tag = line.substring(tagStart);
	} else {
	    tag = line.substring(tagStart, tagEnd);
	    value = line.substring(tagEnd + 1);
	}

	if (debug) {
	    System.out.println("level = " + level);
	    System.out.println("key = " + key);
	    System.out.println("tag = " + tag);
	    System.out.println("value = " + value);
	    System.out.println();
	}

	Node node = new Node(key, tag, value);
	while (level < stack.size())
	    stack.pop();
	stack.push(node);
	if (level > 0) {
	    Node parent = (Node)stack.elementAt(level - 1);
	    parent.children.add(node);
	}
    }

    /**
     * stack used while parsing.
     */
    Stack stack = new Stack();

    /**
     * parse the specified file and return a tree representation.
     */
    static GEDCOM parseFile(String path) throws Exception {
	GEDCOM				   retval = new GEDCOM();

	BufferedReader stream = new BufferedReader(new FileReader(path));
	String				   line;
	while ((line = stream.readLine()) != null) {
	    retval.parseLine(line);
	}

	return retval;
    }
}
