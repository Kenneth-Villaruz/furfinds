package appdev.technologies.furfindspetshop.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.Nullable;

import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.Timestamps;

public class FavoritesHelper
{
    private final Context context;
    private DatabaseHelper dbHelper;

    public FavoritesHelper(@Nullable Context context)
    {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void addToFavorites(int userId, int petId, Runnable onSuccess, Runnable onFail)
    {
        String timestamp = Timestamps.currentTimestamp();

        QueryBuilder query = new QueryBuilder();
        String[] cols = {
                FavoritesModel.COLUMN_USER_FK,
                FavoritesModel.COLUMN_PET_FK,
                FavoritesModel.COLUMN_DATE_CREATED,
                FavoritesModel.COLUMN_DATE_UPDATED
        };

        String[] values = { String.valueOf(userId), String.valueOf(petId), timestamp, timestamp };

        String sql = query
                .insertInto(FavoritesModel.TABLE_NAME, cols)
                .values(values)
                .build();

        try
        {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(sql, query.getParameterBindings());

            if (onSuccess != null)
                onSuccess.run();
        }
        catch (Exception ex)
        {
            Log.w("logger", ex.getMessage());
            if (onFail != null)
                onFail.run();
        }
    }

    public void removeFromFavorites(int userId, int petId, Runnable onSuccess, Runnable onFail)
    {
        QueryBuilder query = new QueryBuilder();

        String sql = query
                .deleteFrom(FavoritesModel.TABLE_NAME)
                .where(new String[][]{{ FavoritesModel.COLUMN_USER_FK, "=", String.valueOf(userId)}, {FavoritesModel.COLUMN_PET_FK, "=", String.valueOf(petId)}})
                .build();

        try
        {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            db.execSQL(sql, query.getParameterBindings());

            if (onSuccess != null)
                onSuccess.run();
        }
        catch (Exception ex)
        {
            Log.w("logger", ex.getMessage());

            if (onFail != null)
                onFail.run();
        }
    }

    public boolean existsInFavorites(int userId, int petId)
    {
        QueryBuilder query = new QueryBuilder();

        String sql = query
                .select(DBSchema.COLUMN_ID)
                .from(FavoritesModel.TABLE_NAME)
                .where(new String[][]{{ FavoritesModel.COLUMN_USER_FK, "=", String.valueOf(userId)}, {FavoritesModel.COLUMN_PET_FK, "=", String.valueOf(petId)}})
                .build();

        try
        {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, query.getParameterBindings());

            return cursor.moveToFirst();
        }
        catch (Exception ex)
        {
            return true;
        }
    }

    public Cursor getFavorites(int userId)
    {
        QueryBuilder query = new QueryBuilder();
        String sql = buildSelectQuery(query, String.valueOf(userId));

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Execute the query
        return db.rawQuery(sql, query.getParameterBindings());
    }

    public int countFavorites(int userId)
    {
        QueryBuilder query = new QueryBuilder();
        String sql = query
                .selectCount(DBSchema.COLUMN_ID)
                .from(FavoritesModel.TABLE_NAME)
                .where(new String[][]{{ FavoritesModel.COLUMN_USER_FK, "=", String.valueOf(userId) }})
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

    /**
     * select
     *      p.name,
     *      p.category,
     *      p.image
     * from favorites as f
     * left join pets as p on p.id = f.pet_fk
     * where f.user_fk = ?
     * @return
     */
    private String buildSelectQuery(QueryBuilder query, String userId)
    {
        String[] selectFields = Extensions.prefixItems(new String[] {
                DBSchema.COLUMN_ID,
                PetsModel.COLUMN_NAME,
                PetsModel.COLUMN_CATEGORY,
                PetsModel.COLUMN_IMAGE,
                PetsModel.COLUMN_SALE_PRICE
        }, "p.");

        String fav_table    = FavoritesModel.TABLE_NAME + " AS f";
        String pets_table   = PetsModel.TABLE_NAME + " AS p";
        String fav_join     = "f." + FavoritesModel.COLUMN_PET_FK;
        String user_fk      = "f." + FavoritesModel.COLUMN_USER_FK;
        String petIdCol     = "p." + DBSchema.COLUMN_ID;

        String sql = query
                .select(selectFields)
                .from(fav_table)
                .leftJoin(pets_table, petIdCol, fav_join)
                .where(new String[][]{{ user_fk, "=", userId }})
                .build();

        return sql;
    }
}
