#include <stdio.h>
#include "global_st.h"
#include "atg_func.h"

/* aonly.c */
int aonly_par(int, char *[], int);
void aonly_head(char *);
void aonly_ent(struct gparam[], char *, int, struct cds_info[], int);
void aonly_fin();
void aonly_help();

/* aonly2.c */
int aonly2_par(int, char *[], int);
void aonly2_head(char *);
void aonly2_ent(struct gparam[], char *, int, struct cds_info[], int);
void aonly2_fin();
void aonly2_help();

/* aonly3.c */
int aonly3_par(int, char *[], int);
void aonly3_head(char *);
void aonly3_ent(struct gparam[], char *, int, struct cds_info[], int);
void aonly3_fin();
void aonly3_help();

/* aonlyes.c */
int aonlyes_par(int, char *[], int);
void aonlyes_head(char *);
void aonlyes_ent(struct gparam[], char *, int, struct cds_info[], int);
void aonlyes_fin();
void aonlyes_help();

/* aonly4.c */
int aonly4_par(int, char *[], int);
void aonly4_head(char *);
void aonly4_ent(struct gparam[], char *, int, struct cds_info[], int);
void aonly4_fin();
void aonly4_help();

/* starteli.c */
int starteli_par(int, char *[], int);
void starteli_head(char *);
void starteli_ent(struct gparam[], char *, int, struct cds_info[], int);
void starteli_fin();
void starteli_help();

/* stopeli.c */
int stopeli_par(int, char *[], int);
void stopeli_head(char *);
void stopeli_ent(struct gparam[], char *, int, struct cds_info[], int);
void stopeli_fin();
void stopeli_help();


/* trsite6.c */
int trsite6_par(int, char *[], int);
void trsite6_head(char *);
void trsite6_ent(struct gparam[], char *, int, struct cds_info[], int);
void trsite6_fin();
void trsite6_help();

/* only.c */
int only_par(int, char *[], int);
void only_head(char *);
void only_ent(struct gparam[], char *, int, struct cds_info[], int);
void only_fin();
void only_help();

/* patonly.c */
int patonly_par(int, char *[], int);
void patonly_head(char *);
void patonly_ent(struct gparam[], char *, int, struct cds_info[], int);
void patonly_fin();
void patonly_help();

/* patonly2.c */
int patonly2_par(int, char *[], int);
void patonly2_head(char *);
void patonly2_ent(struct gparam[], char *, int, struct cds_info[], int);
void patonly2_fin();
void patonly2_help();

/* patonly2m.c */
int patonly2m_par(int, char *[], int);
void patonly2m_head(char *);
void patonly2m_ent(struct gparam[], char *, int, struct cds_info[], int);
void patonly2m_fin();
void patonly2m_help();

/* min_cds_len.c */

int min_cds_len_par(int, char *[], int);
void min_cds_len_head(char *);
void min_cds_len_ent(struct gparam[], char *, int, struct cds_info[], int);
void min_cds_len_fin();
void min_cds_len_help();


/* eliatg.c */
int eliatg_par(int, char *[], int);
void eliatg_head(char *);
void eliatg_ent(struct gparam[], char *, int, struct cds_info[], int);
void eliatg_fin();
void eliatg_help();



/* testvc.c */
int testvc_par(int, char *[], int);
void testvc_head(char *);
void testvc_ent(struct gparam[], char *, int, struct cds_info[], int);
void testvc_fin();
void testvc_help();

/* bun.c */
int bun_par(int, char *[], int);
void bun_head(char *);
void bun_ent(char *, char *, int, int[], int);
void bun_fin();
void bun_help();

/* buns.c */
int buns_par(int, char *[], int n);
void buns_head(char *);
void buns_ent(char *, char *, int, struct cds_info[], int);
void buns_fin();
void buns_help();
int bun_lk_par(int, char *[], int n);
void bun_lk_head(char *);
void bun_lk_ent(struct gparam[], char *, int, struct cds_info[], int);
void bun_lk_fin();
void bun_lk_help();
int bunskip_par(int, char *[], int);
void bunskip_ent(char *, char *, int, struct cds_info[], int);
void bunskip_help(); /* Final process function is common with buns_fin */

