#!/usr/bin/env python

"""
UNDER CONSTRUCTION ...
"""


class Judge:
    def set_hit(self, hit):
        self.hit = hit

    def set_judge_strand(self, judge_strand):
        self.judge_strand = judge_strand
        


class Genomic_Region:
    def __init__(self, region_info,
                 chromosome,
                 strand,
                 start_pos, end_pos):
        
        if start_pos > end_pos:
            raise "Position Error ..."
        
        self.region_info = region_info
        self.chromosome = chromosome
        self.strand = strand
        self.start_pos = start_pos
        self.end_pos = end_pos
        
    def get_region_info(self):
        return self.region_info
        
    def judge_region(self, chromosome, strand, start_pos, end_pos):
        judge = Judge()
        if start_pos > end_pos:
            raise "Position Error for region judgement ..."
        
        if chromosome != self.chromosome:
            judge.set_hit(False)
        else:
            if strand == self.strand:
                judge.set_judge_strand(True)
            else:
                judge.set_judge_strand(False)
                
        