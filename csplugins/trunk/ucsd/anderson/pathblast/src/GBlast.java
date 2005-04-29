import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;

public class GBlast {

  static final int MAX_VER=20000;	//max. vertices in orthology graph
  static final int MAX_GENES=8000;	//largest genome
  static final int NAME_LEN=20;

  static final int SUBNET=15;
  static final int MAX_BFS=4;
  static final double MAX_OVERLAP=0.8;	//max overlap between 2 networks
					// before filtering
  static final int Y_ANNOTS=70;
  static final int Y_CANNOTS=500;
  static final int N_PARTIAL=5;
  static final int PATH_LEN=4;

  static final double TINY=1.E-100;
  static final int BLOCK=100;		//for memory allocations
  static final int SIM_NO=10;		//number of simulations
  static final double PROB_TH=0.01;	//used in definition of orthology
					//graph edges
  static final int CV_INT=5;		//cv iterations

  static DecimalFormat six_two_format = new DecimalFormat("#####0.00");
  static DecimalFormat two_six_format = new DecimalFormat("#0.000000");
  static DecimalFormat two_eight_format = new DecimalFormat("#0.00000000");
  static DecimalFormat e_format = new DecimalFormat("0.000000E00");

// represents homolog tuples
class hom_s {
//  char nameG[3][NAME_LEN];
  String nameG[] = new String[3];
  double e_val;
  int ind[] = new int[3];
  int complexY[] = new int[20];
  int ncY;
};

class single_weight_s {
  double w;
  int v;
};

//for interactions
class edge_s {
  int i1;
  int i2;
  double prob;
};


//global vars
int KS;
hom_s homologs[]; //homologs (vertices of G)
int G[][] = new int[MAX_VER][];//homolog graph
int h_num;//number of homologs
int degG[] = new int[MAX_VER];//degrees in G
//char geneNames[3][MAX_GENES][NAME_LEN];//gene names
String geneNames[][] = new String[3][MAX_GENES];//gene names

int CV_SPECIES = 0;

//int simulation = 0;//is simulation required
boolean simulation = false;//is simulation required

//edge_s interactions[][] = new edge_s[3][]; //interactions 
edge_s interactions[][] = new edge_s[3][]; //interactions 
int n_interactions[] = new int[3]; //their number
static int degS[][] = new int[3][MAX_GENES]; //degrees in species graphs
static double edegS[][] = new double[3][MAX_GENES]; //expected degrees in species graphs
//static single_weight_s s_graph[][] = new single_weight_s[3][MAX_GENES];
static single_weight_s s_graph[][][] = new single_weight_s[3][MAX_GENES][];
int real_h_num; //their number of non-isolated
static int Gmem[] = new int [MAX_VER];//allocated memory for G
int n_genes[] = new int[3];//num. of genes for species
int sens;
//char i_prefix[20], o_prefix[20];
String i_prefix, o_prefix;
int mips_level = 3;
double alpha = 0.8; //prob. of edge in a complex, optimized value
double e_total_weight[] = new double[3];
int graphNeeded = 0;
double p_true[] = new double[3], p_te[] = new double[3], true_factor[] = new double[3];
double e_e[] = new double[3], s_e[] = new double[3];
static double sim_complex_weights[][] = new double[SUBNET][SIM_NO*MAX_VER];
static double sim_path_weights[] = new double[SIM_NO*MAX_VER];
static int c_sols[][] = new int[MAX_VER*N_PARTIAL][SUBNET+1];
static int p_sols[][] = new int[MAX_VER*N_PARTIAL][SUBNET+1];
static double c_weights[] = new double[MAX_VER*N_PARTIAL];
static double p_weights[] = new double[MAX_VER*N_PARTIAL];
int nc_sols, np_sols;
int cv_iter = -1;
static edge_s hidden[] = new edge_s[1000];
int n_hidden, hide_n, hide_p;
double cv_lower_bound[] = new double[3];
double blTh = 1.E-7;
int conn_flag = 0; //is connectivity of complexes required
int all2_flag = 0; //are all-2 edges allowed for 2 species

double random_path_th = 28.922;
//double random_net_th[SUBNET+1] = {100, 100, ...};
double random_net_th[] = {100, 100, 100, 100, 27.1633, 35.2907, 42.6228, 49.9315, 57.6441, 66.8792, 77.7948, 90.5614, 103.878, 116.668, 125.873, 135.523};

//MDA - new global variables
Random random;
StringBuffer sb = new StringBuffer(256);

//MDA - move these statics to class variables - from bfs_search()
int sgenes[][] = new int[MAX_VER][200], n_sgenes[] = new int[MAX_VER];
int i_call = 0;

//static final int NULL = 0;


//////////////////////////////////

public static void main(String args[]) {
 new GBlast().mainProcessing(args);
}

void mainProcessing(String args[]) {
  int i, j;

  if( args.length < 4 ) {
	System.out.print(
		"Usage: -i <i_prefix> -o <o_prefix> [-s (simulation mode)]\n");
	System.exit(0);
  }
  for( i=0 ; i<KS ; i++ ) {
	true_factor[i] = 2.;
	interactions[i] = null;
  }

  for (i = 0; i < args.length; i++) {
    if (args[i].equals("-i")) {
	i_prefix = args[i+1];
	i++;
    }
    if (args[i].equals("-o")) {
	o_prefix = args[i+1];
	i++;
    }
    if (args[i].equals("-s")) {
	simulation = true;
	System.out.print("simulation mode\n");
    }
  }

  read_params( i_prefix );

  for( i=0 ; i<KS ; i++ ) {
	n_genes[i] = 0;
	for( j=0 ; j<MAX_GENES ; j++ )
	  s_graph[i][j] = null;
  }
  for( i=0 ; i<MAX_VER ; i++ )
	G[i] = null;
//	G[i] = NULL;
  homologs = null;

  //read data and initialize
  System.out.println("Fixed random seed");
//  srand48( 0xe3d92ab5 );
  random = new Random(0xe3d92ab5);

  read_homologs( i_prefix );
  read_interactions( i_prefix );
  build_graph( o_prefix );

  if( simulation ) {
// MDA - don't need to clear/bzero arrays since initialized at creation
//	bzero( sim_complex_weights, sizeof( sim_complex_weights ) );
//	bzero( sim_path_weights, sizeof( sim_path_weights ) );
	for( i=0 ; i<SIM_NO ; i++ ) 
	  bfs_search( o_prefix, i_prefix );
	compute_thresholds();
	simulation = false;
	read_homologs( i_prefix );
	read_interactions( i_prefix );
	build_graph( o_prefix );
  }
  //search for complexes and paths
  bfs_search( o_prefix, i_prefix );


  //free memory
  System.out.println("Finished.");

/* MDA - not needed...

//  if( homologs )
  if( homologs != null)
//	free( homologs );
	homologs = null;
  for( i=0 ; i<KS ; i++ ) {
//	free( interactions[i] );
	 interactions[i] = null;
	for( j=0 ; j<MAX_GENES ; j++ )
	  if( degS[i][j]>0 )
//		free( s_graph[i][j] );
		s_graph[i][j] = null;
  }
  for( i=0 ; i<h_num ; i++ )
//	free( G[i] );
	G[i] = null;
//	G[i] = NULL;
*/

}


void read_params( String param_file ) 
{
  String tmp, tokens[];
  int i;

//  sprintf(tmp,"%s_params", param_file);
  tmp = param_file+"_params";
  File f = new File(tmp);
//  if( (fp = fopen( tmp, "r" )) == null)
  if (!f.canRead())
	my_exit("error opening param file");

  try {

    BufferedReader reader = new BufferedReader(new FileReader(tmp));

    tokens = reader.readLine().split("\\s");
    KS = Integer.parseInt(tokens[1]);
    tokens = reader.readLine().split("\\s");
    alpha = Double.parseDouble(tokens[1]);
    tokens = reader.readLine().split("\\s");
    blTh = Double.parseDouble(tokens[1]);
    tokens = reader.readLine().split("\\s");
    graphNeeded = Integer.parseInt(tokens[1]);
    tokens = reader.readLine().split("\\s");
    conn_flag = Integer.parseInt(tokens[1]);
    tokens = reader.readLine().split("\\s");
    all2_flag = Integer.parseInt(tokens[1]);
    tokens = reader.readLine().split("\\s");
    true_factor[0] = Double.parseDouble(tokens[1]);
    tokens = reader.readLine().split("\\s");
    true_factor[1] = Double.parseDouble(tokens[1]);
    tokens = reader.readLine().split("\\s");
    true_factor[2] = Double.parseDouble(tokens[1]);
    tokens = reader.readLine().split("\\s");
    random_path_th = Double.parseDouble(tokens[1]);
    tokens = reader.readLine().split("\\s");
    random_net_th[0] = Double.parseDouble(tokens[1]);
    for( i=1 ; i<=SUBNET ; i++ ) {
        random_net_th[i] = Double.parseDouble(tokens[i+1]);
    }
    reader.close();
  } catch (Exception e) {
    e.printStackTrace();
  }
/*
  fscanf(fp," %s %d ", tmp, &KS);
  fscanf(fp," %s %lf ", tmp, &alpha);
  fscanf(fp," %s %lf ", tmp, &blTh);
  fscanf(fp," %s %d ", tmp, &graphNeeded);
  fscanf(fp," %s %d ", tmp, &conn_flag);
  fscanf(fp," %s %d ", tmp, &all2_flag);
  fscanf(fp," %s %lf ", tmp, &(true_factor[0]));
  fscanf(fp," %s %lf ", tmp, &(true_factor[1]));
  fscanf(fp," %s %lf ", tmp, &(true_factor[2]));
  fscanf(fp," %s %lf ", tmp, &random_path_th);
  fscanf(fp," %s %lf ", tmp, &(random_net_th[0]));
  for( i=1 ; i<=SUBNET ; i++ )
	fscanf(fp, " %lf ", &(random_net_th[i]));
  }
*/

//  printf("Params: %s %s alpha=%g factor=%g,%g,%g th=%g\n",
//		 i_prefix, o_prefix, 
//		 alpha, true_factor[0], (KS>1)?true_factor[1]:(-1), 
//		 (KS>2)?true_factor[2]:(-1), blTh);
//
//  System.out.print("Params: "+i_prefix+" "+o_prefix+" alpha="+alpha+" factor="+
//		 true_factor[0]+","+((KS>1)?true_factor[1]:(-1))+","+
//		 ((KS>2)?true_factor[2]:(-1))+" th="+blTh+"\n");
  sb.delete(0,sb.length());
  sb.append("Params: ");
  sb.append(i_prefix);
  sb.append(" ");
  sb.append(o_prefix);
  sb.append(" alpha=");
  sb.append(alpha);
  sb.append(" factor=");
  sb.append(true_factor[0]);
  sb.append(",");
  sb.append(((KS>1)?true_factor[1]:(-1)));
  sb.append(",");
  sb.append(((KS>2)?true_factor[2]:(-1)));
  sb.append(" th=");
  sb.append(blTh);
  System.out.println(sb);

//  fclose( fp );
}


//currently finds paths of length 4.
//searches for paths of the form i - root - j - v
//int find_path( char flag1[MAX_VER], 
//		int part_sols[N_PARTIAL][SUBNET+1], 
//		double part_weights[N_PARTIAL], int root, int i_call )
int find_path( byte flag1[], 
		int part_sols[][], 
		double part_weights[], int root, int i_call )
{
  int n_sols = 0;
  int size = 0, min_i;
  int dim_index[] = new int[MAX_VER];
  int i, j, k;
  double weight, max_weight = 0, min_w;
  int tmp_sol[] = new int[PATH_LEN];

  part_weights[0] = 0;

  for( i=0 ; i<h_num ; i++ )
//	if( flag1[i] && distinct_nodes( i, root ) )
	if( flag1[i] != 0 && distinct_nodes( i, root ) )
	  dim_index[size++] = i;

  for( i=0 ; i<size ; i++ ) {
	for( j=i+1 ; j<size ; j++ ) {
	  //if i,root,j are on a potentially length-4 path
	  if( distinct_nodes(dim_index[i],dim_index[j]) ) {
		weight = compute_path_weight( dim_index[i], dim_index[j], root,
							  tmp_sol, flag1 );
		if( (simulation && (weight>0)) || (weight>=random_path_th) ) {
		  if( weight>max_weight )
			max_weight = weight;
		  if( n_sols<N_PARTIAL )
			min_i = n_sols;
		  else {
			min_i = 0;
			min_w = part_weights[0];
			for( k=1 ; k<n_sols ; k++ ) {
/* MDA -- try part_weights[k] instead of part_weights[j]
			  if( part_weights[j]<min_w ) {
*/
			  if( part_weights[k]<min_w ) {
				min_w = part_weights[k];
				min_i = k;
			  }
			}
			if( weight<=min_w )
			  min_i = -1;
		  }
		  if( min_i>=0 ) {
			for( k=0 ; k<PATH_LEN ; k++ )
			  part_sols[min_i][k] = tmp_sol[k];
			part_sols[min_i][SUBNET] = PATH_LEN;
			part_weights[min_i] = weight;
			if( n_sols<N_PARTIAL )
			  n_sols++;
		  }
		}
	  }
	}
  }
//MDA
  if( n_sols>0 ) {
//	printf("%d: %s path weight %lf\n", root, simulation?"SIM":"REAL",
//		  max_weight);
//
//	System.out.println(root+": "+(simulation?"SIM":"REAL")+" path weight "
//		 +two_six_format.format(max_weight));
	sb.delete(0,sb.length());
	sb.append(root);
	sb.append(": ");
	sb.append((simulation?"SIM":"REAL"));
	sb.append(" path weight ");
	sb.append(two_six_format.format(max_weight));
	System.out.println(sb);
  }

  if( simulation )
	sim_path_weights[(i_call-1)*MAX_VER+root] = max_weight;
  return n_sols;
}


//double compute_path_weight( int a, int b, int root, int *tmp_sol,
//				char flag1[MAX_VER] )
double compute_path_weight( int a, int b, int root, int[] tmp_sol,
				byte flag1[] )
{
  int i, iter, j, u, v, k, l, w;
  double weight = 0, pw, part;
  int path[] = new int[4];
//  char flag;
  byte flag;

  part = edge_weight( a, root ) + edge_weight( root, b );
  tmp_sol[1] = root;
  //try to extend the path to both sides
  for( iter=0 ; iter<2 ; iter++ ) {
//	if( iter ) {
	if( iter != 0 ) {
	  u = b;
	  v = a;
	} else {
	  u = a;
	  v = b;
	}
	if( degG[v]<1 )
	  continue;

	for( k=0 ; k<degG[v] ; k++ ) {
	  w = G[v][k];
	  if( (!distinct_nodes( w, u )) || (!distinct_nodes( w, v )) ||
		  (!distinct_nodes( w, root )) )
		continue;
	  pw = part + edge_weight( v, w );
	  if( pw>weight ) {
		tmp_sol[0] = u;
		tmp_sol[2] = v;
		tmp_sol[3] = w;
		weight = pw;
	  }
	}
  }
  return weight;
}


//given bfs from a vertex, enumerate complexes of required size
//inside a 2-neighborhood
//int find_heaviest( char flag[MAX_VER], char flag2[MAX_VER],
//			int part_sols[N_PARTIAL][SUBNET+1], 
//			double part_weights[N_PARTIAL], int root,
//			int *best_path, int i_call )
int find_heaviest( byte flag[], byte flag2[],
			int part_sols[][], 
			double part_weights[], int root,
			int[] best_path, int i_call )
{
  int i, j, k, l, p, q;
  int size = 0, ac_size, max_size = 0;
  double weight=0., max, min, max_w, max_weight = 0.;
  int i_root, max_i, min_i;
  int dim_index[] = new int[MAX_VER], tmp_dim[] = new int[MAX_BFS];
//  char max_index[] = new char[MAX_VER], index[] = new char[MAX_VER];
  byte max_index[] = new byte[MAX_VER], index[] = new byte[MAX_VER];
  int n_sol = 0;
  int present[][] = new int[3][MAX_GENES]; //records proteins in the complex
  double delta_w = 0.;
  int y_size, start;
  int path_iter;
  int best[] = new int[SUBNET+1];
  double w_best;
  int sim_index = 0;
//  int all_one_flag = 0;
//MDA
  boolean all_one_flag = false;
//  boolean all_one_flag = true;

  int c_flag;

//MDA
//System.out.println("all_one_flag = true for debugging");
  
  if( simulation )
	sim_index = (i_call-1)*MAX_VER+root;

// MDA - don't need to bzero since initialized to zeroes
//  bzero( present, sizeof( present ) );
//  bzero( max_index, sizeof( max_index ) );

  part_sols[0][SUBNET] = 0;

  i_root = 0;
  for( l=0 ; l<KS ; l++ )
	present[l][homologs[root].ind[l]] = 1;
  size = 1;
  dim_index[0] = root; 

  //greedily form set of MAX_BFS heaviest nodes, which includes root
  while( size<MAX_BFS ) {
	max_w = 0;
	k = -1;
	for( j=0 ; j<h_num ; j++ ) {	//try to add j with disjoint proteins
//	  if( !flag[j] )
          if (flag[j]==0)
		continue;
	  for( i=0 ; i<size ; i++ ) 
		if( dim_index[i]==j )
		  break;
	  if( i<size )
		continue;
	  for( l=0 ; l<KS ; l++ )
//		if( present[l][homologs[j].ind[l]] )
		if( present[l][homologs[j].ind[l]]!=0 )
		  break;
	  if( l<KS )
		continue;
	  delta_w = homologs[j].e_val;//currently mode weights are always 0
	  for( i=0 ; i<size ; i++ ) {
		for( l=0 ; l<KS ; l++ ) {
		  if( present[l][homologs[j].ind[l]]==0 ) {
			delta_w += (compute_vp_weight( homologs[j].ind[l],
										   homologs[dim_index[i]].ind[l], l )/
						(double)present[l][homologs[dim_index[i]].ind[l]]);
		  }
		}
	  }
	  if( delta_w>max_w ) {
		max_w = delta_w;
		k = j;
	  }
	}
	if( k<0 )
	  break;
	for( l=0 ; l<KS ; l++ ) 
	  present[l][homologs[k].ind[l]]++;
	dim_index[size++] = k;
	weight += max_w;
  }

  start = (size>=4) ? 11 : ((1<<size)+1); //starting with 7 will give
                                          //the same 4-vertex seed 
  for( j=start ; j<=(1<<size)+1 ; j+=2 ) { // enumerate subsets containing root
	                                       // using seed or best path
	path_iter = 0;
	if( j>(1<<size) ) {
	  //initialize to best path
	  if( best_path==null)
		continue;
	  if( best_path[1]!=root )
		my_exit("path - root error");
	  path_iter = 1;
	  dim_index[1] = best_path[0];
	  dim_index[2] = best_path[2];
	  dim_index[3] = best_path[3];
	  size = 4;
	  j = 31;
	} else {	  
	  i = 0;
	  for( k=0 ; k<size ; k++ ) {
//		if( (j>>k)&1 )
		if( ((j>>k)&1)==1 )
		  i++;
	  }
	  //bound complex size 
	  if( (i>SUBNET) || (i<3) )
		continue;
	}
	ac_size = 0;

	//now compute weight of seed

//	bzero( index, MAX_VER );
//	bzero( present, sizeof( present ) );
//	bzero( tmp_dim, sizeof( tmp_dim ) );

	index = new byte[MAX_VER];
	present = new int[3][MAX_GENES];
	tmp_dim = new int[MAX_BFS];
/*
	int ii, jj;
	for (ii=0; ii < MAX_VER; ii++) index[ii]=0;
	for (ii=0; ii < 3; ii++)
	    for (jj=0; jj < MAX_GENES; jj++) present[ii][jj]=0;
	for (ii=0; ii < MAX_BFS; ii++) tmp_dim[ii]=0;
*/

	y_size = 0;
	if( all_one_flag )
//	  printf("seed: ");
	  System.out.print("seed: ");
	for( i=0 ; i<size ; i++ )
//	  if( (j>>i)&1 ) {
	  if( ((j>>i)&1)==1 ) {
		if( all_one_flag ) {
//		  printf("%d (%d,%d,%d) ", dim_index[i],
//				 homologs[dim_index[i]].ind[0],
//				 homologs[dim_index[i]].ind[1],
//				 homologs[dim_index[i]].ind[2]);

//		  System.out.print(dim_index[i]+" ("+
//				 homologs[dim_index[i]].ind[0]+","+
//				 homologs[dim_index[i]].ind[1]+","+
//				 homologs[dim_index[i]].ind[2]+") ");
		  sb.delete(0,sb.length());
		  sb.append(dim_index[i]);
		  sb.append(" (");
		  sb.append(homologs[dim_index[i]].ind[0]);
		  sb.append(",");
		  sb.append(homologs[dim_index[i]].ind[1]);
		  sb.append(",");
		  sb.append(homologs[dim_index[i]].ind[2]);
		  sb.append(") ");
		  System.out.print(sb);
		}

		index[dim_index[i]] = 2;
		tmp_dim[ac_size++] = dim_index[i];
		for( l=0 ; l<KS ; l++ )
		  present[l][homologs[dim_index[i]].ind[l]]++;
		if( present[0][homologs[dim_index[i]].ind[0]]==1 )
		  y_size++;
	  }
	if( (KS==1) && (y_size<4) )
	  continue;

	weight = compute_sgw( tmp_dim, ac_size, present );

	if( all_one_flag ) {
//	  printf(" == w=%g\n", weight);
	  sb.delete(0,sb.length());
	  sb.append(" == w=");
	  sb.append(weight);
	  System.out.println(sb);
	}

	if( (weight<0) )
		continue;

//	bzero( best, sizeof( best ) );
	best = new int[SUBNET+1];
//	for (ii=0; ii <SUBNET+1; ii++) best[ii]=0;

	w_best = 0;

	if( (ac_size>=4) && (weight>w_best) &&
		(simulation || (weight>=random_net_th[ac_size])) ) {
	  w_best = weight;
	  k = 0;
	  for( i=0 ; i<h_num ; i++ )
//		if( index[i] )
		if( index[i]!=0 )
		  best[k++] = i;
	  best[SUBNET] = k;
	  if( simulation ) {
		if( sim_complex_weights[ac_size-1][sim_index]<w_best )
		  sim_complex_weights[ac_size-1][sim_index] = w_best;
	  }
	}
	//greedily add/remove most contributing vertex
//	while( 1 ) {
	while( true ) {
	  max_i = -1;
	  max_w = 0;
	  for( i=0 ; i<h_num ; i++ ) { // try to add/remove vertex i
//		if( (!flag2[i]) || (index[i]==2/*in seed*/)  )
		if( (flag2[i]==0) || (index[i]==2/*in seed*/)  )
		  continue;
//		if( index[i] )
		if( index[i]!=0 )
		  k = ac_size-1;
		else {
		  k = ac_size+1;
		  //check for connectivity
//		  if( conn_flag ) {
		  if( conn_flag!=0 ) {
			c_flag = 0;
			for( q=0 ; q<h_num ; q++ ) {
//			  if( index[q] ) {
			  if( index[q]!=0 ) {
				for( p=0 ; p<degG[q] ; p++ )
				  if( G[q][p]==i ) {
					c_flag = 1;
					break;
				  }
//				if( c_flag )
				if( c_flag!=0 )
				  break;
			  }
			}
//			if( !c_flag )
			if( c_flag==0 )
			  continue;
		  }
		}
//		w_addition( index, i, &delta_w, present );
		double a_delta_w[] = new double[1];
		a_delta_w[0] = delta_w;
		w_addition( index, i, a_delta_w, present );
		delta_w = a_delta_w[0];

//		if( (index[i] || (ac_size<SUBNET)) && (delta_w>max_w)) {
		if( (index[i]!=0 || (ac_size<SUBNET)) && (delta_w>max_w)) {
		  max = weight+delta_w;
		  max_i = i;
		  max_w = delta_w;
		}
	  }
	  if( (max_i<0) || (max_w<=0) )
	    break;
	  if( all_one_flag ) {
//		printf("Modifying %d %g\n", max_i, max_w);
//		System.out.println("Modifying "+max_i+" "+ max_w);
		sb.delete(0,sb.length());
		sb.append("Modifying ");
		sb.append(max_i);
		sb.append(" ");
		sb.append(max_w);
		System.out.println(sb);
	  }
//	  if( index[max_i] ) {
	  if( index[max_i]!=0 ) {
		index[max_i] = 0;
		for( l=0 ; l<KS ; l++ )
		  present[l][homologs[max_i].ind[l]]--;
		ac_size--;
	  } else {
		index[max_i] = 1;
		for( l=0 ; l<KS ; l++ )
		  present[l][homologs[max_i].ind[l]]++;
		ac_size++;
	  }
	  weight += max_w;
	  if( (ac_size>=4) && (weight>w_best) &&
		  (simulation || (weight>=random_net_th[ac_size])) ) {
		w_best = weight;
		k = 0;
		for( i=0 ; i<h_num ; i++ )
//		  if( index[i] )
		  if( index[i]!=0 )
			best[k++] = i;
		best[SUBNET] = k;
		if( simulation ) {
		  if( sim_complex_weights[ac_size-1][sim_index]<w_best )
			sim_complex_weights[ac_size-1][sim_index] = w_best;
		}
	  }
	}

	if( all_one_flag ) {
//	  printf("Result: ");
	  System.out.print("Result: ");
	  for( i=0 ; i<best[SUBNET] ; i++ ) {
//		printf("%d (%d,%d,%d) ", best[i],
//			   homologs[best[i]].ind[0], homologs[best[i]].ind[1],
//			   homologs[best[i]].ind[2]);
//		System.out.print(best[i]+" ("+
//			   homologs[best[i]].ind[0]+","+
//			   homologs[best[i]].ind[1]+","+
//			   homologs[best[i]].ind[2]+") ");
		sb.delete(0,sb.length());
		sb.append(best[i]);
		sb.append(" (");
		sb.append(homologs[best[i]].ind[0]);
		sb.append(",");
		sb.append(homologs[best[i]].ind[1]);
		sb.append(",");
		sb.append(homologs[best[i]].ind[2]);
		sb.append(") ");
		System.out.print(sb);
	  }

//	  printf("\n weight=%g w_best=%g\n", weight, w_best);
//	  System.out.println("\n weight="+weight+" w_best="+w_best);
	  sb.delete(0,sb.length());
	  sb.append("\n weight=");
	  sb.append(weight);
	  sb.append(" w_best=");
	  sb.append(w_best);
	  System.out.println(sb);
	}
	if( w_best>0 ) {
	  if( w_best>max_weight ) {
		max_weight = w_best;
		max_size = best[SUBNET];
	  }
	  if( n_sol==N_PARTIAL ) {
		min = part_weights[0];
		min_i = 0;
		for( i=1 ; i<n_sol ; i++ ) {
		  if( min>part_weights[i] ) {
			min = part_weights[i];
			min_i = i;
		  }
		}
//		if( (w_best>min) || path_iter )
		if( (w_best>min) || path_iter!=0 )
		  l = min_i;
		else
		  continue;
	  } else 
		l = n_sol++;
	  for( i=0 ; i<best[SUBNET] ; i++ )
		part_sols[l][i] = best[i];
	  part_sols[l][SUBNET] = i;
	  part_weights[l] = w_best;
	}
  }
  if( n_sol>0 ) {
//	printf("%d: %s size= %d, best weight= %g. n_sols=%d\n", 
//		   root, simulation?"SIM":"REAL",
//		   max_size, max_weight, n_sol);
//	System.out.println(root+": "+(simulation?"SIM":"REAL")+" size= "+
//		   max_size+", best weight= "+two_six_format.format(max_weight)+". n_sols="+n_sol);
	sb.delete(0,sb.length());
	sb.append(root);
	sb.append(": ");
	sb.append((simulation?"SIM":"REAL"));
	sb.append(" size= ");
	sb.append(max_size);
	sb.append(", best weight= ");
	sb.append(two_six_format.format(max_weight));
	sb.append(". n_sols=");
	sb.append(n_sol);
	System.out.println(sb);
//	fflush(stdout);
	System.out.flush();
  }
  
  return n_sol;
}


//compute weight of set of nodes indicated by index
//could be made more efficient
//double compute_sgw( int index[], int size, int present[3][MAX_GENES] )
double compute_sgw( int index[], int size, int present[][] )
{
  double w;
  int i, j, k, l;

  w = 0;
  for( j=0 ; j<size ; j++ ) 
	w += homologs[index[j]].e_val;
  for( l=0 ; l<KS ; l++ ) {
	for( i=0 ; i<n_genes[l] ; i++ ) {
	  if( present[l][i]>0 ) {
		for( j=i+1 ; j<n_genes[l] ; j++ ) {
		  if( present[l][j]>0 ) 
			w += compute_vp_weight( i, j, l );
		}
	  }
	}
  }
  return w;
}


//computes weight change resulting from adding/removing a vertex
//void w_addition( char index[], int i, double *delta_w,
//				 int present[3][MAX_GENES] )
void w_addition( byte index[], int i, double[] delta_w,
				 int present[][] )
{
  int fl[] = new int[3];
  double w;
  int k, l;

// MDA - don't need to bzero since allocated as zeroes
//  bzero( fl, sizeof( fl ) );

  w = homologs[i].e_val;
  for( l=0 ; l<KS ; l++ ) {
//	if( (index[i] && (present[l][homologs[i].ind[l]]>1)) || 
//		((!index[i]) && (present[l][homologs[i].ind[l]]>=1)) )
	if( (index[i]!=0 && (present[l][homologs[i].ind[l]]>1)) || 
		((index[i]==0) && (present[l][homologs[i].ind[l]]>=1)) )
	  fl[l] = 1;
  }
  for( k=0 ; k<h_num ; k++ ) {
//	if( (k!=i) && index[k] ) {
	if( (k!=i) && (index[k]!=0) ) {
	  for( l=0 ; l<KS ; l++ ) {
//		if( !fl[l] ) {
		if( fl[l]==0 ) {
		  w += (compute_vp_weight(homologs[i].ind[l],homologs[k].ind[l],l)/
				(double)present[l][homologs[k].ind[l]]);
		}
	  }
	}
  }
//  if( index[i] ) {
  if( index[i]!=0 ) {
	w = -w;
  }
//  *delta_w = w;
  delta_w[0] = w;
}
 


//for simulation purpose
void shuffle_graphs( int f_call )
{
  int i, sim;
  int j, k, l;
//  int num = f_call ? (1000*h_num):(100*h_num);
  int num = (f_call!=0) ? (1000*h_num):(100*h_num);
  int r;

  for( i=0 ; i<KS ; i++ )
	shuffle_species_graph( i, f_call );

  //shuffle homology relations
  if( KS==1 )
	return;
  for( sim=0 ; sim<num ; sim++ ) {
//	while( 1 ) {
	while( true ) {
//	  i = (int)(drand48()*(double)h_num);
//	  j = (int)(drand48()*(double)h_num);
//	  r = (int)(drand48()*(double)KS);
	  i = (int)(random.nextDouble()*(double)h_num);
	  j = (int)(random.nextDouble()*(double)h_num);
	  r = (int)(random.nextDouble()*(double)KS);
	  for( l=0 ; l<KS ; l++ )
		if( homologs[i].ind[l]==homologs[j].ind[l] )
		  break;
	  if( l==KS )
		break;
	}
	k = homologs[i].ind[r];
	homologs[i].ind[r] = homologs[j].ind[r];
	homologs[j].ind[r] = k;
  }
  build_G();
}


void build_G()
{
  int i, j;

//  bzero( degG, MAX_VER*sizeof( int ) );
  degG = new int[MAX_VER];
//  for (int ii=0; ii < MAX_VER; ii++) degG[ii]=0;

  for( i=0 ; i<h_num ; i++ ) {
	for( j=i+1 ; j<h_num ; j++ ) {
	  if( is_edge( i, j ) ) {
		if( degG[i]==Gmem[i] ) {
		  Gmem[i] += BLOCK;
//		  G[i] = (int *)realloc( G[i], Gmem[i]*sizeof( int ) );
		  int[] newArray = new int[Gmem[i]];
		  System.arraycopy(G[i],0,newArray,0,G[i].length);
		  G[i] = newArray;
		  
		}
		if( degG[j]==Gmem[j] ) {
		  Gmem[j] += BLOCK;
//		  G[j] = (int *)realloc( G[j], Gmem[j]*sizeof( int ) );
		  int[] newArray = new int[Gmem[j]];
		  System.arraycopy(G[j],0,newArray,0,G[j].length);
		  G[j] = newArray;
		}
		G[i][degG[i]] = j;
		G[j][degG[j]] = i;
		degG[i]++;
		degG[j]++;
	  }
	}
  }
}


void shuffle_species_graph( int l, int f_call )
{
  int i, j, a, b, va, vb, q, sim;
  int m = n_interactions[l];
  int n = n_genes[l];
  single_weight_s tmp;
  int q1, q2;
  int bin1, bin2;
//  int num = f_call ? (1000*m):(100*m);
  int num = (f_call!=0) ? (1000*m):(100*m);
  double w1, w2;
  
  for( sim=0 ; sim<m ; sim++ ) {
//	while( 1 ) {
	while( true ) {
//	  i = (int)(drand48()*(double)n);
//	  j = (int)(drand48()*(double)n);
	  i = (int)(random.nextDouble()*(double)n);
	  j = (int)(random.nextDouble()*(double)n);
	  if( (degS[l][i]<=0) || (degS[l][j]<=0) || (i==j) )
		continue;
//	  a = (int)(drand48()*(double)degS[l][i]);
//	  b = (int)(drand48()*(double)degS[l][j]);
	  a = (int)(random.nextDouble()*(double)degS[l][i]);
	  b = (int)(random.nextDouble()*(double)degS[l][j]);
	  va = s_graph[l][i][a].v;
	  vb = s_graph[l][j][b].v;
	  if( (va==vb) || (va==j) || (vb==i) )
		continue;
	  //check for similar weights
	  bin1 = (int)(s_graph[l][i][a].w/0.2);
	  bin2 = (int)(s_graph[l][j][b].w/0.2);
	  if( bin1==bin2 )
		break;
	}
	// now cross
	for( q1=0 ; q1<degS[l][va] ; q1++ )
	  if( s_graph[l][va][q1].v==i )
		break;
	for( q2=0 ; q2<degS[l][vb] ; q2++ )
	  if( s_graph[l][vb][q2].v==j )
		break;
	if( (q1==degS[l][va]) || (q2==degS[l][vb]) ) {
//	  printf("Error!!!\n");
	  System.out.println("Error!!!");
//	  printf("%d,%d %d,%d l=%d i=%d j=%d va=%d vb=%d a=%d b=%d\n", 
//		 q1, degS[l][va], q2, degS[l][vb], l, i, j, va, vb, a, b);
//	  System.out.println(q1+","+degS[l][va]+" "+q2+","+degS[l][vb]+
//		" l="+l+" i="+i+" j="+j+" va="+va+"vb="+vb+" a="+a+" b="+b);
	  sb.delete(0,sb.length());
	  sb.append(q1);
	  sb.append(",");
	  sb.append(degS[l][va]);
	  sb.append(" ");
	  sb.append(q2);
	  sb.append(",");
	  sb.append(degS[l][vb]);
	  sb.append(" l=");
	  sb.append(l);
	  sb.append(" i=");
	  sb.append(i);
	  sb.append(" j=");
	  sb.append(j);
	  sb.append(" va=");
	  sb.append(va);
	  sb.append("vb=");
	  sb.append(vb);
	  sb.append(" a=");
	  sb.append(a);
	  sb.append(" b=");
	  sb.append(b);
	  System.out.println(sb);
	  System.exit(0);
	}
	w1 = s_graph[l][i][a].w;
	w2 = s_graph[l][j][b].w;
	edegS[l][i] = edegS[l][i]-w1+w2;
	edegS[l][va] = edegS[l][va]-w1+w2;
	edegS[l][j] = edegS[l][j]+w1-w2;
	edegS[l][vb] = edegS[l][vb]+w1-w2;
	tmp = s_graph[l][i][a];
	s_graph[l][i][a] = s_graph[l][j][b];
	s_graph[l][j][b] = tmp;
	tmp = s_graph[l][va][q1];
	s_graph[l][va][q1] = s_graph[l][vb][q2];
	s_graph[l][vb][q2] = tmp;
  }
}


//int balanced( int root, int csol[SUBNET+1] )
int balanced( int root, int csol[] )
{
  int i, j, k, l, e;
  int prots[] = new int[SUBNET];
  int n_prots, cand;
  int bal = 0;

  if( KS<2 )
	return 1;
//  printf("%d balance: ", root);
//  System.out.print(root+" balance: ");
  sb.delete(0,sb.length());
  sb.append(root);
  sb.append(" balance: ");
  System.out.print(sb);

  for( l=0 ; l<KS ; l++ ) {
	n_prots = 0;
	for( i=0 ; i<csol[SUBNET] ; i++ ) {
	  cand = homologs[csol[i]].ind[l];
	  for( j=0 ; j<n_prots ; j++ )
		if( prots[j]==cand )
		  break;
	  if( j==n_prots ) 
		prots[n_prots++] = cand;
	}
	e = 0;
	for( i=0 ; i<n_prots ; i++ )
	  for( j=i+1 ; j<n_prots ; j++ ) {
		for( k=0 ; k<degS[l][prots[i]] ; k++ ) 
		  if( s_graph[l][prots[i]][k].v==prots[j] )
			break;
		if( k<degS[l][prots[i]] )
		  e++;
	  }
	if( e>=(n_prots-1) || ((e>=(n_prots-2)) && (e>=3)) )//a tree
	  bal++;
//	printf("%d: %d (v=%d, e=%d) ", l, 
//	   (e>=(n_prots-1) || ((e>=(n_prots-2)) && (e>=3))), n_prots, e);
//	System.out.print(l + ": " +
//	   (( ((e>=(n_prots-1) || ((e>=(n_prots-2)) && (e>=3))) == false )
//		? 0 : 1)) +
//	   " (v=" + n_prots +
//	   ", e=" + e +") ");
	sb.delete(0,sb.length());
	sb.append(l);
	sb.append(": ");
	sb.append(
	   (( ((e>=(n_prots-1) || ((e>=(n_prots-2)) && (e>=3))) == false )
		? 0 : 1)) );
	sb.append(" (v=");
	sb.append(n_prots);
	sb.append(", e=");
	sb.append(e);
	sb.append(") ");
	System.out.print(sb);
  }
//  printf(" == %d\n",(bal==KS));
//  System.out.println(" == "+(bal==KS ? 1 : 0));
  sb.delete(0,sb.length());
  sb.append(" == ");
  sb.append( (bal==KS ? 1 : 0));
  System.out.println(sb);

  return (bal==KS ? 1 : 0);
}


//loop through vertices and compute paths and complexes
//void bfs_search( char *prefix, char *i_prefix )
void bfs_search( String prefix, String i_prefix )
{
  int i, j, k, l, u=0, v, p, goa;
						//neighbors and 2-neighbors
//  char flag[] = new char[MAX_VER], flag2[] = new char[MAX_VER];
  byte flag[] = new byte[MAX_VER], flag2[] = new byte[MAX_VER];
  double weight;
  File fp = null, fpp = null;
  BufferedWriter writerFp = null, writerFpp = null;
  int part_sols[][] = new int[N_PARTIAL][SUBNET+1];
  double part_weights[] = new double[N_PARTIAL];
  int rank;
//  char present[] = new char[Y_CANNOTS];
  byte present[] = new byte[Y_CANNOTS];
  int spec = 0, nt_sol;
//  char s[100];
  String s;
//MDA - move these statics to class static
// static int sgenes[][] = new int[MAX_VER][200], n_sgenes[] = new int[MAX_VER];
// static int i_call = 0;
  int balance;
  int cv_success[] = new int[3], cv_pred[] = new int[3];
  int combined_sols[][] = new int[2*MAX_VER][SUBNET+1];

  i_call++;
  if( simulation )
//	shuffle_graphs( (i_call==1) );
	shuffle_graphs( (i_call==1) ? 1 : 0 );

//  bzero( present, sizeof( present ) );
//MDA - don't need to bzero since allocated as zeroes
//  present = new byte[Y_CANNOTS];

 try {

  if( !simulation ) {
//	sprintf(s, "%s-network.prop", prefix);
	s = prefix + "-network.prop";
//	fp = fopen(s,"w");
//	fprintf(fp,"network\n");
	fp = new File(s);
	writerFp = new BufferedWriter(new FileWriter(fp));
	writerFp.write("network\n");

//	sprintf(s, "%s-path.prop", prefix);
	s = prefix + "-path.prop";
//	fpp = fopen(s,"w");
//	fprintf(fpp,"path\n");
	fpp = new File(s);
	writerFpp = new BufferedWriter(new FileWriter(fpp));
	writerFpp.write("path\n");
  }

  nc_sols = np_sols = 0;
  for( i=0 ; i<h_num ; i++ ) {
	if( degG[i]<2 )
		continue;
//	bzero( flag, sizeof( flag ) );
//	bzero( flag2, sizeof( flag2 ) );

	flag = new byte[MAX_VER];
	flag2 = new byte[MAX_VER];
/*
	for (int ii=0; ii < MAX_VER; ii++) {
	    flag[ii]=0;
	    flag2[ii]=0;
	}
*/

	flag[i] = flag2[i] = 1;
	for( j=0 ; j<degG[i] ; j++ ) {
	  v = G[i][j];
	  flag[v] = flag2[v] = 1;
	  for( k=0 ; k<degG[v] ; k++ )
		flag2[G[v][k]] = 1;
	}
	k = find_path( flag, part_sols, part_weights, i, i_call );
	if( k>0 ) { 
	  u = np_sols;
	  weight = part_weights[0];
	  for( j=0 ; j<k ; j++ ) {
		for( l=0 ; l<SUBNET+1 ; l++ )
		  p_sols[np_sols][l] = part_sols[j][l];
		p_weights[np_sols] = part_weights[j];
		np_sols++;
		if( part_weights[j]>weight ) {
		  weight = part_weights[j];
		  u = np_sols-1;
		}
	  }
	}
	k = find_heaviest( flag, flag2, part_sols, part_weights, i, 
					   ((k>0)?p_sols[u]:null), i_call );
	if( k>0 ) {
	  for( j=0 ; j<k ; j++ ) {
		for( l=0 ; l<SUBNET+1 ; l++ )
		  c_sols[nc_sols][l] = part_sols[j][l];
		c_weights[nc_sols] = part_weights[j];
		nc_sols++;
	  }
	}
  }

//  bzero( n_sgenes, sizeof( n_sgenes ) );
  n_sgenes = new int[MAX_VER];
//  for (int ii = 0; ii < MAX_VER; ii++) n_sgenes[ii]=0;

  nt_sol = 0;
//  printf("============================\n");
  System.out.println("============================");

  if( !simulation ) {
//	printf("Before filtering, significant complexes coverage:\n");
	System.out.println("Before filtering, significant complexes coverage:");
	content_sig( 0, i_prefix );
	//filter complexes - NOTE: commented out by Taylor 9/22/04
	filter_sols( c_sols, c_weights, nc_sols, -1 );
	filter_sols( p_sols, p_weights, np_sols, random_path_th );
//	printf("ID & Score & Size & Purity & Complex category\\\\\n");
	System.out.println("ID & Score & Size & Purity & Complex category\\\\");
	//record for each node the solutions that involve it
	balance = 0;
	for( i=0 ; i<nc_sols ; i++ )
	  if( (c_sols[i][SUBNET]>0) && (c_weights[i]>0) ) {
		for( j=0 ; j<c_sols[i][SUBNET] ; j++ ) {
		  sgenes[c_sols[i][j]][n_sgenes[c_sols[i][j]]++] = i;
		  if( n_sgenes[c_sols[i][j]]>200 )
			my_exit("sgenes full");
		}
		rank = enrichment( i, c_sols[i], c_weights[i], present, 1 );
		balance += balanced( i, c_sols[i] );
		if( rank>=0 ) {
		  nt_sol++;
		  spec += rank;
		}
	  }
//	printf("Balance = %d\n", balance );
	sb.delete(0,sb.length());
	sb.append("Balance = ");
	sb.append(balance);
	System.out.println(sb);

	for( i=0 ; i<h_num ; i++ )
	  if( n_sgenes[i]>0 ) {
		for( j=0 ; j<KS-1 ; j++ )
//		  fprintf(fp, "%s|", homologs[i].nameG[j]);
//		fprintf(fp, "%s = (%d", homologs[i].nameG[KS-1],sgenes[i][0]);
		  writerFp.write(homologs[i].nameG[j]+"|");
		writerFp.write(homologs[i].nameG[KS-1]+" = ("+sgenes[i][0]);

		if( n_sgenes[i]==1 )
//		  fprintf(fp,")\n");
		  writerFp.write(")\n");
		else {
		  for( j=1 ; j<n_sgenes[i] ; j++ )
//			fprintf(fp,"::%d", sgenes[i][j]);
//		  fprintf(fp,")\n");
			writerFp.write("::"+sgenes[i][j]);
		  writerFp.write(")\n");
		}
	  }
//	fclose(fp);
	writerFp.close();


//	bzero( n_sgenes, sizeof( n_sgenes ) );
	n_sgenes = new int[MAX_VER];
//	for (int ii = 0; ii < MAX_VER; ii++) n_sgenes[ii]=0;

//	printf("\nID & Score & Size & Purity & Path category\\\\\n");
	System.out.println("\nID & Score & Size & Purity & Path category\\\\");
	for( i=0 ; i<np_sols ; i++ )
	  if( (p_sols[i][SUBNET]>0) && (p_weights[i]>0) ) {
		for( j=0 ; j<p_sols[i][SUBNET] ; j++ ) {
		  sgenes[p_sols[i][j]][n_sgenes[p_sols[i][j]]++] = i;
		  if( n_sgenes[p_sols[i][j]]>200 )
			my_exit("sgenes full");
		}
		rank = enrichment( i, p_sols[i], p_weights[i], present, 0 );
	  }
	for( i=0 ; i<h_num ; i++ )
	  if( n_sgenes[i]>0 ) {
		for( j=0 ; j<KS-1 ; j++ )
//		  fprintf(fpp, "%s|", homologs[i].nameG[j]);
//		fprintf(fpp, "%s = (%d", homologs[i].nameG[KS-1],sgenes[i][0]);
		  writerFpp.write(homologs[i].nameG[j]+"|");
		writerFpp.write(homologs[i].nameG[KS-1] + " = ("+sgenes[i][0]);
		if( n_sgenes[i]==1 )
//		  fprintf(fpp,")\n");
		  writerFpp.write(")\n");
		else {
		  for( j=1 ; j<n_sgenes[i] ; j++ )
//			fprintf(fpp,"::%d", sgenes[i][j]);
//		  fprintf(fpp,")\n");
			writerFpp.write("::"+sgenes[i][j]);
		  writerFpp.write(")\n");
		}
	  }
//	fclose(fpp);
	writerFpp.close();

	content_sig( 1, i_prefix );
  }
  return;
 } catch (Exception e) {
  e.printStackTrace();
 }
}


//void content_sig( int verbose, char *prefix )
void content_sig( int verbose, String prefix )
{
  int i, j, k, l, u, v, n_u_ints, n_p_ints, p_flag[] = new int[2], p;
  int c, t;
  int n_flag[] = new int[MAX_VER];
  int present[][] = new int[3][MAX_GENES];
  int count;

//  bzero( present, sizeof( present ) );
// MDA - don't need to bzero since allocated as zeroes

  for( i=0 ; i<nc_sols ; i++ ) {
	if( (c_sols[i][SUBNET]>0) && (c_weights[i]>0) ) {
	  for( j=0 ; j<c_sols[i][SUBNET] ; j++ ) {
		for( l=0 ; l<KS ; l++ )
		  present[l][homologs[c_sols[i][j]].ind[l]] = 1;
	  }
	}
  }
  for( i=0 ; i<np_sols ; i++ ) {
	if( (p_sols[i][SUBNET]>0) && (p_weights[i]>0) ) {
	  for( j=0 ; j<p_sols[i][SUBNET] ; j++ ) {
		for( l=0 ; l<KS ; l++ )
		  present[l][homologs[p_sols[i][j]].ind[l]] = 1;
	  }
	}
  }
  for( l=0 ; l<KS ; l++ ) {
	count = 0;
	for( i=0 ; i<MAX_GENES ; i++ )
//	  if( present[l][i] )
	  if( present[l][i]!=0 )
		count++;
//	printf("species %d: %d distinct proteins in paths and complexes\n",
//		   l, count); 
//	System.out.println("species "+l+": "+count+
//		" distinct proteins in paths and complexes");
	sb.delete(0,sb.length());
	sb.append("species ");
	sb.append(l);
	sb.append(": ");
	sb.append(count);
	sb.append(" distinct proteins in paths and complexes");
	System.out.println(sb);
  }

  for( l=0 ; l<KS ; l++ ) {
	n_u_ints = 0;
	for( k=0 ; k<n_interactions[l] ; k++ ) {
	  u = interactions[l][k].i1;
	  v = interactions[l][k].i2;
	  for( i=0 ; i<nc_sols ; i++ )
		if( (c_sols[i][SUBNET]>0) && (c_weights[i]>0) ) {
		  p_flag[0] = p_flag[1] = 0;
		  for( j=0 ; j<c_sols[i][SUBNET] ; j++ ) {
			if( homologs[c_sols[i][j]].ind[l]==u )
			  p_flag[0] = 1;
			else if( homologs[c_sols[i][j]].ind[l]==v )
			  p_flag[1] = 1;
		  }
//		  if( p_flag[0] && p_flag[1] ) {
		  if( (p_flag[0]!=0) && (p_flag[1]!=0) ) {
			n_u_ints++;
			break;
		  }
		}
	}
//	printf("species %d: %d interactions covered by significant complexes\n",
//		   l, n_u_ints);
//	System.out.println("species "+l+": "+n_u_ints+
//		   " interactions covered by significant complexes");
	sb.delete(0,sb.length());
	sb.append("species ");
	sb.append(l);
	sb.append(": ");
	sb.append(n_u_ints);
	sb.append(" interactions covered by significant complexes");
	System.out.println(sb);
  }

  n_u_ints = n_p_ints = u = t = 0;
  for( i=0 ; i<h_num ; i++ )
	for( j=i+1 ; j<h_num ; j++ ) 
	  if( all_one( i, j ) ) {
		v = 0;
		for( k=0 ; k<nc_sols ; k++ ) {
		  if( (c_sols[k][SUBNET]>0) && (c_weights[k]>0) ) {
			c = 0;
			for( l=0 ; l<c_sols[k][SUBNET] ; l++ )
			  for( p=0 ; p<KS ; p++ ) {
				if( homologs[c_sols[k][l]].ind[p]==homologs[i].ind[p] )
				  c |= (1<<p);
				if( homologs[c_sols[k][l]].ind[p]==homologs[j].ind[p] )
				  c |= (1<<(p+KS));
			  }
			if( c==((1<<(2*KS))-1) ) {
			  n_u_ints++;
			  v++;
			  break; 
			}
		  }
		}
		for( k=0 ; k<np_sols ; k++ ) {
		  if( (p_sols[k][SUBNET]>0) && (p_weights[k]>0) ) {
			c = 0;
			for( l=0 ; l<PATH_LEN-1 ; l++ )
			  if( ((p_sols[k][l]==i) && (p_sols[k][l+1]==j)) ||
				  ((p_sols[k][l]==j) && (p_sols[k][l+1]==i)) )
				c++;
//			if( c ) {
			if( c!=0 ) {
			  n_p_ints++;
			  v++;
			  break; 
			}
		  }
		}
		if( v==2 )
		  t++;
		u++;
	  }
//  printf("%d all-one edges out of %d covered by sig. complexes\n", 
//		 n_u_ints, u);
//  printf("%d all-one edges out of %d covered by sig. paths (overlap=%d)\n", 
//		 n_p_ints, u, t);
  sb.delete(0,sb.length());
  sb.append(n_u_ints);
  sb.append(" all-one edges out of ");
  sb.append(u);
  sb.append(" covered by sig. complexes");
  System.out.println(sb);

  sb.delete(0,sb.length());
  sb.append(n_p_ints);
  sb.append(" all-one edges out of ");
  sb.append(u);
  sb.append(" covered by sig. paths (overlap=");
  sb.append(t);
  sb.append(")");
  System.out.println(sb);

}


//int predict( int u, int v, char dist2[][2][NAME_LEN], int n )
int predict( int u, int v, String dist2[][], int n )
{
  return 0;
}


//int is_dist2( int i1, int i2, char dist2[][2][NAME_LEN], int n )
int is_dist2( int i1, int i2, String dist2[][], int n )
{
  return 0;
}


//char is_edge( int i, int j )
boolean is_edge( int i, int j )
{
  int l, u, v, w, k, r;
  int flag1 = 0, flag2 = 0, flago = 0, change_flag;
  double prob;//if wishing to weight probs in the edge decisions

  if( i==j )
//	return 0;
	return false;
  for( l=0 ; l<KS ; l++ ) {
	u = homologs[i].ind[l];
	v = homologs[j].ind[l];
	if( (degS[l][u]<1) || (degS[l][v]<1) )
//	  return 0;
	  return false;
	if( u==v ) {
	  flago++;
	  continue;
	}
	if( degS[l][u]>degS[l][v] ) {
	  w = u;
	  u = v;
	  v = w;
	}
	change_flag = 0;
	for( k=0 ; k<degS[l][u] ; k++ ) {
	  if( s_graph[l][u][k].v==v ) {
		if( s_graph[l][u][k].w>=PROB_TH ) {
		  flag1++;
		  change_flag = 1;
		}
		break;
	  }
	}
//	if( (KS>1) && (!change_flag) ) { 
	if( (KS>1) && (change_flag==0) ) { 
	  prob = 1.;
	  for( k=0 ; k<degS[l][u] ; k++ ) {
		w = s_graph[l][u][k].v;
		for( r=0 ; r<degS[l][w] ; r++ ) {
		  if( s_graph[l][w][r].v==v ) {
			prob *= (1.-s_graph[l][w][r].w*s_graph[l][u][k].w);//OR prob.
			break;
		  }
		}
		if( (1.-prob)>=PROB_TH ) {
		  flag2++;
		  break;
		}
	  }
	}
  }
  if( KS<3 )
	return (((flag1>=1) && ((flag1+flag2+flago)>=KS)) || 
//			((KS==2) && all2_flag && (flag2==2)));
			((KS==2) && all2_flag!=0 && (flag2==2)));
  else
	return (((flag1>=1) && ((flag1+flag2+flago)>=KS)) ||
			((flag1>=2) && ((flag2+flago)>=(KS-3))) || 
			(((flag2+flago)>=KS) && (flago<(KS-1))));
}


//check for interaction within at least one species
//char is_interaction( int i, int j )
boolean is_interaction( int i, int j )
{
  int l, u, v, w, k;
//  char flag = 0;
  boolean flag = false;

  if( i==j )
//	return 0;
	return false;
  for( l=0 ; l<KS ; l++ ) {
	u = homologs[i].ind[l];
	v = homologs[j].ind[l];
	if( (degS[l][u]<1) || (degS[l][v]<1) )
	  continue;
	if( u==v ) 
	  continue;
	if( degS[l][u]>degS[l][v] ) {
	  w = u;
	  u = v;
	  v = w;
	}
	for( k=0 ; k<degS[l][u] ; k++ ) {
	  w = s_graph[l][u][k].v;
	  if( w==v ) {
//		flag = 1;
		flag = true;
		break;
	  }
	}
  }
  return flag;
}


//currently assumes that paths are filtered after complexes
//void filter_sols( int sols[MAX_VER*N_PARTIAL][SUBNET+1], 
//		  double weights[MAX_VER*N_PARTIAL], int n_sols, double th )
void filter_sols( int sols[][], 
		  double weights[], int n_sols, double th )
{
  int i, j, k, l, p;
  int size;
  int num = 0;
  double pval;
  int index[] = new int[MAX_VER*N_PARTIAL];
//  char flag[] = new char[MAX_GENES];
  byte flag[] = new byte[MAX_GENES];
  int o_o[] = new int[3], overlap, oc;
  double rth = th;

//  printf("before filtering %d sols\n", n_sols);
//  System.out.println("before filtering "+n_sols+" sols");
  sb.delete(0,sb.length());
  sb.append("before filtering ");
  sb.append(n_sols);
  sb.append(" sols");
  System.out.println(sb);
//  fflush(stdout);
  System.out.flush();

//  bzero( flag, sizeof( flag ) );
// MDA - dont' need to bzero since allocated as zeroes

  for( i=0 ; i<n_sols ; i++ ) {
	if( th<0 )
	  rth = random_net_th[sols[i][SUBNET]];
	if( rth<0 )
	  rth = 0;
	if( (sols[i][SUBNET]<4) || (weights[i]<rth) ) {
	  sols[i][SUBNET] = 0;
	  continue;
	}
	//check for no intersecting complex
	if( th>0 ) {
	  for( j=0 ; j<nc_sols ; j++ ) {
		overlap = 0;
		for( k=0 ; k<c_sols[j][SUBNET] ; k++ )
		  for( l=0 ; l<sols[i][SUBNET] ; l++ )
			if( sols[i][l]==c_sols[j][k] )
			  overlap++;
		if( overlap>3 ) {
		  sols[i][SUBNET] = 0;
		  break;
		}
		oc = 1;
		for( p=0 ; p<KS ; p++ ) {
		  size = overlap = 0;
//		  bzero( flag, sizeof( flag ) );
  		  flag = new byte[MAX_GENES];
//  		  for (int ii=0; ii<MAX_GENES; ii++) flag[ii]=0;

		  for( l=0 ; l<sols[i][SUBNET] ; l++ ) {
			flag[homologs[sols[i][l]].ind[p]] = 1;
			size++;
		  }
		  overlap = 0;
		  for( k=0 ; k<c_sols[j][SUBNET] ; k++ )
//			if( flag[homologs[c_sols[j][k]].ind[p]] )
			if( flag[homologs[c_sols[j][k]].ind[p]]!=0 )
			  overlap++;
		  if( (size-overlap)>=1 )
			oc = 0;
		}
//		if( oc ) {
		if( oc!=0 ) {
		  sols[i][SUBNET] = 0;
		  break;
		}
	  }
	  if( j<nc_sols ) 
		continue;
	}
	if( weights[i]>0 )
	  num++;
  }

//  printf("found %d significant %s\n", num, (th<0)?"networks":"paths");
// System.out.println("found "+num+" significant "+((th<0)?"networks":"paths"));
  sb.delete(0,sb.length());
  sb.append("found ");
  sb.append(num);
  sb.append(" significant ");
  sb.append(((th<0)?"networks":"paths"));
  System.out.println(sb);

  if( num<=1 ) {
	return;
  }

  for( i=0 ; i<n_sols-1 ; i++ ) {
	if( sols[i][SUBNET]<=0 )
	  continue;
	for( j=i+1 ; j<n_sols ; j++ ) {
	  if( sols[j][SUBNET]<=0 )
		continue;
	  for( l=0 ; l<KS ; l++ ) {
		o_o[l] = overlap = oc = 0;

//		bzero( flag, sizeof( flag ) );
  		flag = new byte[MAX_GENES];
//  		for (int ii=0; ii<MAX_GENES; ii++) flag[ii]=0;

		size = 0;
		for( k=0 ; k<sols[i][SUBNET] ; k++ )
//		  if( !flag[homologs[sols[i][k]].ind[l]] ) {
		  if( flag[homologs[sols[i][k]].ind[l]]==0 ) {
			flag[homologs[sols[i][k]].ind[l]] = 1;
			size++;
		  }
		for( k=0 ; k<sols[j][SUBNET] ; k++ )
//		  if( !flag[homologs[sols[j][k]].ind[l]] ) {
		  if( flag[homologs[sols[j][k]].ind[l]]==0 ) {
			flag[homologs[sols[j][k]].ind[l]] = 1;
			oc++;
		  } else
			overlap++;
		size += oc;
		if( ((float)overlap/(float)size)>MAX_OVERLAP )
		  o_o[l] = 1;
	  }
	  size = sols[j][SUBNET];
	  oc = overlap = 0;
	  for( k=0 ; k<sols[i][SUBNET] ; k++ ) {
		for( l=0 ; l<sols[j][SUBNET] ; l++ ) {
		  if( sols[i][k]==sols[j][l] ) {
			overlap++;
			break;
		  }
		}
		if( l==sols[j][SUBNET] )
		  oc++;
	  }
	  size += oc;
	  if( ((float)overlap/(float)size)>MAX_OVERLAP ) 
		oc = 1;
	  else 
		oc = 0;

	  overlap = 1;
	  //overlap>th in *all* subnets to remove  
	  for( l=0 ; l<KS ; l++ )
		overlap &= o_o[l];

//	  if( oc || overlap ) {
	  if( (oc!=0) || (overlap!=0) ) {
		num--;
		if( weights[i]<weights[j] ) {
		  sols[i][SUBNET] = 0;
		  break;
		} else 
		  sols[j][SUBNET] = 0;
	  }
	}
  }
//  printf("found %d filtered %s\n", num, (th<0)?"networks":"paths");
//  fflush(stdout);
//  System.out.println("found "+num+" filtered "+((th<0)?"networks":"paths"));
  sb.delete(0,sb.length());
  sb.append("found ");
  sb.append(num);
  sb.append(" filtered ");
  sb.append(((th<0)?"networks":"paths"));
  System.out.println(sb);
  System.out.flush();
}


//compute 0.05 significance thresholds for complexes and paths
void compute_thresholds()
{
  int i;
  double th;
  
//  printf("Computing Thresholds\n");
  System.out.println("Computing Thresholds");
//  fflush(stdout);
  System.out.flush();
  random_path_th = my_sort( sim_path_weights, SIM_NO*MAX_VER );
//  printf("PATH_TH: %g\n", random_path_th); 
//  System.out.println("PATH_TH: "+random_path_th); 
  sb.delete(0,sb.length());
  sb.append("PATH_TH: ");
  sb.append(random_path_th); 
  System.out.println(sb);

  random_net_th[0] = random_net_th[1] = random_net_th[2] = 
	random_net_th[3] = 100;
  for( i=4 ; i<=SUBNET ; i++ ) {
	random_net_th[i] = my_sort( sim_complex_weights[i-1], SIM_NO*MAX_VER );
  }
//  printf("NET_TH: ");
  System.out.print("NET_TH: ");
  sb.delete(0,sb.length());
  for( i=0 ; i<=SUBNET ; i++ ) {
//	printf("%g ", random_net_th[i]); 
//	System.out.print(random_net_th[i]+" "); 
	sb.append(random_net_th[i]);
	sb.append(" "); 
  }

//  printf("\n");
  System.out.println(sb);
//  fflush(stdout);
  System.out.flush();
}


double my_sort( double[] a, int n )
{
  int i;
  int count = 0;

  for( i=0 ; i<n ; i++ ) 
	if( a[i]>0 )
	  count++;
//  qsort( a, n, sizeof( double ), dcompar );
  Arrays.sort(a,0,n-1);
  if( count<20 ) {
//	printf("small count = %d\n", count);
//	System.out.println("small count = "+count);
	sb.delete(0,sb.length());
	sb.append("small count = ");
	sb.append(count);
	System.out.println(sb);
  }
  if( count<1 )
	return random_path_th;
  else
	return a[(int)((double)count*0.05)];
}


//int dcompar( const void *a, const void *b )
//{
//  double *u,*v;
//
//  u = (double *)a;
//  v = (double *)b;
//
//  if( (*u)<(*v) )
//	return 1;
//  else if( (*v)<(*u) )
//	return -1;
//  else
//	return 0;
//}


//compute edges weight between u and v in species l
double compute_vp_weight( int u, int v, int l )
{
  int w;
  int i;
  double t;

  if( u==v )
	return 0;
  if( degS[l][u]>degS[l][v] ) {
	w = u;
	u = v;
	v = w;
  }
  for( i=0 ; i<degS[l][u] ; i++ )
    if( s_graph[l][u][i].v==v ) {
      t = calc_single_weight( (int)(edegS[l][u]+0.5), 
			      (int)(edegS[l][v]+0.5), 
			      s_graph[l][u][i].w, l );
      return t;
    }
  return calc_single_weight( (int)(edegS[l][u]+0.5), 
			     (int)(edegS[l][v]+0.5), 
			     p_te[l], l );
}


//void build_graph( char *prefix )
void build_graph( String prefix ) {
  int i, j=0, k, l, u, v, t;
  double prob, w=0;
  int edges = 0;
//  FILE *fp_o, *fp_m;
  File fp_o = null, f_o = null;
  BufferedWriter writerFp_o = null;
//  char name_o[100];
  String name_o;
  //assumes MAX_VER>MAX_GENES
  int target[] = new int[MAX_VER];
//  char c;
  byte c;
  double min_w, max_w;
//  int graphFlag = (graphNeeded && (!simulation));  
  boolean graphFlag = ((graphNeeded!=0) && (!simulation));  
//  char p_present[][] = new char[3][MAX_GENES];
  byte p_present[][] = new byte[3][MAX_GENES];

 try {
  if( graphFlag ) {
//	sprintf(name_o, "%s-graph.sif", prefix); 
	name_o = prefix + "-graph.sif";
//	if( (fp_o = fopen( name_o, "w" )) == null)
//	  my_exit("Can't open file");
	f_o = new File(name_o);
	if (!f_o.exists()) {
	  if (!f_o.createNewFile()) {
	    my_exit("Can't create file: "+name_o);
	  }
	} else if (!f_o.canWrite()) {
	    my_exit("Can't open file: "+name_o);
	}
	writerFp_o = new BufferedWriter(new FileWriter(f_o));
  }

  for( l=0 ; l<KS ; l++ ) 
	e_e[l] = s_e[l] = 0;
//  bzero( degG, MAX_VER*sizeof( int ) );
//  bzero( edegS, sizeof( edegS ) );
  degG = new int[MAX_VER];
  edegS = new double[3][MAX_GENES];
/*
    int ii, jj;
    for (ii=0; ii<MAX_VER; ii++) degG[ii]=0;
    for (ii=0; ii<3; ii++)
    	for (jj=0; jj<MAX_GENES; jj++) edegS[ii][jj]=0;
*/

  edges = 0;
  for( l=0 ; l<KS ; l++ ) {
	for( i=0 ; i<MAX_GENES ; i++ ) {
	  if( degS[l][i]>0 )
		edegS[l][i] = (double)(n_genes[l]-degS[l][i]-1)*p_te[l];
	  if( (degS[l][i]>0) && (s_graph[l][i]==null) ) 
//	    s_graph[l][i] = (single_weight_s *)malloc( degS[l][i]*
//					       sizeof( single_weight_s ) );
//
	    s_graph[l][i] = new single_weight_s[degS[l][i]];
	    for (int kk = 0; kk < degS[l][i]; kk++) {
		s_graph[l][i][kk] = new single_weight_s();
	    }
	}
//	bzero( target, sizeof( target ) );
	target = new int[MAX_VER];
//	for (ii=0; ii<MAX_VER; ii++) target[ii]=0;

	e_total_weight[l] = 0;
	for( i=0 ; i<n_interactions[l] ; i++ ) {
	  u = interactions[l][i].i1;
	  v = interactions[l][i].i2;
	  prob = interactions[l][i].prob;
	  edegS[l][u] += prob;
	  edegS[l][v] += prob;
	  s_graph[l][u][target[u]].v = v; 
	  s_graph[l][u][target[u]++].w = prob; 
	  s_graph[l][v][target[v]].v = u; 
	  s_graph[l][v][target[v]++].w = prob;
	}
	for( i=0 ; i<MAX_GENES ; i++ ) {
	  if( degS[l][i]!=target[i] ) {
//		printf("Degree error %d: %d,%d\n", i, degS[l][i], target[i]);
//		System.out.println("Degree error "+i+": "+degS[l][i]+","+
//			target[i]);
		sb.delete(0,sb.length());
		sb.append( "Degree error ");
		sb.append(i);
		sb.append(": ");
		sb.append(degS[l][i]);
		sb.append(",");
		sb.append(target[i]);
		System.out.println(sb);
	  }
	  if( degS[l][i]>0 )
		e_total_weight[l] += edegS[l][i];
	}
	e_total_weight[l] /= 2.;
//	printf("%d: Expected number of interactions %d\n", l, 
//		   (int)e_total_weight[l]);
//	System.out.println(l+": Expected number of interactions "+
//		   (int)e_total_weight[l]);
	sb.delete(0,sb.length());
	sb.append(l);
	sb.append(": Expected number of interactions ");
	sb.append((int)e_total_weight[l]);
	System.out.println(sb);

	//gather statistics on graph
	if( simulation && ((l==0) || (KS>1)) ) {
	  t = 0;
	  max_w = -1000;
	  min_w = 1000;
	  for( i=0 ; i<n_genes[l] ; i++ ) {
		for( j=i+1 ; j<n_genes[l] ; j++ ) {
		  w = compute_vp_weight( i, j, l );
		  if( w>max_w ) 
			max_w = w;
		  if( w<min_w )
			min_w = w;
		  e_e[l] += w;
		  s_e[l] += w*w; 
		  t++;
		}
	  }
	  e_e[l] /= (double)t;
	  s_e[l] = (double)t/(double)(t-1)*(s_e[l]/(double)t-e_e[l]*e_e[l]);
//	  printf("%d: e=%g, s=%g, max=%g, min=%g\n", l, e_e[l], sqrt(s_e[l]),
//			 max_w, min_w);
//	  System.out.println(l+": e="+e_e[l]+", s="+Math.sqrt(s_e[l])+", max="+
//			max_w+", min="+min_w);
//	  System.out.println(l+": e="+two_six_format.format(e_e[l])+
//		", s="+two_six_format.format(Math.sqrt(s_e[l]))+
//		", max="+ two_six_format.format(max_w)+", min="+
//		two_six_format.format(min_w));
	  sb.delete(0,sb.length());
	  sb.append(l);
	  sb.append(": e=");
	  sb.append(two_six_format.format(e_e[l]));
	  sb.append(", s=");
	  sb.append(two_six_format.format(Math.sqrt(s_e[l])));
	  sb.append(", max=");
	  sb.append(two_six_format.format(max_w));
	  sb.append(", min=");
	  sb.append(two_six_format.format(min_w));
	  System.out.println(sb);
	}
  }

  for( i=0 ; i<h_num ; i++ ) {
	if( G[i]==null) {
//	  G[i] = (int *)malloc( BLOCK*sizeof( int ) );
	  G[i] = new int[BLOCK];
	  Gmem[i] = BLOCK;
	}
  }
  for( i=0 ; i<h_num ; i++ ) {
	for( j=i+1 ; j<h_num ; j++ ) {
	  if( is_edge( i, j ) ) {
		if( degG[i]==Gmem[i] ) {
		  Gmem[i] += BLOCK;
//		  G[i] = (int *)realloc( G[i], Gmem[i]*sizeof( int ) );
		  int[] newArray = new int[Gmem[i]];
		  System.arraycopy(G[i],0,newArray,0,G[i].length);
		  G[i] = newArray;
		}
		if( degG[j]==Gmem[j] ) {
		  Gmem[j] += BLOCK;
//		  G[j] = (int *)realloc( G[j], Gmem[j]*sizeof( int ) );
		  int[] newArray = new int[Gmem[j]];
		  System.arraycopy(G[j],0,newArray,0,G[j].length);
		  G[j] = newArray;
		}
		G[i][degG[i]] = j;
		G[j][degG[j]] = i;
		degG[i]++;
		degG[j]++;
		edges++;
	  } 
//	  if( graphFlag && is_interaction( i, j ) ) {
	  if( graphFlag && is_interaction( i, j ) ) {
		for( l=0 ; l<KS-1 ; l++ )
//		  fprintf(fp_o, "%s|", homologs[i].nameG[l]);
//		fprintf(fp_o, "%s ", homologs[i].nameG[KS-1]);
		  writerFp_o.write(homologs[i].nameG[l]+"|");
		writerFp_o.write(homologs[i].nameG[KS-1]+" ");
		if( KS>1 ) {
		  for( l=0 ; l<KS ; l++ ) {
//			fprintf(fp_o, "%d", pair_distance( i, j, l, &w ));
			double wA[] = new double[1];
			wA[0] = w;
			writerFp_o.write(""+pair_distance( i, j, l, wA ));
			w = wA[0];
		  }
		} else
//		  fprintf(fp_o,"%lf", compute_vp_weight( homologs[i].ind[0],
//					homologs[j].ind[0], 0 ));
		  writerFp_o.write(""+compute_vp_weight( homologs[i].ind[0],
					homologs[j].ind[0], 0 ));
//		fprintf(fp_o, " %s", homologs[j].nameG[0]);
		writerFp_o.write(" "+homologs[j].nameG[0]);
		for( l=1 ; l<KS ; l++ )
//		  fprintf(fp_o, "|%s", homologs[j].nameG[l]);
//		fprintf(fp_o,"\n");
		  writerFp_o.write("|"+homologs[j].nameG[l]);
		writerFp_o.write("\n");
	  }
	}
  }
  if( graphFlag )
//	fclose(fp_o);
	writerFp_o.close();

  real_h_num = 0;
//  bzero( p_present, sizeof( p_present ) );
  p_present = new byte[3][MAX_GENES];
/*
  for (ii=0; ii<3; ii++)
    for (jj=0; jj<MAX_GENES; jj++) p_present[ii][jj]=0;
*/

  for( i=0 ; i<h_num ; i++ ) {
    homologs[i].complexY[0] = -1;
	homologs[i].ncY = 0;
    if( degG[i]>0 ) {
	  real_h_num++;
	  for( k=0 ; k<KS ; k++ )
		p_present[k][homologs[i].ind[k]] = 1;
	}
  }
//  printf("Finished building graph on %d edges, %d vertices\n", 
//		 edges, real_h_num);
// System.out.println("Finished building graph on "+edges+" edges, "+real_h_num+
//		" vertices");
  sb.delete(0,sb.length());
  sb.append("Finished building graph on ");
  sb.append(edges);
  sb.append(" edges, ");
  sb.append(real_h_num);
  sb.append(" vertices");
  System.out.println(sb);

  for( k=0 ; k<KS ; k++ ) {
	v = 0;
	for( i=0 ; i<MAX_GENES ; i++ )
//	  if( p_present[k][i] )
	  if( p_present[k][i]!=0 )
		v++;
//	printf("%d: #distinct proteins in graph = %d\n", k, v);  
//	System.out.println(k+": #distinct proteins in graph = "+v);
	sb.delete(0,sb.length());
	sb.append(k);
	sb.append(": #distinct proteins in graph = ");
	sb.append(v);
	System.out.println(sb);
  }
//  fflush(stdout);
  System.out.flush();

  if( simulation )
	compute_content_stats();

  if( KS==1 )
	real_h_num = j;

 } catch (Exception e) {
    e.printStackTrace();
 }
}


void compute_content_stats()
{
  int i, j, k, l;
  int edges, total;
  int u, v;
  int flag;

  for( l=0 ; l<KS ; l++ ) {
	edges = total = 0;
	for( i=0 ; i<n_interactions[l] ; i++ ) {
	  u = interactions[l][i].i1;
	  v = interactions[l][i].i2;
	  flag = 0;
	  for( j=0 ; j<h_num ; j++ ) {
		if( homologs[j].ind[l]==u )
		  flag |= 1;
		if( homologs[j].ind[l]==v )
		  flag |= 2;
	  }
	  if( flag==3 ) {
		total++;
		for( j=0 ; j<h_num ; j++ ) {
		  if( homologs[j].ind[l]!=u )
			continue;
		  for( k=0 ; k<degG[j] ; k++ )
			if( homologs[G[j][k]].ind[l]==v ) {
			  edges++;
			  break;
			}
		  if( k<degG[j] )
			break;
		}
	  }
	}
//	printf("species %d: %d edges in orthology graph out of %d\n", 
//		   l, edges, total);
//	System.out.println("species "+l+": "+edges+
//		" edges in orthology graph out of "+total);
	sb.delete(0,sb.length());
	sb.append("species ");
	sb.append(l);
	sb.append(": ");
	sb.append(edges);
	sb.append(" edges in orthology graph out of ");
	sb.append(total);
	System.out.println(sb);
  }
}


//int all_one( int i, int j )
boolean all_one( int i, int j )
{
  int edge = 0;
  int k, l, u, v;

  for( l=0 ; l<KS ; l++ ) {
	u = homologs[i].ind[l];
	v = homologs[j].ind[l];
	if( u==v )
	  return false;
	for( k=0 ; k<degS[l][u] ; k++ )
	  if( s_graph[l][u][k].v==v ) {
		edge++;
		break;
	  }
  }
  return (edge==KS);
}


//check for distinct proteins in two nodes
//int distinct_nodes( int i, int j )
//{
//  int l;
//  for( l=0 ; l<KS ; l++ )
//        if( homologs[i].ind[l]==homologs[j].ind[l] )
//          return 0;
//  return 1;
//}

boolean distinct_nodes( int i, int j )
{
  int l;
  for( l=0 ; l<KS ; l++ )
        if( homologs[i].ind[l]==homologs[j].ind[l] )
          return false;
  return true;
}


//compute weight of edge in orthology graph
double edge_weight(int i, int j)
{
  int l;
  double part = 0;

  for( l=0 ; l<KS ; l++ )
        part += compute_vp_weight( homologs[i].ind[l], homologs[j].ind[l], l );
  return part;
}


int pair_distance( int a, int b, int l, double[] p )
{
  int i, j, w;
  int u = homologs[a].ind[l];
  int v = homologs[b].ind[l];
  int dist = 3;
  double prob = 1.;

  p[0] = 0;
  if( u==v )
	return 0;
  for( i=0 ; i<degS[l][u] ; i++ )
	if( s_graph[l][u][i].v==v ) {
	  p[0] = s_graph[l][u][i].w;
	  return 1;
	}
  for( i=0 ; i<degS[l][u] ; i++ ) {
	w = s_graph[l][u][i].v;
	for( j=0 ; j<degS[l][w] ; j++ )
	  if( s_graph[l][w][j].v==v ) {
		prob *= (1.-s_graph[l][w][j].w*s_graph[l][u][i].w);
		dist = 2;
	  }
  }
  p[0] = 1.-prob;
  return dist;
}


//int compute_union( char present[MAX_GENES], int size, int c )
int compute_union( byte present[], int size, int c )
{
  int i, j, cup;
  int flag[] = new int[MAX_GENES];

  for( i=0 ; i<n_genes[0] ; i++ )
	flag[i] = present[i];

  cup = size;
  for( i=0 ; i<h_num ; i++ ) {
//	if( !flag[homologs[i].ind[0]] ) {
	if( flag[homologs[i].ind[0]] == 0 ) {
	  for( j=0 ; j<homologs[i].ncY ; j++ )
		if( homologs[i].complexY[j]==c ) {
		  flag[homologs[i].ind[0]] = 1;
		  cup++;
		}
	}
  }
  return cup;
}
	  

//int enrichment( int ind, int *csol, double weight, char *present, 
//				int phen_flag )
int enrichment( int ind, int[] csol, double weight, byte[] present, 
				int phen_flag )
{
  int i, k;
  float cup, max;
  int max_i;
  int name_i;

  if( csol[SUBNET]<=0 )
    return -1;
//  printf("%d & ", ind);
//  printf("%6.2lf & %d & ", weight, csol[SUBNET] );
//  fflush(stdout);
//  System.out.print(ind+" & ");
//  System.out.print(six_two_format.format(weight)+" & "+ csol[SUBNET] +" & ");
//  System.out.flush();
  sb.delete(0,sb.length());
  sb.append(ind);
  sb.append(" & ");
  sb.append(six_two_format.format(weight));
  sb.append(" & ");
  sb.append( csol[SUBNET]);
  sb.append(" & ");

//  printf("0 & -\\\\\n");
//  fflush(stdout);
//  System.out.println("0 & -\\\\");
  sb.append("0 & -\\\\");
  System.out.println(sb);
  System.out.flush();
  return -1;
}


double calc_single_weight( int d1, int d2, double f, int l )
{
  double p1, p2;
  double prob;
  double pt;

  //f compare to simulations.
  /*based on choosing edges randomly according to degrees:
	after choosing d1 copies of v, number of possibilities to choose d2
    copies of u is:
	1. if no edge: (m-d1)*...*(m-d1-d2+1)*2^d2
	2. if an edge: d2*d1*(m-d1)*...*(m-d1-d2+2)*2^(d2-1), 
	where the d2 factor is because we can choose the 'ball' that 
    creates the edge at each of the d2 trials.
	So prob of edge = 1/(1+(1)/(2)) */
  prob = 1./(1.+(2.*(double)((int)(e_total_weight[l]+0.5)-d1-d2+1)/
				 (double)(d1*d2)));
  pt = p_true[l];
  p1 = alpha*f*(1.-pt)+(1.-alpha)*(1.-f)*pt;
  p2 = prob*f*(1.-pt)+(1.-prob)*(1.-f)*pt;
  
  return (Math.log(p1)-Math.log(p2));
}


//int find_name( char *name, int species ) 
int find_name( String name, int species ) 
{
  int i;

  for( i=0 ; i<n_genes[species] ; i++ )
//	if( !strcmp( geneNames[species][i], name ) )
	if( geneNames[species][i].equals(name) )
	  return i;
  return -1;
}


//int insert_name( char *name, int species ) 
int insert_name( String name, int species ) 
{
  int i;

  for( i=0 ; i<n_genes[species] ; i++ )
//	if( !strcmp( geneNames[species][i], name ) )
	if( geneNames[species][i].equals(name) ) 
	  return i;
//  strcpy( geneNames[species][n_genes[species]], name );
  geneNames[species][n_genes[species]] = new String(name);
  n_genes[species]++;
  return (n_genes[species]-1);
}


//void read_homologs( char *prefix )
void read_homologs( String prefix ) {
//  FILE *fp;
  RandomAccessFile fp = null;
//  char line[100], name[3][NAME_LEN];
  String fName, line, name[] = new String[3];
  int i, j, k;
  double e_val = 0, sum = 0.;
  String evalS = null;

//  sprintf( line, "%s_blast", prefix );
  fName = prefix + "_blast";
  //count number of homologous pairs
  h_num = 0;
  int lineCount=0;

 try { 
   String[] tokens;

//  if( (fp = fopen( line, "r" )) == null)
//	my_exit("Can't open file");
  File f = new File(fName);
  if (!f.canRead())
	my_exit("Can't open file: "+fName);

  fp = new RandomAccessFile(fName, "r");

//  while( fgets( line, 100, fp ) != null) {
  while( (line = fp.readLine()) != null) {
    lineCount++;
    // handle blank lines in file
    line.trim();
    if (line.length() > 0) {
	if( KS==2 ) {
//	  sscanf( line, " %s %s %lf ", name[0], name[1], &e_val );
	  tokens = line.split("\\s");
	  name[0] = tokens[0];
	  name[1] = tokens[1];
	  evalS = tokens[2];
	  // Java requires digit before exponenet
	  if (evalS.charAt(0)=='e'||evalS.charAt(0)=='E') {
	    System.err.println("format error in file " + fName +
		"at or near line "+ lineCount +
		"; " + evalS + " requires mantissa before exponent");
	    System.exit(-1);
	    //evalS="1.0"+evalS;
	  }
	  e_val = Double.parseDouble(evalS);
	} else if( KS==3 ) {
//	  sscanf( line, " %s %s %s %lf ", name[0], name[1], name[2], &e_val );
	  tokens = line.split("\\s");
	  name[0] = tokens[0];
	  name[1] = tokens[1];
	  name[2] = tokens[2];
	  evalS = tokens[3];
	  // Java requires digit before exponenet
	  if (evalS.charAt(0)=='e'||evalS.charAt(0)=='E') {
	    System.err.println("format error in file " + fName +
		"at or near line "+ lineCount +
		"; " + evalS + " requires mantissa before exponent");
	    System.exit(-1);
	    //evalS="1.0"+evalS;
	  }
	  e_val = Double.parseDouble(evalS);
	} else if( KS==1 ) {
//	  sscanf( line, " %s ", name[0] );
	  tokens = line.split("\\s");
	  name[0] = tokens[0];
	}
	if( (KS==1) || (e_val<=blTh) ) {
	  h_num++;
	}
    }
  }
//  rewind(fp);
  fp.seek(0);


//  if( homologs==null )
//	homologs = (hom_s *)malloc( h_num*sizeof( hom_s ) );

//MDA - initialize with empty constructor - do with values elsewhere?
  if( homologs==null ) {
	homologs = new hom_s[h_num];
	for (int kk = 0; kk < h_num; kk++) {
	  homologs[kk] = new hom_s();
	}
  }

  j = 0;
  e_val = 0.;
  lineCount = 0;
//  while( fgets( line, 100, fp ) != null ) {
  while( (line = fp.readLine()) != null) {
    lineCount++;
    // handle blank lines in file
    line.trim();
    if (line.length() > 0) {
	if( KS==2 ) {
//	  sscanf( line, " %s %s %lf ", name[0], name[1], &e_val );
	  tokens = line.split("\\s");
	  name[0] = tokens[0];
	  name[1] = tokens[1];
	  evalS = tokens[2];
	  // Java requires digit before exponenet
	  if (evalS.charAt(0)=='e'||evalS.charAt(0)=='E') {
	    //evalS="1."+evalS;
	    System.err.println("format error in file " + fName +
		"at or near line "+ lineCount +
		"; " + evalS + " requires mantissa before exponent");
	    System.exit(-1);
	  }
	  e_val = Double.parseDouble(evalS);
	} else if( KS==3 ) {
//	  sscanf( line, " %s %s %s %lf ", name[0], name[1], name[2], &e_val );
	  tokens = line.split("\\s");
	  name[0] = tokens[0];
	  name[1] = tokens[1];
	  name[2] = tokens[2];
	  evalS = tokens[3];
	  // Java requires digit before exponenet
	  if (evalS.charAt(0)=='e'||evalS.charAt(0)=='E') {
	    //evalS="1."+evalS;
	    System.err.println("format error in file " + fName +
		"at or near line "+ lineCount +
		"; " + evalS + " requires mantissa before exponent");
	    System.exit(-1);
	   }
	   e_val = Double.parseDouble(evalS);
	} else if( KS==1 ) {
//	  sscanf( line, " %s ", name[0] );
	  tokens = line.split("\\s");
	  name[0] = tokens[0];
	}
	if( (KS==1) || (e_val<=blTh) ) {
	  if( KS<3 ) {
		for( k=0 ; k<j ; k++ ) {
		  for( i=0 ; i<KS ; i++ ) 
//			if( strcmp( homologs[k].nameG[i], name[i] ) )
			if( !(homologs[k].nameG[i]).equals( name[i]) )
			  break;
		  if( i==KS ) {
			if( e_val>homologs[k].e_val )
			  homologs[k].e_val = e_val;
			break;
		  }
		}
	  } else
		k=j;

	  if( k==j ) {
		for( i=0 ; i<KS ; i++ ) {
//		  strcpy( homologs[j].nameG[i], name[i] );
		  homologs[j].nameG[i] = new String(name[i]);
		  homologs[j].ind[i] = -1;
		}
		homologs[j].e_val = e_val;
		if( homologs[j].e_val<TINY )
		  homologs[j].e_val = TINY;
		j++;
	  }
	}
    }
  }
//  fclose( fp );
  fp.close();

//  fprintf(stdout,"homologs num=%d, unique=%d\n", h_num, j);
//  System.out.println("homologs num="+h_num+", unique="+j);
  sb.delete(0,sb.length());
  sb.append("homologs num=");
  sb.append(h_num);
  sb.append(", unique=");
  sb.append(j);
  System.out.println(sb);

//MDA
//for(int kk=0; kk<h_num; kk++) {
//System.out.println("homologs["+kk+"] .nameG[0]="+homologs[kk].nameG[0]+", .nameG[1]="+homologs[kk].nameG[1]+", .e_val="+homologs[kk].e_val);
//}


 } catch (Exception e) {
   e.printStackTrace();
 }
}


//copy homolog structure
void copy_homologs( int source, int target )
{		 	 
  int k;

  for( k=0 ; k<KS ; k++ ) {
	homologs[target].ind[k] = homologs[source].ind[k];
//	strcpy( homologs[target].nameG[k],  homologs[source].nameG[k] );
	homologs[target].nameG[k] = new String(homologs[source].nameG[k]);
  }
  homologs[target].e_val = homologs[source].e_val;
  homologs[target].ncY = homologs[source].ncY;
  for( k=0 ; k<homologs[target].ncY ; k++ )
	homologs[target].complexY[k] = homologs[source].complexY[k];
}


//assumes yeast is numbered 0.
//void read_interactions( char *prefix )
void read_interactions( String prefix ) {
//  FILE *fp;
  File fp = null;
  BufferedReader readerFp = null;
//  char file_name[40];
  String file_name;
//  char name1[NAME_LEN], name2[NAME_LEN];
  String name1, name2;
  int ind1, ind2;
  int i, j, k, n_int, l;
  double e,p, total_e[] = new double[3], p_observed[] = new double[3];
  float prob;
  int n;
//  char training[2100][2][NAME_LEN];
//  char dist2[2100][2][NAME_LEN];
//  char train_orth[5000][NAME_LEN];
  String training[][] = new String[2100][2];
  String dist2[][] = new String[2100][2];
  String train_orth[] = new String[5000];
  int n_train=0, found=0, n_orth=0;
  double upper_bound[] = new double[3], lower_bound[] = new double[3];

  String line, tokens[];

 try {

  if( cv_iter>=0 ) {
	upper_bound[0] = 0.502;
	lower_bound[0] = cv_lower_bound[0] = 0.4146;
	upper_bound[1] = 0.4442;
	lower_bound[1] = cv_lower_bound[1] = 0.3302;
	upper_bound[2] = 0.6731;
	lower_bound[2] = cv_lower_bound[2] = 0.43;

//	printf("bounds = %g %g\n", upper_bound[CV_SPECIES], 
//		   lower_bound[CV_SPECIES]);
//	System.out.println("bounds = "+upper_bound[CV_SPECIES]+" "+
//		   lower_bound[CV_SPECIES]);
	sb.delete(0,sb.length());
	sb.append("bounds = ");
	sb.append(upper_bound[CV_SPECIES]);
	sb.append(" ");
	sb.append(lower_bound[CV_SPECIES]);
	System.out.println(sb);

	n_train = 0;
//	sprintf(file_name,"%s%d_training_ints",prefix,CV_SPECIES);
	file_name = prefix+CV_SPECIES+"_training_ints";
//	fp = fopen(file_name,"r");
	fp = new File(file_name);
	readerFp = new BufferedReader(new FileReader(fp));

//MDA
System.out.println("iffy code: file_name="+file_name);
////////////////////
// MDA - this is iffy code - are the entries on a single line, split across
// lines, or ...? - hope it's 2 entries per line, otherwise we'll have to
// change these next lines...
//
//	while( fscanf( fp, " %s %s ", training[n_train][0], 
//				   training[n_train][1]) == 2) 
	while( (line=readerFp.readLine()) != null) {
	  tokens = line.split("\\s");
	  training[n_train][0] = tokens[0];
	  training[n_train][1] = tokens[1];
	  n_train++;
	  if( n_train>2100 )
		my_exit("training set too big");
	}
//	fclose( fp );
	readerFp.close();
	n_orth = 0;
//	sprintf(file_name,"%s%d_orthology",prefix,CV_SPECIES);

	file_name = prefix+CV_SPECIES+"_orthology";
//	fp = fopen(file_name,"r");
	fp = new File(file_name);
	readerFp = new BufferedReader(new FileReader(fp));

//	while( fscanf( fp, " %s ", train_orth[n_orth]) == 1) 
	while( (line=readerFp.readLine()) != null) {
	  tokens = line.split("\\s");
	  train_orth[n_orth] = tokens[0];
	  n_orth++;
	  if( n_orth>5000 )
		my_exit("orthology set too big");
	}
//	fclose( fp );
	readerFp.close();
//	printf("n_train=%d n_orthology=%d\n", n_train, n_orth);
	sb.delete(0,sb.length());
	sb.append("n_train=");
	sb.append(n_train);
	sb.append(" n_orthology=");
	sb.append(n_orth);
	System.out.println(sb);

	found = hide_n = hide_p = 0;
  }

//  bzero( degS, sizeof( degS ) );
  degS = new int[3][MAX_GENES];
/*
  for (int ii=0; ii<3; ii++)
    for (int jj=0; jj<MAX_GENES; jj++) degS[ii][jj]=0;
*/

  for( i=0 ; i<KS ; i++ ) {
	n_int = 0;
//	sprintf(file_name,"%s%d_int", prefix, i);
	file_name = prefix+i+"_int";

//	if( (fp = fopen( file_name, "r" )) == null )
//	  my_exit("Can't open file");
	fp = new File(file_name);
	if (!fp.canRead())
	  my_exit("Can't open file: "+file_name);

	readerFp = new BufferedReader(new FileReader(fp));

//	fscanf(fp, " %d ", &(n_interactions[i]));
	line = readerFp.readLine();
	tokens = line.split("\\s");
	n_interactions[i] = Integer.parseInt(tokens[0]);

	if( interactions[i]==null ) {
//	  interactions[i] = 
//		(edge_s *)malloc( n_interactions[i]*sizeof( edge_s ) );
	  interactions[i] =  new edge_s[n_interactions[i]];
	  for (int jj = 0; jj < n_interactions[i]; jj++) {
	    interactions[i][jj] = new edge_s();
	  }
	}
	total_e[i] = 0;
	n = n_interactions[i];
	if( (cv_iter>=0) && (i==CV_SPECIES) )
	  n_hidden = 0;

//	while( fscanf( fp, " %s %s %f ", name1, name2, &prob ) == 3 ) 
//	  if( !strcmp(name1,name2) ) 
	while ((line=readerFp.readLine()) != null) {
	  tokens = line.split("\\s");
	  name1 = tokens[0];
	  name2 = tokens[1];
 	  prob = Float.parseFloat(tokens[2]);
	  if (name1.equals(name2)) {

		n_interactions[i]--;
		continue;
	  }

	  ind1 = insert_name( name1, i );
	  ind2 = insert_name( name2, i );

	  //consider 1000 highest and lowest interactions
	  if( (cv_iter>=0) && (i==CV_SPECIES) && 
//		  ((int)(drand48()*CV_INT)==cv_iter) && 
		  ((int)(random.nextDouble()*CV_INT)==cv_iter) && 
		  ((prob>upper_bound[CV_SPECIES]) || 
		   (prob<lower_bound[CV_SPECIES])) ) {
		k = 0;
		for( j=0 ; j<n_orth ; j++ )
//		  if( (!strcmp(name1,train_orth[j])) || 
//			  (!strcmp(name2,train_orth[j])) )
		  if( (name1.equals(train_orth[j])) || 
			  (name2.equals(train_orth[j])) )
			k++;
		if( k==2 ) {
		  for( j=0 ; j<n_train ; j++ )
//			if( ((!strcmp(name1,training[j][0])) && 
//				 (!strcmp(name2,training[j][1]))) || 
//				((!strcmp(name1,training[j][1])) && 
//				 (!strcmp(name2,training[j][0]))) )
			if( ((name1.equals(training[j][0])) && 
				 (name2.equals(training[j][1]))) || 
				((name1.equals(training[j][1])) && 
				 (name2.equals(training[j][0]))) )

			  break;
		  if( j==n_train ) {
			if( prob<upper_bound[CV_SPECIES] )
			  hide_n++;
			else
			  hide_p++;
//			if( strcmp(name1,name2)<0 )
			if( name1.compareTo(name2)<0 ) {
//			  printf("HIDE %s-%s %f\n", name1, name2, prob);
//			  System.out.println("HIDE "+name1+"-"+name2+" "+prob);
			  sb.delete(0,sb.length());
			  sb.append("HIDE ");
			  sb.append(name1);
			  sb.append("-");
			  sb.append(name2);
			  sb.append(" ");
			  sb.append(prob);
			  System.out.println(sb);
			} else {
//			  printf("HIDE %s-%s %f\n", name2, name1, prob);
//			  System.out.println("HIDE "+name2+"-"+name1+" "+prob);
			  sb.delete(0,sb.length());
			  sb.append("HIDE ");
			  sb.append(name2);
			  sb.append("-");
			  sb.append(name1);
			  sb.append(" ");
			  sb.append(prob);
			  System.out.println(sb);
			}
			n_interactions[i]--;
			hidden[n_hidden].i1 = ind1;
			hidden[n_hidden].i2 = ind2;
			hidden[n_hidden++].prob = prob;
			continue;
		  } else 
			found++;
		}
	  }

	  interactions[i][n_int].i1 = ind1;
	  interactions[i][n_int].i2 = ind2;
	  interactions[i][n_int].prob = prob;
   	  degS[i][ind1]++;
	  degS[i][ind2]++;
	  total_e[i] += prob;

	  n_int++;
	  for( k=0 ; k<h_num ; k++ ) {
//		if( !strcmp( name1, homologs[k].nameG[i] ) )
		if( name1.equals( homologs[k].nameG[i] ) )
		  homologs[k].ind[i] = ind1;
//		if( !strcmp( name2, homologs[k].nameG[i] ) )
		if( name2.equals( homologs[k].nameG[i] ) )
		  homologs[k].ind[i] = ind2;
	  }
	}
//	fclose( fp );
	readerFp.close();

//	fprintf(stdout,"%d interactions for species %d on %d genes\n", 
//			n_interactions[i], i, n_genes[i]);
//	System.out.println(n_interactions[i]+" interactions for species "+
//			i+" on "+n_genes[i]+" genes");
	sb.delete(0,sb.length());
	sb.append(n_interactions[i]);
	sb.append(" interactions for species ");
	sb.append(i);
	sb.append(" on ");
	sb.append(n_genes[i]);
	sb.append(" genes");
	System.out.println(sb);
  }
  if( cv_iter>=0 ) {
//	printf("Hid %d (+) %d (-)\n", hide_p, hide_n);
//	System.out.println("Hid "+hide_p+" (+) "+hide_n+" (-)");
	sb.delete(0,sb.length());
	sb.append("Hid ");
	sb.append(hide_p);
	sb.append(" (+) ");
	sb.append(hide_n);
	sb.append(" (-)");
	System.out.println(sb);
  }
//  fflush(stdout);
  System.out.flush();

  //total_e - expected number of true edges we observed.
  //so total_e/n_ints=P(t|observed). using complete prob. on p(t):
  for( l=0 ; l<KS ; l++ ) {
	p_true[l] = total_e[l]*2.*true_factor[l]/
	  (double)(n_genes[l]*(n_genes[l]-1));
	p_observed[l] = (double)n_interactions[l]*2./
	  (double)(n_genes[l]*(n_genes[l]-1));
	p_te[l] = (p_true[l]-p_observed[l]*total_e[l]/(double)n_interactions[l])/
	  (1.-p_observed[l]);
//	printf("%d: p_t=%g, p_o=%g, p_te=%g\n", l, p_true[l], p_observed[l],
//		   p_te[l]);
//MDA
//	System.out.println(l+": p_t="+p_true[l]+", p_o="+p_observed[l]+
//		   ", p_te="+p_te[l]);
//	System.out.println(l+": p_t="+two_eight_format.format(p_true[l])+
//		", p_o="+two_eight_format.format(p_observed[l])+
//		", p_te="+two_eight_format.format(p_te[l]));
	sb.delete(0,sb.length());
	sb.append(l);
	sb.append(": p_t=");
	sb.append(two_eight_format.format(p_true[l]));
	sb.append(", p_o=");
	sb.append(two_eight_format.format(p_observed[l]));
	sb.append(", p_te=");
	sb.append(two_eight_format.format(p_te[l]));
	System.out.println(sb);
  }
  i = 0;
  while( i<h_num ) {
	k = 0;
	for( j=0 ; j<KS ; j++ )
	  if( homologs[i].ind[j]<0 )
		k++;
	if( k>0 ) {
	  if( i<(h_num-1) ) 
		copy_homologs( (h_num-1), i );
      h_num--;
    } else {
	  homologs[i].e_val = 0;
      i++;
	}
  }
//  printf("Final number of (possibly isolated) vertices is %d. MAX_VER=%d\n", 
//		 h_num, MAX_VER);
//  System.out.println("Final number of (possibly isolated) vertices is "+h_num+
//		". MAX_VER="+MAX_VER);
  sb.delete(0,sb.length());
  sb.append("Final number of (possibly isolated) vertices is ");
  sb.append(h_num);
  sb.append(". MAX_VER=");
  sb.append(MAX_VER);
  System.out.println(sb);
//  fflush(stdout);
  System.out.flush();

 } catch (Exception exc) {
  exc.printStackTrace();
 }

}	 		 


//void my_exit( char *str )
void my_exit( String str )
{
//  printf("%s\n", str);
  System.out.println(str);
  System.exit(-1);
}

}
