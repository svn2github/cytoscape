#!/usr/bin/env python

from CellCyc_Packages.CellCyc_Path.Path_Expr_detail2 import Path_Expr_detail_Botstein, Path_Expr_detail_Simons
from networkx import in_degree_centrality, out_degree_centrality, betweenness_centrality, \
    closeness_centrality, eigenvector_centrality, average_clustering

path_detail = Path_Expr_detail_Botstein(start_node_name  = "CyclinD",
                                        goal_node_name   = "Cdc2_p2_CyclinB", # "E2F1_DP"
                                        extra_steps      = 2,
                                        via_node_name    = "E2F5_DP",
                                        judge_on_formula_str = "exp > -0.3") 

# print "--- Betweenness ---"
# print path_detail.output_nxgrf_measure_each_node(betweenness_centrality)

print "Now beginning to calculate Z-values."

#res = path_detail.output_nxgrf_measure_ZValue(betweenness_centrality)
res = path_detail.output_nxgrf_measure_ZValue(in_degree_centrality)

for v in res:
    print v
    
    