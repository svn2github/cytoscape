#!/usr/bin/env python

from Seq_Packages.Seq.Transl1 import codon_table_std
from Usefuls.DictProc1 import rev_key_val_redund
from Usefuls.String_I import str_num_match_simple

rev_codon_table_std = rev_key_val_redund(codon_table_std)

codon_amino_match_score_rs1_rec = {}


def codon_amino_match_score_rs1(codon, amino):
    
    if (codon, amino) in codon_amino_match_score_rs1_rec:
        # print codon, amino, "already registered."
        return codon_amino_match_score_rs1_rec[(codon, amino)]
    
    rev_codons = rev_codon_table_std[amino]
    max = 0
    for rev_codon in rev_codons:
        matches = str_num_match_simple(codon, rev_codon)
        if matches > max:
            max = matches
            
    codon_amino_match_score_rs1_rec[(codon, amino)] = max
    return max


class NucProt_Align:
    def __init__(self, nucseq, protseq):
        self.nucseq  = nucseq
        self.protseq = protseq
        self.recur_rec = {}
        self.recur_lcon = {}
        self.recur_gcon = []
        self.nucdel_mark = "-"
        
    def recur_align_main(self, p1, p2):

        if (p1, p2) in self.recur_rec:
            return self.recur_rec[ (p1, p2) ]
 
        max = -1
        self.recur_lcon[(p1, p2)] = None       
        if p2 < len(self.protseq):
            for i in range(p1 - 1, len(self.nucseq)):
                for j in range(i, len(self.nucseq)):
                    for k in range(j, len(self.nucseq)):
                        ccodon = ""
                        if i < p1:
                            ccodon += self.nucdel_mark
                        else:
                            ccodon += self.nucseq[i]
                        if j <= i:
                            ccodon += self.nucdel_mark
                        else:
                            ccodon += self.nucseq[j]
                        if k <= j:
                            ccodon += self.nucdel_mark
                        else:
                            ccodon += self.nucseq[k]
                        
                        camino = self.protseq[p2]
                        score_codn = codon_amino_match_score_rs1(ccodon, camino)
                        score_rest = self.recur_align_main(k+1, p2+1)
                        
                        if score_codn + score_rest > max:
                            max = score_codn + score_rest
                            self.recur_lcon[(p1, p2)] = (i, j, k)
                        print i, j, k, ccodon, camino, score_codn, score_rest
        
        self.recur_rec[ (p1, p2) ] = max
        return max

    def recur_align_conn(self, p1, p2):
        pass


    def lcon_to_gcon(self):
        
        p1 = 0
        p2 = 0
        
        while(p2 < len(self.protseq)):
            i, j, k = self.recur_lcon[(p1, p2)]
            to_codon_point = []
            if i >= p1:
                to_codon_point.append(i)
            else:
                to_codon_point.append(None)
            if j > i:
                to_codon_point.append(j)
            else:
                to_codon_point.append(None)
            if k > j:
                to_codon_point.append(k)
            else:
                to_codon_point.append(None)

            self.recur_gcon.append(tuple(to_codon_point))
                       
            p1 = k  + 1
            p2 = p2 + 1


if __name__ == "__main__":
    
    print rev_codon_table_std
    print codon_amino_match_score_rs1("tgc", "T")
    print codon_amino_match_score_rs1("tac", "T")
    print codon_amino_match_score_rs1("tac", "T")
    
    print codon_amino_match_score_rs1_rec
    
    np_a = NucProt_Align("acagtacg", "ASDLHSQ")
    final_score = np_a.recur_align_main(0, 0)
    print "Final score:", final_score
    np_a.lcon_to_gcon()
    print np_a.recur_gcon
    