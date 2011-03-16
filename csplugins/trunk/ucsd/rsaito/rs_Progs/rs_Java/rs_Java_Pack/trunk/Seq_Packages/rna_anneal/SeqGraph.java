package rna_anneal;

import java.io.DataInputStream;

class  SeqGraph {
/* Private */
  char seq1[], seq2[]; /* $BG[Ns(B1,2$B$H$=$ND9$5(B */
  int  seq1_len, seq2_len;

  double min_free[][]; /* $B<+M3%(%M%k%.!<$N:G>.CM3JG<9TNs(B
			  seq1[p]$B$H(Bseq2[q]$B$,7k9g$9$k;~$N(Bp,q$B0J9_$N(B
			  $B:G>.<+M3%(%M%k%.!<(B */

  int min_free_valid[][]; /* $B:G>.CM9TNs$KCM$,3JG<$5$l$F$$$k$J$i(B1
			        $B$=$&$G$J$1$l$P(B0 */

  int min_next1[][]; /* $B:G>.$N<+M3%(%M%k%.!<$r$H$k;~$N<!$NG[Ns(B1$B$N7k9g0LCV(B */
  int min_next2[][]; /* $B:G>.$N<+M3%(%M%k%.!<$r$H$k;~$N<!$NG[Ns(B2$B$N7k9g0LCV(B */

  char result1[], result2[];
  char match_g_res1[], match_g_res2[];
  int result_len;
  int match_res1[], match_res2[];

/* Public */
/* SeqGraph$B$N=i4|2=!#G[Ns(B2$BK\$rEO$9!#(B */
  SeqGraph(String iseq1, String iseq2){
        
    seq1 = new char[ iseq1.length() + 1 ];
    seq1_len = iseq1.length();
    iseq1.getChars(0, iseq1.length(), seq1, 0);
    
    seq2 = new char[ iseq2.length() + 1 ];
    seq2_len = iseq2.length();
    iseq2.getChars(0, iseq2.length(), seq2, 0);
    
    min_free = new double [seq1_len][seq2_len];
    min_free_valid = new int [seq1_len][seq2_len];
    min_next1 = new int [seq1_len][seq2_len];
    min_next2 = new int [seq1_len][seq2_len];
    
    for(int i = 0;i < seq1_len;i ++)
      for(int j = 0;j < seq2_len;j ++){
	min_free_valid[i][j] = 0;
	min_next1[i][j] = -1;
	min_next2[i][j] = -1;
      }
    
    result1 = new char[1000];
    result2 = new char[1000];
    match_g_res1 = new char[1000];
    match_g_res2 = new char[1000];
    match_res1 = new int [1000];
    match_res2 = new int [1000];
    
  }
  
  String r1(){
     String ret = new String(result1, 0, result_len);
     return ret;
   }
  String r2(){
     String ret = new String(result2, 0, result_len);
     return ret;
   }

  String match_gr1(){
     String ret = new String(match_g_res1, 0, result_len);
     return ret;
   }
  String match_gr2(){
     String ret = new String(match_g_res2, 0, result_len);
     return ret;
   }



/* p$B$H(Bq$B$,7k9g$9$k;~$N(Bp,q$B0J9_$NItJ,$N:GDc<+M3%(%M%k%.!<(B */
/* $B:F5"%"%k%4%j%:%`(B: 
   find_min(p.q) = find(i,j) + basepair(i,j,i-1,j-1) + penalty(p,q,i-1,j-1)
*/
  double find_min(int p, int q){
    
    int i, j, pos1 = -1, pos2 = -1;
    double min = 0.0, fe1,fe, penalty, basep;
    char pair5[], pair3[];

    pair5 = new char[2];
    pair3 = new char[2];

    if(min_free_valid[p][q] > 0){
      return min_free[p][q];
    }
    /* $B$9$G$K7W;;:Q$_$J$i$=$NCM$r;H$&(B */
    
    else if(p == seq1_len - 1 || q == seq2_len - 1){
      return 0.0;
    }
    /* $BG[Ns$NKvC<$KC#$7$F$$$k(B */
    
    else if(p > seq1_len - 1 || q > seq2_len - 1){
      System.out.print("Error in recognizing sequence length...\n");
      return 0.0;
    }
    
    penalty = 0; basep = -4.0; /* temporary */
    
    for(i = p;i < seq1_len - 1;i ++)
      for(j = q;j < seq2_len - 1;j ++){
	
	/* $B:G=i$N(B5'$BKvC<0J30$OKvC<F1;N$,7k9g$7$F$$$J$/$F$O$J$i$J$$(B */
	if(p != 0 && q != 0 && i == p && j != q)i ++;
	if(p != 0 && q != 0 && i != p && j == q)j ++;
	
	if(bpairok(seq1[i], seq2[j]) > 0 &&
	      bpairok(seq1[i + 1], seq2[j + 1]) > 0){
	    fe1 = find_min(i + 1, j + 1);

	      /* $B%Z%J%k%F%#7W;;(B */
	      if(p == 0 && q == 0)penalty = 0.0;
	      else if(p == i && q == j)penalty = 0.0;
	      else penalty = str_penalty(i - p - 1, j - q - 1);
	      
	      /* $BBP9g$K$h$k<+M3%(%M%k%.!<$N7W;;(B */
	      pair5[0] = seq1[i]    ; pair5[1] = seq2[j];
	      pair3[0] = seq1[i + 1]; pair3[1] = seq2[j + 1];
	      basep = free_e(pair5, pair3);
	      
	    fe  = fe1 + penalty + basep;

	      if(fe < min){ 
		    min = fe;
		        pos1 = i;
		        pos2 = j;

		  } 
	  }
      }
    
    min_free[p][q] = min;
    min_next1[p][q] = pos1;
    min_next2[p][q] = pos2;
    min_free_valid[p][q] = 1;

    return min;
  }
  

