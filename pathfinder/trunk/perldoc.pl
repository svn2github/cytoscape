#!/usr/bin/perl -w
#
# Generate JavaDoc-inspired HTML documentation for a group
# of Perl packages (objects).
# 

use PerlFile;
use File::Spec;

if(scalar(@ARGV) != 2)
{
    die "?0: <directory of .p[ml] files> <output directory>\n";
}

my ($inDir, $outDir) = @ARGV;

my $pm = File::Spec->catfile( $inDir, "*.pm" );
my $pl = File::Spec->catfile( $inDir, "*.pl" );
my @perlFiles = glob "$pm $pl";

my %outputFiles;  # map each packageName to an HTML file
my %tree;         # map packageName to the packages that inherit from it

foreach $file (@perlFiles)
{
    print "documenting ${file}\n";

    my $pf = PerlFile->new($file);
    
    my $filename = $pf->writeHTML($outDir);

    if($pf->packageName() ne "")
    {
	$outputFiles{$pf->packageName()} = $filename;
	
	if(exists($pf->arrays()->{"ISA"}))
	{
	    foreach my $isa (@{$pf->arrays()->{"ISA"}})
	    {
		push @{$tree{$isa}}, $pf->packageName();
	    }
	}
	else
	{
	    if(!exists($tree{$pf->packageName()}))
	    {
		$tree{$pf->packageName()} = [];
	    }
	}
    }
    else
    {
	# Uncomment to include .pl files in documentation
	#$outputFiles{$file} = $filename;
	#$tree{$file} = [];
    }

}

makeIndex($outDir, \%outputFiles, \%tree);

sub makeLink
{
    my ($name, $file) = @_;

    return "<a href=\"$file\" target=\"fileFrame\">$name</a><br>\n";
}

#
# Return an array of the root nodes of the package tree
#
sub getRootPackages
{
    my ($tree) = @_;

    my %childNodes;
    my @roots;
    foreach my $valArray (values %{$tree})
    {
	foreach my $val (@{$valArray})
	{
	    $childNodes{$val}++;
	}
    }

    ## Root nodes are not children 
    foreach my $key (keys %{$tree})
    {
	if(! exists($childNodes{$key}))
	{
	    push @roots, $key;
	}
    }

    return @roots;
}

#
# Recurse the package tree and generate HTML links to each file,
# indenting each link according to its depth in the tree
#
sub recursePackageTree
{
    my ($HTML, $tree, $outputFiles, $root, $depth) = @_;

    my $prefix = "";
    
    $prefix = "&nbsp;&nbsp;&nbsp;&nbsp;" x $depth;

    $$HTML .= $prefix . makeLink($root, $outputFiles->{$root});
    foreach my $child (@{$tree->{$root}})
    {
	recursePackageTree($HTML, $tree, $outputFiles, $child, $depth + 1);
    }
}

#
# Make index.html (entry point file) and overview-frame.html (navigation)
#
sub makeIndex
{
    my ($outDir, $outputFiles, $packageTree) = @_;
    
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

    my @rootPackages = getRootPackages($packageTree);

    foreach my $root (@rootPackages)
    {
	recursePackageTree(\$fileHTML, $packageTree, $outputFiles, $root, 0);
    }
    
    my $now = localtime(time());

    my $overviewText = 
	qq(<html>
	   <head><title>All packages</title></head>
	   <body>
	   <b>All packages</b><br>
	   $fileHTML
	   <br>
	   <font size="-1">
	   <i>
	   Last updated:<br>
	   $now
	   </i>
	   </font>
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

#
# Utility method for printing the tree
#
sub printTree
{
    my ($tree) = @_;
    
    foreach my $key (keys %{$tree})
    {
	my @vals = @{$tree->{$key}};

	print "$key: ";
	foreach my $v (@vals)
	{
	    print "$v, ";
	}
	print "\n";
    }
}
