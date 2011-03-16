#!/usr/bin/env python

import Exon_Intron_Scaling2

class QS_Map:
    """ Defines mapping position between single query and single
    subject sequence for one segment. Specific segment in query 
    sequence can be mapped to subject sequence. """
    
    def __init__(self, 
                 q_start, q_end,
                 s_start, s_end,
                 segm_name = "",
                 q_seq_type = "dna",
                 s_seq_type = "dna"):
        if (q_seq_type == s_seq_type and 
            q_end - q_start != s_end - s_start):
            raise "Segment size not identical."
        
        self.q_start    = q_start
        self.q_end      = q_end
        self.s_start    = s_start
        self.s_end      = s_end
        
        self.segm_name = segm_name
        
        self.q_seq_type = q_seq_type
        self.s_seq_type = s_seq_type
        
    def get_segm_name(self):
        return self.segm_name
        
    def get_query_poss(self):
        return self.q_start, self.q_end

    def get_subj_poss(self):
        return self.s_start, self.s_end

    def map_q_pos_to_subj(self, q_pos):
        """ One position mapping """
        if (self.q_seq_type == "dna" and
            self.s_seq_type == "dna"):
            return (self.s_start +
                    q_pos - self.q_start)

    def map_q_segm_to_subj(self, q_pos1, q_pos2):
        """ One segment mapping """
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
    """ Defines mapping positions between single query
    and single subject sequence for multiple segments. """
    
    def __init__(self,
                 whole_region_name = "",
                 q_seq_type = "dna",
                 s_seq_type = "dna"):
        self.qs_maps = []
        self.q_segm  = []
        
        self.whole_region_name = whole_region_name
        
        self.q_seq_type = q_seq_type
        self.s_seq_type = s_seq_type
        
    def add_map(self,
                q_start, q_end,
                s_start, s_end,
                map_segm_name = ""):
        self.qs_maps.append(QS_Map(q_start, q_end,
                                   s_start, s_end,
                                   map_segm_name,
                                   self.q_seq_type,
                                   self.s_seq_type))
        
    def map_q_segm_to_subj(self, q_pos1, q_pos2):
        """ Refers to all the mappings and maps the given segment. """
        ret = []
        for qs_map in self.qs_maps:
            if qs_map.map_q_segm_to_subj(q_pos1, q_pos2):
                ret.append(qs_map.map_q_segm_to_subj(q_pos1, q_pos2))
        return ret 

    def add_q_segm(self, q_pos1, q_pos2, seqm_name = ""):
        self.q_segm.append((q_pos1, q_pos2, seqm_name))
        
    def map_q_segms_to_subj(self):
        ret = []
        for qs_map in self.qs_maps:
            m_pos1, m_pos2 = qs_map.get_subj_poss()
            ret.append((m_pos1, m_pos2, qs_map.get_segm_name()))
        
        for segm in self.q_segm:
            q_pos1, q_pos2, segm_name = segm
            for mapped_pos in self.map_q_segm_to_subj(q_pos1, q_pos2):
                m_pos1, m_pos2 = mapped_pos
                ret.append((m_pos1, m_pos2, segm_name))
        return ret

    def output_Exon_Intron_Scaling(self):
        gss = Exon_Intron_Scaling2.Genome_Segm_Struct(self.whole_region_name)
        for mapped_segms in self.map_q_segms_to_subj():
            m_pos1, m_pos2, segm_name = mapped_segms
            gss.add_segm(m_pos1, m_pos2, segm_name)
        return gss
    
    def simple_file_reader(self, filename):
        for line in open(filename, "r"):
            r = line.rstrip().split("\t")
            if len(r) == 3:
                pos1, pos2, segm_name = int(r[0]), int(r[1]), r[2]
                self.add_q_segm(pos1, pos2, segm_name)
            elif len(r) == 5:
                q_pos1, q_pos2, s_pos1, s_pos2, segm_name = \
                    int(r[0]), int(r[1]), int(r[2]), int(r[3]), r[4]

if __name__ == "__main__":
    qsmap = QS_Map(100, 150, 1000, 1050)
    print qsmap.get_query_poss()
    print qsmap.get_subj_poss()
    print qsmap.map_q_segm_to_subj(110, 120)
    print qsmap.map_q_segm_to_subj(90, 155)
    print qsmap.map_q_segm_to_subj(140, 155)
    print qsmap.map_q_segm_to_subj(151, 155)
    
    qsmaps = QS_Maps("Genome")
    qsmaps.add_map(101, 150, 1001, 1050, "Exon")
    qsmaps.add_map(161, 200, 1161, 1200, "Exon")
    print qsmaps.map_q_segm_to_subj(141, 170)
    qsmaps.add_q_segm(131, 170, "CDS #1")
    qsmaps.add_q_segm(141, 180, "CDS #2")
    print qsmaps.map_q_segms_to_subj()
    gss = qsmaps.output_Exon_Intron_Scaling()
    for segm in gss.scale(1.0, 1.0, 901, 1300):
        print "\t".join((`segm[0]`, `segm[1]`, segm[2]))
    print
    
    import Usefuls.TmpFile
    tmp_obj = Usefuls.TmpFile.TmpFile_III("""
101   150   1001   1050   Exon
161   200   1161   1200   Exon
131   170   CDS_#1
141   180   CDS_#2
""")
    qsmaps = QS_Maps("Genome")
    qsmaps.simple_file_reader(tmp_obj.filename())
    for segm in gss.scale_II(1.0, .5, 100, 100):
        print "\t".join((`segm[0]`, `segm[1]`, segm[2]))
    print
    
    