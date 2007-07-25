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
 * Class DateColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * @infovis.factory ColumnFactory "date" DENSE
 */
public class DateColumn extends LongColumn {

    public DateColumn(String name) {
        this(name, 10);
    }

    public DateColumn(String name, int reserve) {
        super(name, reserve);
        setFormat(UTCDateFormat.getSharedInstance());
    }

}
