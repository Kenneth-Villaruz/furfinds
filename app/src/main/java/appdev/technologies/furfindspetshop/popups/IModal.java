package appdev.technologies.furfindspetshop.popups;

public interface IModal
{
    void show(String message);
    void show(String message, String title);
    void show(String message, String title, int textAlignment);
    void show(String message, String title, Runnable onPositiveAction);
    void prompt(String message, String title, Runnable onPositiveAction, Runnable onNegativeAction);
}
