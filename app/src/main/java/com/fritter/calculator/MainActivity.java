package com.fritter.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PatternMatcher;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView result_screen, computation_screen;
    Button btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_c, btn_seta, btn_divide, btn_multiply, btn_minus, btn_plus, btn_dot, btn_equals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result_screen = findViewById(R.id.result_screen);
        computation_screen = findViewById(R.id.computation_screen);
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        btn_7 = findViewById(R.id.btn_7);
        btn_8 = findViewById(R.id.btn_8);
        btn_9 = findViewById(R.id.btn_9);
        btn_c = findViewById(R.id.btn_c);
        btn_seta = findViewById(R.id.btn_seta);
        btn_divide = findViewById(R.id.btn_divide);
        btn_multiply = findViewById(R.id.btn_multiply);
        btn_minus = findViewById(R.id.btn_minus);
        btn_plus = findViewById(R.id.btn_plus);
        btn_dot = findViewById(R.id.btn_dot);
        btn_equals = findViewById(R.id.btn_equals);

        result_screen.setText("0");

        btn_c.setOnClickListener(v -> {
            computation_screen.setText("");
            result_screen.setText("");
        });

        btn_dot.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "."));
        btn_plus.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + " + "));
        btn_minus.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + " - "));
        btn_multiply.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + " * "));
        btn_divide.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + " / "));
        btn_equals.setOnClickListener(v -> {
            Double xEquals = eval(computation_screen.getText().toString());
            String xEqualsString = Double.toString(xEquals);
            if (xEqualsString.matches("\\d+\\.0") == true) {
                String noZero = xEqualsString.replaceAll("\\.\\d+", "");
                result_screen.setText(noZero);
            } else {
                result_screen.setText(xEqualsString);
            }
        });

        btn_0.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "0"));
        btn_1.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "1"));
        btn_2.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "2"));
        btn_3.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "3"));
        btn_4.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "4"));
        btn_5.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "5"));
        btn_6.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "6"));
        btn_7.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "7"));
        btn_8.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "8"));
        btn_9.setOnClickListener(v -> computation_screen.setText(computation_screen.getText() + "9"));
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
