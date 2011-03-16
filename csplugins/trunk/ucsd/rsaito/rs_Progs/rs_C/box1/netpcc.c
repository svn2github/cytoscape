#include <stdio.h>
#include "rsgraphn.h"

#define TFILE "nettmp"

double getla();
double strtodouble();

main(){

  int n,s,t;
  char *hostname[100];
  int netsta[100]; 
  int rupf[100]; 
  
  int hostn;
  int first_flag;
  double la;

  char combuf[100];

  combuf[0] = '\0';

  for(n = 0;n < 100;n ++)rupf[n] = 1;

  n = 0;
  hostname[n ++] = "ccn00";
  hostname[n ++] = "ccn01";
  hostname[n ++] = "ccn02";
  hostname[n ++] = "ccn03";
  hostname[n ++] = "ccn04";
  hostname[n ++] = "ccn05";
  hostname[n ++] = "ccn06";
  hostname[n ++] = "ccn07";
  hostname[n ++] = "ccn08";
  hostname[n ++] = "ccn09";
  hostname[n ++] = "ccn10";
  hostname[n ++] = "ccn11";
  hostname[n ++] = "ccn12";
  hostname[n ++] = "ccn13";
  hostname[n ++] = "ccn14";
  hostname[n ++] = "ccn15";
  hostname[n ++] = "ccn16";
  hostname[n ++] = "ccn17";
  hostname[n ++] = "ccn18";
  hostname[n ++] = "ccs00";
  hostname[n ++] = "ccs01";
  hostname[n ++] = "cs1";
  hostname[n ++] = "z001";
  hostname[n ++] = "fs01";
  hostname[n ++] = "fs03";
  hostname[n ++] = "ss14";
  hostname[n ++] = "ss37";
  hostname[n ++] = "ss40";

  hostn = n;
  for(n = 0;n < hostn;n ++)netsta[n] = 0;
 
  rsopen(100,100,100,hostn*20+10);

  first_flag = 0;

while(1){
  for(n = 0;n < hostn;n ++){
     combuf[0] = '\0';            /* "" */
     aptex(combuf,"/usr/etc/ping -s ");  /* "/etc/ping" */
     aptex(combuf,hostname[n]);   /* "/etc/ping hpujsy08" */
     aptex(combuf," 64 1");    /* "/etc/ping hpujsy08 64 1" */
     aptex(combuf," | grep \"round-trip\" > ");
     aptex(combuf,TFILE);         /* file name for redirection */
/*     printf("command:%s\n",combuf); */
     system(combuf);
     if(fcheck(TFILE) == 0)netsta[n] = -1;
     else {
       if(rupf[n]){
         combuf[0] = '\0';
         aptex(combuf,"rup ");
         aptex(combuf,hostname[n]);
         aptex(combuf," | awk '{print $9}' > ");
         aptex(combuf,TFILE);
         system(combuf);
         la = getla(TFILE);
         if(la == -1)netsta[n] = -2;
         else netsta[n] = (int)la;
       }
       else netsta[n] = -2;
     }
     disponeh(n,hostname[n],netsta[n]);
     if(first_flag)for(t = 0;t < 5;t ++){
        for(s = 0;s < hostn;s ++)
	   if(netsta[s] == -1)disponeh(s,hostname[s],-3);
        sleep(1);
	for(s = 0;s < hostn;s ++)
	   if(netsta[s] == -1)disponeh(s,hostname[s],netsta[s]);
        sleep(1);
     }
   }
  first_flag = 1;
/*
  for(n = 0;n < hostn;n ++)printf("%s:%d ",hostname[n],netsta[n]);
  putchar('\n'); 
*/
}  
  rsclose();
}

aptex(ori,ad)
char *ori,*ad;
{
  while(*ori != '\0')ori ++;
  while(*ad != '\0')*ori ++ = *ad ++;
  *ori = '\0';
}


/* if file is null, 0 will be returned */
int fcheck(filnam)
char *filnam;
{
   FILE *fp;
   int ret;

   fp = fopen(filnam,"r");
   if(fgetc(fp) == EOF)ret = 0;
   else ret = 1;
   fclose(fp);
   return ret;
}

double getla(filnam)
char *filnam;
{
   FILE *fp;
   double ret;
   int n;
   char c, buf[20];

   fp = fopen(filnam,"r");
   if((c = fgetc(fp)) == EOF)ret = -1;
   else {
     buf[0] = c; 
     n = 1;
     while(((c = fgetc(fp)) <= '9' && c >= '0') || c == '.'){
	buf[n] = c;
	n ++;
     }
     buf[n] = '\0';
/*     printf("%s\n",buf); */
     ret = strtodouble(buf);
   }
   fclose(fp);
   return ret;
}

double strtodouble(s)
char *s;
{
  double x = 0;
  int p = 0;
  double unit= 0.1;

  while(s[p] >= '0' && s[p] <= '9'){
     x *= 10;
     x += (double)(s[p] - '0');
     p ++;
  }
  if(s[p] == '.'){
    p ++;
    while(s[p] >= '0' && s[p] <= '9'){
    x += (double)(s[p] - '0') * unit;
    unit *= 0.1;
    p ++;
    }
  }
  return x;
}


disponeh(n,hnam,hsta)
int n;
char *hnam;
int hsta;
{
   int px,py;
   char *color_n1,*color_n2;

   px = 10;
   py = 10 + 20*n;


   switch(hsta){
      case 0:color_n1 = "blue";break;
      case -1:color_n1 = "red";break;
      case -2:color_n1 = "purple";break;
      case -3:color_n1 = "brown";break;
      case 1:color_n1 = "pale green";break;
      case 2:color_n1 = "green yellow";break;
   }
   if(hsta >= 3 && hsta < 7){
      color_n1 = "yellow";
   }

   if(hsta >= 7){
      color_n1 = "orange";
   }
/*
   rsfill(px-1,py-1,12,12,color_n1);
   rsfill(px,py,10,10,color_n2);
*/
   drawindi(px - 1, py - 1, 12, 12, color_n1);
   rssymbol(px+16,py+10,hnam,"white");

}

drawindi(x,y,width,height,c1)
int x,y,width,height;
char *c1;
{
   
#define CSIZE 2   
   
   int n,s,t;

   for(n = 0;n < CSIZE;n ++){
 
     rs_xdrawarc(x + n,y + n,width - 2*n,height - 2*n,45*64,180*64,"gray80");
  
     rs_xdrawarc(x + n,y + n,width - 2*n,height - 2*n,225*64,180*64,"gray20");
   }

   rs_xfillarc(x + CSIZE,y + CSIZE,
	       width - 2*CSIZE,height - 2*CSIZE, 0, 360*64,c1);

   rs_xdrawarc(x + CSIZE+1,y + CSIZE+1,
	       width - 2*(CSIZE+1),height - 2*(CSIZE+1), 90*64,90*64,"white");

   rs_xdrawarc(x + CSIZE+1,y + CSIZE+1,
	       width - 2*(CSIZE+1),height - 2*(CSIZE+1),270 *64,90*64,"black");




}


