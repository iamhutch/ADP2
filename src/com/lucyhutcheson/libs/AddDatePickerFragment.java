package com.lucyhutcheson.libs;

import java.util.Calendar;

import com.lucyhutcheson.houseboss.AddActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class AddDatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// USE THE CURRENT DATE AS THE DEFAULT DATE IN THE PICKER
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		long longDate = c.getTime().getTime();
		

		DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
		dialog.getDatePicker().setMinDate(longDate);
		
		// Create a new instance of DatePickerDialog and return it
		return dialog;
	}


	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
			AddActivity.setDate(year, monthOfYear, dayOfMonth);
	}

}
