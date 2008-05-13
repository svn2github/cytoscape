#!/bin/bash
#
# Script to load HanYu's data into CellCircuits
# Perhaps generalize this for arbitrary publications
# Created: Nov 2006
# Modified: Sept 2007, to handle Wang and vandeVijver subtypes
#
OUTPUT=output-hanyu
PUB=Chuang2007_MSB
DATA=../data
DB=cc
DB_USER=mdaly
DB_PASS=mdalysql
ENTREZ_CONVERT= # done on CC:chianti Sept 7, 07
MAKE_GL= # done Sept 12 (with LAST_INSERT_ID workaround for chianti)
INSERT_MODEL= # done 9/12 chianti
COMPUTE_ENRICHMENT= # done 9/12 chianto
INSERT_ENRICHMENT=1

mkdir -p $OUTPUT
mkdir -p $OUTPUT/cache
mkdir -p $OUTPUT/sql

./bin/do-data-dump.sh $DB $OUTPUT

test $ENTREZ_CONVERT && {
    echo "Converting entrez gene ids to db symbols"

    # Convert entrez gene ids in the sif files to symbols in the 
    # gene_product table. Create an sql file for inserting the 
    # missing genes into the database.
    for i in $DATA/$PUB/entrez_sif/*
    do
      name=`basename $i`
      echo "Converting EntrezIDs to symbols for $name"
      label="$PUB-$name"
      [ ! -d $DATA/$PUB/sif/$name ] && {
	  mkdir -p $DATA/$PUB/sif/$name
     }

    ./bin/convertHanYuEntrez2Symbol.pl $OUTPUT/cache \
	$OUTPUT/sql $label $DATA/$PUB/entrez_sif/$name $DATA/$PUB/sif/$name "Homo sapiens"

    echo "Inserting missing genes into DB"
    # Insert missing genes into the database
    mysql -v --user=$DB_USER --password=$DB_PASS $DB \
	< $OUTPUT/sql/$label.insert-DBXREF-GENE_PRODUCT-ASSOCIATION.sql

    # Now refresh the cache because we just added the missing genes
    ./bin/do-data-dump.sh $DB $OUTPUT
    done
}

test $MAKE_GL && {
    echo "### Making GL from SIF"
    ./bin/sif2gl-v2.pl $DATA $OUTPUT $PUB
}

test $INSERT_MODEL && {
    echo "### Inserting into MODEL and GENE_MODEL"

    mysql -u $DB_USER --password=$DB_PASS $DB \
	< $OUTPUT/sql/$PUB.insert-MODEL-GENE_MODEL.sql
    
    mysql -v -u $DB_USER --password=$DB_PASS $DB \
	-e "select count(*) from model where pub = '$PUB'";
    
    mysql -v -u $DB_USER --password=$DB_PASS $DB \
	-e "select count(*) from gene_model where model_id in (select id from model where pub = '$PUB')";
    }

test $COMPUTE_ENRICHMENT && {
    echo "### Computing enrichment"
    ./bin/compute_enrichment_for_pub.pl $DB $OUTPUT/cache n_genes_beneath_BY_GO_term_accession.tab $OUTPUT $PUB

    ./bin/generate_enrichment_insert_SQL.pl $DB $OUTPUT $PUB
}

test $INSERT_ENRICHMENT && {
    echo "### Inserting into ENRICHMENT"
    mysql -v -u $DB_USER --password=$DB_PASS $DB \
	< $OUTPUT/sql/$PUB.insert-ENRICHMENT.sql
    
    mysql -v -u $DB_USER --password=$DB_PASS $DB \
	-e "select count(*) from enrichment where model_id in (select id from model where pub = '$PUB')";
    }

test $LAST_STEPS && {
    echo "### Updated synonyms file"
    ./bin/dump-synonym-file.sh $DB $OUTPUT

    echo ">>> You need to copy the new synonym file to cgi-bin/CCDB and"
    echo ">>> create a sym link to it called 'synonyms.cc.latest.tab'"
}

#
# Manual steps
#
# 1. add pub to cgi-bin/CCDB/Constants.pm
# 2. add pub to HIDDEN_TAGS.html (automatically adds to index.html and about page)
# 3. Update Advanced search page
