package com.lucyhutcheson.libs;

import java.util.Calendar;

import com.lucyhutcheson.houseboss.EditActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class EditDatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {
	
	public DatePickerDialog _dialog;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// USE THE CURRENT DATE AS THE DEFAULT DATE IN THE PICKER
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		long longDate = c.getTime().getTime();
		

		_dialog = new DatePickerDialog(getActivity(), this, year, month, day);
		_dialog.getDatePicker().setMinDate(longDate);
		
		// Create a new instance of DatePickerDialog and return it
		return _dialog;
	}


	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
			EditActivity.setDate(year, monthOfYear, dayOfMonth);
	}
	
	public void setDatePicker(int year, int month, int day) {
		_dialog = new DatePickerDialog(getActivity(), this, year, month, day);
		_dialog.getDatePicker().updateDate(year, month, day);
	}

}
