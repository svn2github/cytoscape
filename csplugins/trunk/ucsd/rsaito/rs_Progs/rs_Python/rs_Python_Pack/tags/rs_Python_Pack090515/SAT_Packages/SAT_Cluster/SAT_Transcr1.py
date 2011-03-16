#!/usr/bin/env python

from General_Packages.Obj_Oriented.Obj_Factory1 import Obj_Factory
# from Seq_Packages.Map.Map_Info1 import Map_Infos

class Transcript:
    def __init__(self, id):
        self.id = id
        self.probes = []
        self.chr = None
        self.strand = None
        self.positions = [] # ((start1, end1), (start2, end2), ...)
    
    def get_id(self):
        return self.id
    
    def set_chromosome(self, chr, strand):
        self.chr = chr
        self.strand = strand
        
    def set_genomic_map_pos(self, positions):
        self.positions = positions
        
    def add_probe(self, probe):
        self.probes.append(probe)
        
    def get_chromosome(self):
        return self.chr, self.strand

    def get_probes(self):
        return self.probes
   
    def get_genomic_map_pos(self):
        return self.positions
    
    def get_genomic_map_pos_squash(self):
        ret = []
        for position in self.get_genomic_map_pos():
            start, end = position
            ret.append(`start`)
            ret.append(`end`)
        return ret
    
    def get_start_end(self):
        min = None
        max = None
        for position_pair in self.get_genomic_map_pos():
            for position in position_pair:
                if min is None or min > position:
                    min = position
                if max is None or max < position:
                    max = position
        
        return min, max
    
        
class Transcript_Factory(Obj_Factory):
    def set_classobj(self):
        self.classobj = Transcript
        

"""
class SAT_Map_Info(Map_Infos):
    def incorp_SAT_transcript_Obj(self, transcript_factory):
        for id in transcript_factory:
            transcript = trancript_factory[id]
            chr, strand = transcript.get_chromosome()
            for pos in transcript.get_genomic_map_pos():
                start, end = pos
"""             


if __name__ == "__main__":
    tfac = Transcript_Factory()
    t1 = tfac.make("AAA")
    t2 = tfac.make("BBB")
    t3 = tfac.make("AAA")
    print id(t1), id(t2), id(t3)
