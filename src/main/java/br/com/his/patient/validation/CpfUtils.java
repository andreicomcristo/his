package br.com.his.patient.validation;

public final class CpfUtils {

    private CpfUtils() {
    }

    public static String digitsOnly(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("\\D", "").trim();
        return digits.isBlank() ? null : digits;
    }

    public static boolean isValid(String cpf) {
        String value = digitsOnly(cpf);
        if (value == null) {
            return true;
        }
        if (value.length() != 11) {
            return false;
        }
        if (value.chars().distinct().count() == 1) {
            return false;
        }

        int d1 = calculateDigit(value.substring(0, 9), 10);
        int d2 = calculateDigit(value.substring(0, 9) + d1, 11);

        return value.equals(value.substring(0, 9) + d1 + d2);
    }

    private static int calculateDigit(String base, int weightStart) {
        int sum = 0;
        int weight = weightStart;
        for (char c : base.toCharArray()) {
            sum += Character.getNumericValue(c) * weight;
            weight--;
        }
        int mod = sum % 11;
        return mod < 2 ? 0 : 11 - mod;
    }
}
