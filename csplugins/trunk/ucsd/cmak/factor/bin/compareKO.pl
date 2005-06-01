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

my %jCnt;
my %cCnt;


sub trim($)
{
	my $string = shift;
	$string =~ s/^\s+//;
	$string =~ s/\s+$//;
	return $string;
}


while(<J>)
{
    if(/^>/) {next;}
    chomp;
    trim($_);
    @f = split(/\s+/);

    #print "$f[0] $f[1] $f[2]\n";
    my $key = join(" ", @f[0,1]); 
    $jpaths{$key} = $f[2];
    $jCnt{$key} = $f[3];
}

while(<C>)
{
    if(/^>/) {next;}
    chomp;
    trim($_);
    @f = split(/\s+/);
    my $key = join(" ", @f[0,1]); 
    $cpaths{$key} = $f[2];
    $cCnt{$key} = $f[3];
}

my ($type, $name, $xp, $cc, $jc);

format STDOUT =
@<<<< @<<<<<<<<<<<<<<<<<< @<<<<< @<<<<< @<<<<<
$type, $name, $xp, $cc, $jc
.

    ($type, $name, $xp, $cc, $jc) =
    (">type", "name", "xpNum", "#C", "#J");
write STDOUT;

sub j_by_xp { $jpaths{$a} <=> $jpaths{$b} }
sub c_by_xp { $cpaths{$a} <=> $cpaths{$b} }

for $x (sort j_by_xp keys(%jpaths))
{
    if(!exists $cpaths{$x})
    {
	($type, $name, $xp, $cc, $jc) =
	    ("J+C-", $x, $jpaths{$x}, "", "$jCnt{$x}");
	write STDOUT;
    }
}

for $x (sort c_by_xp keys(%cpaths))
{
    if(!exists $jpaths{$x})
    {
	($type, $name, $xp, $cc, $jc) =
	    ("C+J-", $x, $cpaths{$x}, "$cCnt{$x}", "");
	write STDOUT;
    }

}

for $x (sort c_by_xp keys(%cpaths))
{
    if(exists $jpaths{$x} &&
       $cCnt{$x} != $jCnt{$x})
    {
	($type, $name, $xp, $cc, $jc) =
	    ("C*J*", $x, $jpaths{$x}, $cCnt{$x}, $jCnt{$x});
	write STDOUT;
    }
}

close J;
close C;
close JOUT;
close COUT;
