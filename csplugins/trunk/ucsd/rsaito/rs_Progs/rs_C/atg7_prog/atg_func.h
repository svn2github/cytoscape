/* global_st.h must be pre-declared */

/* atg7.c */

extern struct gparam entry_info;
extern struct gloparam all;
extern char valid_cds[CDSMAX];

/* func_init.c */

#define MAX_F 200

extern int (*param[MAX_F])(int, char **, int); 
extern void (*head[MAX_F])(char *); 
extern void (*entry[MAX_F])(char *, char *, int, int *, int); 
extern void (*entry2[MAX_F])(char *, char *, int, struct cds_info[], int);
      /* struct cds_info is declared in global_st.h */
extern void (*entry3[MAX_F])(struct gparam[], char *, int, 
			     struct cds_info[], int);
      /* struct gparam is declared in global_st.h */
extern void (*final[MAX_F])(void);
extern void (*help[MAX_F])(void);

extern void (*pred_func)(char *,int, int *, int *);

extern int fc_flag[MAX_F]; 

int func_init(void);

/* nseqread.c */

void nseqread(char **, int *, FILE *);

/* strrout.c */

extern double chi_prob(int, double);
extern double poisson_over(int, double);
extern double norm_half(double);
extern int lpatm(char *, char *);
extern int lpatms3(char *, char *);
extern int lpatmAa(char *, char *);
extern int find_word(char *, char *);
extern int spmatch(char *, char *);
extern int spmatchn(char *, char *, int);
extern int next_patsp(char [], int);
extern int next_patsp2(char [], int);
extern int cmpl(char);
extern char *compseqget(char [], int);
extern int incwi(char *, char *, int);
extern void rev(char *);
extern int countmatch(char *, char *, int);
extern int candrec(struct recbest10 *, struct recbest10[], int);
extern int comp_match(char, char);

/* alignmemt */
double sd_match(char [], char [],char [], char [], int *,
		int [], int []);
double sd_match_opt(char [], char [],char [], char [], int *,
		int [], int []);
void disp_res(char [], char [], int [], int [], int);

/* colcalh.c */

char ntc(nucleotide);
nucleotide cton(char);





