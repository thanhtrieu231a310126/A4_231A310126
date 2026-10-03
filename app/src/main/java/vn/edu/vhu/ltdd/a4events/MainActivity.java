package vn.edu.vhu.ltdd.a4events;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "A4_231A310126";
    private EditText edtSoA, edtSoB, edtCanNang, edtChieuCao;
    private TextView tvKetQua, tvBmi, tvPhanLoai, tvLichSu;
    private final HistoryStore history = new HistoryStore();
    private int bmiLevel = 0;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        edtSoA = findViewById(R.id.edtSoA);
        edtSoB = findViewById(R.id.edtSoB);
        edtCanNang = findViewById(R.id.edtCanNang);
        edtChieuCao = findViewById(R.id.edtChieuCao);
        tvKetQua = findViewById(R.id.tvKetQua);
        tvBmi = findViewById(R.id.tvBmi);
        tvPhanLoai = findViewById(R.id.tvPhanLoai);
        tvLichSu = findViewById(R.id.tvLichSu);
        Button btnCong = findViewById(R.id.btnCong);
        Button btnTru = findViewById(R.id.btnTru);
        Button btnNhan = findViewById(R.id.btnNhan);
        Button btnChia = findViewById(R.id.btnChia);
        btnCong.setOnClickListener(v -> tinhToan('+'));
        btnTru.setOnClickListener(v -> tinhToan('-'));
        View.OnClickListener chung = v -> {
            int id = v.getId();
            if (id == R.id.btnNhan) tinhToan('*');
            else if (id == R.id.btnChia) tinhToan('/');
        };
        btnNhan.setOnClickListener(chung);
        btnChia.setOnClickListener(chung);
        findViewById(R.id.btnXoa).setOnClickListener(v -> xoaTrang());
        findViewById(R.id.btnTinhBmi).setOnClickListener(v -> tinhBmi());
        if (savedInstanceState != null) {
            ArrayList<String> saved = savedInstanceState.getStringArrayList("history");
            history.restore(saved);
            tvKetQua.setText(savedInstanceState.getString("calc", getString(R.string.result_placeholder)));
            tvBmi.setText(savedInstanceState.getString("bmi", getString(R.string.bmi_placeholder)));
            tvPhanLoai.setText(savedInstanceState.getString("classification", ""));
            bmiLevel = savedInstanceState.getInt("bmiLevel", 0);
            applyBmiColor();
        }
        updateHistory();
    }
    private String input(EditText editText) { return editText.getText().toString().trim(); }
    private void clearErrors(EditText... fields) { for (EditText field : fields) field.setError(null); }
    private void tinhToan(char operation) {
        clearErrors(edtSoA, edtSoB);
        try {
            String a = input(edtSoA), b = input(edtSoB);
            double result = CalculatorLogic.calculate(a, b, operation);
            double numberA = CalculatorLogic.parseNumber(a, CalculatorLogic.Field.A);
            double numberB = CalculatorLogic.parseNumber(b, CalculatorLogic.Field.B);
            String formatted = String.format(Locale.getDefault(), "%.2f %c %.2f = %.2f", numberA, operation, numberB, result);
            tvKetQua.setText(formatted);
            history.add(formatted);
            updateHistory();
            Log.d(TAG, "Calculation succeeded: " + operation);
        } catch (CalculatorLogic.ValidationException e) {
            tvKetQua.setText(R.string.result_placeholder);
            showError(e);
        }
    }
    private void xoaTrang() {
        edtSoA.setText(""); edtSoB.setText("");
        clearErrors(edtSoA, edtSoB);
        tvKetQua.setText(R.string.result_placeholder);
        edtSoA.requestFocus();
    }
    private void tinhBmi() {
        clearErrors(edtCanNang, edtChieuCao);
        try {
            double bmi = CalculatorLogic.bmi(input(edtCanNang), input(edtChieuCao));
            tvBmi.setText(String.format(Locale.getDefault(), "BMI = %.1f", bmi));
            tvPhanLoai.setText(phanLoai(bmi));
            bmiLevel = CalculatorLogic.classify(bmi);
            applyBmiColor();
        } catch (CalculatorLogic.ValidationException e) {
            tvBmi.setText(R.string.bmi_placeholder);
            tvPhanLoai.setText("");
            showError(e);
        }
    }
    private String phanLoai(double bmi) {
        int[] labels = {R.string.bmi_under, R.string.bmi_normal, R.string.bmi_over, R.string.bmi_obese};
        return getString(labels[CalculatorLogic.classify(bmi)]);
    }
    private void applyBmiColor() {
        int[] colors = {R.color.bmi_under, R.color.bmi_normal, R.color.bmi_over, R.color.bmi_obese};
        tvPhanLoai.setTextColor(ContextCompat.getColor(this, colors[bmiLevel]));
    }
    private void updateHistory() {
        ArrayList<String> entries = history.snapshot();
        tvLichSu.setText(entries.isEmpty() ? getString(R.string.history_empty) : android.text.TextUtils.join("\n", entries));
    }
    private void showError(CalculatorLogic.ValidationException error) {
        EditText field;
        switch (error.field) {
            case B: field = edtSoB; break;
            case WEIGHT: field = edtCanNang; break;
            case HEIGHT: field = edtChieuCao; break;
            default: field = edtSoA;
        }
        int message;
        switch (error.code) {
            case EMPTY: message = R.string.err_empty; break;
            case FORMAT: message = R.string.err_not_number; break;
            case NON_FINITE: message = R.string.err_finite; break;
            case DIVIDE_ZERO: message = R.string.err_divide_zero; break;
            case POSITIVE: message = error.field == CalculatorLogic.Field.WEIGHT ? R.string.err_weight : R.string.err_height; break;
            default: message = R.string.err_result;
        }
        field.setError(getString(message));
        field.requestFocus();
        if (error.code != CalculatorLogic.ErrorCode.EMPTY) Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Validation: " + error.field + "/" + error.code);
    }
    @Override protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArrayList("history", history.snapshot());
        outState.putString("calc", tvKetQua.getText().toString());
        outState.putString("bmi", tvBmi.getText().toString());
        outState.putString("classification", tvPhanLoai.getText().toString());
        outState.putInt("bmiLevel", bmiLevel);
        super.onSaveInstanceState(outState);
    }
}
