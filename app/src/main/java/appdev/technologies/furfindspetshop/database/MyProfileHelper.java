package appdev.technologies.furfindspetshop.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class MyProfileHelper
{
    private final Context context;
    private DatabaseHelper dbHelper;

    public MyProfileHelper(@Nullable Context context)
    {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public Cursor getBasicDetails(int userId)
    {
        QueryBuilder query = new QueryBuilder();
        String[] cols = {
                UsersModel.COLUMN_FIRSTNAME,
                UsersModel.COLUMN_MIDDLENAME,
                UsersModel.COLUMN_LASTNAME,
                UsersModel.COLUMN_USERNAME,
                UsersModel.COLUMN_CONTACT,
                UsersModel.COLUMN_ADDRESS,
                UsersModel.COLUMN_EMAIL
        };

        String sql = query
                .select(cols)
                .from(UsersModel.TABLE_NAME)
                .where(new String[][]{{ DBSchema.COLUMN_ID, "=", String.valueOf(userId) }})
                .limit(1)
                .build();

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Execute the query
        return db.rawQuery(sql, query.getParameterBindings());
    }
}

