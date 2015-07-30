package gaftech.reeltour.authentification;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import gaftech.reeltour.R;
import gaftech.reeltour.reelapi.ReelApiManager;
import gaftech.reeltour.reelapi.ReelApiSession;

/**
 * Created by r.suleymanov on 28.05.2015.
 * email: ruslancer@gmail.com
 */

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements View.OnClickListener {
    public static final String AUTH_TOKEN_TYPE = "reeltour_token";
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    protected final int REQ_SIGNUP = 1;
    protected final String TAG = this.getClass().getSimpleName();
    protected AccountManager mAccountManager;
    protected String mAuthTokenType;
    private ReelApiManager sServerAuthenticate = null;
    TextView loginField = null;
    TextView passField  = null;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        sServerAuthenticate = new ReelApiManager(this);
        mAccountManager = AccountManager.get(getBaseContext());
        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_API;
        if (accountName != null) {
            ((TextView)findViewById(R.id.loginEmailInput)).setText(accountName);
        }
        this.loginField = (TextView)findViewById(R.id.loginEmailInput);
        this.passField  = (TextView)findViewById(R.id.loginPassInput);
        Button loginBtn = (Button)findViewById(R.id.loginBtn);
        Button regBtn = (Button)findViewById(R.id.regBtn);
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
    }
    public void submit() {
        final String userName = this.loginField.getText().toString();
        final String userPass = this.passField.getText().toString();
        if (userName.length()==0 || userPass.length()==0) {
            Toast.makeText(this, "Please, fill in login and password", Toast.LENGTH_LONG).show();
        } else {
            final String accountType = (getIntent().hasExtra(ARG_ACCOUNT_TYPE)) ? getIntent().getStringExtra(ARG_ACCOUNT_TYPE) : AccountGeneral.ACCOUNT_TYPE;
            new AsyncTask<Void, Void, Intent>() {
                @Override
                protected Intent doInBackground(Void... params) {
                    Log.d("reeltour", TAG + "> Started authenticating");
                    String authtoken = null;
                    Bundle data = new Bundle();
                    try {
                        ReelApiSession session;
                        session = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);
                        authtoken = session.getToken();
                        String membership = session.getMembership();
                        long user_id = session.getId();
                        Log.d("reel", "authentified user id "+String.valueOf(user_id));
                        if (authtoken.length() == 32) {
                            data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                            data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                            data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                            data.putString(AccountGeneral.MEMBERSHIP_FIELD, membership);
                            data.putString(AccountGeneral.USER_ID_FIELD, String.valueOf(user_id));
                            data.putString(PARAM_USER_PASS, userPass);
                        } else {
                            data.putString(KEY_ERROR_MESSAGE, "Error logging");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                    }
                    final Intent res = new Intent();
                    Log.d("reeltour", data.toString());
                    res.putExtras(data);
                    return res;
                }
                @Override
                protected void onPostExecute(Intent intent) {
                    finishLogin(intent);
                }
            }.execute();
        }
    }

    public void finishLogin(Intent intent) {
        Log.d("reel", TAG + "> finishLogin");
        if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
            Toast.makeText(this, "Error. Coudn't authorize. Please try again!", Toast.LENGTH_LONG).show();
        } else {
            String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
            final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
            if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
                Log.d("reel", TAG + "> finishLogin > addAccountExplicitly");
                String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
                String authtokenType = mAuthTokenType;
                String membership = intent.getStringExtra(AccountGeneral.MEMBERSHIP_FIELD);
                String userid = intent.getStringExtra(AccountGeneral.USER_ID_FIELD);
                // Creating the account on the device and setting the auth token we got
                // (Not setting the auth token will cause another call to the server to authenticate the user)
                mAccountManager.addAccountExplicitly(account, accountPassword, null);
                mAccountManager.setAuthToken(account, authtokenType, authtoken);
                mAccountManager.setUserData(account, AccountGeneral.MEMBERSHIP_FIELD, membership);
                mAccountManager.setUserData(account, AccountGeneral.USER_ID_FIELD, userid );
            } else {
                Log.d("reel", TAG + "> finishLogin > setPassword");
                mAccountManager.setPassword(account, accountPassword);
            }
            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                this.submit();
            break;
            case R.id.regBtn:
                Intent i = new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.website)));
                startActivity(i);
            break;
        }
    }


}
