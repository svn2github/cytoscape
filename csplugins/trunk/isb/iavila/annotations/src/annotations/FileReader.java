/**  Copyright (c) 2003 Institute for Systems Biology
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
 * Reads a file that contains a description of groups of genes that
 * can be scored for overrepresented annotations.
 *
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.org>
 */
package annotations;
import java.util.*;
import java.io.*;
public class FileReader {

  /**
   * Reads a file that contains tab-separated groups of genes to score
   * per line.
   * 
   * @param file_name the fully qualified name of the file to read
   * @return a 2D array of Strings with the contents of the file in the
   * same order, or null, if something went wrong
   */
  public static String [][] readFile (String file_name){
    try{
      java.io.FileReader fileReader = new java.io.FileReader(file_name);
      LineNumberReader lineReader = new LineNumberReader(fileReader);
      String line = lineReader.readLine();
      ArrayList allRows = new ArrayList();
      while(line != null){
        line.trim();
        String [] row = line.split("\\s");
        if(row.length > 0){
          allRows.add(row);
          System.err.println("row length = " + row.length);
          for(int i = 0; i < row.length; i++){
            System.err.print("["+row[i]+"]");
          }
          System.err.println();
        }
        line = lineReader.readLine();
      }//while
      String [][] rows = new String [allRows.size()][];
      for(int i = 0; i < allRows.size(); i++){
        String [] aRow = (String[])allRows.get(i);
        rows[i] = aRow;
      }//for i
      
      return rows;
      
    }catch(IOException e){
      e.printStackTrace();
      return null;
    }//catch
  }//readFile
  
}//FileReader
