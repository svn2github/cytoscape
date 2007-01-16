/**
 * 
 */
package org.systemsbiology.cytoscape;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

import org.systemsbiology.cytoscape.*;
import org.systemsbiology.gaggle.boss.Boss;
import org.systemsbiology.gaggle.geese.Goose;
import org.systemsbiology.gaggle.util.MiscUtil;

import javax.swing.JOptionPane;
import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNetworkEvent;
import cytoscape.CyNetworkListener;
import cytoscape.plugin.CytoscapePlugin;

/**
 * @author skillcoy
 * 
 */
public class GagglePlugin extends CytoscapePlugin
	{
	private static Boss GaggleBoss;
	private static GooseDialog Dialog;
	private static boolean Initialized = false;
	protected static String myGaggleName = "Cytoscape2.4";

	// keeps track of all the network ids 
	// key = network id  value = goose name
	private static HashMap<String, String> NetworkGeese;

	private static void print(String S) { System.out.println(S); }

	public GagglePlugin()
		{
		// constructor gets called at load time and every time the toolbar is used
		if (Initialized) { return; }
		print("**GagglePlugin constructor");
		
		NetworkGeese = new HashMap<String, String>();
		Dialog = new GooseDialog();
		// initial connection - this may not be the desired behaivior as this "goose" may not do anything
		GaggleBoss = this.rmiConnect();
		if (GaggleBoss == null) 
			{ 
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Failed to connect to the Boss", "Error", JOptionPane.ERROR_MESSAGE); 
			Dialog.registerButton.setEnabled(true);
			return;
			}
		else
			{ 
			print("** adding action to update button **");
			this.updateAction();
			// this gives an initial goose that is cytoscape with a null network
			CyNetwork CurrentNet = Cytoscape.getNullNetwork();
			this.createNewGoose(CurrentNet);
			NetworkGeese.put(CurrentNet.getIdentifier(), this.createGooseName(CurrentNet));
			}
		Initialized = true;
		}

	/*
	 * Exports and registers the goose with the Boss
	 */
	private void registerGoose(Goose G)
		{
		try
			{
			UnicastRemoteObject.exportObject(G, 0);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			String ErrorMsg = "Cytoscape failed to export remote object.";
			GagglePlugin.showDialogBox(ErrorMsg, "Error", JOptionPane.ERROR_MESSAGE);
			}
		
		try
			{
			GaggleBoss.register(G);
			Dialog.registerButton.setEnabled(false); 
			}
		catch (Exception E)
			{
			E.printStackTrace();
			String errMsg = "Gaggle connection failed!\n" + "Make sure a Gaggle Boss has started and click \"Register\" to try again";
			GagglePlugin.showDialogBox(errMsg, "Error", JOptionPane.ERROR_MESSAGE);
			Dialog.registerButton.setEnabled(true); 
			}
		}
	
	/*
	 * Creates the boss via the rmi connection
	 */
	private Boss rmiConnect() 
		{
		String serviceName = "gaggle";
		String hostname = "localhost";
		String uri = "rmi://" + hostname + "/" + serviceName;
		Boss GB = null;
		try 
			{ 
			GB = (Boss) Naming.lookup(uri); 
			}
		catch (Exception E)
			{
			E.printStackTrace();			
			}
		return GB;
		}
	
	/*
	 * action button
	 */
	private void updateAction()
		{ 
		Dialog.updateButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
					{
					try
						{
						updateNetworkGeese();
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
	private void createNewGoose(CyNetwork Network)
		{
		CyGoose Goose = new CyGoose(Dialog, GaggleBoss);
		Goose.setNetworkId(Network.getIdentifier());
		Goose.setName( this.createGooseName(Network) );
		this.registerGoose(Goose);
		//Dialog.gooseChooser.addItem(Goose.getName());
		MiscUtil.updateGooseChooser(GaggleBoss, Dialog.gooseChooser, Goose.getName(), null);
		}

	/*
	 * Creates a standard goose name for any network
	 */
	private String createGooseName(String Id, String Title)
		{ return (myGaggleName + ": " + Title + "(" + Id +")"); }
	
	private String createGooseName(CyNetwork Network)
		{ return createGooseName(Network.getIdentifier(), Network.getTitle()); }
	
	
	// TODO: remove closed networks from the boss
	private void updateNetworkGeese()
		{
		Iterator<CyNetwork> NetIter = Cytoscape.getNetworkSet().iterator();
		Set<String> CurrentNetIds = new HashSet<String>();
		// this SHOULD be adding each network as a potential "goose" in the list
		while (NetIter.hasNext())
			{
			CyNetwork CurrentNet = NetIter.next();
			CurrentNetIds.add(CurrentNet.getIdentifier());
			print(CurrentNet.getIdentifier());

			// if the network is already in the hash it should be in the list of geese
			if (NetworkGeese.containsKey(CurrentNet.getIdentifier())) continue;
			else
				{
				NetworkGeese.put(CurrentNet.getIdentifier(), this.createGooseName(CurrentNet));
				this.createNewGoose(CurrentNet);
				}
			}
		this.removeOldNetworks(CurrentNetIds);
		}

	
	
	// this needs to remove networks from the boss that have been destroyed
	private void removeOldNetworks(Set<String> AllCurrentNetIds)
		{
		print("removeOldNetworks");
		Iterator<String> NetGeeseIter = NetworkGeese.keySet().iterator();
		
		Collection<String> DeadIds = new ArrayList<String>();
		while (NetGeeseIter.hasNext())
			{
			String Id = NetGeeseIter.next();
			if (AllCurrentNetIds.contains(Id)) continue; 
			else DeadIds.add(Id); 
			}

		Iterator<String> deadIter = DeadIds.iterator();
		while(deadIter.hasNext())
			{ 
			String Id = deadIter.next();
			print("Removeing "+Id+" from the boss");
			NetworkGeese.remove(Id); 
			try { GaggleBoss.remove( NetworkGeese.get(Id)); }
			catch (Exception E) { E.printStackTrace(); }
			Dialog.gooseChooser.removeItem( NetworkGeese.get(Id) );
			MiscUtil.updateGooseChooser(GaggleBoss, Dialog.gooseChooser, null, null);
			}
		}


	public static void showDialogBox(String message, String title, int msgType)
		{
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, title,
				msgType);
		}

	}
