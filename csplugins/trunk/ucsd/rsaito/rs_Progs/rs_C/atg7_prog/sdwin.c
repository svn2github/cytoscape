/* ------------------------------
 *  about translation initiation 
 * ------------------------------
 *  considering free-energy with alignment 
 *
 *  when you change "WINLEN", and then errors take place, 
 *  check function "setbit" &  "testbit"
 * 
 *  "Looks actual matching !!" part is removed. 
 *  see sdwin_act.c for the original one
 *  printf(...) for debugging is removed too. see sdwin_act.c 
 *
 *  This proglum needs much long run time. So try this on "bio" host
 *
 */

/* have error !!!!
 * in command line, need "/" before filename.seq
 *
 *        ex.  ./test.seq
 */



#include <stdio.h>
#include <ctype.h>
#include <string.h>
#include <stdlib.h>

#include "global_st.h"  /* structures are defined */
#include "atg_func.h"   /* mathematical functions used are declared */


/* max value for memory alocation */
#define WINLEN 12  /* length of window */
#define UPTO 25    /* x base upstream from which window shift */
#define DNTO 0     /* y base downstream to which ---   */
#define GENE 5000  /* this is just set to be larger than 
		      the number of E.coli's all genes      */
// #define SAMPLE 100   // if you like sampling, number defined here is done


struct obj_info{
  char fileName[20];    /*  from command line */
  char orgName[100];     /*  from genbank file */
  char rRNA_seq[100];   /*  from sdfile -> DNA string data */
  int lenF;             /* length of fileName[] */
  int lenS;             /*           rRNA_seq[] */
  int CDSnum;
}ObjInfo;

struct shift{
  double energy;
  int gene;
  int loc;
};

struct locational_energy{
  double loc_energy[UPTO+DNTO+1];
  int times;
  double average;
}locEnergySet;



static char *compseqn;    /* normal -> seqn, complement -> compseqn */
static char paramSD[128];

static char rRNAseq[WINLEN+1];  /* ? is this identical to ObjInfo.rRNA... */
static struct shift score[GENE][UPTO+DNTO+WINLEN];    /* see struct shift */
static int parFlg;


/* functions */






void loc_energy_cul(int times, int ncds){
  
  int i,j;
  int total=0;
  
/* printf("locEnergySet.times: %d\n",locEnergySet.times);  */

  for( j = 0; j < times; j++ ){
    locEnergySet.loc_energy[j] = 0;
    for( i = 0; i < ncds; i++ ){
      locEnergySet.loc_energy[j] += score[i][j].energy;
    }
  }
  
  for( i = 0; i < times; i++ ){
    total += (int)locEnergySet.loc_energy[i];
  }
  locEnergySet.average = total/times;

}

void sdget(char *seqfile, char *sdfile, char sdseq[]){

  int i,j,k;
  FILE *fp;
  static char line[2000], rseq[30], seqf[30];
  char tmp_c;

// printf("\n");
// printf("called: sdget\n");
//printf("\n");

  /* Here, rseq will be file name without full path. */
  for(i = strlen(seqfile) - 1, j = 0; i >= 0 && j < 30; i --, j ++){
    if(seqfile[i] == '/'){
      i = i + 1;
      break;
    }
    rseq[j] = seqfile[i];
  }
  rseq[j] = '\0';
  for(i = 0, j = strlen(rseq) - 1; i < j;i ++, j --){
    tmp_c = rseq[i];
    rseq[i] = rseq[j];
    rseq[j] = tmp_c;
  }
/*  printf("rseq : %s\n", rseq); */


  /* error */
  if((fp = fopen(sdfile, "r")) == 0){
    fprintf(stderr, "sdfile could not open.\n");
    exit(1);
  }

  /* search the line which have the same name as file name (---.seq) */
  while(1){    

    /* error */
    if(fgets(line, 2000, fp) == NULL){  /* at the end of the file */
      fprintf(stderr, "there's no match with %s in sdfile\n", rseq);
      exit(1);
    }

    /* Here, seqf will be file name written in sdfile */
    for( i = 0;isspace(line[i]) != 0;i ++);
    for(  j = 0;isspace(line[i]) == 0;i ++, j ++)   seqf[j] = line[i]; 

    seqf[j] = '\0'; 

    /* find the line whose name is same as the file name */
    if(strcmp(rseq, seqf) == 0){    


      for(   ;isspace(line[i]) != 0;i ++);
      for(   j = 0;isspace(line[i]) == 0 && line[i] != '\0';i ++, j ++)
        sdseq[j] = line[i];
      sdseq[j] = '\0';
      break;
    }

  }/* while */



  fclose(fp);
}


