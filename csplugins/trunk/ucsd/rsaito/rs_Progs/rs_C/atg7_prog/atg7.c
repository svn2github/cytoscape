#define VERSION "Aug  7 2000"
/* This program reads GenBank file specified by parameter.
   If the file is not specified, GenBank data is read from
   standard input.
*/

/* A new definition of distance: ATG...ATG -> -6
                               :    ATGATG -> -3
   Value will be negative for upstream.
*/
/* Notice:
   isalpha function does not recognize numerals
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <math.h>

#include "global_st.h"
#include "atg_func.h"

#define VALID 1
#define INVALID 2
#define EXHA 3

/* prototype declaration */

void join_read(char *, struct gparam *,struct gloparam *);
void definition_check(char *, int *, int *, 
		      struct gparam *, struct gloparam *);
void atg_locus_dist(struct gparam *,
	       struct gloparam *, char *, int);
void atg_locus_dist_d(struct gparam *,
	       struct gloparam *, char *, int);

void dist_freq_print(int *,int *,int,int, int,int);

void exon_read(char *, struct gparam *, struct gloparam *);

int cdsstart(char *);
int cdsstart_jo(char *);

void atg_dist(struct gparam *, struct gloparam *,
	 char *, int);

void join_get(char *,int *,int *, int *);
void intron_read(char *,struct gparam *, struct gloparam *);
void atg_rat(struct gparam *, struct gloparam *, char *, int);
void trl_pred(char *, int, struct gparam *, struct gloparam *);
int join_cat(char *,char *);
void cdsse_check(struct gparam *,int);
void locbegend(char *, int *, int *);
int cds_notefield_read(char *, struct gparam *, struct gloparam *);
void atg_dist_d(struct gparam *, struct gloparam *, char *, int);
void cds_field_read(char *, struct gparam *, struct gloparam *);
void procing(struct gparam *,struct gloparam *,	char *, int);
void db_analyze(FILE *,struct gparam *,struct gloparam *);
void db_final_result(FILE *,struct gparam *,struct gloparam *);
void gloparam_init(struct gloparam *);



/* Global Structure */
  struct gparam entry_info;
  struct gloparam all;

/* Global Variable */
  char valid_cds[CDSMAX];

void printhelp(void){
  int n;
  printf("*** Help Menu ***      version %s\n",VERSION);

  printf("-mp\t specify match pattern\n");
  printf("-ve\t specify kind of sequence written in LOCUS ");
  printf("by next parameter\n");
  printf("   \t ex:-ve \"mRNA\"  --> mRNA will be analyzed\n");
  printf("-mo\t specify organism\n");
  printf("-mdef\t specify DEFINITION\n");
  printf("-CDS_string\t specify keyword for coding region(default:CDS)\n");

  printf("-scds\t only one CDS per entry will be recognized\n");
  printf("-dist2\t max distance from specific locus for counting patterns\n");
  printf("-v\t displays processing discription\n");
  printf("-pred\t predict translation initiation sites\n");
  printf("-wa\t warn the abnormal data\n");
  printf("-sms\t next integer indicates value for smoothing\n");
  printf("-s2\t smoothing mode 2\n");

  printf("-jo\t only \"join\" is acceptable for the keyword following CDS\n");
  printf("-npur\t do not discard mitochondrial, immunoglobulin sequences\n");
  printf("-puta\t discard putative CDS\n");
  printf("-ufatg\t discard sequences that have ATG ");
  printf("trinucleotides upstream of start codons and in the same frame\n");
  printf("-nelsp\t do not discard sequences which has splicing in 5'UTR\n");

  printf("-finr\t standart analysis\n");
  printf("-locfr\t displays frequencies of pattern found in each LOCUS\n");
  printf("-locfrd\t displays frequencies of pattern found in each LOCUS ");
  printf("downstream\n");
  printf("-dstd\t displays atg distance distribution\n");
  printf("-upc\t Displays 5'UTR sequences if the specific ");
  printf("triplet pattern(default ATG) is within the distance indicated ");
  printf("in the next parameter\n");


  printf("-upd\t When displaying sequences, number of bases upstream ");
  printf("to be displayed will be indicated by the next parameter\n");
  printf("-dnc\t Displays downstream sequences if the specific ");
  printf("triplet pattern(default ATG) is within the distance indicated ");
  printf("in the next parameter\n");

  printf("-dnd\t When displaying sequences, number of bases downstream ");
  printf("to be displayed will be indicated by the next parameter\n");

  for(n = 0;n < MAX_F;n ++){
    if(help[n] != NULL){
      (*help[n])();
    }
  }
}

/* returns number to plus for command line check */
int func_param_check(int argc,char *argv[],int n)
{
  int prm = 0,s;

  for(s = 0;s < MAX_F;s ++){
    if(param[s] != NULL){

      prm = (*param[s])(argc,argv,n);
      if(prm > 0){ 
	fc_flag[s] = 1;
	break;
      }
    }
    else break;
  }
  return prm;
}


void header_f(int fr,char *line)
{
  int n;
  for(n = 0;n < fr;n ++){
    if(fc_flag[n] == 1 && head[n] != NULL)
      (*head[n])(line);
  }
}


void final_f(int fr)
{
  int n;
/*  printf("Final result:%d functions registered\n",fr); */
  for(n = 0;n < fr;n ++)
     if(fc_flag[n] == 1 && final[n] != NULL)
       (*final[n])();
}

/* sets parameters according to argc and argv
   no command itself should exist in argv */
