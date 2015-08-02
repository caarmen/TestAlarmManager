package ca.rmen.test.alarmmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by calvarez on 01/08/15.
 */
public class MyAccountAuthenticatorService extends Service{
    private MyAccountAuthenticator mAccountAuthenticator;
    public MyAccountAuthenticatorService() {
        super();
        mAccountAuthenticator = new MyAccountAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {

        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
            return mAccountAuthenticator.getIBinder();
        return null;
    }
}
