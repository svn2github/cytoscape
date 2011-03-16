#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int min_length;


int orfsearch_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-orfsearch") == 0){
    min_length = atoi(argv[n + 1]);
    return 2;
  }
  else return 0;
}

void orfsearch_head(char *head){


}

void orfsearch_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){
  int i,j,k,l;
  char *compseqn;

  compseqn = compseqget(seqn, max);

  min_length = (min_length / 3) * 3 + 3;

  for(i = 0;i < max;i ++){
    if(strncmp("taa", &seqn[i], 3) == 0
       || strncmp("tag", &seqn[i], 3) == 0
       || strncmp("tga", &seqn[i], 3) == 0){
      /*      printf("Found %c%c%c at position %d\n", seqn[i],seqn[i+1],seqn[i+2],i); */
      for(j = i - 3;j >= 0;j -= 3){
	/*
	printf("Searching position %d for another stop codon.\n", j);
	*/
	if(strncmp("taa", &seqn[j], 3) == 0
	   || strncmp("tag", &seqn[j], 3) == 0
	   || strncmp("tga", &seqn[j], 3) == 0){
	  /*
	  printf("Another %c%c%c found at position %d\n", seqn[j],seqn[j+1],seqn[j+2],j);
	  printf("Going to search for start codon from position %d to %d\n",
		 j + 3, i - min_length);
		 */
	  for(k = j + 3;k <= i - min_length + 3;k += 3){
	    /*
	    printf("Searching for start codon at position %d\n", k);
	    */
	    if(strncmp("atg", &seqn[k], 3) == 0){
	      printf("%d %d\n", k + 1, i + 2 + 1);
	      /*
	      for(l = k;l <= i;l += 3)printf("%c%c%c ", 
					    seqn[l],seqn[l+1],seqn[l+2]);
	      putchar('\n');
	      */
	      break;
	    }
	  }
	  break;
	}
      }
    }
  }

  /* Do complement strand */

  for(i = 0;i < max;i ++){
    if(strncmp("taa", &compseqn[i], 3) == 0
       || strncmp("tag", &compseqn[i], 3) == 0
       || strncmp("tga", &compseqn[i], 3) == 0){
      for(j = i - 3;j >= 0;j -= 3){
	if(strncmp("taa", &compseqn[j], 3) == 0
	   || strncmp("tag", &compseqn[j], 3) == 0
	   || strncmp("tga", &compseqn[j], 3) == 0){
	  for(k = j + 3;k <= i - min_length + 3;k += 3){
	    if(strncmp("atg", &compseqn[k], 3) == 0){
	      printf("%d %d c\n", max - (i + 2), max - k);
	      /*
	      for(l = k;l <= i;l += 3)printf("%c%c%c ", 
					     compseqn[l],
					     compseqn[l+1],compseqn[l+2]);
	      putchar('\n'); */
	      break;
	    }
	  }
	  break;
	}
      }
    }
  }

  free(compseqn);

}

void orfsearch_fin(){


}

void orfsearch_help(){

  printf("-orfsearch\t Finds the longest ORF: State minimum length\n");

}


int orfsearch_myco_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-orfsearch_myco") == 0){
    min_length = atoi(argv[n + 1]);
    return 2;
  }
  else return 0;
}

void orfsearch_myco_head(char *head){


}

void orfsearch_myco_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){
  int i,j,k,l;
  char *compseqn;

  compseqn = compseqget(seqn, max);

  min_length = (min_length / 3) * 3 + 3;

  for(i = 0;i < max;i ++){
    if(strncmp("taa", &seqn[i], 3) == 0
       || strncmp("tag", &seqn[i], 3) == 0){
      /*      printf("Found %c%c%c at position %d\n", seqn[i],seqn[i+1],seqn[i+2],i); */
      for(j = i - 3;j >= 0;j -= 3){
	/*
	printf("Searching position %d for another stop codon.\n", j);
	*/
	if(strncmp("taa", &seqn[j], 3) == 0
	   || strncmp("tag", &seqn[j], 3) == 0){
	  /*
	  printf("Another %c%c%c found at position %d\n", seqn[j],seqn[j+1],seqn[j+2],j);
	  printf("Going to search for start codon from position %d to %d\n",
		 j + 3, i - min_length);
		 */
	  for(k = j + 3;k <= i - min_length + 3;k += 3){
	    /*
	    printf("Searching for start codon at position %d\n", k);
	    */
	    if(strncmp("atg", &seqn[k], 3) == 0){
	      printf("%d %d\n", k + 1, i + 2 + 1);
	      /*
	      for(l = k;l <= i;l += 3)printf("%c%c%c ", 
					    seqn[l],seqn[l+1],seqn[l+2]);
	      putchar('\n');
	      */
	      break;
	    }
	  }
	  break;
	}
      }
    }
  }

  /* Do complement strand */

  for(i = 0;i < max;i ++){
    if(strncmp("taa", &compseqn[i], 3) == 0
       || strncmp("tag", &compseqn[i], 3) == 0){
      for(j = i - 3;j >= 0;j -= 3){
	if(strncmp("taa", &compseqn[j], 3) == 0
	   || strncmp("tag", &compseqn[j], 3) == 0){
	  for(k = j + 3;k <= i - min_length + 3;k += 3){
	    if(strncmp("atg", &compseqn[k], 3) == 0){
	      printf("%d %d c\n", max - (i + 2), max - k);
	      /*
	      for(l = k;l <= i;l += 3)printf("%c%c%c ", 
					     compseqn[l],
					     compseqn[l+1],compseqn[l+2]);
	      putchar('\n'); */
	      break;
	    }
	  }
	  break;
	}
      }
    }
  }

  free(compseqn);

}

void orfsearch_myco_fin(){


}

void orfsearch_myco_help(){

  printf("-orfsearch_myco\t Finds the longest ORF in Mycoplasma: State minimum length\n");

}



