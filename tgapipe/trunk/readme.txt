Tumor Genome Analysis Pipeline 

This project will provide a set of tools for working with genomic
data.

--------------------
Configuration Files:
--------------------
This project relies on several configuration files:

The pipe.conf file specifies the installation directories of pipeline
components such as BWA, GATK or VarScan.


.my.cnf is expected to exist in the user's home directory and supply
information for accessing the MySQL database. 


The .bashrc file should include the following environment variables:
$CHASMDIR
$MAPDIR



---------
Software:
---------
The following software packages are used by the pipeline:


------------------------
External Code Libraries:
------------------------
pycbio
kent tools


----------
Databases:
----------
The pipeline depends on the following MySQL databases:

UCSC Genome Browser databases: hg18, hg19
SNVBox