void energy_cul(int times, int ncds, struct cds_info cds[],    
		  char seqn[], int max)  
{
  int i, j, k, l, p, q;  
  int loopTime;
  static int counter = 0, temp_j[100];
  double mini = 100.0;

  static char pat1[100], pat2[100];     /* parameter for sd_match function */
  static int temp_res1[UPTO+DNTO+1][WINLEN];

  /* waite for answer from sd_match */
  static char result1[200], result2[200];    
  static int result_len, match_res1[100], match_res2[100];

// printf("\n");
// printf("called: energy_cul()\n"); 
// printf("\n");

  /* make complemente sequence of 16SrRNA -> 
     to analyse match with SD sequence */
  for( k = 0; k < WINLEN; k++ ){
    pat1[k] = cmpl(rRNAseq[k]);
  }
  pat1[k] = '\0';


  /* every instances are assigned for 'sd_match' function.
     and 'sd_match' is invoked. in this for loop.    */

  if(parFlg == 0){ //normalmode
    loopTime = ncds;
  }
  else if(parFlg == 1){
    /*    ObjInfo.loopTime = SAMPLE; 
     */
    loopTime = ncds;
  }
  else{printf("error: parFlg in energy_cul\n");}

  for( i = 0; i < loopTime; i++ ){
    for( j = 0; j < times; j++ ){

      /* assigning parameters */
      for( k = 0; k < WINLEN; k++ ){
	if( cds[i].complement == 0 )      /* normal */
	  pat2[k] = seqn[ cds[i].cds_start -1 - UPTO + j + k ];

	else if( cds[i].complement == 1 ) /* complement */
	  pat2[k] = compseqn[ max - cds[i].cds_end  - UPTO +j + k ];

	else{                             /* error */
	  fprintf(stderr,"cds[i].complement don't have neather 1 or 0\n");
	  exit(1);
	}
      }/* k */
      pat2[k] = '\0';

      /* sd_match is invoked
       by sd_match, free energy between rRNAseq and upper stream 
       is culcualted. Fill each window location's, each gene's 
       free energy.  */
/*
      score[i][j].energy = sd_match(pat1, pat2, result1, result2, // call
				    &result_len, match_res1, match_res2);

*/
      score[i][j].energy = sd_match_opt(pat1, pat2, result1, result2, // call
					&result_len, match_res1, match_res2);


      score[i][j].gene = i;
      score[i][j].loc = j;

/*      printf("sd_match completed score[%d][%d].energy\n", i, j);   */

      for( k = 0;  k< WINLEN; k++ ){
	temp_res1[j][k] = match_res1[k];
      } 
    }/* j */
    score[i][j].gene = -9999;
    score[i][j].loc = -9999;


    /*  find the location where the free energy is the lowest 
     about each gene */

    counter = 0;
    mini = 100.0;
    for( j = 0; j < times; j++ ){

      if( (int)(100 * score[i][j].energy) < (int)(100 * mini) ){
	mini = score[i][j].energy;
	for( l = 0; l < counter; l++ ) temp_j[l] = 0; /* initialization */

	temp_j[0] = j;
	counter = 1;
      }

      else if( (int)(100 * score[i][j].energy) == (int)(100 * mini) ){
	temp_j[counter] = j;
	counter++;
      }
    }/* j */

/*    printf("gene %d's score's completed\n", i); */

  }/* i */
}/* energy_cul */



