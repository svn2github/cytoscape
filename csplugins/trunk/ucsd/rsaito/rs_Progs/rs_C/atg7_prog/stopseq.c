
/* 終始コドンのまわりの配列を表示する */

int karyu_enki;   /* 表示する下流の塩基数 */

int stopseq_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n],"-stopseq") == 0){
    karyu_enki = atoi(argv[n + 1]);
    return 2;    /* 使うパラメータの数 */
  }
  else return 0;
}


stopseq_head(char *line){

}

stopseq_ent(char *entry, char seqn[], int max, 
	    struct cds_info cds[], int ncds)
{
  int n,s;
/* 
   cds[].cds_start  翻訳開始領域(complementのときは翻訳終了)
   cds[].cds_end    翻訳終了領域(complementのときは翻訳開始)
   cds[].complement 0 = 翻訳が通常の向き 1 = 翻訳が逆向き
*/

  for(n = 0;n < ncds;n ++){
    if(cds[n].complement == 0 && cds[n].cds_end != 0){
      for(s = cds[n].cds_end - 2; s <= cds[n].cds_end + karyu_enki; s++)
	if(s <= max)putchar(seqn[s - 1]);
      putchar('\n');
    }
  }
}
    

stopseq_fin(){
  
  printf("翻訳終了領域の処理が無事終了しました。\n");

}

stopseq_help(){

  printf("-stopseq 翻訳終了領域の配列の表示(塩基数を指定)\n");

}



