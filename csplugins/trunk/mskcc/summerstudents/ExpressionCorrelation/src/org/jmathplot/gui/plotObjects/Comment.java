package org.jmathplot.gui.plotObjects;

import java.awt.*;

public class Comment extends Label implements Noteable {

	public Comment(String t,Coord c) {
		super(t,c);
	}

	public boolean tryNote(int[] screenCoord,Graphics comp) {
		if ((coord.getScreenCoordCopy()[0]==screenCoord[0])&&(coord.getScreenCoordCopy()[1]==screenCoord[1])) {
			note(comp);
			return true;
		}
		return false;
	}

	public boolean tryNote(int[] screenCoord) {
		if ((coord.getScreenCoordCopy()[0]==screenCoord[0])&&(coord.getScreenCoordCopy()[1]==screenCoord[1]))
			return true;
		return false;
	}

	public void note(Graphics comp){
		super.plot(comp);
	}
}
