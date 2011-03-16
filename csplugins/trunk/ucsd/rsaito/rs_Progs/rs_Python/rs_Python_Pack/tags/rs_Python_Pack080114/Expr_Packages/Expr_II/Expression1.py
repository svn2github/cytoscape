#!/usr/bin/env python

import Data_Struct.ListList
import Data_Struct.Data_with_Miss2
import Data_Struct.Hash2
import Calc_Packages.Math.Math1 as Math
import Probe1

class Single_Expression:
    """ Single Expression, basically one expression level. """

    def __init__(self, probe, exp_level):
        self.id = None # Maybe object ID is sufficient?
        self.probe = probe
        self.exp_level = exp_level
        self.cond = None

    def get_probe(self):
        return self.probe

    def get_exp_level(self):
        return self.exp_level

    def set_cond(self, cond):
        self.cond = cond

    def get_cond(self):
        return self.cond


class Expression_Pat:
    """ Multiple expression levels for each condition
    for single probe """

    def __init__(self, probe):
        self.probe = probe
        self.exp_pat = []

    def get_probe(self):
        return self.probe

    def add_Single_Expression(self, single_expression):

        if single_expression.get_probe() != self.probe:
            raise "Probe mismatch."

        self.exp_pat.append(single_expression)

    def get_exp_levels(self):

        exp_levels = []
        for exp in self.exp_pat:
            exp_levels.append(exp.get_exp_level())

        return exp_levels

    def get_conditions(self):

        conds = []
        for exp in self.exp_pat:
            conds.append(exp.get_cond())

        return conds


class Expression_Data:
    """ Multiple probes having multiple expression levels """
    def __init__(self):
        self.expr_pat_probe_based_set = {}

    def add_exp_pat(self, expr_pat_probe_based):

        probe = expr_pat_probe_based.get_probe()
        if self.expr_pat_probe_based_set == {}:
            self.expr_pat_probe_based_set[ probe ] = \
                                           expr_pat_probe_based
        elif self.conditions() == \
                 expr_pat_probe_based.get_conditions():
            self.expr_pat_probe_based_set[ probe ] = \
                                           expr_pat_probe_based
        else:
            raise "Condition mismatch."


    def probes(self):
        return self.expr_pat_probe_based_set.keys()

    def conditions(self):
        arbit_probe = self.probes()[0]
        arbit_exp_pat = self.expression_pat_obj(arbit_probe)
        return arbit_exp_pat.get_conditions()

    def row_num(self):
        return len(self.probes())

    def expression_pat_obj(self, probe):
        return self.expr_pat_probe_based_set[ probe ]

    def expression_pat(self, probe):
        return self.expression_pat_obj(probe).get_exp_levels()


    def expression_pat_multi_probes(self, probes):

        if len(probes) == 1:
            return self.expression_pat(probes[0])

        exp_pat_norm = Data_Struct.ListList.ListList()

        for probe in probes:
            exp_pat_del = (
                Usefuls.
                Data_with_Miss2.
                ListList_Size_Conv([self.expression_pat(probe)])
                )
            exp_pat_del_norm = (
                Math.norm(exp_pat_del.get_num_listlist(True)[0])
                )

            exp_pat_del.import_num_listlist([ exp_pat_del_norm ])

            """
            print string.join([probe] +
                              array_string(exp_pat_del.
                              get_idx_numlistlist_to_missing().
                              get_all_lists()[0]), "\t")
                              """

            exp_pat_norm.add_list(
                exp_pat_del.
                get_idx_numlistlist_to_missing().
                get_all_lists()[0])

        return exp_pat_norm.to_single_list()

    def read_expression_data_from_file(self, expfile,
                                       probeid_label, cond_labels):

        expr_hash = Data_Struct.Hash2.Hash("S")
        expr_hash.read_file_hd(filename = expfile,
                                   Key_cols_hd = probeid_label,
                                   Val_cols_hd = cond_labels)

        for probeid in expr_hash.keys():
            probe = Probe1.Probe_Factory().make(probeid)
            expr_pat = Expression_Pat(probe)
            expr_levels = expr_hash.val_force(probeid).split("\t")
            for i in range(len(cond_labels)):
                expr_level = float(expr_levels[i])
                condition  = cond_labels[i]
                expr = Single_Expression(probe, expr_level)
                expr.set_cond(condition)
                expr_pat.add_Single_Expression(expr)

            self.add_exp_pat(expr_pat)


if __name__ == "__main__":

    expr1_1 = Single_Expression("Probe 1", 10.3)
    expr1_1.set_cond("Condition 1")
    expr1_2 = Single_Expression("Probe 1", 10.5)
    expr1_2.set_cond("Condition 2")
    expr1_3 = Single_Expression("Probe 1", 10.7)
    expr1_3.set_cond("Condition 3")
    print expr1_1.get_cond()
    print expr1_1.get_probe()
    print expr1_1.get_exp_level()

    expr_pat1 = Expression_Pat("Probe 1")
    expr_pat1.add_Single_Expression(expr1_1)
    expr_pat1.add_Single_Expression(expr1_2)
    expr_pat1.add_Single_Expression(expr1_3)
    print expr_pat1.get_exp_levels()
    print expr_pat1.get_conditions()

    expr2_1 = Single_Expression("Probe 2", 11.3)
    expr2_1.set_cond("Condition 1")
    expr2_2 = Single_Expression("Probe 2", 11.5)
    expr2_2.set_cond("Condition 2")
    expr2_3 = Single_Expression("Probe 2", 11.7)
    expr2_3.set_cond("Condition 3")
    print expr2_1.get_cond()
    print expr2_1.get_probe()
    print expr2_1.get_exp_level()

    expr_pat2 = Expression_Pat("Probe 2")
    expr_pat2.add_Single_Expression(expr2_1)
    expr_pat2.add_Single_Expression(expr2_2)
    expr_pat2.add_Single_Expression(expr2_3)
    print expr_pat2.get_exp_levels()
    print expr_pat2.get_conditions()

    expr_pat_set = Expression_Data()
    expr_pat_set.add_exp_pat(expr_pat1)
    expr_pat_set.add_exp_pat(expr_pat2)

    print expr_pat_set.expression_pat("Probe 2")

    print expr_pat_set.expression_pat_multi_probes(["Probe 1", "Probe 2"])
