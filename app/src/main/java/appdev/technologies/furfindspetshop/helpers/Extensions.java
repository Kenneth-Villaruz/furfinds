package appdev.technologies.furfindspetshop.helpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import appdev.technologies.furfindspetshop.R;

public class Extensions
{
    public static int TEXT_ALIGN_LEFT = Gravity.START;
    public static int TEXT_ALIGN_RIGHT = Gravity.END;
    public static int TEXT_ALIGN_CENTER = Gravity.CENTER;

    public static final String PREFS_FILE = "petshop_shared_prefs";

    /**
     * Format a float number into currency.
     * @param amount The floating point number
     * @return Formatted number with no trailing decimal places i.e 245 or 247.89
     */
    public static String toCurrency(float amount)
    {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);

        return nf.format(amount);
    }

    /**
     * Use this when storing floating point numbers into database
     * @return Floating point numeric string
     */
    public static String toNumeric(double amount)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(amount);
    }

    /**
     * Convert DP to pixels (PX)
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(Context context, int dp)
    {
//        float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dp * scale + 0.5f);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * This function checks if the password in the EditText:
     *
     * is at least 8 characters long,
     * contains at least one digit,
     * contains at least one letter (either lower case or upper case),
     * contains at least one special character from @#$%^&+=*,
     * does not contain any whitespace.
     * @param password The password
     * @return boolean
     */
    public static boolean isValidPassword(String password)
    {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=*])(?=\\S+$).{8,}$");
        return passwordPattern.matcher(password).matches();
    }

    /**
     * Generate a random number with given length
     * @param length Max number digits
     * @return String
     */
    public static String randomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // for a digit (0-9)
        }

        return sb.toString();
    }

    /**
     * Merge two String arrays
     * @param arr1 Array1
     * @param arr2 Array2
     * @return String Array
     */
    public static String[] mergeStringArrays(String[] arr1, String[] arr2)
    {
        int aLen = arr1.length;
        int bLen = arr2.length;

        String[] arr3 = new String[aLen + bLen]; // result array

        System.arraycopy(arr1, 0, arr3, 0, aLen);
        System.arraycopy(arr2, 0, arr3, aLen, bLen);

        return arr3;
    }

    public static String getAssetsDirectory() {
        return "file:///android_asset/";
    }

    public static String getAppDataPath(Context context)
    {
        File directory = context.getExternalFilesDir(null);
        String path = directory.getAbsolutePath();

        if (!path.endsWith("/"))
            path += "/";

        return path;
    }

    public static String getAuthority(Context context)
    {
        String packageName = context.getPackageName();
        String authority = packageName + ".fileprovider";

        return authority;
    }

    public static Uri getContentUri(Context context, String fileName)
    {
        File file = new File(getAppDataPath(context), fileName);

        return FileProvider.getUriForFile(context, getAuthority(context), file);
    }

    /**
     * This function prefixes each item in a string array with a given prefix
     * @param source
     * @param prefix
     * @return
     */
    public static String[] prefixItems(String[] source, String prefix) {
        for (int i = 0; i < source.length; i++) {
            source[i] = prefix + source[i];
        }
        return source;
    }

    /**
     * Builds a spinner adapter with the first item disabled
     * @param stringArray Resource id of string array
     * @return ArrayAdapter
     */
    public static ArrayAdapter<String> buildSelectMenu(Context context, int stringArray)
    {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                context.getResources().getStringArray(stringArray))
        {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (position == 0) {
                    // Gray out the text
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return arrayAdapter;
    }

    /**
     * Empty / Delete all temporary files which is the "Pictures" directory
     * in the app's data folder. These temporary files are used only when
     * registering a new pet and will become unused after.
     * @param context Context
     */
    public static void truncateTempPhotoDirectory(Context context, Runnable onFinished)
    {
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString());

        if (directory.exists() && directory.isDirectory())
        {
            String[] children = directory.list();

            if (children != null)
            {
                for (String child : children) {
                    new File(directory, child).delete();
                }
            }
        }

        if (onFinished != null)
            onFinished.run();
    }

    /**
     * Get the app's "files" directory in "Android/data/<packagename>/files"
     * @param context
     * @return
     */
    public static File getDataFilesPath(Context context)
    {
        return new File(context.getExternalFilesDir(null).toString());
    }

    /**
     * Deletes a file inside the "files" folder
     * @param context
     * @param filename
     * @param onFinished
     */
    public static void deleteFile(Context context, String filename, Runnable onFinished)
    {
        File directory = getDataFilesPath(context);

        if (directory.exists() && directory.isDirectory())
        {
            File fileToDelete = new File(directory, filename);

            if (fileToDelete.exists())
            {
                fileToDelete.delete();
            }
        }

        if (onFinished != null)
            onFinished.run();
    }

    /**
     * Read bitmap from URI
     */
    public static Bitmap readBitmapFromURI(Context context, Uri uri)
    {
        try
        {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public static Bitmap cropSquareBitmap(Bitmap imageBitmap)
    {
        Bitmap out = imageBitmap;

        // Crop the image if it's not a square
        if (imageBitmap.getHeight() > imageBitmap.getWidth()) {
            out = Bitmap.createBitmap(
                    imageBitmap,
                    0,
                    imageBitmap.getHeight() / 2 - imageBitmap.getWidth() / 2,
                    imageBitmap.getWidth(),
                    imageBitmap.getWidth()
            );
        } else if (imageBitmap.getWidth() > imageBitmap.getHeight()) {
            out = Bitmap.createBitmap(
                    imageBitmap,
                    imageBitmap.getWidth() / 2 - imageBitmap.getHeight() / 2,
                    0,
                    imageBitmap.getHeight(),
                    imageBitmap.getHeight()
            );
        }

        return out;
    }

    /**
     * Checks if a URI is null or empty
     * @param uri The URI object
     * @return boolean
     */
    public static boolean isUriEmpty(Uri uri) {
        return uri == null || uri.toString().isEmpty();
    }

    /**
     * Check if a Bitmap is valid by decoding its Uri into a bitmap first.
     * If decoding fails, then it is not a valid bitmap
     */
    public static boolean isValidBitmap(Uri uri, ContentResolver contentResolver) {
        try
        {
            InputStream inputStream = contentResolver.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Only decode image size, not the whole image
            BitmapFactory.decodeStream(inputStream, null, options);

            return options.outWidth > 0 && options.outHeight > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static void setButtonDrawableStartSelector(Button button, int drawable)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0);
        }
    }

    public static boolean StrNullOrEmpty(String input)
    {
        return input == null || input.trim().isEmpty();
    }
}


/**
 * This function cleans up (sanitizes) and removes any commas and white spaces from the string
 * before checking for multiple decimal points and parsing the cleaned string into a valid float.
 * @param numberString Input string
 * @param onInvalidNumber Callback when failed
 * @return Float number
 */
//    public static Float sanitizeCurrency(String numberString, Runnable onInvalidNumber)
//    {
//        // Remove commas and white spaces
//        String cleanNumberString = numberString.replace(",", "").replace(" ", "");
//
//        // Check for multiple decimal points
//        int dotCount = cleanNumberString.length() - cleanNumberString.replace(".", "").length();
//
//        if (dotCount > 1) {
//            onInvalidNumber.run();
//            return null;
//        }
//
//        // Parse the clean string into a float
//        NumberFormat format = NumberFormat.getInstance();
//        Number number = 0;
//
//        try {
//            number = format.parse(cleanNumberString);
//        }
//        catch (ParseException e) {
//            e.printStackTrace();
//            onInvalidNumber.run();
//            return null;
//        }
//
//        return number.floatValue();
//    }