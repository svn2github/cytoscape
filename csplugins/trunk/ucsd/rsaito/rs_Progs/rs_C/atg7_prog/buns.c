/* 翻訳開始領域atgの前後の塩基を調べる */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "global_st.h"
#include "atg_func.h"

#define BUN_BUF 1000
#define S_POINT 249

static int bun_upstream;
static int bun_downstream;
static int kazu2;
static int betdist;

static double bcnt_atgc[BUN_BUF];
static double bcnt_a[BUN_BUF];
static double bcnt_t[BUN_BUF];
static double bcnt_c[BUN_BUF];
static double bcnt_g[BUN_BUF];

static int buns_a, buns_t, buns_c, buns_g;

static int entro_mode;
static int compl_mode;

static char scodon[10];

double logmod2(double x){
  if(x == 0)return (double)0;
  else return log(x);
}

void buns_ent_fin(void);
void buns_ic_fin(void);
void buns_rseq_fin();
void buns_chi_fin();
void buns_iim_fin();

void buns_rec(char *, int, int, int, int,
         int, double *, 
	 double *, double *, double *, double *);

void buns_rec_comp(char *, int, int, int, int,
         int , double *, 
	 double *, double *, double *, double *);


int  buns_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-buns")==0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    entro_mode = 0;
    compl_mode = 1;
    return 3;
  }

  else if(strcmp(argv[n], "-buns_pat")==0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    strcpy(scodon, argv[n+3]);
    kazu2 = 0;
    entro_mode = 0;
    compl_mode = 1;
    return 4;
  }

  else if(strcmp(argv[n], "-buns_old")==0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    entro_mode = 0;
    compl_mode = 0;
    return 3;
  }

  else if(strcmp(argv[n], "-buns_ent") == 0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    entro_mode = 1;
    compl_mode = 1;
    return 3;
  }

  else if(strcmp(argv[n], "-buns_ic") == 0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    entro_mode = 2;
    compl_mode = 1;
    return 3;
  }

  else if(strcmp(argv[n], "-buns_rseq") == 0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    entro_mode = 4;
    compl_mode = 1;
    return 3;
  }

  else if(strcmp(argv[n], "-buns_chi") == 0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    entro_mode = 5;
    compl_mode = 1;
    return 3;
  }

  else if(strcmp(argv[n], "-buns_iim") == 0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    entro_mode = 3;
    compl_mode = 1;
    return 3;
  }

  else if(strcmp(argv[n], "-buns_ent_pat") == 0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    strcpy(scodon, argv[n+3]);
    kazu2 = 0;
    entro_mode = 1;
    compl_mode = 1;
    return 4;
  }

  else return 0; 
}

int  bunskip_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-bunskip") == 0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    entro_mode = 0;
    kazu2 = 0;
    return 3;
  }
  else return 0; 
}

void buns_head(char *line)
{
}

void buns_ent(char *entry, char *seqn, int max, 
	 struct cds_info cds[], int ncds)
{
  int n;
  int i;

  char cmplseq[20];

