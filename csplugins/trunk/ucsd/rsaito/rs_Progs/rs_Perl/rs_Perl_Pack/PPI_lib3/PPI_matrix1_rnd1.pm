#!/usr/bin/perl -w

use strict;

package PPI_matrix1;


sub add_ppi_random {

    my($object, $protein_set_ref, $num_int) = @_;
    my($count);
    my($rand1, $rand2);
    my($rand_protein1, $rand_protein2);
    my %added_random_ppi;
    my $added_random_ppi;

    for($count = 0;$count < $num_int;){
        $rand1 = int(rand($#$protein_set_ref + 1));
        $rand2 = int(rand($#$protein_set_ref + 1));
        $rand_protein1 = $protein_set_ref->[ $rand1 ];
        $rand_protein2 = $protein_set_ref->[ $rand2 ];
        if(!defined($object->{'ppi_matrix'}
                   ->{$rand_protein1}->{$rand_protein2}) &&
           !defined($object->{'ppi_matrix'}
                   ->{$rand_protein2}->{$rand_protein1}) &&
           !defined($added_random_ppi{$rand_protein1}->{$rand_protein2}) &&
           !defined($added_random_ppi{$rand_protein2}->{$rand_protein1})){
            $added_random_ppi{$rand_protein1}->{$rand_protein2} = "";
            $added_random_ppi{$rand_protein2}->{$rand_protein1} = "";
            $count ++;
            print "Added $rand_protein1 and $rand_protein2\n";
        }
    }

    $added_random_ppi = new PPI_matrix1 \%added_random_ppi;
    return $added_random_ppi;

}

sub extract_ppi_random {
    
    my($object, $default_protein_set_ref, $num) = @_;

    my(%extracted_protein, $partner, $extracted_protein);
    my %extracted_ppi;
    my(%left_protein, @left_protein);
    my $p_count;
    my $value;

    my $protein_set_size = $#{$object->{'protein_set'}} + 1;
    my $rdm;

    foreach(@{$object->{'protein_set'}}){ $left_protein{ $_ } = ""; }

    $p_count = 0;
    foreach(@$default_protein_set_ref){ 
        $extracted_protein{ $_ } = "";
        delete($left_protein{ $_ });
        $p_count ++;
    }

    while(keys(%left_protein) && $p_count < $num){
        @left_protein = keys(%left_protein);
        $rdm = int(rand($#left_protein + 1));
        $extracted_protein = $left_protein[ $rdm ];
        $extracted_protein{ $extracted_protein } = "";
        delete($left_protein{ $extracted_protein });
        $p_count ++;
    }

#    while($p_count < $num){
#       $rdm = int(rand $protein_set_size);
#       $extracted_protein = $object->{'protein_set'}->[ $rdm ];
#       if(!defined($extracted_protein{ $extracted_protein })){
#           $extracted_protein{ $extracted_protein } = "";
#           $p_count ++;
#       }
#    }

#    print "Extracted proteins are:\n";
#    print join(",", keys(%extracted_protein)), "\n";

    foreach $extracted_protein (keys(%extracted_protein)){
        foreach $partner (keys(%{$object->{'ppi_matrix'}
                                 ->{$extracted_protein}})){
            if(defined($extracted_protein{ $partner })){
                $extracted_ppi{ $extracted_protein }->{ $partner } = 
                    $object->{'ppi_matrix'}->{$extracted_protein}->{$partner};
                $extracted_ppi{ $partner }->{ $extracted_protein } = 
                    $object->{'ppi_matrix'}->{$partner}->{$extracted_protein};
            }
        }
    }

    return new PPI_matrix1 \%extracted_ppi;

}

sub extract_interaction_random {

    my($object, $remain_rate) = @_;
    my @ppi_list_nr = @{$object->{'ppi_list_nr'}};
    my $n_int_all = $#ppi_list_nr + 1;
    my $random;

    while(1.0 * ($#ppi_list_nr + 1) / $n_int_all > $remain_rate){
	$random = int(rand($#ppi_list_nr + 1 + 1));
	splice(@ppi_list_nr, $random, 1);
    }

    return new PPI_matrix1 \@ppi_list_nr;

}

1;
