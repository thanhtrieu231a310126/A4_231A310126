package vn.edu.vhu.ltdd.a4events;

public final class LogicSmokeTest {
    private static int checks;
    private static void near(double expected, double actual) {
        checks++;
        if (Math.abs(expected - actual) > 1e-9) throw new AssertionError(expected + " != " + actual);
    }
    private static void rejected(String value, CalculatorLogic.ErrorCode code) {
        checks++;
        try { CalculatorLogic.parseNumber(value, CalculatorLogic.Field.A); throw new AssertionError("Accepted " + value); }
        catch (CalculatorLogic.ValidationException e) { if (e.code != code) throw new AssertionError(e.code); }
    }
    public static void main(String[] args) throws Exception {
        near(15, CalculatorLogic.calculate("12", "3", '+'));
        near(-7.5, CalculatorLogic.calculate("-5.5", "2", '-'));
        near(36, CalculatorLogic.calculate("12", "3", '*'));
        near(4, CalculatorLogic.calculate("12", "3", '/'));
        near(3.5, CalculatorLogic.calculate("7", "2", '/'));
        near(20.761245674740486, CalculatorLogic.bmi("60", "1.70"));
        near(CalculatorLogic.bmi("60", "1.70"), CalculatorLogic.bmi("60", "170"));
        near(2, CalculatorLogic.parseNumber(" 2 ", CalculatorLogic.Field.A));
        rejected("", CalculatorLogic.ErrorCode.EMPTY);
        rejected("abc", CalculatorLogic.ErrorCode.FORMAT);
        rejected("1.5.5", CalculatorLogic.ErrorCode.FORMAT);
        rejected("-", CalculatorLogic.ErrorCode.FORMAT);
        rejected("1,5", CalculatorLogic.ErrorCode.FORMAT);
        rejected("NaN", CalculatorLogic.ErrorCode.NON_FINITE);
        rejected("Infinity", CalculatorLogic.ErrorCode.NON_FINITE);
        rejected("1e309", CalculatorLogic.ErrorCode.NON_FINITE);
        try { CalculatorLogic.calculate("12", "0", '/'); throw new AssertionError("Zero accepted"); }
        catch (CalculatorLogic.ValidationException e) { checks++; if(e.field != CalculatorLogic.Field.B || e.code != CalculatorLogic.ErrorCode.DIVIDE_ZERO) throw new AssertionError(); }
        try { CalculatorLogic.bmi("60", "0"); throw new AssertionError("Zero height accepted"); }
        catch (CalculatorLogic.ValidationException e) { checks++; if(e.field != CalculatorLogic.Field.HEIGHT) throw new AssertionError(); }
        double[] boundaries = {18.499,18.5,22.999,23,24.999,25};
        int[] levels = {0,1,1,2,2,3};
        for(int i=0;i<boundaries.length;i++) { checks++; if(CalculatorLogic.classify(boundaries[i]) != levels[i]) throw new AssertionError("Boundary " + boundaries[i]); }
        System.out.println("PASS: " + checks + " logic checks");
    }
}
