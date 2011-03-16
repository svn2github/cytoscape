#!/usr/bin/env python

""" Using BLAT is easy;
With sequence alignment:
blat -out=pslx subject_DB.fna query.fna outfile

Withoug sequence alignment:
blat -out=psl subject_DB.fna query.fna outfile
"""

import re

NUM_Match = re.compile(r'^\d')

class BLAT_hit:
    def __init__(self, 
                 query_id = None,
                 subject_id = None,
                 query_len = None,
                 subject_len = None,
                 strand = None,
                 query_start = None,
                 query_end = None,
                 subject_start = None,
                 subject_end = None,
                 blocks = None,
                 block_sizes = None,
                 query_starts = None,
                 subject_starts = None,
                 query_aligns = None,
                 subject_aligns = None,
                 matches = None):
        
        self.matches = matches
        self.query_id = query_id
        self.query_len = query_len
        self.query_start = query_start
        self.query_end = query_end
        self.subject_id = subject_id
        self.subject_len = subject_len
        self.subject_start = subject_start
        self.subject_end = subject_end
        self.strand = strand
        self.blocks = blocks
        self.block_sizes = block_sizes
        self.query_starts = query_starts
        self.subject_starts = subject_starts
        self.query_aligns = query_aligns
        self.subject_aligns = subject_aligns      
    
    def get_query_id(self):
        return self.query_id
    
    def get_subject_id(self):
        return self.subject_id
    
    def block_map(self):

        b_map = []
        for i in range(len(self.query_starts)):
            query_start   = self.query_starts[i]
            subject_start = self.subject_starts[i]
            block_size    = self.block_sizes[i]
            if self.strand == "+":
                b_map.append((query_start + 1,
                              query_start + block_size,
                              subject_start + 1,
                              subject_start + block_size))
            elif self.strand == "-":
                b_map.append((self.query_len - query_start,
                              self.query_len - query_start - block_size + 1,
                              subject_start + 1,
                              subject_start + block_size))
            else:
                raise "Strand information blank ..."

        return b_map
                
    def __repr__(self):
        return self.__dict__.__repr__()

class BLAT_psl:
    def __init__(self, pslx_out_file, bestonly = False):
        self.blat_matches    = {}
        self.blat_best_match = {}
        self.read_psl(pslx_out_file, bestonly)
    
    def read_psl(self, pslx_out_file, bestonly = False):    
    
        for line in open(pslx_out_file):
            r = line.rstrip().split("\t")
            if not NUM_Match.search(line) or len(r) < 18:
                continue
            matches        = int(r[ 0])
            strand         = r[ 8]
            query_id       = r[ 9]
            query_len      = int(r[10])
            query_start    = int(r[11])
            query_end      = int(r[12])
            subject_id     = r[13]
            subject_len    = int(r[14])
            subject_start  = int(r[15])
            subject_end    = int(r[16])
            blocks         = int(r[17])
            block_sizes    = map(lambda x:int(x), r[18].rstrip(",").split(","))
            query_starts   = map(lambda x:int(x), r[19].rstrip(",").split(","))
            subject_starts = map(lambda x:int(x), r[20].rstrip(",").split(","))
            # query_aligns   = r[21].rstrip(",").split(",")
            # subject_aligns = r[22].rstrip(",").split(",")

            if query_id not in self.blat_matches:
                self.blat_matches[ query_id ] = []
                self.blat_best_match[ query_id ] = 0
                
            if bestonly is False:
                self.blat_matches[ query_id ].append(
                    BLAT_hit( matches = matches,
                              strand = strand,
                              query_id = query_id,
                              query_len = query_len,
                              query_start = query_start,
                              query_end = query_end,
                              subject_id = subject_id,
                              subject_len = subject_len,
                              subject_start = subject_start,
                              subject_end = subject_end,
                              blocks = blocks,
                              block_sizes = block_sizes,
                              query_starts = query_starts,
                              subject_starts = subject_starts,
                              # query_aligns = query_aligns,
                              # subject_aligns = subject_aligns)
                    ))                                 
                
            if matches > self.blat_best_match[ query_id ]:
                self.blat_best_match[ query_id ] = matches
                if bestonly is True:
                    self.blat_matches[ query_id ] = [
                    BLAT_hit( matches = matches,
                              strand  = strand,
                              query_id = query_id,
                              query_len = query_len,
                              query_start = query_start,
                              query_end = query_end,
                              subject_id = subject_id,
                              subject_len = subject_len,
                              subject_start = subject_start,
                              subject_end = subject_end,
                              blocks = blocks,
                              block_sizes = block_sizes,
                              query_starts = query_starts,
                              subject_starts = subject_starts,
                              # query_aligns = query_aligns,
                              # subject_aligns = subject_aligns
                              ) ]                                               
            
            # print matches, query_id, subject_id, query_align, subject_align

    def __iter__(self):
        return self.blat_matches.__iter__()

    def __getitem__(self, query_id):
        return self.blat_matches[ query_id ]

if __name__ == "__main__":
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsSAT_Config")    

    blatres = BLAT_psl(rsc.Human11k_Cancer_ONC_Map_hg17_BLAT_all, bestonly = True)
    for entry in blatres:
        # print entry, blatres[entry]
        print entry, blatres[entry][0].get_subject_id(), blatres[entry][0].block_map()
        
