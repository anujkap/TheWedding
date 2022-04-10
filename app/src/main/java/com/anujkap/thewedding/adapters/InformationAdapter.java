package com.anujkap.thewedding.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anujkap.thewedding.R;
import com.anujkap.thewedding.classes.Event;
import com.anujkap.thewedding.classes.FAQ;

import java.util.ArrayList;

public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.ViewHolder> {
    ArrayList<FAQ> faqs = new ArrayList<>();
    private Context context;
    int mExpandedPosition = -1;

    public InformationAdapter(ArrayList<FAQ> faqs, Context context) {
        this.faqs = faqs;
        this.context = context;
    }

    @NonNull
    @Override
    public InformationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.information_layout,parent,false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull InformationAdapter.ViewHolder holder, int position) {
        final boolean isExpanded = position==mExpandedPosition;
        holder.description.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(v -> {
            mExpandedPosition = isExpanded ? -1:position;
            notifyItemChanged(position);
        });
        FAQ faq =faqs.get(position);
        holder.title.setText(faq.getQ());
        //TODO military to AM PM converter
        holder.description.setText(faq.getA());
        holder.itemView.findViewById(R.id.card_view).setBackgroundColor(Color.parseColor("#072031"));

    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.description = itemView.findViewById(R.id.event_description);
            this.title = itemView.findViewById(R.id.event_title);
        }
    }
}
