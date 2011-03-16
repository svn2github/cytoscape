#!/usr/bin/env python

import Graph_Packages.Graph.Graph1 as Graph
import Data_Struct.MultiDimDict1
import Data_Struct.Hash2

class MMI_Pred:
    def __init__(self):
        self.ppi = False
        self.mmi = Graph.Graph()
        self.protein2motif = Data_Struct.Hash2.Hash("A")

    def set_PPI(self, ppi):
        self.ppi = ppi

    def set_motif_info(self, protein2motif_file):
        self.protein2motif.read_file(protein2motif_file,
                                     Key_cols = [0],
                                     Val_cols = [1])

    def get_motif(self, protein):
        if self.protein2motif.val_force(protein) != "":
            return self.protein2motif.val_force(protein)
        else:
            return []

    def pred_mmi_from_ppi(self):

        mmi = Data_Struct.MultiDimDict1.MultiDimDict(dim = 2, init_val = 0)

        for p1o, p2o, wt in self.ppi.get_non_redu_pairs():
            p1 = p1o.get_node_name()
            p2 = p2o.get_node_name()

            motifs1 = self.get_motif(p1)
            motifs2 = self.get_motif(p2)

            mmi_tmp = {}
            for m1 in motifs1:
                for m2 in motifs2:
                    mmi_tmp[ m1 + "\t" + m2 ] = ""
                    mmi_tmp[ m2 + "\t" + m1 ] = ""

            for m1m2 in mmi_tmp.keys():
                m1, m2 = m1m2.split("\t")
                mmi.plus_val((m1, m2), 1)

        """
	    print "**** MMI prediction for", p1, "and", p2
	    print "Motif 1:", motifs1
	    print "Motif 2:", motifs2
	    print "MMI    :", mmi_tmp.keys()
	    print "bZIP   :", mmi.get_val(("bZIP_1", "bZIP_1"))
	    """

        self.mmi.read_dict(mmi.get_all_data())

    def get_mmi_val(self, m1, m2):
        if self.mmi.get_edge_by_node_names(m1, m2) != False:
            return self.mmi.get_edge_by_node_names(m1, m2).get_weight()
        else:
            return 0

    def get_all_mmi(self):
        mmi = []
        for p1o, p2o, val in self.mmi.get_all_pairs():
            p1 = p1o.get_node_name()
            p2 = p2o.get_node_name()
            mmi.append((p1, p2, val))
        return mmi


if __name__ == "__main__":

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsIVV_Config")

    ppi_hash = Data_Struct.Hash2.Hash("A")
    ppi_hash.read_file_hd(rsc.KnownPPI_Hsap,
                          Key_cols_hd = ["Gene ID 1", "Gene ID 2"],
                          Val_cols_hd = ["PubMed ID"])

    ppi = Graph.Graph()
    ppi.read_hash_tab2(ppi_hash, "")

    mmi = MMI_Pred()
    mmi.set_PPI(ppi)
    mmi.set_motif_info(rsc.GeneID2Pfam)
    mmi.pred_mmi_from_ppi()

    for m1, m2, val in mmi.get_all_mmi():
        print m1, m2, val

#    print mmi.get_mmi_val("bZIP_1", "bZIP_2")
