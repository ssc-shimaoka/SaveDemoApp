package jp.co.gg_pharma.demo4;

public enum DataType {
    /***
     *セキュア領域のデータタイプ
     */
    DATA_TYPE_AUTHID,
    DATA_TYPE_BIRTH,
    DATA_TYPE_MAILADDRESS,
    DATA_TYPE_PASSWORD,
    DATA_TYPE_KCODE,

    /***
     * ノンセキュア領域のデータタイプ
     */
    DATA_TYPE_CID,
    DATA_TYPE_TCODELIST,
    DATA_TYPE_POINT,
    DATA_TYPE_RANK,
    DATA_TYPE_CHARGE,
    DATA_TYPE_ACCESSNTERVAL,
    DATA_TYPE_LASTTIME
    }
