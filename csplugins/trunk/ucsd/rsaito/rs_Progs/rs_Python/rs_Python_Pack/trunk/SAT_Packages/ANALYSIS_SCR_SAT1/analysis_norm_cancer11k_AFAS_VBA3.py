#!/usr/bin/env python

import Usefuls.Excel_Col_Name as ECN

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

def output_macro(
    identifier = 5,
    annotation = 6,

    dt_sn_start = 48,
    dt_sn_end   = 52,
    dt_an_start = 53,
    dt_an_end   = 57,
    dt_sc_start = 58,
    dt_sc_end   = 62,
    dt_ac_start = 63,
    dt_ac_end   = 67,

    rd_sn_start = 68,
    rd_sn_end   = 72,
    rd_an_start = 73,
    rd_an_end   = 77,
    rd_sc_start = 78,
    rd_sc_end   = 82,
    rd_ac_start = 83,
    rd_ac_end   = 87,

    dt_s_start = 88,
    dt_s_end   = 92,
    dt_a_start = 93,
    dt_a_end   = 97,

    rd_s_start = 98,
    rd_s_end   = 102,
    rd_a_start = 103,
    rd_a_end   = 107
    ):

    macro_tmplt = open(rsc.VBAFile_analysis_cancer11k_AFAS3, "r").read()
    return macro_tmplt % (
        # Cancer_Sense_AFAS_dT               
                          
        dt_sn_start, dt_sn_end,
        dt_sc_start, dt_sc_end,
        dt_an_start, dt_an_end,
        dt_ac_start, dt_ac_end,

        dt_sn_start, dt_sn_end,
        dt_sc_start, dt_sc_end,
        dt_an_start, dt_an_end,
        dt_ac_start, dt_ac_end,

        ECN.col_num_to_alphabet(dt_sn_start),
        ECN.col_num_to_alphabet(dt_ac_end),

        identifier, annotation,
        identifier,

        # Cancer_Sense_AFAS_random

        rd_sn_start, rd_sn_end,
        rd_sc_start, rd_sc_end,
        rd_an_start, rd_an_end,
        rd_ac_start, rd_ac_end,

        rd_sn_start, rd_sn_end,
        rd_sc_start, rd_sc_end,
        rd_an_start, rd_an_end,
        rd_ac_start, rd_ac_end,

        ECN.col_num_to_alphabet(rd_sn_start),
        ECN.col_num_to_alphabet(rd_ac_end),

        identifier, annotation,
        identifier,

        # Cancer_Sense_AFAS_dT_random()
        
        dt_sn_start, dt_sn_end,
        dt_sc_start, dt_sc_end,
        rd_an_start, rd_an_end,
        rd_ac_start, rd_ac_end,

        dt_sn_start, dt_sn_end,
        dt_sc_start, dt_sc_end,
        rd_an_start, rd_an_end,
        rd_ac_start, rd_ac_end,

        ECN.col_num_to_alphabet(dt_sn_start),
        ECN.col_num_to_alphabet(rd_ac_end),

        identifier, annotation,
        identifier,
        
        # MultiNorm_Sense_AFAS_dT

        dt_s_start,
        dt_s_end,
        dt_a_start,
        dt_a_end,

        dt_s_start,
        dt_s_end,
        dt_a_start,
        dt_a_end,

        ECN.col_num_to_alphabet(dt_s_start),
        ECN.col_num_to_alphabet(dt_a_end),
        
        identifier, annotation,
        identifier,

        # MultiNorm_Sense_AFAS_random

        rd_s_start,
        rd_s_end,
        rd_a_start,
        rd_a_end,

        rd_s_start,
        rd_s_end,
        rd_a_start,
        rd_a_end,

        ECN.col_num_to_alphabet(rd_s_start),
        ECN.col_num_to_alphabet(rd_a_end),
        
        identifier, annotation,
        identifier
        )  

if __name__ == "__main__":
    
    print output_macro()


