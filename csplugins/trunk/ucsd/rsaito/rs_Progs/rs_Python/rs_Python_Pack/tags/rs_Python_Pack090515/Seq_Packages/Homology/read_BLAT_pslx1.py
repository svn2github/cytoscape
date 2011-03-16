#!/usr/bin/env python

import re

NUM_Match = re.compile(r'^\d')

class BLAT_pslx:
    def __init__(self, pslx_out_file, bestonly = False):
        self.blat_matches    = {}
        self.blat_best_match = {}
        self.read_pslx(pslx_out_file, bestonly)
    
    def read_pslx(self, pslx_out_file, bestonly = False):    
    
        for line in open(pslx_out_file):
            r = line.rstrip().split("\t")
            if not NUM_Match.search(line) or len(r) < 18:
                continue
            matches        = int(r[ 0])
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
            query_align    = r[21].rstrip(",").split(",")
            subject_align  = r[22].rstrip(",").split(",")

            if query_id not in self.blat_matches:
                self.blat_matches[ query_id ] = []
                self.blat_best_match[ query_id ] = 0
                
            if bestonly is False:
                self.blat_matches[ query_id ].append(
                 { 'matches'        : matches,
                   'query_id'       : query_id,
                   'query_len'      : query_len,
                   'query_start'    : query_start,
                   'query_end'      : query_end,
                   'subject_id'     : subject_id,
                   'subject_len'    : subject_len,
                   'subject_start'  : subject_start,
                   'subject_end'    : subject_end,
                   'blocks'         : blocks,
                   'block_sizes'    : block_sizes,
                   'query_starts'   : query_starts,
                   'subject_starts' : subject_starts,
                   # 'query_align'    : query_align,
                   # 'subject_align'  : subject_align
                    })                                 
                
            if matches > self.blat_best_match[ query_id ]:
                self.blat_best_match[ query_id ] = matches
                if bestonly is True:
                    self.blat_matches[ query_id ] = [
                     { 'matches'        : matches,
                       'query_id'       : query_id,
                       'query_len'      : query_len,
                       'query_start'    : query_start,
                       'query_end'      : query_end,
                       'subject_id'     : subject_id,
                       'subject_len'    : subject_len,
                       'subject_start'  : subject_start,
                       'subject_end'    : subject_end,
                       'blocks'         : blocks,
                       'block_sizes'    : block_sizes,
                       'query_starts'   : query_starts,
                       'subject_starts' : subject_starts,
                       # 'query_align'    : query_align,
                       # 'subject_align'  : subject_align
                        } ]                                               
            
            # print matches, query_id, subject_id, query_align, subject_align

    def __iter__(self):
        return self.blat_matches.__iter__()

    def __getitem__(self, query_id):
        return self.blat_matches[ query_id ]

if __name__ == "__main__":
    blatres = BLAT_pslx("/home/rsaito/TMP/tmp11", bestonly = True)
    for entry in blatres:
        print entry, blatres[entry]
