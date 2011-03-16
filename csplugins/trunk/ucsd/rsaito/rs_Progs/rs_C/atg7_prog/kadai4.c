#include<stdio.h>
#include<string.h>
main(int argc,char *argv[])
{
  FILE *fp; 
  int i,count=0,nn; 
  char c,line[256],nuc[262144];
  if(argc != 3){ printf("parameter error\n"); exit(1);}
  if((fp=fopen(argv[1],"r")) == NULL){
    printf("File \"%s\" does not exist.\n",argv[1]);
    exit(1);
  }

  while(fgets(line,256,fp)!=NULL){
    if(strncmp(&line[12],argv[2],strlen(argv[2])) == 0){
      while(strncmp(line,"//",2)!=0){
	printf("%s",line);
       fgets(line,256,fp);
      }
      break;
    }
  }
  fclose(fp);
}


