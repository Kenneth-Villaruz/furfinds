package appdev.technologies.furfindspetshop.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedHashMap;

public class PostsReactionsModel
{
    public static final String TABLE_NAME           = "posts_reactions";
    public static final String COLUMN_REACTOR_FK    = "reactor_fk";
    public static final String COLUMN_POSTS_FK      = "posts_fk";
    public static final String COLUMN_COMMENT       = "comment";
    public static final String COLUMN_LIKE          = "like";
    public static final String COLUMN_DATE_CREATED  = "date_created";

    public static void buildTable(SQLiteDatabase db)
    {
        String TEXT = DBSchema.FIELD_STRING;
        String INT  = DBSchema.FIELD_INT;

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();

        fields.put(COLUMN_REACTOR_FK,   INT);           // Reactor of the Post's Foreign Key
        fields.put(COLUMN_POSTS_FK,     INT);           // The post id
        fields.put(COLUMN_COMMENT,      TEXT);
        fields.put(COLUMN_LIKE,         INT);
        fields.put(COLUMN_DATE_CREATED, TEXT);          // Timestamp string i.e. 2023-10-04 10:29:16

        String createTableQuery = DBSchema.query_CreateTable(TABLE_NAME, fields);
        db.execSQL(createTableQuery);
    }
}
