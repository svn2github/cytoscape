package fgraph.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
{
    public String format(LogRecord r)
    {
        StringBuffer b = new StringBuffer();
        b.append(r.getLevel());
        b.append(" ");
        b.append(r.getMessage());
        b.append("\n");
        return b.toString();
    }
}