/* testt.c */
int testt_par(int , char **, int n);
void testt_head(char *);
void testt_ent(char *, char *, int, struct cds_info[], int);
void testt_fin();
void testt_help();

/* handget.c */
extern int handget_par(int, char **, int);
extern void handget_head(char *);
extern void handget_ent(char *, char *, int, int *, int);
extern void handget_fin();
extern void handget_help();

/* spsim.c */
extern int spsim_par(int, char **, int);
extern void spsim_head(char *);
extern void spsim_ent(char *, char *, int, struct cds_info[], int);
extern void spsim_fin();
extern void spsim_help();

/* narasen.c */
int narasen_par(int, char **, int);
void narasen_head(char *);
void narasen_ent(char *, char *, int, struct cds_info[], int);
void narasen_fin();
void narasen_help();

/* freq_func.c */
int freq_par(int, char **, int);
void freq_head(char *);
void freq_ent(struct gparam[], char *, int, struct cds_info[], int);
void freq_fin();
void freq_help();

/* patfreq.c */
int utrr_par(int, char **, int);
void utrr_help();
int cdsr_par(int, char **, int);
void cdsr_help();
int smooth_par(int, char **, int);
void smooth_help();
int segm_par(int, char **, int);
void segm_help();

int patavrange_par(int, char **, int);
void patavrange_help();
int patdisprange_par(int, char **, int);
void patdisprange_help();

int total_all_mode_par(int, char **, int);
void total_all_mode_help();

int patfreq_par(int, char **, int);
void patfreq_head(char *);
void patfreq_ent(struct gparam[], char *, int, struct cds_info[], int);
void patfreq_fin();
void patfreq_help();
int patafreq_par(int , char **, int);
void patafreq_head(char *);
void patafreq_ent(struct gparam[], char *, int, struct cds_info[], int);
void patafreq_fin();
void patafreq_help();
int patdfreq_par(int , char **, int);
void patdfreq_head(char *);
void patdfreq_ent(struct gparam[], char *, int, struct cds_info[], int);
void patdfreq_fin();
void patdfreq_help();
int patefreq_par(int , char **, int);
void patefreq_head(char *);
void patefreq_ent(struct gparam[], char *, int, struct cds_info[], int);
void patefreq_fin();
void patefreq_help();
int patefreq2_par(int , char **, int);
void patefreq2_head(char *);
void patefreq2_ent(struct gparam[], char *, int, struct cds_info[], int);
void patefreq2_fin();
void patefreq2_help();


/* wind.c */
int wind_par(int, char **, int);
void wind_head(char *);
void wind_ent(struct gparam[], char *, int, struct cds_info[], int);
void wind_fin();
void wind_help();

int swind_par(int, char **, int);
void swind_head(char *);
void swind_ent(struct gparam[], char *, int, struct cds_info[], int);
void swind_fin();
void swind_help();

int gcwind_par(int, char **, int);
void gcwind_head(char *);
void gcwind_ent(struct gparam[], char *, int, struct cds_info[], int);
void gcwind_fin();
void gcwind_help();


/* ec_970506.c */
int ec970506_par(int, char **, int);
void ec970506_head(char *);
void ec970506_ent(struct gparam[], char *, int, struct cds_info[], int);
void ec970506_fin();
void ec970506_help();

/* trsite5.c */
int trsite5_par(int, char **, int);
void trsite5_head(char *);
void trsite5_ent(struct gparam[], char *, int, struct cds_info[], int);
void trsite5_fin();
void trsite5_help();

/* icisl.c */
int icisl_par(int, char **, int);
void icisl_head(char *);
void icisl_ent(struct gparam[], char *, int, struct cds_info[], int);
void icisl_fin();
void icisl_help();

/* lkatgav.c */
int lkatgav_par(int, char **, int);
void lkatgav_head(char *);
void lkatgav_ent(struct gparam[], char *, int, struct cds_info[], int);
void lkatgav_fin();
void lkatgav_help();

/* junkget.c */
int junkget_par(int, char **, int);
void junkget_head(char *);
void junkget_ent(struct gparam[], char *, int, struct cds_info[], int);
void junkget_fin();
void junkget_help();

