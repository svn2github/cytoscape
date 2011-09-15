#include <iostream>
#include <MsgUtil.h>


void PrintUsage() {
	std::cerr << "usage: " << MsgUtil::GetProgName() << " url\n";
}


int main(int argc, char *argv[]) {
	if (argc != 2)
		PrintUsage();

	std::cout << "Got URL: " << argv[1] << '\n';
}
