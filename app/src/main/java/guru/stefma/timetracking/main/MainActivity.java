package guru.stefma.timetracking.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.List;

import guru.stefma.restapi.objects.WorkList;
import guru.stefma.timetracking.R;
import guru.stefma.timetracking.decorator.TimeTrackDecorator;
import guru.stefma.timetracking.settings.SettingsActivity;
import guru.stefma.timetracking.settings.SettingsManager;
import guru.stefma.timetracking.timetrack.AddTimeTrackActivity;

public class MainActivity extends AppCompatActivity implements MainView {

    public MainPresenter mPresenter;

    private MaterialCalendarView mCalendarView;

    private TextView mWorkSum;

    public static Intent newInstance(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendar_view);
        mWorkSum = (TextView) findViewById(R.id.work_time_sum);

        mPresenter = new MainPresenter(this);
        mPresenter.onViewReady();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main_settings:
                startActivity(SettingsActivity.newInstance(this));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setupCalendarView(@NonNull CalendarDay minimumDay, @NonNull CalendarDay currentDay) {
        mCalendarView.setMinimumDate(minimumDay);
        mCalendarView.setCurrentDate(currentDay);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mPresenter.onCalendarDateSelected(widget, date);
                mCalendarView.clearSelection();
            }
        });
        mCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                mPresenter.onCalendarMonthChanged(widget, date);
            }
        });
    }

    @Override
    public void startTimeTrackActivity(@NonNull WorkList workList, int requestCode) {
        ActivityCompat.startActivityForResult(
                this,
                AddTimeTrackActivity.newInstance(MainActivity.this, workList),
                requestCode,
                null);
    }

    @Override
    public void showSnackbarError() {
        Snackbar.make(mCalendarView, R.string.error_calendar_list, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setWorkTimSum(float workHourSum) {
        mWorkSum.setText(getString(R.string.work_hour_sum, workHourSum));
    }

    @Override
    public void setDecorators(List<TimeTrackDecorator> decorators) {
        mCalendarView.removeDecorators();
        mCalendarView.addDecorators(decorators);
        mCalendarView.invalidateDecorators();
    }

    @Override
    public Drawable getVacationDrawable() {
        return VectorDrawableCompat.create(getResources(),
                R.drawable.ic_vacation_white_24dp, getTheme());
    }

    @Override
    public Drawable getIllnessDrawable() {
        return VectorDrawableCompat.create(getResources(),
                R.drawable.ic_illness_white_24dp, getTheme());
    }

    @Override
    public float getDefaultWorkTime() {
        return SettingsManager.getDefaultWorkingHours(this);
    }

    @Override
    public String getUserToken() {
        return SettingsManager.getUserToken(this);
    }
}
