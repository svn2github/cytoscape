import os
import javax.swing
import urllib

def choose ():
    currentDirectory = os.getcwd()
    chooser = javax.swing.JFileChooser (currentDirectory)
    status = chooser.showOpenDialog (javax.swing.JFrame ())
    choice = ''
    if (status == javax.swing.JFileChooser.APPROVE_OPTION):
        choice = chooser.getSelectedFile().getPath()
        return choice

# this will probably break if you are running as a web start, since
# urllib relies upon socket and socket is broken. Use the WebReader
# class instead?
def webRead (filename, 
  baseUrl = 'http://db.systemsbiology.net/cytoscape/projects/static/highSchool/scripts'):
    url = '%s/%s' % (baseUrl, filename)
    return urllib.urlopen(url).read()

def runWebCode (filename,
  baseUrl = 'http://db.systemsbiology.net/cytoscape/projects/static/highSchool/scripts'):
    text = webRead (filename,baseUrl)
    eval (compile (text,'<string>', 'exec'))
   

