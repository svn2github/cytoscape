import javax.swing.JOptionPane;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

public class HelloWorld extends CytoscapePlugin {

    public HelloWorld() {
        String message = "Hello World!";
        System.out.println(message);
        JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);
    }
}

