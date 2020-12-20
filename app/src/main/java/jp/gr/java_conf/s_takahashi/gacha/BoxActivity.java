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

public class BoxActivity extends AppCompatActivity implements TextWatcher {

    private AdRequest adRequest;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        setTitle("ボックスガチャシミュレータ");

        adRequest = new AdRequest.Builder().build();
        adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);

        ((EditText)findViewById(R.id.editBox)).addTextChangedListener(this);
        ((EditText)findViewById(R.id.editHit)).addTextChangedListener(this);
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

    /* 組み合わせ */
    private double combi(int a, int b)
    {
        double x = 1;
        double y = 1;
        for(int i = a; i > (a-b); i--) {
            x = x * i;
        }
        for(int j = b; j > 0; j--) {
            y = y * j;
        }
        return (x / y);
    }

    /* 階乗 */
    private double factorial(int x)
    {
        double ret = 1;
        for (int i = x; i > 1; i--) {
            ret *= i;
        }
        return ret;
    }

    private void calc() {
        // BOX内の数
        EditText etBox = (EditText) findViewById(R.id.editBox);
        String str_box = etBox.getText().toString();

        // 当たりの数
        EditText etHit = (EditText) findViewById(R.id.editHit);
        String str_hit = etHit.getText().toString();

        // 取得回数
        EditText etKaisu = (EditText) findViewById(R.id.editKaisu);
        String str_kaisu = etKaisu.getText().toString();

        // 入力チェック
        if (str_box.isEmpty() || str_hit.isEmpty() || str_kaisu.isEmpty()) {
            findViewById(R.id.lblResult).setVisibility(View.INVISIBLE);
            findViewById(R.id.lblResultAll).setVisibility(View.INVISIBLE);
            return;
        }

        // 型変換
        int box = Integer.parseInt(str_box);
        int win = Integer.parseInt(str_hit);
        int cnt = Integer.parseInt(str_kaisu);

        // 入力チェック2
        if (win > box || cnt > box) {
            findViewById(R.id.lblResult).setVisibility(View.INVISIBLE);
            findViewById(R.id.lblResultAll).setVisibility(View.INVISIBLE);
            return;
        }

        // 計算
        double numerator = 1;
        double denominator = 1;
        for (int i = 0; i < win; i++) {
            numerator *= (box - cnt - i);
            denominator *= (box - i);
        }
        double result1 = 1 - (numerator / denominator);
        result1 = (result1 * 100);
        if (result1 < 0.00001) {
            result1 = 0.00001;
        }
        if (result1 > 99.99999) {
            result1 = 99.99999;
        }
        if (box == win || box == cnt) {
            result1 = 100;
        }

        double factorial_win = factorial(win);
        double factorial_box = factorial(box);
        double factorial_box_win = factorial(box - win);
        double factorial_cnt_win = combi(cnt, win);
        double result_all = (factorial(win) / (factorial(box) / factorial(box - win))) * combi(cnt, win);
        result_all = (result_all * 100);
        if (result_all < 0.00001) {
            result_all = 0.00001;
        }
        if (result_all > 99.99999) {
            result_all = 99.99999;
        }
        if (box == win || box == cnt) {
            result_all = 100;
        }

        /* 結果表示 */
        String result_msg = String.format("%.5f", result1) + "%の確率で1つ以上当たります。";
        findViewById(R.id.lblResult).setVisibility(View.VISIBLE);
        TextView tvResult = (TextView) findViewById(R.id.lblResult);
        tvResult.setText(result_msg);
        if (win > 1 && win <= cnt && !Double.isNaN(result_all)) {
            String result_all_msg = String.format("%.5f", result_all) + "%の確率で全部当たります。";
            findViewById(R.id.lblResultAll).setVisibility(View.VISIBLE);
            TextView tvResultAll = (TextView) findViewById(R.id.lblResultAll);
            tvResultAll.setText(result_all_msg);
        } else {
            findViewById(R.id.lblResultAll).setVisibility(View.INVISIBLE);
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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.main:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.sub:
                intent = new Intent(this, SubActivity.class);
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
