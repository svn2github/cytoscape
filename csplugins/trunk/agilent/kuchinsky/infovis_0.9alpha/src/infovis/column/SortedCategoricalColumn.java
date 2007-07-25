/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.utils.RowIterator;

import java.text.*;
import java.text.FieldPosition;
import java.text.Format;
import java.util.*;
/**
 * SortedCategoricalColumn is an IntColumn computed from a StringColumn containing the order of each string.
 * 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class SortedCategoricalColumn extends IntColumn {
    protected ColumnLink link;
    protected TreeMap map;
    protected String[] inverse;
    protected Comparator comp;

    class SortedCategoricalFormat extends Format {
        public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
            if (getInverseMap() == null)
                return null;
            if (!(obj instanceof Integer))
                return null;
            int index = ((Integer)obj).intValue();
            if (inverse == null
                || index < 0
                || index >= inverse.length)
                return null;
            pos.setBeginIndex(toAppendTo.length());
            toAppendTo.append(inverse[index]);
            pos.setEndIndex(toAppendTo.length());
            return toAppendTo;
        }
        public Object parseObject(String source, ParsePosition pos) {
            int index = pos.getIndex();
            int last = source.length();

            pos.setIndex(last);
            String catName = source.substring(index, last);
            Integer cat = (Integer) map.get(catName);
            return cat;
        }

    }

    public SortedCategoricalColumn(StringColumn column) {
        super("#SortedCategorical_" + column.getName());
        this.comp = column.getOrder();
        map = new TreeMap(comp);
        setFormat(new SortedCategoricalFormat());
        link = new ColumnLink(column, this) {
            public void update() {
                map.clear();
                for (RowIterator i = fromColumn.iterator();
                    i.hasNext();
                    ) {
                    int row = i.nextRow();
                    String key = fromColumn.getValueAt(row); 
                    LinkedList l = (LinkedList)map.get(key);
                    if (l == null) {
                        l = new LinkedList();
                        map.put(key, l);
                    }
                    l.add(new Integer(row));
                }
                toColumn.clear();
                int index = 0;
                for (java.util.Iterator iter = map.values().iterator();
                    iter.hasNext();
                    index++) {
                    LinkedList l = (LinkedList)iter.next();
                    //System.out.println("["+index+"]="+fromColumn.getValueAt(((Integer)l.getFirst()).intValue())+"/"+l.size());
                    for (java.util.Iterator j = l.iterator(); j.hasNext(); ) {
                        Integer i = (Integer) j.next();
                        setExtend(i.intValue(), index);
                    }
                }
                inverse = new String[map.size()];
                map.keySet().toArray(inverse);
            }
        };
        link.update();
    }
    
    public Comparator getComparator() {
        return comp;
    }
    
    public Map getMap() {
        return map;
    }
    
    public String[] getInverseMap() {
        return inverse;
    }

}
