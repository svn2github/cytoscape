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

def webRead (filename, 
  baseUrl = 'http://db.systemsbiology.net/cytoscape/projects/static/highSchool/scripts'):
    # assumes baseUrl doesn't end with a slash, 
    # seems to work even if it does.
    url = '%s/%s' % (baseUrl, filename)
    return urllib.urlopen(url).read()

def runWebCode (filename,
  baseUrl = 'http://db.systemsbiology.net/cytoscape/projects/static/highSchool/scripts'):
    text = webRead (filename,baseUrl)
    eval (compile (text,'<string>', 'exec'))
   

# TODO - add convenience methods to get common cy2 objects

if __name__ == '__main__':
    print "# in unit testing code for util.py"
    #add unit testing code here
