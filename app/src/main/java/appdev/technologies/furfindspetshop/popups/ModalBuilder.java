package appdev.technologies.furfindspetshop.popups;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.helpers.Extensions;

public class ModalBuilder implements IModal
{
    private final Activity activity;

    private final String   DEFAULT_MODAL_TITLE;
    private final int      BUTTON_ACTION_POSITIVE = 0;
    private final int      BUTTON_ACTION_NEGATIVE = -1;

    private RelativeLayout modalRoot = null;

    private Button positiveButton    = null;
    private Button negativeButton    = null;

    private TextView titleText       = null;
    private TextView messageText     = null;

    public ModalBuilder(Activity activity)
    {
        this.activity = activity;

        DEFAULT_MODAL_TITLE = "Alert";

        initializeViews();
        bindEvents();
    }

    private void initializeViews()
    {
        // Find references to views
        modalRoot      = activity.findViewById(R.id.frame_modal_root);
        positiveButton = activity.findViewById(R.id.frame_modal_btn_ok);
        negativeButton = activity.findViewById(R.id.frame_modal_btn_cancel);
        messageText    = activity.findViewById(R.id.message_textview);
        titleText      = activity.findViewById(R.id.title_textview);

        messageText.setGravity(Extensions.TEXT_ALIGN_CENTER);
    }

    private void bindEvents()
    {
        // Handle interactions here such as click, etc..

        // Always close the dialog after clicking either the OK or Cancel button
        bindDefaultNegativeAction();
        bindDefaultPositiveAction();
    }

    //
    // Just a simple alert dialog that only shows the message, no other properties
    //
    public void show(String message)
    {
        prepareModalContent(message, null, false);
        displayModal();
    }
    //
    // Shows the alert dialog with message and title
    //
    public void show(String message, String title)
    {
        prepareModalContent(message, title, false);
        displayModal();
    }
    //
    // Shows the alert dialog with message having specific text alignment and a title
    //
    public void show(String message, String title, int textAlignment)
    {
        prepareModalContent(message, title, false);
        messageText.setGravity(textAlignment);
        displayModal();
    }
    //
    // Shows the alert dialog with message, title and a handler
    // that executes an action when the positive (OK) button is clicked
    //
    public void show(String message, String title, Runnable onPositiveAction)
    {
        prepareModalContent(message, title, false);

        // Execute the action when positive button was clicked
        positiveButton.setOnClickListener(view -> executeAction(onPositiveAction, BUTTON_ACTION_POSITIVE));

        displayModal();
    }
    //
    // Shows the alert dialog with message, title and a handler
    // that executes an action when the positive (OK) button is clicked.
    // Also, another handler for negative button (Cancel) was added
    //
    public void prompt(String message, String title, Runnable onPositiveAction, Runnable onNegativeAction)
    {
        // Always show the negative button.
        prepareModalContent(message, title, true);

        // Execute the action when positive button was clicked
        positiveButton.setOnClickListener(view -> executeAction(onPositiveAction, BUTTON_ACTION_POSITIVE));

        // If the negative action is NOT null, we expect an action and execute it
        if (onNegativeAction != null)
            negativeButton.setOnClickListener(view -> executeAction(onNegativeAction, BUTTON_ACTION_NEGATIVE));

        displayModal();
    }
    //
    // Create the contents of modal
    //
    private void prepareModalContent(String message, String title, boolean showNegativeButton)
    {
        messageText.setText(message);

        String modalTitle = (title != null) ? title : DEFAULT_MODAL_TITLE;
        titleText.setText(modalTitle);

        if (showNegativeButton)
            showNegativeButton();
        else
            hideNegativeButton();
    }
    //
    // Set the default action when the positive
    // or negative buttons were clicked
    //
    private void bindDefaultPositiveAction()
    {
        positiveButton.setOnClickListener(view -> hideModal());
    }
    private void bindDefaultNegativeAction()
    {
        negativeButton.setOnClickListener(view -> hideModal());
    }
    //
    // Execute the action, close the modal, then rebind its default action
    //
    private void executeAction(Runnable action, int actionType)
    {
        if (action == null)
            return;

        action.run();
        hideModal();

        if (actionType == BUTTON_ACTION_POSITIVE)
            bindDefaultPositiveAction();

        else if (actionType == BUTTON_ACTION_NEGATIVE)
            bindDefaultNegativeAction();
    }
    //
    // Show the modal
    //
    private void displayModal()
    {
        modalRoot.setVisibility(View.VISIBLE);
    }
    //
    // Hide the modal
    //
    private void hideModal()
    {
        modalRoot.setVisibility(View.INVISIBLE);
    }
    //
    // Hide the negative (Cancel) button
    //
    private void hideNegativeButton()
    {
        this.negativeButton.setVisibility(View.INVISIBLE);
    }
    //
    // Show the negative (Cancel) button
    //
    private void showNegativeButton()
    {
        this.negativeButton.setVisibility(View.VISIBLE);
    }
}
