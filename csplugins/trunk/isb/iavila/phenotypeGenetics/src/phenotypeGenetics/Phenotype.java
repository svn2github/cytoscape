/**  Copyright (c) 2005 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
/**
 * Some observable attribute of an organism, usually the consequence of
 * a genetic or environmental manipulation in an experiment.
 */
package phenotypeGenetics;
import java.io.*;
import java.util.*;

public class Phenotype {

  String name, value;
  
  // A suffix for phenotype names, denoting variability/error  
  final static public String deviationString = " error";

  //-----------------------------------------------------------------------------
  public Phenotype (){}
  //-----------------------------------------------------------------------------
  public Phenotype (String name, String value)
  {
    this.name = name;
    this.value = value;
  } // ctor
  //-----------------------------------------------------------------------------
  public boolean isEmpty ()
  {
    return(name == null && value == null);
  }
  //-----------------------------------------------------------------------------
  public void setName (String newValue)
  {
    name = newValue;
  }
  //-----------------------------------------------------------------------------
  public String getName ()
  {
    return name;
  }
  //-----------------------------------------------------------------------------
  public void setValue (String newValue)
  {
    value = newValue;
  }
  //-----------------------------------------------------------------------------
  public String getValue ()
  {
    return value;
  }
  //-----------------------------------------------------------------------------
  public String toString ()
  {
    StringBuffer sb = new StringBuffer ();
    sb.append (name);
    sb.append (" = ");
    sb.append (value);

    return sb.toString ();

  } // toString
  //-----------------------------------------------------------------------------
} // Phenotype
