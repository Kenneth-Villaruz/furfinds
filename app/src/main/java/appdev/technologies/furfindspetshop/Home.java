package appdev.technologies.furfindspetshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.MyPetsHelper;
import appdev.technologies.furfindspetshop.database.PostsModel;
import appdev.technologies.furfindspetshop.database.PostsHelper;
import appdev.technologies.furfindspetshop.database.PostsModel;
import appdev.technologies.furfindspetshop.database.UsersModel;
import appdev.technologies.furfindspetshop.helpers.AsyncWorker;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.recyclerviews.MyPetsAdapter;
import appdev.technologies.furfindspetshop.recyclerviews.MyPetsItem;
import appdev.technologies.furfindspetshop.recyclerviews.PostsAdapter;
import appdev.technologies.furfindspetshop.recyclerviews.PostsItem;

public class Home extends BaseActivityBottomNav
{
    private FloatingActionButton fab_addPost;
    private RelativeLayout loadingDialog;

    private RecyclerView postsRecyclerView;
    private List<PostsItem> postsRecyclerDataset;
    private PostsAdapter postsAdapter;

    private TextView emptyPostLabel;

    private PostsHelper postsHelper;
    private UserAuth userAuth;
    private AsyncWorker asyncWorker;

    @Override
    public void onAwake()
    {
        this.fitSystemWindows = false;
        asyncWorker = new AsyncWorker(this);
        postsHelper = new PostsHelper(this);
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_home);

        setupBottomNavMenu(R.id.bottom_nav_menu);
        setActiveNavItem(R.id.nav_item_home);

        loadingDialog  = findViewById(R.id.loading_spinner_dialog);

        // Setup the Floating Action Button's appearance (FAB)
        fab_addPost = findViewById(R.id.fab_add_post);
        int fabColor = getResources().getColor(R.color.primary_dark);
        ColorStateList fabColorStateList = ColorStateList.valueOf(fabColor);
        fab_addPost.setBackgroundTintList(fabColorStateList);

        emptyPostLabel = findViewById(R.id.empty_posts);

        postsRecyclerView = findViewById(R.id.posts_recycler);
        postsRecyclerDataset = new ArrayList<>();
        postsAdapter = new PostsAdapter(postsRecyclerDataset);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setAdapter(postsAdapter);

        asyncWorker.Run(() ->
        {
            // Run these block of code in the background

            // Show a "Loading" dialog
            runOnUiThread(() -> loadingDialog.setVisibility(View.VISIBLE));

            // Get the information of the currently logged on user
            userAuth = AuthSharedPrefsManager.getInstance(this).getUser();
        },
        () ->
        {
            // Run these block of code on UI thread
            getPosts();
        });
    }

    @Override
    public void bindEvents()
    {

    }

    @Override
    protected void onBackKeyPressed()
    {

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        asyncWorker.Kill();
    }

    @SuppressLint("Range")
    private void getPosts()
    {
        final int[] resultCount = {0};

        asyncWorker.Run(() ->   // Do the heavy processing / long running task in the background
        {
            Cursor cursor = postsHelper.getPosts();
            resultCount[0] = cursor.getCount();

            while (cursor.moveToNext())
            {
                String postContent  = cursor.getString( cursor.getColumnIndex(PostsModel.COLUMN_TEXT_CONTENT) );
                String postDate     = cursor.getString( cursor.getColumnIndex(PostsModel.COLUMN_DATE_CREATED) );
                String postImage    = cursor.getString( cursor.getColumnIndex(PostsModel.COLUMN_IMAGE_CONTENT) );
                String postOwner    = cursor.getString( cursor.getColumnIndex(PostsModel.COLUMN_OWNER_FK));
                String postId       = cursor.getString( cursor.getColumnIndex(DBSchema.COLUMN_ID) );

                String ownerFname   = cursor.getString( cursor.getColumnIndex(UsersModel.COLUMN_FIRSTNAME));
                String ownerMname   = cursor.getString( cursor.getColumnIndex(UsersModel.COLUMN_MIDDLENAME));
                String ownerLname   = cursor.getString( cursor.getColumnIndex(UsersModel.COLUMN_LASTNAME));

                PostsItem item = new PostsItem();
                item.setTextContent(postContent);
                item.setDate(postDate);
                item.setImageContent(postImage);
                item.setOwnerFk(Integer.parseInt(postOwner));
                item.setId(Integer.parseInt(postId));
                item.setOwnerName(String.join(" ", new String[] {
                        ownerFname, ownerMname, ownerLname
                }));
                postsRecyclerDataset.add(item);
            }
        },
        // Invoke on UI thread (Run on UI) after the background task is done
        () ->
        {
            if (resultCount[0] > 0)
            {
                // Hide the "Empty Posts" text
                emptyPostLabel.setVisibility(View.INVISIBLE);

                // Update the recycler view to show the newly added item
                postsAdapter.notifyItemInserted(postsRecyclerDataset.size() - 1);
            }
            else
                // Show the "Empty Posts" text when there are no posts
                emptyPostLabel.setVisibility(View.VISIBLE);

            // Finally, hide the "Loading" dialog after completing the long-running tasks
            loadingDialog.setVisibility(View.INVISIBLE);
        });
    }
}