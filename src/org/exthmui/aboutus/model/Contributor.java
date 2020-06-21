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

package org.exthmui.aboutus.model;

public class Contributor implements ContributorInfo {

    private String mId;
    private int mOrder;
    private String mJob;
    private String mAvatar;
    private String mName;
    private String mSummary;
    private String mImageUrl;
    private String mCoolapkUrl;
    private String mGithubUrl;
    private String mWebsiteUrl;

    public Contributor(ContributorInfo contributor) {
        mId = contributor.getId();
        mOrder = contributor.getOrder();
        mJob = contributor.getJob();
        mAvatar = contributor.getAvatar();
        mName = contributor.getName();
        mSummary = contributor.getSummary();
        mImageUrl = contributor.getImageUrl();
        mCoolapkUrl = contributor.getCoolapkUrl();
        mGithubUrl = contributor.getGithubUrl();
        mWebsiteUrl = contributor.getWebsiteUrl();
    }

    public Contributor(String id, int order, String job, String avatar, String name,
                       String summary, String imageUrl, String coolapkUrl, String githubUrl, String websiteUrl) {
        mId = id;
        mOrder = order;
        mJob = job;
        mAvatar = avatar;
        mName = name;
        mSummary = summary;
        mImageUrl = imageUrl;
        mCoolapkUrl = coolapkUrl;
        mGithubUrl = githubUrl;
        mWebsiteUrl = websiteUrl;
    }

    @Override
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        mOrder = order;
    }

    @Override
    public String getJob() {
        return mJob;
    }

    public void setJob(String job) {
        mJob = job;
    }

    @Override
    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    @Override
    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Override
    public String getCoolapkUrl() {
        return mCoolapkUrl;
    }

    public void setCoolapkUrl(String coolapkUrl) {
        mCoolapkUrl = coolapkUrl;
    }

    @Override
    public String getGithubUrl() {
        return mGithubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        mGithubUrl = githubUrl;
    }

    @Override
    public String getWebsiteUrl() {
        return mWebsiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        mWebsiteUrl = websiteUrl;
    }
}
