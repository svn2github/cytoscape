#!/usr/bin/perl

####################################################################################################
# Authors: Junghwan Park, Iliana Avila-Campillo
# Last modified: December 9, 2005 by Iliana
# Requires XML::Parser::Expat Perl module from CPAN
####################################################################################################

use XML::Parser::Expat;

print "----------------- iproclass_parser.pl -------------------\n";

if(scalar(@ARGV) == 0){
	print "USAGE: perl iproclass_parser.pl <iproclass.xml path>\n"
	die;
}

my ($oid, $hasit, %data, $ipcid);

$oid = 0;
$| = 1;

$parserarser = new XML::Parser::Expat(
		Style 	   => 'Stream', 
		Namespaces => 'ftp://ftp.pir.georgetown.edu/pir_databases/iproclass/iproclass.xml.dtd');

$parserarser->setHandlers( 'Start' => \&xmlstart, 'End' => \&xmlend, 'Char' => \&xmlchar );

open (FH, $ARGV[0]) or die "Could not open $ARGV[0]\n";

system('mkdir xref/parsed');

open (F1, '>./xref/parsed/tdf.keydb') or die "Could not create ./xref/parsed/tdf.keydb\n";
open (F2, '>./xref/parsed/tdf.hasit') or die "Could not create ./xref/parsed/tdf.hasit\n";
open (F3, '>./xref/parsed/tdf.prot_pir') or die "Could not create ./xref/parsed/tdf.prot_pir\n";
open (F4, '>./xref/parsed/tdf.prot_sprot') or die "Could not create ./xref/parsed/tdf.prot_sprot\n";
open (F5, '>./xref/parsed/tdf.prot_trembl') or die "Could not create ./xref/parsed/tdf.prot_trembl\n";
open (F6, '>./xref/parsed/tdf.prot_refseq') or die "Could not create ./xref/parsed/tdf.prot_refseq\n";
open (F7, '>./xref/parsed/tdf.prot_genpeptac') or die "Could not create ./xref/parsed/tdf.prot_genepeptac\n";
open (F8, '>./xref/parsed/tdf.tx_taxonomy') or die "Could not create ./xref/parsed/tdf.tx_taxonomy\n";
open (F9, '>./xref/parsed/tdf.gn_genename') or die "Could not create ./xref/parsed/tdf.gn_genename\n";
open (F10, '>./xref/parsed/tdf.gn_oln') or die "Could not create ./xref/parsed/tdf.gn_oln\n";
open (F11, '>./xref/parsed/tdf.gn_orf') or die "Could not create ./xref/parsed/tdf.gn_orf\n";
open (F12, '>./xref/parsed/tdf.xref_kegg') or die "Could not create ./xref/parsed/tdf.xref_kegg\n";    # not populated here
open (F13, '>./xref/parsed/tdf.xref_gi') or die "Could not create ./xref/parsed/tdf.xref_gi\n";      # not populated here
open (F14, '>./xref/parsed/tdf.xref_ncbigeneid') or die "Could not create ./xref/parsed/tdf.xref_ncbigeneid\n"; # not populated here

#open (F15, '>./xref/parsed/tdf.xref_biblio') or die "Could not create ./xref/parsed/tdf.xref_biblio\n";

open (F16, '>./xref/parsed/tdf.xref_dnaseq') or die "Could not create ./xref/parsed/tdf.xref_dnaseq\n";

#open (F17, '>./xref/parsed/tdf.xref_genomegene_tigr') or die "Could not create ./xref/parsed/tdf.xref_genomegene_tigr\n";
#open (F18, '>./xref/parsed/tdf.xref_genomegene_uwgp') or die "Could not create ./xref/parsed/tdf.xref_genomegene_uwgp\n";
#open (F19, '>./xref/parsed/tdf.xref_genomegene_sgd') or die "Could not create ./xref/parsed/tdf.xref_genomegene_sgd\n";
#open (F20, '>./xref/parsed/tdf.xref_genomegene_fly') or die "Could not create ./xref/parsed/tdf.xref_genomegene_fly\n";
#open (F21, '>./xref/parsed/tdf.xref_genomegene_mgi') or die "Could not create ./xref/parsed/tdf.xref_genomegene_mgi\n";
#open (F22, '>./xref/parsed/tdf.xref_genomegene_gdb') or die "Could not create ./xref/parsed/tdf.xref_genomegene_gdb\n";
#open (F23, '>./xref/parsed/tdf.xref_genomegene_omim') or die "Could not create ./xref/parsed/tdf.xref_genomegene_omim\n";
#open (F24, '>./xref/parsed/tdf.xref_locus') or die "Could not create ./xref/parsed/tdf.xref_locus\n";

open (F25, '>./xref/parsed/tdf.xref_ontology') or die "Could not create ./xref/parsed/tdf.xref_ontology\n";

#open (F26, '>./xref/parsed/tdf.xref_pdb') or die "Could not create ./xref/parsed/tdf.xref_pdb\n";
#open (F27, '>./xref/parsed/tdf.xref_scop') or die "Could not create ./xref/parsed/tdf.xref_scop\n";

open (F28, '>./xref/parsed/tdf.xref_dip') or die "Could not create ./xref/parsed/tdf.xref_dip\n";
open (F29, '>./xref/parsed/tdf.xref_bind') or die "Could not create ./xref/parsed/tdf.xref_bind\n";
open (F30, '>./xref/parsed/tdf.xref_prolinks') or die "Could not create ./xref/parsed/tdf.xref_prolinks\n";

