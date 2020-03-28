package com.Potato.hellocash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class InputSenderDialog extends AlertDialog.Builder {

    public interface InputSenderDialogListener{
        public abstract void onOK(String number);
        public abstract void onCancel(String number);
    }

    private EditText mNumberEdit;

    public InputSenderDialog(Activity activity, final InputSenderDialogListener listener) {
        super( new ContextThemeWrapper(activity, R.style.AppTheme) );

        @SuppressLint("InflateParams") // It's OK to use NULL in an AlertDialog it seems...
                View dialogLayout = LayoutInflater.from(activity).inflate(R.layout.activity_alert_input, null);
        setView(dialogLayout);

        mNumberEdit = dialogLayout.findViewById(R.id.numberEdit);

        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if( listener != null )
                    listener.onOK(String.valueOf(mNumberEdit.getText()));

            }
        });

        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if( listener != null )
                    listener.onCancel(String.valueOf(mNumberEdit.getText()));
            }
        });
    }

    public InputSenderDialog setNumber(String number){
        mNumberEdit.setText( number );
        return this;
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = super.show();
        Window window = dialog.getWindow();
        if( window != null )
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }
}