  if(ncds>0){
    for(n = 1 ;n <= max;n ++){
      switch(seqn[n - 1]){
         case 'a':buns_a ++; break;
         case 't':buns_t ++; break;
         case 'c':buns_c ++; break;
         case 'g':buns_g ++; break;
      }
    }

    for(n=0;n<ncds;n++){
/*
      printf("%d:%s",kazu2,entry);
      if(cds[n].cds_start > 0){
	for(i = cds[n].cds_start;i < cds[n].cds_start +3;i ++)
	  putchar(seqn[i - 1]);
	putchar('\n');
      }
*/
      if(valid_cds[n] == 0)continue;
      if(cds[n].complement == 0 && cds[n].cds_start > 0){

	if(compl_mode == 0 
	   && strncmp(&seqn[cds[n].cds_start - 1],"atg",3) != 0)continue;

	if(strncmp(&seqn[cds[n].cds_start - 1], scodon, strlen(scodon))
	   != 0)continue;
/*	   
	if(strncmp(&seqn[cds[n].cds_start - 1 - 6], "taa", strlen("taa"))
	   != 0)continue;
*/
	buns_rec(seqn, max, cds[n].cds_start - 1, 
		 bun_upstream, bun_downstream,
		 S_POINT, bcnt_atgc, bcnt_a, bcnt_t, bcnt_c, bcnt_g);
	kazu2 ++;
      }
      else if(cds[n].complement == 1 && cds[n].cds_end > 0 && compl_mode){

	for(i = cds[n].cds_end - 1; i > cds[n].cds_end - 1 - 10; i --){
	  cmplseq[ cds[n].cds_end - 1 - i ] = cmpl(seqn[i]);
	}

	if(strncmp(cmplseq, scodon, strlen(scodon))
	   != 0)continue;
/*
	if(strncmp(&seqn[cds[n].cds_end - 1 + 6 - 2], "tta", strlen("tta"))
	   != 0)continue;
*/	
	buns_rec_comp(seqn, max, cds[n].cds_end - 1, 
		 bun_upstream, bun_downstream,
		 S_POINT, bcnt_atgc, bcnt_a, bcnt_t, bcnt_c, bcnt_g);
	kazu2 ++;
      }
    }
  }
}

void bunskip_ent(char *entry, char *seqn, int max, 
	 struct cds_info cds[], int ncds)
{
  int n;
  int i;
  if(ncds>0 && cds[0].complement == 0 && cds[0].cds_start > 0){
    for(n = 1 ;n <= max;n ++){
      switch(seqn[n - 1]){
         case 'a':buns_a ++; break;
         case 't':buns_t ++; break;
         case 'c':buns_c ++; break;
         case 'g':buns_g ++; break;
      }
    }

    for(i = 0;i < cds[0].cds_start - 3;i ++){
      if(strncmp(&seqn[i], "atg", 3) == 0){
	buns_rec(seqn, max, i,
		 bun_upstream, bun_downstream,
		 S_POINT, bcnt_atgc, bcnt_a, bcnt_t, bcnt_c, bcnt_g);
	kazu2 ++;
      }
    }
  }
}


