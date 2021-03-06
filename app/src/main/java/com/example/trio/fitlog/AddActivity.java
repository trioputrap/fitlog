package com.example.trio.fitlog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.trio.fitlog.api.ApiClient;
import com.example.trio.fitlog.api.ApiService;
import com.example.trio.fitlog.database.SqliteDbHelper;
import com.example.trio.fitlog.model.Activity;
import com.example.trio.fitlog.model.ApiResponse;
import com.example.trio.fitlog.model.Type;
import com.example.trio.fitlog.utils.PreferencesHelper;
import com.example.trio.fitlog.utils.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivity extends AppCompatActivity {
    EditText title;
    ImageView icon_activity;
    Spinner type;
    EditText distance;
    EditText hour;
    EditText minute;
    EditText date;
    EditText time;
    EditText desc;
    Calendar myCalendar;

    ProgressBar progressBar;

    MenuItem save;
    Activity activity;

    private Toolbar toolbar_detail;
    boolean isAdd = true;
    boolean pushSuccess = false;

    PreferencesHelper preferencesHelper;
    ApiService apiService;
    SqliteDbHelper sqliteDbHelper;

    public void populateData(Activity activity){
        title.setText(activity.getTitle());
        distance.setText(String.valueOf(activity.getDistance()));
        hour.setText(String.valueOf(activity.getHour()));
        minute.setText(String.valueOf(activity.getMinute()));
        desc.setText(activity.getDescription());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        sqliteDbHelper = SqliteDbHelper.getInstance(this);
        preferencesHelper = new PreferencesHelper(getApplicationContext());
        apiService = ApiClient.getService(getApplicationContext());

        toolbar_detail = findViewById(R.id.toolbar_detail);

        setSupportActionBar(toolbar_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //load all data from sqlite
        final List<Type> types = SqliteDbHelper.getInstance(this).getAllType();

        //finding views
        title = findViewById(R.id.title_input);
        icon_activity = findViewById(R.id.icon_activity);
        type = findViewById(R.id.type_input);
        distance = findViewById(R.id.distance_input);
        hour = findViewById(R.id.hour_input);
        minute = findViewById(R.id.minute_input);
        date = findViewById(R.id.date_input);
        time = findViewById(R.id.time_input);
        desc = findViewById(R.id.desc_input);
        progressBar = findViewById(R.id.progress_horizontal);

        //isntance Calendar
        myCalendar = Calendar.getInstance();

        //spinner
        ArrayAdapter<Type> adapter = new ArrayAdapter<Type>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        activity = new Activity();
        try {
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();

            activity = (Activity) bundle.getSerializable("Activity");
            isAdd = false;
        } catch (Exception e){

        }

        if(isAdd){
            setTitle("Add Activity");
        } else {
            setTitle("Edit Activity");
            myCalendar = Util.stringToCalendar(activity.getDatetime(),"yyyy-MM-dd HH:mm");

            Type type = SqliteDbHelper.getInstance(this).getType(activity.getType_id());

            int type_position = adapter.getPosition(type);
            this.type.setSelection(type_position);

            populateData(activity);
        }

        date.setText(Util.calendarToStringFriendly(myCalendar, "yyyy-MM-dd"));
        time.setText(Util.calendarToString(myCalendar, "HH:mm"));

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                icon_activity.setImageResource(types.get(i).getIcon());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //display datepicker
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                date.setText(Util.calendarToStringFriendly(myCalendar, "yyyy-MM-dd"));
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        //display timepicker
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);

                new TimePickerDialog(
                        AddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                                myCalendar.set(Calendar.MINUTE, selectedMinute);
                                time.setText(Util.calendarToString(myCalendar, "HH:mm"));
                            }
                        },
                        hour,
                        minute,
                        true
                ).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        save = menu.findItem(R.id.save);
        save.setEnabled(true);
        return true;
    }

    private void setResult(){
        Intent add_activity = new Intent(AddActivity.this, AddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Activity", activity);
        add_activity.putExtras(bundle);
        setResult(RESULT_OK, add_activity);
    }

    @Override
    public void onBackPressed() {
        if (!isAdd) {
            setResult();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (!isAdd) {
                    setResult();
                }
                finish();
                return true;
            case R.id.save:
                progressBar.setVisibility(View.VISIBLE);

                Type selectedType = (Type) type.getSelectedItem();
                activity.setTitle(title.getText().toString());
                activity.setUser_id(preferencesHelper.getUserId());
                activity.setType_id(selectedType.getId());
                activity.setDistance(Integer.parseInt(distance.getText().toString()));
                activity.setHour(Integer.parseInt(hour.getText().toString()));
                activity.setMinute(Integer.parseInt(minute.getText().toString()));
                activity.setDatetime(Util.calendarToString(myCalendar, "yyyy-MM-dd HH:mm"));
                activity.setDescription(desc.getText().toString());
                if(isAdd) {
                    activity.setFlag_insert(1);
                    if(Util.isConnected(this)) {
                        apiService.addActivity(
                                activity.getServer_id(),
                                activity.getUser_id(),
                                activity.getTitle(),
                                activity.getDatetime(),
                                activity.getType_id(),
                                activity.getHour(),
                                activity.getMinute(),
                                activity.getDistance(),
                                activity.getDescription()).enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful()) {
                                    int id = Integer.parseInt(response.body().getMessage());
                                    activity.setServer_id(id);
                                    activity.setFlag_insert(0);
                                    pushSuccess = true;
                                } else {
                                    pushSuccess = false;
                                }
                                if (!pushSuccess) {
                                    Toast.makeText(AddActivity.this, "Failed to push, saved locally", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddActivity.this, "Success create new activity", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                SqliteDbHelper.getInstance(getApplicationContext()).insertActivity(activity);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toast.makeText(AddActivity.this, "Failed to push, saved locally", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                SqliteDbHelper.getInstance(getApplicationContext()).insertActivity(activity);
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(AddActivity.this, "Failed to push, saved locally", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        SqliteDbHelper.getInstance(getApplicationContext()).insertActivity(activity);
                        finish();
                    }
                } else {
                    activity.setFlag_update(1);
                    if(Util.isConnected(this)) {
                        apiService.updateActivity(
                                activity.getServer_id(),
                                activity.getUser_id(),
                                activity.getTitle(),
                                activity.getDatetime(),
                                activity.getType_id(),
                                activity.getHour(),
                                activity.getMinute(),
                                activity.getDistance(),
                                activity.getDescription(),
                                activity.getFlag_delete()).enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful()) {
                                    activity.setFlag_update(0);
                                    pushSuccess = true;
                                } else {
                                    pushSuccess = false;
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                sqliteDbHelper.editActivity(activity);
                                setResult();
                                finish();
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toast.makeText(AddActivity.this, "Failed to push, updated locally", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                sqliteDbHelper.editActivity(activity);
                                setResult();
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(AddActivity.this, "Failed to push, updated locally", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        sqliteDbHelper.editActivity(activity);
                        setResult();
                        finish();
                    }
                }
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

}
