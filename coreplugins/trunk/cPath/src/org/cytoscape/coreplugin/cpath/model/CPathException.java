package org.cytoscape.coreplugin.cpath.model;

public class CPathException extends Exception {

    public CPathException (String msg) {
        super(msg);
    }

    public CPathException (String msg, Throwable t) {
        super(msg, t);
    }
}
