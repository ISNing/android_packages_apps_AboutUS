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

package org.exthmui.aboutus.ui.contributor;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.exthmui.aboutus.R;
import org.exthmui.aboutus.Utils;
import org.exthmui.aboutus.controller.AboutController;
import org.exthmui.aboutus.download.DownloadClient;
import org.exthmui.aboutus.model.Contributor;
import org.exthmui.aboutus.model.ContributorInfo;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ContributorFragment extends Fragment {
    private static final String TAG = "ContributorFragment";

    private static ContributorFragment instance;
    private List<Contributor> ContributorList = new ArrayList<>();
    private ContributorAdapter mAdapter;
    private View mView;
    private RecyclerView mRecyclerView;
    private TextView mTitle;

    public ContributorFragment() {
        instance = this;
    }

    public static ContributorFragment getInstance() {
        return instance;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contributor, container, false);
        mRecyclerView = mView.findViewById(R.id.contributor_container);
        mAdapter = new ContributorAdapter();
        mAdapter.setAboutController(AboutController.getInstance(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTitle = mView.findViewById(R.id.contributors_title);
        getContributorsList();

        return mView;
    }

    private void loadContributorsList(File jsonFile)
            throws IOException, JSONException {
        Log.d(TAG, "Adding remote contributors");
        AboutController controller = AboutController.getInstance(getContext());

        List<ContributorInfo> contributors = Utils.parseJsonContributor(jsonFile);
        for (ContributorInfo contributor : contributors) {
            controller.addContributor(contributor);
        }


        List<String> contributorIds = new ArrayList<>();
        List<ContributorInfo> sortedContributors = controller.getContributors();
        if (sortedContributors.isEmpty()) {
            mView.findViewById(R.id.contributor_container).setVisibility(View.GONE);
        } else {
            mView.findViewById(R.id.contributor_container).setVisibility(View.VISIBLE);
            for (ContributorInfo contributor : sortedContributors) {
                contributorIds.add(contributor.getId());
            }
            mAdapter.setData(contributorIds);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getContributorsList() {
        File jsonFile = Utils.getCachedContributorsList(Objects.requireNonNull(getContext()));
        if (Utils.isNetworkAvailable(getContext())) {
            downloadContributorsList();
        } else if (jsonFile.exists()) {
            try {
                loadContributorsList(jsonFile);
                Log.d(TAG, "Cached contributor list parsed");
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error while parsing json list", e);
            }
        }
    }

    private void downloadContributorsList() {
        final File jsonFile = Utils.getCachedContributorsList(Objects.requireNonNull(getContext()));
        final File jsonFileTmp = new File(jsonFile.getAbsolutePath() + UUID.randomUUID());
        String url = Utils.getContributorsListServerURL(getContext());
        Log.d(TAG, "Checking " + url);

        DownloadClient.DownloadCallback callback = new DownloadClient.DownloadCallback() {
            @Override
            public void onFailure(final boolean cancelled) {
                Log.e(TAG, "Could not download updates list");
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    if (!cancelled) {
                        if (jsonFile.exists()) {
                            try {
                                loadContributorsList(jsonFile);
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
            loadContributorsList(jsonNew);
            jsonNew.renameTo(json);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Could not read json", e);
            showToast(R.string.snack_contributors_check_failed, Toast.LENGTH_LONG);
        }
    }

    public void showToast(int stringId, int duration) {
        Toast.makeText(getContext(), stringId, duration).show();
    }

    protected void showContributorDialog(ContributorInfo contributorInfo) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.contributor_dialog, null);
        ImageButton coolapkButton = view.findViewById(R.id.coolapk_action);
        ImageButton githubButton = view.findViewById(R.id.github_action);
        ImageButton websiteButton = view.findViewById(R.id.website_action);
        if (contributorInfo.getCoolapkUrl() == null) {
            coolapkButton.setVisibility(View.GONE);
        } else {
            coolapkButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(contributorInfo.getCoolapkUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
        }

        if (contributorInfo.getGithubUrl() == null) {
            githubButton.setVisibility(View.GONE);
        } else {
            githubButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(contributorInfo.getGithubUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
        }

        if (contributorInfo.getWebsiteUrl() == null) {
            websiteButton.setVisibility(View.GONE);
        } else {
            websiteButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(contributorInfo.getWebsiteUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
        }

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(view);
        dialog.show();
    }
}