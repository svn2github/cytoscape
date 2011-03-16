#!/usr/bin/env perl

use strict;
use warnings;

package cuboid_weighted; # This declaration must come first.
use vars qw(@ISA);
use cuboid; # This declaration must come after package declaration.

@ISA = qw(cuboid);

sub new {
    my $class = shift;
    my($h, $w, $d, $weight) = @_;
    my $cuboid_weighted_ref = new cuboid $h, $w, $d;
    $cuboid_weighted_ref->{"weight"} = $weight;

    bless $cuboid_weighted_ref;
    
}

sub density {
    my $object = shift;
    my $volume = $object->volume;

    return $object->{"weight"} / $volume;

}

1;
