#!/usr/bin/env python

import Data_Struct.Data_Sheet2
import Data_Struct.Hash2
import Data_Struct.ListList
import Data_Struct.Data_with_Miss2
import Calc_Packages.Math.StatsI as Stats

import Express1
import GEO_probe1

import string
from Usefuls.ListProc1 import array_string

class SymAtlas_annotation:
    def __init__(self, gpl96_file, gnf1b_anntable_file):
        self.gpl96 = GEO_probe1.Probe_Info(gpl96_file)

        self.gnf1b_anntable = Data_Struct.Hash2.Hash("A")
        self.gnf1b_anntable.read_file_hd(filename = gnf1b_anntable_file,
                                         Key_cols_hd = ["LocusLink"],
                                         Val_cols_hd = ["Probeset ID"])
        
        self.gnf1b_anntable_moreinfo = Data_Struct.Hash2.Hash("S")
        self.gnf1b_anntable_moreinfo.read_file_hd(filename = gnf1b_anntable_file,
                                         Key_cols_hd = ["LocusLink"],
                                         Val_cols_hd = ["Name", "RefSeq", "UniGene", "UniProt", "Description"])

        gpl96_genes_tmp = self.gpl96.get_genes()
        gnf1b_genes_tmp = self.gnf1b_anntable.keys()

        self.gene_set = {}
        for gene in gpl96_genes_tmp + gnf1b_genes_tmp:
            try:
                gene_int = string.atoi(gene)
            except ValueError:
                gene_int = False
            if not gene_int is False:
                self.gene_set[ gene ] = gene_int

    def get_genes(self):
        return self.gene_set.keys()

    def gene_to_probeids(self, geneid):

        gpl96 = self.gpl96.conv_GeneID_to_probeIDs(geneid)
        if gpl96 is None: gpl96 = []

        gnf1b = self.gnf1b_anntable.val_accord_hd(geneid, "Probeset ID")
        if gnf1b is None: gnf1b = []

        return (gpl96 + gnf1b)

    def get_gene_name_from_geneid(self, geneid):
        return self.gnf1b_anntable_moreinfo.val_accord_hd(geneid, "Name")
    
    def get_description_from_geneid(self, geneid):
        return self.gnf1b_anntable_moreinfo.val_accord_hd(geneid, "Description")        



class SymAtlas(Express1.Express):
    def __init__(self, expr_file, gpl96_file, gnf1b_file):
	self.data_sheet = Data_Struct.Data_Sheet2.Data_Sheet(expr_file)
        self.data_sheet.numerize()
        self.symatlas_annot = SymAtlas_annotation(gpl96_file,
                                                  gnf1b_file)

    def genes(self):
        return self.symatlas_annot.get_genes()

    def expression_pat(self, geneid):

        probeids = self.symatlas_annot.gene_to_probeids(geneid)
        if not probeids:
            return None
        elif len(probeids) == 1:
            """
            print string.join(
                [probeids[0]] +
                array_string(self.expression_pat_probe(probeids[0])), "\t")
            """
            return self.expression_pat_probe(probeids[0])

        exp_pat_norm = Data_Struct.ListList.ListList()

        for probeid in probeids:
            exp_pat_del = (
                Data_Struct.
                Data_with_Miss2.
                ListList_Size_Conv([ self.expression_pat_probe(probeid) ])
                )
            exp_pat_del_norm = (
                Stats.norm(exp_pat_del.get_num_listlist(True)[0])
                )

            exp_pat_del.import_num_listlist([ exp_pat_del_norm ])

            # print string.join([probeid] + array_string(exp_pat_del.get_idx_numlistlist_to_missing().get_all_lists()[0]), "\t")

            exp_pat_norm.add_list(
                exp_pat_del.
                get_idx_numlistlist_to_missing().
                get_all_lists()[0])

        return exp_pat_norm.to_single_list()

    def expression_pat_probe(self, probeid):
        return self.data_sheet.get_data(probeid)

if __name__ == "__main__":

    import Usefuls.rsConfig
    symatlas = Usefuls.rsConfig.RSC_II("SymAtlas")

    symatlas_annot = SymAtlas_annotation(symatlas.gpl96_file,
                                         symatlas.gnf1b_anntable_file)

    symatlas = SymAtlas(symatlas.symatlas_file,
                        symatlas.gpl96_file,
                        symatlas.gnf1b_anntable_file)

    print "\t".join(("Gene", "Gene Name", "Probes", "Description") + tuple(symatlas.conditions()))

    for gene in symatlas.genes():
        probes = symatlas_annot.gene_to_probeids(gene)
        exppat = symatlas.expression_pat(gene)
        gene_name = symatlas_annot.get_gene_name_from_geneid(gene)
        description = symatlas_annot.get_description_from_geneid(gene)
        exppat_str = map(lambda x: `x`, exppat)
        if not gene_name:
            gene_name = ""
        if not description:
            description = ""
        print "\t".join([gene, gene_name, ",".join(probes), description] + exppat_str)


    # print symatlas_annot.gene_to_probeids("2353")
    # print symatlas.expression_pat_probe("1007_s_at")
    # print symatlas.expression_pat("2353")
