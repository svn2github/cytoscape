#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#include "global_st.h"
#include "atg_func.h"

#define UTRRANGE 500
#define CDSRANGE 500

#define MAX_REC 200

static int utrrange = UTRRANGE;
static int cdsrange = CDSRANGE;

static int smooth_value = 1;
static int segment = 0;

static int ndata[ 10000 ];   /* utrrange + cdsrange */
static int obsdata[ 10000 ]; /* utrrange + cdsrange */
static int total_cds;
static char mpat[200];
static char pat0[200], pat1[200], pat2[200], pat_tmp[200];
static char filename[20];

static int upmax, upmin;
static int numoli;

static int avrange_up = 0;
static int avrange_dn = 0;

static int disprange_up = 0;
static int disprange_dn = 0;

static int total_all_mode = 0;
static int total_all_mode_pos = 0;
static int total_all_mode_count = 0;

static struct recbest10 rec[MAX_REC];


/* prototypes */
void patfreq_rec(char [],int, int, char []);
void patfreq_prerec(struct gparam *, char [], char [], int, 
		    struct cds_info[], int, char *);
void patafreq_disp(char *);
double patdfreq_point(int *);
double patdfreq_point2(int *);
double patdfreq_pointm(int *);
void patbestrec(char []);

/* Notice: This function must be registered at the head of func_init */
int utrr_par(int argc, char *argv[], int n){
  if(strcmp(argv[n], "-utrr") == 0){
    utrrange = atoi(argv[n + 1]);
    return 2;
  }
  else return 0;
}
void utrr_help(){

  printf("-utrr\t Specifies upstream range for calculating pattern freq.\n");

}

/* Notice: This function must be registered at the head of func_init */
int cdsr_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-cdsr") == 0){
    cdsrange = atoi(argv[n + 1]);
    return 2;
  }
  return 0;
}

void cdsr_help(){

  printf("-cdsr\t Specifies downstream range for calculating pattern freq.\n");

}


/* Notice: This function must be registered at the head of func_init */
int smooth_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-smooth") == 0){
    smooth_value = atoi(argv[n + 1]);
    return 2;
  }
  else return 0;
}

void smooth_help(){

  printf("-smooth\t Smoothing for calculating pattern frequency\n");

}
void smoother(){

  int i,j;
  int ndata_s, obsdata_s;

  for(i = 0;i < utrrange + cdsrange;i ++){
    ndata_s = 0;
    obsdata_s = 0;
    for(j = i;j < i + smooth_value && j < utrrange + cdsrange;j ++){
      ndata_s += ndata[j];
      obsdata_s += obsdata[j];
    }
    ndata[i] = ndata_s;
    obsdata[i] = obsdata_s;
  }
}

int segm_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-segm") == 0){
    segment = atoi(argv[n + 1]);
    return 2;
  }
  else return 0;

}

void segm_help(){

  printf("-segm\t Counts if oligonucleotide is within segments specified.\n");

}

int patavrange_par(int argc, char *argv[], int n){
  if(strcmp(argv[n], "-patavrange") == 0){
    avrange_up = atoi(argv[n + 1]);
    avrange_dn = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;

}

void patavrange_help(){

  printf("-patavrange\t States range to calculate average: State upstream and downstream.\n");

}


int patdisprange_par(int argc, char *argv[], int n){
  if(strcmp(argv[n], "-patdisprange") == 0){
    disprange_up = atoi(argv[n + 1]);
    disprange_dn = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void patdisprange_help(){

  printf("-patdisprange\t States range to display frequency. State upstream and downstream.\n");

}

int total_all_mode_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-total_all_mode") == 0){
    total_all_mode = 1;
    return 1;
  }
  else return 0;
}

void total_all_mode_help(){

  printf("-total_all_mode\t Average in patfreq is calculated according to frequency of the pattern in the whole sequence.\n");

}

int patfreq_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-patfreq") == 0){
    total_cds = 0;
    strcpy(mpat, argv[n + 1]);
    return 2;
  }
  else return 0;
}

