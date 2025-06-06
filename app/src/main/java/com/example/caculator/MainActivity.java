package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.text.DecimalFormat;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    EditText edtFormula;
    TextView txtResult, txtPreview;
    LinearLayout historyLayout;
    LinkedList<String> historyList = new LinkedList<>();

    String formula = "";
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
            btn.setOnClickListener(v -> appendToFormula(String.valueOf(finalI)));
        }

        // 사칙연산 버튼
        findViewById(R.id.btnAdd).setOnClickListener(v -> appendOperator("+"));
        findViewById(R.id.btnSub).setOnClickListener(v -> appendOperator("-"));
        findViewById(R.id.btnMul).setOnClickListener(v -> appendOperator("*"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> appendOperator("/"));

        // 소수점
        findViewById(R.id.btnDot).setOnClickListener(v -> appendToFormula("."));

        // +/- (부호 변경)
        findViewById(R.id.btnSign).setOnClickListener(v -> toggleSign());

        // 괄호
        findViewById(R.id.btnParen).setOnClickListener(v -> appendToFormula("()"));

        // %
        findViewById(R.id.btnPercent).setOnClickListener(v -> appendToFormula("%"));

        // C (전체 지우기)
        findViewById(R.id.btnClear).setOnClickListener(v -> clearAll());

        // = (계산)
        findViewById(R.id.btnEqual).setOnClickListener(v -> calculate());

        Button btnBackspace = findViewById(R.id.btnBackspace);
        btnBackspace.setOnClickListener(v -> {
            String formula = edtFormula.getText().toString();
            int cursor = edtFormula.getSelectionStart();
            if (!formula.isEmpty() && cursor > 0) {
                String newFormula = formula.substring(0, cursor - 1) + formula.substring(cursor);
                edtFormula.setText(newFormula);
                edtFormula.setSelection(cursor - 1);
                txtPreview.setText(newFormula);
            }
        });
    }

    private void appendToFormula(String str) {
    if (isResultShown) {
        clearAll(); // 새 계산 시작
    }

    int cursor = edtFormula.getSelectionStart();
    String currentInput = edtFormula.getText().toString();
    String newInput = currentInput.substring(0, cursor) + str + currentInput.substring(cursor);
    edtFormula.setText(newInput);
    edtFormula.setSelection(cursor + str.length());
    txtPreview.setText(formula + newInput);
}

    private void appendOperator(String op) {
        String current = edtFormula.getText().toString();

        // 아무것도 없을 경우 무시
        if (current.isEmpty() && !isResultShown) return;

        // 이전 결과에서 시작하는 경우
        if (isResultShown) {
            current = txtResult.getText().toString();
            isResultShown = false;
        }

        formula += current + op;  // 누적 수식으로 이어 붙이기
        edtFormula.setText("");   // 입력창 초기화
        txtPreview.setText(formula); // 상단에 전체 수식 보여주기
        txtResult.setText("0");
    }

    private void toggleSign() {
        String current = edtFormula.getText().toString();
        if (current.isEmpty()) return;
        if (current.startsWith("-")) {
            current = current.substring(1);
        } else {
            current = "-" + current;
        }
        edtFormula.setText(current);
        edtFormula.setSelection(current.length());
        txtPreview.setText(current);
    }

    private void clearAll() {
        formula = "";
        edtFormula.setText("");
        txtResult.setText("0");
        txtPreview.setText("");
        isResultShown = false;
    }

private void calculate() {
    String currentInput = edtFormula.getText().toString();
    String fullExpression = formula + currentInput; // 누적된 전체 수식

    if (fullExpression.isEmpty()) {
        Toast.makeText(this, "수식을 입력하세요", Toast.LENGTH_SHORT).show();
        return;
    }

    try {
        String parsedExp = fullExpression.replace("÷", "/").replace("×", "*");
        Expression expression = new ExpressionBuilder(parsedExp).build();
        double result = expression.evaluate();

        String resultStr;
        DecimalFormat df = new DecimalFormat("0.############");
        if (result == (long) result) {
            resultStr = String.format("%d", (long) result);
        } else {
            resultStr = df.format(result);
        }

        txtResult.setText(resultStr);
        txtPreview.setText("");
        addToHistory(fullExpression, resultStr);
        isResultShown = true;

        // 계산 결과를 다음 수식의 시작점으로
        formula = "";
        edtFormula.setText(resultStr);
        edtFormula.setSelection(resultStr.length());

    } catch (Exception e) {
        txtResult.setText("오류");
        Toast.makeText(this, "잘못된 입력입니다", Toast.LENGTH_SHORT).show();
    }
}


    private void addToHistory(String exp, String resultStr) {
        if (historyList.size() == 9) {
            historyList.removeFirst();
        }
        String record = (historyList.size() + 1) + ". " + exp + " = " + resultStr;
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