int sdwin_par(int argc, char *argv[], int n)
{
  int i, prev;                         /* 16SrRNA which is cut from DB */
  char today[100];


  if(strcmp(argv[n],"-sdwin" ) == 0 ||
     strcmp(argv[n],"-sdwinS" ) == 0 ){
    
    if(strcmp(argv[n],"-sdwinS" ) == 0 ){   // single gene scoring mode
      parFlg = 1;
    }
    else       parFlg = 0;                  // normal mode
    
// printf("parFlg: %d\n", parFlg);


    /* ----------- assigning -------------------------------------------- */
    strcpy(paramSD, argv[n]);
    strcpy(today, argv[argc-1]);
    
    i = 0;
    prev = 0;
    while( today[i] != '\0' ){
      if( today[i] == '/' )       prev = i;
      i++;
    }
    
    strcpy( ObjInfo.fileName, &today[prev+1] );
    /*   printf("fileName : %s\n", ObjInfo.fileName ); */
    
    ObjInfo.lenF = strlen(ObjInfo.fileName);
    
    sdget(argv[argc - 1], "sdfile", ObjInfo.rRNA_seq);  /* call */
    
    /* printf("included rRNA seq(objin): %s\n", ObjInfo.rRNA_seq); */
    
    ObjInfo.lenS = strlen(ObjInfo.rRNA_seq);
    /* printf("lenS: %d\n", ObjInfo.lenS);    */

    /* make consensus sequence */
    for(i = 0; i < WINLEN; i++){
      rRNAseq[i] = cmpl(ObjInfo.rRNA_seq[ObjInfo.lenS -1-i]);   /* 3'- 5'*/
      /*
	 printf("rRNAseq[%d] %c\n", i, rRNAseq[i]);
	 printf("O.rRNAseq[%d] %c\n", ObjInfo.lenS -1 -i, 
	 ObjInfo.rRNA_seq[ObjInfo.lenS -1-i]);
	 printf("rRNAseq: %s\n", rRNAseq); */
    }/* for */
    
    rRNAseq[i] = '\0';
    /*    printf("rRNAseq: %s\n", rRNAseq); */

    /* ----------- assigning end ---------------------------------------- */


    return 1;      /* means sdwin.c need only one paramater "-sdwin" */
  }
  else return 0;
}



void sdwin_head(char *line)
{
  if(strncmp(line, "DEFINITION", 10) == 0){
    strcpy(ObjInfo.orgName, line);
  }

}


void sdwin_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds)
{
  int i, j, k, l, ll;
  int times;              /* how many times the window shifts? */
  static int max_score[GENE];
  int sum_score;

  static double avg_score[UPTO+DNTO+1];
  
  /* --------------------  declaration end ----------------------------- */

  ObjInfo.CDSnum = ncds;

  times = UPTO + DNTO +1;  /* when DNTO is 0, the last location of the top 
			      of the window would be the first codon */
  locEnergySet.times = times;


  /* error */
  if( times <= 0 ){
    fprintf(stderr, "# upstream sequence is shorter than window length\n");
    exit(1);
  }




  
  /* make complement sequence for complemental genes */
  compseqn = compseqget(seqn, max);
  

  energy_cul(times, ncds, cds, seqn, max);   /* free energy */ /* call */

  
  free(compseqn);

  loc_energy_cul(times, ncds); /* call */

}/* ent */
  

/* results would be printed */
void sdwin_fin()
{
  int j, k;
  int i, times; //for single gene score

  /* print */

  printf("# %s", ObjInfo.orgName);

  printf("# paramater : %s\n", paramSD);
  printf("# file name: %s\n",ObjInfo.fileName);
  printf("# CDS: %d\n", ObjInfo.CDSnum);
  printf("# SD: %s\n", ObjInfo.rRNA_seq);
  printf("# UPTO: %d\n", UPTO);
  printf("# DNTO: %d\n", DNTO);
  printf("# WINLEN: %d\n", WINLEN);
  printf("\n");
  printf("\n");

  printf("# Average free-energy score:%lf\n", locEnergySet.average);


/* printf("print for error check\n");
printf("# in struct locEnergySet\n");
printf("times: %d\n", locEnergySet.times);
  printf("\n");
  printf("\n");
*/

  // normal mode
  if(parFlg == 0){     
    /* print locational free-energy */
    printf("# average free energy at each location\n");
    for( j = 0; j < locEnergySet.times; j++ ){
      printf("%d %lf\n", -(UPTO - j), locEnergySet.loc_energy[j]/ObjInfo.CDSnum);
    }
  }// normal

  // single gene scoring mode 
  else if(parFlg == 1){ 
    printf("# this program will put %d genes' score at each location\n", 
	   ObjInfo.CDSnum);
    printf("\n");

    times = UPTO + DNTO +1;

    for(i =0; i < ObjInfo.CDSnum; i++){
      printf("\n");
      printf("\"gene%d\"\n", i);
      for( j = 0; j < times; j++ ){
	printf("%d %lf\n", -(UPTO - j), score[i][j].energy);
      }
    }
  }// single gene scoring mode
  else printf("error: parFlg in sdwin_fin printing\n");
  
  
  
}/* fin */

void sdwin_help()
{
  printf("-sdwin:  score FE between SD sequence and 16SrRNA by each file\n");
  printf("-sdwinS: score FE of each single gene\n");
//  printf("-sdwin2: window flows down from translation termination site\n");
}
  
  



