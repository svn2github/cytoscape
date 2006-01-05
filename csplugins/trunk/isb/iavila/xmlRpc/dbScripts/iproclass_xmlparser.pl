#!/usr/bin/perl

####################################################################################################
# Authors: Iliana Avila-Campillo
# Last modified: December 9, 2005 by Iliana
# Requires XML::Parser::Expat Perl module from CPAN
####################################################################################################

use XML::Parser::Expat;

if(scalar(@ARGV) <= 0){
	print "USAGE: perl iproclass_xmlparser.pl <iproclass.xml location>";
	die;
}

$xmlFile = $ARGV[0];

$parser = new XML::Parser::Expat;
$parser->setHandlers('Start' => \&startXML,'End'   => \&endXML, 'Char'=>\&charNothing);

open (XML, $xmlFile) or die "Could not open file $xmlFile\n";

system("rm -r ipc/parsed");
system("mkdir ipc/parsed");

open (PIR, ">./ipc/parsed/ipc_pir.txt") or die "Could not create ./ipc/parsed/ipc_pir.txt\n";
open (SPROT, ">./ipc/parsed/ipc_sprot.txt") or die "Could not create ./ipc/parsed/ipc_sprot.txt\n";
open (TREMBL, ">./ipc/parsed/ipc_trembl.txt") or die "Could not create ./ipc/parsed/ipc_trembl.txt\n";
open (GB, ">./ipc/parsed/ipc_genbankac.txt") or die "Could not create ./ipc/parsed/ipc_genbankac.txt\n";
open (RS, ">./ipc/parsed/ipc_refseqac.txt") or die "Could not create ./ipc/parsed/ipc_refseqac.txt\n";
open (ONT, ">./ipc/parsed/ipc_ontology.txt") or die "Could not create ./ipc/parsed/ipc_ontology.txt\n";
open (GPEP, ">./ipc/parsed/ipc_genpeptac.txt") or die "Could not create ./ipc/parsed/ipc_genpeptac.txt\n";
open (FUNC, ">./ipc/parsed/ipc_function.txt") or die "Could not create ./ipc/parsed/ipc_function.txt\n";
open (PATH, ">./ipc/parsed/ipc_pathway.txt") or die "Could not create ./ipc/parsed/ipc_pathway.txt\n";
open (KEY, ">./ipc/parsed/ipc_keywords.txt") or die "Could not create ./ipc/parsed/ipc_keywords.txt\n";
open (PDB, ">./ipc/parsed/ipc_pdb.txt") or die "Could not create ./ipc/parsed/ipc_pdb.txt\n";
open (INTERPRO, ">./ipc/parsed/ipc_interpro.txt") or die "Could not create ./ipc/parsed/ipc_interpro.txt\n";

# Variables
my $ipcid;
my %data;
my $entryNum = 0;

$parser->parse(*XML);

close(PIR);
close(SPROT);
close(TREMBL);
close(GB);
close(RS);
close(ONT);
close(GPEP);
close(FUNC);
close(PATH);
close(KEY);
close(XML);
close(PDB);
close(INTERPRO);

sub startXML {
	
	my($p, $el, %atts) = @_;
	
	#print "startXML: $el\n";
	
	$p->setHandlers('Char'=>\&charNothing);
	
	if($el eq 'iProClassEntry'){
		
		# New entry.
		$ipcid = $atts{'ipc-id'};
		$entryNum++;
		print "$ipcid\t$entryNum\n";
	
	}elsif($el eq 'pir-id'){
	
		$p->setHandlers('Char'	=>	\&charPirId);
	
	}elsif($el eq 'pir-ac'){
	
		$p->setHandlers('Char'	=> \&charPirAc);
	
	}elsif($el eq 'sprot-id'){
	
		$p->setHandlers('Char'=>\&charSprotId);
	
	}elsif($el eq 'sprot-ac'){
		
		$p->setHandlers('Char'=>\&charSprotAc);
	
	}elsif($el eq 'trembl-id'){

		$p->setHandlers('Char' => \&charTremblId);

	}elsif($el eq 'trembl-ac'){

		$p->setHandlers('Char' => \&charTremblAc);

	}elsif($el eq 'refseq-ac'){
		
		$p->setHandlers('Char' => \&charRefSeqAc);
	
	}elsif($el eq 'refseq-name'){
	
		$p->setHandlers('Char'=>\&charRefSeqName);
		
	}elsif($el eq 'function'){
	
		$p->setHandlers('Char'=>\&charFunction);
	
	}elsif($el eq 'genbank-ac'){
	
		$p->setHandlers('Char'=>\&charGenBankAc);
	
	}elsif($el eq 'ontology'){
	
		$p->setHandlers('Char'=>\&charOntology);
	
	}elsif($el eq 'pathway'){
	
		$p->setHandlers('Char'=>\&charPathway);
	
	}elsif($el eq 'keywords'){
	
		$p->setHandlers('Char'=>\&charKeywords);
	}elsif($el eq 'genpept-ac'){
	
		$p->setHandlers('Char'=>\&charGenpeptAc);
	
	}elsif($el eq 'pdb-id'){
	
		$p->setHandlers('Char'=>\&charPdbId);
		
	}elsif($el eq 'interpro-id'){
		
		$p->setHandlers('Char'=>\&charInterproId);
		
	}
	

}

