package CCDB::Constants;

require Exporter;

use strict;
use warnings;


use CCDB::DB;
sub getPubInfo;
sub printHash;
sub getImageFormat;
sub trim;


our @ISA = qw(Exporter);
our @EXPORT = qw();
our @EXPORT_OK = qw($cgi_version
		    $search_url
		    $cgi_url
		    $data_url
		    $chianti_url
		    $db_link_by_species 
		    $colors
		    $SYNONYM_FILE
		    $DEBUG
		    $DEFAULT_ENRICHMENT_LIMIT
		    $species_abbrev
		    $pubInfo
			$pubName
		    $DB_INSTANCE
			$USER
			$PASSWORD
		    );
our $VERSION = 1.0;

our $DEBUG = 0;
our $DB_INSTANCE = "cellcircuits_dev"; # not needed anymore

## limit the number of enrichment retrieved 
## (for model_id based queries only)
our $DEFAULT_ENRICHMENT_LIMIT = 3;  

our $cgi_version  = "v1.0";
our $cgi_url      = "/cgi-bin/cellcircuits";

our $search_url   = "http://chianti.ucsd.edu/cellcircuits/search/"; # home of index page

our $data_url     = "/data";

our $chianti_url = 'http://chianti.ucsd.edu';



#our $SYNONYM_FILE = "/opt/www/cgi-bin/search/${cgi_version}/CCDB/synonyms.$DB_INSTANCE.latest.tab";
our $SYNONYM_FILE = "./CCDB/synonyms.latest.tab";

my $entrezURLFormat = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=%s&query_hl=3";


our $db_link_by_species = {
    'Saccharomyces cerevisiae' => "http://db.yeastgenome.org/cgi-bin/locus.pl?locus=",
    'Caenorhabditis elegans'   => "http://wormbase.org/db/gene/gene?name=",
    'Drosophila melanogaster'  => "http://flybase.bio.indiana.edu/.bin/fbidq.html?",
    'Homo sapiens'             => "",
    'Plasmodium falciparum'    => ""
};

our $colors = {
    page_background => '#ffffff',
    error_message => '#B22222',
    pvalue => '#B22222',
    error_background => '#B4C3CA',
    GO_table_dark  => '#446689',
    GO_table_medium => '#738DA7',
    GO_table_light => '#A2B3C5'
};


## Also used as the list of default species by Driver.pm
our $species_abbrev = {
    'Caenorhabditis elegans'   => 'C. ele',
    'Drosophila melanogaster'  => 'D. mel',
    'Homo sapiens'             => 'H. sap',
    'Plasmodium falciparum'    => 'P. fal',
    'Saccharomyces cerevisiae' => 'S. cer'
    };

# test data only
#our $pubInfo = {
#    Begley2002_MCR => {
#	citation => sprintf($entrezURLFormat, 12496357),
#	name => "Begley, Molecular Cancer Research (2002)",
#	img_format => "jpg",
#	supplement_URL => "",
#    }
#};

#our $pubCitation = {
#    'Begley2002_MCR'             => "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=12496357&query_hl=3"
#};

#our $pubName = {
#    'Begley2002_MCR'             => "Begley, Molecular Cancer Research (2002)"
#};

#our $pubSupplementURL = {
#    'Begley2002_MCR' => "http://www.cbs.dtu.dk/cellcycle/yeast_complexes/complexes.php"
#};

#our $img_format = {
#    Begley2002_MCR             => "jpg"
#    };

#exit();
###end of test section ########


my $dbh = CCDB::DB::getDB();

# Get all publication info from DB
my %pubInfoMap = getPubInfo($dbh);

# Get all publication IDs
our %pubIDMap = ();
while ( my ($key, $value) = each(%pubInfoMap) ) {
    $pubIDMap{$key} = $key;
}
#print "Publication IDMap:\n";
#printHash(\%pubIDMap);

# Get Image formats
my %img_formatMap = ();
while ( my ($key, $value) = each(%pubInfoMap) ) {
    $img_formatMap{$key} = getImageFormat($dbh, $key);
}
our $img_format = \%img_formatMap;
#printHash($img_format);

