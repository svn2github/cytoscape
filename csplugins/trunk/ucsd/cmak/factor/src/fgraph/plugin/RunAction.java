package fgraph.plugin;
 
import cytoscape.Cytoscape;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RunAction implements ActionListener
{
    RunDialog dialog = null;

    /**
     * Invoked when the run action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        if(dialog == null)
        {
            dialog = new RunDialog(Cytoscape.getDesktop());
            dialog.pack();
        }
        
        dialog.setVisible(true);        
        //dialog.show();
    }
}
