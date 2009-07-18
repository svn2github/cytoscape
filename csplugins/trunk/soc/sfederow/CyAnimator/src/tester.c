#include <stdio.h>

#include "framesToMPEG.h"

JNIEXPORT void JNICALL Java_example_jni_HelloWorld_writeHelloWorldToStdout(JNIEnv *env, jclass c)
{
	printf("Hello World!");
}