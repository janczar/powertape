package net.janczar.powertape.example.view.main;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.janczar.powertape.example.R;
import net.janczar.powertape.example.view.main.MessagesAdapter.MessageViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private final List<String> items = new ArrayList<>();

    private final LayoutInflater inflater;

    public MessagesAdapter(final Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void swap(List<String> messages) {
        items.clear();
        items.addAll(messages);
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        String message = items.get(position);
        holder.message.setText(message);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message)
        TextView message;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
