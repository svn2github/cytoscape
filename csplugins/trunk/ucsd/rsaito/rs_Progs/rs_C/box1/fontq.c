#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xatom.h>
#include <stdio.h>

void main(argc,argv)
int argc;
char **argv;
{
  Display *d;
  char **fname;
  XFontStruct *fs;
  int i, n;

  d = XOpenDisplay(NULL);
  fname = XListFontsWithInfo(d,argv[1],3000,&n,&fs);
  
  printf("  fname   min   max   asc   des   heig \n");
  for(i = 0;i < n;i ++){
    printf("%8s",fname[i]);
    printf("%4d",fs[i].min_bounds.width);
    printf("%6d",fs[i].max_bounds.width);
    printf("%6d",fs[i].ascent);
    printf("%6d",fs[i].descent);
    printf("%6d\n",fs[i].ascent + fs[i].descent);
  }
}
