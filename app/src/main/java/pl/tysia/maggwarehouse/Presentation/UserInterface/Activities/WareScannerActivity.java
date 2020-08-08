package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import pl.tysia.maggwarehouse.BusinessLogic.Domain.User;
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Ware;
import pl.tysia.maggwarehouse.Persistance.WareService;
import pl.tysia.maggwarehouse.Persistance.WareServiceImpl;
import pl.tysia.maggwarehouse.R;

import java.io.IOException;

public class WareScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    private boolean scanOnDemand = false;

    private boolean isScanning = false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scanner);

        mScannerView = new ZXingScannerView(this);

        FrameLayout cameraFrame = findViewById(R.id.cameraFrame);
        cameraFrame.addView(mScannerView);

        scanOnDemand = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean("scan_on_demand", false);

        Button scanButton = findViewById(R.id.scanButton);
        if (scanOnDemand){
            scanButton.setVisibility(View.VISIBLE);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isScanning = true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        if (isScanning || !scanOnDemand){
            final String code = rawResult.getText();

            showSendingState(true);
            GetPhotoTask task = new GetPhotoTask();

            User user = User.Companion.getLoggedUser(this);

            task.execute(code, user.getToken());

            isScanning = false;
        }else mScannerView.resumeCameraPreview(this);
    }

    private void returnWare(Ware ware){
        Intent returnIntent = getIntent();
        returnIntent.putExtra(WareInfoActivity.WARE_EXTRA, ware);
        setResult(1, returnIntent);

        finish();
    }

    private void showSendingState(boolean state){
        ProgressBar bar = findViewById(R.id.progressBar);

        if (state) bar.setVisibility(View.VISIBLE);
        else bar.setVisibility(View.GONE);
    }

    private void okDialog(String title, String message ){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    class GetPhotoTask extends
            AsyncTask<String, String, Ware> {
        private boolean exceptionOccured = false;
        WareServiceImpl wareService;

        @Override
        protected Ware doInBackground(String... params){

            try {
                wareService = new WareServiceImpl(getApplicationContext());
                return wareService.getWare(params[0], params[1]);
            }catch (IOException e){
                exceptionOccured = true;
                return null;
            }


        }

        @Override
        protected void onPostExecute(Ware ware) {
            showSendingState(false);

            if (ware != null)
                returnWare(ware);
            else if(exceptionOccured){
                finish();
                okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message));
            }else{
                Toast.makeText(WareScannerActivity.this,
                        getString(R.string.product_couldnt_be_found_toast_with_reason,
                                wareService.getLastError()), Toast.LENGTH_LONG)
                        .show();
                mScannerView.resumeCameraPreview(WareScannerActivity.this);
            }
        }

        @Override
        protected void onCancelled() {
            showSendingState(false);
        }
    }
}
