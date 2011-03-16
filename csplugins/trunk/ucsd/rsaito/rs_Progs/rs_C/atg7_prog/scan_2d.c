#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int pos1, pos2;
static int nnuc, range, step;


int scan_2d_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-scan_2d") == 0){
    pos1 = atoi(argv[n + 1]);
    pos2 = atoi(argv[n + 2]);
    nnuc = atoi(argv[n + 3]);
    range = atoi(argv[n + 4]);
    step = atoi(argv[n + 5]);
    return 6;
  }
  else return 0;
}

void scan_2d_head(char *line){

}

void scan_2d_ent(struct gparam *entry_info, char seqn[], int max,
		 struct cds_info cds[], int ncds){
  int i,j,k,con_max;
  char *compseqn;
  int scanp1, scanp2;
  char *scanseq;

  static char seque1[500],seque2[500], result1[1000], result2[1000];
  int result_len;
  static int match_res1[500], match_res2[500];
  double score, min_score;
  int minpos1, minpos2;

  compseqn = compseqget(seqn,max);

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

  min_score = 0;
  for(i = -range;i <= range;i += step){
    if(scanp1 + i < 1 || scanp1 + 1 > max)continue;
    for(j = -range;j <= range;j += step){

      if(scanp2 + j < 1 || scanp2 + j > max)continue;
/*      printf("[%d %d]\n", i,j); */
      strncpy(seque1, &scanseq[scanp1 - 1 + i], nnuc); 
      seque1[nnuc] = '\0';
      strncpy(seque2, &scanseq[scanp2 - 1 + j], nnuc);
      seque2[nnuc] = '\0';
      rev(seque2);
/*
      printf("Sequence 1:%s\n", seque1);
      printf("Sequence 2:%s\n", seque2);
*/
      score = sd_match_opt(seque1, seque2, result1, result2, &result_len, 
			   match_res1, match_res2);
      if(min_score > score){
	min_score = score;
	minpos1 = i; minpos2 = j;
      }
/*
      disp_res(result1, result2, match_res1, match_res2, result_len);
      if(pos1 <= pos2)
	printf("[ %d %d ( %d %d )] Score: %lf\n\n", pos1 + i , pos2 + j,
	       i,j,score);
      else 
	printf("[ %d %d ( %d %d )] Score: %lf\n\n", pos1 - i , pos2 - j,
	       i,j,score);
*/
    }
  }
  strncpy(seque1, &scanseq[scanp1 - 1 + minpos1], nnuc); 
  seque1[nnuc] = '\0';
  strncpy(seque2, &scanseq[scanp2 - 1 + minpos2], nnuc);
  seque2[nnuc] = '\0';
  rev(seque2);
  score = sd_match_opt(seque1, seque2, result1, result2, &result_len, 
		   match_res1, match_res2);
  printf("Sequence 1:%s\n", seque1);
  printf("Sequence 2:%s\n", seque2);
  if(pos1 <= pos2)
    printf("[ %d %d ( %d %d )] Score: %lf\n\n",
	   pos1 + minpos1, pos2 + minpos2, minpos1, minpos2, score);
  else 
    printf("[ %d %d ( %d %d )] Score: %lf\n\n",
	   pos1 - minpos1 , pos2 - minpos2, minpos1, minpos2, score);
  disp_res(result1, result2, match_res1, match_res2, result_len);
  
  j = 0; k = 0; con_max = 0;
  for(i = 0;i < result_len;i ++){
    if(result1[i] != '-' && match_res1[j] == 1)k ++;
    else k = 0;
    if(con_max < k)con_max = k;
    if(result1[i] != '-')j ++;
  }
  printf("Max pairing: %d\n", con_max);


  putchar('\n');




  free(compseqn);
}


void scan_2d_fin(){

}

void scan_2d_help(){

  printf("-scan_2d\t Searches where two sequences annealing with minimun free energy. State 5 numbers:");
  printf("Position 1, Position 2, Number of bases, range, step.\n");

}
