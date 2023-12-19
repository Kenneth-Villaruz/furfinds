package appdev.technologies.furfindspetshop.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class DBSchema
{
    public static final String DATABASE_NAME = "petshop.db";
    public static int DATABASE_VERSION = 1;

    public static final String COLUMN_ID = "id";
    public static final String TABLE_AUTO_INCREMENT = "INTEGER PRIMARY KEY AUTOINCREMENT";

    // SQLite does not support Boolean TRUE and FALSE, but uses integer instead.
    // FALSE will be 0 and TRUE will be 1
    public static final int DATA_FALSE = 0;
    public static final int DATA_TRUE  = 1;

    public static final String FIELD_STRING = "TEXT";
    public static final String FIELD_INT    = "INTEGER";
    public static final String FIELD_FLOAT  = "NUMERIC";

    public static String mapYesNo(String value)
    {
        if (value != null && value.trim().equals("No"))
            return String.valueOf(DATA_FALSE);

        if (value != null && value.trim().equals("Yes"))
            return String.valueOf(DATA_TRUE);

        return "";
    }

    /**
     * This function help us to build tables easily without the headache of concatenations.
     * We will just create a Dictionary Hashtable to create the required column names (keys)
     * having its values as column properties (type, constraint, etc). Sample Usage:
     *<pre>
     * {@code
     * Dictionary<String, String> fields = new Hashtable<>();
     * fields.put("user_name", "VARCHAR(255)");
     *
     * String createTableQuery = DBShared.CreateTable("table1", fields);
     * }
     *</pre>
     *
     * @param table The name of the table
     * @param schema The structure of the fields
     * @return Query for CREATE TABLE
     */

    public static String query_CreateTable(String table, LinkedHashMap<String, String> schema)
    {
        // Use String Builder instead of concatenation
        StringBuilder query = new StringBuilder(String.format("CREATE TABLE %s (%s %s", table, COLUMN_ID, TABLE_AUTO_INCREMENT));

        // Get the LinkedHashMap keys
        for(Map.Entry<String,String> entry : schema.entrySet())
        {
            String columnName = entry.getKey();
            String columnProperties = entry.getValue();
            query.append(", ").append(columnName).append(" ").append(columnProperties);
        }

        // Close the query with semi colon
        query.append(");");

        // The output
        return query.toString();
    }


    /**
     * Query for dropping a table
     * @param table
     * @return
     */
    public static String query_DropTable(String table)
    {
        return "DROP TABLE IF EXISTS " + table;
    }

    public static void dropDatabase(Context context)
    {
        context.deleteDatabase(DATABASE_NAME);
        DATABASE_VERSION = 1;
    }
}
