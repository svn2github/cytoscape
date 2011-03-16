/* 今回は実際にGenBankのファイルをクラスを用いて操作してみましょう。
   もうGenBankファイルを扱うんですよ。

   次のプログラムを打ちこんで走らせてみよう。
*/

#include <iostream.h>
#include <string.h>
#include <stdio.h>

class GBFile {
 private:
  FILE *_fp;    // このクラスで使われるファイルへのポインタ

 public:
  GBFile(char *);
  ~GBFile();
  void get_next_CDS();

};

 GBFile::GBFile(char *filename){  // クラス構築
   if((_fp = fopen(filename, "r")) == NULL){
     cerr << "File " << '"' << filename << '"' << " not found.";
     exit(1);
   }
 }

 GBFile::~GBFile(){ // クラスが無効になると同時にファイルクローズ
   fclose(_fp);
 }

 void GBFile::get_next_CDS(){  // 次のCDSの行を見つける
   static char line[200];
   while(fgets(line, 200, _fp) != NULL){
     if(strncmp(&line[5], "CDS", 3) == 0){
       cout << line;
       break;
     }
   }
 }

void main(int argc, char *argv[]){

  GBFile gb(argv[1]);   // <--- (a)
  
  gb.get_next_CDS();
  gb.get_next_CDS();
  gb.get_next_CDS();


} // <--- (b)

/* プログラムの各行の意味は分かりますか？

(a)の定義と同時にクラス構築が呼ばれます。つまりファイルがオープン
されるのです。

(b)のところにプログラムの流れがくると(つまりプログラム終了時)
~GBFile()が呼ばれます。

gb.get_next_CDS()で(a)のところで指定されたファイルの次のCDSの行を探し、
表示します。

課題１：このプログラムのクラスにDEFINITIONの行を探し、表示する
get_definitionを付け加えましょう。

課題２：このプログラムのクラスGBFileのprivateにint _cds_start[10000];
とint _cds_end[10000];int _ncdsを加え、読み込んだCDSの開始位置、終了位置、
今までに読み込んだCDSの数を書き込むプログラムをget_next_cdsに追加しましょう。



