/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: ColorSetI.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/12/21 03:28:14 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular,
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER
 */
package edu.stanford.genetics.treeview;

import java.awt.*;

/**
 *  This is a general interface to color sets. This should be subclassed for particular
 *  color sets. Other classes, such as presets, which want to configure or get colors
 *  can do it through this interface in a general way. They can also down-cast if
 *  they need to. In general, the types should be constants within the subclass.
 *  See an existing example to get the idea.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version    @version $Revision: 1.4 $ $Date: 2004/12/21 03:28:14 $
 */
public interface ColorSetI {
	/**
	 *  textual descriptions of the types of colors in this set
	 */
	public abstract String[] getTypes();


	/**
	 *  get the color corresponding to the i'th type.
	 *
	 * @param  i  type index
	 * @return    The actual color
	 */
	public abstract Color getColor(int i);


	/**
	 *  set the i'th color to something new.
	 *
	 * @param  i   index of the type
	 * @param  newColor   The new color
	 */
	public abstract void setColor(int i, Color newColor);


	/**
	 *  get the description of the i'th type
	 *
	 * @param  i   index of the type
	 * @return    The description of the type
	 */
	public abstract String getType(int i);


	/**
	 *  get the name of the color set
	 *
	 * @return    Usually the name of the subclass.
	 */
	public String getName();
}

