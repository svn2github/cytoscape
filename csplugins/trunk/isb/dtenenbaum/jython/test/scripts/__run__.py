# here we are in __run.py__
import util

def help():
    "Open a browser displaying a help URL."
    import cytoscape.util.OpenBrowser as ob
    ob.openURL('http://db.systemsbiology.net/cytoscape/jython')

    