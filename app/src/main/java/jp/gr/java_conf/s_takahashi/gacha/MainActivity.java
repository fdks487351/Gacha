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

public class MainActivity extends AppCompatActivity implements TextWatcher, RadioGroup.OnCheckedChangeListener {

    private AdRequest adRequest;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adRequest = new AdRequest.Builder().build();
        adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);

        ((EditText)findViewById(R.id.editRate)).addTextChangedListener(this);
        ((EditText)findViewById(R.id.editSpecialRate)).addTextChangedListener(this);
        ((RadioGroup)findViewById(R.id.radioRensu)).setOnCheckedChangeListener(this);
        ((EditText)findViewById(R.id.editKaisu)).addTextChangedListener(this);

        calc();
        calc2();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
//        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)scrollView.getLayoutParams();
//        marginLayoutParams.height = scrollView.getHeight() - 120;
//        scrollView.setLayoutParams(marginLayoutParams);
//    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        calc();
        calc2();
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
        calc2();
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
        result_msg += "ガチャを" + try_count + "回引いた時に1つ以上当たる確率は" + String.format("%.5f", result_over[1]) + "%です";

        TextView tvResult = (TextView) findViewById(R.id.lblResult);
        tvResult.setText(result_msg);

        findViewById(R.id.lblResult).setVisibility(View.VISIBLE);
        findViewById(R.id.lblKaisubetu).setVisibility(View.VISIBLE);
        findViewById(R.id.tosenHead).setVisibility(View.VISIBLE);
        findViewById(R.id.tosen1).setVisibility(View.VISIBLE);
        findViewById(R.id.tosen2).setVisibility(View.VISIBLE);
        findViewById(R.id.tosen3).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.textTosenNRate1)).setText(String.format("%.5f", result[1]));
        ((TextView)findViewById(R.id.textTosenNRate2)).setText(String.format("%.5f", result[2]));
        ((TextView)findViewById(R.id.textTosenNRate3)).setText(String.format("%.5f", result[3]));
        ((TextView)findViewById(R.id.textTosenNOverRate1)).setText(String.format("%.5f", result_over[1]));
        ((TextView)findViewById(R.id.textTosenNOverRate2)).setText(String.format("%.5f", result_over[2]));
        ((TextView)findViewById(R.id.textTosenNOverRate3)).setText(String.format("%.5f", result_over[3]));
    }

    private void calc2() {
//        /* 提供割合 */
//        var rate = parseFloat(document.getElementById("rate").value);
//        /* 特定枠 */
//        var str_special_rate = document.getElementById("special_rate").value;
//        var special_rate = 0;
//        if (str_special_rate != "") {
//            special_rate = parseFloat(str_special_rate);
//        }
//        /* 連数 */
//        var gacha_num = parseFloat(document.getElementById("gacha_num").value);

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
            findViewById(R.id.lblSanko).setVisibility(View.VISIBLE);
            findViewById(R.id.textSanko50).setVisibility(View.VISIBLE);
            findViewById(R.id.textSanko90).setVisibility(View.VISIBLE);
            findViewById(R.id.textSanko99).setVisibility(View.VISIBLE);
            return;
        }

        // 型変換
        double rate = Double.parseDouble(str_rate);
        double special_rate = 0;
        if (!str_special_rate.isEmpty()) {
            special_rate = Double.parseDouble(str_special_rate);
        }
        int try_count = Integer.parseInt(str_count);

        double[] expect_per = {50, 90, 99};
        double[] result = {0, 0, 0};

        /* 計算 */
//        double normal_per           = rate / 100;
//        double normal_per_miss      = 1 - normal_per;
//        if (str_special_rate == "") {
//            var special_per      = normal_per;
//            var special_per_miss = normal_per_miss;
//        } else {
//            var special_per      = special_rate / 100;
//            var special_per_miss = 1 - special_per;
//        }
        double normal_per = rate / 100;
        double normal_per_miss = 1 - normal_per;
        double special_per_miss;

        if (str_special_rate.isEmpty()) {
            special_per_miss = normal_per_miss;
        } else {
            double special_per = special_rate / 100;
            special_per_miss = 1 - special_per;
        }
        double per_miss = Math.pow(normal_per_miss, (gacha_num - 1)) * special_per_miss;
        double per_miss_log = Math.log(per_miss);
        for (int i = 0; i < result.length; i++) {
            double expect_per_miss = 1 - expect_per[i] / 100;
            double expect_per_miss_log = Math.log(expect_per_miss);
            result[i] = Math.round(expect_per_miss_log / per_miss_log);
        }

        /* 結果表示 */
        findViewById(R.id.lblSanko).setVisibility(View.VISIBLE);
        for (int i = 0; i < result.length; i++) {
            String reference_msg = "";
            if (gacha_num > 1) {
                reference_msg += gacha_num;
                reference_msg += "連";
            }
            reference_msg += "ガチャを";
            reference_msg += (int)result[i];
            reference_msg += "回引くと約";
            reference_msg += (int)expect_per[i];
            reference_msg += "%の確率で1つ以上当たります";

            TextView textSanko = null;
            switch (i) {
                case 0:
                    textSanko = (TextView)findViewById(R.id.textSanko50);
                    break;
                case 1:
                    textSanko = (TextView)findViewById(R.id.textSanko90);
                    break;
                case 2:
                    textSanko = (TextView)findViewById(R.id.textSanko99);
                    break;
            }
            textSanko.setVisibility(View.VISIBLE);
            textSanko.setText(reference_msg);
        }
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
            case R.id.help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.finish:
                finishAndRemoveTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
