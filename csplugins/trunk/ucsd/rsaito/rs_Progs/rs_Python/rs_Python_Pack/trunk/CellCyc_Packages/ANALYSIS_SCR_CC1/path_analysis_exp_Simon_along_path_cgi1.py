#!/usr/bin/env python

import os, sys

add_path = [
    "/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python",
    "/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python/rs_Python_Pack",
    "/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python/rs_Python_Pack/General_Packages"
    ]

for path in add_path:
    if path not in sys.path:
        sys.path.append(path)

if 'PYTHON_RS_CONFIG' not in os.environ:
    os.environ['PYTHON_RS_CONFIG'] = '/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python/rs_Python_Config'
    os.environ['home'] = "/Users/rsaito"
    os.environ['HOME'] = "/Users/rsaito"

from WEB.CGI_BasicI import cgi_html_out


import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
import CellCyc_Packages.CellCyc_Path.Path_Expr_simple1 as Path_search

from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex_Calls1 import CellCyc_Complex_Calls
from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2

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
node_set_names.sort()

import cgi
form = cgi.FieldStorage()
if ("Start_Node" in form
    and form["Start_Node"].value != ""
    and (not form["Start_Node"].value.isspace())):
    start_node_name = form["Start_Node"].value.replace(" ", "")
else:
    start_node_name = "CyclinD"
if ("Goal_Node" in form
    and form["Goal_Node"].value != ""
    and (not form["Goal_Node"].value.isspace())):
    goal_node_name = form['Goal_Node'].value.replace(" ", "")
else:
    goal_node_name = "E2F1_DP"

if ("Mediate_Node" in form
    and form["Mediate_Node"].value != ""
    and (not form["Mediate_Node"].value.isspace())):
    mediate_node_name = form['Mediate_Node'].value.replace(" ", "")
else:
    mediate_node_name = Node_None_Mark

if ("Extra_Steps" in form
    and form["Extra_Steps"].value != ""
    and (not form["Extra_Steps"].value.isspace())):
    extra_steps = int(form['Extra_Steps'].value) + 1
else:
    extra_steps = 3
    

start_node = Node.Node_Factory().make(start_node_name)
goal_node = Node.Node_Factory().make(goal_node_name)

path_search_calls = Path_search.Path_Search_expr(graph1, extra_steps)
path_search_calls.set_start_node(start_node)
path_search_calls.set_goal_node(goal_node)

path_search_calls.loop_steps_until_goal(goal_end_mode = False, on_nodes = None)

goal_path_info = path_search_calls.get_info_goal_path()
path_count = 0

if mediate_node_name == Node_None_Mark:
    via_node_name = None
else:
    via_node_name = mediate_node_name

fw = open(rsc_cellcyc.Cell_cyc_path_analysis_result1, "w")
fw.write(path_search_calls.get_info_I())
fw.close()

fw = open(rsc_cellcyc.Cell_cyc_path_analysis_result3, "w")
fw.write(path_search_calls.\
        get_nodes_info_goal_path_II_str_exp_Simon(cellcyc_cmplx_exp.calc_exp_complex,
                                                  Serum_starvation_Thymidine_block_synchronization,
                                                  via_node_name))
fw.close()


html = open(rsc_cellcyc.Cell_cyc_path_html_analysis_result).read()
html = html.replace("[[StartNode]]", start_node_name)
html = html.replace("[[GoalNode]]",  goal_node_name)
html = html.replace("[[MediateNode]]", mediate_node_name)
html = html.replace("[[ExtraSteps]]",  `extra_steps - 1`)
print cgi_html_out(html)

 