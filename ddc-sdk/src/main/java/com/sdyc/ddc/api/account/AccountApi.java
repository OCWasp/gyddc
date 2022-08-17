package com.sdyc.ddc.api.account;

import com.google.common.collect.Multimap;
import com.radiance.tonclient.Abi;
import com.radiance.tonclient.Crypto;
import com.radiance.tonclient.Processing;
import com.sdyc.ddc.api.ApiBase;
import com.sdyc.ddc.bean.AccountInfo;
import com.sdyc.ddc.bean.DDCResponse;
import com.sdyc.ddc.bean.State;
import com.sdyc.ddc.utils.CommonUtils;
import com.sdyc.ddc.utils.KeyGenerator;
import com.sdyc.ddc.utils.PropertiesReader;
import net.sf.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountApi extends ApiBase {
    protected static AccountApi accountApi;

    public static AccountApi getInstance() {
        synchronized (AccountApi.class) {
            if(accountApi == null){
                accountApi = new AccountApi();
            }
        }
        return accountApi;
    }

    public DDCResponse addAccountByPlatform (String sender, String account, String accountName, String accountDID,
                                             Crypto.KeyPair keyPair) {
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        DDCResponse checkAds1Res = checkAds1(sender, "account");
        if(!checkAds1Res.isSuccess())
            return checkAds1Res;

        if("".equals(accountName) || accountName == null)
            DDCResponse.error("accountName为空");


        if(!checkAvailableAndRole(senderAccAds, 1))
            DDCResponse.error("调用者账户不是平台方");

        AccountInfo senderAccInfo = getAccount(sender);
        if(!sendTransaction(sender, authority,gas12+gasBase, keyPair,true))
            return DDCResponse.error("支付gas失败");

        JSONObject input = new JSONObject();
        input.put("account", account);
        input.put("accountName", accountName);
        input.put("accountDID", accountDID);
        input.put("leaderDID", senderAccInfo.getAccountDID());
        input.put("gasTo", sender);
        Processing.ResultOfProcessMessage callRes =
                callFun(authority,"addAccountByPlatform",input,keyPair,authorityAbi);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse addBatchAccountByPlatform(String sender, List<AccountInfo> accounts, Crypto.KeyPair keyPair) {
        if(accounts.size() > 200)
            return DDCResponse.error("accounts最大长度200");
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAvailableAndRole(senderAccAds,1))
            return DDCResponse.error("调用者账户不是平台方");
        int count = 0;
        List<String> accountsList = new ArrayList<>();
        List<String> accountNameList = new ArrayList<>();
        List<String> accountDIDsList = new ArrayList<>();
        for (AccountInfo accountInfo: accounts) {
            count ++;

            DDCResponse checkAds1Res = checkAds1(accountInfo.getAccount(), "account");
            if(!checkAds1Res.isSuccess())
                return checkAds1Res;

            if(!checkAds1(accountInfo.getAccount(), "").isSuccess())
                return DDCResponse.error(checkAds1(accountInfo.getAccount(), "第" + count + "条添加").getMessage());
            if("".equals(accountInfo.getAccountName()) || accountInfo.getAccountName() == null)
                return DDCResponse.error("第" + count + "条添加账户名称为空");
            if("".equals(accountInfo.getAccountDID()) || accountInfo.getAccountDID() == null)
                return DDCResponse.error("第" + count + "条添加账户accountDID为空");
            accountsList.add(accountInfo.getAccount());
            accountNameList.add(accountInfo.getAccountName());
            accountDIDsList.add(accountInfo.getAccountDID());
        }
        if(!sendTransaction(sender, authority,
                (Integer.parseInt(gas12) * accounts.size()) + gasBase,
                keyPair,true))
            return DDCResponse.error("支付gas失败");
        AccountInfo senderAccInfo = getAccount(sender);
        JSONObject input = new JSONObject();
        input.put("accounts", accountsList.toArray());
        input.put("accountNames", accountNameList.toArray());
        input.put("accountDIDs", accountDIDsList.toArray());
        input.put("leaderDID", senderAccInfo.getAccountDID());
        input.put("gasTo", sender);
        Processing.ResultOfProcessMessage callRes =
                callFun(authority,"addBatchAccountByPlatform",input,keyPair,authorityAbi);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse setSwitcherStateOfPlatform(String sender, boolean isOpen, Crypto.KeyPair keyPair) {
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAvailableAndRole(senderAccAds,0))
            return DDCResponse.error("调用者不是运营方");
        if(!sendTransaction(sender,authority,gas12+gasBase,keyPair,true))
            return DDCResponse.error("支付gas失败");

        JSONObject input = new JSONObject();
        input.put("isOpen", isOpen);
        Processing.ResultOfProcessMessage callRes =
                callFun(authority,"setSwitcherStateOfPlatform",input,keyPair,authorityAbi);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse switcherStateOfPlatform(){
        JSONObject runRes = runGet(authority,"switcherStateOfPlatform",null,authorityAbi,null);
        return DDCResponse.success(runRes.getJSONObject("decoded").getJSONObject("output").getString("value0"));
    }

    private DDCResponse addOperator(String sender, String operator, String accountName, String accountDID, Crypto.KeyPair keyPair) {
        if(!sendTransaction(sender,authority,gas12+gasBase,keyPair,true))
            return DDCResponse.error("支付gas失败");

        JSONObject input = new JSONObject();
        input.put("operator", operator);
        input.put("accountName", accountName);
        input.put("accountDID", accountDID);
        input.put("gasTo", sender);
        Processing.ResultOfProcessMessage callRes = callFun(authority,"addOperator",input,keyPair,authorityAbi);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse addAccountByOperator (String sender,String account,String accountName,String accountDID,String leaderDID, Crypto.KeyPair keyPair) {
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAds1(account, "").isSuccess())
            return checkAds1(account, "account");

        if("".equals(accountName) || accountName == null)
            return DDCResponse.error("accountName为空");

        if(!checkAvailableAndRole(senderAccAds, 0))
            return DDCResponse.error("调用者不是运营方");

        if(!sendTransaction(sender,authority,gas12+gasBase,keyPair,true))
            return DDCResponse.error("支付gas失败");

        JSONObject input = new JSONObject();
        input.put("account", account);
        input.put("accountName", accountName);
        input.put("accountDID", accountDID);
        input.put("leaderDID", (leaderDID == null || "".equals(leaderDID)) ? " " : leaderDID);
        input.put("addPlatform", (leaderDID == null || "".equals(leaderDID)));
        input.put("gasTo", sender);
        Processing.ResultOfProcessMessage callRes =
                callFun(authority,"addAccountByOperator",input,keyPair,authorityAbi);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse addBatchAccountByOperator(String sender, List<AccountInfo> accounts, Crypto.KeyPair keyPair) throws Exception{
        if(accounts.size() > 200)
            return DDCResponse.error("accounts最大长度200");
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;
        if(!checkAvailableAndRole(senderAccAds,0))
            return DDCResponse.error("调用者不是运营方");
        int count = 0;
        List<String> accountsList = new ArrayList<>();
        List<String> accountNameList = new ArrayList<>();
        List<String> accountDIDsList = new ArrayList<>();
        List<String> leaderDIDsList = new ArrayList<>();
        for (AccountInfo accountInfo: accounts) {
            count ++;
            if(!checkAds1(accountInfo.getAccount(), "").isSuccess())
                return DDCResponse.error(checkAds1(accountInfo.getAccount(), "第" + count + "条添加").getMessage());
            if("".equals(accountInfo.getAccountName()) || accountInfo.getAccountName() == null)
                return DDCResponse.error("第" + count + "条添加账户名称为空");
            if("".equals(accountInfo.getAccountDID()) || accountInfo.getAccountDID() == null)
                return DDCResponse.error("第" + count + "条添加账户accountDID为空");
            accountsList.add(accountInfo.getAccount());
            accountNameList.add(accountInfo.getAccountName());
            accountDIDsList.add(accountInfo.getAccountDID());
            leaderDIDsList.add((accountInfo.getLeaderDID()==null || "".equals(accountInfo.getLeaderDID())) ? " " : accountInfo.getLeaderDID());
        }
        if(!sendTransaction(sender,authority,(Integer.parseInt(gas12) * accounts.size()) + gasBase,keyPair,true))
            return DDCResponse.error("支付gas失败");

        JSONObject input = new JSONObject();
        input.put("accounts", accountsList.toArray());
        input.put("accountNames", accountNameList.toArray());
        input.put("accountDIDs", accountDIDsList.toArray());
        input.put("leaderDIDs", leaderDIDsList.toArray());
        input.put("addPlatform", (accounts.get(0).getLeaderDID() == null || "".equals(accounts.get(0).getLeaderDID())));
        input.put("gasTo", sender);
        Processing.ResultOfProcessMessage callRes =
                callFun(authority,"addBatchAccountByOperator",input,keyPair,authorityAbi);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse updateAccState (String sender, String account, State state, boolean changePlatformState, Crypto.KeyPair keyPair) {
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAds1(account, "").isSuccess())
            return checkAds1(account, "account");

        String accountAds = getAccountAddress(account);
        if(accountAds == null)
            return DDCResponse.error("account账户不存");

        AccountInfo senderAcc = getAccount(sender);
        AccountInfo accountAcc = getAccount(account);
        if(!checkAvailableAndRole(senderAccAds, 0) && senderAcc.getAccountDID()!=accountAcc.getLeaderDID())
            return DDCResponse.error("调用者不是运营方或调用者不是account上级");

        if(!sendTransaction(sender,authority,gas12+gasBase,keyPair,true))
            return DDCResponse.error("支付gas失败");
        JSONObject input = new JSONObject();
        input.put("account", account);
        input.put("state", state.equals(State.Frozen)?1:2);
        input.put("changePlatformState", changePlatformState);
        input.put("senderRole", checkAvailableAndRole(senderAccAds, 1) ? 1 : 0 );
        input.put("gasTo", sender);
        Processing.ResultOfProcessMessage callRes =
                callFun(authority,"updateAccountState",input,null,authorityAbi);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse recharge(String sender, String to, BigInteger amount, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        JSONObject input = new JSONObject();
        input.put("to", to);
        input.put("amount", amount);
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(sender, keyPair, gas12+gasBase, senderAccAds, "recharge", input, keyPair, accountAbi, 1);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse rechargeBatch (String sender, Multimap<String, BigInteger> list, Crypto.KeyPair keyPair) throws Exception{
        if(list.size() > 200)
            return DDCResponse.error("list最大长度200");
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        List<String> toList = new ArrayList<>();
        List<BigInteger> amounts = new ArrayList<>();
        BigInteger sumAmount = BigInteger.valueOf(0);
        AccountInfo senderAccount = getAccount(sender);
        int count = 0;
        for (Map.Entry<String, BigInteger> e : list.entries()) {
            count ++;
            String account = e.getKey();
            BigInteger amount = e.getValue();
            sumAmount.add(amount);
            if(amount.compareTo(BigInteger.valueOf(0)) != 1)
                return DDCResponse.error(account + ":充值的业务费额为0");
            if(Ads0.equals(account))
                return DDCResponse.error("第" + count + "条接受者账户为0地址");
            if(sender.equals(account))
                return DDCResponse.error("第" + count + "条接收者账户与转出者账户相同");
            String toAccAds = getAccountAddress(account);
            DDCResponse checkAccAds2 = checkAccAds(toAccAds,"第" + count + "条接收者");
            if(!checkAccAds2.isSuccess())
                return checkAccAds2;
            AccountInfo toAccount = getAccount(account);
            if(checkAvailableAndRole(senderAccAds,0)
                    || (senderAccount.getAccountDID().equals(toAccount.getLeaderDID()))
                    || (senderAccount.getLeaderDID().equals(toAccount.getLeaderDID())
                    && senderAccount.getAccountDID().equals(toAccount.getAccountDID())
                    && checkAvailableAndRole(toAccAds, 2))){
                toList.add(account);
                amounts.add(amount);
                continue;
            } else {
                return DDCResponse.error("第" + count + "条转账信息不满足：运营方给平台方/终端用户充值 、平台方给平台方/终端用户充值 的要求");
            }
        }
        BigInteger balance = balanceOf(sender);
        if(balance.compareTo(sumAmount) == -1){
            return DDCResponse.error("业务费余额不足，当前业务费："+balance+", 需要业务费："+sumAmount);
        }

        JSONObject input = new JSONObject();
        input.put("toList", toList.toArray());
        input.put("amounts", amounts.toArray());
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(sender, keyPair, (Integer.parseInt(gas12) * list.size()) + gasBase,
                senderAccAds, "rechargeBatch", input, null, accountAbi, 1);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public BigInteger balanceOf(String accAddr){
        String accountAddress = getAccountAddress(accAddr);
        if(accountAddress == null){
            return null;
        }
        JSONObject runRes = runGet(accountAddress,"balance",null,accountAbi,null);
        if(runRes == null){
            return null;
        }
        return BigInteger.valueOf(CommonUtils.hexStrToLong(runRes.getJSONObject("decoded").getJSONObject("output").getString("value0")));
    }

    public DDCResponse balanceOfBatch(List<String> accAddrs) {
        if(accAddrs.size() > 200)
            return DDCResponse.error("ddcs最大长度200");
        List<BigInteger> res = new ArrayList<>();
        for (String account : accAddrs) {
            String accountAddress = getAccountAddress(account);
            if(accountAddress == null){
                res.add(BigInteger.valueOf(0));
            }else{
                JSONObject runRes = runGet(accountAddress,"balance",null,accountAbi,null);
                if(runRes == null){
                    res.add(BigInteger.valueOf(0));
                }else{
                    res.add(BigInteger.valueOf(CommonUtils.hexStrToLong(runRes.getJSONObject("decoded").getJSONObject("output").getString("value0"))));
                }
            }
        }
        return DDCResponse.success(res);
    }

    public DDCResponse selfRecharge(String sender, BigInteger amount, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(amount.compareTo(new BigInteger("0")) != 1)
            return DDCResponse.error("amount必须大于零");
        if(!checkAvailableAndRole(senderAccAds, 0)){
            return DDCResponse.error("只有运营方有给自己的充值权限");
        }
        JSONObject input = new JSONObject();
        input.put("amount", amount);
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(sender, keyPair, gas12+gasBase, senderAccAds,"selfRecharge", input, keyPair, accountAbi, 1);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse setApprovalForAll (String sender, String operator, boolean approved, String operator2, Crypto.KeyPair keyPair) throws Exception{
        if(sender.equals(operator)){
            return DDCResponse.error("调用者账户与授权者账户属于同一账户");
        }
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        String operatorAccAds = getAccountAddress(operator);
        DDCResponse checkAdsRes2 = checkAds2(operator, operatorAccAds, "授权者");
        if(!checkAdsRes2.isSuccess())
            return checkAdsRes2;

        if(!onePlatformCheck(sender,operator)){
            return DDCResponse.error("调用者账户与授权者账户不属于同平台");
        }

        // 方法调用权限
        DDCResponse hasPerRes = hasPermission(sender, senderAccAds, null, setApprovalForAllSig);
        if(!hasPerRes.isSuccess())
            return hasPerRes;

        AccountApi accountApi = AccountApi.getInstance();
        DDCResponse payRes = accountApi.pay(operator2, keyPair,collection,queryFee(collection,setApprovalForAllSig), setApprovalForAllSig,0 );
        System.out.println("payRes:"+payRes);
        if(!payRes.isSuccess())
            return payRes;

        JSONObject input = new JSONObject();
        input.put("operator", operator);
        input.put("approved", approved);
        Processing.ResultOfProcessMessage callRes =
            callInternalFun(operator2, keyPair,gas12+gasBase, senderAccAds,
                "setApprovalForAll", input, null, accountAbi, 1);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse isApprovedForAll (String owner, String operator) {
        if(!checkAds1(owner, "").isSuccess())
            return checkAds1(owner, "拥有者");

        if(!checkAds1(operator,"").isSuccess())
            return checkAds1(operator,"授权者");

        String accountAddress = getAccountAddress(owner);
        if(accountAddress == null)
            return DDCResponse.error("拥有者账户不存在");
        JSONObject input = new JSONObject();
        input.put("operator", operator);
        JSONObject runRes = runGet(accountAddress,"isApprovedForAll",input,accountAbi,null);
        JSONObject output = runRes.getJSONObject("decoded").getJSONObject("output");
        DDCResponse res = new DDCResponse();
        res.setSuccess(true);
        res.setData(output.getBoolean("value0"));
        return res;
    }

    public DDCResponse delAccount(String sender, Crypto.KeyPair senderKeys, String account){
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAds1(account,"").isSuccess())
            return checkAds1(account,"account");

        if(!checkAvailableAndRole(senderAccAds,0))
            return DDCResponse.error("调用者不是运营方");
        if(!sendTransaction(sender, authority, gas12+gasBase, senderKeys, true))
            return DDCResponse.error("支付gas失败");

        JSONObject input = new JSONObject();
        input.put("account", account);
        input.put("gasTo", sender);
        Processing.ResultOfProcessMessage callRes =
            callFun(authority,"delAccount",input,senderKeys,authorityAbi);
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse pay(String sender, Crypto.KeyPair keyPair, String collectionAds, BigInteger amount, int sig, int ddcId){
        if(!checkAds1(collectionAds, "接收者业务费").isSuccess())
            return checkAds1(collectionAds, "接收者业务费");

        if (amount.compareTo(new BigInteger("0")) == 0 )
            return DDCResponse.success("无需业务费");
//        BigInteger balance = balanceOf(sender);
//        if (balance.compareTo(amount) == -1 )
//            return DDCResponse.error("业务费不足");
        String accountAddress = getAccountAddress(sender);
        if(!sendTransaction(sender,accountAddress,gas12+gasBase,keyPair,true))
            return DDCResponse.error("支付gas失败");

        JSONObject input = new JSONObject();
        input.put("amount", amount);
        input.put("collection", collectionAds);
        input.put("sig", sig);
        input.put("ddcId", ddcId);
        input.put("gasTo", sender);
        callFun(accountAddress,"pay",input,keyPair,accountAbi);
        return DDCResponse.success();
    }

    public DDCResponse checkFee(String sender, Crypto.KeyPair keyPair, String collectionAds, BigInteger amount, int sig, int ddcId) {
        if(!checkAds1(collectionAds, "接收者业务费").isSuccess())
            return checkAds1(collectionAds, "接收者业务费");

        if (amount.compareTo(new BigInteger("0")) == 0 )
            return DDCResponse.success("无需业务费");
        BigInteger balance = balanceOf(sender);
        if (balance.compareTo(amount) == -1 )
            return DDCResponse.error("业务费不足");
        return DDCResponse.success();
    }

    public DDCResponse safeMultisigWallet(String ownerAds, Crypto.KeyPair keyPair) throws Exception {
        // 随机生产秘钥
        Crypto.KeyPair keys = KeyGenerator.getKey();

        Abi.ABI abi = CommonUtils.abiFromResource("/Wallet_5a33.abi.json");
        Abi.DeploySet deploySet = new Abi.DeploySet(CommonUtils.tvcFromResource("/Wallet_5a33.tvc"));
        // 构造参数
        Abi.CallSet callSet = new Abi.CallSet("constructor", null, null);

        Abi.ResultOfEncodeMessage message = abiModule.encodeMessage(abi,null, deploySet, callSet, new Abi.Signer.Keys(keys),null).get();
        String deployAddress = message.getAddress();

        if(!sendTransaction(ownerAds, deployAddress,gas10+gasBase, keyPair, false))
            return DDCResponse.error("支付gas失败");
        processing.sendMessage(message.getMessage(), abi, true,event -> {}).get();
        JSONObject result = new JSONObject();
        result.put("address", deployAddress);
        result.put("keys", keys);
        return DDCResponse.success(result);
    }

    public DDCResponse setTransable(String sender, String account, boolean flag, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAds1(account,"").isSuccess())
            return checkAds1(account,"account");

        if(!checkAvailableAndRole(senderAccAds,0))
            return DDCResponse.error("调用者不是运营方");
        if(!sendTransaction(sender, account, gas10+gasBase, keyPair, true))
            return DDCResponse.error("支付gas失败");
        Crypto.KeyPair setTrKeys = new Crypto.KeyPair(
                PropertiesReader.config.getString("setTransable_pub"),
                PropertiesReader.config.getString("setTransable_sec")
        );
        JSONObject input = new JSONObject();
        input.put("ok", flag);
        Processing.ResultOfProcessMessage callRes =
                callFun(account,"setTransable",input,setTrKeys,CommonUtils.abiFromResource("/Wallet_5a33.abi.json"));
        return callRes == null
                ?DDCResponse.error("调用合约失败")
                :DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }
}
