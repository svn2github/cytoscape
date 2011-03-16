#!/usr/bin/env python

import os, sys

add_path = [
    "/home/rsaito/Work/Research/rs_Progs/rs_Python",
    "/home/rsaito/Work/Research/rs_Progs/rs_Python/rs_Python_Pack",
    "/home/rsaito/Work/Research/rs_Progs/rs_Python/rs_Python_Pack/General_Packages"
    ]

for path in add_path:
    if path not in sys.path:
        sys.path.append(path)

if 'PYTHON_RS_CONFIG' not in os.environ:
    os.environ['PYTHON_RS_CONFIG'] = '/home/rsaito/Work/Research/rs_Progs/rs_Python/rs_Python_Config'
    os.environ['home'] = "/home/rsaito"
    os.environ['HOME'] = "/home/rsaito"

from WEB.CGI_BasicI import cgi_html_out

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
import CellCyc_Packages.CellCyc_Path.Path_Expr1 as Path_Expr
import CellCyc_Packages.CellCyc_Expr.Botstein1 as Botstein

from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex import CellCyc_Complex_Exp
from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2

# from GNUplot.GNUplot_xlabel1 import GNUplot

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

rsc_geneinfo = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

# cellcyc_cmplx_exp = CellCyc_Complex_Exp(rsc.Botstein_expr,
#                                         rsc_geneinfo.GeneInfo_hs,
#                                         rsc.Cell_cyc_Syno)

graph1 = read_cellcyc_path_II2(rsc.Cell_cyc_path_html_loaded)

import cgi
form = cgi.FieldStorage()
if ("Query_Node" in form
    and form["Query_Node"].value != ""
    and (not form["Query_Node"].value.isspace())):
    query_node_name = form["Query_Node"].value
else:
    query_node_name = "CyclinD"


conditions = [ "C#0",
               "C#1",
               "C#2",
               "C#3",
               "C#4",
               "C#5",
               "C#6",
               "C#7",
               "C#8",
               "C#9",
               "C#10",
               "C#11",
               "C#12",
               "C#13",
               "C#14",
               "C#15",
               "C#16",
               "C#17",
               "C#18",
               "C#19",
               "C#20",
               "C#21",
               "C#22",
               "C#23",
               "C#24",
               "C#25",
               "C#26",
               "C#27",
               "C#28",
               "C#29",
               "C#30",
               "C#31",
               "C#32",
               "C#33",
               "C#34",
               "C#35",
               "C#36",
               "C#37",
               "C#38",
               "C#39",
               "C#40",
               "C#41",
               "C#42",
               "C#43",
               "C#44",
               "C#45",
               "C#46" ]


query_node = Node.Node_Factory().make(query_node_name)

next_step = []
for node in graph1.destination_nodes(query_node):
    next_step.append("<LI>[%s] %s" %
                     (graph1.get_edge_weight(query_node, node), node.get_node_name()))

html_out = """<html>
<head>
<title>The next node along pathway</title>
</head>
<body>
The next node after %s
<hr>
<UL>
%s
</UL>
</body>
</html>
""" % (query_node_name, "\n".join(next_step))

print cgi_html_out(html_out)
