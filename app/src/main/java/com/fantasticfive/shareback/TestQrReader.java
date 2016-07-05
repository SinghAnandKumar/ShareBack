package com.fantasticfive.shareback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import jim.h.common.android.zxinglib.integrator.IntentIntegrator;
import jim.h.common.android.zxinglib.integrator.IntentResult;

public class TestQrReader extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_qr_reader);

        IntentIntegrator.initiateScan(this, R.layout.activity_test_qr_reader, R.id.viewfinder_view, R.id.preview_view, true);

        /*bScanQr = (Button)findViewById(R.id.bScanQr);
        bScanQr.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                IntentIntegrator.initiateScan(RegCameraOpen.this, R.layout.reg_camera_capture,
                        R.id.viewfinder_view, R.id.preview_view, true);
            }
        });
*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,resultCode, data);
                String result = scanResult.getContents();
                Toast.makeText(TestQrReader.this, "Content: "+result, Toast.LENGTH_SHORT).show();
                break;
            default: break;
        }
    }
}