$start = time;
$parserarser->parse(*FH);
close(FH);
$end = time;

$dur = $end-$start;
printf("done. processed time: %10d secs - %2d:%2d:%2d\n",$dur,int($dur/3600), int(($dur - int($dur/3600)*3600)/60), int($dur-int($dur/60)*60));

# Does nothing right now
sub initvars {
}

############################################################### START HANDLERS ##################################################################
sub xmlstart {
	($parser, $element, %atts) = @_;

	if ($element eq 'iProClassEntry') {
		&initvars;
		$oid++;
		$ipcid = $atts{'ipc-id'};
		$parser->setHandlers( 'Start' => \&startIProClassEntry, 'Char' => \&charIProClassEntry, 'End' => \&endIProClassEntry );
	}
}

sub startIProClassEntry {
	($parser, $element, %atts) = @_;

	if ($element eq 'general-info') {
		$parser->setHandlers( 'Start' => \&startGeneralInfo, 'Char' => \&charGeneralInfo, 'End' => \&endGeneralInfo );
	} elsif ($element eq 'xrefs') {
		$parser->setHandlers( 'Start' => \&startXrefs, 'Char' => \&charXrefs, 'End' => \&endXrefs );
	} elsif ($element eq 'family-class') {
		$parser->setHandlers( 'Start' => \&startNull, 'Char' => \&charNull, 'End' => \&endNull );
	}
}

sub startGeneralInfo {
	($parser, $element, %atts) = @_;

	if ($element eq 'pir-nref-id') {
		$parser->setHandlers( 'Start' => \&startPirNrefId, 'Char' => \&charPirNrefId, 'End' => \&endPirNrefId );
	} elsif ($element eq 'csq-id') {
		$parser->setHandlers( 'Start' => \&startCsqId, 'Char' => \&charCsqId, 'End' => \&endCsqId );
	} elsif ($element eq 'protein-info') {
		$parser->setHandlers( 'Start' => \&startProteinInfo, 'Char' => \&charProteinInfo, 'End' => \&endProteinInfo );
	} elsif ($element eq 'taxonomy') {
		$parser->setHandlers( 'Start' => \&startTaxonomy, 'Char' => \&charTaxonomy, 'End' => \&endTaxonomy);
	} elsif ($element eq 'gene-name') {
		$parser->setHandlers( 'Start' => \&startGeneName, 'Char' => \&charGeneName, 'End' => \&endGeneName);
	} elsif ($element eq 'keywords') {
		$parser->setHandlers( 'Start' => \&startGeneralInfoNull, 'Char' => \&charGeneralInfoNull, 'End' => \&endGeneralInfoNull);
	} elsif ($element eq 'function') {
		$parser->setHandlers( 'Start' => \&startGeneralInfoNull, 'Char' => \&charGeneralInfoNull, 'End' => \&endGeneralInfoNull);
	} elsif ($element eq 'complex') {
		$parser->setHandlers( 'Start' => \&startGeneralInfoNull, 'Char' => \&charGeneralInfoNull, 'End' => \&endGeneralInfoNull);
	}
}

sub startPirNrefId { }
sub startCsqId { }


sub startProteinInfo {
	($parser, $element, %atts) = @_;
	
	if ($element eq 'pir') {
		$parser->setHandlers( 'Start' => \&startPir, 'Char' => \&charPir, 'End' => \&endPir );
	} elsif ($element eq 'sprot') {
		$parser->setHandlers( 'Start' => \&startSprot, 'Char' => \&charSprot, 'End' => \&endSprot );
	} elsif ($element eq 'trembl') {
		$parser->setHandlers( 'Start' => \&startTrembl, 'Char' => \&charTrembl, 'End' => \&endTrembl );
	} elsif ($element eq 'refseq') {
		$parser->setHandlers( 'Start' => \&startRefseq, 'Char' => \&charRefseq, 'End' => \&endRefseq );
	} elsif ($element eq 'genpept-ac') {
		$parser->setHandlers( 'Start' => \&startGenPept, 'Char' => \&charGenPept, 'End' => \&endGenPept );
	}
}

sub startPir {
	($parser, $element, %atts) = @_;

	$data{'prot_pir'}->{'index'} = 0;

	if ($element eq 'pir-id') {
		$parser->setHandlers( 'Start' => \&startPirId, 'Char' => \&charPirId, 'End' => \&endPirId );
	} elsif ($element eq 'pir-name') {
		$parser->setHandlers( 'Start' => \&startPirName, 'Char' => \&charPirName, 'End' => \&endPirName );
	} elsif ($element eq 'pir-ac') {
		$parser->setHandlers( 'Start' => \&startPirAc, 'Char' => \&charPirAc, 'End' => \&endPirAc );
	}
}

sub startPirId { }
sub startPirName { }
sub startPirAc { }

sub startSprot {
	($parser, $element, %atts) = @_;

	$data{'prot_sprot'}->{'index'} = 0;
	
	if ($element eq 'sprot-id') {
		$parser->setHandlers( 'Start' => \&startSprotId, 'Char' => \&charSprotId, 'End' => \&endSprotId );
	} elsif ($element eq 'sprot-name') {
		$parser->setHandlers( 'Start' => \&startSprotName, 'Char' => \&charSprotName, 'End' => \&endSprotName );
	} elsif ($element eq 'sprot-ac') {
		$parser->setHandlers( 'Start' => \&startSprotAc, 'Char' => \&charSprotAc, 'End' => \&endSprotAc );
	}
}

