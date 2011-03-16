#!/usr/bin/env perl

use strict;
use warnings;

use General::Usefuls::rsConfig;
use Genome::Genes::RefFlat1;
use Epigenetics::Insulator::CTCF_bind1;


sub insulator_in_spacer(){
	
	my $refFlat_info = &getRefFlatInfo;
	my $insl_info = &getInsulatorBindingInfo;

	my @res = ();	

	local(*FH);
	my %h = read_config("Insulator.cnf");

	my $logpath = $h{ Insulator_in_Spacer_logfile };

	open(FH, "> $logpath");

	for my $chr ($refFlat_info->get_chromosomes()){  # Chromosome #

    	for my $region_info ($refFlat_info->spacer_region2($chr)){

        	my($spacer_start, $spacer_end,
           	$region_left_info, $region_right_info) = @$region_info;


        	my($start_left, $end_left, 
           		$region_ID_left, $strand_left) = @$region_left_info;
       		my($start_right, $end_right, 
           		$region_ID_right, $strand_right) = @$region_right_info;

        	my @insulator_info = 
            	$insl_info->query_region($chr, $spacer_start,  $spacer_end);

	        my $binding_judge = "Not Binding";
    	    if($#insulator_info >= 0){ 
        	   $binding_judge = "Binding"; 
        	}

			push(@res, $binding_judge);

	        print FH join("\t", $chr, 
    	             	 	$region_ID_left, $strand_left, 
        	           		$start_left, $end_left, 
            	       		$region_ID_right, $strand_right, 
                	   		$start_right, $end_right, $binding_judge), "\n";
    	}
	}
	
	close FH;

	return @res;
	
}

unless(caller){
	my @res = &insulator_in_spacer;
	print join("\n", @res), "\n";	
}


1;

