#define ATG_UP 9
#define ATG_DN 1

#define I_UNIT (ATG_UP + ATG_DN) * 4
#define H_UNIT 40
#define O_UNIT 1

#define REINI_LIMIT 80
/*
#define ATGDBMODE
*/

char around_atg[ATG_UP + ATG_DN];

int learn_mode = 0;
int learn_atg = 0;     /* number of ATGs that have been learned by NN */
int learn_correct = 0;
int learn_trial = 0;


int total_entry; /* number of sequences processed by this system */
int total_right_entry;

char weight_save_file[20];
char weight_multi_file[20][20];
int num_multi;

double **w_A1m[20], *w_A1m_bias[20];
double **w_A2m[20], *w_A2m_bias[20];

int atg_pred_correct = 0;
int atg_pred_total = 0; /* based on number of sequences */


int atg_learn_par(int argc, char *argv[], int n){

  FILE *fp;

  if(strcmp(argv[n], "-learn") == 0){
    strcpy(weight_save_file, argv[ ++ n]);
    learn_atg = 0;
    learn_correct = 0;
    learn_mode = 1;
    total_entry = 0;
    total_right_entry = 0;
    srand(time(NULL));
    if((fp = fopen(weight_save_file, "r")) != NULL){
      fclose(fp);
      neural_learn(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, 
		   weight_save_file, 0);
    }
    else neural_learn(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, 
		      NULL, 0);

    return 2;
  }

  else if(strcmp(argv[n], "-learn_test") == 0){
    strcpy(weight_save_file, argv[ ++ n]);
    learn_atg = 0;
    learn_correct = 0;
    learn_mode = 2;
    total_entry = 0;
    total_right_entry = 0;
    neural_learn(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, 
		 weight_save_file, 0);
    return 2;
  }
/*
  else if(strcmp(argv[n], "-learn_better") == 0){
    strcpy(weight_save_file, argv[ ++ n]);
    learn_atg = 0;
    learn_correct = 0;
    learn_mode = 3;
    total_entry = 0;
    total_right_entry = 0;
    if((fp = fopen(weight_save_file,"r")) != NULL){
      fclose(fp);
      neural_learn(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, 
		   weight_save_file, 0);
    }
    else {
      printf("Weight save file \"%s\" not found.\n",weight_save_file);
      exit(1);
    }
    return 2;
  }
*/
  else if(strcmp(argv[n], "-learn_sp") == 0){
    strcpy(weight_save_file, argv[ ++ n]);
    learn_atg = 0;
    learn_correct = 0;
    learn_mode = 4;
    total_entry = 0;
    total_right_entry = 0;
    srand(time(NULL));
    if((fp = fopen(weight_save_file, "r")) != NULL){
      fclose(fp);
      neural_learn(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, 
		   weight_save_file, 0);
    }
    else neural_learn(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, 
		      NULL, 0);
    return 2;
  }

  else return 0;

}

atg_learn_head(char *line){

}

atg_learn_ent(char *entry, char seqn[], int max, int cds[], int ncds){

  int i,j,k;
  int entry_right_flag;

  entry_right_flag = 1;

#ifdef ATGDBMODE
  printf("%s",entry);
#endif
  if(ncds <= 0 || cds[0] <= ATG_UP ||
     strncmp("atg", &seqn[cds[0] - 1], 3) != 0)return; /* atg only */

  total_entry ++; if(total_entry % 2 == 0)return;
  
  if(learn_mode == 4){
    for(i = 0; i < cds[0] - 3;i ++)
      if(strncmp(&seqn[i],"atg",3) == 0)break;
    if(i == cds[0] - 3)return;
  }

  for(i = 0; i <= cds[0] - 1 ; i ++){
    if(strncmp("atg", &seqn[i], 3) == 0){
      for(k = i + 3;k < i + REINI_LIMIT;k += 3){
	if(k >= max){ k = i + REINI_LIMIT; break; }
	if(strncmp("taa",&seqn[k],3) == 0 ||
	   strncmp("tag",&seqn[k],3) == 0 ||
	   strncmp("tga",&seqn[k],3) == 0)break;
      }
      if(k < i + REINI_LIMIT)continue;
     learn_atg ++;
#ifdef ATGDBMODE
      if(i != cds[0] - 1)printf("Non-Start upstream atg:");
      else               printf("Start              atg:");
#endif
      for(j = i - ATG_UP;j < i;j ++){
	if(j >= 0)around_atg[j - i + ATG_UP] = seqn[j];
	else around_atg[j - i + ATG_UP] = '-';
      }
      for(j = i + 3;j < i + 3 + ATG_DN;j ++){
	if(j < max)around_atg[j - i - 3 + ATG_UP] = seqn[j];
	else around_atg[j - i - 3 + ATG_UP] = '-';
      }
#ifdef ATGDBMODE
      for(j = 0; j < ATG_UP + ATG_DN;j ++){
	if(j == ATG_UP)printf(" atg ");
	else if(j == ATG_UP + 3)printf(" atg ");
	putchar(around_atg[j]);
      }
      putchar('\n');
#endif
      if(atg_learn(around_atg, (i==cds[0]-1)) == 1){
#ifdef ATGDBMODE
	printf("prediction correct\n");
#endif
	learn_correct ++;
      }

      else {
#ifdef ATGDBMODE
	printf("prediction wrong\n");
#endif
	entry_right_flag = 0;
      }
    }
  }
    total_right_entry += entry_right_flag;
}

