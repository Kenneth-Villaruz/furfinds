package appdev.technologies.furfindspetshop.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import appdev.technologies.furfindspetshop.R;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static DatabaseHelper sInstance;

    private SQLSeeder m_seeder;
    private Context context;

    private DatabaseHelper(Context context)
    {
        super(context, DBSchema.DATABASE_NAME, null, DBSchema.DATABASE_VERSION);
        m_seeder = new SQLSeeder(context);

        this.context = context;
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Database creation code here
        UsersModel.buildTable(db);
        PetsModel.buildTable(db);
        FavoritesModel.buildTable(db);
        CartModel.buildTable(db);
        PostsModel.buildTable(db);
        PostsReactionsModel.buildTable(db);

        // Seed the database tables
        seedDatabase(db);

        // Copy the folder from "assets" into the external files directory
        copyAssets("pet_seed_photos");
        copyAssets("post_seed_photos");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Database upgrade code here
        db.execSQL(DBSchema.query_DropTable(PetsModel.TABLE_NAME));
        db.execSQL(DBSchema.query_DropTable(UsersModel.TABLE_NAME));

        onCreate(db);
    }

    private void seedDatabase(SQLiteDatabase db)
    {
        try
        {
            int usersSeed = m_seeder.seed(R.raw.users, db);
            int petsSeed = m_seeder.seed(R.raw.pets, db);
            int postsSeed = m_seeder.seed(R.raw.posts, db);
            // Toast.makeText(this, "Rows loaded from file= " + insertCount, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void copyAssets(String folderName)
    {
        AssetManager assetManager = this.context.getAssets();
        String[] files = null;
        try
        {
            files = assetManager.list(folderName);
        }
        catch (IOException e)
        {
            Log.e("logger", "Failed to get asset file list.", e);
        }

        if (files != null) for (String filename : files)
        {
            InputStream in = null;
            OutputStream out = null;
            try
            {
                in = assetManager.open(folderName + "/" + filename);
                File outFile = new File(this.context.getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            }
            catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }


    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

}