void buns_fin()
{
  int i, j, n;

  if(entro_mode == 1){ buns_ent_fin(); return; }
  else if(entro_mode == 2){ buns_ic_fin(); return; }
  else if(entro_mode == 3){ buns_iim_fin(); return; }
  else if(entro_mode == 4){ buns_rseq_fin(); return; }
  else if(entro_mode == 5){ buns_chi_fin(); return; }
  n = buns_a + buns_t + buns_c + buns_g;

  printf("a:%d(%.2lf%%) t:%d(%.2lf%%) c:%d(%.2lf%%) g:%d(%.2lf%%)\n",
	 buns_a, 1.0*buns_a/n,	 buns_t, 1.0*buns_t/n,
 	 buns_c, 1.0*buns_c/n,    buns_g, 1.0*buns_g/n);

  printf("%d",kazu2);
  
  for(i=bun_upstream+1;i>-bun_downstream;i=i-12){
    printf("\n");

    printf("Pos");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      if(-(i-j-1) < 0)printf("%5d ",-(i-j-1));
      else printf("%+5d ",-(i-j-1) + 1);
    }
    putchar('\n');

    printf("---");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("----- ");
    }
    putchar('\n');

    printf(" a|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("%5d ",(int)bcnt_a[S_POINT+1-i+j]);
    }
    printf("\n");
    printf(" t|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("%5d ",(int)bcnt_t[S_POINT+1-i+j]);
    }
    printf("\n");
    printf(" c|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("%5d ",(int)bcnt_c[S_POINT+1-i+j]);
    }
    printf("\n");
    printf(" g|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("%5d ",(int)bcnt_g[S_POINT+1-i+j]);
    }
    printf("\n");
    
    printf("\n");

  }
  printf("\n");
  for(i=bun_upstream+1;i>-bun_downstream;i=i-12){
    printf("\n");
    printf("Pos");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      if(-(i-j-1) < 0)printf("%5d ",-(i-j-1));
      else printf("%+5d ",-(i-j-1) + 1);
    }
    putchar('\n');

    printf("---");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("----- ");
    }
    putchar('\n');

    printf(" a|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("%5.1lf ",bcnt_a[S_POINT+1-i+j]*100/bcnt_atgc[S_POINT+1-i+j]);
    }
    printf("\n");
    printf(" t|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("%5.1lf ",bcnt_t[S_POINT+1-i+j]*100/bcnt_atgc[S_POINT+1-i+j]);
    }
    printf("\n");
    printf(" c|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("%5.1lf ",bcnt_c[S_POINT+1-i+j]*100/bcnt_atgc[S_POINT+1-i+j]);
    }
    printf("\n");
    printf(" g|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("%5.1lf ",bcnt_g[S_POINT+1-i+j]*100/bcnt_atgc[S_POINT+1-i+j]);
    }
    printf("\n");
  }

  printf("\n");
  for(i=bun_upstream+1;i>-bun_downstream;i=i-12){
    printf("\n");
    printf("Pos");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      if(-(i-j-1) < 0)printf("%5d ",-(i-j-1));
      else printf("%+5d ",-(i-j-1) + 1);
    }
    putchar('\n');

    printf("---");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf("----- ");
    }
    putchar('\n');

    printf(" a|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf(" %4.2lf ",1.0*bcnt_a[S_POINT+1-i+j]/bcnt_atgc[S_POINT+1-i+j]/
	     (1.0 * buns_a / n));
    }
    printf("\n");
    printf(" t|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf(" %4.2lf ",1.0*bcnt_t[S_POINT+1-i+j]/bcnt_atgc[S_POINT+1-i+j]/
	     (1.0 * buns_t / n));
    }
    printf("\n");
    printf(" c|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf(" %4.2lf ",1.0*bcnt_c[S_POINT+1-i+j]/bcnt_atgc[S_POINT+1-i+j]/
	     (1.0 * buns_c / n));
    }
    printf("\n");
    printf(" g|");
    for(j=0;j<12 && i-j>-bun_downstream;j++){
      printf(" %4.2lf ",1.0*bcnt_g[S_POINT+1-i+j]/bcnt_atgc[S_POINT+1-i+j]/
	     (1.0 * buns_g / n));
    }
    printf("\n");
  }

  printf("\n");

  printf("end\n");
}
 

void buns_ent_fin(){

  int i;
  double loc_a_rat, loc_t_rat, loc_c_rat, loc_g_rat, loc_total, loc_ent;
  double a_ratio, t_ratio, c_ratio, g_ratio;

  a_ratio = 1.0 * buns_a / (buns_a + buns_t + buns_c + buns_g);
  t_ratio = 1.0 * buns_t / (buns_a + buns_t + buns_c + buns_g);
  c_ratio = 1.0 * buns_c / (buns_a + buns_t + buns_c + buns_g);
  g_ratio = 1.0 * buns_g / (buns_a + buns_t + buns_c + buns_g);
 
  for(i = -bun_upstream; i <= bun_downstream; i++){

    loc_a_rat = bcnt_a[i + S_POINT] / bcnt_atgc[i + S_POINT];
    loc_t_rat = bcnt_t[i + S_POINT] / bcnt_atgc[i + S_POINT];
    loc_c_rat = bcnt_c[i + S_POINT] / bcnt_atgc[i + S_POINT];
    loc_g_rat = bcnt_g[i + S_POINT] / bcnt_atgc[i + S_POINT];
    
    loc_a_rat = loc_a_rat / a_ratio;
    loc_t_rat = loc_t_rat / t_ratio;
    loc_c_rat = loc_c_rat / c_ratio;
    loc_g_rat = loc_g_rat / g_ratio;

    loc_total = loc_a_rat + loc_t_rat + loc_c_rat + loc_g_rat;
    loc_ent = - loc_a_rat / loc_total * 
      logmod2(loc_a_rat / loc_total)/logmod2(2.0)
              - loc_t_rat / loc_total * 
      logmod2(loc_t_rat / loc_total)/logmod2(2.0) 
	      - loc_c_rat / loc_total * 
      logmod2(loc_c_rat / loc_total)/logmod2(2.0)
	      - loc_g_rat / loc_total * 
      logmod2(loc_g_rat / loc_total)/logmod2(2.0);

    printf("%d %lf\n", i, loc_ent);
  }

}

