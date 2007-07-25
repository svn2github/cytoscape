/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Class IdManager
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class IdManager extends IntIntSortedMap {
    protected int timeStamp = 0;
    protected int minId;
    protected int maxId;

    public IdManager() {
        minId = 0;
        maxId = -1;
        assert(checkInvariant());
    }
    
    public IdManager(int size) {
        minId = 0;
        maxId = size-1;
        assert(checkInvariant());
    }
    
    public IdManager(IdManager other) {
        super(other);
        minId = other.minId;
        maxId = other.maxId;
        assert(checkInvariant());
    }
    
    public void clear() {
        minId = 0;
        maxId = -1;
        super.clear();
        assert(checkInvariant());
    }

    public int getMinId() {
        return minId;
    }

    public int getMaxId() {
        return maxId;
    }

    public boolean isFree(int id) {
        return id >= 0 &&
            (id < minId || id > maxId || containsKey(id));
    }
    
    public boolean checkInvariant() {
        if (isEmpty()) {
            return minId <= maxId 
            || (minId == 0 && maxId == -1);
        }
        if (minId >= maxId) return false;
        for (RowIterator iter = keyIterator(); iter.hasNext(); ) {
            int f = iter.nextRow();
            if (f <= minId || f >= maxId) 
                return false;
        }
        return true;
    }

    public void free(int id) {
        assert(checkInvariant());
        if (isFree(id))
            return;
        if (id == maxId) {
            while (!isEmpty() && lastKey() == (maxId - 1)) {
                maxId--;
                remove(maxId);
            }
            maxId--;
            if (isEmpty() && minId > maxId) {
                minId = 0;
                maxId = -1;
            }
        }
        else if (id == minId) {
            while (!isEmpty() && firstKey() == (minId+1)) {
                minId++;
                remove(minId);
            }
            minId++;
            if (isEmpty() && minId > maxId) {
                minId = 0;
                maxId = -1;
            }
        }
        else {
            put(id, timeStamp++);
        }
        assert(checkInvariant());
    }

    public int newId() {
        int newId;
        if (isEmpty()) {
            if (minId == 0) {
                newId = ++maxId;
            }
            else
                newId = --minId;
        }
        else {
            newId = firstKey();
            remove(newId);
        }
        assert(checkInvariant());
        return newId;
    }
    
    public int getIdCount() {
        return maxId - minId + 1 - size();
    }

    public RowIterator iterator() {
        return new IdManagerIterator(this, true);
    }

    public RowIterator reverseIterator() {
        return new IdManagerIterator(this, false);
    }
}