void comline_manage_sub(int argc,char *argv[], struct cparam *p)
{
  int n,s,prm,compro;
  FILE *fp;

  for(n = 0;n < argc;n ++){
/*
   printf("treating %s\n",argv[n]); 
*/
    if(strcmp(argv[n],"-mp") == 0){
      strcpy(p->match_pat,argv[++ n]);
    } /* match pattern */

    else if(strcmp(argv[n],"-ve") == 0){
      strcpy(p->v_entry,argv[++ n]);
    } /* valid entry match text */

    else if(strcmp(argv[n],"-CDS_string") == 0){
      strcpy(p->CDS_string, argv[++ n]);
      /* keywords to find coding region */
    }

    else if(strcmp(argv[n],"-mo") == 0){
      strcpy(p->match_org,argv[++ n]);
    } /* valid entry match organism */

    else if(strcmp(argv[n],"-mdef") == 0){
      strcpy(p->match_def,argv[++ n]);
    }
      /* valid definition */

    else if(strcmp(argv[n],"-scds") == 0){
      p->single_cds  = 1;
    } /* one CDS per entry  */

    else if(strcmp(argv[n],"-dist2") == 0){
      p->ndstmax2 = atoi(argv[++ n]);
    } /* max distance for locus atg counting  */

    else if(strcmp(argv[n],"-v") == 0){
      p->v = 1; /* displays discription. UNDER construction */
    }

    else if(strcmp(argv[n],"-wa") == 0){
      p->wa = 1; /* warning will be displayed */
    }

    else if(strcmp(argv[n],"-finr") == 0){
      p->frd = 1; /* final result will not be displayed */
    }

    else if(strcmp(argv[n],"-sms") == 0){
      p->sms = atoi(argv[ ++ n]);
    } /* smoothing */

    else if(strcmp(argv[n],"-s2") == 0){
      p->s2 = 1;
    } /* smoothing mode 2 */

    else if(strcmp(argv[n],"-jo") == 0){
      p->jo = 1;
    } /* accept only keyword join */

    else if(strcmp(argv[n],"-npur") == 0){
      p->pur = 0;
    } /* eliminates mitochondria, immunotype,..etc */

    else if(strcmp(argv[n],"-puta") == 0){
      p->puta = 1;
    } /* eliminates putative CDS */

    else if(strcmp(argv[n],"-ufatg") == 0){
      p->ufatg = 1;
    } /* eliminate sequences which have ATG located upstream
         of start codon in the same frame */

    else if(strcmp(argv[n],"-nelsp") == 0){
      p->elsp = 0;
    } /* eliminate sequences which seems to have splicing in 5'UTR */

    else if(strcmp(argv[n],"-locfr") == 0){
      p->lfd = 1;
    } /* displays frequency of pattern found in LOCUS */

    else if(strcmp(argv[n],"-locfrd") == 0){
      p->lfdd = 1;
    } /* displays frequency of pattern found in LOCUS downstream */


    else if(strcmp(argv[n],"-dstd") == 0){
      p->dstd = 1;
    } /* displays atg distance distribution */

    else if(strcmp(argv[n],"-upc") == 0){
      p->upc = atoi(argv[++ n]);
    } /* distance within this variable will be displayed */

    else if(strcmp(argv[n],"-upd") == 0){
      p->upd = atoi(argv[ ++n]);
    } /* additional distance upstream for displaying close atg */

    else if(strcmp(argv[n],"-dnc") == 0){
      p->dnc = atoi(argv[++ n]);
    } /* distance(downstream) within this variable will be displayed */

    else if(strcmp(argv[n],"-dnd") == 0){
      p->dnd = atoi(argv[++ n]);
    } /* additional distance downstream for displaying close atg */

    else if(strcmp(argv[n],"-h") == 0){
      printhelp();
      exit(0);
    }

    else if(strcmp(argv[n],"-pred") == 0){
      p->pred_mode = 1;
    } /* prediction mode */

    else if(strcmp(argv[n],"-test") == 0){
      p->test_mode = 1;
    } /* test mode */

    else if((prm = func_param_check(argc,argv,n)) > 0){
      n += prm - 1;
    } /* looks for parameter functions */

    else if((fp = fopen(argv[n],"r")) != NULL){
      fclose(fp);
      p->filename[ p->filenum ++ ] = argv[n];
/*
      printf("File %d:%s\n",p->filenum,p->filename[ p->filenum - 1 ]);
*/
    }

 /* parameter is recognized as file name for data input */

    else {
	fprintf(stderr,"error in command line...\"%s\"\n",argv[n]);
	exit(1);
      }
  }
}


/* gets command line and sets parameters */
void comline_manage(int argc,char *argv[],struct cparam *p)
{
  int n;

/* command line */

  p->argc = argc; 
  p->argv = argv;
  p->filenum = 0;
  p->single_cds = 0;
  p->ndstmax2 = 200;
  p->sms = 1;
  p->s2 = 0;
  p->v = 0;
  p->wa = 0;
  p->frd = 0;
  p->lfd = 0;
  p->lfdd = 0;
  p->dstd = 0;
  p->upc = 0;
  p->upd = 10;
  p->dnc = 0;
  p->dnd = 10;
  p->jo = 1; /* TESTING !!! */
  p->pur = 1;
  p->puta = 0;
  p->ufatg = 0;
  p->elsp = 1;
  strcpy(p->match_pat,"atg");
  strcpy(p->v_entry,"");
  strcpy(p->CDS_string, "CDS");

  p->match_org[0] = '\0';
  p->match_def[0] = '\0';

  p->pred_mode = 0;
  p->test_mode = 0;
  comline_manage_sub(argc - 1,&argv[1],p);
}


/* finds location of nth text */
/* "abc def ghi jkl" 2 -> "ghi jkl" */
/* str1 must end with '\0' */
char *nthtext2(char *str1,int n)
{
  int p,count;

  count = 0;
  p = 0;

  while(p < strlen(str1)){
    if(str1[p] == ' ' || str1[p] == '\t')p ++;
    else if(n == count)return(&str1[p]);
    else {
      count ++;
      while(str1[p] != ' ' && str1[p] != '\t'){
	p ++;
	if(p >= strlen(str1))return NULL;
      }
    }
  }
  return NULL;
}


/* finds entry (keeps reading until the keyword LOCUS is found) */
int find_entry(FILE *fp,struct gparam *entry_info, struct gloparam *all)
{
  char each_line[256];
  char *ve_text;
  ve_text = all->p.v_entry;

  while(fgets(each_line,256,fp) != NULL){
    if(lpatm("LOCUS",each_line)){
      all->nent ++;
      strcpy(entry_info->entry_line,each_line);
      if(lpatm(ve_text,&each_line[36]))return VALID;
      else return INVALID;
    }
  }
  return EXHA;
}


