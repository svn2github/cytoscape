#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <string.h>
#include <stdio.h>

/* rsfunctions are for single window only */
#define BORDER "purple"
#define BACKGR "black"

#define MAX_COLOR 100

static Display *d;
static Window w;
static int window_height, window_width;
static GC gc;
static Font f;
static Pixmap p;

static struct {
  char color[20];
  unsigned long pixel_value;
} clr_map[MAX_COLOR];

static int nreg_color = 0;

/* prototypes */

unsigned long MyColor(Display *, char *);
void rsopen(int, int, unsigned int, unsigned int);
void rsclose();
void rsflush();
void rsclear();
void rspset(int, int, char *);
void rsline(int, int, int, int, char *);
void rslinep(int, int, int, int, char *);
void rscirc(int, int, int, char *);
void rscircp(int, int, int, char *);
void rsarcp(int, int, int, char *);
void rsrect(int, int, int, int, char *);
void rsfill(int, int, int, int, char *);
void rsmouse(int *, int *, int *);
void rssymbol(int, int, char *, char *);
int keyst(int);
void rsmsstat(int *, int *, int *);
int strlength(char *);


unsigned long MyColor(Display *d, char *color)
{    
  Colormap cmap;
  XColor c0,c1;
  int i;
  
  for(i = 0;i < nreg_color;i ++){
    if(strcmp(clr_map[i].color, color) == 0){
/*      printf("Color %s registered in #%d.\n", color, i); */
      return(clr_map[i].pixel_value);
    }
  }
  if(nreg_color >= MAX_COLOR)return 0;
  
  cmap = DefaultColormap(d, 0);
  XAllocNamedColor(d, cmap, color, &c1, &c0);

  strcpy(clr_map[nreg_color].color, color);
  clr_map[nreg_color].pixel_value = c1.pixel;
  nreg_color ++;
  
  return(clr_map[nreg_color - 1].pixel_value);
}

void rsopen(int x,int y, unsigned int width,unsigned int height)
{    
     XSetWindowAttributes att;

     window_width = width;
     window_height = height;

     if((d = XOpenDisplay(NULL)) == NULL)printf("can't open display\n");

     w = XCreateSimpleWindow(d,DefaultRootWindow(d),x,y,width,height,
			     5,MyColor(d,BORDER),MyColor(d,BACKGR));
     p = XCreatePixmap(d, w, width, height, DefaultDepth(d, 0));

     XStoreName(d, w, "Clustering Sample");
     att.backing_store = Always;
     XChangeWindowAttributes(d,w,CWBackingStore,&att);

     XSelectInput(d,w,ButtonPressMask | ButtonReleaseMask);

     gc = XCreateGC(d, w, 0, 0); 

     f = XLoadFont(d,"r16");
     XSetFont(d,gc,f);

     XMapWindow(d,w);
     rsclear();
     XFlush(d);

     }

void rsclose(){

     XCloseDisplay(d);
     }

void rsflush(){

    XCopyArea(d, p, w, gc, 0, 0, window_width, window_height, 0, 0);
    XFlush(d);

}

void rsclear(){

    XGCValues gv;
    gv.foreground = MyColor(d, BACKGR);
    XChangeGC(d, gc, GCForeground, &gv);
    XFillRectangle(d, p, gc, 0, 0, window_width, window_height);

}

void rspset(int x,int y,char *color)
{
     XGCValues gv;


     if(color != NULL){
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCForeground, &gv);
     }

     XDrawPoint(d,w,gc,x,y);
     XFlush(d);

}


     
void rsline(int x1,int y1,int x2,int y2,char *color)
{
     XGCValues gv;

     if(color != NULL){
          gv.line_width = 2;
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCLineWidth | GCForeground, &gv);
     } 
     
     XDrawLine(d,w,gc,x1,y1,x2,y2);
     XFlush(d);
}
   
void rslinep(int x1, int y1, int x2, int y2, char *color)
{

     XGCValues gv;

     if(color != NULL){
          gv.line_width = 2;
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCLineWidth | GCForeground, &gv);
     } 
     
     XDrawLine(d,p,gc,x1,y1,x2,y2);
     XFlush(d);
}

void rscirc(int x,int y,int r,char *color)
{
     XGCValues gv;

     if(color != NULL){
          gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }
     
     XDrawArc(d, w, gc, x-r, y-r, r*2, r*2, 0, 360*64);
     XFlush(d);
}

void rscircp(int x, int y, int r, char *color)
{

     XGCValues gv;

     if(color != NULL){
          gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }
     
     XDrawArc(d, p, gc, x-r, y-r, r*2, r*2, 0, 360*64);
     XFlush(d);
}

void rsarcp(int x, int y, int r, char *color)
{

     XGCValues gv;

     if(color != NULL){
          gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }
     
     XFillArc(d, p, gc, x-r, y-r, r*2, r*2, 0, 360*64);
     XFlush(d);
}




void rsrect(int x,int y,int width,int height,char *color)
{
     XGCValues gv; 
     
     if(color != NULL){
          gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }
     
     XDrawRectangle(d, w, gc, x, y, width, height);  
     XFlush(d);
}




void rsfill(int x,int y,int width,int height,char *color)
{
     XGCValues gv;

     if(color != NULL){
	  gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }

     XFillRectangle(d, w, gc, x, y, width, height);
}


void rsmouse(int *x,int *y,int *b)
{
     XEvent e;
     while(1){ XNextEvent(d,&e);
	       if (e.type == ButtonPress){  *x = e.xbutton.x;
					    *y = e.xbutton.y;
					    *b = e.xbutton.button;
					    }
               if (e.type == ButtonRelease)break;
            }
     }
    
void rssymbol(int x,int y,char *string,char *color)
{
     XGCValues gv;

     if(color != NULL){
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCForeground,&gv);
     } 

     XDrawString(d,w,gc,x,y,string,strlength(string));

     XFlush(d);
}

void rssymbolp(int x,int y,char *string,char *color)
{
     XGCValues gv;

     if(color != NULL){
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCForeground,&gv);
     } 

     XSetFont(d,gc,f);
     XDrawString(d,p,gc,x,y,string,strlength(string));

     XFlush(d);
}

int keyst(int keyseg)
{
     char keys_return[32];
     XQueryKeymap(d, keys_return);
     return (int)keys_return[keyseg];
}


void rsmsstat(int *x,int *y,int *b)
{
  Window root_r,child_r;
  int rx_r,ry_r;

  XQueryPointer(d,w,&root_r,&child_r,&rx_r,&ry_r,
		x,y,(unsigned *)b);
}

int strlength(char *string)
{
  int ct = 0;
  while(string[ct]!= '\0')ct++;
  return ct; 
}
     





