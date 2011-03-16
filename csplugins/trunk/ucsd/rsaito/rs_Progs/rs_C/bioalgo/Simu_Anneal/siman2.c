
#define VERSION "May.30 1996"

/****************************************************************/
/*     alignment program using SIMULATED ANNEALING METHOD       */
/****************************************************************/
/*                                    programmed by R.Saito     */

/* compile with: cc ????.c -lm                                  */

/* notice: 
   log(2) ---> log(2.0)
   EOF must be -1
   time function is called in seqevalfile and may exhaust memory
     by time string

   -itgap function in -lgap (calc_two_lines2) is still under construction
    mutual information will not be displayed correctly
*/

#include <stdio.h>
#include <time.h>
#include <math.h>
#include <string.h>

/* for checking all malloc are made free and all fopen are closed */
int malloc_check = 0;
int fopen_check = 0;

char *malloc_c(size)
size_t size;
{
  malloc_check ++;
  printf("Malloc remaining is %d\n",malloc_check);
  return((char *)malloc(size));
}
void free_c(p)
void *p;
{ 
  malloc_check --;
  free(p);
  printf("Malloc still remaining is %d\n",malloc_check);
}

FILE *fopen_c(filename,type)
char *filename,*type;
{
  fopen_check ++;
  printf("Files open is %d\n",fopen_check);
  return(fopen(filename,type));
}

void fclose_c(fp)
FILE *fp;
{
  fopen_check --;
  fclose(fp);
  printf("Files still open is %d\n",fopen_check);
}


#define GAPM '-'  /* gap mark */
#define REMM '#'   /* remark   */

#define ONECHAIN 1024 /* length of data of each data chain 
			 Data chain is used when reading data 
			 of unknown length */


/* FOR NUCLEIC ACID CODE ONLY */
#define LETTNUM (4 +1) /* number of letters supported
				+1 is temporal for gap */
#define LETTNUM_4 (4)  /* number of letters supported when gaps are 
			  assumed as all the possible letters. */


/* 
1.All the letter and GAPM is acceptable.
2.Mutual information will not be recorded.
3.Top scores will not be calculated. 
*/
double calc_one_line();
double calc_two_lines();
double mincal();

/*
1.NUCLEIC ACID ONLY.
2.Gap is considered as one of the letter.
*/
double calc_one_line2();
double calc_two_lines2();

/*
1.NUCLEIC ACID ONLY.
2.Gap is considered as all the possible letters.
*/
double calc_one_line4();
double calc_two_lines4();
double calc_two_lines4_1(); /* for ignoring two gap pairs */
double calc_two_lines4_2(); /* for assuming two gap pairs as 
			       one of the letter */
double calc_two_lines4_3(); /* for assuming two gap pairs as
			       foreign letters */

/* evaluate sequence alignment
1.NUCLEIC ACID ONLY
2.Gap is considered as one letter or all the possible letters
  (also two gaps pair is either recognised or ignored depending on parameter)
3.Mutual information or joint entropy will be recorded
4.Top table will be calculated and same column will or will not be recognized
  more than twice
5.if two column is filled with gaps,joint entropy may or may not be 0
*/
double mincal5(); /* for only looking certain column only once when 
		     calculating top n score */


double strtodouble();
double evform();
double evalseq();
double colone();

char **seqget();
char **seqmake();

/* This structure variable is used for parameters or as a global variables. */
struct param {

/* command line */
  int argc;
  char **argv;
  int argc2; /* from parameter file */
  char **argv2;

  char *filename; /* File name to read.
		     If NULL,data is read from standard I/O */
  int gright;     /* 1=all gaps column will be shifted to right
		       when saving the file */
  char *filnampar; /* File name to read for parameter.
		      If NULL, not read */
  int printpro;  /* 1=prints out process of aligning */
  int just_eval; /* 0=do alignment 1=just evaluate */
  int pair_disp; /* 1=displays pair evaluation */
  int pair_disp_sort; /* 1=sorts and displays pair evaluation */
  int pair_sort_colo; /* 1=sorts and displays pair evaluation but
			 do not display same column twice */
  int ent_disp;  /* 1=displays entropy of each column */
  int regsavec;   /* if regsavec >= 1, it will be every count to save
		     alignment state */
  char *rgsfilnam; /* file name to save alignment state
		         valid if regsavec >= 1  */

  int testmode;  /* 1=test mode */
/* alignment parameter */
  int count_max; /* number of tries(counts) */
  int count_start; /* number of counts that start from */
  int easeq;     /* possibility (%) for choosing RS_AND argorithm for
		    dropping sequences selected */
  int sper;      /* possibility (%) for keeping selected sequences
		    in RS_AND argorithm */
  double hot_t;  /* worse changes allowable at the rate of 36% at first */
  int cold_c;    /* when counter comes to this value,
		    no worse change allowed */
  int gapper;    /* possibility for adding more gaps */
  
  int treeb;     /* apply tree base method */

/* evaluation parameter */
  int eval_type; /* 0=joint entropy 1=mutual information
		    2=new evaluation */
  int alpha;     /* 0=indicated character(ex.nucleic acid) only
		    1=all the alphabet is acceptable */
  int gapev;     /* 0= gap is treated as one of the letter
		    1= gap is treated as all the possible letters */
  int itgap;     /* 1=ignore two gap pairs for joint entropy */
  int ltgap;     /* 1=two gap pairs will be assumed as one of the letter */
  int fgap;      /* >1 two gap pairs will be assumed as several forein letters
		  */
  int rgap;      /* 1=when itgap is on(ignore two gap pairs),score calculated
		    from joint entropy will be as follows:
		    jeev = score calculated from joint entropy
		    x = joint entropy 
		    r = non two gap pair ratio
		    lettnum = number of letters supported excluding gap
		    
		    jeev = (x - log2(lettnum^2))*r + log2(lettnum^2)
		 */   
  int agpr;      /* 1= when colone_all=1,score will be multiplied by
		       number of colone_all pairs/number of non all_gap pairs
		       In the future,it may be modified to work when
		       colone_all = 0
		 */
  int agcr;      /* 1= when colone_all=1,score will be multiplied by
		       number of columns/number of non-all-gap columns
		       sigmd or jep should be indicated
		 */
  double evthre; /* !=0 threshold for joint entropy.If pair evaluation is 
		    lower than this,evaluation will be ignored,but
                    total evaluation will be multiplied according to
		    rate of valid pairs. colone_all and agpr must be 1 */

  double twogt;  /* threshold for two gaps pair
		    Let s_num be number of sequences(pairs) and s_num2 be 
		    number of pairs without two gap pair.
		    If s_num2/s_num < twogt then that pair is ignored 
		    when calculating joint entropy */
  int gapcol;    /* 1=if two column is all gap,joint entropy will be 0 
		    valid if itgap = 1 */

  int topn;      /* calculates sum of topn and this will be the score
		    if topn=0, it doesn't do that. */
  int topmul;    /* 1=top score will be multiplied */

  int colone;    /* 1=each column is looked only once 
		    when calculating top n score */
  int colone_all; /* 1=each column is looked only once and calculates
		     all the pair (one column may remain)
		     colone <--- 1
		     topn   <--- s_len/2 */

  double jep; /* >= 1 : joint entropy will be 
              (worst joint entropy - joint entropy calculated)^jep
              valid if itgap = 1 */

  int sigmoid; /* apply sigmoid function to joint entropy
                  valid if itgap = 1
		  evaluation = 1 /( (1/siglim + (1/e)^((j.e. - sigp)*sigm)) )
                */
  double siglim; /* limit of upper value */
  double sigp;   /* center position */
  double sigm;   /* multiplication */

  int cons;      /* rate of conservation (valid if p->eval_type = 2) */
  int corr;      /* rate of correlation (valid if p->eval_type = 2) */
  int match;     /* 1=evaluation will be matching of letters */
  int pam250;    /* 1=evaluation will be PAM250 */

/* gap penalties when calculating sum of pairs */
  int outgap;
  int outexgap;
  int opengap;
  int extgap;

  int addmatch;
/* used as global */

  double score1; /* used as score when evthre > 0 */

};


/* structure of data chain.It is used to read data of unknown length */
  struct datachain {
    int ndata;   /* number of data in cdata (max ONECHAIN) */
    char cdata[ONECHAIN];
    struct datachain *next; /* if no more,NULL */
  } ;

/* FUNCTIONS FOR TREE BASE */
/* structure of matrix (ban)
         A     C
         T     C
      o     o     o
    (0,0) (0,1) (0,2)
ATC    (0,0) -------------locations of first letters
      o     o     o
    (1,0) (1,1)
C-T    (1,0)   \
      o ->  o     o
        ^   ^
        this arrow is keeped in this node

*/




#define UP 1
#define LEFT 2
#define CROSS 3

struct dpmatrix {
   int score;
   int dir;
};

struct amino_group {
  int seq_num;
  int seq_len;
  char **seque;
};

struct gaparam {
  int outgap;
  int outexgap;
  int opengap;
  int extgap;

  int addmatch; /* additional match score */

}; /* struct for calculating gap penalty */

/* PAM250 calculation */
int change(c)
char c;
{
  char *amino = "ARNDCQEGHILKMFPSTWYVBZX";

  int i;

  for(i = 0; i < 23; i++)
    if(c == amino[i]) return(i);
  return 0;
}

int simplepoint(c1, c2)
char c1,c2;
{
  if(c1 == c2)return 4;
  else return -2;
}


int point(x,y,match)
int x,y,match;
{
  char *evmat[23];
  char nca[10];
  int i,j,m,n;

  if(match > 0){
    if(x == y)return match;
    else return 0;
  }

  x = change(x);
  y = change(y);

 evmat[0] =
 "2,-2, 0, 0,-2, 0, 0, 1,-1,-1,-2,-1,-1,-4, 1, 1, 1,-6,-3, 0, 0, 0, 0,";

 evmat[1] =
 "-2, 6, 0,-1,-4, 1,-1,-3, 2,-2,-3, 3, 0,-4, 0, 0,-1, 2,-4,-2,-1, 0, 0,";

 evmat[2] =
 "0, 0, 2, 2,-4, 1, 1, 0, 2,-2,-3, 1,-2,-4,-1, 1, 0,-4,-2,-2, 2, 1, 0,";

evmat[3] =
 "0,-1, 2, 4,-5, 2, 3, 1, 1,-2,-4, 0,-3,-6,-1, 0, 0,-7,-4,-2, 3, 3, 0,";

evmat[4] =
 "-2,-4,-4,-5,12,-5,-5,-3,-3,-2,-6,-5,-5,-4,-3, 0,-2,-8, 0,-2,-4,-5, 0,";

evmat[5] =
 "0, 1, 1, 2,-5, 4, 2,-1, 3,-2,-2, 1,-1,-5, 0,-1,-1,-5,-4,-2, 1, 3, 0,";

evmat[6] =
 "0,-1, 1, 3,-5, 2, 4, 0, 1,-2,-3, 0,-2,-5,-1, 0, 0,-7,-4,-2, 2, 3, 0,";

evmat[7] =
 "1,-3, 0, 1,-3,-1, 0, 5,-2,-3,-4,-2,-3,-5,-1, 1, 0,-7,-5,-1, 0,-1, 0,";

evmat[8] =
 "-1, 2, 2, 1,-3, 3, 1,-2, 6,-2,-2, 0,-2,-2, 0,-1,-1,-3, 0,-2, 1, 2, 0,";

evmat[9] =
 "-1,-2,-2,-2,-2,-2,-2,-3,-2, 5, 2,-2, 2, 1,-2,-1, 0,-5,-1, 4,-2,-2, 0,";

evmat[10] =
 "-2,-3,-3,-4,-6,-2,-3,-4,-2, 2, 6,-3, 4, 2,-3,-3,-2,-2,-1, 2,-3,-3, 0,";

evmat[11] =
 "-1, 3, 1, 0,-5, 1, 0,-2, 0,-2,-3, 5, 0,-5,-1, 0, 0,-3,-4,-2, 1, 0, 0,";

evmat[12] =
 "-1, 0,-2,-3,-5,-1,-2,-3,-2, 2, 4, 0, 6, 0,-2,-2,-1,-4,-2, 2,-2,-2, 0,";

evmat[13] =
 "-4,-4,-4,-6,-4,-5,-5,-5,-2, 1, 2,-5, 0, 9,-5,-3,-3, 0, 7,-1,-5,-5, 0,";

evmat[14] =
 "1, 0,-1,-1,-3, 0,-1,-1, 0,-2,-3,-1,-2,-5, 6, 1, 0,-6,-5,-1,-1, 0, 0,";

evmat[15] =
 "1, 0, 1, 0, 0,-1, 0, 1,-1,-1,-3, 0,-2,-3, 1, 2, 1,-2,-3,-1, 0, 0, 0,";

evmat[16] =
 "1,-1, 0, 0,-2,-1, 0, 0,-1, 0,-2, 0,-1,-3, 0, 1, 3,-5,-3, 0, 0,-1, 0,";

evmat[17] =
 "-6, 2,-4,-7,-8,-5,-7,-7,-3,-5,-2,-3,-4, 0,-6,-2,-5,17, 0,-6,-5,-6, 0,";

evmat[18] =
 "-3,-4,-2,-4, 0,-4,-4,-5, 0,-1,-1,-4,-2, 7,-5,-3,-3, 0,10,-2,-3,-4, 0,";

evmat[19] =
 "0,-2,-2,-2,-2,-2,-2,-1,-2, 4, 2,-2, 2,-1,-1,-1, 0,-6,-2, 4,-2,-2, 0,";

evmat[20] =
 "0,-1, 2, 3,-4, 1, 2, 0, 1,-2,-3, 1,-2,-5,-1, 0, 0,-5,-3,-2, 2, 2, 0,";

evmat[21] =
 "0, 0, 1, 3,-5, 3, 3,-1, 2,-2,-3, 0,-2,-5, 0, 0,-1,-6,-4,-2, 2, 3, 0,";

evmat[22] =
 "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,";

/*  printf("%d %d loc.\n",x,y); */

  i = 0;j = 0;
  while(j < y){
     if(evmat[x][i] == ',')j ++;
     i ++;
   }


  n = 0;
  while(evmat[x][i] != ','){
    nca[n ++] = evmat[x][i ++];
  }
  nca[n] = '\0';
/*  printf("number is %s\n",nca); */

  return(atoi(nca));

}


/* PAM250 (end) */


int max3(x,y,z)
int x,y,z;
{
  if(z >= x){
     if(z >= y)return 2;
     else return 1;
  }
  else if(x >= y)return 0;
  else return 1;
}

agmake(ag,seque,s_num,s_len)
/* put sequences to group */ 
struct amino_group *ag;
char **seque;
int s_num,s_len;
{
   int i,j;
   char **seque2;

   seque2 = seqmake(s_num,s_len);
   for(i = 0;i < s_num;i ++)
      for(j = 0;j < s_len;j ++)
	 seque2[i][j] = seque[i][j];

   ag->seq_num = s_num;
   ag->seq_len = s_len;
   ag->seque = seque2;
}

agfree(ag)
struct amino_group *ag;
{
   if(ag->seque != NULL)seqfree(ag->seque);
   ag->seq_num = 0;
   ag->seq_len = 0;
}

agchange(ag1,ag2)
struct amino_group *ag1,*ag2;
{
   char **tmpseque;
   int tmp_num,tmp_len;

   tmpseque = ag1->seque;
   tmp_num = ag1->seq_num;
   tmp_len = ag1->seq_len;

   ag1->seque = ag2->seque;
   ag1->seq_num = ag2->seq_num;
   ag1->seq_len = ag2->seq_len;

   ag2->seque = tmpseque;
   ag2->seq_num = tmp_num;
   ag2->seq_len = tmp_len;
}

struct amino_group *convseqag(seque,s_num,s_len)
/* converts sequences into amino groups */
char **seque;
int s_num,s_len;
{
   struct amino_group *agr0;
   int i,j;

   agr0 = (struct amino_group *)malloc(s_num * sizeof(struct amino_group));

   for(i = 0;i < s_num; i++){
      agr0[i].seque = seqmake(1,s_len);
      for(j = 0;j < s_len;j ++)agr0[i].seque[0][j] = seque[i][j];
      agr0[i].seq_num = 1;
      agr0[i].seq_len = s_len;
   }
   return agr0;
}
	