/* reads header and set parameters */
/* file must be opened */
int header_read(FILE *fp,struct gparam *entry_info,struct gloparam *all)
{
  char each_line[256];
  int i,j,k,ct,cdss;
  int def_over;
  struct cds_info *cds0;
  struct seqm *intron, *exon;

  entry_info->ncds = 0;
  entry_info->ncds2 = 0;
  entry_info->def_valid = 1; def_over = 0;
  
  entry_info->intron.num = 0;
  entry_info->exon.num = 0;

  entry_info->definition[0] = '\0';

  header_f(all->nf_reg,entry_info->entry_line);

  i = 0;

  while(fgets(each_line,256,fp) != NULL){
    /* clear line buffer */
    for(k = strlen(each_line);k < 100;k ++)each_line[k] = '\0';
/*  bzero(&each_line[ strlen(each_line) ], 256 - strlen(each_line)); */

    header_f(all->nf_reg,each_line);

    if(entry_info->def_valid && def_over == 0){
      definition_check(each_line, &entry_info->def_valid, 
		       &def_over, entry_info, all);
      if(def_over == 0)strcat(entry_info->definition, each_line);
    }

/* organism check */
    if(strncmp(each_line,"  ORGANISM",10) == 0){
      if(all->p.v)printf("%s",each_line);
      strcpy(entry_info->source, &each_line[11]);
      for(k = strlen(entry_info->source);k >= 0;k --)
	if(entry_info->source[k] == '\n'){
	  entry_info->source[k] = '\0'; break;
	}
      if(all->p.match_org[0] != '\0' && 
	 find_word(all->p.match_org,&each_line[12]) == 0){
	entry_info->def_valid = 0;
	if(all->p.v)printf("Invalid organism\n");
      }
    }

    cds_field_read(each_line,entry_info,all);
    exon_read(each_line,entry_info,all);
    intron_read(each_line,entry_info,all);

    if(strncmp("ORIGIN",each_line,6) == 0){
      /* 5'UTR splicing check */
      cds0 = &entry_info->each_cds[0];
      exon = &entry_info->exon;
      intron = &entry_info->intron;
      if(all->p.elsp && entry_info->ncds2 > 0 && cds0->cds_start > 0 &&
	 ((exon->num > 0 && exon->start[0] > 1 && 
	   exon->start[0] < cds0->cds_start) ||
	  (exon->num > 0 && exon->end[0] > 0 && 
	   exon->end[0] < cds0->cds_start) ||
	  (intron->num > 0 && intron->start[0] > 0 && 
	   intron->start[0] < cds0->cds_start) ||
	  (intron->num > 0 && intron->end[0] > 0 && 
	   intron->end[0] < cds0->cds_start))){
	if(all->p.v)printf("invalid by splicing in 5'UTR\n",
			   entry_info->entry_line);
	entry_info->def_valid = 0;
      }

      if(entry_info->def_valid == 0){
	entry_info->ncds = 0;
	entry_info->ncds2 = 0;
	if(all->p.v)printf("Invalid Definition\n");
      }
      else all->nent2_vd ++;
    
      if(all->p.single_cds && entry_info->ncds > 1){
	entry_info->ncds = 1;
	entry_info->ncds2 = 1;
      }

      if(all->p.v){
	printf("%d CDS readable in this entry(Simple version).\n",
		entry_info->ncds);

	printf("%d CDS readable in this entry.\n",entry_info->ncds2);
	
      }
      return VALID;
    }
  }
  return EXHA;
}

/* reads CDS field and sets parameters */
void cds_field_read(char *each_line,struct gparam *entry_info,
	       struct gloparam *all)
{

  static int cds_reading = 0;  /* 1 -> CDS record reading */
  static int cds_nec = 0;      /* 1 -> /codon_start=1 keyword found */
  static int cds_valid = 1;    /* 0 -> /partial , /pseudo keyword found */

  struct cds_info *each_cds;

  int i,j,k,read_start;

  if(strcmp(all->p.CDS_string, "CDS") != 0)cds_nec = 1; 
  /* If not search keyword is not CDS, keyword /codon_start is unnecesary */

  each_cds = &(entry_info->each_cds[entry_info->ncds2]); /* check!! */

/* CDS reading */
  if(cds_reading){ 
    if(all->p.v)printf("%s",each_line);
  /* CDS record end */
    if(isalpha(each_line[0]) != 0 || isalpha(each_line[5]) != 0){
      cds_reading = 0; 
      if(all->p.v){
	printf("CDS record end\n");
	printf("information on this CDS:%s\n",entry_info->entry_line);
	printf("cds_start :%d\n",each_cds->cds_start);
	printf("cds_end   :%d\n",each_cds->cds_end);
	printf("splice    :%d\n",each_cds->splice);
	printf("complement:%d\n",each_cds->complement);
	printf("Number of exons:%d\n",each_cds->njoin);
	for(i = 0;i < each_cds->njoin && i < JOINMAX;i ++)
	  printf("exon %d start:%d  end:%d\n",i,
		 each_cds->join[i],each_cds->join_end[i]);
	 
	printf("cds_nec:%d   cds_valid:%d\n",cds_nec,cds_valid);
      }
      if(each_cds->njoin >= JOINMAX && all->p.wa){
	printf("Warning in entry:%s",entry_info->entry_line);
	printf("Warning number of exon is %d\n",each_cds->njoin);
      }
      /* CDS validity check */
      if(cds_nec && cds_valid){
	entry_info->ncds2 ++; /* count CDS if valid */
	if(entry_info->ncds2 >= CDSMAX){
	  if(all->p.wa){
	    printf("Warning in entry %s",entry_info->entry_line);
	    printf("Number of CDS info %d\n",CDSMAX);
	  }
	  entry_info->ncds2 = CDSMAX - 1;
	}

	if(all->p.v)printf("accepted by CDS structure\n");

/* Checks whether this CDS is acceptable by ncds simple version */
	if(each_cds->cds_start > 0 && each_cds->complement == 0 && 
	   (all->p.jo || each_cds->splice == 0)){
	  for(i = 0;i < entry_info->ncds; i++)
	    if(each_cds->cds_start == entry_info->cds_start[i])break;
	  if(i == entry_info->ncds){
	    if(all->p.v)printf("accepted by ncds simple version\n");
	    entry_info->cds_start[entry_info->ncds] = each_cds->cds_start;
	    entry_info->ncds ++;
	  }
	  else if(all->p.v)printf("This start location already recorded\n");
	} /* record to cds_start,ncds */
	each_cds = &(entry_info->each_cds[entry_info->ncds2]);
      }
    }
  /* CDS still reading */
    else {
      join_read(each_line,entry_info,all);
      if(strncmp(&each_line[21],"/partial",8) == 0){
	if(all->p.v)printf("keyword /partial found in CDS\n");
	cds_valid = 0;
      }
      else if(strncmp(&each_line[21],"/pseudo",7) == 0){
	if(all->p.v)printf("keyword /pseudo found in CDS\n");
	cds_valid = 0;
      }
      else if(all->p.puta == 1 && 
	      cds_notefield_read(each_line,entry_info,all) == 1){
	if(all->p.v)printf("keyword /note=\"putative\" found in CDS\n");
	cds_valid = 0;
      }
      else if(strncmp(&each_line[21],"/codon_start=1",14) == 0){
	if(all->p.v)printf("keyword /codon_start=1 found in CDS\n");
	cds_nec = 1;
      }
      else if(strncmp(&each_line[21],"/product=",9) == 0){
	strcpy(each_cds->product, &each_line[31]);
	for(k = strlen(each_cds->product) - 1;k >= 0;k --)
	  if(each_cds->product[k] == '\n' || each_cds->product[k] == '"')
	    each_cds->product[k] = '\0';
      }
      else if(strncmp(&each_line[21],"/gene=",6) == 0){
	strcpy(each_cds->gene, &each_line[28]);
	for(k = strlen(each_cds->gene) - 1;k >= 0;k --)
	  if(each_cds->gene[k] == '\n' || each_cds->gene[k] == '"')
	    each_cds->gene[k] = '\0';
      }
    }
  }

