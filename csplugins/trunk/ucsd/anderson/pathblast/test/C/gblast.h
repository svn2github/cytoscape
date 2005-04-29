//gblast.h: Created by Roded Sharan, July 2004.

#ifndef __GRAPHBLAST_H
#define __GRAPHBLAST_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <strings.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>

#define MAX_VER 20000 //max. vertices in orthology graph
#define MAX_GENES 8000 //largest genome

#define NAME_LEN 20
#define SUBNET 15
#define MAX_BFS 4
#define MAX_OVERLAP 0.8 //max overlap between two networks before filtering
#define Y_ANNOTS 70
#define Y_CANNOTS 500
#define N_PARTIAL 5
#define PATH_LEN 4

int KS;

// represents homolog tuples
typedef struct {
  char nameG[3][NAME_LEN];
  double e_val;
  int ind[3];
  int complexY[20];
  int ncY;
} hom_s;

typedef struct {
  double w;
  int v;
} single_weight_s;

//for interactions
typedef struct {
  int i1;
  int i2;
  double prob;
} edge_s;


//global vars
hom_s *homologs; //homologs (vertices of G)
int *G[MAX_VER];//homolog graph
int h_num;//number of homologs
int degG[MAX_VER];//degrees in G
char geneNames[3][MAX_GENES][NAME_LEN];//gene names

//prtototypes
int is_dist2( int i1, int i2, char dist2[][2][NAME_LEN], int n );
void eval_orth_annot();
void read_go();
int vote_all( int genes[][SUBNET+1], int n,
			  int cv_success[3], int cv_pred[3] ); 
int predict( int u, int v, char dist2[][2][NAME_LEN], int n );
void content_sig( int i, char *prefix );
double edge_weight(int i, int j);
int distinct_nodes( int i, int j );
void my_exit( char *str );
int all_one( int i, int j );
int find_heaviest( char flag[MAX_VER], char flag2[MAX_VER],
				   int part_sols[N_PARTIAL][SUBNET+1], 
				   double part_weights[N_PARTIAL], int root, 
				   int *best_path, int i_call );
double compute_sgw( int index[], int size, int present[3][MAX_GENES] );
void w_addition( char index[], int i, double *delta_w,
				 int present[3][MAX_GENES] );
void Jaccard( int sols[MAX_VER*N_PARTIAL][SUBNET+1], int n_sols,
			  double weights[MAX_VER*N_PARTIAL] );
int pair_distance( int i, int j, int l, double *p );
void compute_content_stats();
void complex_statistics();
int compute_union( char present[MAX_GENES], int size, int c );
void bfs_search( char *prefix, char *i_prefix );
char is_edge( int i, int j );
char is_interaction( int i, int j );
void filter_sols( int sols[MAX_VER*N_PARTIAL][SUBNET+1], 
				  double weights[MAX_VER*N_PARTIAL], int n_sols, double th );
int insert_class( int vannot[10] );
double compute_vp_weight( int u, int v, int l );
int insert_name( char *name, int species );
void copy_homologs( int source, int target );
int insert_name( char *name, int species );
int find_name( char *name, int species );
void read_homologs( char *prefix );
void read_params( char *param_file );
double calc_single_weight( int d1, int d2, double f, int l );
int enrichment( int ind, int *csol, double weight, char *present,
				int phen_flag );
void build_graph( char *prefix );
void read_interactions( char *prefix );
int find_path( char flag[MAX_VER], 
			   int part_sols[N_PARTIAL][SUBNET+1], 
			   double part_weights[N_PARTIAL], int root, int i_call );
double compute_path_weight( int a, int b, int root, int *tmp_sol,
							char flag1[MAX_VER] );
void shuffle_graphs( int f_call );
void shuffle_species_graph( int l, int f_call );
void build_G();
double my_sort( double *a, int n );
void compute_thresholds();
int balanced( int root, int csol[SUBNET+1] );
int dcompar( const void *a, const void *b );

#endif

  
