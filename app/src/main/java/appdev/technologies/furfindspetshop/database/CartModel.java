package appdev.technologies.furfindspetshop.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedHashMap;

public class CartModel
{
    public static final String TABLE_NAME = "cart";
    public static final String COLUMN_PET_FK = "pet_fk";
    public static final String COLUMN_USER_FK = "user_fk";
    public static final String COLUMN_DATE_CREATED = "date_created";
    public static final String COLUMN_DATE_UPDATED = "date_updated";

    public static void buildTable(SQLiteDatabase db)
    {
        String TEXT = DBSchema.FIELD_STRING;
        String INT  = DBSchema.FIELD_INT;

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();

        fields.put(COLUMN_PET_FK,       INT);
        fields.put(COLUMN_USER_FK,      INT);
        fields.put(COLUMN_DATE_CREATED, TEXT);          // Timestamp string i.e. 2023-10-04 10:29:16
        fields.put(COLUMN_DATE_UPDATED, TEXT);          // Timestamp string i.e. 2023-10-04 10:29:16

        String createTableQuery = DBSchema.query_CreateTable(TABLE_NAME, fields);
        db.execSQL(createTableQuery);
    }
}