struct dpmatrix **banmake(tate,yoko)
/* makes dp matrix with size tate x yoko */
/* tate = s_len + 1 */
int tate,yoko;
{
  struct dpmatrix *rec,**rec_table;
  int n;

  rec = (struct dpmatrix *)malloc(tate * yoko * sizeof(struct dpmatrix));
  rec_table = (struct dpmatrix **)
                 malloc(tate * sizeof(struct dpmatrix *));

  for(n = 0;n < tate; n++)
    rec_table[n] = &rec[n * yoko];

  return rec_table;
}

banfree(ban) 
/* release memory for ban */
struct dpmatrix **ban;
{
  free(ban[0]);
  free(ban);
}

int gap_p_calc1(sequen,sequen2,s_len,s_len2,nth,nth2,ban,gp)
/* calculates a penalty of single gap
ex.
  o ->  o    this direction indicates (single?) gap for sequen 
(2,3) (2,4)  state (2,3) to calculate the penalty of this gap 

*/
/* if cross,requires: sequen[nth] = GAPM && sequen2[nth2] != GAPM */
char sequen[],sequen2[];
int s_len,s_len2;
int nth,nth2;
struct dpmatrix **ban;
struct gaparam gp;
{
  int nc1,nc2,open_gap_f,out_gap_f;
  int i,j;
  nc1 = nth;nc2 = nth2;
  out_gap_f = 1;  /* if 0,not out gap */
  open_gap_f = 1; /* if 0,not opening gap */

  while(nc1 > 0 || nc2 > 0){

     if(ban[nc1][nc2].dir == LEFT){ /* gaps inserted in sequen */ 
        nc2 --; /* printf("%c %c ",'-',sequen2[nc2]); */
        if(sequen2[nc2] != GAPM){
	   open_gap_f = 0; /* printf("not open gap "); */
        }
     }
     else if(ban[nc1][nc2].dir == UP){
	nc1 --; /* printf("%c %c ",sequen[nc1],'-'); */
	if(sequen[nc1] != GAPM){
	   out_gap_f = 0; break; /* no additional trace of arcs */
        }
     }
     else if(ban[nc1][nc2].dir == CROSS){
	nc1 --; nc2 --; /* printf("%c %c ",sequen[nc1],sequen2[nc2]); */
	if(sequen[nc1] != GAPM){ out_gap_f = 0; break; }
	else if(sequen[nc1] == GAPM && sequen2[nc2] != GAPM)
	   { open_gap_f=0; /* printf("not open gap "); */ }
       /* else double gaps */ 
     }
     /* putchar('\n'); */
  } 

  for(i = nth;i < s_len;i ++)if(sequen[i] != GAPM)break;
  if(i == s_len){ out_gap_f = 1;/* printf("OUT GAP left\n"); */ }

  if(out_gap_f){
     if(open_gap_f)return gp.outgap;
     else return gp.outexgap;
  }
  if(open_gap_f)return gp.opengap;
  else return gp.extgap;

}

int gap_p_calc2(sequen,sequen2,s_len,s_len2,nth,nth2,ban,gp)
/* calculates a penalty of single gap */
/* requires: sequen2[nth2] = GAPM && sequen[nth] != GAPM */
char sequen[],sequen2[];
int s_len,s_len2;
int nth,nth2;
struct dpmatrix **ban;
struct gaparam gp;
{
  int nc1,nc2,open_gap_f,out_gap_f;
  int i,j;
  nc1 = nth;nc2 = nth2;
  open_gap_f = 1; /* if 0,not opening gap */
  out_gap_f = 1; /* if 0,not out gap */

  while(nc1 > 0 || nc2 > 0){
     if(ban[nc1][nc2].dir == UP){ /* gaps inserted in sequen2 */ 
        nc1 --; /* printf("%c %c ",sequen[nc1],'-'); */
        if(sequen[nc1] != GAPM){
	   open_gap_f = 0; /* printf("not open gap "); */
        }
     }
     else if(ban[nc1][nc2].dir == LEFT){
	nc2 --; /* printf("%c %c ",'-',sequen2[nc2]); */
	if(sequen2[nc2] != GAPM){
	   out_gap_f = 0; break; /* no additional trace of arcs */
        }
     }
     else if(ban[nc1][nc2].dir == CROSS){
	nc1 --; nc2 --; /* printf("%c %c ",sequen[nc1],sequen2[nc2]); */
	if(sequen2[nc2] != GAPM){ out_gap_f = 0; break; }
	else if(sequen[nc1] != GAPM && sequen2[nc2] == GAPM)
	   { open_gap_f=0; /* printf("not open gap "); */ }
       /* else double gaps */ 
     }
     /* putchar('\n'); */
  } 

  for(i = nth2;i < s_len2;i ++)if(sequen2[i] != GAPM)break;
  if(i == s_len2){ out_gap_f = 1;/* printf("OUT GaP left\n");*/ }

  if(out_gap_f){
     if(open_gap_f)return gp.outgap;
     else return gp.outexgap;
  }
  if(open_gap_f)return gp.opengap;
  else return gp.extgap;

}

int uscore_calc_s(sequen,sequen2,s_len,s_len2,nth,nth2,ban,gp)
/* calculates additional score from arc above */
/*  ex. o (3,2)
        |
        o (4,2)
    state (4,2)
*/
char *sequen, *sequen2;
int s_len,s_len2;
int nth,nth2;
struct dpmatrix **ban;
struct gaparam gp;
{
  int i;
  if(sequen[nth - 1] == GAPM)return 0; /* double gap */
  else return gap_p_calc2(sequen,sequen2,s_len,s_len2,nth - 1,nth2,ban,gp);
}

int uscore_calc(agp1,agp2,nth,nth2,ban,gp)
/* calculates additional score from arc above for all the pairs */
struct amino_group *agp1, *agp2;
int nth,nth2;
struct dpmatrix **ban;
struct gaparam gp;
{
  int i,j,eval,sum;

  sum = 0;
  for(i = 0;i < agp1->seq_num;i ++)
    for(j = 0;j < agp2->seq_num;j ++){
       eval = uscore_calc_s(agp1->seque[i],agp2->seque[j],
			    agp1->seq_len,agp2->seq_len,nth,nth2,ban,gp);
       /* printf("score for %d and %d is %d\n",i,j,eval); */
       sum += eval;
    }
  return sum;
}

int sscore_calc_s(sequen,sequen2,s_len,s_len2,nth,nth2,ban,gp)
/* calculates additional score from arc left */
/* ex. o   ->   o
     (3,2)    (3,3)
   state (3,3)
*/
char *sequen, *sequen2;
int s_len,s_len2;
int nth,nth2;
struct dpmatrix **ban;
struct gaparam gp;
{
  if(sequen2[nth2 - 1] == GAPM)return 0; /* double gap */
  else return gap_p_calc1(sequen,sequen2,s_len,s_len2,nth,nth2 - 1,ban,gp);
} 

int sscore_calc(agp1,agp2,nth,nth2,ban,gp)
/* calculates additional score from arc left for all the pairs */
struct amino_group *agp1,*agp2;
int nth,nth2;
struct dpmatrix **ban;
struct gaparam gp;
{
  int i,j,eval,sum;

  sum = 0;
  for(i = 0;i < agp1->seq_num;i ++)
    for(j = 0;j < agp2->seq_num;j ++){
       eval = sscore_calc_s(agp1->seque[i],agp2->seque[j],
			    agp1->seq_len,agp2->seq_len,nth,nth2,ban,gp);
        /* printf("score for %d and %d is %d\n",i,j,eval); */ 
       sum += eval;
    }
  return sum;
}


int dscore_calc_s(sequen,sequen2,s_len,s_len2,nth,nth2,ban,gp,match)
/* calculates additional score from up-left */
/* ex. o (2,3)
        \ 
         o (3,4) 
   state (3,4)
*/
char *sequen, *sequen2;
int s_len,s_len2;
int nth,nth2;
struct dpmatrix **ban;
struct gaparam gp;
int match;
{
   if(sequen[nth - 1] != GAPM && sequen2[nth2 - 1] != GAPM){
     if(gp.addmatch == 0)
       return point(sequen[nth - 1],sequen2[nth2 - 1],match);
     else return addmat(sequen,sequen2,s_len,s_len2,nth,nth2,ban,gp,match);
   }

    
  /* dayhoff matrix */

   else if(sequen[nth - 1] == GAPM && sequen2[nth2 - 1] == GAPM)
      return 0;
   /* double gap */

   else if(sequen[nth - 1] == GAPM)
      return gap_p_calc1(sequen,sequen2,s_len,s_len2,nth - 1,nth2 - 1,ban,gp);

   else if(sequen2[nth2 - 1] == GAPM)
      return gap_p_calc2(sequen,sequen2,s_len,s_len2,nth - 1,nth2 - 1,ban,gp);
 }

int dscore_calc(agp1,agp2,nth,nth2,ban,gp,match)
/* calculates additional score from up-left for all the pairs */
struct amino_group *agp1,*agp2;
int nth,nth2;
struct dpmatrix **ban;
struct gaparam gp;
int match;
{
  int i,j,eval,sum;

  sum = 0;
  for(i = 0;i < agp1->seq_num;i ++)
    for(j = 0;j < agp2->seq_num;j ++){
       eval = dscore_calc_s(agp1->seque[i],agp2->seque[j],
			    agp1->seq_len,agp2->seq_len,nth,nth2,ban,
			    gp,match);
       /* printf("score for %d and %d is %d\n",i,j,eval); */
       sum += eval;
    }
  return sum;
}

int addmat(sequen,sequen2,s_len,s_len2,nth,nth2,ban,gp,match)
/* calculates additional score when previous letter also matches */
/* ex. o (2,3)
        \ 
         o (3,4) 
   state (3,4)
*/
char *sequen, *sequen2;
int s_len,s_len2;
int nth,nth2;
struct dpmatrix **ban;
struct gaparam gp;
int match;
{
  int i,j,addscore;
  i = nth; j = nth2;
/*
  printf("additional score calculation\n");
*/
  addscore = 0;
  
  if(sequen[i-1] != sequen2[j-1])
    return point(sequen[i - 1],sequen2[j - 1],match);

  i --;j --;
  while(i > 0 && j > 0){
/*
    printf("%dth of 0 is %c, %dth of 1 is %c, addscore = %d\n",
	   i,sequen[i],j,sequen2[j],addscore);
*/
    if(ban[i][j].dir == CROSS){
      if(sequen[i - 1] != sequen2[j - 1])break;
      else if(sequen[i - 1] != GAPM)addscore += gp.addmatch;
      i --;j --;
    }
    else if(ban[i][j].dir == UP){
      if(sequen[i - 1] != GAPM)break;
      i --;
    }
  
    else if(ban[i][j].dir == LEFT){
      if(sequen2[j - 1] != GAPM)break;
      j --;
    }
    else { 
      printf("Unexpected direction %d in (%d %d)\n",ban[i][j].dir,i,j); 
      print_ban(ban,s_len,s_len2);
      exit(1);
    }
  }
  return addscore + point(sequen[nth - 1],sequen2[nth2 - 1],match);
}




int g_align(agp1,agp2,result,res_length,gp,match)
/* do group pairwise dynamic programming
   memory for result must be allocated in the main routine */
/* score and length of result will be returned */
struct amino_group *agp1,*agp2;
char **result;
int *res_length;
struct gaparam gp;
int match;
{
   int count,i,j,k;
   int x,y;
   int uscore,sscore,dscore,ret_score;
   int rx,ry;
   struct dpmatrix **ban;

   ban = banmake(agp1->seq_len + 1,agp2->seq_len + 1);

   ban[0][0].score = 0;
   ban[0][0].dir = 0;

   for(i = 1;i <= agp1->seq_len; i++){
      ban[i][0].score = uscore_calc(agp1,agp2,i,0,ban,gp)
	+ ban[i-1][0].score;
      ban[i][0].dir = UP;
   } /* fills left side of ban(matrix arc board for DP) */

   for(j = 1;j <= agp2->seq_len; j++){
      ban[0][j].score = sscore_calc(agp1,agp2,0,j,ban,gp)
	+ ban[0][j-1].score;
      ban[0][j].dir = LEFT;
   } /* fills upper side of ban */

   for(i = 1;i <= agp1->seq_len;i ++)
      for(j = 1;j <= agp2->seq_len;j ++){
       
       uscore = uscore_calc(agp1,agp2,i,j,ban,gp) + ban[i-1][j].score;
       sscore = sscore_calc(agp1,agp2,i,j,ban,gp) + ban[i][j-1].score;
       dscore = dscore_calc(agp1,agp2,i,j,ban,gp,match)
	 + ban[i-1][j-1].score;
/*
       printf("in node (%d %d),up:%d left:%d cross:%d\n",i,j,uscore,sscore,
	       dscore);
*/

       switch(max3(uscore,sscore,dscore)){
	case 0:ban[i][j].score = uscore;
	       ban[i][j].dir = UP;break;
        case 1:ban[i][j].score = sscore;
	       ban[i][j].dir = LEFT;break;
        case 2:ban[i][j].score = dscore;
	       ban[i][j].dir = CROSS;break;
        default:printf("Error in arc direction\n");break;
       }
      } /* fills rest of ban */

/*  print_ban(ban,agp1->seq_len,agp2->seq_len); */

    if(result != NULL)*res_length = bantoresult(agp1,agp2,ban,result);

    ret_score = ban[agp1->seq_len][agp2->seq_len].score;
    banfree(ban);
    return(ret_score);
}

int bantoresult(agp1,agp2,ban,result)
/* put result by looking at ban */
/* result length will be returned */
/* memory for result must be allocated in the main */
struct amino_group *agp1,*agp2;
struct dpmatrix **ban;
char **result;
{
   int i,j,i2,j2;

   int nc1,nc2;
   int res_len; /* result length */
   int begin,end;
   char tmpchar;

   nc1 = agp1->seq_len;
   nc2 = agp2->seq_len;

   i2 = agp1->seq_num;
   res_len = 0;

   while(nc1 > 0 || nc2 > 0){
      switch(ban[nc1][nc2].dir){
       case UP:nc1 --; /* printf("UP"); */
               for(i = 0;i < agp1->seq_num;i ++)
	          result[i][res_len] = agp1->seque[i][nc1];
               for(i = i2;i < i2 + agp2->seq_num;i ++)
	          result[i][res_len] = GAPM;
               break;
       case LEFT:nc2 --; /* printf("LEFT"); */
	       for(i = 0;i < agp1->seq_num;i ++)
		  result[i][res_len] = GAPM;
               for(i = i2;i < i2 + agp2->seq_num;i ++)
		  result[i][res_len] = agp2->seque[i-i2][nc2];
               break;
       case CROSS:nc1 --;nc2 --; /* printf("CROSS"); */
	       for(i = 0;i < agp1->seq_num;i ++)
		  result[i][res_len] = agp1->seque[i][nc1];
               for(i = i2;i < i2 + agp2->seq_num;i ++)
		  result[i][res_len] = agp2->seque[i-i2][nc2];
               break;
      }
      res_len ++;
   }
   /* printf("result length %d\n",res_len); */

   for(begin = 0,end = res_len - 1;begin < end;begin ++,end --){
      for(i = 0;i < agp1->seq_num + agp2->seq_num;i ++){
	 tmpchar = result[i][begin];
         result[i][begin] = result[i][end];
	 result[i][end] = tmpchar;
      }
   } /* reverse */  
   return res_len;
}

print_ban(ban,t,y)
struct dpmatrix **ban;
{
   int i,j;

   printf("printing matrix(%d %d) score\n",t,y);
   for(i = 0;i <= t;i ++){
     for(j = 0;j <= y;j ++){
        printf("%3d",ban[i][j].score);
     }
     putchar('\n');
   }
   printf("printing matrix(%d %d) dir\n",t,y);
   for(i = 0;i <= t;i ++){
     for(j = 0;j <= y;j ++){
	printf("%3d",ban[i][j].dir);
     }
     putchar('\n');
   }
} 