void patfreq_head(char *line){

}

void patfreq_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i,j,k, mpat_len, ccds_start, spos;
  char *compseqn;

  compseqn = compseqget(seqn, max);

  /* Counts frequency of specific pattern all over the sequence */
  if(total_all_mode == 1 && ncds > 0){ /* ncds > 0 is temporary */
    mpat_len = strlen(mpat);
    for(i = 0; i <= max - mpat_len;i ++){
      total_all_mode_pos ++;
      if(strncmp(&seqn[i], mpat, mpat_len) == 0)total_all_mode_count ++;
    }
  }

  patfreq_prerec(entry_info, seqn, compseqn, max, cds, ncds, mpat);
  free(compseqn);
}

void patfreq_fin(){

  int i,j,n1, n2;
  double x, std_x, lambda, av, prob;
  int disp_start, disp_end;

  if(smooth_value > 1)smoother();

/* Calculates average */

  if(total_all_mode == 1){
    printf("Average (Total all mode): total count %d total pos %d av. %lf\n",
	   total_all_mode_count, total_all_mode_pos,
	   1.0 * total_all_mode_count / total_all_mode_pos);
    av = 1.0 * total_all_mode_count / total_all_mode_pos;
  }
  else {

    n1 = 0; n2 = 0;
    if(avrange_up == 0 && avrange_dn == 0)
      for(i = 0;i < utrrange + cdsrange;i ++){
	n1 += obsdata[i];
	n2 += ndata[i];
      }
    else {
      for(i = utrrange - avrange_up;i <= utrrange + avrange_dn;i ++)
	if(i >= 0){
	  n1 += obsdata[i];
	  n2 += ndata[i];
	}
    }
    av = 1.0 * n1 / n2;
    
    if(avrange_up == 0 && avrange_dn == 0)
      printf("Average ratio:%lf\n", av);
    else printf("Average ratio from %d to %d is %lf\n", 
	      -avrange_up, avrange_dn, av);
  }

  if(cdsrange > 3)total_cds = ndata[utrrange + 3]; 
  else total_cds = ndata[ utrrange - strlen(mpat) ]; /* temporary */

  if(disprange_up == 0 && disprange_dn == 0){
    disp_start = 0; disp_end = utrrange + cdsrange - 1;
  }
  else {
    disp_start = utrrange - disprange_up;
    disp_end = utrrange + disprange_dn;
  }
  for(i = disp_start;i <= disp_end;i ++){
    printf("%3d\t%d / %d\t", i - utrrange, obsdata[i], ndata[i]); 
    if(ndata[i] != 0){
      x = 1.0 * obsdata[i] / ndata[i];
      if(total_cds * av > 5){
	std_x = (obsdata[i] - ndata[i] * av)
	  / sqrt(ndata[i] * av * (1-av)); /* ndata[i] <-> total_cds?? */
	prob = 0.5 - norm_half(std_x);
      }
      else {
	lambda = ndata[i] * av;
	std_x = obsdata[i] / (ndata[i] * av);
	prob = poisson_over(obsdata[i], lambda); /* ndata[i] <-> total_cds?? */
      }
      printf("%3d\t %lf\t", i - utrrange, x);
      printf("%lf\t %lf\n",prob,std_x);
    }
    else putchar('\n');
  }
}

void patfreq_help(){

  printf("-patfreq\t Calculates frequency of stated patterns around start codons:State pattern\n");

}

