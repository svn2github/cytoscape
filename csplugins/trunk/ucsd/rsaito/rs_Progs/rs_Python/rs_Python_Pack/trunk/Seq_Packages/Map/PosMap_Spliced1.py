#!/usr/bin/env python

class QS_Map:
    def __init__(self, 
                 q_start, q_end,
                 s_start, s_end,
                 q_seq_type = "dna",
                 s_seq_type = "dna"):
        self.q_start    = q_start
        self.q_end      = q_end
        self.s_start    = s_start
        self.s_end      = s_end
        self.q_seq_type = q_seq_type
        self.s_seq_type = s_seq_type

    def get_query_poss(self):
        return self.q_start, self.q_end

    def get_subj_poss(self):
        return self.s_start, self.s_end

    def map_q_pos_to_subj(self, q_pos):
        if (self.q_seq_type == "dna" and
            self.s_seq_type == "dna"):
            return (self.s_start +
                    q_pos - self.q_start)

    def map_q_segm_to_subj(self, q_pos1, q_pos2):
        if q_pos1 < self.q_start:
            segm_start = self.q_start
        else:
            segm_start = q_pos1
        if self.q_end < q_pos2:
            segm_end = self.q_end
        else:
            segm_end = q_pos2
        if segm_start <= segm_end:
            return (self.map_q_pos_to_subj(segm_start),
                    self.map_q_pos_to_subj(segm_end))
        else:
            return None

class QS_Maps:
    def __init__(self,
                 q_seq_type = "dna",
                 s_seq_type = "dna"):
        self.qs_maps = []
        self.q_seq_type = q_seq_type
        self.s_seq_type = s_seq_type
        
    def add_map(self,
                q_start, q_end,
                s_start, s_end):
        self.qs_maps.append(QS_Map(q_start, q_end,
                                   s_start, s_end,
                                   self.q_seq_type,
                                   self.s_seq_type))
        
    def map_q_segm_to_subj(self, q_pos1, q_pos2):
        ret = []
        for qs_map in self.qs_maps:
            if qs_map.map_q_segm_to_subj(q_pos1, q_pos2):
                ret.append(qs_map.map_q_segm_to_subj(q_pos1, q_pos2))
        return ret 


if __name__ == "__main__":
    qsmap = QS_Map(100, 150, 1000, 1050)
    print qsmap.get_query_poss()
    print qsmap.get_subj_poss()
    print qsmap.map_q_segm_to_subj(110, 120)
    print qsmap.map_q_segm_to_subj(90, 155)
    print qsmap.map_q_segm_to_subj(140, 155)
    print qsmap.map_q_segm_to_subj(151, 155)
    qsmaps = QS_Maps()
    qsmaps.add_map(100, 150, 1000, 1050)
    qsmaps.add_map(160, 200, 1060, 1200)
    print qsmaps.map_q_segm_to_subj(140, 170)
    
    
    