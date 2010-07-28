package org.idekerlab.PanGIAPlugin.utilities.html;

import java.io.*;

public abstract class HTMLBlock
{
	protected abstract void write(BufferedWriter bw, int depth);
}
