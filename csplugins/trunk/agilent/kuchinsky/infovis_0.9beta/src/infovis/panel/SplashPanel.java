/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JEditorPane;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class SplashPanel extends JEditorPane {
    /**
     * Constructor with a specified size.
     * @param w width
     * @param h height
     */
    public SplashPanel(int w, int h) {
        this();
        setPreferredSize(new Dimension(w, h));
    }
    
    /**
     * Constructor.
     *
     */
    public SplashPanel() {
        try {
            setPage("file:spplash.html");
        }
        catch (IOException e) {
            setText(
                "<html><head><title>The InfoVis Toolkit</title></head>\n"+
                "<text>"+
                "<p>Welcome to the <b>InfoVis Toolkit</b> version 0.9beta<br>\n"+
                "Copyright (C) 2003,2004,2005 Jean-Daniel Fekete and INRIA, France.</p>\n"+
                "<p>This software is published under the terms of the X11 Software License\n"+
                "a copy of which has been included with this distribution in the\n"+
                "license-infovis.txt file.</p>\n"+ 
                "</text></html>\n");
        }
        setEditable(false);
    }

}
