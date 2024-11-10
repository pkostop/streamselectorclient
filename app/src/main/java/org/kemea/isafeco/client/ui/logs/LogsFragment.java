package org.kemea.isafeco.client.ui.logs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.kemea.isafeco.client.databinding.FragmentLogsBinding;
import org.kemea.isafeco.client.utils.AppLogger;

public class LogsFragment extends Fragment {

    private FragmentLogsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLogsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLogs;
        textView.setText(AppLogger.getLogger().getLogs().stream().reduce("", (x, y) -> x + "\n" + y));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}