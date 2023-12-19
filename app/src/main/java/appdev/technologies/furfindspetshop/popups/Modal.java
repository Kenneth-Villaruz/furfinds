package appdev.technologies.furfindspetshop.popups;

import android.app.Activity;

public class Modal implements IModal
{
    private final Activity activity;

    public Modal(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void show(String message)
    {
        ModalBuilder modalBuilder = new ModalBuilder(activity);
        modalBuilder.show(message);
    }

    @Override
    public void show(String message, String title)
    {
        ModalBuilder modalBuilder = new ModalBuilder(activity);
        modalBuilder.show(message, title);
    }

    @Override
    public void show(String message, String title, int textAlignment)
    {
        ModalBuilder modalBuilder = new ModalBuilder(activity);
        modalBuilder.show(message, title, textAlignment);
    }

    @Override
    public void show(String message, String title, Runnable onPositiveAction)
    {
        ModalBuilder modalBuilder = new ModalBuilder(activity);
        modalBuilder.show(message, title, onPositiveAction);
    }

    @Override
    public void prompt(String message, String title, Runnable onPositiveAction, Runnable onNegativeAction)
    {
        ModalBuilder modalBuilder = new ModalBuilder(activity);
        modalBuilder.prompt(message, title, onPositiveAction, onNegativeAction);
    }
}
