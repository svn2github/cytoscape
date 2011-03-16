#!/usr/bin/env python

import PosMap_Spliced3

import Seq_Packages.Homology.read_BLAT_psl3 as BLAT

class Single_Map:
    def __init__(self, id, chr, strand, q_s, q_e, s_s, s_e):
        self.id = id
        self.chr = chr
        self.strand = strand
        self.q_s = q_s
        self.q_e = q_e
        self.s_s = s_s
        self.s_e = s_e
        
    def get_id(self):
        return self.id
    
    def get_chr(self):
        return self.chr
    
    def get_strand(self):
        return self.strand
        
    def q_start(self):
        return self.q_s
    
    def q_end(self):
        return self.q_e
    
    def s_start(self):
        return self.s_s
    
    def s_end(self):
        return self.s_e

    def __repr__(self):
        return "Single_Map(%s)" % ",".join((self.id, self.chr, self.strand,
                                            `self.q_s`, `self.q_e`, `self.s_s`, `self.s_e`))

class Map_Info:
    def __init__(self, id):
        self.id = id
        self.map = []
        self.chr = None
        self.strand = None     
        
    def add_map(self, chr, strand, q_s, q_e, s_s, s_e):
        if self.chr is None:
            self.chr = chr
        elif self.chr != chr:
            raise "Chromosome mismatch."
  
        if self.strand is None or strand is None:
            self.strand = strand
        elif self.strand != strand:
            raise "Strand mismatch."
        
        self.map.append(Single_Map(self.id,
                                   chr, strand,
                                   q_s, q_e,
                                   s_s, s_e))
        
    def ret_qsmaps(self):
        qsmaps = PosMap_Spliced3.QS_Maps("Genomic region around %s" % self.id)
        qsmaps.add_map_info(self)
        return qsmaps
    
    def map_q_segms_to_subj(self, segms_h):
        qsmaps = self.ret_qsmaps()
        for segm_name in segms_h:
            start, end = segms_h[segm_name]
            qsmaps.add_q_segm(start, end, segm_name)
        return qsmaps.map_q_segms_to_subj_h()
        
    def get_map(self):
        return self.map
    
    def get_chr(self):
        return self.chr
    
    def get_strand(self):
        return self.strand
    
    
class Map_Infos:
    def __init__(self):
        self.maps = {}
        
    def add_map(self, id, chr, strand, q_s, q_e, s_s, s_e):
        if id not in self.maps:
            self.maps[id] = Map_Info(id)
        self.maps[id].add_map(chr, strand, q_s, q_e, s_s, s_e)
        
    def map_q_segms_to_subj(self, id, segms_h):
        return self.maps[id].map_q_segms_to_subj(segms_h)
    
    def __getitem__(self, id):
        return self.maps[id]
    
    def __iter__(self):
        return self.maps.__iter__()
    
    def incorp_BLAT(self, blat_psl):
        # Takes only one hit
        # This method needs to be checked.
        for query_id in blat_psl:
            chr = blat_psl[query_id][0].get_subject_id()
            for block in blat_psl[query_id][0].block_map():
                q_s, q_e, s_s, s_e = block
                if q_s < q_e:
                    strand = "+"
                else:
                    strand = "-"
                self.add_map(query_id, chr, strand, q_s, q_e, s_s, s_e)

    def read_spaln(self, outfile):
        """ Refer to Gotoh O et al. (2008) NAR """
        gap = {}
        for line in open(outfile):
            if line.startswith('#') or line.startswith('@'):
                continue
            r = line.rstrip().split('\t')
            transcr_id = r[0]
            chr        = r[1][3:] # You must configure your output <---
            q_s        = int(r[6])
            q_e        = int(r[7])
            s_s        = int(r[8])
            s_e        = int(r[9])
            if s_s < s_e:
                strand = "+"
            elif s_s > s_e:
                strand = "-"
            else:
                strand = None
            if abs(q_e - q_s) != abs(s_e - s_s):
                gap[ (transcr_id, q_s, q_e, s_s, s_e) ] = ""
            self.add_map(transcr_id, chr, strand,
                         q_s, q_e, s_s, s_e)
        return gap

if __name__ == "__main__":
    map_info = Map_Info("Transcript X")
    map_info.add_map("7", "+", 101, 200, 1101, 1200)
    map_info.add_map("7", "+", 301, 400, 1301, 1400)
    segms_h = { "CDS #1" : (111, 350),
                "CDS #2" : (390, 450) }
    print map_info.map_q_segms_to_subj(segms_h)
    
    map_infos = Map_Infos()
    map_infos.add_map("TR1", "3", "+", 101, 200, 1101, 1200)
    map_infos.add_map("TR1", "3", "+", 201, 300, 2201, 2300)
    map_infos.add_map("TR2", "4", "-", 301, 400, 5500, 5401)
    map_infos.add_map("TR2", "4", "-", 401, 500, 4400, 4301)    
    print map_infos.map_q_segms_to_subj("TR2", segms_h)

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsSAT_Config")    

    map_infos = Map_Infos()
    blatres = BLAT.BLAT_psl(rsc.Human11k_Cancer_ONC_Map_hg17_BLAT_all, bestonly = True)
    map_infos.incorp_BLAT(blatres)


    for id in map_infos:
        map_info = map_infos[id]
        for map in map_info.get_map():
            print map.get_id(), map.get_chr(), map.get_strand(), \
                map.q_start(), map.q_end(), map.s_start(), map.s_end()
                
    """

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsSAT_Config")
    map_infos = Map_Infos()
    map_infos.read_spaln(rsc.Human11k_Cancer_ONC_Map_hg17_spaln)
    
    for id in map_infos:
        map_info = map_infos[id]
        print map_info.get_chr()
        for map in map_info.get_map():
            print map.get_id(), map.get_chr(), map.get_strand(), map.q_start(), map.q_end(), map.s_start(), map.s_end()
    """
