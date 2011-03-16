
/* definition of global structures */

#define LENMAX 100
#define CDSMAX 15000 /* if too low error will occur */
#define JOINMAX 50
#define ENTMAX 100
#define NDSTMAX 1000
#define NDSTMAX2 1000
#define FILEMAX 100

#define JOINBUF 2000

#define NUM_NUC 4
enum nucleotide { a = 0, t, c, g, n, error };

/* Information about command line(parameter) stated */
/* This structure is included in gloparam */
struct cparam { 
  int argc;
  char **argv;
  char *filename[FILEMAX];
  int filenum;
  int single_cds; /* 1 = only one CDS per entry will be recognized */
  int v; /* 0 = descriptions will not be printed */
  int wa; /* 0 = warning will not be displayed */ 
  int frd; /* 0 = final result will not be printed */
  int ndstmax2; /* max distance from the locus to be counted 
		   in atg_locut_dist function 
		   ex. 200 --> 0 - 199 (3 - 199) */
  int lfd; /* 1 = frequency of pattern found in each LOCUS will be 
	      displayed */
  int lfdd; /* 1 = frequency of pattern found in each LOCUS downstream
	      will be displayed */

  int dstd; /* 1 = atg distance distribution will be displayed */

  int upc; /* 3>= distance within this variable will be displayed 
	    Indicate by positive value */
  int upd; /* additional distance upstream for displaying close atg */

  int dnc; /* 3>= distance within this variable will be displayed
            Indicate by positive value */
  int dnd; /* additional distance downstream for displaying close atg */

  int sms; /* value for smoothing */
  int s2;  /* 1 = smoothing mode 2 */
  int jo; /* 1=accept only keyword join for CDS */
  int pur; /* 1=eliminates mitochondria, immunotype,..etc */
  int puta; /* 1=eliminates putative CDS */
  int ufatg; /* 1=eliminate sequences which have ATG located upstream 
		of start codon in the same frame */
  int elsp; /* 1=if splicing seems to occur in 5'UTR, corresponding
               entry is invalid */
  char v_entry[20]; /* match text for valid entry */

  char CDS_string[20]; /* string that indicate specific region 
                          default: "CDS" */

  char match_pat[20]; /* match pattern */
  char match_org[50]; /* match organism */
  char match_def[50]; /* match definition */
  int iteration; /* usually 1 */

  int pred_mode; /* prediction mode */
  int test_mode; /* test mode */
}; /* information about command line */
   /* This will be included in gloparam */

/* Information about each CDS */
/* This is included in gparam */
/* Number of cds_info in each entry is indicated by ncds2 */  
struct cds_info {
  int cds_start;  /* start of coding region */
  int cds_end;    /* end of coding region */
  int splice;     /* 1 = splicing occurs */
  int complement; /* complement sequence */
  int join[JOINMAX];     /* exon start */
  int join_end[JOINMAX]; /* exon end */
  int njoin;              /* number of exons */
  char gene[100]; /* gene of this coding region */
  char product[100]; /* product of this coding region */
}; /* information about each CDS */

struct seqm {
  int num;
  int start[JOINMAX];
  int end[JOINMAX];
}; /* information about exon and intron segments */

struct gparam {
   char entry_line[LENMAX]; /* LOCUS line of each entry */
   char definition[LENMAX * 4]; /* DEFITITION LINE(valid) of each entry */
   char source[LENMAX]; /* source organism */
   int  cds_start[CDSMAX];  /* records cds start locus(Simple version)*/
   int ncds; /* Number of each CDS in each entry(Simple version.
                It excludes complement, multi-start and
                perhaps splicing sequences). */
   struct cds_info each_cds[CDSMAX];
   int ncds2; /* Number of CDS in each entry */
   int def_valid; /* 1 = valid definition including SOURCE ORGANISM 
                     2 = valid definition when -mdef specified
                     0 = invalid definition */
   struct seqm exon;   /* information about exon in each entry */
   struct seqm intron; /* information about intron in each entry */
}; /* information about each entry */

struct gloparam {
  struct cparam p; /* cparam */
  int nent;  /* number of all the entries */
  int nent2;  /* number of valid entries */
  int nent2_vd; /* number of valid entries including definition */
  int nent3;  /* number of close atg found */
  int nent3_d; /* number of close atg found downstream */
  int nent4;  /* number of close atg not found */
  int nent4_d; /* number of close atg not found downstream */
  int nent5;  /* number of valid CDS (long enough from data start ?) found */
  int nent5_d;
  int total_valid_cds; /* number of total valid CDS */
  int total_atg_start; /* number of CDS that start from atg codon */
  int total_atg; /* number of total atg */
  int total_base; /* number of total bases */
  int total_dist; /* total distance of atg */
  int total_dist_d; /* total distance of atg downstream */
  int dist_dist[NDSTMAX]; /* first pattern found distance distributions */
  int dist_dist_d[NDSTMAX];
  int dist_match[NDSTMAX2]; /* number of match pat found in specific locus */
  int dist_count[NDSTMAX2]; /* number of times that distance is recognzd */
  
  int dist_match_d[NDSTMAX2]; /* number of match pat found in specific locus
				 downstream (ex. -10 -> 10) */
  int dist_count_d[NDSTMAX2]; /* number of times that distance is recognzd
				 downstream */
  int nf_reg; /* number of functions registered */
  int exactly; /* prediction right */
  int wrong; /* prediction wrong */

}; /* information about all the entries */

extern struct gparam entry_info;
extern struct gloparam all;

struct bitset {

  unsigned int b0 : 1;
  unsigned int b1 : 1;
  unsigned int b2 : 1;
  unsigned int b3 : 1;
  unsigned int b4 : 1;
  unsigned int b5 : 1;
  unsigned int b6 : 1;
  unsigned int b7 : 1;
  unsigned int b8 : 1;
  unsigned int b9 : 1;
  unsigned int b10 : 1;
  unsigned int b11 : 1;
  unsigned int b12 : 1;
  unsigned int b13 : 1;
  unsigned int b14 : 1;
  unsigned int b15 : 1;
  unsigned int b16 : 1;
  unsigned int b17 : 1;
  unsigned int b18 : 1;
  unsigned int b19 : 1;
  unsigned int b20 : 1;
  unsigned int b21 : 1;
  unsigned int b22 : 1;
  unsigned int b23 : 1;
  unsigned int b24 : 1;
  unsigned int b25 : 1;
  unsigned int b26 : 1;
  unsigned int b27 : 1;
  unsigned int b28 : 1;
  unsigned int b29 : 1;

};

struct recbest10 {
  double value;
  int int1;
  char str1[100];
  char str2[100];
};



