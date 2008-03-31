/**
 * Chart2D, a java library for drawing two dimensional charts.
 * Copyright (C) 2001 Jason J. Simas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author of this library may be contacted at:
 * E-mail:  jjsimas@users.sourceforge.net
 * Street Address:  J J Simas, 887 Tico Road, Ojai, CA 93023-3555 USA
 */


package net.sourceforge.chart2d;


import java.awt.Font;
import java.util.Vector;
import java.awt.GraphicsEnvironment;


/**
 * A data structure for holding the properties common to all Properties objects.
 * Currently it only holds the font names vector.
 */
abstract class Properties {


  private static String[] fontNames;
  private static boolean gotFontNames = false;


  /**
   * Returns true if the font name exists in the graphics enviornment.
   * @param name The name of the font to determine the existence of.
   * @return boolean If true, then the font name exists.
   */
  public synchronized final boolean isFontNameExists (String name) {

    if (!gotFontNames) getFontNames();
    for (int i = 0; i < fontNames.length; ++i) if (name.equals (fontNames[i])) return true;
    return false;
  }


  private void getFontNames() {

    fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    gotFontNames = true;
  }
}