atg_learn_fin(){

  FILE *fp;

  ++ learn_trial;
  printf("\nneural learning iteration:%d\n",learn_trial);

  printf("total entries:%d\n",total_entry/2 + (total_entry%2));

  printf("neural prediction correct:%d \nneural prediction total  :%d\n",
	 learn_correct,learn_atg);
  printf("neural successful rate:%.2lf%%\n",100.0*learn_correct/learn_atg);

  printf("\nright entry:%d\ntotal entry:%d\n",
	 total_right_entry,total_entry/2 + (total_entry%2));
  printf("rate:%.2lf%%\n",
	 100.0*total_right_entry/(total_entry/2 + (total_entry%2)));



  fp = fopen(weight_save_file,"w");

  fprintf(fp,"\nneural learning iteration:%d\n",learn_trial);
  fprintf(fp,"total entries:%d\n",total_entry);
  fprintf(fp,"neural prediction correct:%d \nneural prediction total  :%d\n",
	 learn_correct,learn_atg);
  fprintf(fp,"neural successful rate:%.2lf%%\n",
	  100.0*learn_correct/learn_atg);

  fclose(fp);

  neural_learn(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, 
	       weight_save_file, 3);
	       
  if(learn_mode == 2 || learn_mode == 3)return;
  if(100.0 * learn_correct / learn_atg < 100.0)all.p.iteration ++;
}

atg_learn_help(){

  printf("-learn\t ATG learning system(state file name)\n");
  printf("-learn_test\t test of learning（state file name）\n");
/*
  printf("-learn_better\t 翻訳開始領域を前の学習結果より予測(ファイル名指定)\n");
*/
  printf("-learn_sp\t ATG learning system Leaky Scanning(state file name)\n");
}



int atg_pred_par(int argc, char *argv[], int n){

  FILE *fp;

  if(strcmp(argv[n], "-atg_pred") == 0){
    strcpy(weight_save_file, argv[ ++ n]);
    
    if((fp = fopen(weight_save_file, "r")) != NULL){
      fclose(fp);
      neural_learn(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL,
		   weight_save_file, 0);
    }
    else {
      printf("weight_save_file \"%s\" not found\n",weight_save_file);
      exit(1);
    }
    return 2;
  }
  else return 0;
}

atg_pred_head(char *line){

}

atg_pred_ent(char *entry, char seqn[], int max, int cds[], int ncds){

  int i,j, k, pred_start;
  double in_A[I_UNIT], out_A[O_UNIT];

  if(ncds <= 0 || cds[0] <= ATG_UP ||
     strncmp("atg", &seqn[cds[0] - 1], 3) != 0)return;

  atg_pred_total ++;  if(atg_pred_total % 2 == 1)return;

/* Prediction start */
  for(i = 0; i < max - 2; i ++){
    if(strncmp(&seqn[i],"atg",3) == 0){

      /* Put nucleotide pattern around ATG trinucleotide into array */
      for(j = i - ATG_UP;j < i;j ++){
	if(j >= 0)around_atg[j - i + ATG_UP] = seqn[j];
	else around_atg[j - i + ATG_UP] = '-';
      }
      for(j = i + 3;j < i + 3 + ATG_DN;j ++){
	if(j < max)around_atg[j - i - 3 + ATG_UP] = seqn[j];
	else around_atg[j - i - 3 + ATG_UP] = '-';
      }
      
      nuc_to_bit(around_atg, in_A);
      neural_learn(I_UNIT, H_UNIT, O_UNIT, in_A, NULL, out_A, NULL, 2);
      
      if(out_A[0] > 0){ 
	for(k = i + 3;k < i + REINI_LIMIT;k += 3){
	  if(k >= max){ k = i + REINI_LIMIT; break; }
	  if(strncmp("taa",&seqn[k],3) == 0 ||
	     strncmp("tag",&seqn[k],3) == 0 ||
	     strncmp("tga",&seqn[k],3) == 0)break;
	}
	if(k >= i + REINI_LIMIT){pred_start = i + 1; break;} 
      }

    }
  }
  if(i == max - 2)pred_start = 0;

/* Prediction check */
    
  if(cds[0] == pred_start)atg_pred_correct ++;
  else {
    printf("%s", entry);
    printf("Correct   start:%d\n",cds[0]);
    printf("Predicted start:%d\n",pred_start);
    putchar('\n');
  }
}

atg_pred_fin(){

  neural_learn(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, 
	       NULL, 3);
  printf("Prediction total:%d  (sequence base)\n", atg_pred_total/2);
  printf("Prediction right:%d\n", atg_pred_correct);

  printf("Ratio:%.2lf%%\n",100.0 * atg_pred_correct / (atg_pred_total/2));

}

atg_pred_help(){

  printf("-atg_pred\t Prediction of initiation site by neural\n");

}