void patfreq_prerec(struct gparam *entry_info, char seqn[], 
		    char compseqn[],int max, struct cds_info cds[], 
		    int ncds, char *pat)
{
  int i,j,k, ccds_start, spos;
  
  for(i = 0;i < ncds; i ++){
/*    printf("looking CDS #%d...\n",i); */
    if(valid_cds[i] == 0)continue;
    if(cds[i].complement == 0){
      if(cds[i].cds_start > 0){
	total_cds ++;
	if(cds[i].cds_start > utrrange)spos = 0;
	else spos = utrrange - cds[i].cds_start + 1;
	patfreq_rec(&seqn[cds[i].cds_start - utrrange - 1],
		    max - (cds[i].cds_start - 1 - utrrange), 
		    spos, pat);

      }
    }
    else {
      if(cds[i].cds_end > 0){
	ccds_start = max - cds[i].cds_end + 1;
	if(ccds_start > 0){
	  total_cds ++;
	  if(ccds_start > utrrange)spos = 0;
	  else spos = utrrange - ccds_start + 1;
	  patfreq_rec(&compseqn[ccds_start - utrrange - 1],
		      max - (ccds_start - 1 - utrrange),
		      spos, pat);
	}
      }
    }
  }
}




void patfreq_rec(char seqnp[],int max, int spos, char pat[])
/* seqnp is a part of sequence */
/* Do not access position of seqnp that is less than spos */
{
  int i,j,k;

  /* (put '\0' to seqnp[utrrange]) */
  for(i = spos;i <= (int)(utrrange) - (int)strlen(pat);i ++){
    ndata[i] ++;
    if(segment == 0 && spmatch(&seqnp[i], pat) == 1){
      obsdata[i] ++;
    }
    else if(segment > 0 && incwi(&seqnp[i], pat, segment) == 1){
      obsdata[i] ++;
    }
  }

  /* (retract '\0' from seqnp[utrrange]) */
  for(i = utrrange /* + strlen(pat) */ ;i < utrrange + cdsrange && i < max;i ++){
    if(i < utrrange + 3)i = utrrange + 3;
    ndata[i] ++;
    if(segment == 0 && spmatch(&seqnp[i], pat) == 1){
      obsdata[i] ++;
    }
    else if(segment > 0 && incwi(&seqnp[i], pat, segment) == 1){
      obsdata[i] ++;
    }
  }

}


/* patafreq processes only one entry */
int patafreq_par(int argc, char *argv[], int n){
  if(strcmp(argv[n], "-patafreq") == 0){
    total_cds = 0;
    strcpy(filename, argv[n + 1]);
    return 2;
  }
  else return 0;
}

void patafreq_head(char *line){

}

void patafreq_ent(struct gparam *entry_info, char seqn[], int max,
		  struct cds_info cds[], int ncds){
  int i,j,k, ccds_start, spos;
  char *compseqn;
  char pat[10];


/*  total_cds = ncds; CHECK !!! */
  compseqn = (char *)(malloc(max * sizeof(char)));
  for(i = max;i > 0;i --)compseqn[max - i] = cmpl(seqn[i - 1]);

  for(i = 0;i < 10;i ++)pat[i] = '\0';
  while(next_patsp(pat, 3) != 1){
    for(i = 0;i < utrrange + cdsrange; i ++)
      ndata[i] = obsdata[i] = 0;
    patfreq_prerec(entry_info, seqn, compseqn, max, cds, ncds, pat);
    
    if(smooth_value > 1)smoother();

    patafreq_disp(pat);
  }
  free(compseqn);

}

void patafreq_fin(){

}

void patafreq_help(){

  printf("-patafreq\t Finds remarkable patterns around start codons:State file name to save results.\n");
}


