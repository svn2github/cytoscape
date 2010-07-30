package cytoscape.actions;

import cytoscape.util.CytoscapeAction;
import cytoscape.dialogs.logger.LoggerDialog;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA. User: skillcoy Date: May 8, 2008 Time: 9:34:03 AM To change this template use File |
 * Settings | File Templates.
 */
public class LoggerAction extends CytoscapeAction
  {
  public LoggerAction() {
    super("Error Console");
    this.setPreferredMenu("Help");
  }

    @Override
    public void actionPerformed(ActionEvent e) {
      LoggerDialog.getLoggerDialog().setVisible(true);
    }
  }
