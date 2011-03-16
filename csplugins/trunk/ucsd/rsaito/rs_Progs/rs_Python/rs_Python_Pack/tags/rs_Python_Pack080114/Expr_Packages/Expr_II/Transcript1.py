#!/usr/bin/env python

from Obj_Oriented.Obj_Factory1 import Obj_Factory
import Probe1
from Data_Struct.Hash2 import Hash

class Transcript:
    def __init__(self, transcriptID):
        self.transcriptID = transcriptID
        self.probes = []
        self.info = {}

    def add_probe(self, probe):
        self.probes.append(probe)

    def get_probes(self):
        return self.probes

    def get_transcriptID(self):
        return self.transcriptID

    def set_expression_data(self, expression_data):
        self.expression_data = expression_data

    def get_expression_data(self):
        return self.expression_data

    def set_info(self, key, val):
        self.info[ key ] = val

    def get_info(self, key):
        return self.info[ key ]

    def expression_pat_all_probes(self):
        pats = []
        probes = self.get_probes()
        for probe in probes:
            pats.append(self.get_expression_data().
                        expression_pat(probe))
        return pats

    def expression_pat_single_probe(self):
        pats = self.expression_pat_all_probes()
        if len(pats) == 1:
            return pats[0]
        else:
            return False

    def expression_pat_all_probes2(self, expression_data):
        pats = []
        probes = self.get_probes()
        for probe in probes:
            pats.append(expression_data.expression_pat(probe))
        return pats

    def expression_pat_single_probe2(self, expression_data):
        pats = self.expression_pat_all_probes2(expression_data)
        if len(pats) == 1:
            return pats[0]
        else:
            return False


class Transcript_Factory(Obj_Factory):
    def set_classobj(self):
        self.classobj = Transcript

    def read_probe_info_from_file(self, probe_info_file,
                                  probeid_label, transcriptid_label,
                                  expr_pat_set):

        expr_hash = Hash("S")
        expr_hash.read_file_hd(filename = probe_info_file,
                                Key_cols_hd = probeid_label,
                                Val_cols_hd = transcriptid_label)

        for probeid in expr_hash.keys():
            probe = Probe1.Probe_Factory().make(probeid)
            transcriptid = expr_hash.val_force(probeid).split("\t")[0]
            transcript = self.make(transcriptid)
            transcript.add_probe(probe)
            transcript.set_expression_data(expr_pat_set)


if __name__ == "__main__":
    from Expression1 import *

    transcript = Transcript("T1000")
    print transcript.get_transcriptID()

    transcript1 = Transcript_Factory().make("T1001")
    transcript2 = Transcript_Factory().make("T1001")
    transcript3 = Transcript_Factory().make("T1003")
    print transcript1.get_transcriptID()
    print transcript2.get_transcriptID()
    print id(transcript1), id(transcript2), id(transcript3)

    transcript1.add_probe("Probe 1")
    transcript1.add_probe("Probe 2")
    print transcript1.get_probes()

    expr1_1 = Single_Expression("Probe 1", 10.3)
    expr1_1.set_cond("Condition 1")
    expr1_2 = Single_Expression("Probe 1", 10.5)
    expr1_2.set_cond("Condition 2")
    expr1_3 = Single_Expression("Probe 1", 10.7)
    expr1_3.set_cond("Condition 3")
    expr_pat1 = Expression_Pat("Probe 1")
    expr_pat1.add_Single_Expression(expr1_1)
    expr_pat1.add_Single_Expression(expr1_2)
    expr_pat1.add_Single_Expression(expr1_3)

    expr2_1 = Single_Expression("Probe 2", 11.3)
    expr2_1.set_cond("Condition 1")
    expr2_2 = Single_Expression("Probe 2", 11.5)
    expr2_2.set_cond("Condition 2")
    expr2_3 = Single_Expression("Probe 2", 11.7)
    expr2_3.set_cond("Condition 3")
    expr_pat2 = Expression_Pat("Probe 2")
    expr_pat2.add_Single_Expression(expr2_1)
    expr_pat2.add_Single_Expression(expr2_2)
    expr_pat2.add_Single_Expression(expr2_3)

    expr_pat_set = Expression_Data()
    expr_pat_set.add_exp_pat(expr_pat1)
    expr_pat_set.add_exp_pat(expr_pat2)

    print transcript1.expression_pat_all_probes2(expr_pat_set)
    print transcript1.expression_pat_single_probe2(expr_pat_set)

    transcript1.set_expression_data(expr_pat_set)

    print transcript1.expression_pat_all_probes()
    print transcript1.expression_pat_single_probe()

    transcript3.set_info("Rin", "Tin")
    print transcript3.get_info("Rin")
