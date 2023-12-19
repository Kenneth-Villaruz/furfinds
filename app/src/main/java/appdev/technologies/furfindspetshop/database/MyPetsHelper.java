package appdev.technologies.furfindspetshop.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyPetsHelper
{
    private final Context context;
    private DatabaseHelper dbHelper;

    public MyPetsHelper(@Nullable Context context)
    {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public Cursor getPets(int userId)
    {
        QueryBuilder query = new QueryBuilder();
        String[] cols = {
                DBSchema.COLUMN_ID,
                PetsModel.COLUMN_NAME,
                PetsModel.COLUMN_CATEGORY,
                PetsModel.COLUMN_IMAGE,
                PetsModel.COLUMN_DESCRIPTION
        };

        String sql = query
                .select(cols)
                .from(PetsModel.TABLE_NAME)
                .where(new String[][]{{ PetsModel.COLUMN_OWNER_FK, "=", String.valueOf(userId) }})
                .orderby(PetsModel.COLUMN_NAME, "ASC")
                .build();

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Execute the query
        return db.rawQuery(sql, query.getParameterBindings());
    }

    public int countPets(int userId)
    {
        QueryBuilder query = new QueryBuilder();
        String sql = query
                .selectCount(DBSchema.COLUMN_ID)
                .from(PetsModel.TABLE_NAME)
                .where(new String[][]{{ PetsModel.COLUMN_OWNER_FK, "=", String.valueOf(userId) }})
                .build();

        int count = 0;

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try
        {
            // Execute the query
            cursor = db.rawQuery(sql, query.getParameterBindings());

            if (cursor.moveToFirst())
                count = cursor.getInt(0);

            return count;
        }
        catch (SQLiteException ex)
        {
            return count;
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public boolean deletePet(int petId)
    {
        QueryBuilder query = new QueryBuilder();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String str_petId = String.valueOf(petId);

        String sql = query.deleteFrom(PetsModel.TABLE_NAME).where(new String[][]{{ DBSchema.COLUMN_ID, "=", str_petId }}).build();

        try
        {
            db.beginTransaction();

            // Delete the pet from the records
            db.execSQL(sql, query.getParameterBindings());

            // Delete the pet from everyone's favorites
            query = new QueryBuilder();
            sql = query.deleteFrom(FavoritesModel.TABLE_NAME).where(new String[][]{{ FavoritesModel.COLUMN_PET_FK, "=", str_petId }}).build();

            db.execSQL(sql, query.getParameterBindings());

            db.setTransactionSuccessful();

            return true;
        }
        catch (SQLiteException ex)
        {
            return false;
        }
        finally {
            // End the transaction. If setTransactionSuccessful() was not called, this will rollback.
            db.endTransaction();
        }
    }
}
