/* This header needs iostream.h */
#define MAXDIM 50

/* WARNING: There is no operator= for this class */
class POINT {
 private:
  int dim;
  double p_array[MAXDIM];

 public:
  /* point.c */
  POINT();
  int get_dim();
  double *get_array();
  double get_array(int);
  POINT operator+(POINT);
  POINT operator-(POINT);
  POINT operator*(double);
  POINT operator/(double);
  void display();
  void append(double);
};


/* prototypes */

/* point.c */
double euc_dist(POINT&, POINT&);
void determine_cluster(int **, POINT *, POINT *, POINT *, int, int);
void calc_gravity_center(int **, POINT *, POINT *, POINT *, int, int);
void pt_unitm(int **, POINT *, POINT *, POINT *, int, int, double);
void doub_to_str(double, int, char[]);
void free2dint(int **);
int **make2dint(int, int);

