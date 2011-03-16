#include <stdio.h>
#include <stdlib.h>

main(int argc, char *argv[]){

  int c;
  FILE *fp_in, *fp_out;
  int counter, block, block_size;
  char outfilename[20];

  if((fp_in = fopen(argv[1],"r")) == NULL){
    fprintf(stderr, "File \"%s\" not found.\n", argv[1]);
    exit(1);
  }
  
  counter = 0;
  block = 0;
  if((block_size = atoi(argv[2])) <= 0){
    fprintf(stderr, "Parameter error...\n");
    exit(1);
  }
  
  sprintf(outfilename, "%s_F%d", argv[1], block);
  printf("Creating file \"%s\" ...\n", outfilename);
  fp_out = fopen(outfilename, "w");
  while((c = fgetc(fp_in)) != EOF){
    counter ++;
    if(counter > block_size){
      fclose(fp_out);
      counter = 0;
      block ++;
      sprintf(outfilename, "%s_F%d", argv[1], block);
      printf("Creating file \"%s\" ...\n", outfilename);
      fp_out = fopen(outfilename, "w");
    }
    fputc(c, fp_out);
  }

  fclose(fp_in);
  fclose(fp_out);

}
