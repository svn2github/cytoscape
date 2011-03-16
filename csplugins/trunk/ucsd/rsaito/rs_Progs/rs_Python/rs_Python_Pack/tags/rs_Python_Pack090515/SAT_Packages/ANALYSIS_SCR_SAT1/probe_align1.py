#!/usr/bin/env python

from Seq_Packages.Homology.One2One_Ssearch1 import One2One_Ssearch, ss_output
from Data_Struct.Hash2 import Hash
from Usefuls.Table_maker import Table_row

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")


class one2one_ssearch(One2One_Ssearch):
    def exhaustive(self, *args, **kwargs):
        onc_onc, afas_onc = args
        tb = Table_row()

        for onc_id in onc_onc:
            ss = self.align(onc_id, onc_id[4:])
            ss_output(onc_id, onc_id[4:], ss, tb)

        for afas_id in afas_onc:
            onc_id = afas_onc[ afas_id ]
            ss = self.align(afas_id, onc_id[4:])
            ss_output(afas_id, onc_id[4:], ss, tb)
            

        
afas_onc = Hash("S")
afas_onc.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                      Key_cols_hd = ["Gene ID (AFAS)"],
                      Val_cols_hd = ["Gene ID (ONC)" ]
                        )

onc_onc = Hash("S")
onc_onc.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                     Key_cols_hd = ["Gene ID (ONC)"],
                     Val_cols_hd = ["Gene ID (ONC)" ]
                     )
  
ssearch = one2one_ssearch(rsc.Human11k_Cancer_ONC_AFAS_probes,
                          rsc.Human11k_Cancer_ONC_fna
                          )
ssearch.exhaustive(onc_onc, afas_onc)

