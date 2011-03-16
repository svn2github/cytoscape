#!/usr/bin/env python

import Graph1
import Node1

import Calc_Packages.Math.MathI as Math
from Path1 import Path


class Node_Mark_simple:
    """ Pathway lengths which reaches given node.
    Path redundancies are not checked """

    def __init__(self, node, limit):

        self.node_mark_simple = []
        self.limit = limit
        self.node = node

    def mark(self, path):
        """ Registers pathway lengths in self.node_mark_simple.
        Path length must NOT be shorter than
        already registered paths in the node_mark.
        Format of self.node_mark_simple:
        [ [path_length1, count1], [path_length2, count2], [path_length3, count3] ... ]
        where path_length1 < path_length2 < path_length3.
        """

        assert path.last_node() is self.node, "Node mismatch."

        if self.get_limit() <= 0:
            return False

        if self.node_mark_simple == []:
            # First pathway
            self.node_mark_simple.append([ len(path), 1 ])
            return True

        else:
            current_rank     = self.get_current_rank()
            current_path_len = self.node_mark_simple[-1][0]

            assert current_path_len <= len(path), "Path warp error ..."

            if current_path_len == len(path):
                """  Adding path having same length as
                those having longest path registered in
                node_mark """
                self.node_mark_simple[-1][1] += 1
                return True

            """ From here, whether to add longer pathways
            has to be decided. """

            assert current_rank <= self.get_limit(), "Node mark malfunction ..."
                       
            if current_rank == self.get_limit():
                return False
            else:
                """ self.under_limit() condition holds. Adding longer pathway """
                self.node_mark_simple.append([ len(path), 1 ])
                return True


    def get_num_pathways(self, k = 99999999):
        """ Pathways within k-th length """
        
        ct = 0
        ret = 0
        for path_len in self.node_mark_simple:
            ret += path_len[1]
            ct += 1
            if ct >= k:
                break
        return ret


    def get_limit(self):
        return self.limit

    def get_current_rank(self):
        return len(self.node_mark_simple)

    def under_limit(self):
        """ Judges whether the mark can accept one more extra pathway. """
        return self.get_current_rank() < self.get_limit()

    def longest_path_len(self):
        if self.node_mark_simple == []:
            return 0
        else:
            return self.node_mark_simple[-1][0]

    def __str__(self):
        out = map(lambda x: "PLen %d:%d" % (x[0], x[1]), self.node_mark_simple)
        return "[ %s ] %s" % (self.node.get_node_name(),  ", ".join(out))


class Node_Mark_Set:
    def __init__(self, limit):
        self.node_mark_set = {}
        self.limit = limit

    def get_node_set(self):
        return self.node_mark_set.keys()

    def get_node_mark(self, node):
        if node in self.node_mark_set:
            return self.node_mark_set[ node ]
        else:
            return None

    def mark(self, node, path):
        if node not in self.node_mark_set:
            self.node_mark_set[ node ] = Node_Mark_simple(node, self.limit)
        return self.node_mark_set[ node ].mark(path)

    def display_node_marks(self):
        print "***** Node Marks *****"
        for node in self.node_mark_set:
            # print "--- " + node.get_node_name() + " ---"
            print self.node_mark_set[ node ]
        print

    def __iter__(self):
        return self.node_mark_set.keys().__iter__()


