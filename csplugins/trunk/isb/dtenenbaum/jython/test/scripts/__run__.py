# here we are in __run.py__
import util

def help():
    "Open a browser displaying a help URL."
    import cytoscape.util.OpenBrowser as ob
    print "Launching web browser with help page...."
    ob.openURL('http://db.systemsbiology.net/cytoscape/jython')

    