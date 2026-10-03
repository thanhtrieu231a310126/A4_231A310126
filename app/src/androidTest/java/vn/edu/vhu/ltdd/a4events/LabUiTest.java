package vn.edu.vhu.ltdd.a4events;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LabUiTest {
    private void set(MainActivity a, int id, String text) {
        EditText edit = a.findViewById(id);
        // Inject malformed strings to exercise validation beyond the numeric keyboard filter.
        edit.setFilters(new InputFilter[0]);
        edit.setText(text);
    }
    private String text(MainActivity a, int id) { return ((TextView)a.findViewById(id)).getText().toString(); }
    private void calc(MainActivity a, String x, String y, int button) {
        ((ScrollView)a.findViewById(R.id.main)).scrollTo(0,0);
        set(a,R.id.edtSoA,x); set(a,R.id.edtSoB,y);
        a.findViewById(button).performClick();
    }
    private void bmi(MainActivity a, String w, String h) {
        set(a,R.id.edtCanNang,w); set(a,R.id.edtChieuCao,h);
        a.findViewById(R.id.btnTinhBmi).performClick();
        ((ScrollView)a.findViewById(R.id.main)).post(() -> ((ScrollView)a.findViewById(R.id.main)).fullScroll(ScrollView.FOCUS_DOWN));
    }
    private void error(MainActivity a, int id, int message) {
        EditText field=a.findViewById(id);
        assertEquals(a.getString(message),String.valueOf(field.getError()));
        assertTrue(field.hasFocus());
    }
    private void capture(ActivityScenario<MainActivity> scenario, String filename) throws Exception {
        SystemClock.sleep(2300);
        final File[] destination = new File[1];
        scenario.onActivity(a -> destination[0]=new File(a.getExternalFilesDir(null),filename+".png"));
        Bitmap screen=InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        assertNotNull(screen);
        try(FileOutputStream out=new FileOutputStream(destination[0])) { screen.compress(Bitmap.CompressFormat.PNG,100,out); }
        screen.recycle();
    }
    @Test public void tenCasesAdvancedAndRotation() throws Exception {
        try(ActivityScenario<MainActivity> s=ActivityScenario.launch(MainActivity.class)) {
            capture(s,"00_giao_dien");
            s.onActivity(a -> { calc(a,"12","3",R.id.btnChia); assertEquals(String.format(Locale.getDefault(),"%.2f %c %.2f = %.2f",12.0,'/',3.0,4.0),text(a,R.id.tvKetQua)); });
            capture(s,"01_chia_dung");
            s.onActivity(a -> { calc(a,"12","0",R.id.btnChia); error(a,R.id.edtSoB,R.string.err_divide_zero); });
            capture(s,"02_loi_chia_0");
            s.onActivity(a -> { calc(a,"","3",R.id.btnCong); error(a,R.id.edtSoA,R.string.err_empty); });
            capture(s,"03_loi_o_trong");
            s.onActivity(a -> { calc(a,"abc","2",R.id.btnNhan); error(a,R.id.edtSoA,R.string.err_not_number); });
            capture(s,"04_loi_dinh_dang");
            s.onActivity(a -> { calc(a,"-5.5","2",R.id.btnTru); assertTrue(text(a,R.id.tvKetQua).endsWith(String.format(Locale.getDefault(),"%.2f",-7.5))); });
            capture(s,"05_so_am");
            s.onActivity(a -> { calc(a,"1.5.5","2",R.id.btnCong); error(a,R.id.edtSoA,R.string.err_not_number); });
            capture(s,"06_hai_dau_cham");
            s.onActivity(a -> { bmi(a,"60","1.70"); assertEquals(String.format(Locale.getDefault(),"BMI = %.1f",60/(1.7*1.7)),text(a,R.id.tvBmi)); assertEquals("Bình thường",text(a,R.id.tvPhanLoai)); });
            capture(s,"07_bmi_met");
            s.onActivity(a -> { bmi(a,"60","170"); assertEquals(String.format(Locale.getDefault(),"BMI = %.1f",60/(1.7*1.7)),text(a,R.id.tvBmi)); });
            capture(s,"08_bmi_cm");
            s.onActivity(a -> { bmi(a,"60","0"); error(a,R.id.edtChieuCao,R.string.err_height); assertEquals(a.getString(R.string.bmi_placeholder),text(a,R.id.tvBmi)); });
            capture(s,"09_loi_chieu_cao_0");
            s.onActivity(a -> { bmi(a,"60",""); error(a,R.id.edtChieuCao,R.string.err_empty); });
            capture(s,"10_loi_bmi_trong");
            // Additional errors and successful recovery after failures.
            s.onActivity(a -> {
                calc(a,"NaN","2",R.id.btnCong); error(a,R.id.edtSoA,R.string.err_finite);
                calc(a,"1e308","1e308",R.id.btnNhan); error(a,R.id.edtSoA,R.string.err_result);
                calc(a,"12","",R.id.btnCong); error(a,R.id.edtSoB,R.string.err_empty);
                bmi(a,"-1","170"); error(a,R.id.edtCanNang,R.string.err_weight);
                bmi(a,"abc","170"); error(a,R.id.edtCanNang,R.string.err_not_number);
                for(int i=1;i<=6;i++) calc(a,Integer.toString(i),"1",R.id.btnCong);
                String history=text(a,R.id.tvLichSu);
                assertEquals(5,history.split("\n").length);
                assertTrue(history.startsWith(String.format(Locale.getDefault(),"%.2f",6.0)));
                a.findViewById(R.id.btnXoa).performClick();
                assertEquals("",text(a,R.id.edtSoA)); assertEquals("",text(a,R.id.edtSoB));
                assertNull(((EditText)a.findViewById(R.id.edtSoA)).getError());
                assertEquals(a.getString(R.string.result_placeholder),text(a,R.id.tvKetQua));
                assertEquals(history,text(a,R.id.tvLichSu));
            });
            capture(s,"11_lich_su_5");
            final String[] before=new String[1];
            s.onActivity(a -> { before[0]=text(a,R.id.tvLichSu); a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); });
            SystemClock.sleep(3000);
            s.onActivity(a -> { assertEquals(Configuration.ORIENTATION_LANDSCAPE,a.getResources().getConfiguration().orientation); assertEquals(before[0],text(a,R.id.tvLichSu)); });
            capture(s,"12_xoay_man_hinh");
            s.onActivity(a -> a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
            SystemClock.sleep(2200);
            s.onActivity(a -> { bmi(a,"60","170"); assertEquals(ContextCompat.getColor(a,R.color.bmi_normal),((TextView)a.findViewById(R.id.tvPhanLoai)).getCurrentTextColor()); });
            capture(s,"13_bmi_xanh");
            s.onActivity(a -> { bmi(a,"68","170"); assertEquals("Thừa cân",text(a,R.id.tvPhanLoai)); assertEquals(ContextCompat.getColor(a,R.color.bmi_over),((TextView)a.findViewById(R.id.tvPhanLoai)).getCurrentTextColor()); });
            capture(s,"14_bmi_vang");
            s.onActivity(a -> { bmi(a,"80","170"); assertEquals("Béo phì",text(a,R.id.tvPhanLoai)); assertEquals(ContextCompat.getColor(a,R.color.bmi_obese),((TextView)a.findViewById(R.id.tvPhanLoai)).getCurrentTextColor()); });
            capture(s,"15_bmi_do");
            s.recreate();
            s.onActivity(a -> { assertEquals("Béo phì",text(a,R.id.tvPhanLoai)); assertEquals(before[0],text(a,R.id.tvLichSu)); });
        }
    }
}
