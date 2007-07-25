/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.*;
import infovis.io.WebLogParser;
import infovis.table.io.AbstractTableReader;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.log4j.Logger;

/**
 * The HTMLGraphReader is creates a graph from html web sites.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.26 $
 * 
 * @infovis.factory GraphReaderFactory html
 */
public class HTMLGraphReader extends AbstractTableReader {
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MIME_TYPE = "mime";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_REF = "ref";
    private static final Logger logger = Logger.getLogger(HTMLGraphReader.class);
    protected Graph graph;
    protected LinkedList queue;
    protected HashMap loaded;
    protected URL context;
    protected URL base;
    protected int currentNode;
    protected StringColumn nameColumn;
    protected StringColumn urlColumn;
    protected IntColumn lengthColumn;
    protected LongColumn dateColumn;
    protected StringColumn mimeColumn;
    protected StringColumn titleColumn;
    protected IntColumn refColumn;
    protected StringBuffer characters;
    protected PrintStream log;
    protected URL realRoot;
    protected boolean addingURL = true;

    protected ParserDelegator parser = new ParserDelegator();
    protected Callback callback = new Callback();
    

    class Callback extends HTMLEditorKit.ParserCallback {
        /**
         * @see javax.swing.text.html.HTMLEditorKit.ParserCallback#handleStartTag(Tag, MutableAttributeSet, int)
         */
        public void handleStartTag(
            Tag t,
            MutableAttributeSet a,
            int pos) {
            if (t == Tag.A) {
                add((String) a.getAttribute(HTML.Attribute.HREF));
            }
            else if (t == Tag.IMG) {
                add((String) a.getAttribute(HTML.Attribute.SRC));
                add((String) a.getAttribute(HTML.Attribute.USEMAP));
            }
            else if (t == Tag.SCRIPT) {
                add((String) a.getAttribute(HTML.Attribute.SRC));
            }
            else if (t == Tag.BODY) {
                add((String) a.getAttribute(HTML.Attribute.BACKGROUND));
            }
            else if (t == Tag.LINK) {
                add((String) a.getAttribute(HTML.Attribute.HREF));
            }
            else if (t == Tag.AREA) {
                add((String) a.getAttribute(HTML.Attribute.HREF));
            }
            else if (t == Tag.OBJECT) {
                add((String) a.getAttribute(HTML.Attribute.CODEBASE));
                add((String) a.getAttribute(HTML.Attribute.CLASSID));
                add((String) a.getAttribute(HTML.Attribute.DATA));
                add((String) a.getAttribute(HTML.Attribute.ARCHIVE));
                add((String) a.getAttribute(HTML.Attribute.USEMAP));
            }
            else if (t == Tag.APPLET) {
                add((String) a.getAttribute(HTML.Attribute.CODEBASE));
            }
            else if (t == Tag.FORM) {
                add((String) a.getAttribute(HTML.Attribute.ACTION));
            }
            else if (t == Tag.INPUT) {
                add((String) a.getAttribute(HTML.Attribute.SRC));
                add((String) a.getAttribute(HTML.Attribute.USEMAP));
            }
            else if (t == Tag.FRAME) {
                add((String) a.getAttribute(HTML.Attribute.SRC));
            }
            else if (t == Tag.BASE) {
                add((String) a.getAttribute(HTML.Attribute.HREF));
            }
            else if (t == Tag.TITLE) {
                characters = new StringBuffer();
            }
        }

        public void handleEndTag(Tag t, int pos) {
            if (t == Tag.TITLE) {
                titleColumn.setExtend(
                    currentNode,
                    characters.toString());
                characters = null;
            }
        }

        public void handleText(char[] text, int pos) {
            if (characters != null) {
                characters.append(text);
            }
        }

        public void handleSimpleTag(
            Tag t,
            MutableAttributeSet a,
            int pos) {
            handleStartTag(t, a, pos);
        }
    }

    /**
     * Constructor for HTMLGraphReader.
     * @param name the URL to load.
     * @param graph the Graph to read.
     */
    public HTMLGraphReader(String name, Graph graph) {
        super(null, name, graph.getEdgeTable());
        this.graph = graph;
        File f = new File(name);
        try {
            if (f.isDirectory() && f.exists()) {
                base = f.toURL();
            }
            else {
                base = new URL(name);
            }
        }
        catch (MalformedURLException e) {
            base = null;
        }
        context = base;
        queue = new LinkedList();
        loaded = new HashMap();
        currentNode = Graph.NIL;
        nameColumn =
            StringColumn.findColumn(
                graph.getVertexTable(),
                COLUMN_NAME);
        urlColumn =
            StringColumn.findColumn(graph.getVertexTable(), COLUMN_URL);
        lengthColumn =
            IntColumn.findColumn(graph.getVertexTable(), COLUMN_LENGTH);
        dateColumn =
            LongColumn.findColumn(graph.getVertexTable(), COLUMN_DATE);
        mimeColumn =
            StringColumn.findColumn(
                graph.getVertexTable(),
                COLUMN_MIME_TYPE);
        titleColumn =
            StringColumn.findColumn(
                graph.getVertexTable(),
                COLUMN_TITLE);
        refColumn =
            IntColumn.findColumn(graph.getEdgeTable(), COLUMN_REF);
    }