# Get supplement URL
my %pubSupplementURLMap = ();
while ( my ($key, $value) = each(%pubInfoMap) ) {
    $pubSupplementURLMap{$key} = trim($pubInfoMap{$key}{supplement_url});
}
our $pubSupplementURL = \%pubSupplementURLMap;
#printHash($pubSupplementURL);

my %pubNameMap = ();
while ( my ($key, $value) = each(%pubInfoMap) ) {
    $pubNameMap{$key} = trim($pubInfoMap{$key}{pubmed_html_short});
}
our $pubName = \%pubNameMap;
#printHash($pubName);

# Get pub citation
my %pubCitationMap = ();
#my $entrezURLFormat = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=%s&query_hl=3";
while ( my ($key, $value) = each(%pubInfoMap) ) {
	my $pmid = $pubInfoMap{$key}{pmid};
    $pubCitationMap{$key} = trim(sprintf($entrezURLFormat, $pmid));
}
our $pubCitation = \%pubCitationMap;
#printHash($pubCitation);

#
my %pubInfoMap2 = ();
while ( my ($key, $value) = each(%pubInfoMap) ) {
	my %one_pubMap = ();
	$one_pubMap{citation} = $pubCitationMap{$key};
	$one_pubMap{name} = $pubNameMap{$key};
	$one_pubMap{img_format} = $img_formatMap{$key};
	$one_pubMap{supplement_URL} = $pubSupplementURLMap{$key};
    $pubInfoMap2{$key} = \%one_pubMap;
}

our $pubInfo = \%pubInfoMap2;
#printHash($pubInfo);




#####################################################################
#####################################################################
sub getPubInfo
{
    my ($dbh) = @_;

	my %pubInfoMap; 
	
	my $get_pubInfo_sth = $dbh->prepare("select * from publications");
	$get_pubInfo_sth->execute();
	
	while (my $ref = $get_pubInfo_sth->fetchrow_hashref()) {
		
		my $pub_id = $ref->{'publication_auto_id'}; 
		my $pmid = $ref->{'pmid'};
		my $supplement_url = $ref->{'supplement_url'};
		my $pubmed_xml_record = $ref->{'pubmed_xml_record'};
		my $pubmed_html_short = $ref->{'pubmed_html_short'};
		
		$pubInfoMap{$pub_id}{pmid} = $pmid;
		$pubInfoMap{$pub_id}{supplement_url} = $supplement_url;
		$pubInfoMap{$pub_id}{pubmed_xml_record} = $pubmed_xml_record;
		$pubInfoMap{$pub_id}{pubmed_html_short} = $pubmed_html_short;
	}

	return %pubInfoMap;
}


# Get the network image format for given publication_id
sub getImageFormat
{
    my ($dbh, $pub_id) = @_;
	my $query = qq{SELECT file_name from network_file_info, network_image_files 
					WHERE network_file_info.image_file_id = network_image_files.id
						AND network_file_info.publication_id = ?};
	
	my $get_image_filename_sth = $dbh->prepare($query);
	$get_image_filename_sth->bind_param(1, $pub_id, {TYPE=>4});
	$get_image_filename_sth->execute();

	if (my $ref = $get_image_filename_sth->fetchrow_hashref()) {
		my $filename = $ref->{'file_name'};
		#my ($baseName, $ext) = split /\./, $filename;
		my $last_dot_pos = rindex($filename, ".");
		my $ext = substr($filename,$last_dot_pos+1, length($filename)-$last_dot_pos);
		return $ext;
	}
	return "unknown";
}

# Perl trim function to remove whitespace from the start and end of the string
sub trim($)
{
	my $string = shift;
	$string =~ s/^\s+//;
	$string =~ s/\s+$//;
	return $string;
}


# print hash table, for debug only
sub printHash  {
    my %theHash = %{$_[0]};

	my @keys = keys %theHash;
	my $count = @keys;
	print "Number of items in hash= $count\n";	
	
	while ( my ($key, $value) = each(%theHash) ) {
		if (ref($value) eq 'HASH') {
			print "$key\n";
			foreach my $k (keys %$value) {
				print "\t".$k."=>".$value->{$k}."\n";
			}
			print "\n";	
		}
		else {
			print "$key => $value\n";
		}
	}	
}

return 1;
