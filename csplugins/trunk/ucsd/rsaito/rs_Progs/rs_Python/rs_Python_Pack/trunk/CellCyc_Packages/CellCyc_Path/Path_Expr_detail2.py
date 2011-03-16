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
                 judge_on_formula_str = None,
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
    
        self.judge_on_formula_str = judge_on_formula_str

        self.set_path_search()
    
    def set_path_search(self):
        
        self.path_search = Path_search.Path_Search(self.graph1, self.extra_steps)
        self.path_search.set_start_node(self.start_node)
        self.path_search.set_goal_node(self.goal_node)
    
    """
    def set_graph(self, igraph):
        self.graph1 = igraph
       """
        
    def get_path_search(self):
        return self.path_search
        
    def get_graph(self):
        return self.graph1
        
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
                if self.judge_on(node_name, exp_label):
                # exp_val = self.get_expval(node_name, exp_label)
                # if self.judge_on(exp_val):
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


    def output_nxgrf_measure_each_node(self, nxgrf_measure_func):

        import Usefuls.Table_maker

        node_names = self.get_path_search().get_graph().get_node_set().get_node_names()
        node_names.sort()
  
        tb = Usefuls.Table_maker.Table_row()
        path_values = {}
        
        for exp_label in self.exp_labels:
            on_nodes = {}
            for node_name in node_names:
                node = Node.Node_Factory().make(node_name)
                if self.judge_on(node_name, exp_label):
                # exp_val = self.get_expval(node_name, exp_label)
                # if self.judge_on(exp_val):
                    on_nodes[node] = True

            # self.set_path_search()
            # self.path_search.loop_steps_until_goal(goal_end_mode = False, on_nodes = on_nodes)
            # path_values[exp_label] = self.path_search.get_goal_path_node_count()
            
            # print exp_label, on_nodes.keys()
            
            nx = self.get_path_search().get_graph().get_networkx_digraph(on_nodes = on_nodes)
            # for s_node in nx:
            #     for d_node in nx[s_node]:
            #        print s_node, d_node, nx[s_node][d_node]
            
            path_values[exp_label] = nxgrf_measure_func(nx)
            # print closeness_centrality(nx)
            
            # E2F5_DP = Node.Node_Factory().make("E2F5_DP")
            # print len(on_nodes), self.path_search.get_goal_path_node_count()
            # print exp_label, ":", self.get_expval("E2F5_DP", exp_label), E2F5_DP in on_nodes, path_values[exp_label].get(E2F5_DP, "---")
                                                                                           
        for node_name in node_names:
            # node = Node.Node_Factory().make(node_name)
            tb.append("Node", node_name)
            for exp_label in self.exp_labels:
                path_count = path_values[exp_label].get(node_name, 0)
                tb.append(exp_label, `path_count`)
            tb.record()

        return tb.get_record_str()
    
    
    def output_nxgrf_measure_ZValue(self, nxgrf_measure_func):

        from Calc_Packages.Stats.StatsI import mean, sd

        shuffle_iteration = 50

        def lf_calc_av(lf_hash):
            lf_vals = lf_hash.values()
            if lf_vals:
                return(sum(lf_vals)/len(lf_vals))
            else:
                return None
                
        import Usefuls.Table_maker

        node_names = self.get_path_search().get_graph().get_node_set().get_node_names()
        node_names.sort()
  
        path_values = {}
        
        val = []
        for exp_label in self.exp_labels:
            on_nodes = {}
            for node_name in node_names:
                node = Node.Node_Factory().make(node_name)
                if self.judge_on(node_name, exp_label):
                    on_nodes[node] = True
            
            nx = self.get_path_search().get_graph().get_networkx_digraph(on_nodes = on_nodes)            
            path_values[exp_label] = nxgrf_measure_func(nx)
            path_val_av = lf_calc_av(path_values[exp_label])
            
            
            # print exp_label, "Real value:", path_val_av

            val_shuffle = []
            for i in range(shuffle_iteration):
                graph_sub      = self.get_graph().get_subgraph_I(on_nodes)
                graph_shuffle  = graph_sub.graph_shuffle_I().get_networkx_digraph()
                path_vals_shuf = nxgrf_measure_func(graph_shuffle)
                path_vals_shuf_av = lf_calc_av(path_vals_shuf)
                val_shuffle.append(path_vals_shuf_av)
                # print "Random", i
            val_mu = mean(val_shuffle)
            val_sd = sd(val_shuffle)
            
            print exp_label, path_val_av, val_mu, val_sd, (path_val_av - val_mu) / val_sd
            val.append((path_val_av - val_mu) / val_sd)

        return val



