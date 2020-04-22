package jp.co.gg_pharma.demo4;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.crypto.NoSuchPaddingException;

public interface StorageAccessInterface {
    // 文字列データの取得
    String loadString(DataType type) throws NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException;
    // 文字列データの設定
    void storeString(DataType type, String data);

    //未実装
//    // 数値データの取得
//    Integer load(DataType type);
//    // 数値データの設定
//    void store(DataType type, Integer data);
}
