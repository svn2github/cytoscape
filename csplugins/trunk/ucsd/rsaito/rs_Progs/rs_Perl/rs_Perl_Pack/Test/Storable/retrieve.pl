#!/usr/bin/perl -w

use strict;
use Storable qw(store retrieve);

my $hash_ref;

$hash_ref = retrieve("store.bin");

foreach my $keys (keys %$hash_ref){
   print "$keys\t$hash_ref->{$keys}\n";
}

print $hash_ref->{"Week"}->{"Japanese"}, "\n";
