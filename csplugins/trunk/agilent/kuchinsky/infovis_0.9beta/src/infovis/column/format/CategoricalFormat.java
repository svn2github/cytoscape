/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.format;

import infovis.column.StringColumn;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;

/**
 * Format for storing categorical string data in an IntColumn.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class CategoricalFormat extends Format {
    protected Map          categories;
    protected StringColumn inverse;

    /**
     * Constructor.
     * 
     * @param name
     *            the categorical format name.
     */
    public CategoricalFormat(String name) {
        categories = new HashMap();
        inverse = new StringColumn(name);
    }

    /**
     * Constructor.
     */
    public CategoricalFormat() {
        this("#categories");
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return categories.hashCode() + inverse.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CategoricalFormat)) {
            return false;
        }
        CategoricalFormat other = (CategoricalFormat) obj;
        if (!categories.equals(other.categories)) {
            return false;
        }
        if (!inverse.equals(other.inverse)) {
            return false;
        }
        return true;
    }

    /**
     * Associate a category name with a value.
     * 
     * @param name
     *            the category name
     * @param value
     *            the integer value.
     */
    public void putCategory(String name, int value) {
        if (value == -1) return;
        Integer i = new Integer(value);
        categories.put(name, i);
        inverse.setExtend(value, name);
    }

    /**
     * Get the category associated with a name.
     * 
     * @param name
     *            the name.
     * @return the category or -1 if not defined.
     */
    public int getCategory(String name) {
        Integer i = (Integer) categories.get(name);
        if (i == null)
            return -1;
        return i.intValue();
    }

    /**
     * Find a category associated with a name, creating it if it doesn't exist
     * yet.
     * 
     * @param name
     *            the category name
     * @return the integer value associated with the name.
     */
    public int findCategory(String name) {
        if (name == null) return -1;
        Integer i = (Integer) categories.get(name);
        if (i == null) {
            int v = categories.size();

            putCategory(name, v);
            return v;
        }
        return i.intValue();
    }

    /**
     * Returns the category name given its index.
     * 
     * @param index
     *            the category index
     * @return the category name.
     */
    public String indexCategory(int index) {
        return inverse.get(index);
    }

    /**
     * Creates a new index associated with the given name.
     * 
     * @param name the name
     * @return a new index associated with the given name.
     */
    public int addCategory(String name) {
        int v = categories.size();
        putCategory(name, v);
        return v;
    }

    /**
     * Clears the association maps.
     */
    public void clear() {
        categories.clear();
        inverse.clear();
    }

    /**
     * @see java.text.Format#format(Object, StringBuffer, FieldPosition)
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        if (!(obj instanceof Integer))
            return null;
        pos.setBeginIndex(toAppendTo.length());
        toAppendTo.append(inverse.get(((Integer) obj).intValue()));
        pos.setEndIndex(toAppendTo.length());
        return toAppendTo;
    }

    /**
     * Select valid characters for categories.
     * 
     * @param c
     *            a character
     * 
     * @return <code>true</code> if the character is a separator.
     */
    public boolean isSeparator(char c) {
        // return !Character.isJavaIdentifierPart(c);
        // NSDL - all characters are significant
        return false;
    }

    /**
     * @see java.text.Format#parseObject(String, ParsePosition)
     */
    public Object parseObject(String source, ParsePosition pos) {
        int index = pos.getIndex();
        int last;

        for (last = pos.getIndex(); last != source.length(); last++) {
            if (isSeparator(source.charAt(last))) {
                break;
            }
        }
        pos.setIndex(last);
        String catName = source.substring(index, last);
        Integer cat = (Integer) categories.get(catName);
        if (cat == null) {
            addCategory(catName);
            cat = (Integer) categories.get(catName);
        }
        return cat;
    }

    /**
     * Returns the categories map.
     * 
     * @return the categories map.
     */
    public Map getCategories() {
        return categories;
    }
}
