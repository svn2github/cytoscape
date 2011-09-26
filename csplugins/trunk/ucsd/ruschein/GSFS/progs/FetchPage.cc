#include <iostream>
#include <MsgUtil.h>
#include <Downloader.h>


void PrintUsage() {
	std::cerr << "usage: " << MsgUtil::GetProgName() << " url [http_header1 http_header2 ... http_headerN]\n";
}


int main(int argc, char *argv[]) {
	if (argc < 2)
		PrintUsage();

	try {
//		Downloader::setDebugMode(true);
		SList<std::string> http_headers;
		for (int i = 2; i < argc; ++i)
			http_headers.push_back(std::string(argv[i]));

		Downloader downloader(Url(argv[1]), http_headers);
//		std::cerr << "Header: " << downloader.getMessageHeader() << '\n';
		if (downloader.anErrorOccurred())
			std::cerr << downloader.getLastErrorMessage() << '\n';
		else
			std::cout << downloader.getMessageBody() << '\n';
	} catch (const std::exception &x) {
		std::cerr << "** Caught exception: " << x.what() << '\n';
	}
}
