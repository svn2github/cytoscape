package GenericPlugin;

import java.util.*;
import java.lang.reflect.Array;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;
import java.io.File;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.CyNetwork;
import cytoscape.data.CyNetworkFactory;
import cytoscape.data.readers.GMLReader;
import cytoscape.view.CyNetworkView;
import cytoscape.util.GinyFactory;
import cytoscape.actions.FitContentAction;
import cytoscape.data.Semantics;

public abstract class PluginUtil  extends CytoscapePlugin implements GenericPlugin {

  /**
   * Print out debugging information?
   */
    public static boolean DEBUG = false;
    public String name="Generic Plugin";
    public String description="This Generic Plugin doesn't do anything";
    public String[] cmdArgs;
    public String file_split_string=";";
    public JDialog uiDialog; //allows for a reference to the current dialog, to allow changing behaviors on verification (see verify below)

    public PluginUtil() {
    }
    
    /**
     * Gets the actions and their argument values (as a HashMap) from the specific plugin
     * actions is a hasTable of hashTables.  Each key is a string (name) and each key is a hashTable of arguments for the action
     * possible classes for the values in the argument hashTable of each action are:
     String, File, FileList(Defined Here), Integer, Double
    */
    abstract public HashMap getActions();
    
    /**
     * kicked off when any action in the plugin is fired.
     */
    abstract public void run(String action,HashMap args);

    /**
     *  Start imlemented functions
     */

