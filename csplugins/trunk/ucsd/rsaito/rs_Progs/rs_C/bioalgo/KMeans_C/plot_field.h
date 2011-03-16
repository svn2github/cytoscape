
// Currently only one instance allowed...
class Plot_field {
 public:
  Plot_field();
  ~Plot_field();
  void plot_each_point(int, int);
  void plot_each_ref(int, int);
  int key(int);
  void mouse(int *, int *, int *);
  void plot_kmeans(int **, 
		   POINT [], POINT [], POINT [],
		   int num, int unitnum, int ite_steps);

};

