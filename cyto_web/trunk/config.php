<?php

# Master Configuration File for Cytoscape.org Web Site
# Author:  Ethan Cerami, MSKCC

# Base URLs for ISB and cBio
$isb_base = "http://db.systemsbiology.net:8080/cytoscape/download/";
$cbio_base = "http://www.cbio.mskcc.org/cytoscape/";

# Cytoscape 1.1 Release File Names
$cyto1_gz = "cytoscape-v1.1.1.tar.gz";
$cyto1_zip = "cytoscape-v1.1.1.zip";
$cyto1_src = "cytoscapeSource-v1.1.1.tar.gz";

# Cytoscape 2.0 Release File Names
$cyto2_gz = "cytoscape-v2.0ALPHA4.tar.gz";
$cyto2_zip = "cytoscape-v2.0ALPHA4.zip";
$cyto2_src = "cytoscapeSource-v1.1.1.tar.gz";

# URL for Cytoscape Install Anywhere
$cyto1_install_anywhere = $isb_base . "installer/installer1.1.1/install.htm";
$cyto2_install_anywhere = "ftp://baker.systemsbiology.net/pub/xmas/cytoscape2/cytoscape2_Build_Output/Web_Installers/install.htm";

# URL for Cytoscape 1.1 Zip/Tar.gz Files (West Coast)
$cyto1_gz_west= $isb_base  . $cyto1_gz;
$cyto1_zip_west= $isb_base . $cyto1_zip;

# URL for Cytoscape 1.1 Zip/Tar.gz Files (East Coast)
$cyto1_gz_east= $cbio_base . $cyto1_gz;
$cyto1_zip_east= $cbio_base . $cyto1_zip;

# URL for Cytoscape 1.1 Source
$cyto1_source_west= $isb_base . $cyto1_src;
$cyto1_source_east= $cbio_base . $cyto1_src;

# URL for Cytoscape 2.0 and Source
$cyto2_gz_east= $cbio_base . $cyto2_gz;
$cyto2_source_east= $cbio_base . $cyto2_src;
?>