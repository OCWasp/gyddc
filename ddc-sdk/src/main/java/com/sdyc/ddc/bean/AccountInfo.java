package com.sdyc.ddc.bean;

public class AccountInfo {
    private String account;
    private String accountDID;
    private String accountName;
    private AccountRole accountRole;
    private String leaderDID;
    private PlatformState platformState;
    private OperatorState operatorState;
    private String field;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountDID() {
        return accountDID;
    }

    public void setAccountDID(String accountDID) {
        this.accountDID = accountDID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
    }

    public String getLeaderDID() {
        return leaderDID;
    }

    public void setLeaderDID(String leaderDID) {
        this.leaderDID = leaderDID;
    }

    public PlatformState getPlatformState() {
        return platformState;
    }

    public void setPlatformState(PlatformState platformState) {
        this.platformState = platformState;
    }

    public OperatorState getOperatorState() {
        return operatorState;
    }

    public void setOperatorState(OperatorState operatorState) {
        this.operatorState = operatorState;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
