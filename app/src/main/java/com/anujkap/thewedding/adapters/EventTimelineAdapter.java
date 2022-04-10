package com.anujkap.thewedding.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anujkap.thewedding.classes.Event;
import com.anujkap.thewedding.R;

import java.util.ArrayList;

public class EventTimelineAdapter extends RecyclerView.Adapter<EventTimelineAdapter.ViewHolder> {
    ArrayList<Event> events = new ArrayList<>();
    private Context context;
    int mExpandedPosition = -1;

    public EventTimelineAdapter(ArrayList<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public EventTimelineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_timeline_layout,parent,false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTimelineAdapter.ViewHolder holder, int position) {
        final boolean isExpanded = position==mExpandedPosition;
        holder.description.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(v -> {
            mExpandedPosition = isExpanded ? -1:position;
            notifyItemChanged(position);
        });
        Event event =events.get(position);
        holder.title.setText(event.getName());
        //TODO military to AM PM converter
        holder.time.setText(event.getTimeStart()+"");
        holder.venue.setText(event.getVenue());
        holder.description.setText(event.getDescription());

        if(holder.getAdapterPosition()%2==0)
            holder.itemView.findViewById(R.id.card_view).setBackgroundColor(Color.parseColor("#071823"));
        else
            holder.itemView.findViewById(R.id.card_view).setBackgroundColor(Color.parseColor("#072031"));

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, time, venue, description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.description = itemView.findViewById(R.id.event_description);
            this.time = itemView.findViewById(R.id.event_time);
            this.venue = itemView.findViewById(R.id.event_venue);
            this.title = itemView.findViewById(R.id.event_title);
        }
    }
}