class Path_Search:
    def __init__(self, graph, limit):

        self.graph = graph
        self.limit = limit
        self._path_set_growing = []
        self._path_set_determ  = []
        self.node_mark_set = Node_Mark_Set(limit)
        self.start_node = None
        self.goal_node = None

    def get_graph(self):

        return self.graph

    def set_start_node(self, node):

        assert self.start_node is None, "Start node re-definition..."

        self.start_node = node
        path = Path(self.graph)
        path.add_node(node)
        self._path_set_growing = [ path ]
        self.node_mark_set.mark(node, path)

    def set_goal_node(self, node):

        assert self.goal_node is None, "Goal node re-definition..."
        self.goal_node = node

    def one_step(self, goal_end_mode = False, on_nodes = None):

        new_path_set = []

        for path in self._path_set_growing:
            for node in path.next_nodes():
                if ((on_nodes is None or node in on_nodes) and
                    (goal_end_mode is False or not path.check_node(self.goal_node))):
                    new_path = path.copy()
                    new_path.add_node(node)
                    if self.node_mark_set.mark(node, new_path):
                        new_path_set.append(new_path)
                        if new_path.last_node() is self.goal_node:
                            self._path_set_determ.append(new_path)

        self._path_set_growing = new_path_set

    def loop_steps(self):

        while self._path_set_growing != []:
            self.one_step()


    def loop_steps_until_goal(self, goal_end_mode = False, on_nodes = None):

        while self._path_set_growing != []:
            goal_node_mark = self.node_mark_set.get_node_mark(
                self.goal_node)
            if (goal_node_mark is None or
                goal_node_mark.under_limit()):
                self.one_step(goal_end_mode, on_nodes)
            else:
                break


    def get_node_mark(self, node):
        return self.node_mark_set.get_node_mark(node)

    def get_node_mark_set(self):
        return self.node_mark_set


    def get_current_goal_path(self):

        return self._path_set_determ


    def get_num_used_goal_path(self, node):

        goal_paths = self.get_current_goal_path()
        num_used = 0

        for path in goal_paths:
            if path.check_node(node):
                num_used += 1

        return num_used

    def get_goal_path_node_count(self):
        
        goal_paths = self.get_current_goal_path()
        node_count = {}
        
        for path in goal_paths:
            for node in path.get_used_nodes_dict():
                node_count[ node ] = node_count.get(node, 0) + 1
                
        return node_count

    def get_nodes_info_goal_path(self):
        """ info[ path_len ][ node ] = < Number of times used > """

        goal_paths = self.get_current_goal_path()
        info = {}

        for path in goal_paths:
            path_len = len(path)
            if path_len not in info:
                info[ path_len ] = {}
            used_nodes = path.get_used_nodes_dict()
            for node in used_nodes.keys():
                info[ path_len ][ node ] = \
                    info[ path_len ].get(node, 0) + 1

        return info


    def get_path_goal_path(self, node):
        """ Returns pathways to goals which passes given node. """

        goal_paths = self.get_current_goal_path()
        ret_path = []

        for path in goal_paths:
            if path.check_node(node):
                ret_path.append(path)
                    
        return ret_path


    def get_nodes_step_goal_path(self):
        """ Returns minimal steps from start among pathways to goals """

        nodes2steps = {}

        goal_paths = self.get_current_goal_path()
        for path in goal_paths:
            step = 0
            for node in path:
                if node not in nodes2steps:
                    nodes2steps[node] = {}
                nodes2steps[node][step] = ""
                step += 1

        step2nodes = {}
        for node in nodes2steps:
            steps = nodes2steps[node].keys()
            for step in steps:
                if step in step2nodes:
                    step2nodes[step].append(node)
                else:
                    step2nodes[step] = [ node ]

        return step2nodes

    def get_info_goal_path(self):
        """ info[ path_len ] = [ Pathway1, Pathway2, ... ] """

        goal_paths = self.get_current_goal_path()
        info = {}

        for path in goal_paths:
            path_len = len(path)
            if path_len not in info:
                info[ path_len ] = [ path ]
            else:
                info[ path_len ].append(path)

        return info

    def get_nodes_info_goal_path_II(self): # Maybe very heavy for large number of nodes?
        """ info[ path_len ][ node ]
        = [ Pathway1, Pathway2, ... ] """

        goal_paths = self.get_current_goal_path()
        info = {}

        for path in goal_paths:
            path_len = len(path)
            if path_len not in info:
                info[ path_len ] = {}
            used_nodes = path.get_used_nodes_dict()
            for node in used_nodes.keys():
                if node in info[ path_len ]:
                    info[ path_len ][ node ].append(path)
                else:
                    info[ path_len ][ node ] = [ path ]

        return info

    def get_nodes_info_goal_path_II_str(self, via_node = None):
        
        goal_path_info = self.get_info_goal_path()
        path_count = 0
        ret = []
        
        for path_len in goal_path_info:
            ret.append("*** Path length %s ***" % path_len)
            for path in goal_path_info[ path_len ]:
                if (via_node and
                    not path.check_node(via_node)):
                    continue
                ret.append("Path %s:" % path_count)
                pnode = None
                for node in path:
                    if pnode is None:
                        wt = ""
                    else:
                        wt = self.get_graph().get_edge_weight(pnode, node)
                    ret.append("\t".join((wt, node.get_node_name())))
                    pnode = node
                path_count += 1
                ret.append("")
                
        return "\n".join(ret)


    def display_info(self):
        
        print "***** Growing Paths *****"
        for path in self._path_set_growing:
            print path
        print
        print "***** Determined Paths *****"
        for path in self._path_set_determ:
            print path
        print
        self.node_mark_set.display_node_marks()


    def get_info_I(self):

        out = ""

        info1 = self.get_nodes_info_goal_path()
        info2 = self.get_nodes_info_goal_path_II()

        path_lens = info1.keys()
        path_lens.sort()
        nodes = {}

        for path_len in info1:
            for node in info1[ path_len ]:
                nodes[ node ] = ""

        for node in nodes:
            ret_tmp = []
            for path_len in path_lens:
                if node in info1[ path_len ]:
                    ret_tmp.append(info1[ path_len ][ node ])
                else:
                    ret_tmp.append(0)
            ret_tmp = Math.cumulat(ret_tmp)
            ret = []
            for elem in ret_tmp:
                ret.append(`elem`)
            out += "\t".join(("[ Overview ]",
                              node.get_node_name(),
                              "\t".join(ret))) + "\n"
            path_count = 0
            for path_len in path_lens:
                if node in info2[ path_len ]:
                    for path in info2[ path_len ][ node ]:
                        out += "\t".join((
                                "[ Description ]",
                                node.get_node_name(),
                                "\t".join(ret),
                                `path_count`,
                                "\t".join(path.get_path_node_names()))) + "\n"
                path_count += 1
        return out

