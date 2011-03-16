#!/usr/bin/env python

import sys

from Data_Struct.DictSet1 import DictSet
from Usefuls.Instance_check import instance_class_check
import Exon_Intron_Scaling2
import Map_Info1

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
            abs(q_end - q_start) != abs(s_end - s_start)):
            # raise "Segment size not identical."
            sys.stderr.write("Segment size not identical: %s (%d, %d) - (%d, %d) %d\n" %
                             (segm_name, q_start, q_end, s_start, s_end,
                              abs(s_end - s_start) - abs(q_end - q_start)))
        
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
        """ Single position mapping """

        if ((self.q_end - self.q_start) *
            (self.s_end - self.s_start)) > 0:
            sgn = 1
        else:
            sgn = -1

        if (self.q_seq_type == "dna" and
            self.s_seq_type == "dna"):
            return (self.s_start +
                    sgn * (q_pos - self.q_start))

    def map_q_segm_to_subj(self, qm_pos1, qm_pos2):
        """ Single segment mapping.
        All combinations of strand are considered.
        Result is based on the strand of the subject.        
        """

        qm1, qm2 = (min(qm_pos1, qm_pos2),
                    max(qm_pos1, qm_pos2))
        q1, q2 = (min(self.q_start, self.q_end),
                  max(self.q_start, self.q_end))
        s1, s2 = (min(self.s_start, self.s_end),
                  max(self.s_start, self.s_end))

        segm_start = max(qm1, q1)
        segm_end   = min(qm2, q2)

        if segm_start > segm_end:
            return None

        sm1 = self.map_q_pos_to_subj(segm_start)
        sm2 = self.map_q_pos_to_subj(segm_end)

        if self.s_start < self.s_end:
            ret1 = min(sm1, sm2)
            ret2 = max(sm1, sm2)
        else:
            ret1 = max(sm1, sm2)
            ret2 = min(sm1, sm2)
        
        return ret1, ret2


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
        
    def add_map_info(self, map_info):
        # instance_class_check(map_info, Map_Info1.Map_Info)
        for map in map_info.get_map():
            self.add_map(map.q_start(),
                         map.q_end(),
                         map.s_start(),
                         map.s_end(),
                         map.get_id())
        
    def map_q_segm_to_subj(self, q_pos1, q_pos2):
        """ Refers to all the mappings and maps the given segment. """
        ret = []
        for qs_map in self.qs_maps:
            map_result = qs_map.map_q_segm_to_subj(q_pos1, q_pos2)
            if map_result:
                ret.append(map_result)
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
    
    def map_q_segms_to_subj_h(self):
        """ qs_maps not returned. """
        ret = DictSet()
        for segm in self.q_segm:
            q_pos1, q_pos2, segm_name = segm
            for mapped_pos in self.map_q_segm_to_subj(q_pos1, q_pos2):
                m_pos1, m_pos2 = mapped_pos
                ret.append(segm_name, (m_pos1, m_pos2))

        for segm_name in ret:
            if self.qs_maps[0].s_start < self.qs_maps[0].s_end:
                ret[segm_name].sort(lambda x, y:x[0] - y[0])
            else:
                ret[segm_name].sort(lambda x, y:y[1] - x[1])
        return ret

    def output_Exon_Intron_Scaling(self):
        gss = Exon_Intron_Scaling2.Genome_Segm_Struct(self.whole_region_name)
        for mapped_segms in self.map_q_segms_to_subj():
            m_pos1, m_pos2, segm_name = mapped_segms
            p1, p2 = (min(m_pos1, m_pos2), max(m_pos1, m_pos2))
            gss.add_segm(p1, p2, segm_name)
        return gss
    
    def simple_file_reader(self, filename): # Under construction?
        for line in open(filename, "r"):
            r = line.rstrip().split("\t")
            if len(r) == 3:
                pos1, pos2, segm_name = int(r[0]), int(r[1]), r[2]
                self.add_q_segm(pos1, pos2, segm_name)
            elif len(r) == 5:
                q_pos1, q_pos2, s_pos1, s_pos2, segm_name = \
                    int(r[0]), int(r[1]), int(r[2]), int(r[3]), r[4]
                # Needs mapping?

if __name__ == "__main__":
    maps = (("++", 101, 200, 1101, 1200),
            ("+-", 101, 200, 1200, 1101),
            ("-+", 200, 101, 1101, 1200),
            ("--", 200, 101, 1200, 1101))
    
    for map in maps:
        sname, qs, qe, ss, se = map
        qsmap = QS_Map(qs, qe, ss, se, sname)
        q = 111
        q11, q12 = (120, 111)
        q21, q22 = ( 51, 110)
        print "Segment name:", qsmap.get_segm_name()
        print "Query pos:", qsmap.get_query_poss()
        print "Subj pos :", qsmap.get_subj_poss()    
        print qsmap.map_q_pos_to_subj(q)
        print qsmap.map_q_segm_to_subj(q11, q12)
        print qsmap.map_q_segm_to_subj(q21, q22)
        print
    
    qsmaps = QS_Maps("Genome")
    qsmaps.add_map(101, 200, 1200, 1101, "Exon #1")
    qsmaps.add_map(301, 400, 1000, 901,  "Exon #2")
    print qsmaps.map_q_segm_to_subj(151, 350)
    qsmaps.add_q_segm(151, 350, "CDS #1")
    qsmaps.add_q_segm( 10,  20, "CDS #2")
    qsmaps.add_q_segm( 90, 700, "CDS #3")
    print qsmaps.map_q_segms_to_subj_h()
    
    gss = qsmaps.output_Exon_Intron_Scaling()
    for segm in gss.scale_II(2.0, 0.5, 200, 100):
        print "\t".join((`segm[0]`, `segm[1]`, segm[2]))
    print
    
