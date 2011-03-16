/* This header needs iostream.h */
#define MAXDIM 50

/* WARNING: There is no operator= for this class */
class SORGAR {
public:
   int num;
   double arr[MAXDIM];

/* sorgar.c */
   SORGAR();
   SORGAR operator+(SORGAR);
   SORGAR operator-(SORGAR);
   SORGAR operator*(double);
   SORGAR operator/(double);
   void display();
   void app(double);
};


/* prototypes */

/* sorgar.c */
double euc_dist(SORGAR&, SORGAR&);
void so_belong_opt(SORGAR *, SORGAR *, int, int, int **, int);
void so_belong(SORGAR *, SORGAR *, SORGAR *, int, int, int **);
void free2dint(int **);
int **make2dint(int, int);
void so_grav(SORGAR *, SORGAR *, SORGAR *, int, int, int **);
void so_unitm(SORGAR *, SORGAR *, SORGAR *, int, int, int **, double);
void doub_to_str(double, int, char[]);

