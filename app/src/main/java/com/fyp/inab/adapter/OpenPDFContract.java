package com.fyp.inab.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OpenPDFContract extends ActivityResultContract<Uri, Integer> {
    @Override
    public Integer parseResult(int i, @Nullable Intent intent) {
        return i;
    }

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
