#!/usr/bin/perl -w

use strict;
require Exporter;

package test1;
use vars qw(@ISA @EXPORT_OK);

@ISA = qw(Exporter); # Current package, not main package
@EXPORT_OK = qw(hello hello2);

sub hello {

    print "Hello, world!\n";

}

sub hello2 {

    print "Hello, everyone!\n";

}

1;