sub startSprotId { }
sub startSprotName { }
sub startSprotAc { }

sub startTrembl { 
	($parser, $element, %atts) = @_;
	$data{'prot_trembl'}->{'index'} = 0;

	if ($element eq 'trembl-id') {
		$parser->setHandlers( 'Start' => \&startTremblId, 'Char' => \&charTremblId, 'End' => \&endTremblId );
	} elsif ($element eq 'trembl-name') {
		$parser->setHandlers( 'Start' => \&startTremblName, 'Char' => \&charTremblName, 'End' => \&endTremblName );
	} elsif ($element eq 'trembl-ac') {
		$parser->setHandlers( 'Start' => \&startTremblAc, 'Char' => \&charTremblAc, 'End' => \&endTremblAc );
	}
}

sub startTremblId { }
sub startTremblName { }
sub startTremblAc { }


sub startRefseq { 
	($parser, $element, %atts) = @_;
	$data{'prot_refseq'}->{'index'} = 0;
	
	if ($element eq 'refseq-name') {
		$parser->setHandlers( 'Start' => \&startRefseqName, 'Char' => \&charRefseqName, 'End' => \&endRefseqName );
	} elsif ($element eq 'refseq-ac') {
		$parser->setHandlers( 'Start' => \&startRefseqAc, 'Char' => \&charRefseqAc, 'End' => \&endRefseqAc );
	}
}

sub startRefseqName { }
sub startRefseqAc { }


sub startGenPept {	
	$data{'prot_genpept'}->{'index'} = 0;
}


sub startTaxonomy {
	($parser, $element, %atts) = @_;
	
	if ($element eq 'source-org') {
		$parser->setHandlers( 'Start' => \&startSourceOrg, 'Char' => \&charSourceOrg, 'End' => \&endSourceOrg );
	} elsif ($element eq 'taxon-group') {
		$parser->setHandlers( 'Start' => \&startTaxonGroup, 'Char' => \&charTaxonGroup, 'End' => \&endTaxonGroup );
	} elsif ($element eq 'taxon-id') {
		$parser->setHandlers( 'Start' => \&startTaxonId, 'Char' => \&charTaxonId, 'End' => \&endTaxonId );
	} elsif ($element eq 'lineage') {
		$parser->setHandlers( 'Start' => \&startLineage, 'Char' => \&charLineage, 'End' => \&endLineage );
	}
}

sub startSourceOrg { }
sub startTaxonGroup { }
sub startTaxonId { }
sub startLineage { }

sub startGeneName { }

sub startGeneralInfoNull { }

sub startXrefs { 
	($parser, $element, %atts) = @_;

	if ($element eq 'biblio') {
		$parser->setHandlers( 'Start' => \&startBiblio, 'Char' => \&charBiblio, 'End' => \&endBiblio );
	} elsif ($element eq 'dna-seq') {
		$parser->setHandlers( 'Start' => \&startDnaSeq, 'Char' => \&charDnaSeq, 'End' => \&endDnaSeq );
	} elsif ($element eq 'genome-gene') {
		$parser->setHandlers( 'Start' => \&startGenomeGene, 'Char' => \&charGenomeGene, 'End' => \&endGenomeGene );
	} elsif ($element eq 'ontology') {
		$parser->setHandlers( 'Start' => \&startOntology, 'Char' => \&charOntology, 'End' => \&endOntology );
	} elsif ($element eq 'structure') {
		$parser->setHandlers( 'Start' => \&startStructure, 'Char' => \&charStructure, 'End' => \&endStructure );
	} elsif ($element eq 'enzyme') {
		$parser->setHandlers( 'Start' => \&startXrefNull, 'Char' => \&charXrefNull, 'End' => \&endXrefNull );
	} elsif ($element eq 'interaction' ) {
		$parser->setHandlers( 'Start' => \&startInteraction, 'Char' => \&charInteraction, 'End' => \&endInteraction );
	} elsif ($element eq 'feature' ) {
		$parser->setHandlers( 'Start' => \&startXrefNull, 'Char' => \&charXrefNull, 'End' => \&endXrefNull );
	} elsif ($element eq 'pathway' ) {
		$parser->setHandlers( 'Start' => \&startXrefNull, 'Char' => \&charXrefNull, 'End' => \&endXrefNull );
	} elsif ($element eq 'ppsd' ) {
		$parser->setHandlers( 'Start' => \&startXrefNull, 'Char' => \&charXrefNull, 'End' => \&endXrefNull );
	} elsif ($element eq 'proteom' ) {
		$parser->setHandlers( 'Start' => \&startXrefNull, 'Char' => \&charXrefNull, 'End' => \&endXrefNull );
	}	
}
sub startBiblio { 
	#($parser, $element, %atts) = @_;

	#$data{'xref_biblio'}->{'index'} = 0;
	
	#if ($element eq 'pmid') {
	#	$parser->setHandlers( 'Start' => \&startPMID, 'Char' => \&charPMID, 'End' => \&endPMID );
	#}
}

sub startPMID { }

