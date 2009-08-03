/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cytoscape.util.plugins.communication;

import junit.framework.TestCase;

/**
 *
 * @author gjj
 */
public class Test extends TestCase {

    public void testComm() {
        PluginsCommunicationSupport.addMessageListener("receiver1", new MessageListener() {
            public void messagedReceived(Message msg) {
                javax.swing.JOptionPane.showMessageDialog(null, "bingo");
            }
        });

        PluginsCommunicationSupport.sendMessage(null,"sender1", "receiver1",null);
    }
}
