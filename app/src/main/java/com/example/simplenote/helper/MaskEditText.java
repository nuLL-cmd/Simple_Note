package com.example.simplenote.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaskEditText {

    public static final String FORMAT_CPF = "###.###.###-##";
    public static final String FORMAT_FONE = "(##) ####-####";
    public static final String FORMAT_CELULAR = "(##) #####-####";
    public static final String FORMAT_CEP = "#####-###";
    public static final String FORMAT_DATE = "##-##-####";
    public static final String FORMAT_HOUR = "##h##m";
    public static final String FORMAT_DATE_HOUR = "##/##/## - ##h##m";

    /**
     * Método que deve ser chamado para realizar a formatação
     *
     * @param editText
     * @param mask
     * @return
     */
    public static TextWatcher mask(final EditText editText, final String mask) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void afterTextChanged(final Editable s) {
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                final String str = MaskEditText.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (final Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }
        };
    }

    public static String unmask(final String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]", "").replaceAll("[)]", "").replaceAll("[h]", "").replaceAll("[m]", "");
    }
}