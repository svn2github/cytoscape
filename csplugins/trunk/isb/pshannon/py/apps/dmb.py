# dmb.py:  an app (stand-alone) version of the cytoscape DataMatrixBrowser
#---------------------------------------------------------------------------------
# RCSid = '$Revision: $   $Date: 2004/07/23 00:30:24 $'
#---------------------------------------------------------------------------------
from javax.swing import *
from java.awt import *
from java.io import File
from javax.swing.event import *

import os, sys

from cytoscape import *
from csplugins.isb.pshannon.dataMatrix import *
from csplugins.isb.pshannon.dataMatrix.gui import *
#---------------------------------------------------------------------------------
frame = JFrame ()
frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE)

reader = DataMatrixReaderFactory.createReader ('ratios.matrix')
reader.read ()
matrices = reader.get ()
browser = DataMatrixBrowser (matrices)
frame.getContentPane().add (browser)
frame.pack ()
frame.setVisible (1)
