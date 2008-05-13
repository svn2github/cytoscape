#!/usr/bin/perl -w
#
# Script to fix the dbxref table created by the Stage1-4 scripts
# Insert a row into the dbxref table
#
while(<>)
{
    chomp;
    print "insert into dbxref (xref_key, xref_dbname) values ('$_', 'CELLCIRCUITS_MISSING_GENE');\n";
}
