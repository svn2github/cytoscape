/*
  File: ArgType.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.equations;


import java.util.List;
import java.util.ArrayList;


/**
 *  An enum specifying a function argument type.
 */
public enum ArgType {
	//                 opt.  mult.args.                          compatible arg. types
	//--------------------------------------------------------------------------------------------------------
	/** An argument that can be converted to integer. */
	INT(              false,   false,  new Class[] { Long.class, Double.class, String.class, Boolean.class }),

	/** An argument that can be converted to a floating point value. */
	FLOAT(            false,   false,  new Class[] { Double.class, Long.class, String.class, Boolean.class }),

	/** An argument that can be converted to a string. */
	STRING(           false,   false,  new Class[] { String.class, Double.class, Long.class, Boolean.class }),

	/** An argument that can be converted to a boolean. */
	BOOL(             false,   false,  new Class[] { Boolean.class, Double.class, Long.class, String.class }),

	/** Any scalar argument. */
	ANY(              false,   false,  new Class[] { Boolean.class, Double.class, Long.class, String.class }),

	INTS(             false,   true,   new Class[] { LongList.class, Long.class, Double.class, String.class, Boolean.class }),
	FLOATS(           false,   true,   new Class[] { DoubleList.class, Double.class, Long.class, String.class, Boolean.class }),
	STRINGS(          false,   true,   new Class[] { StringList.class, String.class, Double.class, Long.class, Boolean.class }),
	BOOLS(            false,   true,   new Class[] { BooleanList.class, Boolean.class, Double.class, Long.class, String.class }),
	STRICT_INT(       false,   false,  new Class[] { Long.class }),
	STRICT_FLOAT(     false,   false,  new Class[] { Double.class }),
	STRICT_STRING(    false,   false,  new Class[] { String.class }),
	STRICT_BOOL(      false,   false,  new Class[] { Boolean.class }),
	OPT_INT(          true,    false,  new Class[] { Long.class, Double.class, String.class, Boolean.class}),
	OPT_FLOAT(        true,    false,  new Class[] { Double.class, Long.class, String.class, Boolean.class }),
	OPT_STRING(       true,    false,  new Class[] { String.class, Double.class, Long.class, Boolean.class }),
	OPT_BOOL(         true,    false,  new Class[] { Boolean.class, Double.class, Long.class, String.class }),
	OPT_INTS(         true,    true,   new Class[] { LongList.class, Long.class, Double.class, String.class, Boolean.class }),
	OPT_FLOATS(       true,    true,   new Class[] { DoubleList.class, Double.class, Long.class, String.class, Boolean.class }),
	OPT_STRINGS(      true,    true,   new Class[] { StringList.class, String.class, Double.class, Long.class, Boolean.class }),
	OPT_BOOLS(        true,    true,   new Class[] { BooleanList.class, Boolean.class, Double.class, Long.class, String.class }),
	OPT_STRICT_INT(   true,    false,  new Class[] { Long.class }),
	OPT_STRICT_FLOAT( true,    false,  new Class[] { Double.class }),

	/** An optional string argument. */
	OPT_STRICT_STRING(true,    false,  new Class[] { String.class }),

	/** An optional boolean argument. */
	OPT_STRICT_BOOL(  true,    false,  new Class[] { Boolean.class }),

	/** One or more lists with arbitrary member element types and/or one or more scalars. */
	ANY_LIST(         false,   true,  new Class[] { List.class, Double.class, Long.class, String.class, Boolean.class }),

	/** A list with arbitrary member element types. */
	STRICT_ANY_LIST(  false,   false,  new Class[] { List.class }),

	/** Zero or more lists with arbitrary member element types and/or zero or more scalars. */
	OPT_ANY_LIST(     true,    true,  new Class[] { List.class, Double.class, Long.class, String.class, Boolean.class });
	
	private boolean isOptional;
	private boolean acceptsMultipleArgs;
	private Class[] compatibleTypes;

	ArgType(final boolean isOptional, final boolean acceptsMultipleArgs, final Class[] compatibleTypes) {
		this.isOptional = isOptional;
		this.acceptsMultipleArgs = acceptsMultipleArgs;
		this.compatibleTypes = compatibleTypes;
	}

	public boolean isOptional() { return isOptional; }
	public boolean acceptsMultipleArgs() { return acceptsMultipleArgs; }
	public Class[] getCompatibleTypes() { return compatibleTypes; }
}
