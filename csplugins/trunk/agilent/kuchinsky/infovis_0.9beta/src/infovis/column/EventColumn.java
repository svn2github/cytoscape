/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.utils.RowIterator;

import java.util.ArrayList;

import cern.colt.list.IntArrayList;

/**
 * Column containing list of timed events.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class EventColumn extends IntColumn {
    protected ArrayList eventValue;
    protected boolean valueUpdated;
    protected int minEvent = -1;
    protected int maxEvent = Integer.MAX_VALUE;
    
    public EventColumn(String name) {
        this(name, 10);
    }

    public EventColumn(String name, int reserve) {
        super(name, reserve);
        eventValue = new ArrayList(reserve);
    }
    
    protected void set(int index) {
    }
    
    public boolean isValueUndefined(int i) {
        return i < 0 
            || i > eventValue.size()
            || eventValue.get(i) == null;
    }
    
    public void setValueUndefined(int i, boolean undef) {
        if (isValueUndefined(i) == undef) return;
        readonly();
    }
    
    public void set(int index, int element) {
        readonly();
    }
    
    public int get(int index) {
        updateValues();
        return super.get(index);
    }    
    public int getMaxEvent() {
        return maxEvent;
    }
    public void setMaxEvent(int maxEvent) {
        if (this.maxEvent == maxEvent) {
            return;
        }
        this.maxEvent = maxEvent;
        valueUpdated = false;
        modified();
    }
    
    public int getMinEvent() {
        return minEvent;
    }
    
    public void setMinEvent(int minEvent) {
        if (this.minEvent == minEvent) {
            return;
        }
        this.minEvent = minEvent;
        valueUpdated = false;
        modified();
    }
    protected int filter(int index) {
        IntArrayList list = getEvents(index);
        return filter(list);
    }
    
    protected int filter(IntArrayList list) {
        if (list == null) return 0;
        int min, max;
        if (minEvent <= list.getQuick(0)) {
            min = 0;
        }
        else {
            min = list.binarySearch(minEvent);
            if (min < 0) {
                min = - min - 1;
            }
        }
        if (maxEvent >= list.getQuick(list.getQuick(list.size()-1))) {
            max = size()-1;
        }
        else {
            max = list.binarySearch(maxEvent);
            if (max < 0) {
                max = - max;
            }
        }
        return max - min + 1;
    }
    
    public void setSize(int newSize) {
        eventValue.ensureCapacity(newSize);
        if (newSize == 0) {
            eventValue.clear();
        }
        else if (newSize < eventValue.size()) {
            while (newSize < eventValue.size()){
                eventValue.remove(eventValue.size()-1);
            }
        }
        else {
            while(eventValue.size() < newSize) {
                eventValue.add(null);
            }
        }
        super.setSize(newSize);
        //FIXME notification won't work
    }
    
    public IntArrayList getEvents(int index) {
        return (IntArrayList)eventValue.get(index);
    }
    
    public void setEvents(int index, IntArrayList list) {
        eventValue.set(index, list);
        super.set(index, filter(list));
    }

    public void addEvent(int index, int event) {
        IntArrayList events = getEvents(index);
        if (events == null) {
            events = new IntArrayList();
        }
        events.add(event);
        setEvents(index, events);
    }
    
    public void addExtendEvent(int index, int event) {
        if (index >= size()) {
            setSize(index+1);
        }
        addEvent(index, event);
    }
    
    public void updateValues() {
        if (valueUpdated) {
            return;
        }
        try {
            disableNotify();
            for (RowIterator iter = iterator(); iter.hasNext(); ) {
                int index = iter.nextRow();
                super.set(index, filter(index));
            }
            valueUpdated = true;
        }
        finally {
            enableNotify();
        }
    }
}
