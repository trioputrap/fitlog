package com.example.trio.fitlog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.trio.fitlog.adapter.ActivityAdapter;
import com.example.trio.fitlog.api.ApiClient;
import com.example.trio.fitlog.api.ApiService;
import com.example.trio.fitlog.database.SqliteDbHelper;
import com.example.trio.fitlog.model.Activity;
import com.example.trio.fitlog.utils.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivitiesFragment extends Fragment {
    private FloatingActionButton fab;
    private Toolbar toolbar_detail;
    private RecyclerView activity_list;
    private ProgressBar progressBar;

    public ActivityAdapter adapter;
    public List<Activity> activityList;
    public ActivitiesFragment() {}

    ApiService apiService;

    public static ActivitiesFragment newInstance() {
        ActivitiesFragment fragment = new ActivitiesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activityList = SqliteDbHelper.getInstance(getActivity()).getAllActivity();
        apiService = ApiClient.getService(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar_detail = getActivity().findViewById(R.id.toolbar_detail);
        getActivity().setTitle("Activities");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar_detail);

        activity_list = view.findViewById(R.id.activity_list);
        fab = view.findViewById(R.id.fab_add_activity);
        progressBar = view.findViewById(R.id.progress_horizontal);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });
        adapter = new ActivityAdapter(getActivity(), activityList);
        activity_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        activity_list.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        activityList = SqliteDbHelper.getInstance(getActivity()).getAllActivity();
        adapter.setItems(activityList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                progressBar.setVisibility(View.VISIBLE);
                apiService.getAllActivity().enqueue(
                        new Callback<List<Activity>>() {
                            @Override
                            public void onResponse(Call<List<Activity>> call, Response<List<Activity>> response) {
                                List<Activity> activities = response.body();
                                SqliteDbHelper.getInstance(getContext()).insertActivities(activities);
                                Util.activitiesLoaded = true;
                                activityList = SqliteDbHelper.getInstance(getActivity()).getAllActivity();
                                adapter.setItems(activityList);
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onFailure(Call<List<Activity>> call, Throwable t) {

                            }
                        }
                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}