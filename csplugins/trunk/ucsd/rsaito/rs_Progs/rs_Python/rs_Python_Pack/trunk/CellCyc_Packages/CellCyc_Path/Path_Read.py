#!/usr/bin/env python

from Data_Struct.Hash2 import Hash
from Graph_Packages.Graph.Graph1 import Graph

def node_connection(nodes_s, nodes_d, sep = "_"):
    
    conn = {}
    for node_s in nodes_s:
        elem_set_s = set(node_s.split(sep))
        dest_cands = []
        max_union_size = 0
        for node_d in nodes_d:
            elem_set_d = set(node_d.split(sep))
            elem_set_union = elem_set_s & elem_set_d
            if max_union_size < len(elem_set_union):
                max_union_size = len(elem_set_union)
                dest_cands = [ node_d ]
            elif max_union_size == len(elem_set_union):
                dest_cands.append(node_d)
        # if max_union_size == 0:
        #    continue
        for dest_node in dest_cands:
            if node_s not in conn:
                conn[ node_s ] = {}
            if node_d not in conn[ node_s ]:
                conn[ node_s ][ dest_node ] = []
            conn[ node_s ][ dest_node ].append(
                ",".join(nodes_s) + " -> " + ",".join(nodes_d)) 
    return conn

def node_connection_I(nodes_s, nodes_d, sep = "_"):
    
    conn = {}
    for node_s in nodes_s:
        elem_set_s = set(node_s.split(sep))
        dest_cands1 = []
        max_union_size = 0
        for node_d in nodes_d:
            elem_set_d = set(node_d.split(sep))
            elem_set_union = elem_set_s & elem_set_d
            if max_union_size < len(elem_set_union):
                max_union_size = len(elem_set_union)
                dest_cands1 = [ (node_d, elem_set_d) ]
            elif max_union_size == len(elem_set_union):
                dest_cands1.append((node_d, elem_set_d))
                
        # if max_union_size == 0:
        #    continue
        
        # print node_s, dest_cands1
        dest_cands = []
        max_cmplx_size = 0
        for cand1_info in dest_cands1:
            node_d, elem_set_d = cand1_info
            if len(elem_set_d) > max_cmplx_size:
                dest_cands = [ node_d ]
                max_cmplx_size = len(elem_set_d)
            elif len(elem_set_d) == max_cmplx_size:
                dest_cands.append(node_d)
        
        for dest_node in dest_cands:
            if node_s not in conn:
                conn[ node_s ] = {}
            if node_d not in conn[ node_s ]:
                conn[ node_s ][ dest_node ] = []
            conn[ node_s ][ dest_node ].append(
                ",".join(nodes_s) + " -> " + ",".join(nodes_d)) 
    return conn


def node_connection_II(nodes_s, nodes_d, sep = "_"):
    
    conn_eval = {}
    for node_s in nodes_s:
        elem_set_s = set(node_s.split(sep))
        for node_d in nodes_d:
            elem_set_d = set(node_d.split(sep))
            elem_set_union = elem_set_s & elem_set_d
            elem_set_union_size = len(elem_set_union)
            if elem_set_union_size in conn_eval:
                conn_eval[ elem_set_union_size ].append((node_s, node_d))
            else:
                conn_eval[ elem_set_union_size ] = [(node_s, node_d)]

    size_sorted = conn_eval.keys()
    size_sorted.sort()
    size_sorted.reverse()

    conn = {}
    node_s_used = {}
    node_d_used = {}
    for elem_set_union_size in size_sorted:
        reaction_added = False
        for reaction in conn_eval[ elem_set_union_size ]:
            node_s, node_d = reaction
            if (node_s not in node_s_used) and (node_d not in node_d_used):
                if node_s not in conn:
                    conn[ node_s ] = {}
                if node_d not in conn[ node_s ]:
                    conn[ node_s ][ node_d ] = []
                conn[ node_s ][ node_d ].append(
                    ",".join(nodes_s) + " -> " + ",".join(nodes_d)) 
                reaction_added = True
        if reaction_added:
            node_s_used[ node_s ] = ""
            node_d_used[ node_d ] = ""
        
    return conn


def read_cellcyc_path(pathfile):
    h = Hash("S")
    h.set_miscolumn_permit()
    h.read_file_hd(pathfile,
                   Key_cols_hd = ["Substrate 1", "Substrate 2" ],
                   Val_cols_hd = ["Product 1", "Product 2", "Product 3"])
    g = {}
    for nodes_s in h:
        nodes_d = h[ nodes_s ]
        for node_s in nodes_s.split("\t"):
            if (node_s == "" or node_s.isspace() or 
                (node_s.startswith('(') and node_s.endswith(')'))):
                # print "Omitting (S)", node_s
                continue
            for node_d in nodes_d.split("\t"):
                if (node_d == "" or node_d.isspace() or
                    (node_d.startswith('(') and node_d.endswith(')'))) :
                    # print "Omitting (D)", node_d
                    continue
                if node_s not in g:
                    g[ node_s ] = {}
                print "Node-S:", node_s, "Node-D:", node_d
                g[ node_s ][ node_d ] = ""
                
    ret_g = Graph()
    ret_g.read_dict(g)
    return ret_g

