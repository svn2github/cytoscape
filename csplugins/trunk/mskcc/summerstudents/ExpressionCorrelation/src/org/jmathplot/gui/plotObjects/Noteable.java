package org.jmathplot.gui.plotObjects;

import java.awt.*;

public interface Noteable {
	public boolean tryNote(int[] screenCoord,Graphics comp);
	public boolean tryNote(int[] screenCoord);
	public void note(Graphics comp);
}