package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User;
import pl.tysia.maggwarehouse.Persistance.WareServiceImpl;
import pl.tysia.maggwarehouse.R;

public class ShelfScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_shelf_scanner);

        mScannerView = new ZXingScannerView(this);

        FrameLayout cameraFrame = findViewById(R.id.cameraFrame);
        cameraFrame.addView(mScannerView);
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
        final String code = rawResult.getText();

        showSendingState(true);
        TestShelfTask task = new TestShelfTask(code);

        task.execute();


        //mScannerView.resumeCameraPreview(this);
    }

    private void startProductsScanning(String code){
        Intent returnIntent = new Intent(this, WaresScannerActivity.class);
        returnIntent.putExtra(WaresScannerActivity.SHELF_EXTRA,code);
        startActivity(returnIntent);
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

    class TestShelfTask extends
            AsyncTask<String, String, Boolean> {
        private boolean exceptionOccured = false;
        WareServiceImpl wareService;
        private String code;

        public TestShelfTask(String code) {
            this.code = code;
        }

        @Override
        protected Boolean doInBackground(String... params){
            User user = User.Companion.getLoggedUser(ShelfScannerActivity.this);

            try {
                wareService = new WareServiceImpl(getApplicationContext());
                return wareService.testShelf(code, user.getToken());
            }catch (IOException e){
                exceptionOccured = true;
                return null;
            }


        }

        @Override
        protected void onPostExecute(Boolean success) {
            showSendingState(false);

            if (success != null)
                startProductsScanning(code);
            else if(exceptionOccured){
                finish();
                okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message));
            }else{
                Toast.makeText(ShelfScannerActivity.this,
                        getString(R.string.shelf_couldnt_be_found_toast_with_reason,
                                wareService.getLastError()), Toast.LENGTH_LONG)
                        .show();
                mScannerView.resumeCameraPreview(ShelfScannerActivity.this);
            }
        }

        @Override
        protected void onCancelled() {
            showSendingState(false);
        }
    }
}