sub startDnaSeq {
	($parser, $element, %atts) = @_;
	$data{'xref_dnaseq'}->{'index'} = 0;

	if ($element eq 'genbank-ac') {
		$parser->setHandlers( 'Start' => \&startGenBankAc, 'Char' => \&charGenBankAc, 'End' => \&endGenBankAc );
	}
}

sub startGenBankAc { }

sub startGenomeGene {
	($parser, $element, %atts) = @_;

	if ($element eq 'tigr-id') {
		$parser->setHandlers( 'Start' => \&startTIGR, 'Char' => \&charTIGR, 'End' => \&endTIGR );
	} elsif ($element eq 'uwgp-id') {
		$parser->setHandlers( 'Start' => \&startUWGP, 'Char' => \&charUWGP, 'End' => \&endUWGP );
	} elsif ($element eq 'sgd-id') {
		$parser->setHandlers( 'Start' => \&startSGD, 'Char' => \&charSGD, 'End' => \&endSGD );
	} elsif ($element eq 'fly-id') {	
		$parser->setHandlers( 'Start' => \&startFLY, 'Char' => \&charFLY, 'End' => \&endFLY );
	} elsif ($element eq 'mgi-id') {	
		$parser->setHandlers( 'Start' => \&startMGI, 'Char' => \&charMGI, 'End' => \&endMGI );
	} elsif ($element eq 'gdb-id') {	
		$parser->setHandlers( 'Start' => \&startGDB, 'Char' => \&charGDB, 'End' => \&endGDB );
	} elsif ($element eq 'omim-id') {	
		$parser->setHandlers( 'Start' => \&startOMIM, 'Char' => \&charOMIM, 'End' => \&endOMIM );
	} elsif ($element eq 'locus') {
		$parser->setHandlers( 'Start' => \&startLocus, 'Char' => \&charLocus, 'End' => \&endLocus );
	}
}
	
sub startTIGR { 
	#$data{'xref_tigr'}->{'index'} = 0;
}

sub startUWGP { 
	#$data{'xref_uwgp'}->{'index'} = 0;	
}

sub startSGD {
	#$data{'xref_sgd'}->{'index'} = 0;	
}

sub startFLY { 
	#$data{'xref_fly'}->{'index'} = 0;	
}

sub startMGI { 
	#$data{'xref_mgi'}->{'index'} = 0;	
}

sub startGDB {
	#$data{'xref_gdb'}->{'index'} = 0;	
}

sub startOMIM { 
	#$data{'xref_omim'}->{'index'} = 0;	
}

sub startLocus { 
	#($parser, $element, %atts) = @_;

	#if ($element eq 'locus-id') {
	#	$parser->setHandlers( 'Start' => \&startLocusId, 'Char' => \&charLocusId, 'End' => \&endLocusId );
	#} elsif ($element eq 'locus-name') {
	#       $parser->setHandlers( 'Start' => \&startLocusName, 'Char' => \&charLocusName, 'End' => \&endLocusName );
	#}
}

sub startLocusId { }
sub startLocusName { }

sub startOntology {
	$data{'xref_ontology'}->{'index'} = 0;	
}

sub startStructure { 
	($parser, $element, %atts) = @_;

	#if ($element eq 'pdb-id') {
	#	$parser->setHandlers( 'Start' => \&startPDB, 'Char' => \&charPDB, 'End' => \&endPDB );
	#} elsif ($element eq 'scop-id' ) {
	#	$parser->setHandlers( 'Start' => \&startSCOP, 'Char' => \&charSCOP, 'End' => \&endSCOP );
	#}
}

sub startPDB { 
	#$data{'xref_pdb'}->{'index'} = 0;	
}
sub startSCOP { 
	#$data{'xref_scop'}->{'index'} = 0;	
}

sub startInteraction { 
	($parser, $element, %atts) = @_;

	if ($element eq 'dip-id') {
		$parser->setHandlers( 'Start' => \&startDIP, 'Char' => \&charDIP, 'End' => \&endDIP );
	} elsif ($element eq 'bind-id') {
		$parser->setHandlers( 'Start' => \&startBIND, 'Char' => \&charBIND, 'End' => \&endBIND );
	}
}

sub startDIP { 
	$data{'xref_dip'}->{'index'} = 0;	
}
sub startBIND {
	$data{'xref_bind'}->{'index'} = 0;	
}

sub startXrefNull { }

sub startNull { }


################################################## CHAR HANDLERS #########################################################

sub xmlchar {}

sub charIProClassEntry {}

sub charGeneralInfo {}

sub charPirNrefId { 
	#($parser, $str) = @_;
	#$data{'prot_pir'}->{'pirnrefid'} = $str;
}
sub charCsqId {
	($parser, $str) = @_;
	$data{'prot_pir'}->{'csqid'} = $str;
}

sub charProteinInfo {}

sub charPir {}

sub charPirId { 
	#($parser, $str) = @_;
	#$data{'prot_pir'}->{'pir'}->{'pirid'}=$str;
}
sub charPirName {
	#($parser, $str) = @_;
	#$data{'prot_pir'}->{'pir'}->{'pirname'}=$str;
}
sub charPirAc { 
	#($parser, $str) = @_;
	#$data{'prot_pir'}->{'pir'}->{'pirac'}=$str;
}

sub charSprot {}

