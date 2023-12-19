package appdev.technologies.furfindspetshop.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Timestamps
{
    /**
     * Complete Date format with 3-letter abbreviation
     */
    public static String FORMAT_COMPLETE_SHORT = "EEE, d MMM yyyy";
    public static String FORMAT_TIMESTAMP_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    public static String currentTimestamp()
    {
        Date date = new Date();

        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date according to the chosen pattern
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_TIMESTAMP_DEFAULT, Locale.ENGLISH);

        // Format the current date time into a string
        return formatter.format(date);
    }

    public static String format(String timestamp, String format)
    {
        String formattedDate = "";

        // String timestamp = "2023-12-08 00:58:53";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(FORMAT_TIMESTAMP_DEFAULT);
            LocalDateTime date = LocalDateTime.parse(timestamp, inputFormatter);

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
            formattedDate = date.format(outputFormatter);
        }
        else
        {
            try
            {
                SimpleDateFormat inputFormat = new SimpleDateFormat(FORMAT_TIMESTAMP_DEFAULT, Locale.ENGLISH);
                Date date = inputFormat.parse(timestamp);

                SimpleDateFormat outputFormat = new SimpleDateFormat(format, Locale.ENGLISH);

                formattedDate = date != null ? outputFormat.format(date) : "";

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return formattedDate;
    }
}
