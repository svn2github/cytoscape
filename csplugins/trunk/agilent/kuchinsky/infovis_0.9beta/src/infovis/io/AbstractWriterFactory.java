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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

/**
 * Class AbstractWriterFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public abstract class AbstractWriterFactory extends BasicFactory {
    ArrayList     creators = new ArrayList();
    static Logger logger   = Logger.getLogger(AbstractReaderFactory.class);

    public AbstractWriterFactory(String factoryName) {
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
    public AbstractWriter create(String name, Table table) {
        AbstractWriter ret = null;
        for (int i = 0; i < creators.size(); i++) {
            Creator c = getCreatorAt(i);
            ret = c.create(name, table);
            if (ret != null)
                break;
        }
        return ret;
    }

    public boolean tryWrite(String name, Table table) {
        AbstractWriter ret = null;
        for (int i = 0; i < creators.size(); i++) {
            Creator c = getCreatorAt(i);
            ret = c.create(name, table);
            if (ret != null) {
                try {
                    boolean ok = ret.write();
                    if (ok)
                        return ok;
                } catch (Exception e) {
                    logger.error("While writing table "+name, e);
                }
            }
        }
        return false;
    }

    public interface Creator {
        public String getName();
        public String getSuffix();
        public AbstractWriter create(String name, Table table);
        public AbstractWriter create(OutputStream out, String name, Table table);
    }

    public abstract static class AbstractCreator implements Creator {
        protected String name;
        protected String suffix;

        public AbstractCreator(String name, String suffix) {
            this.name = name;
            this.suffix = suffix;
        }

        public String getName() {
            return name;
        }

        public String getSuffix() {
            return suffix;
        }

        public OutputStream open(String name, boolean compress)
                throws IOException {
            OutputStream out = null;
            OutputStream os = new FileOutputStream(name);

            if (compress) {
                os = new GZIPOutputStream(os);
            }
            out = new BufferedOutputStream(os);

            return out;
        }

        public AbstractWriter create(String name, Table table) {
            boolean compress = false;
            if (name.endsWith(".gz") || name.endsWith(".Z")) {
                compress = true;
            }
            try {
                return create(open(name, compress), name, table);
            } catch (IOException e) {
                logger.error("Cannot create file for " + name, e);
                return null;
            }
        }
    }
}
