#!/bin/bash

OUT=output
DB=cc
INSERT=1
OUT_SQL=$OUT/sql/ALL-insert-MODEL_SIMILARITY.sql

./bin/do-data-dump.sh $DB $OUT

./bin/score-all-models.pl -a /var/www/html/search/data $OUT/cache \
		    Begley2002_MCR \
		    Bernard2005_PSB \
		    de_Lichtenberg2005_Science \
		    Gandhi2006_NG \
		    Hartemink2002_PSB \
		    Haugen2004_GB \
		    Ideker2002_BINF \
		    Kelley2005_NBT \
		    Sharan2005_PNAS \
		    Suthram2005_Nature \
		    Yeang2005_GB \
 > $OUT_SQL

#mysql -u mdaly --password=mdalysql cc < sql/create_model_similarity.sql

test $INSERT && {
   echo "Inserting $OUT_SQL"

   echo "Before"
   mysql -v -u mdaly --password=mdalysql $DB \
    -e "select count(*) from model_similarity";

   mysql -u mdaly --password=mdalysql $DB < $OUT_SQL

   echo "After"
   mysql -v -u mdaly --password=mdalysql $DB \
    -e "select count(*) from model_similarity";
    
   echo "File size" 
   echo `wc -l $OUT_SQL`

}
