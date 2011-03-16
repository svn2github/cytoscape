#!/usr/bin/env python

from Seq_Packages.GenBank.GenBank1 import GenBank_single
from Usefuls.Circular_String import get_csubstr
from Seq_Packages.Seq.Useful_Seq1 import reverse_complement

class Analysis:
    def __init__(self, gbkfile):
        self.gbk = GenBank_single(gbkfile)
    
    def subseq(self, start, end, cflag = False):
        substr = get_csubstr(self.gbk.get_origin().get_seq(),
                             start, end)
        if cflag:
            substr = reverse_complement(substr)
            
        return substr
    
    def startsite_seq(self, cds_region, 
                      up_range, dn_range):
        
        compl = cds_region.ret_complement()
        
        if compl:
            seq   = self.gbk.get_origin().get_cseq()
            start = len(seq) - cds_region.region_end()
        else:
            seq   = self.gbk.get_origin().get_seq()
            start = cds_region.region_start() - 1
                          
        seq_frag_up = get_csubstr(seq, start + up_range, start)
        start_codon = get_csubstr(seq, start, start + 3)
        seq_frag_dn = get_csubstr(seq, start + 3, start + dn_range)
        seq_frag    = get_csubstr(seq, start + up_range, start + dn_range)
        
        return seq_frag_up, start_codon, seq_frag_dn
    
    def startsite_info(self):
        while(self.gbk.reader()):
            features = self.gbk.get_features()
            cdsno = 0
            for cds in features.get_feature_set_by_key("CDS"):
                cds_region  = cds.get_region()
                cds_region_info = cds.get_region_info_h()
                cds_gene = ""
                if "gene" in cds_region_info:
                    cds_gene    = cds_region_info["gene"][0][1:-1]
                cds_product = ""
                if "product" in cds_region_info:
                    cds_product = cds_region_info["product"][0][1:-1]
                up_seq, scodon, dn_seq = self.startsite_seq(cds_region, -200, +101)
                print "\t".join((`cdsno`, cds_gene, cds_product, up_seq, scodon, dn_seq))
                cdsno += 1
    
if __name__ == "__main__":
    
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("GenBank")

    analysis = Analysis(rsc.Ecoli)
    analysis.startsite_info()
