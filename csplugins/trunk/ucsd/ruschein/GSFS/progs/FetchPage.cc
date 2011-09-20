#include <iostream>
#include <MsgUtil.h>
#include <Downloader.h>


void PrintUsage() {
	std::cerr << "usage: " << MsgUtil::GetProgName() << " url\n";
}


int main(int argc, char *argv[]) {
	if (argc != 2)
		PrintUsage();

	try {
		Downloader downloader(argv[1]);
		if (downloader.anErrorOccurred())
			std::cerr << downloader.getLastErrorMessage() << '\n';
		else
			std::cout << downloader.getMessageBody() << '\n';
	} catch (const std::exception &x) {
		std::cerr << "** Caught exception: " << x.what() << '\n';
	}
}
