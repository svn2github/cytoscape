package org.jmathplot.gui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jmathplot.gui.components.*;
import org.jmathplot.io.*;

/**
 * <p>Copyright : BSD License</p>
 * @author Yann RICHET
 * @version 1.0
 */

public abstract class DataPanel
	extends JPanel
	implements ComponentListener, FilePrintable, ClipBoardPrintable, StringPrintable {

	protected DataToolBar toolBar;
	protected JScrollPane scrollPane;

	public static int[] dimension = new int[] {
					400, 400};

	public DataPanel() {
		setLayout(new BorderLayout());
		initToolBar();
		init();
	}

	protected void initToolBar() {
		toolBar = new DataToolBar(this);
		add(toolBar, BorderLayout.NORTH);
		toolBar.setFloatable(false);
	}

	protected void init() {
		setSize(dimension[0], dimension[1]);
		setPreferredSize(new Dimension(dimension[0], dimension[1]));

		addComponentListener(this);
	}

	public void update() {
		this.remove(scrollPane);
		toWindow();
		this.updateUI();
	}

	protected abstract void toWindow();

	public abstract void toClipBoard();

	public abstract void toASCIIFile(File file);

	public void componentHidden(ComponentEvent e) {}

	public void componentMoved(ComponentEvent e) {}

	public void componentResized(ComponentEvent e) {
		dimension = new int[] {
			    (int) (this.getSize().getWidth()), (int) (this.getSize().getHeight())};
	}

	public void componentShown(ComponentEvent e) {}

}