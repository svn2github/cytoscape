#!/usr/bin/env python

import sys

from Seq_Packages.Seq.Transl1 import codon_table_std
from Seq_Packages.Homology.Fasta_align_pack2 import FASTA_pack_error
from Usefuls.DictProc1 import rev_key_val_redund
from Usefuls.String_I import str_num_match_simple

rev_codon_table_std = rev_key_val_redund(codon_table_std)

codon_amino_match_score_rs1_rec = {}

def codon_amino_match_score_rs1(codon, amino):
                       
    if (codon, amino) in codon_amino_match_score_rs1_rec:
        # print codon, amino, "already registered."
        ca_match_score = codon_amino_match_score_rs1_rec[(codon, amino)]
    else:
        rev_codons = rev_codon_table_std[amino]
        ca_match_score = 0
        for rev_codon in rev_codons:
            matches = str_num_match_simple(codon, rev_codon)
            if matches > ca_match_score:
                ca_match_score = matches    
        codon_amino_match_score_rs1_rec[(codon, amino)] = ca_match_score

    return ca_match_score
    
def codon_gap_simple_optimize(gapcodon, amino):
    
    if gapcodon[0] == "-" and gapcodon.count("-") == 1:
        b1 = gapcodon[1]
        b2 = gapcodon[2]
        possib = ("-" + b1  + b2,
                  b1  + "-" + b2,
                  b1  + b2  + "-")
        scores = [codon_amino_match_score_rs1(possib[0], amino),
                  codon_amino_match_score_rs1(possib[1], amino),
                  codon_amino_match_score_rs1(possib[2], amino)]
        max_score = max(scores)
        max_idx = scores.index(max_score)  
        return possib[max_idx]
        
    else:
        return gapcodon
    

class Pair_nuc_to_amino:
    def __init__(self, nucseq, amnseq, codon_to_amino, amino_to_codon,
                 amino_to_aligned_amino = None,
                 INS = "Ins"):
        
        self.nucseq = nucseq
        self.amnseq = amnseq
        self.codon_to_amino = codon_to_amino
        self.amino_to_codon = amino_to_codon
        self.amino_to_aligned_amino = amino_to_aligned_amino
        self.INS = INS
        

    def investigate_each_by_amino(self):
        
        prot_p_to_codon = {}
        
        for prot_p in range(len(self.amnseq)):

            amino_aln = " "
            if prot_p in self.amino_to_aligned_amino:
                amino_aln = self.amino_to_aligned_amino[ prot_p ]
            amino = self.amnseq[prot_p]
              
            if prot_p in self.amino_to_codon:
                b0p = self.amino_to_codon[prot_p][0]
                b1p = self.amino_to_codon[prot_p][1]
                b2p = self.amino_to_codon[prot_p][2]
                
                if type(b0p) is int:
                    b0 = self.nucseq[b0p]
                elif b0p == self.INS:
                    b0 = "-"
                else:
                    b0 = "?"
                    
                if type(b1p) is int:
                    b1 = self.nucseq[b1p]
                elif b1p == self.INS:
                    b1 = "-"
                else:
                    b1 = "?"    

                if type(b2p) is int:
                    b2 = self.nucseq[b2p]
                elif b2p == self.INS:
                    b2 = "-"
                else:
                    b2 = "?"
                
                codon = b0 + b1 + b2
                codon = codon_gap_simple_optimize(codon, amino_aln)
            else:
                codon = "---"

            prot_p_to_codon[ prot_p ] = codon
        
            trl = codon_table_std.get(codon, "-")
            # print prot_p, amino, codon, trl, amino_aln

        return prot_p_to_codon

    def investigate_each_by_nuc(self):
        
        for nuc_p in range(len(self.nucseq)):
            if nuc_p in self.codon_to_amino and type(self.codon_to_amino[ nuc_p ]) is int:
                amino_p = self.codon_to_amino[ nuc_p ]
                amino = self.amnseq[ amino_p ]
            else:
                amino = "-"
            print nuc_p, self.nucseq[ nuc_p ], amino


