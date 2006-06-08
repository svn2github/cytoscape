#!/usr/bin/perl

die "USAGE: $0 <docbook file> <output dir>\n" if ( $#ARGV != 1 );

system "xmlformat -i $ARGV[0]";

mkdir "$ARGV[1]";
mkdir "$ARGV[1]/images";

$prevWasSection = 0;

open OUT, ">$ARGV[0].tmp";
open FILE, "$ARGV[0]" or die;
while(<FILE>) {
	chomp;
	if ( $_ =~ /\<imagedata\s+fileref=\"(.+\=(.+))\"\s*\/\>/ ) {
		$url = $1;
		$file = $2;
		$url =~ s/\&amp\;/\\\&/g;

		if ( $url !~ /http.+/ ) {
			$url = "http://cytoscape.org" . $url;
		}

		$com =  "curl -G $url > $ARGV[1]/images/$file";

		#print "$com\n";

		#system $com;

		print OUT "<imagedata fileref=\"images/$file\"/>\n";
	} elsif ( $_ =~ /\<section\>/ ) {
		$prevWasSection = 1;
	} elsif ( $_ =~ /\<title\>(.+)\<\/title\>/ ) {
		if ( $prevWasSection == 1 ) {
			$title = $1;
			$title =~ s/\W+//g;
			print OUT "<section id=\"$title\">\n$_\n";
			$prevWasSection = 0;
		} else {
			print OUT "$_\n";
		}

	} elsif ( $_ =~ /\<article\>/ ) {
		print OUT "<article id=\"index\">\n";
	} else {
		print OUT "$_\n";
	}
}
close FILE;
close OUT;

system "xsltproc --stringparam use.id.as.filename 1 --stringparam base.dir $ARGV[1]/ /usr/share/sgml/docbook/xsl-stylesheets/javahelp/javahelp.xsl $ARGV[0].tmp"; 

unlink "$ARGV[0].tmp";
