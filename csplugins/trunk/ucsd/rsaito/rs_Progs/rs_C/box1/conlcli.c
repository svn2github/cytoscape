/* Event notification (connectionless) */

/* Client program */



#include <sys/types.h>

#include <sys/socket.h>

#include <netinet/in.h>

#include <arpa/inet.h>

#include <netdb.h>

#include <stdio.h>



#define  BUFMAX   256

#define  PORT_NO  8000

#define  Err(x)   { fprintf(stderr, "client- "); perror(x); exit(0); }

static   char rmsg[BUFMAX], smsg[] = "REQUEST";

static   int  sofd;

static   struct sockaddr_in  sv_addr, cl_addr;

static   struct hostent *shost;



int cliepro();



void main(argc, argv)

int argc;

char *argv[];

{

/* creates socket(UDP) */

   sofd = socket(AF_INET, SOCK_DGRAM, 0);

   if(sofd < 0)Err("socket");



/* gives a name for the socket */

   bzero((char *)&cl_addr, sizeof(cl_addr));

   cl_addr.sin_family = AF_INET;

   cl_addr.sin_port = htons(0);

   cl_addr.sin_addr.s_addr = htonl(INADDR_ANY);



   if(bind(sofd, &cl_addr, sizeof(cl_addr)) < 0)Err("bind");



/* Sets address for server */

   shost = gethostbyname(argv[1]);

   if(shost == NULL)Err("gethostbyname");

   

   bzero((char *)&sv_addr, sizeof(sv_addr));

   sv_addr.sin_family = AF_INET;

   sv_addr.sin_port = htons(PORT_NO);

   memcpy((char *)&sv_addr.sin_addr, (char *)shost->h_addr,

	   shost->h_length);



   cliepro();



   exit(0);

}



/* Processing routine (client) */

cliepro(){



   int cc, svadlen, nbyte;

   svadlen = sizeof(sv_addr);

   nbyte = strlen(smsg);



/* Request for event */

   if(sendto(sofd, smsg, nbyte, 0, &sv_addr, svadlen) < 0)

      perror("sendto");



/* Receives events */

   while(1){

      cc = recvfrom(sofd, rmsg, BUFMAX, 0, &sv_addr, &svadlen);

      if(cc < 0)perror("recvfrom");

      if(rmsg[0] == '.')break;

      printf("%s", rmsg);

      bzero(rmsg, BUFMAX);

   }

}





