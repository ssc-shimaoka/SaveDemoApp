package jp.co.gg_pharma.demo4;

public class EncryptRule {

    /***
     * コンストラクタ
     */
    EncryptRule()
    {

    }

    /***
     * 暗号化有無チェック
     * @param type
     * @return
     */
    boolean shouldEncrypt(DataType type) {
        boolean res = false;

        switch (type) {
            case DATA_TYPE_AUTHID:
            case DATA_TYPE_BIRTH:
            case DATA_TYPE_MAILADDRESS:
            case DATA_TYPE_PASSWORD:
            case DATA_TYPE_KCODE:
                res = true;
            break;

            case DATA_TYPE_CID:
            case DATA_TYPE_TCODELIST:
            case DATA_TYPE_POINT:
            case DATA_TYPE_RANK:
            case DATA_TYPE_CHARGE:
            case DATA_TYPE_ACCESSNTERVAL:
            case DATA_TYPE_LASTTIME:
                res = false;
            break;
        }
        return res;
    }
}
