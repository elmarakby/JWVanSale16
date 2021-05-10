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
import com.labs.jwvansale16.service.ext.OfflineNetworkErrorScreenSettings;

public class OfflineNetworkErrorScreen extends FrameLayout implements BaseScreen<OfflineNetworkErrorScreenSettings> {
    @NonNull
    private final Toolbar toolbar;
    @NonNull
    private final TextView label;
    @NonNull
    private final TextView instruction;
    @NonNull
    private final Button button;

    public OfflineNetworkErrorScreen(@NonNull Context context) {
        this(context, null);
    }

    public OfflineNetworkErrorScreen(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OfflineNetworkErrorScreen(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OfflineNetworkErrorScreen(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.screen_offline_network_error, this);
        toolbar = findViewById(R.id.toolbar);
        label = findViewById(R.id.offline_network_error_label);
        instruction = findViewById(R.id.offline_network_error_details);
        button = findViewById(R.id.offline_network_error_button);
    }

    public void setButtonClickListener(OnClickListener listener) {
        button.setOnClickListener(listener);
    }

    public void setToolBarBackButtonClickListener(OnClickListener listener) {
        toolbar.setNavigationOnClickListener(listener);
    }

    @NonNull
    public Toolbar getToolBar() {
        return toolbar;
    }

    @Override
    public void initialize(@NonNull OfflineNetworkErrorScreenSettings settings) {
        if (settings.getTitle() != null) {
            toolbar.setTitle(settings.getTitle());
        }
        if (settings.getLabel() != null) {
            label.setText(settings.getLabel());
        }
        if (settings.getInstruction() != null) {
            instruction.setText(settings.getInstruction());
        }
        if (settings.getButtonText() != null) {
            button.setText(settings.getButtonText());
        }
    }
}