int atg_predm_par(int argc, char *argv[], int n){

  FILE *fp;
  int i;

  if(strcmp(argv[n], "-atg_predm") == 0){
    num_multi = atoi(argv[ ++ n]);
    for(i = 0;i < num_multi;i ++){
      strcpy(weight_multi_file[i], argv[ ++ n]);
/*    printf("file %d:%s\n",i, weight_multi_file[i]);  */
      neural_learn_mul(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, 
		   weight_multi_file[i], 0, 
		   &w_A1m[i], &w_A1m_bias[i], &w_A2m[i], &w_A2m_bias[i]);
    }
    return 1 + 1 + num_multi; /* parameter,number of files, file 1, file2.., */
  }
  else return 0;
}

atg_predm_head(char *line){

}

atg_predm_ent(char *entry, char seqn[], int max, int cds[], int ncds){

  int i,j, k, pred_start, posi = 0;
  double in_A[I_UNIT], out_A[O_UNIT];

  if(ncds <= 0 || cds[0] <= ATG_UP ||
     strncmp("atg", &seqn[cds[0] - 1], 3) != 0)return;

  atg_pred_total ++; if(atg_pred_total % 2 == 1)return;
  printf("%s",entry);

/* Prediction start */
  for(i = 0; i < max - 2; i ++){
    if(strncmp(&seqn[i],"atg",3) == 0){

      /* Put nucleotide pattern around ATG trinucleotide into array */
      for(j = i - ATG_UP;j < i;j ++){
	if(j >= 0)around_atg[j - i + ATG_UP] = seqn[j];
	else around_atg[j - i + ATG_UP] = '-';
      }
      for(j = i + 3;j < i + 3 + ATG_DN;j ++){
	if(j < max)around_atg[j - i - 3 + ATG_UP] = seqn[j];
	else around_atg[j - i - 3 + ATG_UP] = '-';
      }
      nuc_to_bit(around_atg, in_A);      

      posi = 0;
      for(k = 0;k < num_multi;k ++){

	neural_learn_mul(I_UNIT, H_UNIT, O_UNIT, in_A, NULL, out_A, NULL, 2,
			 &w_A1m[k],&w_A1m_bias[k],&w_A2m[k],&w_A2m_bias[k]);

	if(out_A[0] > 0)posi ++;

	printf("%d ",out_A[0] > 0); 
      }
      printf(":%d : %d", posi, posi > num_multi / 2); 

      if(posi > num_multi / 2){ 
	for(k = i + 3;k < i + REINI_LIMIT;k += 3){
	  if(k >= max){ k = i + REINI_LIMIT; break; }
	  if(strncmp("taa",&seqn[k],3) == 0 ||
	     strncmp("tag",&seqn[k],3) == 0 ||
	     strncmp("tga",&seqn[k],3) == 0)break; 
	}
	if(k >= i + REINI_LIMIT){ printf("::%d\n",i + 1 == cds[0]);  pred_start = i + 1; break;}
	else printf("::%d(s)\n",i + 1 == cds[0]); 
      }
      else printf("::%d\n",i + 1 == cds[0]); 
    }
  }
  if(i == max - 2)pred_start = 0;

/* Prediction check */
    
  if(cds[0] == pred_start)atg_pred_correct ++;
  else {
    printf("Incorrect:%s", entry);
    printf("Correct   start:%d\n",cds[0]);
    printf("Predicted start:%d\n",pred_start);
    putchar('\n');
  }
}

atg_predm_fin(){

  int i;

  for(i = 0;i < num_multi;i ++)
    neural_learn_mul(I_UNIT, H_UNIT, O_UNIT, NULL, NULL, NULL, NULL, 3,
		     &w_A1m[i],&w_A1m_bias[i],&w_A2m[i],&w_A2m_bias[i]);


  printf("Prediction total:%d  (sequence base)\n", atg_pred_total/2);
  printf("Prediction right:%d\n", atg_pred_correct);

  printf("Ratio:%.2lf%%\n",100.0 * atg_pred_correct / (atg_pred_total/2));

}

atg_predm_help(){

  printf("-atg_predm\t ニューラルによる翻訳開始領域の予測(ファイルを複数指定)\n");

}





/*
#define DBGMODE
*/
double sigmoid(x)
double x;
{

  return 1.0/(1 + exp(-x));

}

double sigmoid_d(x)
double x;
{

  return exp(-x) / (1 + exp(-x)) / (1 + exp(-x));

}

double **ddarray(s_num,s_len) /* allocates 2-d array */
int s_num,s_len;
{
  double *seque,**seque_table;
  int n;

  if((seque = (double *)malloc(s_num * s_len * sizeof(double))) == NULL)
    printf("memory allocation failed\n");

  if((seque_table = (double **)malloc(s_num * sizeof(double *))) == NULL)
    printf("memory allocation failed\n");

  for(n = 0;n < s_num;n ++)
    seque_table[n] = &seque[n * s_len];

  return seque_table;
}

void ddarrayfree(seque) /* releases memory for 2-d array */
double **seque;
{
  free(seque[0]);
  free(seque);
}

double intact_d(x)
double x;
{
  return x;
}

double mult2(x)
double x;
{
  return x * 2;
}

double mult10(x)
double x;
{
  return x * 10;
}

/* calculates activity value of units s_A according to
   input units in_A and weight w_A
*/
calc_Act(in_A, s_A, w_A, m, n, func)
double *in_A, *s_A;
double **w_A; /* w_A[j][i] is a weight from unit i to unit j */
int m,n;      /*    m is a number of input units 
	            n is a number of units to be calculated  */
