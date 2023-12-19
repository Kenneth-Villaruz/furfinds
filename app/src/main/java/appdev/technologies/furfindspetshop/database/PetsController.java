package appdev.technologies.furfindspetshop.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import appdev.technologies.furfindspetshop.helpers.Extensions;

public class PetsController
{
    private final Context context;
    private QueryBuilder query;
    private DatabaseHelper dbHelper;

    public PetsController(@Nullable Context context)
    {
        this.context = context;
        query = new QueryBuilder();
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public Cursor getPetsForYou()
    {
        String sql = query
                    .select(DBSchema.COLUMN_ID,
                            PetsModel.COLUMN_NAME,
                            PetsModel.COLUMN_SALE_PRICE,
                            PetsModel.COLUMN_FOR_ADOPTION,
                            PetsModel.COLUMN_FOR_SALE,
                            PetsModel.COLUMN_IMAGE,
                            PetsModel.COLUMN_OWNER_FK)
                    .from(PetsModel.TABLE_NAME)
                    .where(new String[][]{{ PetsModel.COLUMN_STATUS, "=", PetsModel.STATUS_AVAILABLE }})
                    .orderby(PetsModel.COLUMN_DATE_CREATED, "DESC")
                    .limit(19)
                    .build();
        //Log.w("logger", sql);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;

        if(db != null) {
            cursor = db.rawQuery(sql, query.getParameterBindings());
        }

        return cursor;
    }

    public Cursor findPets(String searchTerm, String[] extraColumns)
    {
        QueryBuilder query = new QueryBuilder();

        String[] fields = {
                DBSchema.COLUMN_ID, PetsModel.COLUMN_NAME, PetsModel.COLUMN_SALE_PRICE, PetsModel.COLUMN_IMAGE,
                PetsModel.COLUMN_FOR_SALE, PetsModel.COLUMN_FOR_ADOPTION, PetsModel.COLUMN_OWNER_FK
        };

        if (extraColumns != null)
            fields = Extensions.mergeStringArrays(fields, extraColumns);

        String sql = query
                .select(fields)
                .from(PetsModel.TABLE_NAME)
                .where(new String[][]{{ PetsModel.COLUMN_NAME, "LIKE", "%" + searchTerm + "%" }, { PetsModel.COLUMN_STATUS, "=", PetsModel.STATUS_AVAILABLE }})
                .build();

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Execute the query
        return db.rawQuery(sql, query.getParameterBindings());
    }
}