void patafreq_disp(char *pat){
  int i,j,n1, n2;
  double x, std_x, lambda, av, prob;
  FILE *fp;
  char filen[20];
  strcpy(filen, filename);
  strcat(filen, "~");
  strcat(filen, pat);
  for(i = 0;i < strlen(filen);i ++)
    if(filen[i] == '-')filen[i] = '_';

  fp = fopen(filen, "w");

  fprintf(fp, "TitleText: Frequency of Specific Nucleotide Pattern around start codons\n");
  fprintf(fp, "XUnitText: Position\n");
  fprintf(fp, "YUnitText: Standardized frequency");


/* Calculates average */
  n1 = 0; n2 = 0;
  if(avrange_up == 0 && avrange_dn == 0)
    for(i = 0;i < utrrange + cdsrange;i ++){
      n1 += obsdata[i];
      n2 += ndata[i];
    }
  else {
    for(i = utrrange - avrange_up;i <= utrrange + avrange_dn;i ++)
      if(i >= 0){
	n1 += obsdata[i];
	n2 += ndata[i];
      }
  }
  av = 1.0 * n1 / n2;

  total_cds = ndata[utrrange + 3]; /* temporary */
  if(total_cds * av > 5)fprintf(fp,"(ND)\n");
  else fprintf(fp,"(PS)\n");
  fprintf(fp,"#Average ratio:%lf\n", av);

  fprintf(fp,"\"%s\"\n", pat);
  for(i = 0;i < utrrange + cdsrange;i ++){
    if(ndata[i] != 0){
      x = 1.0 * obsdata[i] / ndata[i];
      if(total_cds * av > 5){
	std_x = (obsdata[i] - ndata[i] * av)
	  / sqrt(ndata[i] * av * (1-av)); /* ndata[i] <-> total_cds?? */
/*	prob = 0.5 - norm_half(std_x); */
      }
      else {
	lambda = total_cds * av;
	std_x = obsdata[i] / (ndata[i] * av); /* ndata[i] <-> total_cds?? */
/*	prob = poisson_over(obsdata[i], lambda); */
      }
      fprintf(fp,"%3d\t %lf\n", i - utrrange, std_x);
    }
  }
  fclose(fp);
  printf("Results for pattern %s is in %s\n", pat, filen);
}


int patdfreq_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-patdfreq") == 0){
    total_cds = 0;
    printf("*** Significant Pattern Discovery ***\n");
    printf("Please input scope:\n");
    printf("Upstream position from:-");
    scanf("%d", &upmax);
    printf("Upstream position to  :-");
    scanf("%d", &upmin);
    return 1;
  }

  else return 0;
}

void patdfreq_head(char *line){

}

void patdfreq_ent(struct gparam *entry_info, char seqn[], int max,
		  struct cds_info cds[], int ncds){

  int i,j,k, ccds_start, spos;
  char *compseqn;
  double po, max_po;
  int max_pos;

/*  total_cds = ncds; Check !!! */
  compseqn = (char *)(malloc(max * sizeof(char)));
  for(i = max;i > 0;i --)compseqn[max - i] = cmpl(seqn[i - 1]);
  for(i = 0;i < 200;i ++)pat0[i] = pat1[i] = pat2[i] = '\0';

  max_po = 0; strcpy(pat_tmp, pat0);
  while(strlen(pat0) < 19){
    pat1[0] = '\0';
    while(next_patsp2(pat1, 1) != 1){
      for(i = 0;i < utrrange + cdsrange; i ++)
	ndata[i] = obsdata[i] = 0;
      printf("l:\"%s\" + \"%s\"= ",pat1, pat0);
      strcat(pat1, pat0);
      total_cds = 0;
      patfreq_prerec(entry_info, seqn, compseqn, max, cds, ncds, pat1);
      if(smooth_value > 1)smoother();
      po = patdfreq_point(&max_pos);
      printf("Point for pattern \"%s\" is %lf (pos.%d)\n",pat1, po, max_pos);
      if(po > max_po){
	max_po = po; strcpy(pat_tmp, pat1);
      }
      pat1[1] = '\0';
    }
    printf("Switching side\n");
    pat1[0] = '\0';
    strcpy(pat2, pat0);
    while(next_patsp2(pat1, 1) != 1){
      for(i = 0;i < utrrange + cdsrange; i ++)
	ndata[i] = obsdata[i] = 0;
      printf("r:\"%s\" + \"%s\"= ",pat2, pat1);
      strcat(pat2, pat1);
      total_cds = 0;
      patfreq_prerec(entry_info, seqn, compseqn, max, cds, ncds, pat2);
      if(smooth_value > 1)smoother();
      po = patdfreq_point(&max_pos);
      printf("Point for pattern \"%s\" is %lf (pos.%d)\n",pat2, po,max_pos);
      if(po > max_po){
	max_po = po; strcpy(pat_tmp, pat2);
      }
      pat2[ strlen(pat0) ] = '\0';
    }
    strcpy(pat0, pat_tmp);
    printf("New pattern is %s\n", pat0);
  }

  free(compseqn);

}

