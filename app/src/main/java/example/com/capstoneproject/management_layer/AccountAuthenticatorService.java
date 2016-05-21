package example.com.capstoneproject.management_layer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AccountAuthenticatorService extends Service
{
    // Instance field that stores the authenticator object
    private AccountAuthenticatorStub authenticatorStub;

    @Override
    public void onCreate()
    {
        // Create a new authenticator object
        authenticatorStub = new AccountAuthenticatorStub(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        return authenticatorStub.getIBinder();
    }
}

