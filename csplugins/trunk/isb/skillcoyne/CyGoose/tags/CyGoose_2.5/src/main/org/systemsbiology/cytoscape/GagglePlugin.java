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
import java.util.Iterator;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;

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
	private boolean registered;
	private RenameThread renameGoose;
	
	private static Boss gaggleBoss;
	private static GooseDialog gDialog;
	private static CyBroadcast broadcast;
	private static boolean pluginInitialized = false;
	
	protected static String myGaggleName = "Cytoscape";
	// keeps track of all the network ids key = network id  value = goose 
	private static HashMap<String, CyGoose> networkGeese;
	
	private static void print(String S) { System.out.println(S); }

	public GagglePlugin()
		{
		// constructor gets called at load time and every time the toolbar is used
		if (pluginInitialized) { return; }
//		renameGoose = new RenameThread();

		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener( this );
		
		myGaggleName += " v." + CytoscapeVersion.version;
		
		networkGeese = new HashMap<String, CyGoose>();
		gDialog = new GooseDialog();
		
		CytoPanel GoosePanel = Cytoscape.getDesktop().getCytoPanel(javax.swing.SwingConstants.WEST);
		GoosePanel.add("CyGoose", null, gDialog, "Gaggle Goose");
		GoosePanel.setSelectedIndex( GoosePanel.indexOfComponent(gDialog) );
		
		registerAction();
	
		try 
			{ 
			gaggleBoss = rmiConnect(); 
			// this gives an initial goose that is cytoscape with a null network
			createDefaultGoose();
			updateAction();
			registered = true;
//			renameGoose.run();
			}
		catch (Exception E)
			{ // TODO add error message text area to goose panel and stop popping error box up
			registered = false;
			//GagglePlugin.showDialogBox("Failed to connect to the Boss", "Error", JOptionPane.ERROR_MESSAGE);
			System.err.println(E.getMessage());
			//E.printStackTrace();
			}
		
		broadcast = new CyBroadcast(gDialog, gaggleBoss);
		this.addButtonActions();
		pluginInitialized = true;
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
	
	// Network created: create goose  Network destroyed: remove goose
	public void propertyChange(PropertyChangeEvent Event)
		{
		// nothing has been registered, don't try to handle events
		if (!registered) return;
		
		// register a goose
		if (Event.getPropertyName() == Cytoscape.NETWORK_CREATED)
			{
      String netId = Event.getNewValue().toString();
      CyNetwork net = Cytoscape.getNetwork(netId);

      try
      	{
	      CyGoose NewGoose = createNewGoose(net);
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
        CyGoose OldGoose = (CyGoose) networkGeese.get(net.getIdentifier());
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
			gDialog.getMessageLabel().setText(ErrorMsg);
			//GagglePlugin.showDialogBox(ErrorMsg, "Error", JOptionPane.ERROR_MESSAGE);
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
					updateAction();
					registered = true;
//					renameGoose.run();
					// todo want to be able to update all the networks/geese here since user 
					// may have opened some prior to connection
					}
				catch (Exception E)
					{  // TODO quit popping box up, add error message bar to goose panel
					registered = false;
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
					System.out.println("--- update action ----");
					checkNameChange();
					MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
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
		Goose.setName( Network.getTitle() );
		registerGoose(Goose);
		return Goose;
		}

	
	private void checkNameChange()
		{
		System.out.println("checkNameChange()");
		
		Iterator<CyNetwork> NetIter = Cytoscape.getNetworkSet().iterator();
		while (NetIter.hasNext())
			{
			CyNetwork Network = NetIter.next();
			if (networkGeese.containsKey(Network.getIdentifier()))
				{
				// check the name of the goose somehow
				CyGoose Goose = networkGeese.get(Network.getIdentifier());
				System.out.println("*** Goose:" + Goose.getName() + " Network:" + Network.getTitle());
				
				// Network name has changed
				//if (!Goose.getName().matches("^"+Network.getTitle()+"\\(" + Network.getIdentifier() + "\\)(-\\d+)?"))
				if (!Goose.getName().matches("^"+Network.getTitle()+"(-\\d+)?"))
					{
					System.out.println("Renaming goose, net id: " + Network.getIdentifier());
					try 
						{ 
//						Goose.setName( gaggleBoss.renameGoose(Goose.getName(), createGooseName(Network.getTitle()))); 
//						MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
//						networkGeese.put(Network.getIdentifier(), Goose);
						
						// HACK!!! Delete goose, re-register 
						gaggleBoss.remove( Goose.getName() ); 
						gDialog.getGooseBox().removeItem( Goose.getName() );
						UnicastRemoteObject.unexportObject(Goose, true);
						
						networkGeese.put(Network.getIdentifier(), createNewGoose(Network));
			      MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
						}
					catch (RemoteException re)
						{ re.printStackTrace(); }
					}
				}
			else 
				{
	      try
	      	{
		      CyGoose NewGoose = this.createNewGoose(Network);
					networkGeese.put(Network.getIdentifier(), NewGoose);
		      MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
	      	}
	      catch (RemoteException E)
	      	{
	      	GagglePlugin.showDialogBox("Failed to create a new Goose for network " + Network.getTitle(), "Error", JOptionPane.ERROR_MESSAGE);
	      	E.printStackTrace();
	      	}

				}
			}
		}
	
	
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
	
	private class RenameThread extends Thread
		{
		RenameRunnable rr;
		public RenameThread()
			{
			rr = new RenameRunnable();
			}
		public void run()
			{
			while (registered)
				{
				rr.run();
				try { this.wait(1000); }
				catch (Exception E) { E.printStackTrace(); System.exit(0); }
				}
			}
		}
	
	private class RenameRunnable implements Runnable 
		{
		public void run()
			{
			checkNameChange();
			}
		
		
		private void checkNameChange()
			{
			System.out.println("checkNameChange()");
			
			Iterator<CyNetwork> NetIter = Cytoscape.getNetworkSet().iterator();
			while (NetIter.hasNext())
				{
				CyNetwork Network = NetIter.next();
				if (networkGeese.containsKey(Network.getIdentifier()))
					{
					// check the name of the goose somehow
					CyGoose Goose = networkGeese.get(Network.getIdentifier());
					System.out.println("*** Goose:" + Goose.getName() + " Network:" + Network.getTitle());
					
					// Network name has changed
					//if (!Goose.getName().matches("^"+Network.getTitle()+"\\(" + Network.getIdentifier() + "\\)(-\\d+)?"))
					if (!Goose.getName().matches("^"+Network.getTitle()+"(-\\d+)?"))
						{
						System.out.println("Renaming goose, net id: " + Network.getIdentifier());
						try 
							{ 
//							Goose.setName( gaggleBoss.renameGoose(Goose.getName(), createGooseName(Network.getTitle()))); 
//							MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
//							networkGeese.put(Network.getIdentifier(), Goose);
							
							// HACK!!! Delete goose, re-register 
							gaggleBoss.remove( Goose.getName() ); 
							gDialog.getGooseBox().removeItem( Goose.getName() );
							UnicastRemoteObject.unexportObject(Goose, true);
							
							networkGeese.put(Network.getIdentifier(), createNewGoose(Network));
				      MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
							}
						catch (RemoteException re)
							{ re.printStackTrace(); }
						}
					}
				else 
					{
		      try
		      	{
			      CyGoose NewGoose = createNewGoose(Network);
						networkGeese.put(Network.getIdentifier(), NewGoose);
			      MiscUtil.updateGooseChooser(gaggleBoss, gDialog.getGooseBox(), null, null);
		      	}
		      catch (RemoteException E)
		      	{
		      	GagglePlugin.showDialogBox("Failed to create a new Goose for network " + Network.getTitle(), "Error", JOptionPane.ERROR_MESSAGE);
		      	E.printStackTrace();
		      	}

					}
				}
			}

		
		}
	
	}
