package vn.edu.vhu.ltdd.a4events;

/** Pure Java validation and calculations, independent of Android views. */
public final class CalculatorLogic {
    public enum Field { A, B, WEIGHT, HEIGHT }
    public enum ErrorCode { EMPTY, FORMAT, NON_FINITE, POSITIVE, DIVIDE_ZERO, RESULT }
    public static final class ValidationException extends Exception {
        public final Field field;
        public final ErrorCode code;
        ValidationException(Field field, ErrorCode code) {
            super(code.name()); this.field = field; this.code = code;
        }
    }
    private CalculatorLogic() { }
    public static double parseNumber(String raw, Field field) throws ValidationException {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) throw new ValidationException(field, ErrorCode.EMPTY);
        final double number;
        try { number = Double.parseDouble(value); }
        catch (NumberFormatException e) { throw new ValidationException(field, ErrorCode.FORMAT); }
        if (!Double.isFinite(number)) throw new ValidationException(field, ErrorCode.NON_FINITE);
        return number;
    }
    public static double calculate(String rawA, String rawB, char operation) throws ValidationException {
        double a = parseNumber(rawA, Field.A);
        double b = parseNumber(rawB, Field.B);
        if (operation == '/' && b == 0) throw new ValidationException(Field.B, ErrorCode.DIVIDE_ZERO);
        final double result;
        switch (operation) {
            case '+': result = a + b; break;
            case '-': result = a - b; break;
            case '*': result = a * b; break;
            case '/': result = a / b; break;
            default: throw new IllegalArgumentException("Unknown operation");
        }
        if (!Double.isFinite(result)) throw new ValidationException(Field.A, ErrorCode.RESULT);
        return result;
    }
    public static double bmi(String rawWeight, String rawHeight) throws ValidationException {
        double weight = parseNumber(rawWeight, Field.WEIGHT);
        double height = parseNumber(rawHeight, Field.HEIGHT);
        if (weight <= 0) throw new ValidationException(Field.WEIGHT, ErrorCode.POSITIVE);
        if (height <= 0) throw new ValidationException(Field.HEIGHT, ErrorCode.POSITIVE);
        if (height > 3) height /= 100.0;
        double square = height * height;
        double result = weight / square;
        if (!Double.isFinite(square) || !Double.isFinite(result) || result <= 0) {
            throw new ValidationException(Field.HEIGHT, ErrorCode.RESULT);
        }
        return result;
    }
    /** Thresholds specified in Lab A4; classify the unrounded value. */
    public static int classify(double bmi) {
        if (bmi < 18.5) return 0;
        if (bmi < 23) return 1;
        if (bmi < 25) return 2;
        return 3;
    }
}
