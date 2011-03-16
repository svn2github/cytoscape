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
import CellCyc_Packages.CellCyc_Path.Path_Expr1 as Path_Expr
import CellCyc_Packages.CellCyc_Expr.Botstein1 as Botstein

from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex import CellCyc_Complex_Exp
from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2

# from GNUplot.GNUplot_xlabel1 import GNUplot

from Usefuls.Sort_AlphaNum import alphanum_sort_cmp

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

query_node = Node.Node_Factory().make(query_node_name)

query_nodes = [ query_node ]
next_step_info = []
count = 0
while count < 3:
    next_step_info.append("<U>Step %d from %s</U><p>" % (count + 1, query_node.get_node_name()))
    next_query_nodes = []
    for query_node in query_nodes:
        next_step_info.append("The next node after <B>%s</B>" % query_node.get_node_name())
        next_step_info.append("<UL>")

        next_step_info_each = []
        for node in graph1.destination_nodes(query_node):
            next_step_info_each.append("<LI>[%s] %s" %
                                       (graph1.get_edge_weight(query_node, node),
                                        node.get_node_name()))
        next_step_info_each.sort(alphanum_sort_cmp)
        next_step_info += next_step_info_each
        next_step_info.append("</UL>")
        next_step_info.append("<p>")
        next_query_nodes += graph1.destination_nodes(query_node)
    next_step_info.append("<hr>")
    query_nodes = list(set(next_query_nodes))
    count += 1

html_out = """<html>
<head>
<title>The next node along pathway</title>
</head>
<body>
%s
</body>
</html>
""" % "\n".join(next_step_info)

print cgi_html_out(html_out)




