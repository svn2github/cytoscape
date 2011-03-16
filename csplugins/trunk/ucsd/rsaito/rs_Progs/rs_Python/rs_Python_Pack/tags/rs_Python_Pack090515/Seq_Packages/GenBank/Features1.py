#!/usr/bin/env python

from KeyPattern import *
from Data_Struct.DictSet1 import DictSet
from Region1 import *

class Feature_Each:
    def __init__(self):
        self.lines         = []
        self.feaname       = None
        self.region        = None
        self.region_info_h = None
        self.region_info_l = None

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

        final_line = "Not determined"
        while final_line == "Not determined":
            line = fh.readline()
            if line == "":
                final_line = None              
            elif(not line.startswith(feaspace21)): 
                fh.seek(-len(line), 1)
                final_line = line # This feature ends. Maybe next feature.
                final_line.rstrip()
            else:
                self.lines.append(line[21:].rstrip()) # Reads feature info.

        self.parse_lines()
        return final_line

    def get_feaname(self):
        return self.feaname

    def get_lines(self):
        return self.lines

    def split_lines(self):
        region_info = []
        left_bracket_balance = 0
        right_bracket_balance = 0
        dquote_balance = 0
        lines_split = []
        
        for line in self.get_lines():
            region_info.append(line)
                
            left_bracket_balance += line.count("(")
            right_bracket_balance += line.count(")")
            dquote_balance += line.count("\"")
            
            if (left_bracket_balance == right_bracket_balance and
                dquote_balance % 2 == 0):
                lines_split.append(region_info[:])
                region_info = []
                left_bracket_balance = 0
                right_bracket_balance = 0
                dquote_balance = 0
        
        return lines_split
    
    def parse_lines(self):
        
        lines_split = self.split_lines()
        lines_split_joined = []
        for lines in lines_split:
            feamatch = feature_annot.match(lines[0])
            if feamatch:
                featype  = feamatch.group(1)
            if feamatch and featype in multi_line_space:
                lines_split_joined.append(" ".join(lines))
            else:
                lines_split_joined.append("".join(lines))
                
        region = lines_split_joined[0]
        region_infos = lines_split_joined[1:]
        region_info_h = DictSet()
        region_info_l = []
        
        for region_info in region_infos:
            feamatch = feature_annot.match(region_info)
            if feamatch:
                fea_key = feamatch.group(1)
                fea_val = feamatch.group(2)
                region_info_h.append(fea_key, fea_val)
            elif region_info[0] == "/":
                region_info_h.append(region_info[1:], "")
            else:
                region_info_l.append(region_info)
        
        self.region = Simple_Region(region)
        self.region_info_h = region_info_h
        self.region_info_l = region_info_l
        
    def get_region(self):
        return self.region
    
    def get_region_info_h(self):
        return self.region_info_h
    
    def get_region_info_l(self):
        return self.region_info_l

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
    """ Simple method name not recommended. """
    
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
    fh = open(rsc.Ecoli, "r")
    features.reader(fh)
    for cds in features.get_feature_set_by_key("CDS"):
        # print cds.get_both_ends_simple()
        print cds.get_region_info_h()