def read_cellcyc_path_II(pathfile):
    h = Hash("A")
    h.set_miscolumn_permit()
    h.read_file_hd(pathfile,
                   Key_cols_hd = ["Substrate 1", "Substrate 2" ],
                   Val_cols_hd = ["Product 1", "Product 2", "Product 3"])

    def omit_node_cond(node):
        if (node == "" or node.isspace() or 
            (node.startswith('(') and node.endswith(')'))):
            # print "Omitting (S)", node
            return True
        else:
            return False

    g = {}
    for nodes_s_str in h:
        nodes_d_strs = h[ nodes_s_str ]
        for nodes_d_str in nodes_d_strs:              
            nodes_s = []
            for node_s in nodes_s_str.split("\t"):
                if not omit_node_cond(node_s):
                    nodes_s.append(node_s)
    
            nodes_d = []
            for node_d in nodes_d_str.split("\t"):
                if not omit_node_cond(node_d):
                    nodes_d.append(node_d)
    
            g_sub = node_connection_I(nodes_s, nodes_d) ### <---
            for node_s in g_sub:
                if node_s not in g:
                    g[ node_s ] = {}
                for node_d in g_sub[ node_s ]:
                    if not node_d in g[ node_s ]:
                        g[ node_s ][ node_d ] = []
                    g[ node_s ][ node_d ] += g_sub[ node_s ][ node_d ]

    for node_s in g:
        for node_d in g[ node_s ]:
            g[ node_s ][ node_d ] = ";".join(g[ node_s ][ node_d ])
            """
            print "Node-S:", node_s
            print "Node-D:", node_d
            print "Weight:", g[ node_s ][ node_d ]
            print
            """

    ret_g = Graph()
    ret_g.read_dict(g)
    return ret_g

def read_cellcyc_path_II2(pathfile):
    h = Hash("A")
    h.set_miscolumn_permit()
    h.read_file_hd(pathfile,
                   Key_cols_hd = ["RID" ],
                   Val_cols_hd = ["Substrate 1", "Substrate 2",
                                  "Product 1", "Product 2", "Product 3"],
                   Fil_cols_hd = ["RID"])

    def omit_node_cond(node):
        if (node == "" or node.isspace() or 
            (node.startswith('(') and node.endswith(')'))):
            # print "Omitting (S)", node
            return True
        else:
            return False

    g = {}
    for rid in h:
        reaction_str_set = h[ rid ]
        for reaction_str in reaction_str_set:
            nodes_s_tmp = reaction_str.split("\t")[:2]
            nodes_d_tmp = reaction_str.split("\t")[2:5]
            
            nodes_s = []
            for node_s in nodes_s_tmp:
                if not omit_node_cond(node_s):
                    nodes_s.append(node_s)
    
            nodes_d = []
            for node_d in nodes_d_tmp:
                if not omit_node_cond(node_d):
                    nodes_d.append(node_d)
    
            g_sub = node_connection_I(nodes_s, nodes_d) ### <---
            for node_s in g_sub:
                if node_s not in g:
                    g[ node_s ] = {}
                for node_d in g_sub[ node_s ]:
                    if not node_d in g[ node_s ]:
                        g[ node_s ][ node_d ] = []
                    g[ node_s ][ node_d ].append(rid)

    for node_s in g:
        for node_d in g[ node_s ]:
            g[ node_s ][ node_d ] = ",".join(g[ node_s ][ node_d ])
            """
            print "Node-S:", node_s
            print "Node-D:", node_d
            print "Weight:", g[ node_s ][ node_d ]
            print
            """

    ret_g = Graph()
    ret_g.read_dict(g)
    return ret_g

if __name__ == "__main__":


    nodes_s = [ "A_B_C", "D_E_F", "G_H_I" ]
    nodes_d = [ "A_B_C_D_E", "D_E", "E_F_G_H" ]

    nodes_s = [ "Cdk4_CyclinD_p27", "Ink4a" ]
    nodes_d = [ "Cdk4_Ink4a", "p27", "CyclinD" ]

    nodes_s = [ "A_B_C", "D_E_F", "G_H_I" ]
    nodes_d = [ "A_B_C_D_E", "D_E", "A_B_C_E_F" ]

    nodes_s = [ "E2F1" ]
    nodes_d = [ "CyclinA" ]

    """
    conn = node_connection_I(nodes_s, nodes_d)
    for node_s in conn:
        for node_d in conn[node_s]:
            print "\t".join((node_s, node_d, ",".join(conn[node_s][node_d])))
    """

    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
    g = read_cellcyc_path_II2(rsc.Cell_cyc_path_mammal_descr6)
    # g.graph_info1()
    g.graph_display()    





                
                                  
