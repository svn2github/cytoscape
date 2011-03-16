#!/usr/bin/env perl

use strict;
use warnings;
use DisruptCff1;

# You need to install Statistics::Distributions from CPAN.
# [root@globin ~]# cpan
# Terminal does not support AddHistory.
#
# cpan shell -- CPAN exploration and modules installation (v1.9402)
# Enter 'h' for help.
#
# cpan[1]> install Statistics::Distributions

use Statistics::Distributions;

my $dcff = new DisruptCff [1,0,0,0,1,1,1,0,0,0],
                          [1,0,1,1,0,1,1,1,1,1];

print $dcff->c00, "\n";
print $dcff->c01, "\n";
print $dcff->c10, "\n";
print $dcff->c11, "\n";
print $dcff->spe, "\n";
print $dcff->mcc, "\n";
print $dcff->sen, "\n";

my $dcff2 = new DisruptCff [1,0,0,0,0],
                           [1,0,1,1,1];

$dcff2->add([1,1,0,1,0],
	    [1,0,1,0,1]);

print $dcff2->c00, "\n";
print $dcff2->c01, "\n";
print $dcff2->c10, "\n";
print $dcff2->c11, "\n";
print $dcff2->spe, "\n";
print $dcff2->mcc, "\n";
print $dcff2->sen, "\n";

my $dcff3 = new DisruptCff;

$dcff3->add([1,0,0,0,0],
	    [1,0,1,1,1]);

$dcff3->add([1,1,0,1,0],
	    [1,0,1,0,1]);

print $dcff3->c00, "\n";
print $dcff3->c01, "\n";
print $dcff3->c10, "\n";
print $dcff3->c11, "\n";
print $dcff3->spe, "\n";
print $dcff3->mcc, "\n";
print $dcff3->sen, "\n";
print $dcff3->n, "\n";
print $dcff3->chi2,"\n";
print $dcff3->chi2prob, "\n";