double (*func)(); /* function to modify input;usually sigmoid function */
{
  int i,j;
  double weight_sum;
  
  for(j = 0;j < n;j ++){ /* calculates activity values of each unit j */
    
    weight_sum = 0;
    for(i = 0;i < m; i ++){
      weight_sum += (*func)(in_A[i]) * w_A[j][i];
#ifdef DBGMODE
      printf("%d->%d:(%lf will be output as ) %lf x %lf = %lf\n",
	     i,j,in_A[i],(*func)(in_A[i]),w_A[j][i],
	     (*func)(in_A[i]) * w_A[j][i]);
#endif
    }
    s_A[j] = weight_sum;
#ifdef DBGMODE
    printf("sum of weighted outputs:%lf\n",s_A[j]);
#endif
  }

}

/* add bias to units 
   activity value of units must be calculated 

   WARNING: core dump bug may exist in this routine!!
*/
add_bias(bias, s_A, w_A_bias, n)
double bias;             /* constant output of bias unit */
double *s_A, *w_A_bias;  /* activity values of units connected to
                            bias and its weight with bias */
int n;                   /* number of units connected to bias unit */
{
  int i;

  for(i = 0;i < n;i ++){
    s_A[i] += bias * w_A_bias[i];

#ifdef DBGMODE
    printf("bias -> %d: + %lf -> %lf\n",
	   i, w_A_bias[i], s_A[i]);
#endif
  }

}


/* calculates error values of the output layer */
calc_out_er(t_A, out_A, out_A_er, m, func, func_d)
double *t_A, *out_A; /* target values and active values of output units */
double *out_A_er;    /* errors to be calculated here */
int m;               /* number of units used in output layer */
double (*func)(), (*func_d)();
                     /* function used for calculating output and
			function used for scaling error
			differential of func is func_d */
{
  int i;
  double error1;

  for(i = 0;i < m;i ++){
    error1 = t_A[i] - (*func)(out_A[i]);
    out_A_er[i] = error1 * (*func_d)(out_A[i]);
#ifdef DBGMODE    
    printf("output error of unit %d:\n",i);
    printf("   [(target)%lf - (output)%lf] * (scale)%lf = %lf\n",
	   t_A[i], (*func)(out_A[i]), (*func_d)(out_A[i]),
	   out_A_er[i]);
#endif
  }
}

/* calculates error values of the hidden layer */
calc_hid_er(out_A_er, h_A_er, h_A, w_A2, m, n, func_d)
double *out_A_er;   /* error values of output layer */
double *h_A_er;     /* error values of hidden layer to be calculated */
double *h_A;        /* active values of hidden layer */
double **w_A2;      /* weight from hidden layer to output layer */
int m, n;           /* number of units in hidden layer and output layer */
double (*func_d)(); /* function for scaling;usually differential for sigmoid */
{
  int i,j;
  double weight_sum;
  
  for(j = 0;j < m;j ++){

    weight_sum = 0;
    for(i = 0;i < n;i ++){
      weight_sum += out_A_er[i] * w_A2[i][j];
#ifdef DBGMODE
      printf("%d->%d: (error)%lf x (weight)%lf = %lf\n",
	     j,i, out_A_er[i], w_A2[i][j], out_A_er[i] * w_A2[i][j]);
#endif
    }

    h_A_er[j] = weight_sum * (*func_d)(h_A[j]);
#ifdef DBGMODE
    printf("error value of hidden unit %d: %lf x %lf = %lf\n",
	   j, weight_sum, (*func_d)(h_A[j]), h_A_er[j]);
#endif
  }

}

/* change weight according to error values */
change_weight(act_A, a_er, w_A, m, n, r, func)
double *act_A;    /* active value of unit left */
double *a_er;     /* error value of unit right */
double **w_A;     /* weights from left to right */
int m, n;         /* number of left units and right units */
double r;         /* learning rate */
double (*func)(); /* function for calculating output; usually sigmoid */
{
  int i,j;

  for(j = 0;j < n; j ++){
    for(i = 0;i < m;i ++){

      w_A[j][i] += r * (*func)(act_A[i]) * a_er[j];
#ifdef DBGMODE
      printf("weight from %d to %d is now %lf\n",i,j,w_A[j][i]);
#endif
    }
  }
}

/* change weights of bias units */
change_bias_weight(bias, a_er, w_A_bias, n, r)
double bias;             /* constant output of bias unit */
double *a_er;            /* error values */
double *w_A_bias;        /* weight of bias units */
int n;                   /* number of units connected to bias unit */
double r;                /* learning rate */
{
  int i;

  for(i = 0;i < n; i ++){
    w_A_bias[i] += bias * r * a_er[i];

#ifdef DBGMODE
    printf("weight to %d is now %lf\n",i, w_A_bias[i]);
#endif

  }

}


