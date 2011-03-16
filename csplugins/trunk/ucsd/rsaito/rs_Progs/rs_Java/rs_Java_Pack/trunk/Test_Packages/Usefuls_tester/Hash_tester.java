package Usefuls_tester;

import Usefuls.Hash;

class Hash_tester {
    public static void main(String args[]){
	Hash h = new Hash();
	// h.read_file(args[0], 0, 1);
	h.read_file("Data_Files/Test1/tmpfile", 0, 1);
	System.out.println(h.val("Hi"));
	// System.out.println("Hello!");
    }
}

// [rsaito@localhost Java]$ pwd
// /home/rsaito/PPI_IVV/rsIVV_Python4/Java
// [rsaito@localhost Java]$ javac Usefuls/Hash.java
// 
// [rsaito@localhost Java]$ javac Tester/Hash_tester.java
// [rsaito@localhost Java]$ java Tester/Hash_tester Tester/tmpfile
// Hello---Konnichiwa
// Hi---Yaa
// Good evening---Kombanwa
// Yaa
// [rsaito@localhost Java]$ export CLASSPATH="/home/rsaito/PPI_IVV/rsIVV_Python4/Java"
// [rsaito@localhost Java]$ cd Tester/
// [rsaito@localhost Tester]$ java Tester.Hash_tester tmpfile
// Hello---Konnichiwa
// Hi---Yaa
// Good evening---Kombanwa
// Yaa
// [rsaito@localhost Tester]$