sub charSprotId {
	($parser, $str) = @_;
	$data{'prot_sprot'}->{'sprotid'} = $str;
}
sub charSprotName {
	($parser, $str) = @_;
	$data{'prot_sprot'}->{'sprotname'} = $str;
}
sub charSprotAc {
	($parser, $str) = @_;
	$data{'prot_sprot'}->{'sprotac'} = $str;
}

sub charTrembl {}

sub charTremblId {
	($parser, $str) = @_;
	$data{'prot_trembl'}->{'tremblid'} = $str;
}

sub charTremblName {
	($parser, $str) = @_;
	$data{'prot_trembl'}->{'tremblname'} = $str;
}
sub charTremblAc {
	($parser, $str) = @_;
	$data{'prot_trembl'}->{'tremblac'} = $str;
}

sub charRefseq {}

sub charRefseqName {
	($parser, $str) = @_;
	$data{'prot_refseq'}->{'refseqname'} = $str;
}
sub charRefseqAc {
	($parser, $str) = @_;
	$data{'prot_refseq'}->{'refseqac'} = $str;
}

sub charGenPept {	
	($parser, $str) = @_;
	$data{'prot_genpept'}->{'genpeptac'} = $str;
}

sub charTaxonomy {}

sub charSourceOrg { 
	($parser, $str) = @_;
	$data{'tx_taxonomy'}->{'sourceorg'} = $str;
}
sub charTaxonGroup { 
	($parser, $str) = @_;
	$data{'tx_taxonomy'}->{'taxongroup'} = $str;
}
sub charTaxonId { 
	($parser, $str) = @_;
	$data{'tx_taxonomy'}->{'taxonid'} = $str;
}
sub charLineage { 
	($parser, $str) = @_;
	$data{'tx_taxonomy'}->{'lineage'} = $str;
}

sub charGeneName { 
	($parser, $str) = @_;
	$data{'gn_genename'}->{'whole'} = $str;
}

sub charGeneralInfoNull {}

sub charXrefs {}

sub charBiblio {}

sub charPMID { 
	#($parser, $str) = @_;
	#$data{'xref_biblio'}->{'pmid'} = $str;
}

sub charDnaSeq {}

sub charGenBankAc {
	$data{'dnaseq'}->{'genbankac'} = $str;
}

sub charGenomeGene {
}
	
sub charTIGR { 
	#($parser, $str) = @_;
	#$data{'genenomegene'}->{'tigr'} = $str;
}
sub charUWGP { 
	#($parser, $str) = @_;
	#$data{'genenomegene'}->{'uwgp'} = $str;
}
sub charSGD {
	#($parser, $str) = @_;
	#$data{'genenomegene'}->{'sgd'} = $str;
}
sub charFLY { 
	#($parser, $str) = @_;
	#$data{'genenomegene'}->{'fly'} = $str;
}
sub charMGI { 
	#($parser, $str) = @_;
	#$data{'genenomegene'}->{'mgi'} = $str;
}
sub charGDB {
	#($parser, $str) = @_;
	#$data{'genenomegene'}->{'gdb'} = $str;
}
sub charOMIM { 
	#($parser, $str) = @_;
	#$data{'genenomegene'}->{'omim'} = $str;
}
sub charLocus {}

sub charLocusId { 
	#($parser, $str) = @_;
	#$data{'locus'}->{'locusid'} = $str;
}
sub charLocusName { 
	#($parser, $str) = @_;
	#$data{'locus'}->{'locusname'} = $str;
}

sub charOntology {
	($parser, $str) = @_;
	$data{'ontology'}->{'goid'} = $str;
}
sub charStructure {}

sub charPDB { 
	#($parser, $str) = @_;
	#$data{'pdb'}->{'pdbid'} = $str;
}
sub charSCOP { 
	#($parser, $str) = @_;
	#$data{'scop'}->{'scopid'} = $str;
}

sub charInteraction {}

sub charDIP { 
	($parser, $str) = @_;
	$data{'dip'}->{'dipid'} = $str;
}
sub charBIND {
	($parser, $str) = @_;
	$data{'bind'}->{'bindid'} = $str;
}

sub charXrefNull {}

sub charNull {}

################################################### END HANDLERS ########################################################


sub xmlend {
	($parser, $element) = @_;
}

sub endIProClassEntry {
	($parser, $element) = @_;
	
	print F1 "$oid\tipc\t$ipcid\n";
	
	# clean data for the next iProClassEntry element
	undef $data;
	
	print "Done reading protein in PIR, oid = " + $oid;
	
	$oid++;

	$parser->setHandlers( 'Start' => \&xmlstart, 'Char' => \&xmlchar, 'End' => \&xmlend );
}


sub endGeneralInfo {
	($parser, $element) = @_;	
	$parser->setHandlers( 'Start' => \&startIProClassEntry, 'Char' => \&charIProClassEntry, 'End' => \&endIProClassEntry );
}

sub endPirNrefId { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startGeneralInfo, 'Char' => \&charGeneralInfo, 'End' => \&endGeneralInfo );
}

sub endCsqId {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startGeneralInfo, 'Char' => \&charGeneralInfo, 'End' => \&endGeneralInfo );
}

