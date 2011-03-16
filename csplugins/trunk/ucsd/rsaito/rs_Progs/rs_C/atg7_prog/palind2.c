#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

/* Known bugs

Incorrect display of relative position when CDS is completely embedded
into larger CDS region

*/


static int min_base;
static int arb_base;

static int pos_disp_flag;

#define PALIND_ERROR 99999999

/* returns 0 if in any protein coding regions */
int loc_find_from_CDS(struct cds_info cds[], int ncds, int loc, int max){

  int i,j,k;

  for(i = 0;i < ncds;i ++){
    if(cds[i].cds_start == 0 || cds[i].cds_end == 0)continue;
    if(cds[i].cds_start <= loc && loc <= cds[i].cds_end)return 0;
    if(cds[i].cds_start > loc)break;
  }

  /* パリンドロームの位置がGenBankファイル上,最初のCDSより前にある場合 */
  if(i == 0){
    if(cds[i].cds_start == 0 || cds[ncds - 1].cds_end == 0)
      return PALIND_ERROR;
    
    if(cds[i].cds_start - loc < max - cds[ncds - 1].cds_end + loc){
      if(cds[i].complement == 0)return -(cds[i].cds_start - loc);
      else return cds[i].cds_start - loc;
    }
    else {
      if(cds[ncds - 1].complement == 0)
	return max - cds[ncds - 1].cds_end + loc;
      else return -(max - cds[ncds - 1].cds_end + loc);
    }
  }

  /* パリンドロームの位置がGenBankファイル上,最後のCDSより後にある場合 */

  if(i == ncds){
    if(cds[0].cds_start == 0 || cds[ncds - 1].cds_end == 0)return PALIND_ERROR;

    if(max - loc + cds[0].cds_start < loc - cds[ncds - 1].cds_end){
      if(cds[0].complement == 0)
	return -(max - loc + cds[0].cds_start);
      else return max - loc + cds[0].cds_start;
    }
    else {
      if(cds[ncds - 1].complement == 0)
	return loc - cds[ncds - 1].cds_end;
      else return -(loc - cds[ncds - 1].cds_end);
    }
  }


  if(cds[i].cds_start == 0)return PALIND_ERROR;
  if(i > 0 && cds[i - 1].cds_end == 0)return PALIND_ERROR;

  if(cds[i].cds_start - loc < loc - cds[i - 1].cds_end){
    if(cds[i].complement == 0)return -(cds[i].cds_start - loc);
    else return cds[i].cds_start - loc;
  }
  else {
    if(cds[i - 1].complement == 0)return loc - cds[i - 1].cds_end;
    else return -(loc - cds[i - 1].cds_end);
  }
}


int palind2_par(int argc, char *argv[], int n){

 

  if(strcmp(argv[n], "-palind2") == 0){
    min_base = atoi(argv[n + 1]);
    arb_base = atoi(argv[n + 2]);
    pos_disp_flag = 0;
    return 3;
  }
  if(strcmp(argv[n], "-palind2_pos") == 0){
    min_base = atoi(argv[n + 1]);
    arb_base = atoi(argv[n + 2]);
    pos_disp_flag = 1;
    return 3;
  }
  else return 0;
}

void palind2_head(char *head){


}

void palind2_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){
  int i,j,k,p,q;
  int plen, hplen;

  for(i = 0;i < max - 1;i ++){
    for(p = i,q = i + 1 + arb_base;
	p >= 0 && q < max && cmpl(seqn[p]) == seqn[q];
	p --, q++);
    hplen = i - p;
    plen  = hplen * 2;
    if(plen >= min_base){
      printf("Length: %d Position: %d %d ", 
	     plen, p + 1 + 1, p + 1 + plen + arb_base + 1 - 1);

      if(pos_disp_flag)
	printf("R_pos: %d ", loc_find_from_CDS(cds, ncds, i + 1, max));

      printf("Sequence: "); 
      for(j = p + 1;j < q;j ++){
	putchar(seqn[j]);
	if(j == p + hplen)putchar(' ');
	else if(j == p + hplen + arb_base)putchar(' ');
      }
      putchar('\n');
    }
  }
}

void palind2_fin(){


}

void palind2_help(){

  printf("-palind2\t Searches for palindrome sequence with some arbitrary bases: State minimun size and number of arbitrary sequence inside\n"); 
  printf("-palind2_pos\t Searches for palindrome sequence with some arbitrary bases: State minimun size and number of arbitrary sequence inside. Also displays relative position from the nearest CDS\n"); 

}




