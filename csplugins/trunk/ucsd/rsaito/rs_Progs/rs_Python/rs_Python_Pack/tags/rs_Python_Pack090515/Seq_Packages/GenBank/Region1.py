#!/usr/bin/env python

import KeyPattern

class Simple_Fragment:
    def __init__(self, info_str):
        
        if info_str.find("..") >= 0:
            start = info_str.split("..")[0]
            end   = info_str.split("..")[1]
        else:
            start = info_str
            end   = None
            
        if start:
            if start[0] == ">" or start[0] == "<":
                self.start     = int(start[1:])
                self.start_mod = start[0]
            else:
                self.start     = int(start)
                self.start_mod = None
        else:
            self.start     = None
            self.start_mod = None

        if end:
            if end[0] == ">" or end[0] == "<":
                self.end     = int(end[1:])
                self.end_mod = end[0]
            else:
                self.end     = int(end)
                self.end_mod = None
        else:
            self.end     = None
            self.end_mod = None
            
        
    def get_start(self):
        if self.start_mod:
            return None
        else:
            return self.start
        
    def get_end(self):
        if self.end_mod:
            return None
        else:
            return self.end
            

class Simple_Region:
    def __init__(self, info_str = None):
        self.info_str   = info_str
        self.fragments  = None
        self.complement = None
        self.join       = None
        
        if info_str:
            self.parse(info_str)
    
    def parse(self, info_str):
        self.info_str = info_str
        comp_judge = KeyPattern.complement_search.match(info_str)
        if comp_judge:
            info_str = comp_judge.group(1)
            self.complement = True
        else:
            self.complement = False
        join_judge = KeyPattern.join_search.match(info_str)
        if join_judge:
            info_str = join_judge.group(1)
            self.join = True
        else:
            self.join = False
            
        self.fragments = []
        for frag_info in info_str.split(","):
            self.fragments.append(Simple_Fragment(frag_info))
            
    def get_fragments(self):
        return self.fragments
    
    def region_start(self):
        return self.get_fragments()[0].get_start()
    
    def region_end(self):
        return self.get_fragments()[-1].get_end()
    
    def ret_complement(self):
        return self.complement
    
    def __str__(self):
        return " ".join(("[ Simple region ]",
                         "Start:", `self.region_start()`,
                         "End:",   `self.region_end()`,
                         "Comp:",  `self.ret_complement()`))
    

if __name__ == "__main__":
    
    region_info = "complement(join(>3630875..3631288,<3631289..3632481))"
    region = Simple_Region(region_info)
    for frag in region.get_fragments():
        print frag.get_start(), frag.get_end()
    print region.region_start()
    print region.region_end()
    print region.get_fragments()[1].start_mod
    print region.ret_complement()
    
        
    
