#!/usr/bin/env python

def parse_FASTA_header_ids(header):
    ret = {}
    if header.startswith(">"):
        header = header[1:]
    
    type_ids = header.split("|")
    
    while len(type_ids) >= 2:
        idtype = type_ids.pop(0)
        id     = type_ids.pop(0)
        
        if idtype in ret:
            ret[ idtype ].append(id)
        else:
            ret[ idtype ] = [ id ]
    
    return ret

    
if __name__ == "__main__":
    
    print parse_FASTA_header_ids(">ref|NM_001033713|gb|BC096598|gb|U30823|gb|AK052385")
    