void buns_ic_fin(){

  int i;
  double loc_a_rat, loc_t_rat, loc_c_rat, loc_g_rat, loc_total, loc_ic;
  double a_ratio, t_ratio, c_ratio, g_ratio;

  a_ratio = 1.0 * buns_a / (buns_a + buns_t + buns_c + buns_g);
  t_ratio = 1.0 * buns_t / (buns_a + buns_t + buns_c + buns_g);
  c_ratio = 1.0 * buns_c / (buns_a + buns_t + buns_c + buns_g);
  g_ratio = 1.0 * buns_g / (buns_a + buns_t + buns_c + buns_g);
 
  for(i = -bun_upstream; i <= bun_downstream; i++){

    loc_a_rat = bcnt_a[i + S_POINT] / bcnt_atgc[i + S_POINT];
    loc_t_rat = bcnt_t[i + S_POINT] / bcnt_atgc[i + S_POINT];
    loc_c_rat = bcnt_c[i + S_POINT] / bcnt_atgc[i + S_POINT];
    loc_g_rat = bcnt_g[i + S_POINT] / bcnt_atgc[i + S_POINT];
    
    loc_ic = logmod2(loc_a_rat/a_ratio)/logmod2(2.0)*loc_a_rat+ 
	     logmod2(loc_t_rat/t_ratio)/logmod2(2.0)*loc_t_rat+
	     logmod2(loc_c_rat/c_ratio)/logmod2(2.0)*loc_c_rat+
	     logmod2(loc_g_rat/g_ratio)/logmod2(2.0)*loc_g_rat;

    printf("%d %lf\n", i, loc_ic);
  }
}


void buns_chi_fin(){

  int i;
  double e_a, e_c, e_g, e_t, chi;
  double a_ratio, t_ratio, c_ratio, g_ratio;

  a_ratio = 1.0 * buns_a / (buns_a + buns_t + buns_c + buns_g);
  t_ratio = 1.0 * buns_t / (buns_a + buns_t + buns_c + buns_g);
  c_ratio = 1.0 * buns_c / (buns_a + buns_t + buns_c + buns_g);
  g_ratio = 1.0 * buns_g / (buns_a + buns_t + buns_c + buns_g);

  for(i = -bun_upstream; i <= bun_downstream; i++){
    e_a = a_ratio * bcnt_atgc[i + S_POINT];
    e_c = c_ratio * bcnt_atgc[i + S_POINT];
    e_g = g_ratio * bcnt_atgc[i + S_POINT];
    e_t = t_ratio * bcnt_atgc[i + S_POINT];
    
    chi =
      + (bcnt_a[i + S_POINT] - e_a) * (bcnt_a[i + S_POINT] - e_a)
      / e_a
      + (bcnt_c[i + S_POINT] - e_c) * (bcnt_c[i + S_POINT] - e_c)
      / e_c
      + (bcnt_g[i + S_POINT] - e_g) * (bcnt_g[i + S_POINT] - e_g)
      / e_g
      + (bcnt_t[i + S_POINT] - e_t) * (bcnt_t[i + S_POINT] - e_t)
      / e_t;

    printf("%d %lf\n", i, chi);

  }
}