sub endProteinInfo {
	($parser, $element) = @_;

	unless (exists($data{'prot_pir'}->{'recorded'})) {
		print F2 "$oid\tprot_pir\n";
		print F3 "$oid\t$ipcid\t$data{'prot_pir'}->{'csqid'}\n";
	}
	
	undef $data{'prot_pir'}->{'recorded'} if (exists($data{'prot_pir'}->{'recorded'}));
	#undef $data{'prot_pir'}->{'pirnrefid'};
	undef $data{'prot_pir'}->{'csqid'};
	undef $data{'prot_pir'};
	
	$parser->setHandlers( 'Start' => \&startGeneralInfo, 'Char' => \&charGeneralInfo, 'End' => \&endGeneralInfo );
}

sub endPir {
	($parser, $element) = @_;
	
	print F2 "$oid\tprot_pir\n";
	
	#print F3 "$oid\t$ipcid\t$data{'prot_pir'}->{'pirnrefid'}\t$data{'prot_pir'}->{'csqid'}\t$data{'prot_pir'}->{'pir'}->{'pirid'}\t$data{'prot_pir'}->{'pirname'}\t$data{'prot_pir'}->{'pirac'}\n";
	
	print F3 "$oid\t$ipcid\t$data{'prot_pir'}->{'csqid'}\n";
	
	$data{'prot_pir'}->{'recorded'} = 1;

	undef $data{'prot_pir'}->{'pir'}->{'pirid'};
	undef $data{'prot_pir'}->{'pir'}->{'pirname'};
	undef $data{'prot_pir'}->{'pir'}->{'pirac'};
	undef $data{'prot_pir'}->{'pir'};
	
	$parser->setHandlers( 'Start' => \&startProteinInfo, 'Char' => \&charProteinInfo, 'End' => \&endProteinInfo );
}

sub endPirId { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startPir, 'Char' => \&charPir, 'End' => \&endPir );
}

sub endPirName {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startPir, 'Char' => \&charPir, 'End' => \&endPir );
}

sub endPirAc { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startPir, 'Char' => \&charPir, 'End' => \&endPir );
}

sub endSprot {
	($parser, $element) = @_;

	print F2 "$oid\tprot_sprot\n";
	print F4 "$oid\t$data{'prot_sprot'}->{'sprotid'}\t$data{'prot_sprot'}->{'sprotname'}\t$data{'prot_sprot'}->{'sprotac'}\n";

	undef $data{'prot_sprot'}->{'sprotid'};
	undef $data{'prot_sprot'}->{'sprotname'};
	undef $data{'prot_sprot'}->{'sprotac'};
	undef $data{'prot_sprot'};
	
	$parser->setHandlers( 'Start' => \&startProteinInfo, 'Char' => \&charProteinInfo, 'End' => \&endProteinInfo );
}

sub endSprotId {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startSprot, 'Char' => \&charSprot, 'End' => \&endSprot );
}
sub endSprotName {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startSprot, 'Char' => \&charSprot, 'End' => \&endSprot );
}
sub endSprotAc {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startSprot, 'Char' => \&charSprot, 'End' => \&endSprot );
}

sub endTrembl { 
	($parser, $element) = @_;
	
	print F2 "$oid\tprot_trembl\n";
	print F5 "$oid\t$data{'prot_trembl'}->{'tremblid'}\t$data{'prot_trembl'}->{'tremblname'}\t$data{'prot_trembl'}->{'tremblac'}\n";

	undef $data{'prot_trembl'}->{'tremblid'};
	undef $data{'prot_trembl'}->{'tremblname'};
	undef $data{'prot_trembl'}->{'tremblac'};
	undef $data{'prot_trembl'};
	
	$parser->setHandlers( 'Start' => \&startProteinInfo, 'Char' => \&charProteinInfo, 'End' => \&endProteinInfo );
}

sub endTremblId {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startTrembl, 'Char' => \&charTrembl, 'End' => \&endTrembl );
}

sub endTremblName {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startTrembl, 'Char' => \&charTrembl, 'End' => \&endTrembl );
}
sub endTremblAc {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startTrembl, 'Char' => \&charTrembl, 'End' => \&endTrembl );
}

sub endRefseq { 
	($parser, $element) = @_;

	print F2 "$oid\tprot_refseq\n";
	print F6 "$oid\t$data{'prot_refseq'}->{'refseqname'}\t$data{'prot_refseq'}->{'refseqac'}\n";
	
	undef $data{'prot_refseq'}->{'refseqname'};
	undef $data{'prot_refseq'}->{'refseqac'};
	undef $data{'prot_refseq'};
	
	$parser->setHandlers( 'Start' => \&startProteinInfo, 'Char' => \&charProteinInfo, 'End' => \&endProteinInfo );
}

sub endRefseqName {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startRefseq, 'Char' => \&charRefseq, 'End' => \&endRefseq );
}

sub endRefseqAc {
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startRefseq, 'Char' => \&charRefseq, 'End' => \&endRefseq );
}

sub endGenPept {	
	($parser, $element) = @_;
	
	print F2 "$oid\tprot_genpept\n";
	print F7 "$oid\t$data{'prot_genpept'}->{'genpeptac'}\n";

	undef $data{'prot_genpept'}->{'genpeptac'};
	undef $data{'prot_genpept'};
	
	$parser->setHandlers( 'Start' => \&startProteinInfo, 'Char' => \&charProteinInfo, 'End' => \&endProteinInfo );
}

