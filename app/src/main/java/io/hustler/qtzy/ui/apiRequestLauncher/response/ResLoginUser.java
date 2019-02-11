package io.hustler.qtzy.ui.apiRequestLauncher.response;

import io.hustler.qtzy.ui.apiRequestLauncher.Base.BaseResponseUser;

public class ResLoginUser extends BaseResponseUser {
    private String fbAuthToken;
    private String sysAuthToken;

    public String getFbAuthToken() {
        return fbAuthToken;
    }

    public void setFbAuthToken(String fbAuthToken) {
        this.fbAuthToken = fbAuthToken;
    }

    public String getSysAuthToken() {
        return sysAuthToken;
    }

    public void setSysAuthToken(String sysAuthToken) {
        this.sysAuthToken = sysAuthToken;
    }
}
