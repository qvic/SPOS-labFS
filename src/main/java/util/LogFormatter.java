package util;

import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {
    private static final String FORMAT = "%2$-7s %3$s %n";

    @Override
    public String format(LogRecord record) {
        return String.format(FORMAT,
                new Date(record.getMillis()),
                record.getLevel().getLocalizedName(),
                record.getMessage()
        );
    }
}