agagapdel(ag) /* deletes all gap column */
struct amino_group *ag;
{
   char **seque;
   int i,j,k,m,n,p,q;
   int nagap;


   printf("before gap deletion\n");
   seqprint(ag->seque,ag->seq_num,ag->seq_len);

   nagap = 0;
   for(j = 0;j < ag->seq_len;j ++){
      for(i = 0;i < ag->seq_num;i ++)
	 if(ag->seque[i][j] != GAPM)break;
      if(i == ag->seq_num)nagap ++;
   }
   seque = seqmake(ag->seq_num,ag->seq_len - nagap);

   q = 0;
   for(j = 0;j < ag->seq_len;j ++){
      for(i = 0;i < ag->seq_num;i ++)
	 if(ag->seque[i][j] != GAPM)break;
      if(i < ag->seq_num){
	 for(p = 0;p < ag->seq_num;p ++)
	    seque[p][q] = ag->seque[p][j];
         q ++;
      }
   }
   seqfree(ag->seque);
   ag->seque = seque;
   ag->seq_len -= nagap;

   printf("after gap deletion\n");
   seqprint(ag->seque,ag->seq_num,ag->seq_len);
}





agallprint(ag,g_num) /* prints all the groups */
struct amino_group *ag;
int g_num;
{
   int i,j; 
   for(i = 0;i < g_num;i ++){
      printf("group %d:\n",i);
      printf("s_num = %d, s_len = %d\n",
	      ag[i].seq_num,ag[i].seq_len);
      seqprint(ag[i].seque,ag[i].seq_num,ag[i].seq_len);
   }
      

}

/* Alignment by tree base. */
int treeb(seque,s_num,s_len,result,res_len_ret,gp,match)
char **seque; /* sequences to align. indicated by main routine */
int s_num,s_len; /* number of sequences and length of sequences */
char ***result; /* address of result. 
		   memory will be allocated in this routine
		 */
int *res_len_ret; /* address to put the result of alignemt length */
struct gaparam gp; /* gap penalty */
int match; /* match = 1 match score will be applied instead of PAM250 */
{
   struct amino_group *amino_g;
   int g_num,result_len; 
   int score;
   int maxscore,max1,max2;
   int i,j,k,p,q,m,n;
   char **res_tmp;

   amino_g = convseqag(seque,s_num,s_len);
   agallprint(amino_g,s_num);
   g_num = s_num;

   while(g_num > 1){
      maxscore = -1000000;
      for(p = 0;p < g_num - 1;p ++)
	 for(q = p + 1;q < g_num;q ++){
	    score = g_align(&amino_g[p],&amino_g[q],NULL,
			    &result_len,gp,match);
	    score /= amino_g[p].seq_num * amino_g[q].seq_num; 
	    printf("score between group %d and %d is %d\n",
		    p,q,score);
            if(maxscore < score){
	       maxscore = score; max1 = p;max2 = q;
            }
         }
      printf("group %d and %d will be joined\n",max1,max2);
      res_tmp = seqmake(amino_g[max1].seq_num + amino_g[max2].seq_num,
			amino_g[max1].seq_len + amino_g[max2].seq_len);
      score = g_align(&amino_g[max1],&amino_g[max2],res_tmp,
		      &result_len,gp,match);
      score /= amino_g[max1].seq_num * amino_g[max2].seq_num;
      k = amino_g[max1].seq_num + amino_g[max2].seq_num;
      agfree(&amino_g[max1]); agfree(&amino_g[max2]);
      agmake(&amino_g[max1],res_tmp,k,result_len);
      agagapdel(&amino_g[max1]); 
      seqfree(res_tmp);
      if(max2 < g_num - 1)agchange(&amino_g[max2],&amino_g[g_num - 1]);
      g_num --;
      agallprint(amino_g,g_num);
   }
   *result = amino_g[0].seque;
   *res_len_ret = amino_g[0].seq_len;
/*   agfree(&amino_g[0]); */ /* CHECK !! this sequence has to be returned
                                to main routine */
   free(amino_g);
   return score;
}
         
test1(){
  struct amino_group amino_g[2];
  struct dpmatrix **ban;
  char **result;

  amino_g[0].seq_num = 1;
  amino_g[0].seq_len = 5;
  amino_g[0].seque = (char **)malloc(1 * sizeof(char *));
  amino_g[0].seque[0] = "ATTGC";

  amino_g[1].seq_num = 1;
  amino_g[1].seq_len = 4;
  amino_g[1].seque = (char **)malloc(1 * sizeof(char *));
  amino_g[1].seque[0] = "AT--";

  ban = banmake(amino_g[0].seq_len + 1,amino_g[1].seq_len + 1);

  ban[0][0].dir = 0;
  ban[1][1].dir = CROSS;
  ban[1][2].dir = LEFT;
  ban[2][2].dir = UP;
  ban[3][2].dir = UP;
  ban[4][2].dir = UP;
  ban[4][3].dir = LEFT;
  ban[5][4].dir = CROSS;

  printf("score %d\n",uscore_calc(&amino_g[0],&amino_g[1],4,2,ban));
  banfree(ban);
}

int pairscore_t(seque,s_num,s_len,gp,match)
char **seque;
int s_num,s_len;
struct gaparam gp;
int match;
{
  int i,j,each_score,total_score;
  total_score = 0;
  for(i = 0;i < s_num - 1;i ++)
    for(j = i+1;j < s_num;j ++){
      each_score = pairscore(seque[i],seque[j],s_len,gp,match);
/*
      printf("score between seq:%d and seq:%d is %d\n",
	     i,j,each_score);
*/
      total_score += each_score;
    }
/*
  printf("total score:%d\n",total_score);
*/
  return total_score;
}

int pairscore(seqn0,seqn1,s_len,gp,match)
char seqn0[],seqn1[];
int s_len;
struct gaparam gp;
int match;
{
  int i,j,score;
  int bout0,bout1;
  int eout0,eout1;
  int exflag0,exflag1;
  int addscore;

  bout0 = 1; bout1 = 1;
  eout0 = 0; eout1 = 0;
  exflag0 = 0; exflag1 = 0;
  addscore = 0;
  score = 0;

  for(i = 0;i < s_len;i ++){
/*   printf("score %d:%d\n",i-1,score); */
     if(seqn0[i] == GAPM && seqn1[i] == GAPM)continue;

     else if(seqn0[i] == GAPM){
       addscore = 0;
        bout1 = 0; exflag1 = 0;
        if(eout0 != 1){ /* right side out gap check */
	   for(j = i;j < s_len;j ++)if(seqn0[j] != GAPM)break;
	   if(j == s_len)eout0 = 1;
        }
        if(bout0 == 1 || eout0 == 1){ /* out gap score */
	   if(exflag0 == 1)score += gp.outexgap;
	   else score += gp.outgap;
        }
        else { /* gap score */
           if(exflag0 == 1)score += gp.extgap;
	   else score += gp.opengap;
        }
        exflag0 = 1;
     }
     else if(seqn1[i] == GAPM){
       addscore = 0;
        bout0 = 0; exflag0 = 0;
	if(eout1 != 1){ /* right side out gap check */
	   for(j = i;j < s_len;j ++)if(seqn1[j] != GAPM)break;
	   if(j == s_len)eout1 = 1;
        }
	if(bout1 == 1 || eout1 == 1){ /* out gap score */
	   if(exflag1 == 1)score += gp.outexgap;
	   else score += gp.outgap;
        }
	else { /* gap score */
	   if(exflag1 == 1)score += gp.extgap;
	   else score += gp.opengap;
        }
        exflag1 = 1;
     }

    else {
       bout0 = 0; exflag0 = 0;
       bout1 = 0; exflag1 = 0;

       score += point(seqn0[i],seqn1[i],match);
       if(seqn0[i] == seqn1[i]){
	 score += addscore;
	 addscore += gp.addmatch;
       }
       else addscore = 0;
/*
     printf("'%c':'%c' %d\n",seqn0[i],seqn1[i],
	      point(seqn0[i],seqn1[i],match));
*/
    }


  }
  return score;
}

trandseq(seque,s_num,s_len)
char **seque;
int s_num,s_len;
{
   int i,j,r;
   for(i = 0;i < s_num;i ++)
      for(j = 0;j < s_len;j ++){
	r = rand() % 10;
	switch(r){
	  case 0:seque[i][j] = 'A';break;
	  case 1:seque[i][j] = 'R';break;
	  case 2:seque[i][j] = 'N';break;
	  case 3:seque[i][j] = 'Q';break;
	  case 4:seque[i][j] = 'C';break;
	  case 5:seque[i][j] = 'M';break;
	  default:seque[i][j] ='-';break;
        }
      }
}



void treeb_main(argc,argv)
int argc;
char *argv[];
{
  char **seque;
  int s_num,s_len;
  char **result;
  int result_len;
  int score;

  if(argc == 2)seque = seqget(&s_num,&s_len,argv[1]);
  else seque = seqget(&s_num,&s_len,NULL);

  score = treeb(seque,s_num,s_len,&result,&result_len);
  printf("Final Result:%d\n",score);

  seqprint(result,s_num,result_len);
  pairscore_t(result,s_num,result_len);
  seqfree(result);
  seqfree(seque);

}



void main(argc,argv)
     int argc;
     char *argv[];
{
  int s_num,s_len;
  int n;
  char **seque;
  char **result;
  int result_len;

  struct param p; /* parameters and variables used as global */		     
  struct gaparam gp; /* gap penalties for SP */

  srand(time(NULL));
  comline_manage(argc,argv,&p); /* parameter set by command line */

  if(p.testmode == 1){
    testfunc8(&p);
    return;
  }

  gp.outgap = p.outgap; gp.outexgap =  p.outexgap;
  gp.opengap = p.opengap; gp.extgap = p.extgap;
  gp.addmatch = p.addmatch;
  
  seque = seqget(&s_num,&s_len,p.filename);
     /* get sequence data. seque will be top address of sequences */

  if(p.just_eval == 0){
    sleep(3);
    if(p.treeb == 1){
      printf("Tree base\n");
      treeb(seque,s_num,s_len,&result,&result_len,gp,p.match);
      seqfree(seque);
      seque = seqmake(s_num,result_len);
      seqcopy(result,seque,s_num,result_len);
      seqfree(result);
      s_len = result_len;
      printf("Tree base end\n");
    }

    simanneal(seque,s_num,s_len,&p);
    printf("\n\nRESULT:\n");
    seqprint(seque,s_num,s_len);
  }
  printf("score:%lf\n",evalseq(seque,s_num,s_len,&p));

}

/* when test mode is on,this function is called. */
testfunc(p)
struct param *p;
{
  int n;
  testfunc3(p);
  printf("This is a test.\n");
  printf("hot_t = %lf\n",p->hot_t);
  printf("cold_c = %d\n",p->cold_c);


  for(n = 0;n < 1000;n ++)
    printf("%d:%d\n",n,tcheck(0.1,n,p->hot_t,p->cold_c));

}
testfunc2(p)
struct param *p;
{
  int s_num,s_len;
  char **seque;

  seque = seqget(&s_num,&s_len,p->filename);
  seqgapright(seque,s_num,s_len);
  if(p->regsavec > 0)
    seqevalfile(seque,s_num,s_len,1234,"just a test",p);
}

testfunc3(p)
struct param *p;
{
  char **seque;
  double *counter;
  int s_num,s_len;

  counter = (double *)malloc((LETTNUM)*(LETTNUM) * sizeof(double));
  seque = seqget(&s_num,&s_len,p->filename);
     /* get sequence data. seque will be top address of sequences */

  printf("%lf\n",calc_two_lines4_2(seque,s_num,s_len,0,1,counter,p));

  free(counter);
  exit(0);
}

testfunc4(p)
struct param *p;
{
  int s_num,s_len;
  int n1,n2;
  char **seque;

  seque = seqget(&s_num,&s_len,p->filename);
  
  printf("column NO.1:");
  scanf("%d",&n1);
  printf("column NO.2:");
  scanf("%d",&n2);

  printf("all_gap_pair_check = %d\n",
	 all_gap_pair_check(seque,s_num,s_len,n1,n2));
}
testfunc6(p)
struct param *p;
{
  int s_num,s_len;
  char **seque;

  seque = seqget(&s_num,&s_len,p->filename);

  printf("Number of all gap columns are : %d\n",
              count_all_gap_column(seque,s_num,s_len));
}

testfunc7(p)
struct param *p;
{
  char s[20];
  int i,j,ct;

  for(ct = 0;ct < 5;ct ++){
    printf("input characters:");
    scanf("%s",s);
    printf("This is %d\n",strtoint(s));
  }
}

testfunc8(p)
struct param *p;
{
  int s_num = 2,s_len = 20,result_len,score,rscore;
  int n;
  struct gaparam gp;
  char **seque,**result;
  
  seque = seqmake(s_num,s_len);
  gp.outgap = p->outgap; gp.outexgap =  p->outexgap;
  gp.opengap = p->opengap; gp.extgap = p->extgap;
  gp.addmatch = p->addmatch;

  for(n = 0;n < 1000;n ++){
    trandseq(seque,s_num,s_len);
    printf("Sequences:\n");
    seqprint(seque,s_num,s_len);
    score = treeb(seque,s_num,s_len,&result,&result_len,gp,p->match);
    printf("Alignment is done.\n");
    rscore = pairscore_t(result,s_num,result_len,gp,p->match);
    printf("\nAligned\n");
    seqprint(result,s_num,result_len);
    seqfree(result);
    printf("Pairwise Alignment Score:%d\n",score);
    printf("Score recalculated:%d\n",rscore);
    if(score != rscore)break;
  }

}


simanneal(seque,s_num,s_len,p) 
     /* do alignment to sequences in seque and put results in it */

char **seque;
int s_num,s_len;
struct param *p;
{
  int nth,num;
  int ipos;
  int n,s;
  int counter = 0;
  double oldscore,newscore,oldscore1,newscore1,diff;

  char **seque2; /* put aligned sequence in here */
  seque2 = seqmake(s_num,s_len);

  counter = p->count_start; /* reprogram!! */
  if(p->regsavec != 0)
    seqevalfile(seque,s_num,s_len,counter,"initial state.....",p);

  while(counter < p->count_max){

    newstate(seque,seque2,s_num,s_len,p);
    oldscore = evalseq(seque,s_num,s_len,p);  oldscore1 = p->score1;
    newscore = evalseq(seque2,s_num,s_len,p); newscore1 = p->score1;
    diff = newscore - oldscore;

    if(diff > 0 || (diff == 0 && p->evthre == 0)
       || (diff == 0 && newscore1 >= oldscore1)){
      seqcopy(seque2,seque,s_num,s_len);
      if(p->printpro == 1){ /* prints aligning process */
	seqprint(seque,s_num,s_len);
	printf("counter:%d score:%lf\n\n",counter,newscore);

      }
    } /* if the evaluation value gets better, take the new state */

    else if(tcheck(diff*-1,counter,p->hot_t,p->cold_c) == 1 
		   && p->evthre == 0){
      seqcopy(seque2,seque,s_num,s_len);
      if(p->printpro == 1)printf("counter:%d score:%lf\n\n",counter,newscore); 
    }  /* even if the evaluation gets worse,take the new state as allowed */

    else if(tcheck((newscore1-oldscore1)*-1,counter,p->hot_t,p->cold_c) == 1 
		   && p->evthre != 0){ /* check!!! */
      seqcopy(seque2,seque,s_num,s_len);
      if(p->printpro == 1)printf("counter:%d score:%lf\n\n",counter,newscore); 
    }  /* even if the evaluation gets worse,take the new state as allowed */

    counter ++; /* counter will be counted as soon as the next state is
                   determined */
    if(p->regsavec >= 1 && (counter % p->regsavec) == 0)
      seqevalfile(seque,s_num,s_len,counter,"still aligning.....",p);

/*
  for(n = 0;n < s_num;n ++)printf("%d ",seqsel[n]);
  putchar('\n');
  printf("%d %d %d\n",nth,num,ipos);
  seqprint(seque2,s_num,s_len);

  getchar();
*/
  }
  if(p->regsavec >= 1 && (counter % p->regsavec) == 0)
    seqevalfile(seque,s_num,s_len,counter,"alignment finished.",p);
  seqfree(seque2);
}

