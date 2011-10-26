/*
 * CloseResultPanelListener.java
 *
 * Created on August 3, 2006, 3:51 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package BiNGO.GOlorize;


/**
 *
 * @author ogarcia
 */
import java.awt.event.*;
public class CloseResultPanelListener implements ActionListener{
    ResultPanel result;
    /** Creates a new instance of CloseResultPanelListener */
    public CloseResultPanelListener(ResultPanel result) {
        this.result=result;
       
    }
    public void actionPerformed(ActionEvent e){
        
        result.goBin.removeTab(result);
        
    }
    
}
