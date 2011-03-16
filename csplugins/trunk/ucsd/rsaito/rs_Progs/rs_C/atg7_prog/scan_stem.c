#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int pos1, pos2;
static int range;

static int hist1[500],hist2[500];

int search_stem(char *str1, char *str2, int *pos1, int *pos2)
{
   int i,j,k,m,n;
   int stem;
   int max, max_pos_i, max_pos_j;
   int str1_len, str2_len;

   str1_len = strlen(str1);
   str2_len = strlen(str2);

   max = 0;
   stem = 0;
   for(i = 0;i < str1_len; i ++){
      for(j = 0;j < str2_len && i + j < str1_len;j ++){
         if(comp_match(str1[i + j], str2[j]))stem ++;
         else { 
            if(stem > max){ max = stem; max_pos_i = i + j; max_pos_j = j; }
            stem = 0;
         }
      }
      if(stem > max){ max = stem; max_pos_i = i + j; max_pos_j = j; }
      stem = 0;    
   } 
   if(stem > max){ max = stem; max_pos_i = i + j; max_pos_j = j; }
   stem = 0;

   for(j = 0;j < str2_len;j ++){
     for(i = 0;i < str1_len && j + i < str2_len;i ++){
       if(comp_match(str1[i], str2[j + i]))stem ++;
       else {
	 if(stem > max){ max = stem;max_pos_i = i;max_pos_j = i + j; }
	 stem = 0;
       }
     }
     if(stem > max){ max = stem;max_pos_i = i;max_pos_j = i + j; }
     stem = 0;
   }
   if(stem > max){ max = stem;max_pos_i = i;max_pos_j = i + j; }
   stem = 0;

   *pos1 = max_pos_i - max;
   *pos2 = max_pos_j - max;
   return max;
}


int scan_stem_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-scan_stem") == 0){
    pos1 = atoi(argv[n + 1]);
    pos2 = atoi(argv[n + 2]);
    range = atoi(argv[n + 3]);
    return 4;
  }
  else return 0;
}

void scan_stem_head(char *line){

}

void scan_stem_ent(struct gparam *entry_info, char seqn[], int max,
		 struct cds_info cds[], int ncds){
  int i,j,k;
  char *compseqn;
  int scanp1, scanp2;
  char *scanseq;
  
  int max_stem, maxpos1, maxpos2, rpos1, rpos2, apos1, apos2;

  static char seq1[5000],seq2[5000];

  compseqn = compseqget(seqn, max);

   if(pos1 > pos2){
    scanp1 = max - pos1 + 1;
    scanp2 = max - pos2 + 1;
    scanseq = compseqn;
  }
  else {
    scanp1 = pos1;
    scanp2 = pos2;
    scanseq = seqn;
  }

  strncpy(seq1, &scanseq[scanp1 - 1 - range], range*2 + 1);
  seq1[ range*2 + 1 ] = '\0';
  rev(seq1);

  strncpy(seq2, &scanseq[scanp2 - 1 - range], range*2 + 1);
  seq2[ range*2 + 1 ] = '\0';

  max_stem = search_stem(seq1, seq2, &maxpos1, &maxpos2);
  rpos1 = -(maxpos1 + max_stem - range - 1);
  rpos2 =   maxpos2 - range ;

  if(pos1 > pos2){
    apos1 = pos1 - rpos1;
    apos2 = pos2 - rpos2;
  }
  else {
    apos1 = pos1 + rpos1;
    apos2 = pos2 + rpos2;
  }

  printf("Pos: %d %d RelPos: %d %d StemSize: %d\n",
	 apos1, apos2, rpos1, rpos2, max_stem);

  printf("seq1(5'-3'):");
  for(i = max_stem - 1;i >= 0;i --)putchar(seq1[i + maxpos1]);
  putchar('\n');

  printf("seq2(3'-5'):");
  for(i = max_stem - 1;i >= 0;i --)putchar(seq2[i + maxpos2]);
  putchar('\n');

  free(compseqn);
  
}

void scan_stem_fin(){

}

void scan_stem_help(){

  printf("-scan_stem Searches for stem. State 3 numbers: Position 1, 2, range\n");

}

#define COUNT 10000
int rscan_stem_par(int argc, char *argv[], int n)
{
  int i;
  if(strcmp(argv[n], "-rscan_stem") == 0){
    range = atoi(argv[n + 1]);
    for(i = 0;i < 500;i ++)hist1[i] = 0;
    return 2;
  }
  else return 0;
}

void rscan_stem_head(char *line){

}

void rscan_stem_ent(struct gparam *entry_info, char seqn[], int max,
		 struct cds_info cds[], int ncds){

  int i,j,k;
  char *compseqn;
  int scanp1, scanp2;
  char *scanseq;
  int counter;

  int max_stem, maxpos1, maxpos2, rpos1, rpos2, apos1, apos2;

  static char seq1[5000],seq2[5000];

  compseqn = compseqget(seqn, max);

  for(counter = 0;counter < COUNT;counter ++){
    pos1 = random() % (max - range*2) + range;
    pos2 = random() % (max - range*2) + range;

    if(pos1 > pos2){
      scanp1 = max - pos1 + 1;
      scanp2 = max - pos2 + 1;
      scanseq = compseqn;
    }
    else {
      scanp1 = pos1;
      scanp2 = pos2;
      scanseq = seqn;
    }
    
    strncpy(seq1, &scanseq[scanp1 - 1 - range], range*2 + 1);
    seq1[ range*2 + 1 ] = '\0';
    rev(seq1);
    
    strncpy(seq2, &scanseq[scanp2 - 1 - range], range*2 + 1);
    seq2[ range*2 + 1 ] = '\0';
    
    max_stem = search_stem(seq1, seq2, &maxpos1, &maxpos2);
    rpos1 = -(maxpos1 + max_stem - range - 1);
    rpos2 =   maxpos2 - range ;
    
    if(pos1 > pos2){
      apos1 = pos1 - rpos1;
      apos2 = pos2 - rpos2;
    }
    else {
      apos1 = pos1 + rpos1;
      apos2 = pos2 + rpos2;
    }
/*
    printf("Pos: %d %d RelPos: %d %d StemSize: %d\n",
	   apos1, apos2, rpos1, rpos2, max_stem);
    
    printf("seq1(5'-3'):");
    for(i = max_stem - 1;i >= 0;i --)putchar(seq1[i + maxpos1]);
    putchar('\n');
    
    printf("seq2(3'-5'):");
    for(i = max_stem - 1;i >= 0;i --)putchar(seq2[i + maxpos2]);
    putchar('\n');
*/

    if(max_stem < 500)hist1[ max_stem ] ++;
/*    printf("%d %d\n", max_stem, hist1[max_stem]); */
  }

  free(compseqn);
  
}

void rscan_stem_fin(){

  int i, sum;

  for(i = 0;i < 30;i ++)
    printf("%d %d\n", i, hist1[i]);

/*
  for(i = 0, sum = 0;i < 100;i ++){
    printf("%d %lf\n", i, 1.0*(COUNT - sum)/COUNT);
    sum += hist1[i];
  }
*/
}

void rscan_stem_help(){

  printf("-rscan_stem Searches for stem randomly. State range.\n");

}


