package ucsd.rmkelley.Util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
//import java.awt.Graphics2D;

import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.JDialog;

/**
 * Provides special behavior like non-modal blocking and 
 * also an option to disable input using the glass pane. This a somewhat
 * kludgy extension to the JDialog class, it's best if you don't try to make
 * it modal. If you want a modal blocking class, just use JDialog.
 */
public class RyanDialog extends JDialog{
  public RyanDialog(){
    super();
    setGlassPane(new BusyGlassPanel());
  }

  public void enableInput(){
    getGlassPane().setVisible(false);
  }

  public void disableInput(){
    getGlassPane().setVisible(true);
  }
  
  public void show(){
    super.show();
    synchronized(this){
      try{
	this.wait();
      }catch(Exception e){
	e.printStackTrace();
      }
    }
  }

  public void hide(){
    super.hide();
    synchronized(this){
      notify();
    }
  }
  
  public void dispose(){
    super.dispose();
    synchronized(this){
      notify();
    }
  }
  

}

class BusyGlassPanel
    extends JPanel
{
  public BusyGlassPanel() {
    super.setOpaque(false);
    super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    //Suck up them events!!!
    super.addKeyListener( (new KeyAdapter() { }) );
    super.addMouseListener( (new MouseAdapter() { }) );
    super.addMouseMotionListener( (new MouseMotionAdapter() { }) );
  }

  public static final Color COLOR_WASH = new Color(64, 64, 64, 32);

  public final void paintComponent(Graphics p_graphics) {
    Dimension l_size = super.getSize();

    // Wash the pane with translucent gray.
    p_graphics.setColor(COLOR_WASH);
    p_graphics.fillRect(0, 0, l_size.width, l_size.height);

    // Paint a grid of white/black dots. 
    
    /*
    p_graphics.setColor(Color.white);
    for (int j=3; j<l_size.height; j+=8) {
      for (int i=3; i<l_size.width; i+=8) {
	p_graphics.fillRect(i,j,1,1);
      }
    }
    p_graphics.setColor(Color.black);
    for (int j=4; j<l_size.height; j+=8) {
      for (int i=4; i<l_size.width; i+=8) {
	p_graphics.fillRect(i,j,1,1);
      }
    }
    */
    
  }
}
