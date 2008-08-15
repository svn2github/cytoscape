perl load_enrichment_table_step1_create_gwt_file.pl $1
perl load_enrichment_table_step2_compute_enrichment.pl $1
perl load_enrichment_table_step3_insert_SQL.pl $1
php  load_enrichment_table_step4_email2admin.php $1

