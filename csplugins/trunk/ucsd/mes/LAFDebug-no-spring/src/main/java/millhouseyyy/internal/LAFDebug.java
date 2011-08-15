package millhouseyyy.internal;

import javax.swing.*;
import java.awt.*;

public class LAFDebug {

	public LAFDebug() {
		JFrame frame = new JFrame();

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(100,100));

		JButton button = new JButton("NO spring");
		panel.add(button);

		Object[] names = new Object[]{"homer","marge","lisa"};
		JComboBox combo = new JComboBox(names);
		panel.add(combo);

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
