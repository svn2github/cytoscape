/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.*;
import infovis.io.WrongFormatException;

import java.awt.geom.Rectangle2D.Float;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;

// import com.hp.hpl.jena.rdf.arp.*;

/**
 * Class RDFGraphReader
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * 
 * at infovis.factory GraphReaderFactory rdf at infovis.factory
 * GraphReaderFactory owl
 */
public class RDFGraphReader extends AbstractGraphReader
// implements StatementHandler
{
    static final Logger         logger = Logger.getLogger(RDFGraphReader.class);
    protected HashMap           resourceMap;
    protected StringColumn      resourceColumn;
    protected BooleanColumn     isAnonymousColumn;
    protected BooleanColumn     isLiteralColumn;
    protected CategoricalColumn predColumn;

    // protected ARP arp;

    public RDFGraphReader(InputStream in, String name, Graph graph) {
        super(in, name, graph);
        // arp = new ARP();
        // arp.setStatementHandler(this);
    }

    public Float getBbox() {
        return null;
    }

    public boolean load() throws WrongFormatException {
        resourceMap = new HashMap();
        resourceColumn = StringColumn.findColumn(
                graph.getVertexTable(),
                "resource");
        isAnonymousColumn = BooleanColumn.findColumn(
                graph.getVertexTable(),
                "isAnonymous");
        isLiteralColumn = BooleanColumn.findColumn(
                graph.getVertexTable(),
                "isLiteral");

        predColumn = new CategoricalColumn("predicate");
        graph.getEdgeTable().addColumn(predColumn);

        // try {
        // arp.load(in);
        // }
        // catch(Exception e) {
        // logger.error("Trying to read "+getName(), e);
        // return false;
        // }
        return true;
    }

    public int findVertex(String uri) {
        Object o = resourceMap.get(uri);
        int v1;
        if (o == null) {
            v1 = graph.addVertex();
            resourceColumn.setExtend(v1, uri);
            resourceMap.put(uri, new Integer(v1));
        }
        else {
            v1 = ((Integer) o).intValue();
        }
        return v1;

    }

    /*
     * public int findVertex(AResource res) { String uri; if (res.isAnonymous()) {
     * uri = res.getAnonymousID(); } else { uri = res.getURI(); }
     * 
     * int v1 = findVertex(uri); if (res.isAnonymous()) {
     * isAnonymousColumn.setExtend(v1, true); } return v1; }
     * 
     * public void statement(AResource subj, AResource pred, ALiteral lit) { int
     * v1 = findVertex(subj); String value = lit.toString(); int v2 =
     * findVertex(value); isLiteralColumn.setExtend(v2, true); int e =
     * graph.findEdge(v1, v2); predColumn.setValueOrNullAt(e, pred.getURI()); }
     * 
     * public void statement(AResource subj, AResource pred, AResource obj) {
     * int v1 = findVertex(subj); int v2 = findVertex(obj); int e =
     * graph.findEdge(v1, v2); predColumn.setValueOrNullAt(e, pred.getURI()); }
     */
}
