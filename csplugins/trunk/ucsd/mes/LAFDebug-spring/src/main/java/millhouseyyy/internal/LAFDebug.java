package millhouseyyy.internal;

import javax.swing.*;
import java.awt.*;

public class LAFDebug {

	public LAFDebug() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(100,100));
		JButton button = new JButton("Using Spring-DM");
		panel.add(button);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
