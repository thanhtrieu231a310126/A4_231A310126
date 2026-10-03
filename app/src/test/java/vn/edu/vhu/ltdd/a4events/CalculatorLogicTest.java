package vn.edu.vhu.ltdd.a4events;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

public class CalculatorLogicTest {
    @Test public void labCasesAndBoundaries() throws Exception { LogicSmokeTest.main(new String[0]); }
    private void error(CalculatorLogic.Field field, CalculatorLogic.ErrorCode code, String a, String b, boolean bmi) throws Exception {
        try { if(bmi) CalculatorLogic.bmi(a,b); else CalculatorLogic.calculate(a,b,'/'); fail("Expected validation"); }
        catch(CalculatorLogic.ValidationException e) { assertEquals(field,e.field); assertEquals(code,e.code); }
    }
    @Test public void fieldSpecificFailures() throws Exception {
        error(CalculatorLogic.Field.B,CalculatorLogic.ErrorCode.EMPTY,"12","",false);
        error(CalculatorLogic.Field.B,CalculatorLogic.ErrorCode.FORMAT,"12","abc",false);
        error(CalculatorLogic.Field.WEIGHT,CalculatorLogic.ErrorCode.EMPTY,"","170",true);
        error(CalculatorLogic.Field.HEIGHT,CalculatorLogic.ErrorCode.EMPTY,"60","",true);
        error(CalculatorLogic.Field.WEIGHT,CalculatorLogic.ErrorCode.POSITIVE,"-1","170",true);
        error(CalculatorLogic.Field.HEIGHT,CalculatorLogic.ErrorCode.POSITIVE,"60","-1",true);
        error(CalculatorLogic.Field.A,CalculatorLogic.ErrorCode.RESULT,"1e308","1e-308",false);
        error(CalculatorLogic.Field.HEIGHT,CalculatorLogic.ErrorCode.RESULT,"60","1e-308",true);
        error(CalculatorLogic.Field.HEIGHT,CalculatorLogic.ErrorCode.RESULT,"60","1e308",true);
        error(CalculatorLogic.Field.HEIGHT,CalculatorLogic.ErrorCode.RESULT,"1e-308","1e154",true);
    }
    @Test public void nullAndUnsupportedOperation() throws Exception {
        try { CalculatorLogic.parseNumber(null,CalculatorLogic.Field.A); fail(); }
        catch(CalculatorLogic.ValidationException e) { assertEquals(CalculatorLogic.ErrorCode.EMPTY,e.code); }
        try { CalculatorLogic.calculate("1","2",'?'); fail(); }
        catch(IllegalArgumentException expected) { assertEquals("Unknown operation",expected.getMessage()); }
    }
    @Test public void historyCapsRestoresAndCopies() {
        HistoryStore history = new HistoryStore();
        history.restore(null); assertTrue(history.snapshot().isEmpty());
        for(int i=0;i<6;i++) history.add("result"+i);
        assertEquals(Arrays.asList("result5","result4","result3","result2","result1"),history.snapshot());
        history.snapshot().clear(); assertEquals(5,history.snapshot().size());
        HistoryStore restored = new HistoryStore(); restored.restore(history.snapshot());
        assertEquals(history.snapshot(),restored.snapshot());
        restored.restore(Arrays.asList("a","b","c","d","e","f")); assertEquals(5,restored.snapshot().size());
    }
}
