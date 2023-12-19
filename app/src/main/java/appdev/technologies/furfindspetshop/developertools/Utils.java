package appdev.technologies.furfindspetshop.developertools;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.Map;
import java.util.Set;

public class Utils
{
    public static void logCursor(Cursor cursor)
    {
        if (cursor.moveToFirst()) {
            do {
                StringBuilder sb = new StringBuilder();
                int numColumns = cursor.getColumnCount();
                for (int i = 0; i < numColumns; i++) {
                    String columnName = cursor.getColumnName(i);
                    String columnValue = cursor.getString(i);
                    sb.append(columnName).append(": ").append(columnValue);
                    if (i < numColumns - 1) {
                        sb.append(", ");
                    }
                }
                Log.w("logger", sb.toString());
            } while (cursor.moveToNext());
        }
    }

    /*
     * Will be used for testing the Content Values and logging them to logcat
     */
    public static void logContentValues(ContentValues contentValues)
    {
        // Get a set of all keys and values
        Set<Map.Entry<String, Object>> s = contentValues.valueSet ();

        // Loop through the set using an iterator

        for (Map.Entry<String, Object> entry : s)
        {
            // Get the current entry
            // Get the key and value
            String key = entry.getKey();
            Object value = entry.getValue();
            // Do something with the key and value
            Log.w("logger", "Key: " + key + ", Value: " + value);
        }
    }
}
