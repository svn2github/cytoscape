#define MAXFUNC 300

   int (*apcon[MAXFUNC])();
   int apv[MAXFUNC][50];   


apinit(){

   int n;
   for(n = 0;n < MAXFUNC; n++)apcon[n] = NULL;

} 

apcall(){

   int n;
   for(n = 0;n < MAXFUNC; n++)
         if(apcon[n] != NULL)(*apcon[n])(&apcon[n], apv[n]);
  
} 

apreg(func)
 int (*func)();
{
   int n = 0;
   while(n < MAXFUNC){
        if(apcon[n] == NULL){
             apcon[n] = (int (*)())func;
             apv[n][0] = 0;
             return n;
	}
   n++;
   }
   return -1;
} 
