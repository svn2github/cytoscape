#!/usr/bin/env python

import Graph1
import Node1

import Calc_Packages.Math.MathI as Math

class Path:
    def __init__(self, graph):
        self.path = []
        self.used_nodes = {}
        self.graph = graph

    def add_node(self, node):
        self.path.append(node)
        self.used_nodes[ node ] = self.used_nodes.get(node, 0) + 1

    def get_path(self):
        return self.path

    def get_used_nodes_dict(self):
        return self.used_nodes

    def check_node(self, node):
        return self.used_nodes.has_key(node)

    def last_node(self):
        return self.path[-1]

    def copy(self):
        new_path = Path(self.graph)
        new_path.path = self.path[:]
        new_path.used_nodes = self.used_nodes.copy()
        return new_path

    def get_path_node_names(self):
        node_name_set = []
        for node in self.path:
            node_name_set.append(node.get_node_name())
        return node_name_set

    def __len__(self):
        return len(self.path)

    def __str__(self):
        return "Path: " + " -> ".join(self.get_path_node_names())

    def ret_path_bold(self, bnode):
        ret_node = []
        for node in self.get_path():
            if bnode is node:
                ret_node.append("[" + node.get_node_name() + "]")
            else:
                ret_node.append(node.get_node_name())
        return " -> ".join(ret_node)


class Node_Mark:
    """ Pathways which reaches given node.
    Path redundancies are not checked """

    def __init__(self, node, limit):

        self.node_mark = []
        self.limit = limit
        self.node = node

    def get_paths(self):
        return self.node_mark

    def mark(self, path):
        """ Registers additional pathway in self.node_mark.
        Path length must not be shorter than
        already registered paths in the node_mark.
        Format of self.node_mark:
        [[ path1, path2, path3 ], [ path4 ], [ path5, path6 ]]
        where lengths of path1 to 3 are identical and so are
        path5 and path6. The length of pathways must be
        path1 < path4 < path5.
        """

        if path.last_node() is not self.node:
            raise "Node mismatch."

        if self.limit == 0:
            return False

        if self.node_mark == []:
            # First pathway
            self.node_mark.append([ path.copy() ])
            return True

        else:
            current_rank     = len(self.node_mark)
            current_path_len = len(self.node_mark[-1][0])

            if current_path_len > len(path):
                raise "Path warp error ..."

            elif current_path_len == len(path):
                """  Adding path having same length as
                those having longest path registered in
                node_mark """
                self.node_mark[-1].append(path.copy())
                return True

            """ From here, whether to add longer pathways
            has to be decided. """

            if current_rank > self.limit:
                raise "Node mark malfunction ..."
            elif current_rank == self.limit:
                return False
            else:
                """ Adding longer pathway """
                self.node_mark.append([ path.copy() ])
                return True

    def get_num_pathways(self, k = 99999999):
        ct = 0
        ret = 0
        for path_set in self.get_paths():
            ret += len(path_set)
            ct += 1
            if ct >= k:
                break
        return ret


    def get_limit(self):
        return self.limit

    def get_current_rank(self):
        return len(self.node_mark)

    def under_limit(self):
        return self.get_current_rank() < self.get_limit()

    def longest_path_len(self):
        if self.node_mark == []:
            return 0
        else:
            return len(self.node_mark[-1][0])

    def __str__(self):
        ret = ""
        for rank in range(len(self.node_mark)):
            ret += "* Rank " + `rank` + " *\n"
            for path in self.node_mark[rank]:
                ret += path.__str__() + "\n"
        return ret

    def output1(self):
        ret = ""

        ret_p1 = [ self.node.get_node_name(),
                   `self.get_num_pathways(1)`,
                   `self.get_num_pathways(2)`,
                   `self.get_num_pathways(3)` ]

        ret += "\t".join(["[ Overview ]", ] + ret_p1) + "\n"

        for rank in range(len(self.node_mark)):

            for path in self.node_mark[ rank ]:
                ret_p2 = ret_p1[:] + [`rank`, ]
                for node in path.get_path():
                    ret_p2.append(node.get_node_name())
                ret += "\t".join(["[ Description ]",] + ret_p2) + "\n"

        return ret


class Node_Mark_Set:
    def __init__(self, limit):
        self.node_mark_set = {}
        self.limit = limit

    def get_node_mark(self, node):
        if node in self.node_mark_set:
            return self.node_mark_set[ node ]
        else:
            return None

    def mark(self, node, path):
        if node not in self.node_mark_set:
            self.node_mark_set[ node ] = Node_Mark(node, self.limit)
        return self.node_mark_set[ node ].mark(path)

    def display_node_marks(self):
        print "***** Node Marks *****"
        for node in self.node_mark_set:
            print "--- " + node.get_node_name() + " ---"
            print self.node_mark_set[ node ]

    def output_node_marks(self):
        ret = ""
        for node in self.node_mark_set:
            ret += self.get_node_mark(node).output1()
        return ret


