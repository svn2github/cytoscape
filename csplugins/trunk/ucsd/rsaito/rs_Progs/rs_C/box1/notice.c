#include <Xm/Label.h>
#include <Xm/BulletinB.h>
#include <Xm/PushB.h>
#include <stdio.h>

  Widget toplevel,bb,label1,pb1;

unsigned long MyColor(d,color)
     Display *d;
     char *color;
{    
     Colormap cmap;
     XColor c0,c1;

     cmap = DefaultColormap(d, 0);

     XAllocNamedColor(d, cmap, color, &c1, &c0);

     return(c1.pixel);
}


main(argc,argv)
int argc;
char *argv[];
{
  void quitCB();

  int i;
  char str[200];
  Arg args[20];

  XFontStruct *fs1;
  XmFontList fl1;
  XmString comp,comp2;
/*
  str[0] = 0x41;
  str[1] = 0x70;
  str[2] = 0x41;
  str[3] = 0x70;
  str[4] = 0x41;
  str[5] = 0x70;
  str[6] = '\0';
*/

  getfromstdio(str); 

  toplevel = XtInitialize(argv[0],"Lgow",NULL,0,&argc,argv);
  fs1 = XLoadQueryFont(XtDisplay(toplevel),
		"-jis-fixed-medium-r-normal--0-0-75-75-c-0-jisx0208.1983-0");

  fl1 = XmFontListCreate(fs1,"charset1");

  bb = XmCreateBulletinBoard(toplevel,"bb",NULL,0);
  XtManageChild(bb);


  comp = XmStringCreate(str,"charset1");
  i = 0;
  XtSetArg(args[i],XmNfontList,fl1); i ++;
  XtSetArg(args[i],XmNlabelString,(XtArgVal)comp); i ++;
  label1 = XmCreateLabel(bb,"label1",args,i);
  XtManageChild(label1);

  comp2 = XmStringCreate("close","charset1");
  i = 0;
  XtSetArg(args[i],XmNx,180); i ++;
  XtSetArg(args[i],XmNy,60); i ++;
/*
  XtSetArg(args[i],XmNfontList,fl1); i ++;
  XtSetArg(args[i],XmNlabelString,(XtArgVal)comp2); i ++;
*/
  pb1 = XmCreatePushButton(bb,"logout",args,i);
  XtManageChild(pb1);
/*
  i = 0;
  XtSetArg(args[i],XmNbackground,MyColor(XtDisplay(pb1),"purple")); i++;
  XtSetValues(pb1,args,i);
*/

  XtAddCallback(pb1,XmNactivateCallback,quitCB,NULL);


  XtRealizeWidget(toplevel);
  XtMainLoop();

}

void quitCB(w,client_data,call_data)
Widget w;
caddr_t *client_data;
{
  exit(0);
}

getfromstdio(str)
char *str;
{
  char c;
  int ct = 0;
  while((c = getchar()) != EOF && c != '\n'){
    if(c == (char)0x1b)ct = 3;
    if(ct == 0)*str ++ = c;
    else ct --;
  }
  *str = '\0';
}

