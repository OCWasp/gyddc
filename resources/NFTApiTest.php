<!DOCTYPE html> 
<html> 
<body> 
    <?php 
        include("java/Java.inc");

        $opAddress = "0:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        $opKeyPair = new Java('com.radiance.tonclient.Crypto$KeyPair', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');

        $plAddress = "0:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        $plKeyPair = new Java('com.radiance.tonclient.Crypto$KeyPair', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');

        $coAddress = "0:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        $coKeyPair = new Java('com.radiance.tonclient.Crypto$KeyPair', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');

        // 实例化NFTApi
        $NFTApi = new Java('com.sdyc.ddc.api.nft.NFTApi');

        // 铸造 DDC
        // $ddcURI = 'ipfs//xxx';
        // echo $NFTApi->mint($plAddress, $coAddress, $ddcURI, $plAddress, $plKeyPair);

        // DDC Owner
        // echo $NFTApi->ownerOf(new Java('java.math.BigInteger', 1));
        
        // DDC ddcURI 查询
        // echo $NFTApi->ddcURI(new Java('java.math.BigInteger', 1));

        // DDC ddcURI 设置
        // $ddcURI = 'ipfs//xxx';
        // echo $NFTApi->setURI($plAddress, new Java('java.math.BigInteger', 1), $ddcURI, $plAddress, $plKeyPair);

        // DDC 冻结
        // echo $NFTApi->freeze($plAddress, new Java('java.math.BigInteger', 1), $plAddress, $plKeyPair);
    
        // DDC 解冻
        // echo $NFTApi->unFreeze($plAddress, new Java('java.math.BigInteger', 1), $plAddress, $plKeyPair);

        // DDC 转移
        // $form = "0:f2b1993c754a5b1a6d86426e70e77a352b1bf8d71537ef416630a8a24d50dd13";
        // $to = "0:4570679b2879bc821cab5dc6439cd12319e1dece2a99b7bdb4331c6f501ab84d";
        // echo $NFTApi->transferFrom($plAddress, $form, $to, new Java('java.math.BigInteger', 1), $plAddress, $plKeyPair);

        // DDC 赋权
        // $to = "0:4570679b2879bc821cab5dc6439cd12319e1dece2a99b7bdb4331c6f501ab84d";
        // echo $NFTApi->approve($plAddress, $to, new Java('java.math.BigInteger', 1), $plAddress, $plKeyPair);

        // DDC 赋权查询
        // echo $NFTApi->getApproved(new Java('java.math.BigInteger', 1));

        // DDC 数量
        // echo $NFTApi->totalSupply();

        // Owner DDC 数量
        // echo $NFTApi->balanceOf($coAddress);

        // name
        // echo $NFTApi->name();
        
        // symbol
        // echo $NFTApi->symbol();

        // setNameAndSymbol
        // $name = "name1";
        // $symbol = "symbol1";
        // echo $NFTApi->setNameAndSymbol($opAddress, $name, $symbol, $opAddres, $opKeyPair);

        // getLatestDDCId
        // echo $NFTApi->getLatestDDCId();

        // deposit
        // echo $NFTApi->deposit($plAddress, $opAddres, $opKeyPair);

        // collection 业务费
        // echo $NFTApi->getBalance();

        // 设置业务费
        // $ddcAddr = "0:cd4c637b9a8c3ce93cf9c05c10b0afb070cd262ac5fac9b840e13811ca937b83";
        // $sig = new Java('java.lang.Integer', 1684047032);
        // $amount = new Java('java.lang.Integer', 10);
        // echo $NFTApi->setFee($opAddress, $ddcAddr, $sig, $amount, $opAddres, $opKeyPair);

        // 删除业务费
        // $ddcAddr = "0:cd4c637b9a8c3ce93cf9c05c10b0afb070cd262ac5fac9b840e13811ca937b83";
        // $sig = new Java('java.lang.Integer', 1684047032);
        // $amount = new Java('java.lang.Integer', 10);
        // echo $NFTApi->delFee($opAddress, $ddcAddr, $sig, $opAddres, $opKeyPair);
    ?> 
</body> 
</html>