package csplugins.isb.pshannon.py;


import org.python.core.*;


/**
 * SPyConsole Application
 * Developed by Tom Maxwell, maxwell@cbl.umces.edu
 * University of Maryland Institute for Ecological Economics
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @author Tom Maxwell <maxwell@cbl.umces.edu>
 * @version 1.0
 */
public class OutputBuffer extends StdoutWrapper {

	SPyConsole _console;
	String _stylename;

	public OutputBuffer( SPyConsole console, String stylename) {
		_console = console;
		_stylename = stylename;
	}

	public void flush() { ; }

	public void write( String text) {
		_console.write(text, _stylename);
	}
}
