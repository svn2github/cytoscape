
package org.example.tunable.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.example.tunable.*;

public class IntCLFactory implements CLFactory {
	public CLHandler getHandler(Field f, Object o, Tunable t) {
		return new IntCLHandler(f, o, t);
	}
}