sub endTaxonomy {
	($parser, $element) = @_;

	print F2 "$oid\ttx_taxonomy\n";
	print F8 "$oid\t$data{'tx_taxonomy'}->{'sourceorg'}\t$data{'tx_taxonomy'}->{'taxongroup'}\t$data->{'tx_taxonomy'}->{'taxonid'}\t$data->{'tx_taxonomy'}->{'lineage'}\n";

	undef $data{'tx_taxonomy'}->{'sourceorg'};
	undef $data{'tx_taxonomy'}->{'taxongroup'};
	undef $data{'tx_taxonomy'}->{'taxonid'};
	undef $data{'tx_taxonomy'}->{'lineage'};
	undef $data{'tx_taxonomy'};

	$parser->setHandlers( 'Start' => \&startGeneralInfo, 'Char' => \&charGeneralInfo, 'End' => \&endGeneralInfo );
}

sub endSourceOrg { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startTaxonomy, 'Char' => \&charTaxonomy, 'End' => \&endTaxonomy );
}

sub endTaxonGroup { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startTaxonomy, 'Char' => \&charTaxonomy, 'End' => \&endTaxonomy );
}

sub endTaxonId { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startTaxonomy, 'Char' => \&charTaxonomy, 'End' => \&endTaxonomy );
}
sub endLineage { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startTaxonomy, 'Char' => \&charTaxonomy, 'End' => \&endTaxonomy );
}

sub endGeneName { 
	($parser, $element) = @_;
	
	$str = $data{'gn_genename'}->{'whole'};
	
	@names = split(/\;\s/, $str);
	
	foreach $name (@names) {
		@tn = split(/=/, $name);
		$type = $tn[0];
		$cid = $tn[1];

		@ids = split(/\,\s/, $cid);
		
		foreach $id (@ids) {
			if ($type =~ /^Name/) {
				print F2 "$oid\tgn_genename\n";
				print F9 "$oid\t$id\n";
			} elsif ($type =~ /^OrderedLocusName/) {
				print F2 "$oid\tgn_oln\n";
				print F10 "$oid\t$id\n";
			} elsif ($type =~ /^ORFName/) {
				print F2 "$oid\tgn_orf\n";
				print F11 "$oid\t$id\n";
			}	
		}
	}
	
	undef $data{'gn_genename'}->{'whole'};
	undef $data{'gn_genename'};

	$parser->setHandlers( 'Start' => \&startGeneralInfo, 'Char' => \&charGeneralInfo, 'End' => \&endGeneralInfo );
}

sub endGeneralInfoNull { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startGeneralInfo, 'Char' => \&charGeneralInfo, 'End' => \&endGeneralInfo );
}

sub endXrefs { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startIProClassEntry, 'Char' => \&charIProClassEntry, 'End' => \&endIProClassEntry );
}

sub endBiblio { 
	($parser, $element) = @_;
	undef $data{'biblio'};
	$parser->setHandlers( 'Start' => \&startXrefs, 'Char' => \&charXrefs, 'End' => \&endXrefs );
}

sub endPMID { 
	($parser, $element) = @_;

	#print F2 "$oid\txref_biblio\n";
	#print F15 "$oid\t$data{'biblio'}->{'pmid'}\n";

	undef $data{'biblio'}->{'pmid'};
	
	$parser->setHandlers( 'Start' => \&startBiblio, 'Char' => \&charBiblio, 'End' => \&endBiblio );
}

sub endDnaSeq {
	($parser, $element) = @_;
	undef $data{'dnaseq'};
	$parser->setHandlers( 'Start' => \&startXrefs, 'Char' => \&charXrefs, 'End' => \&endXrefs );
}

sub endGenBankAc {
	($parser, $element) = @_;

	print F2 "$oid\txref_dnaseq\n";
	print F16 "$oid\t$data{'dnaseq'}->{'genbankac'}\n";

	undef $data{'dnaseq'}->{'genbankac'};

	$parser->setHandlers( 'Start' => \&startDnaSeq, 'Char' => \&charDnaSeq, 'End' => \&endDnaSeq );
}

sub endGenomeGene {
	($parser, $element) = @_;

	undef $data{'genomegene'};
	$parser->setHandlers( 'Start' => \&startXrefs, 'Char' => \&charXrefs, 'End' => \&endXrefs );
}
	
sub endTIGR { 
	($parser, $element) = @_;

	#print F2 "$oid\txref_genomegene_tigr\n";
	#print F17 "$oid\t$data{'genomegene'}->{'tigr'}\n";

	undef $data{'genomegene'}->{'tigr'};
	$parser->setHandlers( 'Start' => \&startGenomeGene, 'Char' => \&charGenomeGene, 'End' => \&endGenomeGene );
}

sub endUWGP { 
	($parser, $element) = @_;

	print F2 "$oid\txref_genomegene_uwgp\n";
	print F18 "$oid\t$data{'genomegene'}->{'uwgp'}\n";

	undef $data{'genomegene'}->{'uwgp'};
	
	$parser->setHandlers( 'Start' => \&startGenomeGene, 'Char' => \&charGenomeGene, 'End' => \&endGenomeGene );
}

sub endSGD {
	($parser, $element) = @_;

	print F2 "$oid\txref_genomegene_sgd\n";
	print F19 "$oid\t$data{'genomegene'}->{'sgd'}\n";

	undef $data{'genomegene'}->{'sgd'};
	
	$parser->setHandlers( 'Start' => \&startGenomeGene, 'Char' => \&charGenomeGene, 'End' => \&endGenomeGene );
}

