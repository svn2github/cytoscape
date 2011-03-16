#include <X11/Xlib.h>
#include <X11/Xutil.h>

/* rsfunctions are for single window only */

Display *d;
Window w;
GC gc;
Font f;

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

rsopen(x,y,width,height)
   int x;
   int y;
   unsigned int width;
   unsigned int height;
{    XSetWindowAttributes att;

     if((d = XOpenDisplay(NULL)) == NULL)puts("can't open display\n");

     w = XCreateSimpleWindow(d,DefaultRootWindow(d),x,y,width,height,
			     5,MyColor(d,"purple"),MyColor(d,"blue"));

     att.backing_store = Always;
     XChangeWindowAttributes(d,w,CWBackingStore,&att);

     XSelectInput(d,w,ButtonPressMask | ButtonReleaseMask);

     gc = XCreateGC(d, w, 0, 0); 

     f = XLoadFont(d,"r24");

     XMapWindow(d,w);
     XFlush(d);

     }

rsclose(){

     XCloseDisplay(d);
     }


rspset(x,y,color)
     int x,y;
     char *color;
{
     XGCValues gv;


     if(color != NULL){
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCForeground, &gv);
     }

     XDrawPoint(d,w,gc,x,y);
     XFlush(d);

}


     
rsline(x1,y1,x2,y2,color)
     int x1,y1,x2,y2;
     char *color;
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
   
rscirc(x,y,r,color)
     int x,y,r;
     char *color;
{
     XGCValues gv;

     if(color != NULL){
          gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }
     
     XDrawArc(d, w, gc, x-r, y-r, r*2, r*2, 0, 360*64);
     XFlush(d);
}

rsrect(x,y,width,height,color)
     int x,y,width,height;
     char *color;
{
     XGCValues gv; 
     
     if(color != NULL){
          gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }
     
     XDrawRectangle(d, w, gc, x, y, width, height);  
     XFlush(d);
}

rsfill(x,y,width,height,color)
     int x,y,width,height;
     char *color;
{
     XGCValues gv;

     if(color != NULL){
	  gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }

     XFillRectangle(d, w, gc, x, y, width, height);
}


rsmouse(x,y,b)
     int *x;
     int *y;
     int *b;
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
    
rssymbol(x,y,string,color)
     int x,y;
     char *string;
     char *color;
{
     XGCValues gv;

     if(color != NULL){
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCForeground,&gv);
     } 

     XSetFont(d,gc,f);
     XDrawString(d,w,gc,x,y,string,strlength(string));

     XFlush(d);
}

int keyst(keyseg)
     int keyseg;
{
     char keys_return[32];
     XQueryKeymap(d, keys_return);
     return (int)keys_return[keyseg];
}


rsmsstat(x,y,b)
     int *x,*y,*b;
{
  Window root_r,child_r;
  int rx_r,ry_r;

  XQueryPointer(d,w,&root_r,&child_r,&rx_r,&ry_r,
		x,y,b);
}

int strlength(string)
     char *string;
{    int ct = 0;
     while(string[ct]!= '\0')ct++;
     return ct; 
     }
     





