<?php 
    include("java/Java.inc");
    $opAddress = "0:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    $opKeyPair = new Java('com.radiance.tonclient.Crypto$KeyPair', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');

    $plAddress = "0:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    $plKeyPair = new Java('com.radiance.tonclient.Crypto$KeyPair', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');

    $coAddress = "0:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    $coKeyPair = new Java('com.radiance.tonclient.Crypto$KeyPair', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');

    // 实例化AccountApi
    $AccountApi = new Java('com.sdyc.ddc.api.account.AccountApi');

    // 运营方添加业务账号
    // $account = "0:4570679b2879bc821cab5dc6439cd12319e1dece2a99b7bdb4331c6f501ab84d";
    // $accountDID = "000002";
    // $leaderDID = "";
    // echo $AccountApi->addAccountByOperator($opAddress, $account, $accountDID, $leaderDID, $opKeyPair)."<BR>";

    // 平台方添加业务账号
    // $account = "0:f2b1993c754a5b1a6d86426e70e77a352b1bf8d71537ef416630a8a24d50dd13";
    // $accountDID = "000003";
    // $leaderDID = "000002";
    // echo $AccountApi->addAccountByPlatform($plAddress, $account, $accountDID, $leaderDID, $plKeyPair)."<BR>";

    // 运营方充值
    // echo $AccountApi->selfRecharge($opAddress, new Java('java.math.BigInteger', 10000000), $opKeyPair);

    // 运营方转账
    // echo $AccountApi->recharge($opAddress, $plAddress, new Java('java.math.BigInteger', 1000), $opKeyPair)."<BR>";

    // 查询余额
    // echo $AccountApi->balanceOf($plAddress)."<BR>";

    // 平台链账户开关查询
    // echo $AccountApi-> switcherStateOfPlatform();

    // 平台链账户开关设置
    // echo $AccountApi->setSwitcherStateOfPlatform($opAddress, true, $opKeyPair);

    // 获取业务账号
    // echo $AccountApi->getAccountAddress($plAddress);

    // 账号状态
    // echo $AccountApi->accountAvailable($AccountApi->getAccountAddress($plAddress));

    // 修改账号状态
    // $state = new Java('com.sdyc.ddc.bean.State');
    // echo $AccountApi->updateAccState($opAddress, $plAddress, $state->valueOf('Frozen'), false, $opKeyPair);

    // 账号信息
    // echo $AccountApi->getAccount($plAddress)->toString();

    // 账号赋权信息
    // echo $AccountApi->isApprovedForAll($coAddress, $plAddress);

    // 账号赋权
    // echo $AccountApi->setApprovalForAll($coAddress, $plAddress, true, $plAddress, $plKeyPair);

    // 删除业务账号
    // echo $AccountApi->delAccount($opAddress, $opKeyPair, true, $coAddress);

    // 创建链账号
    // echo $AccountApi-> safeMultisigWallet($opAddress, $opKeyPair);

    // 离线生成链账号
    // echo $AccountApi-> getSafeMultisigWallet();

    // 部署链账号
    // $deployKeyPair = new Java('com.radiance.tonclient.Crypto$KeyPair', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');
    // echo $AccountApi-> deploySafeMultisigWallet($opAddress, $deployKeyPair, $opKeyPair);
    
    // 转账白名单
    // echo $AccountApi-> setTransable($opAddress, $plAddress, true, $opKeyPair);
?> 
