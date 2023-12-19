package appdev.technologies.furfindspetshop.database;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import appdev.technologies.furfindspetshop.Home;
import appdev.technologies.furfindspetshop.helpers.Extensions;

public class PostsHelper
{
    private final Context context;
    private DatabaseHelper dbHelper;

    public PostsHelper(@Nullable Context context)
    {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public Cursor getPosts()
    {
        QueryBuilder query = new QueryBuilder();
        String[] posts_cols = Extensions.prefixItems(new String[]
        {
                DBSchema.COLUMN_ID,
                PostsModel.COLUMN_TEXT_CONTENT,
                PostsModel.COLUMN_DATE_CREATED,
                PostsModel.COLUMN_IMAGE_CONTENT,
                PostsModel.COLUMN_OWNER_FK
        }, "p.");

        String[] users_cols = Extensions.prefixItems(new String[]
        {
                UsersModel.COLUMN_FIRSTNAME,
                UsersModel.COLUMN_MIDDLENAME,
                UsersModel.COLUMN_LASTNAME
        }, "u.");

        String[] cols = Extensions.mergeStringArrays(posts_cols, users_cols);

        String posts_table  = PostsModel.TABLE_NAME + " AS p";
        String users_table  = UsersModel.TABLE_NAME + " AS u";
        String users_join   = "u." + DBSchema.COLUMN_ID;
        String posts_join   = "p." + PostsModel.COLUMN_OWNER_FK;

        String sql = query
                .select(cols)
                .from(posts_table)
                .leftJoin(users_table, users_join, posts_join)
                //.where(new String[][]{{ PostsModel.COLUMN_OWNER_FK, "=", String.valueOf(userId) }})

                // Show the latest posts
                .orderby("p." + PostsModel.COLUMN_DATE_CREATED, "DESC")

                // limit the results by 30
                .limit(30)
                .build();

        // Get a reference to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Execute the query
        return db.rawQuery(sql, query.getParameterBindings());
    }
}
