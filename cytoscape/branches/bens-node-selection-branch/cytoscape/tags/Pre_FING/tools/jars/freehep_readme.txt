Jul.12.2004 (Gary Bader)
The FreeHEP library used for image export in Cytoscape requires the following files to
be packaged into the final Cytoscape jar file by 'ant release' to select which export types are available
in the export feature under the file menu:
javax.imageio.spi.ImageWriterSpi
org.freehep.util.export.ExportFileType

Currently, GIF and SWF options have been removed, since they don't work.  This may change in the next
version of the FreeHEP library (version 1.2.2 used here)