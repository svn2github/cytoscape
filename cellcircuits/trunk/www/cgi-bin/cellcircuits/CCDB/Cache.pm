package CCDB::Cache;

# This file keeps a copy of all active objects, 
# with records of their primary keys.

use strict;
use warnings;

my %cache = ();

sub set { $cache{$_[0]}{$_[1]} = $_[2];       }
sub get { return $cache{$_[0]}{$_[1]};        }
sub has { return exists $cache{$_[0]}{$_[1]}; }

1;
