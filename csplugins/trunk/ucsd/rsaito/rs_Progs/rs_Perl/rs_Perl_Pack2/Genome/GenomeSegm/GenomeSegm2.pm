#!/usr/bin/env perl

use strict;
use warnings;

package GenomeSegm;

## --- Genomic coordinate starts with 0 --- ##

=pod

$sgm_info->{ blksize }                                   = $blksize;    # Segment block size
$sgm_info->{ whole_str}->{ $region_ID }->{ $region_str } = $misc_info;  # Region identifying string.
                                                                        # It is used to avoid registering
                                                                        # identical genomic region.
$sgm_info->{ whole }->{ $chrom }->[$n] = ($region_start, $region_end,
                                          $strand, $region_ID);         # Whole chromosome information.
$sgm_info->{ segm }->{ $chrom }->{$i_th_segm}
         ->{ $n } = ($region_start, $region_end,
                     $strand, $region_ID);                              # Chromosome segment information where
                                                                        # each segment size is $segm_info->{ blksize }

=cut

sub new {

    my $class = shift;
    my $blksize = shift; # Block size

    if(!defined($blksize)){
	$blksize = 10000;
	# warn "Block size automatically set to 10 kb.\n";
    }

    my $sgm_info = {
	blksize => $blksize
    };

    return bless $sgm_info;

}

sub add_region($$$$$$$){

    my $sgm_info     = shift;
    my $chrom        = shift;
    my $strand       = shift;
    my $region_start = shift;
    my $region_end   = shift;
    my $region_ID    = shift;
    my $misc_info    = shift;

    my $region_str = join("\t", $chrom, $strand, $region_start, $region_end);
    if(defined($sgm_info->{ whole_str }->{ $region_ID }->{ $region_str })){
	return;
    } # Registration of identical genomic region not allowed.
    $sgm_info->{ whole_str }->{ $region_ID }->{ $region_str } = 
	defined($misc_info) ? $misc_info : "";

    push(@{$sgm_info->{ whole }->{ $chrom }}, 
	 [ $region_start, $region_end, $strand, $region_ID ]);
		   
    my $sgm_start = int($region_start / $sgm_info->{ blksize })
	* $sgm_info->{ blksize };
    my $sgm_end   = int($region_end / $sgm_info->{ blksize })
	* $sgm_info->{ blksize };
    for(my $i = $sgm_start;$i <= $sgm_end;$i += $sgm_info->{ blksize }){
	push(@{$sgm_info->{ segm }->{ $chrom }->{ $i }},
	     [ $region_start, $region_end, $strand, $region_ID ]);
	# warn "Adding [$region_start, $region_end, $region_ID] on chromosome $chrom segm $i\n";
    }

}

sub get_region_IDs($){

    my $sgm_info = shift;
    return (keys %{$sgm_info->{ whole_str }});

}   

sub get_region_ID_based_info($){

    my $sgm_info = shift;
    return $sgm_info->{ whole_str };

}

sub get_chromosomes($){

    my $sgm_info = shift;
    return sort (keys %{$sgm_info->{ whole }});

}

sub get_chr_info($$){

    my $sgm_info = shift;
    my $chr      = shift;
    my @sorted = sort { $a->[0] <=> $b->[0] } @{$sgm_info->{ whole }->{ $chr }};
    
    return @sorted;

}

sub get_info_from_region_ID($$){
    my $sgm_info = shift;
    my $region_ID = shift;

    my @ret = ();

    for my $region_str (keys %{$sgm_info->{ whole_str }->{ $region_ID }}){
	my($chrom, $strand, $region_start, $region_end) = split("\t", $region_str); 
	my $misc_info = $sgm_info->{ whole_str }->{ $region_ID }->{ $region_str };
	push(@ret, [ $chrom, $strand, $region_start, $region_end, $misc_info ]);
    }

    return @ret;

}