/* calculate target answers from inputs */
ques_ans(in_A,t_A,m,n)
double *in_A;     /* input answers */
double *t_A;      /* target answers */
int m;            /* number of inputs */
int n;            /* number of outputs */
{
/* 
   
*/
  int i;
  if(m != 3 || n != 2){
    printf("ERROR:unexpected number of units\n");
    exit(1);
  }

  if(in_A[0] == 1 && in_A[1] == 0 && in_A[2] == 1)
    t_A[0] = 1;
  else t_A[0] = 0;

  if(in_A[0] == 0 && in_A[1] == 1 && in_A[2] == 0)
    t_A[1] = 1;
  else t_A[1] = 0;

}



neural_learn_mul(i_unit, h_unit, o_unit, 
		 in_A, out_A_t, out_A, filename, mode,
		 w_A1_p, w_A1_bias_p, w_A2_p, w_A2_bias_p)
int i_unit, h_unit, o_unit;
double *in_A, *out_A_t, *out_A;
char *filename;
int mode; 
double ***w_A1_p, **w_A1_bias_p;
double ***w_A2_p, **w_A2_bias_p;
/* mode : 
  0 = initialize with number of input unit(i_unit), hidden unit(h_unit),
      output unit(o_unit) 
      in_A, out_A_t and out_A can be NULL
      if filename is indicated, weight will be initialized by weights 
      recorded in file
      ex: neural_learn(3, 5, 2, NULL, NULL, NULL, filename, 0);
  1 = learn : weights in memory(allocated by initialization) is trained 
      and output is put in out_A(not passed to sigmoid function)
      filename can be NULL
      ex: neural_learn(3, 5, 2, in_A, out_A_t, out_A, NULL, 1);       
  2 = test : weights in memory is not modified
      output is put in out_A(not passed to sigmoid function)
      ex: neural_learn(3, 5, 2, in_A, NULL, out_A, NULL, 2);
  3 = end : release memories and write weight information to either 
            standart output(filename = NULL) or file indicated by filename
      ex: neural_learn(3, 5, 2, NULL, NULL, NULL, filename, 3);
*/
{
  int i,j;
  FILE *fp;

  switch(mode){
  case 0:
    *w_A1_p = ddarray(h_unit,i_unit);
    *w_A1_bias_p = (double *)malloc(1 * h_unit * sizeof(double));
    *w_A2_p = ddarray(o_unit,h_unit);
    *w_A2_bias_p = (double *)malloc(1 * o_unit * sizeof(double));

    if(filename != NULL)
      rweight(filename, i_unit, h_unit, o_unit, 
	      *w_A1_p, *w_A1_bias_p, *w_A2_p, *w_A2_bias_p);
    
    else {
/*    printf("\ninput -> hidden weight\n"); */
      for(i = 0;i < i_unit;i ++)
	for(j = 0;j < h_unit;j ++){
	  *w_A1_p[j][i] = 1.0 * (rand() % 20) / 10 - 1; 
/*	  printf("%d -> %d : %lf\n",i,j,w_A1[j][i]); */
	}
      
/*    printf("\nbias -> hidden weight\n"); */
      for(j = 0;j < h_unit;j ++){
	*w_A1_bias_p[j] = 1.0 * (rand() % 20) / 10 - 1;
/*	printf("b    -> %d : %lf\n",j,w_A1_bias[j]); */
      }
      
/*    printf("\nhidden -> output weight\n"); */
      for(i = 0;i < h_unit;i ++)
	for(j = 0;j < o_unit;j ++){
	  *w_A2_p[j][i] = 1.0 * (rand() % 20) / 10 - 1;
/*	  printf("%d -> %d : %lf\n",i,j,w_A2[j][i]); */
	}
      
/*    printf("\nbias -> output weight\n"); */
      for(j = 0;j < o_unit;j ++){
	*w_A2_bias_p[j] = 1.0 * (rand() % 20) / 10 - 1;
/*	printf("b    -> %d : %lf\n",j,w_A2_bias[j]); */
      }
    }

    break;

  case 1:
    backprop(i_unit, h_unit, o_unit, in_A, out_A_t, out_A,
	     *w_A1_p, *w_A1_bias_p, *w_A2_p, *w_A2_bias_p, 0.5, 1);
    break;

  case 2:
    backprop(i_unit, h_unit, o_unit, in_A, out_A_t, out_A,
	     *w_A1_p, *w_A1_bias_p, *w_A2_p, *w_A2_bias_p, 0.5, 2);
    break;

  case 3:
    if(filename != NULL){
      fp = fopen(filename,"a");
      fprintf(fp,"Input neurons used:  %d\n",i_unit);
      fprintf(fp,"Hidden neurons used: %d\n",h_unit);
      fprintf(fp,"Output neurons used: %d\n\n",o_unit);

      fprintf(fp,"\ninput -> hidden weight\n");
      for(i = 0;i < i_unit;i ++)
	for(j = 0;j < h_unit;j ++){
	  fprintf(fp,"%d -> %d : %lf\n",i,j,*w_A1_p[j][i]);
	}
      
      fprintf(fp,"\nbias -> hidden weight\n");
      for(j = 0;j < h_unit;j ++){
	fprintf(fp,"b    -> %d : %lf\n",j,*w_A1_bias_p[j]);
      }
      
      fprintf(fp,"\nhidden -> output weight\n");
      for(i = 0;i < h_unit;i ++)
	for(j = 0;j < o_unit;j ++){
	  fprintf(fp,"%d -> %d : %lf\n",i,j,*w_A2_p[j][i]);
	}
      
      fprintf(fp,"\nbias -> output weight\n");
      for(j = 0;j < o_unit;j ++){
	fprintf(fp,"b    -> %d : %lf\n",j,*w_A2_bias_p[j]);
      }
      fclose(fp);
    }

    ddarrayfree(*w_A1_p);
    ddarrayfree(*w_A2_p);
    free(*w_A1_bias_p);
    free(*w_A2_bias_p);
    break;

  default:
    printf("Mode error...%d\n",mode);
    break;
  }

}




