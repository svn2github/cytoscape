#include <stdio.h>
#include <stdlib.h>
#include "global_st.h"
#include "atg_func.h"

#define BEGINKW "ORIGIN"
#define ENDKW "//"

#define ONECHAIN 65536  /* length of data of each data chain 
                         Data chain is used when reading data 
                         of unknown length */

/* structure of data chain.It is used to read data of unknown length */
  struct datachain {
    int ndata;   /* number of data in cdata (max ONECHAIN) */
    char cdata[ONECHAIN];
    struct datachain *next; /* if no more,NULL */
  } ;

/* Prototype declarations */

void dchaintoar(struct datachain *,int,char **);
void printdatachain(struct datachain *);


/* reads sequence data of any length(?) and allocate buffer and put data into 
   buffer and total number of data is returned to *total (excluding EOF) */
/* File must be opened in main routine!!! */
void nseqread(char **buffer,int *total, FILE *fp)
{
  struct datachain head;
  struct datachain *current,*tmp;


  char c;
  int count; /* counts number of data in array */
  int totalbytes;



  *total = 0; /* counts total data */
  totalbytes = 0;

  current = &head;
  count = 0;
  while((c = fgetc(fp)) != EOF){
    if(c >= 'a' && c <= 'z'){
      *total += 1;
      current->cdata[count ++] = c;
      if(count >= ONECHAIN){
	current->ndata = count;
	if((current->next = 
	    (struct datachain *)malloc(sizeof(struct datachain)))== NULL)
	  { fprintf(stderr,"memory full...\n"); exit(1);}
	totalbytes += ONECHAIN;
	/*    printf("memory allocated.\n"); */
	count = 0; current = current->next;
      }
    }
    else if(c == '/')break;
  }


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

void printdatachain(struct datachain *head)
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
void dchaintoar(struct datachain *head,int total,char **buffer)
/* int total; total number of characters */
{
  struct datachain *current;
  int ct = 0;
  int n;
  *buffer = (char *)malloc((total + 1)*sizeof(char));
     /* 1 added for '\0' */
  if(*buffer == NULL){
    fprintf(stderr, "memory full.....\n");
    exit(1);
  }

  current = head;
  while(current->next != NULL){
    for(n = 0;n < current->ndata;n ++)(*buffer)[ct ++] = current->cdata[n];
    current = current->next;
  }
  for(n = 0;n < current->ndata;n ++)(*buffer)[ct ++] = current->cdata[n];
  (*buffer)[ct ++] = '\0';
}

int buftoseqn2(char *buffer,int total, char **seqn)
/* put only lower case letters to seqn */
/* char **seqn; pointer will be returned ( not 2-D) */
{

  int i,j,k,m,n,p,q,count;
  int bg_flag,bg_loc,cr_flag;
 
  cr_flag = 1;
  bg_flag = 0;
  count = 0;
  for(i = 0;i < total;i ++){
    
    if(cr_flag == 1 && lpatm(BEGINKW,&buffer[i])){
      for( ; buffer[i] != '\n' && i < total;i ++);
      bg_flag = 1;
      bg_loc = i;
/*    printf("count started from location %d\n",i); */
    }

    if(bg_flag && buffer[i] >= 'a' && buffer[i] <='z'){
      /* putchar(buffer[i]); */ count ++;
    }

    if(bg_flag && lpatm(ENDKW,&buffer[i])){
/*
      printf("\ncount stopped in location %d\n",i);
      printf("counter:%d\n",count);
*/
      break;
    }

    if(buffer[i] == '\n'){ cr_flag = 1; /* printf("cr acknowledge\n"); */}
    else { cr_flag = 0; /* printf("cr cleared\n"); */}
  }

  *seqn = (char *)malloc(count * sizeof(char));

  p = 0;
  for(i = bg_loc;i < total;i ++){
    if(buffer[i] >= 'a' && buffer[i] <= 'z')
      (*seqn)[p ++] = buffer[i];
  }
  return count;
}