sub query_region($$$$$){

    my $sgm_info     = shift;
    my $chrom_i        = shift;
    my $region_start_i = shift;
    my $region_end_i   = shift;
    my $strand_i       = shift; # Arbitrary

    my $sgm_start = int($region_start_i / $sgm_info->{ blksize })
	* $sgm_info->{ blksize };
    my $sgm_end   = int($region_end_i / $sgm_info->{ blksize })
	* $sgm_info->{ blksize };

    my %res_tmp = ();

    for(my $i = $sgm_start;$i <= $sgm_end;$i += $sgm_info->{ blksize }){ 
	if(defined($sgm_info->{ segm }->{ $chrom_i }->{ $i })){
	    for my $registered_info 
		(@{$sgm_info->{ segm }->{ $chrom_i }->{ $i }}){
		    my($region_start, $region_end, $strand, $region_ID) =
			@$registered_info;
		    if(!defined($strand_i) or $strand_i eq $strand){
			my $info_str = join("\t", $region_start, $region_end,
					    $strand, $region_ID);
			$res_tmp{ $info_str } = "";
		    }
	    }
	}
    }

    my @res = ();
    for my $res_tmp (keys %res_tmp){
	push(@res, [ split("\t", $res_tmp) ]);
    }

    return @res;

}


sub spacer_region($$$){
    # Returns spacer information along with flanking region information.
    # @spacer_region = ([$spacer0_start, $spacer0_end, $left_region_ID, $right_region_ID], ...)

    my $sgm_info = shift;
    my $chrom    = shift;
    my $strand_i   = shift; # Arbitrary

    my @start_pos_sorted = 
	sort { $a->[0] <=> $b->[0] } @{$sgm_info->{ whole }->{ $chrom }};

    my @spacer_region = ();

    my $max_end = -1;
    my $max_region_ID = "";
    for my $annot (@start_pos_sorted){
	my($region_start, $region_end, $strand, $region_ID) = @$annot;
	if(!defined($strand_i) or $strand_i eq $strand){
	    if($max_end > -1 and $region_start > $max_end + 1){
		push(@spacer_region, 
		     [$max_end + 1, $region_start - 1, $max_region_ID, $region_ID]);
	    }
	    if($max_end < $region_end){
		$max_end = $region_end;
		$max_region_ID = $region_ID;
	    }
	}
	# print join("\t", @$annot), "\n";
    }

    return @spacer_region;

}

sub spacer_region2($$$){
    # Returns spacer information along with detailed flanking region information.
    # @spacer_region = ([$spacer0_start, $spacer0_end, 
    #                     [$left_region_start, $left_region_end, $left_region_ID, $left_strand],
    #                     [$right_region_start, $right_region_end, $right_region_ID, $right_strand]
    #                   ], ...)


    my $sgm_info = shift;
    my $chrom    = shift;
    my $strand_i   = shift; # Arbitrary

    my @start_pos_sorted = 
	sort { $a->[0] <=> $b->[0] } @{$sgm_info->{ whole }->{ $chrom }};

    my @spacer_region = ();

    my $max_start     = -1;
    my $max_end       = -1;
    my $max_region_ID = "";
    my $max_strand    = "";

    for my $region_info (@start_pos_sorted){
	my($region_start, $region_end, $strand, $region_ID) = @$region_info;
	if(!defined($strand_i) or $strand_i eq $strand){
	    if($max_end > -1 and $region_start > $max_end + 1){
		push(@spacer_region, 
		     [$max_end + 1, 
		      $region_start - 1,
		      [$max_start,    $max_end,    $max_region_ID, $max_strand],
		      [$region_start, $region_end, $region_ID,     $strand]]);
	    }
	    if($max_end < $region_end){
		$max_start     = $region_start;
		$max_end       = $region_end;
		$max_region_ID = $region_ID;
		$max_strand    = $strand;
	    }
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
    for my $region_ID (keys %{$sgm_info->{ whole_str }}){
	for my $region_str (keys %{$sgm_info->{ $region_ID }}){
		print join("\t", $region_ID, $region_str,
			   $sgm_info->{ $region_ID }->{ $region_str }), "\n";
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


