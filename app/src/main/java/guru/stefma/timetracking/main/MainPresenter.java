package guru.stefma.timetracking.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import guru.stefma.restapi.ApiHelper;
import guru.stefma.restapi.objects.Work;
import guru.stefma.restapi.objects.WorkList;
import guru.stefma.restapi.objects.Working;
import guru.stefma.restapi.objects.WorkingDay;
import guru.stefma.restapi.objects.WorkingList;
import guru.stefma.restapi.objects.WorkingMonth;
import guru.stefma.timetracking.BuildConfig;
import guru.stefma.timetracking.CalendarViewUtils;
import guru.stefma.timetracking.decorator.TimeTrackDecorator;
import guru.stefma.timetracking.timetrack.AddTimeTrackActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MainPresenter {

    private static final int START_CALENDAR_YEAR = 2016;

    private static final int START_CALENDAR_MONTH = 3;

    private static final int START_CALENDAR_DAY = 1;

    public static final int TIME_TRACK_REQUEST_CODE = 99;

    private final MainView mView;

    private WorkingList mWorkingList;

    public MainPresenter(MainView view) {
        mView = view;
    }

    public void onViewReady() {
        CalendarDay minimumDay = CalendarDay.from(START_CALENDAR_YEAR, START_CALENDAR_MONTH, START_CALENDAR_DAY);
        CalendarDay currentDay = CalendarDay.from(new Date(System.currentTimeMillis()));
        mView.setupCalendarView(minimumDay, currentDay);
        getWorkingList(currentDay);
    }

    public void onCalendarDateSelected(MaterialCalendarView widget, CalendarDay date) {
        if (mWorkingList != null) {
            WorkList workList = mWorkingList.getWorkListFromDay(CalendarViewUtils.from(date));
            mView.startTimeTrackActivity(workList, TIME_TRACK_REQUEST_CODE);
        }
    }

    public void onCalendarMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        getWorkingList(date);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TIME_TRACK_REQUEST_CODE) {
            if (resultCode == AddTimeTrackActivity.RESULT_CODE_SAVE_WORK ||
                    resultCode == AddTimeTrackActivity.RESULT_CODE_DELETE_WORK) {
                Working working = data.getParcelableExtra(AddTimeTrackActivity.KEY_SAVED_WORKING);
                WorkingDay workingDay = working.getWorkingDay();
                CalendarDay calendarDay = CalendarViewUtils.from(workingDay);
                getWorkingList(calendarDay);
            }
        }
    }

    private void getWorkingList(CalendarDay currentDate) {
        WorkingMonth workingMonth = CalendarViewUtils.from(currentDate, mView.getUserToken());
        new ApiHelper().getWorkingMonth(workingMonth, new Callback<WorkingList>() {
            @Override
            public void onResponse(Call<WorkingList> call, Response<WorkingList> response) {
                if (response.isSuccessful()) {
                    mWorkingList = response.body();
                    List<TimeTrackDecorator> decorators = createDecorators();
                    mView.setDecorators(decorators);
                    float workHourSum = calculatorWorkingHours();
                    mView.setWorkTimSum(workHourSum);
                } else {
                    mView.showSnackbarError();
                }
            }

            @Override
            public void onFailure(Call<WorkingList> call, Throwable t) {
                t.printStackTrace();
                mView.showSnackbarError();
            }
        });
    }

    private List<TimeTrackDecorator> createDecorators() {
        List<WorkList> workList = mWorkingList.getWorkList();
        List<TimeTrackDecorator> decorators = new ArrayList<>();
        Drawable vacationDrawable = mView.getVacationDrawable();
        Drawable illnessDrawable = mView.getIllnessDrawable();
        for (WorkList workL : workList) {
            WorkingDay workingDay = workL.getWorkingDay();
            List<Work> workingDays = workL.getWorkList();
            TimeTrackDecorator trackDecorator = TimeTrackDecorator.create(workingDay)
                    .withWorkList(workingDays)
                    .illness(illnessDrawable)
                    .vacation(vacationDrawable)
                    .build();
            decorators.add(trackDecorator);
        }
        return decorators;
    }

    private float calculatorWorkingHours() {
        List<WorkList> workList = mWorkingList.getWorkList();
        float workHourSum = 0;
        for (WorkList workL : workList) {
            for (Work work : workL.getWorkList()) {
                if(work.getIllness() || work.getVacation()) {
                    workHourSum += mView.getDefaultWorkTime();
                }
                workHourSum += work.getWorkTime();
            }
        }

        return workHourSum;
    }

}
