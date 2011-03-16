#!/usr/bin/env perl

##### RefFlat #####

use strict;
use warnings;

use General::Usefuls::rsConfig;
use Genome::GenomeSegm::GenomeSegm2;

sub getRefFlatInfo {

	my %h = rsConfig::read_config("UCSC.cnf");
	my $refFlat_info = new GenomeSegm;
	my $refFlat_file = $h{ RefFlat };
	
	local(*FH);
	open(FH, $refFlat_file) or die "Cannot open \"$refFlat_file\": $!";
	while(my $refflat = <FH>){
   		chomp $refflat;
   		my ($gene, $nm_id, $chr_r, $strand, $start_r, $end_r) = split(/\t/, $refflat);
   		if($chr_r =~ /^chr(\d+|X|Y)$/){
       		$refFlat_info->add_region($chr_r, $strand, $start_r, $end_r, $nm_id);
   		}
	}

	close FH;
	
	return $refFlat_info;
}

unless (caller) {
 	my $refl_info = &getRefFlatInfo;
 	$refl_info->display_region_info("chr3");
}

1;
