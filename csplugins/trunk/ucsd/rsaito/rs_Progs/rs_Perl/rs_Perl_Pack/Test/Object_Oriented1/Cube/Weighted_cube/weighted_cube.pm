#!/usr/bin/perl -w

use strict;

package weighted_cube; # This declaration must come first.
use vars qw(@ISA);
use cube; # This declaration must come after "package weighted_cube".

@ISA = qw(cube);

sub new {
    my $class = shift;
    my($height, $width, $depth, $weight) = @_;
    my $weighted_cube_ref = new cube $height, $width, $depth;
    $weighted_cube_ref->{"weight"} = $weight;

    return bless $weighted_cube_ref;
    
}

sub density {
    my $object = shift;
    my $volume = $object->volume;

    return $object->{"weight"} / $volume;

}

1;
