#!/usr/bin/env python

from Seq_Packages.Seq.MultiFasta2 import MultiFasta
import Seq_Packages.Seq.Useful_Seq1 as Useful_Seq
from Usefuls.Fragment_Sizes import Fragment_Sizes
from Usefuls.Table_maker import Table_row

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("Genomes")

def get_genome_frag_mm9(chr, pos_start, pos_end, strand):
    """ pos_start < pos_end. From pos_start to pos_end - 1. """
    
    mf = MultiFasta(rsc.__dict__[ "mm9_" + chr ])
    sf = mf.get_singlefasta(chr, pos_start + 1, pos_end)
    sseq = sf.get_singleseq()
    if strand == "-":
        sseq.reverse_complement()
    return sseq
    
def get_AFAS_mm9(chr, pos_start, pos_end, strand, frag_size, extra = 0, gene_ID = "", info = ""):
    """ pos_start < pos_end. From pos_start to pos_end - 1. """
    
    ### Antisense!! ###
    strand = {'+':'-',
              '-':'+'}[strand]
    
    pos_start_extra = pos_start - extra
    pos_end_extra   = pos_end   + extra
    sseq = get_genome_frag_mm9(chr, pos_start_extra, pos_end_extra, strand)
    
    frag_upstream = sseq.get_seq()[:extra]
    frag_gene     = sseq.get_seq()[extra:-extra]
    frag_dnstream = sseq.get_seq()[-extra:]
    
    output = Table_row()
          
    for pos_range in Fragment_Sizes(extra, frag_size, "First"):
        start, end = pos_range
        rel_start = -extra + start
        rel_end   = -extra + end
        if strand == "+":
            abs_start = pos_start + rel_start
            abs_end   = pos_start + rel_end
        elif strand == "-":
            abs_start = pos_end - rel_end
            abs_end   = pos_end - rel_start
             
        output.append("ID", gene_ID)
        output.append("Region Type",         "Extra Upstream")
        output.append("Relative Start Pos.", `rel_start`)
        output.append("Relative End Pos.",   `rel_end - 1`)
        output.append("Chromosome",          chr)
        output.append("Strand",              strand)
        output.append("Genomic Start Pos.",  `abs_start + 1`)
        output.append("Genomic End Pos.",    `abs_end`)
        output.append("Genomic Fragment",    frag_upstream[start:end])
        output.append("Info", info)
        output.output('\t')
           
    for pos_range in Fragment_Sizes(len(frag_gene), frag_size, "Last"):
        start, end = pos_range
        rel_start  = start
        rel_end    = end
        if strand == "+":
            abs_start = pos_start + rel_start
            abs_end   = pos_start + rel_end
        elif strand == "-":
            abs_start = pos_end - rel_end
            abs_end   = pos_end - rel_start

        output.append("ID", gene_ID)
        output.append("Region Type",         "Gene Region")
        output.append("Relative Start Pos.", `rel_start`)
        output.append("Relative End Pos.",   `rel_end - 1`)
        output.append("Chromosome",          chr)
        output.append("Strand",              strand)
        output.append("Genomic Start Pos.",  `abs_start + 1`)
        output.append("Genomic End Pos.",    `abs_end`)
        output.append("Genomic Fragment",    frag_gene[start:end])
        output.append("Info", info)
        output.output('\t')
        
    for pos_range in Fragment_Sizes(extra, frag_size, "Last"):
        start, end = pos_range
        rel_start  = start
        rel_end    = end
        if strand == "+":
            abs_start = pos_end + rel_start
            abs_end   = pos_end + rel_end
        elif strand == "-":
            abs_start = pos_start - rel_end
            abs_end   = pos_start - rel_start

        output.append("ID", gene_ID)
        output.append("Region Type",         "Extra Down")
        output.append("Relative Start Pos.", `rel_start`)
        output.append("Relative End Pos.",   `rel_end - 1`)
        output.append("Chromosome",          chr)
        output.append("Strand",              strand)
        output.append("Genomic Start Pos.",  `abs_start + 1`)
        output.append("Genomic End Pos.",    `abs_end`)
        output.append("Genomic Fragment",    frag_dnstream[start:end])
        output.append("Info", info)
        output.output('\t')

    
    
if __name__ == "__main__":
    # sseq = get_genome_frag_mm9("chrM", 3, 10, '-')
    # print sseq.get_seq()
    get_AFAS_mm9("testseq", 100, 205, '+', 10, 55)
    