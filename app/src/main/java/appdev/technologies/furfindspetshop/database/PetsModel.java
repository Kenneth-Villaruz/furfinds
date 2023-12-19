package appdev.technologies.furfindspetshop.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;

public class PetsModel {
    public static final String TABLE_NAME = "pets";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_SEX = "sex";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_AGE_UNITS = "age_units";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_FOR_SALE = "for_sale";
    public static final String COLUMN_FOR_ADOPTION = "for_adoption";
    public static final String COLUMN_SALE_PRICE = "sale_price";
    public static final String COLUMN_HEALTH_CONDITION = "health_condition";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DATE_CREATED = "date_created";
    public static final String COLUMN_DATE_UPDATED = "date_updated";
    public static final String COLUMN_OWNER_FK = "owner_fk";
    public static final String COLUMN_STATUS = "status";

    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_SOLD = "sold";

    public static final String AGE_DAYS     = "Days";
    public static final String AGE_WEEKS    = "Weeks";
    public static final String AGE_MONTHS   = "Months";
    public static final String AGE_YEARS    = "Years";

    public static final String GENDER_MALE = "Male";
    public static final String GENDER_FEMALE = "Female";
    public static final String GENDER_NEUTER = "Neuter";

    public static void buildTable(SQLiteDatabase db)
    {
        String TEXT = DBSchema.FIELD_STRING;
        String INT  = DBSchema.FIELD_INT;

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();

        fields.put(COLUMN_NAME,         TEXT);
        fields.put(COLUMN_CATEGORY,     TEXT);
        fields.put(COLUMN_SEX,          TEXT);
        fields.put(COLUMN_AGE,          TEXT);
        fields.put(COLUMN_AGE_UNITS,    TEXT);
        fields.put(COLUMN_DESCRIPTION,  TEXT);
        fields.put(COLUMN_FOR_SALE,     INT);
        fields.put(COLUMN_FOR_ADOPTION, INT);

        fields.put(COLUMN_SALE_PRICE,   DBSchema.FIELD_FLOAT);
        fields.put(COLUMN_HEALTH_CONDITION, TEXT);

        fields.put(COLUMN_IMAGE,        TEXT);
        fields.put(COLUMN_DATE_CREATED, TEXT);          // Timestamp string i.e. 2023-10-04 10:29:16
        fields.put(COLUMN_DATE_UPDATED, TEXT);          // Timestamp string i.e. 2023-10-04 10:29:16
        fields.put(COLUMN_OWNER_FK,     INT);           // Pet Owner Foreign Key
        fields.put(COLUMN_STATUS,       TEXT);

        String createTableQuery = DBSchema.query_CreateTable(TABLE_NAME, fields);
        db.execSQL(createTableQuery);
    }
}
