package jp.gr.java_conf.s_takahashi.gacha;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements TextWatcher, RadioGroup.OnCheckedChangeListener {

    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adRequest = new AdRequest.Builder().build();
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);

        ((EditText)findViewById(R.id.editRate)).addTextChangedListener(this);
        ((EditText)findViewById(R.id.editSpecialRate)).addTextChangedListener(this);
        ((RadioGroup)findViewById(R.id.radioRensu)).setOnCheckedChangeListener(this);
        ((EditText)findViewById(R.id.editKaisu)).addTextChangedListener(this);

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
        calc();
    }

    /* 組み合わせ */
    private int combi(int a, int b)
    {
        int x = 1;
        int y = 1;
        for(int i = a; i > (a-b); i--) {
            x = x * i;
        }
        for(int j = b; j > 0; j--) {
            y = y * j;
        }
        return (x / y);
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

        // 回数
        EditText etCount = (EditText) findViewById(R.id.editKaisu);
        String str_count= etCount.getText().toString();

        // 入力チェック
        if (str_rate.isEmpty() || str_count.isEmpty()) {
            findViewById(R.id.lblResult).setVisibility(View.INVISIBLE);
            findViewById(R.id.lblKaisubetu).setVisibility(View.INVISIBLE);
            findViewById(R.id.tosenHead).setVisibility(View.INVISIBLE);
            findViewById(R.id.tosen3).setVisibility(View.INVISIBLE);
            findViewById(R.id.tosen2).setVisibility(View.INVISIBLE);
            findViewById(R.id.tosen1).setVisibility(View.INVISIBLE);
            return;
        }

        // 型変換
        double rate = Double.parseDouble(str_rate);
        double special_rate = 0;
        if (!str_special_rate.isEmpty()) {
            special_rate = Double.parseDouble(str_special_rate);
        }
        int try_count = Integer.parseInt(str_count);

        // 計算
        double normal_per = rate / 100;
        double normal_per_miss = 1 - normal_per;
        double special_per;
        double special_per_miss;
        int normal_count;
        int special_count;

        if (str_special_rate.isEmpty()) {
            special_per = normal_per;
            special_per_miss = normal_per_miss;
            normal_count = gacha_num * try_count;
            special_count = 0;
        } else {
            special_per = special_rate / 100;
            special_per_miss = 1 - special_per;
            normal_count = (gacha_num - 1) * try_count;
            special_count = try_count;
        }

        /* N回当たり */
        double[] result = {0, 0, 0, 0};
        for (int i = 0; i < result.length; i++) {
            for (int n = 0; n <= i; n++) {
                int s = i - n;
                if (normal_count >= n && special_count >= s) {
                    double normal_result  = (Math.pow(normal_per,  n) * Math.pow(normal_per_miss,  normal_count  - n)) * combi(normal_count,  n);
                    double special_result = (Math.pow(special_per, s) * Math.pow(special_per_miss, special_count - s)) * combi(special_count, s);
                    result[i] += normal_result * special_result;
                }
            }
        }

        /* N回以上当たり */
        double[] result_over = {1, 1, 1, 1};
        for (int i = 1; i < result_over.length; i++) {
            for (int j = 0; j < i; j++) {
                result_over[i] -= result[j];
            }
        }

        /* 表示用にパーセント表記にする */
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i] * 100;
            if (result[i] < 0.00001) {
                result[i] = 0.00001;
            }
            if (result[i] > 99.99999) {
                result[i] = 99.99999;
            }
            result_over[i] = result_over[i] * 100;
            if (result_over[i] < 0.00001) {
                result_over[i] = 0.00001;
            }
            if (result_over[i] > 99.99999) {
                result_over[i] = 99.99999;
            }
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
        result_msg += "ガチャを" + try_count + "回まわした場合に1つ以上当たる確率は" + String.format("%.5f", result_over[1]) + "%です";

        TextView tvResult = (TextView) findViewById(R.id.lblResult);
        tvResult.setText(result_msg);

        findViewById(R.id.lblResult).setVisibility(View.VISIBLE);
        findViewById(R.id.lblKaisubetu).setVisibility(View.VISIBLE);
        findViewById(R.id.tosenHead).setVisibility(View.VISIBLE);
        findViewById(R.id.tosen3).setVisibility(View.VISIBLE);
        findViewById(R.id.tosen2).setVisibility(View.VISIBLE);
        findViewById(R.id.tosen1).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.textTosenNRate3)).setText(String.format("%.5f", result[3]));
        ((TextView)findViewById(R.id.textTosenNRate2)).setText(String.format("%.5f", result[2]));
        ((TextView)findViewById(R.id.textTosenNRate1)).setText(String.format("%.5f", result[1]));
        ((TextView)findViewById(R.id.textTosenNOverRate3)).setText(String.format("%.5f", result_over[3]));
        ((TextView)findViewById(R.id.textTosenNOverRate2)).setText(String.format("%.5f", result_over[2]));
        ((TextView)findViewById(R.id.textTosenNOverRate1)).setText(String.format("%.5f", result_over[1]));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    // メニューアイテム選択イベント
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