void patdfreq_fin(){

}

void patdfreq_help(){

  printf("-patdfreq\t Finds frequent pattern\n");

}


double patdfreq_point(int *max_pos){
  int i,j,n1, n2;
  double x, std_x, lambda, av, prob, oe;
  double max, max_std_x;
  
  *max_pos = 0;

/* Calculates average */
  n1 = 0; n2 = 0;
  if(avrange_up == 0 && avrange_dn == 0)
    for(i = 0;i < utrrange + cdsrange;i ++){
      n1 += obsdata[i];
      n2 += ndata[i];
    }
  else {
    for(i = utrrange - avrange_up;i <= utrrange + avrange_dn;i ++)
      if(i >= 0){
	n1 += obsdata[i];
	n2 += ndata[i];
      }
  }
  av = 1.0 * n1 / n2;

/* Statistical analysis based on normal and poisson distribution */
  max_std_x = -1000.0;

  if(cdsrange > 3)total_cds = ndata[utrrange + 3]; 
  else total_cds = ndata[ utrrange - numoli ]; /* temporary */

  for(i = utrrange - upmax;i <= utrrange - upmin;i ++){ /* ADJUST here */ 
    if(ndata[i] != 0){
      x = 1.0 * obsdata[i] / ndata[i];
      if(1 /* total_cds * av > 5 */){ /* Notice: Modify here */
	std_x = (obsdata[i] - ndata[i] * av)
	  / sqrt(ndata[i] * av * (1-av)); /* ndata[i] <-> total_cds?? */
/*
	prob = 0.5 - norm_half(std_x); 
	printf("Standard value for position %d is %lf(%d / %d)\n",
	       i - utrrange, std_x, obsdata[i], ndata[i]);
*/
      }
      else {
	std_x = 0.1; /* Temporaly this because evaluation basis is 
			different */
/*
	lambda = ndata[i] * av;
	std_x = obsdata[i] / (ndata[i] * av);  ndata[i] <-> total_cds?? 
	prob = poisson_over(obsdata[i], lambda);
*/
      }
      if(max_std_x < std_x){ max_std_x = std_x;
			     *max_pos = i - utrrange; }
    }
  }
  return max_std_x;
}



double patdfreq_point2(int *max_pos){
  int i,j,n1, n2;
  double x, std_x, lambda, av, prob, oe;
  double max, max_std_x;

  *max_pos = 0;

/* Calculates average */
  n1 = 0; n2 = 0;
  if(avrange_up == 0 && avrange_dn == 0)
    for(i = 0;i < utrrange + cdsrange;i ++){
      n1 += obsdata[i];
      n2 += ndata[i];
    }
  else {
    for(i = utrrange - avrange_up;i <= utrrange + avrange_dn;i ++)
      if(i >= 0){
	n1 += obsdata[i];
	n2 += ndata[i];
      }
  }
  av = 1.0 * n1 / n2;

  max = 0;
  for(i = utrrange - upmax;i <= utrrange - upmin;i ++){
    oe = 1.0 * obsdata[i] / ndata[i] / av;
    if(oe > max){
      max = oe;
      *max_pos = i - utrrange;
    }
  }
  return max;
}

