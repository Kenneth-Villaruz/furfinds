package appdev.technologies.furfindspetshop.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SQLSeeder
{
    private Context context;

    public SQLSeeder(Context context)
    {
        this.context = context;
    }

    public int seed(int resourceId, SQLiteDatabase db) throws IOException
    {
        // Resetting Counter
        int result = 0;

        // Open the resource
        InputStream insertsStream = context.getResources().openRawResource(resourceId);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        // Iterate through lines (assuming each insert has its own line and there's no other stuff)
        StringBuilder statement = new StringBuilder();

        while (insertReader.ready()) {
            String line = insertReader.readLine();
            statement.append(line);
            if (line.endsWith(";")) {
                db.execSQL(statement.toString());
                statement = new StringBuilder();
                result++;
            }
        }

        insertReader.close();

        // Returning number of inserted rows
        return result;
    }
}
