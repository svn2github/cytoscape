#!/usr/bin/perl -w

use strict;

package cube;

sub new {
    my $class = shift;
    my($height, $width, $depth) = @_;
    
    my $cube_ref = {
	"height" => $height,
	"width" => $width,
	"depth" => $depth
	};

    return bless $cube_ref;

}

sub volume {
    my $object = shift;
    return $object->{"height"} * $object->{"width"} * $object->{"depth"};
}

1;






