/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import javax.swing.JTextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Log4J Appender in a JTextArea
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class JTextAreaAppender extends AppenderSkeleton {
    JTextArea jtext;

    public JTextAreaAppender(Layout layout, JTextArea jtext) {
        this.layout = layout;
        this.jtext = jtext;
    }

    protected void append(LoggingEvent event) {
        jtext.append(layout.format(event));

        if (layout.ignoresThrowable()) {
            String[] s = event.getThrowableStrRep();
            if (s != null) {
                int len = s.length;
                for (int i = 0; i < len; i++) {
                    jtext.append(s[i]);
                    jtext.append(Layout.LINE_SEP);
                }
            }
        }
    }

    public boolean requiresLayout() {
        // TODO Auto-generated method stub
        return true;
    }

    public void close() {
        if (closed) return;
        closed = true;
        if (layout != null) {
            jtext.append(layout.getFooter());
        }
    }
}