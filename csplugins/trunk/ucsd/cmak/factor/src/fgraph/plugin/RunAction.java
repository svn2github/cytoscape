package fgraph.plugin;
 
import cytoscape.Cytoscape;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RunAction implements ActionListener
{
    /**
     * Invoked when the run action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        RunDialog dialog = new RunDialog(Cytoscape.getDesktop());
        dialog.pack();
        dialog.setVisible(true);        
    }
}
