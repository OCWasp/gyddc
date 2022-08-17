import com.radiance.tonclient.Crypto;
import com.sdyc.ddc.api.nft.NFTApi;
import org.junit.Test;

import net.sf.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by fengtianhua on 2022/7/27.
 */
public class NFTApiTest {
    static NFTApi nftApi = new NFTApi();
    static String sender = "0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9a";
    static String operator = "0:311efb7e43a1809ef9d26b20dd6150bb3742f7ee58288eeff410b54de3be626d";
    static Crypto.KeyPair keyPair = new Crypto.KeyPair(
            "47797230cfd28f2c5893435e759883c3fa4c3fc05397b3f8ce3612849f9a514f",
            "ae4caeb0532e1dadf95b0ad563f0665d0c2e7940f993ba83dcdb2a11233504fb"
    );

    @Test
    public void mint() {
        JSONObject ddcURI = new JSONObject();
        ddcURI.put("type", "Basic NFTApi");
        ddcURI.put("name", "NFT add TEST 2");
        ddcURI.put("description", "hhhhhhhhhhhhhh!");
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("source", "https://cfile.madmen.app/ipfs/QmPwpWXK2XgCf2bib6GwQPtVDJSH5KL8Yrg3XGTLTvVTi5");
        jsonObject2.put("mimetype", "image/png");
        ddcURI.put("preview", jsonObject2.toString());
        ddcURI.put("files", jsonObject2.toString());
        ddcURI.put("external_url", "https://everscale.network");

        nftApi.mint(operator, operator, ddcURI.toString(), operator, keyPair);
    }

    @Test
    public void approve() {
        try {
            nftApi.approve(sender, "0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9b", BigInteger.valueOf(1), operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getApproved() {
        nftApi.getApproved(BigInteger.valueOf(1));
    }

    @Test
    public void transferFrom() {
        try {
            nftApi.transferFrom(sender, sender, "0:7d46a1ff2ccf9a140bf21d1baa4a857fc02564e1cdd0095b3cfa51bd624e9d9c", BigInteger.valueOf(1), operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void freeze() {
        try {
            nftApi.freeze(sender, BigInteger.valueOf(1), operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unFreeze() {
        try {
            nftApi.unFreeze(sender, BigInteger.valueOf(1), operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void burn() {
        try {
            nftApi.burn(sender, BigInteger.valueOf(1), operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void balanceOf() {
        try {
            nftApi.balanceOf(operator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ownerOf() {
        nftApi.ownerOf(BigInteger.valueOf(1));
    }

    @Test
    public void name() {
        nftApi.name();
    }

    @Test
    public void symbol() {
        nftApi.symbol();
    }

    @Test
    public void ddcURI() {
        try {
            nftApi.ddcURI(BigInteger.valueOf(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setURI() {
        try {
            nftApi.setURI(sender, BigInteger.valueOf(1), "ipfs://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setNameAndSymbol() {
        try {
            nftApi.setNameAndSymbol(sender, "name", "symbol", operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLatestDDCId() {
        nftApi.getLatestDDCId();
    }

    @Test
    public void deposit() {
        try {
            nftApi.deposit(sender, operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setFee() {
        try {
            nftApi.setFee(sender, "0:be146ba2613a3cf0b5103c886ab59f5c0969b2937da7a79c5dcd75b039970165", 150542720, 1, operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delFee() {
        try {
            nftApi.delFee(sender, "0:be146ba2613a3cf0b5103c886ab59f5c0969b2937da7a79c5dcd75b039970165", 150542720, operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delDDC() {
        try {
//            nftApi.delDDC(sender, "0:be146ba2613a3cf0b5103c886ab59f5c0969b2937da7a79c5dcd75b039970165", operator, keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBalance() {
        nftApi.getBalance();
    }

    @Test
    public void totalSupply() {
        nftApi.totalSupply();
    }

}
