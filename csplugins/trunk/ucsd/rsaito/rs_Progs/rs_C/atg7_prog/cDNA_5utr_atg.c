#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

#define CAP_RANGE 10

static int uorf_range;

static int n_sequences;
static int n_sequences_uatg;
static int n_sequences_unknown;

static int n_uatg;
static int n_uorf;
static int n_inframe;
static int n_nonkozak;
static int n_weakkozak;
static int n_cap;
static int n_unknown;

static int loc_disp_flag = 0;

int kozak_dec(char seq[], int atg_start, int max){

  /*
  putchar(seq[atg_start - 3 - 1]);
  putchar(seq[atg_start - 4 - 1]);
  putchar(seq[atg_start - 5 - 1]);
  putchar(seq[atg_start - 6 - 1]);
  putchar(seq[atg_start - 7 - 1]);
  */

  if(atg_start - 3 <= 0 || 
     (seq[atg_start - 3 - 1] == 'a' || seq[atg_start - 3 - 1] == 'g')){
    return 1;
  }
  else return 0;
}

int kozak_weak_dec(char seq[], int atg_start, int max){
  /*
  putchar(seq[atg_start + 3 - 1]);
  */
  if(atg_start + 3 > max || seq[atg_start + 3 - 1] == 'g')return 1;
  else return 0;
}

int uorf_find(char seq[], int atg_start, int cds_start){
  int i,j;
  for(i = atg_start + 3;i <= cds_start && i - atg_start <= uorf_range;i +=3){
    if(strncmp(&seq[i - 1], "taa", 3) == 0 ||
       strncmp(&seq[i - 1], "tag", 3) == 0 ||
       strncmp(&seq[i - 1], "tga", 3) == 0){
      return i - atg_start;
    }
  }
  return 0;
}

int cDNA_5utr_atg_par(int argc, char *argv[], int n){

  
  if(strcmp(argv[n], "-cDNA_5utr_atg") == 0){

    uorf_range = atoi(argv[n + 1]);

    n_sequences = 0;
    n_sequences_uatg = 0;
    n_sequences_unknown = 0;

    n_uatg = 0;
    n_uorf = 0;
    n_inframe = 0;
    n_nonkozak = 0;
    n_weakkozak = 0;
    n_cap = 0;
    n_unknown = 0;

    return 2;
  }
  else if(strcmp(argv[n], "-cDNA_5utr_atg_loc_disp") == 0){

    loc_disp_flag = 1;
    return 1;

  }
  else return 0;
}

void cDNA_5utr_atg_head(char *line){

}

void cDNA_5utr_atg_ent(struct gparam *entry_info, char seqn[], int max,
		       struct cds_info cds[], int ncds){
  int i, j, found_atg, found_unknown;
  int tmp;

  if(ncds == 0 || cds[0].cds_start == 0)return;

  n_sequences ++;

  found_atg = 0;
  found_unknown = 0;
  for(i = 1;i < cds[0].cds_start - 2;i ++){
    if(strncmp(&seqn[i - 1], "atg", 3) != 0)continue;

    found_atg = 1;
    n_uatg ++;

    if((tmp = uorf_find(seqn, i, cds[0].cds_start)) != 0){ 
      /*      printf("UORF_LEN %d\n", tmp); */
      n_uorf ++; 
      continue; 
    }

    if((cds[0].cds_start - i) % 3 == 0){ n_inframe ++; continue; }

    if(kozak_dec(seqn, i, max) == 0 &&
       kozak_weak_dec(seqn, i, max) == 0){
      n_nonkozak ++; continue;
    }

    if(kozak_dec(seqn, i, max) == 0){
      n_weakkozak ++; continue;
    }

    if(i <= CAP_RANGE){ n_cap ++; continue; }

    found_unknown = 1;
    n_unknown ++;

    if(loc_disp_flag){
      printf("LOC ");
      for(j = i;j < i + 3;j ++){
	printf("%d ", j);
      }
      printf("CDS %d - %d ", cds[0].cds_start, cds[0].cds_end);
      printf("%s", entry_info->definition);
    }


  }

  n_sequences_uatg += found_atg;
  n_sequences_unknown += found_unknown;
}

void cDNA_5utr_atg_fin(){

  printf("Total number of sequences with ORF            %6d\n", n_sequences);
  printf("Total number of sequences with ATG in 5'UTR   %6d\n", n_sequences_uatg);
  printf("Total number of sequences with unknown ATG    %6d\n", n_sequences_unknown);


  putchar('\n');

  printf("Total ATG's in 5'UTR:                         %6d\n", n_uatg);
  printf("Number of ATG's with small upstream ORF:      %6d\n", n_uorf);
  printf("Remaining number with ATG's in the same frame:%6d\n", n_inframe);
  printf("Remaining number with non kozak:              %6d\n", n_nonkozak);
  printf("Remaining number with weak kozak:             %6d\n", n_weakkozak);
  printf("Remaining number with neibour of CAP site:    %6d\n", n_cap);
  printf("Remaining number with unknown:                %6d\n", n_unknown);

}

void cDNA_5utr_atg_help(){

  printf("-cDNA_5utr_atg Devide 5'UTR atg's into several characteristic groups: State maximun upstream ORF range\n");

}

