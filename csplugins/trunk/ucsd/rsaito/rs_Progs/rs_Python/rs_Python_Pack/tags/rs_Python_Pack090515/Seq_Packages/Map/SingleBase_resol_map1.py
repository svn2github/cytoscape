#!/usr/bin/env python

# Single-base resolution mapping

class SBR_Map:
    def __init__(self):
        self.query2subj = {}
        # self.subj2query = {}
    
    def add_base_align_q(self, query_pos, subj):
        self.query2subj[ query_pos ] = subj
        
    # def add_base_align_s(self, query, subj_pos):
    #     self.subj2query[ subj_pos ] = query
        
    def get_query2subj(self, pos):
        return self.query2subj.get(pos, None)
    
    def get_subj2query(self, pos):
        return self.subj2query.get(pos, None)
    
    def get_qs_blocks(self):
        
        q_pos_sorted = self.query2subj.keys()
        q_pos_sorted.sort()
        
        q_start = None
        q_prev = None
        s_start = None
        s_prev = None
        ret = []
        
        for q_pos in q_pos_sorted:
            s_pos = self.get_query2subj(q_pos)
            if q_prev is None:
                q_start = q_pos
                s_start = s_pos
            elif (q_pos - q_prev == 1 and
                  abs(s_pos - s_prev) == 1):
                pass
            else:
                ret.append((q_start, q_prev, s_start, s_prev))
                q_start = q_pos
                s_start = s_pos
            
            q_prev = q_pos
            s_prev = s_pos
            
        ret.append((q_start, q_prev, s_start, s_prev))
                
        return ret
    
    
if __name__ == "__main__":
    
    sbrmap = SBR_Map()
    
    # sbrmap.add_base_align_q(7,  17)
    # sbrmap.add_base_align_q(11, 31)
    # sbrmap.add_base_align_q(12, 32)
    # sbrmap.add_base_align_q(13, 34)
    # sbrmap.add_base_align_q(14, 35)
    # sbrmap.add_base_align_q(15, 36)
    # sbrmap.add_base_align_q(18, 38)
    # sbrmap.add_base_align_q(19, 39)
    # sbrmap.add_base_align_q(20, 40)

    sbrmap.add_base_align_q(11, 60)
    sbrmap.add_base_align_q(12, 59)
    sbrmap.add_base_align_q(14, 56)
    sbrmap.add_base_align_q(15, 55)
    sbrmap.add_base_align_q(18, 53)
    sbrmap.add_base_align_q(19, 52)
    sbrmap.add_base_align_q(20, 51)    
    
    print sbrmap.get_qs_blocks()