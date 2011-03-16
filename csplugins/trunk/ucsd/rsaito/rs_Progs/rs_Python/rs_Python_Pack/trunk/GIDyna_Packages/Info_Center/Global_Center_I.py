#!/usr/bin/env python

'''
Created on Mar 10, 2011

@author: rsaito
'''

import sys
import shelve

import Usefuls.rsConfig

rsc_ncbi = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

import BioData_Packages.Gene.NCBI_Synonym


def get_NCBI_syno():

    global ncbi_syno

    shelve_filename = rsc_ncbi.GeneInfo + "_syno.shelve"

    if not 'ncbi_syno' in globals():
        sys.stderr.write("Reading NCBI Synonym information...\n")
        

        ncbi_syno = BioData_Packages.\
            Gene.NCBI_Synonym.NCBI_Gene_Synonyms(rsc_ncbi.GeneInfo, case_mode = False)
        
        d = shelve.open(shelve_filename)
        d[ "Synonyms" ] = ncbi_syno
        d.close()

    return ncbi_syno


if __name__ == "__main__":
    syno = get_NCBI_syno()
    