int tcheck(mdiff,counter,hot_t,cold_c) /* calculation of the permitting range
					  (temperature schedule)
					  if evaluation gets worse than 
					  permitting range,return 0,
					  otherwise return 1 */
double mdiff;
int counter;
double hot_t;
int cold_c;
{
  double t; /* temperature */
  double r; /* random number ( 0 - 1.0 ) */

  if(counter >= cold_c)return 0;

  t = -1.0*hot_t / cold_c * counter + hot_t;
  r = rand()%1000*1.0 / 1000;
/*
  printf("%d: permit possibility:%lf random num:%lf\n",
	 counter,exp(-1.0*mdiff / t),r);
*/
  if(exp(-1.0*mdiff / t) > r)return 1;
  else return 0;

}

seqcopy(seque,seque2,s_num,s_len)
char **seque,**seque2;
int s_num,s_len;
{
  int n,s;
  for(n = 0;n < s_num;n ++)
    for(s = 0;s < s_len;s ++) 
      seque2[n][s] = seque[n][s]; 
}

seqselect(seque,seqsel,s_num,s_len,nth,num,p) 
     /* select sequence to do operation,gaps to move, and number of gaps
	seque:sequences   As a return value seqsel:seqsel sequences selected
	nth: nth of a sequence is selected as a moving gap
	num: number of gaps to move */
char **seque;
int *seqsel;
int s_num,s_len;
int *nth,*num;
struct param *p;
{
  int fselseq,fnth,fnth_first,gapct=0; /* gapct is ADDITIONAL gaps */
  int n,s;
  
  for(n = 0;n < s_num;n++)seqsel[n] = 0;
  fselseq = rand() % s_num ; /* first sequence selected to be operated */
  fnth = rand() % s_len; /* select location */
  fnth_first = fnth; /* first location selected. This variable is used 
		      to recognise the sequence has no gap */

/* move pointing location to gap */
  while(seque[fselseq][fnth] != GAPM){ 
    fnth ++;
    if(fnth >= s_len)fnth = 0;
    if(fnth == fnth_first){ /* if no gap is found,go for next sequence */
      fselseq ++;
      if(fselseq >= s_num)fselseq = 0;
    }
  }

  while(fnth + gapct + 1 < s_len 
	&& seque[fselseq][fnth + gapct + 1] == GAPM
	&& rand() % 100 < p->gapper)gapct ++; /* additional gaps */

  for(n = 0;n < s_num;n ++){
    for(s = fnth;s <= fnth + gapct;s ++){
      if(seque[n][s] != GAPM)break;
      else if(s == fnth + gapct)seqsel[n] = 1;
    }
  }

/* throw out some sequences selected by using two different argorithms 
 switched randomly */
  if(rand() % 100 < p->easeq){
    /* RS_AND argorithm */
    for(n = 0;n < s_num;n ++)
      if(seqsel[n] == 1 && rand() % 100 >= p->sper)seqsel[n] = 0;
    seqsel[fselseq] = 1; /* sequence selected at first is saved */
    /* printf("argorithm 1 used.\n"); */
  } /* throws out each by certain probability */
  else { 
    seqsel_sub(seqsel,s_num);
    /* printf("argorithm 2 used.\n"); */
  } /* throws out by number of sequences with same probability */

  *nth = fnth;
  *num = gapct + 1;

/* printf("%d gaps are chosen.\n",*num); */
}

seqsel_sub(seqsel,s_num)
/* takes away some selected sequences.
   ex. probability of dropping 1 sequence and 5 sequences are equal.
   seqsel[n] = 1 -> sequence n is selected for alignment */
int *seqsel;
int s_num;
{
  int n,onect,r,rt,nseqdrop; /* nseqdrop is number of sequences to take away */
  onect = 0;

/* onect will be the number of 1s exist in seqsel */
  for(n = 0;n < s_num;n ++)if(seqsel[n] == 1)onect ++;
  if(onect <= 1)return;
/*
  for(n = 0;n < s_num;n ++)printf("%d ",seqsel[n]);
  putchar('\n');
*/
  nseqdrop = rand() % onect;
/*  printf("%d sequences to drop.\n",nseqdrop); */

  while(nseqdrop > 0){
    rt = r = rand() % onect; /* select 1 that will be searched(r_th 1). */

 /* moves pointer to r_th of 1 */
    n = 0;
    while(r >= 0){  /* counts 1 r+1 times */
      while(seqsel[n] != 1)n ++;
      r --;
      n ++; /* get ready to find another 1 */
    }
    n --;  /* restore to position that has 1 */

/*    printf("random num:%d -> %dth.\n",rt,n); */
    seqsel[n] = 0;
    onect --;
    nseqdrop --;
  }
/*
  for(n = 0;n < s_num;n ++)printf("%d ",seqsel[n]);
  putchar('\n');
*/
}

int seqscore(seque,s_num,s_len) /* returns the score */
char **seque;
int s_num,s_len;
{
  int n1,n2,s;
  int score = 0;
  for(s = 0;s < s_len;s ++)
    for(n1 = 0;n1 < s_num -1;n1 ++)
      for(n2 = n1 + 1;n2 < s_num;n2 ++)
	if(seque[n1][s] == seque[n2][s] &&
	   seque[n1][s] != GAPM)score ++;
  return score;
}
char **seqget(s_num,s_len,filename)
     /* reads sequences from standard input(filename = NULL) or file,
	arrange them into array sequences,and put the top address of 
	that to seque,and s_num will be number of  sequences,and s_len 
	will be length of sequences including gaps */

/* data -> buffer -> temp -> sequences 
   buffer stores any length(?) of data
   temp has only acceptable characters like alphabet and GAPM
   gap is inserted in sequences so that length of all sequences would
   be equal */

