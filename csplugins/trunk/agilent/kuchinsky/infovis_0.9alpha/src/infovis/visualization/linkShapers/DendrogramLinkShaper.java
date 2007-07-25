/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.linkShapers;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import infovis.visualization.Orientable;

/**
 * Class DendogramLinkShaper
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 *  @infovis.factory  LinkShaperFactory "Dendrogram"
 */
public class DendrogramLinkShaper extends DefaultLinkShaper {

    public DendrogramLinkShaper() {
    }
    
    public String getName() {
        return "Dendrogram";
    }
    
    public Shape createLink(
            Point2D startPos,
            int startOrientation,
            Point2D endPos,
            int endOrientation,
            Shape prevShape) {
        GeneralPath path;
        if (prevShape instanceof GeneralPath) {
            path = (GeneralPath) prevShape;
            path.reset();
        }
        else {
            path = new GeneralPath();
        }
        path.moveTo((float)startPos.getX(), (float)startPos.getY());
        switch(startOrientation) {
        case Orientable.ORIENTATION_NORTH:
        case Orientable.ORIENTATION_SOUTH:
//            path.lineTo(
//                    (float)startPos.getX(), 
//                    (float)(startPos.getY()+endPos.getY())/2);
            path.lineTo(
                (float)endPos.getX(), 
                (float)startPos.getY());
                //(float)(startPos.getY()+endPos.getY())/2);
            break;
        default:
//            path.lineTo(
//                    (float)(startPos.getX()+endPos.getX())/2, 
//                    (float)startPos.getY());
            path.lineTo(
                //(float)(startPos.getX()+endPos.getX())/2,
                (float)startPos.getX(),
                (float)endPos.getY());
            break;
        
        }
        path.lineTo((float)endPos.getX(), (float)endPos.getY());
        return path;
    }

}
