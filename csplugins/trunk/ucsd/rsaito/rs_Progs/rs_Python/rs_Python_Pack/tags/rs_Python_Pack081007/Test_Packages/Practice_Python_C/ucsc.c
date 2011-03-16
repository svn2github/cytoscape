#include <Python.h>

char *complement(char *base){
  int i,j;

  char *cmpl = (char *)PyMem_Malloc(sizeof(char) * (strlen(base)+1) );

  j = 0;
  for( i = strlen(base) -1; 0 <= i ; i--){
    switch(base[i]){
    case 'a':
      cmpl[j++] = 't';
      break;
    case 't':
      cmpl[j++] = 'a';
      break;
    case 'g':
      cmpl[j++] = 'c';
      break;
    case 'c':
      cmpl[j++] = 'g';
      break;
    default:
      printf("Invalid char %c found\n", base[i]);
      base[i+1]='\0';
      printf( "%s\n", base );
      return(0);
    }
  }
  cmpl[j] = '\0';
  return cmpl;

}

void make_query(char *base, char *result){
  int i;
  for( i = 0; i < strlen(base); i++ ){
    result[i] = tolower(base[i]);
  }
  result[i] = '\0';
}

char *load_UCSC_file(char *filename, char *title){
  FILE *fp;
  char *seq;

  // Check: can't open file??
  if( (fp = fopen(filename,"r")) == NULL ){
    printf( "can't open file %s", filename );
    return 0;
  }

  // Check the file size
  fseek( fp, 0L, SEEK_END );	 // move file pointer to the EOF
  size_t f_size = ftell( fp );   // get the file size
  fseek( fp, 0L, SEEK_SET );	 // move fp to the beginning of the file

  // memory allocation
  seq = (char *)PyMem_Malloc(f_size);
  if(seq == NULL) {
      printf("Memory allocation error: file size is %d\n", (int)f_size);
      return 0;
   }
  else{
    fprintf(stderr, "[load_file] %s: size=%d b\n", filename, (int)f_size);
  }

  // read sequence
  int n = 0;
  int line_num = 0;
  char line[256];
  while(fgets(line,256,fp) != NULL){
    line_num++;
    if(line[0] == '>'){
      line[strlen(line) - 1] = '\0'; // remove "\n"
      strcpy(title,&line[1]);        // get after ">"
    }else{
      int c_point;
      for(c_point = 0; line[c_point] != '\n'; c_point++){
	if( ! isalpha(line[c_point]) ){
	  printf("non-alphabet found at line %d: %c\n", line_num, 
		 line[c_point]);
	  return 0;
	}
	seq[n] = tolower(line[c_point]);
	n++;
      }
    }
  }
  seq[n] = '\0';
  fprintf(stderr,"%d characters loaded\n", n);

  return seq;
}


int *search_in_fasta_file(char *query, char *seq)
{
  char *p;
  int index=-9999;

  int alloc_len = 1024;
  int *result=(int *)PyMem_Malloc(sizeof(int) * alloc_len);
  if(result == NULL){
    fprintf(stderr,"Cannot allocate memory %d\n", alloc_len);
  }

  int current_memory_len = alloc_len;
  int i;
  for(p = seq, i = 1; p != '\0'; p++, i++){
    p = strstr(p,query);
    if(p == NULL){
      //fprintf(stderr, "no match after %d(%s)\n", (int)(p - seq)+1, filename);
      break;
    }
    index = (int)(p - seq) + 1;
    //printf("%s:%d\n",title,index);

    // Increase memory
    if(i < current_memory_len -1){
    }else{
      current_memory_len += alloc_len;
      //fprintf(stderr, "i=%d  allocate: %d\n", i, alloc_len * times);
      result = PyMem_Realloc(result, sizeof(int) * current_memory_len);
      if(result == NULL){
	fprintf(stderr,"Cannot reallocate memory %d\n", current_memory_len);
      }
    }

    result[i] = index;
  }
  result[0] = i;
  return result;
}

char *search_in_fastafile(char *query, char *seq, char *title)
{


  int alloc_len = 1024;
  char *result=(char *)PyMem_Malloc(sizeof(char) * alloc_len);
  if(result == NULL){
    fprintf(stderr,"Cannot allocate memory %d\n", alloc_len);
  }


  int alloc_times = 1;
  int max = alloc_len;
  int res_len = 0;
  int index;
  char current[256];
  char *p;
  for(p = seq; p != NULL; p++){

    p = strstr(p,query);

    index = (int)(p - seq) + 1;

    sprintf(current, "%s:%d\n", title, index);

    // Renew result length
    res_len += strlen(current);

    // Resize result, countup alloc_times and renew max
    if( max < res_len +1 ){
      alloc_times++;
      max = alloc_len * alloc_times;
      result = PyMem_Realloc(result, sizeof(char) * max);

      if(result == NULL){
	fprintf(stderr,"Cannot reallocate memory %d\n", alloc_len);
      }
    }

    // Add newly found position to result
    strcat(result, current);

  }

  return result;
}

