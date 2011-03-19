import java.awt.*;
import java.applet.*;

public class SimpleApplet extends Applet{

	public void init(){
		setBackground(Color.black);
		setForeground(Color.white);
		setFont(new Font("Serif", Font.BOLD, 20));
	}

	public void start(){
		repaint();
	}

	public void paint(Graphics g){
		g.drawString("Hello!", 30, 30);
	}
}
