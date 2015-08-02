package ca.rmen.test.alarmmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by calvarez on 01/08/15.
 */
public class AccountUtils {
    private static final String TAG = AccountUtils.class.getSimpleName();
    private static final String USER = "Carmy";
    private static final String ACCOUNT_TYPE = "carmen.account.type";

    static void createAccount(Context context) {
        Log.v(TAG, "createAccount");
        Account account = searchMyAccount(context);
        if (account != null) return;
        account = new Account(USER, ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(context);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentProviderClient client = context.getContentResolver().acquireContentProviderClient(ContactsContract.AUTHORITY_URI);
            ContentValues cv = new ContentValues();
            cv.put(ContactsContract.Groups.ACCOUNT_NAME, account.name);
            cv.put(ContactsContract.Groups.ACCOUNT_TYPE, account.type);
            cv.put(ContactsContract.Settings.UNGROUPED_VISIBLE, 1);

            try {
                client.insert(ContactsContract.Settings.CONTENT_URI.buildUpon().appendQueryParameter(
                        ContactsContract.CALLER_IS_SYNCADAPTER, "true").build(), cv);
            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            Bundle syncParams = new Bundle(4);
            syncParams.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
            syncParams.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
            syncParams.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
            syncParams.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

            ContentResolver.setIsSyncable(account, ContactsContract.AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);

            ContentResolver.requestSync(account, ContactsContract.AUTHORITY, syncParams);
            Log.v(TAG, "createAccount finished");
        }
    }

    private static Account searchMyAccount(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accountList = accountManager.getAccounts();
        for (Account account : accountList) {
            if (ACCOUNT_TYPE.equals(account.type)) {
                return account;
            }
        }
        return null;
    }
}
