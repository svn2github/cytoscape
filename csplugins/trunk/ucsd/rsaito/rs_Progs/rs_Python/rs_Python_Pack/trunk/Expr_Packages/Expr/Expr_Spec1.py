#!/usr/bin/env python

from Data_Struct.Data_with_Miss2 import ListList_Size_Conv
from Data_Struct.ListList import ListList
from Usefuls.Counter import Count2
import Calc_Packages.Math.Math1 as Math

import Graph_Packages.Graph.Graph1 as PPI
import Express1

class Expr_Spec1:
    def __init__(self, expr_obj):

        if not isinstance(expr_obj, Express1.Express):
            raise "Instance type mismatch: Express1 expected."

        self.expr_obj = expr_obj
        self.conds = expr_obj.conditions()
        self.ncount = 0
        self.spec_total = []
        for cond in self.conds:
            self.spec_total.append(0)

    def calc_spec(self, gene1, gene2):
        exp1 = self.expr_obj.expression_pat(gene1)
        exp2 = self.expr_obj.expression_pat(gene2)
        if exp1 is False or exp2 is False:
            return {}

        ll = ListList()
        ll.add_list(exp1)
        ll.add_list(exp2)
        self.data_with_miss = ListList_Size_Conv(ll)
        self.data_with_miss.conv_idx_missing_to_numlistlist()
        self.data_with_miss.add_labels(self.conds)
        exp1_filt, exp2_filt = (
            self.data_with_miss.get_num_listlist().get_all_lists()
            )
        exp1_std = Math.norm(exp1_filt)
        exp2_std = Math.norm(exp2_filt)
        spec = Math.prod_abt(exp1_std, exp2_std)
        labels = self.data_with_miss.valid_labels()

        ret = {}
        for i in range(len(spec)):
            ret[ labels[i] ] = spec[i]
        """
        print "*** Expressional specificity info. ***"
        print "Gene IDs:", gene1, gene2
        print "Exp1 std:", exp1_std
        print "Exp2 std:", exp2_std
        print "Spec:", ret
        """
        return ret

    def analyze_spec(self, ppi):

        if not isinstance(ppi, PPI.Graph):
            raise "Instance type mismatch: PPi2 expected."

        ct = Count2()

        for gene1, gene2, val in ppi.get_non_redu_pairs():
            if gene1 != gene2:
                ct.add_count_dict(self.calc_spec(gene1.get_node_name(),
                                                 gene2.get_node_name()))
                # print "Counter:", ct.counter

        return ct

if __name__ == "__main__":
    import sys
    import string
    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

    import IVV_Packages.IVV_Info.IVV_info1 as IVV_info
    import IVV_Packages.IVV_Info.IVV_filter1 as IVV_filter
    import IVV_Packages.IVV_Info.IVV_Conv as IVV_Conv


    sys.stderr.write("Reading IVV information...\n")
    filter = IVV_filter.IVV_filter()
    filter.set_Prey_filter_file(rsc.PreyFilter)
    ivv_info = IVV_info.IVV_info(rsc.IVVInfo, filter)

    sys.stderr.write("Reading Expression information...\n")
    expr = Express1.SymAtlas(rsc.SymAtlas_Hs_CD)

    ivv_gene = IVV_Conv.IVV_Conv(ivv_info, mode = "S")
    ivv_gene.set_reprod_thres(1)
    ivv_gene.ivv_to_convid()
    spoke = ivv_gene.get_spoke()

    # print spoke

    ppi = PPI.Graph()
    ppi.read_dict(spoke)
    ppi.both_dir()

    expr_spec = Expr_Spec1(expr)
    gene1 = "4087"
    gene2 = "2353" # "6838"

    print expr_spec.calc_spec(gene1, gene2)

    ct = expr_spec.analyze_spec(ppi)
    for cond in ct.get_elems():
        print cond, ct.get_average(cond)


    """
    for p1, p2, val in ppi.get_non_redu_ppi():
        if p1 != p2:
            corr = expr.corr(p1, p2)
            print p1, p2, corr
    """