neural_learn(i_unit, h_unit, o_unit, 
	     in_A, out_A_t, out_A, filename, mode)
int i_unit, h_unit, o_unit;
double *in_A, *out_A_t, *out_A;
char *filename;
int mode; 
/* mode : 
  0 = initialize with number of input unit(i_unit), hidden unit(h_unit),
      output unit(o_unit) 
      in_A, out_A_t and out_A can be NULL
      if filename is indicated, weight will be initialized by weights 
      recorded in file
      ex: neural_learn(3, 5, 2, NULL, NULL, NULL, filename, 0);
  1 = learn : weights in memory(allocated by initialization) is trained 
      and output is put in out_A(not passed to sigmoid function)
      filename can be NULL
      ex: neural_learn(3, 5, 2, in_A, out_A_t, out_A, NULL, 1);       
  2 = test : weights in memory is not modified
      output is put in out_A(not passed to sigmoid function)
      ex: neural_learn(3, 5, 2, in_A, NULL, out_A, NULL, 2);
  3 = end : release memories and write weight information to either 
            standart output(filename = NULL) or file indicated by filename
      ex: neural_learn(3, 5, 2, NULL, NULL, NULL, filename, 3);
*/
{
  int i,j;
  FILE *fp;

  static double **w_A1, *w_A1_bias;
  static double **w_A2, *w_A2_bias;

  switch(mode){
  case 0:
    w_A1 = ddarray(h_unit,i_unit);
    w_A1_bias = (double *)malloc(1 * h_unit * sizeof(double));
    w_A2 = ddarray(o_unit,h_unit);
    w_A2_bias = (double *)malloc(1 * o_unit * sizeof(double));

    if(filename != NULL)
      rweight(filename, i_unit, h_unit, o_unit, 
	      w_A1, w_A1_bias, w_A2, w_A2_bias);
    
    else {
/*    printf("\ninput -> hidden weight\n"); */
      for(i = 0;i < i_unit;i ++)
	for(j = 0;j < h_unit;j ++){
	  w_A1[j][i] = 1.0 * (rand() % 20) / 10 - 1; 
/*	  printf("%d -> %d : %lf\n",i,j,w_A1[j][i]); */
	}
      
/*    printf("\nbias -> hidden weight\n"); */
      for(j = 0;j < h_unit;j ++){
	w_A1_bias[j] = 1.0 * (rand() % 20) / 10 - 1;
/*	printf("b    -> %d : %lf\n",j,w_A1_bias[j]); */
      }
      
/*    printf("\nhidden -> output weight\n"); */
      for(i = 0;i < h_unit;i ++)
	for(j = 0;j < o_unit;j ++){
	  w_A2[j][i] = 1.0 * (rand() % 20) / 10 - 1;
/*	  printf("%d -> %d : %lf\n",i,j,w_A2[j][i]); */
	}
      
/*    printf("\nbias -> output weight\n"); */
      for(j = 0;j < o_unit;j ++){
	w_A2_bias[j] = 1.0 * (rand() % 20) / 10 - 1;
/*	printf("b    -> %d : %lf\n",j,w_A2_bias[j]); */
      }
    }

    break;

  case 1:
    backprop(i_unit, h_unit, o_unit, in_A, out_A_t, out_A,
	     w_A1, w_A1_bias, w_A2, w_A2_bias, 0.5, 1);
    break;

  case 2:
    backprop(i_unit, h_unit, o_unit, in_A, out_A_t, out_A,
	     w_A1, w_A1_bias, w_A2, w_A2_bias, 0.5, 2);
    break;

  case 3:
    if(filename != NULL){
      fp = fopen(filename,"a");
      fprintf(fp,"Input neurons used:  %d\n",i_unit);
      fprintf(fp,"Hidden neurons used: %d\n",h_unit);
      fprintf(fp,"Output neurons used: %d\n\n",o_unit);

      fprintf(fp,"\ninput -> hidden weight\n");
      for(i = 0;i < i_unit;i ++)
	for(j = 0;j < h_unit;j ++){
	  fprintf(fp,"%d -> %d : %lf\n",i,j,w_A1[j][i]);
	}
      
      fprintf(fp,"\nbias -> hidden weight\n");
      for(j = 0;j < h_unit;j ++){
	fprintf(fp,"b    -> %d : %lf\n",j,w_A1_bias[j]);
      }
      
      fprintf(fp,"\nhidden -> output weight\n");
      for(i = 0;i < h_unit;i ++)
	for(j = 0;j < o_unit;j ++){
	  fprintf(fp,"%d -> %d : %lf\n",i,j,w_A2[j][i]);
	}
      
      fprintf(fp,"\nbias -> output weight\n");
      for(j = 0;j < o_unit;j ++){
	fprintf(fp,"b    -> %d : %lf\n",j,w_A2_bias[j]);
      }
      fclose(fp);
    }

    ddarrayfree(w_A1);
    ddarrayfree(w_A2);
    free(w_A1_bias);
    free(w_A2_bias);
    break;

  default:
    printf("Mode error...%d\n",mode);
    break;
  }

}


