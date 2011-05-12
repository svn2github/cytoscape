import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.plugin.CyPlugin;


public class HideSingletonNodesPlugin extends CyPlugin {
	public HideSingletonNodesPlugin(CyPluginAdapter adapter){
		super(adapter);
		adapter.getCySwingApplication().addAction(new MenuAction(adapter));
	}
}