char *format(char *chr, int *pos, char c){

  int alloc_len = 1024;
  char *result=(char *)PyMem_Malloc(sizeof(char) * alloc_len);
  if(result == NULL){
    fprintf(stderr,"Cannot allocate memory %d\n", alloc_len);
  }
  result[0] = '\0';

  int total_str_len = 0;
  int current_memory_len = alloc_len;
  char current[256];
  int i;
  for(i = 1; i < pos[0]; i++){

    // new string
    if(c == '+'||c == '-'){
      sprintf(current, "%s:%c:%d,", chr, pos[i], '+');
    }else{
      sprintf(current, "%s:%d,", chr, pos[i]);
    }

    // Renew result length
    total_str_len += strlen(current);

    // Resize result and renew current_memory_len
    if( current_memory_len < total_str_len +1 ){

      current_memory_len += alloc_len; 

      result = PyMem_Realloc(result, sizeof(char) * current_memory_len);
      if(result == NULL){
	fprintf(stderr,"Cannot reallocate memory %d\n", alloc_len);
      }
    }
    // Add newly found position to result
    strcat(result, current);
  }

  return result;
}


char *find(char *filename, char *query, int mode){

  fprintf(stderr,"# query = %s\n", query);

  // minus strand
  char *cmpl = complement(query);
  //fprintf(stderr, "cmpl = %s\n", cmpl);

  char title[256];
  char *seq = load_UCSC_file(filename, title);   // Load

  // Return
  char *res;
  if( mode == 0){
    int *res0 = search_in_fasta_file(query, seq);      // Search
    res = format(title, res0, 0);
  }else if( mode == 1 ){
    int *res0 = search_in_fasta_file(cmpl, seq);     // Search
    res = format(title, res0, 0);
  }else{
    int *res_plus = search_in_fasta_file(query, seq);      // Search
    if( strcmp(cmpl, query) == 0 ){
      res = format(title,  res_plus, 0 );
    }
    else{
      int *res_minus = search_in_fasta_file(cmpl, seq);     // Search
      res = format(title,  res_plus, '+' );
      char *res2 = format(title,  res_minus, '-' );
      res = (char *)PyMem_Realloc( res, sizeof(char) * 
				 ( strlen(res)+strlen(res2) +1 ) );
      strcat(res, res2);
    }
  }
  PyMem_Free(seq);
  return res;

}
char *extract(char *filename, int start, int end){

  int reslen = end - start + 1;
  if(reslen<1){
    fprintf(stderr, "start pos: %d, end pos: %d, error[length: %d]\n", 
	    start, end, reslen);
    return 0;
  }

  char title[256];
  char *seq = load_UCSC_file(filename, title);   // Load

  char *result='\0';
  if( (result = (char *)PyMem_Malloc(sizeof(char)*(reslen +1)) )== NULL ){
    fprintf(stderr, "Memory allocation error\n");
    return 0;
  }
  //sprintf(result, "%s:%d-%d\t", title, start, end);
  strncpy(result, &seq[start-1], reslen);

  PyMem_Free(seq);
  return result;
}





// ======================================================
// Bridge between C and Python
// ======================================================
static PyObject*
ucsc_find(self, args)
     PyObject *self;
     PyObject *args;
{
  char *filename;
  char *query;
  int mode;

  if (!PyArg_ParseTuple(args, "ssi", &filename, &query, &mode)) {
    return NULL;
  } else {
    char *result = find(filename, query, mode);
    return Py_BuildValue("s", result);
  }

}
static PyObject*
ucsc_extract(self, args)
     PyObject *self;
     PyObject *args;
{
  char *filename;
  int start, end;

  if (!PyArg_ParseTuple(args, "sii", &filename, &start, &end)) {
    return NULL;
  } else {
    char *result = extract(filename, start, end);
    return Py_BuildValue("s", result);
  }

}



// ======================================================
// Register methods of this module
// ======================================================

static PyMethodDef
ucsc_methods[] = {
    {"find", ucsc_find, METH_VARARGS,
     "find(char *filename, char *query_seq, int mode) -> char *positions\n  mode=0->search plus strand, mode=1->minus strand search, mode=default->both strand search\n"},
    {"extract", ucsc_extract, METH_VARARGS,
     "extract(char *filename, int start_pos, int end_pos) -> char *sequence"},
    { NULL, NULL, 0, NULL}
};


// ======================================================
// Init of Module
// ======================================================
void
initucsc(void)
{
    Py_InitModule("ucsc", ucsc_methods);
}
