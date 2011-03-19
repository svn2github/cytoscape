#!/usr/bin/python

import sys;
import re;
import string;

class pubmed:

    def __init__(self):
        self.pmid = ""
        self.title = ""
        self.authors = []
        self.year = ""
        self.journal = ""
        self.vol = ""
        self.num = ""
        self.page = ""

    def get_pmid(self):
        return self.pmid
    def get_title(self):
        return self.title
    def get_authors(self):
        return self.authors
    def get_year(self):
        return self.year
    def get_journal(self):
        return self.journal
    def get_vol(self):
        return self.vol
    def get_num(self):
        return self.num
    def get_page(self):
        return self.page

    def output_html(self, emp):
        print '<A href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=' + self.get_pmid() + '&query_hl=1&itool=pubmed_docsum">' + "<img src=\"button_gray.gif\">" + "</a>"
        authors = string.join(self.get_authors(), ", ")
        emphasize = re.compile(emp)
        authors_emphasized = \
                           emphasize.sub("<B><U>" + emp + "</U></B>", authors)
        print authors_emphasized
        print "(" + self.get_year() + ")"
        print "<B>" + self.get_title() + "</B>"
        print "<I>" + self.get_journal() + "</I> " + self.get_vol() + \
              "(" + self.get_num() + ")" + ":" + self.get_page()
        print "<p>"

    def output_tab(self):
        print string.join(
            ( self.get_pmid(),
              string.join(self.get_authors(), ","),
              self.get_title(),
              self.get_journal(),
              self.get_vol(),
              self.get_num(),
              self.get_page()
              ),
            "\t"
            )
    

    def record(self, fh):
        
        init = re.compile('^([A-Z ]{4,4})- ')
        next = re.compile('^      ')
        keym = re.compile('^([A-Z]+) *- (.*)')

        rec_hash = {}
        
        record_started = 0
        current_key = ""

        line = "DUMMY"
        while line <> "" and record_started < 2: 
            line = fh.readline()
            linec = string.rstrip(line)    
            if init.match(linec):
                record_started = 1
                key_extract = keym.match(linec)
                current_key = key_extract.group(1)
                value = key_extract.group(2)
                if(current_key in rec_hash):
                    rec_hash[ current_key ].append(value)
                else:
                    rec_hash[ current_key ] = [ value ]
                
            elif next.match(linec) and record_started == 1:
                additional_v = next.sub('', linec)
                lel = len(rec_hash[ current_key ]) - 1
                rec_hash[ current_key ][lel] = \
                          rec_hash[ current_key ][lel] + \
                          " " + additional_v
            elif record_started == 1:
                record_started = 2;
            
#        for key in rec_hash.keys():
#            idx_count = 0
#            for idx in rec_hash[ key ]:
#                print key + "\t" +  `idx_count` + "\t" + idx
#                idx_count = idx_count + 1

        if record_started == 0:
            return record_started

        self.title = rec_hash[ "TI" ][0]
        self.authors = rec_hash[ "AU" ]
        self.pmid = rec_hash[ "PMID" ][0]
        if "PG" in rec_hash:
            self.page = rec_hash[ "PG" ][0]
        self.journal = rec_hash[ "TA" ][0]
        if "VI" in rec_hash:
            self.vol = rec_hash[ "VI" ][0]
        if "IP" in rec_hash:
            self.num = rec_hash[ "IP" ][0]
        if "DP" in rec_hash:
            self.year = rec_hash[ "DP" ][0].split(" ")[0]
            
        return record_started
            
if __name__ == "__main__":
    filename = sys.argv[1]
    fh = open(filename, 'r')
    papers = []
    paper = pubmed();
    while paper.record(fh) > 0:
        papers.append(paper)
        paper = pubmed();

    for p in papers:
        p.output_html("Saito R")


    fh.close()
    
