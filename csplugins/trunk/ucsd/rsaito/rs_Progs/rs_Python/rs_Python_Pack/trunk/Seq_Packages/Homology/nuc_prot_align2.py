#!/usr/bin/env python

from Seq_Packages.Seq.Transl1 import codon_table_std
from Usefuls.DictProc1 import rev_key_val_redund
from Usefuls.String_I import str_num_match_simple

rev_codon_table_std = rev_key_val_redund(codon_table_std)

class NucProt_Align:
    def __init__(self, nucseq, protseq):
        self.nucseq  = nucseq
        self.protseq = protseq
        self.recur_rec = {}
        self.recur_lcon = {}
        self.recur_gcon = {}
        
        self.mark_nucdel = "-"
        
        self.mark_gap = '-'
        self.mark_sta_gap = ' '
        self.mark_same_amino = '>'
        
        self.codon_amino_match_score_rs1_rec = {}
        
    def _next_pointer(self, i, j, k, fix = None):
        # i, j, k cannot be attributes; they are used in recursion.
        
        if i > j or j > k:
            raise "Unexpected value order (%d %d %d)" % (i, j, k)
        
        if fix == 2:
            return None
        k += 1
        if k >= len(self.nucseq):
            if fix == 1:
                return None
            else:
                j += 1
                k = j
                if j >= len(self.nucseq):
                    if fix == 0:
                        return None
                    else:
                        i += 1
                        j = i
                        k = i
                        if i >= len(self.nucseq):
                            return None
                        
        return i, j, k
              
    def _set_ijk(self, p1, fix):
        
        i = p1 - 1
        j = p1 - 1
        k = p1 - 1
        
        if fix == 0:
            i = p1
            j = p1
            k = p1
        if fix == 1:
            i = p1 - 1
            j = p1
            k = p1
        if fix == 2:
            i = p1 - 1
            j = p1 - 1
            k = p1  

        return i, j, k

    def get_codon_amin_from_ijk(self, p1, i, j, k):
        
            ccodon = ""
            if i < p1:
                ccodon += self.mark_nucdel
            else:
                ccodon += self.nucseq[i]
            if j <= i:
                ccodon += self.mark_nucdel
            else:
                ccodon += self.nucseq[j]
            if k <= j:
                ccodon += self.mark_nucdel
            else:
                ccodon += self.nucseq[k]

            return ccodon

    def recur_align_main(self, p1, p2, fix = None):

        bug_check_point = (4, 3, 0)

        if (p1, p2, fix) in self.recur_rec:
            return self.recur_rec[ (p1, p2, fix) ]

        i, j, k = self._set_ijk(p1, fix)
        
        max_score = None
        max_score_con_fix = None
        max_score_con_next_p1 = None
        max_score_con_next_p2 = None
        max_i = None
        max_j = None
        max_k = None
        
        while True:
            
            score_codn = self.codon_amino_match_score_rs1(p1, p2, i, j, k)
            
            best_score_con     = None # If this is kept None, it means no further connections are necessary?
            best_score_con_fix = None # If this is kept None, it means no further connections are necessary?
            best_score_con_next_p1 = None
            best_score_con_next_p2 = None
            for next_p1 in range(k+1, len(self.nucseq)):
                for next_p2 in range(p2+1, len(self.protseq)):
                    score_con0 = (+ self.gap_penalty(p1, p2, i, j, k, next_p1, next_p2, next_fix = 0)
                                  + self.recur_align_main(next_p1, next_p2, fix = 0) # Actually, next_fix
                                  + self.upstream_free_penalty(p1, p2, fix, i, j, k, next_p1, next_p2, next_fix = 0))                  
                    score_con1 = (+ self.gap_penalty(p1, p2, i, j, k, next_p1, next_p2, next_fix = 1)
                                  + self.recur_align_main(next_p1, next_p2, fix = 1)
                                  + self.upstream_free_penalty(p1, p2, fix, i, j, k, next_p1, next_p2, next_fix = 1)) 
                    score_con2 = (+ self.gap_penalty(p1, p2, i, j, k, next_p1, next_p2, next_fix = 2)
                                  + self.recur_align_main(next_p1, next_p2, fix = 2)
                                  + self.upstream_free_penalty(p1, p2, fix, i, j, k, next_p1, next_p2, next_fix = 2))                  
                    
                    score_con = max(score_con0, score_con1, score_con2)
                    score_con_fix = [score_con0, score_con1, score_con2].index(score_con) # Be careful for the order.
                    if best_score_con is None or score_con > best_score_con:
                        best_score_con     = score_con
                        best_score_con_fix = score_con_fix
                        best_score_con_next_p1 = next_p1
                        best_score_con_next_p2 = next_p2
                        
                    if (p1, p2, fix) == bug_check_point:
                        print "[p1, p2, fix] = [%d %d %s]; Connection scores for (i, j, k) = (%d %d %d); (next_p1, next_p2) = (%d %d); score_cons:%d %d %d" % \
                            (p1, p2, `fix`, i, j, k, next_p1, next_p2, score_con0, score_con1, score_con2)
                        print "--- Con scores   : nfix0=%d nfix1=%d nfix2=%d" % \
                            (self.recur_align_main(next_p1, next_p2, fix = 0),
                             self.recur_align_main(next_p1, next_p2, fix = 1),
                             self.recur_align_main(next_p1, next_p2, fix = 2))
                        print "--- Gap penalties: nfix0=%d nfix1=%d nfix2=%d" % \
                            (self.gap_penalty(p1, p2, i, j, k, next_p1, next_p2, next_fix = 0),
                             self.gap_penalty(p1, p2, i, j, k, next_p1, next_p2, next_fix = 1),
                             self.gap_penalty(p1, p2, i, j, k, next_p1, next_p2, next_fix = 2))
                        print "--- Upstream free: nfix0=%d nfix1=%d nfix2=%d" % \
                            (self.upstream_free_penalty(p1, p2, fix, i, j, k, next_p1, next_p2, next_fix = 0),
                             self.upstream_free_penalty(p1, p2, fix, i, j, k, next_p1, next_p2, next_fix = 1),
                             self.upstream_free_penalty(p1, p2, fix, i, j, k, next_p1, next_p2, next_fix = 2))
            
            dnstr_fe_penalty = self.downstream_free_penalty(p1, p2, i, j, k)
            
            if best_score_con is None or dnstr_fe_penalty > best_score_con: 
                # None means that we are at the end of sequence
                # 2nd term: Negative value expected.
                score_current = score_codn + dnstr_fe_penalty
                best_score_con_fix = None
                best_score_con_next_p1 = None
                best_score_con_next_p2 = None
            else:
                score_current = score_codn + best_score_con
                
            if (p1, p2, fix) == bug_check_point:
                ccodon = self.get_codon_amin_from_ijk(p1, i, j, k) 
                camino = self.protseq[p2]
                dnstr_aligned_cpos = self.dnstream_first_aligned_codon_pos(p1, i, j, k)
                upstr_aligned_cpos = self.upstream_last_aligned_codon_pos(p1, i, j, k)
                print ("[Checked] func param = (%+2d %+2d fix=%4s) %+2d %+2d %+2d %s %s codon_score=%d dnstr_fe_penalty=%d first_codon_pos=%s last_codon_pos=%s " + 
                       "score=%d score_dnstr_fe=%d bscore_con=%s bfix=%s bcon_np1=%s bcon_np2=%s max=%s") % \
                        (p1, p2, `fix`, i, j, k, ccodon, camino, score_codn, dnstr_fe_penalty, dnstr_aligned_cpos, upstr_aligned_cpos, 
                         score_current, score_codn + dnstr_fe_penalty, 
                         `best_score_con`, `best_score_con_fix`, `best_score_con_next_p1`, `best_score_con_next_p2`, `max_score`)
                        
            if max_score is None or score_current > max_score:
                max_score = score_current
                max_i = i
                max_j = j
                max_k = k
                max_score_con_fix = best_score_con_fix
                max_score_con_next_p1 = best_score_con_next_p1
                max_score_con_next_p2 = best_score_con_next_p2
            
            next_p = np_a._next_pointer(i, j, k, fix)
            if next_p is None:
                break
            i, j, k = next_p


        if (p1, p2, fix) == bug_check_point:
            print "[ Result of function call (%+2d %+2d fix=%4s) ]" % (p1, p2, `fix`),
            print "max i,j,k = %d,%d,%d next-fix=%s, next_p1=%s, next_p2=%s" % (max_i, max_j, max_k,
                                                                            `max_score_con_fix`,
                                                                            `max_score_con_next_p1`,
                                                                            `max_score_con_next_p2`)
        
        self.recur_rec[ (p1, p2, fix) ] = max_score
        self.recur_lcon[(p1, p2, fix) ] = (max_i, max_j, max_k, 
                                           max_score_con_fix,
                                           max_score_con_next_p1,
                                           max_score_con_next_p2)
        return max_score
    
    
    def lcon_to_gcon(self):
        
        p1 = 0
        p2 = 0
        fix = None
        
        while True:
            (i, j, k, next_fix, next_p1, next_p2) = \
                self.recur_lcon[(p1, p2, fix)]
        
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

            self.recur_gcon[p2] = tuple(to_codon_point)
                       
            p1  = next_p1
            p2  = next_p2
            fix = next_fix

            if fix is None:
                break
             
            
    def gcon_to_align(self):
        
        align_nuc = ""
        align_prt = ""
        align_sta = ""
        match_p1 = -1
        match_p2 = -1
        p1 = 0
        p2 = 0
        
        while p2 < len(self.protseq):
            if p2 in self.recur_gcon:
                for i in range(len(self.recur_gcon[p2])):
                    nucpos_in_codon = self.recur_gcon[p2][i]
                    if nucpos_in_codon is not None:
                        new_match_p1 = nucpos_in_codon
                        new_match_p2 = p2
                        diff_p1 = new_match_p1 - match_p1
                        diff_p2 = new_match_p2 - match_p2
                        
                        if match_p1 >= 0 and match_p2 >= 0:
                            align_nuc += self.nucseq[match_p1+1:match_p1+diff_p1]
                            if diff_p2 > 0:
                                align_prt += self.protseq[match_p2+1:match_p2+diff_p2]
    
                            if diff_p1 < diff_p2:
                                align_nuc += self.mark_gap * (diff_p2 - diff_p1)
                            elif diff_p1 > diff_p2:
                                if diff_p2 > 0:
                                    align_prt += self.mark_gap * (diff_p1 - diff_p2)
                                else:
                                    align_prt += self.mark_gap * (diff_p1 - diff_p2 - 1)
                                    
                            align_sta += self.mark_sta_gap * (max(diff_p1, diff_p2) - 1)
                        
                        align_nuc += self.nucseq[ new_match_p1 ]
                        align_sta += `i`
                        if diff_p2 > 0:
                            align_prt += self.protseq[ new_match_p2 ]
                        else:
                            align_prt += self.mark_same_amino
                            
                        match_p1 = new_match_p1
                        match_p2 = new_match_p2
     
            p2 += 1

        return align_nuc, align_prt, align_sta
    
    def upstream_last_aligned_codon_pos(self, p1, i, j, k):
        if k > j:
            return 2
        if j > i:
            return 1
        if i >= p1:
            return 0
        
        return None

    def dnstream_first_aligned_codon_pos(self, p1, i, j, k):
        if i >= p1:
            return 0
        if j > i:
            return 1
        if k > j:
            return 2
               
        return None

    """ Scores and Penalties """

    def gap_penalty(self,
                    p1, p2,
                    i, j, k,
                    next_p1, next_p2, next_fix):
        
        upstr_aligned_codon_pos = self.upstream_last_aligned_codon_pos(p1, i, j, k)
        dnstr_aligned_codon_pos = self.dnstream_first_aligned_codon_pos(p1, i, j, k)
        nuc_interval = next_p1 - k  - 1
        amn_interval = next_p2 - p2 - 1

        return -(nuc_interval + amn_interval)
    
    def upstream_free_penalty(self,
                              p1, p2, fix, i, j, k,
                              next_p1, next_p2, next_fix):
        """ This function should be called from recur_align_main attribute
        where non-base-connection call (fix=None) is strictly restricted;
        ideally, it should be called only for p1, p2, fix = 0, 0, None.
        The penalty would be undefined if no connection downstream. """
        
        return 0 # Below is ignored, but this may be practical.
        
        if fix is not None:
            return 0
        
        upstr_aligned_codon_pos = self.upstream_last_aligned_codon_pos(p1, i, j, k)
        dnstr_aligned_codon_pos = self.dnstream_first_aligned_codon_pos(p1, i, j, k)

        if upstr_aligned_codon_pos is not None:
            return -min(k, p2) # Last connection 
        
        return -min(next_p1, next_p2)
    
    def downstream_free_penalty(self, p1, p2, i,j,k):
        """ This function should be called from recur_align_main attribute
        where non-base-connection call (fix=None) is strictly restricted. """
               
        return 0 # Below is ignored, but this may be practical.    

        upstr_aligned_codon_pos = self.upstream_last_aligned_codon_pos(p1, i, j, k)
        
        if upstr_aligned_codon_pos is not None: # Base connection
            return -min(len(self.nucseq) - k - 1, len(self.protseq) - p2 - 1)
        else: # No base connection at all
            return 0
        

    def codon_amino_match_score_rs1(self, p1, p2, i, j, k):
        
        codon = self.get_codon_amin_from_ijk(p1, i, j, k) 
        amino = self.protseq[p2]
               
        if (codon, amino) in self.codon_amino_match_score_rs1_rec:
            # print codon, amino, "already registered."
            ca_match_score = self.codon_amino_match_score_rs1_rec[(codon, amino)]
        
        else:
            rev_codons = rev_codon_table_std[amino]
            ca_match_score = 0
            for rev_codon in rev_codons:
                matches = str_num_match_simple(codon, rev_codon)
                if matches > ca_match_score:
                    ca_match_score = matches    
            self.codon_amino_match_score_rs1_rec[(codon, amino)] = ca_match_score
        
        if i < j:
            penalty_ij = -1*(j - i - 1)
        elif i == j:
            penalty_ij = -2
        if j < k:
            penalty_jk = -1*(k - j - 1)
        else:
            penalty_jk = -2
        
        return ca_match_score + penalty_ij + penalty_jk

    """ Scores and Penalties (End) """


if __name__ == "__main__":
      
    np_a = NucProt_Align("atcgtgcagtac", "MQLWMQHHLADSLASLALSDLALSDLALQ")

    """
    i, j, k = 0, 0, 0
    while True:
        print i, j, k
        ret = np_a._next_pointer(i, j, k, fix = None)
        if ret is None:
            break
        else:
            i, j, k = ret
    """        
    
    score = np_a.recur_align_main(0, 0, None)
    print
    print "Score:", score
    print "Recursion record :", np_a.recur_rec
    print "Local connection :", np_a.recur_lcon
    # np_a.lcon_to_gcon2()
    # print np_a.recur_gcon2
    np_a.lcon_to_gcon()
    print "Global connection:", np_a.recur_gcon
    print
    
    align_nuc, align_prt, align_sta = np_a.gcon_to_align()
    print "*** Alignment ***"
    print align_nuc
    print align_sta
    print align_prt
    