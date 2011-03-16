/****************************************************************************/
/* atg後のストップコドンを調べる                                            */
/*                                                                          */
/*                                                    製作者                */
/*                                                    製作日 平成9年1月6日  */
/****************************************************************************/

int input_sp14;
int kazusp14[2][256];
double count_sp14[2][4][256];
char *sp14[4]={"taa","tag","tga","TOT"};


int stop_par14(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-stop14")==0){
    input_sp14=atoi(argv[n+1]);
    return 2;
  }
  else return 0;
}


stop_ent14(char *entry, char *seqn, int max, int cds[], int ncds)
{
  int i, j, type, n=0;
  int first=0;

  if(ncds>0){

    /** 開始コドン **/
    if(strncmp(&seqn[cds[n]-1],"atg",3)==0){
      for(i=1;i<=input_sp14 && cds[n]-1+i<max;i++){
	kazusp14[0][i]++;
	for(type=0;type<3;type++){
	  if(strncmp(&seqn[cds[n]-1+2+i],sp14[type],3)==0){ 
	    count_sp14[0][type][i]++;
	    count_sp14[0][  3 ][i]++;
	    /*if(i<=12) printf("%s\n",entry);*/
	  }
	}
      }
    }
    

    /** 最初のATG(非開始コドン) **/
    if(strncmp(&seqn[cds[0]-1],"atg",3)==0){
      for(j=0;j<cds[0]-3 && first==0;j++){
	if(strncmp(&seqn[j],"atg",3)==0){
	  first++;
	  for(i=1;i<=input_sp14 && j+i<cds[0]-1;i++){
	    kazusp14[1][i]++;
	    for(type=0;type<3;type++){
	      if(strncmp(&seqn[j+2+i],sp14[type],3)==0){
		count_sp14[1][type][i]++;
		count_sp14[1][  3 ][i]++;
	      }
	    }
	  }
	}
      }
    }
    
  }
}

stop_fin14()
{
  int i, j, type, sw1, sw2;

  goto xg;

  for(sw1=0;sw1<2;sw1++){/*1*/
    printf("%d",(int)kazusp14[sw1][1]);
    for(i=1;i<=input_sp14;i+=12){/*2*/
      for(sw2=0;sw2<2;sw2++){/*3*/
	for(type=0;type<5;type++){/*4*/
	  if(type==4){/*5*/
	    printf("\n ALL|");
	    for(j=0;j<12 && i+j<=input_sp14;j++){/*6*/
	      if(sw2==0) printf("%6d",(int)kazusp14[sw1][i+j]);
	      else printf("%6.1lf",(double)kazusp14[sw1][i+j]*100/kazusp14[sw1][i+j]);
	      /*6*/}
	    /*5*/}
	  
	  else{/*7*/
	    printf("\n %s|",sp14[type]);
	    for(j=0;j<12 && i+j<=input_sp14;j++){/*8*/
	      if(sw2==0) printf("%6d",(int)count_sp14[sw1][type][i+j]);
	      else printf("%6.2lf",(double)count_sp14[sw1][type][i+j]*100/kazusp14[sw1][i+j]);
	      /*8*/}
	    /*7*/}
	  /*4*/}
	printf("\n");
	/*3*/}
      /*2*/}
    printf("\n");
    /*1*/}



/*************************** xgraph_data ****************************/
  xg: ;

  for(i=1;i<=input_sp14;i++){
    if(count_sp14[1][3][i]>0){
      printf("%6d %.2lf\n",i,(double)count_sp14[1][3][i]*100/kazusp14[1][i]);
    }
  }
  printf("\n");
  

/********************************************************************/

}


stop_help14()
{
  printf("-stop14\t atg後の終止コドンを調べる \n");
  printf("         コドン数を指定して下さい \n");
}  