int *s_num,*s_len;
char *filename;
{
  int topn,topfn,maxc,ccnt,spoint,n,comment;
  char c,*buffer,*temp;
  int nbuffer; /* size of buffer excluding EOF */
  int bufp = 0; /* points inside buffer */
  char *sequeget; /* variable for putting the top address of arranged
		     sequences */

  char **seque2d; /* points memory table of each sequences' top addresses
		     it will look like 2-D array */

  ndataread(&buffer,&nbuffer,filename); 
     /* reads data including sequence data */
  temp = (char *)malloc((nbuffer + 1) * sizeof(char));
     /* +1 is for EOF code */
 
  /* allocates memory for reading */
  maxc = 0;    /* variable for keeping the length of the longest line */
  topn = 0;    /* counts how many head of a line(i.e. number of sequences)
		  if not a character,it is not assumed to be a head of line */
  topfn = 0;   /* this will be one if the head of a line is already read */
  ccnt = 0;    /* letter counter for each line */
  spoint = 0;  /* place to save sequences */
  comment = 0; /* flag:if 1,the rest to end of line will be comment. */

  while((c = buffer[bufp ++]) != EOF){
    if(((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == GAPM)
       && comment == 0){
         /* accepts only specific characters */
      if(topfn == 0){ topfn = 1; topn ++ ;} /* if head of line(sequence) is 
					       found,it is assumed as a 
					       sequence, and starts counting 
					       */
      ccnt ++;
      temp[spoint] = c;spoint ++;
    }
    if(c == '\n'){
      if(topfn == 1){ temp[spoint] = c;spoint ++; }
         /* if no head of a line was found, no carriage return */
      topfn = 0;
      if(ccnt > maxc)maxc = ccnt;
      ccnt = 0;
      comment = 0;
    }
    if(c == REMM)comment = 1;
  }
  temp[spoint] = EOF;
  free(buffer);

  printf("\nSequence input is as follows:\n");
  for(n = 0;n < spoint; n++)putchar(temp[n]);
  printf("line :%d longest :%d\n\n",topn,maxc);

  temptoseq(temp,&sequeget,topn,maxc); /* needs '&' for want of a return of
					  address into sequeget */

  seque2d = (char **)malloc(topn * sizeof(char *));
       /* allocates memory for table of top addresses of sequences */
  
  for(n = 0;n < topn;n ++)seque2d[n] = &sequeget[n * maxc];
       /* makes table */
				       
  printf("GAP is inserted as follow:\n");
  seqprint_1D(sequeget,topn,maxc);
  putchar('\n');
  free(temp);

  *s_num = topn;
  *s_len = maxc;

  return seque2d;
}


temptoseq(temp,seque,s_num,s_len) 
     /* converts letter sequences to sequences for alignment and put result
	into memory allocated.  The pointer to allocated memory is put into
	*seque. LETTER SEQUENCES AS A INPUT TO THIS FUNCTION MUST BE IN A
	PROPER FORMAT */
char *temp,**seque;
int s_num,s_len;
{
  int n,s;
  int spoint;
  char c;
  *seque = (char *)malloc(s_num*s_len * sizeof(char));

  n = 0;s = 0;spoint = 0;

  while(n < s_num){
    c = temp[spoint];
    if(c == '\n' || c == EOF){
      for( ;s < s_len;s ++)(*seque)[n*s_len+s] = GAPM;
      n ++; s = 0;
    }
    else {
      (*seque)[n*s_len+s] = c;
      s ++;
    }
    spoint ++;
  }
}

/* reads data of any length(?) and allocate buffer and put data into 
   buffer and total number of data is returned to *total (excluding EOF) */
ndataread(buffer,total,filename)
char **buffer;
int *total;
char *filename;
{
  struct datachain head;
  struct datachain *current,*tmp;

  FILE *fp;

  char c;
  int count; /* counts number of data in array */

  *total = 0; /* counts total data */

  if(filename != NULL){
    fp = fopen(filename,"r");
    if(fp == NULL){
      fprintf(stderr,"file \"%s\" not found\n",filename);
      exit(1);
    }
  } /* opens file for data input */

  current = &head;
  count = 0;
  while((c = ((filename == NULL) ? getchar() : fgetc(fp))) != EOF){
    *total += 1;
    current->cdata[count ++] = c;
    if(count >= ONECHAIN){
      current->ndata = count;
      current->next = (struct datachain *)malloc(sizeof(struct datachain));
/*    printf("memory allocated.\n"); */
      count = 0; current = current->next;
    }
  }
  if(filename != NULL)fclose(fp);

  current->ndata = count;
  current->next = NULL;

/*  printdatachain(&head); 
    printf("total character:%d\n",*total); */

  dchaintoar(&head,*total,buffer);
     /* converts data chain to array.
	Top address of array will be written into *buffer */

  if(head.next != NULL){
    current = head.next;
    while(current->next != NULL){
      tmp = current->next;
      free(current);/* printf("memory released.\n"); */
      current = tmp;
    }
    free(current); /* printf("memory released.\n"); */
  }
}

printdatachain(head)
struct datachain *head;
{
  struct datachain *current;
  int n;

  current = head;
  while(current->next != NULL){
    printf("number of character:%d\n",current->ndata);
    for(n = 0;n < current->ndata;n ++)putchar(current->cdata[n]);
    putchar('\n');
    current = current->next;
  }
  printf("number of character:%d\n",current->ndata);
  for(n = 0;n < current->ndata;n ++)putchar(current->cdata[n]);
  putchar('\n');
}

/* converts data chain to array 
   top address of array will be written into *buffer */
dchaintoar(head,total,buffer)
struct datachain *head;
int total; /* total number of characters */
char **buffer;
{
  struct datachain *current;
  int ct = 0;
  int n;
  *buffer = (char *)malloc((total + 1)*sizeof(char));
     /* 1 added for EOF */

  current = head;
  while(current->next != NULL){
    for(n = 0;n < current->ndata;n ++)(*buffer)[ct ++] = current->cdata[n];
    current = current->next;
  }
  for(n = 0;n < current->ndata;n ++)(*buffer)[ct ++] = current->cdata[n];
  (*buffer)[ct ++] = EOF;
}

seqprint_1D(seque,s_num,s_len) /* prints sequences */
char *seque;
int s_num,s_len;
{
  int n,s;
  for(n = 0;n < s_num;n ++){
    for(s = 0;s < s_len;s ++)
      putchar(seque[n*s_len+s]);
    putchar('\n');
  }
}

seqprint(seque,s_num,s_len)
char **seque;
int s_num,s_len;
{
  int n,s;
/*
  printf("Sequences table information\n");
  printf("Table start from:%p\n",seque);
  for(n = 0;n < s_num;n ++)
    printf("table %d:%p\n",n,seque[n]);
*/
  for(n = 0;n < s_num;n ++){
    for(s = 0;s < s_len;s ++)
      putchar(seque[n][s]);
    putchar('\n');
  }
}

seqfree(seque) /* releases memory for sequences */
char **seque;
{
/*  printf("releasing table:%p\n",seque[0]); */
  free(seque[0]);
/*  printf("releasing sequences:%p\n",seque); */
  free(seque);
}

char **seqmake(s_num,s_len) /* allocates memory for sequences */
int s_num,s_len;
{
  char *seque,**seque_table;
  int n;

  if((seque = (char *)malloc(s_num * s_len * sizeof(char))) == NULL)
    printf("memory allocation failed\n");

  if((seque_table = (char **)malloc(s_num * sizeof(char *))) == NULL)
    printf("memory allocation failed\n");

  for(n = 0;n < s_num;n ++)
    seque_table[n] = &seque[n * s_len];

  return seque_table;
}


seqgapshift(sequen,sequen2,s_num,s_len,pickloc,n,putloc)
 /* pick n elements from the picklocth letter of the sequence sequen
    and shift to cover space made, and put those elements into putloc.
    Result will be put in sequen2 */

 /* ex.sequence: ABCDEFG   pickloc=4 n=2 putloc=1
                  |  ^^
           ----> AEFBCDG
	        (0123456)
 */
 /* sequen and sequen2 MUST NOT BE THE SAME ADDRESS 
    Also watch for the overlap of sequence */

char *sequen,*sequen2;
int s_num,s_len;
int pickloc,putloc,n;
{
  int p,p2;

  if(putloc < pickloc){  /* move forward */
    for(p = 0,p2 = 0;p < putloc;p++,p2++)sequen2[p] = sequen[p2];
    for(p2 = pickloc;p2 < pickloc + n;p++,p2 ++)
      sequen2[p] = sequen[p2];
    for(p2 = putloc ;p2 < pickloc ;p++,p2++)sequen2[p] = sequen[p2];
    for(p2 = pickloc+n;p2 < s_len;p++,p2++)sequen2[p] = sequen[p2];
  }
  else if(pickloc+n <= putloc){ /* move backward */
    for(p = 0,p2 = 0;p < pickloc;p++,p2++)sequen2[p] = sequen[p2];
    for(p2 = pickloc + n;p2 <= putloc;p ++,p2 ++)sequen2[p] = sequen[p2];
    for(p2 = pickloc;p2 < pickloc +n;p ++,p2 ++)sequen2[p] = sequen[p2];
    for(p2 = putloc + 1;p2 < s_len;p++, p2++)sequen2[p] = sequen[p2];
  }
  else printf("gap shift error...\n");
}


/* evaluate sequences alignment 
1.All the character is available. 
2.Gap is considered as one of the letter.
3.Mutual information and joint entropy will not be recorded to be displayed.
*/
double mincal(seque,s_num,s_len,p)
char **seque;
int s_num,s_len;
struct param *p;
{
  int *pflag; /* flags that indicate corresponding character has already
		 counted */
  double *entscore; /* saves score for each column */

  double i1,i2,i1_2;
  int n,n1,n2;
  double score = 0;

  pflag = (int *)malloc(s_num * sizeof(int)); /* memory is allocated here
						 to make allocation 
						 only once */
  entscore = (double *)malloc(s_len * sizeof(double));

  for(n = 0;n < s_len;n ++)
    entscore[n] = calc_one_line(seque,s_num,s_len,n,pflag);

  if(p->ent_disp == 1){
    for(n = 0,score = 0;n < s_len;n ++){
      score += entscore[n];
      printf("entropy of column %3d is %lf\n",n,entscore[n]);
    }
    printf("Sum of entropy of each column is %lf.\n",score);
  }

  score = 0;
  for(n1 = 0;n1 < s_len-1;n1 ++)
    for(n2 = n1+1;n2 < s_len;n2 ++){
      i1 = entscore[n1];
      i2 = entscore[n2];
      i1_2 = calc_two_lines(seque,s_num,s_len,n1,n2,pflag);
      score += evform(i1,i2,i1_2,p);
/*    printf("%d %d %lf : %lf : %lf : %lf\n",n1,n2,i1,i2,i1_2,i1+i2-i1_2); */
    }
  free(pflag);
  free(entscore);
  return(score);
}

/* calculate entropy 
1.All the character is available.
2.Gap is considered as one of the letter.
*/
double calc_one_line(seque,s_num,s_len,nth,pflag)
  char **seque;
  int s_num,s_len,nth;
  int *pflag;
{
  int n,s;
  int ct; /* counts same character */
  double ctts = 0;
  char srchfor; /* character to search for */

  for(n = 0;n < s_num;n ++)pflag[n] = 0;

  for(n = 0;n < s_num;n ++){
    if(pflag[n] == 1)continue; /* if this character is already counted just
				  go for next */
    ct = 0;
    srchfor = seque[n][nth];
    for(s = n;s < s_num ;s ++){
      if(seque[s][nth] == srchfor){
	ct ++;
	pflag[s] = 1;
      }
    }
/*    printf("%d: ",ct); */
    ctts += -1.0*ct/s_num*log(1.0*ct/s_num)/log(2.0);
  }
  return(ctts);
}

/* calculate joint entropy
1.All the character is available.
2.Gap is considered as one of the letter.
*/
double calc_two_lines(seque,s_num,s_len,nth1,nth2,pflag)
  char **seque;
  int s_num,s_len,nth1,nth2;
  int *pflag;
{
  int n,s;
  int ct; /* counts same character */
  double ctts = 0;
  char srchfor1,srchfor2; /* character to search for */

  for(n = 0;n < s_num;n ++)pflag[n] = 0;

  for(n = 0;n < s_num;n ++){
    if(pflag[n] == 1)continue; /* if this character is already counted just
				  go for next */
    ct = 0;
    srchfor1 = seque[n][nth1];
    srchfor2 = seque[n][nth2];
    for(s = n;s < s_num ;s ++){
      if(seque[s][nth1] == srchfor1 &&
	 seque[s][nth2] == srchfor2){
	ct ++;
	pflag[s] = 1;
      }
    }
/*    printf("%d: ",ct);  */
    ctts += -1.0*ct/s_num*log(1.0*ct/s_num)/log(2.0); /* log(2) = 0.69314718 */
  }
  return(ctts);
}


/* calculate entropy
1.NUCLEIC ACID ONLY
2.Gap is considered as one of the letter.
*/
double calc_one_line2(seque,s_num,s_len,nth,counter,p)
  char **seque;
  int s_num,s_len,nth;
  int counter[]; /* counter for each letter */
  struct param *p;
{
  int n;
  int lettnth;
  double ctts = 0;

  for(n = 0;n < LETTNUM;n ++)counter[n] = 0; /* clears counter */

  for(n = 0;n < s_num;n ++){
    lettnth = lettctmap(seque[n][nth]);
    counter[lettnth] ++; /* adds corresponding counter */
  }

  for(n = 0;n < LETTNUM;n ++) /* calculates entropy for nth column */
    if(counter[n] != 0)
      ctts += -1.0*counter[n]/s_num*log(1.0*counter[n]/s_num)/log(2.0);
/*
  for(n = 0;n < LETTNUM;n ++)
    printf("%2d::",counter[n]);
  putchar('\n');
*/
  return(ctts);
}

/* calculate joint entropy
1.NUCLEIC ACID ONLY
2.Gap is considered as one of the letter
*/
double calc_two_lines2(seque,s_num,s_len,nth1,nth2,counter,p)
  char **seque;
  int s_num,s_len,nth1,nth2;
  int counter[LETTNUM][LETTNUM];
  struct param *p;
{
  int n,s;
  double ctts = 0;
  double var1;
  double t;
  int lettnth1,lettnth2;
  int s_num2; /* number of non double gap */
  
  s_num2 = s_num;

  for(s = 0;s < LETTNUM;s ++)
    for(n = 0;n < LETTNUM;n ++)counter[n][s] = 0; /* clears counter */

  for(n = 0;n < s_num;n ++){
    lettnth1 = lettctmap(seque[n][nth1]);
    lettnth2 = lettctmap(seque[n][nth2]);
    if(p->itgap != 1 || seque[n][nth1] != GAPM || seque[n][nth2] != GAPM)
      counter[lettnth1][lettnth2] ++;
    else s_num2 --;
  } /* adds corresponding counter */
/*
  printf("[%d,%d]:rgap:%d s_num2:%d\n",nth1,nth2,p->rgap,s_num2);
*/
/* checks for validity of joint entropy and if invalid, return 9999.0 */
  if(p->rgap == 0){
    if(s_num2 != 0 && 1.0*s_num2/s_num < p->twogt)return 9999.0;
    else {
      if(s_num2 == 0 && p->gapcol == 1)return 0.0;
      if(s_num2 == 0 && p->gapcol == 0)
	return log(LETTNUM * LETTNUM * 1.0)/log(2.0);
    }
  }

 if(p->rgap == 1 && s_num2 == 0){
  /*  printf("all gap:%d,%d\n",nth1,nth2); */ return 0.0;
 }

/* calculates joint entropy from ratio */
  for(s = 0;s < LETTNUM;s ++)
    for(n = 0;n < LETTNUM;n ++)
      if(counter[n][s] != 0)
	ctts += -1.0*counter[n][s]/s_num2*log(1.0*counter[n][s]/s_num2)
	  /log(2.0);
/*
  for(n = 0;n < LETTNUM;n ++)
    for(s = 0;s < LETTNUM;s ++)
      printf("%2d::",counter[n][s]);

  putchar('\n');
*/
  
  if(p->rgap == 1){
    t = ( log(1.0 * LETTNUM * LETTNUM)/log(2.0) );
    ctts = (ctts - t) * s_num2 / s_num + t;
  }

  if(p->jep > 0){
/*
    printf("%lf - %lf = %lf\n",log(1.0 * LETTNUM * LETTNUM)/log(2.0),
	   ctts,log(1.0 * LETTNUM * LETTNUM)/log(2.0) - ctts);
*/
    return(-pow(log(1.0 * LETTNUM * LETTNUM)/log(2.0)-ctts,
		1.0*p->jep)); 
       /* score will be positive in future(max 4 on ATGC) */
  }
  else if(p->sigmoid == 1){
    var1 = log(1.0 * LETTNUM * LETTNUM)/log(2.0) - ctts;
    return(-1.0 / 
           (1.0/p->siglim + pow(1.0/exp(1.0),(var1 - p->sigp)*p->sigm))
          );
  }
  else return(ctts);
}

/* gap is treated as all the letters available */
double calc_one_line4(seque,s_num,s_len,nth,counter,p)
  char **seque;
  int s_num,s_len,nth;
  double counter[]; /* counter for each letter. This must be allocated in
		     mother function */
  struct param *p;   
{
  int n,s;
  int lettnth;
  double ctts = 0;



  for(n = 0;n < LETTNUM_4;n ++)counter[n] = 0; /* clears counter */

  for(n = 0;n < s_num;n ++){
    lettnth = lettctmap2(seque[n][nth]);
    if(lettnth != 1000) /* gap code is 1000 */
      counter[lettnth] += 1; /* adds corresponding counter */
    else for(s = 0;s < LETTNUM_4;s ++)counter[s] += 1.0/LETTNUM_4;
  }

  for(n = 0;n < LETTNUM_4;n ++) /* calculates entropy for nth column */
    if(counter[n] != 0)
      ctts += -1.0*counter[n]/s_num*log(1.0*counter[n]/s_num)/log(2.0);
/*
  printf("outputting counter for column %d...\n",nth);
  for(n = 0;n < LETTNUM_4;n ++)printf("%lf::",counter[n]);
  putchar('\n');
*/

  return(ctts);
}


/* joint entropy 
   gap is considered as all the possible letters */
double calc_two_lines4(seque,s_num,s_len,nth1,nth2,counter,p)
  char **seque;
  int s_num,s_len,nth1,nth2;
  double counter[LETTNUM_4][LETTNUM_4];
  struct param *p;
{
  int n,s,t;
  double ctts = 0;
  int lettnth1,lettnth2;
  int ngap = 0; /* counts number of gaps pairs */

  if(p->gapcol == 1){
    for(n = 0;n < s_num;n ++)if(seque[n][nth1] != GAPM)break;
    if(n == s_num)
      for(s = 0;s < s_num;s ++)if(seque[s][nth2] != GAPM)break;
    if(n + s == s_num * 2)return 0.0;
  } /* if two column is filled with gaps,0 may be returned */

  for(s = 0;s < LETTNUM_4;s ++)
    for(n = 0;n < LETTNUM_4;n ++)counter[n][s] = 0; /* clears counter */

  for(n = 0;n < s_num;n ++){
    lettnth1 = lettctmap2(seque[n][nth1]);
    lettnth2 = lettctmap2(seque[n][nth2]);
/* checks for gap (code 1000) */
    if(lettnth1 != 1000 && lettnth2 != 1000)
      counter[lettnth1][lettnth2] += 1.0;
    else if(lettnth1 == 1000 && lettnth2 != 1000)
      for(s = 0;s < LETTNUM_4;s ++)counter[s][lettnth2] += 1.0/LETTNUM_4;
    else if(lettnth1 != 1000 && lettnth2 == 1000)
      for(t = 0;t < LETTNUM_4;t ++)counter[lettnth1][t] += 1.0/LETTNUM_4;
    else if(lettnth1 == 1000 && lettnth2 == 1000)
      for(s = 0;s < LETTNUM_4;s ++)
	for(t = 0;t < LETTNUM_4;t ++)
	  { counter[s][t] += 1.0/LETTNUM_4/LETTNUM_4; ngap ++; }
  } /* adds corresponding counter */

  if(p->gapcol == 1 && ngap == s_num)return 0;
    /* if gapcol = 1 and gap is filled, return 0 */

/* calculates joint entropy from ratio */
  for(s = 0;s < LETTNUM_4;s ++)
    for(n = 0;n < LETTNUM_4;n ++)
      if(counter[n][s] != 0)
	ctts += -1.0*counter[n][s]/s_num*log(1.0*counter[n][s]/s_num)/log(2.0);
/*
  for(n = 0;n < LETTNUM_4;n ++)
    for(s = 0;s < LETTNUM_4;s ++)
      printf("%lf::",counter[n][s]);
  putchar('\n');
*/

  return(ctts);
}


/* joint entropy 
   gap is considered as all the possible letters,
   but two gap pair is assumed as if it does not exist */
double calc_two_lines4_1(seque,s_num,s_len,nth1,nth2,counter,p)
  char **seque;
  int s_num,s_len,nth1,nth2;
  double counter[LETTNUM_4][LETTNUM_4];
  struct param *p;
{
  int n,s,t;
  double ctts = 0;
  double var1;

  int lettnth1,lettnth2;
  int s_num2; /* counts valid sequence pair(two gap pair is invalid */

  for(s = 0;s < LETTNUM_4;s ++)
    for(n = 0;n < LETTNUM_4;n ++)counter[n][s] = 0; /* clears counter */

  s_num2 = s_num;
  for(n = 0;n < s_num;n ++){
    lettnth1 = lettctmap2(seque[n][nth1]);
    lettnth2 = lettctmap2(seque[n][nth2]);
/* checks for gap (code 1000) */
    if(lettnth1 != 1000 && lettnth2 != 1000)
      counter[lettnth1][lettnth2] += 1.0;
    else if(lettnth1 == 1000 && lettnth2 != 1000)
      for(s = 0;s < LETTNUM_4;s ++)counter[s][lettnth2] += 1.0/LETTNUM_4;
    else if(lettnth1 != 1000 && lettnth2 == 1000)
      for(t = 0;t < LETTNUM_4;t ++)counter[lettnth1][t] += 1.0/LETTNUM_4;
    else if(lettnth1 == 1000 && lettnth2 == 1000)
      s_num2 --; /* keep it for validity */
  } /* adds corresponding counter */

/* checks for validity of joint entropy and if invalid, return 9999.0 */
  if(p->rgap == 0){
    if(s_num2 != 0 && 1.0*s_num2/s_num < p->twogt)return 9999.0;
    else {
      if(s_num2 == 0 && p->gapcol == 1)return 0.0;
      if(s_num2 == 0 && p->gapcol == 0)
	return log(LETTNUM_4 * LETTNUM_4 * 1.0)/log(2.0);
    }
  }

 if(p->rgap == 1 && s_num2 == 0)return 0.0;

/* calculates joint entropy from ratio */
  for(s = 0;s < LETTNUM_4;s ++)
    for(n = 0;n < LETTNUM_4;n ++)
      if(counter[n][s] != 0)
	ctts += -1.0*counter[n][s]/s_num2*log(1.0*counter[n][s]/s_num2)
	  /log(2.0);
/*
  for(n = 0;n < LETTNUM_4;n ++)
    for(s = 0;s < LETTNUM_4;s ++)
      printf("%lf::",counter[n][s]);
  putchar('\n');
*/
  if(p->rgap == 1){
    t = (int)( log(1.0 * LETTNUM_4 * LETTNUM_4)/log(2.0) );
    ctts = (ctts - t) * s_num2 / s_num + t;
  }

  if(p->jep > 0){
/*
    printf("%lf - %lf = %lf\n",log(1.0 * LETTNUM_4 * LETTNUM_4)/log(2.0),
	   ctts,log(1.0 * LETTNUM_4 * LETTNUM_4)/log(2.0) - ctts);
*/
    return(-pow(log(1.0 * LETTNUM_4 * LETTNUM_4)/log(2.0)-ctts,
		1.0*p->jep)); 
       /* score will be positive in future(max 4 on ATGC) */
  }
  else if(p->sigmoid == 1){
    var1 = log(1.0 * LETTNUM_4 * LETTNUM_4)/log(2.0) - ctts;
    return(-1.0 / 
           (1.0/p->siglim + pow(1.0/exp(1.0),(var1 - p->sigp)*p->sigm))
          );
  }
  else return(ctts);
}
/* joint entropy 
   gap is considered as all the possible letters,
   but two gap pair is assumed as one of the letter */
/* UNDER CONSTRUCTION */
double calc_two_lines4_2(seque,s_num,s_len,nth1,nth2,counter,p)
  char **seque;
  int s_num,s_len,nth1,nth2;
  double counter[LETTNUM][LETTNUM];
  struct param *p;
{
  int n,s,t;
  double ctts = 0;
  int lettnth1,lettnth2;
  int s_num2; /* counts valid sequence pair(two gap pair is invalid */

  for(s = 0;s < LETTNUM;s ++)
    for(n = 0;n < LETTNUM;n ++)counter[n][s] = 0; /* clears counter */

  s_num2 = s_num;
  for(n = 0;n < s_num;n ++){
    lettnth1 = lettctmap2(seque[n][nth1]);
    lettnth2 = lettctmap2(seque[n][nth2]);
/* checks for gap (code 1000) */
    if(lettnth1 != 1000 && lettnth2 != 1000)
      counter[lettnth1][lettnth2] += 1.0;
    else if(lettnth1 == 1000 && lettnth2 != 1000)
      for(s = 0;s < LETTNUM_4;s ++)counter[s][lettnth2] += 1.0/LETTNUM_4;
    else if(lettnth1 != 1000 && lettnth2 == 1000)
      for(t = 0;t < LETTNUM_4;t ++)counter[lettnth1][t] += 1.0/LETTNUM_4;
    else if(lettnth1 == 1000 && lettnth2 == 1000){
      counter[LETTNUM-1][LETTNUM-1] += 1.0;
      s_num2 --; /* keep it for validity */
    }
  } /* adds corresponding counter */

/* checks for validity of joint entropy and if invalid, return 9999.0 */
  if(s_num2 != 0 && 1.0*s_num2/s_num < p->twogt)return 9999.0;
/*
  if(s_num2 == 0 && p->gapcol == 1)return 0.0;
  if(s_num2 == 0 && p->gapcol == 0)return 9999.0;
*/
/* calculates joint entropy from ratio */
  for(s = 0;s < LETTNUM;s ++)
    for(n = 0;n < LETTNUM;n ++)
      if(counter[n][s] != 0)
	ctts += -1.0*counter[n][s]/s_num*log(1.0*counter[n][s]/s_num)
	  /log(2.0);
/*
  for(n = 0;n < LETTNUM;n ++)
    for(s = 0;s < LETTNUM;s ++)
      printf("%lf::",counter[n][s]);
  putchar('\n');
*/
  return(ctts);
}

/* joint entropy 
   gap is considered as all the possible letters,
   but two gap pair is assumed as n foreign letters 
   ex. if n = 3
   --         VV
   AT   --->  WW
              XX
              AT
              AT
              AT
*/
/* UNDER CONSTRUCTION */
double calc_two_lines4_3(seque,s_num,s_len,nth1,nth2,counter,p)
  char **seque;
  int s_num,s_len,nth1,nth2;
  double counter[LETTNUM][LETTNUM];
  struct param *p;
{
  int n,s,t;
  double ctts = 0;
  int lettnth1,lettnth2;
  int s_num2; /* counts valid sequence pair(two gap pair is invalid */

  for(s = 0;s < LETTNUM;s ++)
    for(n = 0;n < LETTNUM;n ++)counter[n][s] = 0; /* clears counter */

  s_num2 = s_num;
  for(n = 0;n < s_num;n ++){
    lettnth1 = lettctmap2(seque[n][nth1]);
    lettnth2 = lettctmap2(seque[n][nth2]);
/* checks for gap (code 1000) */
    if(lettnth1 != 1000 && lettnth2 != 1000)
      counter[lettnth1][lettnth2] += 1.0;
    else if(lettnth1 == 1000 && lettnth2 != 1000)
      for(s = 0;s < LETTNUM_4;s ++)counter[s][lettnth2] += 1.0/LETTNUM_4;
    else if(lettnth1 != 1000 && lettnth2 == 1000)
      for(t = 0;t < LETTNUM_4;t ++)counter[lettnth1][t] += 1.0/LETTNUM_4;
    else if(lettnth1 == 1000 && lettnth2 == 1000){
      counter[LETTNUM-1][LETTNUM-1] += 1.0;
      s_num2 --; /* keep it for validity */
    }
  } /* adds corresponding counter */

/* checks for validity of joint entropy and if invalid, return 9999.0 */
  if(s_num2 != 0 && 1.0*s_num2/s_num < p->twogt)return 9999.0;
/*
  if(s_num2 == 0 && p->gapcol == 1)return 0.0;
  if(s_num2 == 0 && p->gapcol == 0)return 9999.0;
*/
/* calculates joint entropy from ratio */
  for(s = 0;s < LETTNUM_4;s ++)
    for(n = 0;n < LETTNUM_4;n ++)
      if(counter[n][s] != 0)
	ctts += -1.0*counter[n][s]/s_num*log(1.0*counter[n][s]/s_num)
	  /log(2.0);

  t = s_num - s_num2; /* number of two gap pairs */

  if(t != 0)
     ctts += -1.0 *
             p->fgap * t / (p->fgap * s_num) * 
	     log(1.0 * t / (p->fgap * s_num)) / log(2.0);
/*
  printf("t = %d\n",t);
  printf("fgap = %d\n",p->fgap);
*/
/*
  for(n = 0;n < LETTNUM;n ++)
    for(s = 0;s < LETTNUM;s ++)
      printf("%lf::",counter[n][s]);
  putchar('\n');
*/
  return(ctts);
}

/* evaluate sequence alignment
1.NUCLEIC ACID ONLY
2.Gap is considered as one letter or all the possible letters
  (also two gaps pair is either recognised or ignored depending on parameter)
3.Mutual information or joint entropy will be recorded
4.Top table will be calculated and same column will or will not be recognized
  more than twice
5.if two column is filled with gaps,joint entropy may or may not be 0 
*/
double mincal5(seque,s_num,s_len,p)
char **seque;
int s_num,s_len;
struct param *p;
{
  double *counter; /* counts each letter (double for gaps) */
  double *entscore; /* saves score for each column */

  double i1,i2,i1_2;
  int n,n1,n2;
  double score = 0;
  double cfrom;
  double *toptable; /* top n score will be stored */

  double (*entfunc)(); /* pointer to function of entropy */
  double (*jefunc)(); /* pointer to function of joint entropy */

/* mutual information recording */
  double *mi_score; /* variable sequence of mutual information score */
  int *mi_x; /* variable sequence to store one column to calculate m.i. */
  int *mi_y; /* variable sequence to store one column to calculate m.i. */
  double tmpscore;
  int tmpxy;
/* ex.  if mi_score[10] = 1.5, mi_x[10] = 2, mi_y[10] = 5,
        mutual information for position 2 and 5 is 1.5 */

  if(p->colone_all == 1)p->topn = s_len / 2;
     /* if colone_all is 1,all the pair will be calculated */

  entscore = (double *)malloc(s_len * sizeof(double));
  counter = (double *)malloc((LETTNUM)*(LETTNUM) * sizeof(double));
     /* if gap is assumed as all the possible letters, 1 can be decreased
	from LETTNUM but not necessary */

  mi_score = (double *)malloc(s_len*(s_len-1)/2*sizeof(double));
  mi_x = (int *)malloc(s_len*(s_len-1)/2*sizeof(int));
  mi_y = (int *)malloc(s_len*(s_len-1)/2*sizeof(int));

  if(p->topn > 0 && p->colone == 0){
    toptable = (double *)malloc(p->topn * sizeof(double));
    for(n = 0;n < p->topn;n ++)toptable[n] = 10000; /* 10000 is empty code */
  } /* If top n score is to be calculated,allocate memory for top board.
       If each column is recognized only once,there is no need to
       allocate top board for now */

  if(p->gapev == 0){  /* gap is treated as one of the letter */
    entfunc = calc_one_line2;
    jefunc = calc_two_lines2;
  }
  else if(p->itgap == 1){ /* two gap pair ignorance */
    entfunc = calc_one_line4;
    jefunc = calc_two_lines4_1; /* ignore two gaps pair */
  }
  else if(p->ltgap == 1){
    entfunc = calc_one_line4;
    jefunc = calc_two_lines4_2; /* assume two gap pair as one of the letter */
  }
  else if(p->fgap > 0){
    entfunc = calc_one_line4;
    jefunc = calc_two_lines4_3; /* assume two gap pair as 
				   fgap forein letters */
  }
  else {
    entfunc = calc_one_line4;
    jefunc = calc_two_lines4; 
  }

/* calculates entropy */
  for(n = 0;n < s_len;n ++)
    entscore[n] = (*entfunc)(seque,s_num,s_len,n,counter,p);

  if(p->ent_disp == 1){
    for(n = 0,score = 0;n < s_len;n ++){
      score += entscore[n];
      printf("entropy of column %3d is %lf\n",n,entscore[n]);
    }
    printf("Sum of entropy of each column is %lf.\n",score);
  }

/* calculates score */
  score = 0;
  n = 0;
  for(n1 = 0;n1 < s_len-1;n1 ++)
    for(n2 = n1+1;n2 < s_len;n2 ++){
      i1 = entscore[n1]; mi_x[n] = n1;
      i2 = entscore[n2]; mi_y[n] = n2;
      i1_2 = (*jefunc)(seque,s_num,s_len,n1,n2,counter,p);

      tmpscore = evform(i1,i2,i1_2,p); /* calculates evaluation */
      mi_score[n] = tmpscore ;

      if(p->topn > 0 && p->colone == 0)instable(toptable, tmpscore, p->topn);
      score +=  tmpscore;
      n ++;
    } /* score should be keeped if not calculating top score */

/*
  for(n = 0;n < p->topn;n ++)printf("%lf\n",toptable[n]);
  sleep(10);
*/

/* displays pair evalutation of each pair */
  if(p->pair_disp == 1)print_pair_eval(s_len,mi_x,mi_y,mi_score,p);

/* bubble sorting of pair evaluation */
  if(p->pair_disp_sort == 1 || p->colone == 1)
    merge_pair_eval(s_len,mi_x,mi_y,mi_score,p);
  if(p->pair_disp_sort == 1)print_pair_eval(s_len,mi_x,mi_y,mi_score,p);


/* calculate top p->topn th 
   each column is recognized only once */
  if(p->topn > 0 && p->colone == 0){
    score = 0;
    for(n = 0;n < p->topn;n ++)
      if(toptable[n] != 10000){
	if(p->topmul == 1)score += toptable[n] * topmulf(n,p->topn);
	else score += toptable[n];
      }
  }
  if(p->topn > 0 && p->colone == 1)
    score = colone(seque,s_num,s_len,mi_x,mi_y,mi_score,p);


/* stair graph 
  n1 = 0;
  for(n = 0;n < s_len*(s_len -1)/2;n ++)
    if(mi_score[n] >= 1.0)n1 ++;
  printf("j.entropy (1.0 -    ) :%d\n",n1);


  for(cfrom = 0.9;cfrom >= 0.0;cfrom -= 0.1){
    for(n1 = 0,n = 0;n < s_len*(s_len -1)/2;n ++)
      if(mi_score[n] >= cfrom && mi_score[n] < cfrom + 0.1)n1 ++;
    printf("j.entropy (%.1lf - %.1lf) :%d\n",cfrom,cfrom + 0.1,n1);
  }
*/
  free(mi_score);
  free(mi_x);
  free(mi_y);

  free(entscore);
  free(counter);
  if(p->topn > 0 && p->colone == 0)free(toptable);

  return(score);
}

/* evaluates top n score from mutual information recording table 
   (it must be SORTED!!!!!)
   same column will not be looked more than twice */
double colone(seque,s_num,s_len,mi_x,mi_y,mi_score,p)
char **seque;
int s_num,s_len;
int *mi_x,*mi_y;
double *mi_score;
struct param *p;
{
  double score;
  int *colflag; /* if 1,corresponding column is 
		   already looked as top score */
  int n,s;
  int allgapp = 0; /* number of all gap pairs */
  int valid = 0; /* number of valid pairs */
  double score_valid = 0;
  int nondgp;
  double worstje;

  colflag = (int *)malloc(s_len * sizeof(int));

  for(n = 0;n < s_len;n ++)colflag[n] = 0;

  n = s_len*(s_len - 1)/2 - 1;
  s = 0;
  score = 0;

  while(s < p->topn){
    if(colflag[mi_x[n]] == 0 && colflag[mi_y[n]] == 0){ 
      if(p->topmul == 1)score += mi_score[n] * topmulf(s,p->topn);
      else score += mi_score[n]; 
         /* if topmul=1, top n score will be multiplied. */
      if(mi_score[n] >= -(p->evthre)){ valid ++; score_valid += mi_score[n]; }
      colflag[mi_x[n]] = 1; colflag[mi_y[n]] = 1;
      s ++;
      if(p->agpr == 1)
	allgapp += all_gap_pair_check(seque,s_num,s_len,mi_x[n],mi_y[n]);
      if(p->pair_sort_colo == 1)
	print_one_pair_eval(seque,s_num,s_len,
			    mi_x[n],mi_y[n],mi_score[n],p);
          /* displays pair evaluation */
    }
    n --;
  }

  p->score1 = score;

  valid -= allgapp; /* number of pairs valid (all gap pairs not included) */
  nondgp = s_len/2 - allgapp; /* number of non-all gap pairs */
  worstje = (int)( log(1.0 * LETTNUM_4 * LETTNUM_4)/log(2.0));
/*
  printf("valid pairs :%d\n",valid);
  printf("non d-gap pairs: %d\n",s_len/2 - allgapp);
  printf("valid score :%lf\n",score_valid);
  printf("valid score2:%lf\n",(score_valid + worstje*nondgp)
                              * (1.0 * valid / nondgp)
                              - worstje * nondgp);
*/
  if(p->evthre > 0)score = (score_valid + worstje * nondgp)
                           * (1.0 * valid / nondgp) - worstje * nondgp;

  if(p->agpr == 1){ score *= s_len/2 * 1.0/nondgp;
		    p->score1 *= s_len/2 * 1.0/nondgp; }

  if(p->agcr == 1){
    n = count_all_gap_column(seque,s_num,s_len);
    if(n != 0)score *= 1.0 * s_len / (s_len - n);
  }

  free(colflag);
  return(score);
}

/* returns the number multiplied to top n score */
int topmulf(s,topn)
int s; /* s+1 th score (range from 0 to topn - 1) */
int topn; /* top n scores that supposed to be calculated */
{
  return(topn - s);
}

/* count number of all gap column in sequences seque
   and number will be returned */
int count_all_gap_column(seque,s_num,s_len)
char **seque;
int s_num,s_len;
{
   int count_ag = 0;
   int n,s;
   for(n = 0;n < s_len;n ++){
     for(s = 0;s < s_num;s ++)if(seque[s][n] != GAPM)break;
     if(s == s_num)count_ag ++;
   }
   return(count_ag);
}



/* if column n1 and n2 is filled with gaps,1 will be returned
   otherwise 0 will be returned */
int all_gap_pair_check(seque,s_num,s_len,n1,n2)
char **seque;
int s_num,s_len;
int n1,n2;
{
  int n;
  
  for(n = 0;n < s_num;n ++)if(seque[n][n1] != GAPM)return 0;
  for(n = 0;n < s_num;n ++)if(seque[n][n2] != GAPM)return 0;

  return 1;
}

int lettctmap(lett)
/* mapping from letter to counter no. */
char lett;
{
  switch(lett){
      case GAPM:return 4;break;
      case 'A': return 0;break;
      case 'T': return 1;break;
      case 'G': return 2;break;
      case 'C': return 3;break;
      default : printf("code error\n");break;
      }
}

int lettctmap2(lett)
/* mapping from letter to counter no. 
 Used when gap is assumed as all letters */
char lett;
{
  switch(lett){
      case GAPM:return 1000;break;
      case 'A': return 0;break;
      case 'T': return 1;break;
      case 'G': return 2;break;
      case 'C': return 3;break;
      default : printf("code error\n");break;
      }
}

/* puts score in order (10000 is empty code) 
   ex. toptable = {30,20,10,9,10000,1000} k = 25
   -> 30,25,20,10,9,10000 */
instable(toptable,k,topn)
double *toptable;
double k;
int topn;
{
  int i = 0;
  int n;

  while(i < topn){
    if(toptable[i] == 10000){
      toptable[i] = k;
      break;
    }
    else if(k > toptable[i]){
      for(n = topn - 1;n > i;n --)toptable[n] = toptable[n - 1];
      toptable[i] = k;
      break;
    }
    i ++;

  }
}

int strtoint(s) /* converts string into integer */
char *s;
{
  int p = 0;
  int x = 0;
  int negative = 0;

  if(s[0] == '-'){ negative = 1;p ++; }
  while(s[p] >= '0' && s[p] <= '9'){
    x *= 10;
    x += (int)(s[p] - '0');
    p ++;
  }
  if(negative == 1)return x*-1;
  else return x;
}

/* gets command line and sets parameters */
comline_manage(argc,argv,p)
int argc;
char *argv[];
struct param *p;
{
  int n;

/* command line */

  p->argc = argc; 
  p->argv = argv;

  p->filename = NULL; /* file name to read.If null data is read from
			 standard I/O */
  p->gright = 0;      /* 1=all gaps column will be shifted to right
			 when saving the file */
  p->filnampar = NULL; /* file name to read as parameter.If NULL,not read */
  p->printpro = 0;  /* 1=prints out process of aligning */
  p->just_eval = 0; /* 0=do alignment 1=just evaluate */
  p->pair_disp = 0; /* 1=displays pair evaluation */
  p->pair_disp_sort = 0; /* 1=sorts and displays pair evaluation */
  p->pair_sort_colo = 0; /* 1=sorts and displays pair evaluation but
			    do not display the same column twice */
  p->ent_disp = 0;  /* 1=displays entropy of each column */
  p->regsavec = 0;   /* if regsave >= 1,it will be every count to save
			alignment state */

  p->testmode = 0;  /* 1=testmode */

/* alignment parameter */
  p->count_max = 0; /* number of tries(counts) */
  p->count_start = 0;  /* count start from this */
  p->easeq = 50;    /* possibility (%) for choosing certain argorithm (RS_AND
		       argorithm) for dropping sequences selected */
  p->sper = 95;     /* possibility (%) for keeping selected sequences
		       in RS_AND argorithm */
  p->hot_t = 0.1;    /* worse changes allowable at the rate of 36% at first */
  p->cold_c = 1000;  /* when counter comes to this value,
		       no worse change allowed */
  p->gapper = 50;  /* possibility for adding more gaps (%) */

  p->treeb = 0;    /* 1 = apply tree base method */

/* evaluation parameter */
  p->eval_type = 0; /* 0=joint entropy 1=mutual information 
		       2=new evaluation */
  p->alpha = 0;     /* 0=indicated character(ex.nucleic acid) only
		       1=all the alphabet is acceptable */
  p->gapev = 1;     /* 0=gap is treated as one of the letter
		       1=gap is treated as all the possible letters */
  p->itgap = 0;     /* 1=ignore two gap pairs */
  p->ltgap = 0;     /* 1=two gap pairs will be assumed as one of the letter
		       when gap is assumed as all the letters */
  p->fgap = 0;      /* >1 two gap pair is assumed as several forein letters */
  p->rgap = 0;      /* 1= when itgap=1,joint entropy will be recalculated
		       using non-gap pair ratio */
  p->agpr = 0;      /* 1=when colone_all=1,score will be multiplied by
		       number of pairs in colone_all=1/number of non 
                       all gap pairs
                    */
  p->agcr = 0;      /* 1=when colone_all=1,score will be multiplied by
		       number of columns/number of non-all-gap-columns 
		     */
  p->evthre = 0;    /* !=0 threshold for joint entropy.If pair evaluation is
                       lower than this,the evaluation will be ignored for
		       that pair,but total evaluation will be multiplied
		       according to rate of valid pairs. colone_all and
		       agpr must be 1 */

  p->twogt = 0.0;   /* threshold for evaluating columns that has 
		       two gap pairs */
  p->gapcol = 0;    /* 1=if two column is all gaps,joint entropy will be 0
		       valid if itgap = 1 */
		     
  p->topn = 0;      /* 0=calculates all the pairs as a score
		       n=calculates sum of top n as a score */
  p->topmul = 0;    /* 1=top score will be multiplied */

  p->colone = 0;    /* 1=each column will be looked only once when
		       calculating top n score */
  p->colone_all = 0; /* 1=each column is looked only once and calculates
			all the pair (one column may remain)
			colone <--- 1
			topn   <--- s_len/2 */

  p->jep = 0;    /* 1>= joint entropy will be 
		    (worst joint entropy - joint entropy calculated)^jep
		    valid if itgap = 1 */

  p->sigmoid = 0; /* 1= apply sigmoid function to joint entropy
		     valid if itgap = 1
                   evaluation = 1 /( (1/siglim + (1/e)^((j.e. - sigp)*sigm)) )
		   */
  p->siglim = 1.0;
  p->sigp = 1.0;
  p->sigm = 1.0;


  p->cons = 1;      /* rate of conservation (valid if p->eval_type = 2) */
  p->corr = 1;      /* rate of correlation  (valid if p->eval_type = 2) */
  
  p->match = 1;     /* >0 evaluation will be matching of letters 
		      -1= simple matching */
  p->pam250 = 0;    /* 1=evaluation will be PAM250 */

/* Gap Penalties when calculating SP */
  p->outgap = 0;
  p->outexgap = 0;
  p->opengap = -7;
  p->extgap = -1;

  p->addmatch = 0;

  comline_manage_sub(argc - 1,&argv[1],p);
}

/* sets parameters according to argc and argv
   no command itself should exist in argv */
comline_manage_sub(argc,argv,p)
int argc;
char *argv[];
struct param *p;
{
  int n;

  for(n = 0;n < argc;n ++){
/*    printf("treating %s\n",argv[n]); */

    if(strcmp(argv[n],"-e") == 0)p->just_eval = 1;
         /* if -e is indicated,just evaluate */
    else if(strcmp(argv[n],"-p") == 0)p->printpro = 1;
         /* if -p is indicated,process of aligning will be printed */

    else if(strcmp(argv[n],"-pair") == 0)p->pair_disp = 1;
         /* if -pair is indicated,display pair evaluation of each pair */

    else if(strcmp(argv[n],"-pair_sort") == 0)p->pair_disp_sort = 1;
         /* if -pair_sort is indicated,sort and display pair evaluation */

    else if(strcmp(argv[n],"-pair_sort_colo") == 0)p->pair_sort_colo = 1;
         /* if -pair_sort_colo is indicated,pair evaluation will be
	    sorted and displayed,but same column will not be displayed
	    more than once */

    else if(strcmp(argv[n],"-ent") == 0)p->ent_disp = 1;
         /* if -ent is indicated,entropy of each column is displayed */

    else if(strcmp(argv[n],"-fs") == 0){
      p->regsavec = strtoint(argv[++ n]);
      p->rgsfilnam = argv[++ n];
    }    /* if -fs is indicated,the next number will be every count to
	    save alignment state and then file name to save */

    else if(strcmp(argv[n],"-gright") == 0)p->gright = 1;
         /* if "-gright" is indicated,all the gap column will be 
	    shifted to right */

    else if(strcmp(argv[n],"-test") == 0)p->testmode = 1;
         /* if -test is indicated, test mode is turned on */
    
/* alignment parameter */
    else if(strcmp(argv[n],"-c") == 0)p->count_max = strtoint(argv[++ n]);
         /* if -c is indicated,the next number will be count_max */

    else if(strcmp(argv[n],"-cs") == 0)p->count_start = strtoint(argv[ ++n]);
         /* if -cs is indicated,the next number will be initial counter */

    else if(strcmp(argv[n],"-easeq") == 0)p->easeq = strtoint(argv[++ n]);
         /* if -easeq is indicated,the next integer will be the possibility
	    for choosing certain argorithm (RS_AND argorithm) */

    else if(strcmp(argv[n],"-sper") == 0)p->sper = strtoint(argv[++ n]);
         /* if -sper is indicated,the next integer will be the possibilily
	    for keeping selected sequences in RS_AND argorithm */

    else if(strcmp(argv[n],"-hot") == 0)p->hot_t = strtodouble(argv[++ n]);
         /* if -hot is indicated,the next double number will be the worse
	    changes at the rate of 36% at first */

    else if(strcmp(argv[n],"-cold") == 0)p->cold_c = strtoint(argv[++ n]);
         /* if -cold is indicated,the next integer will be the counter
	    of no more worse change allowed */

    else if(strcmp(argv[n],"-gapper") == 0)p->gapper = strtoint(argv[++ n]);
         /* if -gapper is indicated,the next integer will be 
	    the possibility(%) for adding more gaps */

    else if(strcmp(argv[n],"-treeb") == 0)p->treeb = 1;
         /* if -treeb is indicated, tree base will be applied */

/* evaluation parameter */
    else if(strcmp(argv[n],"-mi") == 0)p->eval_type = 1;
         /* if -mi is indicated,use mutual information as evaluation */

    else if(strcmp(argv[n],"-ne") == 0){
      p->eval_type = 2;
      p->cons = strtoint(argv[++ n]);
      p->corr = strtoint(argv[++ n]);
    }    /* if -ne is indicated,use new evaluation (cons*ent. + corr*j.e.) */

    else if(strcmp(argv[n],"-match") == 0)p->match = strtoint(argv[++ n]);
         /* if -match is indicated,evaluation will be matching of letters */

    else if(strcmp(argv[n],"-pam250") == 0){
      p->pam250 = 1;
      p->match = 0;
    } /* if -pam250 is indicated,evaluation will be PAM250 */

/* gap penalties when calculating pam250 */
    else if(strcmp(argv[n],"-outgap") == 0)p->outgap = strtoint(argv[++ n]);
    else if(strcmp(argv[n],"-outexgap") == 0)
      p->outexgap = strtoint(argv[++ n]);
    else if(strcmp(argv[n],"-opengap") == 0)p->opengap = strtoint(argv[++ n]);
    else if(strcmp(argv[n],"-extgap") == 0)p->extgap = strtoint(argv[++ n]);

    else if(strcmp(argv[n],"-addmatch") == 0)
      p->addmatch = strtoint(argv[++ n]); /* add match score */


    else if(strcmp(argv[n],"-alpha") == 0)p->alpha = 1;
         /* if -alpha is indicated,all the alphabet including GAPM will be
	    acceptable */

    else if(strcmp(argv[n],"-lgap") == 0)p->gapev = 0;
         /* if -lgap is indicated,assume gap as one of the letter */

    else if(strcmp(argv[n],"-itgap") == 0)p->itgap = 1;
         /* if -itgap is indicated,two gap pair is ignored for j.e */

    else if(strcmp(argv[n],"-ltgap") == 0)p->ltgap = 1;
         /* if -ltgap is indicated,two gap pair is assumed as one of 
	    the letter when gap is assumed as all the possible letters */
    
    else if(strcmp(argv[n],"-fgap") == 0)p->fgap = strtoint(argv[++ n]);
         /* if -fgap is indicated,two gap pair is assumed as 
	    fgap forein letters */

    else if(strcmp(argv[n],"-rgap") == 0)p->rgap = 1;
         /* if -rgap is indicated,joint entropy will be recalculated using
	    non gap pair ratio */
    
    else if(strcmp(argv[n],"-agpr") == 0){ p->agpr = 1; p->gapcol = 1;}
         /* if -agpr is indicated,score will be multiplied by number of
	    pairs in colone_all/number of non all gap pairs */

    else if(strcmp(argv[n],"-agcr") == 0){ p->agcr = 1; p->gapcol = 1;}
         /* if -agcr is indicated,score will be multiplied by number of
	    columns/number of non-all-gap columms */

    else if(strcmp(argv[n],"-evthre") == 0)
      p->evthre = strtodouble(argv[++ n]);
         /* threshold for joint entropy. write positive value */

    else if(strcmp(argv[n],"-twogt") == 0)p->twogt = strtodouble(argv[++ n]);
         /* if -twogt is indicated,the next number will be the threshold
	    for evaluating columns that has two gap pairs */

    else if(strcmp(argv[n],"-gapcol") == 0)p->gapcol = 1;
         /* 1=if two column is filled with gaps,joint entropy will be 0
	    valid if itgap = 1 */

    else if(strcmp(argv[n],"-top") == 0)p->topn = strtoint(argv[ ++ n]);
         /* if -topn is indicated,the next number will be the number
	    of evaluation from the top to be the score */

    else if(strcmp(argv[n],"-topmul") == 0)p->topmul = 1;
         /* if -topmul is indicated, each top score will be multiplied */

    else if(strcmp(argv[n],"-colone") == 0)p->colone = 1;
         /* if -colone is indicated,each column will be looked only once
	    when calculating top n score */

    else if(strcmp(argv[n],"-colone_all") == 0)
      { p->colone_all = 1; p->colone = 1; }
         /* if -colone_all is indicated,each column will be looked only 
	    once and all the pair will be calculated (one column may remain)
	    topn should be set later by looking at s_len */
    
    else if(strcmp(argv[n],"-jep") == 0)p->jep = strtodouble(argv[ ++ n]);
         /* if -jep is indicated,joint entropy will be
            (worst joint entropy - joint entropy calculated)^next number
            valid if -itgap is indicated */

    else if(strcmp(argv[n],"-sigmd") == 0){
      p->sigmoid = 1;
      p->siglim = strtodouble(argv[ ++ n]);
      p->sigp   = strtodouble(argv[ ++ n]);
      p->sigm   = strtodouble(argv[ ++ n]);

      p->sigp -= log(p->siglim)/p->sigm;
    }    /* if -sigmd is indicated,
	  evaluation = 1 /( (1/siglim + (1/e)^((j.e. - sigp)*sigm)) )
          siglim,sigp,sigm will be following after parameter "-sigmd" */

    else if(strcmp(argv[n],"-h") == 0)printhelp();

    else if(strcmp(argv[n],"-pf") == 0){
      p->filnampar = argv[ ++n];
      parafr(p->filnampar,&(p->argc2),&(p->argv2));
      comline_manage_sub(p->argc2,p->argv2,p);
    }    /* if -pf is indicated,the next parameter will be file name
	    for parameters. */

    else if(n == argc - 1)p->filename = argv[n];
         /* parameter is recognized as file name for data input */

    else {
      fprintf(stderr,"error in command line...\"%s\"\n",argv[n]);
      exit(1);
    }
  }
}

/* reads parameter file and converts into argc argv format */
parafr(filename,argc2,argv2)
char *filename;
int *argc2;
char ***argv2;
{
   char *buffer;
   int total;
   int n;
   int ct; /* counts number of parameters */
   int parf; /* parameter found flag: 1 = found */
   int remf; /* remark flag:1 = remark until the end of the line */

   ndataread(&buffer,&total,filename); /* buffer memory must be kept 
					  for holding parameters */
   
/* checks number of parameters */
   parf = 0;remf = 0;
   for(n = 0,ct = 0;n < total;n ++){
     if(buffer[n] != ' ' && buffer[n] != '\n' && buffer[n] != '\t'
	 && buffer[n] != REMM && remf == 0){ /* non-remark chara found */
       if(parf == 0){ parf = 1;ct ++; } /* head chara of parameter */
     }
     else if(buffer[n] == '\n'){ parf = 0; remf = 0; }
     else if(buffer[n] == ' ' || buffer[n] == '\t')parf = 0;
     else if(buffer[n] == REMM)remf = 1;
   }

   *argc2 = ct;
   *argv2 = (char **)malloc(ct * sizeof(char *)); /* pointers to param */

/* put '\0' after each parameter and record address of parameters */
   for(n = 0,ct = 0;n < total;n ++){
     if(buffer[n] != ' ' && buffer[n] != '\n' && buffer[n] != '\t'
	 && buffer[n] != REMM && remf == 0){ /* non-remark chara found */
       if(parf == 0){ parf = 1; (*argv2)[ct ++] = &buffer[n] ; }
          /* head chara of parameter */
     }
     else if(buffer[n] == '\n'){ parf = 0; remf = 0;buffer[n] = '\0'; }
     else if(buffer[n] == ' ' || buffer[n] == '\t')
       {parf = 0; buffer[n] = '\0'; }
     else if(buffer[n] == REMM){ remf = 1; buffer[n] = '\0';}
   }
 }

/* prints pair evaluation of each pair which is already calculated 
 and recorded in array */
print_pair_eval(s_len,mi_x,mi_y,mi_score,p)
int s_len;
int *mi_x,*mi_y; /* columns of each pair */
double *mi_score; /* scores of each pair is in these variables */
struct param *p;
{
  int n;
  char *mesg;

/* choose message */
  switch(p->eval_type){
    case 0:mesg = "j.entropy";break;
    case 1:mesg = "m.info";break;
    case 2:mesg = "evaluation";break;
    default:printf("eval_type error...%d\n",p->eval_type);break;
  }

  for(n = 0;n < s_len*(s_len - 1)/2;n ++)
    printf("%s between col.%3d and col.%3d is %lf\n",
	   mesg,mi_x[n],mi_y[n],mi_score[n]);
}

/* displays pair evaluation when corresponding column is calculated.
   mainly used by colone function which admits counting only once for each
   column */
print_one_pair_eval(seque,s_num,s_len,n1,n2,eval,p)
char **seque;
int s_num,s_len;
int n1,n2;
double eval;
struct param *p;
{
  int n;
  char *mesg;
  double e1,e2,je;
  double *counter; /* counts each letter.used when calculating ent.
		      and m.i. */
  struct param ptemp;

  counter = (double *)malloc((LETTNUM)*(LETTNUM) * sizeof(double));
     /* if gap is assumed as all the possible letters, 1 can be decreased
	from LETTNUM but not necessary */

  ptemp.rgap = 0;
  ptemp.jep = 0;
  ptemp.sigmoid = 0;
  ptemp.twogt = 0;

/* choose message */
  switch(p->eval_type){
    case 0:mesg = "j.entropy";break;
    case 1:mesg = "m.info";break;
    case 2:mesg = "evaluation";break;
    default:printf("eval_type error...%d\n",p->eval_type);break;
  }

/* calculates mutual information.gap is treated as one of the letter */
  e1 = calc_one_line2(seque,s_num,s_len,n1,counter,&ptemp);
  e2 = calc_one_line2(seque,s_num,s_len,n2,counter,&ptemp);
  je = calc_two_lines2(seque,s_num,s_len,n1,n2,counter,&ptemp);
  
  printf("%s between col.%3d and col.%3d is %lf   :   %lf\n",
	   mesg,n1,n2,eval,e1+e2-je);

  free(counter);
}  



/* Do bubble sorting to pair evaluation. 
   Pair evaluation must be done in mother function */
bubble_pair_eval(s_len,mi_x,mi_y,mi_score,p)
int s_len; /* length of sequence including gaps */
int *mi_x,*mi_y; /* columns of pair */
double *mi_score; /* scores of each pair is in these variables */
struct param *p;
{
  int n1,n2;
  double tmpscore;
  int tmpxy;

  for(n1 = s_len*(s_len - 1)/2 - 1;n1 > 0;n1 --)
    for(n2 = 0;n2 < n1;n2 ++)
      if(mi_score[n2] > mi_score[n2 + 1]){
	  tmpscore = mi_score[n2 + 1];
	  mi_score[n2 + 1] = mi_score[n2];
	  mi_score[n2] = tmpscore;
	  tmpxy = mi_x[n2+1]; mi_x[n2+1] = mi_x[n2]; mi_x[n2] = tmpxy;
	  tmpxy = mi_y[n2+1]; mi_y[n2+1] = mi_y[n2]; mi_y[n2] = tmpxy;
	}
}

/* Do merge sorting to pair evaluation. 
   Pair evaluation must be done in mother function */
merge_pair_eval(s_len,mi_x,mi_y,mi_score,p)
int s_len; /* length of sequence including gaps */
int *mi_x,*mi_y; /* columns of pair */
double *mi_score; /* scores of each pair is in these variables */
struct param *p;
{
  int *wmi_x,*wmi_y;
  double *wscore;
  int length;
  length = s_len*(s_len - 1)/2;

  wmi_x = (int *)malloc(length * sizeof(int)); /* can be half size */
  wmi_y = (int *)malloc(length * sizeof(int));
  wscore = (double *)malloc(length * sizeof(double));

  merge_pair_eval_sub(mi_x,mi_y,mi_score,0,length-1,
		      wmi_x,wmi_y,wscore);

  free(wmi_x);
  free(wmi_y);
  free(wscore);
}
merge_pair_eval_sub(mi_x,mi_y,mi_score,first,last,wmi_x,wmi_y,wscore)
int *mi_x,*mi_y,*wmi_x,*wmi_y;
double *mi_score,*wscore;
int first,last;
{
  int middle;
  int i,j,k,p;

  if(first < last){ /* if more than one letter for sorting */
    middle = (first + last)/2;
    merge_pair_eval_sub(mi_x,mi_y,mi_score,first,middle,wmi_x,wmi_y,wscore);
    merge_pair_eval_sub(mi_x,mi_y,mi_score,
			middle + 1,last,wmi_x,wmi_y,wscore);
    p = 0; /* used as pointing to copy to work area and number of letters
	      in the work area (first half) */
    for(i = first;i <= middle;i ++,p ++){
      wmi_x[p] = mi_x[i];
      wmi_y[p] = mi_y[i];
      wscore[p] = mi_score[i];
    }

    i = middle + 1;j = 0;k = first;
        /* now i is used for pointing of last half
	   j is used for pointing of work area (first half)
	   k is used for pointing to place to copy */
    while(i <= last && j < p){
      /* copy until first or last half is all copied */
      if(wscore[j] <= mi_score[i]){
	mi_score[k] = wscore[j];
	mi_x[k] = wmi_x[j];
	mi_y[k] = wmi_y[j];
	k ++; j ++;
      }
      else {
	mi_score[k] = mi_score[i];
	mi_x[k] = mi_x[i];
	mi_y[k] = mi_y[i];
	k ++; i ++;
      }
    }

    while(j < p){
      mi_score[k] = wscore[j];
      mi_x[k] = wmi_x[j];
      mi_y[k] = wmi_y[j];
      k ++; j ++;
    } /* copies rest of first half */
  }
}


/* calculates evaluation from entropy(i1,i2) and joint entropy(i1_2)
   by looking at eval_type */
double evform(i1,i2,i1_2,p)
double i1,i2,i1_2;
struct param *p;
{
  double tmpscore;
  switch(p->eval_type){
    case 0:tmpscore = -i1_2;         break; /* joint entropy */
    case 1:tmpscore = i1 + i2 - i1_2;break; /* mutual information */
    case 2:tmpscore = (p->corr - p->cons)*(i1 + i2) - (p->corr)*i1_2;break;
                                            /* new evaluation */
    default:printf("eval_type error...%d\n",p->eval_type);break;
  }
  return tmpscore;
}

/* looks sequences seque and make a new state seque2 
   memory for seque2 must be allocated in mother routine */
newstate(seque,seque2,s_num,s_len,p)
char **seque;   /* old state of sequences */
char **seque2;  /* new state of sequences */
int s_num,s_len; /* number of sequences & length of sequences including gaps */
struct param *p;
{
  int nth,num; /* location of gaps to move and number of gaps to move */
  int ipos; /* location to move gaps to */
  int *seqsel; /* flags for selected sequences: 1 = selected */
  int n,s;

  seqsel = (int *)malloc(s_num * sizeof(int));

  seqselect(seque,seqsel,s_num,s_len,&nth,&num,p);
     /* determine sequences to align and location and number of gaps */

/*
  printf("old state:\n");
  seqprint(seque,s_num,s_len);

  printf("sequences selected are:\n");
  for(n = 0;n < s_num;n ++)printf("%d ",seqsel[n]);
  putchar('\n');
*/

  ipos = rand() % (s_len - num); /* determines where to move gaps */
  if(ipos >= nth)ipos += num; /* places to move gaps are restricted */
/*
  printf("gaps to move is in position %d\n",nth);
  printf("number of gaps to move is %d\n",num);
  printf("destination location to move gaps is %d\n",ipos);
*/
  for(n = 0;n < s_num;n ++){
    if(seqsel[n] == 1)
      seqgapshift(seque[n],seque2[n],
		  s_num,s_len,nth,num,ipos); /* move gaps if selected */
    else for(s = 0;s < s_len;s ++)seque2[n][s] = seque[n][s];
    /* if not selected,copy without any change */
  } /* result for aligning seque is put into seque2 */

  free(seqsel);
}

/* converts string to double value */
double strtodouble(s)
char *s;
{
  double x = 0;
  int p = 0;
  double unit= 0.1;

  while(s[p] >= '0' && s[p] <= '9'){
    x *= 10;
    x += (double)(s[p] - '0');
    p ++;
  }
  if(s[p] == '.'){
    p ++;
    while(s[p] >= '0' && s[p] <= '9'){
      x += (double)(s[p] - '0') * unit;
      unit *= 0.1;
      p ++;
    }
  }
  return x;
}

/* branch center for evaluation of sequences alignment */
double evalseq(seque,s_num,s_len,p)
char **seque;
int s_num,s_len;
struct param *p;
{
  struct gaparam gp;
  gp.outgap = p->outgap;
  gp.outexgap = p->outexgap;
  gp.opengap = p->opengap;
  gp.extgap = p->extgap;
  gp.addmatch = p->addmatch;

  if(p->pam250 == 1 || p->match > 0 )
    return((double)pairscore_t(seque,s_num,s_len,gp,p->match));

  else if(p->match == -1)return((double)seqscore(seque,s_num,s_len));

  else if(p->alpha == 1)return(mincal(seque,s_num,s_len,p));
     /* all the letter is acceptable */

  else {
    printf("Mincal5 called.\n");
    return(mincal5(seque,s_num,s_len,p));

  }
}

/* write sequence and evaluation to file */
seqevalfile(seque,s_num,s_len,counter,message,p)
char **seque;
int s_num,s_len;
int counter;
char *message;
struct param *p;
{
  FILE *fp;
  int n,s;
  time_t tp;
  tp = time(NULL);

  if(p->gright == 1)seqgapright(seque,s_num,s_len);
  fp = fopen(p->rgsfilnam,"w+");
  if(fp == NULL)return;

/* print title and version */
  fprintf(fp,"# ***************************\n");
  fprintf(fp,"# *** Result of Alignment ***\n");
  fprintf(fp,"# ***************************\n");
  fprintf(fp,"#     version %s\n\n",VERSION);
/* writes command line */
  fputc('#',fp);
  for(n = 0;n < p->argc;n ++)fprintf(fp,"%s ",p->argv[n]);
  fputc('\n',fp);

  if(p->filnampar != NULL){
    fputc('#',fp);
    for(n = 0;n < p->argc2;n ++)fprintf(fp,"%s ",p->argv2[n]);
    fputc('\n',fp);
  }

  fprintf(fp,"\n#%s\n",ctime(&tp));

/* writes column numbers */
  fputc('#',fp);
  n = 1;
  while(n < s_len){
    if(n % 10 == 0)fprintf(fp,"%d",n/10);
    else fputc(' ',fp);
    n ++;
  }
  fputc('\n',fp);

  fputc('#',fp);
  n = 1;
  while(n < s_len){
    fprintf(fp,"%d",n % 10);
    n ++;
  }
  fputc('\n',fp);

  for(n = 0;n < s_num;n ++){
    for(s = 0;s < s_len;s ++)fputc(seque[n][s],fp);
    fputc('\n',fp);
  }
  fprintf(fp,"#counter:%d score:%lf\n",
	  counter,evalseq(seque,s_num,s_len,p));
  fprintf(fp,"#%s\n",message);
  fclose(fp);
}

/* moves all pair gap column to right */
seqgapright(seque,s_num,s_len)
char **seque;
int s_num,s_len;
{
  int n,s,t,u;

  for(n = s_len - 2;n >= 0;n --){
    for(s = 0;s < s_num;s ++)if(seque[s][n] != GAPM)break;
    if(s == s_num){ /* all gaps column found */
      for(t = 0;t < s_num;t ++){
	for(u = n;u < s_len - 1;u ++)
	  seque[t][u] = seque[t][u + 1];
	seque[t][s_len - 1] = GAPM;
      }
    }
  }
}


printhelp(){
  printf("*****************************************************\n");
  printf("* Alignment program using simulated anealing method *\n");
  printf("*****************************************************\n");
  printf("                          last update on ");
  printf("%s \n\n",VERSION);
  printf("-pf\t the next string will be file name to read as parameter\n");
  printf("-e\tjust evaluate \n");
  printf("-p\tdisplays aligning processes\n");  
  printf("-pair\t display pair evaluation of each pair  \n");
  printf("-pair_sort\t sorts and display pair evaluation\n");
  printf("-pair_sort_colo\t sorts and displays pair evaluation ");
  printf("-but same column will not be displayed more than once.\n");
  printf("-ent\t display entropy of each column\n");
  printf("-fs\t saves alignment state regulary.The next number will be ");
  printf("every count to save alignment state and then file name to save\n");
  printf("-gright\t all gap column will be shifted to right when saving ");
  printf("the file\n");
  printf("-hot\t the next double number will be the worse changes ");
  printf("at the rate of 36%% at first\n");
  printf("-cold\t the next integer will be the counter for no more worse ");
  printf("change allowed\n");


  printf("\nAlignment parameter\n");
  printf("-c\t the next integer will be count_max\n");
  printf("-cs\t the next integer will be counter to start from\n");
  printf("-easeq\t the next integer will be the possibility for choosing ");
  printf("certain argorithm (RS_AND argorithm)\n");
  printf("-sper\t the next integer will be the possibilily for keeping ");
  printf("selected sequences in RS_AND argorithm\n");
  printf("-gapper\t the next integer will be the possibility for adding ");
  printf("more gaps\n");
  printf("-treeb\t apply tree base\n");

  printf("\nEvaluation parameter\n");
  printf("-mi\t use mutual information as evaluation\n");
  printf("-ne\t the next two numbers indicate conservation and correlation ");
  printf("for evaluation\n");
  printf("-match\t evaluation will be matching of letters (state match score)\n");
  printf("-pam250\t evaluation will be PAM250\n");
  printf("SP score gap penalties\n");
  printf("-outgap\t next negative value will indicate out gap penalty\n");
  printf("-outexgap\t next negative value will indicate out ex penalty\n");
  printf("-opengap\t next negative value will indicate open gap penalty\n");
  printf("-extgap\t next negative value will indicate extention gap penalty\n");
  printf("-addmatch\t next positive value will indicate additional score ");
  printf("for more matches\n");

  printf("-alpha\t all the alphabet including GAPM will be acceptable\n");
  printf("-lgap\t assume gap as one of the letter\n");
  printf("-itgap\t two gap pair is ignored for j.e\n");
  printf("-ltgap\t two gap pair is assumed as one of the letter\n");
  printf("-fgap\t two gap pair will be assumed as number of forein letters ");
  printf("that is indicated by next number.\n");
  printf("-rgap\t score calculated from joint entropy will be as follows:\n");
  printf("\tjeev = score calculated from joint entropy\n");
  printf("\tx = joint entropy\n");
  printf("\tr = non two gap pair ratio\n");
  printf("\tlettnum = number of letters supported excluding gap\n");
  printf("\t-->jeev = (x - log2(lettnum^2))*r + log2(lettnum^2)\n");
  printf("-agpr\t score will be multiplied by number of pairs in ");
  printf("colone_all/number of non all gap pairs.valid if -colone_all is ");
  printf("indicated\n");
  printf("-agcr\t score will be multiplied by number of columns/");
  printf("number of non-all-gap-columns.valid if -colone_all is ");
  printf("indicated.\n");
  printf("-evthre The next positive number will indicate threshold for ");
  printf("joint entropy.If evaluation is lower than this,the evaluation ");
  printf("will be ignored for that pair,but the total evaluation will be ");
  printf("multiplied according to the rate of valid pairs.\n");
  printf("Let's assume :\n");
  printf("\ts = total joint entropy\n");
  printf("\tw = worst score when non-two-gap pairs have worst evaluation.");
  printf("It is number of non-two-gap pairs * worst evaluation.\n");
  printf("\tr = number of valid pairs/number of non-two-gap pairs\n");
  printf("Then score = (s - w) * r + w\n");
  printf("\t-colone_all and -agpr must be indicated\n");

  printf("-twogt\t the next number will be the threshold for evaluating "); 
  printf("columns that has two gap pairs\n");
  printf("-gapcol\t if two column is filled with gaps,joint entropy ");
  printf("will be 0.valid if two gap pair is ignored\n");

  printf("-top\t the next number will be the number of ");
  printf("evaluation from the top to be the score\n");
  printf("-topmul\t each top score will be multiplied\n");
  printf("-colone\t each column will be looked only once when calculating ");
  printf("-colone_all\tcalculates the sum of all the pair evaluation ");
  printf("but same column will not be evaluated more than once. ");
  printf("each time higher pair evaluation will be selected.\n");
  printf("top n score\n");
  printf("-jep\t let the next number be x.Joint entropy will be as follows\n");
  printf("\t (worst joint entropy - joint entropy calculated)^x\n");
  printf("\t valid if -itgap is indicated\n");
  printf("-sigmd let the following numbers be siglim,sigp,sigm.");
  printf("evaluation for each pair will be ");
  printf(" 1 /( (1/siglim + (1/e)^((j.e. - sigp)*sigm)) ) ");
  printf("valid if -itgap is indicated. ");
  printf(" siglim:upper limit  sigp:center sigm:multiplication\n");

  printf("\n-h help\n");
  exit(0);
}
