package com.example.trio.fitlog.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.trio.fitlog.R;
import com.example.trio.fitlog.api.ApiClient;
import com.example.trio.fitlog.api.ApiService;
import com.example.trio.fitlog.model.Profile;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.single.SingleFlatMapIterableObservable;
import io.reactivex.schedulers.Schedulers;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    Context context;
    List<Profile> follows;
    int follow_state;

    public void setFollows(List<Profile> follows){
        this.follows = follows;
    }

    public FollowAdapter(Context context, List<Profile> follows, int follow_state) {
        this.context = context;
        this.follows = follows;
        this.follow_state = follow_state;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.follow_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowAdapter.ViewHolder viewHolder, int i) {
        Profile follow = follows.get(i);
        viewHolder.username.setText(follow.getUsername());
        viewHolder.name.setText(follow.getName());
        viewHolder.button_follow.setText("Remove");
    }

    @Override
    public int getItemCount() {
        return follows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView name;
        public Button button_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            button_follow = itemView.findViewById(R.id.button_follow);
            button_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Profile profile = follows.get(getAdapterPosition());
                    ApiService apiService = ApiClient.getService(context);

                    follows.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    if(follow_state==1){
                        Observable<Integer> unfollow = apiService.unfollow(profile.getId())
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                        unfollow.subscribe();
                    } else {
                        Observable<Integer> removeFollower = apiService.remove_follower(profile.getId())
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                        removeFollower.subscribe();
                    }
                }
            });
        }
    }
}
