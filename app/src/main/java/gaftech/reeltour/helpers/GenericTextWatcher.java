package gaftech.reeltour.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Created by r.suleymanov on 03.07.2015.
 * email: ruslancer@gmail.com
 */

class GenericTextWatcher implements TextWatcher {
    protected View view;
    public  GenericTextWatcher(View view) {
        this.view = view;
    }
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    public void afterTextChanged(Editable editable) { }
}