class Path_Expr_detail_Botstein(Path_Expr_detail):
    
    def set_cellcyc_cmplx_exp(self):
        
        from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex import CellCyc_Complex_Exp
        import CellCyc_Packages.CellCyc_Expr.Botstein_data_labels as Botstein        

        self.cellcyc_cmplx_exp = CellCyc_Complex_Exp(rsc_cellcyc.Botstein_expr,
                                                     rsc_geneinf.GeneInfo_hs,
                                                     rsc_cellcyc.Cell_cyc_Syno)
        
        self.exp_labels = Botstein.conditions_C

    def record_var(self, node_name, exp_label):
        
        if "_exp_h" not in vars(self):
            self._exp_h = {}
        
        if ("timeseries", node_name) not in self._exp_h:
            ts = []
            for label in self.exp_labels:
                ts.append(self.get_expval(node_name, label))
            self._exp_h[("timeseries", node_name)] = tuple(ts)

        if ("max", node_name) not in self._exp_h:
            self._exp_h[ ("max", node_name ) ] = max(self._exp_h[("timeseries", node_name)])

        if ("min", node_name) not in self._exp_h:
            self._exp_h[ ("min", node_name ) ] = min(self._exp_h[("timeseries", node_name)])

        if ("ave", node_name) not in self._exp_h:
            total = 0
            count = 0
            for expval in self._exp_h[("timeseries", node_name)]:
                if expval is not None:
                    total += expval
                    count += 1
                    
            if count > 0:
                self._exp_h[("ave", node_name )] = total / count
            else:
                self._exp_h[("ave", node_name )] = None
                
            """
            if node_name == "E2F5_DP_p_Rb_Cdk4_p_CyclinD":
                print node_name, total / count
                print ts
            """
             
        if ("var", node_name) not in self._exp_h:
            if self._exp_h[ ("ave", node_name ) ] is None:
                self._exp_h[("var", node_name )] = None
            else:
                total = 0
                count = 0
                ave   = self._exp_h[ ("ave", node_name ) ]
                for expval in self._exp_h[("timeseries", node_name)]:
                    if expval is not None:
                        total += (expval - ave)**2
                        count += 1
                self._exp_h[("var", node_name )] = total / count


    def judge_on(self, node_name, exp_label):
        
        exp = self.get_expval(node_name, exp_label)
        
        if exp is None:
            return True
        
        self.record_var(node_name, exp_label)
        
        max = self._exp_h[ ("max", node_name ) ]
        min = self._exp_h[ ("min", node_name ) ]
        ave = self._exp_h[ ("ave", node_name ) ]       
        var = self._exp_h[ ("var", node_name ) ] 

        if self.judge_on_formula_str is None:
            self.judge_on_formula_str = "exp > ave"
        
        if eval(self.judge_on_formula_str):
            return True
        else:   
            return False    
    
    
    def format_exp_labels(self):
        
        return map(lambda x:" %-4s " % x, self.exp_labels)

    def format_expl(self, node_name, exp_label):
        
        expval = self.get_expval(node_name, exp_label)
        
        if expval is None:
            judge = True
        else:
            judge  = self.judge_on(node_name, exp_label)
        
        if judge:
            judge_str = "*"
        else:
            judge_str = " "
        
        if expval is None:
            return "      "
        else:
            return "%+3.2f%c" % (expval, judge_str)
        

class Path_Expr_detail_Simons(Path_Expr_detail):
    
    def set_cellcyc_cmplx_exp(self):
        
        from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex_Calls1 import CellCyc_Complex_Calls
        import CellCyc_Packages.CellCyc_Expr.Simons_data_labels as Simons

        self.cellcyc_cmplx_exp = CellCyc_Complex_Calls(rsc_cellcyc.Simons_calls,
                                                       rsc_geneinf.GeneInfo_hs,
                                                       rsc_cellcyc.Cell_cyc_Syno_Calls)
        
        self.exp_labels = Simons.Serum_starvation_Thymidine_block_synchronization


    def judge_on(self, node_name, exp_label):
        
        # No implementation of judge_on_formula_str processing so far...
        
        exp = self.get_expval(node_name, exp_label) 
        # print expval
        if exp:
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


    path_detail = Path_Expr_detail_Botstein(start_node_name  = "CyclinD",
                                            goal_node_name   = "Cdc2_p2_CyclinB", # "E2F1_DP"
                                            extra_steps      = 2,
                                            via_node_name    = "E2F5_DP",
                                            judge_on_formula_str = "exp > -0.3")   

    """                                  
    path_detail = Path_Expr_detail_Simons(start_node_name = "CyclinD",
                                          goal_node_name  = "Cdc2_p2_CyclinB", # "E2F1_DP"
                                          extra_steps     = 2,
                                          via_node_name   = "E2F5_DP",
                                          judge_on_formula_str = None)
    """

    print path_detail.output_exp_path()

    # print path_detail.exp_labels
    
    print path_detail.output_count_paths_each_node()
    
    from networkx import in_degree_centrality, out_degree_centrality, betweenness_centrality, \
        closeness_centrality, eigenvector_centrality, average_clustering
    
    print
    print "--- Betweenness ---"
    print path_detail.output_nxgrf_measure_each_node(betweenness_centrality)

    





 








