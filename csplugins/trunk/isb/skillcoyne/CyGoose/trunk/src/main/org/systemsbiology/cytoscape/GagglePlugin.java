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
import org.systemsbiology.gaggle.boss.Boss;
import org.systemsbiology.gaggle.geese.Goose;
import org.systemsbiology.gaggle.util.MiscUtil;

import javax.swing.JOptionPane;

//import java.util.Set;
import java.util.HashMap;
//import java.util.Iterator;
//import java.util.HashSet;
//import java.util.Collection;
//import java.util.ArrayList;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
//import cytoscape.view.cytopanels.CytoPanelState;

import cytoscape.CyNetwork;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.CytoscapeVersion;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author skillcoy
 * 
 */
public class GagglePlugin extends CytoscapePlugin implements PropertyChangeListener
	{
	private static Boss GaggleBoss;
	private static GooseDialog Dialog;
	private static boolean Initialized = false;
	protected static String myGaggleName = "Cytoscape";
	// keeps track of all the network ids key = network id  value = goose 
	private static HashMap<String, Goose> NetworkGeese;
	
	private static void print(String S) { System.out.println(S); }

	public GagglePlugin()
		{
		// constructor gets called at load time and every time the toolbar is used
		if (Initialized) { return; }

		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener( this );
		
		myGaggleName += " v." + CytoscapeVersion.version;
		
		NetworkGeese = new HashMap<String, Goose>();
		Dialog = new GooseDialog();
		
		CytoPanel GoosePanel = Cytoscape.getDesktop().getCytoPanel(javax.swing.SwingConstants.WEST);
		GoosePanel.add("CyGoose Plugin", null, Dialog, "Gaggle Goose");
		GoosePanel.setSelectedIndex( GoosePanel.indexOfComponent(Dialog) );
		
		this.registerAction();
	
		try 
			{ 
			GaggleBoss = this.rmiConnect(); 
			// this gives an initial goose that is cytoscape with a null network
			this.createDefaultGoose();
			this.updateAction();
			}
		catch (Exception E)
			{
			GagglePlugin.showDialogBox("Failed to connect to the Boss", "Error", JOptionPane.ERROR_MESSAGE);
			E.printStackTrace();
			}
		
		Initialized = true;
		}
	
	private void createDefaultGoose() throws RemoteException
		{
		// this gives an initial goose that is cytoscape with a null network
		CyNetwork CurrentNet = Cytoscape.getNullNetwork();
		CurrentNet.setTitle(myGaggleName);
		Goose NewGoose = this.createNewGoose(CurrentNet);
		NetworkGeese.put(CurrentNet.getIdentifier(), NewGoose);
    MiscUtil.updateGooseChooser(GaggleBoss, Dialog.getGooseBox(), null, null);

    try { Cytoscape.getDesktop().setTitle("Goose: " + NewGoose.getName()); }
		catch (RemoteException E) { E.printStackTrace(); }
		}
	
	
	public void propertyChange(PropertyChangeEvent Event)
		{
		// register a goose
		if (Event.getPropertyName() == Cytoscape.NETWORK_CREATED)
			{
      String netId = Event.getNewValue().toString();
      CyNetwork net = Cytoscape.getNetwork(netId);

      try
      	{
	      Goose NewGoose = this.createNewGoose(net);
				NetworkGeese.put(net.getIdentifier(), NewGoose);
	      MiscUtil.updateGooseChooser(GaggleBoss, Dialog.getGooseBox(), null, null);
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
        Goose OldGoose = (Goose) NetworkGeese.get(net.getIdentifier());
      	Name = OldGoose.getName();
				GaggleBoss.remove( OldGoose.getName() ); 
				UnicastRemoteObject.unexportObject(OldGoose, true);
				Dialog.getGooseBox().removeItem( OldGoose.getName() );
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
		print("BOSS: " + GaggleBoss.toString() );
		RegisteredName = GaggleBoss.register(G);
		G.setName(RegisteredName);
		Dialog.getRegisterButton().setEnabled(false); 
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
		Dialog.getRegisterButton().addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent event)
				{
				try
					{
					GaggleBoss = rmiConnect();
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
		Dialog.getUpdateButton().addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
					{
					try
						{
						MiscUtil.updateGooseChooser(GaggleBoss, Dialog.getGooseBox(), null, null);
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
	private Goose createNewGoose(CyNetwork Network) throws RemoteException
		{
		CyGoose Goose = new CyGoose(Dialog, GaggleBoss);
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
//			if (NetworkGeese.containsKey(CurrentNet.getIdentifier())) continue;
//			else
//				{
//				Goose NewGoose = this.createNewGoose(CurrentNet);
//				NetworkGeese.put(CurrentNet.getIdentifier(), NewGoose);
//				}
//			}
//		this.removeOldNetworks(CurrentNetIds);
//		MiscUtil.updateGooseChooser(GaggleBoss, Dialog.getGooseBox(), null, null);
//		}
//
//	/* 
//	 * Remove dead geese from the boss
//	 * This will only remove networks that have been completely deleted, a network with no view is still a goose
//	 */
//	private void removeOldNetworks(Set<String> AllCurrentNetIds)
//		{
//		Iterator<String> NetGeeseIter = NetworkGeese.keySet().iterator();
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
//				print("Removing " + NetworkGeese.get(Id).getName() + " from the boss");
//				GaggleBoss.remove( NetworkGeese.get(Id).getName() ); 
//				UnicastRemoteObject.unexportObject(NetworkGeese.get(Id), true);
//				Dialog.getGooseBox().removeItem( NetworkGeese.get(Id).getName() );
//				}
//			catch (Exception E) { E.printStackTrace(); }
//			NetworkGeese.remove(Id); 
//			}
//		}


	public static void showDialogBox(String message, String title, int msgType)
		{ JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, title, msgType); }

	}
