/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.format;

import java.io.ObjectStreamException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashSet;

/**
 * Format for boolean columns.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class BooleanFormat extends Format {
    private static BooleanFormat instance;
    
    protected HashSet yesSet = new HashSet();
    protected HashSet noSet = new HashSet();
    
    /** List of names for the value TRUE. */
    public static final String YES_STRING[] = {
            "yes",
            "y",
            "true",
            "t",
            "1"
    };
    
    /** List of names for the value FALSE. */
    public static final String NO_STRING[] = {
            "no",
            "n",
            "false",
            "f",
            "0"
    };
    
    /**
     * Returns an instance of that format.
     * @return an instance of that format
     */
    public static BooleanFormat getInstance() {
        if (instance == null) {
            instance = new BooleanFormat();
        }
        return instance;
    }
    
    /**
     * Sets the instance of that format.
     * @param format the format
     * @return the old format
     */
    public static BooleanFormat setInstance(BooleanFormat format) {
        BooleanFormat f = instance;
        instance = format;
        return f;
    }
    
    /**
     * Constructor.
     */
    public BooleanFormat() {
        initialize();
    }
    
    protected void initialize() {
        for (int i = 0; i < YES_STRING.length; i++) {
            addYesString(YES_STRING[i]);
        }
        for (int i = 0; i < NO_STRING.length; i++) {
            addNoString(NO_STRING[i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object parseObject(String source, ParsePosition pos) {
        String s = source.toLowerCase();
        if (yesSet.contains(s)) {
            pos.setIndex(s.length());
            return Boolean.TRUE;
        }
        if (noSet.contains(s)) {
            pos.setIndex(s.length());
            return Boolean.FALSE;
        }
        return null;
    }

    /**
     * Adds a new name for the YES value.
     * @param yes the new name.
     */
    public void addYesString(String yes) {
        yesSet.add(yes.toLowerCase());
    }
    
    /**
     * Adds a new name for the NO value.
     * @param no the new name
     */
    public void addNoString(String no) {
        noSet.add(no.toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        toAppendTo.append(obj.toString());
        return toAppendTo;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return getInstance();
    }
}
