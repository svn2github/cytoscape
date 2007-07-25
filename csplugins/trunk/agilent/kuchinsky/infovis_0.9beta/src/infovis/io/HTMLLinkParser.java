/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Class HTMLLinkParser
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class HTMLLinkParser {

    protected ParserDelegator parser = new ParserDelegator();
    protected Callback callback = new Callback();
    protected StringBuffer characters;
    protected ArrayList links;
    
    public HTMLLinkParser() {
    }
        
    public void add(String url) {
        if (links == null)
            links = new ArrayList();
        links.add(url);
    }

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
    
    public ArrayList parseHTMLLinks(BufferedReader reader) throws IOException {
        links = null;
        parser.parse(reader, callback, true);
        return links;
    }

}
