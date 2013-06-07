package com.example.budgetmanager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * InputFilter that filters text for currency input.
 *
 * @author Ji jiwpark90
 */
public class CurrencyInputFilter implements InputFilter {

	// regex pattern to match
	Pattern mPattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?");

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {

		String result =	dest.subSequence(0, dstart)	+ source.toString()	+
				dest.subSequence(dend, dest.length());

		Matcher matcher = mPattern.matcher(result);

		if (!matcher.matches()) {
			return dest.subSequence(dstart, dend);
		}

		return null;
	}
}