backprop(i_unit, h_unit, o_unit, in_A, out_A_t, out_A, 
	      w_A1, w_A1_bias, w_A2, w_A2_bias, l_rate, mode)
int i_unit, h_unit, o_unit; /* numbers of input, hidden, output units */

/* memory for arrays must be allocated in parental routine */
double *in_A, *out_A_t;     /* input data and target data */
double *out_A;              /* output data */
double **w_A1, *w_A1_bias;   /* weight from input to hidden layer,
                                weight from bias to hidden layer */
double **w_A2, *w_A2_bias;   /* weight from hidden to output layer,
                                weight from bias to output layer */
double l_rate;              /* learning rate */
int mode;                   /* 1 = learning, 2 = test */
{

#define B_CONST 1.0

  FILE *fp;
  int i,j,k;
  double *h_A, *h_A_er;
  double *out_A_er;


  h_A = (double *)malloc(h_unit * sizeof(double));
  h_A_er = (double *)malloc(h_unit * sizeof(double));

  out_A_er = (double *)malloc(o_unit * sizeof(double));

#ifdef DBGMODE 
  printf("input:");
  for(i = 0;i < i_unit;i ++)printf("%2d ",(int)in_A[i]);
  putchar('\n');
  
  printf("target:");
  for(i = 0;i < o_unit;i ++)printf("%2d ",(int)out_A_t[i]);
  putchar('\n');
#endif 

  calc_Act(in_A, h_A, w_A1, i_unit, h_unit, intact_d);
  add_bias(B_CONST, h_A, w_A1_bias, h_unit);

#ifdef DBGMODE
  printf("\nactive value of hidden layer\n");
  for(i = 0;i < h_unit;i ++)
    printf("%lf\n",h_A[i]);
#endif

  calc_Act(h_A, out_A, w_A2, h_unit, o_unit, sigmoid);
  add_bias(B_CONST, out_A, w_A2_bias, o_unit);

#ifdef DBGMODE
  printf("\nactive value of output layer\n");
  for(i = 0;i < o_unit;i ++)
    printf("%lf\n",out_A[i]);
#endif
#ifdef DGMMODE 
  printf("\nfinal output\n");
  for(i = 0;i < o_unit;i ++)
    printf("%lf\n",sigmoid(out_A[i]));
#endif 
  if(mode == 2){
    free(h_A); free(h_A_er);
    free(out_A_er);

    return; /* In test mode, error calculation does not
                          have to be done */
  }

  calc_out_er(out_A_t, out_A, out_A_er, o_unit, sigmoid, sigmoid_d);

#ifdef DBGMODE
  printf("\noutput layer error\n");
  for(i = 0;i < o_unit;i ++)
    printf("%lf\n",out_A_er[i]);
#endif

#ifdef DBGMODE
  printf("recalculating hidden -> output weight\n");
#endif

  change_weight(h_A, out_A_er, w_A2, h_unit, o_unit, l_rate, sigmoid);
  change_bias_weight(B_CONST, out_A_er, w_A2_bias, o_unit, l_rate);


  calc_hid_er(out_A_er, h_A_er, h_A, w_A2, h_unit, o_unit, sigmoid_d);

#ifdef DBGMODE
  printf("\nhidden layer error\n");
  for(i = 0;i < h_unit;i ++)
    printf("%lf\n",h_A_er[i]);
#endif

#ifdef DBGMODE
  printf("recalculating input -> hidden weight\n");
#endif
  change_weight(in_A, h_A_er, w_A1, i_unit, h_unit, l_rate, intact_d);
  change_bias_weight(B_CONST, h_A_er, w_A1_bias, h_unit, l_rate);

  free(h_A); free(h_A_er);
  free(out_A_er);

}



