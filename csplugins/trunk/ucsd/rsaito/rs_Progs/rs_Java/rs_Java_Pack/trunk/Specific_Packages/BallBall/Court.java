package BallBall;

import java.applet.Applet;
import java.awt.*;

public class Court extends Applet {
	BallBall_Virtual_Space vspc;
	
	public void init(){
		vspc = new BallBall_Virtual_Space();	
	}
	
    public void paint(Graphics g) {	   	
		int i;
		try {
			for(i = 0;i < 500;i ++){
				vspc.move();
				vspc.get_ball().print_status_info();
				draw_court(g);
				Thread.sleep(10);
				/* System.out.println(vspc.get_upper_boundary()); */
			}
		}
    	catch (InterruptedException e){}
    }
    
    public int draw_court(Graphics g){
    	g.setColor(new Color(255,255,255));
    	g.fillRect(vspc.get_left_end(),
    			   vspc.get_upper_end(),
    			   vspc.get_right_end() - vspc.get_left_end() + 1,
    			   vspc.get_lower_end() - vspc.get_upper_end() + 1);
		g.setColor(new Color(1,1,1));
		g.drawArc(vspc.get_ball().get_position_x(),
				  vspc.get_ball().get_position_y(),
				  5, 5, 0, 360);	
    	return 1;
    }
    
}
