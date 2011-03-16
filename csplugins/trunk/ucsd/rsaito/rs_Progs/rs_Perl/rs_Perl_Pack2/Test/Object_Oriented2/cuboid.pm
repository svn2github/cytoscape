#!/usr/bin/perl -w

use strict;

package cuboid;

sub new {
    my $class = shift;
    my($l, $w, $d) = @_;
    
    my $cuboid_ref = {
	"length" => $l,
	"width" => $w,
	"depth" => $d
	};

    bless $cuboid_ref; # Automatically returns this object.

}

sub volume {
    my $object = shift;
    return $object->{"length"} * $object->{"width"} * $object->{"depth"};
}

1;






