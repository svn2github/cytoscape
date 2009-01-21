
package org.example.tunable.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import org.example.tunable.*;

public abstract class AbstractGuiHandler extends AbstractHandler implements GuiHandler, ActionListener {

	public AbstractGuiHandler(Field f, Object o, Tunable t) {
		super(f,o,t);	
	}

	public void actionPerformed(ActionEvent ae) {
		handle();
	}

	public abstract void handle();

	public abstract JPanel getJPanel();
}
