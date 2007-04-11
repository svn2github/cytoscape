/**
 * 
 */
package org.systemsbiology.cytoscape;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.net.MalformedURLException;

import org.systemsbiology.cytoscape.CyGoose;
import org.systemsbiology.cytoscape.dialog.GooseDialog;
import org.systemsbiology.cytoscape.CyBroadcast;

import org.systemsbiology.gaggle.boss.Boss;
import org.systemsbiology.gaggle.geese.Goose;
import org.systemsbiology.gaggle.util.MiscUtil;

import javax.swing.JOptionPane;

import java.util.HashMap;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;

import cytoscape.CyNetwork;
import cytoscape.plugin.CytoscapePlugin;
//import cytoscape.plugin.PluginInfo;
//import cytoscape.plugin.PluginInfo.Category;
import cytoscape.CytoscapeVersion;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author skillcoy
 * 
 */
public class GagglePlugin extends CytoscapePlugin implements PropertyChangeListener
	{
	private static Boss gaggleBoss;
	private static GooseDialog gDialog;
	private static CyBroadcast broadcast;
	private static boolean Initialized = false;

	protected static String myGaggleName = "Cytoscape";
	// keeps track of all the network ids key = network id  value = goose 
	private static HashMap<String, CyGoose> networkGeese;
	
	private static void print(String S) { System.out.println(S); }

	public GagglePlugin()
		{
		// constructor gets called at load time and every time the toolbar is used
		if (Initialized) { return; }

		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener( this );
		
		myGaggleName += " v." + CytoscapeVersion.version;
		
		networkGeese = new HashMap<String, CyGoose>();
		gDialog = new GooseDialog();
		
		CytoPanel GoosePanel = Cytoscape.getDesktop().getCytoPanel(javax.swing.SwingConstants.WEST);
		GoosePanel.add("CyGoose Plugin", null, gDialog, "Gaggle Goose");
		GoosePanel.setSelectedIndex( GoosePanel.indexOfComponent(gDialog) );
		
		this.registerAction();
	
		try 
			{ 
			gaggleBoss = this.rmiConnect(); 
			// this gives an initial goose that is cytoscape with a null network
			this.createDefaultGoose();
			this.updateAction();
			}
		catch (Exception E)
			{
			GagglePlugin.showDialogBox("Failed to connect to the Boss", "Error", JOptionPane.ERROR_MESSAGE);
			E.printStackTrace();
			}
		
		broadcast = new CyBroadcast(gDialog, gaggleBoss);
		this.addButtonActions();
		Initialized = true;
		}
	
	
	private String getTargetGoose()
		{
    int targetGooseIndex = this.gDialog.getGooseBox().getSelectedIndex();
		String targetGooseName = (String) this.gDialog.getGooseBox().getSelectedItem();
		print("Target index: "+targetGooseIndex+"  Target item: "+targetGooseName);
		return targetGooseName;
		}

	
	// creates the "null" network goose 
	private void createDefaultGoose() throws RemoteException
		{
		// this gives an initial goose that is cytoscape with a null network
		CyNetwork CurrentNet = Cytoscape.getNullNetwork();
		CurrentNet.setTitle(myGaggleName);
		CyGoose NewGoose = this.createNewGoose(CurrentNet);
		networkGeese.put(CurrentNet.getIdentifier(), NewGoose);
    MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);

    Cytoscape.getDesktop().setTitle("Goose: " + NewGoose.getName());
		}
	
	
	public void propertyChange(PropertyChangeEvent Event)
		{
		// nothing has been registered, don't try to handle events
		if (gaggleBoss == null) 
			return;
		
		// register a goose
		if (Event.getPropertyName() == Cytoscape.NETWORK_CREATED)
			{
      String netId = Event.getNewValue().toString();
      CyNetwork net = Cytoscape.getNetwork(netId);

      try
      	{
	      CyGoose NewGoose = this.createNewGoose(net);
				networkGeese.put(net.getIdentifier(), NewGoose);
	      MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
      	}
      catch (RemoteException E)
      	{
      	GagglePlugin.showDialogBox("Failed to create a new Goose for network " + net.getTitle(), "Error", JOptionPane.ERROR_MESSAGE);
      	E.printStackTrace();
      	}
      
      
			}
		// remove a goose
		if (Event.getPropertyName() == Cytoscape.NETWORK_DESTROYED)
			{
      String netId = Event.getNewValue().toString();
      CyNetwork net = Cytoscape.getNetwork(netId);

      String Name = "";
      try
      	{
        Goose OldGoose = (Goose) networkGeese.get(net.getIdentifier());
      	Name = OldGoose.getName();
				gaggleBoss.remove( OldGoose.getName() ); 
				UnicastRemoteObject.unexportObject(OldGoose, true);
				gDialog.getGooseBox().removeItem( OldGoose.getName() );
      	}
      catch (RemoteException E)
      	{
      	GagglePlugin.showDialogBox("Failed to remove goose '" + Name + "' from Boss", "Warning", JOptionPane.WARNING_MESSAGE);
      	E.printStackTrace();
      	}
			}
		
		}

	/*
	 * Exports and registers the goose with the Boss
	 */
	private void registerGoose(Goose G) throws RemoteException
		{
		String RegisteredName = null;
		
		try { UnicastRemoteObject.exportObject(G, 0); }
		catch (RemoteException e)
			{
			e.printStackTrace();
			String ErrorMsg = "Cytoscape failed to export remote object.";
			GagglePlugin.showDialogBox(ErrorMsg, "Error", JOptionPane.ERROR_MESSAGE);
			}
		
		String FilePath = null;
		java.util.Iterator pI = cytoscape.CytoscapeInit.getPluginURLs().iterator();
		while (pI.hasNext())
			{
			java.net.URL url = (java.net.URL)pI.next();
			if (url.getPath().contains("CyGoose.jar"))
				{
				FilePath = url.getPath();
				FilePath = FilePath.replace("/", System.getProperty("file.separator"));
				FilePath = FilePath.replaceFirst("file:", "");
				}
			}
//		System.setProperty("java.rmi.server.codebase", FilePath);
//		System.out.println( "CLASSPATH: " + System.getProperty("java.class.path"));
//		System.out.println("RMI CODEBASE: " + System.getProperty("java.rmi.server.codebase"));
		RegisteredName = gaggleBoss.register(G);
		G.setName(RegisteredName);
		gDialog.getRegisterButton().setEnabled(false); 
		}
	
	/*
	 * Creates the boss via the rmi connection
	 */
	private Boss rmiConnect() throws RemoteException, java.rmi.NotBoundException, MalformedURLException 
		{
		String serviceName = "gaggle";
		String hostname = "localhost";
		
		String osName = System.getProperty("os.name");
		print("OS name: " + osName);

		String uri = "rmi://" + hostname + "/" + serviceName;
		print("Rmi uri: " + uri);
		
		return ( (Boss) Naming.lookup(uri) ); 
		}
	
	private void registerAction()
		{
		gDialog.getRegisterButton().addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent event)
				{
				try
					{
					gaggleBoss = rmiConnect();
					createDefaultGoose();
					// todo want to be able to update all the networks/geese here since user 
					// may have opened some prior to connection
					}
				catch (Exception E)
					{ 
					GagglePlugin.showDialogBox("Failed to connect to the Boss", "Error", JOptionPane.ERROR_MESSAGE);
					E.printStackTrace(); 
					}
				}
			}	);
		}
	
	/*
	 * action button
	 */
	private void updateAction()
		{ 
		gDialog.getUpdateButton().addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
					{
					try
						{
						MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
						}
					catch (Exception ex)
						{
						ex.printStackTrace();
						}
					}
			});
		}

	/*
	 * Creates a new goose for the given network
	 */
	private CyGoose createNewGoose(CyNetwork Network) throws RemoteException
		{
		CyGoose Goose = new CyGoose(gDialog, gaggleBoss);
		Goose.setNetworkId(Network.getIdentifier());
		Goose.setName( this.createGooseName(Network) );
		this.registerGoose(Goose);
		return Goose;
		}

	/*
	 * Creates a standard goose name for any network
	 * TODO: 
	 * Burak suggested that renaming the network to the gaggle name would be useful
	 * for some reason Network.setTitle("title") isn't working for me though
	 * Ideally geese could be assigned ids rather than id'd by their name, this would make it easier 
	 * but that's up to gaggle
	 */
	private String createGooseName(String Id, String Title)
		{ return (Title + "(" + Id +")"); }
	private String createGooseName(CyNetwork Network)
		{ return createGooseName(Network.getIdentifier(), Network.getTitle()); }
	
	/*
	 * Updates the list of network geese in the boss by adding new networks as gees
	 * and removing destroyed networks.
	 * TODO: If 2 sessions of cytoscape were open and one gets closed it's geese do not go away WHY?
	 */
