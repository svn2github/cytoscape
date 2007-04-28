#!/usr/bin/perl -w

die "$0: <outputdir>\n" if (scalar(@ARGV) != 1);

my $dir = shift @ARGV;
opendir(D, $dir) || die "Can't open $dir: $!\n";
my @files = readdir(D);

print "<html><body><ul>\n";
print "<h1>Datasets analyzed</h1>\n";
foreach my $f (@files)
{
    next if (! -e "$dir/$f/index.html" || $f =~ /^\./);
    print "<li><a href=\"$f/index.html\">$f</a>\n";
}

print "</ul></body></html>\n";
