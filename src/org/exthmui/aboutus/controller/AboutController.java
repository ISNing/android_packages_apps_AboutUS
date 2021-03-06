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

package org.exthmui.aboutus.controller;

import android.content.Context;
import android.util.Log;
import org.exthmui.aboutus.model.Contributor;
import org.exthmui.aboutus.model.ContributorInfo;
import org.exthmui.aboutus.model.Maintainer;
import org.exthmui.aboutus.model.MaintainerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutController {

    private static AboutController sAboutController;
    private final String TAG = "AboutController";
    private final Context mContext;
    private Map<String, Contributor> mContributors = new HashMap<>();
    private Map<String, Maintainer> mMaintainer = new HashMap<>();

    private AboutController(Context context) {
        mContext = context.getApplicationContext();
    }

    public static synchronized AboutController getInstance(Context context) {
        if (sAboutController == null) {
            sAboutController = new AboutController(context);
        }
        return sAboutController;
    }

    public boolean addContributor(final ContributorInfo contributorInfo) {
        Log.d(TAG, "Adding contributor: " + contributorInfo.getId());
        if (mContributors.containsKey(contributorInfo.getId())) {
            Log.d(TAG, "Contributor (" + contributorInfo.getId() + ") already added");
            Contributor contributorAdded = mContributors.get(contributorInfo.getId());
            return false;
        }
        Contributor contributor = new Contributor(contributorInfo);
        mContributors.put(contributor.getId(), contributor);
        return true;
    }

    public boolean addMaintainer(final MaintainerInfo maintainerInfo) {
        Log.d(TAG, "Adding maintainer: " + maintainerInfo.getId());
        if (mMaintainer.containsKey(maintainerInfo.getId())) {
            Log.d(TAG, "Maintainer (" + maintainerInfo.getId() + ") already added");
            Maintainer maintainerAdded = mMaintainer.get(maintainerInfo.getId());
            return false;
        }
        Maintainer maintainer = new Maintainer(maintainerInfo);
        mMaintainer.put(maintainer.getId(), maintainer);
        return true;
    }

    public List<ContributorInfo> getContributors() {
        return new ArrayList<ContributorInfo>(mContributors.values());
    }

    public ContributorInfo getContributor(String id) {
        return mContributors.get(id);
    }

    public List<MaintainerInfo> getMaintainer() {
        return new ArrayList<MaintainerInfo>(mMaintainer.values());
    }

    public MaintainerInfo getMaintainer(String id) {
        return mMaintainer.get(id);
    }
}