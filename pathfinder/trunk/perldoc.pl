#!/usr/bin/perl -w

use PerlFile;
use File::Spec;

if(scalar(@ARGV) != 2)
{
    die "?0: <directory> <outdir>\n";
}

my ($inDir, $outDir) = @ARGV;

my $pm = File::Spec->catfile( $inDir, "*.pm" );
my $pl = File::Spec->catfile( $inDir, "*.pl" );
my @perlFiles = glob "$pm $pl";

my @outputFiles;

foreach $file (@perlFiles)
{
    print "documenting ${file}\n";

    my $pf = PerlFile->new($file);
    
    push @outputFiles, $pf->writeHTML($outDir);
}

makeIndex($outDir, \@outputFiles);

sub makeLink
{
    my ($name, $file) = @_;

    return "<a href=\"$file\" target=\"fileFrame\">$name</a>";
}

sub makeIndex
{
    my ($outDir, $outputFiles) = @_;
    
    my $indexText = 
	qq(<html>
	   <head><title>Perl documentation</title></head>
	   <FRAMESET cols="20%,80%">
	   <FRAME src="overview-frame.html" name="fileListFrame" title="All Packages">
	   <FRAME src="" name="fileFrame" title="Package Info">
	   </FRAMESET>
	   </html>
	   );
    

    my $fileHTML = "";

    foreach my $f (@{$outputFiles})
    {
	$fileHTML .= makeLink(PerlFile::basename($f), $f) . "<br>\n";
    }
    
    my $overviewText = 
	qq(<html>
	   <head><title>All packages</title></head>
	   <body>
	   <b>All packages</b>
	   $fileHTML
	   </body>
	   </html>
	   );
    
    
    my $indexFile = File::Spec->catfile($outDir, "index.html");
    my $overviewFile = File::Spec->catfile($outDir, "overview-frame.html");
    
    open(OUT, ">$indexFile") || die "Can't open $indexFile\n";
    print OUT $indexText;
    close OUT;

    open(OUT, ">$overviewFile") || die "Can't open $overviewFile\n";
    print OUT $overviewText;
    close OUT;
}