/* atcgcon.c */
int atcgcon_par(int, char **, int);
void atcgcon_head(char *);
void atcgcon_ent(struct gparam[], char *, int, struct cds_info[], int);
void atcgcon_fin();
void atcgcon_help();

/* utrcds_gc.c */
int utrcds_gc_par(int, char **, int);
void utrcds_gc_head(char *);
void utrcds_gc_ent(struct gparam[], char *, int, struct cds_info[], int);
void utrcds_gc_fin();
void utrcds_gc_help();

/* cDNA_5utr_atg.c */
int cDNA_5utr_atg_par(int, char **, int);
void cDNA_5utr_atg_head(char *);
void cDNA_5utr_atg_ent(struct gparam[], char *, int, struct cds_info[], int);
void cDNA_5utr_atg_fin();
void cDNA_5utr_atg_help();


/* trterm.c */
int trterm_par(int, char **, int);
void trterm_head(char *);
void trterm_ent(struct gparam[], char *, int, struct cds_info[], int);
void trterm_fin();
void trterm_help();

/* hfindpat.c */
int hfindpat_par(int, char **, int);
void hfindpat_head(char *);
void hfindpat_ent(struct gparam[], char *, int, struct cds_info[], int);
void hfindpat_fin();
void hfindpat_help();

/* pseudo_atg.c */
int pseudo_atg_par(int, char **, int);
void pseudo_atg_head(char *);
void pseudo_atg_ent(struct gparam[], char *, int, struct cds_info[], int);
void pseudo_atg_fin();
void pseudo_atg_help();

/* scan_2d.c */
int scan_2d_par(int, char **, int);
void scan_2d_head(char *);
void scan_2d_ent(struct gparam[], char *, int, struct cds_info[], int);
void scan_2d_fin();
void scan_2d_help();

/* scan_stem.c */
int scan_stem_par(int, char **, int);
void scan_stem_head(char *);
void scan_stem_ent(struct gparam[], char *, int, struct cds_info[], int);
void scan_stem_fin();
void scan_stem_help();
int rscan_stem_par(int, char **, int);
void rscan_stem_head(char *);
void rscan_stem_ent(struct gparam[], char *, int, struct cds_info[], int);
void rscan_stem_fin();
void rscan_stem_help();


/* scan_bstem.c */
int scan_bstem_par(int, char **, int);
void scan_bstem_head(char *);
void scan_bstem_ent(struct gparam[], char *, int, struct cds_info[], int);
void scan_bstem_fin();
void scan_bstem_help();

/* scan_mstem.c */
int scan_mstem_par(int, char **, int);
void scan_mstem_head(char *);
void scan_mstem_ent(struct gparam[], char *, int, struct cds_info[], int);
void scan_mstem_fin();
void scan_mstem_help();


/* sdwin.c */
int sdwin_par(int, char **, int);
void sdwin_head(char *);
void sdwin_ent(struct gparam[], char *, int, struct cds_info[], int);
void sdwin_fin();
void sdwin_help();




/* cyaex.c */
int cyaex_par(int, char **, int);

void cyaex_ent(struct gparam[], char *, int, struct cds_info[], int);

void cyaex_help();

/* palind.c */
int palind_par(int, char **, int);
void palind_head(char *);
void palind_ent(struct gparam[], char *, int, struct cds_info[], int);
void palind_fin();
void palind_help();

/* palind2.c */
int palind2_par(int, char **, int);
void palind2_head(char *);
void palind2_ent(struct gparam[], char *, int, struct cds_info[], int);
void palind2_fin();
void palind2_help();

/* palind3.c */
int palind3_par(int, char **, int);
void palind3_head(char *);
void palind3_ent(struct gparam[], char *, int, struct cds_info[], int);
void palind3_fin();
void palind3_help();

/* palind4.c */
int palind4_par(int, char **, int);
void palind4_head(char *);
void palind4_ent(struct gparam[], char *, int, struct cds_info[], int);
void palind4_fin();
void palind4_help();

