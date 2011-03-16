#!/usr/bin/env perl

use strict;
use warnings;

use General::Usefuls::rsConfig;
use Genome::Genes::RefFlat1;
use Epigenetics::Histone::histone_mods1;

sub histone_status_beside_spacer($){
	
	my $mtype = shift;
	my $range_5 = shift; $range_5 = -100 if !defined($range_5);
	my $range_3 = shift; $range_3 = +100 if !defined($range_3);
	
	my $histone_info = getHistoneModsInfo_NPS($mtype);
	my $refFlat_info = &getRefFlatInfo;
	
	my @ret = ();
	
	local(*FH);
	my %h = read_config("Histone_mods_log.cnf");
	
	### Determination of log file path. ###
	my $log_dir = $h{ HistoneBesideSpacer_Path };
	my $logpath = "${log_dir}/${mtype}_info"; $logpath =~ s/\/\/+/\//g;
		
	open(FH, "> $logpath");
	
	for my $chr ($refFlat_info->get_chromosomes()){

    	for my $region_info ($refFlat_info->spacer_region2($chr)){
        	my($spacer_start, $spacer_end,
           	   $region_left_info, $region_right_info) = @$region_info;

        	my($start_left, $end_left, $region_ID_left, $strand_left) = @$region_left_info;
        	my($start_right, $end_right, $region_ID_right, $strand_right) = @$region_right_info;

        	my($q_start_left,  $q_end_left)  = ($start_left  + $range_5, $end_left  + $range_3);
        	my($q_start_right, $q_end_right) = ($start_right + $range_5, $end_right + $range_3);

        	my @histone_info_left = 
            	$histone_info->query_region($chr, $q_start_left,  $q_end_left);
        	my @histone_info_right = 
            	$histone_info->query_region($chr, $q_start_right, $q_end_right);
            
        	my $histone_status_left = "F";
        	if($#histone_info_left >= 0){ $histone_status_left = "Me"; }
        	my $histone_status_right = "F";
        	if($#histone_info_right >= 0){ $histone_status_right = "Me"; }
        
        	push(@ret, $histone_status_left . "-" . $histone_status_right);
        
       		print FH join("\t", $chr, 
							$region_ID_left, $strand_left, 
                	   		$start_left, $end_left, $histone_status_left,
                   			$region_ID_right, $strand_right, 
                   			$start_right, $end_right, $histone_status_right), "\n";
    	}

	}
	
	close FH;
	
	return @ret;
	
}

unless(caller){
	my @ret = histone_status_beside_spacer("H3K27me3");
	print join("\n", @ret), "\n";
	print $#ret + 1, "\n";
}

1;
