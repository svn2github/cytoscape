from org.rosuda.JRclient import *
R = Rconnection ()
#matrix0 = mb.getMatrices()[0]
#columnTitles = [x for x in matrix0.getColumnTitles ()]
#columnData = []
#for title in columnTitles:
#  column = [x for x in m.getColumn (title)]
#  columnData.append (column)
#
#print 'number of columns: %d' % len (columnData)

#--------------------------------------------------------------------------
def boxplot (*vectors):

  max = len (vectors)
  if (max == 0):
    return

  cmd = 'boxplot ('
  namesArgument = ', names=c('

  for i in range (max):
    tmpName = 'tmp%d' % i
    cmd += tmpName
    namesArgument += '"%s"' % columnTitles [i]
    if (i < (max - 1)):
      cmd += ',' 
      namesArgument += ','
    R.assign (tmpName, vectors [i])

  namesArgument += ')'
  cmd += namesArgument
  cmd += ')'

  print 'cmd: %s' % cmd

  R.voidEval (cmd)

#--------------------------------------------------------------------------
def getColumns (matrixNumber=mb.getCurrentTabAndTableIndex ()):

  matrix = mb.getMatrices()[matrixNumber]
  print 'getColumns on matrix number %d' % matrixNumber
  columnTitles = [x for x in matrix.getColumnTitles ()]
  print '  column count: %d' % len (columnTitles)
  columnArray = []
  for title in columnTitles:
    column = [x for x in matrix.getColumn (title)]
    print '   column height: %d' % len (column)
    columnArray.append (column)

  return columnArray

#--------------------------------------------------------------------------
def selectBySigma (sigmaThreshold = 1.0):
  """
  select all rows in the current matrix which have ANY column
  measurment greater than <sigmaThreshold>, where sigma
  is calculated independently for each column"
  """

  currentMatrixIndex = mb.getCurrentTabAndTableIndex ()
  print 'currentMatrixIndex: %d' % currentMatrixIndex
  columns = getColumns (currentMatrixIndex)
  geneNames = mb.getMatrices()[currentMatrixIndex].getRowTitles ()
  print 'number of geneNames: %d' % len (geneNames)
  genesAboveThreshold = []
  
  for c in range (len (columns)):
    column = columns [c]
    R.assign ('tmp', column)
    stats = [x for x in R.eval ('summary (tmp)').getContent ()]
    mean = stats [3]
    stdDev = R.eval ('sqrt (var (tmp))').getContent ()
    aboveThresholdCount = 0
    max = mean + (sigmaThreshold * stdDev)
    min = mean - (sigmaThreshold * stdDev)
    print 'column height: %d' % len (column)
    for i in range (len (column)):
      value = column [i]
      if (value <= min or value >= max):
        aboveThresholdCount += 1
        name = geneNames [i]
        if (not name in genesAboveThreshold):
          genesAboveThreshold.append (name)
    print '%d: %d   %f +/- %f  count: %d' % (c, len (columns [c]), mean, stdDev, aboveThresholdCount)
      

  print 'above threshold: %s' % genesAboveThreshold
  mb.selectRowsByName (genesAboveThreshold)


#--------------------------------------------------------------------------
def stats (vector):
  R.assign ('tmp', vector)
  result = [x for x in R.eval ('summary (tmp)').getContent ()]
  stdDev = R.eval ('sqrt (var (tmp))').getContent ()
  s  = '       count: %d\n'  % len (vector)   
  s += '         min: %f\n'  % result [0]
  s += '1st quartile: %f\n'  % result [1]
  s += '      median: %f\n'  % result [2]
  s += '        mean: %f\n'  % result [3]
  s += '3rd quartile: %f\n'  % result [4]
  s += '         max: %f\n'  % result [5]
  s += '       sigma: %f\n'  % stdDev
  print s
  
#--------------------------------------------------------------------------
R = Rconnection ()
columns = getColumns ()