/* cds_echo.c */
int cds_echo_par(int, char **, int);
void cds_echo_head(char *);
void cds_echo_ent(struct gparam[], char *, int, struct cds_info[], int);
void cds_echo_fin();
void cds_echo_help();

/* patfind.c */
int patfind_par(int, char **, int);
void patfind_head(char *);
void patfind_ent(struct gparam[], char *, int, struct cds_info[], int);
void patfind_fin();
void patfind_help();

/* colcal.c */
int colcal_par(int, char **, int);
void colcal_head(char *);
void colcal_ent(struct gparam[], char *, int, struct cds_info[], int);
void colcal_fin();
void colcal_help();

/* orfsearch.c */
int orfsearch_par(int, char **, int);
void orfsearch_head(char *);
void orfsearch_ent(struct gparam[], char *, int, struct cds_info[], int);
void orfsearch_fin();
void orfsearch_help();
int orfsearch_myco_par(int, char **, int);
void orfsearch_myco_head(char *);
void orfsearch_myco_ent(struct gparam[], char *, int, struct cds_info[], int);
void orfsearch_myco_fin();
void orfsearch_myco_help();

/* rand_pro.c */
int rand_pro_par(int, char **, int);
void rand_pro_head(char *);
void rand_pro_ent(struct gparam[], char *, int, struct cds_info[], int);
void rand_pro_fin();
void rand_pro_help();

/* idinuc_count.c */
int idinuc_count_par(int, char **, int);
void idinuc_count_head(char *);
void idinuc_count_ent(struct gparam[], char *, int, struct cds_info[], int);
void idinuc_count_fin();
void idinuc_count_help();

/* dash3.c */
int dash3_par(int, char **, int);
void dash3_head(char *);
void dash3_ent(struct gparam[], char *, int, struct cds_info[], int);
void dash3_fin();
void dash3_help();


int (*param[MAX_F])(int, char **, int); 
void (*head[MAX_F])(char *); 
void (*entry[MAX_F])(char *, char *, int, int *, int); 
void (*entry2[MAX_F])(char *, char *, int, struct cds_info[], int);
void (*entry3[MAX_F])(struct gparam[], char *, int, struct cds_info[],int);
void (*final[MAX_F])(void);
void (*help[MAX_F])(void);

void (*pred_func)(char *,int, int *, int *);

int fc_flag[MAX_F]; 