if __name__ == "__main__":

    import sys
    from General_Packages.Usefuls.TmpFile import TmpFile_III

    print "### Path Class Check ###"

    node_factory = Node1.Node_Factory()

    path1 = Path()
    path1.add_node(node_factory.make("A"))
    path1.add_node(node_factory.make("B"))
    path1.add_node(node_factory.make("C"))
    path2 = path1.copy()
    path1.add_node(node_factory.make("GOAL"))

    path2.add_node(node_factory.make("D"))
    path2.add_node(node_factory.make("GOAL"))

    tmp_obj = TmpFile_III("""
XXX   YYY    ...
GOAL  RETRY  ...
GOAL  REST   ...
GOAL  RETIRE ...
""")
    graph0 = Graph1.Graph()
    graph0.read_from_file(tmp_obj.filename(), 0, 1, 2)
    graph0.graph_display()

    path3 = Path(graph0)
    path3.add_node(node_factory.make("H"))
    path3.add_node(node_factory.make("I"))
    path3.add_node(node_factory.make("J"))
    path3.add_node(node_factory.make("K"))
    # path3.add_node(node_factory.make("L"))
    path4 = path3.copy()
    path5 = path3.copy()
    path3.add_node(node_factory.make("GOAL"))
    path4.add_node(node_factory.make("M"))
    path4.add_node(node_factory.make("GOAL"))
    path5.add_node(node_factory.make("N"))
    path5.add_node(node_factory.make("P"))
    path5.add_node(node_factory.make("GOAL"))

    print "Paths:"
    print path1
    print path2
    print path3
    print path3.ret_path_bold(node_factory.make("I"))
    print path4
    print path5

    print ",".join([i.get_node_name() for i in path3.next_nodes()])

    print

    print "### Node_Mark Class Check ###"

    node_mark = Node_Mark_simple(node_factory.make("GOAL"), 3)
    print node_mark.mark(path1)
    print node_mark.mark(path2)
    print node_mark.mark(path3)
    print node_mark.get_current_rank()
    print node_mark.mark(path4)
    print node_mark.mark(path5)
    print node_mark.get_limit()
    print node_mark.longest_path_len()

    print node_mark

    tmp_obj = TmpFile_III("""

Node-A     Node-B (1)
Node-A     Node-C (1)
Node-A     Node-E (1)
Node-B     Node-D (2)
Node-C     Node-E (3)
Node-C     Node-F (3)
Node-D     Node-F (4)
Node-E     Node-F (5)
Node-F     Node-E (6)

Node-A     Node-1 (7)
Node-1     Node-2 (8)
Node-2     Node-3 (9)
Node-3     Node-4 (10)
Node-4     Node-5 (11)
Node-5     Node-F (12)
Node-1     Node-E (8)

Node-A     Node-15 (1)
Node-15    Node-F (13)

""")

    graph1 = Graph1.Graph()
    graph1.read_from_file(tmp_obj.filename(), 0, 1, 2)
    graph1.graph_display()

    starting_node = node_factory.make("Node-A")
    intermediate_node1 = node_factory.make("Node-C")
    intermediate_node2 = node_factory.make("Node-E")
    intermediate_node3 = node_factory.make("Node-F")
    intermediate_node4 = node_factory.make("Node-B")
    intermediate_node5 = node_factory.make("Node-15")
    intermediate_node6 = node_factory.make("Node-J")
    goal_node = node_factory.make("Node-F")

    path_search = Path_Search(graph1, 3)
    path_search.set_start_node(starting_node)
    path_search.set_goal_node(goal_node)
    # path_search.one_step(False, (starting_node, goal_node, intermediate_node6))
    # path_search.one_step(False, (starting_node, goal_node, intermediate_node6))
    # path_search.one_step(False, (starting_node, goal_node, intermediate_node6))
    # path_search.one_step()
    # path_search.one_step()
    # path_search.one_step()
    # path_search.one_step()
    # path_search.one_step()
    # path_search.loop_steps()
    # path_search.display_info()   
    
    path_search.loop_steps_until_goal(goal_end_mode = True,
                                      on_nodes = [ starting_node,
                                                   intermediate_node1,
                                                   intermediate_node2,
                                                   intermediate_node3,
                                                   intermediate_node4,
                                                   intermediate_node5,
                                                   goal_node ])
                                      
    path_search.display_info()
    print path_search.get_node_mark(goal_node)
    print path_search.get_node_mark(goal_node).get_num_pathways(3)

    for path in path_search.get_current_goal_path():
            print path
    print

    goal_path_info = path_search.get_nodes_info_goal_path()
    for k in goal_path_info:
        for node in goal_path_info[k]:
            print "Step", k, "pathway :", node, goal_path_info[k][node]

    print

    goal_path_info_II = path_search.get_nodes_info_goal_path_II()
    for k in goal_path_info_II:
        for node in goal_path_info_II[k]:
            for path in goal_path_info_II[k][node]:
                print "Step", k, "pathway :", node, \
                      goal_path_info[k][node], path

    print path_search.get_info_I()

    print path_search.get_num_used_goal_path(node_factory.make("Node-E"))
    print path_search.get_num_used_goal_path(node_factory.make("Node-F"))
    for path in path_search.get_path_goal_path(node_factory.make("Node-E")):
        print path.ret_path_bold(node_factory.make("Node-E"))
    
    print "---"
    print path_search.get_nodes_info_goal_path_II_str()
    
    for step in path_search.get_nodes_step_goal_path():
        print step, ",".join(map(lambda x: x.get_node_name(), path_search.get_nodes_step_goal_path()[step]))
    
    for node in path_search.get_goal_path_node_count():
        print node, path_search.get_goal_path_node_count()[node]
