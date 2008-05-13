#! /usr/bin/perl -w

use strict;
use DBI;

my $ccdev = 'mysql -u mdaly --password=mdalysql cellcircuits_dev';

my $usage=<<USG;
 usage: $0 <db-name>

    e.g. $0 cellcircuits_dev

USG

die $usage if(@ARGV != 1);


my $server   = 'localhost';
my $db       = shift @ARGV;
my $username = 'mdaly';
my $password = 'mdalysql';

my $dbh = DBI->connect("dbi:mysql:$db", $username, $password);

my @pubs = qw(
	      Begley2002_MCR
	      Bernard2005_PSB
	      de_Lichtenberg2005_Science
	      Gandhi2006_NG
	      Hartemink2002_PSB
	      Haugen2004_GB
	      Ideker2002_BINF
	      Kelley2005_NBT
	      Sharan2005_PNAS
	      Suthram2005_Nature
	      Yeang2005_GB
	      );

my $sifList_path = '/var/www/html/search/data';

my $model_sql = 'INSERT INTO model (pub,name) VALUES ';

my $insert_sql_FILE = "insert_into_model." . $$ . ".sql";
open INSERT, "> $insert_sql_FILE" or die "Cannot open $insert_sql_FILE: $!\n";

foreach my $pub (@pubs){

    my $sifList = "$sifList_path/$pub/sifList";

    open(LIST_FILE, "< $sifList") or die "Cannot open $sifList: $!\n";

    while(<LIST_FILE>)
    {

	chomp; s/^[.]\/sif\///;
	
	my @line = split(/\t/);
	my $name = $line[0]; $name =~ s/[.]sif$//;

	print INSERT $model_sql, "('$pub','$name');\n";

    }

    close LIST_FILE;

}

close INSERT;

my $create_sql_FILE = "create_model.sql";

print        `cat ./sql/$create_sql_FILE | $ccdev\n`;
print STDERR "cat ./sql/$create_sql_FILE | $ccdev\n";

print        `cat $insert_sql_FILE | $ccdev\n`;
print STDERR "cat $insert_sql_FILE | $ccdev\n";

print `mv $insert_sql_FILE ./sql/insert_into_model.sql\n`;
print `chmod 666 ./sql/insert_into_model.sql\n`;
