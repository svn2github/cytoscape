package GenericPlugin;
import java.util.HashMap;

class PluginUtilThread extends Thread{
    
    public PluginUtilThread(String actionName,HashMap args,PluginUtil plugin){
	plugin.run(actionName,args);
    }
}
