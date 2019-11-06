package com.example.todo;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private Button submitBtn;
    private Button cancelBtn;
    private RatingBar newItemImportance;

    private EditText newItemTitle;

    private float importance;
    private boolean done;
    private EditText newItemDetails;
    private TextView newItemEndDate;
    private TextView newItemEndTime;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private Bundle extras;

    private String originalTask;
    private float originalImportance;
    private String originalDetails;
    private String originalStartDate;
    private String originalEndDate;
    private String originalEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_add);

        cancelBtn = (Button) findViewById(R.id.cancel_button);
        submitBtn = (Button) findViewById(R.id.submit_button);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        newItemTitle = findViewById(R.id.item_task_edit);

        newItemImportance = findViewById(R.id.item_importance_edit);
        importance = 0;

        newItemImportance.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                importance = rating;
            }
        });

        newItemDetails = findViewById(R.id.item_details_edit);

        newItemEndDate = findViewById(R.id.item_dateEnd_edit);
        newItemEndTime = findViewById(R.id.item_timeEnd_edit);

        newItemEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        newItemEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(
                        AddActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        timeSetListener,
                        hour, minute,true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                String month_s = String.format("%02d", month);
                String day_s = String.format("%02d", day);
                String date = year + "-" + month_s + "-" + day_s;
                newItemEndDate.setText(date);
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String hour_s = String.format("%02d", hour);
                String minute_s = String.format("%02d", minute);
                String time = hour_s + ":" + minute_s;
                newItemEndTime.setText(time);
            }
        };

        extras = getIntent().getExtras();
        if (extras != null) {
            originalTask = extras.getString("title");
            originalDetails = extras.getString("details");
            originalImportance = extras.getFloat("importance");
            originalStartDate = extras.getString("startDate");
            originalEndDate = extras.getString("endDate");
            originalEndTime = extras.getString("endTime");
            done = extras.getBoolean("done");
            newItemDetails.setText(extras.getString("details"));
            newItemTitle.setText(extras.getString("title"));
            newItemImportance.setRating(extras.getFloat("importance"));
            newItemEndDate.setText(extras.getString("endDate"));
            newItemEndTime.setText(extras.getString("endTime"));
        } else {
            done = false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_button: // cancel adding task
                if (extras != null) {
                    Intent i = new Intent(AddActivity.this, MainActivity.class);
                    i.putExtra("title", originalTask);
                    i.putExtra("importance", originalImportance);
                    i.putExtra("details", originalDetails);
                    i.putExtra("startDate", originalStartDate);
                    i.putExtra("done", done);
                    if (originalEndTime.length() + originalEndDate.length() == 0) i.putExtra("endDate", "");
                    else i.putExtra("endDate", originalEndDate + " " + originalEndTime);;
                    startActivity(i);
                    finish();
                } else {
                    finish();
                }
                break;
            case R.id.submit_button: // Adding task submitted
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String itemTitle = newItemTitle.getText().toString();
                float itemImportance = importance;
                String itemDetails = newItemDetails.getText().toString();
                String itemStartDate;
                if (extras != null) {
                    itemStartDate = extras.getString("startDate");
                } else {
                    itemStartDate = dateFormat.format(Calendar.getInstance().getTime()).toString(); // assign today's date
                }

                String date_ = newItemEndDate.getText().toString();
                String time_ = newItemEndTime.getText().toString();
                String itemEndDate = "";

                if (date_.length() > 0 || time_.length() > 0) { // At Least one is here
                    if (date_.length() > 0) itemEndDate = itemEndDate + date_ + " ";
                    else itemEndDate = itemEndDate + itemStartDate.substring(0, 10) + " ";

                    if (time_.length() > 0) itemEndDate = itemEndDate + time_;
                    else itemEndDate = itemEndDate + "00:00";

                }

                if (itemTitle.length() == 0) {
                    // Blink the title input field
                    break;
                }
                Intent i = new Intent(AddActivity.this, MainActivity.class);
                i.putExtra("title", itemTitle);
                i.putExtra("importance", itemImportance);
                i.putExtra("details", itemDetails);
                i.putExtra("startDate", itemStartDate);
                i.putExtra("endDate", itemEndDate);
                i.putExtra("done", done);
                startActivity(i);
                break;

        }
    }
}


