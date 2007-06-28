#!/usr/bin/perl -w
#
# Usage: 
# 1. Analyze, generate R script) cpos.pl <ORFlist> <outdir>
# 2. Analyze, generate R, run R)  cpos.pl -run <ORFlist> <outdir>
# 3. Analyze, gen and run R, gen HTML)  cpos.pl -run -html <ORFlist> <outdir>
# 

use POSIX;
use SGD;
use Funspec;
use GeneSet;
use GeneSetTasks;
use FeatureData;
use ChromosomeData;
use Util qw(which keysSortedByValueArraySize);

use File::Basename;

my $USAGE = <<USAGE;
$0: <outputDir> <orflist|featurefiles>+ 
 -agilent 
 -html                    Generate HTML
 -network                    Generate Network SIF file
 -run                     Run R
 -featureType [sgd|boyer] Gene feature type
 -featureFile             Gene feature file
 -org [sc|hs]             Organism
 -bkgdFile                Location of all probes
 -mtd                     Min Telomere Distance cutoff
 -ks                      Kolmogorov-Smirnoff test type (less|greater|two.sided)
 -image [ps|pdf|png]      Default is png
USAGE

# Parse command line args
my @newArg;

my $MTD_CUTOFF = 30000;
my $USE_AGILENT = 0;
my $WRITE_HTML = 0;
my $WRITE_NETWORK = 0;
my $RUN_R = 0;
my $ORGANISM = "sc";
my $FEATURE_TYPE = "sgd";
my $FEATURE_FILE = $ENV{"HOME"} ."/data/SGD_features.tab";
my $BKGD_FILE = "";
my $IMAGE_FORMAT = "png";
my $KS_TEST = "less";

my %CHROMOSOME_FILES = ( sc => $ENV{"HOME"} . "/data/sc-chromosome-data.txt",
			 hs => $ENV{"HOME"} . "/data/hs-chromosome-data.txt");


{local $arg;
 while (@ARGV)
 {
     $arg = shift;
     if($arg =~ /^-agilent$/) { $USE_AGILENT = 1 }
     elsif($arg =~ /^-html$/) { $WRITE_HTML = 1 }
     elsif($arg =~ /^-network$/) { $WRITE_NETWORK = 1 }
     elsif($arg =~ /^-featureType$/) { $FEATURE_TYPE = lc(shift @ARGV) }
     elsif($arg =~ /^-featureFile$/) { $FEATURE_FILE = shift @ARGV }
     elsif($arg =~ /^-bkgdFile$/) { $BKGD_FILE = shift @ARGV }
     elsif($arg =~ /^-org$/) { $ORGANISM = lc(shift @ARGV) }
     elsif($arg =~ /^-mtd$/) { $MTD_CUTOFF = shift @ARGV }
     elsif($arg =~ /^-run$/) { $RUN_R = 1 }
     elsif($arg =~ /^-image$/) { $IMAGE_FORMAT = shift @ARGV; }
     elsif($arg =~ /^-ks$/) { $KS_TEST = shift @ARGV; }
     else { push @newArg, $arg }
 }
}

die $USAGE if (scalar(@newArg) < 2);
die "Bad image format: $IMAGE_FORMAT.  Use ps or png.\n" unless ($IMAGE_FORMAT eq "png" || 
								 $IMAGE_FORMAT eq "ps" || 
								 $IMAGE_FORMAT eq "pdf");

# Global variables
my ($OUTDIR, @INPUT_FILES) = @newArg;

my $basename = fileparse($INPUT_FILES[0]);

my $BKGD_PLOT_FILE = $basename . ".bkgd.$IMAGE_FORMAT";
my $KS_PLOT_FILE = $basename . ".pvplot.$IMAGE_FORMAT";
my $KS_TAB_FILE = $basename . ".ks-pvalues.tab";
   
my $HTML_FILE = $OUTDIR . "/index.html";
my $NETWORK_FILE = $OUTDIR . "/" . $basename . ".mtd" . $MTD_CUTOFF . ".sif";
my $R_FILE = $OUTDIR . "/" . $basename . ".R";

die "$OUTDIR does not exist\n" if(! -e $OUTDIR);


my ($features, $chrData);

if(FeatureData::typeExists($FEATURE_TYPE))
{
    print STDERR "### Initializing feature table: $FEATURE_TYPE, $FEATURE_FILE\n";
    $features = FeatureData::create($FEATURE_TYPE, $FEATURE_FILE);
}
else { print STDERR "### Bad feature type: $FEATURE_TYPE\n"; die $USAGE; }

