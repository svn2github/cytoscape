/*
 * TRTextPanel.java
 *
 * Created on April 4, 2007, 4:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package clusterMaker.algorithms.autosome.view.view2d;

/**
 *
 * @author a_newman
 */
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.*;

import java.awt.*;
/**
 *
 * @author a_newman
 */
public class Text_JPanel extends javax.swing.JPanel{
    
    private pic_Drawer pic;
    
    /** Creates a new instance of BlockPanel */
    public Text_JPanel(pic_Drawer pic) {
        this.pic = pic;
        this.setPreferredSize(new Dimension(this.pic.rendImage.getWidth(), this.pic.rendImage.getHeight()));
        //this.setDoubleBuffered(true);
        repaint();
    }
    
    public void paintComponent( Graphics g )
    {
       g.drawImage(pic.rendImage, 0, 0, this); 
    }
    
    public void update(pic_Drawer pic) {
        this.pic = pic;
        this.setPreferredSize(new Dimension(pic.rendImage.getWidth(), pic.rendImage.getHeight()));
        repaint();
    }
    
    public void setText(){
        
    }

 
}
