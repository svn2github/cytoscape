#!/usr/bin/env python

import os, sys

import datetime
import os
import socket

hostname = socket.gethostname()
curr     = datetime.datetime.today()
t_stamp  = curr.strftime("%Y%m%d%H%M%S")
pid      = os.getpid()

fstamp   = "%s_%s-%s" % (hostname, pid, t_stamp)

""" This is machine-specific settings. """
add_path = [
    "/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python",
    "/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python/rs_Python_Pack",
    "/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python/rs_Python_Pack/General_Packages"
    ]
""" ---------------------------------- """


for path in add_path:
    if path not in sys.path:
        sys.path.append(path)

if 'PYTHON_RS_CONFIG' not in os.environ:
    os.environ['PYTHON_RS_CONFIG'] = '/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python/rs_Python_Config'
    os.environ['home'] = "/Users/rsaito"
    os.environ['HOME'] = "/Users/rsaito"

from WEB.CGI_BasicI import cgi_html_out
from Usefuls.Empty_check import notEmpty_cgi_form

import Usefuls.rsConfig
rsc_cellcyc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

import CellCyc_Packages.CellCyc_Path.Path_Expr_detail2 as Path_expr_detail
Node_None_Mark = '-'


import cgi
form = cgi.FieldStorage()

if notEmpty_cgi_form(form, "Start_Node"):
    start_node_name = form["Start_Node"].value.replace(" ", "")
else:
    start_node_name = "CyclinD"

if notEmpty_cgi_form(form, "Goal_Node"):
    goal_node_name = form['Goal_Node'].value.replace(" ", "")
else:
    goal_node_name = "E2F1_DP"

if notEmpty_cgi_form(form, "Mediate_Node"):
    mediate_node_name = form['Mediate_Node'].value.replace(" ", "")
else:
    mediate_node_name = Node_None_Mark

if notEmpty_cgi_form(form, "Extra_Steps"):
    extra_steps = int(form['Extra_Steps'].value) + 1
else:
    extra_steps = 3
    
if notEmpty_cgi_form(form, "Expr_Data"):
    expr_data = form['Expr_Data'].value.replace(" ", "")
else:
    expr_data = "Simons"

if notEmpty_cgi_form(form, "Judge_On"):
    judge_on_formula_str = form['Judge_On'].value.replace(" ", "")
else:
    judge_on_formula_str = None

if ("TS_Path_Count" in form
    and form[ "TS_Path_Count" ].value == "ON"):
    ts_path_count = True
else:
    ts_path_count = False

if mediate_node_name == Node_None_Mark:
    via_node_name = None
else:
    via_node_name = mediate_node_name

if judge_on_formula_str:
    judge_on_formula_o = judge_on_formula_str
else:
    judge_on_formula_o = "(Default)"


if expr_data == "Botstein":
    path_detail = Path_expr_detail.Path_Expr_detail_Botstein(start_node_name,
                                                             goal_node_name,
                                                             extra_steps,
                                                             via_node_name,
                                                             judge_on_formula_str)
else:
    path_detail = Path_expr_detail.Path_Expr_detail_Simons(start_node_name,
                                                           goal_node_name,
                                                           extra_steps,
                                                           via_node_name,
                                                           judge_on_formula_str)

exp_path            = path_detail.output_exp_path() # Pathway calculation with all node on.
path_detail_all_on  = path_detail.get_path_search()
goal_path_info      = path_detail_all_on.get_info_goal_path()
goal_path_each_info = path_detail_all_on.get_info_I()

if ts_path_count:
    count_path = path_detail.output_count_paths_each_node() # Previous pathway calculations stored in path_detail will be invalid.
else:
    count_path = "Calculation disabled."

result_file1 = fstamp + "_result1"
result_file2 = fstamp + "_result2"
result_file3 = fstamp + "_result3"
result_file_fpath1 = rsc_cellcyc.Cell_cyc_path_analysis_result_dir + result_file1
result_file_fpath2 = rsc_cellcyc.Cell_cyc_path_analysis_result_dir + result_file2
result_file_fpath3 = rsc_cellcyc.Cell_cyc_path_analysis_result_dir + result_file3

fw = open(result_file_fpath1, "w")
fw.write(goal_path_each_info)
fw.close()

fw = open(result_file_fpath2, "w")
fw.write(count_path)
fw.close()

fw = open(result_file_fpath3, "w")
fw.write(exp_path)
fw.close()

html = open(rsc_cellcyc.Cell_cyc_path_html_analysis_result2).read()
html = html.replace("[[StartNode]]", start_node_name)
html = html.replace("[[GoalNode]]",  goal_node_name)
html = html.replace("[[MediateNode]]", mediate_node_name)
html = html.replace("[[ExtraSteps]]",  `extra_steps - 1`)
html = html.replace("[[ExprData]]", expr_data)
html = html.replace("[[JudgeOnFormula]]",  judge_on_formula_o)
html = html.replace("[[Result1]]", result_file1)
html = html.replace("[[Result2]]", result_file2)
html = html.replace("[[Result3]]", result_file3)
print cgi_html_out(html)

# print fstamp

