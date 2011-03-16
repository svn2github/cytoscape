#!/usr/bin/env python

# import sys
import re
from optparse import OptionParser
from Seq_Packages.Seq_Split.Seq_Split_proc1 import Seq_Split_proc, merge_results
from Seq_Packages.Homology.read_SAX_BLAST_m7_II import read_XML_SAX2
from Seq_Packages.Seq.MultiFasta2 import MultiFasta_MEM
from Homology_term1 import *
from Usefuls.String_I import deleter
from Usefuls.ListProc1 import list_extract, list_del_elems, get_indexes
from Log.rsppLog import stamp
from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsBioinfo_Config")

def concatenate_no_head_space(s1, s2):
    if s1 == "":
        return s2
    else:
        return s1 + " " + s2
    
class Seq_Split_proc_blast_m7(Seq_Split_proc):
    def merge(self):
        if self.mode["manual"]:
            return
        
        res_files  = self.res_files
        merge_file = self.param['wfile'] + "result"
        if (self.mode['misc'] and "aseq" in self.mode['misc'].split(",")):
            aseq_file = self.param['wfile'] + "qmatch.fasta"
        else:
            aseq_file = None
        
        merge_results_blast_m7(res_files, merge_file, aseq_file)
        

def merge_results_blast_m7(res_files, merge_file, aseq_file = None):
    mf_query_aligned = MultiFasta_MEM()
    
    header_out = True
    fh = open(merge_file, "w")
    for res_file in res_files:
        tb = read_XML_SAX2(res_file)
        for rec in tb.get_record():
            if header_out:
                (idx_query_ID, idx_query_aligned,
                 idx_subj_aligned, idx_match_seq) = \
                     get_indexes(tb.return_header(), (t_query_ID, t_query_aligned_seq,
                                                      t_subject_aligned_seq, t_match_seq))
                extract_idxs      = list_del_elems(range(len(tb.return_header())),
                                                   (idx_query_aligned, idx_subj_aligned, idx_match_seq))
                rec_out_header = list_extract(tb.return_header(), extract_idxs)
                fh.write("\t".join(rec_out_header) + "\n")
                header_out = False
                
            rec_out = list_extract(rec, extract_idxs)
            fh.write("\t".join(rec_out) + "\n")
            query_aligned_seq = deleter(rec[idx_query_aligned], r"-X*\/") # <-- Watch for this.
            mf_query_aligned.set_sequence(rec[idx_query_ID],
                                          query_aligned_seq)
    fh.close()
    
    if aseq_file:
        fh = open(aseq_file, "w")
        fh.write(mf_query_aligned.out_fasta_all() + "\n")
        fh.close()
        

usage = "usage: %prog [options]"
parser = OptionParser(usage)
parser.add_option("-m", dest = "manual",
                  action = "store_true",
                  help = "Manual mode")
parser.add_option("-c", "--command", dest = "cmd",
                   default = "@blastx",
                   help = "Invoking command")
parser.add_option("-n", dest = "num",
                  default = "4",
                  help = "Number of split files")
parser.add_option("-q", dest = "query",
                  default = rsc.test_query_file,
                  help = "Query FASTA file")
parser.add_option("-s", dest = "subj",
                  default = rsc.test_subj_file,
                  help = "Subject file")
parser.add_option("-o", "--outbasename", dest = "outbasename",
                  default = "/tmp/tmpfile-",
                  help = "Base path name for output files")
# parser.add_option("-p", "--program", dest = "program",
#                   default = "@blastx",
#                   help = "Program (blastx, hmmpfam*).")
parser.add_option("--merge-files-only", dest = "files_to_merge",
                  default = None,
                  help = "Merging of files (file1,file2,...) only")
parser.add_option("--stamp", dest = "stamp",
                  default = "",
                  help = "Manual stamp")
parser.add_option("--mess", dest = "mess",
                  action = "store_true",
                  help = "Leave mess")
parser.add_option("--shuffle", dest = "shuffle",
                  action = "store_true",
                  help = "Sequence shuffling")
parser.add_option("--parse-id-off", dest = "parseid",
                  action = "store_false",
                  default = True,
                  help = "Turns off header ID parser")
parser.add_option("-v", "--verbose", dest = "verbose",
                  action = "store_true",
                  help = "Verbose mode")
parser.add_option("--misc", dest = "misc",
                  default = None,
                  help = "Miscellaneous parameters (separated by commas)")

(options, args) = parser.parse_args()

abr_blastx = re.compile(r'^@blastx($|\s)') # Is this regular expression really proper?
abr_blastp = re.compile(r'^@blastp($|\s)') # Is this regular expression really proper?

Seq_Split_proc_class = Seq_Split_proc
cmd = options.cmd

if abr_blastx.match(options.cmd):
    Seq_Split_proc_class = Seq_Split_proc_blast_m7
    cmd = options.cmd.replace("@blastx",
                              "%s -p blastx -m7 -S 1 -e 1.0e-2 -d %s -i %%s" % (rsc.BLASTALL, options.subj))
    # %%s is one of split query files.
    # %% is necessary because % string arithmetic is used.
    
elif abr_blastp.match(options.cmd):
    Seq_Split_proc_class = Seq_Split_proc_blast_m7
    cmd = options.cmd.replace("@blastp",
                              "%s -p blastp -m7 -S 1 -e 1.0e-30 -d %s -i %%s" % (rsc.BLASTALL, options.subj))
    

if options.verbose:
    print "Options :", options, args
    print "Command :", cmd

if options.files_to_merge:
    issue = stamp(options.stamp)
    result_files = options.files_to_merge.split(",")
    merge_file   = options.outbasename + issue + "_" + "result"
    if abr_blastx.match(options.cmd) or abr_blastp.match(options.cmd):
        if (options.misc and "aseq" in options.misc.split(",")):
            aseq_file = options.outbasename + issue + "_" + "qmatch.fasta"
        else:
            aseq_file = None
        merge_results_blast_m7(result_files, merge_file, aseq_file)
    else:
        merge_results(result_files, merge_file)

else:
    if options.stamp:
        issue = stamp(options.stamp)
    else:
        issue = stamp(cmd)
    ssp = Seq_Split_proc_class(options.query,
                               { 'num'     : int(options.num),
                                 'wfile'   : options.outbasename + issue + "_",
                                 'cmd'     : cmd },
                               { 'parseid' : options.parseid, 
                                 'shuffle' : options.shuffle,
                                 'manual'  : options.manual,
                                 'mess'    : options.mess,
                                 'verbose' : options.verbose,
                                 'misc'    : options.misc })

"""
If you want to just merge all blast results, following may work.
./seq_split_invoke1.py \
 -c "/usr/local/Bioinfo/blast-2.2.20/bin/blastall \
 -p blastx -S 1 -e 1.0e-2 \
 -d /Users/rsaito/UNIX/Work/Research/Data_Public/RefSeq/Human/human.protein.faa \
 -i %s" -q /Users/rsaito/UNIX/Work/Research/Data_Public/RefSeq/Human/human.rna_partial.fna
"""