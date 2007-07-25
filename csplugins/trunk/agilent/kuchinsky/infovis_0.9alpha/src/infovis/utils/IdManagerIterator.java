/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Class IdManagerIterator
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class IdManagerIterator extends TableIterator {
    protected IdManager idManager;
    protected int last = -1;

    public IdManagerIterator(IdManager idManager, int first, int end, boolean up) {
        super(first, end, up);
        this.idManager = idManager;
    }

    public IdManagerIterator(IdManager idManager, int first, int end) {
        this(idManager, first, end, first < end);
    }
    
    public IdManagerIterator(IdManager idManager, boolean up) {
        this(
                idManager, 
                up ? idManager.getMinId() : idManager.getMaxId(), 
                up ? (idManager.getMaxId() + 1) : (idManager.getMinId() - 1),
                up);
    }
    
    public int nextRow() {
        last = row;
        do {
            super.nextRow();
        }
        while (idManager.containsKey(row));
        return last;
    }
    public void remove() {
        idManager.free(last);
    }

}