  void getres_SeqGraph(){
    int i,j,k, u,v, n1,n2,lead, diff;

  i = 0; j = 0; u = 0; v = 0; lead = 0;
    while(i < seq1_len){
      if(min_next1[i][j] >= 0){
	n1 = min_next1[i][j];
	n2 = min_next2[i][j];

      for(k = i;k < n1;k ++){
	result1[u ++] = seq1[k]; 

      } /* $B<!$N7k9gD>A0$^$G$N1v4p$r3JG<(B */

	diff = n2 - n1;
      /* seq1 $B$H(B seq2 $B$N7k9g0LCV$N$A$,$$$r%.%c%C%W$GD4@0(B */

	for(k = 0;k < diff - lead;k ++)
	    result1[u ++] = '-';
	lead = diff;
	result1[u ++] = seq1[n1];

	match_res1[n1    ] = 1;
	match_res1[n1 + 1] = 1;
	i = n1 + 1; j = n2 + 1;
      }
      else {

	for(;i < seq1_len;i ++)
	    result1[u ++] = seq1[i];
	break;
      }
    }

    i = 0; j = 0; lead = 0;
    while(j < seq2_len){
      if(min_next2[i][j] >= 0){
	n1 = min_next1[i][j];
	n2 = min_next2[i][j];

	for(k = j;k < n2;k ++){
	    result2[v ++] = seq2[k];

	  } /* $B<!$N7k9gD>A0$^$G$N1v4p$r3JG<(B */

	diff = n1 - n2;
	/* seq1 $B$H(B seq2 $B$N7k9g0LCV$N$A$,$$$r%.%c%C%W$GD4@0(B */
	for(k = 0;k < diff - lead;k ++)
	    result2[v ++] = '-';
	lead = diff;
	result2[v ++] = seq2[n2]; 

      match_res2[n2    ] = 1;
	match_res2[n2 + 1] = 1;
	i = n1 + 1; j = n2 + 1;
      }
      else {

	for(;j < seq2_len;j ++)
	    result2[v ++] = seq2[j];
	break;
      }
    }
  
    if(u < v)for(i = u;i < v;i ++)result1[i] = '-';
    else if(u > v)for(j = v;j < u;j ++)result2[j] = '-';
    
    result_len = (u >= v) ? u : v;

    int ct = 0,mct = 0;
    for(i = 0;i < result_len;i ++)
      if(result1[i] == '-')match_g_res1[mct ++ ] = ' ';
      else {
	if(match_res1[ct] == 1)match_g_res1[mct ++ ] = '*';
	else match_g_res1[mct ++] = ' ';
	ct ++;
      }

    ct = 0;mct = 0;
    for(i = 0;i < result_len;i ++)
      if(result2[i] == '-')match_g_res2[mct ++] = ' ';
      else {
	if(match_res2[ct] == 1)match_g_res2[mct ++] = '*';
	else match_g_res2[mct ++] = ' ';
	ct ++;
      }
   
 
}
  

/* $B1v4p(Bc1$B$H(Bc2$B$,BP9g2DG=$J$i(B1$B$rJV$9!#$=$&$G$J$1$l$P(B0$B$rJV$9!#(B */
    public int bpairok(char c1, char c2){
      String pair[] = {
         "at", "cg", "gt"
	 };

      int i,j;

      for(i = 0; i < 3;i ++)
         if((c1 == pair[i].charAt(0) && c2 == pair[i].charAt(1)) ||
            (c1 == pair[i].charAt(1) && c2 == pair[i].charAt(0)))
               return 1;
      return 0;

    }


/* If return value is INP, invalid */

