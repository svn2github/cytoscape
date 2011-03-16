/* X11/Xlib.h and X11/Xutil.h must be included. */

extern Display *d;
extern Window w;
extern int window_height, window_width;
extern GC gc;
extern Font f;
extern Pixmap p;

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
void rssymbolp(int, int, char *, char *);
int keyst(int);
void rsmsstat(int *, int *, int *);
int strlength(char *);
