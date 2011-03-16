#!/usr/bin/env python

# This module uses many global variables.
# This module should be imported by "import",
# rather than by "from"

import sys

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

from Seq_Packages.Homology.Homol_measure import HM
import IVV_Packages.PPI_Pred.PPIPred6 as PPIPred

def calc_ppipred(bait_hm = HM(1.0e-3),
                 prey_hm = HM(1.0e-1, 0.7, 0, 10),
                 mode = "S",
                 reprod = 1):
    
    global ppipred
    
    if not 'ppipred' in globals():
        
        sys.stderr.write("IVV -> Gene Calculation ... Bait:%s Prey:%s\n" 
                         % (bait_hm.__repr__(), prey_hm.__repr__()))
        
        ppipred = PPIPred.PPIPred_Result(bait_hm, prey_hm,
                                         mode, reprod)

    return ppipred

def get_ppi_pred():

    global ppipred

    return ppipred

if __name__ == "__main__":

    ppipred_tmp = calc_ppipred()
