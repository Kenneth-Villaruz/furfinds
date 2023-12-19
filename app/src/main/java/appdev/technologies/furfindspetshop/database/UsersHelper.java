package appdev.technologies.furfindspetshop.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

import appdev.technologies.furfindspetshop.helpers.Timestamps;

public class UsersHelper
{
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_PET_OWNER = 2;

    public static final String PREF_KEY_VERIFY_CODE = "verification_code_pref";
    public static final String PREF_KEY_USERNAME = "username_pref";

    private final Context context;
    private QueryBuilder query;
    private DatabaseHelper dbHelper;

    public UsersHelper(@Nullable Context context)
    {
        this.context = context;
        query = new QueryBuilder();
        dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * Authenticate user during Login
     * @param email
     * @param password
     * @return
     */
    public Cursor authenticate(String email, String password)
    {
        QueryBuilder query = new QueryBuilder();
        // SELECT email, password FROM users WHERE (email = ? AND password = ?) OR (username = ? AND password = ?)
        String sql = query
                .select(UsersModel.COLUMN_EMAIL, UsersModel.COLUMN_PASSWORD, UsersModel.COLUMN_USERNAME, DBSchema.COLUMN_ID)
                .from(UsersModel.TABLE_NAME)
                .where(new String[][] {{UsersModel.COLUMN_EMAIL, "=", email}, {UsersModel.COLUMN_PASSWORD, "=", password}})
                .orWhere(new String[][] {{UsersModel.COLUMN_USERNAME, "=", email}, {UsersModel.COLUMN_PASSWORD, "=", password}})
                .limit(1)
                .build();

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Execute the query
        Cursor cursor = db.rawQuery(sql, query.getParameterBindings());

        return cursor;
    }

    /**
     * User registration
     */
    public boolean register(LinkedHashMap<String, String> userData)
    {
        String timestamp = Timestamps.currentTimestamp();

        userData.put(UsersModel.COLUMN_ROLE, String.valueOf(ROLE_PET_OWNER));
        userData.put(UsersModel.COLUMN_DATE_CREATED, timestamp);
        userData.put(UsersModel.COLUMN_DATE_UPDATED, timestamp);

        Set<String> keys = userData.keySet();
        Collection<String> values = userData.values();

        QueryBuilder query = new QueryBuilder();
        String sql = query
                .insertInto(UsersModel.TABLE_NAME, keys.toArray(new String[0]))
                .values(values.toArray(new String[0]))
                .build();

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try
        {
            db.execSQL(sql, query.getParameterBindings());

            return true;
        }
        catch (Exception ex)
        {
            Log.w("logger", ex.getMessage());
            return false;
        }
    }

    public boolean identityExists(String value, String column)
    {
        QueryBuilder query = new QueryBuilder();
        String sql = query
                .select(column)
                .from(UsersModel.TABLE_NAME)
                .where(new String[][] {{column, "=", value}})
                .limit(1)
                .build();

        // Get the parameters
        ArrayList<String> parameters = query.getParameters();

        // Convert ArrayList to array
        String[] parametersArray = new String[parameters.size()];
        parameters.toArray(parametersArray);

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Execute the query
        Cursor cursor = db.rawQuery(sql, parametersArray);

        boolean exists = cursor.getCount() > 0;

        // Don't forget to close the Cursor when you're done with it
        cursor.close();

        return exists;
    }

    public String readVerificationCode(String username)
    {
        QueryBuilder query = new QueryBuilder();
        String sql = query
                .select(UsersModel.COLUMN_VERIFY_CODE)
                .from(UsersModel.TABLE_NAME)
                .where(new String[][] {{UsersModel.COLUMN_USERNAME, "=", username}})
                .limit(1)
                .build();

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try
        {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(sql, query.getParameterBindings());

            if (cursor.moveToFirst())
                return cursor.getString(0);
            else
                return "";
        }
        catch (SQLiteException ex)
        {
            // Handle the exception
            return "";
        }
        finally
        {
            if (cursor != null)
                cursor.close();

//            if (db != null)
//                db.close();
        }
    }

    public void setUserVerified(String username)
    {

    }
}

