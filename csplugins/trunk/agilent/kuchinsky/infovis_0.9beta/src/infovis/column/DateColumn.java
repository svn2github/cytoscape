/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.column.format.UTCDateFormat;

/**
 * Specialization of <code>LongColumn</code> to manage dates.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 * @infovis.factory ColumnFactory "date" DENSE
 */
public class DateColumn extends LongColumn {

    /**
     * Creates a DateColumn with a specified name.
     * @param name the name
     */
    public DateColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a DateColumn with a specified name and reserved size.
     * @param name the name
     * @param reserve the reserved size
     */
    public DateColumn(String name, int reserve) {
        super(name, reserve);
        setFormat(UTCDateFormat.getSharedInstance());
    }

}
