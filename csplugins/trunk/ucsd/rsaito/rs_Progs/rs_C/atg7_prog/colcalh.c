/* global_st.h and atg_func.h must be included */

char ntc(nucleotide n){

  switch(n){
  case a:return 'a';
  case t:return 't';
  case c:return 'c';
  case g:return 'g';
  default:return 'n';
  }
}

nucleotide cton(char chr){

  switch(chr){
  case 'a':return a;
  case 't':return t;
  case 'c':return c;
  case 'g':return g;
  default:return error;
  }

}

double log_0_0(double x){
  if(x)return log(x);
  else return 0.0;
}

class Dinuc_Matrix {
  
public:
  double mmatrix[NUM_NUC][NUM_NUC];
  int dinuc_count[NUM_NUC][NUM_NUC];

  int total;
  int total_i[NUM_NUC];
  int total_j[NUM_NUC];

  void initialize(char **, int, const double[NUM_NUC][NUM_NUC]);
  void count_row_column();
  double log_likelihood();
  double independent_test();
  double mutual_information();
  void dinuc_matrix_print();
  void dinuc_matrix_print_r();
  void dinuc_matrix_print_oe();
  void markov_matrix_print();

  void multiply_mmatrix(double[NUM_NUC][NUM_NUC]);
};

void Dinuc_Matrix::initialize(char **dinuc, int num_dinuc,
			 const double mmt[NUM_NUC][NUM_NUC]){

  nucleotide nuc1, nuc2;

  for(int i = 0;i < NUM_NUC;i ++)
    for(int j = 0;j < NUM_NUC; j++){
      mmatrix[i][j] = mmt[i][j];
    }

  for(int i = 0;i < NUM_NUC;i ++)
    for(int j = 0;j < NUM_NUC;j ++)
      dinuc_count[i][j] = 0;
  
  for(int i = 0;i < num_dinuc;i ++){
    nuc1 = cton(dinuc[i][0]);
    nuc2 = cton(dinuc[i][1]);
    dinuc_count[nuc1][nuc2] ++;
  }

  count_row_column();

}

void Dinuc_Matrix::count_row_column(){

  int i,j, sub_total;
  
  for(i = 0;i < NUM_NUC;i ++){
    sub_total = 0;
    for(j = 0;j < NUM_NUC;j ++)sub_total += dinuc_count[i][j];
    total_i[i] = sub_total;
  }
  
  for(j = 0;j < NUM_NUC;j ++){
    sub_total = 0;
    for(i = 0;i < NUM_NUC;i ++)sub_total += dinuc_count[i][j];
    total_j[j] = sub_total;
  }
  
  for(i = 0,total = 0;i < NUM_NUC;i ++)total += total_i[i];

}


void Dinuc_Matrix::dinuc_matrix_print(){

  for(int i = 0;i < NUM_NUC;i ++){
    cout << ntc((nucleotide)i) << "  ";
    for(int j = 0;j < NUM_NUC;j ++)
      cout << dinuc_count[i][j] << ' ';
    cout << '\n';
  }
}

void Dinuc_Matrix::dinuc_matrix_print_r(){

  for(int i = 0;i < NUM_NUC;i ++){
    cout << ntc((nucleotide)i) << "  ";
    for(int j = 0;j < NUM_NUC;j ++){
      if(total_i[i])printf("%.2lf ", 1.0 * dinuc_count[i][j] / total_i[i]);
      else cout << "0.00 " << ' ';
    }
    cout << '\n';
  }
}

void Dinuc_Matrix::dinuc_matrix_print_oe(){

  double observed, expected;

  for(int i = 0;i < NUM_NUC;i ++){
    cout << ntc((nucleotide)i) << "  ";
    for(int j = 0;j < NUM_NUC;j ++){
      observed = 1.0 * dinuc_count[i][j];
      expected = 1.0 * total_i[i] * total_j[j] / total;
      if(total_i[i]*total_j[j])printf("%.2lf ",observed/expected);
      else cout << "1.00 " << ' ';
    }
    cout << '\n';
  }
}



void Dinuc_Matrix::markov_matrix_print(){

  for(int i = 0;i < NUM_NUC;i ++){
    cout << ntc((nucleotide)i) << "  ";
    for(int j = 0;j < NUM_NUC;j ++)
      cout << mmatrix[i][j] << ' ';
    cout << '\n';
  }
}

double Dinuc_Matrix::log_likelihood(){

  double chi_total;
  int ni;

  chi_total = 0.0;

  for(int i = 0;i < NUM_NUC;i ++){
    ni = 0;
    for(int j = 0;j < NUM_NUC;j ++)ni += dinuc_count[i][j];
    for(int j = 0;j < NUM_NUC;j ++){
      if(ni)chi_total += 1.0 * dinuc_count[i][j] * 
	      log_0_0(1.0 * dinuc_count[i][j] / (ni * mmatrix[i][j]));
    }
  }
  return chi_total * 2.0;
}

double Dinuc_Matrix::independent_test(){

  double chi_total;
  
  int i,j;

  chi_total = 0.0;

  for(i = 0;i < NUM_NUC;i ++)
    for(j = 0;j < NUM_NUC;j ++){
      if(total_i[i] * total_j[j])
	chi_total += dinuc_count[i][j] * 
	  log_0_0(1.0*dinuc_count[i][j] / (1.0*total_i[i]*total_j[j] / total));
    }

  return chi_total * 2.0;

}

double Dinuc_Matrix::mutual_information(){


  double m_total;
  
  int i,j;

  m_total = 0.0;

  for(i = 0;i < NUM_NUC;i ++)
    for(j = 0;j < NUM_NUC;j ++){
      if(total_i[i] * total_j[j]){
	m_total += ( 1.0 * dinuc_count[i][j] / total) *
	  log_0_0(1.0 * dinuc_count[i][j] / total / 
		  (1.0*total_i[i] / total * total_j[j] / total))/
	  log_0_0(2.0);
	/*
	cout << ntc(i) << ' ' << ntc(j) <<  ' ' << ( 1.0 * dinuc_count[i][j] / total) *
	  log_0_0(1.0 * dinuc_count[i][j] / total / 
		  (1.0*total_i[i] / total * total_j[j] / total))/
	  log_0_0(2.0) << '\n';
	  */
	}
    }

  return m_total;


}



void Dinuc_Matrix::multiply_mmatrix(double mm[NUM_NUC][NUM_NUC]){

  int i,j,k;
  double tmp_total;

  static double tmp[NUM_NUC][NUM_NUC];

  for(i = 0;i < NUM_NUC;i ++){
    for(j = 0;j < NUM_NUC;j ++){
      for(k = 0, tmp_total = 0.0;k < NUM_NUC;k ++)
	tmp_total += mmatrix[i][k] * mm[k][j];
      tmp[i][j] = tmp_total;
    }
  }

  for(i = 0;i < NUM_NUC;i ++)
    for(j = 0;j < NUM_NUC;j ++)
      mmatrix[i][j] = tmp[i][j];

}





