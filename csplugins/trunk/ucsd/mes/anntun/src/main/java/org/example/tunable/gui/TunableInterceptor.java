package org.example.tunable.gui;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import org.example.command.Command;
import org.example.tunable.*;

public class TunableInterceptor {

	public static void modify(Command[] commands, Component parent) {
		for ( Command d : commands ) {

			java.util.List<GuiHandler> lh = new LinkedList<GuiHandler>();

			// Find each field in the class.
			for (Field f : d.getClass().getFields()) {

				// See if the field is annotated as a Tunable.
   				if (f.isAnnotationPresent(Tunable.class)) {
					try {
						Tunable a = f.getAnnotation(Tunable.class);
						System.out.println("We're modifying Tunable:  " + f.getName() + 
						                   " : " + a.description());

						HandlerFactory h = handlers.get(f.getType());
						if ( h != null )
						 	lh.add( h.getHandler(f,d,a) );	
						else
							System.out.println("No handler for type: " + f.getType().getName());

					} catch (Throwable ex) {
						System.out.println("Modification failed: " + f.toString() );
						ex.printStackTrace();
					}
				}
			}

			popupGUI( lh, parent );
		}
	}

	private static Map<Class,HandlerFactory> handlers; 
	
	static {
		handlers = new HashMap<Class,HandlerFactory>();
		handlers.put( int.class, new IntHandlerFactory() );
		handlers.put( String.class, new StringHandlerFactory() );
	}

	private static void popupGUI(java.util.List<GuiHandler> lh, Component parent ) {
			JPanel mainPanel = new JPanel();
			for (GuiHandler gh : lh) {
				mainPanel.add(gh.getJPanel());
			}
			
		 JOptionPane.showConfirmDialog(parent, mainPanel, "Set Parameters", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE ); 

		 for ( GuiHandler h : lh )
		 	h.handle();
	}
}