sub charNothing{}

sub charPdbId {
	my($p, $str) = @_;
	$data{'pdb-id'} = $str;
}

sub charInterproId {
	my($p, $str) = @_;
	$data{'interpro-id'} = $str;
}

sub charGenpeptAc {
	my($p, $str) = @_;
	$data{'genpept-ac'} = $str;
}

sub charKeywords {
	my($p,$str) = @_;
	$data{'keywords'} = $str;

}

sub charPathway {
	my($p,$str)=@_;
	$data{'pathway'}=$str;
}

sub charOntology {
	my($p,$str)=@_;
	@fields = split /\s+/, $str;
	chomp $fields[0];
	$data{'ontology'}=$fields[0];
}

sub charGenBankAc {
	my($p,$str)=@_;
	$data{'genbank-ac'}=$str;
}

sub charFunction {
	my($p,$str)=@_;
	$data{'function'}=$str;
}

sub charRefSeqAc {
	my($p,$str) = @_;
	$data{'refseq-ac'} = $str;
}

sub charSprotAc {
	my($p, $str) = @_;
	$data{'sprot-ac'} = $str;
}

sub charRefSeqName {
	my($p, $str) = @_;
	$data{'refseq-name'} = $str;
}

sub charSprotId {
	my($p, $str) = @_;
	$data{'sprot-id'} = $str;
}


sub charTremblAc {
	my($p, $str) = @_;
	$data{'trembl-ac'} = $str;
	#print "charTremblAc: [$data{'trembl-ac'}]\n";
}

sub charTremblId {
	my($p, $str) = @_;
	$data{'trembl-id'} = $str;
	#print "charTremblId: [$data{'trembl-id'}]\n";
}

sub charPirId {
	my($p, $str) = @_;
	$data{'pir-id'} = $str;
}

sub charPirAc {
	my($p,$str) = @_;
	$data{'pir-ac'} = $str;
}

sub endXML {
	my($p, $el) = @_;
	
	$p->setHandlers('Char'=>\&charNothing);
	
	if($el eq 'pir'){
		
		print PIR "$ipcid\t$data{'pir-id'}\t$data{'pir-ac'}\n";
		
	}elsif($el eq 'trembl'){
	
		print TREMBL "$ipcid\t$data{'trembl-id'}\t$data{'trembl-ac'}\n";
	
	}elsif($el eq 'sprot'){
		
		print SPROT "$ipcid\t$data{'sprot-id'}\t$data{'sprot-ac'}\n";
		
	}elsif($el eq 'refseq'){
	
		print RS "$ipcid\t$data{'refseq-ac'}\t$data{'refseq-name'}\n";
	
	}elsif($el eq 'function'){
		
		print FUNC "$ipcid\t$data{'function'}\n";
	
	}elsif($el eq 'genbank-ac'){
		
		print GB "$ipcid\t$data{'genbank-ac'}\n";
		
	}elsif($el eq 'ontology'){
	
		print ONT "$ipcid\t$data{'ontology'}\n";
	
	}elsif($el eq 'pathway'){
	
		print PATH "$ipcid\t$data{'pathway'}\n";
	
	}elsif ($el eq 'genpept-ac'){
	
		print GPEP "$ipcid\t$data{'genpept-ac'}\n";
	
	}elsif($el eq 'iProClassEntry'){
		
		undef $data;
		$ipcid = "";
	
	}elsif($el eq 'keywords'){
	
		print KEY "$ipcid\t$data{'keywords'}\n";
	
	}elsif($el eq 'pdb-id'){
		 
		print PDB "$ipcid\t$data{'pdb-id'}\n";
		
	}elsif($el eq 'interpro-id'){
	
		print PDB "$ipcid\t$data{'interpro-id'}\n";
	
	}
		
	#print "endXML: $el\n";
}
                     
                     
                     
                     