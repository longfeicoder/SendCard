package com.troy.sendcard.interactor.Impl;

import com.troy.sendcard.SendCardApp;
import com.troy.sendcard.bean.request.HandledParam;
import com.troy.sendcard.bean.respone.HandledResult;
import com.troy.sendcard.config.Constant;
import com.troy.sendcard.http.api.HandledApi;
import com.troy.sendcard.interactor.IHandledInteractor;
import com.troy.sendcard.util.SignUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chenlongfei on 16/4/12.
 */
public class HandledInteractorImpl implements IHandledInteractor {
    private IHandledInteractor.HandledListener mHandledListener;

    public HandledInteractorImpl(HandledListener handledListener) {
        mHandledListener = handledListener;
    }

    @Override
    public void getHandledList(int page) {
        SendCardApp.UserInfo userInfo = new SendCardApp.UserInfo();
        HandledParam param = new HandledParam();
        param.setUser_id(userInfo.getUserId());
        param.setSecret(userInfo.getSecret());
        param.setTime(System.currentTimeMillis() + "");
        param.setPage(page + "");
        param.setPageSize(Constant.PAGE_SIZE);
        param.setSign(SignUtil.sign(param.createUnhandledParam()));

        HandledApi.getApi().getService().getResult(param.createUnhandledParam())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HandledResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mHandledListener.onFail();
                    }

                    @Override
                    public void onNext(HandledResult handledResult) {
                        if (handledResult.getCode() == Constant.ResponeStatus.OK) {
                            mHandledListener.onSuccess(handledResult.getData());
                        } else {
                            mHandledListener.onFail();
                        }
                    }
                });
    }
}