    /**
     * Test whether a specified URL should be considered for loading.
     *
     * @param url the url.
     *
     * @return <code>true</code> if a specified URL should be
     * considered for loading.
     */
    public boolean considerURL(URL url) {
        return url.getHost().equalsIgnoreCase(base.getHost())
            && url.getPath().startsWith(base.getPath());
    }

    /**
     * Adds a specified url to the queue of URL to load.
     *
     * @param url the url.
     *
     * @return <code>true</code> if the URL has been queued.
     */
    public boolean add(String url) {
        if (url == null || ! isAddingURL())
            return false;
        try {
            url = URLDecoder.decode(url);
            int query = Math.max(url.indexOf("?"), url.indexOf("#"));
            String strippedUrl;
            if (query == -1)
                strippedUrl = url;
            else
                strippedUrl = url.substring(0, query);

            return add(new URL(context, strippedUrl));
            //        } catch (UnsupportedEncodingException e) {
            //            return false;
        }
        catch (MalformedURLException e) {
            logger.warn("Invalid URL found in HTMLGraphReader.add("+url+")", e);
            return false;
        }
    }

    /**
     * Normalize a specified URL.
     *
     * @param url the URL.
     *
     * @return the normalized URL.
     */
    public URL normalize(URL url) {
        int i = 0;
        String f = url.getFile();
        if ((i = f.indexOf("//", i)) != -1) {
            while ((i = f.indexOf("//", i)) != -1) {
                f = f.substring(0, i + 1) + f.substring(i + 2);
            }
            try {
                url = new URL(url, f);
            }
            catch (MalformedURLException e) {
                ;//ignore
            }
        }
        return url;
    }

    /**
     * Adds a specified url to the queue of URL to load.
     *
     * @param url the url.
     *
     * @return <code>true</code> if the URL has been queued.
     */
    public boolean add(URL url) {
        if (url == null || ! isAddingURL())
            return false;
        url = normalize(url);

        if (!considerURL(url))
            return false;

        String urlString = url.toString();
        Integer i = (Integer) loaded.get(urlString);

        if (i == null) {
            int node = graph.addVertex();
            urlColumn.setExtend(node, urlString);
            int lastSlash = urlString.lastIndexOf('/');
            if (lastSlash != -1)
                nameColumn.setExtend(
                    node,
                    urlString.substring(lastSlash + 1));
            else
                nameColumn.setExtend(node, url.getFile());
            lengthColumn.setExtend(node, 0);
            loaded.put(urlString, new Integer(node));
            if (log != null) {
                log.println(
                    "adding '"
                        + urlString
                        + "' "
                        + queue.size()
                        + " "
                        + loaded.size());
            }
            queue.addLast(urlString);
            if (currentNode != Graph.NIL) {
                int edge = graph.addEdge(currentNode, node);
                refColumn.setExtend(edge, 1);
            }
            return true;
        }
        if (currentNode != Graph.NIL) {
            int edge = graph.getEdge(currentNode, i.intValue());
            if (edge == Graph.NIL) {
                edge = graph.addEdge(currentNode, i.intValue());
                refColumn.setExtend(edge, 1);
            }
            else {
                refColumn.set(edge, refColumn.get(edge) + 1);
            }
        }
        return false;
    }
    
