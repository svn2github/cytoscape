from distutils.core import setup, Extension

setup(name="sum",
            version="1.0",
            ext_modules=[Extension('sum', ['sum.c'])])
