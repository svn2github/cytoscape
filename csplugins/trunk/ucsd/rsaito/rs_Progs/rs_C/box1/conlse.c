/* Event notification (connectionless) */

/* Server program */



#include <sys/types.h>

#include <sys/socket.h>

#include <netinet/in.h>

#include <arpa/inet.h>

#include <netdb.h>

#include <stdio.h>



#define  BUFMAX  256

#define  PORT_NO 8000

#define  Err(x)  { fprintf(stderr, "server- "); perror(x); exit(0); }

static   char rmsg[10], smsg[BUFMAX];

static   int sofd;

static   struct sockaddr_in sv_addr, cl_addr;



int servpro();



void main(){

/* creates socket(UDP) */

   sofd = socket(AF_INET, SOCK_DGRAM, 0);

   if(sofd < 0) Err("socket");



/* gives a name for the socket */

   bzero((char *)&sv_addr, sizeof(sv_addr));

   sv_addr.sin_family = AF_INET;

   sv_addr.sin_port   = htons(PORT_NO);

   sv_addr.sin_addr.s_addr = htonl(INADDR_ANY);



   if(bind(sofd, &sv_addr, sizeof(sv_addr)) < 0) Err("bind");



   servpro();

}



/* processing routine for server */

servpro(){



   int cc, cadlen, nbyte;

   FILE *fp;

   while(1){

/* Accept the request for event */

      cadlen = sizeof(cl_addr);

      cc = recvfrom(sofd, rmsg, 10, 0, &cl_addr, &cadlen);

      if(cc < 0)perror("recvfrom");

      fp = fopen("EVENT", "r");

      while(fgets(smsg, BUFMAX, fp) != NULL){



/* Sends the event */

	 nbyte = strlen(smsg);

	 if(sendto(sofd,smsg,nbyte, 0, &cl_addr, cadlen) < 0)

	    perror("sendto");

      }

      fclose(fp);

   }

}



