package com.example.simplenote.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationdrawer.R;
import com.example.simplenote.provider.CommitmentProvider;

import java.util.List;

public class CommitmentAdapter extends RecyclerView.Adapter<CommitmentAdapter.DataHandler> {
    private List<CommitmentProvider> commitentProviderList;
    public OnItemClickListener listener;
    public OnItemLongClickListener longListener;

    public CommitmentAdapter(List<CommitmentProvider> commitentProviderList) {
        this.commitentProviderList = commitentProviderList;
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public interface  OnItemLongClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener longListener){
        this.longListener = longListener;
    }
    @NonNull
    @Override
    public CommitmentAdapter.DataHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_commitment,parent, false);
        return new DataHandler(convertView,listener, longListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommitmentAdapter.DataHandler holder, int position) {
        CommitmentProvider commitmentProvider = commitentProviderList.get(position);
        holder.txt_tile_commitment.setText(commitmentProvider.getTitle());
        holder.txt_date_commitment.setText(commitmentProvider.getDate());
    }

    @Override
    public int getItemCount() {
        return commitentProviderList.size();
    }

    public class DataHandler extends RecyclerView.ViewHolder {
        private TextView txt_tile_commitment;
        private TextView txt_date_commitment;
        public DataHandler(@NonNull View itemView, final OnItemClickListener listener, final OnItemLongClickListener longListener) {
            super(itemView);

            txt_date_commitment = itemView.findViewById(R.id.txt_date_commitment);
            txt_tile_commitment = itemView.findViewById(R.id.txt_title_commitment);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            longListener.onItemClick(position);
                    }
                    return true;
                }
            });
        }
    }
}
