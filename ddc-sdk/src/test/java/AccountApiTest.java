import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.radiance.tonclient.Crypto;
import com.sdyc.ddc.api.account.AccountApi;
import com.sdyc.ddc.bean.AccountInfo;
import com.sdyc.ddc.bean.State;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengtianhua on 2022/7/27.
 */
public class AccountApiTest {
    static AccountApi accountApi = new AccountApi();
    static String address = "0:311efb7e43a1809ef9d26b20dd6150bb3742f7ee58288eeff410b54de3be626d";
    static Crypto.KeyPair keyPair = new Crypto.KeyPair(
            "47797230cfd28f2c5893435e759883c3fa4c3fc05397b3f8ce3612849f9a514f",
            "ae4caeb0532e1dadf95b0ad563f0665d0c2e7940f993ba83dcdb2a11233504fb"
    );

    @Test
    public static void addAccountByPlatform() {
        accountApi.addAccountByPlatform(address,"0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9a","accountName5","5",keyPair);
    }

    @Test
    public static void addBatchAccountByPlatform() {
        List<AccountInfo> accounts = new ArrayList<>();

        AccountInfo accountInfo1 = new AccountInfo();
        accountInfo1.setAccount("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9a");
        accountInfo1.setAccountDID("1001");
        accountInfo1.setAccountName("accountName2");
        accounts.add(accountInfo1);

        AccountInfo accountInfo2 = new AccountInfo();
        accountInfo2.setAccount("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9b");
        accountInfo2.setAccountDID("1002");
        accountInfo2.setAccountName("accountName3");
        accounts.add(accountInfo2);

        accountApi.addBatchAccountByPlatform(address,accounts,keyPair);
    }

    @Test
    public static void setSwitcherStateOfPlatform() {
        accountApi.setSwitcherStateOfPlatform(address, true, keyPair);
    }

    @Test
    public static void addAccountByOperator() {
        accountApi.addAccountByOperator(address, "0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9a", "accountName3", "1003", "1", keyPair);
    }

    @Test
    public static void addBatchAccountByOperator() {
        List<AccountInfo> accounts = new ArrayList<>();

        AccountInfo accountInfo1 = new AccountInfo();
        accountInfo1.setAccount("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9a");
        accountInfo1.setAccountDID("1001");
        accountInfo1.setAccountName("accountName2");
        accounts.add(accountInfo1);

        AccountInfo accountInfo2 = new AccountInfo();
        accountInfo2.setAccount("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9b");
        accountInfo2.setAccountDID("1002");
        accountInfo2.setAccountName("accountName3");
        accounts.add(accountInfo2);

        try {
            accountApi.addBatchAccountByOperator(address, accounts, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public static void updateAccState() {
        accountApi.updateAccState(address, "0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9b", State.Frozen, true, keyPair);
    }

    @Test
    public static void recharge() {
        try {
            accountApi.recharge(address, "0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9b", BigInteger.valueOf(1), keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public static void rechargeBatch() {
        Multimap<String, BigInteger> list = LinkedHashMultimap.create();
        list.put("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9b", BigInteger.valueOf(1));
        list.put("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9c", BigInteger.valueOf(1));
        list.put("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9d", BigInteger.valueOf(1));
        list.put("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9e", BigInteger.valueOf(1));
        try {
            accountApi.rechargeBatch(address, list, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public static void balanceOf() {
        accountApi.balanceOf("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9b");
    }

    @Test
    public static void balanceOfBatch() {
        List<String> list = new ArrayList<>();
        list.add("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9b");
        list.add("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9c");
        list.add("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9d");
        list.add("0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9e");
        accountApi.balanceOfBatch(list);
    }

    @Test
    public static void selfRecharge() {
        try {
            accountApi.selfRecharge(address, BigInteger.valueOf(1), keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public static void setApprovalForAll() {
        try {
            accountApi.setApprovalForAll(address, "0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9c", true, address, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public static void isApprovedForAll() {
        accountApi.isApprovedForAll(address, "0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9c");
    }

    @Test
    public static void safeMultisigWallet() {
        try {
            accountApi.safeMultisigWallet(address, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public static void setTransable() {
        try {
            accountApi.setTransable(address, "0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9c", true, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
