package HelloWorld;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;


/**
 * A menu item "Hello World" will appear at Plugins menu. Click on the menu
 * item, a message dialog will show up.
 */
public class HelloWorldPlugin extends CytoscapePlugin {

        public HelloWorldPlugin() {
                MyPluginMenuAction menuAction = new MyPluginMenuAction(this);
                Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);
        }
        
        public class MyPluginMenuAction extends CytoscapeAction {

                public MyPluginMenuAction(HelloWorldPlugin myPlugin) {
                        super("Hello World");
                        setPreferredMenu("Plugins");
                }

                public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Hello World is selected!");       
                }
        }
}
