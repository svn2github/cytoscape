#!/usr/bin/env python

import sys
import string
import os

from IVV_Packages.IVV_Info.IVV_info1 import IVV_info
from IVV_Packages.IVV_Info.IVV_filter1 import IVV_filter
from Seq_Packages.Seq.MultiFasta2 import MultiFasta
from Seq_Packages.Seq.Fasta_align_pack2 import Ssearch
from Seq_Packages.Homology.Homology_descr4 import HomologyDescr
from Seq_Packages.Homology.Homology_term1 import *
from General_Packages.Data_Struct.MultiDimDict1 import MultiDimDict

class IVV_RefSeq_match:
    def set_db(self, ivv_info, ivvseqdb, refseqdb):
        self.ivv_info = ivv_info
        self.ivvseqdb = ivvseqdb
        self.refseqdb = refseqdb
        self.ivvseq = MultiFasta(self.ivvseqdb)
        self.refseq = MultiFasta(self.refseqdb)
        self.refseq_version = {}
        self.refseqid_valid = MultiDimDict(1, 0)

    def set_fastacmd_path(self, path):
        """ You can use this method only after setting self.ivvseq """
        self.ivvseq.set_fastacmd_EXEC(path)

    def set_fastaexec_path(self, path):
        self.fastaexec_path = path

    def set_match_result_file(self, filename):
        self.match_result_file = filename

    def match_all(self):
        prey_info = self.ivv_info.Prey_info()

        fh = open(self.match_result_file, "w")
        fh.write(string.join((
            t_query_ID, t_subject_ID,
            t_e_value,
            t_identity_abs,
            t_positive_abs,
            t_overlap,
            t_query_len,
            t_subject_len,
            t_query_start,
            t_query_end,
            t_subject_start,
            t_subject_end), "\t") + "\n")

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
                    `ss.eval()`,
                    `int(ss.ident()   * ss.overlp())`,  # Correct ?
                    `int(ss.similar() * ss.overlp())`,  # Correct ?
                    `ss.overlp()`,  `ss.q_len()`, `ss.s_len()`,
                    `ss.q_start()`, `ss.q_end()`,
                    `ss.s_start()`, `ss.s_end()`), "\t") + "\n")
                self.refseqid_valid.plus_val((refseqid,), 1)
                count += 1

            if count % 1000 == 0:
                sys.stderr.write("Processed " + `count` +
                                 " sequences.\n")

        sys.stderr.write("Processed " + `count` +
                         " sequences.\n")

        fh.close()

    def load_match(self, match_result_file):

        self.match = HomologyDescr(match_result_file)
        return self.match


    def fasta_match(self, ivvseqid, refseqid):

        ivvseq_single = self.ivvseq.get_singlefasta(ivvseqid)
        refseq_single = self.refseq.get_singlefasta(refseqid)

        if refseq_single is None:
            return False

        else:
            ss = Ssearch()
            ss.set_fasta_obj(ivvseq_single, refseq_single)
            ss.exec_fasta()
            ss.parse_result()
            ret = ss

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
            refseq = MultiFasta(self.refseqdb)
            seqobj = refseq.get_singlefasta(refseqid)
            seqobj.set_ID("lcl|" + refseqid + " " +
                          refseqid_to_geneid[ refseqid ] + " " +
                          refseqid_to_symbol[ refseqid ] + " " +
                          "(" + refseqid + "."
                          + self.refseq_version[refseqid] + ")")
            fh.write(seqobj.get_singleseq().return_fasta(60) + "\n")

        fh.close()

if __name__ == "__main__":

    from General_Packages.Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsIVV_Config")

    filter = IVV_filter()
    filter.set_Prey_filter_file(rsc.PreyFilter)

    sys.stderr.write("Reading IVV information ...\n")
    ivv_info = IVV_info(rsc.IVVInfo, filter)

    ivm = IVV_RefSeq_match()
    ivm.set_db(ivv_info, rsc.IVVSeq, rsc.RefSeq_RNA_Human)

    """ This part should be used to calculate
    relationship between IVV and RefSeq

    ivm.set_match_result_file(rsc.HomolIVVRefSeq_Ssearch)
    ivm.match_all()
    ivm.output_related_refseqids(rsc.IVVRefSeq_MatchSeq)

    """


    homol = ivm.load_match(rsc.HomolIVVRefSeq_Ssearch)

    query = "T060407_H07_K03.seq"
    subject = "NM_004082"

    ss = ivm.fasta_match(query, subject)
    print ss.q_start(), "-", ss.q_end()
    print ss.s_start(), "-", ss.s_end()

    print homol.query_start(query, subject), homol.query_end(query, subject)
    print homol.subject_start(query, subject), homol.subject_end(query, subject)
