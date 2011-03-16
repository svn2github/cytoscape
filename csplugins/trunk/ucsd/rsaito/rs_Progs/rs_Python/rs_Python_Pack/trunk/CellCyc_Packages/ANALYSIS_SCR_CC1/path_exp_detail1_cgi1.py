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

import Usefuls.rsConfig
rsc_cellcyc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

import CellCyc_Packages.CellCyc_Path.Path_Expr_detail1 as Path_expr_detail
Node_None_Mark = '-'


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
    
if ("Expr_Data" in form
    and form["Expr_Data"].value != ""
    and (not form["Expr_Data"].value.isspace())):
    expr_data = form['Expr_Data'].value.replace(" ", "")
else:
    expr_data = None

if ("TS_Path_Count" in form
    and form[ "TS_Path_Count" ].value == "ON"):
    ts_path_count = True
else:
    ts_path_count = False

if mediate_node_name == Node_None_Mark:
    via_node_name = None
else:
    via_node_name = mediate_node_name


if expr_data == "Botstein":
    path_detail = Path_expr_detail.Path_Expr_detail_Botstein(start_node_name,
                                                             goal_node_name,
                                                             extra_steps,
                                                             via_node_name)
else:
    path_detail = Path_expr_detail.Path_Expr_detail_Simons(start_node_name,
                                                           goal_node_name,
                                                           extra_steps,
                                                           via_node_name)

exp_path = path_detail.output_exp_path()
goal_path_info = path_detail.get_path_search().get_info_goal_path()

if ts_path_count:
    count_path = path_detail.output_count_paths_each_node()
else:
    count_path = "Calculation disabled."

fw = open(rsc_cellcyc.Cell_cyc_path_analysis_result1, "w")
fw.write(path_detail.get_path_search().get_info_I())
fw.close()

fw = open(rsc_cellcyc.Cell_cyc_path_analysis_result2, "w")
fw.write(count_path)
fw.close()

fw = open(rsc_cellcyc.Cell_cyc_path_analysis_result3, "w")
fw.write(exp_path)
fw.close()

html = open(rsc_cellcyc.Cell_cyc_path_html_analysis_result).read()
html = html.replace("[[StartNode]]", start_node_name)
html = html.replace("[[GoalNode]]",  goal_node_name)
html = html.replace("[[MediateNode]]", mediate_node_name)
html = html.replace("[[ExtraSteps]]",  `extra_steps - 1`)
print cgi_html_out(html)