void buns_rseq_fin(){

  int i;
  int nseql;
  double loc_a_rat, loc_t_rat, loc_c_rat, loc_g_rat, loc_total, loc_rseq;
  double a_ratio, t_ratio, c_ratio, g_ratio;
  double hgenome;

  a_ratio = 1.0 * buns_a / (buns_a + buns_t + buns_c + buns_g);
  t_ratio = 1.0 * buns_t / (buns_a + buns_t + buns_c + buns_g);
  c_ratio = 1.0 * buns_c / (buns_a + buns_t + buns_c + buns_g);
  g_ratio = 1.0 * buns_g / (buns_a + buns_t + buns_c + buns_g);

  hgenome = - a_ratio * logmod2(a_ratio)/logmod2(2.0)
            - t_ratio * logmod2(t_ratio)/logmod2(2.0)
            - c_ratio * logmod2(c_ratio)/logmod2(2.0)
            - g_ratio * logmod2(g_ratio)/logmod2(2.0);
 
  for(i = -bun_upstream; i <= bun_downstream; i++){

    nseql = (int)(bcnt_atgc[i + S_POINT]);

    loc_a_rat = bcnt_a[i + S_POINT] / bcnt_atgc[i + S_POINT];
    loc_t_rat = bcnt_t[i + S_POINT] / bcnt_atgc[i + S_POINT];
    loc_c_rat = bcnt_c[i + S_POINT] / bcnt_atgc[i + S_POINT];
    loc_g_rat = bcnt_g[i + S_POINT] / bcnt_atgc[i + S_POINT];
    
    loc_rseq = hgenome - (4-1)/(2.0 * logmod2(2.0) * nseql) +
               logmod2(loc_a_rat)/logmod2(2.0)*loc_a_rat + 
               logmod2(loc_t_rat)/logmod2(2.0)*loc_t_rat +
	       logmod2(loc_c_rat)/logmod2(2.0)*loc_c_rat +
	       logmod2(loc_g_rat)/logmod2(2.0)*loc_g_rat;

    printf("%d %lf\n", i, loc_rseq);
        
    printf("buns: a:%d t:%d c:%d g:%d hgenome:%lf\n", 
	   buns_a, buns_t,buns_c,buns_g,hgenome);
    printf("Loc: a:%.1lf t:%.1lf c:%.1lf g:%.1lf all:%d\n",
	   bcnt_a[i + S_POINT],bcnt_t[i + S_POINT],
	   bcnt_c[i + S_POINT],bcnt_g[i + S_POINT], nseql);
    printf("Ent: %lf\n",
	   - logmod2(loc_a_rat)/logmod2(2.0)*loc_a_rat 
	   - logmod2(loc_t_rat)/logmod2(2.0)*loc_t_rat 
	   - logmod2(loc_c_rat)/logmod2(2.0)*loc_c_rat 
	   - logmod2(loc_g_rat)/logmod2(2.0)*loc_g_rat);

    printf("modifier: %lf\n", - (4-1)/(2.0 * logmod2(2.0) * nseql));


    putchar('\n');
    
  }
}


