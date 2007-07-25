/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.io;

import infovis.Table;
import infovis.utils.BasicFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

/**
 * Abstract factory of table readers.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 */
public abstract class AbstractReaderFactory extends BasicFactory {
    ArrayList creators = new ArrayList();
    static Logger logger = Logger.getLogger(AbstractReaderFactory.class);

    /**
     * Constructor for TableReaderFactory.
     */
    public AbstractReaderFactory(String factoryName) {
        addDefaultCreators(factoryName);
    }

    /**
     * Adds a creator of table reader.
     * 
     * @param c
     *            the Creator.
     */
    public void add(Creator c) {
        creators.add(c);
    }

    /**
     * Removes a creator of table reader.
     * 
     * @param c
     *            the Creator.
     * 
     * @return <code>true</code> if the Creator was removed.
     */
    public boolean remove(Creator c) {
        return creators.remove(c);
    }

    /**
     * Returns an iterator over the added creators.
     * 
     * @return an iterator over the added creators.
     */
    public Iterator iterator() {
        return creators.iterator();
    }

    /**
     * Returns the Creator at a specified index.
     * 
     * @param index
     *            the index.
     * 
     * @return the Creator at a specified index or null.
     */
    public Creator getCreatorAt(int index) {
        return (Creator) creators.get(index);
    }

    public Creator getCreatorNamed(String name) {
        for (int i = 0; i < creators.size(); i++) {
            Creator c = getCreatorAt(i);
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    /**
     * Returns an <code>AbstractReader</code> able to read the specified
     * resource name or <code>null</code>.
     * 
     * @param name
     *            the resource name.
     * @param table
     *            the table.
     * 
     * @return an <code>AbstractReader</code> able to read the specified
     *         resource name or <code>null</code>.
     */
    public AbstractReader create(String name, Table table) {
        AbstractReader ret = null;
        for (int i = 0; i < creators.size(); i++) {
            Creator c = getCreatorAt(i);
            ret = c.create(name, table);
            if (ret != null)
                break;
        }
        return ret;
    }

    public AbstractReader create(InputStream in, String name, Table table) {
        AbstractReader ret = null;
        for (int i = 0; i < creators.size(); i++) {
            Creator c = getCreatorAt(i);
            ret = c.create(in, name, table);
            if (ret != null)
                break;
        }
        return ret;
    }

    public boolean tryRead(String name, Table table) {
        AbstractReader ret = null;
        for (int i = 0; i < creators.size(); i++) {
            Creator c = getCreatorAt(i);
            ret = c.create(name, table);
            if (ret != null) {
                try {
                    boolean ok = ret.load();
                    if (ok)
                        return ok;
                } catch (Exception e) {
                    logger.error("Cannot read table", e);
                }
            }

        }
        return false;
    }

    /**
     * Interface for Table Reader creators.
     */
    public static interface Creator {
        public String getName();

        public AbstractReader create(String name, Table table);
        
        public AbstractReader create(InputStream in, String name,
                Table table);
    }

    public abstract static class AbstractCreator implements Creator {
        protected String suffix;

        protected boolean needingOpen;

        public AbstractCreator(String suffix, boolean needingOpen) {
            this.suffix = suffix;
            this.needingOpen = needingOpen;
        }
        
        public AbstractCreator(String suffix) {
            this(suffix, true);
        }

        public String getName() {
            return suffix;
        }

        public InputStream open(String name, boolean decompress)
                throws IOException, FileNotFoundException {
            if (! needingOpen) {
                return null;
            }
            InputStream is = null;
            if (name.indexOf(':') != -1) {
                try {
                    URL url = new URL(name);
                    is = url.openStream();
                } catch (Exception e) {
                    ; // Ignore 
                    //logger.warn("Opening file "+name, e);
                }
            }
            if (is == null) {
                is = new FileInputStream(name);
            }
            if (decompress) {
                is = new GZIPInputStream(is);
            }
            if (is != null && ! (is instanceof BufferedInputStream)) {
                is = new BufferedInputStream(is);
            }

            return is;
        }

        public AbstractReader create(String name, Table table) {
            boolean decompress = false;
            String realName = name;
            if (! needingOpen) {
                return create(null, name, table);
            }
            if (name.endsWith(".gz") || name.endsWith(".Z")) {
                decompress = true;
                realName = name.substring(0, name.lastIndexOf('.'));
            }
            if (!realName.endsWith("." + suffix))
                return null;
            try {
                return create(open(name, decompress), name, table);
            } catch (Exception e) {
                logger.error("Creating from file "+name, e);
                return null;
            }
        }
    }
}