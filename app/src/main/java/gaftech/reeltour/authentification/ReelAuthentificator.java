package gaftech.reeltour.authentification;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import gaftech.reeltour.reelapi.ReelApiManager;
import gaftech.reeltour.reelapi.ReelApiSession;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

/**
 * Created by r.suleymanov on 23.06.2015.
 * email: ruslancer@gmail.com
 */

public class ReelAuthentificator extends AbstractAccountAuthenticator {
    private String TAG = "ReelAuthenticator";
    private final Context mContext;
    private ReelApiManager sServerAuthenticate = null;
    public ReelAuthentificator(Context context) {
        super(context);
        this.mContext = context;
    }
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d("reel", TAG + "> addAccount");
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d("reel", TAG + "> getAuthToken");
        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_API)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        String authToken  = am.peekAuthToken(account, authTokenType);
        String membership = am.getUserData(account, AccountGeneral.MEMBERSHIP_FIELD);
        String user_data_id = am.getUserData(account, AccountGeneral.USER_ID_FIELD);
        long user_id = Integer.valueOf(user_data_id);
        ReelApiSession session;
        Log.d("reel", TAG + "> peekAuthToken returned - " + authToken);
        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    Log.d("reel", TAG + "> re-authenticating with the existing password");
                    session = sServerAuthenticate.userSignIn(account.name, password, authTokenType);
                    Log.d("reel", "ReelAuthentificator " + session.getId() + ", " +session.getMembership());
                    authToken = session.getToken();
                    membership = session.getMembership();
                    user_id = session.getId();
                    //saving account properties
                    am.setUserData(account, AccountGeneral.MEMBERSHIP_FIELD, membership);
                    am.setUserData(account, AccountGeneral.USER_ID_FIELD, String.valueOf(user_id));

                } catch (Exception e) {
                    e.printStackTrace();
                    authToken = null;
                }
            }
        }
        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountGeneral.MEMBERSHIP_FIELD, membership);
            result.putString(AccountGeneral.USER_ID_FIELD, String.valueOf(user_id));
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }
        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AccountGeneral.AUTHTOKEN_TYPE_API.equals(authTokenType))
            return AccountGeneral.AUTHTOKEN_TYPE_API_LABEL;
        else
            return authTokenType + " (Label)";
    }
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }
}