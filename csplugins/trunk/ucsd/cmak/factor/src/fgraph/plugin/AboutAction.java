package fgraph.plugin;
 
import cytoscape.Cytoscape;

import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutAction implements ActionListener
{
    private static final String ABOUT =
        "<html><body>This plugin implements the model inference algorithm described in <br>Yeang, et. al. <i>\"Automated pathway discovery and refinement on a scaffold of molecular interactions\"</i> 2005</body></html>";
    
    /**
     * Invoked when the about action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                      ABOUT,
                                      "About",
                                      JOptionPane.INFORMATION_MESSAGE);

        /*
        AboutDialog dialog = new AboutDialog(Cytoscape.getDesktop());
        dialog.pack();
        dialog.setVisible(true);
        */
    }
}
