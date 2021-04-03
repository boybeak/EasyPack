package easy.safr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SafrFragment extends Fragment {
    private String id;
    void setID(String id) {
        this.id = id;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        EasySAFR.onResult(id, requestCode, resultCode, data);
        id = null;
    }
}
