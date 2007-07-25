/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example;
import javax.swing.JFrame;

import infovis.panel.MainFrameDecorator;
//import agile2d.AgileJFrame;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class AGLMain  {

    public static void main(String args[]) {
    //AgileJFrame frame = new AgileJFrame("OpenGL Infovis Toolkit");
    JFrame frame = new JFrame("OpenGL Infovis Toolkit");        
    
    new MainFrameDecorator(frame);
    frame.setVisible(true);
    frame.pack();
    }

}
