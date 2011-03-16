/* 翻訳開始領域atgの前後の塩基を調べる (Original:佐々木秀和) */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int imput_n;
int kazu[256];

double count_a[256];
double count_t[256];
double count_c[256];
double count_g[256];


int  bun_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-bun")==0){
    imput_n=atoi(argv[n+1]); 
    return 2;
  }
  else return 0; 
}

void bun_head(char *line)
{
/*  
  if(head_n<5){
    if(find_word("mitochond",line)==1){mit_flag=1;}
    head_n++;
  }
*/
}

void bun_ent(char *entry, char *seqn, int max, int cds[], int ncds)
{
  int n;
  int i;
  if(ncds>0){
    for(n=0;n<ncds;n++)
      {
	/*printf("%d\n",imput_n);*/
	if(strncmp(&seqn[cds[n]-1],"atg",3)==0){
	  for(i=imput_n+1;i>-5;i--){
	    if(cds[n]-i<0){}/*printf("-");*/
	    else{
	      /*printf("%c",seqn[cds[n]-i]);*/
	      kazu[250-i]++;
	      if(seqn[cds[n]-i]=='a') count_a[250-i]++;
	      if(seqn[cds[n]-i]=='t') count_t[250-i]++;
	      if(seqn[cds[n]-i]=='c') count_c[250-i]++;
	      if(seqn[cds[n]-i]=='g') count_g[250-i]++;
	    }
	  }
	  /*printf("\n");*/
	}
      }
  }
}

void bun_fin()
{
  int i, j, n;
  double e_a01[256], e_t01[256], e_c01[256], e_g01[256], entropy01[256]; 

  /*goto xg;*/
  
  printf("%d",kazu[250-1]);
  for(i=imput_n+1;i>-5;i=i-12){
    printf("\n");
    printf(" a|");
    for(j=0;j<12 && i-j>-5;j++){
      printf("%5d ",(int)count_a[250-i+j]);
    }
    printf("\n");
    printf(" t|");
    for(j=0;j<12 && i-j>-5;j++){
      printf("%5d ",(int)count_t[250-i+j]);
    }
    printf("\n");
    printf(" c|");
    for(j=0;j<12 && i-j>-5;j++){
      printf("%5d ",(int)count_c[250-i+j]);
    }
    printf("\n");
    printf(" g|");
    for(j=0;j<12 && i-j>-5;j++){
      printf("%5d ",(int)count_g[250-i+j]);
    }
    printf("\n");
    
    printf("\n");
    printf(" a|");
    for(j=0;j<12 && i-j>-5;j++){
      printf("%5.1lf ",count_a[250-i+j]*100/kazu[250-i+j]);
    }
    printf("\n");
    printf(" t|");
    for(j=0;j<12 && i-j>-5;j++){
      printf("%5.1lf ",count_t[250-i+j]*100/kazu[250-i+j]);
    }
    printf("\n");
    printf(" c|");
    for(j=0;j<12 && i-j>-5;j++){
      printf("%5.1lf ",count_c[250-i+j]*100/kazu[250-i+j]);
    }
    printf("\n");
    printf(" g|");
    for(j=0;j<12 && i-j>-5;j++){
      printf("%5.1lf ",count_g[250-i+j]*100/kazu[250-i+j]);
    }
    printf("\n");
  }


/************************  エントロピー計算  **************************
 xg: ;
  for(i=imput_n+1;i>-5;i--){
    e_a01[250-i]=(double)count_a[250-i]/kazu[250-i];
    e_t01[250-i]=(double)count_t[250-i]/kazu[250-i];
    e_c01[250-i]=(double)count_c[250-i]/kazu[250-i];
    e_g01[250-i]=(double)count_g[250-i]/kazu[250-i];

    entropy01[250-i]= -e_a01[250-i] * (log_ent(e_a01[250-i]))                
		      -e_t01[250-i] * (log_ent(e_t01[250-i]))
                      -e_c01[250-i] * (log_ent(e_c01[250-i]))
                      -e_g01[250-i] * (log_ent(e_g01[250-i]));
    
    printf("%6d %lf\n",-i+1 ,entropy01[250-i]);
  }
***********************************************************************/
  
}
 
void bun_help()
{
  printf("-bun\t Profile analyses around translation initiation site\n");
}  





