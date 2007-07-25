/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.io;

import infovis.Tree;
import infovis.column.*;
import infovis.io.WrongFormatException;
import infovis.metadata.AggregationConstants;

import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * 
 * Creates a Class Tree from a Jar File.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 * 
 * @infovis.factory TreeReaderFactory jar
 */
public class JarTreeReader extends AbstractTreeReader {
    public static final String    COLUMN_NAME    = "name";
    public static final String    COLUMN_SIZE    = "size";
    public static final String    COLUMN_PATH    = "path";
    public static final String    COLUMN_PACKAGE = "package";

    protected String              jarName;
    protected LongColumn          sizeColumn;
    protected StringColumn        nameColumn;
    protected StringColumn        pathColumn;
    protected IntColumn           packageColumn;
    protected JarInputStream      jarIS;
    protected JarEntryClassLoader loader;

    class JarEntryClassLoader extends ClassLoader {
        Map map;
        public JarEntryClassLoader() {
            map = new HashMap();
        }
        public void addClass(String name, byte[] b) {
            map.put(name, b);
        }
        
        public Map getMap() { return map; }
        protected Class findClass(String name) throws ClassNotFoundException {
            byte[] b = (byte[])map.get(name);
            if (b == null) return null;
            return defineClass(name, b, 0, b.length);
        }
        
        public int getSize(String name) {
            byte[] b = (byte[])map.get(name);
            if (b == null) return -1;
            return b.length;
        }
    }

    public JarTreeReader(InputStream in, String name, Tree tree) {
        super(in, "jar", tree);

        loader = new JarEntryClassLoader();

        nameColumn = StringColumn.getColumn(tree, COLUMN_NAME);
        if (nameColumn == null) {
            nameColumn = new StringColumn(COLUMN_NAME);
        }
        sizeColumn = LongColumn.getColumn(tree, COLUMN_SIZE);
        if (sizeColumn == null) {
            sizeColumn = new LongColumn(COLUMN_SIZE);
        }
        sizeColumn.getMetadata().addAttribute(
                AggregationConstants.AGGREGATION_TYPE,
                AggregationConstants.AGGREGATION_TYPE_ADDITIVE);
        
        pathColumn = StringColumn.getColumn(tree, COLUMN_PATH);
        if (pathColumn == null) {
            pathColumn = new StringColumn(COLUMN_PATH);
        }
        packageColumn = IntColumn.getColumn(tree, COLUMN_PACKAGE);
        if (packageColumn == null) {
            packageColumn = new CategoricalColumn(COLUMN_PACKAGE);
        }
    }

    protected int loadClass(String name, Map map) {
        int node;
        
        if (name == null) {
            return Tree.ROOT;
        }
        Integer i = (Integer)map.get(name);

        if (i != null) {
            return i.intValue();
        }
        Class c;
        try {
            c = loader.loadClass(name);
        }
        catch(ClassNotFoundException e) {
            return Tree.ROOT;
        }
        if (c.isInterface() || c.getSuperclass() == null) {
            return Tree.ROOT;
        }
        node = tree.addNode(loadClass(c.getSuperclass().getName(), map));

        map.put(name, new Integer(node));
        String path = c.getName();
        pathColumn.setExtend(node, path);
        int lastDot = path.lastIndexOf('.');
        name = path;
        String pack = "";
        if (lastDot != -1) {
            name = path.substring(lastDot + 1);
            pack = path.substring(0, lastDot);
        }
        nameColumn.setExtend(node, name);
        packageColumn.setValueOrNullAt(node, pack);
        int size = loader.getSize(name);
        if (size != -1)
            sizeColumn.setExtend(node, size);

        return node;
    }

    public boolean load() throws WrongFormatException {
        try {
            jarIS = new JarInputStream(getIn());
            
            JarEntry entry;
            while ((entry = jarIS.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".class") && name.indexOf('$') == -1) {
                    name = name.substring(0, name.length() - 6);
                    name = name.replace('/', '.');
                    int len = (int) entry.getSize();
                    byte[] b = new byte[len];
                    int readLen = 0;
                    int totalLen = 0;
                    while (readLen != -1) {
                        readLen = jarIS.read(b, totalLen, len - totalLen);
                        if (readLen != -1)
                            totalLen += readLen;
                        if (readLen == 0)
                            break;
                    }
                    try {
                        loader.addClass(name, b);
                    } catch (Exception ex) {
                        ; // ignore
                    }
                }
            }
            HashMap map = new HashMap();
            loadClass(Object.class.getName(), map);
            for (Iterator iter = loader.getMap().keySet().iterator(); iter.hasNext(); ) {
                String name = (String)iter.next();
                loadClass(name, map);
            }
        } catch (Exception e) {
            return false;
        }
        tree.addColumn(nameColumn);
        tree.addColumn(sizeColumn);
        tree.addColumn(pathColumn);
        tree.addColumn(packageColumn);
        return true;
    }

    public boolean loadFile(String jarName) {
        if (loader == null)
            return false;
        this.jarName = jarName;
        return load();
    }

}