double patdfreq_pointm(int *min_pos){
  int i,j,n1, n2;
  double x, std_x, lambda, av, prob, oe;
  double min, min_std_x;

/* Calculates average */
  n1 = 0; n2 = 0;
  if(avrange_up == 0 && avrange_dn == 0)
    for(i = 0;i < utrrange + cdsrange;i ++){
      n1 += obsdata[i];
      n2 += ndata[i];
    }
  else {
    for(i = utrrange - avrange_up;i <= utrrange + avrange_dn;i ++)
      if(i >= 0){
	n1 += obsdata[i];
	n2 += ndata[i];
      }
  }
  av = 1.0 * n1 / n2;

  *min_pos = 0;

/* Statistical analysis based on normal and poisson distribution */
  min_std_x = 1000.0;

  if(cdsrange > 3)total_cds = ndata[utrrange + 3]; 
  else total_cds = ndata[ utrrange - numoli ]; /* temporary */

  for(i = utrrange - upmax;i <= utrrange - upmin;i ++){ /* ADJUST here */ 
    if(ndata[i] != 0){
      x = 1.0 * obsdata[i] / ndata[i];
      if(total_cds * av > 5){
	std_x = (obsdata[i] - ndata[i] * av)
	  / sqrt(ndata[i] * av * (1-av)); /* ndata[i] <-> total_cds?? */
/*
	prob = 0.5 - norm_half(std_x); 
	printf("Standard value for position %d is %lf(%d / %d)\n",
	       i - utrrange, std_x, obsdata[i], ndata[i]);
*/
      }
      else {
	std_x = 0.1; /* Temporaly this because evaluation basis is 
			different */
/*
	lambda = ndata[i] * av;
	std_x = obsdata[i] / (ndata[i] * av);  ndata[i] <-> total_cds?? 
	prob = poisson_over(obsdata[i], lambda);
*/
      }
      if(min_std_x > std_x){ min_std_x = std_x;
			     *min_pos = i - utrrange; }
    }
  }
  return min_std_x;
}

int patefreq_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-patefreq") == 0){
    upmax = atoi(argv[n + 1]);
    upmin = atoi(argv[n + 2]);
    numoli = atoi(argv[n + 3]);
    return 4;
  }
  else return 0;
}

void patefreq_head(char *line){

}

void patefreq_ent(struct gparam *entry_info, char seqn[], int max,
		  struct cds_info cds[], int ncds){

  int i,j,k;
  static char mpat[20]; 
  char *compseqn;
  double po;
  int max_pos;

  compseqn = compseqget(seqn, max);

  for(i = 0;i < numoli - 1;i ++)
    mpat[i] = 'g';
  mpat[i] = '\0';

  while(next_patsp(mpat, numoli) == 0){
    for(i = 0;i < utrrange + cdsrange; i ++)
      ndata[i] = obsdata[i] = 0;
    patfreq_prerec(entry_info, seqn, compseqn, max, cds, ncds, mpat);
    if(smooth_value > 1)smoother();
    po = patdfreq_point(&max_pos);
    printf("%s %lf %d\n",mpat, po, max_pos);
  }
  free(compseqn);
}

void patefreq_fin(){



}

void patefreq_help(){

  printf("-patefreq\t Prints statistically frequent patterns:");
  printf("Specify scope and oligo length:ex:30 10 4 -> 30 bases upstream to 10 bases upstream ");
  printf("and length of oligonucleotide is 4\n");

}


int patefreq2_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-patefreq2") == 0){
    upmax = atoi(argv[n + 1]);
    upmin = atoi(argv[n + 2]);
    numoli = atoi(argv[n + 3]);
    return 4;
  }
  else return 0;
}

void patefreq2_head(char *line){

}

void patefreq2_ent(struct gparam *entry_info, char seqn[], int max,
		  struct cds_info cds[], int ncds){

  int i,j,k;
  static char mpat[20]; 
  char *compseqn;
  double po;
  int max_pos;

  compseqn = compseqget(seqn, max);

  for(i = 0;i < numoli - 1;i ++)
    mpat[i] = 'g';
  mpat[i] = '\0';

  while(next_patsp(mpat, numoli) == 0){
    for(i = 0;i < utrrange + cdsrange; i ++)
      ndata[i] = obsdata[i] = 0;
    patfreq_prerec(entry_info, seqn, compseqn, max, cds, ncds, mpat);
    if(smooth_value > 1)smoother();
    po = patdfreq_pointm(&max_pos);
    printf("%s %lf %d\n",mpat, po, max_pos);
  }
  free(compseqn);
}

