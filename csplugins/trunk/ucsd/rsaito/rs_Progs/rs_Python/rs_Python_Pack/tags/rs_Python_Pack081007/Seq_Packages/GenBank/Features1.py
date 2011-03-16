#!/usr/bin/env python

from KeyPattern import *

class Feature_Each:
    def __init__(self):
        self.lines = []
        self.feaname = None

    def reader(self, fh):
        
        # Feature name expected in the first line.
        line = fh.readline()
        if(not line.startswith(feaspace5) or
           line[5].isspace()):
            fh.seek(-len(line), 1)
            raise "No feature name found: (%s)" % line
        else:
            self.feaname = fea21_search.search(line[:21]).group(1)
            self.lines.append(line[21:].rstrip())

        while True:
            line = fh.readline()
            if line == "":
                return None              
            elif(not line.startswith(feaspace21)): 
                fh.seek(-len(line), 1)
                return line.rstrip() # This feature ends. Maybe next feature.
            
            self.lines.append(line[21:].rstrip()) # Reads feature info.

    def get_feaname(self):
        return self.feaname

    def get_lines(self):
        return self.lines

    def get_both_ends_simple(self):
        d_match = both_ends_d_search_simple.search(self.get_lines()[0])
        if d_match:
            return (int(d_match.group(1)), 
                    int(d_match.group(2)), "d")
        c_match = both_ends_c_search_simple.search(self.get_lines()[0])
        if c_match:
            return (int(c_match.group(1)),
                    int(c_match.group(2)), "c")
        return None



class Features:
    def __init__(self):
        self.features = []

    def reader(self, fh):
        while True: # Find "FEATURES"
            line = fh.readline()
            if(line == "" or 
               line.startswith(basecount) or
               line.startswith(origin) or
               line.startswith(entry_end)):   
                return None
            if line.startswith(feastart):
                break # "FEATURE" found.
        
        count = 0
        while True:
            fe = Feature_Each()
            line = fe.reader(fh)
            feaname = fe.get_feaname()
            # print count, fe.get_feaname(), fe.get_lines()
            if feaname is not None:
                self.features.append(fe)
                if feaname in vars(self):
                    self.__dict__[ feaname ].append(fe)
                else:
                    self.__dict__[ feaname ] = [ fe ]
                
            if(line is None or line == "" or
               line.startswith(basecount) or
               line.startswith(origin) or
               line.startswith(entry_end)):
                break

        if line.startswith(origin):
            return line
        else:
            return None

    def get_feature_set(self):
        return self.features

    def get_feature_set_by_key(self, feakey):
        if feakey in vars(self):
            return getattr(self, feakey)
        else:
            return []

if __name__ == "__main__":

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("GenBank")
    
    features = Features()
    fh = open(rsc.M_genitalium, "r")
    features.reader(fh)
    for cds in features.get_feature_set_by_key("CDS"):
        print cds.get_both_ends_simple()
    
    
