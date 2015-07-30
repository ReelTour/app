package gaftech.reeltour;

import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {
    @Override
    public void onClick(View v) {

    }
    /*
    TextView loginField = null;
    TextView passField  = null;
    UsersDatabase db    = null;
    SessionManagerActivity session;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        mAccountManager = AccountManager.get(this);

        Button loginBtn = (Button)findViewById(R.id.loginBtn);
        Button regBtn = (Button)findViewById(R.id.regBtn);
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);

        this.loginField = (TextView)findViewById(R.id.loginEmailInput);
        this.passField  = (TextView)findViewById(R.id.loginPassInput);
        //shared parameters
        db = new UsersDatabase(this);

        //session = new SessionManagerActivity(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                String email = this.loginField.getText().toString();
                String password = this.passField.getText().toString();
                Log.d("login", email + ", " + password);
                User u = db.findByEmailPassword(email, password);
                if (u != null || (email.equals("test") && password.equals("12345")) ) {
                    Log.d("login", "OK");
                    //creating session
                    //session.createLoginSession(u);

                    Intent intent = new Intent(this, AgentActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.regBtn:
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
