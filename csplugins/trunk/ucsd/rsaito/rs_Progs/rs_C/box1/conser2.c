/* Event notification (connection) */



#include <sys/types.h>

#include <sys/socket.h>

#include <netinet/in.h>

#include <arpa/inet.h>

#include <netdb.h>

#include <stdio.h>

#include <fcntl.h>



#define BUFMAX 45

#define PORT_NO 8002

#define Err(x) { fprintf(stderr, "server- "); perror(x); exit(0); }



static char rmsg[10], smsg[BUFMAX];

static int sofd, nsofd;

static struct sockaddr_in sv_addr, cl_addr;

static struct hostent *shost,*chost;

static char shostname[10];



int msgpro();



/* main routine (server) */

main(){



   int cadlen;



/* creates socket (TCP) */

   sofd = socket(AF_INET, SOCK_STREAM, 0);

   if(sofd < 0)Err("socket");



/* gets host name of server */

   if(gethostname(shostname, sizeof(shostname)) < 0)

      Err("gethostname")

   else printf("Host name:%s\n",shostname);



/* gets IP address from host name of server */

   shost = gethostbyname(shostname);

   if(shost == NULL)Err("gethostbyname")

   else { printf("IP address:"); 

          printf("%d.%d.%d.%d\n",(int)shost->h_addr[0],

				 (int)shost->h_addr[1],

				 (int)shost->h_addr[2],

				 (int)shost->h_addr[3]);

        }



/* gives a name for a socket */

   bzero((char *)&sv_addr, sizeof(sv_addr));

   sv_addr.sin_family = AF_INET;

   sv_addr.sin_port = htons(PORT_NO);

   memcpy((char *)&sv_addr.sin_addr, (char *)shost->h_addr,

	  shost->h_length);

   if(bind(sofd, &sv_addr, sizeof(sv_addr)) < 0)Err("bind");

   printf("Name was given to socket.\n");



/* accepts request for connection from client */

/* indicates maximum number of waiting sequence */

   if(listen(sofd, 5) == -1)Err("listen");



/* waits for connection from client */

      

      cadlen = sizeof(cl_addr);

      printf("Waiting for connection from client...\n");

      if((nsofd = accept(sofd, &cl_addr, &cadlen)) < 0)Err("accept");

      chost = gethostbyaddr((char *)&cl_addr.sin_addr,

			    sizeof(struct in_addr), AF_INET);

      msgpro(); /* child process */

      close(sofd); /* parental process */

   

}



/* processing routine (server) */

msgpro(){



   int cc, cadlen, nbyte, scount;



   printf("Connected from %s\n", chost->h_name);



/* Accepts requests for events */

   if(recv(nsofd, rmsg, 10, 0) < 0)perror("recv");



   scount = 0;

   while(fgets(smsg, BUFMAX, stdin) != NULL){



/* Sends event */

      nbyte = strlen(smsg);

      if(send(nsofd, smsg, nbyte, 0) < 0)break;

      scount ++;

   }

   shutdown(nsofd, 2); 

   close(nsofd);

   printf("Closing connection.\n");

}