/* パラメータによって呼び出される関数をここで登録 */
/* 変数 n には登録された関数の数が入る */
int func_init(void)
{
   int n;
   for(n = 0;n < MAX_F;n ++){
      param[n] = NULL;
      head[n] = NULL;
      entry[n] = NULL;
      entry2[n] = NULL;
      entry3[n] = NULL;
      final[n] = NULL;
      help[n] = NULL;
      fc_flag[n] = 0;
    }

/* ここにATG開始領域予測関数を書く */
   pred_func = NULL;

   n = 0;

/* ここに普通のパラメータで呼び出される関数を書く */

   param[n] = aonly_par;
   head[n] = NULL;
   entry3[n] = aonly_ent;
   final[n] = aonly_fin;
   help[n] = aonly_help;
   n ++;

   param[n] = aonly2_par;
   head[n] = NULL;
   entry3[n] = aonly2_ent;
   final[n] = aonly2_fin;
   help[n] = aonly2_help;
   n ++;

   param[n] = aonly3_par;
   head[n] = NULL;
   entry3[n] = aonly3_ent;
   final[n] = aonly3_fin;
   help[n] = aonly3_help;
   n ++;

   param[n] = aonlyes_par;
   head[n] = NULL;
   entry3[n] = aonlyes_ent;
   final[n] = aonlyes_fin;
   help[n] = aonlyes_help;
   n ++;

   param[n] = aonly4_par;
   head[n] = NULL;
   entry3[n] = aonly4_ent;
   final[n] = aonly4_fin;
   help[n] = aonly4_help;
   n ++;

   param[n] = starteli_par;
   head[n] = NULL;
   entry3[n] = starteli_ent;
   final[n] = starteli_fin;
   help[n] = starteli_help;
   n ++;

   param[n] = stopeli_par;
   head[n] = NULL;
   entry3[n] = stopeli_ent;
   final[n] = stopeli_fin;
   help[n] = stopeli_help;
   n ++;

   param[n] = trsite6_par;
   head[n] = NULL;
   entry3[n] = trsite6_ent;
   final[n] = trsite6_fin;
   help[n] = trsite6_help;
   n ++;

   param[n] = only_par;
   head[n] = NULL;
   entry3[n] = only_ent;
   final[n] = only_fin;
   help[n] = only_help;
   n ++;

   param[n] = patonly_par;
   head[n] = NULL;
   entry3[n] = patonly_ent;
   final[n] = patonly_fin;
   help[n] = patonly_help;
   n ++;

   param[n] = patonly2_par;
   head[n] = NULL;
   entry3[n] = patonly2_ent;
   final[n] = patonly2_fin;
   help[n] = patonly2_help;
   n ++;

   param[n] = patonly2m_par;
   head[n] = NULL;
   entry3[n] = patonly2m_ent;
   final[n] = patonly2m_fin;
   help[n] = patonly2m_help;
   n ++;

   param[n] = min_cds_len_par;
   head[n] = NULL;
   entry3[n] = min_cds_len_ent;
   final[n] = min_cds_len_fin;
   help[n] = min_cds_len_help;
   n ++;

   param[n] = eliatg_par;
   head[n] = NULL;
   entry3[n] = eliatg_ent;
   final[n] = eliatg_fin;
   help[n] = eliatg_help;
   n ++;

   param[n] = utrr_par;
   head[n] = NULL;
   entry3[n] = NULL;
   final[n] = NULL;
   help[n] = utrr_help;
   n ++;

   param[n] = cdsr_par;
   head[n] = NULL;
   entry3[n] = NULL;
   final[n] = NULL;
   help[n] = cdsr_help;
   n ++;

   param[n] = smooth_par;
   head[n] = NULL;
   entry3[n] = NULL;
   final[n] = NULL;
   help[n] = smooth_help;
   n ++;

   param[n] = segm_par;
   head[n] = NULL;
   entry3[n] = NULL;
   final[n] = NULL;
   help[n] = segm_help;
   n ++;

   param[n] = patavrange_par;
   head[n] = NULL;
   entry3[n] = NULL;
   final[n] = NULL;
   help[n] = patavrange_help;
   n ++;

   param[n] = patdisprange_par;
   head[n] = NULL;
   entry3[n] = NULL;
   final[n] = NULL;
   help[n] = patdisprange_help;
   n ++;

   param[n] = total_all_mode_par;
   head[n] = NULL;
   entry3[n] = NULL;
   final[n] = NULL;
   help[n] = total_all_mode_help;
   n ++;

   param[n] = testvc_par;
   head[n] = NULL;
   entry3[n] = testvc_ent;
   final[n] = testvc_fin;
   help[n] = testvc_help;
   n ++;

   param[n] = bun_par;
   head[n] = NULL;
   entry[n] = bun_ent;
   final[n] = bun_fin;
   help[n] = bun_help;
   n ++;

   param[n] = buns_par;
   head[n] = NULL;
   entry2[n] = buns_ent;
   final[n] = buns_fin;
   help[n] = buns_help;
   n ++;

   param[n] = bun_lk_par;
   head[n] = NULL;
   entry3[n] = bun_lk_ent;
   final[n] = bun_lk_fin;
   help[n] = bun_lk_help;
   n ++;



   param[n] = bunskip_par;
   head[n] = NULL;
   entry2[n] = bunskip_ent;
   final[n] = buns_fin;
   help[n] = bunskip_help;
   n ++;

   param[n] = testt_par;
   head[n] = NULL;
   entry2[n] = testt_ent;
   final[n] = testt_fin;
   help[n] = testt_help;
   n ++;

   param[n] = handget_par;
   head[n] = NULL;
   entry[n] = handget_ent;
   final[n] = handget_fin;
   help[n] = handget_help;
   n ++;

   param[n] = spsim_par;
   head[n] = NULL;
   entry2[n] = spsim_ent;
   final[n] = spsim_fin;
   help[n] = spsim_help;
   n ++;
   
   param[n] = narasen_par;
   head[n] = NULL;
   entry2[n] = narasen_ent;
   final[n] = narasen_fin;
   help[n] = narasen_help;
   n ++;

   param[n] = freq_par;
   head[n] = freq_head;
   entry3[n] = freq_ent;
   final[n] = freq_fin;
   help[n] = freq_help;
   n ++;

   param[n] = patfreq_par;
   head[n] = patfreq_head;
   entry3[n] = patfreq_ent;
   final[n] = patfreq_fin;
   help[n] = patfreq_help;
   n ++;

   param[n] = patafreq_par;
   head[n] = patafreq_head;
   entry3[n] = patafreq_ent;
   final[n] = patafreq_fin;
   help[n] = patafreq_help;
   n ++;

   param[n] = patdfreq_par;
   head[n] = patdfreq_head;
   entry3[n] = patdfreq_ent;
   final[n] = patdfreq_fin;
   help[n] = patdfreq_help;
   n ++;

   param[n] = patefreq_par;
   head[n] = patefreq_head;
   entry3[n] = patefreq_ent;
   final[n] = patefreq_fin;
   help[n] = patefreq_help;
   n ++;

   param[n] = patefreq2_par;
   head[n] = patefreq2_head;
   entry3[n] = patefreq2_ent;
   final[n] = patefreq2_fin;
   help[n] = patefreq2_help;
   n ++;

   param[n] = wind_par;
   head[n] = wind_head;
   entry3[n] = wind_ent;
   final[n] = wind_fin;
   help[n] = wind_help;
   n ++;

   param[n] = swind_par;
   head[n] = swind_head;
   entry3[n] = swind_ent;
   final[n] = swind_fin;
   help[n] = swind_help;
   n ++;

   param[n] = gcwind_par;
   head[n] = gcwind_head;
   entry3[n] = gcwind_ent;
   final[n] = gcwind_fin;
   help[n] = gcwind_help;
   n ++;

   param[n] = ec970506_par;
   head[n] = ec970506_head;
   entry3[n] = ec970506_ent;
   final[n] = ec970506_fin;
   help[n] = ec970506_help;
   n ++;

   param[n] = trsite5_par;
   head[n] = trsite5_head;
   entry3[n] = trsite5_ent;
   final[n] = trsite5_fin;
   help[n] = trsite5_help;
   n ++;

   param[n] = icisl_par;
   head[n] = icisl_head;
   entry3[n] = icisl_ent;
   final[n] = icisl_fin;
   help[n] = icisl_help;
   n ++;

   param[n] = lkatgav_par;
   head[n] = lkatgav_head;
   entry3[n] = lkatgav_ent;
   final[n] = lkatgav_fin;
   help[n] = lkatgav_help;
   n ++;

   param[n] = junkget_par;
   head[n] = junkget_head;
   entry3[n] = junkget_ent;
   final[n] = junkget_fin;
   help[n] = junkget_help;
   n ++;

   param[n] = atcgcon_par;
   head[n] = atcgcon_head;
   entry3[n] = atcgcon_ent;
   final[n] = atcgcon_fin;
   help[n] = atcgcon_help;
   n ++;

   param[n] = utrcds_gc_par;
   head[n] = utrcds_gc_head;
   entry3[n] = utrcds_gc_ent;
   final[n] = utrcds_gc_fin;
   help[n] = utrcds_gc_help;
   n ++;

   param[n] = cDNA_5utr_atg_par;
   head[n] = cDNA_5utr_atg_head;
   entry3[n] = cDNA_5utr_atg_ent;
   final[n] = cDNA_5utr_atg_fin;
   help[n] = cDNA_5utr_atg_help;
   n ++;

   param[n] = trterm_par;
   head[n] = trterm_head;
   entry3[n] = trterm_ent;
   final[n] = trterm_fin;
   help[n] = trterm_help;
   n ++;

   param[n] = hfindpat_par;
   head[n] = hfindpat_head;
   entry3[n] = hfindpat_ent;
   final[n] = hfindpat_fin;
   help[n] = hfindpat_help;
   n ++;

   param[n] = pseudo_atg_par;
   head[n] = pseudo_atg_head;
   entry3[n] = pseudo_atg_ent;
   final[n] = pseudo_atg_fin;
   help[n] = pseudo_atg_help;
   n ++;

   param[n] = scan_2d_par;
   head[n] = scan_2d_head;
   entry3[n] = scan_2d_ent;
   final[n] = scan_2d_fin;
   help[n] = scan_2d_help;
   n ++;

   param[n] = scan_stem_par;
   head[n] = scan_stem_head;
   entry3[n] = scan_stem_ent;
   final[n] = scan_stem_fin;
   help[n] = scan_stem_help;
   n ++;

   param[n] = rscan_stem_par;
   head[n] = rscan_stem_head;
   entry3[n] = rscan_stem_ent;
   final[n] = rscan_stem_fin;
   help[n] = rscan_stem_help;
   n ++;

   param[n] = scan_bstem_par;
   head[n] = scan_bstem_head;
   entry3[n] = scan_bstem_ent;
   final[n] = scan_bstem_fin;
   help[n] = scan_bstem_help;
   n ++;

   param[n] = scan_mstem_par;
   head[n] = scan_mstem_head;
   entry3[n] = scan_mstem_ent;
   final[n] = scan_mstem_fin;
   help[n] = scan_mstem_help;
   n ++;


   param[n] = sdwin_par;
   head[n] = sdwin_head;
   entry3[n] = sdwin_ent;
   final[n] = sdwin_fin;
   help[n] = sdwin_help;
   n ++;


   param[n] = cyaex_par;
   head[n] = NULL;
   entry3[n] = cyaex_ent;
   final[n] = NULL;
   help[n] = cyaex_help;
   n ++;

   param[n] = palind_par;
   head[n] = palind_head;
   entry3[n] = palind_ent;
   final[n] = palind_fin;
   help[n] = palind_help;
   n ++;

   param[n] = palind2_par;
   head[n] = palind2_head;
   entry3[n] = palind2_ent;
   final[n] = palind2_fin;
   help[n] = palind2_help;
   n ++;

   param[n] = palind3_par;
   head[n] = palind3_head;
   entry3[n] = palind3_ent;
   final[n] = palind3_fin;
   help[n] = palind3_help;
   n ++;

   param[n] = palind4_par;
   head[n] = palind4_head;
   entry3[n] = palind4_ent;
   final[n] = palind4_fin;
   help[n] = palind4_help;
   n ++;

   param[n] = cds_echo_par;
   head[n] = cds_echo_head;
   entry3[n] = cds_echo_ent;
   final[n] = cds_echo_fin;
   help[n] = cds_echo_help;
   n ++;

   param[n] = patfind_par;
   head[n] = patfind_head;
   entry3[n] = patfind_ent;
   final[n] = patfind_fin;
   help[n] = patfind_help;
   n ++;

   param[n] = colcal_par;
   head[n] = colcal_head;
   entry3[n] = colcal_ent;
   final[n] = colcal_fin;
   help[n] = colcal_help;
   n ++;

   param[n] = orfsearch_par;
   head[n] = orfsearch_head;
   entry3[n] = orfsearch_ent;
   final[n] = orfsearch_fin;
   help[n] = orfsearch_help;
   n ++;

   param[n] = orfsearch_myco_par;
   head[n] = orfsearch_myco_head;
   entry3[n] = orfsearch_myco_ent;
   final[n] = orfsearch_myco_fin;
   help[n] = orfsearch_myco_help;
   n ++;

   param[n] = rand_pro_par;
   head[n] = rand_pro_head;
   entry3[n] = rand_pro_ent;
   final[n] = rand_pro_fin;
   help[n] = rand_pro_help;
   n ++;

   param[n] = idinuc_count_par;
   head[n] = idinuc_count_head;
   entry3[n] = idinuc_count_ent;
   final[n] = idinuc_count_fin;
   help[n] = idinuc_count_help;
   n ++;

   param[n] = dash3_par;
   head[n] = dash3_head;
   entry3[n] = dash3_ent;
   final[n] = dash3_fin;
   help[n] = dash3_help;
   n ++;

   return n;
}



