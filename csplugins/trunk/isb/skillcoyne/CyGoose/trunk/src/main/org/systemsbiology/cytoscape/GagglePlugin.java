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
	private static HashMap<String, String> NetworkGeese;

	private static void print(String S) { System.out.println(S); }

	public GagglePlugin()
		{
		// constructor gets called at load time and every time the toolbar is used
		// if the toolbar could be attached to the CyMenu bar instead of the plugin
		// this would not be an issue...I think.
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
			NetworkGeese.put(CurrentNet.getIdentifier(), CurrentNet.getTitle());
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
	protected void updateAction()
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



	protected void createNewGoose(CyNetwork Network)
		{
		CyGoose Goose = new CyGoose(Dialog, GaggleBoss);
		Goose.setNetworkId(Network.getIdentifier());
		Goose.setName( this.createGooseName(Network) );
		this.registerGoose(Goose);
		MiscUtil.updateGooseChooser(GaggleBoss, Dialog.gooseChooser, Goose.getName(), null);
		}

	
	private String createGooseName(CyNetwork Network)
		{
		return (myGaggleName + ": " + Network.getTitle() + "(" + Network.getIdentifier() + ")");
		}
	
	
	// TODO: remove closed networks from the boss
	protected void updateNetworkGeese()
		{
		Iterator<CyNetwork> NetIter = Cytoscape.getNetworkSet().iterator();

		// this SHOULD be adding each network as a potential "goose" in the list
		while (NetIter.hasNext())
			{
			CyNetwork CurrentNet = NetIter.next();

			// if the network is already in the hash it should be in the list of geese
			if (NetworkGeese.containsKey(CurrentNet.getIdentifier())) continue;
			else
				{
				this.createNewGoose(CurrentNet);
				NetworkGeese.put(CurrentNet.getIdentifier(), CurrentNet.getTitle());
				}
			//this.removeOldNetworks();
			}
		}

	// this needs to remove networks from the boss as well
	private void removeOldNetworks()
		{
		Iterator<String> NetGeeseIter = NetworkGeese.keySet().iterator();
		java.util.Set<CyNetwork> AllCurrentNetworks = Cytoscape.getNetworkSet();

		while (NetGeeseIter.hasNext())
			{
			String Id = NetGeeseIter.next();
			if (!AllCurrentNetworks.contains(Id))
				{ // I don't know for sure that removes options from the chooser, we
				// shall see
				Dialog.gooseChooser.removeItem(Id + ":"
						+ NetworkGeese.get(Id));
				NetworkGeese.remove(Id);
				}
			}

		}

	/*
   * TODO: Create a new goose for each network opened. - do NOT create the
   * dialog new each time - broadcast buttons will need to be aware of which
   * network ("goose") they are looking at
   */

	public static void showDialogBox(String message, String title, int msgType)
		{
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, title,
				msgType);
		}

	}
