import os
import javax.swing
import csplugins.isb.dtenenbaum.jython as jy

def choose ():
    currentDirectory = os.getcwd()
    chooser = javax.swing.JFileChooser (currentDirectory)
    status = chooser.showOpenDialog (javax.swing.JFrame ())
    choice = ''
    if (status == javax.swing.JFileChooser.APPROVE_OPTION):
        choice = chooser.getSelectedFile().getPath()
        return choice

def webRead (filename, 
  baseUrl = 'http://db.systemsbiology.net/cytoscape/jython/scripts'):
    url = '%s/%s' % (baseUrl, filename)
    webReader = jy.WebReader()
    return webReader.read(url)
    #return urllib.urlopen(url).read()

def runWebCode (filename,
  baseUrl = 'http://db.systemsbiology.net/cytoscape/jython/scripts'):
    text = webRead (filename,baseUrl)
    eval (compile (text,'<string>', 'exec'))
   

