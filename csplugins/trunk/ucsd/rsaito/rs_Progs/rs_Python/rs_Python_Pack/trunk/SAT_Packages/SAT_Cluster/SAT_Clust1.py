#!/usr/bin/env python

from General_Packages.Obj_Oriented.Obj_Factory1 import Obj_Factory

class Cluster:
    def __init__(self, id):
        self.transcripts = [] 
        self.id = id
    
    def set_chromosome(self, chr):
        self.chr = chr
        
    def set_start(self, start):
        self.start = start
        
    def set_end(self, end):
        self.end = end
        
    def get_start(self):
        return self.start
    
    def get_end(self):
        return self.end
        
    def get_chromosome(self):
        return self.chr    
    
    def add_transcript(self, transcript):
        if not transcript in self.transcripts:
            self.transcripts.append(transcript)
        
    def get_transcripts(self):
        return self.transcripts
    
    def def_representative(self, transcr_id):
        self.rep_transcr_id = transcr_id
        
    def get_representative(self):
        return self.rep_transcr_id


class Cluster_Factory(Obj_Factory):
    def set_classobj(self):
        self.classobj = Cluster
        
if __name__ == "__main__":
    cfac = Cluster_Factory()
    t1 = cfac.make("AAA")
    t2 = cfac.make("BBB")
    t3 = cfac.make("AAA")
    print id(t1), id(t2), id(t3)
        

    
    