//	private void updateNetworkGeese()
//		{
//		Iterator<CyNetwork> NetIter = Cytoscape.getNetworkSet().iterator();
//		Set<String> CurrentNetIds = new HashSet<String>();
//
//		// this should be adding each network as a potential "goose" in the list
//		while (NetIter.hasNext())
//			{
//			CyNetwork CurrentNet = NetIter.next();
//			CurrentNetIds.add(CurrentNet.getIdentifier());
//
//			// if the network is already in the hash it should be in the list of geese
//			if (networkGeese.containsKey(CurrentNet.getIdentifier())) continue;
//			else
//				{
//				Goose NewGoose = this.createNewGoose(CurrentNet);
//				networkGeese.put(CurrentNet.getIdentifier(), NewGoose);
//				}
//			}
//		this.removeOldNetworks(CurrentNetIds);
//		MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
//		}
//
//	/* 
//	 * Remove dead geese from the boss
//	 * This will only remove networks that have been completely deleted, a network with no view is still a goose
//	 */
//	private void removeOldNetworks(Set<String> AllCurrentNetIds)
//		{
//		Iterator<String> NetGeeseIter = networkGeese.keySet().iterator();
//		
//		Collection<String> DeadIds = new ArrayList<String>();
//		while (NetGeeseIter.hasNext())
//			{
//			String Id = NetGeeseIter.next();
//			// null network = 0, should never try to remove it
//			if ( AllCurrentNetIds.contains(Id) || Id.equals("0")) continue; 
//			else DeadIds.add(Id); 
//			}
//
//		Iterator<String> deadIter = DeadIds.iterator();
//		while(deadIter.hasNext())
//			{ 
//			String Id = deadIter.next();
//			try 
//				{ 
//				print("Removing " + networkGeese.get(Id).getName() + " from the boss");
//				gaggleBoss.remove( networkGeese.get(Id).getName() ); 
//				UnicastRemoteObject.unexportObject(networkGeese.get(Id), true);
//				gDialog.getGooseBox().removeItem( networkGeese.get(Id).getName() );
//				}
//			catch (Exception E) { E.printStackTrace(); }
//			networkGeese.remove(Id); 
//			}
//		}


	private void addButtonActions()
		{
		System.out.println("add button actions");
		/* broadcast name list to other goose (geese) */
		gDialog.getListButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
          {
          try
            {
            CyNetwork Network = Cytoscape.getCurrentNetwork();
        		CyGoose Goose = networkGeese.get(Network.getIdentifier());
            String TargetGoose = getTargetGoose();

            broadcast.broadcastNameList(Goose, TargetGoose);
            }
          catch (Exception ex)
            {
            ex.printStackTrace();
            }
          }
      });

    /* broadcast a network to other goose (geese) */
    gDialog.getNetButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
          {
          System.out.println("Net action");
          try
            {
            CyNetwork Network = Cytoscape.getCurrentNetwork();
        		CyGoose Goose = networkGeese.get(Network.getIdentifier());
            String TargetGoose = getTargetGoose();
            
            broadcast.broadcastNetwork(Goose, TargetGoose);
            }
          catch (Exception ex)
            {
            ex.printStackTrace();
            }
          }
      });

    /* broadcast data matrix to other goose (geese) */
    gDialog.getMatrixButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
          {
          try
            {
            CyNetwork Network = Cytoscape.getCurrentNetwork();
        		CyGoose Goose = networkGeese.get(Network.getIdentifier());
            String TargetGoose = getTargetGoose();
            
            broadcast.broadcastDataMatrix(Goose, TargetGoose);
            }
          catch (Exception ex)
            {
            ex.printStackTrace();
            }
          }
      });

    /* broadcast HashMap to other goose (geese) */
    gDialog.getMapButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
          {
          try
            {
            CyNetwork Network = Cytoscape.getCurrentNetwork();
        		CyGoose Goose = networkGeese.get(Network.getIdentifier());
            String TargetGoose = getTargetGoose();
            
            broadcast.broadcastHashMap(Goose, TargetGoose);
            }
          catch (Exception ex)
            {
            ex.printStackTrace();
            }
					}
			});
		}

	
	
	public static void showDialogBox(String message, String title, int msgType)
		{ JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, title, msgType); }
	
//	public PluginInfo getPluginInfoObj()
//		{
//		PluginInfo Info = new PluginInfo(this.getClass().getName());
//		Info.setName("CyGoose");
//		Info.setPluginVersion("2.4.3");
//		Info.setCategory(Category.COMMUNICATION_SCRIPTING.getCategoryText());
//		Info.setDescription(getDesc());
//		Info.addAuthor("Sarah Killcoyne", "Institute for Systems Biology");
//		Info.addAuthor("John Lin, Kevin Drew and Richard Bonneau", "NYU Bonneau Lab");
//		}
//
//	private String getDesc()
//		{
//		String Desc = "The CyGoose Cytoscape Plugin gives any network in Cytoscape full access to the Gaggle. " +
//		"The Gaggle is a tool to create a data exploration and analysis environment. " +
//		"Other geese (independent threads/tools which Cytoscape can now interact with through the Gaggle Boss) " + 
//		"can be found at http://gaggle.systemsbiology.org/docs/geese/. " +
//		"ISSUE: This goose does not work with a local install of Cytoscape on a Windows machine. It will work with Mac or Linux and " + 
//		"as a webstart on Mac/Linux/Windows. We are working on this and will release a fix as soon as we can.";
//		}
	
	}