  /* CDS record start */
  if(strncmp(&each_line[5],all->p.CDS_string,strlen(all->p.CDS_string)) == 0){
    if(all->p.v){
      printf("CDS found:\n");
      printf("%s",each_line);
    }
    cds_reading = 1;
    cds_valid = 1; cds_nec = 0;
   
    each_cds->product[0] = '\0';
    each_cds->gene[0] = '\0';
    join_read(each_line,entry_info,all);
  }
}

/* reads join field and set parameters
   if this join is valid plus entry_info->ncds2 in main routine */
void join_read(char *each_line,struct gparam *entry_info,
	  struct gloparam *all)
{
  static char join_buffer[JOINBUF];
  struct cds_info *each_cds;
  static int join_reading = 0;
  int join_read_start;

  each_cds = &(entry_info->each_cds[entry_info->ncds2]);

/* join reading */
  if(join_reading){

  /* join end */
    if(isalpha(each_line[0]) != 0 || isalpha(each_line[5] != 0))
      join_reading = 0;

  /* join end + join still reading */
    else if(join_cat(&each_line[21],join_buffer) == 1){
      join_reading = 0;
      if(all->p.v)printf("join_buffer:%s\n",join_buffer);
      join_get(join_buffer,each_cds->join,each_cds->join_end,
	       &(each_cds->njoin));
      each_cds->cds_start = each_cds->join[0];
      if(each_cds->njoin <= JOINMAX)
	each_cds->cds_end = each_cds->join_end[each_cds->njoin - 1];
      else each_cds->cds_end = 0;
    }
  }

/* join start */
  if(strncmp(&each_line[5],all->p.CDS_string,strlen(all->p.CDS_string)) == 0){
    each_cds->splice = 0;
    each_cds->complement = 0;
    each_cds->njoin = 0;
    join_read_start = 21;
    if(strncmp(&each_line[join_read_start],"complement",10) == 0){
      join_read_start = 32;
      each_cds->complement = 1;
    } /* keyword complement check */
    if(strncmp(&each_line[join_read_start],"join",4) == 0){
      if(all->p.v)printf("join found\n");
      each_cds->splice = 1;
      join_reading = 1;
      join_buffer[0] = '\0';
      if(join_cat(&each_line[join_read_start + 5],join_buffer) == 1){
	/* put into join buffer */
	join_reading = 0;
	join_get(join_buffer,each_cds->join,each_cds->join_end,
		 &(each_cds->njoin)); /* get information for join */
	each_cds->cds_start = each_cds->join[0];
	if(each_cds->njoin < JOINMAX)
	  each_cds->cds_end = each_cds->join_end[each_cds->njoin - 1];
	else each_cds->cds_end = 0;
	if(all->p.v)printf("join buffer:%s\n",join_buffer);
      }
      else {
	if(all->p.v)printf("join line more than 1\n");
      }
    }
    else {
      locbegend(&each_line[join_read_start],
		&(each_cds->cds_start),&(each_cds->cds_end));
    }
  }
}


/* returns 1 if join information ends
   format of join_line must be nnn..nnn,nnn..nnn, may be stopped by ')' 
   join_buf must be stopped with '\0'
*/
int join_cat(char *join_line,char *join_buf)
{
   int i,p;
   i = 0;
   p = strlen(join_buf);
   
   if(p > JOINBUF){
     strcpy(join_buf,"0..0\0");
     return 1;
   }

   while(join_line[i] != '\0' && join_line[i] != '\n'){
      if(join_line[i] != ' ' && join_line[i] != '\t' && p <= JOINBUF){
	 if(join_line[i] == ')'){
	    join_buf[p] = '\0';
	    return 1;
         }
         else {
	    join_buf[p] = join_line[i];
	    p ++; 
         }
      }
      i ++;
   }
   join_buf[p] = '\0';
   return 0;
}


/* str must be nnn..nnn,nnn..nnn,nnn..nnn\0 format */
void join_get(char *str,int begin[],int end[], int *njoin)
{
   int i,j;

   locbegend(str,begin,end);
   *njoin = 1;

   i = 0;
   while(str[i] != '\0' && i < strlen(str)){
      i ++;
      if(str[i] == ','){
	 i += 1;
	 if(*njoin < JOINMAX)
	   locbegend(&str[i],&begin[*njoin],&end[*njoin]);
	 *njoin += 1;
      }
   }
}
/* str must be nnnn..nnnn format */
/* 0 will be put for unknown */
void locbegend(char *str, int *begin, int *end)
{
   int i,j;   

   *begin = atoi(str);
   for(i = 0;i < strlen(str) - 1;i ++){
      if(strncmp(&str[i],"..",2) == 0){
	 i+= 2; break;
      } 
   }
   if(i == strlen(str) - 1){
      *end = 0;
   }
   else {
      *end = atoi(&str[i]);
   }
}


/* reads information about intron segments */
void intron_read(char *each_line,struct gparam *entry_info,
	    struct gloparam *all)
{
  int start, end, num;
  if(entry_info->intron.num >= JOINMAX){
    if(all->p.wa)printf("Warning:Number of introns over threshold\n");
    return;
  }

  if(strncmp("intron",&each_line[5],6) == 0){
    locbegend(&each_line[21], &start, &end);
    num = entry_info->intron.num;
    entry_info->intron.start[num] = start;
    entry_info->intron.end[num] = end;
    entry_info->intron.num ++;
  }
}

/* reads information about exon segments */
void exon_read(char *each_line, struct gparam *entry_info,
	  struct gloparam *all)
{
  int start, end, num;
  if(entry_info->exon.num >= JOINMAX){
    if(all->p.wa)printf("Warning:Number of exons over threshold\n");
    return;
  }

  if(strncmp("exon",&each_line[5],4) == 0){
    locbegend(&each_line[21], &start, &end);
    num = entry_info->exon.num;
    entry_info->exon.start[num] = start;
    entry_info->exon.end[num] = end;
    entry_info->exon.num ++;
  }
}

/* returns 1 if keyword "putative" is found in CDS note field */
int cds_notefield_read(char *each_line, struct gparam *entry_info,
		       struct gloparam *all)
{
  static int note_reading = 0; /* 1 = CDS note field reading */

