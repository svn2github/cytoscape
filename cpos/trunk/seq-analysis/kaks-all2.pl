#!/usr/bin/perl -w

use Funspec;
use Getopt::Long;

my ($outdir);
GetOptions('o|output:s' => \$outdir);

die "$0: [-o <outdir>] [fasta file]\n" if (scalar(@ARGV) == 0);

my @fastaFiles = @ARGV;

my $KAKS_SCRIPT = "kaks.sh";

my $rFile = $outdir . "/kaks.R";
my $scriptFile = $outdir . "/run.sh";

open R, ">$rFile" || die "Can't open $rFile: $!\n";
open SCRIPT, ">$scriptFile" || die "Can't open $scriptFile\n";

print R qq(source("../plot.kaks.R")) . "\n";

print R qq(pdf(file="kaks.pdf")) . "\n";

print R qq(w.ave <- c()) . "\n";

my (@filesProcessed, @namesProcessed);
foreach my $file (@fastaFiles)
{    
    my $name = $file;
    if($file =~ /(\S+)\.fasta/)
    {
	$name = $1;
    }
    next if ($name eq "2");

    printf SCRIPT "%s %s\n", $KAKS_SCRIPT, $file;
    push @filesProcessed, $file;
    push @namesProcessed, $name;
}

printf R ("data <- plot.kaks(files=c(%s), names=c(%s), group.name=%s)\n", 
	  join(", ", map { qt($_ . ".kaks")} @filesProcessed),
	  join(", ", map { qt($_) } @namesProcessed),
	  qt("Unpartitioned files"));

print R qq(w.ave <- rbind(w.ave, lapply(data\$w, median))) . "\n";

printf R "barplot(w.ave, names.arg=c(%s), las=2)\n", join(", ", map { qt($_) } @namesProcessed),;
print R qq(dev.off()) . "\n";
close R;

close SCRIPT;

sub qt
{
    my ($s) = @_;
    return "\"" . $s . "\"";
}

