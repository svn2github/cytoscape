#!/usr/bin/env python

import sys
sys.path.append("../")

import string
import os
import IVV_info
import IVV_filter
from Seq.MultiFasta import MultiFasta_fastacmd
import BioTool1.Fasta1
import Homology.Homology1_descr
import Usefuls.MultiDimDict

class IVV_RefSeq_match:
    def set_db(self, ivv_info, ivvseqdb, refseqdb):
        self.ivv_info = ivv_info
        self.ivvseqdb = ivvseqdb
        self.refseqdb = refseqdb
        self.ivvseq = MultiFasta_fastacmd(self.ivvseqdb)
        self.refseq = MultiFasta_fastacmd(self.refseqdb)
        self.refseq_version = {}

        self.refseqid_valid = Usefuls.MultiDimDict.MultiDimDict(1, 0)
        
    def set_fastacmd_path(self, path):
        self.ivvseq.set_fastacmd_EXEC(path)

    def set_fastaexec_path(self, path):
        self.fastaexec_path = path

    def set_match_result_file(self, filename):
        self.match_result_file = filename

    def match_all(self):
        prey_info = self.ivv_info.Prey_info()

        fh = open(self.match_result_file, "w")
        fh.write(string.join((
            "Query ID", "Subject ID", "E-value",
            "Identity_r", "Positive_r", "Overlap",
            "Query length", "Subject length",
            "Query start", "Query end",
            "Subject start", "Subject end"), "\t") + "\n")

        count = 0
        for ivvseqid in prey_info.preys():
            refseqid = prey_info.get_qual_noerror(ivvseqid, "hit_refseqid")
            if not refseqid:
                continue
            refseqid, version = refseqid.split(".")
            self.refseq_version[ refseqid ] = version
            ss = self.fasta_match(ivvseqid, refseqid)

            if ss != False:
                fh.write(string.join((
                    ivvseqid, refseqid,
                    ss.eval(), ss.ident(), ss.similar(),
                    ss.overlp(), ss.q_len(), ss.s_len(),
                    ss.q_start(), ss.q_end(),
                    ss.s_start(), ss.s_end()), "\t") + "\n")
                self.refseqid_valid.plus_val((refseqid,), 1)
                count += 1

            if count % 1000 == 0:
                sys.stderr.write("Processed " + `count` +
                                 " sequences.\n")

        sys.stderr.write("Processed " + `count` +
                         " sequences.\n")

        fh.close()

    def load_match(self, match_result_file):
        self.match = Homology.Homology1_descr.Homology1_descr()
        """
        self.match.read_homol_file(match_result_file,
                                   terms = [ [ "Query ID" ],
                                             [ "Subject ID",
                                               "E-value",
                                               "Overlap",
                                               "Query length",
                                               "Subject length",
                                               "Query start",
                                               "Query end",
                                               "Subject start",
                                               "Subject end" ]])
                                               """
        self.match.read_homol_file(match_result_file,
                                   terms = [ [ "Query ID" ],
                                             [ "Subject ID",
                                               "Query length",
                                               "Subject length",
                                               "Query start",
                                               "Query end",
                                               "Subject start",
                                               "Subject end" ]])
        return self.match


    def fasta_match(self, ivvseqid, refseqid):
        
        ivvseqfile = self.ivvseq.get_seqfile(ivvseqid)
        refseqfile = self.refseq.get_seqfile(refseqid)

        if os.path.getsize(refseqfile) == 0:
            ret = False

        else:
            ss = BioTool1.Fasta1.Ssearch1()
            ss.set_seq1(ivvseqfile)
            ss.set_seq2(refseqfile)
            ss.set_path(self.fastaexec_path)
            ss.exec_fasta()
            ss.parse_result()
            ret = ss

        os.remove(ivvseqfile)
        os.remove(refseqfile)

        return ret
        
    def output_related_refseqids(self, filename):
        refseqids = self.refseqid_valid.get_all_data().keys()
        refseqid_to_geneid = {}
        refseqid_to_symbol = {}

        prey_info = self.ivv_info.Prey_info()
        for ivvseqid in prey_info.preys():
            refseqid = prey_info.get_qual_noerror(ivvseqid, "hit_refseqid")
            if not refseqid:
                continue
            refseqid = refseqid.split(".")[0]
            geneid = prey_info.geneid(ivvseqid)
            symbol = prey_info.genesymbol(ivvseqid)
            refseqid_to_geneid[ refseqid ] = geneid
            refseqid_to_symbol[ refseqid ] = symbol

        fh = open(filename, "w")
        
        for refseqid in refseqids:
            refseq = MultiFasta_fastacmd(self.refseqdb)
            seqobj = refseq.get_seq(refseqid)
            seqobj.set_ID("lcl|" + refseqid + " " +
                          refseqid_to_geneid[ refseqid ] + " " +
                          refseqid_to_symbol[ refseqid ] + " " +
                          "(" + refseqid + "."
                          + self.refseq_version[refseqid] + ")")
            fh.write(seqobj.return_fasta(60))
                          
        fh.close()

if __name__ == "__main__":

    ivv_info_file = "../../IVV/ivv_human7.3_info"
    ivv_prey_filter = "test_filter"
    ivvseqdb = "../../IVV/ivv_human7.3.tfa"
    refseqdb = "../../../RefSeq/human.rna.fna"
    fastacmd_path = "/pub/software/BLAST/bin/fastacmd"
    fastaexec_path = "/pub/software/FASTA/ssearch34"
    match_result_file = "../../IVV/ivv_human7.3_refseq_match"
    refseq_for_ivv = "../../IVV/ivv_human7.3_refseq.tfa"

    """
    filter = IVV_filter.IVV_filter1()
    filter.set_Prey_filter_file(ivv_prey_filter)

    sys.stderr.write("Reading IVV information ...\n")
    ivv_info = IVV_info.IVV_info(ivv_info_file, filter)
    """
    
    ivm = IVV_RefSeq_match()

    """ This part should be used to calculate
    relationship between IVV and RefSeq
    ivm.set_db(ivv_info, ivvseqdb, refseqdb)
    ivm.set_fastacmd_path(fastacmd_path)
    ivm.set_fastaexec_path(fastaexec_path)
    ivm.set_match_result_file(match_result_file)
    ivm.match_all()
    ivm.output_related_refseqids(refseq_for_ivv)
    """
    
    homol = ivm.load_match(match_result_file)

    query = "T051018_C2_N13.seq"
    subject = "NM_000995"

    """
    ss = ivm.fasta_match(query, subject)
    print ss.q_start(), "-", ss.q_end()
    print ss.s_start(), "-", ss.s_end()
    """

    print homol.query_start(query), homol.query_end(query)
    print homol.subject_start(query), homol.subject_end(query)
