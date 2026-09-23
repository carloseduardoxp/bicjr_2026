package com.github.binarywang.wxpay.example;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.transfer.*;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.TransferService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

public class NewTransferApiExample {

    private final TransferService transferService;

    public NewTransferApiExample(WxPayConfig config) {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(config);
        
        this.transferService = wxPayService.getTransferService();
    }

    public void transferExample() {
        try {
            TransferBillsRequest request = TransferBillsRequest.newBuilder()
                .appid("wx1234567890123456")                    // 应用ID
                .outBillNo("TRANSFER_" + System.currentTimeMillis()) // 商户转账单号，确保唯一
                .transferSceneId("1005")                       // 转账场景ID（1005=佣金报酬）
                .openid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o")       // 收款用户的openid
                .userName("张三")                               // 收款用户真实姓名（可选，会自动加密）
                .transferAmount(100)                           // 转账金额，单位：分（此处为1元）
                .transferRemark("佣金报酬")                     // 转账备注，用户可见
                .notifyUrl("https://your-domain.com/transfer/notify") // 异步通知地址（可选）
                .userRecvPerception("Y")                       // 用户收款感知：Y=会收到通知，N=不会收到通知
                .build();

            // 发起转账
            TransferBillsResult result = transferService.transferBills(request);

            // 输出结果
            System.out.println("=== 转账发起成功 ===");
            System.out.println("商户单号: " + result.getOutBillNo());
            System.out.println("微信转账单号: " + result.getTransferBillNo());
            System.out.println("创建时间: " + result.getCreateTime());
            System.out.println("状态: " + result.getState());
            System.out.println("跳转领取页面信息: " + result.getPackageInfo());

        } catch (WxPayException e) {
            System.err.println("转账失败: " + e.getMessage());
            System.err.println("错误代码: " + e.getErrCode());
            System.err.println("错误描述: " + e.getErrCodeDes());
        }
    }

    /**
     * 通过商户单号查询转账结果
     */
    public void queryByOutBillNoExample() {
        try {
            String outBillNo = "TRANSFER_1642567890123";
            TransferBillsGetResult result = transferService.getBillsByOutBillNo(outBillNo);

            System.out.println("=== 查询转账结果（商户单号）===");
            System.out.println("商户单号: " + result.getOutBillNo());
            System.out.println("微信转账单号: " + result.getTransferBillNo());
            System.out.println("状态: " + result.getState());
            System.out.println("转账金额: " + result.getTransferAmount() + "分");
            System.out.println("用户openid: " + result.getOpenid());
            System.out.println("转账备注: " + result.getTransferRemark());

        } catch (WxPayException e) {
            System.err.println("查询失败: " + e.getMessage());
        }
    }

    /**
     * 通过微信转账单号查询转账结果
     */
    public void queryByTransferBillNoExample() {
        try {
            String transferBillNo = "1000000000000000000000000001";
            TransferBillsGetResult result = transferService.getBillsByTransferBillNo(transferBillNo);

            System.out.println("=== 查询转账结果（微信单号）===");
            System.out.println("微信转账单号: " + result.getTransferBillNo());
            System.out.println("状态: " + result.getState());
            System.out.println("失败原因: " + result.getFailReason());

        } catch (WxPayException e) {
            System.err.println("查询失败: " + e.getMessage());
        }
    }

    /**
     * 撤销转账示例
     * 注意：只有在特定状态下才能撤销
     */
    public void cancelTransferExample() {
        try {
            String outBillNo = "TRANSFER_1642567890123";
            TransferBillsCancelResult result = transferService.transformBillsCancel(outBillNo);

            System.out.println("=== 撤销转账结果 ===");
            System.out.println("商户单号: " + result.getOutBillNo());
            System.out.println("状态: " + result.getState());
            System.out.println("更新时间: " + result.getUpdateTime());

        } catch (WxPayException e) {
            System.err.println("撤销失败: " + e.getMessage());
        }
    }

    /**
     * 处理转账回调通知示例
     * 这个方法通常在您的Web服务器的回调接口中调用
     */
    public void handleNotifyExample(String notifyData, String timestamp, String nonce, String signature, String serial) {
        try {
            // 构建签名头信息
            SignatureHeader header = new SignatureHeader();
            header.setTimeStamp(timestamp);
            header.setNonce(nonce);
            header.setSignature(signature);
            header.setSerial(serial);

            // 解析并验签回调数据
            TransferBillsNotifyResult notifyResult = transferService.parseTransferBillsNotifyResult(notifyData, header);

            System.out.println("=== 处理转账回调通知 ===");
            System.out.println("商户单号: " + notifyResult.getResult().getOutBillNo());
            System.out.println("微信转账单号: " + notifyResult.getResult().getTransferBillNo());
            System.out.println("状态: " + notifyResult.getResult().getState());
            System.out.println("转账金额: " + notifyResult.getResult().getTransferAmount() + "分");
            System.out.println("更新时间: " + notifyResult.getResult().getUpdateTime());

            // 根据状态处理业务逻辑
            switch (notifyResult.getResult().getState()) {
                case "SUCCESS":
                    System.out.println("转账成功，进行业务处理...");
                    // 更新订单状态、发送通知等
                    break;
                case "FAIL":
                    System.out.println("转账失败，失败原因: " + notifyResult.getResult().getFailReason());
                    // 处理失败逻辑
                    break;
                default:
                    System.out.println("其他状态: " + notifyResult.getResult().getState());
            }

        } catch (WxPayException e) {
            System.err.println("回调处理失败: " + e.getMessage());
        }
    }

    public void batchTransferExample() {
        try {
            TransferBatchesRequest.TransferDetail detail1 = TransferBatchesRequest.TransferDetail.newBuilder()
                .outDetailNo("DETAIL_" + System.currentTimeMillis() + "_1")
                .transferAmount(100)  // 1元
                .transferRemark("佣金1")
                .openid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o")
                .userName("张三")
                .build();

            TransferBatchesRequest.TransferDetail detail2 = TransferBatchesRequest.TransferDetail.newBuilder()
                .outDetailNo("DETAIL_" + System.currentTimeMillis() + "_2")
                .transferAmount(200)  // 2元
                .transferRemark("佣金2")
                .openid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6p")
                .userName("李四")
                .build();

            TransferBatchesRequest batchRequest = TransferBatchesRequest.newBuilder()
                .appid("wx1234567890123456")
                .outBatchNo("BATCH_" + System.currentTimeMillis())
                .batchName("佣金批量发放")
                .batchRemark("2024年1月佣金")
                .totalAmount(300)  // 总金额：3元
                .totalNum(2)       // 总笔数：2笔
                .transferDetailList(java.util.Arrays.asList(detail1, detail2))
                .transferSceneId("1005")  // 转账场景ID
                .build();

            TransferBatchesResult batchResult = transferService.transferBatches(batchRequest);

            System.out.println("=== 批量转账发起成功 ===");
            System.out.println("商户批次单号: " + batchResult.getOutBatchNo());
            System.out.println("微信批次单号: " + batchResult.getBatchId());
            System.out.println("批次状态: " + batchResult.getBatchStatus());

        } catch (WxPayException e) {
            System.err.println("批量转账失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        WxPayConfig config = new WxPayConfig();
        config.setAppId("wx1234567890123456");         
        config.setMchId("1234567890");                 
        config.setApiV3Key("your_api_v3_key_32_chars"); 
        config.setPrivateKeyPath("path/to/private.pem"); 
        config.setCertSerialNo("your_certificate_serial"); 

        NewTransferApiExample example = new NewTransferApiExample(config);

        System.out.println("===============================");

        example.transferExample();
    }
}