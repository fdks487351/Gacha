package jp.gr.java_conf.s_takahashi.gacha;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import androidx.appcompat.app.AppCompatActivity;

public class SubActivity extends AppCompatActivity implements TextWatcher, RadioGroup.OnCheckedChangeListener {

    private AdRequest adRequest;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        setTitle("ガチャシミュレータ(逆算)");

        adRequest = new AdRequest.Builder().build();
        adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);

        ((EditText)findViewById(R.id.editRate)).addTextChangedListener(this);
        ((EditText)findViewById(R.id.editSpecialRate)).addTextChangedListener(this);
        ((RadioGroup)findViewById(R.id.radioRensu)).setOnCheckedChangeListener(this);
        ((EditText)findViewById(R.id.editExpect)).addTextChangedListener(this);

        calc();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        calc();
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        RadioButton rensu1 = (RadioButton) findViewById(R.id.radioRensu1);
        if (rensu1.isChecked()) {
            TextView specialRate = (TextView) findViewById(R.id.editSpecialRate);
            specialRate.setText("");
            specialRate.setEnabled(false);
        }
        RadioButton rensu10 = (RadioButton) findViewById(R.id.radioReusu10);
        if (rensu10.isChecked()) {
            TextView specialRate = (TextView) findViewById(R.id.editSpecialRate);
            specialRate.setEnabled(true);
        }

        calc();
    }

    private void calc() {
        // 提供割合
        EditText etRate = (EditText) findViewById(R.id.editRate);
        String str_rate = etRate.getText().toString();

        // 特定枠
        EditText etSpRate = (EditText) findViewById(R.id.editSpecialRate);
        String str_special_rate = etSpRate.getText().toString();

        // 連数
        int gacha_num = 10;
        RadioButton radio = (RadioButton) findViewById(R.id.radioRensu1);
        if (radio.isChecked()) {
            gacha_num = 1;
        }

        // 当選期待確率
        EditText etExpect = (EditText) findViewById(R.id.editExpect);
        String str_expect= etExpect.getText().toString();

        // 入力チェック
        if (str_rate.isEmpty() || str_expect.isEmpty()) {
            findViewById(R.id.lblResult).setVisibility(View.INVISIBLE);
            return;
        }

        // 型変換
        double rate = Double.parseDouble(str_rate);
        double special_rate = 0;
        if (!str_special_rate.isEmpty()) {
            special_rate = Double.parseDouble(str_special_rate);
        }
        double expect = Double.parseDouble(str_expect);

        // 計算
        int result = 0;
        if (str_special_rate.isEmpty() || gacha_num == 1) {
            double miss_rate = 1 - rate / 100;
            double miss_rate_multi = Math.pow(miss_rate, gacha_num);
            double expect_miss_rate = 1 - expect / 100;
            double miss_rate_multi_log = Math.log(miss_rate_multi);
            double expect_miss_rate_log = Math.log(expect_miss_rate);
            double need_num = expect_miss_rate_log / miss_rate_multi_log;
            result = (int)Math.ceil(need_num);
        } else {
            double miss_rate = 1 - rate / 100;
            double miss_sprate = 1 - special_rate / 100;
            double miss_rate_multi = Math.pow(miss_rate, gacha_num - 1) * miss_sprate;
            double expect_miss_rate = 1 - expect / 100;
            double miss_rate_multi_log = Math.log(miss_rate_multi);
            double expect_miss_rate_log = Math.log(expect_miss_rate);
            double need_num = expect_miss_rate_log / miss_rate_multi_log;
            result = (int)Math.ceil(need_num);
        }

        /* 結果表示 */
        String result_msg = "";
        result_msg  = "提供割合が" + str_rate + "%";
        if (!str_special_rate.isEmpty()) {
            result_msg += "(特定枠が" + str_special_rate + "%)";
        }
        result_msg += "の";
        if (gacha_num > 1) {
            result_msg += gacha_num + "連";
        }
        result_msg += "ガチャを" + result + "回引くと約" + str_expect + "%の確率で1つ以上当たります";

        findViewById(R.id.lblResult).setVisibility(View.VISIBLE);
        TextView tvResult = (TextView) findViewById(R.id.lblResult);
        tvResult.setText(result_msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // メニューアイテム選択イベント
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.main:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.help:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.finish:
                finishAndRemoveTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
