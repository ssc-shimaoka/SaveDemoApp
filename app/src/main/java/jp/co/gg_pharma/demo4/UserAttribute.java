package jp.co.gg_pharma.demo4;

import android.app.Application;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.spec.MGF1ParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;



public class UserAttribute extends Application
implements StorageAccessInterface {

    private static UserAttribute sInstance;

    EncryptRule encryptRule = null;
    private static final String PrefKey = "User";
    private KeyStore mKeyStore;
    private String providerName;
    private String encryptAlgorithm;
    private String aliasName;

    /***
     *コンストラクタ
     */
    UserAttribute(String EncryptAlgorith, String KeyProviderName, String SecureKeyAlias)
    {
        encryptRule = new EncryptRule();

        encryptAlgorithm = EncryptAlgorith;
        providerName = KeyProviderName;
        aliasName = SecureKeyAlias;
        prepareKeyStore(aliasName, providerName);
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        sInstance = this;
//    }
//
//    public static synchronized UserAttribute getInstance() {
//        return sInstance;
//    }


    /***
     * 認証ID（隠しID) 取得
     * @return
     */
    public String loadAuthID() throws NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException {
        return loadString(DataType.DATA_TYPE_AUTHID);
    }

    /***
     * 認証ID（隠しID) 設定
     * @return
     */
    public void storeAuthID(String data)
    {
        storeString(DataType.DATA_TYPE_AUTHID, data);
    }

    //共通処理（データ保存・取得）------------------------------------------------------------------
    /***
     * データ取得処理　(文字列)
     * @param type
     * @return
     */
     public String loadString(DataType type)  throws NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException{
         String result = null;

         // 暗号化チェック
         boolean encrypt = encryptRule.shouldEncrypt(type);

         SharedPreferences pref = getSharedPreferences(getPackageName(), MODE_PRIVATE);
         String loadData = pref.getString(String.valueOf(type), "");

         // 暗号化処理
         if(encrypt == true)
         {
             result = decryptString(mKeyStore, aliasName, loadData);
         }else{
             result = loadData;
         }

         return result;
     }

    private static final String PREF_NAME = "test_setting";

    /***
     * データ設定処理　(文字列)
     * @param type
     * @return
     */
    public void storeString(DataType type, String data)
    {
        String result = null;

        // 暗号化チェック
        boolean encrypt = encryptRule.shouldEncrypt(type);

        // 暗号化処理
        if(encrypt == true)
        {
            result = encryptString(mKeyStore, aliasName, data);
        }else{
            result = data;
        }

        // 保存処理
        // 以下のSharedPreferencesのインスタンス取得にて例外が発生している
        // 例外：java.lang.reflect.InvocationTargetException
        // nullオブジェクト参照で仮想メソッド 'android.content.SharedPreferences android.content.Context.getSharedPreferences（java.lang.String、int）'を呼び出そうとしました
        // context取得など色々試したが、改善されず・・・。
        
        SharedPreferences pref = getSharedPreferences("Data", MODE_PRIVATE);
        pref.edit().putString("DATA_TYPE_AUTHID", result).apply();

    }

    //未実装
//    /***
//     * データ取得処理　(数値)
//     * @param type
//     * @return
//     */
//    public Integer load(DataType type)
//    {
//        int result = 0;
//
//        // 暗号化チェック
//        boolean encrypt = encryptRule.shouldEncrypt(type);
//
//        return result;
//    }
//
//    /***
//     * データ設定処理　(数値)
//     * @param type
//     * @return
//     */
//    public void store(DataType type, Integer data)
//    {
//        // 暗号化チェック
//        boolean encrypt = encryptRule.shouldEncrypt(type);
//
//    }


    //共通処理（暗号化関連）------------------------------------------------------------------

    /***
     *KeyStore準備処理
     * @param alias
     * @param providerName
     */
    private void prepareKeyStore(String alias, String providerName) {
        try {
            mKeyStore = KeyStore.getInstance(providerName);
            mKeyStore.load(null);
            createNewKey(mKeyStore, alias, providerName);
        } catch (Exception e) {
            Log.e("encrypt Err",e.toString());
        }
    }

    /***
     *キー作成処理
     * @param keyStore
     * @param alias
     * @param providerName
     */
    private void createNewKey(KeyStore keyStore, String alias, String providerName) {
        try {
            // Create new key if not having been registered
            if (!keyStore.containsAlias(alias)) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, providerName);
                keyPairGenerator.initialize(
                        new KeyGenParameterSpec.Builder(
                                alias,
                                KeyProperties.PURPOSE_DECRYPT)
                                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                                .build());
                keyPairGenerator.generateKeyPair();
            }
        } catch (Exception e) {
            Log.e("encrypt Err",e.toString());
        }
    }

    /***
     * 暗号化処理
     * @param keyStore
     * @param alias
     * @param plainText
     * @return
     */
    private String encryptString(KeyStore keyStore, String alias, String plainText) {
        Log.d("start encrypt",plainText);
        String encryptedText = null;
        try {
            PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();

            OAEPParameterSpec spec = new OAEPParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
            Cipher cipher = Cipher.getInstance(encryptAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, spec);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, cipher);
            cipherOutputStream.write(plainText.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] bytes = outputStream.toByteArray();
            encryptedText = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("encrypt Err",e.toString());
        }

        Log.d("encrypted to",encryptedText);
        return encryptedText;
    }

    /***
     * 複合化処理
     * @param keyStore
     * @param alias
     * @param encryptedText
     * @return
     * @throws NoSuchPaddingException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws InvalidKeyException
     * @throws IOException
     */
    private String decryptString(KeyStore keyStore, String alias, String encryptedText) throws NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException {
        Log.d("start encrypt" ,encryptedText);
        String plainText = null;
        try {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);

            Cipher cipher = Cipher.getInstance(encryptAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(encryptedText, Base64.DEFAULT)), cipher);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int b;
            while (true){
                b = cipherInputStream.read();
                if( b == -1 ){
                    break;
                }
                outputStream.write(b);
            }
            outputStream.close();
            plainText = outputStream.toString("UTF-8");
        } catch (Exception e) {
            Log.e("encrypt Err",e.toString());
            throw e;
        }
        Log.d("decrypted to",plainText);
        return plainText;
    }
}
