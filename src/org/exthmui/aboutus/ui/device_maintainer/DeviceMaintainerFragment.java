/*
 * Copyright (C) 2020 The exTHmUI Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.exthmui.aboutus.ui.device_maintainer;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.exthmui.aboutus.R;
import org.exthmui.aboutus.Utils;
import org.exthmui.aboutus.controller.AboutController;
import org.exthmui.aboutus.download.DownloadClient;
import org.exthmui.aboutus.model.ContributorInfo;
import org.exthmui.aboutus.model.MaintainerInfo;
import org.exthmui.aboutus.ui.OnlineImageView;
import org.exthmui.aboutus.ui.contributor.ContributorFragment;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DeviceMaintainerFragment extends Fragment {

    private static final String TAG = "DeviceMaintainerFragment";
    private View mView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_maintainer, container, false);
        getMaintainerList();
        //MaintainerInfo maintainer = AboutController.getInstance(getContext()).getMaintainer("col_or");
        //showContributorDialog(maintainer);
        return mView;
    }

    private void loadMaintainerList(File jsonFile)
            throws IOException, JSONException {
        Log.d(TAG, "Adding remote maintainer");
        AboutController controller = AboutController.getInstance(getContext());

        MaintainerInfo maintainers = Utils.parseJsonMaintainer(jsonFile);

        controller.addMaintainer(maintainers);
        MaintainerInfo maintainerInfo = controller.getMaintainer("col_or");

        OnlineImageView maintainerAvatar = mView.findViewById(R.id.maintainer_avatar);
        TextView maintainerName = mView.findViewById(R.id.maintainer_name);
        TextView maintainerSummary = mView.findViewById(R.id.maintainer_summary);
        TextView maintainer_maintainDevices = mView.findViewById(R.id.maintainer_maintain_devices);
        ImageButton coolapkButton = mView.findViewById(R.id.coolapk_action);
        ImageButton githubButton = mView.findViewById(R.id.github_action);
        ImageButton websiteButton = mView.findViewById(R.id.website_action);

        if (maintainerInfo.getSummary() == null) {
            maintainerSummary.setVisibility(View.GONE);
        } else {
            maintainerSummary.setText(maintainerInfo.getSummary());
        }
        if (maintainerInfo.getMaintainDevices() == null) {
            maintainer_maintainDevices.setVisibility(View.GONE);
        } else {
            maintainer_maintainDevices.setText(maintainerInfo.getMaintainDevices());
        }
        if (maintainerInfo.getAvatar() == null) {
            maintainerAvatar.setVisibility(View.GONE);
        } else {
            maintainerAvatar.setImageURL(maintainerInfo.getAvatar());
        }
        if (maintainerInfo.getName() == null) {
            maintainerName.setVisibility(View.GONE);
        } else {
            maintainerName.setText(maintainerInfo.getName());
        }
        if (maintainerInfo.getCoolapkUrl() == null) {
            coolapkButton.setVisibility(View.GONE);
        } else {
            coolapkButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(maintainerInfo.getCoolapkUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
        }

        if (maintainerInfo.getGithubUrl() == null) {
            githubButton.setVisibility(View.GONE);
        } else {
            githubButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(maintainerInfo.getGithubUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
        }

        if (maintainerInfo.getWebsiteUrl() == null) {
            websiteButton.setVisibility(View.GONE);
        } else {
            websiteButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(maintainerInfo.getWebsiteUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
        }

    }

    private void getMaintainerList() {
        File jsonFile = Utils.getCachedContributorsList(Objects.requireNonNull(getContext()));
        if (Utils.isNetworkAvailable(getContext())) {
            downloadMaintainerList();
        } else if (jsonFile.exists()) {
            try {
                loadMaintainerList(jsonFile);
                Log.d(TAG, "Cached contributor list parsed");
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error while parsing json list", e);
            }
        }
    }

    private void downloadMaintainerList() {
        final File jsonFile = Utils.getCachedMaintainerList(Objects.requireNonNull(getContext()));
        final File jsonFileTmp = new File(jsonFile.getAbsolutePath() + UUID.randomUUID());
        String url = Utils.getMaintainerListServerURL(getContext());
        Log.d(TAG, "Checking " + url);

        DownloadClient.DownloadCallback callback = new DownloadClient.DownloadCallback() {
            @Override
            public void onFailure(final boolean cancelled) {
                Log.e(TAG, "Could not download updates list");
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    if (!cancelled) {
                        if (jsonFile.exists()) {
                            try {
                                loadMaintainerList(jsonFile);
                                Log.d(TAG, "Cached contributor list parsed");
                            } catch (IOException | JSONException e) {
                                Log.e(TAG, "Error while parsing json list", e);
                                showToast(R.string.snack_contributors_check_failed, Toast.LENGTH_LONG);
                            }
                        }
                    }
                });
            }

            @Override
            public void onResponse(int statusCode, String url,
                                   DownloadClient.Headers headers) {
            }

            @Override
            public void onSuccess(File destination) {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    Log.d(TAG, "Update list downloaded");
                    processNewJson(jsonFile, jsonFileTmp);
                });
            }
        };

        final DownloadClient downloadClient;
        try {
            downloadClient = new DownloadClient.Builder()
                    .setUrl(url)
                    .setDestination(jsonFileTmp)
                    .setDownloadCallback(callback)
                    .build();
        } catch (IOException exception) {
            Log.e(TAG, "Could not build download client");
            showToast(R.string.snack_contributors_check_failed, Toast.LENGTH_LONG);
            return;
        }

        downloadClient.start();
    }

    private void processNewJson(File json, File jsonNew) {
        try {
            loadMaintainerList(jsonNew);
            jsonNew.renameTo(json);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Could not read json", e);
            showToast(R.string.snack_contributors_check_failed, Toast.LENGTH_LONG);
        }
    }

    public void showToast(int stringId, int duration) {
        Toast.makeText(getContext(), stringId, duration).show();
    }


}