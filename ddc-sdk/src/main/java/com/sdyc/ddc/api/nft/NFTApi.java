package com.sdyc.ddc.api.nft;
import com.radiance.tonclient.Crypto;
import com.radiance.tonclient.Processing;
import com.sdyc.ddc.api.ApiBase;
import com.sdyc.ddc.api.account.AccountApi;
import com.sdyc.ddc.bean.DDCResponse;
import com.sdyc.ddc.utils.CommonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NFTApi extends ApiBase{
    protected static NFTApi nftApi;

    public static NFTApi getInstance(){
        synchronized (NFTApi.class) {
            if(nftApi == null){
                nftApi = new NFTApi();
            }
        }
        return nftApi;
    }
    public DDCResponse mintBatch(String sender, String to, String ddcURI, Integer amount, long time, String operator, Crypto.KeyPair keyPair){
        try {
            ExecutorService fixedPool = Executors.newFixedThreadPool((int)time);
            for (int i = 0; i < amount; i ++) {
                fixedPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long time = System.currentTimeMillis();
                        System.out.println("time start:"+time);
                        int num = 10;
                        do{
                            num --;
                            DDCResponse res = mint(sender, to, ddcURI, operator,keyPair);
                            if(res.isSuccess()){
                                System.out.println("time end:"+time);
                                break;
                            } else {
                                System.out.println("time exception:"+time);
                            }
                        }while (num>0);
                        if(num == 0)
                            System.out.println("time  0 exception:" + time);
                    }
                });
                System.out.println("mint:::: "+ ( i + 1));
                Thread.sleep(time);
            }
            fixedPool.shutdown();
            return DDCResponse.success("创建成功");
        }catch (Exception e){
            return DDCResponse.error("运行异常: " + e);
        }
    }

    public DDCResponse mint(String sender, String to, String ddcURI, String operator, Crypto.KeyPair keyPair) {
        try {
            // 检查调用者账号格式和账号的可用性
            String senderAccAds = getAccountAddress(sender);
            DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
            if(!checkAdsRes.isSuccess())
                return checkAdsRes;

            // 方法调用权限
            DDCResponse hasPerRes = hasPermission(sender, senderAccAds, null, mintNftSig);
            if(!hasPerRes.isSuccess())
                return hasPerRes;

            //查询mint业务费
            AccountApi accountApi = AccountApi.getInstance();
            DDCResponse payRes = accountApi.checkFee(operator, keyPair,collection,queryFee(collection,mintNftSig), mintNftSig,0 );
            if(!payRes.isSuccess())
                return payRes;

            JSONObject input = new JSONObject();
//            input.put("json","\""+ ddcURI +"\"");
            input.put("json", ddcURI );
            input.put("owner", to);
            input.put("collection", collection );
            Random random = new Random();
            Processing.ResultOfProcessMessage callRes =
                    callInternalFun(operator,keyPair,gas12+gasBase,
                            CommonUtils.getMinter("minter"+random.nextInt(minterNum)),
                            "mint",input,
                            null,CommonUtils.abiFromResource("/Minter.abi.json"), 1);

            accountApi.pay(operator, keyPair,collection,queryFee(collection,mintNftSig), mintNftSig,0 );

            return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
        } catch (Exception e){
            return DDCResponse.error("运行异常: " + e);
        }
    }

    public DDCResponse approve(String sender, String to, BigInteger ddcId, String operator, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;
        DDCResponse checkAds1Res = checkAds1(to, "接收者");
        if(!checkAds1Res.isSuccess())
            return checkAds1Res;

        String nftAddress = nftAddress(ddcId);
        if(nftAddress==null)
            return DDCResponse.error("DDC不存在");

        // 方法调用权限
        DDCResponse hasPerRes = hasPermission(sender, senderAccAds, nftAddress, approveSig);
        if(!hasPerRes.isSuccess())
            return hasPerRes;

        //查询mint业务费
        AccountApi accountApi = AccountApi.getInstance();
        DDCResponse payRes = accountApi.pay(operator, keyPair, collection, queryFee(collection, approveSig),approveSig,ddcId.intValue());
        System.out.println("payRes:"+payRes);
        if(!payRes.isSuccess())
            return payRes;

        JSONObject input = new JSONObject();
        input.put("to", to);
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(operator, keyPair,gas02+gasBase, nftAddress,"approve",input, keyPair,nftAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public String getApproved(BigInteger ddcId) {
        JSONObject res = runGet(nftAddress(ddcId), "getApproved", null, nftAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output").getString("value0");
    }

    public DDCResponse transferFrom(String sender, String from, String to, BigInteger ddcId, String operator, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        String toAccAds = getAccountAddress(to);
        DDCResponse checkAdsRes2 = checkAds2(to, toAccAds, "接收者");
        if(!checkAdsRes2.isSuccess())
            return checkAdsRes2;

        String nftAddress = nftAddress(ddcId);
        if(nftAddress==null)
            return DDCResponse.error("DDC不存在");

        // 方法调用权限
        DDCResponse hasPerRes = hasPermission(sender, senderAccAds, nftAddress, transferSig);
        if(!hasPerRes.isSuccess())
            return hasPerRes;

        //查询mint业务费
        AccountApi accountApi = AccountApi.getInstance();
        DDCResponse payRes = accountApi.pay(operator, keyPair, collection, queryFee(collection, transferSig),transferSig,ddcId.intValue());
        if(!payRes.isSuccess())
            return payRes;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("to", to);
        jsonObject.put("sendGasTo", sender);
        jsonObject.put("components", "");
        jsonObject.put("callbacks", new JSONObject());
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(operator, keyPair,gas10+gasBase, nftAddress,"transfer",jsonObject, keyPair,nftAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse freeze(String sender, BigInteger ddcId, String operator, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        String nftAddress = nftAddress(ddcId);
        if(nftAddress==null)
            return DDCResponse.error("DDC不存在");

        if(!checkAvailableAndRole(senderAccAds, 0))
            return DDCResponse.error("调用者不是运营方");

        Processing.ResultOfProcessMessage callRes =
                callInternalFun(operator, keyPair,gas05+gasBase, nftAddress,"freeze",null, keyPair,nftAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse unFreeze(String sender, BigInteger ddcId, String operator, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        String nftAddress = nftAddress(ddcId);
        if(nftAddress==null)
            return DDCResponse.error("DDC不存在");

        if(!checkAvailableAndRole(senderAccAds, 0))
            return DDCResponse.error("调用者不是运营方");

        Processing.ResultOfProcessMessage callRes =
                callInternalFun(operator, keyPair,gas05+gasBase, nftAddress(ddcId),"unFreeze",null, keyPair,nftAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse burn(String sender, BigInteger ddcId, String operator, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        String nftAddress = nftAddress(ddcId);
        if(nftAddress==null)
            return DDCResponse.error("DDC不存在");

        // 方法调用权限
        DDCResponse hasPerRes = hasPermission(sender, senderAccAds, nftAddress,burnSig);
        if(!hasPerRes.isSuccess())
            return hasPerRes;

        //查询mint业务费
        AccountApi accountApi = AccountApi.getInstance();
        DDCResponse payRes = accountApi.pay(operator, keyPair, collection, queryFee(collection, burnSig),burnSig,ddcId.intValue());
        if(!payRes.isSuccess())
            return payRes;

        JSONObject input = new JSONObject();
        input.put("dest", nftAddress);
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(operator, keyPair,gas01+gasBase, nftAddress,"burn",input, keyPair,nftAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse balanceOf(String owner) throws Exception{
        DDCResponse checkAdsRes = checkAds1(owner, "拥有者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("answerId", 0);
        JSONObject nftCodeHash = runGet(collection,
                "nftCodeHash", jsonObject,
                collectionAbi, null);
        String nftCodeHashStr = nftCodeHash.getJSONObject("decoded").getJSONObject("output").getString("codeHash");
        Object accounts;
        String lastId = "";
        JSONArray tt = new JSONArray();
        do{
            accounts = subWithCodeHash(nftCodeHashStr.replace("0x", ""),  lastId==null?"":lastId);
            JSONArray nftIdArr = JSONArray.fromObject(accounts);
            for (int i = 0; i < nftIdArr.size(); i++) {
                lastId = nftIdArr.getJSONObject(i).getString("id");
                JSONObject nftInfo = runGet(lastId, "getInfo", jsonObject,
                        nftAbi, null);
                JSONObject nftInfoObj = nftInfo.getJSONObject("decoded").getJSONObject("output");
                if(owner.equals(nftInfoObj.getString("owner"))){
                    tt.add(nftIdArr.getJSONObject(i));
                }
            }
        } while (JSONArray.fromObject(accounts).size() > 0);
        return DDCResponse.success(BigInteger.valueOf(tt.size()));
    }

    public DDCResponse ownerOf(BigInteger ddcId) {
        JSONObject res = getInfo(nftAddress(ddcId));
        if(res == null)
            return DDCResponse.error("DDC不存在");
        return DDCResponse.success(res.getString("owner"));
    }

    public String name() {
        JSONObject res = runGet(collection, "name", null, collectionAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output").getString("value0");
    }

    public String symbol() {
        JSONObject res = runGet(collection, "symbol", null, collectionAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output").getString("value0");
    }

    public String ddcURI(BigInteger ddcId){
        JSONObject res = runGet(nftAddress(ddcId), "ddcURI", null, nftAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output").getString("value0");
    }

    public DDCResponse setURI(String sender, BigInteger ddcId, String ddcURI, String platform, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        String nftAddress = nftAddress(ddcId);
        if(nftAddress==null)
            return DDCResponse.error("DDC不存在");

        // 方法调用权限
        DDCResponse hasPerRes = hasPermission(sender, senderAccAds, nftAddress,setURISig);
        if(!hasPerRes.isSuccess())
            return hasPerRes;

        JSONObject input = new JSONObject();
        input.put("ddcURI", ddcURI);
        input.put("gasTo", sender);

        Processing.ResultOfProcessMessage callRes =
                callInternalFun(platform, keyPair, gas10+gasBase, nftAddress(ddcId), "setURI", input, keyPair,nftAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse setNameAndSymbol(String sender, String name, String symbol, String operator, Crypto.KeyPair keyPair) throws Exception {
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAvailableAndRole(senderAccAds, 0))
            return DDCResponse.error("调用者不是运营方");

        JSONObject input = new JSONObject();
        input.put("name", name);
        input.put("symbol", symbol);
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(operator, keyPair,gas01+gasBase, collection,"setNameAndSymbol",input, keyPair,collectionAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public String getLatestDDCId() {
        JSONObject res = runGet(collection, "getLatestDDCId", null, collectionAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output").getString("value0");
    }

    public void deposit(String sender, String operator, Crypto.KeyPair keyPair) throws Exception {
        JSONObject input = new JSONObject();
        input.put("amount", 1);
        callInternalFun(operator, keyPair,"100000000", collection,"deposit",input, keyPair,collectionAbi, 1);
    }

    public DDCResponse setFee(String sender, String ddcAddr, Integer sig, Integer amount, String operator, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAvailableAndRole(senderAccAds, 0))
            return DDCResponse.error("调用者不是运营方");
        JSONObject input = new JSONObject();
        input.put("sig", sig);
        input.put("amount", amount);

        Processing.ResultOfProcessMessage callRes =
                callInternalFun(operator, keyPair,gas01+gasBase, ddcAddr,"setFee",input, keyPair,collectionAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public DDCResponse delFee(String sender, String ddcAddr, Integer sig, String operator, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAvailableAndRole(senderAccAds, 0))
            return DDCResponse.error("调用者不是运营方");
        JSONObject input = new JSONObject();
        input.put("sig", sig);
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(operator, keyPair,gas01+gasBase, ddcAddr,"delFee",input, keyPair,collectionAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    public BigInteger getBalance() {
        JSONObject res = runGet(collection, "getBalance", null, collectionAbi, null);
        return res == null ? BigInteger.valueOf(0) : BigInteger.valueOf(CommonUtils.hexStrToLong(res.getJSONObject("decoded").getJSONObject("output").getString("value0")));
    }

    public Long totalSupply() {
        JSONObject input = new JSONObject();
        input.put("answerId", 0);
        JSONObject res = runGet(collection, "totalSupply", input, collectionAbi, null);
        return res == null ? 0L : res.getJSONObject("decoded").getJSONObject("output").getLong("count");
    }

    protected Integer getFunctionIds(String functionName) {
        JSONObject input = new JSONObject();
        input.put("functionName", functionName);
        JSONObject res = runGet(collection, "getFunctionIds", input, collectionAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output").getInt("value0");
    }

    protected String nftAddress(BigInteger ddcId) {
        JSONObject input = new JSONObject();
        input.put("answerId", 0);
        input.put("id", ddcId);
        JSONObject res = runGet(collection, "nftAddress", input, collectionAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output").getString("nft");
    }

    protected String getJson(String address) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("answerId", 0);
        JSONObject res = runGet(address, "getJson", jsonObject, nftAbi, null);
        return res == null ? null : res.getJSONObject("decoded").getJSONObject("output").getString("json");
    }

    public DDCResponse delDDC (String sender, String ddcAddr, String operator, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAvailableAndRole(senderAccAds, 0))
            return DDCResponse.error("调用者不是运营方");
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(operator, keyPair,gas10+gasBase, ddcAddr,"delDDC",null, keyPair,collectionAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    private DDCResponse addFunction (String sender, int role, int sig, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAvailableAndRole(senderAccAds, 0))
            return DDCResponse.error("调用者不是运营方");

        JSONObject input = new JSONObject();
        input.put("role", role);
        input.put("sig", sig);
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(sender, keyPair,gas01+gasBase, collection,"addFunction",input, keyPair,collectionAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    private DDCResponse delFunction (String sender, int role, int sig, Crypto.KeyPair keyPair) throws Exception{
        String senderAccAds = getAccountAddress(sender);
        DDCResponse checkAdsRes = checkAds2(sender, senderAccAds, "调用者");
        if(!checkAdsRes.isSuccess())
            return checkAdsRes;

        if(!checkAvailableAndRole(senderAccAds, 0))
            return DDCResponse.error("调用者不是运营方");

        JSONObject input = new JSONObject();
        input.put("role", role);
        input.put("sig", sig);
        Processing.ResultOfProcessMessage callRes =
                callInternalFun(sender, keyPair,gas01+gasBase, collection,"delFunction",input, keyPair,collectionAbi, 1);
        return DDCResponse.success(JSONObject.fromObject(callRes).getJSONObject("transaction").getString("id"));
    }

    private JSONArray getFunctions(int role) {
        JSONObject input = new JSONObject();
        input.put("role", role);
        JSONObject res = runGet(collection, "getFunctions", input, collectionAbi, null);
        return res==null ? null : res.getJSONObject("decoded").getJSONObject("output").getJSONArray("value0");
    }
}