    public void initialize(){
	cmdArgs=Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
	HashMap actions=getActions();
	runActionsFromCmdLine(actions);
	if (cmdDoNotAddActions()){return;}
	Iterator it=actions.keySet().iterator();
	String actionName;
	while (it.hasNext()){
	    actionName=it.next().toString();
            Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new PluginUtilAction(actionName,(HashMap)actions.get(actionName),this));
	}
    }
    
    public void initialize(String initname, String initdescription){
	name=initname;description=initdescription;
	initialize();
    }

    /**
     * Gives a description of this plugin.
     */

    public String describe() {
	return description;
    }
    
    public boolean cmdDoNotAddActions(){
	for (int i=0;i<cmdArgs.length;i++){
	    if (cmdArgs[i].equals("--doNotAddActions")){return true;}
	}
	return false;
    }
    
    public boolean runActionsFromCmdLine(HashMap actions){
	String actionName;
	String[] curArg;
	boolean actionsRun=false;
	boolean exitWhenFinished=false;
	for (int i=0;i<cmdArgs.length;i++){
	    if (cmdArgs[i].indexOf("--")==0){
		curArg=cmdArgs[i].split(",");
		actionName=curArg[0].substring(2);
		//is actionName an action?
		if (actions.containsKey(actionName)){
		    //assign cmdLineVars in curArg
		    HashMap actionArgs=(HashMap)actions.get((Object)actionName);
		    for (int j=1;j<curArg.length;j++){
			if (!setValue(actionArgs,curArg[j].split("=")[0],curArg[j].split("=")[1])){
			    System.err.println("could not assign commandLine Argument "+curArg[j].split("=")[0]);
			}
		    }
		    //Run the action
		    Thread t = new PluginUtilThread(actionName,(HashMap)actions.get(actionName),this); 
		    t.start();
		    actionsRun=true;
		    
		}   
		else{
		    if (cmdArgs[i].equals("--exit")){exitWhenFinished=true;}
		}
	    }
	}
	if (exitWhenFinished){System.exit(0);}
	return actionsRun;
    }

    public String usage(String actionName,HashMap args) {

        String usageStr;
	usageStr="--"+actionName;
	Iterator it=args.entrySet().iterator();
	String nexti;
	while (it.hasNext()){
	    nexti=it.next().toString();
	    usageStr=usageStr+",["+nexti+"="+args.get(nexti).toString()+"]";
	}
	return usageStr;
    }
    
    public String verify(String curVal,String newVal){
	try{
	    curVal=new String(newVal);
	    return curVal;
	}
	catch (ClassCastException e){
	}
	return null;
    }

    public File verify(File curVal,String newVal){
	curVal=new File(newVal);
	if(!curVal.isFile()){return null;}
	return curVal;
    }

    public File[] verify(File[] curVal,String newVal){
	String[] files=newVal.split(file_split_string);
	for(int i=0;i<files.length;i++){
	    System.out.println(files[i]);
	}
	File[] tmpVal=new File[files.length];
	for(int i=0;i<files.length;i++){
	    tmpVal[i]=new File(files[i]);
	    if (!tmpVal[i].isFile()){
		System.out.println("invalid file number "+(new Integer(i)).toString()+":"+files[i]);
		return null;
	    }
	}
	curVal=tmpVal;
	return curVal;
    }

    public Integer verify(Integer curVal,String newVal){
	try{
	    curVal=new Integer(newVal);
	    return curVal;
	}
	catch (NumberFormatException e){
	}
	return null;
    }

    public Double verify(Double curVal,String newVal){
	try{
	    curVal=new Double(newVal);
	    return curVal;
	}
	catch (NumberFormatException e){
	}
	return null;
    }


    public boolean setValue(HashMap args,String curArg,String newVal){

	Object curVal=args.get(curArg);
	if (curVal.getClass().equals(String.class)){
	    String val=verify((String)curVal,newVal);
	    if (val!=null){args.put(curArg,val);return true;}
	}
	else if (curVal.getClass().equals(File.class)){
	    File val=verify((File)curVal,newVal);
	    if (val!=null){args.put(curArg,val);return true;}
	}
	else if (curVal.getClass().equals(File[].class)){
	    File[] val= verify((File[])curVal,newVal);
	    if (val!=null){args.put(curArg,val);return true;}
	}
	else if (curVal.getClass().equals(Double.class)){
	    Double val=verify((Double)curVal,newVal);
	    if (val!=null){args.put(curArg,val);return true;}
	}
	else if (curVal.getClass().equals(Integer.class)){
	    Integer val= verify((Integer)curVal,newVal);
	    if (val!=null){args.put(curArg,val);return true;}
	}
	else{
	    System.err.println("Unable to verify class of current value:  "+curVal.toString()+" of class "+curVal.getClass().toString()+".  This value will not be changed to:  "+newVal+".  Continuing...");
	}
	return false;
    }

    public boolean getUIArgs(String actionName,final HashMap args){
	//put all internal args into this HashMap so they can be accessed from actions (nested classes)
	final HashMap internalArgs=new HashMap(1);
	internalArgs.put("processed",new Boolean(false));
	JFrame frame=new JFrame();
	//setup the dialog in the frame, with the actionName as the title, as a modal dialog (true)
	JDialog uiDialog=new JDialog(frame,actionName,true);
	//required in order to stop this process until the dialog is finished
	internalArgs.put("uiDialog",uiDialog);
	Container contentPane = uiDialog.getContentPane();
	contentPane.setLayout(new BorderLayout());    

	PluginUITableModel uiArgs=new PluginUITableModel(args,this,file_split_string);
	internalArgs.put("uiArgs",uiArgs);
	PluginUITable uiTbl=new PluginUITable((TableModel)uiArgs);

	JScrollPane scrollPane = new JScrollPane(uiTbl);

	scrollPane.setPreferredSize(new Dimension(100,100));
	JPanel centerPanel=new JPanel();
	centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.Y_AXIS));
	centerPanel.add(new JLabel(this.name));
	centerPanel.add(scrollPane);
	
	JPanel southPanel = new JPanel();
	JButton runButton = new JButton("Run Action");
	runButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
		    JDialog uiDialog=(JDialog)internalArgs.get("uiDialog");
		    PluginUITableModel uiArgs=(PluginUITableModel)internalArgs.get("uiArgs");
		    uiDialog.dispose();
		    //change the arguments in the HashMap to those from the TableModel
		    args.clear();
		    args.putAll(uiArgs.args);
		    internalArgs.put("processed",new Boolean(true));
		}
	});
	JButton cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
		    JDialog uiDialog=(JDialog)internalArgs.get("uiDialog");
		    uiDialog.dispose();
		    internalArgs.put("processed",new Boolean(false));
		}
	});
	southPanel.add(runButton);
	southPanel.add(cancelButton);

	//setup the Dialog
	contentPane.add(centerPanel,BorderLayout.CENTER);
	contentPane.add(southPanel,BorderLayout.SOUTH);
System.out.println("about to pack");
	this.uiDialog=uiDialog;
	uiDialog.pack();
System.out.println("packed");
	uiDialog.show();
System.out.println("returning "+((Boolean)internalArgs.get("processed")).toString());
	return ((Boolean)internalArgs.get("processed")).booleanValue();
    }

}
