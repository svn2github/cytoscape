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
	protected static String myGaggleName = "Cytoscape2.4";

	private GooseDialog Dialog;

	private int NetworkGeeseStartIndex;

	private String targetGoose = "Boss";

	// keeps track of all the network ids
	private HashMap<String, String> NetworkGeese;

	private static void print(String S) { System.out.println(S); }

	public GagglePlugin()
		{
		this.NetworkGeese = new HashMap<String, String>();
		this.Dialog = new GooseDialog();
		// initial connection - this may not be the desired behaivior as this "goose" may not do anything
		Boss GBoss = this.rmiConnect();
		if (GBoss == null) 
			{ 
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Failed to connect to the Boss", "Error", JOptionPane.ERROR_MESSAGE); 
			this.Dialog.registerButton.setEnabled(true);
			return;
			}
		else
			{ 
			this.updateAction();
			// this gives an initial goose that is cytoscape with a null network
			CyNetwork CurrentNet = Cytoscape.getCurrentNetwork();

			this.createNewGoose(CurrentNet);
			this.NetworkGeese.put(CurrentNet.getIdentifier(), CurrentNet.getTitle());
			}
		}

	/*
	 * Exports and registers the goose with the Boss
	 */
	private void registerGoose(Boss B, Goose G)
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
			B.register(G);
			this.Dialog.registerButton.setEnabled(false); 
			}
		catch (Exception E)
			{
			E.printStackTrace();
			String errMsg = "Gaggle connection failed!\n" + "Make sure a Gaggle Boss has started and click \"Register\" to try again";
			GagglePlugin.showDialogBox(errMsg, "Error", JOptionPane.ERROR_MESSAGE);
			this.Dialog.registerButton.setEnabled(true); 
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
		this.Dialog.updateButton.addActionListener(new ActionListener()
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
		Boss GaggleBoss = this.rmiConnect();
		CyGoose Goose = new CyGoose(this.Dialog, GaggleBoss);
		Goose.setNetworkId(Network.getIdentifier());
		Goose.setName(myGaggleName + ": " + Network.getTitle() + "(" + Network.getTitle() + ")");
		this.registerGoose(GaggleBoss, Goose);
		MiscUtil.updateGooseChooser(GaggleBoss, this.Dialog.gooseChooser, Goose.getName(), null);
		}


	// TODO: remove closed networks from the boss
	protected void updateNetworkGeese()
		{
		Iterator<CyNetwork> NetIter = Cytoscape.getNetworkSet().iterator();


		// add separator to group network "geese" names
		NetworkGeeseStartIndex = this.Dialog.gooseChooser.getItemCount();
		//this.Dialog.gooseChooser.addItem("-- Cytoscape Networks --");

		// this SHOULD be adding each network as a potential "goose" in the list
		while (NetIter.hasNext())
			{
			CyNetwork CurrentNet = NetIter.next();

			// if the network is already in the hash it should be in the list of geese
			if (this.NetworkGeese.containsKey(CurrentNet.getIdentifier())) continue;
			else
				{
				this.createNewGoose(CurrentNet);
				}
			//this.removeOldNetworks();
			}
		}
	// this needs to remove networks from the boss as well...not sure how to do that yet
	private void removeOldNetworks()
		{
		Iterator<String> NetGeeseIter = this.NetworkGeese.keySet().iterator();
		java.util.Set<CyNetwork> AllCurrentNetworks = Cytoscape.getNetworkSet();

		while (NetGeeseIter.hasNext())
			{
			String Id = NetGeeseIter.next();
			if (!AllCurrentNetworks.contains(Id))
				{ // I don't know for sure that removes options from the chooser, we
				// shall see
				this.Dialog.gooseChooser.removeItem(Id + ":"
						+ this.NetworkGeese.get(Id));
				this.NetworkGeese.remove(Id);
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