if(exists($CHROMOSOME_FILES{$ORGANISM}))
{
    $chrData = ChromosomeData->new($CHROMOSOME_FILES{$ORGANISM});
}
else { print STDERR "### Bad organism: $ORGANISM\n"; die $USAGE; }

my @geneSets;

foreach my $in (@INPUT_FILES)
{
    print STDERR "Processing $in...\n";
    die "$in does not exist\n" if(! -e $in);
    if($in =~ /\.ORFlist/)
    {
	push @geneSets, processORFlist($in);
    }
    else
    {
	push @geneSets, processFeatureFile($in);
    }
}

writeRcommands(\@geneSets);

if($RUN_R)
{
    print "### Running R: R CMD BATCH $R_FILE\n";
    system("R CMD BATCH $R_FILE");
}

if($WRITE_HTML)
{
    print "### Writing HTML To $HTML_FILE\n";
    writeHTML(\@geneSets, $MTD_CUTOFF);
}

if($WRITE_NETWORK)
{
    print "### Writing Network to $NETWORK_FILE\n";
    writeNetwork(\@geneSets, $MTD_CUTOFF);
}

sub processORFlist
{
    my ($file) = @_;
    my ($cat2orf, $orf2cat) = readORFlist($file);

    my @geneSets;
    foreach my $cat (sort keys %{$cat2orf})
    {
	next if (scalar(@{$cat2orf->{$cat}}) == 0);
	
	my $name = sanitizeName($cat);
	
	my $gs = GeneSet->new($name, $cat2orf->{$cat});
	$gs->analyze($features, $chrData);
	$gs->printData("$OUTDIR/$name.loc");
	$gs->dataFile($name . ".loc");
	
	push @geneSets, $gs;
	
	if(0) # debug stuff
	{
	    printf "mids: %s\n", join(", ", @{$gs->midPoints()});
	    printf "tels: %s\n", join(", ", @{$gs->minTelomereDist()});
	    printf "cens: %s\n", join(", ", @{$gs->centromereDist()});
	    printf "chrs: %s\n", join(", ", @{$gs->chromosomes()});
	}
    }
    return @geneSets;
}

