/* Event notification (connection) */



#include <sys/types.h>

#include <sys/socket.h>

#include <netinet/in.h>

#include <arpa/inet.h>

#include <netdb.h>

#include <stdio.h>

#include <fcntl.h>



#define  BUFMAX  85

#define  PORT_NO 8002

#define  Err(x)  { fprintf(stderr, "client- "); perror(x); exit(0); }

static   char rmsg[BUFMAX], smsg[] = "REQUEST";

static   int  sofd;

static   struct sockaddr_in sv_addr;

static   struct hostent *shost;



int cliepro();



/* main routine (client) */

void main(argc, argv)

int argc;

char *argv[];

{

/* creates socket (TCP) */

   sofd = socket(AF_INET, SOCK_STREAM, 0);

   if(sofd < 0)Err("socket");



/* sets server address */

   shost = gethostbyname(argv[1]);

   if(shost == NULL)Err("gethostbyname");



   bzero((char *)&sv_addr, sizeof(sv_addr));

   sv_addr.sin_family = AF_INET;

   sv_addr.sin_port = htons(PORT_NO);

   memcpy((char *)&sv_addr.sin_addr, (char *)shost->h_addr,

          shost->h_length);



/* request for a socket connection */

   if(connect(sofd, &sv_addr, sizeof(sv_addr)) < 0) Err("connect");

   printf("Connection to %s has been made.\n",argv[1]);



   cliepro();

   if(shutdown(sofd, 2) < 0)perror("shutdown");

   close(sofd);

   printf("Closing connection.\n");

   exit(0);

}



/* processing routine (client) */

cliepro(){



   int cc, svadlen, nbyte, linect;

   char *p;

   svadlen = sizeof(sv_addr);



/* request for event */

   nbyte = strlen(smsg);

   if(send(sofd, smsg, nbyte, 0) < 0)perror("send");



/* Receives events */

   linect = 0;

   while(1){

      cc = recv(sofd, rmsg, BUFMAX, 0); 

/*    cc = recvfrom(sofd, rmsg, BUFMAX, 0, &sv_addr, &svadlen); */

      if(cc < 0)perror("recv");

      if((p = strstr(rmsg, "\n.")) != NULL)

         bzero(&p[2], strlen(&p[2]));

      printf("%s", rmsg);

      if(rmsg[0] == '.' || strrstr(rmsg, "\n.") != NULL){ break; }

      if(strlen(rmsg) == 0)break; 

      bzero(rmsg, BUFMAX);

      linect ++;

   }

}



