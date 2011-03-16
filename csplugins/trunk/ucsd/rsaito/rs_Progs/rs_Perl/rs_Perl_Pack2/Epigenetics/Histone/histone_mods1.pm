#!/usr/bin/env perl

use strict;
use warnings;

use General::Usefuls::rsConfig;
use Genome::GenomeSegm::GenomeSegm2;

sub getHistoneModsInfo_NPS($) {
	
	my $mtype = shift;
	my %h = read_config("Histone_mods_NPS.cnf");

	my $histone_info = new GenomeSegm;

	my $histone;
	my $histone_data_file = $h{ $mtype };
	my $line_counter = 0;
	local(*FH);
	open(FH, $histone_data_file) or die "Cannot open \"$histone_data_file\": $!";

	my $dummy = <FH>;

	while($histone = <FH>){
   		chomp $histone;
   		my($chr_m, $start_m, $end_m, $info1, $info2) = split(/\t/, $histone);
   		$histone_info->add_region($chr_m, "", $start_m, $end_m, "Modified");

 		$line_counter ++;
   		if($line_counter % 10000 == 0){ 
    	   	warn "Read $line_counter lines ($mtype).\n";
   		}
   		
   		# if($line_counter > 100000){
   		# 	warn "Test halt..."; 
   		# 	last;
   		# } ### Comment-out these lines for actual analysis!

	}
	close FH;

	return $histone_info;
}

unless (caller) {
 	my $histone_info = getHistoneModsInfo_NPS("H3K27me1");
 	$histone_info->display_region_info("chr3");
}

1;