  int i,j,k, read_start;

/* note field reading */
  if(note_reading){
    /* note field end */
    if(isalpha(each_line[0]) != 0 /* ex."ORIGIN" */ || 
       isalpha(each_line[5] != 0) || each_line[21] == '/'){
      note_reading = 0;
      if(all->p.v)printf("CDS note field end\n");
    }
    /* note field still reading */
    else { 
      if(find_word("putative",each_line) != 0)return 1;
    }
  }

/* note field start */
/* Don't write "else" before "if" statement !!*/
  if(strncmp(&each_line[21], "/note=", 6) == 0){
    note_reading  = 1;
    if(all->p.v)printf("CDS note field start\n");
    if(find_word("putative",each_line) != 0)return 1;
  }

  return 0;

}



/* def_valid = 0 ... invalid  1 ... valid so far */
/* def_over = 1 ... DEFINITION area is all read */
void definition_check(char *each_line, int *def_valid, int *def_over, 
		 struct gparam *entry_info, struct gloparam *all)
{
/*
      if(all->p.v)printf("DEFINITION check!!\n"); 
*/

  if(find_word("exon",each_line) || find_word("partial",each_line)
     || find_word("pseudo",each_line) 
     || ( all->p.pur && find_word("mitochond",each_line))
     || ( all->p.pur && find_word("immuno",each_line))
     || ( all->p.pur && find_word("variabl",each_line))
     || ( all->p.pur && find_word("receptor",each_line))
     ){
    *def_valid = 0;*def_over = 1;
    if(all->p.v)printf("definition invalid:%s\n%s",entry_info,each_line);
  }

  else if(all->p.match_def[0] != '\0' && *def_valid != 0 && 
	  find_word(all->p.match_def, each_line))
    *def_valid = 2;

  else if(strncmp("DEFINITION",each_line,10) != 0 && each_line[0] != ' '){
    *def_over = 1;
    if(all->p.match_def[0] != '\0' && *def_valid == 1)*def_valid = 0;
  }

}


/* set parameters by reading CDS information
   indicated by each_line (join(....)) 
   start position or -1 will be returned */
int cdsread(struct gparam *entry_info,
	    struct gloparam *all, char *each_line)
{
   int i,ct,cdss = -1;
   char *join_info;
   join_info = nthtext2(each_line,1);
   if(join_info != NULL){
     if(all->p.jo)cdss = cdsstart_jo(join_info);
     else cdss = cdsstart(join_info);
     if(all->p.v){
       printf("%s",join_info);
       printf("start:%d\n",cdss);
     }
     if(cdss != -1){
       ct = entry_info->ncds;
       for(i = 0;i < ct;i ++){
	 if(entry_info->cds_start[i] == cdss)break;
       }
       if(i < ct){
	 if(all->p.v)printf("This CDS is already recognized\n");
	 cdss = -1;
       }
       else {
         entry_info->cds_start[ct] = cdss;
         if(all->p.v){
	   printf("checking CDS %dth",entry_info->ncds + 1); 
	   printf(" in this entry\n");
         }
       }
     } 
   } 
   return cdss;
}

int spacect(char *line)
{
   int n = 0;

   while(line[n] == ' ')n ++;

   return n;
}

char *seq_read(FILE *fp,struct gparam *entry_info,
	       struct gloparam *all, int *maxn)
{
  char *buffer;
  int total;
  nseqread(&buffer,&total,fp);
  if(all->p.v)printf("total bytes:%d\n",total);
  *maxn = total;
  return buffer;
}

void procing(struct gparam *entry_info,struct gloparam *all,
	char *seqn, int max)
{
  int n,s;

  if(all->p.ufatg && entry_info->ncds > 0)
    for(s = 0;s < entry_info->ncds;s ++)
      if(entry_info->cds_start[s] > 0){
	for(n = entry_info->cds_start[s] - 3;n > 0; n -= 3){
	  if(strncmp(&seqn[n - 1],all->p.match_pat,
		     strlen(all->p.match_pat)) == 0){
	    if(all->p.v)printf("Invalid by in-frame upstream ATG\n");
	    return;
	  }
	}
      }


  if(all->p.frd || all->p.dstd || all->p.upc > 0 || all->p.dnc > 0){
    atg_dist(entry_info,all,seqn,max);
    atg_dist_d(entry_info,all,seqn,max);
  }
  if(all->p.frd)atg_rat(entry_info,all,seqn,max);
  if(all->p.lfd)atg_locus_dist(entry_info,all,seqn,max);
  if(all->p.lfdd)atg_locus_dist_d(entry_info,all,seqn,max);

  if(all->p.v)printf("Going to entry processing subroutines.\n");

  for(n = 0;n < all->nf_reg;n ++){
    if(fc_flag[n] == 1){
      if(entry[n] != NULL)
	(*entry[n])(entry_info->entry_line,seqn,max,
		    entry_info->cds_start,entry_info->ncds);
      if(entry2[n] != NULL)
	(*entry2[n])(entry_info->entry_line,seqn,max,
		     entry_info->each_cds, entry_info->ncds2);
      if(entry3[n] != NULL)
	(*entry3[n])(entry_info,seqn,max,entry_info->each_cds,
		     entry_info->ncds2);
    }
  }
}

/* calculates number of match pattern found in each locus
   from start codon */
void atg_locus_dist(struct gparam *entry_info,
	       struct gloparam *all, char *seqn, int max)
{
  int i,j,k,m,n,distance;
  int start;
  char *match_pat;
  match_pat = all->p.match_pat;
  
  for(i = 0;i < entry_info->ncds;i ++){ /* loop by number of CDS 
					 in each entry */
    start = entry_info->cds_start[i];
    if(lpatm(match_pat,&seqn[start - 1])){
/*  if(start >= strlen(match_pat) + all->p.ndstmax2){  change formula !!! */
      all->nent5 ++;
      for(n = start, distance = 0;
	  n > 0 && start - n < all->p.ndstmax2;
	  n --,distance --){
	all->dist_count[-1 * distance] ++;
	if(lpatm(match_pat,&seqn[n - 1])){
	  all->dist_match[-1 * distance] ++;
	  if(all->p.v){
	    printf("%s found %d (upstream) of locus %d\n",
		   match_pat, distance, start);
	  }
	}
      }
          
/*  } */
    }
  }
}

/* calculates number of match pattern found in each locus 
   downstream from start codon */
void atg_locus_dist_d(struct gparam *entry_info,
		 struct gloparam *all, char *seqn, int max)
{
  int i,j,k,m,n,distance;
  int start;
  char *match_pat;
  match_pat = all->p.match_pat;

