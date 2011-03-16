#!/usr/bin/env perl

use strict;
use warnings;

package GenomeSegm;

## --- Genomic coordinate starts with 0 --- ##

sub new {

    my $class = shift;
    my $blksize = shift;

    if(!defined($blksize)){
	$blksize = 10000;
	# warn "Block size automatically set to 10 kb.\n";
    }

    my $sgm_info = {
	blksize => $blksize
    };

    return bless $sgm_info;

}

sub add_region($$$$$$){

    my $sgm_info     = shift;
    my $chrom        = shift;
    my $region_start = shift;
    my $region_end   = shift;
    my $region_ID    = shift;
    my $misc_info    = shift;

    my $region_str = join("\t", $region_start, $region_end);
    if(defined($sgm_info->{ whole_str }->{ $chrom }
	       ->{ $region_ID }->{ $region_str })){ return; }
    $sgm_info->{ whole_str }->{ $chrom }
    ->{ $region_ID }->{ $region_str } = 
	defined($misc_info) ? $misc_info : "";

    push(@{$sgm_info->{ whole }->{ $chrom }}, 
	 [ $region_start, $region_end, $region_ID ]);
		   
    my $sgm_start = int($region_start / $sgm_info->{ blksize })
	* $sgm_info->{ blksize };
    my $sgm_end   = int($region_end / $sgm_info->{ blksize })
	* $sgm_info->{ blksize };
    for(my $i = $sgm_start;$i <= $sgm_end;$i += $sgm_info->{ blksize }){
	push(@{$sgm_info->{ segm }->{ $chrom }->{ $i }},
	     [ $region_start, $region_end, $region_ID ]);
	# warn "Adding [$region_start, $region_end, $region_ID] on chromosome $chrom segm $i\n";
    }

}

sub delete_chromosome($$){ # Needs test

    my $sgm_info = shift;
    my $chrom    = shift;

    if(defined($sgm_info->{ whole_str }->{ $chrom })){
	delete $sgm_info->{ whole_str }->{ $chrom };
    }

    if(defined($sgm_info->{ whole }->{ $chrom })){
	delete $sgm_info->{ whole }->{ $chrom };
    }

    if(defined($sgm_info->{ sgm }->{ $chrom })){
	delete $sgm_info->{ sgm }->{ $chrom };
    }

}

sub get_region_ID_based_info($$){
    my $sgm_info = shift;
    return $sgm_info->{ whole_str };
}

sub query_region($$$$){

    my $sgm_info     = shift;
    my $chrom        = shift;
    my $region_start = shift;
    my $region_end   = shift;

    my $sgm_start = int($region_start / $sgm_info->{ blksize })
	* $sgm_info->{ blksize };
    my $sgm_end   = int($region_end / $sgm_info->{ blksize })
	* $sgm_info->{ blksize };

    my %res_tmp = ();

    for(my $i = $sgm_start;$i <= $sgm_end;$i += $sgm_info->{ blksize }){ 
	if(defined($sgm_info->{ segm }->{ $chrom }->{ $i })){
	    for my $registered_info 
		(@{$sgm_info->{ segm }->{ $chrom }->{ $i }}){
		    my($region_start, $region_end, $region_ID) =
			@$registered_info;
		    my $info_str = join("\t", $region_start, 
					$region_end, $region_ID );
		    $res_tmp{ $info_str } = "";
	    }
	}
    }

    my @res = ();
    for my $res_tmp (keys %res_tmp){
	push(@res, [ split("\t", $res_tmp) ]);
    }

    return @res;

}

sub spacer_region($$){

    my $sgm_info = shift;
    my $chrom    = shift;

    my @start_pos_sorted = 
	sort { $a->[0] <=> $b->[0] } @{$sgm_info->{ whole }->{ $chrom }};

    my @spacer_region = ();

    my $max_end = -1;
    my $max_region_ID = "";
    for my $annot (@start_pos_sorted){
	my($region_start, $region_end, $region_ID) = @$annot;
	if($max_end >= 0 && $region_start > $max_end + 1){
	    push(@spacer_region, 
		 [$max_end + 1, $region_start - 1, $max_region_ID, $region_ID]);
	}
	if($max_end < $region_end){
	    $max_end = $region_end;
	    $max_region_ID = $region_ID;
	}

	# print join("\t", @$annot), "\n";
    }

    return @spacer_region;

}


sub display_region_info($$){

    my $sgm_info = shift;
    my $chrom    = shift;

    print "### Whole Genome Information ###\n\n";
    print "##  Region ID-based info ##\n";
    for my $chrom (keys %{$sgm_info->{ whole_str }}){
	my $chrom_str = $sgm_info->{ whole_str }->{ $chrom };
	for my $region_ID (keys %{$chrom_str}){
	    for my $region_str (keys %{$chrom_str->{ $region_ID }}){
		print join("\t", $region_ID, $region_str,
			   $chrom_str->{ $region_ID }->{ $region_str }), "\n";
	    }
	}
    }

    print "\n";

    print "## Region info ##\n";
    for my $chrom (keys %{$sgm_info->{ whole }}){
	my @regions_sorted = sort { $a->[0] <=> $b->[0] } 
	   @{$sgm_info->{ whole }->{ $chrom }};
	for my $region (@regions_sorted){
	    print join("\t", $chrom, @$region), "\n";
	}
    }

    print "\n";

    print "### Segment Information ###\n";
    my @valid_sgm_sorted 
	= sort {$a <=> $b } keys %{$sgm_info->{ segm }->{ $chrom }};
    for my $valid_sgm (@valid_sgm_sorted){
	my @region_sorted = 
	    sort { $a->[0] <=> $b->[0] } @{$sgm_info->{ segm }
					   ->{ $chrom }->{ $valid_sgm }};
	for my $registered_info (@region_sorted){
	    print join("\t", $valid_sgm, @$registered_info), "\n";
	}
    }

    print "\n";
}

1;