rweight(file_name,i_unit,h_unit,o_unit,w_A1, w_A1_bias, w_A2, w_A2_bias)
char *file_name;
int i_unit, h_unit, o_unit;
double **w_A1, *w_A1_bias, **w_A2, *w_A2_bias;
{
  FILE *fp;
  char line[200];
  char str1[20], str2[20], str3[20], str4[20];
  int i_unit_r, h_unit_r, o_unit_r;
  int src,dst;
  double weight;
  int i,j,k;

  if((fp = fopen(file_name,"r")) == NULL){
    fprintf(stderr, "File %s does not exist.\n",file_name);
    exit(1);
  }

  while(fgets(line, 200, fp) != NULL){

    if(strncmp("Input neurons used:", line, strlen("Input neurons used:"))
       == 0){
      i_unit_r = atoi(&line[ strlen("Input neurons used:") ]);
/*    printf("Number of input neurons:%d\n",i_unit_r); */
      if(i_unit_r != i_unit){
	fprintf(stderr,"Number of input neurons is incorrect.\n");
	exit(1);
      }
    }

    if(strncmp("Hidden neurons used:", line, strlen("Hidden neurons used:"))
       == 0){
      h_unit_r = atoi(&line[ strlen("Hidden neurons used:") ]);
/*    printf("Number of hidden neurons:%d\n",h_unit_r); */
      if(h_unit_r != h_unit){
	fprintf(stderr,"Number of hidden neurons is incorrect.\n");
	exit(1);
      }
    }

    if(strncmp("Output neurons used:", line, strlen("Output neurons used:"))
       == 0){
      o_unit_r = atoi(&line[ strlen("Output neurons used:") ]);
/*    printf("Number of output neurons:%d\n",o_unit_r); */
      if(o_unit_r != o_unit){
	fprintf(stderr,"Number of output neurons is incorrect.\n");
	exit(1);
      }
    }

    if(strncmp("input -> hidden weight",line,strlen("input -> hidden weight"))
       == 0){
/*    printf("weight from input to hidden layer\n"); */
      for(i = 0;i < i_unit;i ++)
	for(j = 0;j < h_unit;j ++){
	  fgets(line, 200, fp);
	  sscanf(line,"%d %s %d %s %lf",&src,str1,&dst,str2,&weight);
	  w_A1[dst][src] = weight;
/*	  printf("%d --> %d : %lf\n",src, dst, weight); */
	}
/*    putchar('\n'); */
    }

    if(strncmp("bias -> hidden weight",line,strlen("bias -> hidden weight"))
       == 0){
/*    printf("weight from bias to hidden layer\n"); */
      for(i = 0; i < h_unit;i ++){
	fgets(line, 200, fp);
	sscanf(line,"%s %s %d %s %lf",str1,str2,&dst,str3,&weight);
	w_A1_bias[dst] = weight;
/*      printf("bias --> %d : %lf\n",dst,weight); */
      }
/*    putchar('\n'); */
    }

    if(strncmp("hidden -> output weight",line,
	       strlen("hidden -> output weight")) == 0){
/*    printf("hidden -> output weight\n"); */
      for(i = 0;i < h_unit;i ++)
	for(j = 0;j < o_unit;j ++){
	  fgets(line, 200, fp);
	  sscanf(line,"%d %s %d %s %lf",&src,str1,&dst,str2,&weight);
	  w_A2[dst][src] = weight;
/*	  printf("%d --> %d : %lf\n",src, dst, weight); */
	}
/*    putchar('\n'); */
    }

    if(strncmp("bias -> output weight",line,strlen("bias -> output weight"))
       == 0){
/*    printf("weight from bias to output layer\n"); */
      for(i = 0;i < o_unit;i ++){
	fgets(line, 200, fp);
	sscanf(line, "%s %s %d %s %lf",str1,str2,&dst,str3,&weight);
	w_A2_bias[dst] = weight;
/*	printf("bias --> %d : %lf\n",dst,weight); */
      }
/*    putchar('\n'); */
    }
  }
  fclose(fp);
}

int atg_learn(around_atg,start_flag)
char *around_atg;
int start_flag;    /* 1 = sequences around start codon */
/* warning : neural_learn system must be initialized before using 
   this subroutine */
{
  
#define L_RATE 0.5

  double in_A[I_UNIT], out_A_t[O_UNIT], out_A[O_UNIT];
  /*        input           input           output      */

  int i, j, trial, n1,n2,n3;
  char *nucleo = "atcg";
  char triplet[3];

  nuc_to_bit(around_atg, in_A);

  out_A_t[0] = start_flag;
  
  if(learn_mode == 2)
    neural_learn(I_UNIT, H_UNIT, O_UNIT, in_A, NULL,    out_A, NULL, 2);
  else
    neural_learn(I_UNIT, H_UNIT, O_UNIT, in_A, out_A_t, out_A, NULL, 1);

  if(start_flag == 1 && out_A[0] > 0.0)return 1;
  else if(start_flag == 0 && out_A[0] < 0.0)return 1;
  else return 0;

}
  
/* convert nucleotide sequence into bits
   'a' -> 1000   't' -> 0100   'c' -> 0010   'g' -> 0001
*/
nuc_to_bit(nuc_seq, b_bits)
char *nuc_seq;   /* input: nucleotide sequences */
double b_bits[]; /* output:bits; number of elements it can contain must 
                                 be 4 times as large as that of nuc_seq */
{
  int i,j;
  
  j = 0;
  for(i = 0; i < strlen(nuc_seq); i++){

    switch(nuc_seq[i]){

       case 'a':b_bits[j ++] = 1;b_bits[j ++] = 0;
                b_bits[j ++] = 0;b_bits[j ++] = 0;break;

       case 't':b_bits[j ++] = 0;b_bits[j ++] = 1;
                b_bits[j ++] = 0;b_bits[j ++] = 0;break;

       case 'c':b_bits[j ++] = 0;b_bits[j ++] = 0;
                b_bits[j ++] = 1;b_bits[j ++] = 0;break;

       case 'g':b_bits[j ++] = 0;b_bits[j ++] = 0;
                b_bits[j ++] = 0;b_bits[j ++] = 1;break;

        default:b_bits[j ++] = 0;b_bits[j ++] = 0;
                b_bits[j ++] = 0;b_bits[j ++] = 0;break;

       }
  }
}