void buns_iim_fin(){

  int i;
  int nseql;
  double bfreq;
  double wmv;

  double a_ratio, t_ratio, c_ratio, g_ratio;
  double hgenome;

  a_ratio = 1.0 * buns_a / (buns_a + buns_t + buns_c + buns_g);
  t_ratio = 1.0 * buns_t / (buns_a + buns_t + buns_c + buns_g);
  c_ratio = 1.0 * buns_c / (buns_a + buns_t + buns_c + buns_g);
  g_ratio = 1.0 * buns_g / (buns_a + buns_t + buns_c + buns_g);

  hgenome = - a_ratio * logmod2(a_ratio)/logmod2(2.0)
            - t_ratio * logmod2(t_ratio)/logmod2(2.0)
            - c_ratio * logmod2(c_ratio)/logmod2(2.0)
            - g_ratio * logmod2(g_ratio)/logmod2(2.0);

  printf("Genome entropy:%lf\n", hgenome);

  printf("pos");
  for(i = -bun_upstream;i <= bun_downstream;i ++){
    printf("       %+3d",i);
  }
  putchar('\n');

  printf(" a|");
  for(i = -bun_upstream;i <= bun_downstream;i ++){
    bfreq = bcnt_a[i + S_POINT] / bcnt_atgc[i + S_POINT];
    if(bfreq == 0.0)bfreq = 1.0 / (bcnt_atgc[i + S_POINT] + 2);
    nseql = (int)(bcnt_atgc[i + S_POINT]);
    wmv = hgenome - (4-1)/(2.0 * logmod2(2.0) * nseql) + 
      logmod2(bfreq)/logmod2(2.0);
    printf(" %+3.6lf", wmv);
  }
  putchar('\n');

  printf(" t|");
  for(i = -bun_upstream;i <= bun_downstream;i ++){
    bfreq = bcnt_t[i + S_POINT] / bcnt_atgc[i + S_POINT];
    if(bfreq == 0.0)bfreq = 1.0 / (bcnt_atgc[i + S_POINT] + 2);
    nseql = (int)(bcnt_atgc[i + S_POINT]);
    wmv = hgenome - (4-1)/(2.0 * logmod2(2.0) * nseql) + 
      logmod2(bfreq)/logmod2(2.0);
    printf(" %+3.6lf", wmv);
  }
  putchar('\n');

  printf(" c|");
  for(i = -bun_upstream;i <= bun_downstream;i ++){
    bfreq = bcnt_c[i + S_POINT] / bcnt_atgc[i + S_POINT];
    if(bfreq == 0.0)bfreq = 1.0 / (bcnt_atgc[i + S_POINT] + 2);
    nseql = (int)(bcnt_atgc[i + S_POINT]);
    wmv = hgenome - (4-1)/(2.0 * logmod2(2.0) * nseql) + 
      logmod2(bfreq)/logmod2(2.0);
    printf(" %+3.6lf", wmv);
  }
  putchar('\n');

  printf(" g|");
  for(i = -bun_upstream;i <= bun_downstream;i ++){
    bfreq = bcnt_g[i + S_POINT] / bcnt_atgc[i + S_POINT];
    if(bfreq == 0.0)bfreq = 1.0 / (bcnt_atgc[i + S_POINT] + 2);
    nseql = (int)(bcnt_atgc[i + S_POINT]);
    wmv = hgenome - (4-1)/(2.0 * logmod2(2.0) * nseql) + 
      logmod2(bfreq)/logmod2(2.0);
    printf(" %+3.6lf", wmv);
  }
  putchar('\n');

}





void buns_help()
{
  printf("-buns\t Displays profile around start AUG(state 2 numbers)\n");
  printf("-buns_pat\t Displays profile around start AUG(state 2 numbers and pattern)\n");
  printf("-buns_old\t Displays profile(Do not accept complement sequences\n");
  printf("-buns_ent\t Calculates normalized entropy\n");
  printf("-buns_ic\t Calculates information content\n");
  printf("-buns_rseq\t Calculates rseq\n");
  printf("-buns_iim\t Individual information matrix\n");
  printf("-buns_ent_pat\t Calculates normalized entropy with specified start codon\n");
}  

void bunskip_help()
{
  printf("-bunskip\t Displays profile around skipped AUGs(state 2 num)\n");
}


int  bune_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-bune")==0){
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    return 3;
  }
  else return 0; 
}

void bune_ent(char *entry, char *seqn, int max, 
	 struct cds_info cds[], int ncds)
{
  int n;
  int i;
  if(ncds>0){
    for(n=0;n<ncds;n++){
      kazu2++;
      if(cds[n].complement == 0 && cds[n].cds_end > 0)
	buns_rec(seqn, max, cds[n].cds_end - 1 - 2, 
		 bun_upstream, bun_downstream,
		 S_POINT, bcnt_atgc, bcnt_a, bcnt_t, bcnt_c, bcnt_g);
      else if(cds[n].cds_start > 0)
	buns_rec_comp(seqn, max, cds[n].cds_start - 1 + 2, 
		 bun_upstream, bun_downstream,
		 S_POINT, bcnt_atgc, bcnt_a, bcnt_t, bcnt_c, bcnt_g);
      
    }
  }
}

void bune_help()
{
  printf("-bune\t 翻訳終了領域atgの前後の塩基を調べる(塩基数２つ指定)\n");
}  