class Path_Search:
    def __init__(self, graph, limit):

        self.graph = graph
        self.limit = limit
        self.path_set = []
        self.node_mark_set = Node_Mark_Set(limit)
        self.start_node = None
        self.goal_node = None

    def get_graph(self):

        return self.graph


    def set_start_node(self, node):

        if self.start_node is not None:
            raise "Start node redefinition..."

        self.start_node = node
        path = Path(self.graph)
        path.add_node(node)
        self.path_set = [ path ]
        self.node_mark_set.mark(node, path)

    def set_goal_node(self, node):

        if self.goal_node is not None:
            raise "Goal node redefinition..."
        self.goal_node = node

    def one_step(self, goal_end_mode = False):

        new_path_set = []

        for path in self.path_set:
            for node in self.graph.destination_nodes(path.last_node()):
                if goal_end_mode and path.check_node(self.goal_node):
                    continue
                new_path = path.copy()
                new_path.add_node(node)
                if self.node_mark_set.mark(node, new_path):
                    new_path_set.append(new_path)

        self.path_set = new_path_set


    def one_step_consider_ON(self, on_nodes,
                             goal_end_mode = False):

        new_path_set = []

        for path in self.path_set:
            for node in self.graph.destination_nodes(path.last_node()):
                if node not in on_nodes:
                    continue
                if goal_end_mode and path.check_node(self.goal_node):
                    continue
                new_path = path.copy()
                new_path.add_node(node)
                if self.node_mark_set.mark(node, new_path):
                    new_path_set.append(new_path)
        
        self.path_set = new_path_set


    def loop_steps(self):

        while self.path_set != []:
            self.one_step()


    def loop_steps_until_goal(self, goal_end_mode = False):

        while self.path_set != []:
            goal_node_mark = self.node_mark_set.get_node_mark(
                self.goal_node)
            if (goal_node_mark is None or
                goal_node_mark.under_limit()):
                self.one_step(goal_end_mode)
            else:
                break

    def loop_steps_until_goal_consider_ON(self,
                                          on_nodes,
                                          goal_end_mode = False):

        if self.start_node not in on_nodes:
            return
        if self.goal_node not in on_nodes:
            return
        
        while self.path_set != []:
            """
            print "Current paths:"
            for path in self.path_set:
                print path
            print
            """

            goal_node_mark = self.node_mark_set.get_node_mark(
                self.goal_node)
            if (goal_node_mark is None or
                goal_node_mark.under_limit()):
                self.one_step_consider_ON(on_nodes, goal_end_mode)
            else:
                break

    def get_node_mark(self, node):
        return self.node_mark_set.get_node_mark(node)

    def get_node_mark_set(self):
        return self.node_mark_set


    def get_current_goal_path(self):
        # You can also use paths in the node mark of goal_node

        node_mark = self.node_mark_set \
                   .get_node_mark(self.goal_node)
        if node_mark:
            ret_paths = node_mark.get_paths()
        else:
            ret_paths = []

        return ret_paths


    def get_num_used_goal_path(self, node):

        goal_paths = self.get_current_goal_path()
        num_used = 0

        for path_set in goal_paths:
            for path in path_set:
                if path.check_node(node):
                    num_used += 1

        return num_used


    def get_nodes_info_goal_path(self):
        """ info[ path_len ][ node ] = < Number of times used > """

        goal_paths = self.get_current_goal_path()
        info = {}

        for path_set in goal_paths:
            path_len = len(path_set[0])
            info[ path_len ] = {}
            for path in path_set:
                used_nodes = path.get_used_nodes_dict()
                for node in used_nodes.keys():
                    info[ path_len ][ node ] = \
                          info[ path_len ].get(node, 0) + 1

        return info


    def get_path_goal_path(self, node):

        goal_paths = self.get_current_goal_path()
        ret_path = []

        for path_set in goal_paths:
            for path in path_set:
                if path.check_node(node):
                    ret_path.append(path)
                    
        return ret_path

    def get_nodes_step_goal_path(self):

        nodes2step = {}

        goal_paths = self.get_current_goal_path()
        for path_set in goal_paths:
            for path in path_set:
                step = 0
                for node in path.get_path():
                    if (node not in nodes2step or
                        nodes2step[node] > step):
                        nodes2step[node] = step
                    step += 1

        step2nodes = {}
        for node in nodes2step:
            step = nodes2step[node]
            if step in step2nodes:
                step2nodes[step].append(node)
            else:
                step2nodes[step] = [ node ]

        return step2nodes


    def get_nodes_step_goal_path_II(self):

        nodes2steps = {}

        goal_paths = self.get_current_goal_path()
        for path_set in goal_paths:
            for path in path_set:
                step = 0
                for node in path.get_path():
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


    def get_nodes_info_goal_path_II(self):
        """ info[ path_len ][ node ]
        = [ Pathway1, Pathway2, ... ] """

        goal_paths = self.get_current_goal_path()
        info = {}

        for path_set in goal_paths:
            path_len = len(path_set[0])
            info[ path_len ] = {}
            for path in path_set:
                used_nodes = path.get_used_nodes_dict()
                for node in used_nodes.keys():
                    if node in info[ path_len ]:
                        info[ path_len ][ node ].append(path)
                    else:
                        info[ path_len ][ node ] = [ path ]

        return info

    def display_info(self):
        print "***** All Paths *****"
        for path in self.path_set:
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

    node_factory = Node1.Node_Factory()


    path1 = Path(None)
    path1.add_node(node_factory.make("A"))
    path1.add_node(node_factory.make("B"))
    path1.add_node(node_factory.make("C"))
    path2 = path1.copy()
    path1.add_node(node_factory.make("GOAL"))

    path2.add_node(node_factory.make("D"))
    path2.add_node(node_factory.make("GOAL"))

    path3 = Path(None)
    path3.add_node(node_factory.make("H"))
    path3.add_node(node_factory.make("I"))
    path3.add_node(node_factory.make("J"))
    path3.add_node(node_factory.make("K"))
    # path3.add_node(node_factory.make("L"))
    path3.add_node(node_factory.make("GOAL"))

    print "Paths:"
    print path1
    print path2
    print path3
    print path3.ret_path_bold(node_factory.make("I"))

    node_mark = Node_Mark(node_factory.make("GOAL"), 4)
    print node_mark.mark(path1)
    print node_mark.mark(path2)
    print node_mark.mark(path3)
    print node_mark.get_limit()
    print node_mark.get_current_rank()
    print node_mark.under_limit()
    print node_mark.longest_path_len()

    print node_mark


    from General_Packages.Usefuls.TmpFile import TmpFile_III
    tmp_obj = TmpFile_III("""

Node-A     Node-B
Node-A     Node-C
Node-A     Node-E
Node-B     Node-D
Node-C     Node-E
Node-C     Node-F
Node-D     Node-F
Node-E     Node-F
Node-F     Node-E

Node-A     Node-1
Node-1     Node-2
Node-2     Node-3
Node-3     Node-4
Node-4     Node-5
Node-5     Node-F
Node-1     Node-E

Node-A     Node-15
Node-15    Node-F

""")

    graph1 = Graph1.Graph()
    graph1.read_from_file2(tmp_obj.filename(), 0, 1, None)
    graph1.graph_display()

    starting_node = node_factory.make("Node-A")
    intermediate_node1 = node_factory.make("Node-C")
    intermediate_node2 = node_factory.make("Node-E")
    intermediate_node3 = node_factory.make("Node-F")
    intermediate_node4 = node_factory.make("Node-B")
    intermediate_node5 = node_factory.make("Node-15")
    goal_node = node_factory.make("Node-F")

    path_search = Path_Search(graph1, 5)
    path_search.set_start_node(starting_node)
    path_search.set_goal_node(goal_node)
    # path_search.one_step()
    # path_search.display_info()
    path_search.loop_steps_until_goal_consider_ON([ starting_node,
                                                    intermediate_node1,
                                                    intermediate_node2,
                                                    intermediate_node3,
                                                    intermediate_node4,
                                                    intermediate_node5,
                                                    goal_node ],
                                                  goal_end_mode = True)
    # path_search.loop_steps()
    path_search.display_info()
    print path_search.get_node_mark(goal_node)
    print path_search.get_node_mark(goal_node).output1()
    print path_search.get_node_mark(goal_node).get_num_pathways(3)

    print path_search.get_node_mark_set().output_node_marks()
    for path_set in path_search.get_current_goal_path():
        for path in path_set:
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

    for step in path_search.get_nodes_step_goal_path_II():
        print "***", step, "***"
        for node in path_search.get_nodes_step_goal_path_II()[ step ]:
            print node
    
