#!/usr/bin/env python

import sys

import xml.sax as sax
from xml.sax.handler import ContentHandler

import re

from Usefuls.Table_maker import Table_row
from Homology_term1 import *

class MyHandler(ContentHandler):

    def __init__(self, iolist):
        self._tagstack = []
        self._atrstack = []
        self.iolist = iolist
        self.init()

        self.tb = Table_row()
        self.id_split = re.compile(r'[\s|]', re.IGNORECASE)

    def startElement(self, name, attrs):
        self._tagstack.append(name)
        self._atrstack.append(attrs)
        self._current_chara = ""
        self.start_process()

    def endElement(self, name):
        if not self._current_chara.isspace():
            self.main_process()
        self.end_process()
        self._current_chara = ""
        self._tagstack.pop()
        self._atrstack.pop()

    def characters(self, chara):
        self._current_chara += chara

    def current_name(self):
        if self._tagstack != []:
            return self._tagstack[-1]
        else:
            return None

    def current_attr(self):
        if self._atrstack != []:
            return self._atrstack[-1]
        else:
            return None

    #### The following methods are highly specific to BLAST result processing.

    def init(self):
        self.hit_count = -1

    def main_process(self):

        if self.current_name() == "Iteration_query-def":
            self.query_ID = self._current_chara
        elif self.current_name() == "Iteration_query-len":
            self.query_len = self._current_chara

        if self.hit_count >= 0:
            if ("Hsp" in self._tagstack and
                self.hsp_registered is True):
                return
            tmp_key = self.current_name()
            tmp_val = self._current_chara
            self.iolist[self.hit_count][ tmp_key ] = tmp_val
            if not "|" in self.query_ID:
                     sys.stderr.write(self.query_ID + "\t" +
                                      tmp_key + "\t" + tmp_val + "\n")


    def start_process(self):
        if self.current_name() == "Hit":
            self.hit_count += 1
            self.iolist.append({})
            self.hsp_registered = False

    def end_process(self):

        if self.current_name() == "Hsp":
            self.hsp_registered = True

        elif (self.current_name() == "Iteration" and
              "query_ID" in vars(self) and self.query_ID):
            query_IDs = self.id_split.split(self.query_ID)
            self.tb.append(t_query_ID, query_IDs[1])

            for each_hit in self.iolist:

                subject_IDs = self.id_split.split(each_hit[ "Hit_id" ])
                self.tb.append(t_subject_ID, subject_IDs[1])
                self.tb.append(t_e_value, each_hit[ "Hsp_evalue" ])
                self.tb.append(t_identity_abs, each_hit[ "Hsp_identity"])
                self.tb.append(t_positive_abs, each_hit[ "Hsp_positive" ])
                self.tb.append(t_overlap, each_hit[ "Hsp_align-len" ])
                self.tb.append(t_query_len, self.query_len)
                self.tb.append(t_subject_len, each_hit[ "Hit_len" ])
                self.tb.append(t_query_start, each_hit[ "Hsp_query-from" ])
                self.tb.append(t_query_end, each_hit[ "Hsp_query-to" ])
                self.tb.append(t_subject_start, each_hit[ "Hsp_hit-from" ])
                self.tb.append(t_subject_end, each_hit[ "Hsp_hit-to" ])
                self.tb.output("\t")


            # print self.iolist
            self.iolist = []
            self.hit_count = -1
            self.query_ID = None
            self.query_len = None


class MyHandler2(MyHandler):
    def end_process(self):

        if self.current_name() == "Hsp":
            self.hsp_registered = True

        elif (self.current_name() == "Iteration" and
              "query_ID" in vars(self) and self.query_ID):
            query_IDs = self.id_split.split(self.query_ID)
            self.tb.append("Query ID", query_IDs[1])

            for each_hit in self.iolist:

                subject_IDs = self.id_split.split(each_hit[ "Hit_id" ])
                self.tb.append(t_subject_ID, subject_IDs[1])
                self.tb.append(t_e_value, each_hit[ "Hsp_evalue" ])
                self.tb.append(t_identity_abs, each_hit[ "Hsp_identity"])
                self.tb.append(t_positive_abs, each_hit[ "Hsp_positive" ])
                self.tb.append(t_overlap, each_hit[ "Hsp_align-len" ])
                self.tb.append(t_query_len, self.query_len)
                self.tb.append(t_subject_len, each_hit[ "Hit_len" ])
                self.tb.append(t_query_start, each_hit[ "Hsp_query-from" ])
                self.tb.append(t_query_end, each_hit[ "Hsp_query-to" ])
                self.tb.append(t_subject_start, each_hit[ "Hsp_hit-from" ])
                self.tb.append(t_subject_end, each_hit[ "Hsp_hit-to" ])
                self.tb.append(t_query_aligned_seq, each_hit[ "Hsp_qseq" ])
                self.tb.append(t_subject_aligned_seq, each_hit[ "Hsp_hseq" ])
                self.tb.append(t_match_seq, each_hit[ "Hsp_midline" ])
                self.tb.record()


            # print self.iolist
            self.iolist = []
            self.hit_count = -1
            self.query_ID = None
            self.query_len = None    


def read_XML_SAX(xml_filename, iolist):
    parser = sax.make_parser()
    parser.setFeature(sax.handler.feature_namespaces, 0)
    parser.setFeature("http://xml.org/sax/features/external-general-entities",False) # This is necessary to skip DTD validation.
    parser.setContentHandler(MyHandler(iolist))
    fh = open(xml_filename, 'r')
    parser.parse(fh)
    fh.close()

def read_XML_SAX2(xml_filename):
    parser = sax.make_parser()
    parser.setFeature(sax.handler.feature_namespaces, 0)
    parser.setFeature("http://xml.org/sax/features/external-general-entities",False) # This is necessary to skip DTD validation.
    parser.setContentHandler(MyHandler2([]))
    fh = open(xml_filename, 'r')
    parser.parse(fh)
    fh.close()
    return parser.getContentHandler().tb

if __name__ == "__main__":

    blast_m7_output_file = sys.argv[1]
    if len(sys.argv) <= 2:
        read_XML_SAX(blast_m7_output_file, [])
    else:
        print read_XML_SAX2(blast_m7_output_file).get_record()

