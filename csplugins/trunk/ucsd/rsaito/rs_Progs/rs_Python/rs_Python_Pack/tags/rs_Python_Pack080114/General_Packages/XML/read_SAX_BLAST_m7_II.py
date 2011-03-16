#!/usr/bin/env python

import sys

import xml.sax as sax
from xml.sax.handler import ContentHandler

import re

from Usefuls.Table_maker import Table_row


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

        elif self.current_name() == "Iteration":
            query_IDs = self.id_split.split(self.query_ID)
            self.tb.append("Query ID", query_IDs[1])

            for each_hit in self.iolist:

                subject_IDs = self.id_split.split(each_hit[ "Hit_id" ])
                self.tb.append("Subject ID", subject_IDs[1])
                self.tb.append("E-value", each_hit[ "Hsp_evalue" ])
                self.tb.append("Identity_abs", each_hit[ "Hsp_identity"])
                self.tb.append("Positive_abs", each_hit[ "Hsp_positive" ])
                self.tb.append("Overlap", each_hit[ "Hsp_align-len" ])
                self.tb.append("Query length", self.query_len)
                self.tb.append("Subject length", each_hit[ "Hit_len" ])
                self.tb.append("Query start", each_hit[ "Hsp_query-from" ])
                self.tb.append("Query end", each_hit[ "Hsp_query-to" ])
                self.tb.append("Subject start", each_hit[ "Hsp_hit-from" ])
                self.tb.append("Subject end", each_hit[ "Hsp_hit-to" ])
                self.tb.output("\t")


            # print self.iolist
            self.iolist = []
            self.hit_count = -1
            self.query_ID = None
            self.query_len = None


def read_XML_SAX(xml_filename, iolist):
    parser = sax.make_parser()
    parser.setFeature(sax.handler.feature_namespaces, 0)
    parser.setContentHandler(MyHandler(iolist))
    fh = open(xml_filename, 'r')
    parser.parse(fh)
    fh.close()


if __name__ == "__main__":

    blast_m7_output_file = sys.argv[1]

    read_XML_SAX(blast_m7_output_file, [])


