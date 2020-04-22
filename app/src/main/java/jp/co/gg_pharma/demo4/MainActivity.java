package jp.co.gg_pharma.demo4;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private UserAttribute user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String encryptAlgorithm = getString(R.string.EncryptAlgorith);
        String providerName = getString(R.string.KeyProviderName);
        String aliasName = getString(R.string.SecureKeyAlias);

        user = new UserAttribute(encryptAlgorithm, providerName, aliasName);
    }

    /***
     * saveボタン押下
     * @param view
     */
    public void savebutton(View view)
    {
        String result;
        user.storeAuthID("AUTHID000001");

        //以下を有効にした場合、正常にSaveされる
        //SharedPreferences pref = getSharedPreferences("DataSave", MODE_PRIVATE);
        //pref.edit().putString("DATA_TYPE_AUTHID", "AUTHID000001").apply();
    }

    /***
     * loadボタン押下
     * @param view
     */
    public void loadbutton(View view)
    {
        SharedPreferences pref = getSharedPreferences("DataSave", MODE_PRIVATE);
        String loadData = pref.getString("DATA_TYPE_AUTHID", "");

        Log.d("loadData = ", loadData);
    }
//    public void loadbutton(View view) throws NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException {
//        user.loadAuthID();
//    }
}