   public double free_e(char pair5[], char pair3[]){
      double free_e_mat[][] = {
      /*         3' AU    UA    CG    GC    UG    GU */
      /* 5' AU */{ -0.9, -0.9, -2.1, -1.7, -0.7, -0.5 },
      /*    UA */{ -1.1, -0.9, -2.3, -1.8, -0.5, -0.7 },
      /*    CG */{ -1.8, -1.7, -2.9, -2.0, -1.5, -1.5 },
      /*    GC */{ -2.3, -2.1, -3.4, -2.9, -1.9, -1.3 },
      /*    UG */{ -0.7, -0.5, -1.3, -1.5, -0.5, 1000.0 },
      /*    GU */{ -0.5, -0.7, -1.9, -1.5, 1000.0, -0.5 }};
      int i;
      char pair_point[][];
      int di_code[];

      pair_point = new char[2][2];
      di_code = new int[2];

      pair_point[0][0] = pair5[0]; pair_point[0][1] = pair5[1];
      pair_point[1][0] = pair3[0]; pair_point[1][1] = pair3[1];

      for(i = 0;i < 2;i ++){
         if(strncmp("at", pair_point[i], 2) == 0)di_code[i] = 0; 
         else if(strncmp("ta", pair_point[i], 2) == 0)di_code[i] = 1; 
         else if(strncmp("cg", pair_point[i], 2) == 0)di_code[i] = 2; 
         else if(strncmp("gc", pair_point[i], 2) == 0)di_code[i] = 3; 
         else if(strncmp("tg", pair_point[i], 2) == 0)di_code[i] = 4; 
         else if(strncmp("gt", pair_point[i], 2) == 0)di_code[i] = 5; 
         else di_code[i] = 1000;
       }
      if(di_code[0] == 1000 || di_code[1] == 1000)return 1000.0; 
      else return free_e_mat[ di_code[0] ][ di_code[1] ];

    }

   public double str_penalty(int n1, int n2){

     int b1, b2;

     double bulge[] = {
       /*  ?0    ?1    ?2    ?3    ?4    ?5    ?6    ?7    ?8    ?9  */ 
           0.0,  3.3,  5.2,  6.0,  6.7,  7.4,  8.2,  9.1, 10.0, 10.5, /*  0- 9 */
          11.0, 11.0, 11.8, 11.8, 12.5, 12.5, 13.0, 13.0, 13.6, 13.6, /* 10-19 */
          14.0, 14.0, 14.0, 14.0, 14.0, 15.0, 15.0, 15.0, 15.0, 15.0, /* 20-29 */
          15.8 };

     double interior[] = {
       /*  ?0    ?1    ?2    ?3    ?4    ?5    ?6    ?7    ?8    ?9  */ 
           0.0,  0.8,  0.8,  1.3,  1.7,  2.1,  2.5,  2.6,  2.8,  3.1, /*  0- 9 */
           3.6,  3.6,  4.4,  4.4,  5.1,  5.1,  5.6,  5.6,  6.2,  6.2, /* 10-19 */
           6.6,  6.6,  6.6,  6.6,  6.6,  7.6,  7.6,  7.6,  7.6,  7.6, /* 20-29 */
           8.4 };

     b1 = (n1 >= n2) ? n1 : n2;
     b2 = (n2 <  n1) ? n2 : n1;
  
     if(b2 > 0){
        if(b1 < interior.length)return interior[b1];
        else return interior[ interior.length - 1 ];
      }
     else {
        if(b1 < bulge.length)return bulge[b1];
        else return bulge[ bulge.length - 1 ];
      }
   }
 
   public int strncmp(String str1, char str2[], int n){
      int i;
      for(i = 0;i < n;i ++)
         if(str1.charAt(i) != str2[i])break;
      if(i == n)return 0;
      else return 1;


    }
/*
   public static void main(String args[]){
     char seque1[], seque2[];
     double min_fe;
     int i,j,ct;
     String sequence1, sequence2;

     try {
        System.out.print("Sequence 1:");
        System.out.flush();
        sequence1 = new DataInputStream(System.in).readLine();

	}catch(java.io.IOException e){
        System.out.println("Could not get input.");
        return;
	}

     try { 
        System.out.print("Sequence 2:");
        System.out.flush();
        sequence2 = new DataInputStream(System.in).readLine();
	}catch(java.io.IOException e){
        System.out.println("Could not get input.");
        return;
	}

     System.out.print("Finding optimal solution...\n");
     SeqGraph sg = new SeqGraph(sequence1, sequence2);
     min_fe = sg.find_min(0, 0);
     sg.getres_SeqGraph();
     System.out.println(sg.match_gr1());
     System.out.println(sg.r1());
     System.out.println(sg.r2());
     System.out.println(sg.match_gr2());

     System.out.print("Free energy = ");
     System.out.println(min_fe);

     }
 */
};


