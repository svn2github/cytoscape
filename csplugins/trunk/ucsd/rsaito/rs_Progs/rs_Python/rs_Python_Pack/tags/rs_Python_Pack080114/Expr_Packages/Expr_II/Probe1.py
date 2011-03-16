#!/usr/bin/env python

from General_Packages.Data_Struct.Hash2 import Hash
from General_Packages.Obj_Oriented.Obj_Factory1 import Obj_Factory
import Transcript1

class Probe:
    def __init__(self, probeid):
        self.probeid = probeid
        self.transcript = None
        self.sequence = None

    def get_probeid(self):
        return self.probeid

    def set_transcript(self, transcript):
        self.transcript = transcript

    def get_transcript(self):
        return self.transcript

    def set_sequence(self, seq):
        self.seq = seq

    def get_sequence(self):
        return self.seq

    def __str__(self):
        return "Probe: " + self.get_probeid()


class Probe_Factory(Obj_Factory):
    def set_classobj(self):
        self.classobj = Probe

    def read_probe_info_from_file(self, probe_info_file,
                                  probeid_label, probeseq_label,
                                  transcriptid_label):

        probes_hash = Hash("S")
        probes_hash.read_file_hd(filename = probe_info_file,
                                   Key_cols_hd = probeid_label,
                                   Val_cols_hd = probeseq_label +
                                   transcriptid_label)

        for probeid in probes_hash.keys():
            probe = self.make(probeid)
            probeseq, transcriptid = \
                probes_hash.val_force(probeid).split("\t")
            probe.set_sequence(probeseq)
            probe.set_transcript(Transcript1.
                                 Transcript_Factory().make(transcriptid))



if __name__ == "__main__":

    probe = Probe_Factory().make("Probe 1")
    probe.set_transcript("AC001")
    print probe.get_probeid()
    print probe.get_transcript()
    probe.set_sequence("ACGT")
    print probe.get_sequence()
