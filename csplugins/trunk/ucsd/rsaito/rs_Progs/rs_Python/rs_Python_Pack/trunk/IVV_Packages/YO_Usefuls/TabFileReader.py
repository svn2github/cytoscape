class Tabfile:
       def __init__( self, filename ):
           self.filename = filename
           self.fh = open( self.filename, "r" )
       def readline( self ):
           line = self.fh.readline()
           if line == "":
               return False
           else:
               return line.split( "\t" )
       def __del__( self ):
           self.fh.close()