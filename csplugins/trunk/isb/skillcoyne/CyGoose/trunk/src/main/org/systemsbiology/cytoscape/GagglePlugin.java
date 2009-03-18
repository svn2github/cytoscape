/**
 *
 */
package org.systemsbiology.cytoscape;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.CytoscapeVersion;
import cytoscape.logger.CyLogger;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.CytoscapeDesktop;
import org.systemsbiology.cytoscape.dialog.GooseDialog;
import org.systemsbiology.cytoscape.dialog.GooseDialog.GooseButton;
import org.systemsbiology.gaggle.core.Boss;
import org.systemsbiology.gaggle.geese.common.GaggleConnectionListener;
import org.systemsbiology.gaggle.geese.common.GooseShutdownHook;
import org.systemsbiology.gaggle.geese.common.RmiGaggleConnector;
import org.systemsbiology.gaggle.util.MiscUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/** @author skillcoy */
public class GagglePlugin extends CytoscapePlugin implements PropertyChangeListener, GaggleConnectionListener,
    GooseListChangedListener
  {
  protected static CyLogger logger = CyLogger.getLogger(GagglePlugin.class);

  private boolean registered;
  private CyGoose defaultGoose;

  private static Boss gaggleBoss;
  private static boolean connectedToGaggle = false;
  private static GooseDialog gDialog;
  private static CyBroadcast broadcast;
  private static boolean pluginInitialized = false;

  private static String ORIGINAL_GOOSE_NAME;
  protected static String myGaggleName;
  // keeps track of all the network ids key = network id  value = goose
  private static HashMap<String, CyGoose> networkGeese;
  private static Set<String> species;

  public GagglePlugin()
    {
    // constructor gets called at load time and every time the toolbar is used
    if (pluginInitialized)
      return;

    Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);

    ORIGINAL_GOOSE_NAME = "Cytoscape" + " v." + new CytoscapeVersion().getFullVersion();
    myGaggleName = ORIGINAL_GOOSE_NAME;

    networkGeese = new HashMap<String, CyGoose>();
    gDialog = new GooseDialog();

    CytoPanel GooseCyPanel = Cytoscape.getDesktop().getCytoPanel(javax.swing.SwingConstants.WEST);
    GooseCyPanel.add("CyGoose", gDialog);
    //GooseCyPanel.add("CyGoose", null, gDialog, "Gaggle Goose");
    GooseCyPanel.setSelectedIndex(GooseCyPanel.indexOfComponent(gDialog));

    try
      {
      // this gives an initial goose that is cytoscape with a null network
      createDefaultGoose();
      gDialog.displayMessage("Connected To Gaggle Boss");
      registered = true;
      }
    catch (RemoteException re)
      {
      registered = false;
      this.gDialog.displayMessage("Not connected to Gaggle Boss");
      re.printStackTrace();
      }

    broadcast = new CyBroadcast(gDialog, gaggleBoss);
    this.addButtonActions();
    pluginInitialized = true;
    }


  private String getTargetGoose()
    {
    int targetGooseIndex = this.gDialog.getGooseChooser().getSelectedIndex();
    String targetGooseName = (String) this.gDialog.getGooseChooser().getSelectedItem();
    logger.info("Target index: " + targetGooseIndex + "  Target item: " + targetGooseName);
    return targetGooseName;
    }


  public void gooseListChanged(String[] gooseList)
    {
    logger.debug("gooseListChanged() " + gooseList.toString());
    if (connectedToGaggle)
      {
      if (gDialog.getGooseChooser() != null && defaultGoose != null)
        {
        logger.debug("how many geese are there? " + networkGeese.size());
        logger.debug("is defaultGoose null? " + (defaultGoose == null));
        MiscUtil.updateGooseChooser(gDialog.getGooseChooser(), defaultGoose.getName(), defaultGoose.getActiveGooseNames());
        gDialog.getGooseChooser().setSelectedIndex(0);
        }

      }
    }

  public void setConnected(boolean connected, Boss boss)
    {
    logger.debug("setConnected() " + boss.toString());
    gaggleBoss = boss;
    connectedToGaggle = connected;
    gDialog.setConnectButtonStatus(connected);
    if (connectedToGaggle)
      {
      gDialog.displayMessage("Connected To Gaggle Boss");
      if (gDialog.getGooseChooser() != null && defaultGoose != null)
        {
        Cytoscape.getDesktop().setTitle(defaultGoose.getName());
        MiscUtil.updateGooseChooser(gDialog.getGooseChooser(), defaultGoose.getName(), defaultGoose.getActiveGooseNames());
        gDialog.getGooseChooser().setSelectedIndex(0);
        }
      }
    else
      {
      gDialog.displayMessage("Not connected to Gaggle Boss");
      }
    }

  // creates the "null" network goose
  private void createDefaultGoose() throws RemoteException
    {
    logger.info("Creating default goose...");
    // this gives an initial goose that is cytoscape with a null network
    CyNetwork CurrentNet = Cytoscape.getNullNetwork();
    CurrentNet.setTitle(myGaggleName);
    defaultGoose = this.createNewGoose(CurrentNet);
    defaultGoose.addGooseListChangedListener(this);

    networkGeese.put(CurrentNet.getIdentifier(), defaultGoose);
    logger.info("size of active names: " + defaultGoose.getActiveGooseNames().length);
    for (String g : defaultGoose.getActiveGooseNames())
      logger.info(g);

    if (gDialog.getGooseChooser() == null)
      logger.info("goose dialog NULL before updating");

    MiscUtil.updateGooseChooser(gDialog.getGooseChooser(), defaultGoose.getName(), defaultGoose.getActiveGooseNames());
    gDialog.getGooseChooser().setSelectedIndex(0);

    Cytoscape.getDesktop().setTitle(defaultGoose.getName());
    }

  // Network created: create goose  Network destroyed: remove goose Network title changed: change the goose name
  public void propertyChange(PropertyChangeEvent Event)
    {
    // nothing has been registered, don't try to handle events
    if (!registered) return;

    if (Event.getPropertyName() == Cytoscape.NETWORK_TITLE_MODIFIED)
      { // change the goose name
      logger.info("===== EVENT " + Event.getPropertyName() + "======");
      try
        { // this allows the goose to work in 2.5 as well
        Class titleChange = Class.forName("cytoscape.CyNetworkTitleChange");

        cytoscape.CyNetworkTitleChange OldTitle = (cytoscape.CyNetworkTitleChange) Event.getOldValue();
        cytoscape.CyNetworkTitleChange NewTitle = (cytoscape.CyNetworkTitleChange) Event.getNewValue();

        // this should always be true but if somehow it ain't....
        if (!OldTitle.getNetworkIdentifier().equals(NewTitle.getNetworkIdentifier()))
          {
          logger.warn("ERROR: " + Cytoscape.NETWORK_TITLE_MODIFIED + " event does not refer to the same networks!");
          return;
          }
        else
          {
          CyGoose goose = this.networkGeese.get(NewTitle.getNetworkIdentifier());
          if (goose != null && !goose.getName().equals(NewTitle.getNetworkTitle()))
            {
            try
              {
              String NewGooseName = this.gaggleBoss.renameGoose(goose.getName(), NewTitle.getNetworkTitle());
              Cytoscape.getNetwork(goose.getNetworkId()).setTitle(NewGooseName);
              }
            catch (RemoteException re)
              {
              re.printStackTrace();
              }
            }
          }

        }
      catch (java.lang.ClassNotFoundException cnf)
        {
        logger.error("Caught a ClassNotFoundException for cytoscape.CyNetworkTitleChange");
        }
      }
    else if (Event.getPropertyName() == Cytoscape.NETWORK_CREATED)
      { // register a goose
      logger.info("==== Event " + Event.getPropertyName() + "====");
      String netId = Event.getNewValue().toString();
      CyNetwork net = Cytoscape.getNetwork(netId);

      try
        {
        CyGoose NewGoose = createNewGoose(net);
        networkGeese.put(net.getIdentifier(), NewGoose);
        MiscUtil.updateGooseChooser(gDialog.getGooseChooser(), "ADummyString", NewGoose.getActiveGooseNames());
        }
      catch (RemoteException E)
        {
        GagglePlugin.showDialogBox("Failed to create a new Goose for network " + net.getTitle(), "Error", JOptionPane.ERROR_MESSAGE);
        E.printStackTrace();
        }
      }
    else if (Event.getPropertyName() == Cytoscape.NETWORK_DESTROYED)
      { // remove a goose
      logger.info("==== Event " + Event.getPropertyName() + "====");
      String netId = Event.getNewValue().toString();
      CyNetwork net = Cytoscape.getNetwork(netId);

      String Name = "";
      try
        {
        CyGoose OldGoose = (CyGoose) networkGeese.get(net.getIdentifier());
        Name = OldGoose.getName();
        gaggleBoss.unregister(OldGoose.getName());
        UnicastRemoteObject.unexportObject(OldGoose, true);
        gDialog.getGooseChooser().removeItem(OldGoose.getName());
        }
      catch (RemoteException E)
        {
        GagglePlugin.showDialogBox("Failed to remove goose '" + Name + "' from Boss", "Warning", JOptionPane.WARNING_MESSAGE);
        E.printStackTrace();
        }
      }
    else if (Event.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED)
      {
      logger.info("=== Event " + Event.getPropertyName() + "===");
      CyGoose current = networkGeese.get(Cytoscape.getCurrentNetwork().getIdentifier());
      gDialog.setSpeciesText(current.getSpeciesName());
      }
    
    }

  public void onCytoscapeExit()
    {
    logger.info("GagglePlugin in onCytoscapeExit()");

    for (Iterator<String> it = networkGeese.keySet().iterator(); it.hasNext();)
      {
      String key = it.next();
      CyGoose goose = networkGeese.get(key);
      if (!goose.getNetworkId().equals("0"))
        {
        try
          {
          gaggleBoss.unregister(goose.getName());
          }
        catch (RemoteException e)
          {
          logger.info("Error disconnecting from gaggle, goose may have already been disconnected by user.");
          e.printStackTrace();
          }
        }
      }
    logger.info("leaving onCytoscapeExit()");
    }

  /*
  * action button
  */

  /*
  * Creates a new goose for the given network
  */

  private CyGoose createNewGoose(CyNetwork Network) throws RemoteException, IllegalArgumentException
    {
    logger.info("createNewGoose(): initial network name: " + Network.getTitle());
    CyGoose Goose = new CyGoose(gDialog);//, gaggleBoss);
    Goose.setNetworkId(Network.getIdentifier());
    Goose.setName(Network.getTitle());
    RmiGaggleConnector connector = new RmiGaggleConnector(Goose);
    connector.addListener(this);
    if (Network.getIdentifier().equals("0"))
      {
      new GooseShutdownHook(connector);
      }

    try
      {
      connector.connectToGaggle();
      }
    catch (Exception e)
      {
      throw new RemoteException(e.getMessage());
      }

    logger.info("goose name after registration: " + Goose.getName());
    logger.info("setting title on network " + Network.getIdentifier());
    Network.setTitle(Goose.getName());
    gaggleBoss = connector.getBoss();
    Goose.setBoss(gaggleBoss);

    return Goose;
    }


  private void addButtonActions()
    {
    logger.info("add button actions");
    // layouts
    java.util.Collection<CyLayoutAlgorithm> Layouts = CyLayouts.getAllLayouts();
    gDialog.getLayoutChooser().addItem("Default");
    for (CyLayoutAlgorithm current : Layouts)
      {
      gDialog.getLayoutChooser().addItem(current.getName());
      }

    gDialog.setSpeciesText(
        CytoscapeInit.getProperties().getProperty("defaultSpeciesName"));

    /* broadcast name list to other goose (geese) */
    gDialog.addButtonAction(GooseButton.LIST, new ActionListener()
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
    gDialog.addButtonAction(GooseButton.NETWORK, new ActionListener()
    {
    public void actionPerformed(ActionEvent event)
      {
      logger.info("Net action");
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
    gDialog.addButtonAction(GooseButton.MATRIX, new ActionListener()
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
    gDialog.addButtonAction(GooseButton.TUPLE, new ActionListener()
    {
    public void actionPerformed(ActionEvent event)
      {
      try
        {
        CyNetwork Network = Cytoscape.getCurrentNetwork();
        CyGoose Goose = networkGeese.get(Network.getIdentifier());
        String TargetGoose = getTargetGoose();

        broadcast.broadcastTuple(Goose, TargetGoose);
        }
      catch (Exception ex)
        {
        ex.printStackTrace();
        }
      }
    });
    this.connectAction();
    }

  private void connectAction()
    {
    logger.info("in connectAction");
    gDialog.addButtonAction(GooseButton.CONNECT, new ActionListener()
    {
    public void actionPerformed(ActionEvent event)
      {
      logger.info("in connectAction's listener");
      try
        {
        if (event.getActionCommand().equals("connect"))
          {
          // reconnect all geese
          for (Iterator<String> it = networkGeese.keySet().iterator(); it.hasNext();)
            {
            String gooseNetworkId = it.next();
            CyGoose goose = networkGeese.get(gooseNetworkId);
            String newName = gaggleBoss.register(goose);
            if (gooseNetworkId.equals("0"))
              {
              Cytoscape.getDesktop().setTitle(newName);
              }
            else
              {
              CyNetwork cyNet = Cytoscape.getNetwork(gooseNetworkId);
              logger.info("old name: " + cyNet.getTitle());
              cyNet.setTitle(newName);
              logger.info("new name is: " + cyNet.getIdentifier());
              }
            }
          gDialog.setConnectButtonStatus(true);
          }
        else if (event.getActionCommand().equals("disconnect"))
          {
          // disconnect all geese
          for (Iterator<String> it = networkGeese.keySet().iterator(); it.hasNext();)
            {
            String gooseNetworkId = it.next();
            CyGoose goose = networkGeese.get(gooseNetworkId);
            gaggleBoss.unregister(goose.getName());
            }
          gDialog.setConnectButtonStatus(false);
          myGaggleName = ORIGINAL_GOOSE_NAME;
          defaultGoose.setName(ORIGINAL_GOOSE_NAME);
          Cytoscape.getDesktop().setTitle(ORIGINAL_GOOSE_NAME);
          }

        }
      catch (Exception E)
        {  // TODO quit popping box up, add error message bar to goose panel
        gDialog.setConnectButtonStatus(false);
        gDialog.displayMessage("Failed to communicate with Gaggle");
//                    GagglePlugin.showDialogBox("Failed to communicate with Gaggle", "Error", JOptionPane.ERROR_MESSAGE);
        E.printStackTrace();
        }
      }
    });
    }


  public static void showDialogBox(String message, String title, int msgType)
    {
    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, title, msgType);
    }


  }
