package com.example.stresstestencryption;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.pax.poslink.CommSetting;
import com.pax.poslink.aidl.BasePOSLinkCallback;
import com.pax.poslink.fullIntegration.InputAccount;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utils.init(MainActivity.this);
    }

    private CommSetting commset(){
        CommSetting commSetting = new CommSetting();
        commSetting.setType(CommSetting.AIDL);
        commSetting.setTimeOut("-1");
        return commSetting;
    }

    public InputAccount.InputAccountRequest doInputAccount(){
        InputAccount.InputAccountRequest inputreq = new InputAccount.InputAccountRequest();

        inputreq.setEdcType("CREDIT");
        inputreq.setTransType("SALE");
        inputreq.setAmount("100");
        inputreq.setMagneticSwipeEntryFlag("1");
        inputreq.setManualEntryFlag("1");
        inputreq.setContactlessEntryFlag("1");
        inputreq.setContactEMVEntryFlag("1");
        inputreq.setEncryptionFlag("1");
        inputreq.setKeySLot("1");
        inputreq.setTimeOut("300");

        return inputreq;
    }

    public void processtrans(View view) {
        EditText times = findViewById(R.id.editTextTextPersonName);
        int iterr = Integer.parseInt(times.getText().toString());

        for (int i = 0; i < iterr; i++) {
            try {
                final int finalI = i;
                InputAccount.inputAccountWithEMV(this, doInputAccount(), commset(), new BasePOSLinkCallback<InputAccount.InputAccountResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onFinish(InputAccount.InputAccountResponse inputAccountResponse) {
                        final String rescode = inputAccountResponse.getResultCode();
                        final String restext = inputAccountResponse.getResultTxt();
                        final String ksn = inputAccountResponse.getKsn();
                        final String pan = inputAccountResponse.getPan();
                        final String mpan = inputAccountResponse.getMaskedPAN();
                        final String bdk = "0123456789ABCDEFFEDCBA9876543210";

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient();
                                HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse("http://emv.eumes.io/dukpt/decrypt/sred")).newBuilder();
                                builder.addQueryParameter("bdk", bdk);
                                builder.addQueryParameter("ksn", ksn);
                                builder.addQueryParameter("ciphertext", pan);
                                String s = builder.build().toString();
                                MediaType mediaType = MediaType.parse("text/plain");
                                RequestBody body = RequestBody.create(mediaType, "");
                                Request request = new Request.Builder()
                                        .url(s)
                                        .method("POST", body)
                                        .build();
                                try {
                                    utils.log("APP", "-----Starting Iteration: -"+ finalI +"-----");
                                    Log.i("Result code", rescode);
                                    utils.log("Result code", rescode);
                                    Log.i("Result text", restext);
                                    utils.log("Result text", restext);
                                    Log.i("BDK", bdk);
                                    utils.log("BDK", bdk);
                                    Log.i("KSN", ksn);
                                    utils.log("KSN", ksn);
                                    Log.i("PAN", pan);
                                    utils.log("PAN", pan);
                                    Log.i("Masked PAN", mpan);
                                    utils.log("Masked PAN", mpan);

                                    Log.i("Request", request.toString());
                                    utils.log("Request", request.toString());
                                    Response response = client.newCall(request).execute();
                                    StringBuilder jsonData = new StringBuilder();
                                    BufferedReader br = null;
                                    try {
                                        String line;
                                        br = new BufferedReader(Objects.requireNonNull(response.body()).charStream());
                                        while ((line = br.readLine()) != null) {
                                            jsonData.append(line).append("\n");
                                        }
                                        br.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i("Response body", jsonData.toString());
                                    utils.log("Response body", jsonData.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }
                }, null);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}