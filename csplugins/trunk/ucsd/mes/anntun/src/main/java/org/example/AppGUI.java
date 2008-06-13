package org.example;

import org.example.tunable.gui.*;
import org.example.command.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AppGUI
{
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */

    private static void createAndShowGUI() {

        //Create and set up the window.
        frame = new JFrame("Tunable Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel p = new JPanel();
		JButton b = new JButton("Execute Command");
		b.addActionListener( new MyActionListener() ); 
		p.add(b);

        frame.setContentPane(p);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

	private static class MyActionListener implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				TunableInterceptor.modify(stuff, frame);
				for ( Command c : stuff ) 
					c.execute();
			}
		
	}

	private static JFrame frame;
	private static Command[] stuff = { new PrintSomething() }; // , new PrintSomethingElse() };
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
