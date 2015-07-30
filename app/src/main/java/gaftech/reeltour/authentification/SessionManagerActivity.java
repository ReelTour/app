package gaftech.reeltour.authentification;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import gaftech.reeltour.contentprovider.ReelContentProviderContract;
import gaftech.reeltour.helpers.SharedSession;
import gaftech.reeltour.waiter.ControlActivity;

/**
 * Created by r.suleymanov on 23.06.2015.
 * email: ruslancer@gmail.com
 */

public class SessionManagerActivity extends ControlActivity {
    AccountManager mAccountManager = null;
    AccountManagerCallback mAccountManagerCallback = null;
    protected Bundle currentSession;
    protected Account account;
    private boolean mInvalidate;
    private AlertDialog mAlertDialog;
    private static final String STATE_DIALOG = "state_dialog";
    private static final String STATE_INVALIDATE = "state_invalidate";
    public static final String ACCOUNT_EXTRA_FIELD = "account";
    public static final String AUTHTOKEN_BUNDLE_FIELD_NAME = "authtoken";
    public static final String AUTHORITY = "rsuleymanov.realty.datasync.provider";
    ProgressDialog dialog;
    public static SharedSession msharedSession;


    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (msharedSession != null &&
                    msharedSession.getID()>0 &&
                    msharedSession.hasMembership()
                ) {
            onAccessAllowed();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msharedSession = new SharedSession(this);
        mAccountManager = AccountManager.get(this);
        mAccountManagerCallback = new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    Bundle bnd = future.getResult();
                    Log.d("reel", "account bundle " + bnd.toString());
                    Account[] accounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                    for (int i=0; i<accounts.length; i++) {
                        Log.d("reel", "check account " + accounts[i].name + " and " + bnd.getString("authAccount"));
                        if (accounts[i].name.equals(bnd.getString("authAccount"))) {
                            account = accounts[i];
                        }
                    }
                    if (account == null) {
                        account = new Account(bnd.getString("authAccount"), bnd.getString("accountType"));
                        Log.d("reel", "Add access explicitly "+bnd.toString());
                        mAccountManager.addAccountExplicitly(account, bnd.getString("USER_PASS"), bnd);
                    }
                    bnd.putString(AccountGeneral.MEMBERSHIP_FIELD, mAccountManager.getUserData(account, AccountGeneral.MEMBERSHIP_FIELD));
                    bnd.putInt(AccountGeneral.USER_ID_FIELD, Integer.valueOf(mAccountManager.getUserData(account, AccountGeneral.USER_ID_FIELD)));
                    ContentResolver.setIsSyncable(account, ReelContentProviderContract.AUTHORITY, 1);
                    ContentResolver.setSyncAutomatically(account, ReelContentProviderContract.AUTHORITY, true);
                    ContentResolver.addPeriodicSync(account, ReelContentProviderContract.AUTHORITY, Bundle.EMPTY, 3600);
                    msharedSession.putBundle(bnd);


                    onAccessAllowed();
                } catch (Exception e) {
                    Log.d("reel", "operation cancelled");
                    onAccessNotAllowed();
                    e.printStackTrace();
                }
            }
        };
        requestAccess();
    }
    public void requestAccess() {
        Log.d("session", msharedSession.getLogin()+" " + msharedSession.getID() + " " +msharedSession.getMembership());
        if (msharedSession == null || (!msharedSession.hasMembership() && msharedSession.getID() <= 0) ) {
            dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
            final Account[] accounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
            if (accounts.length > 0) {
                String[] p = {};
                mAccountManager.getAuthTokenByFeatures(AccountGeneral.ACCOUNT_TYPE,
                        AccountGeneral.AUTHTOKEN_TYPE_API, p, this, null, null,
                        mAccountManagerCallback, null);
            } else {
                mAccountManager.addAccount(AccountGeneral.ACCOUNT_TYPE, null, null, null, this,
                        mAccountManagerCallback, null);
            }
       }
    }
    public void onAccessAllowed() {
        //stub method

    }
    public void onAccessNotAllowed() {
        //stub method
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void createSession() {
        requestAccess();
    }
    public void destroySession() {
        String token = msharedSession.getToken();
        if (account != null) {
            mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, token);
        }
        msharedSession.clearMe();
        requestAccess();
    }

    private void showMessage(final String msg) {
        if (TextUtils.isEmpty(msg))
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
