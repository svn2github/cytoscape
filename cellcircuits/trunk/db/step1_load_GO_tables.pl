#!/usr/bin/perl

my $dir   = '/cellar/data/cellcircuits';
my $file  = 'GO_tables_list.txt';
my $egdir = 'go_200609-assocdb-tables';

my $ccdev = 'mysql -u mdaly --password=mdalysql cellcircuits_dev';

my $import_cmd = "mysqlimport -u mdaly --password=mdalysql -L cellcircuits_dev"

my $usage=<<USAGE;
$0 <GO-tables-dir> <tables-list-file>

e.g. $0 $egdir $file

USAGE

die $usage if(@ARGV != 2);

my $GO_tables_dir     = join '/', $dir, shift @ARGV;
my $tables_list_file = shift @ARGV;

my $tables_list = read_tables_list_file($tables_list_file);

my @sql = ();
my @txt = ();
for my $i ( 0 .. $#{ $tables_list } )
{
    my $sql = $GO_tables_dir . '/' . $tables_list->[$i] . '.sql'; 
    if(-e $sql)
    { 
	push @sql, $sql; print "$sql\n";
    }
    else 
    {
	die "$sql does not exist.\n";
    }

    my $txt = $GO_tables_dir . '/' . $tables_list->[$i] . '.txt';
    if(-e $txt)
    { 
	push @txt, $txt; print "$txt\n";
    }
    else 
    {
	die "$txt does not exist.\n";
    }
}

my $sql_list = join " ", @sql;

print        `cat $sql_list | $ccdev\n`;
#print STDERR "cat $sql_list | $ccdev\n`;


my $txt_list = join " ", @txt;

print        `$import_cmd $txt_list\n`;
#print STDERR "$import_cmd $txt_list\n";

sub read_tables_list_file
{
    my $f = shift @_;
    
    my @tables = ();
    
    open(FILE, "< $f") or die "Cannot open $f: $!\n";
    while(<FILE>)
    {
	chomp;
	split(/\t/);
	push @tables, $_[0];
    }
    close(FILE);
    
    return \@tables;
}


