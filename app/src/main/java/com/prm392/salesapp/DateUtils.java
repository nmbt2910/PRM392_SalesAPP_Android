package com.prm392.salesapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    /**
     * Try to parse common ISO-8601 date/time representations and return a
     * nicely formatted local date/time string. Falls back to the original
     * input if parsing fails.
     */
    public static String formatIsoToLocal(String iso) {
        if (iso == null || iso.trim().isEmpty()) return "-";

        String[] patterns = new String[]{
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String p : patterns) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(p, Locale.getDefault());
                // assume incoming times with trailing Z or no offset are UTC
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date d = parser.parse(iso);
                if (d != null) {
                    SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    out.setTimeZone(TimeZone.getDefault());
                    return out.format(d);
                }
            } catch (ParseException | IllegalArgumentException ignored) {
                // try next pattern
            }
        }

        // Could not parse — return original string so user still sees data
        return iso;
    }
}
