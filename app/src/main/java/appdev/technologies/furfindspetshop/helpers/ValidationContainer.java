package appdev.technologies.furfindspetshop.helpers;

import android.view.View;

public class ValidationContainer
{
    private String   key;
    private String   value;
    private View     view;
    private String   tagName = "";

    public ValidationContainer(String key, View view, String value)
    {
        this.key = key;
        this.view = view;
        this.value = value;
    }

    public ValidationContainer(String key, View view, String value, String tagName)
    {
        this.key = key;
        this.view = view;
        this.value = value;
        this.tagName = tagName;
    }

    public String getTagName() { return tagName; }
    public String getValue() { return value; }
    public View getView() { return view; }
    public String getKey() {return key;}
}