  for(i = 0;i < entry_info->ncds;i ++){ /* loop by number of CDS
					   in each entry */
     start = entry_info->cds_start[i];
     if(lpatm(match_pat,&seqn[start - 1])){
       all->nent5_d ++; 
       for(n = start, distance = 0;
	   n + strlen(match_pat) - 1 <= max && n - start < all->p.ndstmax2;
	   n ++, distance ++){
	 all->dist_count_d[distance] ++;
         if(lpatm(match_pat,&seqn[n - 1])){
	   all->dist_match_d[distance] ++;
	   if(all->p.v){
	     printf("%s found %d (downstream) of locus %d\n",
		    match_pat,distance,start);
           }
         }
       }
     }
  }
}


/* counts all the atg included in the sequence 
   result will be recorded in the structure variable */
void atg_rat(struct gparam *entry_info,struct gloparam *all,
	char *seqn, int max)
{
  int n,i;
  char *match_pat;
  match_pat = all->p.match_pat;

  for(n = 0;n < max - strlen(match_pat);n ++){
    if(lpatm(match_pat,&seqn[n])){
      all->total_atg ++;
      n += strlen(match_pat) - 1; /* -1 is for n ++ */
    }
  }
  all->total_base += max;
}


/* calculates atg distance */
void atg_dist(struct gparam *entry_info, struct gloparam *all,
	 char *seqn, int max)
{
   
  int i,j,k,m,n,distance;
  int start;
  char *match_pat;
  match_pat = all->p.match_pat;

  for(i = 0;i < entry_info->ncds;i ++){
    all->total_valid_cds ++;
    start = entry_info->cds_start[i];
    if(lpatm(match_pat,&seqn[start - 1])){
      all->total_atg_start ++;
      distance = -3;
      for(n = start - strlen(match_pat);n > 0; n --){
	if(lpatm(match_pat,&seqn[n - 1]))break;
	else distance --;
      }
      if(n <= 0){
	if(all->p.v)printf("Pattern %s not found.\n",match_pat);
	all->nent4 ++;
      }
      else {
	if(all->p.v)printf("distance:%d\n",distance);
	if(distance <= -1000 && all->p.wa){
	  printf("warning in entry %s",entry_info->entry_line);
	  printf("distance is %d from CDS %d\n",distance,start);
	}
	if(distance >= all->p.upc*-1){
	  printf("very close %s found :%s",match_pat,entry_info->entry_line);
	  printf("very close %s found :CDS start %d, distance %d\n",
		 match_pat,start,distance);
	  printf(">%d\n",all->nent3);
/*
        for(m = 12;
	    entry_info->entry_line[m] != ' ' &&
	    entry_info->entry_line[m] != '\t';m ++)
	  putchar(entry_info->entry_line[m]);
	printf("(%d_%d)\n",start,distance*-1);
*/
	  m = start - all->p.upc - all->p.upd; /* display start */
	  k = start + strlen(match_pat) + all->p.dnd; /* display stop */
	  for(;m < k;m ++){
	    if(m == start)putchar(' ');
	    if(m == start + strlen(match_pat))putchar(' ');
	    if(m >= 1 && m <= max)putchar(seqn[m - 1]);
	    else putchar(' ');
	  }
	  printf("\n\n");
	}
	all->nent3 ++;
	all->total_dist += distance;
	if(-1 * distance < NDSTMAX - 1)
	  all->dist_dist[-1 * distance] ++;
	else {
	  all->dist_dist[NDSTMAX - 1] ++;
	  if(all->p.v){
	    printf("far :%s",entry_info->entry_line);
	    printf("far distance:%d\n",distance);
	  }
	}
	if(all->p.v){
	  for(k = n;k < start + strlen(match_pat);k ++){
	    putchar(seqn[k-1]);
	  }
	  putchar('\n');
	  printf("total %s distance calculatable:%d\n",match_pat,all->nent3);
	  printf("total distance:%d\n",all->total_dist);
	}
      }
    }
    else {
      if(all->p.wa || all->p.v){
	printf("in %s",entry_info);
	printf("CDS %d:",start);
	for(n = 0;n < 3;n ++)putchar(seqn[start - 1 + n]);
	putchar('\n');
      }
    }
  }
}


/* calculates atg distance downstream */
void atg_dist_d(struct gparam *entry_info,struct gloparam *all,
	   char *seqn, int max)
{
   int i,j,k,m,n,distance;
   int start;
   char *match_pat;
   match_pat = all->p.match_pat;

   for(i = 0;i < entry_info->ncds;i ++){
     start = entry_info->cds_start[i]; /* remark:way of recording CDS depends
					  on header read */
     if(lpatm(match_pat,&seqn[start - 1])){
       distance = 3;
       for(n = start + strlen(match_pat);n <= max;n ++){
	 if(lpatm(match_pat,&seqn[n - 1]))break;
	 else distance ++;
       }
       if(n > max){
	 if(all->p.v)printf("Pattern %s not found downstream\n",match_pat);
	 all->nent4_d ++;
       }
       else {
	 if(all->p.v)printf("distance:%d\n",distance);
	 if(distance >= 1000 && all->p.wa){
	   printf("warning in entry %s",entry_info->entry_line);
	   printf("distance is %d downstream from CDS %d\n",
		  distance,start);
	 }
	 if(distance <= all->p.dnc){
	   printf("very close atg found ds:%s",entry_info->entry_line);
	   printf("very close atg found ds:CDS start %d, distance %d\n",
		  start,distance);
	   m = start - all->p.upd; /* display start */
	   k = start + strlen(match_pat) + all->p.dnc + all->p.dnd; 
	   /* display stop */

	   for(;m < k;m ++){
	     if(m == start)putchar(' ');
	     if(m == start + strlen(match_pat))putchar(' ');
	     if(m >= 1 && m <= max)putchar(seqn[m - 1]);
	     else putchar(' ');
	   }
	   putchar('\n');
	 }
	 all->nent3_d ++;
	 all->total_dist_d += distance;
	 if(distance < NDSTMAX - 1)
	   all->dist_dist_d[distance] ++;
	 else {
	   all->dist_dist_d[NDSTMAX - 1] ++;
	   if(all->p.v){
	     printf("far :%s",entry_info->entry_line);
	     printf("far distance:%d\n",distance);
	   }
	 }
	 if(all->p.v){
	   for(k = start;k < n + strlen(match_pat);k ++){
	     putchar(seqn[k - 1]);
	   }
	   putchar('\n');
	   printf("total atg distance calculatable downstream:%d\n",
		  all->nent3_d);
	   printf("total distance downstream:%d\n",all->total_dist_d);
	 }
       }
     }
     else {
       if(all->p.wa || all->p.v){
	 printf("in %s",entry_info);
	 printf("CDS %d:",start);
	 for(n = 0;n < 3;n ++)putchar(seqn[start - 1 + n]);
	 putchar('\n');
       }
     }
   }
 }




