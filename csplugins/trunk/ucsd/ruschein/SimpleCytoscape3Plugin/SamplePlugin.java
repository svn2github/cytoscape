import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.plugin.CyPlugin;


public class SamplePlugin extends CyPlugin {
	public SamplePlugin(CyPluginAdapter adapter){
		super(adapter);
		adapter.getCySwingApplication().addAction(new MenuAction(adapter));
	}
}
