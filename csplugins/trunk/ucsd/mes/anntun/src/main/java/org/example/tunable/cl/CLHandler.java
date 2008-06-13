
package org.example.tunable.cl;

import java.lang.reflect.*;
import javax.swing.JPanel;
import org.apache.commons.cli.*;
import org.example.tunable.*;

public interface CLHandler {
	public Option getOption();
	public void handleLine( CommandLine line );
}
