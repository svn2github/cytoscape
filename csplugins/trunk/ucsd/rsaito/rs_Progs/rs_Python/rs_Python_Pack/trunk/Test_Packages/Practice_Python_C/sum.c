#include <Python.h>

int sum(int x, int y){

  return x + y;

}

// ======================================================
// Bridge between C and Python
// ======================================================
static PyObject*
sum_sum(self, args)
     PyObject *self;
     PyObject *args;
{
  int x, y;

  if (!PyArg_ParseTuple(args, "ii", &x, &y)){
    return NULL;
  } else {
    int z = sum(x, y);
    return Py_BuildValue("i", z);
  }

}


// ======================================================
// Register methods of this module
// ======================================================


static PyMethodDef
sum_methods[] = {
  {"sum", sum_sum, METH_VARARGS,"This is a test..."},
  { NULL, NULL, 0, NULL}
};

// ======================================================
// Init of Module
// ======================================================
void
initsum(void)
{
  Py_InitModule("sum", sum_methods);
}
