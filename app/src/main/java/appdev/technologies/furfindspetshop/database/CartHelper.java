package appdev.technologies.furfindspetshop.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.Nullable;

import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.Timestamps;

public class CartHelper
{
    private final Context context;
    private DatabaseHelper dbHelper;

    public CartHelper(@Nullable Context context)
    {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void addToCart(int userId, int petId, Runnable onSuccess, Runnable onFail)
    {
        String timestamp = Timestamps.currentTimestamp();

        QueryBuilder query = new QueryBuilder();
        String[] cols = {
                CartModel.COLUMN_USER_FK,
                CartModel.COLUMN_PET_FK,
                CartModel.COLUMN_DATE_CREATED,
                CartModel.COLUMN_DATE_UPDATED
        };

        String[] values = { String.valueOf(userId), String.valueOf(petId), timestamp, timestamp };

        String sql = query
                .insertInto(CartModel.TABLE_NAME, cols)
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

    public void removeFromCart(int userId, int petId, Runnable onSuccess, Runnable onFail)
    {
        QueryBuilder query = new QueryBuilder();

        String sql = query
                .deleteFrom(CartModel.TABLE_NAME)
                .where(new String[][]{{ CartModel.COLUMN_USER_FK, "=", String.valueOf(userId)}, {CartModel.COLUMN_PET_FK, "=", String.valueOf(petId)}})
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

    public boolean existsInCart(int userId, int petId)
    {
        QueryBuilder query = new QueryBuilder();

        String sql = query
                .select(DBSchema.COLUMN_ID)
                .from(CartModel.TABLE_NAME)
                .where(new String[][]{{ CartModel.COLUMN_USER_FK, "=", String.valueOf(userId)}, {CartModel.COLUMN_PET_FK, "=", String.valueOf(petId)}})
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

    public Cursor getCartItems(int userId)
    {
        QueryBuilder query = new QueryBuilder();
        String sql = buildSelectQuery(query, String.valueOf(userId));

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Execute the query
        return db.rawQuery(sql, query.getParameterBindings());
    }

//    public int countCartItems(int userId)
//    {
//        QueryBuilder query = new QueryBuilder();
//        String sql = query
//                .selectCount(DBSchema.COLUMN_ID)
//                .from(CartModel.TABLE_NAME)
//                .where(new String[][]{{ CartModel.COLUMN_USER_FK, "=", String.valueOf(userId) }})
//                .build();
//
//        int count = 0;
//
//        // Get a reference to the database
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = null;
//
//        try
//        {
//            // Execute the query
//            cursor = db.rawQuery(sql, query.getParameterBindings());
//
//            if (cursor.moveToFirst())
//                count = cursor.getInt(0);
//
//            return count;
//        }
//        catch (SQLiteException ex)
//        {
//            return count;
//        }
//        finally {
//            if (cursor != null)
//                cursor.close();
//        }
//    }

    /**
     * select
     *      p.name,
     *      p.category,
     *      p.image
     * from cart as c
     * left join pets as p on p.id = c.pet_fk
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

        String cart_table   = CartModel.TABLE_NAME + " AS c";
        String pets_table   = PetsModel.TABLE_NAME + " AS p";
        String cart_join    = "c." + CartModel.COLUMN_PET_FK;
        String user_fk      = "c." + CartModel.COLUMN_USER_FK;
        String petIdCol     = "p." + DBSchema.COLUMN_ID;

        String sql = query
                .select(selectFields)
                .from(cart_table)
                .leftJoin(pets_table, petIdCol, cart_join)
                .where(new String[][]{{ user_fk, "=", userId }})
                .build();

        return sql;
    }
}