package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User;
import pl.tysia.maggwarehouse.Persistance.WareServiceImpl;
import pl.tysia.maggwarehouse.R;

public class WaresScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    static final String SHELF_EXTRA = "pl.tysia.maggwarehouse.shelf_extra";

    private String shelf;
    private ZXingScannerView mScannerView;
    private TextView productTV;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_products_scanner);

        shelf = getIntent().getStringExtra(SHELF_EXTRA);
        productTV = findViewById(R.id.added_product_tv);

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
        SendWareTask task = new SendWareTask(code);

        task.execute();

    }

    public void onFinishClicked(View view){
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

    class SendWareTask extends
            AsyncTask<String, String, Boolean> {
        private boolean exceptionOccured = false;
        WareServiceImpl wareService;

        private String code;

        public SendWareTask(String code) {
            this.code = code;
        }

        @Override
        protected Boolean doInBackground(String... params){

            User user = User.Companion.getLoggedUser(WaresScannerActivity.this);

            try {
                wareService = new WareServiceImpl(getApplicationContext());
                return wareService.sendWare(code, shelf, user.getToken());
            }catch (IOException e){
                exceptionOccured = true;
                return null;
            }

        }

        @Override
        protected void onPostExecute(Boolean success) {
            showSendingState(false);

            if (success){
                productTV.setText("Dodano produkt nr " + code);
                mScannerView.resumeCameraPreview(WaresScannerActivity.this);
            }
            else if(exceptionOccured){
                finish();
                okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message));
            }else{
                Toast.makeText(WaresScannerActivity.this,
                        getString(R.string.product_couldnt_be_send,
                                wareService.getLastError()), Toast.LENGTH_LONG)
                        .show();
                mScannerView.resumeCameraPreview(WaresScannerActivity.this);
            }
        }

        @Override
        protected void onCancelled() {
            showSendingState(false);
        }
    }
}
