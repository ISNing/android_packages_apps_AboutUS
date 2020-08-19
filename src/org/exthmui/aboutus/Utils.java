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

package org.exthmui.aboutus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import org.exthmui.aboutus.model.Contributor;
import org.exthmui.aboutus.model.ContributorInfo;
import org.exthmui.aboutus.model.Maintainer;
import org.exthmui.aboutus.model.MaintainerInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Utils {

    private static final String TAG = "Utils";

    public static List<ContributorInfo> parseJsonContributor(File file)
            throws IOException, JSONException {
        List<ContributorInfo> contributors = new ArrayList<>();

        StringBuilder json = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                json.append(line);
            }
        }

        JSONObject obj = new JSONObject(json.toString());
        JSONArray contributorsList = obj.getJSONArray("response");
        for (int i = 0; i < contributorsList.length(); i++) {
            if (contributorsList.isNull(i)) {
                continue;
            }
            try {
                ContributorInfo contributor = parseJsonContributor(contributorsList.getJSONObject(i));
                contributors.add(contributor);
            } catch (JSONException e) {
                Log.e(TAG, "Could not parse contributor object, index=" + i, e);
            }
        }
        /*

         * Do not put any same order contributors in the json file!!!!!!!!

         */
        //对id进行排序 大>小
        Log.d(TAG, "Sorting updates(list).");
        contributors.sort(Comparator.comparing(ContributorInfo::getOrder));
        return contributors;
    }

    public static MaintainerInfo parseJsonMaintainer(File file)
            throws IOException, JSONException {
        List<MaintainerInfo> maintainer = new ArrayList<>();

        StringBuilder json = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                json.append(line);
            }
        }

        JSONObject obj = new JSONObject(json.toString());
        JSONObject maintainerList = obj.getJSONObject("data");
        MaintainerInfo maintainer1 = parseJsonMaintainer(maintainerList);
//        for (int i = 0; i < maintainerList.length(); i++) {
//            if (maintainerList.isNull(i)) {
//                continue;
//            }
//            try {
//                MaintainerInfo maintainer1 = parseJsonMaintainer(maintainerList.getJSONObject(i));
//                maintainer.add(maintainer1);
//            } catch (JSONException e) {
//                Log.e(TAG, "Could not parse contributor object, index=" + i, e);
//            }
//        }
        /*

         * Do not put any same order contributors in the json file!!!!!!!!

         */
        //对id进行排序 大>小
        //Log.d(TAG, "Sorting updates(list).");
        //contributors.sort(Comparator.comparing(MaintainerInfo::getOrder));
        return maintainer1;
    }

    // This should really return an ContributorInfo object, but currently this only
    // used to initialize contributorInfo objects
    private static ContributorInfo parseJsonContributor(JSONObject object) throws JSONException {
        Contributor contributor = new Contributor(object.getString("id"), object.getInt("order"),
                object.getString("job"), object.getString("avatar"), object.getString("name"),
                object.getString("summary"), object.getString("imageUrl"), null, null, null);
        try {
            contributor.setCoolapkUrl(object.getString("coolapkUrl"));
        } catch (JSONException ignored) {
        }
        try {
            contributor.setGithubUrl(object.getString("githubUrl"));
        } catch (JSONException ignored) {
        }
        try {
            contributor.setWebsiteUrl(object.getString("websiteUrl"));
        } catch (JSONException ignored) {
        }
        return contributor;
    }

    // This should really return an MaintainerInfo object, but currently this only
    // used to initialize maintainerInfo objects
    private static MaintainerInfo parseJsonMaintainer(JSONObject object) throws JSONException {
        Maintainer maintainer = new Maintainer(object.getString("avatar"), object.getString("id"),
                object.getString("summary"), object.getString("name"), object.getString("maintaindevices"),
                object.getString("imageUrl"), null, null, null);
        try {
            maintainer.setCoolapkUrl(object.getString("coolapkUrl"));
        } catch (JSONException ignored) {
        }
        try {
            maintainer.setGithubUrl(object.getString("githubUrl"));
        } catch (JSONException ignored) {
        }
        try {
            maintainer.setWebsiteUrl(object.getString("websiteUrl"));
        } catch (JSONException ignored) {
        }
        return maintainer;
    }

    //To get the file location in ContributorsList
    public static File getCachedContributorsList(Context context) {
        return new File(context.getCacheDir(), "contributors.json");
    }

    //To get the file location in MaintainerList
    public static File getCachedMaintainerList(Context context) {
        return new File(context.getCacheDir(), "maintainer.json");
    }

    //TODO: 改返回值
    public static String getContributorsListServerURL(Context context) {
        //return context.getString(R.string.aboutus_server_url);
        return "http://karslr.apocalypse.icu/exthmui/about_us_server/contributors.json";
    }

    //TODO: 改返回值
    public static String getMaintainerListServerURL(Context context) {
        //return context.getString(R.string.aboutus_server_url);
        return "http://karslr.apocalypse.icu/exthmui/about_us_server/maintainer.json";
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        NetworkCapabilities nc = cm.getNetworkCapabilities(network);
        try{
            return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) |
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) |
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) |
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }catch (NullPointerException e){
            return false;
        }
    }

    public static ViewGroup.LayoutParams getLayoutParams(int tw, int th, View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = tw;
        params.height = th;
        return params;
    }
}
