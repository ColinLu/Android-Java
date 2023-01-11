/*
 * Copyright 2017 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.colin.library.android.media.def;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2019-12-24 09:58
 * <p>
 * 描述：扫描结束返回类
 */
public final class MediaResult {
    @NonNull
    public final List<MediaFolder> mList;
    @NonNull
    public final List<MediaFile> mSelected;

    public MediaResult() {
        this.mList = new ArrayList<>();
        this.mSelected = new ArrayList<>();
    }

    public MediaResult(@NonNull List<MediaFolder> list, @NonNull List<MediaFile> selected) {
        this.mList = list;
        this.mSelected = selected;
    }
}