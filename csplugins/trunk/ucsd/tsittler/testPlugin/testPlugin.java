package testPlugin;
import java.util.*;
import GenericPlugin.*;
import cytoscape.Cytoscape;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class testPlugin extends PluginUtil{
    public testPlugin(){
	initialize("testPlugin","this plugin illustrates the use of GenericPlugin");
    }

    public ArgVector getActions(){
	ArgVector actions=new ArgVector(1);
	ArgVector args=new ArgVector(5);
	args.put("name","something");
	args.put("intVal",new Integer(123));
	args.put("fileVal",new File(""));
	args.put("filesVal",new File[2]);
	args.put("doubleVal",new Double(1234));
	actions.put("doSomething",args);
	return actions;
    }

    public void run(String action,ArgVector args){
	String outputStr="action: "+action;
	for (int i=0;i<args.size();i++){
	    String nextOne=args.arg(i).toString();
	    outputStr=outputStr+nextOne+"="+args.get((Object)nextOne).toString();
	}
	JFrame frame=new JFrame();
	JOptionPane.showMessageDialog(frame, outputStr);
    }
}
