#!/bin/bash
#
# This script is used to load Sourav's data into CellCircuits
# Must be run from the "db" directory
#
# Requires: the file "n_genes_beneath_BY_GO_term_accession.tab"
#           in the db directory
PUB=BandyopadhyayGersten2007
DB=cc
OUTPUT=output
DATA=../data
INSERT=

test -d $OUTPUT || mkdir -p $OUTPUT

./bin/do-data-dump.sh $DB $OUTPUT

./bin/sif2gl-SouravMerril.pl $DATA $OUTPUT $PUB

test $INSERT && {
    echo "### Inserting into MODEL and GENE_MODEL"
    exit;
    mysql -u mdaly --password=mdalysql $DB \
	< $OUTPUT/sql/$PUB.insert-MODEL-GENE_MODEL.sql
    
    mysql -v -u mdaly --password=mdalysql $DB \
	-e "select count(*) from model where pub = '$PUB'";
    
    mysql -v -u mdaly --password=mdalysql $DB \
	-e "select count(*) from gene_model where model_id in (select id from model where pub = '$PUB')";
    }

./bin/compute_enrichment_for_pub.pl $DB n_genes_beneath_BY_GO_term_accession.tab $OUTPUT $PUB

./bin/generate_enrichment_insert_SQL.pl $DB $OUTPUT $PUB


test $INSERT && {
    echo "### Inserting into ENRICHMENT"
    mysql -v -u mdaly --password=mdalysql $DB \
	< $OUTPUT/sql/$PUB.insert-ENRICHMENT.sql
    
    mysql -v -u mdaly --password=mdalysql $DB \
	-e "select count(*) from enrichment where model_id in (select id from model where pub = '$PUB')";
    }