/* calculates start locus from CDS information 
   such as "join(1234,...) -> 1234 */
int cdsstart(char *line)
{
  int i,n;
  int cdss;

  cdss = 0;
  for(i = 0;i < strlen(line);i ++){
    if(line[i] >= '0' && line[i] <= '9'){
      cdss *= 10;
      cdss += line[i] - '0';
    }
    else if(line[i] == '<' || line[i] == '>' /* || line[i] == 'c' */ 
	    || (line[i] >= 'A' && line[i] <= 'Z'))
      return -1;
    else if(line[i] == '.' || line[i] == ',' || 
	    line[i] == ')' || line[i] == ' ')return cdss;
  }
  return -1;

}


/* accept only keyword "join" for CDS */
int cdsstart_jo(char *line)
{
  int i,n;
  int cdss;
  char *join_match = "join(";

  i = 0; cdss = 0;
  if(lpatm(join_match,line))i += strlen(join_match);

  for(  ;i < strlen(line); i ++){
    if(line[i] >= '0' && line[i] <= '9'){
      cdss *= 10;
      cdss += line[i] - '0';
    }
    else if(line[i] == '.' || line[i] == ',' || 
	    line[i] == ')' || line[i] == ' ')return cdss;
    else return -1;
  }
  return 0; /* This will not be executed */
}



void db_analyze(FILE *fp,struct gparam *entry_info,struct gloparam *all)
{
  int status,maxn,i,j,k;
  char *seqn;
  int pcds[CDSMAX],pncds;

/* If the entry is invalid, entry_info[0].def_valid is set to 0 */ 

  if(all->p.v)printf(" *** PROCESSING DESCRIPTIONS *** \n");
  while(1){
    status = find_entry(fp,&entry_info[0],all); /* find entry */
    if(status == VALID){ /* find valid entry */
      if(all->p.v)
	printf("\n>>valid ENTRY %d:\n%s",all->nent,entry_info[0].entry_line);
      all->nent2 ++;

      for(i = 0;i < CDSMAX;i ++)valid_cds[i] = 1;

      if(all->p.v)printf(">>header read\n");
      if(header_read(fp,&entry_info[0],all) == EXHA)return; /* header read */

      if(all->p.v)
	printf("There are %d(Simple version %d) CDS in %s\n",
	       entry_info[0].ncds2,
	       entry_info[0].ncds,entry_info[0].entry_line);


      seqn = seq_read(fp,&entry_info[0],all,&maxn); /* sequence read */

      if(all->p.wa)
	cdsse_check(entry_info,maxn); /* 0 <= cds start < maxn check */

      if(entry_info[0].def_valid){
	if(all->p.v)printf(">>sequence processing\n");
	procing(&entry_info[0],all,seqn,maxn); /* processing for each ent. */
      }

/* translation start prediction */
      if(all->p.pred_mode && pred_func != NULL && entry_info[0].ncds &&
	 entry_info[0].def_valid)
	trl_pred(seqn,maxn,entry_info,all);

      free(seqn);
    }
    else if(status == EXHA)return;
    else if(status == INVALID)continue;
  }
}

void trl_pred(char seqn[], int maxn,struct gparam *entry_info,
	 struct gloparam *all)
{
    int pcds[CDSMAX],pncds;
    int i;

    (*pred_func)(seqn,maxn,pcds, &pncds);

    if(pncds >= 1 && entry_info->ncds >= 1 && 
       entry_info->cds_start[0] == pcds[0])all->exactly ++;
    else {
      all->wrong ++;
      printf("in entry:%s",entry_info->entry_line);
      printf("There are %d translation initiation sites:",entry_info->ncds);
      for(i = 0;i < entry_info->ncds;i ++)
	printf("%d ",entry_info->cds_start[i]);
      putchar('\n');
      printf("predicted %d translation initiation sites:",pncds);
      for(i = 0;i < pncds;i ++)
	printf("%d ",pcds[i]);
      putchar('\n');putchar('\n');
    }
}

int varcmp(int cds[],int cds2[],int ncds){
  int i;
  for(i = 0;i < ncds;i ++)
    if(cds[i] != cds2[i])break;
  if(i == ncds)return 1;
  else return 0;
}

/* if any one of cds in entry_info is over maxn, 0 will be put */
void cdsse_check(struct gparam *entry_info,int maxn)
{
  int k;
 
  for(k = 0;k < entry_info->ncds2;k ++)
    if(entry_info->each_cds[k].cds_start > maxn 
       || entry_info->each_cds[k].cds_start < 0){
      printf("Warning in entry %s",entry_info->entry_line);
      printf("Number of base is %d\n",maxn);
      printf("reffered %d\n",entry_info->each_cds[k].cds_start);
/*      entry_info->each_cds[k].cds_start = 0; */
    }
}


/* not all of final result is controlled by variable frd */
void db_final_result(FILE *fp,struct gparam *entry_info,
		struct gloparam *all)
{
  int n,s;
  double u,u1;
  if(all->p.frd){
    printf("\n * Result of standart analysis *\n");
    printf("Total entries           :%d\n",all->nent);
    printf("LOCUS match entries     :%d\n",all->nent2);
    printf("Valid entries           :%d\n",all->nent2_vd);
    printf("Number of initiation sites which has ATG trinucleotides upstream\n");
    printf("within the specific distance:%d\n",all->nent3);
    printf("Number of initiation sites which has no ATG trinucleotides upstream\n");
    printf("within the specific distance:%d\n",all->nent4);
    if(all->p.lfd)printf("number of count valid CDS  :%d\n",all->nent5);
    printf("Average distance from start codon to the first ATG upstream:%lf\n",1.0 * all->total_dist / all->nent3);
    
    printf("Number of initiation sites which has ATG trinucleotides downstream\n");
    printf("within the specific distance    :%d\n",all->nent3_d);
    printf("Number of initiation sites which has no ATG tirinucleotides downstream\n");
    printf("within the specific distance:%d\n",all->nent4_d);
    if(all->p.lfdd)printf("number of count valid CDS downstream  :%d\n",
			   all->nent5_d);
    printf("Average distance from start codon to the first ATG downstream:%lf\n",1.0 * all->total_dist_d / all->nent3_d);
    printf("valid translation inititation sites:%d\n",all->total_valid_cds);
    printf("number of translation initiation sites start with %s:%d\n",all->p.match_pat,
	   all->total_atg_start);
    printf("rate of translation initiation sites start with %s:%.2lf%%\n",all->p.match_pat,
	   100.0*all->total_atg_start/all->total_valid_cds);
    printf("total bases:%d\n",all->total_base);
    printf("number of total pattern of %s :%d\n",all->p.match_pat,all->total_atg);
    printf("rate of %s in sequences:%lf (%lf)\n",all->p.match_pat,
	   1.0*all->total_atg/all->total_base, 
	   1.0*all->total_base/all->total_atg);
  }


