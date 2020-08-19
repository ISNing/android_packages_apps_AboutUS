package org.exthmui.aboutus.model;

public class Maintainer implements MaintainerInfo {

    private String mAvatar;
    private String mId;
    private String mSummary;
    private String mName;
    private String mMaintainDevices;
    private String mImageUrl;
    private String mCoolapkUrl;
    private String mGithubUrl;
    private String mWebsiteUrl;

    public Maintainer(MaintainerInfo maintainer) {
        mAvatar = maintainer.getAvatar();
        mId = maintainer.getId();
        mSummary = maintainer.getSummary();
        mName = maintainer.getName();
        mMaintainDevices = maintainer.getMaintainDevices();
        mImageUrl = maintainer.getImageUrl();
        mCoolapkUrl = maintainer.getCoolapkUrl();
        mGithubUrl = maintainer.getGithubUrl();
        mWebsiteUrl = maintainer.getWebsiteUrl();
    }

    public Maintainer(String avatar, String id, String summary, String name, String maintaindevices,
                       String imageUrl, String coolapkUrl, String githubUrl, String websiteUrl) {
        mAvatar = avatar;
        mId = id;
        mSummary = summary;
        mName = name;
        mMaintainDevices = maintaindevices;
        mImageUrl = imageUrl;
        mCoolapkUrl = coolapkUrl;
        mGithubUrl = githubUrl;
        mWebsiteUrl = websiteUrl;
    }
    @Override
    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    @Override
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getMaintainDevices() {
        return mMaintainDevices;
    }

    public void setMaintainDevices(String maintainDevices) {
        mMaintainDevices = maintainDevices;
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