class Pair_res_to_res1:
    def __init__(self, qseq, sseq, qseq_to_sseq, sseq_to_qseq,
                 INS = "Ins"):
        
        self.qseq = qseq
        self.sseq = sseq
        
        self.qseq_to_sseq = qseq_to_sseq
        self.sseq_to_qseq = sseq_to_qseq

        self.INS = INS

    def investigate_each_by_query(self):
        
        # print self.qseq_to_sseq
        
        for query_p in range(len(self.qseq)):
            if query_p in self.qseq_to_sseq and type(self.qseq_to_sseq[ query_p ]) is int:
                subj_p = self.qseq_to_sseq[ query_p ]
                sseq_res = self.sseq[ subj_p ]
            else:
                subj_p = "-"
                sseq_res = "-"
            
            print query_p, subj_p, self.qseq[ query_p ], sseq_res


class Integ_Nuc_Prot__Prot_Nuc:
    def __init__(self, nuc_to_prot1, prot_prot, nuc_to_prot2):
        
        self.nuc_to_prot1 = nuc_to_prot1
        self.nuc_to_prot2 = nuc_to_prot2
        self.prot_prot = prot_prot

    def analyze1(self):
        
        ret = []
        
        protseq1 = self.prot_prot.qseq
        protseq2 = self.prot_prot.sseq
        
        prot_to_codon1 = self.nuc_to_prot1.investigate_each_by_amino()
        prot_to_codon2 = self.nuc_to_prot2.investigate_each_by_amino()
        
        for prot_p1 in range(len(protseq1)):
            amino1  = protseq1[ prot_p1 ]
            if prot_p1 in self.prot_prot.qseq_to_sseq and type(self.prot_prot.qseq_to_sseq[ prot_p1 ]) is int:
                prot_p2 = self.prot_prot.qseq_to_sseq[ prot_p1 ]
            else:
                continue
            
            if prot_p1 in prot_to_codon1:
                codon1 = prot_to_codon1[ prot_p1 ]
            else:
                continue
            
            if prot_p2 in prot_to_codon2:
                codon2 = prot_to_codon2[ prot_p2 ]
            else:
                continue   
    
            ret.append((prot_p1, prot_p2, protseq1[prot_p1], protseq2[prot_p2], codon1, codon2))
   
        return ret


def invoke_amino_codon_align1(nucseq1, protseq1, nucseq2, protseq2,
                              nucseq_ID1 = "Nuc#1", protseq_ID1 = "Prot#1",
                              nucseq_ID2 = "Nuc#2", protseq_ID2 = "Prot#2"):
    
    from Seq_Packages.Seq.SingleSeq2 import SingleSeq
    from Fasta_align_pack2 import FastY, Ssearch
    
    try:
        fy = FastY()
        # s1 = SingleSeq("atg atg atg ccc ccc at ccc ccc ccc tag atg atg atg atg atg")
        # """             M   M   M   P   P      P   P   P   Y   M   M   M   M   M   """
        nuc1 = SingleSeq(nucseq1)
        nuc1.set_ID(nucseq_ID1)
        # s2 = SingleSeq("MMMPPPPPYMMMMMM")
        prt1 = SingleSeq(protseq1)    
    
        prt1.set_ID(protseq_ID1)
        fy.set_singleseq_obj(nuc1, prt1)
            
        fy.exec_fasta()
        # fy.display_raw_result()
        
        align_nuc_prt1 = fy.get_disp_alignment()
       
        pair_nuc_prt1 = fy.codon_amino_corresp()
        # pair_nuc_prt1.investigate_each_by_nuc()
        # print "---"
        # print pair_nuc_prt1.investigate_each_by_amino()
       
        fy = FastY()
        # s1 = SingleSeq("atg atg atg ccc ccc at ccc ccc ccc tag atg atg atg atg atg")
        # """             M   M   M   P   P      P   P   P   Y   M   M   M   M   M   """
        nuc2 = SingleSeq(nucseq2)
        nuc2.set_ID(nucseq_ID2)
        prt2 = SingleSeq(protseq2)    
    
        prt2.set_ID(protseq_ID2)
        fy.set_singleseq_obj(nuc2, prt2)
            
        fy.exec_fasta()
        # fy.display_raw_result()
        
        align_nuc_prt2 = fy.get_disp_alignment()
       
        pair_nuc_prt2 = fy.codon_amino_corresp()
        # pair_nuc_prt2.investigate_each_by_nuc()
        # print "---"
        # print pair_nuc_prt2.investigate_each_by_amino()
       
        ss = Ssearch()
        ss.set_singleseq_obj(prt1, prt2)
        ss.exec_fasta()
        # ss.display_raw_result()
        
        align_prt_prt = ss.get_disp_alignment()
       
        prot_prot = ss.residue_residue_corresp()
        
        integ = Integ_Nuc_Prot__Prot_Nuc(pair_nuc_prt1, prot_prot, pair_nuc_prt2)
        info = integ.analyze1()
        
        return info, align_prt_prt, align_nuc_prt1, align_nuc_prt2
    
    except FASTA_pack_error, info:
        sys.stderr.write(info.__str__() + "\n")
        raise FASTA_pack_error, info
    

