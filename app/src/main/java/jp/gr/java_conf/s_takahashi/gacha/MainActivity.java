package jp.gr.java_conf.s_takahashi.gacha;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adRequest = new AdRequest.Builder().build();
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);

        Button btn_calc = (Button) findViewById(R.id.btnCalc);
        btn_calc.setOnClickListener(new ResultButtonClickListener());
    }

    class ResultButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCalc:
                    EditText etRate = (EditText) findViewById(R.id.editText1);
                    EditText etCount = (EditText) findViewById(R.id.editText2);

                    String strRate = etRate.getText().toString();
                    String strCount= etCount.getText().toString();

                    if (strRate.isEmpty() || strCount.isEmpty()) {
                        break;
                    }

                    double dRate = Double.parseDouble(strRate);
                    int iCount = Integer.parseInt(strCount);

                    double dResult = (1 - Math.pow(1 - dRate / 100, iCount)) * 100;

                    String strResult = String.format("%.2f", dResult);
                    if (dRate <= 0) {
                        strResult = "0.00";
                    } else if (dRate >= 100) {
                        strResult = "100.00";
                    } else {
                        if (strResult.equals("0.00")) {
                            strResult = "0.01";
                        }
                        if (strResult.equals("100.00")) {
                            strResult = "99.99";
                        }
                    }

                    TextView tvResult = (TextView) findViewById(R.id.lblResult);
                    tvResult.setText(strRate + "%のガチャを" + iCount + "回行うと" + strResult + "%の確率で当たります");
                    break;

                case R.id.sub_btnCalc:
                    TextView tvSubResult = (TextView) findViewById(R.id.sub_lblResult);

                    EditText etRate1 = (EditText) findViewById(R.id.sub_editText1);
                    EditText etExpect = (EditText) findViewById(R.id.sub_editText2);
                    EditText etPrice = (EditText) findViewById(R.id.sub_editText3);

                    String strRate1 = etRate1.getText().toString();
                    String strExpect = etExpect.getText().toString();
                    String strPrice = etPrice.getText().toString();

                    if (strRate1.isEmpty() || strExpect.isEmpty()) {
                        break;
                    }

                    double dRate1 = Double.parseDouble(strRate1);
                    double dExpect = Double.parseDouble(strExpect);

                    if (!(dRate1 > 0 && dRate1 <= 100)) {
                        tvSubResult.setText("");
                        break;
                    }
                    if (!(dExpect > 0 && dExpect < 100)) {
                        tvSubResult.setText("");
                        break;
                    }

                    int iRevCount = (int) Math.ceil(Math.log(1.0 - dExpect / 100.0) / Math.log(1.0 - dRate1 / 100.0));

                    String strSubResult = strRate1 + "%のガチャを" + String.valueOf(iRevCount) + "回行うと"  + strExpect + "%の確率で当たります";

                    if (!strPrice.isEmpty()) {
                        int iPrice = Integer.parseInt(strPrice);
                        iPrice *= iRevCount;
                        strSubResult = strRate1 + "%のガチャで" + String.valueOf(iPrice) + "円使うと"  + strExpect + "%の確率で当たります";;
                    }

                    tvSubResult.setText(strSubResult);
                    break;

                default:
                    break;
            }
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
            case R.id.normal:
                setContentView(R.layout.activity_main);
                AdView adView = (AdView) findViewById(R.id.adView);
                adView.loadAd(adRequest);
                Button btn_calc = (Button) findViewById(R.id.btnCalc);
                btn_calc.setOnClickListener(new ResultButtonClickListener());
                break;
            case R.id.reverse:
                setContentView(R.layout.activity_sub);
                AdView sub_adView = (AdView) findViewById(R.id.sub_adView);
                sub_adView.loadAd(adRequest);
                Button sub_btn_calc = (Button) findViewById(R.id.sub_btnCalc);
                sub_btn_calc.setOnClickListener(new ResultButtonClickListener());
                break;
            case R.id.finish:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
