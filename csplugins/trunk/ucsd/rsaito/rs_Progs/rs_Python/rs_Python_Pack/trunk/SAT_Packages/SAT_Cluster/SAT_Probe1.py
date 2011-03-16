#!/usr/bin/env python

from General_Packages.Obj_Oriented.Obj_Factory1 import Obj_Factory

class Probe:
    def __init__(self, id):
        self.id = id
        
    def get_id(self):
        return self.id
    
    def set_chromosome(self, chr, strand):
        self.chr = chr
        self.strand = strand
    
    def set_transcript(self, transcript):
        self.transcript = transcript
        
    def set_genomic_map_pos(self, positions):
        self.g_positions = positions
        
    def set_transcript_map_pos(self, positions):
        # Maybe not used for C's lab viewer
        self.t_positions = positions

    def get_transcript(self):
        return self.transcript

    def get_genomic_map_pos(self):
        return self.g_positions

    def get_genomic_map_pos_squash(self):
        ret = []
        for position in self.g_positions:
            start, end = position
            ret.append(`start`)
            ret.append(`end`)
        return ret
        
    def get_chromosome(self):
        return self.chr, self.strand

        
class Probe_Factory(Obj_Factory):
    def set_classobj(self):
        self.classobj = Probe


if __name__ == "__main__":
    pfac = Probe_Factory()
    t1 = pfac.make("AAA")
    t2 = pfac.make("BBB")
    t3 = pfac.make("AAA")
    print id(t1), id(t2), id(t3)