void patefreq2_fin(){



}

void patefreq2_help(){

  printf("-patefreq2\t Prints statistically low frequent patterns:");
  printf("Specify scope and oligo length:ex:30 10 4 -> 30 bases upstream to 10 bases upstream ");
  printf("and length of oligonucleotide is 4\n");

}




int patefreq3_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-patefreq3") == 0){
    upmax = atoi(argv[n + 1]);
    upmin = atoi(argv[n + 2]);
    numoli = atoi(argv[n + 3]);
    return 4;
  }
  else return 0;
}

void patefreq3_head(char *line){

}

void patefreq3_ent(struct gparam *entry_info, char seqn[], int max,
		   struct cds_info cds[], int ncds){


  int i,j,k;
  static char mpat[20]; 
  char *compseqn;
  double po;
  int max_pos;

  compseqn = compseqget(seqn, max);

/* clears best records */
  for(i = 0;i < MAX_REC;i ++)
    rec[i].value = 0.0;

/* fills mpat with "gggg..g" which will be "aaaa..a" in the next turn */
  for(i = 0;i < numoli - 1;i ++)
    mpat[i] = 'g';
  mpat[i] = '\0';

  while(next_patsp(mpat, numoli) == 0){
    for(i = 0;i < utrrange + cdsrange; i ++)
      ndata[i] = obsdata[i] = 0;
    patfreq_prerec(entry_info, seqn, compseqn, max, cds, ncds, mpat);
    if(smooth_value > 1)smoother();
    patbestrec(mpat);
  }
  free(compseqn);
}

void patefreq3_fin(){
  
  int i;
  for(i = 0;i < MAX_REC && rec[i].value != 0;i ++){
    printf("%s %lf %d   \t(%d)\n", rec[i].str1, rec[i].value, rec[i].int1,i);
  }
}


void patefreq3_help(){

  printf("-patefreq3\t Prints statistically frequent patterns:");
  printf("Specify scope and oligo length.ex:30 10 4 -> 30 bases upstream to 10 bases upstream ");
  printf("and length of oligonucleotide is 4. Same pattern may appear.\n");
}


void patbestrec(char mpat[]){

  int i,j,n1,n2, dummy;
  double x, std_f, lambda, av, prob, oe;
  double max, max_std_x;
  static struct recbest10 rcand;

/* Calculates average */
  n1 = 0; n2 = 0;
  if(avrange_up == 0 && avrange_dn == 0)
    for(i = 0;i < utrrange + cdsrange;i ++){
      n1 += obsdata[i];
      n2 += ndata[i];
    }
  else {
    for(i = utrrange - avrange_up;i <= utrrange + avrange_dn;i ++)
      if(i >= 0){
	n1 += obsdata[i];
	n2 += ndata[i];
      }
  }
  av = 1.0 * n1 / n2;

  for(i = utrrange - upmax;i <= utrrange - upmin;i ++){
    if(ndata[i] == 0)continue;
    if(ndata[i] * av < 5 || ndata[i] * (1-av) < 5)continue;
    std_f = (obsdata[i] - ndata[i] * av) / sqrt(ndata[i] * av * (1-av));
    rcand.value = std_f;
/*
    printf("Recorded %s:%lf %d\n", mpat, std_f, i - utrrange);
*/
    strcpy(rcand.str1, mpat);
    rcand.int1 = i - utrrange;
    dummy = candrec(&rcand, rec, MAX_REC);
/*
    if(dummy == 1)printf("Recorded %s:%lf %d\n", mpat, std_f, i - utrrange);
*/
  }

}


