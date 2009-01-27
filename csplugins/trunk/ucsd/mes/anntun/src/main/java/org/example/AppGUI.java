package org.example;

import org.example.tunable.*;
import org.example.tunable.internal.gui.*;
import org.example.command.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AppGUI
{

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
		JFrame frame = new JFrame("Tunable Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		TunableInterceptor ti = new GuiTunableInterceptor(frame);
		JPanel p = new JPanel();
		p.add( new JButton(new MyAction("Print Something", new PrintSomething(), ti)));
		p.add( new JButton(new MyAction("JActiveModules", new JActiveModules(), ti)));
		p.add( new JButton(new MyAction("SearchActive", new SearchActive(), ti)));
        frame.setContentPane(p);
        frame.pack();
        frame.setVisible(true);
    }

	private static class MyAction extends AbstractAction {
		Command com;
		TunableInterceptor ti;
		MyAction(String title, Command com, TunableInterceptor ti) {
			super(title);
			this.com = com;
			this.ti = ti;
		}
		public void actionPerformed(ActionEvent a) {
			// intercept the command and modify any tunable fields
			ti.intercept(com);

			// execute the command
			com.execute();
		}
	}
}