if __name__ == "__main__":
    from Seq_Packages.Seq.SingleSeq2 import SingleSeq
    from Fasta_align_pack2 import FastY, Ssearch
    
    info, align_prot_prot, align_nuc_prot1, align_nuc_prot2 \
        = invoke_amino_codon_align1(nucseq1 = """atg gcggccagca ggaggctgat gaaggagctt gaagaaatcc
gcaaatgtgg c gatgaaaaac ttccgtaaca tccaggttga tgaagctaat ttattgactt
ggcaagggct tattgttcct gacaaccctc catatgataa gggagccttc aatcgaaa
tcaactttcc agcagagtac""",
                             protseq1 = """
MAASRRLMKELEEIRKCGMKNFRNIQVDEANLLTWQGLIVPDNP
PYDKGAFRIEINFPAEYPFKPPKITFKTKIYHPNIDEKGQVCLPVISAENWKPATKTD
QVIQSLIALVNDPQPEHPLRADLAEEYSKDRKKFCKNAEEFTKKYGEKRPVD""",
                             nucseq2 = """atg gcggccagca ggaggctgat gaaggagctt gaagaaatcc
gcaaatgtgg c gatgaaaaac ttccgtaaca ac tccaggttga tgaagctaat ttattgactt
ggcaagggct tattgttcct gacaaccctc catatgataa gggagccttc aatcgaaa
tcaactttcc agcagagtac""",
                             protseq2 = """
MAASRRLMKELEEIRKCGMKNFRNIQVDEANLLTWQGLIVPDNP
PYDKGAFRIEINFPAEYPFKPPKKTKIYHPNIDEKGQVCLPVISAENWKPATKTD
QVIQSLIALVNDPQPEHPLRADLAEEYSKDRKKFCKNAEEFTKKYGEKRPVD""")

    for residue_info in info:
        (amino_num1, amino_num2, 
         amino1, amino2, 
         codon1, codon2) = residue_info
        print "\t".join((`amino_num1`, `amino_num2`,
                         amino1, amino2,
                         codon1, codon2))        

    
    # print codon_amino_match_score_rs1("a-g", "M")
    
    """
    ss = Ssearch()
    s1 = SingleSeq("MQHCCASDLGEDECQHPTIIHMSDMAHWMQHCCASDLGEDECQHPTIIHMSDMAHW")
    s1.set_ID("SeqA")
    s2 = SingleSeq("MQHCCASDLGEQQECQHPTIHMSDMAHWMQHCCASDLDECQHPTIIHMSDMAHW")
    s2.set_ID("SeqB")
    ss.set_singleseq_obj(s1, s2)
    ss.exec_fasta()
    ss.display_raw_result()
    print "\n".join(ss.get_disp_alignment())
    ss.residue_residue_corresp()
    pair_info = ss.residue_residue_corresp()
    pair_info.investigate_each_by_query()
    """
    
    
    
    