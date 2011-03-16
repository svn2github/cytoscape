#!/usr/bin/perl -w

use strict;

package Get_exp_data;
require Exporter;

@Get_exp_data::ISA = qw(Exporter);
@Get_exp_data::EXPORT = qw(get_exp_data get_exp_data_f2);

sub get_exp_data($$){

    my($filename, $synonym_ref) = @_;
    local(*FH);
    my(%expression);
    my(@exp_data, @exp_data_clean);
    my($count);
    my($dummy);
    my($orf_name);

    open(FH, $filename) || die "Cannot open \"$filename\":$!";

    $dummy = <FH>;
#    $dummy = <FH>;

    while(<FH>){
        chomp;
        @exp_data = split(/\t/);
        $orf_name = shift(@exp_data);
        if(defined($synonym_ref->{$orf_name})){
            $orf_name = $synonym_ref->{$orf_name};
        }
        shift(@exp_data);
        @exp_data_clean = ();
        foreach(@exp_data){
            s/ //g;
            push(@exp_data_clean, $_);
        }
        $expression{ $orf_name } = [ @exp_data_clean ];

    }
    close FH;
    return %expression;
}

sub get_exp_data_f2($$){

    my($filename, $synonym_ref) = @_;
    local(*FH);
    my(%expression);
    my(@exp_data, @exp_data_clean);
    my($count);
    my($dummy);
    my($orf_name);

    open(FH, $filename) || die "Cannot open \"$filename\":$!";

    $dummy = <FH>;
#    $dummy = <FH>;

    while(<FH>){
        chomp;
        @exp_data = split(/\t/);
        $orf_name = shift(@exp_data);
        if(defined($synonym_ref->{$orf_name})){
            $orf_name = $synonym_ref->{$orf_name};
        }
#        shift(@exp_data);
        @exp_data_clean = ();
        foreach(@exp_data){
            s/ //g;
            push(@exp_data_clean, $_);
        }
        $expression{ $orf_name } = [ @exp_data_clean ];

    }
    close FH;
    return %expression;
}

sub get_exp_data_f3($$){

    my($filename, $synonym_ref) = @_;
    local(*FH);
    my(%expression);
    my(@exp_data, @exp_data_clean);
    my($count);
    my($dummy);
    my($orf_name);

    open(FH, $filename) || die "Cannot open \"$filename\":$!";

    $dummy = <FH>;
#    $dummy = <FH>;

    while(<FH>){
        chomp;
        @exp_data = split(/\t/);
        $orf_name = shift(@exp_data);
        if(defined($synonym_ref->{$orf_name})){
            $orf_name = $synonym_ref->{$orf_name};
        }
        shift(@exp_data);
        shift(@exp_data);
        @exp_data_clean = ();
        foreach(@exp_data){
            s/ //g;
            push(@exp_data_clean, $_);
        }
        $expression{ $orf_name } = [ @exp_data_clean ];

    }
    close FH;
    return %expression;
}

1;