  if(all->p.dstd){
    printf("distance distributions\n");
    for(n = 0;n < NDSTMAX - 1;n ++)
      printf("%d %d\n",n,all->dist_dist[n]);
    printf("%d %d\n",NDSTMAX-1,all->dist_dist[NDSTMAX-1]); /* over */
  } /* prints distance distribution */
 
  if(all->p.lfd){
    printf("pattern %s distributions:\n",all->p.match_pat);
    dist_freq_print(all->dist_match,all->dist_count,all->p.ndstmax2,
		    all->p.sms,1,all->p.s2);
  }

  if(all->p.lfdd){
    printf("pattern %s distributions(downstream):\n",all->p.match_pat);
    dist_freq_print(all->dist_match_d,all->dist_count_d,all->p.ndstmax2,
		    all->p.sms,0,all->p.s2);
  }

  if(all->p.pred_mode){
    printf("Prediction right:%d\n",all->exactly);
    printf("Prediction wrong:%d\n",all->wrong);
    printf("ratio:%.2lf%%\n",100.0*all->exactly/(all->exactly + all->wrong));
  }

/*
  for(n = 0;n < all->p.ndstmax2;n ++)
    printf("%d %d\n",n,all->dist_count_d[n]);
*/

  final_f(all->nf_reg); /* output result by function registered */
}


/* prints frequency of locus */
void dist_freq_print(int match[],int count[],int max,int sms,
		int flag1,int flag2)
/* 1 = minus reverse order (usually used for upstream) */
/* 1 = center smooth mode */
{
  int n,s,u,u1;

  if(flag2 == 0 && flag1 == 0){
    for(n = 0;n + sms - 1 < max;n += sms){
      for(s = n,u = 0,u1 = 0;s < n + sms;s ++){
	u += match[s];
	u1+= count[s];
      }
      if(u1 != 0)printf("%d %lf\n",n,1.0*u/u1);
    }
  }
  else if(flag2 == 0 && flag1 == 1){
    for(n = max - (max % sms);
	n >= sms;
	n -= sms){
      for(s = n - sms,u = 0,u1 = 0;s < n; s++){
	u += match[s];
	u1+= count[s];
      }
      if(u1 != 0)printf("%d %lf\n", -1*(n-sms), 1.0*u/u1);
    }
  }
  else if(flag2 == 1 && flag1 == 0){
    for(n = 0;n + sms - 1 < max;n ++){
      for(s = n,u = 0,u1 = 0;s < n + sms;s ++){
	u += match[s];
	u1+= count[s];
      }
      if(u1 != 0)printf("%d %lf\n",n,1.0*u/u1);
    }
  }
  else if(flag2 == 1 && flag1 == 1){
    for(n = max;
	n >= sms;
	n --){
      for(s = n - sms,u = 0,u1 = 0;s < n; s++){
	u += match[s];
	u1+= count[s];
      }
      if(u1 != 0)printf("%d %lf\n", -1*(n-sms), 1.0*u/u1);
    }
  }



}



void gloparam_init(struct gloparam *all)
{
  int n;
  all->nent = 0; /* number of all the entries */
  all->nent2 = 0; /* number of valid entries */
  all->nent2_vd = 0; /* number of valid entries including definition */
  all->nent3 = 0; /* number of close atg found */
  all->nent3_d = 0; /* number of close atg found downstream */
  all->nent4 = 0; /* number of close atg not found */
  all->nent4_d = 0; /* number of close atg not found downstream */
  all->nent5 = 0; /* number of valid CDS 
		     (long enough from data start) found */
  all->nent5_d = 0;
  all->total_valid_cds = 0;
  all->total_atg_start = 0;
  all->total_atg = 0;
  all->total_base = 0;
  all->total_dist = 0; /* total distance of atg */
  all->total_dist_d = 0; /* total distance of atg downstream */
  for(n = 0;n < NDSTMAX;n ++)all->dist_dist[n] = 0;
  for(n = 0;n < NDSTMAX2;n ++)all->dist_match[n] = 0;
  for(n = 0;n < NDSTMAX2;n ++)all->dist_count[n] = 0;

  for(n = 0;n < NDSTMAX2;n ++)all->dist_dist_d[n] = 0; 
  for(n = 0;n < NDSTMAX2;n ++)all->dist_match_d[n] = 0;
  for(n = 0;n < NDSTMAX2;n ++)all->dist_count_d[n] = 0;  
  
  all->exactly = 0;
  all->wrong = 0;

  for(n = 0;n < CDSMAX;n ++)valid_cds[n] = 1;

} 
    

void testfunc2(){
  char str1[20];
  char line[50];

  printf("Input 2 words.\n");
  scanf("%s",str1);
  scanf("%s",line);

  printf("%d\n",find_word(str1,line));

}

void testfunc(){
  int i,j;
  char line[20];

  printf("Input nuc.acid:");
  scanf("%s",line);
  for(i = 0;i < strlen(line);i ++)
    putchar(cmpl(line[i]));
}



main(int argc,char *argv[])
{
  FILE *fp;
  int i,j;

  all.nf_reg = func_init(); /* register functions */
  comline_manage(argc,argv,&(all.p)); /* initializes some parameter
					 by looking at command lines */

  if(all.p.test_mode){
    testfunc();
    exit(0);
  }

/* genbank analysis */
/* warning !! from the second iteration step, global variable
   will not be initialized!! */
/* notice: gloparam_init or comline_manage may reset all.p.filenum */
  if(all.p.filenum > 0){
    all.p.iteration = 1; /* This value is increased in functions */

    for(i = 0;i < all.p.iteration;i ++){ /* iteration of processing */
      gloparam_init(&all);
      if(i >= 1)comline_manage(argc, argv, &(all.p)); 
	/* initialize some parameters especially in parameter functions */
      for(j = 0;j < all.p.filenum;j ++){ /* Do calculation for all files */
	if((fp = fopen(all.p.filename[j],"r")) == NULL){
	  printf("File \"%s\" does not exist.\n",all.p.filename[j]);
	  exit(1);
	}
	db_analyze(fp, &entry_info, &all);
	fclose(fp);
      }
      db_final_result(fp, &entry_info, &all);
    }
  }
  else {
    fp = stdin;
    gloparam_init(&all);
    db_analyze(fp, &entry_info, &all);
    db_final_result(fp, &entry_info, &all);
  }

}