sub endFLY { 
	($parser, $element) = @_;

	print F2 "$oid\txref_genomegene_fly\n";
	print F20 "$oid\t$data{'genomegene'}->{'fly'}\n";

	undef $data{'genomegene'}->{'fly'};
	
	$parser->setHandlers( 'Start' => \&startGenomeGene, 'Char' => \&charGenomeGene, 'End' => \&endGenomeGene );
}

sub endMGI { 
	($parser, $element) = @_;

	#print F2 "$oid\txref_genomegene_mgi\n";
	#print F21 "$oid\t$data{'genomegene'}->{'mgi'}\n";

	undef $data{'genomegene'}->{'mgi'};
	
	$parser->setHandlers( 'Start' => \&startGenomeGene, 'Char' => \&charGenomeGene, 'End' => \&endGenomeGene );
}

sub endGDB {
	($parser, $element) = @_;

	#print F2 "$oid\txref_genomegene_gdb\n";
	#print F22 "$oid\t$data{'genomegene'}->{'gdb'}\n";

	undef $data{'genomegene'}->{'gdb'};
	
	$parser->setHandlers( 'Start' => \&startGenomeGene, 'Char' => \&charGenomeGene, 'End' => \&endGenomeGene );
}

sub endOMIM { 
	($parser, $element) = @_;

	#print F2 "$oid\txref_genomegene_omim\n";
	#print F23 "$oid\t$data{'genomegene'}->{'omim'}\n";

	undef $data{'genomegene'}->{'omim'};
	
	$parser->setHandlers( 'Start' => \&startGenomeGene, 'Char' => \&charGenomeGene, 'End' => \&endGenomeGene );
}

sub endLocus { 
	($parser, $element) = @_;
	
	#print F2 "$oid\txref_locus\n";
	#print F24 "$oid\t$data{'locus'}->{'locusid'}\t$data{'locus'}->{'locusname'}\n";
	
	undef $data{'locus'}->{'locusid'};
	undef $data{'locus'}->{'locusname'};
	undef $data{'locus'};
	
	$parser->setHandlers( 'Start' => \&startGenomeGene, 'Char' => \&charGenomeGene, 'End' => \&endGenomeGene );
}

sub endLocusId { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startLocus, 'Char' => \&charLocus, 'End' => \&endLocus );
}

sub endLocusName { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startLocus, 'Char' => \&charLocus, 'End' => \&endLocus );
}

sub endOntology {
	($parser, $element) = @_;

	print F2 "$oid\txref_ontology\n";
	print F25 "$oid\t$data{'ontology'}->{'goid'}\n";

	undef $data{'ontology'}->{'goid'};
	undef $data{'ontology'};
	
	$parser->setHandlers( 'Start' => \&startXrefs, 'Char' => \&charXrefs, 'End' => \&endXrefs );
}

sub endStructure { 
	($parser, $element) = @_;

	$parser->setHandlers( 'Start' => \&startXrefs, 'Char' => \&charXrefs, 'End' => \&endXrefs );
}

sub endPDB { 
	($parser, $element) = @_;

	#$parserdbdata = $data{'xref_pdb'}->{'pdbid'};

	#$parserdbdata =~ /([0-9A-Za-z]+)((:([0-9A-Z]))?(\(.+\\))/;

	#$parserdbid = $1;
	#$chain = $3;
	#$detail = $4;
	
	#print F2 "$oid\txref_pdb\n";
	#print F26 "$oid\t$parserdbid\t$chain\t$detail\n";

	undef $data{'xref_pdb'}->{'pdbid'};
	undef $data{'xref_pdb'};

	$parser->setHandlers( 'Start' => \&startStructure, 'Char' => \&charStructure, 'End' => \&endStructure );
}

sub endSCOP { 
	($parser, $element) = @_;
	
	#print F2 "$oid\txref_scop\n";
	#print F27 "$oid\t$data{'xref_scop'}->{'scopid'}\n";

	undef $data{'xref_scop'}->{'scopid'};
	undef $data{'xref_scop'};
	
	$parser->setHandlers( 'Start' => \&startStructure, 'Char' => \&charStructure, 'End' => \&endStructure );
}

sub endInteraction { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startXrefs, 'Char' => \&charXrefs, 'End' => \&endXrefs );
}

sub endDIP { 
	($parser, $element) = @_;

	print F2 "$oid\txref_dip\n";
	print F28 "$oid\t$data{'xref_dip'}->{'dipid'}\n";

	undef $data{'xref_dip'}->{'dipid'};
	undef $data{'xref_dip'};
	
	$parser->setHandlers( 'Start' => \&startInteraction, 'Char' => \&charInteraction, 'End' => \&endInteraction );
}
sub endBIND {
	($parser, $element) = @_;

	print F2 "$oid\txref_bind\n";
	print F29 "$oid\t$data{'xref_bind'}->{'bindid'}\n";

	undef $data{'xref_bind'}->{'bindid'};
	undef $data{'xref_bind'};

	$parser->setHandlers( 'Start' => \&startInteraction, 'Char' => \&charInteraction, 'End' => \&endInteraction );
}

sub endXrefNull { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startXrefs, 'Char' => \&charXrefs, 'End' => \&endXrefs );
}

sub endNull { 
	($parser, $element) = @_;
	$parser->setHandlers( 'Start' => \&startIProClassEntry, 'Char' => \&charIProClassEntry, 'End' => \&endIProClassEntry );
}

