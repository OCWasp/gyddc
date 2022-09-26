package com.sdyc.ddc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.radiance.tonclient.*;
import com.sdyc.ddc.bean.*;
import com.sdyc.ddc.utils.CommonUtils;
import com.sdyc.ddc.utils.PropertiesReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiBase {
    protected static TONContext context;
    public static Net net;
    protected static Crypto crypto;
    protected static Processing processing;
    public static Abi abiModule;
    protected static Tvm tvm;
    // address
    protected static String Ads0 = "0:0000000000000000000000000000000000000000000000000000000000000000";
    public static String authority = PropertiesReader.config.getString("authority");
    protected static String bsnOperator = PropertiesReader.config.getString("bsnOperator");
    public static String collection = PropertiesReader.config.getString("collection");
    public static String depolyWallet = PropertiesReader.config.getString("depolyWallet");
    // abi
    public static Abi.ABI collectionAbi = CommonUtils.abiFromResource("/Collection.abi.json");
    public static Abi.ABI authorityAbi = CommonUtils.abiFromResource("/Authority.abi.json");
    protected static Abi.ABI accountAbi = CommonUtils.abiFromResource("/Account.abi.json");
    protected static Abi.ABI nftAbi = CommonUtils.abiFromResource("/Nft.abi.json");
    protected static Abi.ABI deployWalletAbi = CommonUtils.abiFromResource("/DepolyWallet.abi.json");
    protected static Abi.ABI walletAbi = CommonUtils.abiFromResource("/Wallet.abi.json");

    // gas
    protected static String gas01 = PropertiesReader.config.getString("gas0_1");
    protected static String gas02 = PropertiesReader.config.getString("gas0_2");
    protected static String gas05 = PropertiesReader.config.getString("gas0_5");
    protected static String gas10 = PropertiesReader.config.getString("gas1_0");
    protected static String gas12 = PropertiesReader.config.getString("gas1_2");
    protected static String gasBase = PropertiesReader.config.getString("gasBase");
    // sig
    protected static Integer mintNftSig = Integer.parseInt(PropertiesReader.config.getString("mintNft"));
    protected static Integer approveSig = Integer.parseInt(PropertiesReader.config.getString("approve"));
    protected static Integer transferSig = Integer.parseInt(PropertiesReader.config.getString("transfer"));
    protected static Integer burnSig = Integer.parseInt(PropertiesReader.config.getString("burn"));
    protected static Integer setURISig = Integer.parseInt(PropertiesReader.config.getString("setURI"));
    protected static Integer setApprovalForAllSig = Integer.parseInt(PropertiesReader.config.getString("setApprovalForAll"));
    //minter
    protected static Integer minterNum = Integer.parseInt(PropertiesReader.config.getString("minterNum"));


    public ApiBase(){
        init();
    }

    public static void init() {
        try {
            context = TONContext.create(new Client.ClientConfig(new Client.NetworkConfig(PropertiesReader.config.getString("network"))));
            net = new Net(context);
            crypto = new Crypto(context);
            processing = new Processing(context);
            abiModule = new Abi(context);
            tvm = new Tvm(context);
        }catch (TONException e){
            e.printStackTrace();
        }
    }

    protected Processing.ResultOfProcessMessage callFun(String address, String functionName, JSONObject inputObject, Crypto.KeyPair keyPair, Abi.ABI abi) {
        try {
            Abi.CallSet callSet = new Abi.CallSet(
                    functionName,
                    new Abi.FunctionHeader(null, null, keyPair == null ? null : keyPair.getPublic()),
                    inputObject==null ? new JSONObject() : inputObject.toString()
            );
            CompletableFuture<Processing.ResultOfProcessMessage> processingRes = processing.processMessage(abi, address, null, callSet, keyPair == null ? Abi.Signer.None : new Abi.Signer.Keys(keyPair), null, false, System.out::println);
            processingRes.join();
            return processingRes.get();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    protected Processing.ResultOfProcessMessage callInternalFun(String giveAddress, Crypto.KeyPair giveKeyPair, String value,
                                                                                String targetAddress, String targetFunctionName, JSONObject targetInputObject,
                                                                                Crypto.KeyPair targetKeyPair, Abi.ABI targetAbi, int flag) throws Exception{
        if(!checkTransaction(giveAddress, targetAddress))
            return null;
        Long time = System.currentTimeMillis();
        // 目标合约消息
        Abi.ResultOfEncodeMessageBody targetMessage = abiModule.encodeMessageBody(
                targetAbi,
                new Abi.CallSet(
                        targetFunctionName,
                        new Abi.FunctionHeader(null, null, targetKeyPair == null ? null : targetKeyPair.getPublic()),
                        targetInputObject==null ? new JSONObject() : targetInputObject.toString()
                ),
                true,
                targetKeyPair == null ? Abi.Signer.None : new Abi.Signer.Keys(targetKeyPair),
                null
        ).get();

        // give 合约转账
        JSONObject giveJsonObject = new JSONObject();
        giveJsonObject.put("dest", targetAddress);
        giveJsonObject.put("value", value);
        giveJsonObject.put("bounce", false);
        giveJsonObject.put("flags", flag);
        CompletableFuture<Processing.ResultOfProcessMessage> result = null;

        if(bsnOperator.equals(giveAddress)){
//        if(true){
            giveJsonObject.put("payload", targetMessage.getBody());

            Abi.CallSet callSet = new Abi.CallSet(
                    "sendTransaction",
                    new Abi.FunctionHeader(null, null, giveKeyPair == null ? null : giveKeyPair.getPublic()),
                    giveJsonObject.toString()
            );
            result = processing.processMessage(CommonUtils.abiFromResource("/SafeMultisigWallet.abi.json"),
                    giveAddress, null, callSet, giveKeyPair == null ? Abi.Signer.None : new Abi.Signer.Keys(giveKeyPair),
                    10, false, System.out::println);
        }else{
            giveJsonObject.put("body", targetMessage.getBody());
            Abi.CallSet callSet = new Abi.CallSet(
                    "transfer",
                    new Abi.FunctionHeader(null, null, giveKeyPair == null ? null : giveKeyPair.getPublic()),
                    giveJsonObject.toString()
            );
            result = processing.processMessage(walletAbi,
                    giveAddress, null, callSet, giveKeyPair == null ? Abi.Signer.None : new Abi.Signer.Keys(giveKeyPair),
                    10, false, System.out::println);
        }
        System.out.println("sendTransaction result 1:"+time + result);
        result.join();
        System.out.println("sendTransaction result 2:"+time + result);
        System.out.println("sendTransaction result 3:"+System.currentTimeMillis() + result);
        return result.get();
    }

    protected boolean sendTransaction(String from, String to, String value, Crypto.KeyPair giveKeyPair, boolean bounce){
        try {
            if(!checkTransaction(from, to))
                return false;
            JSONObject giveJsonObject = new JSONObject();
            giveJsonObject.put("dest", to);
            giveJsonObject.put("value", value);
            giveJsonObject.put("bounce", bounce);
            giveJsonObject.put("flags", 1);
            CompletableFuture<Processing.ResultOfProcessMessage> result;
            if(bsnOperator.equals(from)){
//            if(true){
                giveJsonObject.put("payload", "");
                Abi.CallSet callSet = new Abi.CallSet(
                        "sendTransaction",
                        new Abi.FunctionHeader(null, null, giveKeyPair == null ? null : giveKeyPair.getPublic()),
                        giveJsonObject.toString()
                );
                result = processing.processMessage(CommonUtils.abiFromResource("/SafeMultisigWallet.abi.json"),
                        from, null, callSet, giveKeyPair == null ? Abi.Signer.None : new Abi.Signer.Keys(giveKeyPair),
                        10, false, System.out::println);
            }else{
                giveJsonObject.put("body", "");
                Abi.CallSet callSet = new Abi.CallSet(
                        "transfer",
                        new Abi.FunctionHeader(null, null, giveKeyPair == null ? null : giveKeyPair.getPublic()),
                        giveJsonObject.toString()
                );
                result = processing.processMessage(walletAbi,
                        from, null, callSet, giveKeyPair == null ? Abi.Signer.None : new Abi.Signer.Keys(giveKeyPair),
                        10, false, System.out::println);
            }
            result.join();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    protected JSONObject runGet(String address, String funName, Object input, Abi.ABI abi, Crypto.KeyPair keyPair) {
        try {
            Object[] accounts = (Object[])this.net.queryCollection("accounts", String.format("{\"id\":{\"eq\":\"%s\"}}", address), "id acc_type boc data code", null, null).get();
            if(accounts.length <= 0){
                return null;
            }
            Map account = (Map)accounts[0];
            String acc_type = MapUtils.getString(account, "acc_type");
            if("1".equals(acc_type)){
                String bocI = MapUtils.getString(account, "boc");
                Abi.CallSet callSet = new Abi.CallSet(funName, new Abi.FunctionHeader((Number)null, (Long)null, keyPair == null ? "" : keyPair.getPublic()), input);
                Abi.ResultOfEncodeMessage resultOfEncodeMessage = abiModule.encodeMessage(abi, address, null, callSet, (keyPair == null ? Abi.Signer.None : new Abi.Signer.Keys(keyPair)), null).get();
                Object o = tvm.runTvm(resultOfEncodeMessage.getMessage(), bocI, null, abi, null, false).get();
                return JSONObject.fromObject(o);
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    protected DDCResponse runGet2(String address, String funName, Object input, Abi.ABI abi, Crypto.KeyPair keyPair) {
        try {
            Object[] accounts = (Object[])this.net.queryCollection("accounts", String.format("{\"id\":{\"eq\":\"%s\"}}", address), "id acc_type boc data code", null, null).get();
            if(accounts.length <= 0)
                return DDCResponse.error(address + "不存在");
            Map account = (Map)accounts[0];
            String acc_type = MapUtils.getString(account, "acc_type");
            if(!"1".equals(acc_type))
                return DDCResponse.error(address + "状态不是active");
            String bocI = MapUtils.getString(account, "boc");
            Abi.CallSet callSet = new Abi.CallSet(funName, new Abi.FunctionHeader((Number)null, (Long)null, keyPair == null ? "" : keyPair.getPublic()), input);
            Abi.ResultOfEncodeMessage resultOfEncodeMessage = abiModule.encodeMessage(abi, address, null, callSet, (keyPair == null ? Abi.Signer.None : new Abi.Signer.Keys(keyPair)), null).get();
            Object o = tvm.runTvm(resultOfEncodeMessage.getMessage(), bocI, null, abi, null, false).get();
            return DDCResponse.success(o);
        }catch (Exception e){
            e.printStackTrace();
            return DDCResponse.error("运行异常：" + e);
        }
    }

    protected Object subWithCodeHash(String hash, String lastId) throws Exception{
        Object o = net.queryCollection("accounts",
                String.format("{\"code_hash\":{\"eq\":\"%s\"},\"id\":{\"gt\":\"%s\"}}", hash, lastId),
                "id", null, null).get();
        return o;
    }

    public boolean accountAvailable (String account) {
        if(account == null)
            return false;
        JSONObject runRes = runGet(account,"accountAvailable",null,accountAbi,null);
        return runRes == null ? false : runRes.getJSONObject("decoded").getJSONObject("output").getBoolean("value0");
    }

    public String getAccountAddress (String address) {
        JSONObject input = new JSONObject();
        input.put("account", address);
        JSONObject accountAddressRes = runGet(authority,"getAccountAddress", input,authorityAbi,null);
        return accountAddressRes == null ? null : accountAddressRes.getJSONObject("decoded").getJSONObject("output").getString("value0");
    }

    public boolean onePlatformCheck (String acc1, String acc2) {
        String acc1AccountAds = getAccountAddress(acc1);
        String acc2AccountAds = getAccountAddress(acc2);
        if (acc1AccountAds == null || acc2AccountAds == null)
            return false;
        AccountInfo acc1Account = getAccount(acc1);
        AccountInfo acc2Account = getAccount(acc2);
        if (acc1Account == null || acc2Account == null)
            return false;
        if(checkAvailableAndRole(acc1AccountAds, 1)){
            if(checkAvailableAndRole(acc2AccountAds, 1)){
                if(acc1Account.getAccountDID().equals(acc2Account.getAccountDID()) &&
                        acc1Account.getLeaderDID().equals(acc2Account.getLeaderDID())){
                    return true;
                } else {
                    return false;
                }
            } else {
                if(acc1Account.getAccountDID().equals(acc2Account.getLeaderDID())){
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            if(checkAvailableAndRole(acc2AccountAds, 1)){
                if(acc1Account.getLeaderDID().equals(acc2Account.getAccountDID())){
                    return true;
                } else {
                    return false;
                }
            } else {
                if(acc1Account.getLeaderDID().equals(acc2Account.getLeaderDID())){
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public boolean checkAvailableAndRole(String account, int role) {
        if(account == null){
            return false;
        }
        try {
            JSONObject input = new JSONObject();
            input.put("role", role);
            JSONObject runRes = runGet(account,"checkAvailableAndRole",input,accountAbi,null);
            return runRes == null ? false : runRes.getJSONObject("decoded").getJSONObject("output").getBoolean("value0");
        }catch (Exception e){
            return false;
        }
    }

    public AccountInfo getAccount(String account) {
        if(!CommonUtils.matcherAds(account))
            return null;
        if(Ads0.equals(account))
            return null;
        String accountAddress = getAccountAddress(account);
        if(accountAddress == null)
            return null;
        JSONObject runRes = runGet(accountAddress,"getAccountInfo",null,accountAbi,null);
        if (runRes==null)
            return null;
        JSONObject output = runRes.getJSONObject("decoded").getJSONObject("output");
        AccountInfo res = new AccountInfo();
        res.setAccount(accountAddress);
        res.setAccountDID(output.getString("value0"));
        res.setAccountName(output.getString("value1"));
        int value2 = output.getInt("value2");
        res.setAccountRole(value2==0?AccountRole.Operator:(value2==1?AccountRole.PlatformManager:AccountRole.Consumer));
        res.setLeaderDID(output.getString("value3"));
        res.setPlatformState(output.getInt("value4")==0?PlatformState.Frozen:PlatformState.Active);
        res.setOperatorState(output.getInt("value5")==0?OperatorState.Frozen: OperatorState.Active);
        res.setField(output.getString("value6"));
        return res;
    }

    protected DDCResponse checkAccAds(String accAds, String str){
        if(accAds == null)
            return DDCResponse.error(str + "账户不存在");
        if(!accountAvailable(accAds))
            return DDCResponse.error(str + "账户状态不活跃");
        return DDCResponse.success();
    }

    protected DDCResponse checkAds1(String sender, String str){
        if(!CommonUtils.matcherAds(sender))
            return DDCResponse.error(str + "账户不为标准备address格式");
        if(Ads0.equals(sender))
            return DDCResponse.error(str + "账户为0地址");
        return DDCResponse.success();
    }

    /**
     * 检查链账户格式和是否活跃
     * @param sender
     * @param accAds
     * @param str
     * @return
     */
    protected DDCResponse checkAds2(String sender, String accAds, String str){
        if(!CommonUtils.matcherAds(sender))
            return DDCResponse.error(str + "账户不为标准备address格式");
        if(Ads0.equals(sender))
            return DDCResponse.error(str + "账户为0地址");
        if(accAds == null)
            return DDCResponse.error(str + "账户不存在");
        if(!accountAvailable(accAds))
            return DDCResponse.error(str + "账户状态不活跃");
        return DDCResponse.success();
    }

    /**
     * 检查调用方法权限
     * @param sender 调用者链账户
     * @param senderAcc 调用者账户
     * @param nftAddress ddc地址
     * @param sig 方法签名
     * @return
     */
    protected DDCResponse hasPermission(String sender, String senderAcc, String nftAddress, int sig){
        if(nftAddress!=null){
            JSONObject nftInfo = getInfo(nftAddress);
            String owner = nftInfo.getString("owner");
            JSONObject res = runGet(nftAddress, "getApproved", null, nftAbi, null);
            if(!sender.equals(owner) && (res != null && !sender.equals(res.getJSONObject("decoded").getJSONObject("output").getString("value0")))) {
                String accountAddress = getAccountAddress(owner);
                if(accountAddress == null)
                    return DDCResponse.error("DDC拥有者账户不存在");
                JSONObject input = new JSONObject();
                input.put("operator", sender);
                JSONObject runRes = runGet(accountAddress,"isApprovedForAll",input,accountAbi,null);
                JSONObject output = runRes.getJSONObject("decoded").getJSONObject("output");
                if(output==null || !output.getBoolean("value0"))
                    return DDCResponse.error("调用者不是DDC拥有者或DDC授权者或拥有者账户授权者");
            }
        }
        JSONObject runRes2 = runGet(senderAcc,"getAccountInfo",null,accountAbi,null);
        if (runRes2==null)
            return DDCResponse.error("调用者账号不存在");
        int senderRole = runRes2.getJSONObject("decoded").getJSONObject("output").getInt("value2");
        JSONObject input2 = new JSONObject();
        input2.put("role", senderRole);
        input2.put("sig", sig);
        JSONObject runRes3 = runGet(collection,"hasFunctionPermission",input2,collectionAbi,null);
        if (runRes3==null)
            return DDCResponse.error("查询 hasFunctionPermission 失败");
        if(!runRes3.getJSONObject("decoded").getJSONObject("output").getBoolean("value0")){
            return DDCResponse.error("没有权限调用sig:"+ sig +"方法");
        }
        return DDCResponse.success();
    }

    protected JSONObject getInfo(String nftAddress) {
        JSONObject input = new JSONObject();
        input.put("answerId", 0);
        JSONObject res = runGet(nftAddress, "getInfo", input, nftAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output");
    }

    /**
     * 解析event
     * @param transactions 监听到的交易
     * @param abi 解析交易的abi
     * @return
     */
    public DDCResponse parseEvent(JsonNode transactions, Abi.ABI abi) {
        try {
            JSONObject tranJs = JSONObject.fromObject(transactions.toString());
            System.out.println("tranJs:"+tranJs);
            tranJs = searchOutMesById(tranJs.getString("id"));
            if (tranJs==null)
                return DDCResponse.error("没有event事件 -- tran null");
            if (!tranJs.has("out_messages"))
                return DDCResponse.error("没有event事件");
            JSONArray outMsgs = tranJs.getJSONArray("out_messages");
            JSONArray resEventArr = new JSONArray();
            for (int i = 0; i<outMsgs.size(); i++){
                JSONObject outMsg = outMsgs.getJSONObject(i);
                if(!outMsg.has("msg_type")||outMsg.getInt("msg_type")!=2)
                    continue;
                String boc = outMsg.getString("boc");
                CompletableFuture<Abi.DecodedMessageBody> evenRes = decodeMessagePlus(abi, boc);
                JSONObject evenResJs = JSONObject.fromObject(evenRes.get());
                System.out.println();
                if(evenResJs!=null){
                    if("Event".equals(evenResJs.getString("bodyType"))){
                        resEventArr.add(evenResJs);
                    }
                }
            }
            if(resEventArr.size() > 0){
                return DDCResponse.success(resEventArr);
            }
            return DDCResponse.error("没有even事件");
        } catch (Exception e) {
            e.printStackTrace();
            return DDCResponse.error("解析失败");
        }
    }

    private CompletableFuture<Abi.DecodedMessageBody> decodeMessagePlus (Abi.ABI abi, String boc) {
        CompletableFuture<Abi.DecodedMessageBody> msgRes = abiModule.decodeMessage(abi, boc, true);
        msgRes.join();
        return msgRes;
    }

    private JSONObject searchOutMesById (String id) throws Exception {
        Object[] o = (Object[])net.queryCollection("transactions",
                String.format("{\"id\":{\"eq\":\"%s\"}}", id),
                "id out_messages{id,boc,msg_type,msg_type_name} now", null, null).get();
        return o.length > 0 ? JSONObject.fromObject(o[0]):null;
    }

    protected Integer getFunctionIds(String functionName) {
        JSONObject input = new JSONObject();
        input.put("functionName", functionName);
        JSONObject res = runGet(collection, "getFunctionIds", input, collectionAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output").getInt("value0");
    }

    /**
     * 查询业务费
     * @param ddcAddr
     * @param sig
     * @return
     */
    public BigInteger queryFee(String ddcAddr, Integer sig) {
        JSONObject input = new JSONObject();
        input.put("sig", sig);
        JSONObject res = runGet(ddcAddr, "queryFee", input, collectionAbi, null);
        return res == null ? BigInteger.valueOf(0) : BigInteger.valueOf(CommonUtils.hexStrToLong(res.getJSONObject("decoded").getJSONObject("output").getString("value0")));
    }

    public static void destroy() {
        if (context != null)
            context.destroy();
    }

    /**
     * 平台方不能给平台方转账
     * @param from
     * @param to
     * @return
     */
    private boolean checkTransaction(String from, String to) {
        return !(checkAvailableAndRole(getAccountAddress(from),1) && checkAvailableAndRole(getAccountAddress(to),1));
    }
}
