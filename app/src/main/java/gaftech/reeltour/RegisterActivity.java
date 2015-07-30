package gaftech.reeltour;

import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {
    @Override
    public void onClick(View v) {

    }
    /*
    UsersDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new UsersDatabase(this);
        Button regSave = (Button)findViewById(R.id.regSave);
        regSave.setOnClickListener(this);
    }

    public boolean validate(String email, String password, String name) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) return false;
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regSave:
                EditText nt = (EditText) findViewById(R.id.nameRegText);
                EditText et = (EditText) findViewById(R.id.emailRegText);
                EditText pt = (EditText) findViewById(R.id.passwordRegText);
                String email  = et.getText().toString();
                String name   = nt.getText().toString();
                String pass = pt.getText().toString();
                TextView rv = (TextView) findViewById(R.id.regErrorView);
                if (this.validate(email, pass, name)) {
                    int res = db.createAgent(new User(0, name, email, pass));
                    if (res == 0) {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        rv.setText(R.string.register_exec_error);
                    }
                } else {
                    //wrong data in the fields
                    rv.setText(R.string.register_form_fill_all_error);
                }
                break;
        }
    }
    */
}
