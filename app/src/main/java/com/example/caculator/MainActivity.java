package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    EditText edtFormula;
    TextView txtResult;
    LinearLayout historyLayout;
    LinkedList<String> historyList = new LinkedList<>();
    boolean isResultShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtFormula = findViewById(R.id.edtFormula);
        txtResult = findViewById(R.id.txtResult);
        historyLayout = findViewById(R.id.historyLayout);

        // 숫자 버튼
        int[] numBtnIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int i = 0; i < numBtnIds.length; i++) {
            Button btn = findViewById(numBtnIds[i]);
            int finalI = i;
            btn.setOnClickListener(v -> appendToFormula(String.valueOf(finalI)));
        }

        // 사칙연산 버튼
        findViewById(R.id.btnAdd).setOnClickListener(v -> appendOperator("+"));
        findViewById(R.id.btnSub).setOnClickListener(v -> appendOperator("-"));
        findViewById(R.id.btnMul).setOnClickListener(v -> appendOperator("*"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> appendOperator("/"));

        // 소수점
        findViewById(R.id.btnDot).setOnClickListener(v -> appendDot());

        // 괄호
        findViewById(R.id.btnParen).setOnClickListener(v -> appendParen());

        // +/- (부호 변경)
        findViewById(R.id.btnSign).setOnClickListener(v -> toggleSign());

        // % (퍼센트)
        findViewById(R.id.btnPercent).setOnClickListener(v -> appendPercent());

        // C (전체 지우기)
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            edtFormula.setText("");
            txtResult.setText("0");
            isResultShown = false;
        });

        // = (계산)
        findViewById(R.id.btnEqual).setOnClickListener(v -> calculateFormula());
    }

    private void appendToFormula(String str) {
        if (isResultShown) {
            edtFormula.setText("");
            isResultShown = false;
        }
        edtFormula.append(str);
    }

    private void appendOperator(String op) {
        String formula = edtFormula.getText().toString();
        if (formula.isEmpty()) return;
        char last = formula.charAt(formula.length() - 1);
        if ("+-*/".indexOf(last) >= 0) {
            // 마지막이 연산자면 교체
            edtFormula.setText(formula.substring(0, formula.length() - 1) + op);
        } else {
            edtFormula.append(op);
        }
        isResultShown = false;
    }

    private void appendDot() {
        String formula = edtFormula.getText().toString();
        if (isResultShown || formula.isEmpty() || "+-*/(".contains(formula.substring(formula.length() - 1))) {
            edtFormula.append("0.");
            isResultShown = false;
        } else {
            // 현재 숫자에 .이 이미 있으면 추가하지 않음
            int i = formula.length() - 1;
            while (i >= 0 && "0123456789.".indexOf(formula.charAt(i)) >= 0) {
                if (formula.charAt(i) == '.') return;
                i--;
            }
            edtFormula.append(".");
        }
    }

    private void appendParen() {
        String formula = edtFormula.getText().toString();
        int open = 0, close = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') open++;
            if (c == ')') close++;
        }
        if (open == close || formula.endsWith("(") || formula.isEmpty() || "+-*/".contains(formula.substring(formula.length() - 1))) {
            edtFormula.append("(");
        } else {
            edtFormula.append(")");
        }
    }

    private void toggleSign() {
        String formula = edtFormula.getText().toString();
        if (formula.isEmpty()) return;
        // 마지막 숫자 블록을 찾아 부호를 토글
        int i = formula.length() - 1;
        while (i >= 0 && "0123456789.".indexOf(formula.charAt(i)) >= 0) i--;
        if (i >= 0 && formula.charAt(i) == '-') {
            edtFormula.setText(formula.substring(0, i) + formula.substring(i + 1));
        } else {
            edtFormula.setText(formula.substring(0, i + 1) + "-" + formula.substring(i + 1));
        }
        edtFormula.setSelection(edtFormula.getText().length());
    }

    private void appendPercent() {
        String formula = edtFormula.getText().toString();
        if (formula.isEmpty()) return;
        char last = formula.charAt(formula.length() - 1);
        if ("0123456789)".indexOf(last) >= 0) {
            edtFormula.append("%");
        }
    }

    private void calculateFormula() {
        String formula = edtFormula.getText().toString();
        if (formula.isEmpty()) {
            Toast.makeText(this, "수식을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // 연산자 변환(÷, × → /, *)
            formula = formula.replace("÷", "/").replace("×", "*").replace("%", "/100");
            Expression expression = new ExpressionBuilder(formula).build();
            double result = expression.evaluate();
            txtResult.setText(String.valueOf(result));
            addToHistory(formula + " = " + result);
            isResultShown = true;
        } catch (Exception e) {
            txtResult.setText("오류");
            Toast.makeText(this, "잘못된 수식입니다", Toast.LENGTH_SHORT).show();
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