    public boolean remove(String urlString) {
        for (Iterator iter = queue.iterator(); iter.hasNext(); ) {
            Object o = iter.next();
            if (o.equals(urlString)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }
    
    URL tryDirectory(File dir, String index)
        throws MalformedURLException {
        File file = new File(dir, index);
        if (file.exists()) {
            return new URL("file", "", file.getPath());
        }
        return null;
    }

    URLConnection openConnection(URL url) throws IOException {
        if (realRoot == null) {
            return context.openConnection();
        }
        if (!url.getFile().startsWith(base.getFile())) {
            return null;
        }

        String newFile =
            url.getFile().substring(base.getFile().length());
        URL newUrl = new URL(realRoot, newFile);
        if (newUrl.getProtocol().equalsIgnoreCase("file")) {
            File file = new File(newUrl.getPath());
            if (file.isDirectory()) {
                String[] indexes =
                    { "index.html", "index.htm", "index.shtml" };
                newUrl = null;
                for (int i = 0; i < indexes.length; i++) {
                    newUrl = tryDirectory(file, indexes[i]);
                    if (newUrl != null)
                        break;
                }
                if (newUrl == null)
                    return null;
            }
        }
        return newUrl.openConnection();
    }

    public int loadOne(String urlString)
        throws FileNotFoundException, IOException {
        File f = new File(urlString);
        if (f.exists()) {
            context = f.toURL();
        }
        else {
            context = new URL(urlString);
        }
        remove(urlString);
        currentNode = ((Integer) loaded.get(urlString)).intValue();
        URLConnection conn = openConnection(context);
        if (conn == null)
            return Graph.NIL;

        int size = conn.getContentLength();
        if (size >= 0) {
            lengthColumn.setExtend(currentNode, size);
        }

        long date = conn.getDate();
        if (date > 0) {
            dateColumn.setExtend(currentNode, date);
        }

        String contentType = conn.getContentType();
        if (contentType == null) {
            contentType =
                URLConnection.guessContentTypeFromName(
                    context.getFile());
        }
        if (contentType != null) {
            mimeColumn.setExtend(currentNode, contentType);
        }

        if (contentType != null
            && contentType.startsWith("text/html")) {
            InputStream in = conn.getInputStream();
            setIn(in);
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(in));
            parser.parse(reader, callback, true);
            reader.close();
        }
        return currentNode;
    }

    /**
     * @see infovis.io.AbstractReader#load()
     */
    public boolean load() {
        while (!queue.isEmpty()) {
            try {
                String urlString = (String) queue.getFirst();
                loadOne(urlString);
            }
            catch (FileNotFoundException e) {
                logger.warn("File not found in HTMLGraphReader", e);
            }
            catch (IOException e) {
                logger.warn("IO Exceptio in HTMLGraphReader", e);
            }
            catch (Exception e) {
                logger.error("Unexpected exception in HTMLGraphReader", e);
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a column of URLs.
     *
     * @param obj an ObjectColumn which will contain the urls.
     * If <code>null</code>, a new one will be created with "URL" as name.
     */
    public ObjectColumn createUrlColumn(ObjectColumn obj) {
        if (obj == null) {
            obj = new ObjectColumn("URL");
        }

        obj.ensureCapacity(graph.getVerticesCount());

        for (Iterator i = loaded.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            URL url = (URL) entry.getKey();
            Integer val = (Integer) entry.getValue();
            obj.set(val.intValue(), url);
        }
        return obj;
    }

    /**
     * Returns the URL map.
     *
     * @return the URL map.
     */
    public Map getUrlMap() {
        return loaded;
    }

    /**
     * Returns the log.
     * @return PrintStream
     */
    public PrintStream getLog() {
        return log;
    }

    /**
     * Sets the log.
     * @param log The log to set
     */
    public void setLog(PrintStream log) {
        this.log = log;
    }

    /**
     * DOCUMENT ME!
     *
     * @param in DOCUMENT ME!
     * @param path DOCUMENT ME!
     */
    public void annotateWithLog(BufferedReader in, String path) {
        WebLogParser logParser = new WebLogParser();
        WebLogParser.Entry entry = new WebLogParser.Entry();
        IntColumn hitCount =
            IntColumn.findColumn(graph.getVertexTable(), "hits");

        try {
            while ((entry = logParser.readEntry(in, entry)) != null) {
                URL url = new URL(base, entry.httpArg);

                Integer i = (Integer) loaded.get(url);
                if (i != null) {
                    int vertex = i.intValue();
                    if (hitCount.isValueUndefined(vertex)) {
                        hitCount.setExtend(vertex, 0);
                    }
                    else {
                        hitCount.set(vertex, hitCount.get(vertex) + 1);
                    }
                }
            }
        }
        catch (IOException e) {
            logger.error("While reading log", e);
        }
    }

    /**
     * Returns the realRoot.
     * @return URL
     */
    public URL getRealRoot() {
        return realRoot;
    }

    /**
     * Sets the realRoot.
     * @param realRoot The realRoot to set
     */
    public void setRealRoot(URL realRoot) {
        this.realRoot = realRoot;
    }
    
    /**
     * Returns the list of URL strings on the queue
     * @return the list of URL strings on the queue
     */
    public LinkedList getQueue() {
        return queue;
    }

    /**
     * Sets the list of URLs to load, as strings
     * @param list the list of URLs to load, as strings
     */
    public void setQueue(LinkedList list) {
        queue = list;
    }

    public boolean isAddingURL() {
        return addingURL;
    }

    public void setAddingURL(boolean b) {
        addingURL = b;
    }
    
    public URL getBase() {
        return base;
    }
}
