package com.agilent.AdaptiveJavaHelp.example;

import java.net.URL;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.help.*;

import com.agilent.AdaptiveJavaHelp.DynamicTextMonitors;
import com.agilent.AdaptiveJavaHelp.DynamicText;

public class ExampleMain
{
    static final String PATH_TO_HELPSET = "/example-helpset/example.hs";
    static boolean hasButtonBeenClicked = false;
    static JTextField field = null;

    private static void startGUI()
    {
	JFrame frame = new JFrame("AdaptiveJavaHelp Example");
	JButton showHelpButton = new JButton("Show JavaHelp");
	JButton button = new JButton("This is a button");
	field = new JTextField("This is a text field");
	button.addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent e)
		{
		    hasButtonBeenClicked = true;
		}
	    });

	frame.setLayout(new FlowLayout());
	frame.add(showHelpButton);
	frame.add(new JSeparator());
	frame.add(button);
	frame.add(field);
	frame.pack();
	frame.setVisible(true);

	try
	    {
		URL hsURL = ExampleMain.class.getResource(PATH_TO_HELPSET);
		HelpSet hs = new HelpSet(null, hsURL);
		HelpBroker hb = hs.createHelpBroker();
		showHelpButton.addActionListener(new CSH.DisplayHelpFromSource(hb));
	    }
	catch (Exception e)
	    {
		System.out.println("Failed to load help set");
		e.printStackTrace();
	    }

	DynamicTextMonitors.start();
	DynamicTextMonitors.register("\\%whats-in-the-text-field\\%", new DynamicText()
	    {
		public String getText()
		{
		    return field.getText();
		}
	    });
    }

    public static void main(String[] args)
    {
	SwingUtilities.invokeLater(new Runnable()
	    {
		public void run()
		{
		    startGUI();
		}
	    });
    }

    public static boolean hasButtonBeenClicked()
    {
	return hasButtonBeenClicked;
    }

    public static String getText()
    {
	return field.getText();
    }
}
