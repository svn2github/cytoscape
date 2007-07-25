/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.column.format.ColorFormat;
import infovis.metadata.ValueCategory;

/**
 * Class ColorColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * @infovis.factory ColumnFactory "color" DENSE
 */
public class ColorColumn extends IntColumn {
    private static final long serialVersionUID = -6127011888416071260L;

    public ColorColumn(String name) {
        this(name, 10);

    }

    public ColorColumn(String name, int reserve) {
        super(name, reserve);
        getMetadata().put(ValueCategory.VALUE_CATEGORY_TYPE,
                ValueCategory.VALUE_CATEGORY_TYPE_EXPLICIT);
        setFormat(ColorFormat.getInstance());
    }

}