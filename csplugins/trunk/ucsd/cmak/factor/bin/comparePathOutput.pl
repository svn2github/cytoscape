#!/usr/bin/perl -w

my $jfile = $ARGV[0];
my $cfile = $ARGV[1];

print STDERR "Java file: $jfile. C file: $cfile\n";

open(J, $jfile) || die "Can't open Java path file: $jfile\n";
open(C, $cfile) || die "Can't open C path file: $cfile\n";

open(JOUT, ">${jfile}.missing") || die "Can't open Java output file\n";
open(COUT, ">${cfile}.missing") || die "Can't open C output file\n";

my %jpaths;
my %cpaths;

sub trim($)
{
	my $string = shift;
	$string =~ s/^\s+//;
	$string =~ s/\s+$//;
	return $string;
}


while(<J>)
{
    chomp;
    $jpaths{trim($_)} = 1;
}

while(<C>)
{
    chomp;
    $cpaths{trim($_)} = 1;
}

foreach $path (keys(%jpaths))
{
    if(!exists $cpaths{$path})
    {
	printf COUT "$path\n";
    }
    
}

foreach $path (keys(%cpaths))
{
    if(!exists $jpaths{$path})
    {
	print JOUT "$path\n";
    }
    
}

close J;
close C;
close JOUT;
close COUT;
