package com.chenxu.workassistant.email;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenxu.workassistant.R;

import javax.mail.MessagingException;

public class EmailHolder extends RecyclerView.ViewHolder {

    private ImageView ivEmailState;
    private TextView tvEmailForm;
    private TextView tvEmailTime;
    private TextView tvEmailTitle;

    public EmailHolder(View itemView) {
        super(itemView);
        ivEmailState = itemView.findViewById(R.id.iv_email_state);
        tvEmailForm = itemView.findViewById(R.id.tv_email_form);
        tvEmailTime = itemView.findViewById(R.id.tv_email_time);
        tvEmailTitle = itemView.findViewById(R.id.tv_email_title);
    }

    public void bindData(Email email){
        if (email.isNews()){
            ivEmailState.setImageResource(R.drawable.email_unread);
        }else {
            ivEmailState.setImageResource(R.drawable.email_read);
        }
        tvEmailForm.setText(email.getFrom());
        tvEmailTime.setText(email.getSentdate());
        tvEmailTitle.setText(email.getSubject());
    }
}
