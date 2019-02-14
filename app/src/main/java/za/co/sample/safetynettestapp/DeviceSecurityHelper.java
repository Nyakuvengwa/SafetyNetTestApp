package za.co.sample.safetynettestapp;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import androidx.annotation.NonNull;

public class DeviceSecurityHelper {

    private static DeviceSecurityHelper mHelper;
    private final String TAG = getClass().getName();

    private final Random mRandom = new SecureRandom();

    private String mResult;

    private String mPendingResult;
    private Activity activity;
    private OnSuccessListener<SafetyNetApi.AttestationResponse> mSuccessListener =
            new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                @Override
                public void onSuccess(SafetyNetApi.AttestationResponse attestationResponse) {
                    /*
                     Successfully communicated with SafetyNet API.
                     Use result.getJwsResult() to get the signed result data. See the server
                     component of this sample for details on how to verify and parse this result.
                     */
                    mResult = attestationResponse.getJwsResult();
                    Log.d(TAG, "Success! SafetyNet result:\n" + mResult + "\n");

                        /*
                         TODO(developer): Forward this result to your server together with
                         the nonce for verification.
                         You can also parse the JwsResult locally to confirm that the API
                         returned a response by checking for an 'error' field first and before
                         retrying the request with an exponential backoff.

                         NOTE: Do NOT rely on a local, client-side only check for security, you
                         must verify the response on a remote server!
                        */
                }
            };
    /**
     * Called when an error occurred when communicating with the SafetyNet API.
     */
    private OnFailureListener mFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            // An error occurred while communicating with the service.
            mResult = null;

            if (e instanceof ApiException) {
                // An error with the Google Play Services API contains some additional details.
                ApiException apiException = (ApiException) e;
                Log.d(TAG, "Error: " +
                        CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": " +
                        apiException.getMessage());
            } else {
                // A different, unknown type of error occurred.
                Log.d(TAG, "ERROR! " + e.getMessage());
            }

        }
    };

    private DeviceSecurityHelper(Activity activity) {
        this.activity = activity;
    }

    public static DeviceSecurityHelper getInstance(Activity activity) {
        if (mHelper == null) {
            mHelper = new DeviceSecurityHelper(activity);
        }
        return mHelper;
    }

    public void sendSafetyNetRequest() {
        Log.i(TAG, "Sending SafetyNet API request.");

        String nonceData = TAG + System.currentTimeMillis();
        byte[] nonce = getRequestNonce(nonceData);


        SafetyNetClient client = SafetyNet.getClient(activity);
        Task<SafetyNetApi.AttestationResponse> task = client.attest(nonce, "AIzaSyCLVPT93XjufLpyntARJxivuSjiBdfj1NA");

        task.addOnSuccessListener(activity, mSuccessListener)
                .addOnFailureListener(activity, mFailureListener);

    }

    private byte[] getRequestNonce(String data) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[24];
        mRandom.nextBytes(bytes);
        try {
            byteStream.write(bytes);
            byteStream.write(data.getBytes());
        } catch (IOException e) {
            return null;
        }
        return byteStream.toByteArray();
    }
}
