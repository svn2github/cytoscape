require 'java'

include_class 'cytoscape.Cytoscape'
include_class 'cytoscape.plugin.CytoscapePlugin'
include_class 'cytoscape.view.CyMenus'
include_class 'cytoscape.util.CytoscapeAction'

include_class 'java.awt.event.ActionEvent'
include_class 'javax.swing.JOptionPane'

class RubyAction < CytoscapeAction
  def initialize()
    super("Plugin written in Ruby")
    setPreferredMenu("Plugins")
  end

  def actionPerformed(evt)
    JOptionPane.showMessageDialog(
    nil, "Hello Ruby world", "JRuby on Cytoscape",
    JOptionPane::INFORMATION_MESSAGE)
  end
end

class RubyPlugin < CytoscapePlugin
  def register_menu
    cyMenus = Cytoscape.getDesktop().getCyMenus()
    cyMenus.addAction(RubyAction.new)
  end
end

RubyPlugin.new.register_menu()