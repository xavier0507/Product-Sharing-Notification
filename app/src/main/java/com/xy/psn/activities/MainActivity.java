package com.xy.psn.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xy.psn.R;
import com.xy.psn.managers.RequestManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText registerIdEditText;
    private Button registerButton;

    private EditText sendingIdEditText;
    private Button shareButton;
    private TextView saleNameTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.findUI();
        this.registerEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register:
                String id = registerIdEditText.getText().toString();
                RequestManager.getInstance().insertUserPushData(id);
                break;

            case R.id.button_share:
                String sendId = sendingIdEditText.getText().toString();
                String saleName = saleNameTextView.getText().toString();
                String message = "Hi, " + "你朋友希望你買來送他喔！";
                RequestManager.getInstance().shareProduct(sendId, saleName, message);
                break;
        }
    }

    private void findUI() {
        this.registerIdEditText = (EditText) findViewById(R.id.editText_register_id);
        this.registerButton = (Button) findViewById(R.id.button_register);

        this.sendingIdEditText = (EditText) findViewById(R.id.editText_sendUser_id);
        this.shareButton = (Button) findViewById(R.id.button_share);
        this.saleNameTextView = (TextView) findViewById(R.id.text_sale_name);
    }

    private void registerEvent() {
        this.registerButton.setOnClickListener(this);
        this.shareButton.setOnClickListener(this);
    }
}
