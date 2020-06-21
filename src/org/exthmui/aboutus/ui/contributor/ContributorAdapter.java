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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.exthmui.aboutus.AboutActivity;
import org.exthmui.aboutus.R;
import org.exthmui.aboutus.Utils;
import org.exthmui.aboutus.controller.AboutController;
import org.exthmui.aboutus.model.ContributorInfo;
import org.exthmui.aboutus.ui.OnlineImageView;

import java.util.List;

public class ContributorAdapter extends RecyclerView.Adapter<ContributorAdapter.ViewHolder> {

    private List<String> mContributorIds;
    private AboutController mAboutController;

    public void setAboutController(AboutController aboutController) {
        mAboutController = aboutController;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mContributorIds == null ? 0 : mContributorIds.size();
    }

    public void setData(List<String> contributorIds) {
        mContributorIds = contributorIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.contributor_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    public void notifyItemChanged(String contributorId) {
        if (mContributorIds == null) {
            return;
        }
        notifyItemChanged(mContributorIds.indexOf(contributorId));
    }

    public void removeItem(String contributorId) {
        if (mContributorIds == null) {
            return;
        }
        int position = mContributorIds.indexOf(contributorId);
        mContributorIds.remove(contributorId);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (mContributorIds == null) {
            return;
        }

        final String contributorId = mContributorIds.get(i);
        ContributorInfo contributor = mAboutController.getContributor(contributorId);
        viewHolder.mContributorName.setText(contributor.getName());
        viewHolder.mContributorSummary.setText(contributor.getSummary());
        viewHolder.mAvatarView.setImageURL(contributor.getAvatar());
        viewHolder.mImageView.setImageURL(contributor.getImageUrl());
        viewHolder.mImageView.setCallback(new OnlineImageView.OnlineImageViewCallback() {
            @Override
            public void OnSuccess() {
                AboutActivity.getInstance().runOnUiThread(() -> {
                    int h = viewHolder.mImageView.getMeasuredHeight();
                    int w = (viewHolder.mImageView.getImageHeight() == 0 ? 0 : (h * viewHolder.mImageView.getImageWidth() /
                            viewHolder.mImageView.getImageHeight()));
                    viewHolder.mImageView.setLayoutParams(Utils.getLayoutParams(w, h, viewHolder.mImageView));
                });
            }

            @Override
            public void OnError() {
            }
        });

        viewHolder.mImageView.setImageURL(contributor.getImageUrl());

        if (contributor.getCoolapkUrl() == null && contributor.getGithubUrl() == null && contributor.getWebsiteUrl() == null) {
            viewHolder.mContributorAction.setVisibility(View.GONE);
        } else {
            viewHolder.mContributorAction.setOnClickListener(v -> {
                ContributorFragment.getInstance().showContributorDialog(contributor);
            });
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mContributorName;
        private final TextView mContributorSummary;
        private final OnlineImageView mAvatarView;
        private final OnlineImageView mImageView;
        private final ImageButton mContributorAction;

        public ViewHolder(View view) {
            super(view);
            mContributorName = view.findViewById(R.id.contributor_title);
            mContributorSummary = view.findViewById(R.id.contributor_summary);
            mAvatarView = view.findViewById(R.id.contributor_avatar);
            mImageView = view.findViewById(R.id.contributor_image);
            mContributorAction = view.findViewById(R.id.contributor_action);
        }

    }
}
