package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    EditText edtFormula;
    TextView txtResult, txtPreview;
    LinearLayout historyLayout;
    LinkedList<String> historyList = new LinkedList<>();

    String firstValue = "";
    String operator = "";
    String secondValue = "";
    boolean isSecond = false;
    boolean isResultShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtFormula = findViewById(R.id.edtFormula);
        txtResult = findViewById(R.id.txtResult);
        txtPreview = findViewById(R.id.txtPreview);
        historyLayout = findViewById(R.id.historyLayout);

        // 숫자 버튼
        int[] numBtnIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int i = 0; i < numBtnIds.length; i++) {
            Button btn = findViewById(numBtnIds[i]);
            int finalI = i;
            btn.setOnClickListener(v -> appendNumber(String.valueOf(finalI)));
        }

        // 사칙연산 버튼
        findViewById(R.id.btnAdd).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btnSub).setOnClickListener(v -> setOperator("-"));
        findViewById(R.id.btnMul).setOnClickListener(v -> setOperator("*"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> setOperator("/"));

        // 소수점
        findViewById(R.id.btnDot).setOnClickListener(v -> appendDot());

        // +/- (부호 변경)
        findViewById(R.id.btnSign).setOnClickListener(v -> toggleSign());

        // C (전체 지우기)
        findViewById(R.id.btnClear).setOnClickListener(v -> clearAll());

        // = (계산)
        findViewById(R.id.btnEqual).setOnClickListener(v -> calculate());
    }

    private void appendNumber(String num) {
        if (isResultShown) {
            clearAll();
        }
        if (!isSecond) {
            firstValue += num;
            edtFormula.setText(firstValue);
        } else {
            secondValue += num;
            edtFormula.setText(secondValue);
        }
    }

    private void setOperator(String op) {
        if (firstValue.isEmpty()) {
            Toast.makeText(this, "먼저 숫자를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!operator.isEmpty() && !secondValue.isEmpty()) {
            // 연속 연산: 이전 결과로 갱신
            calculate();
            firstValue = txtResult.getText().toString();
            secondValue = "";
        }
        operator = op;
        isSecond = true;
        txtPreview.setText(firstValue + " " + operator);
        edtFormula.setText(""); // 입력칸 비우기
        isResultShown = false;
    }

    private void appendDot() {
        if (!isSecond) {
            if (!firstValue.contains(".")) {
                if (firstValue.isEmpty()) firstValue = "0";
                firstValue += ".";
                edtFormula.setText(firstValue);
            }
        } else {
            if (!secondValue.contains(".")) {
                if (secondValue.isEmpty()) secondValue = "0";
                secondValue += ".";
                edtFormula.setText(secondValue);
            }
        }
    }

    private void toggleSign() {
        if (!isSecond) {
            if (firstValue.startsWith("-")) {
                firstValue = firstValue.substring(1);
            } else if (!firstValue.isEmpty()) {
                firstValue = "-" + firstValue;
            }
            edtFormula.setText(firstValue);
        } else {
            if (secondValue.startsWith("-")) {
                secondValue = secondValue.substring(1);
            } else if (!secondValue.isEmpty()) {
                secondValue = "-" + secondValue;
            }
            edtFormula.setText(secondValue);
        }
    }

    private void clearAll() {
        firstValue = "";
        operator = "";
        secondValue = "";
        isSecond = false;
        isResultShown = false;
        edtFormula.setText("");
        txtResult.setText("0");
        txtPreview.setText(""); // 이전 계산식도 지움
    }

    private void calculate() {
        if (firstValue.isEmpty() || operator.isEmpty() || secondValue.isEmpty()) {
            Toast.makeText(this, "수식을 완성하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        double result = 0;
        try {
            double num1 = Double.parseDouble(firstValue);
            double num2 = Double.parseDouble(secondValue);
            switch (operator) {
                case "+": result = num1 + num2; break;
                case "-": result = num1 - num2; break;
                case "*": result = num1 * num2; break;
                case "/":
                    if (num2 == 0) {
                        Toast.makeText(this, "0으로 나눌 수 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    result = num1 / num2;
                    break;
            }
            txtResult.setText(String.valueOf(result));
            // =을 눌렀을 때만 전체 계산식 표시
            txtPreview.setText(firstValue + " " + operator + " " + secondValue + " =");
            addToHistory(firstValue + " " + operator + " " + secondValue + " = " + result);
            // 다음 연산을 위해 결과를 첫 번째 값으로 설정
            firstValue = String.valueOf(result);
            operator = "";
            secondValue = "";
            isSecond = false;
            isResultShown = true;
            edtFormula.setText(firstValue); // 결과를 입력칸에 표시
        } catch (Exception e) {
            txtResult.setText("오류");
            Toast.makeText(this, "잘못된 입력입니다", Toast.LENGTH_SHORT).show();
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
            tv.setTextColor(0xFFFFFFFF); // 흰색
            historyLayout.addView(tv);
        }
    }
}
