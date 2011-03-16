/****************************************************************************/
/* Relationship of Leaky Scaning to Distance between NS-AUG and S-AUG       */
/*                                                                          */
/*                                                         Prog H.Sasaki    */
/*                                                         Date Nov.11 1996 */
/****************************************************************************/

#define width07 30


int kazu07[256];
int count07[2][256];



int bun7_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-bun7")==0){
    return 1;
  }
  else return 0;
}


bun7_ent(char *entry, char *seqn, int max, int cds[], int ncds)
{
  int i, n;
  
  if(ncds>0){
    for(n=0;n<ncds;n++){/*0*/
      if(strncmp(&seqn[cds[n]-1],"atg",3)==0){/*1*/
	for(i=0;i<width07;i++){/*2*/
	  
	  /** NS-ATG >>> S-ATG **/
	  if(strncmp(&seqn[cds[n]-1-3-i],"atg",3)==0){/*3*/
	    kazu07[i]++;
	    count07[0][i]++;
	    /*3*/}
	  
	  /** S-ATG >>> NS-ATG **/
	  if(strncmp(&seqn[cds[n]-1+3+i],"atg",3)==0){/*4*/
	    kazu07[i]++;
	    count07[1][i]++;
	    /*4*/}
	  
	  /*2*/}
	/*1*/}
      /*0*/}
  }
}

bun7_fin()
{
  int i;

  for(i=0;i<width07;i++){
      printf("%6d %6.2lf\n",i ,(double)count07[0][i]*100/kazu07[i]);
  }
}


bun7_help()
{
  printf("-bun7\t Relationship of Leaky Scaning to Distance between NS-AUG and S-AUG \n");
}  




