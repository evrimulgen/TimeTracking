package guru.stefma.timetracking;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import guru.stefma.restapi.ApiHelper;
import guru.stefma.restapi.objects.Time;
import guru.stefma.restapi.objects.Work;
import guru.stefma.restapi.objects.Working;
import guru.stefma.restapi.objects.WorkingDay;
import guru.stefma.restapi.objects.WorkingMonth;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WorkingDay workingDay = new WorkingDay();
                workingDay.setDay(29);
                workingDay.setYear(2016);
                workingDay.setMonth(3);

                Time startTimeFirstWork = new Time();
                startTimeFirstWork.setHour(12);
                startTimeFirstWork.setMinute(10);
                Time endTimeFirstWork = new Time();
                endTimeFirstWork.setHour(10);
                endTimeFirstWork.setMinute(30);
                Work firstWork = new Work();
                firstWork.setBreakTime(true);
                firstWork.setStartTime(startTimeFirstWork);
                firstWork.setEndTime(endTimeFirstWork);

                Time startTimeSecondWork = new Time();
                startTimeSecondWork.setHour(15);
                startTimeSecondWork.setMinute(30);
                Time endTimeSecondWork = new Time();
                endTimeSecondWork.setHour(19);
                endTimeSecondWork.setMinute(30);
                Work secondWork = new Work();
                secondWork.setBreakTime(false);
                secondWork.setStartTime(startTimeSecondWork);
                secondWork.setEndTime(endTimeSecondWork);

                ArrayList<Work> works = new ArrayList<>();
                works.add(firstWork);
                works.add(secondWork);

                Working working = new Working();
                working.setToken(getString(R.string.USER_TOKEN));
                working.setWorkingDay(workingDay);
                working.setWorkList(works);

                new ApiHelper().saveWorking(working, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.e("response_code", String.valueOf(response.code()));
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        findViewById(R.id.fab1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WorkingMonth workingMonth = new WorkingMonth();
                workingMonth.setMonth(2);
                workingMonth.setToken(getString(R.string.USER_TOKEN));
                workingMonth.setYear(2016);

                new ApiHelper().getWorkingMonth(workingMonth, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.e("response_code", String.valueOf(response.code()));
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
