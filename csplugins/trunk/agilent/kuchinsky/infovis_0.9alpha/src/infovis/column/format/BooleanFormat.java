/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.format;

import java.text.*;
import java.text.Format;
import java.util.HashSet;

/**
 * Class BooleanFormat
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class BooleanFormat extends Format {
    private static BooleanFormat instance;
    
    protected HashSet yesSet = new HashSet();
    protected HashSet noSet = new HashSet();
    static final String yesString[] = {
            "yes",
            "y",
            "true",
            "t",
            "1"
    };
    
    static final String noString[] = {
            "no",
            "n",
            "false",
            "f",
            "0"
    };
    
    public static BooleanFormat getInstance() {
        if (instance == null) {
            instance = new BooleanFormat();
        }
        return instance;
    }
    
    public static BooleanFormat setInstance(BooleanFormat format) {
        BooleanFormat f = instance;
        instance = format;
        return f;
    }
    
    public BooleanFormat() {
        initialize();
    }
    
    protected void initialize() {
        for (int i = 0; i < yesString.length; i++) {
            addYesString(yesString[i]);
        }
        for (int i = 0; i < noString.length; i++) {
            addNoString(noString[i]);
        }
    }
    
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
    
    public void addYesString(String yes) {
        yesSet.add(yes.toLowerCase());
    }
    
    public void addNoString(String no) {
        noSet.add(no.toLowerCase());
    }

    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        toAppendTo.append(obj.toString());
        return toAppendTo;
    }

}
