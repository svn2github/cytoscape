#!/usr/bin/env perl

### Insulator_CTCF ###

use strict;
use warnings;

use General::Usefuls::rsConfig;
use Genome::GenomeSegm::GenomeSegm2;

sub getInsulatorBindingInfo {

	my %h = rsConfig::read_config("Insulator.cnf");
	my $insl_info = new GenomeSegm;

	my $insl_file = $h{ CTCF_MACS_peaks };
	local(*FL);
	open(FL, $insl_file) or die "Cannot open \"$insl_file\": $!";

	my $dummy = <FL>;

	my $line_count = 0;
	while(my $data = <FL>){
   		chomp $data;
   		my($chr_d, $srt_d, $end_d, $info1, $info2) = split(/\t/, $data);
   		$insl_info->add_region($chr_d, "?", $srt_d, $end_d, "Insulator");
   		$line_count += 1;
   		if($line_count % 10000 == 0){
       		warn "Read $line_count lines (CTCF).\n";
   		}
	}

	close FL;

	return $insl_info;
}

unless (caller) {
 	my $insl_info = &getInsulatorBindingInfo;
 	$insl_info->display_region_info("chr3");
}


1;


