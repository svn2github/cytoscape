#!/usr/bin/env python

import sys
import Data_Struct.Data_Sheet3
from Usefuls.Instance_check import instance_class_check
import Graph_Packages.Graph.Graph1 as Graph
import Usefuls.Synonyms2
import BioData_Packages.Gene.NCBI_Synonym

class Botstain_Sheet(Data_Struct.Data_Sheet3.Data_Sheet):
    def __init__(self, filename, synonyms = None, sep = "\t"):
        if synonyms:
            instance_class_check(synonyms, Usefuls.Synonyms2.Synonyms)
        
        self.synonyms = synonyms
        Data_Struct.Data_Sheet3.Data_Sheet.__init__(self, filename, sep)
        self.numerize()

    def extract_col_labels(self, lines, sep):
        first_line = lines.pop(0)
        """ This will extract and eliminate first line """
        col_lb_immature = first_line.split(sep)
        return col_lb_immature[6:]

    def extract_row_label_data(self, line_a):
        label = line_a[1]
        if self.synonyms:
            label = self.synonyms.to_main_force(label)
        data = line_a[6:]
        return (label, data)

    def get_data(self, row_key):
        if self.synonyms:
            row_key = self.synonyms.to_main_force(row_key)
        return Data_Struct.Data_Sheet3.Data_Sheet.get_data(self, row_key)

    def get_datum(self, row_key, col_key):
        
        if self.synonyms:
            row_key = self.synonyms.to_main_force(row_key)
        return Data_Struct.Data_Sheet3.Data_Sheet.get_datum(self, row_key, col_key)

    def get_genes_above_thres_simp(self, col_label, thres):

        genes = self.row_labels()
        genes_above = []
        for gene in genes:
            expr = self.get_datum(gene, col_label)
            # print gene, expr
            if expr is not False and expr >= thres:
                genes_above.append(gene)
        return genes_above


    def get_genes_above_thres(self, genes, col_label, thres):

        genes_above = []
        for gene in genes:
            expr = self.get_datum(gene, col_label)
            # print gene, expr
            if expr is not False and expr >= thres:
                genes_above.append(gene)
        return genes_above

    def get_genes_above_thres_graph(self, graph, col_label, thres):
        instance_class_check(graph, Graph.Graph)

        genes_above = []
        for gene_obj in graph.get_node_set():
            expr = self.get_datum(gene_obj.get_node_name(), col_label)
            # print gene_obj.get_node_name(), expr
            if expr is False:
                sys.stderr.write("Expression data for " +
                                 gene_obj.get_node_name() +
                                 " not found.\n")
                genes_above.append(gene_obj)
            elif expr >= thres:
                genes_above.append(gene_obj)
        return genes_above


if __name__ == "__main__":
    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
    rsc_geneinfo = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")
    synonyms = BioData_Packages.Gene.NCBI_Synonym.NCBI_Gene_Synonyms(rsc_geneinfo.GeneInfo_hs, case_mode = False)
    bot_expr = Botstain_Sheet(rsc.Botstain_expr,
                              synonyms)
    print "HCP5" in bot_expr.row_labels()
    print bot_expr.get_data("HCP5")
    bot_expr.numerize()
    print bot_expr.get_data("STK15")
    print bot_expr.get_genes_above_thres((
        "STK15",
        "PLK",
        "UBCH10",
        "MAPK13",
        "CDC2",
        "TOP2A",
        "CENPE",
        "TOP2A",
        "KPNA2",
        "FLJ10468"), "A#2", -1)


    import Usefuls.TmpFile
    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

STK15      PLK      a
MAPK13     CDC2     b
KPNA2      CENPE    c
XXXX       YYYY     XY
""")

    graph1 = Graph.Graph()
    graph1.read_from_file2(tmp_obj.filename(), 0, 1, None)
    for node in  bot_expr.get_genes_above_thres_graph(graph1, "A#2", -1):
        print node.get_node_name()
