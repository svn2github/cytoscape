#!/usr/bin/env python

import os

from Usefuls.Hash_recorder import Hash_recorder

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
import Graph_Packages.Graph.Path_search_simple1 as Path_search

from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2

import Usefuls.rsConfig
rsc_cellcyc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
rsc_geneinf = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

Node_None_Mark = '-'


class Path_Expr_detail:
    
    def __init__(self,
                 start_node_name = "CyclinD",
                 goal_node_name  = "Cdc2_p2_CyclinB", # "E2F1_DP"
                 extra_steps     = 2,
                 via_node_name   = None,
                 graph_file      = rsc_cellcyc.Cell_cyc_path_mammal_descr6):

        self.graph1 = read_cellcyc_path_II2(graph_file)        
        self.extra_steps = extra_steps
        
        self.start_node = Node.Node_Factory().make(start_node_name)
        self.goal_node  = Node.Node_Factory().make(goal_node_name)

        if via_node_name is None:
            self.via_node = None
        else:
            self.via_node = Node.Node_Factory().make(via_node_name)
            
        self.set_cellcyc_cmplx_exp() # Virtual. Should be implemented in sub-class.    
    
    
    def set_path_search(self):
        
        self.path_search = Path_search.Path_Search(self.graph1, self.extra_steps)
        self.path_search.set_start_node(self.start_node)
        self.path_search.set_goal_node(self.goal_node)
    
        
    def get_path_search(self):
        return self.path_search
        
    def get_expval(self, node_name, exp_label):
        
        if '_exp_complex' not in vars(self):        
            self._exp_complex = Hash_recorder(self.cellcyc_cmplx_exp.calc_exp_complex)
        
        return self._exp_complex.get(node_name, exp_label)


    def output_count_paths_each_node(self):

        import Usefuls.Table_maker

        node_names = self.get_path_search().get_graph().get_node_set().get_node_names()
        node_names.sort()
  
        tb = Usefuls.Table_maker.Table_row()
        path_counts = {}
        
        for exp_label in self.exp_labels:
            on_nodes = {}
            for node_name in node_names:
                node = Node.Node_Factory().make(node_name)
                exp_val = self.get_expval(node_name, exp_label)
                if self.judge_on(exp_val):
                    on_nodes[node] = True

            self.set_path_search()
            self.path_search.loop_steps_until_goal(goal_end_mode = False, on_nodes = on_nodes)
            path_counts[exp_label] = self.path_search.get_goal_path_node_count()
            
            # E2F5_DP = Node.Node_Factory().make("E2F5_DP")
            # print len(on_nodes), self.path_search.get_goal_path_node_count()
            # print exp_label, ":", self.get_expval("E2F5_DP", exp_label), E2F5_DP in on_nodes, path_counts[exp_label].get(E2F5_DP, "---")
                                                                                           
        for node_name in node_names:
            node = Node.Node_Factory().make(node_name)
            tb.append("Node", node_name)
            for exp_label in self.exp_labels:
                path_count = path_counts[exp_label].get(node, 0)
                tb.append(exp_label, `path_count`)
            tb.record()

        return tb.get_record_str()

    def output_exp_path(self, sep = " "):

        self.set_path_search()
        self.path_search.loop_steps_until_goal(goal_end_mode = False, on_nodes = None)
        goal_path_info = self.path_search.get_info_goal_path()
        path_count = 0
        
        ret = []        
        
        for path_len in goal_path_info:
            ret.append("*** Path length %s ***" % path_len)
            for path in goal_path_info[ path_len ]:
                if (self.via_node and
                    not path.check_node(self.via_node)):
                    continue
                ret.append("Path %s:" % path_count)
                ret.append("\t".join(("Cond", sep.join(self.format_exp_labels()))))
                pnode = None
                for node in path:
                    if pnode is None:
                        wt = ""
                    else:
                        wt = self.path_search.get_graph().get_edge_weight(pnode, node)
                    expl = []
                    for exp_label in self.exp_labels:
                        expl.append(self.format_expl(node.get_node_name(), exp_label))

                    ret.append("\t".join((wt, sep.join(expl), node.get_node_name())))
                    pnode = node
                path_count += 1
                ret.append("")
                
        return "\n".join(ret)
        

class Path_Expr_detail_Botstein(Path_Expr_detail):
    
    def set_cellcyc_cmplx_exp(self):
        
        from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex import CellCyc_Complex_Exp
        import CellCyc_Packages.CellCyc_Expr.Botstein_data_labels as Botstein        

        self.cellcyc_cmplx_exp = CellCyc_Complex_Exp(rsc_cellcyc.Botstein_expr,
                                                     rsc_geneinf.GeneInfo_hs,
                                                     rsc_cellcyc.Cell_cyc_Syno)
        
        self.exp_labels = Botstein.conditions_C

    def format_exp_labels(self):
        
        return map(lambda x:" %-4s" % x, self.exp_labels)


    def judge_on(self, expval):
        # print expval
        if expval >= -0.5:
            return True
        else:
            return False      


    def format_expl(self, node_name, exp_label):
        
        expval = self.get_expval(node_name, exp_label) 
        
        if expval is None:
            return "     "
        else:
            return "%+3.2f" % expval
        

class Path_Expr_detail_Simons(Path_Expr_detail):
    
    def set_cellcyc_cmplx_exp(self):
        
        from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex_Calls1 import CellCyc_Complex_Calls
        import CellCyc_Packages.CellCyc_Expr.Simons_data_labels as Simons

        self.cellcyc_cmplx_exp = CellCyc_Complex_Calls(rsc_cellcyc.Simons_calls,
                                                       rsc_geneinf.GeneInfo_hs,
                                                       rsc_cellcyc.Cell_cyc_Syno_Calls)
        
        self.exp_labels = Simons.Serum_starvation_Thymidine_block_synchronization


    def judge_on(self, expval):
        # print expval
        if expval:
            return True
        else:
            return False        

    def format_exp_labels(self):
        
        return map(lambda x:x[4:8], self.exp_labels)
    

    def format_expl(self, node_name, exp_label):
        
        expval = self.get_expval(node_name, exp_label)
        call = { True:  "O",
                 False: "-" }[ expval ]
        return "   %s" % call


if __name__ == "__main__":


    path_detail = Path_Expr_detail_Botstein(start_node_name = "CyclinD",
                                            goal_node_name  = "Cdc2_p2_CyclinB", # "E2F1_DP"
                                            extra_steps     = 2,
                                            via_node_name   = "E2F5_DP")

    """                                  
    path_detail = Path_Expr_detail_Simons(start_node_name = "CyclinD",
                                          goal_node_name  = "Cdc2_p2_CyclinB", # "E2F1_DP"
                                          extra_steps     = 2,
                                          via_node_name   = "E2F5_DP")
    """

    print path_detail.output_exp_path()

    # print path_detail.exp_labels
    
    print path_detail.output_count_paths_each_node()





 








