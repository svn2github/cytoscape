package csplugins.layout;

import cytoscape.logger.CyLogger;

import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.Profile;
import csplugins.layout.EdgeWeighter;

import cytoscape.util.intr.IntIntHash;
import cytoscape.util.intr.IntObjHash;

import cytoscape.*;

import cytoscape.view.*;

import giny.view.*;

import java.awt.Dimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.Collection;


/**
 * The LayoutLabelPartition class ....
 *
 * @author <a href="mailto:ghuck@gmail.com">Gerardo Huck</a>
 * @version 0.1
 */
public class LayoutLabelPartition extends LayoutPartition {

    public LayoutLabelPartition(int nodeCount, int edgeCount) {
	super(nodeCount, edgeCount);
    }

}
