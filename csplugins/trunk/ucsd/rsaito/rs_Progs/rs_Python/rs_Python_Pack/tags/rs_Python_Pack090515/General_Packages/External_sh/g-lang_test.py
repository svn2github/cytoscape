#!/usr/bin/env python

import sys
from External_sh import External_sh

class G_Prot_Seq(External_sh):
    
    def script(self):
        r"""#!/usr/bin/perl
# This script extracts gene names and their corresponding
# protein sequences from GenBank file of E. coli (NC_000913.gbk)

use strict;
use G;

my $gb = new G($ARGV[0], "no msg");       

my $i = 1; 
for(my $i = 1; defined(%{$gb->{"CDS$i"}});$i ++){

    my $feature_num = $gb->{"CDS$i"}->{"feature"};
    my $cds_seq = $gb->get_cdsseq("CDS$i");
    if(length($cds_seq) % 3 == 0){
	my $protein_seq = translate($cds_seq);
	$protein_seq =~ s/\/$//g;
	print $gb->{"FEATURE$feature_num"}->{"gene"}, "\t", $protein_seq, "\n";
    }

}

$gb->DESTROY();

"""

    def result(self):
        ret = {}
        for each in self.output():
            gene, prot_seq = each.split("\t")
            ret[gene] = prot_seq
        return ret

if __name__ == "__main__":

    gbkfile = sys.argv[1]
    
    g_prot_seq = G_Prot_Seq()
    g_prot_seq.set_param(gbkfile)
    gene2prot_seq = g_prot_seq.result()
    print "Protein sequence for thrA is:", gene2prot_seq["thrA"]
    
