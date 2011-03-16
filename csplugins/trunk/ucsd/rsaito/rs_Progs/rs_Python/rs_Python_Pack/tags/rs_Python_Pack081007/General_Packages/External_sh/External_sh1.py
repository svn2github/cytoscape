#!/usr/bin/env python

import tempfile
import string
import os

class External_sh:
    def __init__(self):
        self.param = None
        self.sh_tmpfile = tempfile.mktemp()
        self.sh_fileobj = open(self.sh_tmpfile, "w")
        self.sh_fileobj.write(self.script.__doc__)
        self.sh_fileobj.flush()
        self.sh_fileobj.close()
        os.chmod(self.sh_tmpfile, 0700)

    def set_param(self, *param):
        self.param = param

    def __del__(self):
        os.remove(self.sh_tmpfile)
        pass

    def output(self):
        fh = os.popen(self.sh_tmpfile + " " +
                      string.join(self.param, " "), "r")
        res = fh.readlines()
        fh.close()
        return res

    def script(self):
        """#!/usr/bin/perl

use strict;
use G;

my $gb = new G($ARGV[0], "no msg");       

my $i = 1; 
while(defined(%{$gb->{"CDS$i"}})){ 

    my $feature_num = $gb->{"CDS$i"}->{"feature"};
    my $protein_seq = translate($gb->get_cdsseq("CDS$i"));
    $protein_seq =~ s/\/$//g;
    print $gb->{"FEATURE$feature_num"}->{"gene"}, "\t", $protein_seq, "\n";
   
    $i ++; 
} 


$gb->DESTROY();
"""



if __name__ == "__main__":
    ext_scr = External_sh()
    ext_scr.set_param("/pub/dnadb/ncbi2/genomes/Bacteria/Escherichia_coli_K12/NC_000913.gbk")
    print ext_scr.output()
