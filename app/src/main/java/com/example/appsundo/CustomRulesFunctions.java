package com.example.appsundo;

import static android.content.ContentValues.TAG;

import android.text.InputType;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomRulesFunctions {

    public void restrictText(@NonNull ArrayList<TextInputEditText> textArr) {

        for (TextInputEditText i : textArr) {

            i.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        }

    }

    public void restrictNumber(@NonNull ArrayList<TextInputEditText> numArr) {

        for (TextInputEditText i : numArr) {
            i.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
            ;
        }

    }

}