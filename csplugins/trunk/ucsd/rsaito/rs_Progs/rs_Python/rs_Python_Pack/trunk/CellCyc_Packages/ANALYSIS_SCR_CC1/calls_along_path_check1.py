#!/usr/bin/env python

import os

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
# import CellCyc_Packages.CellCyc_Path.Path_Expr1 as Path_Expr

from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex_Calls1 import CellCyc_Complex_Calls
from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2

from Usefuls.Table_maker import Table_row

from CellCyc_Packages.CellCyc_Expr.Simons_data_labels import *

import Usefuls.rsConfig
rsc_cellcyc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
rsc_geneinf = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

Node_None_Mark = '-'

graph1 = read_cellcyc_path_II2(rsc_cellcyc.Cell_cyc_path_mammal_descr6)

cellcyc_cmplx_exp = CellCyc_Complex_Calls(rsc_cellcyc.Simons_calls,
                                          rsc_geneinf.GeneInfo_hs,
                                          rsc_cellcyc.Cell_cyc_Syno_Calls)

node_set_names = graph1.get_node_set().get_node_names()

node_set_names2 = []
for node in node_set_names:
    node_set_names2 += node.split("_")
    
node_set_names = list(set(node_set_names2)) # Optionally comment this line
node_set_names.sort()

"""
for node_name in node_set_names:
    judge = cellcyc_cmplx_exp.calc_exp_complex(node_name, "X05_0345.CEL")
    print "Complex", node_name, "final judge:", judge
"""
 
tb = Table_row()
for node_name in node_set_names:
    tb.append("Node Name", node_name)
    syno_mode = cellcyc_cmplx_exp.get_calc_type(node_name)
    for exp_label in Serum_starvation_Thymidine_block_synchronization:
        if syno_mode == "IGNORE":
            call = "-"
        else:
            call = `cellcyc_cmplx_exp.calc_exp_complex(node_name, exp_label)`
        tb.append(exp_label, call)
    tb.output("\t")
    