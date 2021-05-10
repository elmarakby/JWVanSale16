package com.labs.jwvansale16.service;

import com.labs.jwvansale16.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.sap.cloud.mobile.fiori.onboarding.BaseScreen;
import com.labs.jwvansale16.service.ext.OfflineTransactionIssueScreenSettings;

public class OfflineTransactionIssueScreen extends FrameLayout implements BaseScreen<OfflineTransactionIssueScreenSettings> {
    @NonNull
    private final Toolbar toolbar;
    @NonNull
    private final TextView label;
    @NonNull
    private final TextView instruction;
    @NonNull
    private final TextView imageAvatar;
    @NonNull
    private final TextView prevUserName;
    @NonNull
    private final TextView prevUserMail;
    @NonNull
    private final TextView instruction2;
    @NonNull
    private final Button button;

    public OfflineTransactionIssueScreen(@NonNull Context context) {
        this(context, null);
    }

    public OfflineTransactionIssueScreen(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OfflineTransactionIssueScreen(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OfflineTransactionIssueScreen(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.screen_offline_transaction_issue, this);
        toolbar = findViewById(R.id.toolbar);
        label = findViewById(R.id.offline_transaction_issue_label);
        instruction = findViewById(R.id.offline_transaction_issue_description1);
        imageAvatar = findViewById(R.id.imageAvatar);
        prevUserName = findViewById(R.id.offline_transaction_issue_previous_username);
        prevUserMail = findViewById(R.id.offline_transaction_issue_previous_usermail);
        instruction2 = findViewById(R.id.offline_transaction_issue_description2);
        button = findViewById(R.id.offline_transaction_issue_button);
    }

    public void setButtonClickListener(OnClickListener listener) {
        button.setOnClickListener(listener);
    }

    public void setToolBarBackButtonClickListener(OnClickListener listener) {
        toolbar.setNavigationOnClickListener(listener);
    }

    public void setPrevUserName(String name) {
        prevUserName.setText(name);
        imageAvatar.setText(String.valueOf(name.toUpperCase().charAt(0)));
    }

    public void setPrevUserMail(String mail) {
        prevUserMail.setText(mail);
    }

    @NonNull
    public Toolbar getToolBar() {
        return toolbar;
    }

    @Override
    public void initialize(@NonNull OfflineTransactionIssueScreenSettings settings) {
        if (settings.getTitle() != null) {
            toolbar.setTitle(settings.getTitle());
        }
        if (settings.getLabel() != null) {
            label.setText(settings.getLabel());
        }
        if (settings.getInstruction() != null) {
            instruction.setText(settings.getInstruction());
        }
        if (settings.getInstruction2() != null) {
            instruction2.setText(settings.getInstruction2());
        }
        if (settings.getButtonText() != null) {
            button.setText(settings.getButtonText());
        }
    }
}