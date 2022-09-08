import com.fasterxml.jackson.databind.JsonNode;
import com.sdyc.ddc.api.nft.NFTApi;
import com.sdyc.ddc.bean.DDCResponse;
import com.sdyc.ddc.utils.CommonUtils;
import org.junit.Test;

import java.util.ArrayList;

public class EventTest {
    @Test
    public void subscription() throws Exception {
        NFTApi nftApi = NFTApi.getInstance();

        ArrayList<JsonNode> transactions = new ArrayList<>();
        nftApi.net.subscribeCollection("transactions",
                CommonUtils.subscribeFilter("0:e7ea4e29a6ff8b3232d803019a6ec7b05d86c3ac9cc1d02719693df8b3efc29a"),
                CommonUtils.subscribeResult,
                event -> {
                    synchronized (transactions) {
                        transactions.add(event.getResult());
                    }
                }).get();
        while (true) {
            System.out.println("Collection ...");
            Thread.sleep(5000L);
            ArrayList<JsonNode> transactionTemp = new ArrayList<>();
            transactionTemp.addAll(transactions);
            transactions.clear();
            for (JsonNode tran : transactionTemp){
                DDCResponse res = nftApi.parseEvent(tran, NFTApi.collectionAbi);
                System.out.println("Collection event:"+res);
            }
        }
    }
}
