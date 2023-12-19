package appdev.technologies.furfindspetshop.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedHashMap;

public class PostsModel
{
    public static final String TABLE_NAME           = "posts";
    public static final String COLUMN_OWNER_FK      = "owner_fk";
    public static final String COLUMN_TEXT_CONTENT  = "text_content";
    public static final String COLUMN_IMAGE_CONTENT = "image_content";
    public static final String COLUMN_DATE_CREATED  = "date_created";

    public static void buildTable(SQLiteDatabase db)
    {
        String TEXT = DBSchema.FIELD_STRING;
        String INT  = DBSchema.FIELD_INT;

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();

        fields.put(COLUMN_TEXT_CONTENT, TEXT);
        fields.put(COLUMN_IMAGE_CONTENT,TEXT);
        fields.put(COLUMN_DATE_CREATED, TEXT);          // Timestamp string i.e. 2023-10-04 10:29:16
        fields.put(COLUMN_OWNER_FK,     INT);           // Post Owner Foreign Key

        String createTableQuery = DBSchema.query_CreateTable(TABLE_NAME, fields);
        db.execSQL(createTableQuery);
    }
}
