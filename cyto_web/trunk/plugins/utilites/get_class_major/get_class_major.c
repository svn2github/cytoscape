/*
  File: get_class_major.c
  Author: Johannes Ruscheinski

  A utility for the extraction of Java class file major version numbers.
 */
#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>
#include <unistd.h>
#include <fcntl.h>
#include <arpa/inet.h>


void PrintUsage() {
	fprintf(stderr, "usage: get_class_major class_file_name\n");
	exit(EXIT_FAILURE);
}


int main(int argc, char *argv[]) {
	if (argc != 2)
		PrintUsage();

	const int input = open(argv[1], O_RDONLY);
	if (input == -1) {
		fprintf(stderr, "get_class_major: can't open \"%s\" for reading!\n", argv[1]);
		return EXIT_FAILURE;
	}

	uint32_t magic;
	if (read(input, &magic, sizeof magic) != sizeof magic) {
		fprintf(stderr, "get_class_major: can't read file magic from \"%s\"!\n", argv[1]);
		return EXIT_FAILURE;
	}

	const uint32_t MAGIC = htonl(0xCAFEBABEu);
	if (MAGIC != magic) {
		fprintf(stderr, "get_class_major: \"%s\" is not a Java class file!\n", argv[1]);
		return EXIT_FAILURE;
	}

	uint16_t version;
	if (read(input, &version, sizeof version) != sizeof version) {
		fprintf(stderr, "get_class_major: can't read minor version from \"%s\"!\n", argv[1]);
		return EXIT_FAILURE;
	}
	if (read(input, &version, sizeof version) != sizeof version) {
		fprintf(stderr, "get_class_major: can't read major version from \"%s\"!\n", argv[1]);
		return EXIT_FAILURE;
	}

	printf("%u\n", ntohs(version));

	return EXIT_SUCCESS;
}
