package jp.gr.java_conf.s_takahashi.gacha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Button btn_calc = (Button) findViewById(R.id.btnCalc);
        btn_calc.setOnClickListener(new ResultButtonClickListener());
    }

    class ResultButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            EditText etRate = (EditText) findViewById(R.id.editText1);
            EditText etCount = (EditText) findViewById(R.id.editText2);

            String strRate = etRate.getText().toString();
            String strCount= etCount.getText().toString();

            double dRate = Double.parseDouble(strRate);
            int iCount = Integer.parseInt(strCount);

            double dResult = (1 - Math.pow(1 - dRate / 100, iCount)) * 100;

            String strResult = String.format("%.3f", dResult);
            if (dRate > 0.0 && strResult.equals("0.000")) {
                strResult = "0.001";
            }
            if (dRate < 100.0 && strResult.equals("100.000")) {
                strResult = "99.999";
            }

            TextView tvResult = (TextView) findViewById(R.id.lblResult);
            tvResult.setText(strRate + "%のガチャを" + iCount + "回行うと" + strResult + "%の確率で当たります");
        }
    }
}
