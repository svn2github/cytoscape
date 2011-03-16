#!/usr/bin/env python

import sys

# from General_Packages.Data_Struct.Hash2 import Hash

from Expr_Packages.Expr_II.Expression1 import *
from Expr_Packages.Expr_II.Probe1 import *
from Expr_Packages.Expr_II.Transcript1 import *
from SAT_Packages.SAT.SAT1 import *

import Expr_Packages.Expr_II.Transcript1 as Transcript
import Expr_Packages.Expr_II.Probe1 as Probe

import Usefuls.rsConfig
rsc_antis = Usefuls.rsConfig.RSC_II("rsAntis_Config")

def read_human11k_dT():

    probeid_label = ("Probe Name",)
    transcriptid_label = ("Transcript ID", )
    probeseq_label = ("Probe Sequence", )
    tissues = ("Brain", "Fibroblast", "Heart", "Liver", "Testis")

    expr_pat_set = Expression_Data()
    expr_pat_set.read_expression_data_from_file(rsc_antis.human11k_dT,
                                                probeid_label,
                                                tissues)

    Probe_Factory().read_probe_info_from_file(rsc_antis.human11k_dT,
                                              probeid_label,
                                              probeseq_label,
                                              transcriptid_label)
    Transcript_Factory().read_probe_info_from_file(rsc_antis.human11k_dT,
                                                   probeid_label,
                                                   transcriptid_label,
                                                   expr_pat_set)

    sat_set = SAT_Set()
    sat_set.read_SAT_info_from_file_version11k(rsc_antis.human11k_okay)

    return expr_pat_set, sat_set

def read_human11k_random():

    probeid_label = ("Probe Name",)
    transcriptid_label = ("Transcript ID", )
    probeseq_label = ("Probe Sequence", )
    tissues = ("Brain", "Fibroblast", "Heart", "Liver", "Testis")

    expr_pat_set = Expression_Data()
    expr_pat_set.read_expression_data_from_file(rsc_antis.human11k_random,
                                                probeid_label,
                                                tissues)

    Probe_Factory().read_probe_info_from_file(rsc_antis.human11k_random,
                                              probeid_label,
                                              probeseq_label,
                                              transcriptid_label)
    Transcript_Factory().read_probe_info_from_file(rsc_antis.human11k_random,
                                                   probeid_label,
                                                   transcriptid_label,
                                                   expr_pat_set)

    sat_set = SAT_Set()
    sat_set.read_SAT_info_from_file_version11k(rsc_antis.human11k_okay)

    return expr_pat_set, sat_set

def read_mouse11k_random():

    probeid_label = ("Probe Name",)
    transcriptid_label = ("Transcript ID", )
    probeseq_label = ("Probe Sequence", )
    tissues = (
        # "3T3", fibroblast cell-line
        # "3T3_cyto", RNA from cytoplasm
        # "3T3_nuc",  RNA from nucleus
        "Bowel",
        "Brain",
        "BreastCancerSoft",
        "BreastCancerSolid",
        "Heart",
        # "IVFembryo9.5", In vitro fertilization 9.5 days
        "Kidney",
        "Liver",
        "Lung",
        "MammaryGrand",
        # "Null-G5",
        # "Partheno",
        # "Pat_P14",
        "Placenta",
        # "SL10",
        # "Sib_P26",
        "Skeletal_muscle",
        "Spleen",
        "Stomach",
        "Testis",
        # "Testis-fraction#2",
        # "Testis-fraction#6",
        # "Testis-fraction#9",
        "Thymus",
        # "Wtp27"
        )

    expr_pat_set = Expression_Data()
    expr_pat_set.read_expression_data_from_file(rsc_antis.mouse11k_random,
                                                probeid_label,
                                                tissues)

    Probe_Factory().read_probe_info_from_file(rsc_antis.mouse11k_random,
                                              probeid_label,
                                              probeseq_label,
                                              transcriptid_label)
    Transcript_Factory().read_probe_info_from_file(rsc_antis.mouse11k_random,
                                                   probeid_label,
                                                   transcriptid_label,
                                                   expr_pat_set)

    sat_set = SAT_Set()
    sat_set.read_SAT_info_from_file_version11k(rsc_antis.mouse11k_okay)

    return expr_pat_set, sat_set


def read_mouse11k_nerve_dT():
    probeid_label = ("Probe Name",)
    transcriptid_label = ("Transcript ID", )
    probeseq_label = ("Probe Sequence", )
    tissues = ("D0", "D2", "D4", "D6", "D8")

    expr_pat_set = Expression_Data()
    expr_pat_set.read_expression_data_from_file(rsc_antis.mouse11k_dT,
                                                probeid_label,
                                                tissues)

    Probe_Factory().read_probe_info_from_file(rsc_antis.mouse11k_dT,
                                              probeid_label,
                                              probeseq_label,
                                              transcriptid_label)
    Transcript_Factory().read_probe_info_from_file(rsc_antis.mouse11k_dT,
                                                   probeid_label,
                                                   transcriptid_label,
                                                   expr_pat_set)

    sat_set = SAT_Set()
    sat_set.read_SAT_info_from_file_version11k(rsc_antis.mouse11k_okay)

    return expr_pat_set, sat_set



def read_mouse11k_nerve_random():
    probeid_label = ("Probe Name",)
    transcriptid_label = ("Transcript ID", )
    probeseq_label = ("Probe Sequence", )
    tissues = ("D0", "D2", "D4", "D6", "D8")

    expr_pat_set = Expression_Data()
    expr_pat_set.read_expression_data_from_file(rsc_antis.mouse11k_random,
                                                probeid_label,
                                                tissues)

    Probe_Factory().read_probe_info_from_file(rsc_antis.mouse11k_random,
                                              probeid_label,
                                              probeseq_label,
                                              transcriptid_label)
    Transcript_Factory().read_probe_info_from_file(rsc_antis.mouse11k_random,
                                                   probeid_label,
                                                   transcriptid_label,
                                                   expr_pat_set)

    sat_set = SAT_Set()
    sat_set.read_SAT_info_from_file_version11k(rsc_antis.mouse11k_okay)

    return expr_pat_set, sat_set


if __name__ == "__main__":
    import SAT_Packages.SAT.SAT_gnuplot1 as SAT_gnuplot

    expr_pat_set, sat_set = read_human11k_dT()
    probe = expr_pat_set.probes()[0]
    print probe.get_probeid(),
    print probe.get_sequence(),
    print probe.get_transcript().get_transcriptID()
    print probe.get_transcript().get_probes()[0].get_probeid()
    print expr_pat_set.expression_pat(probe)
    print expr_pat_set.conditions()

    expr_pat_set, sat_set = read_mouse11k_nerve_dT()
    probe = expr_pat_set.probes()[0]
    print probe.get_probeid(),
    print probe.get_sequence(),
    print probe.get_transcript().get_transcriptID()
    print probe.get_transcript().get_probes()[0].get_probeid()
    print expr_pat_set.expression_pat(probe)
    print expr_pat_set.conditions()

    sat = sat_set.get_sats()[0]
    sat_plot = SAT_gnuplot.SAT_gnuplot()
    sat_plot.import_sat(sat)
    sat_plot.gnuplot_line()
    print sat.get_info("overlap length")

