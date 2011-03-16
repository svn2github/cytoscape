#!/usr/bin/env python

import Data_Struct.Hash2 as Hash

from Usefuls.rsConfig import RSC_II

rsc = RSC_II("rsAntis_Config")

hepatic_dT = Hash.Hash("S")
hepatic_dT.read_file_hd(rsc.Human11k_Cancer_Hepatic_dT,
                        Key_cols_hd = [ "Transcript ID" ],
                        Val_cols_hd = [ "Hepatic_C12",
                                        "Hepatic_C16",
                                        "Hepatic_C20",
                                        "Hepatic_C5",
                                        "Hepatic_C6",
                                        "Hepatic_N12",
                                        "Hepatic_N16",
                                        "Hepatic_N20",
                                        "Hepatic_N5",
                                        "Hepatic_N6" ])
                                       
print hepatic_dT.all_data()

