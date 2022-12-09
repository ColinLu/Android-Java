package com.colin.library.android.http.progress;

import androidx.annotation.MainThread;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 20:33
 * <p>
 * 描述： TODO
 */
public interface IProgress {
  /*网络请求 进度提示*/
  @MainThread
  default void progress(float total, float progress) {
  }
}
