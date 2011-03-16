#!/usr/bin/env python

def cgi_html_out(html):
    # No spaces are allowed between Content-type and <html> 
    cgi_header = "Content-type: text/html\n\n"
    return cgi_header + html

if __name__ == "__main__":
    print cgi_html_out("""<html>
<head>
<title>Test title</title>
</head>
<body>
Hello!
</body>
</html>""")