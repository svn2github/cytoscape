/*
  File: ConsoleLogger.java

  Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  - University of California San Francisco

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.logger;

/**
 * The ConsoleLogger is the default log handler for Cytoscape and does nothing
 * much more than issue messages to System.out or System.err, as appropriate.
 */
public class ConsoleLogger implements CyLogHandler {
	private static ConsoleLogger logger = null;

	protected ConsoleLogger() {}

	public void handleLog(LogLevel level, String logMessage) {
		if (level == LogLevel.LOG_ERROR || level == LogLevel.LOG_FATAL)
			System.err.println(logMessage);
		else
			System.out.println(logMessage);
	}

	public static ConsoleLogger getLogger() {
		if (logger == null)
			logger = new ConsoleLogger();

		return logger;
	}
}
