package gaftech.reeltour.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import gaftech.reeltour.R;
import gaftech.reeltour.authentification.AccountGeneral;
import gaftech.reeltour.contentprovider.ReelContentProviderContract;

/**
 * Created by r.suleymanov on 24.06.2015.
 * email: ruslancer@gmail.com
 */

public class SharedSession {
    Context _context;
    SharedPreferences sharedPref    = null;
    SharedPreferences.Editor editor = null;
    AccountManager mAccountManager = null;

    public SharedSession(Context context) {
        _context = context;
        sharedPref = _context.getSharedPreferences(_context.getString(R.string.preference_file_key), _context.MODE_PRIVATE);
        editor = sharedPref.edit();
        mAccountManager = AccountManager.get(context);
    }

    public void putBundle(Bundle data) {
        editor.putString("Membership", data.getString("membership"));
        editor.putString("Token",  data.getString("authtoken"));
        editor.putString("Login", data.getString("authAccount"));
        editor.putInt("Id", data.getInt("id"));
        editor.commit();
    }

    public void clearMe() {
        editor.clear();
        editor.commit();
    }

    public boolean hasMembership() {
        String m = this.getMembership();
        return (m != null && !m.isEmpty() &&
                (m.equals(AccountGeneral.MEMBERSHIP_PRO_NAME) ||
                        m.equals(AccountGeneral.MEMBERSHIP_STANDARD_NAME)) );
    }

    public void putAccount(Account account) {

    }

    public Boolean isPro() {
        return sharedPref.getString("Membership", "").equals(AccountGeneral.MEMBERSHIP_PRO_NAME);
    }
    public String getToken() {
       return sharedPref.getString("Token", null);
    }
    public String getMembership() {
       return sharedPref.getString("Membership", null);
    }
    public String getLogin() {
        return sharedPref.getString("Login", null);
    }
    public long getID(){ return sharedPref.getInt("Id", -1); }


    Account account;
    public void requestSync() {
        Account[] accounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        for (int i=0; i<accounts.length; i++) {
            Log.d("reel", "check account " + accounts[i].name + " and " + this.getLogin());
            if (accounts[i].name.equals(this.getLogin())) {
                account = accounts[i];
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                ContentResolver.requestSync(account, ReelContentProviderContract.AUTHORITY, bundle);
                break;
            }
        }
    }
}
