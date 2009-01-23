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

	private static JFrame frame;

    private static void createAndShowGUI() {
        frame = new JFrame("Tunable Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		JPanel p = new JPanel();
		JButton b = new JButton("Process Command");
		b.addActionListener( new MyActionListener() ); 
		p.add(b);
        frame.setContentPane(p);
        frame.pack();
        frame.setVisible(true);
    }

	// the command comes from wherever
	// it's static here to illustrate that each time we run the command
	// the changes we make the various fields persist in the object
	private static Command com = new JActiveModules();

	private static class MyActionListener implements ActionListener {
			TunableInterceptor ti; 
			public MyActionListener() {
				// create an interceptor for this context
				// in this cases it's a GUI, so it needs a parent Component
				// to render the dialog
				ti = new GuiTunableInterceptor(frame);
			}
			
			public void actionPerformed(ActionEvent ae) {

				// intercept the command and modify any tunable fields
				ti.intercept(com);

				// execute the command
				com.execute();
			}
		
	}
}
