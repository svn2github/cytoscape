package org.example.tunable.internal.cl;

import org.apache.commons.cli.*;
import org.example.tunable.*;

public interface CLHandler extends Handler {
	public Option getOption();
	public void handleLine( CommandLine line );
}
