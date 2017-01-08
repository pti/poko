package fi.reuna.poko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.regex.Pattern;

public class NewBarcodeDialog extends DialogFragment {

    private static final String ARG_CODE = "code";

    EditText nameField;
    EditText codeField;

    static NewBarcodeDialog newInstance(String code) {
        NewBarcodeDialog dialog = new NewBarcodeDialog();

        Bundle args = new Bundle();
        args.putString(NewBarcodeDialog.ARG_CODE, code);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.new_title);

        View content = getActivity().getLayoutInflater().inflate(R.layout.new_barcode_view, null);
        nameField = (EditText) content.findViewById(R.id.new_name_field);
        codeField = (EditText) content.findViewById(R.id.new_code_field);
        builder.setView(content);

        codeField.setText(getArguments().getString(ARG_CODE, null));

        codeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateSaveButton();
            }
        });

        codeField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE && isValidInput()) {
                    handleSave();
                    dismiss();
                    return true;
                }

                return false;
            }
        });

        builder.setPositiveButton(R.string.edit_save_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleSave();
            }
        });

        builder.setNegativeButton(R.string.edit_cancel_label, null);

        AlertDialog alert = builder.create();

        // Needed to show the soft keyboard without having to touch the text field first.
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // AlertDialog's buttons do not seem to be accessible after create() so delay updating save button state until onResume().

        return alert;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSaveButton();
    }

    void handleSave() {
        Barcode barcode = new Barcode();
        barcode.setCreated(new Date());
        barcode.setName(nameField.getText().toString());
        barcode.setBarcodeType(BarcodeType.Code39);
        barcode.setCode(codeField.getText().toString());
        BarcodeManager.instance.addBarcode(barcode);
    }

    boolean isValidInput() {
        String code = codeField.getText().toString();
        return Pattern.compile("[A-Z0-9-+$/%]+").matcher(code).matches();
    }

    void updateSaveButton() {
        Button positiveButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setEnabled(isValidInput());
    }
}