sub sanitizeName
{
    my ($name) = @_;
     # R does not like "-" characters in strings.  Replace with periods
    $name =~ s/[-\s\/\(\)\'\":\[\]\\]/\./g;
    return $name;
}
sub processFeatureFile
{
    my ($file) = @_;

    my @genes;
    open(IN, $file) || die "Can't open $file: $!\n";
    while(<IN>)
    {
	next if (/^#/);
	chomp;
	my @F = split(/\t/);
	push @genes, $F[0] if (scalar(@F) >= 4);
    }

    $features = FeatureData::create($FEATURE_TYPE, $file);

    my $name = sanitizeName(fileparse($file));
    my $gs = GeneSet->new($name, \@genes);

    $gs->analyze($features, $chrData);
    $gs->printData("$OUTDIR/$name.loc");
    $gs->dataFile($name . ".loc");
    
    if(0) # debug stuff
    {
	printf "mids: %s\n", join(", ", @{$gs->midPoints()});
	printf "tels: %s\n", join(", ", @{$gs->minTelomereDist()});
	printf "cens: %s\n", join(", ", @{$gs->centromereDist()});
	printf "chrs: %s\n", join(", ", @{$gs->chromosomes()});
    }
    return $gs;
}


# Read in the KS-pvalues.  
# These are created by the R script that is generated by writeRcommands().
sub readKSData
{
    my ($f) = @_;
    open(IN, "$f") || die "Can't open $f: $!\n";

    my %dat;
    while(<IN>)
    {
	next if ($. == 1);
	chomp;
	my ($cat, $pval) = split;
	$dat{$cat} = $pval;
    }
    return \%dat;
}

sub writeNetwork
{
    my ($geneSets, $mtdCutoff) = @_;
    
    # subt maps gene sets names to an array of genes with mtd <= mtdCutoff
    my $subt = filterSetsByMTD($geneSets, $mtdCutoff);

    open N, ">$NETWORK_FILE" || die "Can't open $NETWORK_FILE: $!\n";

    # Edges for each gene set
    foreach my $gs (@{$geneSets})
    {
	my $name = $gs->name();
	my @genes = @{$subt->{$name}};

	map { printf N "%s pd %s\n", $name, $_ } @genes;
    }
    close N;
}

#
# write the HTML navigation file to the $OUTDIR
#
sub writeHTML
{
    my ($geneSets, $mtdCutoff) = @_;

    my %ks = %{readKSData($OUTDIR . "/" . $KS_TAB_FILE)};

    open H, ">$HTML_FILE" || die "Can't open $HTML_FILE: $!\n";

    # This Javascript changes the src of the "locPlot" <img> tag when the
    # "show plot" link is clicked for each TF
    print H <<EOF;
<html><head><title>$basename</title>

<SCRIPT LANGUAGE="JavaScript">
<!--
function change_image(image) {
     if(document.images)
     {
                document["histPlot"].src = image
     }
}
function change_image3(histImage, chrImage, vizImage) {
     if(document.images)
     {
                document["histPlot"].src = histImage
                document["chrPlot"].src = chrImage
                document["vizPlot"].src = vizImage
     }
}

function toggle_element(id) {
     if(document[id].style.display == "")
         document[id].style.display = "none";
     else
         document[id].style.display = "";
}

function hide_images() {
     toggle_element("histPlot")
     toggle_element("chrPlot")
     toggle_element("vizPlot")
     toggle_element("bkPlot")
}
//-->
</SCRIPT> 

<style type="text/css">	\@import "../screen.css"; </style>

</head>
<body bgcolor="white">
EOF

    my @sortedGS = sort { $ks{$a->name()} <=> $ks{$b->name()}} @{$geneSets};

    my $topImg = (scalar(@sortedGS) > 0 ? pngForGS($sortedGS[0], "hist", $IMAGE_FORMAT) : "");

    my ($plotHTML, $tableClass);
    if($ORGANISM eq "sc")
    {
	$tableClass = qt("narrow");
	my $topImg2 = (scalar(@sortedGS) > 0 ? pngForGS($sortedGS[0], "chr", $IMAGE_FORMAT) : "");
	my $topImg3 = (scalar(@sortedGS) > 0 ? pngForGS($sortedGS[0], "viz", $IMAGE_FORMAT) : "");
	$plotHTML = tag("a", tag("img", "", {src=>$topImg2, 
				    alt=>"plots by chromosome", 
				    name=>"chrPlot"}),
			
			{onClick=>"toggle_element('chrPlot')"})
		    . 
	            tag("a", tag("img", "", {src=>$topImg, 
				    alt=>"chromosome binding locations", 
				    name=>"histPlot"}),
			{onClick=>"toggle_element('histPlot')"})
	            . "<br>" .
		    tag("a", tag("img", "", {src=>$topImg3, 
					     alt=>"chromosome visualization", 
					     name=>"vizPlot"}),
			{onClick=>"toggle_element('vizPlot')"});
    }
    else
    {
	$tableClass = qt("wide");
	$plotHTML = tag("img", "", {src=>$topImg, 
				    alt=>"chromosome binding locations", 
				    name=>"histPlot"}) . "<br>";
    }

    # Print the ORFlist file name, the TF-specific plot, 
    # and the background distribution plot
    print H join("\n", (tag("h3", $basename), 
			"All P-values (black), Fraction subtelomeric (grey)" . "<br>",
			tag("img", "", {src=>$KS_PLOT_FILE, alt=>"all KS pvalues"}),
			tag("div",
			    tag("a", "Hide/Swap All", {onClick=>"hide_images()"}) .
			    " Click to hide one." .
			    "<br>" . 
			    $plotHTML . 
			    tag("a", tag("img", "", {src=>$BKGD_PLOT_FILE, 
						     alt=>"no background distribution", 
						     name=>"bkPlot"}),
				{onClick=>"toggle_element('bkPlot')"}),
			    {id=>"histograms"})));
    
    print H "Min Telomere Distance cutoff = [$mtdCutoff]\n";
    print H "<br>Kolmogorov-Smirnoff test = [$KS_TEST]\n";
    print H "<br><br><table id=\"main\" class=$tableClass>\n";
    # Print the table header row
    print H tag("tr", 
		join("\n", tag("th", "Name"), tag("th", "KS Pval"), 
		     tag("th", "#Genes"),
		     tag("th", "#Subt"),
		     tag("th", "Frac."),
		     tag("th", "Hist"))
	       ) . "\n";
    
    # subt maps gene sets names to an array of genes with mtd <= mtdCutoff
    my $subt = filterSetsByMTD(\@sortedGS, $mtdCutoff);

    # Print one table row per GeneSet
    foreach my $gs (@sortedGS)
    {
	next unless(defined($gs->dataFile()));

	my $name = $gs->name();
	my $Nsubt = scalar(@{$subt->{$name}});
	my $pval = (exists($ks{$name}) ?  formatPval($ks{$name}) : "NA");
	my $mouseOver;
	if($ORGANISM eq "sc")
	{
	    $mouseOver = sprintf("change_image3( '%s', '%s', '%s')", 
				 pngForGS($gs, "hist", $IMAGE_FORMAT),
				 pngForGS($gs, "chr", $IMAGE_FORMAT),
				 pngForGS($gs, "viz", $IMAGE_FORMAT));
	}
	else
	{
	    $mouseOver = sprintf("change_image( '%s')", pngForGS($gs, "hist", $IMAGE_FORMAT));
	}
	my $Ncat = scalar(@{$gs->orfs()});
	print H tag("tr",
		    tag("td", addBreaks($name)) . 
		    tag("td", $pval) . 
		    tag("td", $Ncat, {class=>"center"}) . 
		    tag("td", $Nsubt, {class=>"center"}) . 
		    tag("td", sprintf("%.2f", $Nsubt/$Ncat), {class=>"center"}) . 
		    tag("td", 
			tag("a", "show plot", 
			    { onMouseOver=>$mouseOver }))
		   ) . "\n";
    }

    print H "</table><br>\n";
    
    my $subtTotal = 0;
    map { $subtTotal += scalar(@{$subt->{$_}}) } keys %{$subt};

    my $subtByGene = invertORFlist($subt);
    printf H "Total Subtelomeric genes: %d<br>\n", $subtTotal;
    printf H "Unique Subtelomeric genes: %d<br>\n", scalar(keys %{$subtByGene});

    print H tag("h3", "Subtelomeric genes") . "\n";
    print H "<table id=\"subt\" class=\"wide\">\n";
    my @subtSorted = keysSortedByValueArraySize($subtByGene);

    my @gt1 = which(\@subtSorted, sub {$subtByGene->{$_[0]} > 1});
    my $nameIndex = $features->indexOfField("geneName");
    my $descIndex = $features->indexOfField("description");
    #print STDERR "### nI=$nameIndex, dI=$descIndex\n";
    foreach my $gene (@subtSorted[@gt1])
    {
	my $name = $features->getByIndex($gene, $nameIndex);
	my $desc = $features->getByIndex($gene, $descIndex);

	#printf STDERR "n=$name, d=$desc\n";
	print H tag("tr",
		    tag("td", $gene) .
		    tag("td", $name) .
		    tag("td", scalar(@{$subtByGene->{$gene}})) .
		    tag("td", $desc) .
		    tag("td", join("<br>\n", @{$subtByGene->{$gene}}))
		   ), "\n";
    }
    print H "</table>\n";

    print H "</body></html>\n";
}

# sub makeImageLink
# {
#     my $mouseOver;
#     if($ORGANISM eq "sc")
#     {
# 	$mouseOver = sprintf("change_image3( '%s', '%s', '%s')", 
# 			     pngForGS($gs, "hist"),
# 			     pngForGS($gs, "chr"),
# 			     pngForGS($gs, "viz"));
#     }
#     else
#     {
# 	$mouseOver = sprintf("change_image( '%s')", pngForGS($gs, "hist"));
#     }
#     tag("a", "show plot", 
# 	{ onMouseOver=>$mouseOver }))
# }

# replace _ and . with spaces
sub addBreaks
{
    my ($word) = @_;

    $word =~ s/[_\.]/ /g;

    return $word;
}

##
## Utility method used to wrap text in an arbitrary 
## HTML tag with attributes
##
sub tag
{
    my ($tag, $str, $attr_hash) = @_;

    my $h = "<$tag";

    if(defined($attr_hash))
    {
        foreach my $key (keys %{$attr_hash})
        {
            $h .= qq( $key="$attr_hash->{$key}");
        }
    }
    
    if(defined($str))
    {
        $h .= ">$str</$tag>";
    }
    else
    {
        $h .= "/>";
    }

    return $h;
}

# utility method to format a pvalue
sub formatPval
{
    my ($pval) = @_;
    if($pval < 1e-6)
    {
	return(sprintf("%.2e", $pval))
    }
    return(sprintf("%.6f", $pval))
}

# R script is expected to be written and executed in $OUTDIR
sub writeRcommands
{
    my ($geneSets) = @_;
    
    open(OUT, ">$R_FILE") || die "Can't open $R_FILE: $!\n";

    my $header = <<EOF;
source("ProbeDist.R")
bkgd.max <- NA
D.list <- list()
GLOBAL.ks.test.alternative <- "$KS_TEST"
EOF

    if($BKGD_FILE ne "")
    {
	$header .= newRImage("$OUTDIR/$BKGD_PLOT_FILE", $IMAGE_FORMAT);
	$header .= qq(dat <- read.table(file="$BKGD_FILE", header=T));
	$header .= q(
all.results <- plot.hist.modular(dat, main="Background distribution")
bkgd.max <-  all.results$h$mids[which.max(all.results$h$counts)]
abline(v=bkgd.max, col="blue", lty=2)
dev.off()

);
    }

    my $ylim = $USE_AGILENT ? "c(0,20)" : "c(0,30)";

    print OUT $header;

    if($ORGANISM eq "sc")
    {
	printf OUT "sc.chr <- read.chr.data(%s)\n", qt( $CHROMOSOME_FILES{$ORGANISM} );
    }

    foreach my $gs (@{$geneSets})
    {
	next unless(defined($gs->dataFile()));

	my $name = $gs->name();
	
	print OUT sprintf("dat <- read.table(file=%s, header=T)\n", qt($OUTDIR . "/" . $gs->dataFile()));
	if($ORGANISM eq "sc")
	{
	    #print OUT sprintf("results <- plot.yeast(dat, mtd.thresh=3e4, main=%s, ylim=%s, bkgd.max=bkgd.max, all.results=all.results)\n", qt($name), $ylim);
	    print OUT newRImage($OUTDIR . "/" . pngForGS($gs, "hist", $IMAGE_FORMAT), $IMAGE_FORMAT);
	    print OUT sprintf("results <- plot.hist.modular(dat, mtd.thresh=%s, main=%s, ylim=%s, bkgd.max=bkgd.max, all.results=all.results)\n", 
			      $MTD_CUTOFF, qt($name), $ylim);
	    print OUT "dev.off()\n";
	    print OUT newRImage($OUTDIR . "/" . pngForGS($gs, "chr", $IMAGE_FORMAT), $IMAGE_FORMAT);
	    print OUT sprintf("plot.chr(dat, mtd.thresh=%s)\n", $MTD_CUTOFF);
	    print OUT "dev.off()\n";
	    print OUT newRImage($OUTDIR . "/" . pngForGS($gs, "viz", $IMAGE_FORMAT), $IMAGE_FORMAT);
	    print OUT sprintf("viz.chr(dat, mtd.thresh=%s, chr.data=sc.chr)\n", $MTD_CUTOFF);
	}
	else
	{
	    print OUT newRImage($OUTDIR . "/" . pngForGS($gs, "hist", $IMAGE_FORMAT), $IMAGE_FORMAT);
	    print OUT sprintf("results <- plot.hist.modular(dat, main=%s, ylim=%s, bkgd.max=bkgd.max, all.results=all.results)\n", qt($name), $ylim);
	}

	print OUT sprintf("D.list <- c(D.list, list(list(cat=%s, D=results\$D)))\n", qt($name));
	print OUT sprintf("dev.off()\n\n");
    }

    print OUT newRImage($OUTDIR . "/" . $KS_PLOT_FILE, $IMAGE_FORMAT, 400, 250);
    print OUT sprintf("plot.pvalues(outfile=%s, D.list=D.list, all.results=all.results, mtd.thresh=%d)\n", qt($OUTDIR . "/" . $KS_TAB_FILE), $MTD_CUTOFF);
    print OUT qq(dev.off()\n);

    close OUT;
}

#Generate the R command to start a new image plot
sub newRImage
{
    my ($outfile, $type, $height, $width) = @_;


    if($type eq "postscript" || $type eq "ps")
    {
	# postscript and pdf hight and width are in inches, not pixels
	# so divide by 100 (approx scaling)
	if(!defined($height)) 	{ $height = 4 }
	else { $height = $height/100 }
	
	if(!defined($width)) { $width = 4 }
	else { $width = $width/100}

	return sprintf("postscript(file=%s, width=%s, height=%s)\n", 
		       qt($outfile), $height, $width);
    }
    if($type eq "pdf")
    {
	if(!defined($height)) 	{ $height = 4 }
	else { $height = $height/100 }
	
	if(!defined($width)) { $width = 4 }
	else { $width = $width/100}

	return sprintf("pdf(file=%s, width=%s, height=%s)\n",
		       qt($outfile), $height, $width);
    }
    else
    {
	$height = 300 if(!defined($height));
	$width = 300 if(!defined($width));
	return sprintf("png(filename=%s, width=%s, height=%s)\n",
		       qt($outfile), $height, $width);
    }
}

sub pngForGS
{
    my ($gs, $plotType, $format) = @_;
    
    my $filename = $gs->dataFile();
    if(defined($plotType))
    {
	$filename .= "." . $plotType;
    }
    if(defined($format))
    {
	$filename .= "." . $format;
    }
    return $filename;
}

# enclose a string in double quotes
sub qt
{
    return '"' . shift(@_) . '"';
}

