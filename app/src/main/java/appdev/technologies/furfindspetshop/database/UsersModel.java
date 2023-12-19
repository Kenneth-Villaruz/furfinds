package appdev.technologies.furfindspetshop.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;

public class UsersModel {
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_MIDDLENAME = "middlename";
    public static final String COLUMN_LASTNAME = "lastname";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_VERIFIED_AT = "email_verified_at";
    public static final String COLUMN_VERIFY_CODE = "verify_code";
    public static final String COLUMN_DATE_CREATED = "date_created";
    public static final String COLUMN_DATE_UPDATED = "date_updated";

    public static void buildTable(SQLiteDatabase db)
    {
        String TEXT = DBSchema.FIELD_STRING;
        String INT  = DBSchema.FIELD_INT;

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();

        fields.put(COLUMN_FIRSTNAME     , TEXT);
        fields.put(COLUMN_MIDDLENAME    , TEXT);
        fields.put(COLUMN_LASTNAME      , TEXT);
        fields.put(COLUMN_USERNAME      , TEXT);
        fields.put(COLUMN_EMAIL         , TEXT);
        fields.put(COLUMN_PASSWORD      , TEXT);
        fields.put(COLUMN_CONTACT       , TEXT);
        fields.put(COLUMN_ADDRESS       , TEXT);
        fields.put(COLUMN_ROLE          , INT);
        fields.put(COLUMN_IMAGE         , TEXT);
        fields.put(COLUMN_VERIFIED_AT   , TEXT);
        fields.put(COLUMN_VERIFY_CODE   , TEXT);
        fields.put(COLUMN_DATE_CREATED  , TEXT);
        fields.put(COLUMN_DATE_UPDATED  , TEXT);

        String createTableQuery = DBSchema.query_CreateTable(TABLE_NAME, fields);
        db.execSQL(createTableQuery);
    }
}
