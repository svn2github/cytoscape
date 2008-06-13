package SawdVisualizer;

import javax.swing.*;                                                                                                                          
import javax.swing.event.*;
import java.awt.*;                                                                                                                             
import java.awt.event.*;                                                                                                                       

public class TitleWindow extends JWindow
{
	public TitleWindow(final ActionListener action_listener)
        {                                                                                                                                      
		ImageIcon title_image = new ImageIcon(getClass().getResource("/title.png"));                                                   
		JLabel title_label = new JLabel(title_image);                                                                                  
		JLabel click_label = new JLabel("Click to continue...");
		title_label.addMouseListener(new MouseInputAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				TitleWindow.this.dispose();
				if (action_listener != null)
					action_listener.actionPerformed(new ActionEvent(TitleWindow.this, ActionEvent.ACTION_PERFORMED, null));
			}
		});
		getContentPane().add(title_label);
		//getContentPane().add(click_label);
		pack();
		center();
		setAlwaysOnTop(true);
        }

	private void center()
	{
		Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
		GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment()
		                                                         .getDefaultScreenDevice()
		                                                         .getDefaultConfiguration();
		Insets screen_insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration);

		screen_size.width -= screen_insets.left;
		screen_size.width -= screen_insets.right;
		screen_size.height -= screen_insets.top;
		screen_size.height -= screen_insets.bottom;

		Dimension frame_size = getSize();
		setLocation(((screen_size.width / 2) - (frame_size.width / 2)) + screen_insets.left,
		           ((screen_size.height / 2) - (frame_size.height / 2)) + screen_insets.top);
	}
}