int bun_lk_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-bun_lk") == 0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    entro_mode = 0;
    return 3;
  }
  else if(strcmp(argv[n], "-bun_lk_ent") == 0){
    buns_a = 0; buns_t = 0; buns_c = 0; buns_g = 0;
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    kazu2 = 0;
    entro_mode = 1;
    return 3;
  }
  else return 0;
}

void bun_lk_head(char *line)
{

}

void bun_lk_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i,n;

  if(ncds == 0 || cds[0].cds_start == 0 || cds[0].complement == 1 ||
     valid_cds[0] == 0)return;  
  
  for(n = 1 ;n <= max;n ++){
    switch(seqn[n - 1]){
    case 'a':buns_a ++; break;
    case 't':buns_t ++; break;
    case 'c':buns_c ++; break;
    case 'g':buns_g ++; break;
    }
  }

  for(i = 0;i < cds[0].cds_start - 3;i ++)
    if(strncmp(&seqn[i],"atg",3) == 0){
      buns_rec(seqn, max, i, bun_upstream, bun_downstream,
	       S_POINT, bcnt_atgc, bcnt_a, bcnt_t, bcnt_c, bcnt_g);
      kazu2 ++;
    }
}

void bun_lk_fin(){

  buns_fin();

}


void bun_lk_help(){

  printf("-bun_lk\t Displays profile around leaky scanned AUGs. State upstream and downstream.\n");
  printf("-bun_lk_ent\t Calculates entropy around leaky scanned AUGs. State upstream and downstream.\n");
/*
  printf("-bun_lk\t leaky scanning AUG前後の配列を調べる（塩基数２つ指定）\n");
*/
}



int buns2_par(int argc, char *argv[], int n){

  if(strcmp(argv[n],"-buns2") == 0){
    bun_upstream=atoi(argv[n+1]); 
    bun_downstream=atoi(argv[n+2]);
    betdist = atoi(argv[n+3]);
    kazu2 = 0;
    return 4;
  }
  else return 0;
}

void buns2_ent(char *entry, char *seqn, int max, int cds[], int ncds){

  int n;

  for(n = 0;n < ncds;n ++){
    if(cds[n] > 0 && cds[n]+betdist+3 > 0 && cds[n]+betdist+3 <= max &&
       strncmp(&seqn[cds[n]-1],"atg",3) == 0 &&
       strncmp(&seqn[cds[n]-1 + betdist + 3],"atg",3) == 0){
      buns_rec(seqn, max, cds[n] - 1, 
	       bun_upstream, bun_downstream,
	       S_POINT, bcnt_atgc, bcnt_a, bcnt_t, bcnt_c, bcnt_g);
      kazu2 ++;
    }
  }
}

void buns2_help(){
  
  printf("-buns2\t State distance between two AUGs(3 numbers)\n");

}


void buns_rec(char seq[], int max, int spoint, int upst_n, int dnst_n,
         int rec_s, double atgc_buf[], 
	 double a_buf[], double t_buf[], double c_buf[], double g_buf[])
{
  int i;

  for(i = -upst_n;i <= dnst_n;i ++)
    if(i + spoint >= 0 && i + spoint < max){
      atgc_buf[rec_s + i] ++;
      switch(seq[i + spoint]){
        case 'a':a_buf[rec_s + i] ++;break;
	case 't':t_buf[rec_s + i] ++;break;
	case 'c':c_buf[rec_s + i] ++;break;
	case 'g':g_buf[rec_s + i] ++;break;
      }
    }
}

void buns_rec_comp(char seq[], int max, int spoint, int upst_n, int dnst_n,
         int rec_s, double atgc_buf[], 
	 double a_buf[], double t_buf[], double c_buf[], double g_buf[])
{
  int i;

  for(i = -upst_n;i <= dnst_n;i ++)
    if(spoint - i < max && spoint - i >= 0){
      atgc_buf[rec_s + i] ++;
      switch(seq[spoint - i]){
        case 'a':t_buf[rec_s + i] ++;break;
	case 't':a_buf[rec_s + i] ++;break;
	case 'c':g_buf[rec_s + i] ++;break;
	case 'g':c_buf[rec_s + i] ++;break;
      }
    }
}










