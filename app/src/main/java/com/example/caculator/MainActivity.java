package com.example.caculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    EditText edt1, edt2;
    TextView txtResult;
    LinearLayout historyLayout;
    Button btnAdd, btnSub, btnMul, btnDiv, btnClear;
    Button[] numButtons = new Button[10];
    LinkedList<String> historyList = new LinkedList<>();

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.######");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        txtResult = findViewById(R.id.txtResult);
        historyLayout = findViewById(R.id.historyLayout);

        btnAdd = findViewById(R.id.btnAdd);
        btnSub = findViewById(R.id.btnSub);
        btnMul = findViewById(R.id.btnMul);
        btnDiv = findViewById(R.id.btnDiv);
        btnClear = findViewById(R.id.btnClear);

        // 숫자 버튼 연결
        for (int i = 0; i <= 9; i++) {
            String buttonID = "btn" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            numButtons[i] = findViewById(resID);
            final int number = i;
            numButtons[i].setOnClickListener(v -> {
                if (edt1.hasFocus()) {
                    edt1.append(String.valueOf(number));
                } else if (edt2.hasFocus()) {
                    edt2.append(String.valueOf(number));
                }
            });
        }

        // 연산자 버튼 이벤트
        btnAdd.setOnClickListener(v -> performOperation("+"));
        btnSub.setOnClickListener(v -> performOperation("-"));
        btnMul.setOnClickListener(v -> performOperation("*"));
        btnDiv.setOnClickListener(v -> performOperation("/"));

        // 초기화 버튼
        btnClear.setOnClickListener(v -> {
            edt1.setText("");
            edt2.setText("");
            txtResult.setText("계산 결과: ");
            historyList.clear();
            historyLayout.removeAllViews();
        });
    }

    private void performOperation(String operator) {
        try {
            double num1 = Double.parseDouble(edt1.getText().toString());
            double num2 = Double.parseDouble(edt2.getText().toString());

            double result = 0;
            switch (operator) {
                case "+": result = num1 + num2; break;
                case "-": result = num1 - num2; break;
                case "*": result = num1 * num2; break;
                case "/":
                    if (num2 != 0) {
                        result = num1 / num2;
                    } else {
                        Toast.makeText(MainActivity.this, "0으로 나눌 수 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
            }

            String resultStr = decimalFormat.format(result);
            txtResult.setText("계산 결과: " + resultStr);
            String expression = edt1.getText().toString() + " " + operator + " " + edt2.getText().toString() + " = " + resultStr;
            addToHistory(expression);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "숫자를 입력하세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToHistory(String record) {
        if (historyList.size() == 9) {
            historyList.removeFirst();
        }
        historyList.add(record);

        historyLayout.removeAllViews();
        for (String item : historyList) {
            TextView tv = new TextView(this);
            tv.setText(item);
            tv.setTextSize(16);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            historyLayout.addView(tv);
        }
    }
}
