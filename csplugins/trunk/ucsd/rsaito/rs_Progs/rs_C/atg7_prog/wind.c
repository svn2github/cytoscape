#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#include "global_st.h"
#include "atg_func.h"

#define MAXWIN 5000
#define CENTER 2500

static int winl;
static char pat[20], nucleo[4];
static int upto, dnto;
static int win_count[MAXWIN];
static int nuc_count[MAXWIN];

int wind_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-wind") == 0){
    strcpy(pat, argv[n + 1]);
    winl = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;

}

void wind_head(char *line){


}

void wind_ent(struct gparam *entry_info, char seqn[], int max,
	      struct cds_info cds[], int ncds){

  int i,j,k,m,n;
  char *compseqn;
  int count;
/*
  compseqn = (char *)(malloc(max * sizeof(char)));
  if(compseqn == NULL){
    fprintf(stderr, "Memory full in wind function.\n");
    exit(1);
  }
  for(i = max;i > 0;i --)compseqn[max - i] = cmpl(seqn[i - 1]);
*/
  
  for(i = 0;i <= max - winl;i += winl){ /* window movement */
    count = 0;
    for(j = i;j < i + winl && j + strlen(pat) <= max;j ++){

      if(strncmp(&seqn[j], pat, strlen(pat)) == 0){ /* putchar('!'); */ count ++;}
/*      putchar(seqn[j]); */
    }  
    printf("%d %d\n", i, count);
  }
/*
  free(compseqn);
*/
}


void wind_fin(){


}

void wind_help(){

  printf("-wind\t Scans sequence by window:One entry only(state pattern and window size)\n");

}


int swind_par(int argc, char *argv[], int n){
  int i;

  if(strcmp(argv[n], "-swind") == 0){
    strcpy(nucleo, argv[n + 1]);
    winl = atoi(argv[n + 2]);
    upto = atoi(argv[n + 3]);
    dnto = atoi(argv[n + 4]);
    for(i = 0;i < MAXWIN;i ++){
      win_count[i] = 0;
      nuc_count[i] = 0;
    }
    return 5;
  }
  else return 0;

}

void swind_head(char *line){

}

void swind_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds){
  int i,j,k,m,n, count;
  char *compseqn;
  
  compseqn = compseqget(seqn, max);
  
  for(n = 0;n < ncds;n ++){
    if(valid_cds[n] == 0)continue;
    
    if(cds[n].complement == 0){
      if(cds[n].cds_start == 0)continue;
      
      for(i = cds[n].cds_start - upto;i <= cds[n].cds_start + dnto;i ++){
	if(i < 1 || i + winl - 1 > max)continue;
	win_count[ CENTER - ( cds[n].cds_start - i ) ] ++;
	count = 0;
	for(j = i;j < i + winl;j ++){
	  for(k = 0;k < strlen(nucleo);k ++)
	    if(nucleo[k] == seqn[j - 1])break;
	  if(k < strlen(nucleo))count ++;
	}
	nuc_count[ CENTER - ( cds[n].cds_start - i) ] += count;
      }
      
    }
    else {
      if(cds[n].cds_end == 0)continue;
      for(i = max - cds[n].cds_end + 1 - upto;
	  i <= max - cds[n].cds_end + 1 + dnto; i ++){
	if(i < 1 || i + winl - 1 > max)continue;
	win_count[ CENTER - ( max - cds[n].cds_end + 1 - i) ] ++;
	count = 0;
	for(j = i;j < i + winl;j ++){
	  for(k = 0;k < strlen(nucleo);k ++)
	    if(nucleo[k] == compseqn[j - 1])break;
	  if(k < strlen(nucleo))count ++;
	}
	nuc_count[ CENTER - ( max - cds[n].cds_end + 1 - i) ] += count;
      }
    }
  }
  
  free(compseqn);
  
}

void swind_fin(){

  int i,j;
/*
  printf("Valid window information\n");
  for(i = CENTER - upto; i <= CENTER + dnto;i ++)
    printf("%d %d\n", i - CENTER, win_count[i]);

  putchar('\n');

  printf("Nucleotide count information\n");
  for(i = CENTER - upto; i <= CENTER + dnto;i ++)
    printf("%d %d\n", i - CENTER, nuc_count[i]);

  putchar('\n');
*/
  printf("Nucleotide Content\n");
  for(i = CENTER - upto; i <= CENTER + dnto;i ++)
    if(win_count[i] != 0)
      printf("%d %lf\n", 
	     i - CENTER, 1.0 * nuc_count[i] / (win_count[i] * winl));

}

void swind_help(){

  printf("-swind\t Calculates nucleotide distribution in windows:");
  printf("State nucleotides, window size, how far upstream and downstream\n");

}


int gcwind_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-gcwind") == 0){
    strcpy(pat, argv[n + 1]);
    winl = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;

}

void gcwind_head(char *line){


}

void gcwind_ent(struct gparam *entry_info, char seqn[], int max,
	      struct cds_info cds[], int ncds){

  int i,j,k,m,n;
  char *compseqn;
  int count;
  
  for(i = 0;i <= max - winl;i += winl){ /* window movement */
    count = 0;
    for(j = i;j < i + winl;j ++){
      for(k = 0;k < strlen(pat);k ++){
	if(seqn[j] == pat[k])break;
      }
      if(k < strlen(pat))count ++;
    }
    printf("%d %lf\n", i, 1.0 * count / winl);
  }
}


void gcwind_fin(){


}

void gcwind_help(){

  printf("-gcwind\t Calculates nucleotide content in the whole genome:One entry only(state bases and window size)\n");

}

